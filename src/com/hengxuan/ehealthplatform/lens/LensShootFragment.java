package com.hengxuan.ehealthplatform.lens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.log.Log;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LensShootFragment extends Fragment {
	//UI
	private View view;
	protected LensMonitorView lensMonitorView;
	protected ImageView ivShoot;
	public static final String PHOTO_PATH = "photoPath";
	private HandleClick mHandleClick;
	private int index;
//	private TextView eyeLabel;
//	private ImageView eyesChooseIcon;
//	private TextView tvLeftEye, tvRightEye;
//	private boolean openedEyesChooseBar = false;
	
	
	public interface HandleClick{
		public void shoot();
		public void exit();
	}
	
	public void setHanleClick(HandleClick h){
		mHandleClick = h;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle = getArguments();
		index = bundle.getInt("index");
		
		view = inflater.inflate(R.layout.lens_shoot, null, false);
		lensMonitorView = (LensMonitorView)view.findViewById(R.id.lens_monitor_view);
		(view.findViewById(R.id.chacha)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//关闭activity
			}
		});
		
		ivShoot = (ImageView)view.findViewById(R.id.shoot);
		ivShoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LensBaseActivity.photoPath = savePhoto();
				//切换到shootConfirmFragment
				mHandleClick.shoot();
			}
		});
		
		if(index == LensBaseActivity.INDEX_IRIS){
//			eyesChooseIcon = (ImageView) view.findViewById(R.id.eyes);
//			final View eyesChooseBar = view.findViewById(R.id.eye_choose_bar);
//			eyeLabel = (TextView)view.findViewById(R.id.eye_label);
//			tvLeftEye = (TextView)view.findViewById(R.id.tv_left_eye);
//			tvRightEye = (TextView)view.findViewById(R.id.tv_right_eye);
//			eyeLabel.setVisibility(View.VISIBLE);
//			eyesChooseIcon.setVisibility(View.VISIBLE);
//			eyesChooseIcon.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					if(openedEyesChooseBar){
//						eyesChooseIcon.setImageResource(R.drawable.eyes);
//						openedEyesChooseBar = false;
//						eyesChooseBar.setVisibility(View.GONE);
//					}else{
//						eyesChooseIcon.setImageResource(R.drawable.eyes_choose);
//						openedEyesChooseBar = true;
//						eyesChooseBar.setVisibility(View.VISIBLE);
//					}
//				}
//			});
//			tvLeftEye.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					tvLeftEye.setBackgroundColor(Color.BLUE);
//					tvRightEye.setBackgroundColor(Color.TRANSPARENT);
//					eyeLabel.setText(R.string.left_eye);
//					((LensBaseActivity)getActivity()).setIrisIndex(1);
//				}
//			});
//			tvRightEye.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					tvLeftEye.setBackgroundColor(Color.TRANSPARENT);
//					tvRightEye.setBackgroundColor(Color.BLUE);
//					eyeLabel.setText(R.string.Right_eye);
//					((LensBaseActivity)getActivity()).setIrisIndex(2);
//				}
//			});
			//初始值
			((LensBaseActivity)getActivity()).setIrisIndex(1);
		}
		
		return view;
	}
	
	
	
	
	/**
	 * 
	 * @return the saved photo Path
	 */
	protected String savePhoto(){
		String filePath = null;
		Bitmap bmp = lensMonitorView.getCaptureImage();
		String savePath = Environment.getExternalStorageDirectory()
		.toString()
		+ File.separator
		+ "dxlphoto"
		+ File.separator;
		String filename = String.valueOf(System.currentTimeMillis())+".png";
		File file = new File(savePath.concat(filename));
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if (bmp != null) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(savePath + filename);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				return savePath + filename;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filePath;
	}
	
	
}
