package com.jiuzhansoft.ehealthtec.lens.skin;

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

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.lens.hair.HairReportDetailActivity;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

public class SkinReportDetailActivity extends BaseActivity {
	private static final String TAG = "skin";
	private ListView listview;
	private ArrayList<HashMap<String, String>> list;
	private String getDate;
	private HashMap<String, String> content;
	private String getUserPin;
	private TextView badnettv;
	private Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContent(R.layout.simple_list);
		setTitle(R.string.skin_report);
		badnettv = (TextView) findViewById(R.id.isempty);
		Intent intent = getIntent();
//		getDate = intent.getStringExtra("currentdate");
		content = (HashMap<String, String>)intent.getSerializableExtra("content");
		getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		list = new ArrayList<HashMap<String, String>>();
//		queryData();
		list.add(content);
		listview = (ListView) findViewById(R.id.expandlist);
		listview.setAdapter(
		new SimpleAdapter(SkinReportDetailActivity.this, 
				list, 
				R.layout.skin_hair_report_detail, 
				new String[]{"name", "status", "reason", "suggestion"}, 
				new int[]{R.id.analysis_mode,
				R.id.analysis_status,
				R.id.cause_of_formation,
				R.id.recommendations_for_improvement}));
//		handler = new Handler(){
//			@Override
//			public void handleMessage(Message msg) {
//				// TODO Auto-generated method stub
//				switch(msg.what){
//				case 1:
//					listview.setAdapter(
//							new SimpleAdapter(SkinReportDetailActivity.this, 
//									list, 
//									R.layout.skin_hair_report_detail, 
//									new String[]{"name", "status", "reason", "suggestion"}, 
//									new int[]{R.id.analysis_mode,
//									R.id.analysis_status,
//									R.id.cause_of_formation,
//									R.id.recommendations_for_improvement}));
//					break;
//				case 2:
//					badnettv.setVisibility(View.VISIBLE);
//					listview.setVisibility(View.GONE);
//				default:break;
//				}
//				super.handleMessage(msg);
//			}			
//		};

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
					String status = jsonobjectproxy.getStringOrNull("status");
					String reason = jsonobjectproxy.getStringOrNull("reason");
					String suggestion = jsonobjectproxy.getStringOrNull("suggestion");
					
					HashMap<String, String> irisMap = new HashMap<String, String>();
					irisMap.put("name", name);
					irisMap.put("status", status);
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
			jsonObject.put("local", CommonUtil.getLocalLauguage(this));
			jsonObject.put("infotype", "0");
			jsonObject.put("userId", getUserPin);
			jsonObject.put("beginTime", getDate);
			jsonObject.put("endTime", getDate);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.SKANDATELIST);
		httpsetting.setJsonParams(jsonObject);
		httpsetting.setRequestMethod("GET");
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				JSONObjectProxy json = response.getJSONObject();
				try {
					int code = json.getInt("code");
					String msg = json.getString("msg");
					JSONArrayPoxy object = json.getJSONArrayOrNull("object");
					Message message = new Message();
					if(code == 1 && object != null){
						dataToContentlist(object);
						message.what = 1;
						handler.sendMessage(message);
					}else{
						message.what = 2;
						handler.sendMessage(message);
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
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
}
