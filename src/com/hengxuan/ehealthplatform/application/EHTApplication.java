package com.hengxuan.ehealthplatform.application;

import java.util.ArrayList;
import java.util.List;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.constant.PreferenceKeys;
import com.hengxuan.ehealthplatform.log.Log;
import com.hengxuan.ehealthplatform.product.Product;
import com.hengxuan.ehealthplatform.user.UserLogin;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;

public class EHTApplication extends Application {

	//包含多少产品
	public static List<Product> productList = new ArrayList<Product>();
	private static EHTApplication instance;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		initProduct();
		//捕捉未捕捉的异常
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this));
	}


	private void initProduct() {
		// TODO Auto-generated method stub
		//默认包含的产品
		Product massager = new Product(this, getString(R.string.massager));
		massager.isRecentUse = true;//test
		massager.setComments(R.string.massager_comment);
		massager.setLogo(R.drawable.massager_icon);
		productList.add(massager);
		Product iris = new Product(this, getString(R.string.iris));
		iris.isRecentUse = true;//test
		iris.setComments(R.string.iris_comment);
		iris.setLogo(R.drawable.lens_icon);
		productList.add(iris);
	}
	


	public static EHTApplication getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}
	

	
	
}
