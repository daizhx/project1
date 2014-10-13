package com.jiuzhansoft.ehealthtec.weight;
import android.os.Parcelable;

public abstract class IBean implements Parcelable {
	public final static int STABLE = 0;
	public final static int DYNAMIC = 1;
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
