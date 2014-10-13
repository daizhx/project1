package com.jiuzhansoft.ehealthtec.lens.hair;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.R.color;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensBaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensConstant;
import com.jiuzhansoft.ehealthtec.lens.LensShootBaseActivity;
import com.jiuzhansoft.ehealthtec.log.Log;

public class HairAnalysisActivity extends BaseActivity implements
		HairView.OnCanAnalysisListener {
	private int containerWidth;
	private int containerHeight;
	private FrameLayout container;
	private FrameLayout bottomBar;
	private String picPath;
	private HairView filterView = null;
	private Button analysis;
	private int analysisClass;
	// 撤销选区
	private ImageView chacha;
	// 取消选区
	private ImageView reset;

	private RelativeLayout menu;

	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;

	private TextView textView;
	private HairTouchView hairTouchView;
	private int screenWidth, screenHeight;
	private TextView tvZoom,tvLine;

	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setTitle(R.string.hair_analysis);
		setContentView(R.layout.activity_hair_analysis);
		container = (FrameLayout) findViewById(R.id.container);
		bottomBar = (FrameLayout) findViewById(R.id.bottom_bar);
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
		// 布局文件中定义为60
		int bottomBarHeight = (int) (density * 60);
		containerHeight = screenHeight - statusBarHeight - actionBarHeight
				- bottomBarHeight;
		containerWidth = screenWidth;

		// filterView = (HairView) findViewById(R.id.filter);
		
		change2HairView();
		changeBottomBar();

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
				if(analysisClass == 1)return;
				tv1.setBackgroundColor(Color.BLUE);
				tv2.setBackgroundColor(Color.TRANSPARENT);
				tv3.setBackgroundColor(Color.TRANSPARENT);
				tv4.setBackgroundColor(Color.TRANSPARENT);
				if(analysisClass == 4){
					setAnalysisClass(1);
					change2HairView();
					changeBottomBar();
				}else{
					setAnalysisClass(1);
				}
				
			}
		});
		tv4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(analysisClass == 4)return;
				tv4.setBackgroundColor(Color.BLUE);
				tv2.setBackgroundColor(Color.TRANSPARENT);
				tv3.setBackgroundColor(Color.TRANSPARENT);
				tv1.setBackgroundColor(Color.TRANSPARENT);
				setAnalysisClass(4);
				change2HairTouchView();
				changeBottomBar();
			}
		});
		tv2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(analysisClass == 2)return;
				tv2.setBackgroundColor(Color.BLUE);
				tv1.setBackgroundColor(Color.TRANSPARENT);
				tv3.setBackgroundColor(Color.TRANSPARENT);
				tv4.setBackgroundColor(Color.TRANSPARENT);
				if(analysisClass == 4){
					setAnalysisClass(2);
					change2HairView();
					changeBottomBar();
				}else{
					setAnalysisClass(2);
				}
				
			}
		});
		tv3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(analysisClass == 3)return;
				// TODO Auto-generated method stub
				tv3.setBackgroundColor(Color.BLUE);
				tv2.setBackgroundColor(Color.TRANSPARENT);
				tv1.setBackgroundColor(Color.TRANSPARENT);
				tv4.setBackgroundColor(Color.TRANSPARENT);
				if(analysisClass == 4){
					setAnalysisClass(3);
					change2HairView();
					changeBottomBar();
				}else{
					setAnalysisClass(3);
				}
				

			}
		});

		// init class one as the default analysis class,
		tv1.setBackgroundColor(Color.BLUE);
		tv2.setBackgroundColor(Color.TRANSPARENT);
		tv3.setBackgroundColor(Color.TRANSPARENT);
		tv4.setBackgroundColor(Color.TRANSPARENT);
		setAnalysisClass(1);
	}

	protected void change2HairView() {
		// TODO Auto-generated method stub
		if(filterView != null){
			container.removeAllViews();
			container.addView(filterView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}else{
			filterView = new HairView(this);
			picPath = getIntent().getExtras().getString(LensConstant.PHOTO_PATH);
			if (picPath == null) {
				finish();
				return;
			}
			filterView.setPicPath(picPath);
			filterView.setBounds(containerWidth, containerHeight);
			filterView.setBuild(true);

			filterView.setOnCanAnalysisListener(this);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			container.addView(filterView, lp);
		}
	}

	protected void changeBottomBar() {
		// TODO Auto-generated method stub
		View view = null;
		if (analysisClass == 4) {
			view = getLayoutInflater().inflate(R.layout.bottom_bar_2, null);
			tvZoom = (TextView)view.findViewById(R.id.tv_zoom);
//			tvZoom.setCompoundDrawables(null, getResources().getDrawable(R.drawable.ic_zoom_blue), null, null);
			tvZoom.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_zoom_blue, 0, 0);
			hairTouchView.setDrawLine(false);
			tvZoom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(hairTouchView.getDrawLine()){
						hairTouchView.setDrawLine(false);
						tvZoom.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_zoom_blue, 0, 0);
						tvLine.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_line, 0, 0);
					}
				}
			});
			
			tvLine = (TextView)view.findViewById(R.id.tv_line);
			tvLine.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(!hairTouchView.getDrawLine()){
						hairTouchView.setDrawLine(true);
						hairTouchView.setDrawStart(true);
//						tvLine.setCompoundDrawables(null, getResources().getDrawable(R.drawable.ic_line_blue), null, null);
						tvLine.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_line_blue, 0, 0);
						tvZoom.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_zoom, 0, 0);
					}
				}
			});
			
			TextView tvAna = (TextView)view.findViewById(R.id.tv_ana);
			tvAna.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (hairTouchView.canAnalysis()) {
						Intent intent = new Intent(
								HairAnalysisActivity.this,
								HairAnalysisResultActivity.class);
						intent.putExtra("mode", HairView.HAIR_DETECTION);
						intent.putExtra("content", hairTouchView.getPoreRadius());
						startActivity(intent);
					} else {
						Toast.makeText(
								HairAnalysisActivity.this,
								getResources().getString(
										R.string.please_draw_radius),
								Toast.LENGTH_LONG).show();
					}
				}
			});
			TextView tvReset = (TextView)view.findViewById(R.id.tv_reset);
			tvReset.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(hairTouchView.getDrawLine()){
						hairTouchView.setDrawLine(true);
						hairTouchView.setDrawStart(true);
					}else{
						Toast.makeText(
								HairAnalysisActivity.this,
								getResources().getString(R.string.please_draw_radius),Toast.LENGTH_LONG).show();
					}
				}
			});
			
		} else {
			view = getLayoutInflater()
					.inflate(R.layout.bottom_bar_1, null);
			analysis = (Button) view.findViewById(R.id.analysis);
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

			chacha = (ImageView) view.findViewById(R.id.chacha);
			chacha.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					filterView.delete();
					// analysis.setImageResource(R.drawable.analysis_disable_btn);
				}
			});
			reset = (ImageView) view.findViewById(R.id.reset);
			reset.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					filterView.cancel();
					if (!filterView.canAnalysis()) {
						// analysis.setImageResource(R.drawable.analysis_disable_btn);
						analysis.setEnabled(false);
					}
				}
			});
			
		}
		
		bottomBar.removeAllViews();
		bottomBar.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	protected void change2HairTouchView() {
		// TODO Auto-generated method stub
		textView = (TextView) findViewById(R.id.other_data);
		hairTouchView = new HairTouchView(this);
		hairTouchView.setTextView(textView);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenHeight = displaymetrics.heightPixels;
		screenWidth = displaymetrics.widthPixels;
		container.removeAllViews();
		container.addView(hairTouchView, 0, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		hairTouchView.setScaleType(ImageView.ScaleType.FIT_XY);
		Bitmap bitmap = BitmapFactory.decodeFile(picPath);
		hairTouchView
				.init(HairAnalysisActivity.this, bitmap, screenHeight / 20);
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

	}

	@Override
	public void onCanAnalysis() {
		// TODO Auto-generated method stub
		analysis.setEnabled(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// return super.onTouchEvent(event);
		return hairTouchView.getMultiTouchController().onTouchEvent(event);
	}
}
