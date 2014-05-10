package com.hengxuan.ehealthplatform.lens.activity;

import java.sql.Connection;
import java.util.List;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.log.Log;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.Toast;

public class ConnectivityUtils {
	private Context mContext;
	private static ConnectivityUtils mConnectivityUtils;
	private ConnectivityManager mConnectivityManager;
	private WifiManager mWifiManager;
	private NetworkInfo mNetworkInfo;
	private WifiInfo mWifiInfo;
	private ProgressDialog mProgressDialog;
	private BroadcastReceiver mBroadcastReceiver;
	
	private ConnectivityUtils(Context context){
//		mContext = context.getApplicationContext();
		mContext = context;
		mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		mBroadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
					if(connect2Lens()){
						((ConnectLensListener)mContext).onConnectSuccess();
					}else{
						((ConnectLensListener)mContext).onConnectFail();
					}
				}else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
					if(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED){
						//scan lens ap and connect
						mWifiManager.startScan();
					}
				}
				
			}
		};
		
	}
	public static ConnectivityUtils getInstance(Context context){

		if(mConnectivityUtils == null){
			mConnectivityUtils = new ConnectivityUtils(context);
		}
		return mConnectivityUtils;
	}
	
	private void showProgressDialog(){
		//show a progress
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(mContext.getResources().getText(R.string.search_wifi));
		mProgressDialog.show();
	}
	
	public synchronized void connect2LensExt(){
		mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if(mNetworkInfo == null){
			//no network connected,should not occurred
			//TODO,open WIFI
			return;
		}
		if(mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
			//save wifi info
			mWifiInfo = mWifiManager.getConnectionInfo();
			
			mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			mWifiManager.startScan();
			showProgressDialog();
			
		}else if(mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
			
			if(mWifiManager.isWifiEnabled()){
				//NO AP connected
				mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				mWifiManager.startScan();
				//show a progress
				showProgressDialog();
			}else{
				//open WIFI
				mWifiManager.setWifiEnabled(true);
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
				intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				mContext.registerReceiver(mBroadcastReceiver, intentFilter);
				showProgressDialog();
			}
			
		}else{
			//TODO
		}
		return;
	}
	
	public synchronized void disconnect2LensExt(){
		if(mWifiInfo == null){
			mWifiManager.setWifiEnabled(false);
		}else{
			mWifiManager.disconnect();
			mWifiManager.enableNetwork(mWifiInfo.getNetworkId(), true);
			mWifiManager.reconnect();
		}
	}
	
	private boolean connect2Lens(){
		mProgressDialog.dismiss();
		mContext.unregisterReceiver(mBroadcastReceiver);
		List<ScanResult> APList = mWifiManager.getScanResults();
		for(ScanResult scanResult : APList){
			if(scanResult.SSID.equals("EHT")){
				List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
				for(WifiConfiguration wifiConfiguration : list){
					Log.v("daizhx", "ssid:"+wifiConfiguration.SSID);
					if(wifiConfiguration.SSID != null && wifiConfiguration.SSID.equals("\"" + "EHT" + "\"")){
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
		//not found EHT AP
		Toast.makeText(mContext, R.string.no_lens, Toast.LENGTH_SHORT).show();
		return false;		
	}
	

	public boolean isConnectLens(){
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		ssid = ssid.replaceAll("\"", "");
		if(!TextUtils.isEmpty(ssid) && ssid.substring(0, 3).equals("EHT")){
			return true;
		}
		return false;
	}
	
	public interface ConnectLensListener{
		public void onConnectSuccess();
		public void onConnectFail();
	}
}
