package com.hengxuan.ehealthplatform.user;

import com.hengxuan.ehealthplatform.constant.PreferenceKeys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class UserLogin {

	private static boolean UserState = false;
	
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
	
	public static String getUserName(Context context){
		if(hasLogin()){
			SharedPreferences sp = context.getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
			String str = sp.getString(PreferenceKeys.SYS_USER_NAME, null);
			return str;
		}
		return null;
	}
}
