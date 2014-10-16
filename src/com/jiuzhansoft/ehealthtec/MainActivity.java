package com.jiuzhansoft.ehealthtec;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.activity.GameCenterActivity;
import com.jiuzhansoft.ehealthtec.activity.HealthClub;
import com.jiuzhansoft.ehealthtec.activity.MassageActivity;
import com.jiuzhansoft.ehealthtec.activity.PhysicalExamActivity;
import com.jiuzhansoft.ehealthtec.activity.ReportActivity;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupSetting;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.product.Product;
import com.jiuzhansoft.ehealthtec.update.UpdateManager;
import com.jiuzhansoft.ehealthtec.update.UpdateService;
import com.jiuzhansoft.ehealthtec.user.UserInformationActivity;
import com.jiuzhansoft.ehealthtec.user.UserLogin;
import com.jiuzhansoft.ehealthtec.user.UserLoginActivity;
import com.jiuzhansoft.ehealthtec.utils.AsynImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends SlidingActivity implements OnClickListener {
	private static final String TAG = "MainActivity";
	private int mDisplayWidth = 0;
	private int mDisplayHeight = 0;
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter = new TopPagerViewAdapter();
	private ImageView ind1, ind2, ind3;
	private Bitmap mPlaceHolderBitmap;
	private SlidingMenu mSlideMenu;
	private static final int REQ_ACTION_LOGIN = 1;
	// private Boolean isSlideMenuOpen = false;
	// 从服务器上获取的图片
	private List<File> imageList = new ArrayList<File>();
	// health tip
	private TextView tvTip;
	private FragmentTransaction mFragmentTransaction;
	private FragmentManager mFragmentManager;
	private MainMenuFragment mMainMenuFragment;
	private HealthTipsFragment mHealthTipsFragment;
	private boolean fragmentFlag = false;
	public ImageView leftIcon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mDisplayWidth = displayMetrics.widthPixels;
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.slide_menu);
		mSlideMenu = getSlidingMenu();
		mSlideMenu.setBehindOffset(mDisplayWidth / 3);
		// 关闭手势
		mSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		initActionBar();
		mFragmentManager = getFragmentManager();
		mMainMenuFragment = new MainMenuFragment();
		mFragmentManager.beginTransaction()
				.add(R.id.fragment_container, mMainMenuFragment).commit();
		initView();
		getLoginState();

		// 更新
		//UpdateManager updateManager = UpdateManager.getUpdateManager(this);
		//updateManager.checkAndUpdate();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void getLoginState() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences(PreferenceKeys.FILE_NAME,
				MODE_PRIVATE);
		boolean isLogin = pref.getBoolean(PreferenceKeys.SYS_USER_LOGIN, false);
		UserLogin.setUserState(isLogin);
	}
	
	/**
	 * 初始化actionBar
	 */
	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		// 采用此方法在第一次进入该界面时，还是会先闪一下home icon和title
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(view, lp);
		leftIcon = (ImageView) view.findViewById(R.id.left_icon);
		leftIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(fragmentFlag){
					mFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
					mFragmentManager.popBackStack();
					leftIcon.setImageResource(R.drawable.person_icon);
					fragmentFlag = false;
					return;
				}
				
				if (UserLogin.hasLogin()) {
					showMenu();
					((TextView) findViewById(R.id.user_name)).setText(UserLogin
							.getUserName());
				} else {
					// 登录成功返回时，显示slide menu
					startActivityForResult(new Intent(MainActivity.this,
							UserLoginActivity.class), REQ_ACTION_LOGIN);
				}
			}
		});
	}

	/**
	 * 设置actionBar home icon
	 * @param resId
	 * @param listener
	 */
	public void setLeftIcon(int resId, OnClickListener listener) {
		ActionBar actionBar = getActionBar();
		View view = actionBar.getCustomView();
		ImageView ivLeft = (ImageView) view.findViewById(R.id.left_icon);
		ivLeft.setImageResource(R.drawable.ic_action_back);
		ivLeft.setOnClickListener(listener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQ_ACTION_LOGIN) {
			if (resultCode == Activity.RESULT_OK) {
				showMenu();
				((TextView) findViewById(R.id.user_name)).setText(UserLogin
						.getUserName());
			} else {
				// TODO
			}
		}
	}
	
	/**
	 * 初始化Views
	 */
	private void initView() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) findViewById(R.id.title_viewPager);
		ind1 = (ImageView) findViewById(R.id.ind1);
		ind2 = (ImageView) findViewById(R.id.ind2);
		ind3 = (ImageView) findViewById(R.id.ind3);

		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				switch (position) {
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
//		mPlaceHolderBitmap = decodeSampledBitmapFromResource(getResources(),
//				R.drawable.placehold, 540, 200);

		// initial the slideMenu
		ListView list1 = (ListView) findViewById(R.id.list1);
		ArrayList<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
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
		SimpleAdapter simpleAdapter1 = new SimpleAdapter(this, maps,
				R.layout.list_item_1, new String[] { "icon", "text" },
				new int[] { R.id.icon, R.id.text });
		list1.setAdapter(simpleAdapter1);
		list1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					startActivity(new Intent(MainActivity.this,
							UserInformationActivity.class));
					break;
				case 1:
					startActivity(new Intent(MainActivity.this,
							ReportActivity.class));
					break;
				case 2:
					// 分享
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, getResources()
							.getString(R.string.share));
					intent.putExtra(Intent.EXTRA_TEXT, getResources()
							.getString(R.string.sharecontent));
					startActivity(Intent.createChooser(intent, getTitle()));
					break;

				default:
					break;
				}
			}
		});

		// list2 is up to user info
		ListView list2 = (ListView) findViewById(R.id.list2);
		final Product[] recentProduct = new Product[] { null, null };
		int index = 0;
		for (Product p : EHTApplication.productList) {
			// 仅仅只搜索2个
			if (index >= 2)
				break;
			if (p.isRecentUse) {
				recentProduct[index++] = p;
			}
		}
		BaseAdapter baseAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(MainActivity.this)
							.inflate(R.layout.list_item_2, null);
				} else {
					// TODO
				}
				ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
				TextView title = (TextView) convertView
						.findViewById(R.id.title);
				TextView comment = (TextView) convertView
						.findViewById(R.id.comment);
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
		list2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				recentProduct[position].EntryProduct(MainActivity.this);
			}
			
		});
		
		Button b = (Button)findViewById(R.id.btn_logout);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(UserLogin.hasLogin()){
					UserLogin.setUserState(false);
					toggle();
				}
			}
		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 顶部viewPager适配器
	 * @author Administrator
	 *
	 */
	private class TopPagerViewAdapter extends PagerAdapter {
		AsynImageLoader asynImageLoader = new AsynImageLoader();
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
			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
					ViewPager.LayoutParams.MATCH_PARENT,
					ViewPager.LayoutParams.MATCH_PARENT);
			// 创建imageView,异步加载图片
			ImageView imageView = new ImageView(MainActivity.this);
			// BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			// task.execute(R.drawable.placehold);
			// loadBitmap(0, imageView);
			AsynImageLoader asynImageLoader = new AsynImageLoader();
			position += 1;
			asynImageLoader
					.showImageAsyn(MainActivity.this,imageView,
							"http://182.254.137.149:8080/client/adv/" + position
									+ ".jpg", R.drawable.placehold);
			container.addView(imageView, lp);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView((ImageView) object);
		}
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (fragmentFlag) {
				fragmentFlag = false;
				leftIcon.setImageResource(R.drawable.person_icon);
				return super.onKeyDown(keyCode, event);
			}
			final AlertDialog alertDialog = (new AlertDialog.Builder(this))
					.create();
			CharSequence charsequence = getText(R.string.pg_home_exit_confrim_string);
			alertDialog.setMessage(charsequence);
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
					getText(R.string.exit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
//							exitLogin();
							finish();
						}

					});
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
					getText(R.string.cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertDialog.cancel();
						}

					});
			alertDialog.show();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void setFragmentFlage(boolean b) {
		fragmentFlag = b;
	}

	private void exitLogin() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences(PreferenceKeys.FILE_NAME,
				MODE_PRIVATE);
		pref.edit().putBoolean(PreferenceKeys.SYS_USER_LOGIN, false).commit();
		pref.edit().putString(PreferenceKeys.SYS_USER_NAME, "").commit();
	}

	public FragmentManager getMyFragmentManager() {
		return mFragmentManager;
	}

}
