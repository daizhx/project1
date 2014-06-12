package com.hengxuan.ehealthplatform.weight;

import android.os.Parcel;
import android.os.Parcelable;

public class Head implements Parcelable {
	
	public final static int TYPE_STABLE = 0xCE;
	public final static int TYPE_DYNAMIC = 0xCA;

	private int type;

	public Head() {

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void analysis(int[] i) {
		type = i[7];
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(type);
	}

	public static final Parcelable.Creator<Head> CREATOR = new Parcelable.Creator<Head>() {
		public Head createFromParcel(Parcel in) {
			return new Head(in);
		}

		public Head[] newArray(int size) {
			return new Head[size];
		}
	};

	private Head(Parcel in) {
		type = in.readInt();
	}
}
