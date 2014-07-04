package com.hengxuan.ehealthplatform.lens.naevus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.lens.LensBaseActivity;
import com.hengxuan.ehealthplatform.lens.LensConstant;
import com.hengxuan.ehealthplatform.lens.LensPhotoList;
import com.hengxuan.ehealthplatform.lens.LensShootBaseActivity;
import com.hengxuan.ehealthplatform.lens.iris.IrisEntryActivity;

public class NaevusEntryActivity extends BaseActivity {
	// capture new photo
	private Button btnNew;
	// review old photo
	private Button btnOld;
	private Button btnHelp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.naevus_analysis);
		setContent(R.layout.lens_entry_menu);
		btnNew = (Button) findViewById(R.id.new_photo);
		btnOld = (Button) findViewById(R.id.old_photo);
		btnHelp = (Button) findViewById(R.id.help);
//		btnHelp.setVisibility(View.GONE);
		btnNew.setText(R.string.new_naevus_photo);
		btnOld.setText(R.string.old_naevus_photo);
		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NaevusEntryActivity.this,
						LensBaseActivity.class);
				int index = LensConstant.INDEX_NAEVUS;
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});

		btnOld.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NaevusEntryActivity.this,
						LensPhotoList.class);
				int index = LensConstant.INDEX_NAEVUS;
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});

		btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}