package com.hengxuan.ehealthplatform.user;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.constant.PreferenceKeys;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstFuncId;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import com.hengxuan.ehealthplatform.log.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
					jsonobject.put("password", sUserPassword);
					jsonobject.put("username", sUserName);
					HttpSetting	httpsetting = new HttpSetting();
					httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_LOGIN);
					httpsetting.setJsonParams(jsonobject);
					httpsetting.setListener(new HttpGroup.OnAllListener() {
						public void onEnd(HttpResponse httpresponse)
						{
							if (Log.D) {
								Log.d("LoginActivity", "onLogin.httpsetting.listenter.onEnd");
							}
							if(httpresponse.getJSONObject() != null)
							{
	
							try
							{
								String s5 = httpresponse.getJSONObject().getJSONObject("registerInfo").get("pin").toString();
								if (Log.D)
									Log.d(TAG, "Login pin.."+s5);
								LoginSuccess(s5);
							}
							catch (Exception exception)
							{
								post(new Runnable(){
									@Override
									public void run() {
										Toast.makeText(UserLoginActivity.this, getResources().getString(R.string.login_data_error),Toast.LENGTH_SHORT).show();					
									}			
								});	
								if (Log.D)
								{
									StringBuilder stringbuilder = new StringBuilder("error message:");
									Log.d(TAG, stringbuilder.append(exception.getMessage()).toString());
								}
							}
								}
							else
								{
								  if (Log.D)
										Log.d(TAG, "get empty string.....");
									LoginError("");

								}
						}
	
						public void onError(HttpError httperror)
						{
							if (Log.D) {
								Log.d(TAG, "onLogin.httpsetting.listenter.onError");
							}
//							clearRemember(UserLoginActivity.this);
						}
	
						public void onProgress(int i, int j)
						{
						}
	
						public void onStart()
						{
							if (Log.D)
								Log.d(TAG, "Start to login......");
						}

					});
					
					httpsetting.setNotifyUser(true);
					httpsetting.setShowProgress(true);
					getHttpGroupaAsynPool().add(httpsetting);
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
	
	private void LoginError(final String tip)
	{
		if (Log.D) { 
			Log.d(TAG, "LoginError");
		}

		post(new Runnable() {
			public void run()
			{
				if (Log.D) {
					Log.d(TAG, "LoginError.post.run");
				}
				
				try
				{
//					mRememberMe.setChecked(true);
					post(new Runnable() {
						public void run()
						{
							// alertDialog = dialogBuilder.show();
							final AlertDialog dialog = (new AlertDialog.Builder(UserLoginActivity.this)).create();
							dialog.show();
							
							if("".equals(tip))
								dialog.setMessage(getString(R.string.login_failed_message));
							else
								dialog.setMessage(tip);
							dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							
						}
					});					
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
					if (Log.D)
					{
						StringBuilder stringbuilder = new StringBuilder("Error Message:");
						String s3 = stringbuilder.append(exception.getMessage()).toString();
						Log.d(TAG, "Login Error:"+s3);
					}
				}
			}
		});
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
	public static String EncryptPassword2(String s)
	{
		//TODO
		return s;
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
