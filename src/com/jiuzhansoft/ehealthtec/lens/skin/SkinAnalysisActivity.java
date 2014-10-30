package com.jiuzhansoft.ehealthtec.lens.skin;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensBaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensConstant;

public class SkinAnalysisActivity extends BaseActivity implements
		OnClickListener {

	private String picPath;
	private PopupWindow pop;

	private ArrayList<HashMap<String, String>> anaOperList;
	private SimpleAdapter adapter;

	private View menuView;

	public static final int MSG_OIL = 1;
	public static final int MSG_WATER = 6;
	public static final int MSG_PIGMENT = 2;
	public static final int MSG_ELASTIC = 4;
	public static final int MSG_COLLAGEN = 3;

	public static final String ACTION = "com.jiuzhansoft.ehealthtec.ACTION_SKIN_ANALYSIS";
	private int currentMenu;// 0,1,2,3,4,5,6,7

	private FragmentManager mFragmentManager;
	private ImagePixelFragment imagePixelFragment;
	private MannulAnalysisFragment mannulAnalysisFragment;

	// 显示手动操作的结果
	private TextView mTextView;
	public static final int ANALYSIS_MODE_PORE = 5;
	public static final int ANALYSIS_MODE_ACNE = 7;
	public static final int ANALYSIS_MODE_SENSI_NUM = 8;
	public static final int ANALYSIS_MODE_SENSI_AREA = 9;

	private int analysisClass;
	public static final int PIXEL_ANALYSIS = 0;
	public static final int MANNUL_ANALYSIS = 1;

	private RelativeLayout bottonBar;

	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setTitle(R.string.skin_analysis);
		setContentView(R.layout.skin_analysis_activity);
		mTextView = (TextView) findViewById(R.id.skin_other_data);
		bottonBar = (RelativeLayout) findViewById(R.id.bottom);
		change2PixelAnalysisBottom();
		 picPath =
		 getIntent().getExtras().getString(LensConstant.PHOTO_PATH);
		// test
//		picPath = Environment.getExternalStorageDirectory() + File.separator
//				+ "dxlphoto" + File.separator + "test.png";
		if (picPath == null) {
			return;
		}

		mFragmentManager = getFragmentManager();
		imagePixelFragment = new ImagePixelFragment();
		imagePixelFragment.setPicPath(picPath);
		setPixelAnalysis(imagePixelFragment);

		mannulAnalysisFragment = new MannulAnalysisFragment();
		mannulAnalysisFragment.setPicPath(picPath);
		mannulAnalysisFragment.setTextView(mTextView);

		mFragmentManager.beginTransaction()
				.add(R.id.container, imagePixelFragment).commit();
		analysisClass = PIXEL_ANALYSIS;

		{
			// 初始化
			menus[0] = (TextView) findViewById(R.id.tv1);
			menus[1] = (TextView) findViewById(R.id.tv2);
			menus[2] = (TextView) findViewById(R.id.tv3);
			menus[3] = (TextView) findViewById(R.id.tv4);
			menus[4] = (TextView) findViewById(R.id.tv5);
			menus[5] = (TextView) findViewById(R.id.tv6);
			menus[6] = (TextView) findViewById(R.id.tv7);
			menus[7] = (TextView) findViewById(R.id.tv8);
			menus[0].setBackgroundColor(Color.BLUE);
			for (int i = 0; i < 8; i++) {
				menus[i].setOnClickListener(this);
			}
			currentMenu = 0;
			setBottonBarClickListener(bottonBar);

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (pop != null && pop.isShowing()) {
				pop.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	TextView[] menus = new TextView[8];

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.tv1:
			currentMenu = 0;
			setMenuBg(currentMenu);
			change2PixelAnalysis();
			break;
		case R.id.tv2:
			currentMenu = 1;
			setMenuBg(currentMenu);
			change2PixelAnalysis();
			break;
		case R.id.tv3:
			currentMenu = 2;
			setMenuBg(currentMenu);
			change2PixelAnalysis();
			break;
		case R.id.tv4:
			currentMenu = 3;
			setMenuBg(currentMenu);
			change2PixelAnalysis();
			break;
		case R.id.tv5:
			currentMenu = 4;
			setMenuBg(currentMenu);
			change2PixelAnalysis();
			break;
		case R.id.tv6:
			currentMenu = 5;
			setMenuBg(currentMenu);
			mannulAnalysisFragment.setAnalysisMode(ANALYSIS_MODE_PORE);
			change2MannulAnalysis();
			break;
		case R.id.tv7:
			currentMenu = 6;
			setMenuBg(currentMenu);
			mannulAnalysisFragment.setAnalysisMode(ANALYSIS_MODE_ACNE);
			change2MannulAnalysis();
			return;
		case R.id.tv8:
			currentMenu = 7;
			setMenuBg(currentMenu);
			mannulAnalysisFragment.setAnalysisMode(ANALYSIS_MODE_SENSI_NUM);
			change2MannulAnalysis();
			return;
		case R.id.analysis:
			mPixelAnalysis.onPixelAnalysis(currentMenu);
			return;
		default:
			break;
		}

	}

	private void change2MannulAnalysis() {
		if (analysisClass == PIXEL_ANALYSIS) {
			mFragmentManager.beginTransaction()
					.replace(R.id.container, mannulAnalysisFragment).commit();
			analysisClass = MANNUL_ANALYSIS;
			mTextView.setVisibility(View.VISIBLE);
//			change2ManulAnaBottom();
		}
		change2ManulAnaBottom();
	}

	private void change2PixelAnalysis() {
		if (analysisClass == MANNUL_ANALYSIS) {
			mFragmentManager.beginTransaction()
					.replace(R.id.container, imagePixelFragment).commit();
			analysisClass = PIXEL_ANALYSIS;
			mTextView.setVisibility(View.GONE);
			change2PixelAnalysisBottom();
		}
	}

	private void change2PixelAnalysisBottom() {
		// TODO Auto-generated method stub
		bottonBar.removeAllViews();
		View view = getLayoutInflater().inflate(R.layout.analysis_bar, null,
				false);
		bottonBar.addView(view);
		setBottonBarClickListener(view);
	}

	private void setBottonBarClickListener(View bottomBar) {
		((Button) bottomBar.findViewById(R.id.analysis))
				.setOnClickListener(this);
	}

	private int operateState;
	private static final int ZOOM = 1;
	private static final int LINE = 2;
	private static final int ANALYSIS = 3;
	private static final int RESET = 4;
	private static final int CIRCLE = 5;
	private static final int NUMBER = 6;
	private static final int SIZE = 7;
	private ImageView iv_zoom;
	private TextView tv_zoom;
	private ImageView iv_line;
	private TextView tv_line;
	private ImageView iv_ana;
	private TextView tv_ana;
	private ImageView iv_reset;
	private TextView tv_reset;
	private ImageView iv_circle;
	private TextView tv_circle;
	private ImageView iv_number;
	private TextView tv_number;
	private ImageView iv_size;
	private TextView tv_size;

	private void change2ManulAnaBottom() {
		bottonBar.removeAllViews();
		View view = null;
		switch (currentMenu) {
		case 5:
			view = getLayoutInflater().inflate(R.layout.skin_pore_bottom,
					null, false);
			iv_line = (ImageView) view.findViewById(R.id.iv_line);
			tv_line = (TextView) view.findViewById(R.id.tv_line);
			view.findViewById(R.id.line).setOnClickListener(BottomBarClickListener);
			break;
		case 6:
			view = getLayoutInflater().inflate(R.layout.skin_ance_bottom,
					null, false);
			iv_circle = (ImageView) view.findViewById(R.id.iv_circle);
			tv_circle = (TextView) view.findViewById(R.id.tv_circle);
			view.findViewById(R.id.circle).setOnClickListener(BottomBarClickListener);
			break;
		case 7:
			view = getLayoutInflater().inflate(R.layout.skin_sensi_bottom,
					null, false);
			iv_size = (ImageView) view.findViewById(R.id.iv_size);
			tv_size = (TextView) view.findViewById(R.id.tv_size);
			
			iv_number = (ImageView) view.findViewById(R.id.iv_number);
			tv_number = (TextView) view.findViewById(R.id.tv_number);
			view.findViewById(R.id.size).setOnClickListener(BottomBarClickListener);
			view.findViewById(R.id.number).setOnClickListener(BottomBarClickListener);
			break;
		default:
			break;
		};
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		bottonBar.addView(view, lp);
		view.findViewById(R.id.zoom).setOnClickListener(BottomBarClickListener);
		view.findViewById(R.id.ana).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					iv_ana.setImageResource(R.drawable.ic_ana_blue);
					tv_ana.setTextColor(Color.BLUE);
					return true;
				case MotionEvent.ACTION_UP:
					iv_ana.setImageResource(R.drawable.ic_ana);
					tv_ana.setTextColor(Color.WHITE);
					if (mannulAnalysisFragment.imageView.canAnalysis()) {
						Intent intent = new Intent(SkinAnalysisActivity.this,
								SkinAnalysisResultActivity.class);
						if (currentMenu == 5) {
							intent.putExtra("mode", ANALYSIS_MODE_PORE);
							intent.putExtra("content",
									mannulAnalysisFragment.imageView.getPoreRadius());
						} else if (currentMenu == 6) {
							intent.putExtra("mode", ANALYSIS_MODE_ACNE);
							intent.putExtra("content",
									mannulAnalysisFragment.imageView.getAcneRadius());
						} else if (currentMenu == 7) {
							if (mannulAnalysisFragment.imageView.getAnalysisMode() == ANALYSIS_MODE_SENSI_NUM) {
								intent.putExtra("mode", ANALYSIS_MODE_SENSI_NUM);
								intent.putExtra("content",
										mannulAnalysisFragment.imageView
												.getBloodStreakNum());
							} else {
								intent.putExtra("mode", ANALYSIS_MODE_SENSI_AREA);
								intent.putExtra("content",
										mannulAnalysisFragment.imageView
												.getBloodStreakArea());
							}
						}
						startActivity(intent);
					} else {
						Toast.makeText(SkinAnalysisActivity.this,
								getResources().getString(R.string.please_draw_radius),
								Toast.LENGTH_LONG).show();
					}
//					operateState = ANALYSIS;
					return true;
				}
				return false;
			}
		});
		view.findViewById(R.id.reset).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					iv_reset.setImageResource(R.drawable.reset_blue);
					tv_reset.setTextColor(Color.BLUE);
					return true;
				case MotionEvent.ACTION_UP:
					iv_reset.setImageResource(R.drawable.reset);
					tv_reset.setTextColor(Color.WHITE);
					if (mannulAnalysisFragment.imageView.getDrawLine()) {
						mannulAnalysisFragment.imageView.setDrawLine(true);
						mannulAnalysisFragment.imageView.setDrawStart(true);
					}
