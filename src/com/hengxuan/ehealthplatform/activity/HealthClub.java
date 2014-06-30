package com.hengxuan.ehealthplatform.activity;


import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.healthclub.EntriesActivity;
import com.hengxuan.ehealthplatform.lens.LensBaseActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HealthClub extends BaseActivity implements OnClickListener{
	private int flag;
	private int gender;
	private static final int ACUPOINT = 1;
	private static final int DISEASE = 2;
	private static final int MAN = 1;
	private static final int WEMAN = 0;
	
	private TextView tvAcupoint, tvDisease;
	private ImageView ivSetSex, ivPerson;
	private int textChoosedColor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContent(R.layout.activity_health_club);
		setTitle(R.string.health_bar);
		initData();
	}
	
	
	
	private void initData() {
		// TODO Auto-generated method stub
		tvAcupoint = (TextView)findViewById(R.id.tv1);
		tvDisease = (TextView)findViewById(R.id.tv2);
		ivSetSex = (ImageView)findViewById(R.id.iv1);
		ivPerson = (ImageView)findViewById(R.id.iv2);
		
		tvAcupoint.setOnClickListener(this);
		tvDisease.setOnClickListener(this);
		ivSetSex.setOnClickListener(this);
		ivPerson.setOnClickListener(this);
		
		textChoosedColor = Color.parseColor("#0f86fe");
		tvAcupoint.setBackgroundColor(textChoosedColor);
		flag = ACUPOINT;
		gender = MAN;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv1:
			if(flag == DISEASE){
				tvAcupoint.setBackgroundColor(textChoosedColor);
				tvDisease.setBackgroundColor(Color.TRANSPARENT);
				flag = ACUPOINT;
			}
			break;
		case R.id.tv2:
			if(flag == ACUPOINT){
				tvDisease.setBackgroundColor(textChoosedColor);
				tvAcupoint.setBackgroundColor(Color.TRANSPARENT);
				flag = DISEASE;
			}
			break;
		case R.id.iv1:
			if(gender == MAN){
				gender = WEMAN;
				ivSetSex.setImageResource(R.drawable.weman_indicator);
				ivPerson.setImageResource(R.drawable.weman_profile);
			}else if(gender == WEMAN){
				gender = MAN;
				ivSetSex.setImageResource(R.drawable.man_indicator);
				ivPerson.setImageResource(R.drawable.man_profile);
			}
			break;
		case R.id.iv2:
			//TODO
			Intent intent = new Intent(HealthClub.this, EntriesActivity.class);
			intent.putExtra("index", flag);
//			intent.putExtra("gender", gender);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
