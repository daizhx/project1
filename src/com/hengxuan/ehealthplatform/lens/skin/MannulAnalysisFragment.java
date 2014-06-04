package com.hengxuan.ehealthplatform.lens.skin;

import com.hengxuan.ehealthplatform.R;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MannulAnalysisFragment extends Fragment {
	//the fragment ui
	public SkinTouchView imageView;
	private String picPath;
	private TextView mTextView;
	private int analysisMode;
	private RelativeLayout imageContainer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		imageContainer = (RelativeLayout) inflater.inflate(R.layout.fragment_image_container, null, false);
		imageView = new SkinTouchView(getActivity());
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setAnalysisMode(analysisMode);
		imageView.setTextView(mTextView);
		Bitmap bitmap = BitmapFactory.decodeFile(picPath);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenHeight = displaymetrics.heightPixels;
		imageView.init(getActivity(), bitmap,
				screenHeight / 20);
		imageContainer.addView(imageView, 0, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
//		imageView.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View arg0, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return imageView.getMultiTouchController().onTouchEvent(event);
//			}
//		});
		
		mTextView.setVisibility(View.VISIBLE);
		imageView.setDrawLine(false);
		return imageContainer;
	}
	
	
	
	public void setTextView(TextView tv){
		mTextView = tv;
	}
	
	public void setAnalysisMode(int mode){
		analysisMode = mode;
		if(imageView != null){
			imageView.setAnalysisMode(mode);
			imageView.setDrawLine(false);
		}
	}
	
	public void setPicPath(String s){
		picPath = s;
	}
	
	
}
