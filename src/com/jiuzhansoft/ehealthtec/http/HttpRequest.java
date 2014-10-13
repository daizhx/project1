package com.jiuzhansoft.ehealthtec.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.config.Configuration;
import com.jiuzhansoft.ehealthtec.http.HttpGroup.Handler;
import com.jiuzhansoft.ehealthtec.http.constant.ConstCacheFileTime;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFileProp;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.constant.ConstSysConfig;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.http.utils.CacheFileItem;
import com.jiuzhansoft.ehealthtec.http.utils.CacheFileTableDBHelper;
import com.jiuzhansoft.ehealthtec.http.utils.CommonUtil;
import com.jiuzhansoft.ehealthtec.http.utils.DialogController;
import com.jiuzhansoft.ehealthtec.http.utils.FileGuider;
import com.jiuzhansoft.ehealthtec.http.utils.FileService;
import com.jiuzhansoft.ehealthtec.http.utils.IOUtil;
import com.jiuzhansoft.ehealthtec.http.utils.LanguageUtil;
import com.jiuzhansoft.ehealthtec.http.utils.Md5Encrypt;
import com.jiuzhansoft.ehealthtec.http.utils.NetUtils;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

public class HttpRequest implements HttpGroup.StopController {
//	private android.os.Handler mHandler = new android.os.Handler();

	class HttpDialogController extends DialogController {
		protected ArrayList<HttpRequest> httpRequestList;
		protected Activity myActivity;

		HttpDialogController() {
		}

		protected void actionCancel() {
			actionCommon(false);
		}

		protected void actionCommon(boolean flag) {
			if (Log.D) { 
				Log.d("HttpRequest", "HttpDialogController.actionCommon");
			}
			
			alertDialog.dismiss();
			if (Log.D) {
				StringBuilder localStringBuilder1 = new StringBuilder("id:");
				int i = HttpRequest.this.httpSetting.getId();
				localStringBuilder1.append(i).append(
						"- notifyUser() retry -->> httpRequestList.size() = ");
				int j = this.httpRequestList.size();
				String str = "" + j;
				Log.d("HttpGroup", str);
			}
			HashMap hashMap = HttpGroup.alertDialogStateMap;
			int l = this.httpRequestList.size();
			int k = 0;
			synchronized (hashMap){
				while (k < l) {
					HttpRequest httprequest = (HttpRequest) httpRequestList.get(k);
					if (flag)
						httprequest.manualRetry = true;
					synchronized (httprequest) {
						httprequest.notify();
					}
					
					k++;
				}
			}
			hashMap.remove(myActivity);

		}

		protected void actionRetry() {
			actionCommon(true);
		}

		public void init(ArrayList<HttpRequest> arrayList, Activity activity) {
			if (Log.D) { 
				Log.d("HttpRequest", "HttpDialogController.init");
			}
			
			this.myActivity = activity;
			this.httpRequestList = arrayList;
			init(myActivity);
		}
	}

	private HttpGroup.Handler cacheHandler;
	protected HttpURLConnection conn;
	private HttpGroup.Handler connectionHandler;
	protected boolean connectionRetry;
	private HttpGroup.Handler contentHandler;
	private HttpGroup.CompleteListener continueListener;
	private int currentHandlerIndex;
	protected ArrayList<HttpError> errorList;
	private HttpGroup.Handler firstHandler;
	private ArrayList<HttpGroup.Handler> handlers;
	// add by jerry.deng
	HttpGroup httpGroup;
	protected HttpResponse httpResponse;
	protected HttpSetting httpSetting;
	protected InputStream inputStream;
	private IOUtil.ProgressListener ioProgressListener;
	protected boolean manualRetry;
	private HttpGroup.Handler paramHandler;
	private HttpGroup.Handler proxyHandler;
	private boolean stopFlag;
	private HttpGroup.Handler testHandler;
	private String thirdHost;

	public HttpRequest(HttpSetting httpSet, HttpGroup group) {
		if (Log.D) { 
			Log.d("HttpRequest", "HttpRequest(setting, group)");
		}
		
		handlers = new ArrayList();
		httpSetting = httpSet;
		httpGroup = group;
		currentHandlerIndex = 0;
		// the first handler---make up sent data;--include functionId and body
		// data
		paramHandler = new HttpGroup.Handler() {
			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "paramHandler.run");
				}
				
