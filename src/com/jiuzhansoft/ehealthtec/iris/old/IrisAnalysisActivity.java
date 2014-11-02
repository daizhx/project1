package com.jiuzhansoft.ehealthtec.iris.old;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.utils.DPIUtils;
import com.jiuzhansoft.ehealthtec.lens.LensConstant;
import com.jiuzhansoft.ehealthtec.lens.iris.CanvasIris;
import com.jiuzhansoft.ehealthtec.lens.iris.IrisDataCache;
import com.jiuzhansoft.ehealthtec.log.Log;

public class IrisAnalysisActivity extends BaseActivity {
	
	private static final String TAG = "MainActivity";
	
	private ImageButton zoomIn = null;
	private ImageButton zoomOut = null;
	private View devideLine = null;
	private View.OnClickListener zoomOnClickListener = null;
	private View.OnClickListener ObjSelectorOnClickListener = null;
	private View.OnClickListener clrSelectorOnClickListener = null;
	private View myIrisView = null;
//	RelativeLayout iris_title = null;
	//private Spinner object_select = null;
	private Button object_select = null;
//	private Button return_btn = null;
	private int displayHeight;
	private int irisViewWidth = 0;
	private int irisViewHeight = 0;
	private DisplayMetrics metrics;
	private int mpaddingLeft = 5;
	private int popWidth = 220;
	private View MenuView  = null;
	private PopupWindow pop;
	private Handler handler = new Handler();
	private int radioBtnSelectedId = R.id.iris_image;
	private boolean isHandleEnd = false;
	private int iris_index = 0;
	private String[] iris_image_paths;
	private RadioGroup iris_image_tab_selector = null;
	//左右眼是否合并操作完成的数据
	private boolean irisMergedTag[] = null;
	
	// 点击标尺按钮后获得当前中心点
	public PointF centerPoint = new PointF();
	// 点击标尺后获得当前最大的半径
	public float getStandardR, getStandardMinR, getStandardMidR;
	
	private LinearLayout linearL;
	// CanvasIris的视图
	private View getIrisView;
	
	private boolean isClick = false;
	
	//用于标识是左眼还是右眼
	public static final int LEFT_IRIS_EYE_ID = 0;
	public static final int RIGHT_IRIS_EYE_ID = 1;

