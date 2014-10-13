package com.jiuzhansoft.ehealthtec.sphygmomanometer.data;
import android.os.Parcel;
import android.os.Parcelable;

public class Msg extends IBean {

	public static final int MESSAGE_STATE_CONNECTING = 0;
	public static final int MESSAGE_STATE_NONE = 1;
	public static final int MESSAGE_STATE_CONNECTED = 2;

	public static final int START = 0x06;

	private int msg_code;
	private String device_name;

	public Msg() {
		super();
	}

	public Msg(int msgCode, String deviceName) {
		super();
		msg_code = msgCode;
		device_name = deviceName;
	}

	public int getMsg_code() {
		return msg_code;
	}

	public void setMsg_code(int msgCode) {
		msg_code = msgCode;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String deviceName) {
		device_name = deviceName;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(msg_code);
		dest.writeString(device_name);
	}

	public static final Parcelable.Creator<Msg> CREATOR = new Parcelable.Creator<Msg>() {
		public Msg createFromParcel(Parcel in) {
			return new Msg(in);
		}

		public Msg[] newArray(int size) {
			return new Msg[size];
		}
	};

	private Msg(Parcel in) {
		msg_code = in.readInt();
		device_name = in.readString();
	}

	public void analysis(int[] i) {
		msg_code = i[2];
	}
}
