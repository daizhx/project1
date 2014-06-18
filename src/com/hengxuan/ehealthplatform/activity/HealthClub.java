package com.hengxuan.ehealthplatform.activity;


import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.lens.LensBaseActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HealthClub extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		openFloatView(false);
		super.onCreate(savedInstanceState);
		Button btn = new Button(this);
		btn.setText("button");
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog alertDialog = new AlertDialog.Builder(HealthClub.this).create();
				alertDialog.setMessage(getString(R.string.open_lens));
				alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						getString(R.string.confirm),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
//								restoreWifiInfo();
								finish();
							}
						});
				alertDialog.show();
				
			}
		});
		setContentView(btn);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent parentIntent = new Intent(HealthClub.this, MainActivity.class);
			parentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(parentIntent);
			finish();
			break;
			
		default:
			break;
		}
		
		return super.onKeyUp(keyCode, event);
	}
}
