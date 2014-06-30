package com.hengxuan.ehealthplatform.lens;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.lens.hair.HairAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.iris.IrisAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.naevus.NaevusAnalysisActivity;
import com.hengxuan.ehealthplatform.lens.skin.SkinAnalysisActivity;
import com.hengxuan.ehealthplatform.log.Log;

public class LensBaseActivity extends BaseActivity implements
		LensShootFragment.HandleClick, ShootConfirmFragment.HandleClick {

	private static final String TAG = "Lens";
	private FragmentManager mFragmentManager;
	private LensShootFragment lensShootFragment;
	private ShootConfirmFragment shootConfirmFragment;
	private WifiManager mWifiManager;
	private WifiInfo currentWifiInfo;
	//拍摄照片的类别
	public int index;
	public static final int INDEX_IRIS = 1;
	public static final int INDEX_SKIN = 2;
	public static final int INDEX_HAIR = 3;
	public static final int INDEX_NAEVUS = 4;
	public static String photoPath;
	public static final String PHOTO_PATH = "photoPath";
	private int irisIndex;//左眼-1，右眼-2
	private int currentNetWorkId;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				if (connect2Lens()) {
					onConnectSuccess();
				} else {
					onConnectFail();
				}
			} else if (intent.getAction().equals(
					WifiManager.WIFI_STATE_CHANGED_ACTION)) {
				if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED) {
					// scan lens ap and connect
					mWifiManager.startScan();
				}
			}

		}
	};

	private boolean connect2Lens() {
		List<ScanResult> APList = mWifiManager.getScanResults();
		for (ScanResult scanResult : APList) {
			if (scanResult.SSID.equals("EHT")) {
				List<WifiConfiguration> list = mWifiManager
						.getConfiguredNetworks();
				for (WifiConfiguration wifiConfiguration : list) {
					Log.v(TAG, "ssid:" + wifiConfiguration.SSID);
					if (wifiConfiguration.SSID != null
							&& wifiConfiguration.SSID.equals("\"" + "EHT"
									+ "\"")) {
						currentWifiInfo = mWifiManager.getConnectionInfo();
						setCurrentNetWorkId(currentWifiInfo
										.getNetworkId());
						mWifiManager.disconnect();
						mWifiManager.enableNetwork(wifiConfiguration.networkId,
								true);
						mWifiManager.reconnect();
						return true;
					}
				}

				// add conf
				WifiConfiguration conf = new WifiConfiguration();
				conf.SSID = "\"" + "EHT" + "\"";
				conf.preSharedKey = "\"" + "12345678" + "\"";
				mWifiManager.addNetwork(conf);
				for (WifiConfiguration wifiConfiguration : list) {
					if (wifiConfiguration.SSID != null
							&& wifiConfiguration.SSID.equals("\"" + "EHT"
									+ "\"")) {
						currentWifiInfo = mWifiManager.getConnectionInfo();
						setCurrentNetWorkId(currentWifiInfo
										.getNetworkId());
						mWifiManager.disconnect();
						mWifiManager.enableNetwork(wifiConfiguration.networkId,
								true);
						mWifiManager.reconnect();
						return true;
					}
				}
				// if found EHT AP,mostly return true,should not return
				// false,there must be something wrong
				return false;
			}
		}
		return false;
	}

	/**
	 * 判读是否连接到镜头
	 * @return
	 */
	public boolean isConnectLens() {
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		ssid = ssid.replaceAll("\"", "");
		if (ssid.length() < 3) {
			// prevent throwing a IndexOutBoundException
			return false;
		}
		if (!TextUtils.isEmpty(ssid) && ssid.substring(0, 3).equals("EHT")) {
			return true;
		}
		return false;
	}

	public void onConnectSuccess() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectSuccess");
		unregisterReceiver(mBroadcastReceiver);

		// delay 2s
		// new Handler().postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// //startActivity(new Intent(LensConnectActivity.this,
		// IrisInspectionActivity.class));
		// startActivity(new Intent(LensConnectActivity.this,
		// SkinShootActivity.class));
		// finish();
		// }
		// }, 2000);
		Log.d(TAG, "index=" + index);
		switch (index) {
		case INDEX_IRIS:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x30, (byte) 0xD2 };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case INDEX_HAIR:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case INDEX_SKIN:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case INDEX_NAEVUS:
			try {
				DatagramSocket s = new DatagramSocket();
				InetAddress local = InetAddress.getByName("10.10.10.254");
				byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
						0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
						0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
				DatagramPacket p = new DatagramPacket(message, message.length,
						local, 8080);
				s.send(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		lensShootFragment = new LensShootFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		lensShootFragment.setArguments(bundle);
		lensShootFragment.setHanleClick(this);
		mFragmentManager.beginTransaction()
				.replace(R.id.root, (Fragment) lensShootFragment).commit();
	}

	public void onConnectFail() {
		Log.d(TAG, "onConnectFail");
		unregisterReceiver(mBroadcastReceiver);
		// TODO Auto-generated method stub
		AlertDialog alertDialog = new AlertDialog.Builder(LensBaseActivity.this).create();
		alertDialog.setMessage(getString(R.string.open_lens));
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						restoreWifiInfo();
						finish();
					}
				});
		alertDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lens_base);
		mFragmentManager = getSupportFragmentManager();
		LensConnectFragment lensConnectFragment = new LensConnectFragment();
		mFragmentManager.beginTransaction()
				.replace(R.id.root, (Fragment) lensConnectFragment).commit();
		// 获取检测的类别
		index = getIntent().getIntExtra("index", 0);
		Log.d(TAG, "index = " + index);

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		registerReceiver(mBroadcastReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		if (mWifiManager.isWifiEnabled()) {
			if (isConnectLens()) {
				onConnectSuccess();
				return;
			} else {
				mWifiManager.startScan();
			}
		} else {
			// open wifi
			mWifiManager.setWifiEnabled(true);
		}

		// 初始化后面的fragment
		shootConfirmFragment = new ShootConfirmFragment();
		shootConfirmFragment.setHandleClick(this);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 断开镜头wifi
		Log.d(TAG, "onDestroy:restore wifi connect");
		if(isConnectLens()){
			restoreWifiInfo();
		}
		super.onDestroy();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void confirm() {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (index) {
		case INDEX_IRIS:
			intent = new Intent(LensBaseActivity.this,
					IrisAnalysisActivity.class);
			intent.putExtra("irisIndex", irisIndex);
			break;
		case INDEX_SKIN:
			intent = new Intent(LensBaseActivity.this,
					SkinAnalysisActivity.class);
			break;
		case INDEX_HAIR:
			intent = new Intent(LensBaseActivity.this,
					HairAnalysisActivity.class);
			break;
		case INDEX_NAEVUS:
			intent = new Intent(LensBaseActivity.this,
					NaevusAnalysisActivity.class);
			break;
		default:
			break;
		}
		intent.putExtra(LensBaseActivity.PHOTO_PATH, photoPath);
		startActivity(intent);
		finish();
	}

	@Override
	public void shoot() {
		// TODO Auto-generated method stub
		Bundle args = new Bundle();
		args.putString("photoPath", photoPath);
		args.putInt("index", index);
		shootConfirmFragment.setArguments(args);
		mFragmentManager.beginTransaction()
				.replace(R.id.root, (Fragment) shootConfirmFragment).commit();
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	public void restoreWifiInfo() {
		Log.d("daizhx", "restoreWifiInfo");
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.disconnect();
		int networkId = getCurrentNetWorkId();
		if (networkId >= 0) {
			wifiManager.enableNetwork(networkId, true);
			wifiManager.reconnect();
		} else {
			// 3G
			wifiManager.setWifiEnabled(false);
		}

	}

	public void setCurrentNetWorkId(int i) {
		currentNetWorkId = i;
	}

	public int getCurrentNetWorkId() {
		return currentNetWorkId;
	}
	
	/**
	 * 标记iris是左眼还是右眼
	 * @param index 1-左眼 2-右眼
	 */
	public void setIrisIndex(int index){
		irisIndex = index;
	}
}
