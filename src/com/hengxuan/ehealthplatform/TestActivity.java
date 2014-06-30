package com.hengxuan.ehealthplatform;

import java.io.File;

import com.hengxuan.ehealthplatform.lens.LensBaseActivity;
import com.hengxuan.ehealthplatform.user.UserLogin;
import com.hengxuan.ehealthplatform.utils.MyAsynImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TestActivity extends Activity {

	private PopupWindow popupWindow;
	//拍摄的是左眼还是右眼
	private int eyesIndex = 1;//左眼-1，右眼-2
	private EditText ownerEt;
	//photo tag
	private String photoTag;
	private int photoIndex=2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
//		ImageView iv = (ImageView)findViewById(R.id.img);
//		MyAsynImageLoader myAsynImageLoader = new MyAsynImageLoader(this);
//		String pic = Environment.getExternalStorageDirectory() + File.separator + "dxlphoto" + File.separator + "test.png";
//		myAsynImageLoader.loadBitmap(pic, iv, 500, 500);
//		myAsynImageLoader.loadBitmap(R.drawable.head2sl, iv);
		((Button)findViewById(R.id.btn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popup();
			}
		});
		
	}
	
	protected void popup() {
		// TODO Auto-generated method stub		
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.popup_photo_confirm, null);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		int width = outMetrics.widthPixels;
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		popupWindow = new PopupWindow(v,(int)(width*0.8), LayoutParams.WRAP_CONTENT);
		TextView tvTitle = (TextView)v.findViewById(R.id.title);
		switch (photoIndex) {
		case LensBaseActivity.INDEX_IRIS:
			tvTitle.setText(R.string.iris_photo);
			RadioButton rb1 = (RadioButton)v.findViewById(R.id.rb1);
			rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				private int eyesIndex;

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg1){
						//选中左眼
						eyesIndex = 1;
						photoTag = getString(R.string.left_eye);
						
					}else{
						eyesIndex = 2;
						photoTag = getString(R.string.Right_eye);
					}
					Log.d("daizhx", "eyesIndex = "+eyesIndex);
				}
			});
			break;
		case LensBaseActivity.INDEX_HAIR:
			tvTitle.setText(R.string.hair_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.hair);
			break;
		case LensBaseActivity.INDEX_SKIN:
			tvTitle.setText(R.string.skin_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.skin);
			break;
		case LensBaseActivity.INDEX_NAEVUS:
			tvTitle.setText(R.string.naevus_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.naevus);
			break;
		default:
			break;
		}
		ownerEt = (EditText)v.findViewById(R.id.et_owner);
		ownerEt.setText(UserLogin.getUserName());
		Button btnCancel = (Button)v.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
		((Button)v.findViewById(R.id.btn_confirm)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				String name = ownerEt.getText().toString();
//				Log.d("daizhx", "name = "+ name);
//				recordPhotoInfo(name);
//				popupWindow.dismiss();
//				//如果是虹膜照片，添加照片信息
//				mHandleClick.confirm();
			}
		});
		
		
//		popupWindow.setContentView(view);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupWindow.update();
		popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}
}
