package com.jiuzhansoft.ehealthtec.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.log.Log;

public class BluetoothServiceProxy {

	public static String name;
	public static String mac;
	public static int i = 0;
	public static BluetoothSocket btSocket;
	public static OutputStream outStream ;
	public static boolean open_flag = false;
	public static boolean music_flag = false;
	/*
	 * 编码规则：(两个字节，第一个字节代表标志，第二个字节指令内容)
	 * 按摩机设置(00):0x0010~0x0100(size = 240)
	 * **模式设置：0x0011~0x0030(size = 32, (火罐:0x0011,推拿:0x0012,锤击:0x0013,针灸:0x0014,按摩:0x0015,刮痧:0x0016,自动:0x0017音乐:x00018))
	 * **强度设置：0x0031~0x0050(size = 32,(0:0x0031,~10:0x003b,~14:0x003f,15~0x0040,16:0x0041))
	 * **时间设置：0x0051~0x0070(size = 32,(0:0x0051,5:0x0052,10:0x0053,15:0x0054,20:0x0055,25:0x0056,30:0x0057,35:0x0058,40:0x0059,45:0x005a,50:0x005b,55:0x005c,60:0x005d))
	 * **频率设定：0x0071~0x0080(size= 16,(0x0071:低频,0x0072:高频))
	 * **
	 * 数据传输：0x1100(11代表数据传输,00为要传输的数据内容)
	*/
	public static final short MODE_TAG_1 = 0x0011;
	public static final short MODE_TAG_2 = 0x0012;
	public static final short MODE_TAG_3 = 0x0013;
	public static final short MODE_TAG_4 = 0x0014;
	public static final short MODE_TAG_5 = 0x0015;
	public static final short MODE_TAG_6 = 0x0016;
	public static final short MODE_TAG_7 = 0x0017;
	public static final short MODE_TAG_8 = 0x0018;
	
	public static final short STRENGTH_TAG = 0x0031;
	
	public static final short TIME_TAG = 0x0051;
	
	public static final short H_FR_TAG = 0x0071;
	public static final short L_FR_TAG = 0x0072;
	BluetoothServiceProxy()
	{
		name = null;
		mac = null;
		btSocket = null;
		outStream = null;
	}
	
	static private boolean isChangeMassageMode(short command){
		if((command == MODE_TAG_1) ||
				(command == MODE_TAG_2) ||
				(command == MODE_TAG_3) ||
				(command == MODE_TAG_4) ||
				(command == MODE_TAG_5) ||
				(command == MODE_TAG_6) ||
				(command == MODE_TAG_7) ){
			return true;
		}
		return false;
	}
	
	private static byte[] shortToByteArray(short s) {
		   byte[] shortBuf = new byte[2];
		   for(int i=0;i<2;i++) {
		   int offset = (shortBuf.length - 1 -i)*8;
		   shortBuf[i] = (byte)((s>>>offset)&0xff);
		   }
		   return shortBuf;
	}
	
	static private void connectIOException(Context context){
		Toast.makeText(context, 
				context.getResources().getString(R.string.disconnectbluetooth), 
				Toast.LENGTH_SHORT).show();
		
		/*
		if (MusicService.isServiceRunning(context,"com.jiuzhansoft.ehealthtec.service.MusicService")) {
			Intent musicintent = new Intent();
			musicintent.setClass(context, MusicService.class);
			System.out.println("******服务停止了2***");
			context.stopService(musicintent);
		}*/
		/*
		if (BlueToothInfo.outStream != null) {
            try {
            	BlueToothInfo.outStream.flush();
            	BlueToothInfo.outStream = null;
            } catch (IOException e) {
                    e.printStackTrace();
            }
		}
		*/
		disconnectBluetooth();
		
//		Intent intent = new Intent(context, BluetoothDeviceInterface.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
	}
	
	static public Boolean sendCommandToDevice(short command) throws Exception//发送指令
	{	
		if (Log.E) {
			StringBuilder strBuilder = new StringBuilder("command = ").append(command); 
			Log.e("BlueTooth", "sendCommandToDevice()-" + strBuilder.toString());
		}
		
//		try {
			if(btSocket != null)
			{
				if(command >= 0x1100)
				{
					if(music_flag)
					{
//						try{
							outStream = btSocket.getOutputStream();
							//System.out.println("command="+command);
							byte[] msgBuffer = shortToByteArray(command);
							//System.out.println("msgBuffer="+msgBuffer);
							outStream.write(msgBuffer);
							if(Log.E) {
								StringBuilder strBuilder = new StringBuilder("command = ").append(command); 
								Log.e("sendCommand : ", strBuilder.toString());
							}
//						}catch(Exception e){
//							if(Log.E) {
//								Log.e("sendCommand : ", "exception");
//							}
//							connectIOException(context);
//						}
					}
				} else {
					if(Log.E) {
						StringBuilder strBuilder = new StringBuilder("command = ").append(command); 
						Log.e("sendCommand : ", "not music," + strBuilder.toString());
					}
//					try{
						if(command == MODE_TAG_8) {
							music_flag = true;
						}
						if(isChangeMassageMode(command)) {
							music_flag = false;
						}
						
						outStream = btSocket.getOutputStream();
						System.out.println("command="+command);
						byte[] msgBuffer = shortToByteArray(command);
						System.out.println("msgBuffer="+msgBuffer);
						outStream.write(msgBuffer);
//					}catch(Exception e){
//						if(Log.E) {
//							Log.e("sendCommand : ", "not music, exception");
//						}
//						connectIOException(context);
//					}
				}
				return true;
			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return false;
	}
	
	static public void disconnectBluetooth()//断开连接
	{
		if(btSocket != null)
		{
			if (outStream != null) {
                try {
                	outStream.flush();
                	outStream = null;
                } catch (IOException e) {
                        e.printStackTrace();
                }
			}

			try {
				btSocket.close();
			} catch (IOException e2) {
                e2.printStackTrace();
			}
			
			btSocket = null;
            name = null;
            mac = null;
		}
	}
	static public void closeBluetooth()//关闭蓝牙
	{
		BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
		disconnectBluetooth();
		if(mAdapter != null)
		{
			/*
			if(mAdapter.isDiscovering())
			{
				mAdapter.cancelDiscovery();
			}
			if(mAdapter.isEnabled() && BlueToothInfo.open_flag)
			{
				mAdapter.disable();
			}
			*/
			mAdapter.cancelDiscovery();
			mAdapter.disable();
		}
	}
	static public Boolean isconnect()
	{
		if(btSocket != null){
			return true;
		}
		else
			return false;
	}


}
