package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.R;

import android.os.Bundle;
import android.widget.GridView;

public class PhysicalExamActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		GridView gridView = (GridView)findViewById(R.id.gridview);
		
	}
}
