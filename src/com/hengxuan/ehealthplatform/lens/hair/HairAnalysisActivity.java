package com.hengxuan.ehealthplatform.lens.hair;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.R.color;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.lens.LensBaseActivity;
import com.hengxuan.ehealthplatform.lens.LensShootBaseActivity;
import com.hengxuan.ehealthplatform.log.Log;

public class HairAnalysisActivity extends BaseActivity implements
		HairView.OnCanAnalysisListener {
	public static final String ACTION = "com.hengxuan.ehealthplatform.ACTION_HAIR_ANALYSIS";
	private int containerWidth;
	private int containerHeight;
	private FrameLayout container;
	private String picPath;
	private HairView filterView;
	private Button analysis;
	private int analysisClass;
	// 撤销选区
	private ImageView chacha;
	// 取消选区
	private ImageView reset;

	private RelativeLayout menu;
	private LinearLayout bottomBar;
	
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	
	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.hair_analysis);
		setContentView(R.layout.activity_hair_analysis);
		container = (FrameLayout) findViewById(R.id.container);
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
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		int screenWidth = outMetrics.widthPixels;
		int screenHeight = outMetrics.heightPixels;
		float density = outMetrics.density;
		//布局文件中定义为60
		int bottomBarHeight =  (int)(density*60);		
		containerHeight = screenHeight - statusBarHeight - actionBarHeight - bottomBarHeight;
		containerWidth = screenWidth;

		filterView = (HairView) findViewById(R.id.filter);
		picPath = getIntent().getExtras().getString(LensBaseActivity.PHOTO_PATH);
//		picPath = LensShootBaseActivity.TEXT_PIC;
		filterView.setPicPath(picPath);
		filterView.setBounds(containerWidth, containerHeight);
		filterView.setBuild(true);

		filterView.setOnCanAnalysisListener(this);

		analysis = (Button) findViewById(R.id.analysis);
		analysis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (filterView.canAnalysis()) {
					filterView.setAnalysis(true, analysisClass);
				} else {
					Toast.makeText(
							HairAnalysisActivity.this,
							getResources().getString(
									R.string.please_make_selection),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		chacha = (ImageView) findViewById(R.id.chacha);
		chacha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filterView.delete();
//				analysis.setImageResource(R.drawable.analysis_disable_btn);
			}
		});
		reset = (ImageView) findViewById(R.id.reset);
		reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				filterView.cancel();
				if(!filterView.canAnalysis()){
//					analysis.setImageResource(R.drawable.analysis_disable_btn);
					analysis.setEnabled(false);
				}
			}
		});

		initMenuView();
	}
	

	private void initMenuView() {
		menu = (RelativeLayout) findViewById(R.id.menu);
		menu.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		tv4 = (TextView) findViewById(R.id.tv4);
		tv1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv1.setBackgroundColor(Color.BLUE);
				tv2.setBackgroundColor(Color.TRANSPARENT);
				tv3.setBackgroundColor(Color.TRANSPARENT);
				tv4.setBackgroundColor(Color.TRANSPARENT);
				setAnalysisClass(1);
			}
		});
		tv4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv4.setBackgroundColor(Color.BLUE);
				tv2.setBackgroundColor(Color.TRANSPARENT);
				tv3.setBackgroundColor(Color.TRANSPARENT);
				tv1.setBackgroundColor(Color.TRANSPARENT);
				setAnalysisClass(4);
				Intent intent = new Intent(HairAnalysisActivity.this, HairOtherAnalysisActivity.class);
				intent.putExtra("picPath", picPath);
				startActivity(intent);
			}
		});
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv2.setBackgroundColor(Color.BLUE);
				tv1.setBackgroundColor(Color.TRANSPARENT);
				tv3.setBackgroundColor(Color.TRANSPARENT);
				tv4.setBackgroundColor(Color.TRANSPARENT);
				setAnalysisClass(2);
			}
		});
		tv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv3.setBackgroundColor(Color.BLUE);
				tv2.setBackgroundColor(Color.TRANSPARENT);
				tv1.setBackgroundColor(Color.TRANSPARENT);
				tv4.setBackgroundColor(Color.TRANSPARENT);
				setAnalysisClass(3);
				
			}
		});
		
		//init class one as the default analysis class,
		
	}

	private void setAnalysisClass(int mode) {
		analysisClass = mode;
	}

	// private void initMenuView() {
	// // TODO Auto-generated method stub
	// menu = (RelativeLayout)findViewById(R.id.menu);
	// int textSize = 18;
	//
	// TextView tv1 = new TextView(this);
	// tv1.setText(R.string.hair_water_analysis);
	// tv1.setTextSize(textSize);
	// tv1.setPadding(0, 0, 3, 0);
	// tv1.setTextColor(Color.WHITE);
	// tv1.setBackgroundColor(Color.BLUE);
	// LayoutParams lp1 = new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT);
	// lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	// lp1.addRule(RelativeLayout.CENTER_VERTICAL);
	// tv1.setLayoutParams(lp1);
	// tv1.setId(1);
	// menu.addView(tv1);
	//
	// TextView tv2 = new TextView(this);
	// tv2.setText(R.string.hair_gloss_analysis);
	// tv2.setTextSize(textSize);
	// tv2.setTextColor(Color.WHITE);
	// tv2.setPadding(3, 0, 3, 0);
	// LayoutParams lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT);
	// lp2.addRule(RelativeLayout.RIGHT_OF, 1);
	// tv2.setLayoutParams(lp2);
	// tv2.setId(2);
	// menu.addView(tv2);
	//
	// TextView tv3 = new TextView(this);
	// tv3.setText(R.string.elastic);
	// tv3.setTextColor(Color.WHITE);
	// tv3.setTextSize(textSize);
	// tv3.setPadding(3, 0, 3, 0);
	// LayoutParams lp3 = new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT);
	// lp3.addRule(RelativeLayout.RIGHT_OF, 2);
	// tv3.setLayoutParams(lp3);
	// tv3.setId(3);
	// menu.addView(tv3);
	//
	// TextView tv4 = new TextView(this);
	// tv4.setText(R.string.hair_detection);
	// tv4.setTextColor(Color.WHITE);
	// tv4.setTextSize(textSize);
	// tv4.setPadding(3, 0, 3, 0);
	// LayoutParams lp4 = new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT);
	// lp4.addRule(RelativeLayout.RIGHT_OF, 3);
	// tv4.setLayoutParams(lp2);
	// tv4.setId(4);
	// menu.addView(tv4);
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tv1.setBackgroundColor(Color.BLUE);
		tv2.setBackgroundColor(Color.TRANSPARENT);
		tv3.setBackgroundColor(Color.TRANSPARENT);
		tv4.setBackgroundColor(Color.TRANSPARENT);
		setAnalysisClass(1);
	}

	@Override
	public void onCanAnalysis() {
		// TODO Auto-generated method stub
//		analysis.setImageResource(R.drawable.analysis_btn);
		analysis.setEnabled(true);
	}
}
