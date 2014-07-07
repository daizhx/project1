package com.hengxuan.ehealthplatform.lens;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.R.string;
import com.hengxuan.ehealthplatform.lens.MyDataBaseContract.ImagesInfo;
import com.hengxuan.ehealthplatform.myview.MyImageView;
import com.hengxuan.ehealthplatform.user.UserLogin;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ShootConfirmFragment extends Fragment {
	private View view;
	// absolutely path
	private String photoPath;
	private MyImageView photoImage;
	//照片类型
	private int photoIndex;
	// click this to delete photoFile and reShoot
	private ImageView iVchacha;
	// click this to be Ok
	private ImageView iVcheck;
	private HandleClick mHandleClick;
	//保存照片详细信息
	private PopupWindow popupWindow;
	//拍摄的是左眼还是右眼
	private int eyesIndex = 0;//左眼-0，右眼-1
	private EditText ownerEt;
	//photo tag
	private String photoTag;
	
	public void setHandleClick(HandleClick h){
		mHandleClick = h;
	}
	
	public interface HandleClick{
		public void cancel();
		public void confirm();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		photoPath = args.getString("photoPath");
		photoIndex = args.getInt("index");
		
		view = inflater.inflate(R.layout.activity_shoot_confirm, null, false);
		photoImage = (MyImageView) view.findViewById(R.id.img_photo);
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		photoImage.setImageBitmap(bitmap);

		iVchacha = (ImageView) view.findViewById(R.id.chacha);
		iVchacha.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(photoPath);
				if (file.delete()) {
					// TODO
					mHandleClick.cancel();
				} else {
					// TODO
				}
			}
		});
		iVcheck = (ImageView) view.findViewById(R.id.shoot);
		iVcheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//添加拍摄的虹膜照片的基本信息
				popup();
			}
		});
		return view;
		
	}
	protected void popup() {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.popup_photo_confirm, null);
//		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		int displayWidth = outMetrics.widthPixels; 
		popupWindow = new PopupWindow(v, (int)(displayWidth * 0.8), LayoutParams.WRAP_CONTENT);
		TextView tvTitle = (TextView)v.findViewById(R.id.title);
		switch (photoIndex) {
		case LensConstant.INDEX_IRIS:
			tvTitle.setText(R.string.iris_photo);
			photoTag = getString(R.string.left_eye);
			RadioButton rb1 = (RadioButton)v.findViewById(R.id.rb1);
			rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg1){
						//选中左眼
						eyesIndex = 0;
						photoTag = getString(R.string.left_eye);
						
					}else{
						eyesIndex = 1;
						photoTag = getString(R.string.Right_eye);
					}
					Log.d("daizhx", "eyesIndex = "+eyesIndex);
				}
			});
			break;
		case LensConstant.INDEX_HAIR:
			tvTitle.setText(R.string.hair_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.hair);
			break;
		case LensConstant.INDEX_SKIN:
			tvTitle.setText(R.string.skin_photo);
			((RadioGroup)v.findViewById(R.id.radio_group)).setVisibility(View.GONE);
			((TextView)v.findViewById(R.id.eye_label)).setVisibility(View.GONE);
			photoTag = getString(R.string.skin);
			break;
		case LensConstant.INDEX_NAEVUS:
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
				String name = ownerEt.getText().toString();
				Log.d("daizhx", "name = "+ name);
				recordPhotoInfo(name);
				popupWindow.dismiss();
				//如果是虹膜照片，添加照片信息
				mHandleClick.confirm();
			}
		});
		
		
//		popupWindow.setContentView(view);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.white));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		popupWindow.update();
		popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}
	protected void recordPhotoInfo(String owner) {
		// TODO Auto-generated method stub
		
		MyDbHelper myDbHelper = new MyDbHelper(getActivity());
		SQLiteDatabase db = myDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String strdate = simpleDateFormat.format(date);
		
		values.put(ImagesInfo.COLUMN_NAME_OWNER, owner);
		values.put(ImagesInfo.COLUMN_NAME_TAG,photoTag);
		values.put(ImagesInfo.COLUMN_NAME_DATA, photoPath);
		values.put(ImagesInfo.COLUMN_NAME_DATE, strdate);
		values.put(ImagesInfo.COLUMN_NAME_TYPE, photoIndex);
		long newRowId = db.insert(ImagesInfo.TABLE_NAME, null, values);
		Log.d("daizhx", "newRowId="+newRowId);
	}

	
}
