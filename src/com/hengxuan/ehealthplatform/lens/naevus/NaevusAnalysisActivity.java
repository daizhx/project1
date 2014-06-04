package com.hengxuan.ehealthplatform.lens.naevus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.BoringLayout.Metrics;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.lens.LensBaseActivity;
import com.hengxuan.ehealthplatform.lens.LensShootBaseActivity;


public class NaevusAnalysisActivity extends BaseActivity {

	private NaevusView filterView;
	private String photoPath;
	private PopupWindow pop;
	private View menuView;
	private ArrayList<HashMap<String, String>> anaOperList;
	private SimpleAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.naevus_analysis);
		setLeftIcon(R.drawable.ic_action_back);
		setRightIcon(R.drawable.ic_action_overflow, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayPopupInfo();
			}
		});
		setContentView(R.layout.activity_naevus_analysis);
		
		Intent intent = getIntent();
		photoPath = intent.getStringExtra(LensBaseActivity.PHOTO_PATH);
		filterView = (NaevusView)findViewById(R.id.naevus_filter);
		filterView.setPicPath(photoPath);
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		int statusBarHeight = 0;
		Class c;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();  
			Field field = c.getField("status_bar_height");  
			int x = Integer.parseInt(field.get(obj).toString());  
			statusBarHeight = getResources().getDimensionPixelSize(x); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int actionBarHeight = getActionBarHeight();
		filterView.setBounds(screenWidth, screenHeight - statusBarHeight -actionBarHeight);
		
		
		anaOperList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.build_selection));
		anaOperList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.cancel_selection));
		anaOperList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.delete_selection));
		anaOperList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.analysis_text));
		anaOperList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.check_file));
		anaOperList.add(map);
		adapter = new SimpleAdapter(NaevusAnalysisActivity.this,
				anaOperList,
				R.layout.date_list,
				new String[]{"name"},
				new int[]{R.id.getcurrentdate});
		menuView = getLayoutInflater().inflate(R.layout.skan_ana_pop, null);		
		
		ListView list = (ListView)menuView.findViewById(R.id.skan_ana_oper_list);
		list.setAdapter(adapter);
		list.getBackground().setAlpha(100);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch(position){
					case 0:
						filterView.setBuild(true);
						break;
					case 1:
						filterView.cancel();
						break;
					case 2:
						filterView.delete();
						break;
					case 3:
						if(filterView.canAnalysis()){
							filterView.setAnalysis(true);
						}else{
							Toast.makeText(NaevusAnalysisActivity.this, getResources().getString(R.string.please_make_selection), Toast.LENGTH_LONG).show();
						}
						break;
					case 4:
						Intent intent = new Intent(NaevusAnalysisActivity.this, CheckFileActivity.class);
						startActivity(intent);
						break;
					default:
						break;
				}
				pop.dismiss();
			}
			
		});
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return filterView.onTouchEvent(event);
	}
	private void displayPopupInfo() {
		if(pop == null){
			pop = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			menuView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			pop.setAnimationStyle(R.style.skan_pop);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOutsideTouchable(true);
			pop.showAsDropDown(rightIcon);
			pop.update();
		}else{
			if(pop.isShowing()){
				pop.dismiss();
				pop = null;
			}else{
				pop = null;
				pop = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, true);
				menuView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				pop.setAnimationStyle(R.style.skan_pop);
				pop.setBackgroundDrawable(new BitmapDrawable());
				pop.setOutsideTouchable(true);
				pop.showAsDropDown(rightIcon);
				pop.update();
			}
		}
	}
	
}
