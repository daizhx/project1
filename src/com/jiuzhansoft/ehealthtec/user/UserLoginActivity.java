package com.jiuzhansoft.ehealthtec.user;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.http.utils.Des3;
import com.jiuzhansoft.ehealthtec.http.utils.Md5Encrypt;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class UserLoginActivity extends BaseActivity {
	private static final String TAG = "Login";
	private Button loginBtn;
	private Button registerBtn;
	private EditText mUserNameTxt;
	private EditText mUserPassword;
	private String sUserName;
	private String sUserPassword;
	
	private String intentAction;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mTitle.setText(R.string.user_login);
		setContentView(R.layout.activity_login);
		mUserNameTxt = (EditText)findViewById(R.id.edit_username);
		mUserPassword = (EditText)findViewById(R.id.edit_pw);
		loginBtn = (Button)findViewById(R.id.login_btn);
		registerBtn = (Button)findViewById(R.id.register_btn);
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onLogin();
			}
		});
		registerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(UserLoginActivity.this, UserRegisterActivity.class));
			}
		});
		intentAction = getIntent().getStringExtra("action");
		
	}

	
	private void onLogin()
	{
		if (Log.D) { 
			Log.d(TAG, "onLogin");
		}
		
		if (!nameCheck() && !passWordCheck())
		{
			InputMethodManager inputmethodmanager = (InputMethodManager)getSystemService("input_method");
			IBinder ibinder = mUserPassword.getWindowToken();
			inputmethodmanager.hideSoftInputFromWindow(ibinder, 0);
			sUserName = getUserName();
			sUserPassword = getUserPassword();
			
			if (sUserPassword.length() > 0 && sUserName.length() > 0)
			{
				try
				{
					JSONObject jsonobject = new JSONObject();
					//√‹¬Îº”√‹
					jsonobject.put("password", sUserPassword);
					jsonobject.put("username", sUserName);
					HttpSetting	httpsetting = new HttpSetting();
					httpsetting.setRequestMethod("POST");
					httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_LOGIN);
					httpsetting.setJsonParams(jsonobject);
					httpsetting.setListener(new HttpGroup.OnAllListener() {
						public void onEnd(HttpResponse httpresponse){
							JSONObjectProxy json = httpresponse.getJSONObject();
							if (Log.D) {
								Log.d(TAG, "Login.onEnd:"+json);
							}
							try {
								int code = json.getInt("code");
								String msg = json.getString("msg");
								JSONObject object = json.getJSONObjectOrNull("object");
								if(code ==1 && object != null){
									String userId = object.getString("userId");
									LoginSuccess(userId);
								}else{
//									Log.d(TAG,"Login fail msg="+msg);
									Toast.makeText(UserLoginActivity.this, getResources().getString(R.string.login_failed_message)+msg, Toast.LENGTH_SHORT).show();
									
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
	
						public void onError(HttpError httperror){
							if (Log.D) {
								Log.d(TAG, "Login.onError:"+httperror);
							}
						}
						public void onProgress(int i, int j){
							Log.d(TAG, "Login.onProgress:i="+i+",j="+j);
						}
	
						public void onStart(){
							if (Log.D)
								Log.d(TAG, "Login.onStart");
						}

					});
					
					httpsetting.setNotifyUser(true);
					httpsetting.setShowProgress(true);
					HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void LoginSuccess(String userPin) {
		// TODO Auto-generated method stub
		putBoolean2Preference(PreferenceKeys.SYS_USER_LOGIN, true);
		putString2Preference(PreferenceKeys.SYS_USER_NAME, sUserName);
		putString2Preference(PreferenceKeys.SYS_USER_PASSWORD, sUserPassword);
		putString2Preference(ConstHttpProp.USER_PIN, userPin);
		UserLogin.setUserState(true);
		UserLogin.UserName = sUserName;
		if(intentAction != null){
			startActivity(new Intent(intentAction));
		}else{
			setResult(Activity.RESULT_OK, null);
		}
		finish();
	}
	
	private void cancelLogin(){
		setResult(Activity.RESULT_CANCELED, null);
		finish();
	}


	private boolean nameCheck()
	{
		if (Log.D) {
			Log.d(TAG, "nameCheck");
		}
		
		boolean flag = false;
		if (TextUtils.isEmpty(mUserNameTxt.getText().toString().trim()))
		{
			flag = true;
			//mUserNameTxt.setError(getString(R.string.login_user_name_hint));
			mUserNameTxt.setError(
					Html.fromHtml("<font color=#00ff00>"
							+getResources().getString(R.string.login_user_name_hint)
							+"</font>"));
		}
		
		return flag;
	}
	
	private boolean passWordCheck()
	{
		if (Log.D) { 
			Log.d(TAG, "passWordCheck");
		}
		
		boolean flag = false;
		if (TextUtils.isEmpty(mUserPassword.getText().toString().trim()))
		{
			flag = true;
			//mUserPassword.setError(getString(R.string.login_user_password_hint));
			mUserPassword.setError(
					Html.fromHtml("<font color=#00ff00>"
							+getResources().getString(R.string.login_user_password_hint)
							+"</font>"));
		}
		return flag;
	}
	
	private String getUserName()
	{
		return mUserNameTxt.getText().toString();
	}

	private String getUserPassword()
	{
		return EncryptPassword2(mUserPassword.getText().toString());
	}
	private String getUserPasswordNoCode()
	{
		return mUserPassword.getText().toString();
	}
	
	
	/**
	 * º”√‹√‹¬Î
	 * @param s
	 * @return
	 */
	public static String EncryptPassword2(String s)
	{
		//TODO
		String s1 = Md5Encrypt.MD5(s);
		String s2 = null;
		try {
			s2 = Des3.encode(s1, Des3.DES3_KEY, Des3.DES3_IV);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return s2;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			try 
			{
//				UserLoginActivity.clearRemember(UserLoginActivity.this);
				cancelLogin();
				if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 5)
					overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);
				return true;
			}
			catch(Exception localException)
			{
			      Log.v("CancleFailed", localException.getMessage());
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
