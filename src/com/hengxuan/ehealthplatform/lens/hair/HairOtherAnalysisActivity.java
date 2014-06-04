package com.hengxuan.ehealthplatform.lens.hair;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;

public class HairOtherAnalysisActivity extends BaseActivity {

	public static final int ANALYSIS_MODE_PORE = 5;
	public static final int ANALYSIS_MODE_ACNE = 6;
	public static final int ANALYSIS_MODE_SENSI_NUM = 7;
	public static final int ANALYSIS_MODE_SENSI_AREA = 8;
	private int analysisMode;
	private String picPath;
	private RelativeLayout imageContainer;
//	private Button oper;
	private HairTouchView imageView;
	private int screenHeight;
	private int screenWidth;
	private ArrayAdapter<String> adapter;
	private View menuView;
	private PopupWindow pop;
	private TextView textView;
	private int radioCheckId = R.id.pore_zoom;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.hair_detection);
		setRightIcon(R.drawable.ic_action_overflow, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pop != null && pop.isShowing()) {
					pop.dismiss();
					pop = null;
					return;
				}
				menuView = getLayoutInflater().inflate(
						R.layout.pore_oper_selector, null);
				initSettingLayout(menuView);
				PopSettingMenu(menuView, v);
			}
		});
		setLeftIcon(R.drawable.ic_action_back);
		setContentView(R.layout.activity_hair_other_analysis);
		picPath = getIntent().getExtras().getString("picPath");
		imageContainer = (RelativeLayout) findViewById(R.id.container);
		

		textView = (TextView) findViewById(R.id.other_data);
		imageView = new HairTouchView(this);
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
		imageView.init(HairOtherAnalysisActivity.this, bitmap,
				screenHeight / 20);
	}

	public void initSettingLayout(final View view) {

		RadioGroup group1 = (RadioGroup) view.findViewById(R.id.pore_oper);
		group1.check(radioCheckId);
		group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.pore_zoom:
					radioCheckId = R.id.pore_zoom;
					imageView.setDrawLine(false);
					break;
				case R.id.pore_draw:
					radioCheckId = R.id.pore_draw;
					imageView.setDrawLine(true);
					imageView.setDrawStart(true);
					break;
				case R.id.pore_analysis:
					if (imageView.canAnalysis()) {
						Intent intent = new Intent(
								HairOtherAnalysisActivity.this,
								HairAnalysisResultActivity.class);
						intent.putExtra("mode", HairView.HAIR_DETECTION);
						intent.putExtra("content", imageView.getPoreRadius());
						startActivity(intent);
					} else {
						Toast.makeText(
								HairOtherAnalysisActivity.this,
								getResources().getString(
										R.string.please_draw_radius),
								Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.pore_reset:
					if (imageView.getDrawLine()) {
						imageView.setDrawLine(true);
						imageView.setDrawStart(true);
					}
					break;
				}
				if (pop != null)
					pop.dismiss();
			}
		});
	}

	public void PopSettingMenu(View menuview, View view) {
		if (pop == null) {
			pop = new PopupWindow(menuview, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			pop.setAnimationStyle(R.style.skan_pop);
			pop.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
			pop.setOutsideTouchable(true);
			pop.showAsDropDown(view, Gravity.LEFT, 0);
			pop.update();
		} else {
			if (pop.isShowing()) {
				pop.dismiss();
				pop = null;
			} else {
				pop = null;
				pop = new PopupWindow(menuview, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, true);
				pop.setAnimationStyle(R.style.skan_pop);
				pop.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
				pop.setOutsideTouchable(true);
				pop.showAsDropDown(view, Gravity.LEFT, 0);
				pop.update();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// return super.onTouchEvent(event);
		return imageView.getMultiTouchController().onTouchEvent(event);
	}
	
	
}
