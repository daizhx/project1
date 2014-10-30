package com.jiuzhansoft.ehealthtec.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuzhansoft.ehealthtec.MainActivity;
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
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;
import com.jiuzhansoft.ehealthtec.utils.StatisticsReportUtil;

public class UserRegisterActivity extends BaseActivity {
	private static final String TAG = "register";
	private Button mConfirmBtn;
	private EditText mRegisterFirstPwd;
	private EditText mRegisterMail;
	private EditText mRegisterName;
	private EditText mRegisterSecondPwd;
	// private EditText mRegisterPhone;

	private String sRegMailAddr;
	private String sRegName;
	private String sRegPwd1;
	private String sRegPwd2;
	private String sRegUuid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		mRegisterName = (EditText) findViewById(R.id.edit_username);
		mRegisterName
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View view, boolean flag) {
						if (!flag)
							nameCheck();
					}
				});
		mRegisterMail = (EditText) findViewById(R.id.edit_email);
		mRegisterMail
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					public void onFocusChange(View view, boolean flag) {
						if (!flag)
							mailCheck();
					}
				});
		mRegisterMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		// mRegisterPhone = (EditText)findViewById(R.id.register_phone);
		// mRegisterPhone.setOnFocusChangeListener(new
		// View.OnFocusChangeListener() {
		// public void onFocusChange(View view, boolean flag)
		// {
		// if (!flag){
		// if(TextUtils.isEmpty(mRegisterMail.getText().toString().trim()))
		// phoneCheck();
		// }
		// }
		// });
		// mRegisterPhone.setInputType(InputType.TYPE_CLASS_PHONE);
		mRegisterFirstPwd = (EditText) findViewById(R.id.edit_pw);
		mRegisterSecondPwd = (EditText) findViewById(R.id.edit_pw2);

		mConfirmBtn = (Button) findViewById(R.id.register);
		mConfirmBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (Log.D) {
					Log.d("RegisterActivity",
							"initBtn.mConfirmBtn.ClickListener.onClick");
				}
				register();
			}
		});
	}

	private void register() {
		if (Log.D) {
			Log.d(TAG, "register");
		}
		try {
			if (!inputCheck()) {
				InputMethodManager inputmethodmanager = (InputMethodManager) getSystemService("input_method");
				IBinder ibinder = mRegisterSecondPwd.getWindowToken();
				inputmethodmanager.hideSoftInputFromWindow(ibinder, 0);
				getRegisterUserInfo();
				JSONObject jsonobject = new JSONObject();

				jsonobject.put("username", sRegName);
				jsonobject.put("password", sRegPwd1);
				if (!TextUtils.isEmpty(sRegMailAddr))
					jsonobject.put("email", sRegMailAddr);
				// if(!TextUtils.isEmpty(sRegPhone))
				// jsonobject.put("phone", sRegPhone);
				HttpSetting httpsetting = new HttpSetting();
				httpsetting.setRequestMethod("POST");
				httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_REGISTER);
				httpsetting.setJsonParams(jsonobject);

				httpsetting.setListener(new HttpGroup.OnAllListener() {
					public void onEnd(HttpResponse httpresponse) {
						JSONObjectProxy json = httpresponse.getJSONObject();
						if(json == null)return;
						
						try {
							int code = json.getInt("code");
							String msg = json.getString("msg");
							JSONObject object = json.getJSONObjectOrNull("object");
							if(code ==1 && object != null){
								String userId = object.getString("userId");
								registerSuccess(userId);
							}else{
//								Log.d(TAG,"Login fail msg="+msg);
								Toast.makeText(UserRegisterActivity.this, getResources().getString(R.string.register_fail)+msg, Toast.LENGTH_SHORT).show();
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					public void onError(HttpError httperror) {
						if (Log.D) {
							Log.d("RegisterActivity",
									"onRegister.HttpGroup.OnAllListener.onError---"+httperror);
						}

//						UserRegisterActivity.this.showDialog(getText(
//								R.string.register_err_busy).toString());
					}

					public void onProgress(int i, int j) {
					}

					public void onStart() {
						if (Log.D) {
							Log.d("RegisterActivity",
									"onRegister.HttpGroup.OnAllListener.onStart");
						}

					}
				});
				httpsetting.setNotifyUser(true);
				httpsetting.setShowProgress(true);
				HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);

			}
		} catch (JSONException exception) {
			String s = ((JSONException) (exception)).getMessage();
			Log.d("Register Error", s);
		}

		return;
	}

	protected void showDialog(String string) {
		// TODO Auto-generated method stub
		final AlertDialog alertDialog = (new AlertDialog.Builder(
				UserRegisterActivity.this)).create();
		alertDialog.show();
		alertDialog.setMessage(string);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
				getText(R.string.confirm),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
					}
				});
	}

	// pass with returning false;fail with return true
	private boolean inputCheck() {
		if (Log.D) {
			Log.d("RegisterActivity", "inputCheck");
		}
		boolean flag = false;
		String mailStr = mRegisterMail.getText().toString().trim();
		// String phoneStr = mRegisterPhone.getText().toString();
		if (TextUtils.isEmpty(mailStr) /* && TextUtils.isEmpty(phoneStr) */) {
			flag = true;
			mailCheck();
		}
		if (!TextUtils.isEmpty(mailStr)) {
			if (!CommonUtil.checkEmailWithSuffix(mailStr)) {
				flag = true;
				mRegisterMail.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.not_mail_format)
						+ "</font>"));
			}
		}
		if (nameCheck() /* || mailCheck() || phoneCheck() */)
			flag = true;

		String s = mRegisterFirstPwd.getText().toString().trim();
		if (!TextUtils.isEmpty(s)) {
			if (!CommonUtil.checkPassword(s, 6, 20)) {
				flag = true;
				// mRegisterFirstPwd.setError(getString(R.string.user_password_hint));
				mRegisterFirstPwd.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_password_hint)
						+ "</font>"));
			} else {
				String s3 = mRegisterSecondPwd.getText().toString();
				if (!s.equals(s3)) {
					flag = true;
					// mRegisterSecondPwd.setError(getText(R.string.password_not_same));
					mRegisterSecondPwd.setError(Html
							.fromHtml("<font color=#00ff00>"
									+ getResources().getString(
											R.string.password_not_same)
									+ "</font>"));
				}
			}
		} else {
			flag = true;
			EditText edittext = mRegisterFirstPwd;
			String s1 = getString(R.string.login_user_password_hint);
			// edittext.setError(s1);
			edittext.setError(Html.fromHtml("<font color=#00ff00>" + s1
					+ "</font>"));
		}

		if (Log.D)
			Log.d("temp", "inputCheck-end");

		return flag;
	}

	// fail with return true,pass with false.
	private boolean mailCheck() {
		if (Log.D) {
			Log.d("RegisterActivity", "mailCheck");
		}
		boolean flag = false;
		String s = mRegisterMail.getText().toString().trim();
		if (!TextUtils.isEmpty(s)) {
			if (!CommonUtil.checkEmailWithSuffix(s)) {
				flag = true;
				mRegisterMail.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.not_mail_format)
						+ "</font>"));
			}
		} else {
			flag = true;
			mRegisterMail.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.register_mail_addr)
					+ "</font>"));
		}
		if (Log.D)
			Log.d("RegisterActivity", "mailCheck-end");
		return flag;
	}

	// fail with true,pass with false.
	private boolean nameCheck() {
		if (Log.D) {
			Log.d("RegisterActivity", "nameCheck");
		}

		boolean flag = false;
		String s = mRegisterName.getText().toString();
		int i = CommonUtil.getLength(s.trim());
		if (Log.D) {
			String s1 = (new StringBuilder("length:")).append(i).toString();
			Log.d("temp", s1);
		}

		if (!TextUtils.isEmpty(s.trim())) {
			if (!CommonUtil.checkUsername(s)) {
				flag = true;
				mRegisterName.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_name_hint)
						+ "</font>"));
			} else if (i < 4 || i > 20) {
				flag = true;
				// mRegisterName.setError(getText(R.string.user_name_hint));
				mRegisterName.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_name_hint)
						+ "</font>"));
			}
		} else {
			flag = true;
			// mRegisterName.setError(getString(R.string.login_user_name_hint));
			mRegisterName.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.login_user_name_hint)
					+ "</font>"));
		}

		if (Log.D)
			Log.d("temp", "nameCheck-end");

		return flag;
	}

	protected void getRegisterUserInfo() {
		if (Log.D) {
			Log.d("RegisterActivity", "getRegisterUserInfo");
		}
		sRegName = mRegisterName.getText().toString();
		sRegMailAddr = mRegisterMail.getText().toString();
		// sRegPhone = mRegisterPhone.getText().toString();
		sRegPwd1 = mRegisterFirstPwd.getText().toString();
		sRegPwd2 = mRegisterSecondPwd.getText().toString();
		userInputPasswordMd5Handler(mRegisterFirstPwd.getText().toString(),
				mRegisterSecondPwd.getText().toString());
	}

	private void userInputPasswordMd5Handler(String password1, String password2) {
		sRegPwd1 = EncryptPassword2(password1);
		sRegPwd2 = EncryptPassword2(password2);
	}

	/**
	 * º”√‹√‹¬Î
	 * 
	 * @param s
	 * @return
	 */
	public static String EncryptPassword2(String s) {
		// TODO
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
	
	private void registerSuccess(String userId){
		putString2Preference(ConstHttpProp.USER_PIN, userId);
		putBoolean2Preference(PreferenceKeys.SYS_USER_LOGIN, true);
		putString2Preference(PreferenceKeys.SYS_USER_NAME, sRegName);

		UserLogin.setUserState(true);
		UserLogin.UserName = sRegName;
		
		Intent intent = new Intent(UserRegisterActivity.this,MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("index", 1);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
}
