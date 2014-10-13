package com.jiuzhansoft.ehealthtec.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.jiuzhansoft.ehealthtec.config.Configuration;
import com.jiuzhansoft.ehealthtec.http.HttpGroup.OnReadyListener;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.http.utils.Base64;
import com.jiuzhansoft.ehealthtec.log.Log;



public abstract class HttpGroup{
	// 抽象借口
	public interface CompleteListener {
		public abstract void onComplete(Bundle paramBundle);
	}

	public interface CustomOnAllListener extends HttpGroup.OnAllListener {
	}

	interface Handler {
		public abstract void run();
	}

	public abstract interface HttpSettingParams {
		public abstract void putJsonParam(String paramString, Object paramObject);

		public abstract void putMapParams(String paramString1,
				String paramString2);
	}

	public interface HttpTaskListener {
	}

	public interface OnAllListener extends OnStartListener, OnEndListener,
			OnErrorListener, OnProgressListener {
	}

	public interface OnCommonListener extends OnEndListener, OnErrorListener,
			OnReadyListener {
	}

	public interface OnEndListener extends HttpTaskListener {
		public abstract void onEnd(HttpResponse response);
	}

	public interface OnErrorListener extends HttpTaskListener {
		public abstract void onError(HttpError httpError);
	}

	public interface OnGroupEndListener {
		public abstract void onEnd();
	}

	public interface OnGroupErrorListener {
		public abstract void onError();
	}

	public interface OnGroupProgressListener {
		public abstract void onProgress(int i, int j);
	}

	public interface OnGroupStartListener {
		public abstract void onStart();
	}

	public interface OnGroupStepListener {
		public abstract void onStep(int i, int j);
	}

	public interface OnProgressListener extends HttpTaskListener {
		public abstract void onProgress(int i, int j);
	}

	public interface OnReadyListener extends HttpTaskListener {
		public abstract void onReady(HttpSettingParams httpSettingParams);
	}

	public interface OnStartListener extends HttpTaskListener {
		public abstract void onStart();
	}

	public interface StopController {
		public abstract boolean isStop();

		public abstract void stop();
	}

	// Static variables--start
	static final HashMap<?, ?> alertDialogStateMap = new HashMap<Object, Object>();
	static final int attempts = Integer.parseInt(Configuration.getProperty("attempts"));
	static final int attemptsTime = Integer.parseInt(Configuration.getProperty("attemptsTime"));
	static String charset = "UTF-8";

	static final int connectTimeout = Integer.parseInt(Configuration.getProperty("connectTimeout"));

	protected static String cookies = null;

	static final String host = Configuration.getProperty("host");
	private static int httpIdCounter = 0;
	static String mMd5Key;
	static JSONObjectProxy mModules;

	protected static final int readTimeout = Integer.parseInt(Configuration.getProperty("readTimeout"));

	// non-static varialbes stat
	private int completesCount;

	private HttpGroupSetting httpGroupSetting;

	protected ArrayList<HttpRequest> httpList;

	private int maxProgress;

	private int maxStep;
	// group listener interface;
	private OnGroupEndListener onGroupEndListener;
	private OnGroupErrorListener onGroupErrorListener;
	private OnGroupProgressListener onGroupProgressListener;
	private OnGroupStartListener onGroupStartListener;
	private OnGroupStepListener onGroupStepListener;

	protected int priority;
	private int progress;
	private boolean reportUserInfoFlag;
	private int step;
	protected int type;
	private boolean useCaches;

	public HttpGroup() {
		// TODO Auto-generated constructor stub
	}
	// constructor
	public HttpGroup(HttpGroupSetting groupSetting) {
		if (Log.D) { 
			Log.d("HttpGroup", "HttpGroup(setting)");
		}
		
		useCaches = false;
		httpList = new ArrayList<HttpRequest>();
		reportUserInfoFlag = true;
		completesCount = 0;
		maxProgress = 0;
		progress = 0;
		maxStep = 0;
		step = 0;
		httpGroupSetting = groupSetting;
		priority = groupSetting.getPriority();
		type = groupSetting.getType();
	}

