package com.jiuzhansoft.ehealthtec.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpRequest;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

public class UserInformationActivity extends BaseActivity {
	private EditText etUser, etPw, etCpw, etAddr, etPhone, etContacts, etEmail;
	private String name, phoneNum, address, email;
	private String newPW;
	private Button ivSaveBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.user_info);
		setContentView(R.layout.activity_user_info);
		ivSaveBtn = (Button) findViewById(R.id.save);

		etUser = (EditText) findViewById(R.id.et_username);
		etUser.setText(UserLogin.getUserName());

		etContacts = (EditText) findViewById(R.id.et_contacts);
		etContacts.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean isFocus) {
				// TODO Auto-generated method stub
				if (!isFocus) {
					realNameCheck();
				}
			}
		});

		etPhone = (EditText) findViewById(R.id.et_phone);
		etPhone.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean isFocus) {
				// TODO Auto-generated method stub
				if (!isFocus) {
					phoneNumCheck();
				}
			}
		});

		etAddr = (EditText) findViewById(R.id.et_addr);
		etAddr.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean isFocus) {
				// TODO Auto-generated method stub
				if (!isFocus) {
					addressCheck();
				}
			}
		});

		etEmail = (EditText) findViewById(R.id.et_email);
		etEmail.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean isFocus) {
				// TODO Auto-generated method stub
				if (!isFocus) {
					emailCheck();
				}
			}
		});
		etPw = (EditText) findViewById(R.id.et_pw);
		etCpw = (EditText) findViewById(R.id.et_cpw);

		ivSaveBtn = (Button) findViewById(R.id.save);
		ivSaveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				submit();
			}
		});
	}

	protected boolean realNameCheck() {
		// TODO Auto-generated method stub
		name = etContacts.getText().toString().trim();
		if (name == null || name.equals("")) {
			etContacts.setText("");
			name = "";
		}
		return true;
	}

	protected boolean phoneNumCheck() {
		String s = etPhone.getText().toString().trim();
		if (s.equals("") || s == null) {
			etPhone.setText("");
			phoneNum = "";
			return true;
		}
		phoneNum = etPhone.getText().toString();
		return true;
	}

	protected boolean addressCheck() {
		String s = etAddr.getText().toString().trim();
		if (s.equals("") || s == null) {
			etAddr.setText("");
			address = "";
			return true;
		}
		address = etAddr.getText().toString();
		return true;
	}

	private boolean emailCheck() {
		String s = etEmail.getText().toString().trim();
		if (s.equals("") || s == null) {
			etEmail.setText("");
			email = "";
			return true;
		}
		if (!CommonUtil.checkEmailWithSuffix(s)) {
			etEmail.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.not_mail_format)
					+ "</font>"));
			return false;
		}
		email = etEmail.getText().toString();
		return true;
	}

	private boolean pwCheck() {
		String pw = etPw.getText().toString().trim();
		String cpw = etCpw.getText().toString().trim();
		if (pw != null && cpw != null) {
			if (cpw.equals(pw)) {
				newPW = pw;
				return true;
			}
		}
		if (pw == null && cpw == null) {
			return true;
		}
		etCpw.setError(Html.fromHtml("<font color=#00ff00>"
				+ getResources().getString(R.string.password_not_same)
				+ "</font>"));
		return false;
	}

	private void submit() {
		if (!pwCheck() || !realNameCheck() || !phoneNumCheck()
				|| !addressCheck() || !emailCheck()) {
			return;
		}
		JSONObject jsonobject = new JSONObject();
		try {
//			jsonobject.put("userPin", Integer
//					.parseInt(getStringFromPreference(ConstHttpProp.USER_PIN)));
			jsonobject.put("username", UserLogin.UserName);
			jsonobject.put("realName", name);
			jsonobject.put("phone", phoneNum);
			jsonobject.put("address", address);
			jsonobject.put("email", email);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_EDIT);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setRequestMethod("POST");
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				String s = getText(R.string.register_err_busy).toString();
				showAlertDialog(s);
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				try {
					if (json != null && json.getInt("code") == 1) {
						// alertDialogBuilder.show();
						final AlertDialog dialog = (new AlertDialog.Builder(
								UserInformationActivity.this)).create();
						dialog.setTitle(getString(R.string.submit_success));
						dialog.setButton(AlertDialog.BUTTON_NEUTRAL,
								getString(R.string.ok),
								new DialogInterface.OnClickListener() {
	
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										dialog.dismiss();
										finish();
									}
	
								});
						dialog.show();
						
					}else{
						String s = getText(R.string.register_err_busy).toString();
						showAlertDialog(s);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}
		});
		// httpsetting.setNotifyUser(true);
		httpsetting.setShowProgress(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);

	}

	void showAlertDialog(final String s) {
		// alertDialogBuilder.show();
		final AlertDialog dialog = (new AlertDialog.Builder(
				UserInformationActivity.this)).create();
		dialog.setTitle(s);
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL,
				getString(R.string.ok),
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
	
				});
		dialog.show();
	}
}
