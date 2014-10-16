package com.jiuzhansoft.ehealthtec.healthclub;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupSetting;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.log.Log;

public class SymptomDetailActivity extends BaseActivity {

	//�������ӿ�
	private static final String GET_SYMPTOM_DETAIL = "getOneDiseaseByPos";
	private TextView tvDescription;
	private TextView tvSymptom;
	private String str1, str2;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1){
				tvDescription.setText(str1);
				tvSymptom.setText(str2);
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContent(R.layout.activity_symptom);
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		setTitle(name);
		int id = intent.getIntExtra("id", -1);
		if(id < 0){
			return;
		}
		tvDescription = (TextView)findViewById(R.id.tv_description);
		tvSymptom = (TextView)findViewById(R.id.tv_symptom);
		queryData(id);
	}
	
	private void queryData(int id){
		HttpSetting httpSetting = new HttpSetting();
		httpSetting.setFunctionId(GET_SYMPTOM_DETAIL);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpSetting.setJsonParams(jsonObject);
		httpSetting.setListener(new HttpGroup.OnAllListener() {
			
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
				JSONArrayPoxy jsonArrayPoxy = response.getJSONARRAY();
				if(jsonArrayPoxy == null)return;
				
				for(int i=0; i < jsonArrayPoxy.length();i++){
					try {
						JSONObject jsonObject = jsonArrayPoxy.getJSONObject(i);
						str1 = jsonObject.getString("disease_desc");
						str2 = jsonObject.getString("manifestation");
//						Log.d("daizhx","disease_desc=" +str1);
//						Log.d("daizhx", "symptom="+str2);
						mHandler.sendEmptyMessage(1);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}
		});
		httpSetting.setShowProgress(true);
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
		localHttpGroupSetting.setMyActivity(SymptomDetailActivity.this);
		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSONARRAY);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpSetting);
		
	}

}
