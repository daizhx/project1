package com.hengxuan.ehealthplatform.lens.skin;

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

public class SkinEntryActivity extends BaseActivity {
	// capture new photo
	private Button btnNew;
	// review old photo
	private Button btnOld;
	private Button btnHelp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.skin_analysis);
		setContent(R.layout.lens_entry_menu);
		btnNew = (Button) findViewById(R.id.new_photo);
		btnOld = (Button) findViewById(R.id.old_photo);
		btnHelp = (Button) findViewById(R.id.help);
		// btnHelp.setVisibility(View.GONE);
		btnNew.setText(R.string.new_skin_photo);
		btnOld.setText(R.string.old_skin_photo);
		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SkinEntryActivity.this,
						LensBaseActivity.class);
				int index = LensConstant.INDEX_SKIN;
				intent.putExtra("index", index);
				startActivity(intent);
			}
		});

		btnOld.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SkinEntryActivity.this,
						LensPhotoList.class);
				int index = LensConstant.INDEX_SKIN;
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
