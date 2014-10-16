package com.jiuzhansoft.ehealthtec.update;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupSetting;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpRequest;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * singleton
 * 
 * @author daizhx
 * 
 */
public class UpdateManager {
	private static final String TAG = "updateManager";
	private static UpdateManager mUpdateManager;
	private Context mContext;
	private int localVersion;
	private int serverVersion;
	private String downloadUrl;
	private static final String GET_NEW_VERSION = "getNewVersion";

	public static UpdateManager getUpdateManager(Context context) {
		if (mUpdateManager == null) {
			mUpdateManager = new UpdateManager(context);
		}
		return mUpdateManager;
	}

	private UpdateManager(Context context) {
		mContext = context;
	}

	public void checkAndUpdate() {
		// 获取当前版本
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
			Log.d(TAG, "localVersion="+localVersion);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取服务器上的最新版本及URL
		HttpSetting	httpsetting = new HttpSetting();
		httpsetting.setFunctionId(GET_NEW_VERSION);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("sysName", "1");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httpsetting.setJsonParams(jsonObject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			
			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onError");
			}
			
			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onEnd:response"+response);
				try {
					JSONObjectProxy json = response.getJSONObject();
					//it is a bug,not fixed,so added this code
					if(json == null)return;
					String versionCode = json.get("version").toString();
					Log.d(TAG, "serverVersion="+versionCode);
					downloadUrl = response.getJSONObject().get("url").toString();
//					downloadUrl = response.getJSONObject().get("url").toString();
					Log.d(TAG, "downloadUrl="+downloadUrl);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				Log.d(TAG, "downloadUrl="+downloadUrl);
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}
		});
		
		httpsetting.setShowProgress(false);
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
		localHttpGroupSetting.setMyActivity((Activity)mContext);
		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSON);
		HttpGroupaAsynPool.getHttpGroupaAsynPool((Activity)mContext).add(httpsetting);
	}
	
	
	/**
	 * 如果发现新版本，提示用户更新 
	 */
	public void checkVersion() {
		if (localVersion < serverVersion) {
			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
			alert.setTitle(R.string.soft_update)
					.setMessage(mContext.getResources().getText(R.string.soft_update_message))
					.setPositiveButton(mContext.getText(R.string.update),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent updateIntent = new Intent(
											mContext,
											UpdateService.class);
									updateIntent.putExtra(
											"app_name",
											mContext.getResources().getString(
													R.string.app_name));
									updateIntent.putExtra("url", downloadUrl);
									mContext.startService(updateIntent);
								}
							})
					.setNegativeButton(mContext.getText(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
		}
	}


}
