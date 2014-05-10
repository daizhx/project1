package com.hengxuan.ehealthplatform.lens.iris;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.utils.DPIUtils;
import com.hengxuan.ehealthplatform.log.Log;

public class IrisAnalysisActivity extends BaseActivity {
	private static final String TAG = "IrisAnalysisActivity";
	// photo image path
	private String imagePath;
	private int iris_index;
	private String[] iris_image_paths = { null };
	private IrisImageView irisImageView;
	private int displayHeight;
	private LinearLayout linearL;
	private View getIrisView;
	private PointF centerPoint;
	private float getStandardR;
	private float getStandardMidR;
	private float getStandardMinR;
	//左右眼是否合并操作完成的数据
	private boolean irisMergedTag[] = null;
	//合并按钮
	private ImageView ivMergeBtn;

	private void initView() {
		irisImageView = (IrisImageView) findViewById(R.id.irisMyView);
		// initial the photo image
		irisImageView.initObjList(iris_image_paths, iris_index);

		// 开始执行CanvasIris.java类，不断更新所画的标尺，直至第三环画好结束
		getIrisView = getStandardView();
		linearL = (LinearLayout) findViewById(R.id.irisll);
		linearL.addView(getIrisView);
		
		ivMergeBtn = (ImageView)findViewById(R.id.merge_btn);
		ivMergeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getStandardR = ((CanvasIris) getIrisView).getMaxR();
				getStandardMinR = ((CanvasIris) getIrisView).getMinR();
				getStandardMidR = ((CanvasIris) getIrisView).getMidR();
				if(0 != getStandardMinR && 0 != getStandardMidR && 0 != getStandardR){
					mergeIrisView();
				}
				
			}
		});
	}

	public View getStandardView() {
		float center_x = getWindowManager().getDefaultDisplay().getWidth();
		float center_y = getWindowManager().getDefaultDisplay().getHeight();
		center_x = center_x / 2;
		center_y = center_y / 2;
		View irisView = new CanvasIris(IrisAnalysisActivity.this, iris_index,
				center_x, center_y);
		return irisView;
	}

	private void initImage() {
		ViewTreeObserver vto = irisImageView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				irisImageView.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				if (Log.D) {
					Log.d(TAG, "zoom in height = " + irisImageView.getHeight());
				}
			}

		});
		vto.addOnPreDrawListener(new OnPreDrawListener() {

			private int irisViewWidth;
			private int irisViewHeight;

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub

				if (Log.D) {
					Log.d(TAG,
							"set iris view de width and height----------------------------");
				}
				irisViewWidth = irisImageView.getMeasuredWidth();
				irisViewHeight = irisImageView.getMeasuredHeight();
				if (Log.D) {
					Log.d(TAG, "irisView--width=" + irisViewWidth);
					Log.d(TAG, "irisView--height=" + irisViewHeight);
				}

				irisImageView.resetGalleryWidth(getDisplayHeight()
						- irisViewHeight);
				return true;

			}
		});
		// set default display
		DPIUtils.setDefaultDisplay(super.getWindowManager().getDefaultDisplay());
		DPIUtils.setDensity(super.getResources().getDisplayMetrics().density);
	}

	public int getDisplayHeight() {
		if (displayHeight == -1) {
			DisplayMetrics metrics = super.getResources().getDisplayMetrics();
			if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayHeight = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			} else {
				displayHeight = Math.max(metrics.widthPixels,
						metrics.heightPixels);
			}
		}
		return displayHeight;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		imagePath = bundle.getString("image_path");
		iris_index = bundle.getInt("iris_image_index");
		iris_image_paths[0] = imagePath;

		setContentView(R.layout.actvity_iris_analysis);
		initView();

	}

	// 合并网格到虹膜照片中
	private void mergeIrisView() {
		centerPoint = ((CanvasIris) getIrisView).getCenter();
		if (0 != getStandardMinR && 0 != getStandardMidR && 0 != getStandardR) {
			ProgressDialog proDialog = ProgressDialog.show(
					IrisAnalysisActivity.this,
					getResources().getString(R.string.merge_image),
					getResources().getString(R.string.merge_wait));
			new IrisImageHandlerThread(proDialog, 2, getIrisView,
					centerPoint, getStandardR, getStandardMinR, getStandardMidR)
					.start();
			linearL.removeAllViews();
			getIrisView = null;
			linearL = null;
		}

	}
	
	private class IrisImageHandlerThread extends Thread {
		private ProgressDialog progressDlg = null;
		private int index;
		private View getCurrentView;
		private PointF getCenterP = new PointF();
		private float getStandardR, getStandardMinR, getStandardMidR;

		public IrisImageHandlerThread(ProgressDialog progressDialog, int index,
				View view, PointF getCenterP, float getStandardR,
				float getStandardMinR, float getStandardMidR) {
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
			// 处理图片合并的问题
			irisImageView.setTouchEventHandler(index,
					iris_index, getCurrentView,
					getCenterP, getStandardR, getStandardMinR, getStandardMidR);
			if (index == 2){
				irisImageView.isCompleted(true);
			}
			// 设置合并的标志
			irisMergedTag[iris_index] = true;
			// 初始化虹膜相关的信息
			IrisDataCache.getInstance().initIrisDataByIndex(iris_index);
			// 删除进度提示框
			this.progressDlg.dismiss();
		}

	}
	
	
}
