package com.hengxuan.ehealthplatform.activity;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.constant.PreferenceKeys;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpGroupSetting;
import com.hengxuan.ehealthplatform.http.HttpGroupaAsynPool;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import com.hengxuan.ehealthplatform.log.Log;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends Activity {
	public TextView mTitle;
	public ImageView leftIcon;
	public ImageView rightIcon;
	private SharedPreferences mSharedPreferences;
	
	private ArrayList<DestroyListener> destroyListenerList = new ArrayList<DestroyListener>();
	
	public interface DestroyListener {
		public abstract void onDestroy();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(view, lp);
		mTitle = (TextView)view.findViewById(R.id.title);
		leftIcon = (ImageView)view.findViewById(R.id.left_icon);
		leftIcon.setImageResource(R.drawable.back_icon);
		leftIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		rightIcon = (ImageView)view.findViewById(R.id.right_icon);
		rightIcon.setVisibility(View.VISIBLE);
		rightIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(BaseActivity.this, MainActivity.class));
				//Çå³ýactivity stack
			}
		});
		handler = new Handler();
	}
	
	public static Bitmap readBitmap(Context context, int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		SoftReference<Bitmap> softreference = new SoftReference<Bitmap>(bitmap);
		return softreference.get();
	}
	
	public static Drawable readDrawable(Context context, int resId){
		Bitmap bitmap = readBitmap(context, resId);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		SoftReference<BitmapDrawable> softreference = new SoftReference<BitmapDrawable>(bd);
		return softreference.get();
	}
	
	 public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);
			
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(res, resId, options);
	}
	 
	//called by decodeSampledBitmapFromResource
		private static int calculateInSampleSize(Options options, int reqWidth,
				int reqHeight) {
			// TODO Auto-generated method stub
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;
			
			if(height > reqHeight || width > reqWidth){
				final int halfHeight = options.outHeight;
				final int halfWidth = options.outWidth;
				
				while((halfHeight/inSampleSize) > reqHeight && (halfWidth/inSampleSize) > reqWidth){
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
		
		public void onShowModal()
		{
		}
		public void onHideModal()
		{
		}
		
		public void addDestroyListener(DestroyListener listener) {
			if (this.destroyListenerList == null) {
				return;
			}
			destroyListenerList.add(listener);
		}
		
		public HttpGroup getHttpGroupaAsynPool() {
			return getHttpGroupaAsynPool(ConstHttpProp.TYPE_JSON);
		}

		public HttpGroup getHttpGroupaAsynPool(int paramInt) {
			if (Log.D) {
				Log.d("MyActivity", "getHttpGroupaAsynPool");
			}
			
			HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
			localHttpGroupSetting.setMyActivity(this);
			localHttpGroupSetting.setType(paramInt);
			return getHttpGroupaAsynPool(localHttpGroupSetting);
		}
		public HttpGroup getHttpGroupaAsynPool(HttpGroupSetting paramHttpGroupSetting) {
			if (Log.D) {
				Log.d("MyActivity", "getHttpGroupaAsynPool");
			}
			
			HttpGroupaAsynPool localHttpGroupaAsynPool = new HttpGroupaAsynPool(paramHttpGroupSetting);
			addDestroyListener(localHttpGroupaAsynPool);
			return localHttpGroupaAsynPool;
		}
		
		public String getStringFromPreference(String key){
			String ret = null;
			if(mSharedPreferences == null){
				mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
			}
			ret = mSharedPreferences.getString(key, null);
			return ret;
		}
		public void putString2Preference(String key,String value){
			if(mSharedPreferences == null){
				mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
			}
			mSharedPreferences.edit().putString(key, value).commit();
		}
		public boolean getBooleanFromPreference(String key){
			boolean ret = false;
			if(mSharedPreferences == null){
				mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
			}
			ret = mSharedPreferences.getBoolean(key, false);
			return ret;
		}
		public void putBoolean2Preference(String key, boolean value){
			if(mSharedPreferences == null){
				mSharedPreferences = getSharedPreferences(PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
			}
			mSharedPreferences.edit().putBoolean(key, value).commit();
		}
}
