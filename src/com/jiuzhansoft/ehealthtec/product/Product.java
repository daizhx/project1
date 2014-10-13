package com.jiuzhansoft.ehealthtec.product;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import org.json.JSONException;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.constant.PreferenceKeys;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Product {
	public int id;
	public String name;
	// values in ConstEquipId.java
	public int mTypeId;//验证序列号参数
	// make no sense,http need this, value in ConstHttpProp
	public String theCode;//验证序列号参数
	public String clientCode = "GZ-Hengxuan";//验证序列号参数
	// a short description for the product
	private String mDescription;
	private int mLogoResId;
	public Uri logoUri;
	// intent action
	public String mEntryIntent;
	public boolean isRecentUse;
	public boolean isVerificated;
	

	// public Product(Context context) {
	// // TODO Auto-generated constructor stub
	// mContext = context.getApplicationContext();
	// }

	public Product(String productName, int typeId,
			String comments, int logoResId, String entryIntent) {
		// TODO Auto-generated constructor stub
		name = productName;
		mTypeId = typeId;
		mDescription = comments;
		mLogoResId = logoResId;
		mEntryIntent = entryIntent;
		SharedPreferences pref = EHTApplication.getInstance().getSharedPreferences(
				PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
		isVerificated = pref.getBoolean(name, false);
	}

	// 检查是否已认证过设备
	public boolean IsVerificated() {
		// TODO Auto-generated method stub
		return isVerificated;
	}

	public boolean setVerification() {
		SharedPreferences pref = EHTApplication.getInstance().getSharedPreferences(
				PreferenceKeys.FILE_NAME, Context.MODE_PRIVATE);
		pref.edit().putBoolean(name, true).commit();
		isVerificated = true;
		return isVerificated;
	}
	
	public void setTheCode(String s){
		theCode = s;
	}
	public void setClientCode(String s){
		clientCode = s;
	}

	public void setProductName(String s) {
		name = s;
	}

	public void setTypeId(int id) {
		mTypeId = id;
	}

	public void setComments(String s) {
		mDescription = s;
	}

	public void setComments(int resId) {
		mDescription = EHTApplication.getInstance().getString(resId);
	}

	public void setLogo(int resId) {
		mLogoResId = resId;
	}

	public void setLogo(Uri uri) {
		// TODO
	}

	public String getName() {
		return name;
	}

	public String getComments() {
		return mDescription;
	}

	public Bitmap getLogo() {
		return readLogoBitmap(mLogoResId);
	}

	public void setEntryIntent(String action) {
		mEntryIntent = action;
	}

	public static Bitmap readLogoBitmap(int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = EHTApplication.getInstance().getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		SoftReference<Bitmap> softreference = new SoftReference<Bitmap>(bitmap);
		return softreference.get();
	}



	public void EntryProduct(Context context) {
		if (mEntryIntent != null) {
			Intent intent = new Intent();
			intent.setAction(mEntryIntent);
			context.startActivity(intent);
		}
	}
}
