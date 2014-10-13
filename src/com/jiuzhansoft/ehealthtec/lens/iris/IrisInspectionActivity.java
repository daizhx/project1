package com.jiuzhansoft.ehealthtec.lens.iris;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensMonitorParameter;
import com.jiuzhansoft.ehealthtec.lens.LensMonitorView;

public class IrisInspectionActivity extends BaseActivity implements OnClickListener {
	
	private LensMonitorView lensMonitorView;
	private ImageView mShootIcon;
	private String savePath = "";
	private String filename = "";
	private ViewGroup setting;
	private ImageView shootBound;
	private TextView hintText;
	private ImageView ivChooseEye;
	private TextView tvPhotoLable;
	private TextView tvLeftEye;
	private TextView tvRightEye;
	private int iris_index;//LEFT_IRIS_EYE_ID,RIGHT_IRIS_EYE_ID
	public static final int LEFT_IRIS_EYE_ID = 0;
	public static final int RIGHT_IRIS_EYE_ID = 1;
	private final int idOK = 1; 
	
	private void initView(){
		lensMonitorView = (LensMonitorView)findViewById(R.id.lens_monitor_view);
		LensMonitorParameter para = initParam();
		lensMonitorView.setCmPara(para);
		lensMonitorView.start();
		mShootIcon = (ImageView)findViewById(R.id.shoot);
		mShootIcon.setOnClickListener(this);
		setting = (ViewGroup)findViewById(R.id.setting);
		shootBound = (ImageView)findViewById(R.id.center);
		hintText = (TextView)findViewById(R.id.tv_hint);
		ivChooseEye = (ImageView)findViewById(R.id.eyes);
		ivChooseEye.setOnClickListener(this);
		tvPhotoLable = (TextView)findViewById(R.id.eye_label);
		tvPhotoLable.setOnClickListener(this);
		tvLeftEye = (TextView)findViewById(R.id.tv_left_eye);
		tvLeftEye.setOnClickListener(this);
		tvRightEye = (TextView)findViewById(R.id.tv_right_eye);
		tvRightEye.setOnClickListener(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iris);
		initView();
	}

	
	private LensMonitorParameter initParam()
	{
		LensMonitorParameter param = new LensMonitorParameter();
		param.setId(1);
		param.setConnectType(0);
		param.setIp("10.10.10.254");
		param.setLocal_dir("/sdcard");
		param.setName("192.168.1.102");
		param.setUsername("aaaaa");
		param.setPassword("123456");
		param.setPort(8080);
		param.setTime_out(2000);
		param.setConnectType(BIND_AUTO_CREATE);
		return param;
	}


	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		lensMonitorView.stop();
		super.onDestroy();
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.shoot:
			saveImage();
			lensMonitorView.setRunning(false);
			hideView();
			mShootIcon.setImageResource(R.drawable.save);
			mShootIcon.setId(idOK);
			if(tvPhotoLable.getVisibility() != View.VISIBLE){
				tvPhotoLable.setVisibility(View.VISIBLE);
			}
			break;
		case idOK:
			go2AnalysisActivity();
			break;
		case R.id.eyes:
			tvPhotoLable.setVisibility(View.INVISIBLE);
			if(ivChooseEye.getVisibility() == View.GONE){
				ivChooseEye.setVisibility(View.VISIBLE);
				ivChooseEye.setImageResource(R.drawable.eyes_choose);
			}else{
				ivChooseEye.setVisibility(View.GONE);
				ivChooseEye.setImageResource(R.drawable.eyes);
			}
			break;
		case R.id.tv_left_eye:
			tvLeftEye.setBackgroundColor(Color.rgb(0x0F, 0x86, 0xFE));
			tvRightEye.setBackgroundResource(0);
			tvPhotoLable.setText(R.string.left_eye);
			iris_index = LEFT_IRIS_EYE_ID;
			break;
		case R.id.tv_right_eye:
			tvRightEye.setBackgroundColor(Color.rgb(0x0F, 0x86, 0xFE));
			tvLeftEye.setBackgroundResource(0);
			tvPhotoLable.setText(R.string.Right_eye);
			iris_index = RIGHT_IRIS_EYE_ID;
			break;
		default:
			break;
		}
	}
	
	/*start IrisAnalysisActivity with the shoot image that saved in file*/
	private void go2AnalysisActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(IrisInspectionActivity.this, IrisAnalysisActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("image_path", savePath + filename);
		bundle.putInt("iris_image_index", iris_index);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void hideView() {
		// TODO Auto-generated method stub
		setting.setVisibility(View.INVISIBLE);
		shootBound.setVisibility(View.INVISIBLE);
		hintText.setVisibility(View.INVISIBLE);
		ivChooseEye.setVisibility(View.INVISIBLE);
		ivChooseEye.setEnabled(false);
	}
	
	private void reShowView() {
		// TODO Auto-generated method stub
		setting.setVisibility(View.VISIBLE);
		shootBound.setVisibility(View.VISIBLE);
		hintText.setVisibility(View.VISIBLE);
		ivChooseEye.setEnabled(true);
	}


	/*get photo bitmap and compress in the a PNG file*/
	private void saveImage(){
		Bitmap bmp = lensMonitorView.getCaptureImage();
		savePath = Environment.getExternalStorageDirectory()
		.toString()
		+ File.separator
		+ "dxlphoto"
		+ File.separator;
		filename = String.valueOf(System.currentTimeMillis())+".png";
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
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

}
