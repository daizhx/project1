package com.jiuzhansoft.ehealthtec.lens;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.iris.old.IrisAnalysisActivity;
import com.jiuzhansoft.ehealthtec.lens.hair.HairAnalysisActivity;
//import com.jiuzhansoft.ehealthtec.lens.iris.IrisAnalysisActivity;
import com.jiuzhansoft.ehealthtec.lens.naevus.NaevusAnalysisActivity;
import com.jiuzhansoft.ehealthtec.lens.skin.SkinAnalysisActivity;
import com.jiuzhansoft.ehealthtec.myview.MyImageView;

public class ShootConfirmActivity extends BaseActivity {
	//absolutely path
	private String photoFile;
	private MyImageView photoImage;
	//click this to delete photoFile and reShoot
	private ImageView iVchacha;
	//click this to be Ok
	private ImageView iVcheck;
	private Intent tointent;
	private int index;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoot_confirm);
		photoImage = (MyImageView)findViewById(R.id.img_photo);
		Intent intent = getIntent();
		photoFile = intent.getStringExtra(LensShootBaseActivity.PHOTO_PATH);
		index = intent.getIntExtra("index", 0);
		Bundle bundle = intent.getBundleExtra("bundle");
		if(bundle != null){
			tointent = bundle.getParcelable("intent");
		}
		Bitmap bitmap = BitmapFactory.decodeFile(photoFile);
		photoImage.setImageBitmap(bitmap);
		
		
		iVchacha = (ImageView)findViewById(R.id.chacha);
		iVchacha.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(photoFile);
				if(file.delete()){
					//TODO
				}else{
					//TODO
				}
				finish();
			}
		});
		iVcheck = (ImageView)findViewById(R.id.shoot);
		iVcheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (index) {
				case LensShootBaseActivity.IRIS_PHOTO_INDEX:
					intent = new Intent(ShootConfirmActivity.this, IrisAnalysisActivity.class);
					break;
				case LensShootBaseActivity.SKIN_PHOTO_INDEX:
					intent = new Intent(ShootConfirmActivity.this, SkinAnalysisActivity.class);
				case LensShootBaseActivity.HAIR_PHOTO_INDEX:
					intent = new Intent(ShootConfirmActivity.this, HairAnalysisActivity.class);
				case LensShootBaseActivity.NAEVUS_PHOTO_INDEX:
					intent = new Intent(ShootConfirmActivity.this, NaevusAnalysisActivity.class);
				default:
					break;
				}
				intent.putExtra(LensShootBaseActivity.PHOTO_PATH, photoFile);
				startActivity(intent);
//				tointent.putExtra(LensShootBaseActivity.PHOTO_PATH, photoFile);
//				startActivity(tointent);
			}
		});
	}
}
