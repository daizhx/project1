package com.hengxuan.ehealthplatform.lens.activity;


import com.hengxuan.ehealthplatform.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FileDataActivity extends Activity {
	private ImageView image;
	private Bitmap abitmap;
	private Bitmap obitmap;
	private boolean o;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nevus_file_data);
		
		String path = getIntent().getExtras().getString("o");
		
		obitmap = BitmapFactory.decodeFile(path);
		abitmap = BitmapFactory.decodeFile(path.substring(0, path.length() - 5) + "a.png");
		
		image = (ImageView)findViewById(R.id.naevus_file_data_image);
		
		image.setImageBitmap(abitmap);
		
		image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(o){
					image.setImageBitmap(abitmap);
					o = false;
				}else{
					image.setImageBitmap(obitmap);
					o = true;
				}
			}
			
		});
		
	}
}
