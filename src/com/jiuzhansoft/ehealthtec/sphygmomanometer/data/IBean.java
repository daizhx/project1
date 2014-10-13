package com.jiuzhansoft.ehealthtec.sphygmomanometer.data;
import android.os.Parcelable;

public abstract class IBean implements Parcelable {
	public final static int DATA = 0;
	public final static int MESSAGE = 1;
	public final static int ERROR = 2;
	public final static int PRESSURE = 3;
	public Head head;
	
	public IBean(){
		head = null;
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}
	
	public abstract void analysis(int[] i);
}
