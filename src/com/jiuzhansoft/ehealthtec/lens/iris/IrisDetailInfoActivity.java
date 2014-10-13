package com.jiuzhansoft.ehealthtec.lens.iris;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class IrisDetailInfoActivity extends BaseActivity {

	private TextView titleText;
	private TextView iris_result_buwei;
	private TextView iris_result_color;
	private TextView iris_result_bingzheng;
	private TextView iris_result_bingfazheng;
	private TextView iris_result_suggesstion;
	private Button addtoReportBtn;
	private Organ organ;
	private int colorId;
	private JSONArrayPoxy jsonPoxy;
	private static final int INET_AVAILABLE = 1;
	private ProgressDialog mProgressDialog;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == INET_AVAILABLE){
				mProgressDialog.dismiss();
				getServerData2InitView();
			}
		};
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iris_detail);
		setTitle(R.string.iris_result);
		
		iris_result_buwei=(TextView)findViewById(R.id.iris_result_organ_name);
		iris_result_color=(TextView)findViewById(R.id.iris_result_color);
		iris_result_bingzheng=(TextView)findViewById(R.id.iris_result_symptomDesc);
		iris_result_bingfazheng=(TextView)findViewById(R.id.iris_result_announcements);
		iris_result_suggesstion=(TextView)findViewById(R.id.iris_result_maintenanceSuggestion);
		addtoReportBtn = (Button) findViewById(R.id.nextbtn);
		
		Bundle b = super.getIntent().getExtras();
		organ = (Organ)b.getSerializable("organ_info");
		colorId=b.getInt("colorId");
		initView();	
	}

	private void getServerData2InitView() {
		// TODO Auto-generated method stub
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("bid",organ.getOrganId());
			jsonobject.put("cid",colorId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.IRISINFOBYPARTANDCOLOR);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			
			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				if(response.getJSONObject()!=null){
					try {									
						jsonPoxy=response.getJSONObject().getJSONArray("irisInfo");
						post(new Runnable(){
							
							@Override
							public void run() {
								// TODO Auto-generated method stub								
								try {
									JSONObject json=jsonPoxy.getJSONObject(0);
									iris_result_bingzheng.setText(json.getString("symptomDesc"));
									iris_result_bingfazheng.setText(json.getString("announcements"));
									iris_result_suggesstion.setText(json.getString("maintenancesSuggestion"));	
									addtoReportBtn.setEnabled(true);
									addtoReportBtn.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											addtoReportBtn.setEnabled(false);
											// TODO Auto-generated method stub
											int getCurIndex = getIntFromPreference("currentIndex");
											SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
											Date currentdate = new Date(System.currentTimeMillis());
											String timestr = format.format(currentdate);
											if(getCurIndex != -1){
												String getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
												addToServer(timestr, getUserPin, getCurIndex, organ.getOrganId(), colorId);
											}												
										}
									});
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									
									final AlertDialog dialog = (new AlertDialog.Builder(IrisDetailInfoActivity.this)).create();
									dialog.show();
									dialog.setMessage(getText(R.string.notalldata));
									dialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.ok), new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											dialog.dismiss();
										}
									});
									
									e.printStackTrace();
								}															
							}
							
						});
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stubd
				
			}
		});
		httpsetting.setNotifyUser(true);
		getHttpGroupaAsynPool().add(httpsetting);				
	}

	private void initView() {
		// TODO Auto-generated method stub
		iris_result_buwei.setText(organ.getName());
		if(colorId==1){
			iris_result_color.setText(getResources().getString(R.string.iris_color_light));
		}else if(colorId==2){
			iris_result_color.setText(getResources().getString(R.string.iris_color_dark_brown));
		}else if(colorId==3){
			iris_result_color.setText(getResources().getString(R.string.iris_color_dark_brown));
		}else if(colorId==4){
			iris_result_color.setText(getResources().getString(R.string.iris_color_deeply_black));
		}
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		String getssid = info.getSSID();
		getssid = getssid.replaceAll("\"", "");
		if(!TextUtils.isEmpty(getssid)
				&&getssid.substring(0, 3).equals("EHT")){
			//¶Ï¿ª¾µÍ·
//			((EHTApplication)getApplicationContext()).restoreWifiInfo();
			//start
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getText(R.string.disconnect_lens));
			mProgressDialog.show();
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for(int i=0; i<3; i++){
					ConnectivityManager cm = (ConnectivityManager)IrisDetailInfoActivity.this.getSystemService(CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = cm.getActiveNetworkInfo();
					if(networkInfo.isAvailable()){
						Message msg = Message.obtain();
						msg.what = INET_AVAILABLE;
						handler.sendMessage(msg);
					}else{
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					}
				}
			}.run();
			
		}else{
			getServerData2InitView();
		}
			
	}
	
	private void addToServer(String currentDate, String userPin, 
			int eyeIndex, int bodyId, int colorId){
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("client_code", "GZ-Hengxuan");
			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userPin", userPin);
			jsonobject.put("eye", eyeIndex);
			jsonobject.put("bodyId", bodyId);
			jsonobject.put("colorId", colorId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.ADDTOSERVER);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				final int getcode = response.getCode();
				post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(getcode == 200)
							Toast.makeText(IrisDetailInfoActivity.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(IrisDetailInfoActivity.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}});
		httpsetting.setNotifyUser(true);
		getHttpGroupaAsynPool().add(httpsetting);
	}
}
