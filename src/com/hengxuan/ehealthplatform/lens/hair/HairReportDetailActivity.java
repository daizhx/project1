package com.hengxuan.ehealthplatform.lens.hair;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstFuncId;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import com.hengxuan.ehealthplatform.http.json.JSONArrayPoxy;
import com.hengxuan.ehealthplatform.http.json.JSONObjectProxy;

public class HairReportDetailActivity extends BaseActivity {

	private ListView listview;
	private ArrayList<HashMap<String, String>> list;
	private String getDate;
	private String getUserPin;
	private TextView badnettv;
	private Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.hair_report);
		setContentView(R.layout.simple_list);
		badnettv = (TextView) findViewById(R.id.isempty);
		Intent intent = getIntent();
		getDate = intent.getStringExtra("currentdate");
		getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		list = new ArrayList<HashMap<String, String>>();
		queryData();
		listview = (ListView) findViewById(R.id.expandlist);
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 1:
					listview.setAdapter(
							new SimpleAdapter(HairReportDetailActivity.this, 
									list, 
									R.layout.skin_hair_report_detail, 
									new String[]{"name", "description", "reason", "suggestion"}, 
									new int[]{R.id.analysis_mode,
									R.id.analysis_status,
									R.id.cause_of_formation,
									R.id.recommendations_for_improvement}));
					break;
				case 2:
					badnettv.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				default:break;
				}
				super.handleMessage(msg);
			}			
		};
	}
	
	private void dataToContentlist(JSONArrayPoxy jsonarraypoxy){
		if(jsonarraypoxy != null && jsonarraypoxy.length() >0){
			int i = 0;
			// ArrayList<HashMap<String, String>>  localArrayList = new ArrayList<HashMap<String, String>> ();
			// ArrayList<HashMap<String, String>> localArrayList = contentlist.get(childposition);
			while(i< jsonarraypoxy.length()){
				try {
					JSONObjectProxy jsonobjectproxy = jsonarraypoxy.getJSONObject(i);
					String name = jsonobjectproxy.getStringOrNull("name");
					String description = jsonobjectproxy.getStringOrNull("description");
					String reason = jsonobjectproxy.getStringOrNull("reason");
					String suggestion = jsonobjectproxy.getStringOrNull("suggestion");
					
					HashMap<String, String> irisMap = new HashMap<String, String>();
					irisMap.put("name", name);
					irisMap.put("description", description);
					irisMap.put("reason", reason);
					irisMap.put("suggestion", suggestion);
					// localArrayList.add(irisMap);
					list.add(irisMap);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				i++;
			}
		}
	}
	
	private void queryData(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userPin", getUserPin);
			jsonObject.put("currentDate", getDate);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.HARECONTENTLIST);
		httpsetting.setJsonParams(jsonObject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				Message message = new Message();
				if (response.getJSONObject() != null
						&& response.getJSONObject().getJSONArrayOrNull(
								"hairInfoList") != null
						&& response.getJSONObject()
								.getJSONArrayOrNull("hairInfoList").length() > 0){
					JSONArrayPoxy common_diease_list = 
						response.getJSONObject().getJSONArrayOrNull("hairInfoList");
					dataToContentlist(common_diease_list);
					message.what = 1;
					handler.sendMessage(message);
				}else{
					message.what = 2;
					handler.sendMessage(message);
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
		getHttpGroupaAsynPool().add(httpsetting);
	}
}
