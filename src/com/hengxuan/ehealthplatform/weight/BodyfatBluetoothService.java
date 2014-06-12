package com.hengxuan.ehealthplatform.weight;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.hengxuan.ehealthplatform.utils.CodeFormat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BodyfatBluetoothService {
	
	private int state = 0;
	public static final int STATE_UNCONNECTED = 0;
	public static final int STATE_CONNECTED = 1;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	/*
	 * private static final UUID MY_UUID = UUID
	 * .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
	 */
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private InputStream mmInStream;
	private OutputStream mmOutStream;

	private Handler mHandler;
	private ConnectedThread mConnectedThread;
	
	private boolean running = true;

	public BodyfatBluetoothService() {
		
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}
	
	public void setRunning(boolean run){
		running = run;
	}

	public int getmState() {
		return state;
	}
	
	/**
	 * 发送消息到前台
	 * 
	 * @param code
	 * @param bean
	 */
	private void send(IBean weight, IBean damp) {
		if (mHandler != null) {
			//Message msg = mHandler.obtainMessage(code);
			Message msg = mHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putParcelable("weight", weight);
			bundle.putParcelable("damp", damp);
			msg.setData(bundle);
			msg.sendToTarget();
		}
	}

	/**
	 * 连接方法
	 */
	public void connect() {
		if (device != null) {
			try {
				socket = device.createRfcommSocketToServiceRecord(MY_UUID);
				socket.connect();
				connected();
			} catch (IOException e) {
				e.printStackTrace();
				connectionFailed();
			}
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param f
	 */
	public void write(byte[] f) {
		if (mmOutStream != null)
			try {
				mmOutStream.write(f);
			} catch (IOException e) {
				e.printStackTrace();
			}		
	}

	/**
	 * 连接成功后启动接收数据的线程
	 */
	private void connected() {
		if (socket != null) {
			mConnectedThread = new ConnectedThread();
			mConnectedThread.start();
			state = STATE_CONNECTED;
		}

	}

	/**
	 * 停止
	 */
	public void stop() {
		try {
			if (socket != null) {
				socket.close();
			}

			if (mConnectedThread != null) {
				mConnectedThread.interrupt();
				running = false;
			}
			state = STATE_UNCONNECTED;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void connectionFailed() {
		//send(IBean.ERROR, new Error(Error.ERROR_CONNECTION_FAILED));
	}

	private void connectionLost() {
		//send(IBean.ERROR, new Error(Error.ERROR_CONNECTION_LOST));
	}

	private class ConnectedThread extends Thread {
		public ConnectedThread() {
			try {
				mmInStream = socket.getInputStream();
				mmOutStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			byte[] buffer = new byte[32];
			while (running) {
				try {
					if (mmInStream.available() > 0) {
						Head head = new Head();
						mmInStream.read(buffer);
						int[] f = CodeFormat.bytesToHexStringTwo(buffer, 15);
						int result = 0;
						for(int i = 4; i < 14; i++){
							result = result ^ f[i];
						}
						if(result == f[14]){
							head.analysis(f);
							Weight weight = new Weight();
							weight.analysis(f);
							weight.setHead(head);
							
							Damp damp = new Damp();
							damp.analysis(f);
							damp.setHead(head);
							
							send(weight, damp);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					connectionLost();
					interrupt();
					break;
				}
			}
		}
	}
}