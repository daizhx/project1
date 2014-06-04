package com.hengxuan.ehealthplatform.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;

public class UserInformationActivity extends BaseActivity {
	private Button ivSaveBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.user_info);
		setContentView(R.layout.activity_user_info);
		ivSaveBtn = (Button)findViewById(R.id.save);
	}
}
