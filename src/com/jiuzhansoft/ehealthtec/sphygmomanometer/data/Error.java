package com.jiuzhansoft.ehealthtec.sphygmomanometer.data;

import android.os.Parcel;
import android.os.Parcelable;


public class Error extends IBean {

	/**
	 * ����ʧ��
	 */
	public static final int ERROR_CONNECTION_FAILED = 0;
	/**
	 * ���Ӷ�ʧ
	 */
	public static final int ERROR_CONNECTION_LOST = 1;

	// Ѫѹ�Ǵ�����Ϣ����
	/**
	 * E-E EEPROM�쳣
	 */
	public static final int ERROR_EEPROM = 0x0E;
	/**
	 * E-1 ���������ź�̫С��ѹ��ͻ��
	 */
	public static final int ERROR_HEART = 0x01;
	/**
	 * E-2 ��Ѷ����
	 */
	public static final int ERROR_DISTURB = 0x02;
	/**
	 * E-3 ����ʱ�����
	 */
	public static final int ERROR_GASING = 0x03;
	/**
	 * E-4 ��õĽ���쳣
	 */
	public static final int ERROR_TEST = 0x05;
	/**
	 * E-C У���쳣
	 */
	public static final int ERROR_REVISE = 0x0C;
	/**
	 * E-B ��Դ�͵�ѹ
	 */
	public static final int ERROR_POWER = 0x0B;

	/**
	 * ������룬�ô�������Ϊ����ʱ�Ĵ���(int����)�����Ӻ�Ѫѹ�Ƿ��͵Ĵ���(float����)
	 */
	private int error_code;

	private int error;

	public Error() {
		super();
	}

	public Error(int errorCode) {
		super();
		error_code = errorCode;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int errorCode) {
		error_code = errorCode;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(error_code);
		dest.writeInt(error);
	}

	public static final Parcelable.Creator<Error> CREATOR = new Parcelable.Creator<Error>() {
		public Error createFromParcel(Parcel in) {
			return new Error(in);
		}

		public Error[] newArray(int size) {
			return new Error[size];
		}
	};

	private Error(Parcel in) {
		error_code = in.readInt();
		error = in.readInt();
	}

	public void analysis(int[] f) {
		error = f[3];
	}
}
