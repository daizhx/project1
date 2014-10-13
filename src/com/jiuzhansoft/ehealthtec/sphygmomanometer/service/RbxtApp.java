package com.jiuzhansoft.ehealthtec.sphygmomanometer.service;



import com.jiuzhansoft.ehealthtec.sphygmomanometer.ICallback;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.IBean;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Error;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Msg;

import android.os.Handler;
import android.os.Message;

public class RbxtApp {

	public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	private BluetoothService service;

	private ICallback call;

	private Handler mmHandler;

	public void init(){
		createHandler();
		setupChat();
		service.setHandler(mmHandler);
		service.setRunning(true);
	}
	
	public void setupChat(){
		if (service == null)
		service = new BluetoothService();
	}

	public ICallback getCall() {
		return call;
	}

	public void setCall(ICallback call) {
		this.call = call;
	}

	public BluetoothService getService() {
		return service;
	}

	private void createHandler() {
		mmHandler = new Handler() {
			public void handleMessage(Message msg) {
				IBean bean = msg.getData().getParcelable("bean");
			System.out.println(bean.toString() + "aaaaaaaaaaaaaaaaaass");
				if (call != null){
					switch (msg.what) {					
					case IBean.ERROR:
						getCall().onError((Error) bean);
						break;
					case IBean.MESSAGE:	
						System.out.println(getCall().toString()+"getCall()getCall()getCall()");
						System.out.println(Msg.class+"aaaaaa");
						getCall().onMessage((Msg) bean);						
						break;
					case IBean.DATA:
						getCall().onReceive(bean);
						break;
					}
				}
			}
		};
	}
}
