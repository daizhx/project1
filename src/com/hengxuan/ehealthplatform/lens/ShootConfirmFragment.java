package com.hengxuan.ehealthplatform.lens;

import java.io.File;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.lens.hair.HairAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.iris.IrisAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.naevus.NaevusAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.skin.SkinAnalysisActivity;
import com.hengxuan.ehealthplatform.myview.MyImageView;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShootConfirmFragment extends Fragment {
	private View view;
	// absolutely path
	private String photoFile;
	private MyImageView photoImage;
	// click this to delete photoFile and reShoot
	private ImageView iVchacha;
	// click this to be Ok
	private ImageView iVcheck;
	private HandleClick mHandleClick;

	
	public void setFilePath(String filePath){
		photoFile = filePath;
	}
	public void setHandleClick(HandleClick h){
		mHandleClick = h;
	}
	
	public interface HandleClick{
		public void cancel();
		public void confirm();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_shoot_confirm, null, false);

		photoImage = (MyImageView) view.findViewById(R.id.img_photo);
		
		Bitmap bitmap = BitmapFactory.decodeFile(photoFile);
		photoImage.setImageBitmap(bitmap);

		iVchacha = (ImageView) view.findViewById(R.id.chacha);
		iVchacha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(photoFile);
				if (file.delete()) {
					// TODO
					mHandleClick.cancel();
				} else {
					// TODO
				}
			}
		});
		iVcheck = (ImageView) view.findViewById(R.id.shoot);
		iVcheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandleClick.confirm();
			}
		});
		return view;
	}
}
