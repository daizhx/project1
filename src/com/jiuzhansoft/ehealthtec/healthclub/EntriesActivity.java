package com.jiuzhansoft.ehealthtec.healthclub;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.SimpleArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.activity.HealthClub;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupSetting;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

/**
 * 查看常见穴位与症状Entries
 * @author Administrator
 *
 */
public class EntriesActivity extends BaseActivity {
	private static final String TAG = "EntriesActivity";
	//
	private int flag;
	private static final int ACUPOINT = 1;
	private static final int DISEASE = 2;
	private ListView lvPosition;
	private ArrayList<String> list2 = new ArrayList<String>();
	private ArrayAdapter<String> adapter4list2;
	private int[] ids;
	//服务器接口
	private static final String GET_SYMPTOM_LIST = "disease/getDiseaseByPos";
	private static final String GET_ACUPOINT_LIST = "acupoint/getAcupointByPos";
	
	private int currentPosition = 1;
	private String[] positionStrIds;
	private SearchView mSearchView;
//	private SearchManager mSearchManager;
	
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
		mSearchView = (SearchView)findViewById(R.id.searchView1);
//		mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		Intent intent = getIntent();
		flag = intent.getIntExtra("index", 0);
		if(flag == ACUPOINT){
			setTitle(R.string.common_acupoint);
//			mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
		}else if(flag == DISEASE){
			setTitle(R.string.common_disease);
//			mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
		}
		mSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.d("daizhx", "--------------------"+hasFocus);
				if(hasFocus){
					
				}
			}
		});
		mSearchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(EntriesActivity.this, SearchActivity.class));
//				onSearchRequested();
			}
		});
		mSearchView.setOnSearchClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(EntriesActivity.this, SearchActivity.class));
//				onSearchRequested();
			}
		});
		
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
//		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, positionStrIds);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, positionStrIds);
		lvPosition.setAdapter(arrayAdapter);
		lvPosition.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvPosition.setItemChecked(0, true);
		lvPosition.setTextFilterEnabled(true);
		lvPosition.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.d("daizhx", "position="+position);
				int p = position + 1;
				lvPosition.setItemChecked(position, true);
				queryData("" + p);
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
				if(flag == ACUPOINT){
					intent.setClass(EntriesActivity.this, AcupointDetailActivity.class);
				}else if(flag == DISEASE){
					intent.setClass(EntriesActivity.this, SymptomDetailActivity.class);
				}else{
					//TODO
					Log.d("daizhx", "flag not right");
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
		if(flag == ACUPOINT){
			httpsetting.setFunctionId(GET_ACUPOINT_LIST);
		}else if(flag == DISEASE){
			httpsetting.setFunctionId(GET_SYMPTOM_LIST);
		}else{
			//TODO
			Log.d("daizhx", "flag not right");
		}
		httpsetting.setRequestMethod("GET");
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("position", position);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		httpsetting.setJsonParams(jsonObject);
		httpsetting.addArrayListParam(position+"");
		httpsetting.addArrayListParam(HealthClub.gender+"");
		httpsetting.addArrayListParam(CommonUtil.getLocalLauguage(this)+"");
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
				JSONObjectProxy resjson = response.getJSONObject();
				if(resjson == null)return;
				JSONArrayPoxy object = resjson.getJSONArrayOrNull("object");
				if(object == null)return;
				
				int length = object.length();
				ids = new int[length];
				list2.clear();
				for(int i=0;i<length;i++){
					try {
						JSONObjectProxy json = object.getJSONObject(i);
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
//		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
//		localHttpGroupSetting.setMyActivity(EntriesActivity.this);
//		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSONARRAY);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
}