	public static void cleanCookies() {
		cookies = null;
	}

	public static String mergerUrlAndParams(String s, Map<?, ?> map) {
		if (Log.D) { 
			Log.d("HttpGroup", "mergerUrlAndParams");
		}
		
		String retUrlAndParams = null;
		if (map != null) {
			StringBuilder stringbuilder;
			Iterator<?> iterator = null;
			Set<?> set = map.keySet();
			if (set == null || set.isEmpty()) {
				retUrlAndParams = s;
			} else {
				stringbuilder = new StringBuilder(s);
				int i = s.indexOf("?");
				if (i == -1) {
					stringbuilder.append("?");
				} else {
					String s2 = s.substring(i + 1);
					if (!TextUtils.isEmpty(s2) && !s2.endsWith("&"))
						stringbuilder.append("&");
				}
				iterator = set.iterator();
				for (; iterator.hasNext();) {
					String key = (String) iterator.next();
					String value = (String) map.get(key);
					stringbuilder.append(key).append("=").append(value);
					if (iterator.hasNext())
						stringbuilder.append("&");
				}
				retUrlAndParams = stringbuilder.toString();
			}

		} else {
			retUrlAndParams = s;
		}

		return retUrlAndParams;
	}

	public static void queryMd5Key(CompleteListener completelistener) {
		if (Log.D) { 
			Log.d("HttpGroup", "queryMd5Key(listener)");
		}
		
		HttpGroupSetting httpgroupsetting = new HttpGroupSetting();
		httpgroupsetting.setPriority(ConstHttpProp.PRIORITY_JSON);
		httpgroupsetting.setType(ConstHttpProp.TYPE_JSON);
		queryMd5Key(((HttpGroup) (new HttpGroupaAsynPool(httpgroupsetting))),
				completelistener);
	}

