package com.hengxuan.ehealthplatform.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.config.Configuration;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpGroupSetting;
import com.hengxuan.ehealthplatform.http.HttpGroupaAsynPool;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.log.Log;
import com.hengxuan.ehealthplatform.utils.StatisticsReportUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorActivity extends Activity {

	private Button btnCancel;
	private Button btnSubmit;
	private CheckBox checkBox;
	private EditText editText;
	private String errorStr;
	private boolean isKill;
	private boolean isRestart;
	private ProgressDialog loading;
	private String msg;
	private TextView textView;
	private String user;

	public ErrorActivity() {
		if (Log.D) {
			Log.d("ErrorActivity", "ErrorActivity");
		}

		isKill = true;
	}

	private void doPost(String s, JSONObject jsonobject) {
		if (Log.D) {
			Log.d("ErrorActivity", "doPost");
		}

		HttpGroupSetting httpgroupsetting = new HttpGroupSetting();
		httpgroupsetting.setPriority(1000);
		httpgroupsetting.setType(1000);
		HttpGroupaAsynPool httpgroupaasynpool = new HttpGroupaAsynPool(
				httpgroupsetting);
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setFunctionId(s);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			private void restart() {
				if (Log.D) {
					Log.d("ErrorActivity",
							"doPost.HttpGroup.OnAllListener.restart");
				}

				if (isRestart()) {
					Intent intent = new Intent(ErrorActivity.this,
							MainActivity.class);
					startActivity(intent);
				}
				killProcess();
			}

			public void onEnd(HttpResponse httpresponse) {
				if (Log.D) {
					Log.d("ErrorActivity",
							"doPost.HttpGroup.OnAllListener.onEnd");
				}
				restart();
			}

			public void onError(HttpError httperror) {
				if (Log.D) {
					Log.d("ErrorActivity",
							"doPost.HttpGroup.OnAllListener.onError");
				}
				restart();
			}

			public void onProgress(int i, int j) {
			}

			public void onStart() {
			}
		});
		httpgroupaasynpool.add(httpsetting);
	}

	private boolean isRestart() {
		boolean flag;
		if (checkBox != null){
			flag = isRestart;
		}else{
			flag = false;
		}

		if (Log.D) {
			Log.d("ErrorActivity", "isRestart?"+flag);;
		}
		return flag;
	}

	private void killProcess() {
		if (Log.D) {
			Log.d("ErrorActivity", "killProcess");
		}

		finish();
		Process.killProcess(Process.myPid());
		System.exit(0);
	}

	private void myOnClick(int i) {
		if (Log.D) {
			Log.d("ErrorActivity", "myOnClick");
		}

		switch (i) {
		case DialogInterface.BUTTON_POSITIVE:
			StringBuilder stringbuilder = new StringBuilder();
			Editable editable = editText.getText();
			stringbuilder.append(editable).append("|| version code: ");
			int j = StatisticsReportUtil.getSoftwareVersionCode();
			stringbuilder.append(j).append(" ||");
			errorStr = stringbuilder.append(errorStr).toString();

			onSubmitError();

			break;
		case DialogInterface.BUTTON_NEGATIVE:
			killProcess();
			break;
		}
	}

	private void onActivity() {
		if (Log.D) {
			Log.d("ErrorActivity", "onActivity");
		}

		btnSubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				myOnClick(DialogInterface.BUTTON_POSITIVE);
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				myOnClick(DialogInterface.BUTTON_NEGATIVE);
			}
		});

		StringBuilder stringbuilder = new StringBuilder();
		CharSequence charsequence = textView.getText();
		stringbuilder.append(charsequence).append("||");
		String s = stringbuilder.append(msg).toString();
		textView.setText(s);
	}

	private void onDialog(View view) {
		if (Log.D) {
			Log.d("ErrorActivity", "onDialog");
		}

		AlertDialog.Builder builder = (new AlertDialog.Builder(this))
				.setView(view);
		builder.setMessage(msg).setTitle(R.string.app_error_title);
		builder.setPositiveButton(R.string.app_error_submit,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						myOnClick(DialogInterface.BUTTON_POSITIVE);
					}
				});
		builder.setNegativeButton(R.string.app_error_close,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						myOnClick(DialogInterface.BUTTON_NEGATIVE);
					}
				});
		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialoginterface, int i,
					KeyEvent keyevent) {
				switch (i) {
				case KeyEvent.KEYCODE_BACK:
					return true;
				case KeyEvent.KEYCODE_SEARCH:
					return true;
				default:
					return false;
				}
			}
		}).show();
		/*
		 * final AlertDialog alertDialog = (new
		 * AlertDialog.Builder(this)).create(); alertDialog.show(); Window
		 * window = alertDialog.getWindow();
		 * window.setContentView(R.layout.myalert); ((TextView)
		 * window.findViewById(R.id.msgtv)).setText(msg); ((Button)
		 * window.findViewById(R.id.btn3)).setVisibility(View.GONE);
		 * ((LinearLayout)
		 * window.findViewById(R.id.msgtv)).setVisibility(View.VISIBLE); Button
		 * button1 = (Button) window.findViewById(R.id.btn1);
		 * button1.setText(getResources().getString(R.string.app_error_submit));
		 * button1.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub StringBuilder stringbuilder = new StringBuilder();
		 * Editable editable = editText.getText();
		 * stringbuilder.append(editable).append("|| version code: "); int j =
		 * StatisticsReportUtil.getSoftwareVersionCode();
		 * stringbuilder.append(j).append(" ||"); errorStr =
		 * stringbuilder.append(errorStr).toString();
		 * 
		 * onSubmitError(); alertDialog.dismiss(); } }); Button button2 =
		 * (Button) window.findViewById(R.id.btn2);
		 * button2.setText(getResources().getString(R.string.app_error_close));
		 * button2.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub killProcess(); } }); alertDialog.setOnKeyListener(new
		 * OnKeyListener() {
		 * 
		 * @Override public boolean onKey(DialogInterface arg0, int i, KeyEvent
		 * arg2) { // TODO Auto-generated method stub switch(i){ case
		 * KeyEvent.KEYCODE_BACK: return true; case KeyEvent.KEYCODE_SEARCH:
		 * return true; default: return false; } } });
		 */
	}

	private void onSubmitError() {
		if (Log.D) {
			Log.d("ErrorActivity", "onSubmitError");
		}

		try {
			final JSONObject json = new JSONObject();
			if (errorStr.length() > 20000) {
				errorStr = errorStr.substring(0, 20000);
			}

			json.put("msg", errorStr);
			json.put("partner", Configuration.getProperty("partner"));
			loading = ProgressDialog.show(this, null,
					getString(R.string.app_error_tag_send));
			loading.setOnKeyListener(new DialogInterface.OnKeyListener() {
				public boolean onKey(DialogInterface dialoginterface, int i,
						KeyEvent keyevent) {
					if (i == 4)
						dialoginterface.dismiss();
					return false;
				}
			});
			(new Thread() {
				public void run() {
					isKill = false;
					doPost("crash", json);
				}
			}).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (isRestart) {
			Toast.makeText(this, getString(R.string.app_error_tag_restart), 1)
					.show();
		} else {
			Toast.makeText(this, getString(R.string.app_error_tag_close), 1)
					.show();
		}
		finish();
	}

	protected void onCreate(Bundle bundle) {
		if (Log.D) {
			Log.d("ErrorActivity", "onCreate");
		}

		super.onCreate(bundle);
		getWindow().setFlags(1024, 1024);

		boolean flag = requestWindowFeature(1);
		user = getIntent().getStringExtra("user");
		errorStr = getIntent().getStringExtra("error");
		msg = getString(R.string.app_error_msg);

		// if (flag)
		// {
		setTheme(0x103000b);
		setContentView(R.layout.app_error_activity);
		findViewById(R.id.linearLayout_button_error)
				.setVisibility(View.VISIBLE);
		textView = (TextView) findViewById(R.id.textView_msg_error);
		btnSubmit = (Button) findViewById(R.id.button_submit_error);
		btnCancel = (Button) findViewById(R.id.button_cancel_error);

		checkBox = (CheckBox) findViewById(R.id.checkBox_restart_error);
		isRestart = checkBox.isChecked();
		editText = (EditText) findViewById(R.id.editText_text_error);
		onActivity();

		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton compoundbutton,
					boolean flag) {
				isRestart = flag;
			}
		});
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public boolean onKeyDown(int i, KeyEvent keyevent) {
		if (Log.D) {
			Log.d("ErrorActivity", "onKeyDown");
		}

		killProcess();
		return false;
	}

	protected void onStop() {
		if (Log.D) {
			Log.d("ErrorActivity", "onStop");
		}

		// if (isKill)
		// killProcess();
		super.onStop();
	}

}
