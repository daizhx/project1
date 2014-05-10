package com.hengxuan.ehealthplatform.lens.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
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
import com.hengxuan.ehealthplatform.log.Log;

//镜头设备主界面
public class LensMainActivity extends BaseActivity {
	private ListView mListView;
	private ArrayList<HashMap<String, String>> mArrayList = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lens_main);
		mListView = (ListView)findViewById(R.id.lens_list);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.iris_miriam));
		mArrayList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.skan_detector));
		mArrayList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.hair_testing_instrument));
		mArrayList.add(map);

		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.naevus_analysis));
		mArrayList.add(map);

		SimpleAdapter adapter = new SimpleAdapter(LensMainActivity.this,
				mArrayList,
				R.layout.list_item_1,
				new String[]{"name"},
				new int[]{R.id.text});
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch(position){
					case 0:
						intent = new Intent(LensMainActivity.this, IrisMainActivity.class);
						break;
					case 1:
						intent = new Intent(LensMainActivity.this, SkinMainActivity.class);
						break;
					case 2:
						intent = new Intent(LensMainActivity.this, HairMainActivity.class);
						break;
					case 3:
						intent = new Intent(LensMainActivity.this, NevusMainActivity.class);
						break;
					default:
						break;
				}
				startActivity(intent);
			}
			
		});
	}

}
