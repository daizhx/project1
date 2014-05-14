package com.hengxuan.ehealthplatform.application;

import java.util.ArrayList;
import java.util.List;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.log.Log;
import com.hengxuan.ehealthplatform.product.Product;

import android.app.Application;
import android.content.ContextWrapper;
import android.net.wifi.WifiManager;

public class EHTApplication extends Application {
	private int currentNetWorkId;
	//包含多少产品
	public static List<Product> productList = new ArrayList<Product>();
	private static EHTApplication instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		initProduct();
	}
	
	private void initProduct() {
		// TODO Auto-generated method stub
		//默认包含的产品
		Product massager = new Product(this, getString(R.string.massager));
		massager.isRecentUse = true;//test
		massager.setComments(R.string.massager_comment);
		massager.setLogo(R.drawable.massager_icon);
		productList.add(massager);
		Product iris = new Product(this, getString(R.string.iris_miriam));
		iris.isRecentUse = true;//test
		iris.setComments(R.string.iris_comment);
		iris.setLogo(R.drawable.iris_icon);
		productList.add(iris);
	}
	
	public void setCurrentNetWorkId(int i){
		currentNetWorkId = i;
	}
	public int getCurrentNetWorkId(){
		return currentNetWorkId;
	}

	public static EHTApplication getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}
	
	public void restoreWifiInfo(){
			Log.d("daizhx", "restoreWifiInfo");
			WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
			wifiManager.disconnect();
			int networkId = getCurrentNetWorkId();
			if(networkId >= 0){
				wifiManager.enableNetwork(networkId, true);
				wifiManager.reconnect();
			}else{
				//3G
				wifiManager.setWifiEnabled(false);
			}

	}
}
