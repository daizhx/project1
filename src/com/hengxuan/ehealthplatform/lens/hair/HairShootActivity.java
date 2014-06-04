package com.hengxuan.ehealthplatform.lens.hair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.lens.LensShootBaseActivity;
import com.hengxuan.ehealthplatform.lens.ShootConfirmActivity;
import com.hengxuan.ehealthplatform.lens.skin.SkinAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.skin.SkinShootActivity;

public class HairShootActivity extends LensShootBaseActivity {
	//the photo file in absolutely path
	private String photoFile;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.hair_photo);
		ivShoot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photoFile = savePhoto();
				Intent intent = new Intent(HairShootActivity.this, ShootConfirmActivity.class);
				Intent tointent = new Intent();
				tointent.setAction(SkinAnalysisActivity.ACTION);
				Bundle bundle = new Bundle();
				bundle.putParcelable("intent", tointent);
				intent.putExtra("bundle", tointent);
				intent.putExtra(PHOTO_PATH, photoFile);
				startActivity(intent);
			}
		});
	}

}
