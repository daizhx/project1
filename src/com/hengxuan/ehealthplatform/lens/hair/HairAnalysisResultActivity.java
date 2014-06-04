package com.hengxuan.ehealthplatform.lens.hair;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstFuncId;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import com.hengxuan.ehealthplatform.http.json.JSONArrayPoxy;
import com.hengxuan.ehealthplatform.log.Log;



public class HairAnalysisResultActivity extends BaseActivity {
	
	private int analysisMode;
	private TextView contentText;
	private TextView content;
	private TextView stateText;
	private TextView state;
	private TextView reason;
	private TextView suggest;
	private JSONArrayPoxy jsonPoxy;
	private JSONObject json;
	private Button addtoReportBtn;
	private String hairContentText="";
	private String sentContent="";
	private String hairContent="";
	private String hairStateText="";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skin_analysis_result);
		setTitle(R.string.hair_result);
		contentText = (TextView)findViewById(R.id.skan_content_text);
		content = (TextView)findViewById(R.id.skan_content);
		stateText = (TextView)findViewById(R.id.skan_state_text);
		state = (TextView)findViewById(R.id.skan_state);
		reason = (TextView)findViewById(R.id.skan_reason);
		suggest = (TextView)findViewById(R.id.skan_suggest);
		addtoReportBtn = (Button) findViewById(R.id.nextbtn);
		addtoReportBtn.setEnabled(false);
		addtoReportBtn.setVisibility(View.VISIBLE);
		addtoReportBtn.setText(getResources().getString(R.string.addto_report));
		addtoReportBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addtoReportBtn.setEnabled(false);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date currentdate = new Date(System.currentTimeMillis());
				String timestr = format.format(currentdate);
				String getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
				addToServer(timestr, getUserPin, analysisMode, sentContent);
			}
			
		});
		analysisMode = getIntent().getExtras().getInt("mode");
		
		int per = 0;
		switch(analysisMode){
			case HairView.HAIR_WATER:
				hairContentText=getResources().getString(R.string.hair_water_state);
				if(getIntent().getExtras().getInt("content") == 0){
					sentContent = "0";
				}else if(getIntent().getExtras().getInt("content") <= 6){
					sentContent = "1-6";
				}else if(getIntent().getExtras().getInt("content") <= 10){
					sentContent = "7-10";
				}else{
					sentContent = "11-15";
				}
				per = getIntent().getExtras().getInt("content");
				if(per > 15)
					per = 15;
				hairContent = per + "%";
				hairStateText=getResources().getString(R.string.hair_water_content);
				break;
			case HairView.HAIR_GLOSS:
				hairContentText=getResources().getString(R.string.hair_gloss_state);
				if(getIntent().getExtras().getInt("content") <= 6){
					sentContent = "0-6";
				}else if(getIntent().getExtras().getInt("content") <= 13){
					sentContent = "7-13";
				}else{
					sentContent = "14-35";
				}
				per = getIntent().getExtras().getInt("content");
				if(per >35)
					per = 35;
				hairContent = per + "%";
				hairStateText=getResources().getString(R.string.hair_gloss_content);
				break;
			case HairView.HAIR_ELASTIC:
				hairContentText=getResources().getString(R.string.hair_elastic);
				if(getIntent().getExtras().getInt("content") <= 18){
					sentContent = "0-18";
				}else if(getIntent().getExtras().getInt("content") <= 38){
					sentContent = "19-38";
				}else{
					sentContent = "50";
				}
				per = getIntent().getExtras().getInt("content");
				if(per > 50)
					per = 50;
				hairContent = per + "%";
				hairStateText=getResources().getString(R.string.hair_elastic_content);
				break;
			case HairView.HAIR_DETECTION:
				hairContentText=getResources().getString(R.string.rooting_state);
				if(getIntent().getExtras().getFloat("content") <= 0.01){
					sentContent = "<0.01mm";
				}else if(getIntent().getExtras().getFloat("content") <= 0.15){
					sentContent = "0.01<r<0.15(mm)";
				}else if(getIntent().getExtras().getFloat("content") <= 0.2){
					sentContent = "0.15<r<0.2(mm)";
				}else{
					sentContent = "r>0.2(mm)";
				}
				float length = getIntent().getExtras().getFloat("content");
				if(length < 0.01)
					length = 0.01f;
				hairContent = String.format("%.2f", length) + "mm";
				hairStateText=getResources().getString(R.string.rooting_radius);
				break;
			default:
				break;
		}
		contentText.setText(hairStateText);
		content.setText(hairContent);
		stateText.setText(hairContentText);
		getHairResult(sentContent);
	}
	
	private void getHairResult(String content) {
		// TODO Auto-generated method stub

		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("type", analysisMode);
			jsonobject.put("content", content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.HAIRANALYSISRESULT);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			
			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(HttpError httpError) {
				
			}
			
			@Override
			public void onEnd(HttpResponse response) {
				if(response.getJSONObject()!=null){
					try {									
						jsonPoxy=response.getJSONObject().getJSONArray("hairInfo");
						post(new Runnable(){
							
							@Override
							public void run() {
								try {
									json=jsonPoxy.getJSONObject(0);
									state.setText(json.getString("description"));
									reason.setText(json.getString("reason"));
									suggest.setText(json.getString("suggestion"));	
									addtoReportBtn.setEnabled(true);
								} catch (JSONException e) {
									
									final AlertDialog dialog = (new AlertDialog.Builder(HairAnalysisResultActivity.this)).create();
									dialog.show();
									dialog.setMessage(getResources().getString(R.string.notalldata));
									dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
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

	private void addToServer(String currentDate, String userPin, 
			int type, String content){
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("client_code", "GZ-Hengxuan");
			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userPin", userPin);
			jsonobject.put("type", type);
			jsonobject.put("content", content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.ADDHAIRRESULTTOSERVER);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onEnd(HttpResponse response) {
				final int getcode = response.getCode();
				post(new Runnable() {
					
					@Override
					public void run() {
						if(getcode == 200)
							Toast.makeText(HairAnalysisResultActivity.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(HairAnalysisResultActivity.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
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
