package com.hengxuan.ehealthplatform.lens.skin;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;

public class SkinOtherAnalysisActivity extends BaseActivity {

	
	public static final int ANALYSIS_MODE_PORE = 5;
	public static final int ANALYSIS_MODE_ACNE = 7;
	public static final int ANALYSIS_MODE_SENSI_NUM = 8;
	public static final int ANALYSIS_MODE_SENSI_AREA = 9;
	private int analysisMode;
	private RelativeLayout imageContainer;
	private Button oper, spinner;
	private SkinTouchView imageView;
	private String picPath;
	private int screenHeight;
	private int screenWidth;
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, String>> otherList;
	private View menuView;
	private PopupWindow pop;
	private TextView textView;
	private int radioCheckId = R.id.pore_zoom;
	
	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setTitle(R.string.skin_analysis);
		setContentView(R.layout.skin_other_analysis);
		picPath = getIntent().getExtras().getString("picPath");
//		analysisMode = ANALYSIS_MODE_PORE;
		analysisMode = getIntent().getExtras().getInt("analysisMode");
		otherList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.pore_analysis));
		otherList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.acne_analysis));
		otherList.add(map);
		map = new HashMap<String, String>();
		map.put("name", getResources().getString(R.string.sensi_analysis));
		otherList.add(map);
		
		imageContainer = (RelativeLayout) findViewById(R.id.skan_container);
		oper = (Button)findViewById(R.id.skan_other_oper);
		oper.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(pop != null && pop.isShowing()){
					pop.dismiss();
					pop = null;
					return;
				}
				switch(analysisMode){
					case ANALYSIS_MODE_PORE:
						menuView = getLayoutInflater().inflate(R.layout.pore_oper_selector, null);
						
						break;
					case ANALYSIS_MODE_ACNE:						
						menuView = getLayoutInflater().inflate(R.layout.acne_oper_selector, null);
						
						break;
					case ANALYSIS_MODE_SENSI_NUM:						
						menuView = getLayoutInflater().inflate(R.layout.sensi_oper_selector, null);
						break;
					case ANALYSIS_MODE_SENSI_AREA:
						menuView = getLayoutInflater().inflate(R.layout.sensi_oper_selector, null);
						break;
					default:
						break;
				}
				initSettingLayout(menuView, true);
				PopSettingMenu(menuView,arg0);
			}
			
		});
		spinner = (Button)findViewById(R.id.other_ana);
		adapter = new SimpleAdapter(SkinOtherAnalysisActivity.this,
				otherList,
				R.layout.date_list,
				new String[]{"name"},
				new int[]{R.id.getcurrentdate});
		spinner.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuView = getLayoutInflater().inflate(R.layout.skan_ana_pop, null);
				initSettingLayout(menuView, false);
				PopSettingMenu(menuView, v);
			}
			
		});
		
		textView = (TextView)findViewById(R.id.skan_other_data);
		imageView = new SkinTouchView(this);
		imageView.setTextView(textView);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenHeight = displaymetrics.heightPixels;
		screenWidth = displaymetrics.widthPixels;
		imageContainer.addView(imageView, 0, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		Bitmap bitmap = BitmapFactory.decodeFile(picPath);
		imageView.init(SkinOtherAnalysisActivity.this, bitmap,
				screenHeight / 20);
		imageView.setAnalysisMode(ANALYSIS_MODE_PORE);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//return super.onTouchEvent(event);
		return imageView.getMultiTouchController().onTouchEvent(event);
	}
	public void initSettingLayout(final View view, boolean oper) {
		if(oper){
			switch(analysisMode){
			case ANALYSIS_MODE_PORE:
				RadioGroup group1 = (RadioGroup) view.findViewById(R.id.pore_oper);
				group1.check(radioCheckId);
				group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
							case R.id.pore_zoom:
								radioCheckId=R.id.pore_zoom;
								imageView.setDrawLine(false);
								break;
							case R.id.pore_draw:
								radioCheckId=R.id.pore_draw;
								imageView.setDrawLine(true);
								imageView.setDrawStart(true);
								break;
							case R.id.pore_analysis:
								if(imageView.canAnalysis()){
									Intent intent = new Intent(SkinOtherAnalysisActivity.this, SkinAnalysisResultActivity.class);
									intent.putExtra("mode", ANALYSIS_MODE_PORE);
									intent.putExtra("content", imageView.getPoreRadius());
									startActivity(intent);
								}else{
									Toast.makeText(SkinOtherAnalysisActivity.this, getResources().getString(R.string.please_draw_radius), Toast.LENGTH_LONG).show();
								}
								break;
							case R.id.pore_reset:
								if(imageView.getDrawLine()){
									imageView.setDrawLine(true);
									imageView.setDrawStart(true);
								}
								break;
						}
						if(pop != null)
							pop.dismiss();
					}
				});
				break;
			case ANALYSIS_MODE_ACNE:
				RadioGroup group2 = (RadioGroup) view.findViewById(R.id.acne_oper);
				group2.check(radioCheckId);
				group2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
							case R.id.acne_zoom:
								radioCheckId=R.id.acne_zoom;
								imageView.setDrawLine(false);
								break;
							case R.id.acne_draw:
								radioCheckId=R.id.acne_draw;
								imageView.setDrawLine(true);
								imageView.setDrawStart(true);
								break;
							case R.id.acne_analysis:
								if(imageView.canAnalysis()){
									Intent intent = new Intent(SkinOtherAnalysisActivity.this, SkinAnalysisResultActivity.class);
									intent.putExtra("mode", ANALYSIS_MODE_ACNE);
									intent.putExtra("content", imageView.getAcneRadius());
									startActivity(intent);
								}else{
									Toast.makeText(SkinOtherAnalysisActivity.this, getResources().getString(R.string.please_draw_radius), Toast.LENGTH_LONG).show();
								}
								break;
							case R.id.acne_reset:
								if(imageView.getDrawLine()){
									imageView.setDrawLine(true);
									imageView.setDrawStart(true);
								}
								break;
						}
						if(pop != null)
							pop.dismiss();
					}
				});
				break;
			case ANALYSIS_MODE_SENSI_NUM:
				RadioGroup group3 = (RadioGroup) view.findViewById(R.id.sensi_oper);
				group3.check(radioCheckId);
				group3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
							case R.id.sensi_zoom:
								radioCheckId=R.id.sensi_zoom;
								imageView.setDrawLine(false);
								break;
							case R.id.sensi_draw_num:
								radioCheckId=R.id.sensi_draw_num;
								imageView.setDrawLine(true);
								imageView.setDrawStart(false);
								imageView.setAnalysisMode(ANALYSIS_MODE_SENSI_NUM);
								break;
							case R.id.sensi_draw_area:
								radioCheckId=R.id.sensi_draw_area;
								imageView.setDrawLine(true);
								imageView.setDrawStart(true);
								imageView.setAnalysisMode(ANALYSIS_MODE_SENSI_AREA);
								break;
							case R.id.sensi_analysis:
								if(imageView.canAnalysis()){
									Intent intent = new Intent(SkinOtherAnalysisActivity.this, SkinAnalysisResultActivity.class);
									if(imageView.getAnalysisMode() == ANALYSIS_MODE_SENSI_NUM){
										intent.putExtra("mode", ANALYSIS_MODE_SENSI_NUM);
										intent.putExtra("content", imageView.getBloodStreakNum());
									}else{
										intent.putExtra("mode", ANALYSIS_MODE_SENSI_AREA);
										intent.putExtra("content", imageView.getBloodStreakArea());
									}
									startActivity(intent);
								}else{
									if(imageView.getAnalysisMode() == ANALYSIS_MODE_SENSI_NUM){
										Toast.makeText(SkinOtherAnalysisActivity.this, getResources().getString(R.string.please_draw_num), Toast.LENGTH_LONG).show();
									}else{
										Toast.makeText(SkinOtherAnalysisActivity.this, getResources().getString(R.string.please_draw_size), Toast.LENGTH_LONG).show();
									}
								}
								break;
							case R.id.sensi_reset:
								if(imageView.getDrawLine()){
									imageView.setDrawLine(true);
									imageView.setDrawStart(true);
								}
								break;
						}
						if(pop != null)
							pop.dismiss();
					}
				});
				break;
			case ANALYSIS_MODE_SENSI_AREA:
				
				break;
				
			}
		}else{
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
							spinner.setText(getResources().getString(R.string.pore_analysis));
							radioCheckId=R.id.pore_zoom;
							analysisMode = ANALYSIS_MODE_PORE;
							imageView.setAnalysisMode(ANALYSIS_MODE_PORE);
							imageView.setDrawLine(false);
							break;
						case 1:
							spinner.setText(getResources().getString(R.string.acne_analysis));
							radioCheckId=R.id.acne_zoom;
							analysisMode = ANALYSIS_MODE_ACNE;
							imageView.setAnalysisMode(ANALYSIS_MODE_ACNE);
							imageView.setDrawLine(false);
							break;
						case 2:
							spinner.setText(getResources().getString(R.string.sensi_analysis));
							radioCheckId=R.id.sensi_zoom;
							analysisMode = ANALYSIS_MODE_SENSI_NUM;
							imageView.setAnalysisMode(ANALYSIS_MODE_SENSI_NUM);
							imageView.setDrawLine(false);
							break;
						default:
							break;
					}
					if(pop != null)
						pop.dismiss();
				}
				
			});
		}
		
	}
	public void PopSettingMenu(View menuview,View view){
		if(pop == null){
			pop = new PopupWindow(menuview, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			pop.setAnimationStyle(R.style.skan_pop);
			//pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOutsideTouchable(true);
			pop.showAsDropDown(view, Gravity.CENTER_HORIZONTAL, 0);
			pop.update();
		}else{
			if(pop.isShowing()){
				pop.dismiss();
				pop = null;
			}else{
				pop = null;
				pop = new PopupWindow(menuview, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, true);
				pop.setAnimationStyle(R.style.skan_pop);
				//pop.setBackgroundDrawable(new BitmapDrawable());
				pop.setOutsideTouchable(true);
				pop.showAsDropDown(view, Gravity.CENTER_HORIZONTAL, 0);
				pop.update();
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(pop != null && pop.isShowing()){
				pop.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
