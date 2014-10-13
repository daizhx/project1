package com.jiuzhansoft.ehealthtec.weight;

import android.os.Handler;
import android.os.Message;

public class BodyfatApp {

	public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	private BodyfatBluetoothService service;

	private BodyfatCallback call;

	private Handler mmHandler;

	public void init(){
		createHandler();
		setupChat();
		service.setHandler(mmHandler);
		service.setRunning(true);
	}
	
	public void setupChat(){
		if (service == null)
		service = new BodyfatBluetoothService();
	}

	public BodyfatCallback getCall() {
		return call;
	}

	public void setCall(BodyfatCallback call) {
		this.call = call;
	}

	public BodyfatBluetoothService getService() {
		return service;
	}

	private void createHandler() {
		mmHandler = new Handler() {
			public void handleMessage(Message msg) {
				IBean weight = msg.getData().getParcelable("weight");
				IBean damp = msg.getData().getParcelable("damp");
				if (call != null){
					//switch (msg.what) {					
					//case IBean.STABLE:
						getCall().onReceive(weight, damp);
						//break;
					//case IBean.DYNAMIC:	
						//getCall().onReceive(weight, damp);						
						//break;
					//}
				}
			}
		};
	}
}
