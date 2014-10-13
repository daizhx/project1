package com.jiuzhansoft.ehealthtec.weight;

import android.os.Parcel;
import android.os.Parcelable;

public class Damp extends IBean{
	private int high;
	private int low;
	private int damp;
	private boolean error = false;
	
	public Damp(){}
	
	public Damp(int high, int low){
		this.high = high;
		this.low = low;
		this.damp = high * 10 + low;
	}
	
	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getDamp() {
		return damp;
	}

	public void setDamp(int weight) {
		this.damp = weight;
	}
	
	public boolean getError(){
		return error;
	}
	
	public void setError(boolean error){
		this.error = error;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(high);
		dest.writeInt(low);
		dest.writeInt(damp);
	}

	@Override
	public void analysis(int[] i) {
		// TODO Auto-generated method stub
		high = i[10];
		low = i[11];
		if(high == 0xEE && low == 0xEE)
			error = true;
		else
			damp = high * 256 + low;
	}
	
	public static final Parcelable.Creator<Damp> CREATOR = new Parcelable.Creator<Damp>() {
		public Damp createFromParcel(Parcel in) {
			return new Damp(in);
		}

		public Damp[] newArray(int size) {
			return new Damp[size];
		}
	};
	
	public Damp(Parcel in){
		high = in.readInt();
		low = in.readInt();
		damp = in.readInt();
	}
}
