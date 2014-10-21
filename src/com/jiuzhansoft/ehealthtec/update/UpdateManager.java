package com.jiuzhansoft.ehealthtec.update;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.config.Configuration;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
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
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * �汾���¹���
 * 1,����Ƿ��и��£���������
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
	
	//�ɸ��µİ汾��
    private String versionCode;
    //�ɸ��µı�־,1-��ѡ���£�2-ǿ�Ƹ���
    private int updateFlag;
    //���ذ�url
    private String updateUrl;
    //����������Ϣ
    private String updateDetail;

	public static UpdateManager getUpdateManager(Context context) {
		if (mUpdateManager == null) {
			mUpdateManager = new UpdateManager(context);
		}
		return mUpdateManager;
	}

	private UpdateManager(Context context) {
		mContext = context;
	}

	public void checkUpdate() {
		//������--����24Сʱ����
        SharedPreferences sp = mContext.getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
        long lastTime = sp.getLong("lastCheckTime", 0);
        if((System.currentTimeMillis() - lastTime) > Configuration.CHECK_UPDATE_INTERVAl) {
            try {
                int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(),0).versionCode;
                checkUpdate(mContext.getPackageName(), ""+versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            sp.edit().putLong("lastCheckTime", System.currentTimeMillis());
        }
	}
	
	
	/**
	 * ��������°汾����ʾ�û����� 
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

    private void getUpdateInfo(JSONObject object) {
        try {
            versionCode = object.getString("version_code");
            updateFlag = object.getInt("update_flag");
            updateUrl = object.getString("soft_url");
            updateDetail = object.getString("update_detail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * �¼����½ӿ�
	 * @param packageName
	 * @param versionCode
	 */
	private void checkUpdate(String packageName, String versionCode){
		{
	        Log.d(TAG, "check update:package="+packageName+",versionCode="+versionCode);
	        HttpSetting httpSetting = new HttpSetting();
	        httpSetting.setFunctionId(ConstFuncId.UPDATE);
	        httpSetting.setRequestMethod("GET");
	        httpSetting.addArrayListParam(packageName);
	        httpSetting.addArrayListParam(versionCode);
	        httpSetting.addArrayListParam("2");//android
	        httpSetting.setListener(new HttpGroup.OnAllListener() {
				
				@Override
				public void onProgress(int i, int j) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onError(HttpError httpError) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onEnd(HttpResponse response) {
					// TODO Auto-generated method stub
					JSONObjectProxy json = response.getJSONObject();
					try {
	                    if(json.getInt("code") == 1){
	                        //�ɸ���-������ʾ��
	                        JSONObject object = json.getJSONObject("object");
	                        getUpdateInfo(object);
	                        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
	                        alertDialog.setTitle(R.string.update);
	                        alertDialog.setMessage(updateDetail);
	                        if(updateFlag == 1){
	                        	//��ѡ����
	                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(DialogInterface dialogInterface, int i) {
	                                    //�������ظ��·���
	                                    Intent intent = new Intent(mContext, UpdateService.class);
	                                    intent.putExtra("apkPath", updateUrl);
	                                    mContext.startService(intent);
	                                    alertDialog.dismiss();
	                                }
	                            });
	                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel),new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(DialogInterface dialogInterface, int i) {
	                                    alertDialog.dismiss();
	                                }
	                            });
	                        }else{
	                            //�������
	                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
	                                @Override
	                                public void onClick(DialogInterface dialogInterface, int i) {
	                                    //�������ظ��·���
	                                    Intent intent = new Intent(mContext, UpdateService.class);
	                                    intent.putExtra("apkPath", updateUrl);
	                                    mContext.startService(intent);
	                                    alertDialog.dismiss();
	                                }
	                            });
	                        }
	                        alertDialog.show();
	                    }else{
	                        //û�и���
	                        //TODO should remove after test
	                    }
	                } catch (JSONException e) {
	                    e.printStackTrace();
	                }
				}
				
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					
				}
			});
	        httpSetting.setShowProgress(false);
			HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
			localHttpGroupSetting.setMyActivity((Activity)mContext);
			localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSON);
			HttpGroupaAsynPool.getHttpGroupaAsynPool((Activity)mContext).add(httpSetting);
	    }
		
	}

}