	public static void queryMd5Key(HttpGroup httpgroup,
			final CompleteListener listener) {
		
		if (Log.D) { 
			Log.d("HttpGroup", "queryMd5Key(httpgroup, listener)");
		}
		
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setFunctionId("key");
		JSONObject jsonobject = new JSONObject();
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new OnAllListener() {

			@Override
			public void onStart() {
				if (Log.D) { 
					Log.d("HttpGroup", "queryMd5Key.OnAllListener.onStart");
				}
			}

			@Override
			public void onEnd(HttpResponse response) {
				if (Log.D) { 
					Log.d("HttpGroup", "queryMd5Key.OnAllListener.onEnd");
				}
				
				String key = response.getJSONObject().getStringOrNull("key");
				if (key != null) {
					int i = 0;
					int keyLen = 0;
					byte bytes[] = null;
					bytes = Base64.decode(key);
					keyLen = bytes.length;

					while (i < keyLen) {
						bytes[i] = (byte) (~bytes[i]);
						i++;
					}
					String s1 = new String(bytes);
					if (Log.D) {
						String s2 = (new StringBuilder("md5Key -->> ")).append(
								s1).toString();
						Log.d("HttpGroup", s2);
					}
					HttpGroup.setMd5Key(s1);
					if (listener != null)
						listener.onComplete(null);
				}
			}

			@Override
			public void onError(HttpError httpError) {
				if (Log.D) { 
					Log.d("HttpGroup", "queryMd5Key.OnAllListener.onError");
				}
				
				if (listener != null)
					listener.onComplete(null);
			}

			@Override
			public void onProgress(int i, int j) {

			}

		});
		httpsetting.setPost(true);
		httpgroup.add(httpsetting);
	}

	public static void setModules(JSONObjectProxy jsonobjectproxy) {
		mModules = jsonobjectproxy;
	}

	public static void setCookies(String s) {
		cookies = s;
	}

	public static void setMd5Key(String s) {
		mMd5Key = s;
	}

	public HttpRequest add(HttpSetting httpSetting) {
		if (Log.D) { 
			Log.d("HttpGroup", "add");
		}
		
		httpIdCounter += 1;
		httpSetting.setId(httpIdCounter);
		tryEffect(httpSetting);
		if (Log.I) {
			String s = new StringBuilder("id:").append(httpSetting.getId())
					.append("- onStart -->> ").toString();
			Log.i("HttpGroup", s);
		}
		httpSetting.onStart();
		HttpRequest request = new HttpRequest(httpSetting, this);
		OnReadyListener readyListener = httpSetting.getOnReadyListener();
		if (readyListener != null) {
			if (Log.D) { 
				Log.d("HttpGroup", "add -- ready listener isn't null");
			}
			
			new HttpGroup_Thread(this, readyListener, httpSetting, request).start();
		} else {
			if (Log.D) { 
				Log.d("HttpGroup", "add -- ready listener is null");
			}
			add2(request);
		}
		return request;
	}

	public HttpRequest add(String funcId, JSONObject jsonObj, OnAllListener listener) {
		if (Log.D) { 
			Log.d("HttpGroup", "add(funcId, json, listener)");
		}
		
		HttpSetting httpSetting = new HttpSetting();
		httpSetting.setFunctionId(funcId);
		httpSetting.setJsonParams(jsonObj);
		httpSetting.setListener(listener);
		return add(httpSetting);
	}

	public HttpRequest add(String url, Map<String, String> map, OnAllListener listener) {
		if (Log.D) { 
			Log.d("HttpGroup", "add(funcId, map, listener)");
		}
		
		HttpSetting httpSetting = new HttpSetting();
		httpSetting.setUrl(url);
		httpSetting.setMapParams(map);
		httpSetting.setListener(listener);
		return add(httpSetting);
	}

	public void add2(HttpRequest request) {
		if (Log.D) { 
			Log.d("HttpGroup", "add2(request)");
		}
		
		HttpSetting httpSetting = request.getHttpSetting();

		if ((Log.I) && (httpSetting.getFunctionId() != null)) {
			String s = new StringBuilder("id:").append(httpSetting.getId())
					.append("- functionId -->> ")
					.append(httpSetting.getFunctionId()).toString();
			Log.i("HttpGroup", s);
		}
		if ((Log.I) && (httpSetting.getUrl() != null)) {
			String s = new StringBuilder("id:").append(httpSetting.getId())
					.append("- url -->> ").append(httpSetting.getUrl())
					.toString();
			Log.i("HttpGroup", s);
		}
		if (httpSetting.getType() == 0) {
			httpSetting.setType(this.type);
		}
		if (httpSetting.getPriority() == 0) {
			httpSetting.setPriority(this.priority);
		}
		if (httpSetting.getPriority() == 0)
			switch (httpSetting.getType()) {
			case ConstHttpProp.TYPE_JSON:
				httpSetting.setPriority(ConstHttpProp.PRIORITY_JSON);
				break;
			case ConstHttpProp.TYPE_IMAGE:
				httpSetting.setPriority(ConstHttpProp.PRIORITY_IMAGE);
				break;
			case ConstHttpProp.TYPE_FILE:
				httpSetting.setPriority(ConstHttpProp.PRIORITY_FILE);
				break;
			default:

			}
		execute(request);
	}

	// here--------------
	protected void addCompletesCount() {
		if (Log.D) { 
			Log.d("HttpGroup", "addCompletesCount");
		}
		
		completesCount = completesCount + 1;
		if (completesCount == httpList.size())
			onEnd();
	}

	public void addMaxProgress(int i) {
		if (Log.D) { 
			Log.d("HttpGroup", "addMaxProgress");
		}
		
		maxProgress = maxProgress + i;
		onProgress(maxProgress, progress);
	}

	protected void addMaxStep(int i) {
		if (Log.D) { 
			Log.d("HttpGroup", "addMaxStep");
		}
		
		int j = maxStep + i;
		maxStep = j;
		int k = maxStep;
		int l = step;
		onStep(k, l);
	}

	protected void addProgress(int i) {
		if (Log.D) { 
			Log.d("HttpGroup", "addProgress");
		}
		
		progress += i;
		onProgress(maxProgress, progress);
	}

	protected void addStep(int i) {
		if (Log.D) { 
			Log.d("HttpGroup", "addStep");
		}
		
		step = step + i;
		onStep(maxStep, step);
	}

	public abstract void execute(HttpRequest paramHttpRequest);

