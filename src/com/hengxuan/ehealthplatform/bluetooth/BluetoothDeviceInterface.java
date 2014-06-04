package com.hengxuan.ehealthplatform.bluetooth;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.massager.MassagerActivity;

public class BluetoothDeviceInterface extends BaseActivity {


	final int MSG_CONNECT_OK = 0;
	final int MSG_CONNECT_ERR = 1;
	
	private TextView title;
	private static final String TAG = "";
	private BluetoothAdapter mAdapter = null;
	private Map<String, String> blueToothInfo = null;
	private Map<String, Boolean> BlueToothDeviceConnectState;// 当前设备连接状态
	private Map<String, Boolean> BlueToothDeviceBoundState;// 当前设备配对状态
	private List<Map<String, String>> NewDevices;
	public BroadcastReceiver mReceiver;
	int CurrentFindDevicesCount;
	private ListView bluetoothdeviceInfoList;
	private SimpleAdapter myArrayAdapter;
	private final int REQUEST_ENABLE_BT = 2;
	
	private int getPosition;
	private String ishome = "";
	private boolean flag = false;
	// 如果进入蓝牙连接界面，但是没有点击button时，isClick = false
	// (用于返回键)
	private boolean isClick = false;

	//private DefaultEffectHttpListener defaulteffecthttplistener;
	
	Handler handler;
	// public static String constmac;
	
	private Boolean checkexist(List<Map<String, String>> NewDevices,
			Map<String, String> blueToothInfo) {
		for (int i = 0; i < NewDevices.size(); i++) {
			if (NewDevices.get(i).get("mac").equals(blueToothInfo.get("mac"))) {
				return true;
			}
		}

		return false;
	}

