package com.hengxuan.ehealthplatform.fragment;

import com.hengxuan.ehealthplatform.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MassageFragment extends Fragment {
	private int mDisplayWidth;
	private RelativeLayout homeRl, selectRl;
	private ImageView bottomimg;
	private int IMGID = 0;
	private Button preBtn, nextBtn;
	private int BTN1 = 1;
	private int BTN2 = 2;
	private ImageView toselectiv;
	//private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View mView = inflater.inflate(R.layout.selectview, container,false);
		initView(mView);
		return mView;
	}
	private void initView(View mView) {
		// TODO Auto-generated method stub
		mDisplayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mDisplayWidth = (int) (mDisplayWidth * 0.9 + 0.5);
        homeRl = (RelativeLayout) mView.findViewById(R.id.homeid);
        selectRl = (RelativeLayout) mView.findViewById(R.id.selectrl);
                
        bottomimg = new ImageView(getActivity());
        RelativeLayout.LayoutParams rlp = 
        	new RelativeLayout.LayoutParams((int)(mDisplayWidth+0.5), (int)(mDisplayWidth+0.5));
        bottomimg.setLayoutParams(rlp);
        bottomimg.setId(IMGID);
        bottomimg.setBackgroundResource(R.drawable.displaybg);
        
        preBtn = new Button(getActivity());
        RelativeLayout.LayoutParams preRlp = 
        	new RelativeLayout.LayoutParams((int)(mDisplayWidth * 0.4 + 0.5), (int)(mDisplayWidth * 0.8 + 0.5));
        preRlp.addRule(RelativeLayout.ALIGN_TOP, IMGID);
        preRlp.addRule(RelativeLayout.ALIGN_LEFT, IMGID);
        preRlp.leftMargin = (int)(mDisplayWidth * 0.1 + 0.5);
        preRlp.topMargin = (int)(mDisplayWidth * 0.1 + 0.5 - 3);
        preBtn.setLayoutParams(preRlp);
        preBtn.setId(BTN1);
        preBtn.setText(getResources().getString(R.string.massage));
        preBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(mDisplayWidth * 0.5 / 8));
        
        preBtn.setTextColor(Color.argb(255, 0, 255, 255));
        preBtn.setPadding(0, 0, (int)(mDisplayWidth * 0.4 * 0.3), 0);
        preBtn.setBackgroundResource(R.drawable.prebtn_unpress);
        //preBtn.setOnClickListener(this);
        
        nextBtn = new Button(getActivity());
        RelativeLayout.LayoutParams nextRlp = 
        	new RelativeLayout.LayoutParams((int)(mDisplayWidth * 0.4 + 0.5), (int)(mDisplayWidth * 0.8 + 0.5));
        nextRlp.addRule(RelativeLayout.ALIGN_BASELINE, BTN1);
        nextRlp.addRule(RelativeLayout.ALIGN_BOTTOM, BTN1);
        nextRlp.addRule(RelativeLayout.RIGHT_OF, BTN1);
        nextBtn.setLayoutParams(nextRlp);
        nextBtn.setId(BTN2);
        nextBtn.setText(getResources().getString(R.string.mydianjiu));
        nextBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float)(mDisplayWidth * 0.5 / 8));
        /*if(LanguageUtil.getLanguage() == 3){
        	nextBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15); 
        	nextBtn.setSingleLine(true);
        	// nextBtn.setFocusable(true);
        	// nextBtn.setFocusableInTouchMode(true);
        	nextBtn.setEllipsize(TruncateAt.MARQUEE);
        	nextBtn.setMarqueeRepeatLimit(-1);
        }else
        	nextBtn.setTextSize(20f);   */     	
        nextBtn.setTextColor(Color.argb(255, 0, 255, 255));
        nextBtn.setPadding((int)(mDisplayWidth * 0.4 * 0.3), 0, 0, 0);
        nextBtn.setBackgroundResource(R.drawable.nextbtn_unpress);
        //nextBtn.setOnClickListener(this);
        
        toselectiv = new ImageView(getActivity());
        RelativeLayout.LayoutParams rlp2select = 
        	new RelativeLayout.LayoutParams((int)(mDisplayWidth*0.4), (int)(mDisplayWidth*0.4));
        rlp2select.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        toselectiv.setLayoutParams(rlp2select);
        toselectiv.setBackgroundResource(R.drawable.toselect);
        
        selectRl.addView(bottomimg);
        selectRl.addView(preBtn);
        selectRl.addView(nextBtn);
        homeRl.addView(toselectiv);
	}
}