				if (httpSetting.getFunctionId() != null) {
					String s = httpSetting.getFunctionId();
					httpSetting.putMapParams(ConstFuncId.FUNCTION_ID, s);
					String s1 = httpSetting.getJsonParams().toString();
					try {
						s1 = new String(s1.getBytes(),"utf-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (Log.I) {
						StringBuilder stringbuilder = new StringBuilder("id:");
						String s2 = stringbuilder.append(httpSetting.getId())
								.append("- body -->> ").append(s1).toString();
						Log.i("HttpGroup", s2);
					}
					httpSetting.putMapParams("body", s1);
				}
				// enter next handler;
				nextHandler();
			}
		};
		/**
		 * the second handler function:get host and port
		 * 
		 */
		proxyHandler = new HttpGroup.Handler() {
			private String hostAndPort;

			public String getHostAndPortByUrl(String s) {
				if (Log.D) { 
					Log.d("HttpRequest", "proxyHandler.getHostAndPortByUrl");
				}
				
				String s1;
				if (hostAndPort != null)
					s1 = hostAndPort;
				else if (s != null) {
					int i = s.indexOf("://") + 3;
					int j = s.indexOf("/", i);
					if (i == -1)
						s1 = null;
					else if (j == -1) {
						s1 = null;
					} else {
						hostAndPort = s.substring(i, j);
						s1 = hostAndPort;
					}
				} else {
					s1 = null;
				}
				return s1;
			}

			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "proxyHandler.run");
				}
				
				String proxyHost = NetUtils.getProxyHost();
				
				if (Log.D) { 
					Log.d("HttpRequest", "proxyHost : " + proxyHost);
				}
				
				if (proxyHost != null) {
					String url = httpSetting.getUrl();
					if (url != null) {
						thirdHost = getHostAndPortByUrl(url);
						if (thirdHost != null) {
							httpSetting.setUrl(url.replace(thirdHost, proxyHost));
						}
					}
				}
				if (httpSetting.getFunctionId() != null) {
					String lastUrl = null;
					if (proxyHost != null) {
						lastUrl = (new StringBuilder("http://"))
								.append(proxyHost)
								.append(Configuration.getProperty("mainserver"))
								.append("/client/rest/").toString();
					} else {
						StringBuilder stringbuilder = new StringBuilder(
								"http://");
						lastUrl = stringbuilder
								.append(HttpGroup.host)
								.append(Configuration.getProperty("mainserver"))
								.append("/client/rest/").toString();
					}
					httpSetting.setUrl(lastUrl);
				}
				nextHandler();
			}
		};
		/**
		 * the third handler function:设置连接的时间参数
		 */
		firstHandler = new HttpGroup.Handler() {
			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "firstHandler.run");
				}
				
				if (httpSetting.getConnectTimeout() == 0) {
					httpSetting.setConnectTimeout(HttpGroup.connectTimeout);
				}
				if (httpSetting.getReadTimeout() == 0) {
					httpSetting.setReadTimeout(HttpGroup.readTimeout);
				}
				if (httpSetting.getType() == ConstHttpProp.TYPE_IMAGE
						|| httpSetting.getType() == ConstHttpProp.PRIORITY_FILE) {
					//for file , only with get;
					httpSetting.setPost(false);
				}
				
				if (httpSetting.getType() == ConstHttpProp.TYPE_IMAGE)
					httpSetting.setReadTimeout(ConstCacheFileTime.HOUR);
				
				if (httpSetting.getType() == ConstHttpProp.TYPE_IMAGE) {
					httpSetting.setLocalFileCache(true);
					httpSetting.setLocalFileCacheTime(ConstCacheFileTime.IMAGE);
				}
				
				if (httpSetting.getType() == ConstHttpProp.TYPE_IMAGE) {
					// httpSetting.setNeedGlobalInitialization(false);
				}
				
				/*if (httpSetting.isNeedGlobalInitialization()) {
					GlobalInitialization.initNetwork(true);
				}*/
				httpGroup.addMaxStep(1);
				urlParam();
				if (checkModule(0)) {
					if (Log.D) {
						StringBuilder stringbuilder = new StringBuilder(
								"id:");
						int k = httpSetting.getId();
						String s = stringbuilder.append(k)
								.append("- functionId close -->> ")
								.toString();
						Log.d("HttpGroup", s);
					}
				} else if (TextUtils.isEmpty(httpSetting.getUrl())
						&& TextUtils.isEmpty(httpSetting.getFunctionId())
						|| httpSetting.getUrl().endsWith(".gif")
						|| httpSetting.getUrl().endsWith(".bmp")) {
					HttpError httperror = new HttpError();
					httperror.setErrorCode(2);
					httperror.setResponseCode(404);
					throwError(httperror);
					httpSetting.onError(getLastError());
				} else {
					nextHandler();
					if (isLastError()) {
						if (Log.I) {
							StringBuilder stringbuilder1 = new StringBuilder(
									"id:");
							int l = httpSetting.getId();
							String s1 = stringbuilder1.append(l)
									.append("- onError -->> ").toString();
							Log.i("HttpGroup", s1);
						}
						httpSetting.onError(getLastError());
					} else {
						if (Log.I) {
							StringBuilder stringbuilder2 = new StringBuilder(
									"id:");
							int i1 = httpSetting.getId();
							String s2 = stringbuilder2.append(i1)
									.append("- onEnd -->> ").toString();
							Log.i("HttpGroup", s2);
						}
						httpGroup.addCompletesCount();
						httpGroup.addStep(1);
						httpSetting.onEnd(httpResponse);
					}
				}
			}
		};
		/**
		 * the fourth handler--test handler function
		 */
		testHandler = new HttpGroup.Handler() {
			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "testHandler.run");
				}
				
				String s;
				Boolean boolean1 = Boolean.valueOf(false);
				if (!Configuration.getBooleanProperty("testMode", boolean1).booleanValue()) {
					nextHandler();
					return;
				}
			}
		};
		cacheHandler = new HttpGroup.Handler() {
			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "cacheHandler.run");
				}

				if (httpSetting.getCacheMode() != ConstHttpProp.CACHE_MODE_ONLY_NET
						&& httpSetting.isLocalFileCache()) {
					File file = findCachesFileByMd5();
					if (file != null) {
						if (httpSetting.getLocalFileCacheTime() != 0L
								&& CacheFileTableDBHelper.isExpired(file)) {
							httpResponse = new HttpResponse();
							switch (httpSetting.getType()) {
							// json type cache
							case ConstHttpProp.TYPE_JSON:
								FileInputStream fileinputstream = null;
								if (Log.D) {
									StringBuilder stringbuilder1 = new StringBuilder(
											"id:");
									int j = httpSetting.getId();
									String s1 = stringbuilder1.append(j)
											.append("- read json file -->> ")
											.toString();
									Log.d("HttpGroup", s1);
								}
								try {
									fileinputstream = new FileInputStream(file);
									String s3 = IOUtil.readAsString(
											fileinputstream, HttpGroup.charset);
									httpResponse.setString(s3);
									JSONObject jsonobject = new JSONObject(
											httpResponse.getString());
									JSONObjectProxy jsonobjectproxy = new JSONObjectProxy(
											jsonobject);
									httpResponse.setJsonObject(jsonobjectproxy);
									if (fileinputstream != null) {
										fileinputstream.close();
									}
								} catch (Exception exception) {
									httpResponse = null;
									exception.printStackTrace();
								} finally {
									if (fileinputstream != null) {
										try {
											fileinputstream.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
								return;
								// image file type cache
							case ConstHttpProp.TYPE_IMAGE:
								if (Log.D) {
									StringBuilder stringbuilder2 = new StringBuilder(
											"id:");
									int k = httpSetting.getId();
									String s5 = stringbuilder2.append(k)
											.append("- read image file -->> ")
											.toString();
									Log.d("HttpGroup", s5);
								}
								try {
									long l = file.length();
									httpResponse.setLength(l);
									Bitmap bitmap = BitmapFactory.decodeFile(
											file.getAbsolutePath(),
											getBitmapOpt());
									httpResponse.setBitmap(bitmap);
									httpResponse
											.setDrawable(new BitmapDrawable(
													bitmap));
								} catch (Throwable throwable) {
									file.delete();
									httpResponse = null;
									doNetAndCache();
								}
								return;
							}
						} else {
							if (Log.D) {
								StringBuilder stringbuilder = new StringBuilder(
										"id:");
								int i = httpSetting.getId();
								String s = stringbuilder
										.append(i)
										.append("- local file cache time out -->> ")
										.toString();
								Log.d("HttpGroup", s);
							}
						}
					}
				}
				doNetAndCache();
			}
		};
		connectionHandler = new HttpGroup.Handler() {
			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "connectionHandler.run");
				}

				int i = 0;
				boolean isContinue = false;
				while (i < HttpGroup.attempts) {
					try {
						beforeConnection();
						String s = httpSetting.getFinalUrl();						
						if (s == null)
							s = httpSetting.getUrl();
						URL url = new URL(s);
						try {
							conn = (HttpURLConnection) url.openConnection();
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

						if (NetUtils.getProxyHost() != null) {
							if (thirdHost != null) {
								conn.setRequestProperty("X-Online-Host", thirdHost);
							} else {
								conn.setRequestProperty("X-Online-Host", HttpGroup.host);
							}

						}

						conn.setConnectTimeout(httpSetting.getConnectTimeout());
						conn.setReadTimeout(httpSetting.getReadTimeout());
						conn.setUseCaches(httpGroup.isUseCaches());
						conn.setRequestProperty("Charset", HttpGroup.charset);
						conn.setRequestProperty("Connection", "Keep-Alive");
						conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
						// set cookies
						if (HttpGroup.cookies != null) {
							if (Log.D) {
								StringBuilder stringbuilder2 = new StringBuilder("id:");
								int k1 = httpSetting.getId();
								StringBuilder stringbuilder3 = stringbuilder2
										.append(k1).append("- cookies set -->> ");
								String s6 = HttpGroup.cookies;
								String s7 = stringbuilder3.append(s6)
										.toString();
								Log.d("HttpGroup", s7);
							}

							conn.setRequestProperty("Cookie", HttpGroup.cookies);
							Editor editor = CommonUtil.getGySharedPreferences().edit();
							editor.putString(ConstSysConfig.SYS_SERVICE_COOKIES, HttpGroup.cookies).commit();

						}
						if (Log.D) {
							StringBuilder stringbuilder4 = new StringBuilder("id:");
							int l1 = httpSetting.getId();
							String s10 = stringbuilder4.append(l1)
									.append("- handleGetOrPost() -->> ")
									.toString();
							Log.d("HttpGroup", s10);
						}
						handleGetOrPost();
						if (connectionRetry) {
							connectionRetry = false;
							isContinue = true;
						}
						if (!isContinue) {
							return;
						}
						if (i < HttpGroup.attempts - 1) {
							if (Log.D) {
								StringBuilder stringbuilder5 = new StringBuilder("id:");
								int j2 = httpSetting.getId();
								StringBuilder stringbuilder6 = stringbuilder5
										.append(j2).append("- sleep -->> ");
								int k2 = HttpGroup.attemptsTime;
								String s11 = stringbuilder6.append(k2).toString();
								Log.d("HttpGroup", s11);
							}
							Thread.sleep(HttpGroup.attemptsTime);

						}
					} catch (Exception e) {
						HttpError httperror = new HttpError(e);
						throwError(httperror);
						isContinue = false;
					}
					if (!isContinue) {
						break;
					}
					if (manualRetry) {
						manualRetry = false;
						clearErrorList();
						i = 0;
					} else {
						i++;
					}
				}
			}
		};
		contentHandler = new HttpGroup.Handler() {
			public void run() {
				if (Log.D) { 
					Log.d("HttpRequest", "contentHandler.run");
				}
				
				if (Log.D) {
					StringBuilder stringbuilder = new StringBuilder("id:");
					int i = httpSetting.getId();
					String s = stringbuilder.append(i)
							.append("- contentHandler -->>").toString();
					Log.d("HttpGroup", s);
				}
				try {
					if (httpSetting.getType() == ConstHttpProp.TYPE_JSON) {
						jsonContent();
					}
					if (httpSetting.getType() == ConstHttpProp.TYPE_JSONARRAY) {
						jsonArrayContent();
					}
					if (httpSetting.getType() == ConstHttpProp.TYPE_IMAGE) {
						imageContent();
					}
					if (httpSetting.getType() == ConstHttpProp.TYPE_FILE) {
						fileContent();
					}
					httpResponse.clean();
					if (Log.D) {
						StringBuilder stringbuilder1 = new StringBuilder("id:");
						int j = httpSetting.getId();
						String s1 = stringbuilder1.append(j)
								.append("- contentHandler -->> ok").toString();
						Log.d("HttpGroup", s1);
					}
					return;
				} catch (Exception e) {
					throwError(new HttpError(e));
					connectionRetry = true;
				}
			}
		};
		ioProgressListener = new IOUtil.ProgressListener() {
			public void notify(int progess, int paramInt2) {
				httpGroup.addProgress(progess);
				int k = Long.valueOf(httpResponse.getLength()).intValue();
				httpSetting.onProgress(k, paramInt2);
			}
		};
		continueListener = new HttpGroup.CompleteListener() {
			public void onComplete(Bundle paramBundle) {
				synchronized (HttpRequest.this) {
					notify();
				}
			}
		};
		// add handler to List
		// add proxy handler to list
		handlers.add(proxyHandler);
		// add parameter handler to list
		handlers.add(paramHandler);
		// add first handler to list
		handlers.add(firstHandler);
		// add test handler to handler list
		handlers.add(testHandler);
		// add cache handler to handler list
		handlers.add(cacheHandler);
		// add connection handler to handler list
		handlers.add(connectionHandler);
		// add content handler to handler list
		handlers.add(contentHandler);
	}

	private class AttestationWIFIDialogController extends HttpDialogController {
		private int state;
		

		public void onClick(DialogInterface dialoginterface, int i) {
			if (Log.D) { 
				Log.d("HttpRequest", "HttpRequest.AttestationWIFIDialogController.onClick");
			}
			
			switch (i) {
			case DialogInterface.BUTTON_POSITIVE: {
				switch (state) {
				case 0:
					if (Log.D)
						Log.d("HttpGroup", "http dialog BUTTON_POSITIVE -->> 1");
					state = 1;
//					mHandler.post(new Runnable() {
//
//						@Override
//						public void run() {
//							if (Log.D) { 
//								Log.d("HttpRequest", "HttpRequest.AttestationWIFIDialogController.onClick.run");
//							}
//							
//							setMessage(myActivity.getResources().getString(R.string.mess_is_retry));
//							setPositiveButton(myActivity.getResources().getString(R.string.mess_retry));
//							// if(AttestationWIFIDialogController.this.isShowing())
//							AttestationWIFIDialogController.this.show();
//							Uri localUri = Uri.parse(Configuration.getProperty("guanyi_url"));
//							Intent intent = new Intent(Intent.ACTION_VIEW, localUri);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							EHTApplication.getInstance().startActivity(intent);
//						}
//
//					});
					break;
				case 1:
					if (Log.D)
						Log.d("HttpGroup", "http dialog BUTTON_POSITIVE -->> 2");
					actionRetry();
					break;
				default:
					break;
				}
			}
			case DialogInterface.BUTTON_NEGATIVE:
				if (Log.D)
					Log.d("HttpGroup", "http dialog BUTTON_NEGATIVE -->> 1");
				actionCancel();
				break;
			default:
				break;
			}
		}
	}

	private class ErrorDialogController extends HttpDialogController {
		public void onClick(DialogInterface dialoginterface, int l) {
			switch (l) {
			case DialogInterface.BUTTON_POSITIVE:
				actionRetry();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				actionCancel();
				break;
			default:
				break;
			}
		}
	}

	private void alertAttestationWIFIDialog() {
		if (Log.D) { 
			Log.d("HttpRequest", "alertAttestationWIFIDialog");
		}
		final Activity myActivity = httpGroup.getHttpGroupSetting().getMyActivity();
		AttestationWIFIDialogController wifiDialogController = new AttestationWIFIDialogController();
		wifiDialogController.setTitle(myActivity.getResources().getString(R.string.mess_wifi_cert));
		wifiDialogController.setMessage(myActivity.getResources().getString(R.string.mess_wifi_need_cert));
		wifiDialogController.setPositiveButton(myActivity.getResources().getString(R.string.ok));
		wifiDialogController.setNegativeButton(myActivity.getResources().getString(R.string.cancel));
		notifyUser(wifiDialogController);
	}

	private void alertErrorDialog() {
		if (Log.D) { 
			Log.d("HttpRequest", "alertErrorDialog");
		}
		
		if (Log.D) {
			StringBuilder localStringBuilder2 = new StringBuilder("id:");
			int j = this.httpSetting.getId();
			String str2 = j + "- alertErrorDialog() -->> true";
			Log.d("HttpGroup", str2);
		}

		if (!this.httpSetting.isNotifyUser()) {
			if (Log.D) {
				StringBuilder stringbuilder1 = new StringBuilder("id:");
				int j = httpSetting.getId();
				String s1 = stringbuilder1.append(j)
						.append("- alertErrorDialog() -->> true").toString();
				Log.d("HttpGroup", s1);
			}

			ErrorDialogController errorDlgController = new ErrorDialogController();
			CharSequence charsequence = EHTApplication.getInstance().getText(
					R.string.alert_title_poor_network2);
			errorDlgController.setTitle(charsequence);
			CharSequence charsequence1 = EHTApplication.getInstance().getText(
					R.string.alert_message_poor_network2);
			errorDlgController.setMessage(charsequence1);
			CharSequence charsequence2 = EHTApplication.getInstance().getText(
					R.string.retry);
			errorDlgController.setPositiveButton(charsequence2);
			int k;
			CharSequence charsequence3 = null;
			if (httpSetting.isNotifyUserWithExit())
				k = R.string.exit;
			else
				k = R.string.cancel;
			charsequence3 = EHTApplication.getInstance().getText(k);
			errorDlgController.setNegativeButton(charsequence3);
			notifyUser(errorDlgController);
		}
	}

	private void beforeConnection() {
		if (Log.D) { 
			Log.d("HttpRequest", "beforeConnection");
		}
		
		if (checkModule(3)) {
			if (Log.D) {
				StringBuilder localStringBuilder1 = new StringBuilder("id:");
				int i = this.httpSetting.getId();
				String str1 = i + "- encrypt -->> ";
				Log.d("HttpGroup", str1);
			}
			if (HttpGroup.mMd5Key == null) {
				HttpGroup.queryMd5Key(this.continueListener);
				if (Log.D) {
					StringBuilder localStringBuilder2 = new StringBuilder("id:");
					int j = this.httpSetting.getId();
					String str2 = j + "- encrypt wait start -->> ";
					Log.d("HttpGroup", str2);
				}
				try {
					synchronized(this){
						wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (Log.D) {
					StringBuilder localStringBuilder3 = new StringBuilder("id:");
					int k = this.httpSetting.getId();
					String str3 = k + "- encrypt wait end -->> ";
					Log.d("HttpGroup", str3);
				}
			}
			String s3 = String.valueOf(httpSetting.getUrl());
			StringBuilder stringbuilder3 = (new StringBuilder(s3))
					.append("&hash=");
			String s4 = String.valueOf(httpSetting.getJsonParams().toString());
			StringBuilder stringbuilder4 = new StringBuilder(s4);
			String s6 = Md5Encrypt.md5(stringbuilder4.append(HttpGroup.mMd5Key)
					.toString());
			String s7 = stringbuilder3.append(s6).toString();
			httpSetting.setFinalUrl(s7);
		}
	}

	public void checkErrorInteraction() {
		if (Log.D) { 
			Log.d("HttpRequest", "checkErrorInteraction");
		}
		
		HttpError httpError = getLastError();
		if ((httpError != null) && (httpError.getErrorCode() == 0)) {
			String str = httpError.getException().getMessage();
			if ("attestation WIFI".equals(str)) {
				alertAttestationWIFIDialog();
				return;
			}
		}
		if (isLastError())
			alertErrorDialog();
	}

	protected boolean checkModule(int id) {
		if (Log.D) { 
			Log.d("HttpRequest", "checkModule");
		}
		
		boolean flag = false;
		if ((httpSetting.getFunctionId() != null) && (HttpGroup.mModules != null)) {
			JSONObjectProxy jsonObjectProxy = HttpGroup.mModules;
			String funcId = httpSetting.getFunctionId();
			Integer fincInteger = jsonObjectProxy.getIntOrNull(funcId);
			if (fincInteger != null) {
				if (id == fincInteger.intValue())
					flag = true;
			}
		}
		return flag;
	}

	private void clearErrorList() {
		getErrorList().clear();
	}

	protected void connectionHandler2() {
		if (Log.D) { 
			Log.d("HttpRequest", "connectionHandler2");
		}
		
		String logString = null;
		InputStream is = null;
		Object obj = null;
		if (Log.D) {
			logString = new StringBuilder("id:").append(httpSetting.getId())
					.append("- connectionHandler2() -->> ").toString();
			Log.d("HttpGroup", logString);
		}
		try {
			Log.d("daizhx", "connect:"+conn.getURL());
			conn.connect();
			if (Log.D) {
				logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- ResponseCode() -->> ").toString();
				Log.d("HttpGroup", logString);
			}			
			Map map = conn.getHeaderFields();
			httpResponse.setHeaderFields(map);
			if (Log.D) {
				JSONObject jsonObject = new JSONObject();
				Set set = conn.getHeaderFields().entrySet();
				Iterator iterator = set.iterator();
				String key = null;
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					if (entry.getKey() == null) {
						key = "<null>";
					} else {
						key = (String) entry.getKey();
					}
					Collection collection = (Collection) entry.getValue();
					jsonObject.put(key, (new JSONArray(collection)).toString());

				}
				logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- headerFields -->> ")
						.append(jsonObject.toString()).toString();
				Log.d("HttpGroup", logString);
			}
			httpResponse.setCode(conn.getResponseCode());
			httpResponse.setLength(conn.getContentLength());
			int respLen = Long.valueOf(httpResponse.getLength()).intValue();
			httpGroup.addMaxProgress(respLen);
			httpResponse.setType(conn.getContentType());
			if (httpResponse.getCode() == HttpURLConnection.HTTP_OK) {
				if (Log.D) {
					logString = new StringBuilder("id:")
							.append(httpSetting.getId())
							.append("- ResponseCode() -->> ok").toString();
					Log.d("HttpGroup", logString);
				}
				String cookies = conn.getHeaderField("Set-Cookie");
				if (cookies != null) {
					if (Log.D) {
						logString = new StringBuilder("id:")
								.append(httpSetting.getId())
								.append("- cookies get -->> ").append(cookies)
								.toString();
						Log.d("HttpGroup", logString);
					}
					HttpGroup.cookies = cookies.substring(0,
							cookies.indexOf(";"));
				}
				String cotentEncoding = conn.getHeaderField("Content-Encoding");			
				if (!"gzip".equals(cotentEncoding)) {					
					is = conn.getInputStream();
					obj = is;
				} else {
					is = conn.getInputStream();
					obj = new GZIPInputStream(is);
				}
				httpResponse.setInputStream(((InputStream) (obj)));
				if (Log.D) {
					logString = new StringBuilder("id:")
							.append(httpSetting.getId())
							.append("- ResponseCode() -->> ok nextHandler()")
							.toString();
					Log.d("HttpGroup", logString);
				}
				nextHandler();
			} else {
				HttpError httpError = new HttpError();
				httpError.setErrorCode(2);
				httpError.setResponseCode(httpResponse.getCode());
				throwError(httpError);
				connectionRetry = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			HttpError httperror = new HttpError(e);
//			httperror.setNoRetry(true);
			throwError(httperror);
		} finally {
			try {
				if (httpResponse.getInputStream() != null) {
					httpResponse.getInputStream().close();
					httpResponse.setInputStream(null);
				}
				if (conn != null) {
					conn.disconnect();
					conn = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void doNetAndCache() {
		if (Log.D) { 
			Log.d("HttpRequest", "doNetAndCache");
		}
		
		if (Log.D) {
			StringBuilder localStringBuilder = new StringBuilder("id:");
			int i = this.httpSetting.getId();
			String str = i + "- doNetAndCache() -->> ";
			Log.d("HttpGroup", str);
		}
		int j = this.httpSetting.getCacheMode();
		if (ConstHttpProp.CACHE_MODE_ONLY_CACHE == j) {
			Exception localException = new Exception("no cache");
			HttpError localHttpError = new HttpError(localException);
			localHttpError.setNoRetry(true);
			throwError(localHttpError);
		} else {
			nextHandler();
			if (isLastError())
				save();
		}
	}

	/**
	 * read data from internet to local file
	 */
	private void fileContent() {
		if (Log.D) { 
			Log.d("HttpRequest", "fileContent");
		}
		
		try {
			FileGuider fileguider = httpSetting.getSavePath();
			long l = httpResponse.getLength();
			fileguider.setAvailableSize(l);
			FileOutputStream fileoutputstream = FileService.openFileOutput(fileguider);
			InputStream inputStream = httpResponse.getInputStream();
			IOUtil.readAsFile(inputStream, fileoutputstream,
					ioProgressListener, this);
			File file = Environment.getExternalStorageDirectory();//JiuZhanApplication.getInstance().getFilesDir();
			File file1 = new File(file, fileguider.getFileName());
			if (Log.D) {
				StringBuilder stringBuilder = new StringBuilder("id:");
				int i = httpSetting.getId();
				String str2 = i + "- download() apkFilePath -->> " + file1;
				Log.d("HttpGroup", str2);
			}
			if (isStop())
				file1.delete();
			httpResponse.setSaveFile(file1);
			return;
		} catch (Exception exception) {
			if (Log.D) {
				StringBuilder stringBuilder1 = new StringBuilder("id:");
				int j = httpSetting.getId();
				String str3 = j + "- file content connection read error -->> ";
				Log.d("HttpGroup", str3, exception);
			}
			HttpError httpError = new HttpError(exception);
			throwError(httpError);
			connectionRetry = true;
		}
	}

	/**
	 * get file from cache according to md5 value
	 * 
	 * @return
	 */
	private File findCachesFileByMd5() {
		if (Log.D) { 
			Log.d("HttpRequest", "findCachesFileByMd5");
		}
		
		FileService.Directory directory = null;
		if (Log.D) {
			StringBuilder localStringBuilder1 = new StringBuilder("id:");
			int i = this.httpSetting.getId();
			String str1 = i + "- findCachesFileByMd5() -->> ";
			Log.d("HttpGroup", str1);
		}
		switch (httpSetting.getType()) {
		case ConstHttpProp.TYPE_JSON:
			directory = FileService.getDirectory(ConstFileProp.INTERNAL_TYPE_CACHE);
			break;
		case ConstHttpProp.TYPE_IMAGE:
			directory = FileService.getDirectory(ConstFileProp.INTERNAL_TYPE_FILE);
			break;
		}
		if (Log.D) {
			String logString = new StringBuilder("id:")
					.append(httpSetting.getId())
					.append("- findCachesFileByMd5() directory -->> ")
					.append(directory).toString();
			Log.d("HttpGroup", logString);
		}
		File file;
		if (directory == null) {
			file = null;
		} else {
			File file1 = directory.getDir();
			if (Log.D) {
				String logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- findCachesFileByMd5() dir.exists() -->> ")
						.append(file1.exists()).toString();
				Log.d("HttpGroup", logString);
			}
			if (Log.D) {
				String logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- findCachesFileByMd5() dir.isDirectory() -->> ")
						.append(file1.isDirectory()).toString();
				Log.d("HttpGroup", logString);
			}
			if (Log.D) {
				String logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- findCachesFileByMd5() dir -->> ")
						.append(file1).toString();
				Log.d("HttpGroup", logString);
			}
			File afile[] = file1.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					String md5 = httpSetting.getMd5();
					boolean isPointedType;
					if (md5 == null)
						isPointedType = false;
					else
						isPointedType = filename.startsWith(md5);
					return isPointedType;
				}

			});
			if (Log.D) {
				String logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- findCachesFileByMd5() fileList -->> ")
						.append(afile).toString();
				Log.d("HttpGroup", logString);
			}
			if (afile != null && afile.length > 0) {
				if (Log.D) {
					String logString = new StringBuilder("id:")
							.append(httpSetting.getId())
							.append("- can find caches file by md5 -->> ")
							.toString();
					Log.d("HttpGroup", logString);
				}
				file = afile[0];
			} else {
				file = null;
			}
		}
		return file;
	}

	private void get() throws Exception {
		if (Log.D) { 
			Log.d("HttpRequest", "get");
		}
		
		if (Log.D) {
			String logString = new StringBuilder("id:")
					.append(httpSetting.getId()).append("- get() -->> ")
					.toString();
			Log.d("HttpGroup", logString);
		}

		httpResponse = new HttpResponse(conn);
		conn.setRequestMethod("GET");
		if (Log.D) {
			String logString = new StringBuilder("id:")
					.append(httpSetting.getId()).append("- get() -->> ok")
					.toString();
			Log.d("HttpGroup", logString);
		}
	}

	private BitmapFactory.Options getBitmapOpt() {
		if (Log.D) { 
			Log.d("HttpRequest", "getBitmapOpt");
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (httpResponse.getLength() > 0x10000L) {
			if (Log.D) {
				String logString = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- opt.inSampleSize -->> ").append(2)
						.toString();
				Log.d("HttpGroup", logString);
			}
			options.inSampleSize = 2;
		}
		return options;
	}

	private ArrayList<HttpError> getErrorList() {
		if (Log.D) { 
			Log.d("HttpRequest", "getErrorList");
		}
		
		if (errorList == null) {
			errorList = new ArrayList();
		}
		return errorList;
	}

	public HttpSetting getHttpSetting() {
		return httpSetting;
	}

	private HttpError getLastError() {
		if (Log.D) { 
			Log.d("HttpRequest", "getLastError");
		}
		
		ArrayList arraylist = getErrorList();
		int len = arraylist.size();
		HttpError httpError = null;
		if (len > 0) {
			httpError = (HttpError) errorList.get(len - 1);
		}

		return httpError;
	}

	private void handleGetOrPost() throws Exception {
		if (Log.D) { 
			Log.d("HttpRequest", "handleGetOrPost");
		}
		if (httpSetting.isGet())
			get();
		else
			post();
		connectionHandler2();
	}

	private void imageContent() throws Exception {
		if (Log.D) { 
			Log.d("HttpRequest", "imageContent");
		}
		
		if (httpResponse.getType() == null
				|| !httpResponse.getType().contains("image/")) {
			HttpError httpError = new HttpError();
			httpError.setErrorCode(2);
			httpError.setResponseCode(404);
			throwError(httpError);
			connectionRetry = true;
		} else {
			try {
				InputStream is = httpResponse.getInputStream();
				// read data from input stream;
				byte bytes[] = IOUtil.readAsBytes(is, ioProgressListener);
				httpResponse.setInputData(bytes);
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
						bytes.length, getBitmapOpt());
				httpResponse.setBitmap(bitmap);
				httpResponse.setDrawable(new BitmapDrawable(bitmap));
			} catch (Throwable throwable) {
				if (Log.D) {
					String logString = new StringBuilder("id:")
							.append(httpSetting.getId())
							.append("- image content connection read error -->> ")
							.toString();
					Log.d("HttpGroup", logString);
				}
				HttpError httpError = new HttpError(throwable);
				httpError.setNoRetry(true);
				throwError(httpError);
			}
		}
	}

	public boolean isLastError() {
		if (Log.D) { 
			Log.d("HttpRequest", "isLastError");
		}
		
		int errListLen = 0;
		int attemptNum = 0;
		boolean flag = false;
		if (errorList != null) {
			errListLen = errorList.size();
			attemptNum = HttpGroup.attempts;
			if (errListLen < attemptNum)
				flag = false;
			else
				flag = true;
		} else {
			flag = false;
		}
		if (!flag) {
			HttpError httpError = getLastError();
			if (httpError != null && httpError.isNoRetry())
				flag = true;
		}
		if (Log.D) {
			String s = new StringBuilder("id:").append(httpSetting.getId()).append("- isLastError() -->> ")
					.append(flag).toString();
			Log.d("HttpGroup", s);
		}
		return flag;
	}

	public boolean isStop() {
		return stopFlag;
	}

	private void jsonArrayContent() throws Exception{
		try
		{
			InputStream inputstream = httpResponse.getInputStream();
			String s = IOUtil.readAsString(inputstream, HttpGroup.charset, ioProgressListener);
			httpResponse.setString(s);
			
			
		} catch (Exception exception) {
			HttpError httperror = new HttpError(exception);
			throwError(httperror);
			connectionRetry = true;
			return;
		}
		try
		{
			JSONArray jsonArray = new JSONArray(httpResponse.getString());
			JSONArrayPoxy jsonArrayPoxy = new JSONArrayPoxy(jsonArray);
			httpResponse.setJsonArray(jsonArrayPoxy);
		} catch (JSONException jsonexception) {
			if (Log.D)
			{
				StringBuilder stringbuilder7 = new StringBuilder("id:");
				String s1 = stringbuilder7.append(httpSetting.getId()).append("- Can not format json -->> ").toString();
				Log.d("HttpGroup", s1, jsonexception);
			}
			HttpError httperror2 = new HttpError(jsonexception);
			throwError(httperror2);
			connectionRetry = true;
			
			return ;
		}
	}
	private void jsonContent() throws Exception {
		//if (httpResponse.getType() != null && httpResponse.getType().contains("application/json"))
		//{
			try
			{
				InputStream inputstream = httpResponse.getInputStream();
				String s = IOUtil.readAsString(inputstream, HttpGroup.charset, ioProgressListener);
				httpResponse.setString(s);
				if (Log.I)
				{
					StringBuilder stringbuilder = new StringBuilder("id:");
					stringbuilder.append(httpSetting.getId()).append("- response string -->> ");
					
					Log.i("HttpGroup", stringbuilder.append(httpResponse.getString()).toString());
				}
			} catch (Exception exception) {
				if (Log.D)
				{
					StringBuilder stringbuilder6 = new StringBuilder("id:");
					String s = stringbuilder6.append(httpSetting.getId()).append("- json content connection read error -->> ").toString();
					Log.d("HttpGroup", s, exception);
				}
				HttpError httperror = new HttpError(exception);
				throwError(httperror);
				connectionRetry = true;
				
				return;
			}
				
			try
			{
				JSONObject jsonobject = new JSONObject(httpResponse.getString());
				JSONObjectProxy jsonobjectproxy = new JSONObjectProxy(jsonobject);
				httpResponse.setJsonObject(jsonobjectproxy);
			} catch (JSONException jsonexception) {
				if (Log.D)
				{
					StringBuilder stringbuilder7 = new StringBuilder("id:");
					String s1 = stringbuilder7.append(httpSetting.getId()).append("- Can not format json -->> ").toString();
					Log.d("HttpGroup", s1, jsonexception);
				}
				HttpError httperror2 = new HttpError(jsonexception);
				throwError(httperror2);
				connectionRetry = true;
				
				return ;
			}}
//			Integer integer = null;
//			try
//			{
//				integer = Integer.valueOf(httpResponse.getJSONObject().getString("code"));
//			}
//			catch (NumberFormatException numberformatexception)
//			{
//				if (Log.D)
//				{
//					StringBuilder stringbuilder1 = new StringBuilder("id:");
//					String s2 = stringbuilder1.append(httpSetting.getId()).append("- Can not format jsonCode -->> ").toString();
//					Log.d("HttpGroup", s2, numberformatexception);
//				}
//				HttpError httperror3 = new HttpError(numberformatexception);
//				throwError(httperror3);
//				connectionRetry = true;
//				
//				return ;
//			}
//			catch (JSONException jsonexception1)
//			{
//				if (Log.D)
//				{
//					StringBuilder stringbuilder9 = new StringBuilder("id:");
//					String s12 = stringbuilder9.append(httpSetting.getId()).append("- not find jsonCode -->> ").toString();
//					Log.d("HttpGroup", s12, jsonexception1);
//				}
//				HttpError httperror4 = new HttpError(jsonexception1);
//				throwError(httperror4);
//				connectionRetry = true;
//				
//				return ;
//			}
//	
//			if (integer != null && integer.intValue() != 0)
//			{
//				Integer integer1 = Integer.valueOf(9);
//				if (integer.equals(integer1))
//				{
//					HttpGroup.queryMd5Key(continueListener);
//					
//					synchronized (this){
//						try {
//							if (Log.D)
//							{
//								StringBuilder stringbuilder2 = new StringBuilder("id:");
//								stringbuilder2.append(httpSetting.getId()).append("- encrypt wait start -->> ");
//								Log.d("HttpGroup", stringbuilder2.append(httpSetting.getUrl()).toString());
//							}
//							
//							wait();
//							
//							if (Log.D)
//							{
//								StringBuilder stringbuilder4 = new StringBuilder("id:");
//								stringbuilder4.append(httpSetting.getId()).append("- encrypt wait end -->> ");
//								Log.d("HttpGroup", stringbuilder4.append(httpSetting.getUrl()).toString());
//							}
//							
//							connectionRetry = true;
//							
//							return ;
//						} catch(InterruptedException interruptedexception) {
//							interruptedexception.printStackTrace();
//							
//							connectionRetry = true;
//							
//							return ;
//						}
//					}
//				}
//				
//				Integer integer2 = Integer.valueOf(10);
//				if (integer.equals(integer2))
//				{
//					setModule(3);
//					connectionRetry = true;
//				} else if (integer.intValue() == -1) 
//				{
//					HttpError httperror5 = new HttpError();
//					httperror5.setErrorCode(3);
//					httperror5.setJsonCode(integer.intValue());
//					httperror5.setHttpResponse(httpResponse);
//					throwError(httperror5);
//					connectionRetry = true;
//				} else if (integer.intValue() == 30 || integer.intValue() == 1 || integer.intValue() == 2)
//				{
//					final BaseActivity myActivity = httpGroup.getHttpGroupSetting().getMyActivity();
//					if (myActivity != null)
//					{
//						myActivity.post(new Runnable() {
//							public void run()
//							{
//								Toast.makeText(myActivity, R.string.server_busy, 1).show();
//							}
//						});
//					}
//					HttpError httperror6 = new HttpError();
//					httperror6.setErrorCode(3);
//					httperror6.setJsonCode(integer1.intValue());
//					httperror6.setHttpResponse(httpResponse);
//					httperror6.setNoRetry(true);
//					throwError(httperror6);
//				}
//			}
//		}
//		else
//		{
//			HttpError httperror = new HttpError();
//			httperror.setErrorCode(2);
//			httperror.setResponseCode(404);
//			throwError(httperror);
//			connectionRetry = true;
//			
//			return;
//		}
//	}

	protected void nextHandler() {
		if (Log.D) { 
			Log.d("HttpRequest", "nextHandler");
		}
		
		int i = currentHandlerIndex;
		if (Log.D) {
			String logString = new StringBuilder("id:")
					.append(httpSetting.getId())
					.append("- nextHandler() i -->> ")
					.append(currentHandlerIndex).toString();
			Log.d("HttpGroup", logString);
		}
		currentHandlerIndex = currentHandlerIndex + 1;
		if (i < handlers.size()) {

			((Handler) handlers.get(i)).run();
		}
	}

	private void notifyUser(final HttpDialogController httpDialogController) {
		if (Log.D) { 
			Log.d("HttpRequest", "notifyUser");
		}
		
		ArrayList arrayList = null;
		Activity activity = httpGroup.getHttpGroupSetting().getMyActivity();
		if (activity != null) {
			boolean flag = false;
			HashMap hashmap = HttpGroup.alertDialogStateMap;
			synchronized(hashmap) {
				arrayList = (ArrayList) hashmap.get(activity);
				if (arrayList == null) {
					arrayList = new ArrayList();
					hashmap.put(activity, arrayList);
					flag = true;
					arrayList.add(this);
				}
			}
			
			if (Log.D) {
				String s = new StringBuilder("id:").append(httpSetting.getId())
						.append("- notifyUser() -->> result = ").append(flag)
						.toString();
				Log.d("HttpGroup", s);
			}
			httpDialogController.init(arrayList, activity);
//			mHandler.post(new Runnable() {
//
//				@Override
//				public void run() {
//					httpDialogController.show();
//				}
//
//			});
			if (Log.D) {
				String s1 = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- dialog wait start -->> ").toString();
				Log.d("HttpGroup", s1);
			}
			try {
				synchronized(this){
				wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (Log.D) {
				String s2 = new StringBuilder("id:")
						.append(httpSetting.getId())
						.append("- dialog wait end -->> ").toString();
				Log.d("HttpGroup", s2);
			}
		}
	}

	private void post() throws Exception {
		if (Log.D) { 
			Log.d("HttpRequest", "post");
		}
		get();
		/*
		String logString = null;
		byte[] bytes;
		if (Log.D) {
			logString = new StringBuilder("id:").append(httpSetting.getId())
					.append("- post() -->> ").toString();
			Log.d("HttpGroup", logString);
		}
		httpResponse = new HttpResponse(conn);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		if (httpSetting.getMapParams() == null) {
			bytes = "body=".getBytes();
		} else {
			StringBuilder requestString = new StringBuilder();
			Map map = httpSetting.getMapParams();
			Iterator iterator = map.keySet().iterator();
			for (; iterator.hasNext();) {
				String key = (String) iterator.next();
				if (!(ConstFuncId.FUNCTION_ID).equals(key)) {
					String value = (String) map.get(key);
					if (Log.I) {
						logString = new StringBuilder("id:")
								.append(httpSetting.getId())
								.append("- param key and value -->> ")
								.append(key).append("：").append(value)
								.toString();
						Log.i("HttpGroup", logString);
					}
					requestString.append(key).append("=").append(value);
					if (iterator.hasNext())
						requestString.append("&");
				}
			}
			bytes = requestString.toString().getBytes();
		}
		conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		if (Log.D) {
			logString = new StringBuilder("id:").append(httpSetting.getId())
					.append("- post() -->> 1").toString();
			Log.d("HttpGroup", logString);
		}
		OutputStream os = conn.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		if (Log.D) {
			logString = new StringBuilder("id:").append(httpSetting.getId())
					.append("- post() -->> 2").toString();
			Log.d("HttpGroup", logString);
		}
		dos.write(bytes);
		if (Log.D) {
			logString = new StringBuilder("id:").append(httpSetting.getId())
					.append("- post() -->> ready").toString();
			Log.d("HttpGroup", logString);
		}
		dos.flush();
		if (Log.D) {
			logString = new StringBuilder("id:").append(httpSetting.getId())
					.append("- post() -->> ok").toString();
			Log.d("HttpGroup", logString);
		}
		*/
	}

	private void save() {
		if (Log.D) { 
			Log.d("HttpRequest", "save");
		}
		
		String logString = null;
		FileService.Directory directory = null;
		boolean flag = false;
		if (this.httpSetting.isLocalFileCache()) {
			switch (this.httpSetting.getType()) {
			case ConstHttpProp.TYPE_JSON:
				if (Log.D) {
					logString = new StringBuilder("id:")
							.append(httpSetting.getId())
							.append("- save json file start -->> ").toString();
					Log.d("HttpGroup", logString);
				}
				directory = FileService.getDirectory(ConstFileProp.INTERNAL_TYPE_CACHE);
				if (directory != null) {
					String md5 = String.valueOf(httpSetting.getMd5());
					String jsonFilename = (new StringBuilder(md5)).append(
							".json").toString();
					if (httpResponse != null) {
						flag = FileService.saveToSDCard(FileService.getDirectory(2), md5,httpResponse.getString());
						if (flag) {
							long l = httpSetting.getLocalFileCacheTime();
							CacheFileItem cachefile = new CacheFileItem(jsonFilename,
									httpSetting.getLocalFileCacheTime());
							cachefile.setDirectory(directory);
							CacheFileTableDBHelper.insertOrUpdate(cachefile);
						}
						if (Log.D) {
							logString = new StringBuilder("id:")
									.append(httpSetting.getId())
									.append("- save json file -->> ")
									.append(flag).toString();
							Log.d("HttpGroup", logString);
						}
					}
				}
				break;
			case ConstHttpProp.TYPE_IMAGE:
				if (Log.D) {
					logString = new StringBuilder("id:")
							.append(httpSetting.getId())
							.append("- save image file start -->> ").toString();
					Log.d("HttpGroup", logString);
				}
				directory = FileService.getDirectory(ConstFileProp.INTERNAL_TYPE_FILE);
				if (directory != null) {
					String md5 = String.valueOf(httpSetting.getMd5());
					String imageFilename = (new StringBuilder(md5)).append(
							".image").toString();
					if (httpResponse != null) {
						byte bytes[] = httpResponse.getInputData();
						flag = FileService.saveToSDCard(directory, imageFilename, bytes);
						if (flag) {
							CacheFileItem cachefile1 = new CacheFileItem(imageFilename,
									httpSetting.getLocalFileCacheTime());
							cachefile1.setDirectory(directory);
							CacheFileTableDBHelper.insertOrUpdate(cachefile1);
						}
						if (Log.D) {
							logString = new StringBuilder("id:")
									.append(httpSetting.getId())
									.append("- save image file -->> ")
									.append(flag).toString();
							Log.d("HttpGroup", logString);
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	protected void setModule(int funcId) {
		if (Log.D) { 
			Log.d("HttpRequest", "setModule");
		}
		
		if ((this.httpSetting.getFunctionId() == null)
				|| (HttpGroup.mModules == null))
			return;
		try {
			JSONObjectProxy jsonObjectProxy = HttpGroup.mModules;
			String functionId = httpSetting.getFunctionId();
			jsonObjectProxy.put(functionId, funcId);
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
	}

	public void stop() {
		stopFlag = true;
	}

	public void throwError(HttpError error) {
		if (Log.D) { 
			Log.d("HttpRequest", "throwError");
		}
		
		getErrorList().add(error);
		int errListSize = getErrorList().size();
		error.setTimes(errListSize);
		if (Log.I) {
			String errMsg = new StringBuilder("id:").append(httpSetting.getId())
				.append("- HttpError -->> ").append(error).toString();
			Log.i("HttpGroup", errMsg);
		}
		checkErrorInteraction();
	}

	public void typeHandler() {
		nextHandler();
	}

	private void urlParam() {
		if (Log.D) { 
			Log.d("HttpRequest", "urlParam");
		}
		
		StringBuilder sb = null;
		if (!httpSetting.isGet()) {
			if (httpSetting.getMapParams() != null)
				if (httpGroup.isReportUserInfoFlag()) {
					sb = (new StringBuilder(
							String.valueOf(httpSetting.getUrl())));
					String s1 = (String) httpSetting.getMapParams().get("functionId");
					sb.append(s1);
					//String s2 = StatisticsReportUtil.getReportString(httpSetting.isNeedGlobalInitialization());
					//httpSetting.setUrl(sb.append(s2).toString());
					httpSetting.setUrl(sb.toString());
				} else {
					sb = (new StringBuilder(
							String.valueOf(httpSetting.getUrl()))).append("?")
							.append("functionId=");
					String s5 = (String) httpSetting.getMapParams().get("functionId");
					httpSetting.setUrl(sb.append(s5).toString());
				}
		} else {
			String url = httpSetting.getUrl();
			Map map = httpSetting.getMapParams();
			String functionId=(String) map.get("functionId");
			url=url+functionId;
			JSONObject json=httpSetting.getJsonParams();
			String completeUrl = addParams(url, json);
			if (httpGroup.isReportUserInfoFlag()
					&& httpSetting.getType() == ConstHttpProp.TYPE_JSON) {
				sb = new StringBuilder(String.valueOf(completeUrl));
				//String s10 = StatisticsReportUtil.getReportString(httpSetting.isNeedGlobalInitialization());
				//httpSetting.setUrl(sb.append(s10).toString());
				httpSetting.setUrl(sb.toString());
			} else {
				httpSetting.setUrl(completeUrl);
			}
		}
	}
	public String addParams(String s, JSONObject json) {
		String retUrlAndParams = null;
		if (json != null) {
			StringBuilder stringbuilder;
			Iterator<?> iterator = null;			
			stringbuilder = new StringBuilder(s);
			int i = s.indexOf("?");
			if (i == -1) {
				stringbuilder.append("?");
			} else {
				String s2 = s.substring(i + 1);
				if (!TextUtils.isEmpty(s2) && !s2.endsWith("&"))
					stringbuilder.append("&");
			}
			iterator =json.keys();
			for (; iterator.hasNext();) {
				String key = (String) iterator.next();
				String value;
				try {
					value = json.get(key)+"";
					stringbuilder.append(key).append("=").append(java.net.URLEncoder.encode(value));	
					if (iterator.hasNext())
						stringbuilder.append("&");
				} catch (JSONException e) {
					e.printStackTrace();
				}					
			}
			retUrlAndParams = stringbuilder.append("&language=").append(LanguageUtil.getLanguage()).toString();			
		} else {
			retUrlAndParams = s;
		}
		return retUrlAndParams;
	}
}
