package com.hengxuan.ehealthplatform.lens.activity;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hengxuan.ehealthplatform.R;

import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.lens.activity.ConnectivityUtils.ConnectLensListener;
import com.hengxuan.ehealthplatform.log.Log;


public class NevusMainActivity extends BaseActivity implements ConnectLensListener {
	
	private Button getBtn;
	private Button anaBtn;
	
	private ImageView nevusPic;
	private String picPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.naevus_analysis);
		setContentView(R.layout.nevus_main);
		
		getBtn = (Button)findViewById(R.id.get_naevus_picture);
		getBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConnectivityUtils connectivityUtils = ConnectivityUtils.getInstance(NevusMainActivity.this);
				connectivityUtils.connect2LensExt();
			}
		});
		anaBtn = (Button)findViewById(R.id.ana_naevus_picture);
		anaBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NevusMainActivity.this, NevusAnalysisActivity.class);
				intent.putExtra("picPath", picPath);
				startActivityForResult(intent, 0);
			}
			
		});
		anaBtn.setEnabled(false);
		nevusPic = (ImageView)findViewById(R.id.naevus_picture);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		// TODO Auto-generated method stub
		if(intent == null){
			return;
		}
		Bundle bundle = intent.getExtras();
		picPath = bundle.getString("image_path");
		File file = new File(picPath);
		if(file.exists()){
			nevusPic.setImageBitmap(BitmapFactory.decodeFile(picPath));
			anaBtn.setEnabled(true);
		}
	}

	//连接lens成功时,调用该函数
	@Override
	public void onConnectSuccess() {
		// TODO Auto-generated method stub
		Log.v("daizhx", "onConnectSuccess");
		try {
			DatagramSocket s = new DatagramSocket();
			InetAddress local = InetAddress.getByName("10.10.10.254");
			byte[] message = {(byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C, 0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65, 0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD};
			DatagramPacket datagramPacket = new DatagramPacket(message, message.length, local, 8080);
			s.send(datagramPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(NevusMainActivity.this, LensShootActivity.class);
		intent.putExtra("index", 2);
		startActivityForResult(intent, 0);		
	}

	@Override
	public void onConnectFail() {
		// TODO Auto-generated method stub
		
	}
}
