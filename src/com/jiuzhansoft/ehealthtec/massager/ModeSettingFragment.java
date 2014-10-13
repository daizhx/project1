package com.jiuzhansoft.ehealthtec.massager;

import java.util.HashMap;
import java.util.Map;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.bluetooth.BluetoothServiceProxy;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ModeSettingFragment extends Fragment implements View.OnClickListener {
	private View mViewGroup;
	private int mDisplayWidth;
	private int mDisplayHeight;
	private int mWidthBtn;
	private RelativeLayout layout1;
	
	private Context mContext;
	private ImageView centerImage;
	private static final int CENTER_BOTTON_ID = 30;
	
	private float pivotX;
	private float pivotY;
	
	private int currentMode = 0;
	private static final int MODE_CUPPING = 1;
	private static final int MODE_THUMP = 2;
	private static final int MODE_ACUPUNCTURE = 3;
	private static final int MODE_MASSAGE = 4;
	private static final int MODE_MANIPULATION = 5;
	private static final int MODE_SCRAPPING = 6;
	private static final int MODE_AUTO = 7;
	private float rotateAngle;
//	private int rotateSteps = 0;
//	private Map<Integer, Integer> modeMap = new HashMap<Integer, Integer>();
//	private int flag = 0;
	
	FragmentChangeListener mFragmentChangeListener;
	
	//转过的角度
	private float oldAngle = 0f;
	private float oldAngle2 = 0f;
	
	private int[]	centerImages	= 
	{ 
			R.drawable.button_1_big,
			R.drawable.button_2_big, 
			R.drawable.button_3_big, 
			R.drawable.button_4_big, 
			R.drawable.button_5_big, 
			R.drawable.button_6_big, 
			R.drawable.button_7_big
	};
	private int[] modeIcon = {
			R.drawable.button_1,
			R.drawable.button_2, 
			R.drawable.button_3, 
			R.drawable.button_4, 
			R.drawable.button_5, 
			R.drawable.button_6, 
			R.drawable.button_7			
	};
	private int[] stringIds = {
			R.string.massage_machine_modle4,
			R.string.massage_machine_modle1,
			R.string.massage_machine_modle2,
			R.string.massage_machine_modle5,
			R.string.massage_machine_modle3,
			R.string.massage_machine_modle6,
			R.string.massage_machine_modle7
	};
	
	private short modes[] = {
			BluetoothServiceProxy.MODE_TAG_1,
			BluetoothServiceProxy.MODE_TAG_3,
			BluetoothServiceProxy.MODE_TAG_4,
			BluetoothServiceProxy.MODE_TAG_5,
			BluetoothServiceProxy.MODE_TAG_2,
			BluetoothServiceProxy.MODE_TAG_6,
			BluetoothServiceProxy.MODE_TAG_7
	};
	
	
	private static final int CUPPING_VIEW_ID = 1;
	private static final int THUMP_VIEW_ID = 2;
	private static final int ACUPUNCTURE_VIEW_ID = 3;
	private static final int MASSAGE_VIEW_ID = 4;
	private static final int MANIPULATION_VIEW_ID = 5;
	private static final int SCRAPPING_VIEW_ID = 6;
	private static final int AUTO_VIEW_ID = 7;
	
	private int modeIds[] = {1,2,3,4,5,6,7};
	private int firtModeId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("daizhx", "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("daizhx", "onCreateView");
		mViewGroup =  inflater.inflate(R.layout.fragment_massager_mode, null, false);
		mContext = getActivity();
//		if(flag == 0){
//		//初始化一次
////		initModeMap();
//		flag = 1;
//		}
		oldAngle = 0f;
		oldAngle2 = 0f;
		initView();
		Button btn1 = (Button) mViewGroup.findViewById(R.id.time_settings);
		btn1.setOnClickListener(this);
		Button btn2 = (Button) mViewGroup.findViewById(R.id.strength_settings);
		btn2.setOnClickListener(this);
		return mViewGroup;
		
	}
	
	
//	private void initModeMap() {
//		// TODO Auto-generated method stub
//		for(int i=0;i<7;i++){
//			modeMap.put(i+1, i+1);
//		}
//	}

	private void initView() {
		// TODO Auto-generated method stub
		mDisplayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		mDisplayHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		
        //float r = (float) (mDisplayWidth * 0.9 * 0.5);
		final int r = (int) (mDisplayWidth * 0.75 * 0.5);
		//中心点距离顶部的长度
		int pivot_top = r + (int)(mDisplayWidth * 0.1 + 0.5);
		pivotX = mDisplayWidth/2;
		pivotY = pivot_top;
		
        mWidthBtn = (int) (mDisplayWidth / 4);        
        
        layout1 = (RelativeLayout) mViewGroup.findViewById(R.id.anmorl);
        
        //添加圆圈背景图
        ImageView circleiv = new ImageView(mContext);
        RelativeLayout.LayoutParams circlerl =
        	new RelativeLayout.LayoutParams(
        			r*2, r*2);
        circlerl.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        circlerl.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
        circlerl.topMargin = pivot_top - r;
        circleiv.setLayoutParams(circlerl);
//        circleiv.setBackgroundDrawable(BaseActivity.readDrawable(getApplicationContext(), R.drawable.bg_all_mode));
        circleiv.setBackgroundResource(R.drawable.bg_all_mode);
        layout1.addView(circleiv);
        
        //添加中心图
        centerImage = new ImageView(mContext);
        RelativeLayout.LayoutParams mycenterrl = 
        	new RelativeLayout.LayoutParams((int)(mWidthBtn * 2 - 100 ), (int)(mWidthBtn * 2 - 100));
        mycenterrl.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mycenterrl.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
        mycenterrl.topMargin = pivot_top - mWidthBtn + 50;
        centerImage.setLayoutParams(mycenterrl);
        centerImage.setId(CENTER_BOTTON_ID);
        centerImage.setImageResource(centerImages[currentMode]);
//        centerImage.setOnClickListener(mContext);
        layout1.addView(centerImage);
        
        int mode = currentMode;
        firtModeId = modeIds[currentMode];
        // button1
        {
//        int mode = modeMap.get(1) - 1;
        	
        View view = (((Activity) mContext).getLayoutInflater()).inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode]);//cupping
        RelativeLayout.LayoutParams rlp1 = 
        	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
        rlp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlp1.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
        rlp1.topMargin = pivot_top + r - mWidthBtn/2;
        view.setLayoutParams(rlp1);
        view.setId(modeIds[mode]);
        view.setOnClickListener(this);
        layout1.addView(view);
        }
        
        // button2
        {
//        	int mode = modeMap.get(2) - 1;
        mode++;	
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode%7]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode%7]);//thump-捶打
	      RelativeLayout.LayoutParams rlp2 = 
	    	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
	    rlp2.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
	    rlp2.topMargin = pivot_top + (int)(r*Math.cos((2*Math.PI)/7)) - mWidthBtn/2;
	    rlp2.leftMargin = mDisplayWidth/2 - (int)(r*Math.sin((2*Math.PI)/7)) - mWidthBtn/2;
	    view.setLayoutParams(rlp2);
	    view.setId(modeIds[mode%7]);
	    view.setOnClickListener(this);
	    layout1.addView(view);
        }
        
        // button3
        {
//        	int mode = modeMap.get(3) - 1;
        mode++;	
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode%7]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode%7]);//Acupuncture-针灸
	      RelativeLayout.LayoutParams rlp3 = 
	    	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
	    rlp3.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);  
	    rlp3.topMargin = pivot_top - (int)(r*Math.sin(2*((2*Math.PI)/7) - Math.PI/2)) - mWidthBtn/2;
	    rlp3.leftMargin = mDisplayWidth/2 - (int)(r*Math.cos(2*((2*Math.PI)/7) - Math.PI/2)) - mWidthBtn/2;
	    view.setLayoutParams(rlp3);
	    view.setId(modeIds[mode%7]);
	    view.setOnClickListener(this);
	    layout1.addView(view);
        }
        
        // button4
        {
//        	int mode = modeMap.get(4) - 1;
        mode++;	
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode%7]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode%7]);//massage-按摩
	      RelativeLayout.LayoutParams rlp4 = 
	    	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
	    rlp4.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);  
	    rlp4.topMargin = pivot_top - (int)(r*Math.cos(Math.PI - 3*((2*Math.PI)/7))) - mWidthBtn/2;
	    rlp4.leftMargin = mDisplayWidth/2 - (int)(r*Math.sin(Math.PI - 3*((2*Math.PI)/7))) - mWidthBtn/2;
	    view.setLayoutParams(rlp4);
	    view.setId(modeIds[mode%7]);
	    view.setOnClickListener(this);
	    layout1.addView(view);
        }
        
        // button5
        {
//        	int mode = modeMap.get(5) - 1;
        mode++;	
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode%7]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode%7]);//推拿
	      RelativeLayout.LayoutParams rlp5 = 
	    	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
	    rlp5.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
	    rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
	    rlp5.topMargin = pivot_top - (int)(r*Math.cos(Math.PI - 3*((2*Math.PI)/7))) - mWidthBtn/2;
	    rlp5.rightMargin = mDisplayWidth/2 - (int)(r*Math.sin(Math.PI - 3*((2*Math.PI)/7))) - mWidthBtn/2;
	    view.setLayoutParams(rlp5);
	    view.setId(modeIds[mode%7]);
	    view.setOnClickListener(this);
	    layout1.addView(view);
        }
        
        // button6
        {
//        	int mode = modeMap.get(6) - 1;
        mode++;	
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode%7]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode%7]);//刮痧
	      RelativeLayout.LayoutParams rlp6 = 
	    	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
	    rlp6.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);  
	    rlp6.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
	    rlp6.topMargin = pivot_top - (int)(r*Math.sin(2*((2*Math.PI)/7) - Math.PI/2)) - mWidthBtn/2;
	    rlp6.rightMargin = mDisplayWidth/2 - (int)(r*Math.cos(2*((2*Math.PI)/7) - Math.PI/2)) - mWidthBtn/2;
	    view.setLayoutParams(rlp6);
	    view.setId(modeIds[mode%7]);
	    view.setOnClickListener(this);
	    layout1.addView(view);

        }
        
        // button7
        {
//        	int mode = modeMap.get(7) - 1;
        	mode++;
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.mybutton, null);
        ImageView iv = (ImageView)view.findViewById(R.id.img);
        iv.setImageResource(modeIcon[mode%7]);
        TextView text = (TextView)view.findViewById(R.id.text);
        text.setText(stringIds[mode%7]);//auto
	      RelativeLayout.LayoutParams rlp7 = 
	    	new RelativeLayout.LayoutParams(mWidthBtn, mWidthBtn);
	    rlp7.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
	    rlp7.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
	    rlp7.topMargin = pivot_top + (int)(r*Math.cos((2*Math.PI)/7)) - mWidthBtn/2;
	    rlp7.rightMargin = mDisplayWidth/2 - (int)(r*Math.sin((2*Math.PI)/7)) - mWidthBtn/2;
	    view.setLayoutParams(rlp7);
	    view.setId(modeIds[mode%7]);
	    view.setOnClickListener(this);
	    layout1.addView(view);
        }        

	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.d("daizhx", "onAttach: " + currentMode);
		try{
			mFragmentChangeListener = (FragmentChangeListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implement FragmentChangeListener");
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.d("daizhx", "onSaveInstanceState");
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		Log.d("daizhx", "onClick:id="+id);
		switch (id) {
		case R.id.strength_settings:
			mFragmentChangeListener.onChangeStrengthSetting();
			break;
		case R.id.time_settings:
			mFragmentChangeListener.onChangeTimeSetting();
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			startCircleAnimation(layout1, id, 1000);
			break;
		default:
			break;
		}
	}
	
	
	/**
	 * 
	 * @param viewGroup
	 * @param index
	 * @param durationMillis
	 */
	private void startCircleAnimation(final ViewGroup viewGroup, final int viewId, int durationMillis){
		//计算初始角度
		int angle = viewId - firtModeId;
		if(angle < 0){
			angle += 7;
		}
		float startAngle = angle*(360f/7) + oldAngle;
		//float startAngle = (viewId - firtModeId)*(360f/7) + oldAngle;
		if(startAngle >= 360f){
			startAngle -= 360f;
		}
		rotateAngle = 360f - startAngle;
		if(rotateAngle == 360f){
			rotateAngle = 0f;
		}
		ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(viewGroup, "rotation", oldAngle, rotateAngle+oldAngle);
//		RotateAnimation rotateAnimation = new RotateAnimation(0, rotateAngle, mDisplayWidth/2, pivotY);
//		rotateAnimation.setFillAfter(false);
		rotateAnimation.setDuration(durationMillis);
//		viewGroup.startAnimation(rotateAnimation);
		rotateAnimation.setInterpolator(new LinearInterpolator());
		rotateAnimation.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				currentMode = viewId - 1;
				centerImage.setImageResource(centerImages[currentMode]);
				setBTCommand(modes[currentMode]);
				for(int i = 2; i<=8; i++){
					ObjectAnimator animator = ObjectAnimator.ofFloat(viewGroup.getChildAt(i), "rotation", oldAngle2, oldAngle2-rotateAngle);
					animator.setDuration(500);
					animator.setInterpolator(new LinearInterpolator());
					animator.start();
				}
				
				oldAngle2 -= rotateAngle;
				if(360f + oldAngle2 <= 0){
					oldAngle2 += 360f;
				}
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		rotateAnimation.start();
		oldAngle += rotateAngle;
		if(oldAngle >= 360f){
			oldAngle -= 360f;
		}
	}
	
	
	private void setBTCommand(final short mycommandId){
		Log.d("daizhx", "setBTCommand mode select:"+mycommandId);
		if(BluetoothServiceProxy.isconnect()) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() { //向按摩机发送数据

				@Override
				public void run() {
					try {
						BluetoothServiceProxy.sendCommandToDevice(mycommandId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						BluetoothServiceProxy.disconnectBluetooth();
						((MassagerActivity)getActivity()).setBTDisconnect();
					}
				}

			}, 0L);

		} else {
			Toast.makeText(getActivity(), getString(R.string.disconnectedstate), 0).show();
			
		}
	}
	
	
}