	private void openBluetoothDevice() {
		if (mAdapter == null)
			mAdapter = BluetoothAdapter.getDefaultAdapter();// 获取可用的蓝牙适配器
		if (mAdapter == null)
			return;
		if (!mAdapter.isEnabled()) { // 判断蓝牙是否被打开
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_ENABLE_BT);
		} else {
			initBluetoothDevice();
		}
	}

	/*get the bonded devices and recording into data struct,
	 * the start bt discovery
	 * */
	private void initBluetoothDevice() {
		/*
		 * if(mAdapter == null) mAdapter=
		 * BluetoothAdapter.getDefaultAdapter();//获取可用的蓝牙适配器 if(mAdapter ==
		 * null) return; if(!mAdapter.isEnabled()){//判断蓝牙是否被打开 //不做提示，强行打开
		 * //Intent cwjIntent = new
		 * Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		 * //cwjIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
		 * 300); Intent intent = new
		 * Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		 * startActivityForResult(intent,REQUEST_ENABLE_BT); //
		 * mAdapter.enable(); }
		 */

		if (mAdapter.isDiscovering())
			mAdapter.cancelDiscovery();
		while (true) {
			if (mAdapter.isEnabled())
				break;
		}

		Object[] pairedDevices = mAdapter.getBondedDevices().toArray(); // 已经配对的蓝牙对象
		BluetoothDevice tempdevice;
		for (int i = 0; i < pairedDevices.length; i++) {
			tempdevice = (BluetoothDevice) pairedDevices[i];
			if (tempdevice.getAddress() != null
					&& !tempdevice.getAddress().equals("")) {
				blueToothInfo = new HashMap<String, String>();
				blueToothInfo.put("name", tempdevice.getName());
				blueToothInfo.put("mac", tempdevice.getAddress());
				if (!checkexist(NewDevices, blueToothInfo)) { // 防止重复添加

					NewDevices.add(blueToothInfo);
					BlueToothDeviceBoundState
							.put(tempdevice.getAddress(), true);// 当前为已配对列表
					if (blueToothInfo.get("mac").equals(BluetoothServiceProxy.mac))
						BlueToothDeviceConnectState.put(
								tempdevice.getAddress(), true);
					else
						BlueToothDeviceConnectState.put(
								tempdevice.getAddress(), false);
				}
			}
		}
		// 执行更新列表的代码
		myArrayAdapter.notifyDataSetChanged();
		mAdapter.startDiscovery();// 查找远程设备
		title.setText(R.string.searching_device);

	}

	
	private void setbuletoothdevicelist() {
		//this.runOnUiThread(new Runnable() {

			//@Override
			//public void run() {
				// TODO Auto-generated method stub
				String as[] = { "name", "mac" };
				int ai[] = { R.id.bluetooth_item_name2,
						R.id.bluetooth_item_mac2 };


				myArrayAdapter = new SimpleAdapter(BluetoothDeviceInterface.this,
						NewDevices, R.layout.bluetooth_device_listitem, as, ai) {

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						// TODO Auto-generated method stub
						View view = super
								.getView(position, convertView, parent);
						String name = NewDevices.get(position).get("name");
						String mac = NewDevices.get(position).get("mac");
						TextView textview = (TextView) view
								.findViewById(R.id.bluetooth_item_name2);
						TextView textview1 = (TextView) view
								.findViewById(R.id.bluetooth_item_mac2);
						if (name != null)
							textview.setText(name);
						if (mac != null)
							textview1.setText(mac);
						Button button = (Button) view
								.findViewById(R.id.connection_item);
						final String constmac = mac;
						// constmac = mac;
						if (BlueToothDeviceBoundState.get(mac)) { // 已配对的设备可以直接连接
							if (BlueToothDeviceConnectState.get(mac)) {
								button.setText(R.string.disconnect);
								button.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										// 断开连接
										//Toast.makeText(BluetoothDeviceInterface.this, getResources().getString(R.string.tryto_connect), Toast.LENGTH_SHORT).show();
										if (BluetoothServiceProxy.btSocket != null) {
											if (BluetoothServiceProxy.outStream != null) {
												try {
													BluetoothServiceProxy.outStream
															.flush();
												} catch (IOException e) {
													Log.e(TAG,
															"ON PAUSE: Couldn't flush output stream.",
															e);
												}

											}

											try {
												BluetoothServiceProxy.btSocket.close();
												BluetoothServiceProxy.btSocket = null;
												BlueToothDeviceConnectState
														.put(BluetoothServiceProxy.mac,
																false);
												BluetoothServiceProxy.name = null;
												BluetoothServiceProxy.mac = null;
												notifyDataSetChanged();
												Toast.makeText(
														BluetoothDeviceInterface.this,
														getString(R.string.disconnected),
														0).show();
											} catch (IOException e2) {
												Log.e(TAG,
														"ON PAUSE: Unable to close socket.",
														e2);
											}
										} else {
											BlueToothDeviceConnectState
											.put(BluetoothServiceProxy.mac,
													false);
											notifyDataSetChanged();
										}
									}

								});
							} else {
								button.setText(R.string.connect);
								button.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// 连接
										// Toast.makeText(BluetoothDeviceInterface.this, getResources().getString(R.string.tryto_connect), 0).show();
										isClick = true;
										//defaulteffecthttplistener.onStart();
										mAdapter.cancelDiscovery();
										final BluetoothDevice currentdevice = mAdapter
										.getRemoteDevice(constmac);
										try {
											if (BluetoothServiceProxy.btSocket != null) {
												if (BluetoothServiceProxy.outStream != null) {
													try {
														BluetoothServiceProxy.outStream
														.flush();
													} catch (IOException e) {
														Log.e(TAG,
																"ON PAUSE: Couldn't flush output stream.",
																e);
													}
												}
												try {
													BluetoothServiceProxy.btSocket
													.close();
													BluetoothServiceProxy.btSocket = null;
													BlueToothDeviceConnectState
													.put(BluetoothServiceProxy.mac,
															false);
													BluetoothServiceProxy.mac = null;
													BluetoothServiceProxy.name = null;
												} catch (IOException e2) {
													Log.e(TAG,
															"ON PAUSE: Unable to close socket.",
															e2);
												}
											}
											
											Method m = currentdevice
											.getClass()
											.getMethod(
													"createRfcommSocket",
													new Class[] { int.class });
											BluetoothServiceProxy.btSocket = (BluetoothSocket) m
											.invoke(currentdevice, 1);
											
										} catch (SecurityException e) {
											e.printStackTrace();
											Toast.makeText(
													BluetoothDeviceInterface.this,
													getString(R.string.not_open_device),
													0).show();
										} catch (NoSuchMethodException e) {										
											e.printStackTrace();
											Toast.makeText(
													BluetoothDeviceInterface.this,
													getString(R.string.not_open_device),
													0).show();
										} catch (IllegalArgumentException e) {											
											e.printStackTrace();
											Toast.makeText(
													BluetoothDeviceInterface.this,
													getString(R.string.not_open_device),
													0).show();
										} catch (IllegalAccessException e) {											
											e.printStackTrace();
											Toast.makeText(
													BluetoothDeviceInterface.this,
													getString(R.string.not_open_device),
													0).show();
										} catch (InvocationTargetException e) {											
											e.printStackTrace();
											Toast.makeText(
													BluetoothDeviceInterface.this,
													getString(R.string.not_open_device),
													0).show();
										}
										
										new Thread(new Runnable() {
											@Override
											public void run() {
												try {
													BluetoothServiceProxy.btSocket.connect();
													
													BlueToothDeviceConnectState.put(
															currentdevice.getAddress(),
															true);
													BluetoothServiceProxy.name = currentdevice
													.getName();
													BluetoothServiceProxy.mac = currentdevice
													.getAddress();
													// notifyDataSetChanged();
													// if(BluetoothServiceProxy.isconnect())
														handler.sendEmptyMessage(MSG_CONNECT_OK);
													
													Log.e(TAG,
															"ON RESUME: BT connection established, data transfer link open.");
													//getData();
													// Intent intent = new Intent(BluetoothDeviceInterface.this, HomeActivity2.class);
													/*Intent intent = null;
													if(ishome.equals("ishome"))
														intent = new Intent(BluetoothDeviceInterface.this, HomeActivity2.class);
													else if(ishome.equals("iselectro"))
														intent = new Intent(BluetoothDeviceInterface.this, Electrocautery2Activity.class);
													else if(ishome.equals("isgame"))
														intent = new Intent(BluetoothDeviceInterface.this, GameList2.class);
													Bundle bundle = new Bundle();
													bundle.putInt("getPosition", getPosition);
													intent.putExtras(bundle);
													// startActivity(intent);
													setResult(Activity.RESULT_OK, intent);*/
													BluetoothDeviceInterface.this.finish();
													
													int version = Integer.valueOf(android.os.Build.VERSION.SDK);
													if(version >= 5)
														overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);
													
												} catch (IOException e) {
													
													try {
														BluetoothServiceProxy.btSocket.close();
														BluetoothServiceProxy.btSocket = null;
														BlueToothDeviceConnectState
														.put(currentdevice
																.getAddress(),
																false);
														handler.sendEmptyMessage(MSG_CONNECT_ERR);
														
														flag = true;
														/*getData();
														Intent intent = new Intent(BluetoothDeviceInterface.this, HomeActivity.class);
														Bundle bundle = new Bundle();
														bundle.putInt("getPosition", getPosition);
														intent.putExtras(bundle);
														// startActivity(intent);
														setResult(Activity.RESULT_OK, intent);
														BluetoothDeviceInterface.this.finish();
														
														int version = Integer.valueOf(android.os.Build.VERSION.SDK);
														if(version >= 5)
															overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);*/
													} catch (IOException e2) {
														Log.e(TAG,
																"ON RESUME: Unable to close socket during connection failure",
																e2);
													}
													
												}
												
												//defaulteffecthttplistener.onEnd(null);
											}
										}).start();
									}
								});
							}
						} else { // 未配对的设备
							button.setText(R.string.pair);
							button.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// Toast.makeText(BluetoothDeviceInterface.this, getResources().getString(R.string.tryto_connect), 0).show();									
									mAdapter.cancelDiscovery();
									BluetoothDevice currentdevice = mAdapter
											.getRemoteDevice(constmac);
									try {
										if (currentdevice.getBondState() == BluetoothDevice.BOND_NONE) {
											// 利用反射方法调用BluetoothDevice.createBond(BluetoothDevice
											// remoteDevice);
											Method createBondMethod = BluetoothDevice.class
													.getMethod("createBond");
											Log.d("BlueTooth",
													"开始配对");
											Toast.makeText(
													BluetoothDeviceInterface.this,
													getString(R.string.startpair),
													0).show();
											createBondMethod.invoke(currentdevice);
										}
									} catch (Exception e) {
										e.printStackTrace();
										Toast.makeText(
												BluetoothDeviceInterface.this,
												getString(R.string.not_support_device),
												0).show();
									}
								}

							});
						}
						return view;
					}

				};

				bluetoothdeviceInfoList.setAdapter(myArrayAdapter);
			//}

		//});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_device_list);
		bluetoothdeviceInfoList = (ListView) findViewById(R.id.bluetoothdevice_list);
		NewDevices = new ArrayList<Map<String, String>>();
		BlueToothDeviceConnectState = new HashMap<String, Boolean>();
		BlueToothDeviceBoundState = new HashMap<String, Boolean>();
		
		setbuletoothdevicelist();
		openBluetoothDevice();
		//initBluetoothDevice();

		registerBTBroadcastReceiver();
		// 指明一个来自于远程设备的低级别（ACL）连接的断开
		// filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		// registerReceiver(mReceiver, filter);
		
		//defaulteffecthttplistener = new DefaultEffectHttpListener(this);
		
		handler = new Handler() {
			public void handleMessage(Message message)
			{
				switch(message.what)
				{
				case MSG_CONNECT_OK:
					/*Toast.makeText(
							BluetoothDeviceInterface.this,
							getString(R.string.connected),
							0).show();*/
					break;
				case MSG_CONNECT_ERR:
					Toast.makeText(
							BluetoothDeviceInterface.this,
							getString(R.string.not_open_device),
							0).show();
					break;
				}
				
			}
		};
	}

	private void registerBTBroadcastReceiver() {
		// TODO Auto-generated method stub
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d("bluetooth","action="+action);
				// 找到设备
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {// 搜索未配对的列表
						blueToothInfo = new HashMap<String, String>();
						blueToothInfo.put("name", device.getName());
						blueToothInfo.put("mac", device.getAddress());
						if (!checkexist(NewDevices, blueToothInfo))// 防止重复添加
						{
							NewDevices.add(blueToothInfo);
							BlueToothDeviceBoundState.put(device.getAddress(),
									false);// 当前为未配对列表
							if (blueToothInfo.get("mac").equals(BluetoothServiceProxy.mac)){
								BlueToothDeviceConnectState.put(
										device.getAddress(), true);
							}else{
								BlueToothDeviceConnectState.put(
										device.getAddress(), false);
							}
						}
						Log.v(TAG,
								"find device:" + device.getName()
										+ device.getAddress());
					}
				} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED
						.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING:
						Log.d("BlueTooth", "正在配对......");
						Toast.makeText(
								BluetoothDeviceInterface.this,
								getString(R.string.pairing),
								0).show();
						break;
					case BluetoothDevice.BOND_BONDED:
						Log.d("BlueTooth", "完成配对");
						Toast.makeText(
								BluetoothDeviceInterface.this,
								getString(R.string.paired),
								0).show();
						BlueToothDeviceBoundState
								.put(device.getAddress(), true);
						break;
					case BluetoothDevice.BOND_NONE:
						Log.d("BlueTooth", "取消配对");
						Toast.makeText(
								BluetoothDeviceInterface.this,
								getString(R.string.cancelpair),
								0).show();
					default:
						break;
					}
				} else if (action
						.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
					BluetoothDevice btDevice = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String strPsw = "1234";
					try {
						setPin(btDevice.getClass(), btDevice, strPsw);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) { // 搜索完成
					title.setText(R.string.search_is_completed);

				}
				// 执行更新列表的代码
				myArrayAdapter.notifyDataSetChanged();
			}
		};
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);// 查找到设备
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);// 配对设备状态
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(
				"android.bluetooth.device.action.PAIRING_REQUEST");//
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);// 查找设备完成
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		/*
		 * if(BluetoothServiceProxy.btSocket != null) { if (BluetoothServiceProxy.outStream !=
		 * null) { try { BluetoothServiceProxy.outStream.flush();
		 * BluetoothServiceProxy.outStream = null; } catch (IOException e) { Log.e(TAG,
		 * "ON PAUSE: Couldn't flush output stream.", e); }
		 * 
		 * }
		 * 
		 * 
		 * try { BluetoothServiceProxy.btSocket.close();
		 * BlueToothDeviceState.put(BluetoothServiceProxy.mac, false);
		 * BluetoothServiceProxy.btSocket = null; } catch (IOException e2) { Log.e(TAG,
		 * "ON PAUSE: Unable to close socket.", e2); } }
		 */
		super.onDestroy();

	}

	public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str)
			throws Exception {

		try {

			Method removeBondMethod = btClass.getDeclaredMethod("setPin",

			new Class[] { byte[].class });

			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,

			new Object[] { str.getBytes() });

			Log.e("returnValue", "" + returnValue);

		} catch (SecurityException e) {

			e.printStackTrace();

		} catch (IllegalArgumentException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return true;

	}

	@Override
	protected void onActivityResult(int i, int j, Intent intent) {
		if (i == REQUEST_ENABLE_BT) {
			if (j == RESULT_OK) {
				BluetoothServiceProxy.open_flag = true;
				initBluetoothDevice();
			} else {
				finish();
			}
		}
	}
	
	private void getData(){
		Intent getIntent = getIntent();
		getPosition = getIntent.getIntExtra("position", -1);
		ishome = getIntent.getStringExtra("ishome");
	}

	private void toIntent(){
		getData();
		Intent intent = null;
		if(ishome.equals("ishome")){
			intent = new Intent(BluetoothDeviceInterface.this, MassagerActivity.class);
		}
		else if(ishome.equals("iselectro")){
			//intent = new Intent(BluetoothDeviceInterface.this, Electrocautery2Activity.class);
		}
		else if(ishome.equals("isgame")){
			//intent = new Intent(BluetoothDeviceInterface.this, GameList2.class);
		}
		Bundle bundle = new Bundle();
		bundle.putInt("getPosition", getPosition);
		bundle.putBoolean("isFlag", flag);
		intent.putExtras(bundle);
		// startActivity(intent);
		setResult(Activity.RESULT_OK, intent);
		BluetoothDeviceInterface.this.finish();
		
		int version = Integer.valueOf(android.os.Build.VERSION.SDK);
		if(version >= 5)
			overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// AlwaysMarqueeTextView mAlwaysTextView, ImageButton mImageButton, int position, final short commandId, int currentAnim
		/*if(keyCode == KeyEvent.KEYCODE_BACK){
			if(flag){
				//toIntent();
				return true;				
			}else if(!isClick){
				//toIntent();
				return true;
			}
		}*/
		return super.onKeyDown(keyCode, event);
	}


}
