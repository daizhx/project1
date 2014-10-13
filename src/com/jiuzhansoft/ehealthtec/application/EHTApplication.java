package com.jiuzhansoft.ehealthtec.application;

import java.util.ArrayList;
import java.util.List;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.constant.ConstEquipId;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.product.Product;
import com.jiuzhansoft.ehealthtec.user.UserLogin;

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
	public NetworkStatusMonitor mNetworkStatusMonitor;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		initProducts();
		mNetworkStatusMonitor = new NetworkStatusMonitor(this);
		//捕捉未捕捉的异常
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this));
	}

	/**
	 * 注册产品
	 */
	private void initProducts() {
		// TODO Auto-generated method stub
		//默认包含的产品
		Product massager = new Product(getString(R.string.massager), ConstEquipId.MASSAGEID, null, R.drawable.massager_icon, null);
		massager.isRecentUse = true;//test
		massager.setComments(R.string.massager_comment);
		massager.setEntryIntent("com.ehealthplatform.intent.action.MASSAGER");
		massager.setTheCode(ConstHttpProp.ANMOCODE);
		massager.setVerification();
		productList.add(massager);
		
		Product Lens_iris = new Product(getString(R.string.iris_analysis), ConstEquipId.LENSID, null, R.drawable.lens_icon, null);
		Lens_iris.isRecentUse = true;//test
		Lens_iris.setComments(R.string.iris_comment);
		Lens_iris.setEntryIntent("com.ehealthplatform.intent.action.LENS_IRIS");
		productList.add(Lens_iris);
		
		Product musicMassager = new Product(getString(R.string.music_massage), ConstEquipId.MASSAGEID, null, R.drawable.music_massage_entry, "com.ehealthplatform.intent.action.MusicMASSAGER");
		musicMassager.setVerification();
		productList.add(musicMassager);
		
		Product Lens_skin = new Product(getString(R.string.skin_analysis), ConstEquipId.LENSID, null, R.drawable.skin_entry, "com.ehealthplatform.intent.action.LENS_SKIN");
		productList.add(Lens_skin);
		
		Product Lens_naevus = new Product(getString(R.string.naevus_analysis), ConstEquipId.LENSID, null, R.drawable.naevus_entry, "com.ehealthplatform.intent.action.LENS_NAEVUS");
		productList.add(Lens_naevus);
		
		Product lens_hair = new Product(getString(R.string.hair_analysis), ConstEquipId.LENSID, null, R.drawable.hair_entry, "com.ehealthplatform.intent.action.LENS_HAIR");
		productList.add(lens_hair);
		
		//注册体重称
		Product weight_scale = new Product(getString(R.string.weighting_scale), ConstEquipId.WEIGHTID, null, R.drawable.weight_entry, "com.ehealthplatform.intent.action.WEIGHT");
		productList.add(weight_scale);
		
		//注册血压计
		Product sphygmomanometer = new Product(getString(R.string.sphygmomanometer), ConstEquipId.BLOODPRESSID, null, R.drawable.bloody_entry, "com.ehealthplatform.intent.action.sphygmomanometer");
		productList.add(sphygmomanometer);
	}
	
	public static EHTApplication getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}
	
}
