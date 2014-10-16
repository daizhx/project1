package com.jiuzhansoft.ehealthtec.lens.iris;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;



public class QueryCurrentDay extends BaseActivity implements OnClickListener{

	private ListView listview;
	private ArrayList<HashMap<String, String>> list;
	private String getDate;
	private int eyeIndex;
	private TextView tvWhichEye, tvWhichColor;
	private String getUserPin;
	private TextView badnettv;
	private Handler handler;
	private int[] eyesResId = new int[]{
			R.string.left_eye,
			R.string.Right_eye
	};
	
	private void dataToContentlist(JSONArrayPoxy jsonarraypoxy){
		if(jsonarraypoxy != null && jsonarraypoxy.length() >0){
			int i = 0;
			// ArrayList<HashMap<String, String>>  localArrayList = new ArrayList<HashMap<String, String>> ();
			// ArrayList<HashMap<String, String>> localArrayList = contentlist.get(childposition);
			while(i< jsonarraypoxy.length()){
				try {
					JSONObjectProxy jsonobjectproxy = jsonarraypoxy.getJSONObject(i);
					String iris_buwei = jsonobjectproxy.getStringOrNull("iris_buwei");
					String iris_color = jsonobjectproxy.getStringOrNull("iris_color");
					String iris_symptomDesc = jsonobjectproxy.getStringOrNull("iris_symptomDesc");
					String iris_announcements = jsonobjectproxy.getStringOrNull("iris_announcements");
					String iris_suggesstion = jsonobjectproxy.getStringOrNull("iris_suggesstion");
					
					HashMap<String, String> irisMap = new HashMap<String, String>();
					irisMap.put("iris_buwei", iris_buwei);
					irisMap.put("iris_color", iris_color);
					irisMap.put("iris_symptomDesc", iris_symptomDesc);
					irisMap.put("iris_announcements", iris_announcements);
					irisMap.put("iris_suggesstion", iris_suggesstion);
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
			jsonObject.put("eye", eyeIndex+"");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.CONTENTLIST);
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
								"diseaseList") != null
						&& response.getJSONObject()
								.getJSONArrayOrNull("diseaseList").length() > 0){
					JSONArrayPoxy common_diease_list = 
						response.getJSONObject().getJSONArrayOrNull("diseaseList");
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
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.activity_iris_reports_detail);
		badnettv = (TextView) findViewById(R.id.badnet);
		Intent intent = getIntent();
		getDate = intent.getStringExtra("currentdate");
		eyeIndex = intent.getIntExtra("eyeIndex", 0);
		
		getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		list = new ArrayList<HashMap<String, String>>();
		queryData();
		listview = (ListView) findViewById(R.id.current_datelist);
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case 1:
					listview.setAdapter(
							new SimpleAdapter(QueryCurrentDay.this, 
									list, 
									R.layout.irisreport_detail, 
									new String[]{"iris_buwei", "iris_color", "iris_symptomDesc", "iris_announcements", "iris_suggesstion"}, 
									new int[]{R.id.irisreport_result_organ_name,
									R.id.irisreport_result_color,
									R.id.irisreport_result_symptomDesc,
									R.id.irisreport_result_announcements,
									R.id.irisreport_result_maintenanceSuggestion}));
					break;
				case 2:
					badnettv.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				default:break;
				}
				super.handleMessage(msg);
			}			
		};
		tvWhichEye = (TextView)findViewById(R.id.tv_eyes_index);
		tvWhichEye.setBackgroundColor(Color.BLUE);
		tvWhichEye.setText(eyesResId[eyeIndex]);
		
		tvWhichColor = (TextView)findViewById(R.id.tv_color);
		tvWhichColor.setBackgroundColor(Color.BLUE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}


}
