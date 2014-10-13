package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.IBean;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Msg;

public interface ICallback {
	public void onReceive(IBean bean);
	public void onMessage(Msg message);
	public void onError(com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Error error);
}
