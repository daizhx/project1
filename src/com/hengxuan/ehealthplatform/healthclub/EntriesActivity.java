package com.hengxuan.ehealthplatform.healthclub;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.SimpleArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpGroupSetting;
import com.hengxuan.ehealthplatform.http.HttpGroupaAsynPool;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import com.hengxuan.ehealthplatform.http.json.JSONArrayPoxy;
import com.hengxuan.ehealthplatform.http.json.JSONObjectProxy;
import com.hengxuan.ehealthplatform.log.Log;

public class EntriesActivity extends BaseActivity {
	private static final String TAG = "EntriesActivity";
	private int index;
	private static final int ACUPOINT = 1;
	private static final int DISEASE = 2;
	private ListView lvPosition;
	private ArrayList<String> list2 = new ArrayList<String>();
	private ArrayAdapter<String> adapter4list2;
	private int[] ids;
	//服务器接口
	private static final String GET_SYMPTOM_LIST = "getDiseaseByPos";
	private static final String GET_ACUPOINT_LIST = "getAcupointByPos";
	
	private int currentPosition = 1;
	private String[] positionStrIds;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				adapter4list2.notifyDataSetChanged();
			}
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContent(R.layout.activity_entry);
		Intent intent = getIntent();
		index = intent.getIntExtra("index", 0);
		if(index == ACUPOINT){
			setTitle(R.string.common_acupoint);
			index = ACUPOINT;
		}else if(index == DISEASE){
			setTitle(R.string.common_disease);
			index = DISEASE;
		}
		lvPosition = (ListView)findViewById(R.id.list1);
		positionStrIds = new String[]{
					getString(R.string.back),
					getString(R.string.belly),
					getString(R.string.sole),
					getString(R.string.hand),
					getString(R.string.palm),
					getString(R.string.head),
					getString(R.string.leg),
					getString(R.string.chest),
					getString(R.string.foot),
					getString(R.string.os_pelvicum)
				};
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positionStrIds);
		lvPosition.setAdapter(arrayAdapter);
		lvPosition.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("daizhx", "position="+position);
				
			}
			
		});
		
		adapter4list2 = new ArrayAdapter<String>(this, R.layout.list_item_1, R.id.text, list2);
		ListView listView = (ListView)findViewById(R.id.list2);
		listView.setAdapter(adapter4list2);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				int id = ids[position];
//				Log.d("daizhx", "position="+position+",id="+id);
				Intent intent = new Intent();
				intent.putExtra("id", id);
				intent.putExtra("name", list2.get(position));
				if(index == ACUPOINT){
					intent.setClass(EntriesActivity.this, AcupointDetailActivity.class);
				}else if(index == DISEASE){
					intent.setClass(EntriesActivity.this, SymptomDetailActivity.class);
				}else{
					//TODO
					Log.d("daizhx", "index not right");
				}
				startActivity(intent);
			}
		});
		
		queryData("1");
	}
	
	
	//获取数据
	private void queryData(String position){
		//get data from server
		HttpSetting	httpsetting = new HttpSetting();
		if(index == ACUPOINT){
			httpsetting.setFunctionId(GET_ACUPOINT_LIST);
		}else if(index == DISEASE){
			httpsetting.setFunctionId(GET_SYMPTOM_LIST);
		}else{
			//TODO
			Log.d("daizhx", "index not right");
		}
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("position", position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpsetting.setJsonParams(jsonObject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			
			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onError");
			}
			
			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onEnd:response"+response);
				JSONArrayPoxy jsonArrayPoxy = response.getJSONARRAY();
				if(jsonArrayPoxy == null)return;
//				Log.d("daizhx", "jsonAarry="+jsonArrayPoxy);
				int length = jsonArrayPoxy.length();
				ids = new int[length];
				for(int i=0;i<length;i++){
					try {
						JSONObjectProxy json = jsonArrayPoxy.getJSONObject(i);
						int id = json.getInt("id");
						String name = json.getString("name");
						Log.d("daizhx", "id = "+ id);
						Log.d("daizhx", "name = "+ name);
						ids[i] = id;
						list2.add(name);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//adapter4list2.notifyDataSetChanged();
				handler.sendEmptyMessage(1);
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
			}
			
		});
		
		httpsetting.setShowProgress(true);
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
		localHttpGroupSetting.setMyActivity(EntriesActivity.this);
		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSONARRAY);
		HttpGroupaAsynPool httpGroupaAsynPool = new HttpGroupaAsynPool(
				localHttpGroupSetting);
		httpGroupaAsynPool.add(httpsetting);
	}
}
