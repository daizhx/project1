package com.jiuzhansoft.ehealthtec.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.constant.ConstSysConfig;
import com.jiuzhansoft.ehealthtec.http.utils.FileGuider;
import com.jiuzhansoft.ehealthtec.http.utils.Md5Encrypt;
import com.jiuzhansoft.ehealthtec.http.utils.URLParamMap;

public class HttpSetting implements HttpGroup.HttpSettingParams {
	private int cacheMode;
	private int connectTimeout;
	private int effect;
	private int effectState;
	private String finalUrl;
	private String functionModal;
	private String functionId;
	private int id;
	private JSONObject jsonParams;
	//REST模式
	private ArrayList<String> arrayListParams;
	private boolean localFileCache;
	private long localFileCacheTime;
	private boolean localMemoryCache;
	private Map mapParams;
	private String md5;
	private boolean notifyUser;
	private boolean notifyUserWithExit;
	private HttpGroup.OnEndListener onEndListener;
	private HttpGroup.OnErrorListener onErrorListener;
	private HttpGroup.OnProgressListener onProgressListener;
	private HttpGroup.OnReadyListener onReadyListener;
	private HttpGroup.OnStartListener onStartListener;
	// private boolean get;
	public String requestMethod;
	private int priority;
	private int readTimeout;
	private FileGuider savePath;
	private int type;
	private String url;
	// is to show progress of the http task -- daizhx
	private boolean showProgress;

	public HttpSetting() {
		// Ĭ��Ϊget��ʽ
		requestMethod = ConstSysConfig.REQUEST_METHOD;
		notifyUser = false;
		notifyUserWithExit = false;
		localMemoryCache = false;
		localFileCache = false;
		localFileCacheTime = 65535L;
		effect = ConstHttpProp.EFFECT_DEFAULT;
		effectState = ConstHttpProp.EFFECT_STATE_NO;
		cacheMode = ConstHttpProp.CACHE_MODE_AUTO;
	}