//					operateState = RESET;
					return true;
				}
				return false;
			}
		});

		iv_zoom = (ImageView) view.findViewById(R.id.iv_zoom);
		tv_zoom = (TextView) view.findViewById(R.id.tv_zoom);
		
		iv_ana = (ImageView) view.findViewById(R.id.iv_ana);
		tv_ana = (TextView) view.findViewById(R.id.tv_ana);
		
		iv_reset = (ImageView) view.findViewById(R.id.iv_reset);
		tv_reset = (TextView) view.findViewById(R.id.tv_reset);
		
		operateState = ZOOM;
		iv_zoom.setImageResource(R.drawable.ic_zoom_blue);
		tv_zoom.setTextColor(Color.BLUE);
	}


	private OnClickListener BottomBarClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			changeBottomHightlight(operateState, id);

		}
	};

	private void changeBottomHightlight(int state, int id) {

		// 取消高亮
		switch (state) {
		case ZOOM:
			iv_zoom.setImageResource(R.drawable.ic_zoom);
			tv_zoom.setTextColor(Color.WHITE);
			break;
		case LINE:
			iv_line.setImageResource(R.drawable.ic_line);
			tv_line.setTextColor(Color.WHITE);
			break;
		case CIRCLE:
			iv_circle.setImageResource(R.drawable.ic_skin_circle);
			tv_circle.setTextColor(Color.WHITE);
			break;
		case NUMBER:
			iv_number.setImageResource(R.drawable.ic_skin_number);
			tv_number.setTextColor(Color.WHITE);
			break;

		case SIZE:
			iv_size.setImageResource(R.drawable.ic_skin_size);
			tv_size.setTextColor(Color.WHITE);
			break;
		default:
			break;
		}

		// 高亮
		switch (id) {
		case R.id.zoom:
			operateState = ZOOM;
			iv_zoom.setImageResource(R.drawable.ic_zoom_blue);
			tv_zoom.setTextColor(Color.BLUE);
			mannulAnalysisFragment.imageView.setDrawLine(false);
			break;
		case R.id.line:
			iv_line.setImageResource(R.drawable.ic_line_blue);
			tv_line.setTextColor(Color.BLUE);
			operateState = LINE;
			mannulAnalysisFragment.imageView.setDrawLine(true);
			mannulAnalysisFragment.imageView.setDrawStart(true);
			break;
		
		case R.id.circle:
			iv_circle.setImageResource(R.drawable.ic_skin_circle_blue);
			tv_circle.setTextColor(Color.BLUE);
			operateState = CIRCLE;
			mannulAnalysisFragment.imageView.setDrawLine(true);
			mannulAnalysisFragment.imageView.setDrawStart(true);
			break;
		case R.id.number:
			iv_number.setImageResource(R.drawable.ic_skin_number_blue);
			tv_number.setTextColor(Color.BLUE);
			operateState = NUMBER;

			mannulAnalysisFragment.imageView.setDrawLine(true);
			mannulAnalysisFragment.imageView.setDrawStart(false);
			mannulAnalysisFragment.imageView
					.setAnalysisMode(ANALYSIS_MODE_SENSI_NUM);
			break;
		case R.id.size:
			iv_size.setImageResource(R.drawable.ic_skin_size_blue);
			tv_size.setTextColor(Color.BLUE);
			operateState = SIZE;
			mannulAnalysisFragment.imageView.setDrawLine(true);
			mannulAnalysisFragment.imageView.setDrawStart(true);
			mannulAnalysisFragment.imageView
					.setAnalysisMode(ANALYSIS_MODE_SENSI_AREA);
			break;
		default:
			break;
		}

	}

	/**
	 * 高亮当前menu项
	 * 
	 * @param id
	 */
	private void setMenuBg(int index) {
		for (int i = 0; i < 8; i++) {
			menus[i].setBackgroundColor(Color.TRANSPARENT);
		}
		menus[index].setBackgroundColor(Color.BLUE);
	}

	public interface PixelAnalysis {
		public void onPixelAnalysis(int mode);
	}

	private PixelAnalysis mPixelAnalysis;

	private void setPixelAnalysis(PixelAnalysis p) {
		mPixelAnalysis = p;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(mannulAnalysisFragment != null && analysisClass == MANNUL_ANALYSIS){
		return mannulAnalysisFragment.imageView.getMultiTouchController()
				.onTouchEvent(event);
		}else{
			return super.onTouchEvent(event);
		}
	}
}
