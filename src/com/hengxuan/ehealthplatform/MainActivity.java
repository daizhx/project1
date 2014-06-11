package com.hengxuan.ehealthplatform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.activity.GameCenterActivity;
import com.hengxuan.ehealthplatform.activity.HealthClub;
import com.hengxuan.ehealthplatform.activity.MassageActivity;
import com.hengxuan.ehealthplatform.activity.PhysicalExamActivity;
import com.hengxuan.ehealthplatform.activity.ReportActivity;
import com.hengxuan.ehealthplatform.application.EHTApplication;
import com.hengxuan.ehealthplatform.constant.PreferenceKeys;
import com.hengxuan.ehealthplatform.log.Log;
import com.hengxuan.ehealthplatform.product.Product;
import com.hengxuan.ehealthplatform.update.UpdateManager;
import com.hengxuan.ehealthplatform.update.UpdateService;
import com.hengxuan.ehealthplatform.user.UserInformationActivity;
import com.hengxuan.ehealthplatform.user.UserLoginActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends SlidingActivity implements OnClickListener{
	private int mDisplayWidth = 0;
	private int mDisplayHeight = 0;
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter = new TopPagerViewAdapter();
	private ImageView ind1, ind2,ind3;
	private Bitmap mPlaceHolderBitmap;
	private SlidingMenu mSlideMenu;
	private boolean mlsLogin = false;
	private static final int REQ_ACTION_LOGIN = 1;
	//private Boolean isSlideMenuOpen = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mDisplayWidth = displayMetrics.widthPixels;
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.slide_menu);
		mSlideMenu = getSlidingMenu();
		mSlideMenu.setBehindOffset(mDisplayWidth/3);
		//关闭手势
		mSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		initActionBar();
		initView();
		getLoginState();
		

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void getLoginState() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences(PreferenceKeys.FILE_NAME, MODE_PRIVATE);
		mlsLogin = pref.getBoolean(PreferenceKeys.SYS_USER_LOGIN, false);
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		//采用此方法在第一次进入该界面时，还是会先闪一下home icon和title
//		actionBar.setDisplayShowHomeEnabled(false);
//		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(view, lp);
		ImageView slideMenuIcon = (ImageView)view.findViewById(R.id.left_icon);
		slideMenuIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mlsLogin){
					showMenu();
				}else{
					//登录成功返回时，显示slide menu
					startActivityForResult(new Intent(MainActivity.this, UserLoginActivity.class), REQ_ACTION_LOGIN);
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQ_ACTION_LOGIN){
			if(resultCode == Activity.RESULT_OK){
				showMenu();
				mlsLogin = true;
			}else{
				//TODO
			}
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager)findViewById(R.id.title_viewPager);
		ind1 = (ImageView)findViewById(R.id.ind1);
		ind2 = (ImageView)findViewById(R.id.ind2);
		ind3 = (ImageView)findViewById(R.id.ind3);
		
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				switch(position){
				case 0:
					ind1.setImageResource(R.drawable.page_ind_selected);
					ind2.setImageResource(R.drawable.page_ind);
					ind3.setImageResource(R.drawable.page_ind);
					break;
				case 1:
					ind2.setImageResource(R.drawable.page_ind_selected);
					ind1.setImageResource(R.drawable.page_ind);
					ind3.setImageResource(R.drawable.page_ind);
					break;
				case 2:
					ind3.setImageResource(R.drawable.page_ind_selected);
					ind2.setImageResource(R.drawable.page_ind);
					ind1.setImageResource(R.drawable.page_ind);
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
				// TODO Auto-generated method stub

			}
		});
		mPlaceHolderBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.placehold, 540, 200);
		
		((ImageView)findViewById(R.id.img1)).setOnClickListener(this);
		((ImageView)findViewById(R.id.img2)).setOnClickListener(this);
		((ImageView)findViewById(R.id.img3)).setOnClickListener(this);
		((ImageView)findViewById(R.id.img4)).setOnClickListener(this);
		
		//initial the slideMenu
		ListView list1 = (ListView)findViewById(R.id.list1);
		ArrayList<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("icon", R.drawable.person_icon);
		map1.put("text", getString(R.string.user_info));
		maps.add(map1);
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("icon", R.drawable.check_icon);
		map2.put("text", getString(R.string.physical_report));
		maps.add(map2);
		
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("icon", R.drawable.icon_share);
		map3.put("text", getString(R.string.share));
		maps.add(map3);
		SimpleAdapter simpleAdapter1 = new SimpleAdapter(this, maps, R.layout.list_item_1, new String[]{"icon", "text"}, new int[]{R.id.icon, R.id.text});
		list1.setAdapter(simpleAdapter1);
		list1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					startActivity(new Intent(MainActivity.this, UserInformationActivity.class));
					break;
				case 1:
					startActivity(new Intent(MainActivity.this, ReportActivity.class));
					break;
				case 2:
					//分享
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share));
					intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sharecontent));
					startActivity(Intent.createChooser(intent, getTitle()));
					break;

				default:
					break;
				}
			}
		});
		
		//list2 is up to user info
		ListView list2 = (ListView)findViewById(R.id.list2);
		final Product[] recentProduct = new Product[]{
				null,null
		};
		int index = 0;
		for(Product p : EHTApplication.productList){
			//仅仅只搜索2个
			if(index >= 2)break;
			if(p.isRecentUse){
				recentProduct[index++] = p;
			}
		}
		BaseAdapter baseAdapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item_2, null);
				}else{
					//TODO
				}
				ImageView iv = (ImageView)convertView.findViewById(R.id.icon);
				TextView title = (TextView)convertView.findViewById(R.id.title);
				TextView comment = (TextView)convertView.findViewById(R.id.comment);
				iv.setImageBitmap(recentProduct[position].getLogo());
				title.setText(recentProduct[position].getName());
				comment.setText(recentProduct[position].getComments());
				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return recentProduct.length;
			}
		};
		list2.setAdapter(baseAdapter);
		
		initDateView();
	}
	
	private void initDateView(){
		int[] monthsId = {
				R.string.first_month,
				R.string.second_month,
				R.string.third_month,
				R.string.fourth_month,
				R.string.fifth_month,
				R.string.sixth_month,
				R.string.seventh_month,
				R.string.eighth_month,
				R.string.ninth_month,
				R.string.tenth_month,
				R.string.eleven_month,
				R.string.twelve_month
		};
 		TextView tvMonth = (TextView)findViewById(R.id.tv_month);
		TextView tvDay = (TextView)findViewById(R.id.tv_date);
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		tvMonth.setText(getText(monthsId[month]));
		tvDay.setText(Integer.toString(day));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class TopPagerViewAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
			//创建imageView,异步加载图片
			ImageView imageView = new ImageView(MainActivity.this);
//			BitmapWorkerTask task = new BitmapWorkerTask(imageView);
//			task.execute(R.drawable.placehold);
			loadBitmap(0, imageView);
			container.addView(imageView, lp);
			return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((ImageView)object);
		}
	}
	
	//load image res to imageview,should change to res url.
	public void loadBitmap(int resId, ImageView imageView){
		if(cancelPotencialWork(resId, imageView)){
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable aysnDrawable = new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(aysnDrawable);
			task.execute(resId);
		}
	}
	
	private boolean cancelPotencialWork(int data, ImageView imageView) {
		// TODO Auto-generated method stub
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkTask(imageView);
		if(bitmapWorkerTask != null){
			final int bitmapData = bitmapWorkerTask.data;
			if(bitmapData == 0 || bitmapData != data){
				bitmapWorkerTask.cancel(true);
			}else{
				return false;
			}
		}
		return true;
	}
	
	private static BitmapWorkerTask getBitmapWorkTask(ImageView imageView){
		if(imageView != null){
			final Drawable drawable = imageView.getDrawable();
			if(drawable instanceof AsyncDrawable){
				final AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	static class AsyncDrawable extends BitmapDrawable{
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
		
		public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask); 
		}
		
		public BitmapWorkerTask getBitmapWorkerTask(){
			return bitmapWorkerTaskReference.get();
		}
	}
	
	//图片加载异步任务
	 class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap>{
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;
		
		public BitmapWorkerTask(ImageView imageView){
			//use a WeakReference to ensure the imageview can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			data = params[0];
			if(data != 0){
				return decodeSampledBitmapFromResource(getResources(), data, 540, 200);
			}else{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// TODO Auto-generated method stub
			if(isCancelled()){
				bitmap = null;
			}
			if(imageViewReference != null && bitmap != null){
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkTask(imageView);
				if(imageView != null && bitmapWorkerTask == this){
					imageView.setImageBitmap(bitmap);
				}
			}
		}
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch(id){
		case R.id.img1:
			if(mlsLogin){
				startActivity(new Intent(this, MassageActivity.class));
			}else{
				Intent intent = new Intent(this, UserLoginActivity.class);
				intent.putExtra("action", "ehealthplatform.intent.action.MASSAGE");
				startActivity(intent);
			}
			break;
		case R.id.img2:
			if(mlsLogin){
				startActivity(new Intent(this, PhysicalExamActivity.class));
			}else{
				Intent intent = new Intent(this, UserLoginActivity.class);
				intent.putExtra("action", "ehealthplatform.intent.action.PHYSICAL");
				startActivity(intent);
			}
			break;
		case R.id.img3:
			startActivity(new Intent(this, HealthClub.class));
			break;
		case R.id.img4:
			startActivity(new Intent(this, GameCenterActivity.class));
			break;
		default:
			break;
		}
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			final AlertDialog alertDialog = (new AlertDialog.Builder(this)).create();
			CharSequence charsequence = getText(R.string.pg_home_exit_confrim_string);
			alertDialog.setMessage(charsequence);
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.exit), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					exitLogin();
					finish();
				}
				
			});
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertDialog.cancel();
				}
				
			});
			alertDialog.show();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);
		}
	}

	private void exitLogin() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences(PreferenceKeys.FILE_NAME, MODE_PRIVATE);
		pref.edit().putBoolean(PreferenceKeys.SYS_USER_LOGIN, false).commit();
	}
}