	public int getCacheMode() {
		return cacheMode;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public int getEffect() {
		return effect;
	}

	public int getEffectState() {
		return effectState;
	}

	public String getFinalUrl() {
		return finalUrl;
	}

	public String getFunctionId() {
		return functionId;
	}

	public int getId() {
		return id;
	}

	public JSONObject getJsonParams() {
		if (jsonParams == null) {
			jsonParams = new JSONObject();
		}
		return jsonParams;
	}
	//REST模式 GET请求参数
	public ArrayList<String> getArrayListParams(){
		if(arrayListParams == null){
			arrayListParams = new ArrayList<String>();
		}
		return arrayListParams;
	}

	public long getLocalFileCacheTime() {
		return localFileCacheTime;
	}

	public Map<String, String> getMapParams() {
		return mapParams;
	}

	public String getMd5() {

		String retMd5 = null;
		String url = null;
		if (md5 == null) {
			url = getUrl();
			if (url != null) {
				// ȡ�õ�ַ�����������
				int i = 0;
				int j = 0;
				while (j < 3) {
					int k = i + 1;
					i = url.indexOf("/", k);
					j++;
				}
				if (i != -1) {
					String s2 = getUrl().substring(i);
					if (!isGet()) {
						md5 = Md5Encrypt.md5(new StringBuilder(String
								.valueOf(s2)).append(getJsonParams())
								.toString());
						retMd5 = md5;
					} else {
						md5 = Md5Encrypt.md5(s2);
						retMd5 = md5;
					}
					// if (Log.D) {
					// StringBuilder stringbuilder1 = (new StringBuilder(
					// "urlPath -->> ")).append(s2).append(
					// " md5 -->> ");
					// String s6 = stringbuilder1.append(md5).toString();
					// Log.d("HttpGroup", s6);
					// }
				} else {
					retMd5 = null;
				}

			} else {
				retMd5 = null;
			}

		} else
			retMd5 = md5;
		return retMd5;
	}

	public HttpGroup.OnEndListener getOnEndListener() {
		return onEndListener;
	}

	public HttpGroup.OnErrorListener getOnErrorListener() {
		return onErrorListener;
	}

	public HttpGroup.OnProgressListener getOnProgressListener() {
		return onProgressListener;
	}

	public HttpGroup.OnReadyListener getOnReadyListener() {
		return onReadyListener;
	}

	public HttpGroup.OnStartListener getOnStartListener() {
		return onStartListener;
	}

	public int getPriority() {
		return priority;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public FileGuider getSavePath() {
		return savePath;
	}

	public int getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public boolean isLocalFileCache() {
		return localFileCache;
	}

	public boolean isLocalMemoryCache() {
		return localMemoryCache;
	}

	public boolean isNotifyUser() {
		return notifyUser;
	}

	public boolean isNotifyUserWithExit() {
		return notifyUserWithExit;
	}

	public boolean isGet() {
		return requestMethod.equals("GET");
	}

	public void setRequestMethod(String s) {
		requestMethod = s;
	}

	public void onEnd(final HttpResponse paramHttpResponse) {
		if (onEndListener == null){
			return;
		}
		//把回调函数放到主线程中执行
		Handler handler = new Handler(paramHttpResponse.mContext.getMainLooper());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onEndListener.onEnd(paramHttpResponse);	
			}
		});
		
	}

	public void onError(HttpError paramHttpError) {
		if (onErrorListener == null)
			return;
		onErrorListener.onError(paramHttpError);
	}

	public void onProgress(int paramInt1, int paramInt2) {
		if (onProgressListener == null)
			return;
		onProgressListener.onProgress(paramInt1, paramInt2);
	}

	public void onStart() {
		if (onStartListener == null)
			return;
		onStartListener.onStart();
	}

	public void putJsonParam(String name, Object object) {
		if (jsonParams == null) {
			jsonParams = new JSONObject();
		}
		try {
			jsonParams.put(name, object);
		} catch (JSONException exception) {

		}
	}
	
	//REST模式 GET方式添加参数
	public void addArrayListParam(String val){
		if(arrayListParams == null){
			arrayListParams = new ArrayList<String>();
		}
		arrayListParams.add(val);
	}

	public void putMapParams(String name, String value) {
		if (mapParams == null) {
			mapParams = new URLParamMap(HttpGroup.charset);
		}
		mapParams.put(name, value);
	}

	public void setCacheMode(int cacheMode) {
		this.cacheMode = cacheMode;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public void setEffectState(int effectState) {
		this.effectState = effectState;
	}

	public void setFinalUrl(String finalUrl) {
		this.finalUrl = finalUrl;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setJsonParams(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				String s = jsonObject.toString();
				jsonParams = new JSONObject(s);
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}

	}
	
	public void setArrayListParams(ArrayList<String> a){
			arrayListParams = a;
	}

	public void setListener(HttpGroup.HttpTaskListener listener) {

		if (listener instanceof HttpGroup.CustomOnAllListener) {
			setEffect(ConstHttpProp.EFFECT_NO);
		}

		if (listener instanceof DefaultEffectHttpListener) {
			setEffectState(ConstHttpProp.EFFECT_STATE_YES);
		}

		if (listener instanceof HttpGroup.OnErrorListener) {
			onErrorListener = (HttpGroup.OnErrorListener) listener;
		}

		if (listener instanceof HttpGroup.OnStartListener) {
			onStartListener = (HttpGroup.OnStartListener) listener;
		}

		if (listener instanceof HttpGroup.OnProgressListener) {
			onProgressListener = (HttpGroup.OnProgressListener) listener;
		}

		if (listener instanceof HttpGroup.OnEndListener) {
			onEndListener = (HttpGroup.OnEndListener) listener;
		}

		if (listener instanceof HttpGroup.OnReadyListener) {
			onReadyListener = (HttpGroup.OnReadyListener) listener;
		}
	}

	public void setLocalFileCache(boolean localFileCache) {
		this.localFileCache = localFileCache;
	}

	public void setLocalFileCacheTime(long localFileCacheTime) {
		this.localFileCacheTime = localFileCacheTime;
	}

	public void setLocalMemoryCache(boolean localMemoryCache) {
		this.localMemoryCache = localMemoryCache;
	}

	public void setMapParams(Map map) {

		if (map != null) {
			Iterator iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String s = (String) iterator.next();
				String s1 = (String) map.get(s);
				putMapParams(s, s1);
			}
		}
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public void setNotifyUser(boolean notifyUser) {
		this.notifyUser = notifyUser;
	}

	public void setNotifyUserWithExit(boolean notifyUserWithExit) {
		this.notifyUserWithExit = notifyUserWithExit;
	}

	// public void setPost(boolean get) {
	// this.get = get;
	// }
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setSavePath(FileGuider savePath) {
		this.savePath = savePath;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setShowProgress(boolean b) {
		showProgress = b;
	}

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setFunctionModal(String modal) {
		this.functionModal = modal;
	}

	public String getFunctionModal() {
		return this.functionModal;
	}
}