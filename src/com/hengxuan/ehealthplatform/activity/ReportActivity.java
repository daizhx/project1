package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.R;

import android.os.Bundle;

/**
 * ��챨�洰��
 * @author Administrator
 *
 */
public class ReportActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.physical_report);
		setContentView(R.layout.activity_grid);
	}
}
