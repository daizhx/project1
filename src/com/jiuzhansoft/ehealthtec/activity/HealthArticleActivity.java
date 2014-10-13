package com.jiuzhansoft.ehealthtec.activity;

import com.jiuzhansoft.ehealthtec.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HealthArticleActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");
		
		TextView tvTitle = (TextView)findViewById(R.id.tv_title);
		tvTitle.setText(title);
		((TextView)findViewById(R.id.tv_content)).setText(content);
	}
}
