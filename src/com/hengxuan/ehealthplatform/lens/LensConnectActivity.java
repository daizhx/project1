/**
 * 
 */
package com.hengxuan.ehealthplatform.lens;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.application.EHTApplication;
import com.hengxuan.ehealthplatform.lens.iris.IrisInspectionActivity;
import com.hengxuan.ehealthplatform.lens.skin.SkinShootActivity;
import com.hengxuan.ehealthplatform.log.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class LensConnectActivity extends Activity {
	private static final String TAG = "LensConnect";
	private WifiManager mWifiManager;
	private WifiInfo currentWifiInfo;
	private int index;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
				if(connect2Lens()){
					onConnectSuccess();
				}else{
					onConnectFail();
				}
			}else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
				if(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED){
					//scan lens ap and connect
					mWifiManager.startScan();
				}
			}
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TextView text = new TextView(this);
		text.setText("connecting Lens ...");
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
		text.setLayoutParams(lp);
		setContentView(text);
		index = getIntent().getIntExtra("index", 0);
		
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		if(mWifiManager.isWifiEnabled()){
			if(isConnectLens()){
				onConnectSuccess();
				return;
			}else{
				mWifiManager.startScan();
			}
		}else{
			//open wifi
			mWifiManager.setWifiEnabled(true);
		}
	}
	
	
	private boolean connect2Lens(){
		List<ScanResult> APList = mWifiManager.getScanResults();
		for(ScanResult scanResult : APList){
			if(scanResult.SSID.equals("EHT")){
				List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
				for(WifiConfiguration wifiConfiguration : list){
					Log.v(TAG, "ssid:"+wifiConfiguration.SSID);
					if(wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\"" + "EHT" + "\"")){
						currentWifiInfo = mWifiManager.getConnectionInfo();
//						((EHTApplication)getApplication()).setCurrentNetWorkId(currentWifiInfo.getNetworkId());
						mWifiManager.disconnect();
						mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
						mWifiManager.reconnect();
						return true;
					}
				}
				
				//add conf
				WifiConfiguration conf = new WifiConfiguration();
				conf.SSID = "\"" + "EHT" + "\"";
				conf.preSharedKey = "\"" + "12345678" + "\"";
				mWifiManager.addNetwork(conf);
				for(WifiConfiguration wifiConfiguration : list){
					if(wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\"" + "EHT" + "\"")){
						currentWifiInfo = mWifiManager.getConnectionInfo();
//						((EHTApplication)getApplication()).setCurrentNetWorkId(currentWifiInfo.getNetworkId());
						mWifiManager.disconnect();
						mWifiManager.enableNetwork(wifiConfiguration.networkId, true);
						mWifiManager.reconnect();
						return true;
					}
				}
				//if found EHT AP,mostly return true,should not return false,there must be something wrong
				return false;
			}
		}
		return false;		
	}
	
	public boolean isConnectLens(){
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		ssid = ssid.replaceAll("\"", "");
		if(ssid.length() < 3){
			//prevent throwing a IndexOutBoundException
			return false;
		}
		if(!TextUtils.isEmpty(ssid) && ssid.substring(0, 3).equals("EHT")){
			return true;
		}
		return false;
	}	

	public void onConnectSuccess() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectSuccess");
		unregisterReceiver(mBroadcastReceiver);
		
		//delay 2s
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				//startActivity(new Intent(LensConnectActivity.this, IrisInspectionActivity.class));
//				startActivity(new Intent(LensConnectActivity.this, SkinShootActivity.class));
//				finish();
//			}
//		}, 2000);
		Log.d(TAG, "index="+index);
		if(index == LensShootBaseActivity.IRIS_PHOTO_INDEX){
			Intent intent = new Intent(LensConnectActivity.this, IrisInspectionActivity.class);
			intent.putExtra("index", index);
			startActivity(intent);
		}else{
			Intent intent = new Intent(LensConnectActivity.this, LensShootBaseActivity.class);
			intent.putExtra("index", index);
			startActivity(intent);
		}
		finish();
	}

	public void onConnectFail() {
		Log.d(TAG, "onConnectFail");
		unregisterReceiver(mBroadcastReceiver);
		// TODO Auto-generated method stub
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setMessage(getString(R.string.open_lens));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getString(R.string.confirm), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		alertDialog.show();
	}
	
	
}
