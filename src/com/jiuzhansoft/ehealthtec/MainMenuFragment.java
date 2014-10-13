package com.jiuzhansoft.ehealthtec;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.activity.GameCenterActivity;
import com.jiuzhansoft.ehealthtec.activity.HealthClub;
import com.jiuzhansoft.ehealthtec.activity.MassageActivity;
import com.jiuzhansoft.ehealthtec.activity.PhysicalExamActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupSetting;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.user.UserLogin;
import com.jiuzhansoft.ehealthtec.user.UserLoginActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuFragment extends Fragment implements OnClickListener {
	private static final String TAG = "MainActivity";
	private TextView tvTip;
	private String tip;
	private View rootView;

	// 更新UI
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				tvTip.setText(tip);
				break;

			default:
				break;
			}

		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_main_menu, null, false);
		((ImageView) rootView.findViewById(R.id.img1)).setOnClickListener(this);
		((ImageView) rootView.findViewById(R.id.img2)).setOnClickListener(this);
		((ImageView) rootView.findViewById(R.id.img3)).setOnClickListener(this);
		((ImageView) rootView.findViewById(R.id.img4)).setOnClickListener(this);
		tvTip = (TextView) rootView.findViewById(R.id.tip);
		tvTip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HealthTipsFragment healthTipsFragment = new HealthTipsFragment();
				FragmentTransaction ft = ((MainActivity) getActivity())
						.getMyFragmentManager().beginTransaction();
//				ft.setCustomAnimations(R.anim.up_in, R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_left);
				ft.setCustomAnimations(R.anim.up_in, R.anim.slide_out_right);
				ft.replace(R.id.fragment_container, healthTipsFragment);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		initDateView(rootView);
		// setTipText((TextView)view.findViewById(R.id.tip));
		getTipText();
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.img1:
			if (UserLogin.hasLogin()) {
				startActivity(new Intent(getActivity(), MassageActivity.class));
			} else {
				Intent intent = new Intent(getActivity(),
						UserLoginActivity.class);
				intent.putExtra("action",
						"ehealthplatform.intent.action.MASSAGE");
				startActivity(intent);
			}
			break;
		case R.id.img2:
			if (UserLogin.hasLogin()) {
				startActivity(new Intent(getActivity(),
						PhysicalExamActivity.class));
			} else {
				Intent intent = new Intent(getActivity(),
						UserLoginActivity.class);
				intent.putExtra("action",
						"ehealthplatform.intent.action.PHYSICAL");
				startActivity(intent);
			}
			break;
		case R.id.img3:
			startActivity(new Intent(getActivity(), HealthClub.class));
			break;
		case R.id.img4:
			startActivity(new Intent(getActivity(), GameCenterActivity.class));
			break;
		default:
			break;
		}
	}

	// 日期显示
	private void initDateView(View v) {
		int[] monthsId = { R.string.january, R.string.february,
				R.string.march, R.string.april,
				R.string.may, R.string.june,
				R.string.july, R.string.august,
				R.string.september, R.string.october,
				R.string.november, R.string.december };
		TextView tvMonth = (TextView) v.findViewById(R.id.tv_month);
		TextView tvDay = (TextView) v.findViewById(R.id.tv_date);
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		tvMonth.setText(getText(monthsId[month]));
		tvDay.setText(Integer.toString(day));
	}

	/**
	 * 获取网络上的文章显示
	 * 
	 * @param tip
	 */
	private void getTipText() {
		HttpSetting httpSetting = new HttpSetting();
		httpSetting.setFunctionId(ConstFuncId.TODAY_TIP);
		httpSetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onProgress");
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onError");
			}

			@Override
			public void onEnd(HttpResponse response) {
				Log.d(TAG, "onEnd:response---");

				// TODO Auto-generated method stub
				try {
					JSONArrayPoxy jsonArray = response.getJSONARRAY();
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = (JSONObject) jsonArray
									.get(i);
							String content = jsonObject.getString("content");
							String title = jsonObject.getString("title");
							String url = jsonObject.getString("url");
							Log.d(TAG, "content=" + content + ",title=" + title
									+ ",url=" + url);
//							tip = content;
							tip = title;
							mHandler.sendEmptyMessage(1);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				Log.d(TAG, "onStart");
			}
		});
		httpSetting.setNotifyUser(false);
		httpSetting.setShowProgress(false);
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
		localHttpGroupSetting.setMyActivity(getActivity());
		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSONARRAY);
		HttpGroupaAsynPool httpGroupaAsynPool = new HttpGroupaAsynPool(
				localHttpGroupSetting);
		httpGroupaAsynPool.add(httpSetting);
	}
}
