package com.hengxuan.ehealthplatform.application;

import java.util.ArrayList;
import java.util.List;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.product.Product;

import android.app.Application;
import android.content.ContextWrapper;

public class EHTApplication extends Application {
	private int currentNetWorkId;
	//�������ٲ�Ʒ
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
		//Ĭ�ϰ����Ĳ�Ʒ
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
	
	
}
