package com.hengxuan.ehealthplatform.http;

import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;


public class HttpGroupSetting {
	private BaseActivity myActivity;
	private int priority;
	private int type;

	public HttpGroupSetting() {

	}

	public BaseActivity getMyActivity() {
		return myActivity;
	}

	public int getPriority() {
		return priority;
	}

	public int getType() {
		return type;
	}

	public void setMyActivity(BaseActivity activity) {
		this.myActivity = activity;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setType(int type) {
		this.type = type;
		if (this.priority == 0)
			switch (type) {
			case ConstHttpProp.TYPE_JSON:
				setPriority(ConstHttpProp.PRIORITY_JSON);
				break;
			case ConstHttpProp.TYPE_IMAGE:
				setPriority(ConstHttpProp.PRIORITY_IMAGE);
				break;
			default:
				break;
			}
	}
}