//	public void onDestroy() {
//	}

	public HttpGroupSetting getHttpGroupSetting() {
		return httpGroupSetting;
	}

	public boolean isReportUserInfoFlag() {
		return reportUserInfoFlag;
	}

	public boolean isUseCaches() {
		return useCaches;
	}

	protected void onEnd() {
		if (onGroupEndListener != null)
			onGroupEndListener.onEnd();
	}

	protected void onError() {
		if (onGroupErrorListener != null)
			onGroupErrorListener.onError();
	}

	private void onProgress(int i, int j) {
		if (onGroupProgressListener != null)
			onGroupProgressListener.onProgress(i, j);
	}

	protected void onStart() {
		if (onGroupStartListener != null)
			onGroupStartListener.onStart();
	}

	private void onStep(int i, int j) {
		if (onGroupStepListener != null)
			onGroupStepListener.onStep(i, j);
	}

	public void setOnGroupEndListener(OnGroupEndListener endListener) {
		onGroupEndListener = endListener;
	}

	public void setOnGroupErrorListener(OnGroupErrorListener errorListener) {
		onGroupErrorListener = errorListener;
	}

	public void setOnGroupProgressListener(
			OnGroupProgressListener progressListener) {
		onGroupProgressListener = progressListener;
	}

	public void setOnGroupStartListener(OnGroupStartListener startListener) {
		onGroupStartListener = startListener;
	}

	public void setOnGroupStepListener(OnGroupStepListener stepListener) {
		onGroupStepListener = stepListener;
	}

	public void setHttpGroupSetting(HttpGroupSetting httpGroupSetting) {
		this.httpGroupSetting = httpGroupSetting;
	}

	public void setReportUserInfoFlag(boolean reportUserInfoFlag) {
		this.reportUserInfoFlag = reportUserInfoFlag;
	}

	public void setUseCaches(boolean useCaches) {
		this.useCaches = useCaches;
	}

	private void tryEffect(HttpSetting httpsetting) {
		if (Log.D) { 
			Log.d("HttpGroup", "tryEffect");
		}
		Activity activity = httpGroupSetting.getMyActivity();
		int i = httpsetting.getEffect();
		if ((ConstHttpProp.EFFECT_DEFAULT == i) && (httpsetting.getEffectState() == ConstHttpProp.EFFECT_STATE_NO)) {
			DefaultEffectHttpListener defaulteffecthttplistener = new DefaultEffectHttpListener(
					httpsetting, activity);
			httpsetting.setListener(defaulteffecthttplistener);
		}
	}
}

// 内部类
class HttpGroup_Thread extends Thread {
	HttpGroup group;
	HttpRequest httpRequest;
	HttpSetting httpSetting;
	HttpGroup.OnReadyListener onReadyListener;

	public HttpGroup_Thread(HttpGroup httpGroup, OnReadyListener readyListener, HttpSetting httpSetting, HttpRequest request) {
		if (Log.D) { 
			Log.d("HttpGroup", "HttpGroup_Thread.HttpGroup_Thread");
		}
		
		this.httpSetting = httpSetting;
		this.httpRequest = request;
		this.onReadyListener = readyListener;
		this.group = httpGroup;
	}

	public void run() {
		if (Log.D) { 
			Log.d("HttpGroup", "HttpGroup_Thread.run");
		}
		
		onReadyListener.onReady(httpSetting);
		group.add2(httpRequest);
	}
}
