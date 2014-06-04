package com.hengxuan.ehealthplatform.lens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.log.Log;

/**
 *this class and subclass should only work with lens connected 
 * @author daizhx
 *
 */
public class LensShootBaseActivity extends BaseActivity {
	protected LensMonitorView lensMonitorView;
	protected ImageView ivShoot;
	public static final String PHOTO_PATH = "photoPath";
	private String photoPath;
	//index of photo class
	private int index;
	public static final int IRIS_PHOTO_INDEX = 1;
	public static final int SKIN_PHOTO_INDEX = 2;
	public static final int HAIR_PHOTO_INDEX = 3;
	public static final int NAEVUS_PHOTO_INDEX = 4;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		index = getIntent().getIntExtra("index", 0);
		setContentView(R.layout.lens_shoot);
		lensMonitorView = (LensMonitorView)findViewById(R.id.lens_monitor_view);
		findViewById(R.id.chacha).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//lensMonitorView.stop();
				finish();
			}
		});
		
		ivShoot = (ImageView)findViewById(R.id.shoot);
		ivShoot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photoPath = savePhoto();
				Intent intent = new Intent(LensShootBaseActivity.this, ShootConfirmActivity.class);
				intent.putExtra("index", index);
				intent.putExtra("photoPath", photoPath);
				Log.d("lens", "LensShootBaseActivity: index="+index + ", photoPath="+photoPath);
				startActivity(intent);
				finish();
			}
		});
		
		
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//lensMonitorView.stop();
	}
}
