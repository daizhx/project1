package com.hengxuan.ehealthplatform.lens.iris;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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

public class IrisReportActivity extends BaseActivity implements OnClickListener {

	private TextView tvLeftEye, tvRightEye;
	private String getStartDate, getEndDate;

	private ArrayList<HashMap<String, String>> datelist;
	private ArrayList<ArrayList<HashMap<String, String>>> contentlist;
	private String getUserPin;
	
	private ListView expandListView;
	private TextView isEmptytv;
	private Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.history_iris_report);
		setContentView(R.layout.activity_iris_report);
		
		datelist = new ArrayList<HashMap<String, String>>();
		contentlist = new ArrayList<ArrayList<HashMap<String, String>>>();
		getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		expandListView = (ListView)findViewById(R.id.list);
		isEmptytv = (TextView) findViewById(R.id.isempty);
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 1:
					final int getEyeIndex = msg.arg1;
					expandListView.setAdapter(
							new SimpleAdapter(IrisReportActivity.this, 
									datelist, 
									R.layout.date_list, 
									new String[]{"retDate"}, 
									new int[]{R.id.getcurrentdate}));
					expandListView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							TextView dateview = (TextView) arg1.findViewById(R.id.getcurrentdate);
							Intent intent = new Intent(IrisReportActivity.this, QueryCurrentDay.class);
							intent.putExtra("currentdate", dateview.getText().toString());
							intent.putExtra("eyeIndex", getEyeIndex);
							startActivity(intent);
							
							if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 5)
								overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);
						}
					});
					break;
				case 2:
					expandListView.setVisibility(View.GONE);
					isEmptytv.setVisibility(View.VISIBLE);
					break;
				default:break;
				}
				super.handleMessage(msg);
			}			
		};
		
		tvLeftEye = (TextView)findViewById(R.id.tv_left);
		tvRightEye = (TextView)findViewById(R.id.tv_right);
		tvLeftEye.setOnClickListener(this);
		tvRightEye.setOnClickListener(this);
		tvLeftEye.setBackgroundColor(Color.BLUE);
		showHistory(0);
	}
	
	private void showHistory(final int index){
		datelist = null;
		datelist = new ArrayList<HashMap<String, String>>();
		expandListView.setVisibility(View.VISIBLE);
		isEmptytv.setVisibility(View.GONE);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userPin", getUserPin);
			jsonObject.put("startDate", getStartDate);
			jsonObject.put("endDate", getEndDate);
			jsonObject.put("eye", index);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.DATELIST);
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
								"dateList") != null
						&& response.getJSONObject()
								.getJSONArrayOrNull("dateList").length() > 0){
					JSONArrayPoxy datePoxy = response.getJSONObject().getJSONArrayOrNull("dateList");
					dateToList(datePoxy);
					
					message.what = 1;
					message.arg1 = index;
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
	
	private void dateToList(JSONArrayPoxy poxy){
		int i=0;
		while(i<poxy.length()){
			JSONObjectProxy objectproxy;
			try {
				objectproxy = poxy.getJSONObject(i);
				HashMap<String, String> dateMap = new HashMap<String, String>();
				dateMap.put("retDate", objectproxy.getStringOrNull("retDate"));
				datelist.add(dateMap);
				contentlist.add(new ArrayList<HashMap<String, String>>());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.tv_left:
			tvLeftEye.setBackgroundColor(Color.BLUE);
			tvRightEye.setBackgroundColor(Color.TRANSPARENT);
			showHistory(0);
			break;
		case R.id.tv_right:
			tvLeftEye.setBackgroundColor(Color.TRANSPARENT);
			tvRightEye.setBackgroundColor(Color.BLUE);
			showHistory(1);
			break;
		default:
			break;
		}
	}
}
