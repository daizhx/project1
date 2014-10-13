package com.jiuzhansoft.ehealthtec.weight;

import android.os.Parcel;
import android.os.Parcelable;

public class Weight extends IBean{

	private int high;
	private int low;
	private int weight;
	
	public Weight(){}
	
	public Weight(int high, int low){
		this.high = high;
		this.low = low;
		this.weight = Integer.parseInt(high + "", 16) * 256 + Integer.parseInt(low + "", 16);
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

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
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
		dest.writeInt(weight);
	}

	@Override
	public void analysis(int[] i) {
		// TODO Auto-generated method stub
		high = i[8];
		low = i[9];
		weight = high * 256 + low;
	}
	
	public static final Parcelable.Creator<Weight> CREATOR = new Parcelable.Creator<Weight>() {
		public Weight createFromParcel(Parcel in) {
			return new Weight(in);
		}

		public Weight[] newArray(int size) {
			return new Weight[size];
		}
	};
	
	public Weight(Parcel in){
		high = in.readInt();
		low = in.readInt();
		weight = in.readInt();
	}

}
