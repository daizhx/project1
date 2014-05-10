package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.fragment.MassageFragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

public class MainActivity1 extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		
		FrameLayout container = (FrameLayout)findViewById(R.id.fragment_container);
		
		MassageFragment massageFragment = new MassageFragment();
		
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, massageFragment).commit();
	}
	
}
