package com.hengxuan.ehealthplatform.user;

import android.app.Activity;
import android.content.Intent;

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
}