	public IrisAnalysisActivity(){
		this.displayHeight = -1;
		this.zoomOnClickListener = new ZoomOnClickListenerImpl();
		this.ObjSelectorOnClickListener = new ObjectSlectionClickListener();
		this.clrSelectorOnClickListener = new ColorSlectionClickListener();
		this.irisMergedTag = new boolean[2];
		//初始化为false
		this.irisMergedTag[0] = false;
		this.irisMergedTag[1] = false;
		
	}
    @Override
    public void onCreate(Bundle bundle) {
//    	super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(bundle);
        this.metrics = new DisplayMetrics();
        super.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setContentView(R.layout.iris_analysis);
        this.iris_index = super.getIntent().getExtras().getInt("irisIndex");
        SharedPreferences preferences = getSharedPreferences("getIndex", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentIndex", this.iris_index);
        editor.commit();
//		this.iris_image_paths = super.getIntent().getExtras().getStringArray("iris_pic_paths");
        String imagePath = getIntent().getStringExtra(LensConstant.PHOTO_PATH);
        this.iris_image_paths = new String[2];
        this.iris_image_paths[iris_index] = imagePath;
        
        this.initView();
        this.initImage();
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        this.iris_title.measure(w, h);
//        int width =this.iris_title.getMeasuredWidth();
//        int height =this.iris_title.getMeasuredHeight();
//        if(Log.I){
//        	Log.i(TAG, "----iris width=" + width + ",height=" + height);
//        }
        ViewTreeObserver observer =  this.myIrisView.getViewTreeObserver();
        observer.addOnPreDrawListener(new OnPreDrawListener(){

			@Override
			public boolean onPreDraw() {
				if(Log.D){
					Log.d(IrisAnalysisActivity.TAG,
							"set iris view de width and height----------------------------");
				}
				IrisAnalysisActivity.this.irisViewWidth = IrisAnalysisActivity.this.myIrisView.getMeasuredWidth();
				IrisAnalysisActivity.this.irisViewHeight = IrisAnalysisActivity.this.myIrisView.getMeasuredHeight();
				if(Log.D){
					Log.d(IrisAnalysisActivity.TAG, "irisView--width="
							+ IrisAnalysisActivity.this.irisViewWidth);
					Log.d(IrisAnalysisActivity.TAG, "irisView--height="
							+ IrisAnalysisActivity.this.irisViewHeight);
				}
				
				if(IrisAnalysisActivity.this.myIrisView instanceof IrisImageView){
					((IrisImageView)(IrisAnalysisActivity.this.myIrisView)).resetGalleryWidth(IrisAnalysisActivity.this.getDisplayHeight() - IrisAnalysisActivity.this.irisViewHeight);
				}
				return true;
			}
        	
        });
        //set default display
        DPIUtils.setDefaultDisplay(super.getWindowManager().getDefaultDisplay());
        DPIUtils.setDensity(super.getResources().getDisplayMetrics().density);
    }
    
    

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		super.onWindowFocusChanged(hasFocus);
		Rect frame = new Rect();
		super.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		if(Log.D){
			Log.d(TAG, "statusBarHeight = " + statusBarHeight);
		}
		int currentTop = super.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = currentTop - statusBarHeight;
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
    public int getDisplayHeight() {
    	if(this.displayHeight == -1){
			DisplayMetrics metrics = super.getResources().getDisplayMetrics();
			if(super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				this.displayHeight = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			}else{
				this.displayHeight = Math.max(metrics.widthPixels,
						metrics.heightPixels);
			}
		}
		return this.displayHeight;
	}
	public void setDisplayHeight(int displayHeight) {
		this.displayHeight = displayHeight;
	}
	
	private void initView(){
		this.myIrisView = (IrisImageView)findViewById(R.id.irisMyView);
		//初始化图片
		((IrisImageView) myIrisView).initObjList(this.iris_image_paths,this.iris_index);
		
//		this.iris_title = (RelativeLayout)super.findViewById(R.id.iris_title);
		this.zoomIn = (ImageButton)super.findViewById(R.id.image_zoom_in);
		this.devideLine = (View)super.findViewById(R.id.image_zoom_devide_line);
		this.devideLine.setBackgroundColor(Color.rgb(0, 255, 255));
		this.zoomOut = (ImageButton)super.findViewById(R.id.image_zoom_out);
		this.object_select = (Button)super.findViewById(R.id.object_select);
//		this.return_btn = (Button)super.findViewById(R.id.btn_back);
//		final RadioButton rightEyeBtn = (RadioButton)super.findViewById(R.id.btn_eye_right);
//		final RadioButton leftEyeBtn = (RadioButton)super.findViewById(R.id.btn_eye_left);
		
		this.iris_image_tab_selector = (RadioGroup)super.findViewById(R.id.iris_image_tab_selector);
		this.iris_image_tab_selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(Log.D){
					Log.d(IrisAnalysisActivity.TAG, "checked radio button id=" + checkedId);
				}
				//选项改变后，进行相关的处理
				if(null != getIrisView){
					linearL.removeAllViews();
					getIrisView = null;
					linearL = null;
				}
				((IrisImageView)myIrisView).removePop();
				switch(checkedId){
				case R.id.btn_eye_left:
					if(IrisAnalysisActivity.this.iris_index == RIGHT_IRIS_EYE_ID){
						((IrisImageView)myIrisView).isCompleted(((IrisImageView)myIrisView).isMergeCompleted(1));
						((IrisImageView)myIrisView).setColorId(0);
					}
					IrisAnalysisActivity.this.iris_index = LEFT_IRIS_EYE_ID;
//					leftEyeBtn.setBackgroundResource(R.drawable.android_big_button_pressed);
//					rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_normal);
					break;
				case R.id.btn_eye_right:
					if(IrisAnalysisActivity.this.iris_index == LEFT_IRIS_EYE_ID){
						((IrisImageView)myIrisView).isCompleted(((IrisImageView)myIrisView).isMergeCompleted(0));
						((IrisImageView)myIrisView).setColorId(0);
					}
					IrisAnalysisActivity.this.iris_index = RIGHT_IRIS_EYE_ID;
//					leftEyeBtn.setBackgroundResource(R.drawable.android_big_button_normal);
//					rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_pressed);
					break;
				}
				IrisAnalysisActivity.this.freshIrisImageArea(IrisAnalysisActivity.this.iris_index);
				((IrisImageView)myIrisView).setIndex(iris_index);
				zoomIn.setVisibility(View.VISIBLE);
				devideLine.setVisibility(View.VISIBLE);
				zoomOut.setVisibility(View.VISIBLE);
			}
		});
		//根据不同的值设置不同的高亮按钮
		if(this.iris_index == LEFT_IRIS_EYE_ID){
			this.iris_image_tab_selector.check(R.id.btn_eye_left);
//			leftEyeBtn.setBackgroundResource(R.drawable.android_big_button_pressed);
//			rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_normal);
			//判断右眼是否有图片传入
			if(this.iris_image_paths[RIGHT_IRIS_EYE_ID] == null){
//				rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_disable);
//				rightEyeBtn.setEnabled(false);
			}
		}else if(this.iris_index == RIGHT_IRIS_EYE_ID){
			this.iris_image_tab_selector.check(R.id.btn_eye_right);
//			leftEyeBtn.setBackgroundResource(R.drawable.android_big_button_normal);
//			rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_pressed);
			//判断右眼是否有图片传入
			if(this.iris_image_paths[LEFT_IRIS_EYE_ID] == null){
//				leftEyeBtn.setBackgroundResource(R.drawable.android_big_button_disable);
//				leftEyeBtn.setEnabled(false);
			}
		}else{
			this.iris_image_tab_selector.check(R.id.btn_eye_left);
//			leftEyeBtn.setBackgroundResource(R.drawable.android_big_button_pressed);
//			rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_normal);
			//判断右眼是否有图片传入
			if(this.iris_image_paths[RIGHT_IRIS_EYE_ID] == null){
//				rightEyeBtn.setBackgroundResource(R.drawable.android_big_button_disable);
//				rightEyeBtn.setEnabled(false);
			}
		}
	}
	
	private void freshIrisImageArea(int index){
		IrisImageView irisView = (IrisImageView)myIrisView;
		irisView.initObjList(this.iris_image_paths, this.iris_index);
		boolean isCompleted = irisView.isMergeCompleted(index);
		if(isCompleted){//如果已经合并完成
			this.isHandleEnd = true;
			this.radioBtnSelectedId = R.id.merge_image;
			this.object_select.setText(R.string.color);
			this.radioBtnSelectedId = -1;
			IrisAnalysisActivity.this.object_select
					.setOnClickListener(IrisAnalysisActivity.this.clrSelectorOnClickListener);
			
		}else{//没有合并完成
			this.isHandleEnd = false;
			this.radioBtnSelectedId = R.id.iris_image;
			this.object_select.setText(R.string.iris_editor);
			this.object_select.setOnClickListener(ObjSelectorOnClickListener);
		}
		irisView.invalidate();
	}
	
	private void initImage(){
		//this.zoomIn.setVisibility(View.VISIBLE);
		
		ViewTreeObserver vto = this.myIrisView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

			@Override
			public void onGlobalLayout() {
				IrisAnalysisActivity.this.myIrisView.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				if(Log.D){
					Log.d(IrisAnalysisActivity.TAG, "zoom in height = " + IrisAnalysisActivity.this.myIrisView.getHeight());
				}
			}
			
		});
		//设置显示虹膜图片的监听器
		
		this.zoomIn.setOnClickListener(this.zoomOnClickListener);
		//this.zoomOut.setVisibility(View.VISIBLE);
		this.zoomOut.setOnClickListener(this.zoomOnClickListener);
		this.object_select.setOnClickListener(this.ObjSelectorOnClickListener);
