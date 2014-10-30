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
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

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

/**
 * 虹膜分析结果
 * @author Administrator
 *
 */
public class IrisDetailInfoActivity extends BaseActivity {
	private static final String TAG = "iris";
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
//		JSONObject jsonobject=new JSONObject();
//		try {
//			jsonobject.put("local", CommonUtil.getLocalLauguage(this));
//			jsonobject.put("bid",organ.getOrganId());
//			jsonobject.put("cid",colorId);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.IRISINFOBYPARTANDCOLOR);
		httpsetting.setRequestMethod("GET");
		httpsetting.addArrayListParam(CommonUtil.getLocalLauguage(this)+ "");
		httpsetting.addArrayListParam(organ.getOrganId()+"");
		httpsetting.addArrayListParam(colorId+"");
//		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			
			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				Log.d(TAG, "httpError:"+httpError.getMessage());
			}
			
			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				try {
					int code = json.getInt("code");
					String msg = json.getString("msg");
					Log.d(TAG, "msg="+msg);
					JSONObject object = json.getJSONObjectOrNull("object");
					if(code == 1 && object != null){
						iris_result_bingzheng.setText(object.getString("symptomDesc"));
						iris_result_bingfazheng.setText(object.getString("announcements"));
						iris_result_suggesstion.setText(object.getString("suggestion"));
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
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stubd
				
			}
		});
		httpsetting.setNotifyUser(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);				
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
			//断开镜头
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
			jsonobject.put("clientId", "GZ-Hengxuan");
//			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userId", userPin);
			jsonobject.put("eye", eyeIndex);
			jsonobject.put("partId", bodyId);
			jsonobject.put("colorId", colorId);
			jsonobject.put("imgUrl", "0");//写成"null"服务器不通过
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.ADDTOSERVER);
		httpsetting.setRequestMethod("POST");
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				try {
					int code = json.getInt("code");
					if(code == 1){
						Toast.makeText(IrisDetailInfoActivity.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(IrisDetailInfoActivity.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		httpsetting.setShowProgress(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
}
