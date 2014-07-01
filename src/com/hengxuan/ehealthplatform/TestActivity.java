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
	//ÅÄÉãµÄÊÇ×óÑÛ»¹ÊÇÓÒÑÛ
	private int eyesIndex = 1;//×óÑÛ-1£¬ÓÒÑÛ-2
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

			}
		});
		
	}
	

}
