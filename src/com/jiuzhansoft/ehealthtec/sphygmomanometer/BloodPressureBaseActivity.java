package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.IBean;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Msg;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Error;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.service.RbxtApp;
import android.os.Bundle;

public class BloodPressureBaseActivity extends BaseActivity implements ICallback{
	
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
	public static RbxtApp rbxt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(rbxt == null){
			rbxt = new RbxtApp();
			rbxt.init();
		}else{
			rbxt.getService().setRunning(true);
		}		
	}
	protected void onStart() {
		super.onStart();
		rbxt.setCall(this);
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onMessage(Msg message) {
		
	}

	public void onReceive(IBean bean) {
		
	}
	@Override
	public void onError(Error error) {
		// TODO Auto-generated method stub
		
	}
}