//		this.return_btn.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				IrisAnalysisActivity.this.finish();
//			}
//		});
	}
	
	public float getStandR(){
		return getStandardR;
	}
	
	public PointF getCenterPoint(){
		return centerPoint;
	}
	
	public Bitmap getBitmapFromLinearLayout(){
		Bitmap linearBmp = linearL.getDrawingCache();
		return linearBmp;
	}
	
	public View getStandardView(){
		float center_x = getWindowManager().getDefaultDisplay().getWidth();
		float center_y = getWindowManager().getDefaultDisplay().getHeight();
		center_x = center_x / 2;
		center_y = center_y / 2;
		View irisView = new CanvasIris(IrisAnalysisActivity.this, iris_index, center_x, center_y);
		return irisView;
	}
	
	public void initSettingLayout(final View view, int id) {
		if (id == R.id.object_select) {
			if (this.isHandleEnd == false) {//如果图片合并没有完成
				RadioGroup group = (RadioGroup) view
						.findViewById(R.id.edit_group);
				group.check(this.radioBtnSelectedId);
				group.setOnCheckedChangeListener(null);
				group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						int index = 0;
						switch (checkedId) {
						case R.id.iris_image:
							isClick = false;
							IrisAnalysisActivity.this.radioBtnSelectedId = R.id.iris_image;
							index = IrisImageView.OBJECT_CONTENT_INDEX;
							if(null != getIrisView){
								linearL.removeAllViews();
								getIrisView = null;
								linearL = null;
							}
							zoomIn.setVisibility(View.VISIBLE);
							devideLine.setVisibility(View.VISIBLE);
							zoomOut.setVisibility(View.VISIBLE);
							break;
						case R.id.standrad_image:
							isClick = true;
							IrisAnalysisActivity.this.radioBtnSelectedId = R.id.standrad_image;
							index = IrisImageView.OBJECT_MODELE_INDEX;
							zoomIn.setVisibility(View.INVISIBLE);
							devideLine.setVisibility(View.INVISIBLE);
							zoomOut.setVisibility(View.INVISIBLE);
							break;
						case R.id.merge_image:
							if(isClick){
								getStandardR = ((CanvasIris) getIrisView).getMaxR();
								getStandardMinR = ((CanvasIris) getIrisView).getMinR();
								getStandardMidR = ((CanvasIris) getIrisView).getMidR();
								if(0 != getStandardMinR && 0 != getStandardMidR && 0 != getStandardR){
									IrisAnalysisActivity.this.radioBtnSelectedId = R.id.merge_image;
									IrisAnalysisActivity.this.object_select.setText(R.string.color);
									IrisAnalysisActivity.this.radioBtnSelectedId = -1;
									IrisAnalysisActivity.this.isHandleEnd = true;
									IrisAnalysisActivity.this.object_select
									.setOnClickListener(IrisAnalysisActivity.this.clrSelectorOnClickListener);
									index = IrisImageView.OBJECT_COMPONENT_INDEX;									
								}
							}
							zoomIn.setVisibility(View.VISIBLE);
							devideLine.setVisibility(View.VISIBLE);
							zoomOut.setVisibility(View.VISIBLE);
							break;
						}
						
						if(checkedId == R.id.standrad_image){
							/*if(null != getIrisView){
								linearL.removeAllViews();
								getIrisView = null;
							}*/
							// 开始执行CanvasIris.java类，不断更新所画的标尺，直至第三环画好结束
							
							getIrisView = getStandardView();
							linearL = (LinearLayout) findViewById(R.id.irisll);
							linearL.addView(getIrisView);
							
						}//else if (checkedId != R.id.merge_image) {
						if(checkedId == R.id.iris_image){
							((IrisImageView) (IrisAnalysisActivity.this.myIrisView))
									.setTouchEventHandler(index,IrisAnalysisActivity.this.iris_index, getIrisView, centerPoint, getStandardR, getStandardMinR, getStandardMidR);
						} else if(checkedId == R.id.merge_image){
							if(!isClick){
								Toast.makeText(IrisAnalysisActivity.this, 
										getResources().getString(R.string.isclick), Toast.LENGTH_SHORT).show();
							}else{
								// 开启线程进行处理
								centerPoint = ((CanvasIris) getIrisView).getCenter();
								if(0 != getStandardMinR && 0 != getStandardMidR && 0 != getStandardR){
									ProgressDialog proDialog = ProgressDialog.show(
											IrisAnalysisActivity.this, getResources().getString(R.string.merge_image), 
											getResources().getString(R.string.merge_wait));
									new IrisImageHandlerThread(proDialog, index, getIrisView, centerPoint, getStandardR, getStandardMinR, getStandardMidR)
									.start();
									//linearL.setVisibility(View.GONE);	
									zoomIn.setVisibility(View.VISIBLE);
									devideLine.setVisibility(View.VISIBLE);
									zoomOut.setVisibility(View.VISIBLE);
									linearL.removeAllViews();
									getIrisView = null;
									linearL = null;
								}else{
									Toast.makeText(IrisAnalysisActivity.this, 
											getResources().getString(R.string.isclick), Toast.LENGTH_SHORT).show();
								}
							}
						}

					}
				});
				RadioBtnOnClickListener listener = new RadioBtnOnClickListener();
				RadioButton iris_image = (RadioButton) view
						.findViewById(R.id.iris_image);
				iris_image.setOnClickListener(listener);
				RadioButton standrad_image = (RadioButton) view
						.findViewById(R.id.standrad_image);
				standrad_image.setOnClickListener(listener);
				RadioButton merge_image = (RadioButton) view
						.findViewById(R.id.merge_image);
				merge_image.setOnClickListener(listener);
			} else {//如果图片合并完成--处理颜色相关的信息
				RadioGroup group = (RadioGroup) view
						.findViewById(R.id.color_selections);
				group.check(this.radioBtnSelectedId);
				group.setOnCheckedChangeListener(null);
				group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.color_light:
							((IrisImageView)myIrisView).setColorId(1);
							IrisAnalysisActivity.this.radioBtnSelectedId = R.id.color_light;
							Toast.makeText(IrisAnalysisActivity.this, getResources().getString(R.string.color_light), Toast.LENGTH_SHORT).show();
							break;
						case R.id.color_dark_brown:
							((IrisImageView)myIrisView).setColorId(2);
							IrisAnalysisActivity.this.radioBtnSelectedId = R.id.color_dark_brown;
							Toast.makeText(IrisAnalysisActivity.this, getResources().getString(R.string.color_dark_brown), Toast.LENGTH_SHORT).show();
							break;
						case R.id.color_dead_brown:
							((IrisImageView)myIrisView).setColorId(3);
							IrisAnalysisActivity.this.radioBtnSelectedId = R.id.color_dead_brown;
							Toast.makeText(IrisAnalysisActivity.this, getResources().getString(R.string.color_dead_brown), Toast.LENGTH_SHORT).show();
							break;
						case R.id.color_deeply_black:
							((IrisImageView)myIrisView).setColorId(4);
							IrisAnalysisActivity.this.radioBtnSelectedId = R.id.color_deeply_black;
							Toast.makeText(IrisAnalysisActivity.this, getResources().getString(R.string.color_deeply_black), Toast.LENGTH_SHORT).show();
							break;
						}

					}
				});
				RadioBtnOnClickListener listener = new RadioBtnOnClickListener();
				RadioButton color_light = (RadioButton) view
						.findViewById(R.id.color_light);
				color_light.setOnClickListener(listener);
				RadioButton color_dark_brown = (RadioButton) view
						.findViewById(R.id.color_dark_brown);
				color_dark_brown.setOnClickListener(listener);
				RadioButton color_dead_brown = (RadioButton) view
						.findViewById(R.id.color_dead_brown);
				color_dead_brown.setOnClickListener(listener);
				
				RadioButton color_deeply_black = (RadioButton) view
						.findViewById(R.id.color_deeply_black);
				color_deeply_black.setOnClickListener(listener);
			}

		}
	}
	
	public void PopSettingMenu(View menuview,View view){
		if(pop == null){
			pop = new PopupWindow(menuview, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true);
			//pop.setBackgroundDrawable(new ColorDrawable(Color.GREEN));
			// pop.setBackgroundDrawable(new ColorDrawable(0x7700FF00));
			pop.setAnimationStyle(R.style.skan_pop);
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
				//pop.setBackgroundDrawable(new ColorDrawable(Color.MAGENTA));
				// pop.setBackgroundDrawable(new ColorDrawable(0x7700FF00));
				pop.setAnimationStyle(R.style.skan_pop);
				pop.setOutsideTouchable(true);
				pop.showAsDropDown(view, Gravity.CENTER_HORIZONTAL, 0);
				pop.update();
			}
		}
	}

	/**
     * CLASS OnClickListenerImpl
     * @author JERRY
     *
     */
	private class ZoomOnClickListenerImpl implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			//放小被按下
			case R.id.image_zoom_out:
				((IrisImageView)IrisAnalysisActivity.this.myIrisView).zoomOut();
				break;
			//中间分割线被按下
			case R.id.image_zoom_devide_line:
				break;
			//放大按钮被按下
			case R.id.image_zoom_in:
				((IrisImageView)IrisAnalysisActivity.this.myIrisView).zoomIn();
				break;
			}
		}
		
	}
	
	
	private class RadioBtnOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			IrisAnalysisActivity.this.handler.post(new Runnable() {

				@Override
				public void run() {
					IrisAnalysisActivity.this.pop.dismiss();
				}

			});
		}

	}
	
	private class IrisImageHandlerThread extends Thread {
		private ProgressDialog progressDlg = null;
		private int index;
		private View getCurrentView;
		private PointF getCenterP = new PointF();
		private float getStandardR, getStandardMinR, getStandardMidR;

		public IrisImageHandlerThread(ProgressDialog progressDialog,int index, View view, 
				PointF getCenterP, float getStandardR, float getStandardMinR, float getStandardMidR){
			this.progressDlg = progressDialog;
			this.index = index;
			getCurrentView = view;
			this.getCenterP = getCenterP;
			this.getStandardR = getStandardR;
			this.getStandardMinR = getStandardMinR;
			this.getStandardMidR = getStandardMidR;
		}
		@Override
		public void run() {
			//处理图片合并的问题
			IrisImageView view = (IrisImageView)IrisAnalysisActivity.this.myIrisView;
			view.setTouchEventHandler(index,IrisAnalysisActivity.this.iris_index, getCurrentView, getCenterP, getStandardR, getStandardMinR, getStandardMidR);
			if(index == 2)
				((IrisImageView)myIrisView).isCompleted(true);
			//设置合并的标志
			IrisAnalysisActivity.this.irisMergedTag[IrisAnalysisActivity.this.iris_index] = true;
			//初始化虹膜相关的信息
			IrisDataCache.getInstance().initIrisDataByIndex(IrisAnalysisActivity.this.iris_index);
			//删除进度提示框
			this.progressDlg.dismiss();
			
		}
		
	}

	@Override
	protected void onDestroy() {
		if(Log.D){
			Log.d(TAG, "onDestroy");
		}
		super.onDestroy();
	}
	@Override
	protected void onStop() {
		if(Log.D){
			Log.d(TAG, "onStop");
		}
		super.onStop();
	}
	
	private class ObjectSlectionClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			//显示popup选择框
			int mviewpos[] = new int[2];
			v.getLocationOnScreen(mviewpos);
			if (!(mviewpos[0] + popWidth < metrics.widthPixels)) {
				mpaddingLeft = mviewpos[0]
						- (metrics.widthPixels - popWidth);
			}
			MenuView = getLayoutInflater().inflate(R.layout.iris_image_selector, null);
			initSettingLayout(MenuView,R.id.object_select);
			PopSettingMenu(MenuView,v);
		}
		
	}
	
	private class ColorSlectionClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			//显示popup选择框
			int mviewpos[] = new int[2];
			v.getLocationOnScreen(mviewpos);
			if (!(mviewpos[0] + popWidth < metrics.widthPixels)) {
				mpaddingLeft = mviewpos[0]
						- (metrics.widthPixels - popWidth);
			}
			
			
			
			MenuView = getLayoutInflater().inflate(R.layout.color_selector, null);
			initSettingLayout(MenuView,R.id.object_select);
			PopSettingMenu(MenuView,v);
		}
		
	}
    
}