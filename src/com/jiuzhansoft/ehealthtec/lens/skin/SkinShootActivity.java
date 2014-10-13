package com.jiuzhansoft.ehealthtec.lens.skin;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.lens.LensShootBaseActivity;
import com.jiuzhansoft.ehealthtec.lens.ShootConfirmActivity;

public class SkinShootActivity extends LensShootBaseActivity {
	private String photoPath;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.skin_photo);
		ivShoot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photoPath = savePhoto();
				Intent intent = new Intent(SkinShootActivity.this, ShootConfirmActivity.class);
				Intent tointent = new Intent();
				tointent.setAction(SkinAnalysisActivity.ACTION);
				Bundle bundle = new Bundle();
				bundle.putParcelable("intent", tointent);
				intent.putExtra("bundle", tointent);
				intent.putExtra(PHOTO_PATH, photoPath);
				startActivity(intent);
			}
		});
		
	}
}
