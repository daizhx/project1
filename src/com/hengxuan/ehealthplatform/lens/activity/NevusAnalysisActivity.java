package com.hengxuan.ehealthplatform.lens.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.hengxuan.ehealthplatform.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NevusAnalysisActivity extends Activity {

	 /** Called when the activity is first created. */
	private NevusView filterView;
	private Button operation;
	
	private String picPath;
	private PopupWindow pop;
	private boolean isAna;
	private ArrayList<HashMap<String, String>> anaOperList;
	private SimpleAdapter adapter;
	private View menuView;
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState); 
       setContentView(R.layout.nevus_analysis_activity);
       WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		int screenHeight = wm.getDefaultDisplay().getHeight();
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame); 
		
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
		picPath = getIntent().getExtras().getString("picPath");
       filterView = (NevusView)findViewById(R.id.naevus_filter);
       filterView.setPicPath(picPath);
       filterView.setBounds(screenWidth, screenHeight - statusBarHeight);
       operation = (Button)findViewById(R.id.naevus_build);
       operation.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				displayPopupInfo();
			}
       	
       });
       operation.getBackground().setAlpha(150);
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
		adapter = new SimpleAdapter(NevusAnalysisActivity.this,
				anaOperList,
				R.layout.list_item_1,
				new String[]{"name"},
				new int[]{R.id.text});
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
							setButtonEnable(false);
						}else{
							Toast.makeText(NevusAnalysisActivity.this, getResources().getString(R.string.please_make_selection), Toast.LENGTH_LONG).show();
						}
						break;
					case 4:
						Intent intent = new Intent(NevusAnalysisActivity.this, CheckFileActivity.class);
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
			pop.showAsDropDown(operation);
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
				pop.showAsDropDown(operation);
				pop.update();
			}
		}
	}
	public void setButtonEnable(boolean enable){
		operation.setEnabled(enable);
	}

}
