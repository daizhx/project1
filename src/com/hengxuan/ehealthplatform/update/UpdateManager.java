package com.hengxuan.ehealthplatform.update;

import org.json.JSONException;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpRequest;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstFuncId;
import com.hengxuan.ehealthplatform.log.Log;

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
					String versionCode = response.getJSONObject().get("code").toString();
					Log.d(TAG, "serverVersion="+versionCode);
					downloadUrl = response.getJSONObject().get("versionList").toString();
//					downloadUrl = response.getJSONObject().get("url").toString();
					
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
		
		httpsetting.setNotifyUser(true);
		((BaseActivity)mContext).getHttpGroupaAsynPool().add(httpsetting);
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
