package com.hengxuan.ehealthplatform.user;

import com.hengxuan.ehealthplatform.application.EHTApplication;
import com.hengxuan.ehealthplatform.constant.PreferenceKeys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class UserLogin {

	public static boolean UserState = false;
	public static String UserName;
	
	public static void setUserState(boolean paramInt){
		UserState = paramInt;
	}
	public static boolean hasLogin(){		
		return UserState;
	}	  
	public static boolean checkUserLogin(Activity activity){
		if (!hasLogin()){
			Intent intent = new Intent(activity, UserLoginActivity.class);
				activity.startActivity(intent);
		}
		return hasLogin();
	}
	
	public static String getUserName(){
		Context context = EHTApplication.getInstance().getApplicationContext();
		if(UserName == null){
			SharedPreferences sp = context.getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
			UserName = sp.getString(PreferenceKeys.SYS_USER_NAME, null);
		}
		return UserName;
	}
	
	
}
