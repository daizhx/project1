package com.jiuzhansoft.ehealthtec.activity;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupSetting;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 * 
 */
public class BaseActivity extends FragmentActivity {
	public TextView mTitle;
	public ImageView leftIcon;
	public ImageView rightIcon;
	private SharedPreferences mSharedPreferences;
	private ArrayList<DestroyListener> destroyListenerList = new ArrayList<DestroyListener>();
	WindowManager mWindowManager;
	WindowManager.LayoutParams wmParams;
	private int mDisplayWidth;
	private int mDisplayHeight;
	LayoutInflater inflater;
	ImageView mFloatView;
	LinearLayout mFloatLayout;
	// switch of float view
	private boolean openFloatView = true;

	public interface DestroyListener {
		public abstract void onDestroy();
	}

	public interface SetDestroyListener {
		public void setDestroyListener();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//customize action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(view, lp);
		mTitle = (TextView) view.findViewById(R.id.title);
		leftIcon = (ImageView) view.findViewById(R.id.left_icon);
		leftIcon.setImageResource(R.drawable.ic_action_back);
		leftIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});
		rightIcon = (ImageView) view.findViewById(R.id.right_icon);
		rightIcon.setVisibility(View.VISIBLE);
		rightIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(BaseActivity.this, MainActivity.class));
				// 清除activity stack
			}
		});
		setContentView(R.layout.activity_base);
		handler = new Handler();
	}
	
	public void setContent(int layoutId){
		FrameLayout content = (FrameLayout)findViewById(R.id.content);
		getLayoutInflater().inflate(layoutId, content);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mDisplayWidth = getWindowManager().getDefaultDisplay().getWidth();
		mDisplayHeight = getWindowManager().getDefaultDisplay().getHeight();

		if (openFloatView) {
			inflater = this.getLayoutInflater();// LayoutInflater.from(getApplication());

			mFloatLayout = (LinearLayout) inflater.inflate(
					R.layout.float_layout, null);
			mFloatLayout.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			int w = mFloatLayout.getMeasuredWidth();
			int h = mFloatLayout.getMeasuredHeight();

			// 添加浮动导航按钮
			// 获取的是LocalWindowManager对象
//			mWindowManager = this.getWindowManager();
//			mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
			// 获取的是CompatModeWrapper对象,用上面的方式获取的wm的话，在改界面弹出的AlertDialog不显示
			 mWindowManager = (WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
			wmParams = new WindowManager.LayoutParams();

			wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
			wmParams.format = PixelFormat.RGBA_8888;
			;
			wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			wmParams.gravity = Gravity.LEFT | Gravity.TOP;

			wmParams.x = mDisplayWidth - w / 2;
			wmParams.y = mDisplayHeight - h / 2;
			wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
			wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

			mWindowManager.addView(mFloatLayout, wmParams);
			mFloatView = (ImageView) mFloatLayout.findViewById(R.id.float_id);
			// 绑定触摸移动监听
			mFloatView.setOnTouchListener(new OnTouchListener() {
				float prevX, prevY;
				boolean flag = false;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN:
						prevX = event.getRawX();
						prevY = event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						wmParams.x = (int) event.getRawX()
								- mFloatLayout.getWidth() / 2;
						// 25为状态栏高度
						wmParams.y = (int) event.getRawY()
								- mFloatLayout.getHeight() / 2;
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);

						break;

					case MotionEvent.ACTION_UP:
						float x = event.getRawX();
						float y = event.getRawY();
						Log.d("daizhx", "x=" + x + ",y=" + y
								+ ",mDisplayWidth=" + mDisplayWidth);
						if (x > mDisplayWidth / 2) {
							wmParams.x = mDisplayWidth
									- mFloatLayout.getWidth() / 2;
						} else {
							wmParams.x = 0;
						}
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						if (FloatMath.sqrt((x - prevX) * (x - prevX)
								+ (y - prevY) * (y - prevY)) < 10f) {
							if (!flag) {
								showFloatWindow();
								flag = true;
							} else {
								ppw.dismiss();
								flag = false;
							}
						}
						break;
					default:
						break;
					}
					// TODO Auto-generated method stub

					return true;
				}
			});
		}
	}
	
	protected void openFloatView(boolean b){
		openFloatView = b;
	}

	PopupWindow ppw;

	private void showFloatWindow() {
		View view = inflater.inflate(R.layout.popup_navigation, null);

		// view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
		// view.setLayoutParams(new LayoutParams(300, 300));
		// view.measure(View.MeasureSpec.UNSPECIFIED,
		// View.MeasureSpec.UNSPECIFIED);
		ppw = new PopupWindow(view, (int) (mDisplayWidth * 0.8),
				(int) (mDisplayWidth * 0.8));
		ppw.setFocusable(true);
		ppw.setTouchable(true);
		ppw.setBackgroundDrawable(getApplication().getResources().getDrawable(
				android.R.color.transparent));
		ppw.setOutsideTouchable(true);
		ppw.update();
		ppw.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
		view.findViewById(R.id.top).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(BaseActivity.this,
						MassageActivity.class));
				ppw.dismiss();
			}
		});
		view.findViewById(R.id.left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(BaseActivity.this,
						PhysicalExamActivity.class));
				ppw.dismiss();
			}
		});
		view.findViewById(R.id.bottom).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						startActivity(new Intent(BaseActivity.this,
								MainActivity.class));
						ppw.dismiss();
					}
				});
		view.findViewById(R.id.right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(BaseActivity.this, HealthClub.class));
				ppw.dismiss();
			}
		});
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mFloatLayout != null && mFloatLayout.isShown()) {
			mWindowManager.removeView(mFloatLayout);
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mFloatLayout != null && mFloatLayout.isShown()) {
			mWindowManager.removeView(mFloatLayout);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mFloatLayout != null && mFloatLayout.isShown()) {
			mWindowManager.removeView(mFloatLayout);
		}
	}

	public void setTitle(int resId) {
		mTitle.setText(resId);
	}
	
	public void setTitle(String str){
		mTitle.setText(str);
	}

	public void setRightIcon(int resId) {
		rightIcon.setImageResource(resId);
	}

	public void setRightIcon(int resId, OnClickListener l) {
		rightIcon.setImageResource(resId);
		rightIcon.setOnClickListener(l);
	}

	public void setRightIconClickListener(OnClickListener l) {
		rightIcon.setOnClickListener(l);
	}

	public void setLeftIcon(int resId) {
		leftIcon.setImageResource(resId);
	}

	public void setLeftIconClickListener(OnClickListener l) {
		leftIcon.setOnClickListener(l);
	}

	public int getActionBarHeight() {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public static Bitmap readBitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		SoftReference<Bitmap> softreference = new SoftReference<Bitmap>(bitmap);
		return softreference.get();
	}

	public static Drawable readDrawable(Context context, int resId) {
		Bitmap bitmap = readBitmap(context, resId);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		SoftReference<BitmapDrawable> softreference = new SoftReference<BitmapDrawable>(
				bd);
		return softreference.get();
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	// called by decodeSampledBitmapFromResource
	private static int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		// TODO Auto-generated method stub
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = options.outHeight;
			final int halfWidth = options.outWidth;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	private Handler handler;

	public void post(Runnable action) {
		if (Log.D) {
			Log.d("MyActivity", "post(runable)");
		}

		final Runnable ar = action;

		handler.post(new Runnable() {

			@Override
			public void run() {
				if (!isFinishing())
					ar.run();
			}

		});
	}

	public void post(final Runnable action, int i) {
		if (Log.D) {
			Log.d("MyActivity", "post(runable : " + String.valueOf(i) + " )");
		}

		long l = i;
		final Runnable ar = action;
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!isFinishing())
					ar.run();
			}

		}, l);
	}

	public void onShowModal() {
	}

	public void onHideModal() {
	}

	public void addDestroyListener(DestroyListener listener) {
		if (this.destroyListenerList == null) {
			return;
		}
		destroyListenerList.add(listener);
	}

  
	
	public String getStringFromPreference(String key) {
		String ret = null;
		if (mSharedPreferences == null) {
			mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME,
					Context.MODE_PRIVATE);
		}
		ret = mSharedPreferences.getString(key, null);
		return ret;
	}

	public void putString2Preference(String key, String value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME,
					Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putString(key, value).commit();
	}

	public boolean getBooleanFromPreference(String key) {
		boolean ret = false;
		if (mSharedPreferences == null) {
			mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME,
					Context.MODE_PRIVATE);
		}
		ret = mSharedPreferences.getBoolean(key, false);
		return ret;
	}

	public void putBoolean2Preference(String key, boolean value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME,
					Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public void putInt2Preference(String key, int value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME,
					Context.MODE_PRIVATE);
		}
		mSharedPreferences.edit().putInt(key, value).commit();
	}

	public int getIntFromPreference(String key) {
		int ret = -1;
		if (mSharedPreferences == null) {
			mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME,
					Context.MODE_PRIVATE);
		}
		ret = mSharedPreferences.getInt(key, -1);
		return ret;
	}
}
