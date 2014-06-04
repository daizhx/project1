package com.hengxuan.ehealthplatform.product;

import java.io.InputStream;
import java.lang.ref.SoftReference;

import org.json.JSONException;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.constant.PreferenceKeys;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstFuncId;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private static Context mContext;
	private String name;
	//values in ConstEquipId.java
	private int TypeId;
	//make no sense,http need this, value in ConstHttpProp
	private String mode;
	private String manufacturer;//eg. "GZ-Hengxuan"
	private String comments;
	private Bitmap logo;
	private String entry;//eg. .Massager.class
	private String entryIntent;
	public boolean isRecentUse;
	private boolean isVerificated;
	
	public Product(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context.getApplicationContext();
	}
	
	public Product(Context context, String productName) {
		// TODO Auto-generated constructor stub
		mContext = context.getApplicationContext();
		name = productName;
		SharedPreferences pref = mContext.getSharedPreferences(PreferenceKeys.FILE_NAME, mContext.MODE_PRIVATE);
		isVerificated = pref.getBoolean(name, false);
	}
	
	//检查是否已认证过设备
	//the Context must a BaseActivity
	public boolean checkIsVerificated(final Context getContext) {
		// TODO Auto-generated method stub
		if(isVerificated){
			return true;
		}else{
			
		}
		
		return false;
	}
	public boolean setVerification(){
		SharedPreferences pref = mContext.getSharedPreferences(PreferenceKeys.FILE_NAME, mContext.MODE_PRIVATE);
		pref.edit().putBoolean(name, true).commit();
		isVerificated = true;
		return isVerificated;
	}
	public void setName(String s){
		name = s;
	}
	public void setComments(String s){
		comments = s;
	}
	public void setComments(int resId){
		comments = mContext.getString(resId);
	}
	public void setLogo(Bitmap bmp){
		logo = bmp;
	}
	public void setLogo(int resId){
		logo = readLogoBitmap(resId);
	}
	public String getName(){
		return name;
	}
	public String getComments(){
		return comments;
	}
	public Bitmap getLogo(){
		return logo;
	}
	
	private static Bitmap readLogoBitmap(int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = mContext.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		SoftReference<Bitmap> softreference = new SoftReference<Bitmap>(bitmap);
		return softreference.get();
	}
	
	//transfer the sequence code to the server via http
	public static boolean isTransferSequenceCode(final Context getContext, final Class getClass, final int getcodeid, final String thecode, final String clientCode){
		final String getUserId = ((BaseActivity)getContext).getStringFromPreference(ConstHttpProp.USER_PIN);
		//USER_PIN什么时候保存的？
//		SharedPreferences sp = getContext.getSharedPreferences(ConstHttpProp.USER_PIN, Context.MODE_PRIVATE);
//		final String getUserId = sp.getString(ConstHttpProp.USER_PIN, null);
		
		final Dialog dialog = new Dialog(getContext, R.style.dialog2);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.dialog_input);
		
		// final EditText codeet = (EditText) window.findViewById(R.id.getcode);
		final EditText numet = (EditText) window.findViewById(R.id.getnum);
		
		final Button okbtn = (Button) window.findViewById(R.id.mybtn);
		numet.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				okbtn.setEnabled(true);
			}
		});
		okbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(numet.getText().toString())){
					verificationCode(getUserId, 
							getcodeid, 
							numet.getText().toString(),
							getContext,
							getClass,
							thecode,
							clientCode);
				}
				dialog.dismiss();
			}
		});
		Button canclebtn = (Button) window.findViewById(R.id.mybtnCancle);
		canclebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		return true;
	}

	// 设备序列号验证
	public static void verificationCode(String userPin, int code, String serialNum, 
			final Context getContext, final Class getClass, final String thecode, final String clientCode){
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.SERIALNUM);
		httpsetting.putJsonParam("userPin", userPin);
		httpsetting.putJsonParam("client_code", clientCode);
		httpsetting.putJsonParam("equipment_code", code);
		httpsetting.putJsonParam("serialNum", serialNum);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				if(response.getJSONObject() != null){
					try {
						final String result_code = response.getJSONObject().getString("code");
						Handler handler = new Handler();
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(result_code.equals("1")){
									((BaseActivity)getContext).putBoolean2Preference(thecode, true);
									Toast.makeText(getContext, getContext.getResources().getString(R.string.verified), Toast.LENGTH_LONG).show();
									Intent intent = new Intent(getContext, getClass);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									getContext.startActivity(intent);
								}else{
									Toast.makeText(getContext, getContext.getResources().getString(R.string.verify_failed), Toast.LENGTH_SHORT).show();
								}
							}
						});
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub

			}

		});
		httpsetting.setNotifyUser(true);
		((BaseActivity)getContext).getHttpGroupaAsynPool().add(httpsetting);
	}
}
