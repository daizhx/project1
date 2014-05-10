package com.hengxuan.ehealthplatform.lens.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.hengxuan.ehealthplatform.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LensShootActivity extends Activity {
	
	private LensMonitorView mLensMonitorView;
	private Button eye_shoot_button;
	private boolean eye_staus;
	private String filename = "";
	private String savePath = "";
	private int iris_index = 0;
	private boolean flag = false;
	private WifiManager wifiManager;
	// 用户是否已拍照
	private boolean isfinish = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lens_shoot_activity);
		setIrisContentView();
	}
	
	private void setIrisContentView()
	{

		mLensMonitorView = (LensMonitorView)findViewById(R.id.mLensMonitorView);
//		eye_shoot_button = (Button)findViewById(R.id.eye_shoot_button);
		eye_shoot_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(eye_shoot_button.getText().toString()
						.equals(getResources().getString(R.string.photograph_text))){
					Bitmap bmp = mLensMonitorView.getCaptureImage();
					savePath = Environment.getExternalStorageDirectory()
					.toString()
					+ File.separator
					+ "dxlphoto"
					+ File.separator;
					filename = String.valueOf(System.currentTimeMillis())+".png";
					File file = new File(savePath.concat(filename));
					if(!file.getParentFile().exists())
						file.getParentFile().mkdirs();
					if(bmp != null)
					{
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
					mLensMonitorView.setRunning(false);
					isfinish = true;
					eye_shoot_button.setText(getResources().getString(R.string.back));
				}else{
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("image_path", savePath + filename);
					bundle.putInt("iris_image_index", LensShootActivity.this.iris_index);
					intent.putExtras(bundle);
		            mLensMonitorView.setRunning(false);
		            setResult(RESULT_OK, intent);
		            finish();
				}
			}   
			
		});
		LensMonitorParameter param = initParam();
		mLensMonitorView.setCmPara(param);
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
}
