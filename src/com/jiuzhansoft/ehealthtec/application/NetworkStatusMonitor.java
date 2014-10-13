package com.jiuzhansoft.ehealthtec.application;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusMonitor {
	//poor,can not access internet
	public static final int NETWORK_DISCONNECT = 0;
	//can access internet
	public static final int NETWORK_STATUS_LEVEL2 = 1;
	
	private Context mContext;
	public int networkStatus;
	
	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				String s = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
				boolean islostConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				
			}
		}
		
	}
	public NetworkStatusMonitor(Context c) {
		// TODO Auto-generated constructor stub
		mContext = c;
		NetworkInfo networkInfo = ((ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(networkInfo == null){
			//ÎÞÍøÂçÁ¬½Ó
			networkStatus = NETWORK_DISCONNECT;
		}else{
			
		}
		MyBroadcastReceiver br = new MyBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		c.registerReceiver(br, intentFilter);
	}
}
