package com.jiuzhansoft.ehealthtec.lens.iris;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.activity.PhysicalExamActivity;
import com.jiuzhansoft.ehealthtec.lens.LensBaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensPhotoList;
import com.jiuzhansoft.ehealthtec.lens.LensShootBaseActivity;

public class IrisEntryActivity extends BaseActivity {
	// capture new photo
	private Button btnNew;
	// review old photo
	private Button btnOld;
	// open iris check help
	private Button btnHelp;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.iris_analysis);
		setContent(R.layout.lens_entry_menu);
		btnNew = (Button) findViewById(R.id.new_photo);
		btnOld = (Button) findViewById(R.id.old_photo);
		btnHelp = (Button) findViewById(R.id.help);

		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(IrisEntryActivity.this,
						LensBaseActivity.class);
				int index = LensShootBaseActivity.IRIS_PHOTO_INDEX;
				intent.putExtra("index", index);
				startActivity(intent);
				
			}
		});

		btnOld.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(IrisEntryActivity.this,
						LensPhotoList.class);
				int index = LensShootBaseActivity.IRIS_PHOTO_INDEX;
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
