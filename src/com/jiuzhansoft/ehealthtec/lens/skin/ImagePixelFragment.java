package com.jiuzhansoft.ehealthtec.lens.skin;

import java.math.BigDecimal;

import com.jiuzhansoft.ehealthtec.R;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImagePixelFragment extends Fragment implements SkinAnalysisActivity.PixelAnalysis{
	private View fragmentView;
	private String picPath;
	private ImageView skinPic;
	private ImageView filterPic;
	private Bitmap bm;
	private Bitmap pm;
	private int width,height;
	private Handler handler;
	
	public static final int MSG_OIL = 1;
	public static final int MSG_WATER = 6;
	public static final int MSG_PIGMENT = 2;
	public static final int MSG_ELASTIC = 4;
	public static final int MSG_COLLAGEN = 3;
	
	private int oilNum;
	private final int oilMinColor = 170;
	private final int oilMaxColor = 230;
	
	private int waterNum;
	private final int waterColor = 125;
	
	private int pigmentNum;
	private final int pigmentColor = 140;
	
	private int elasticNum;
	private final int elasticMinColor = 120;
	private final int elasticMaxColor = 150;
	
	private int collagenNum;
	private final int collagenMinColor = 120;
	private final int collagenMaxColor = 144;
	
	private boolean isAna;
	
	public void setPicPath(String s){
		picPath = s;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragmentView = inflater.inflate(R.layout.fragment_image_pixel, null, false);
		skinPic = (ImageView)fragmentView.findViewById(R.id.ana_skan_picture);
		filterPic = (ImageView)fragmentView.findViewById(R.id.ana_filter_picture);
		bm=BitmapFactory.decodeFile(picPath);
		skinPic.setImageBitmap(bm);
		width=bm.getWidth();
    	height=bm.getHeight();
    	
    	handler = new Handler(){
			public void handleMessage(Message message){
				pm=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
	    	    filterPic.setImageBitmap(pm);
				BigDecimal per = null;
				Intent intent = new Intent(getActivity(), SkinAnalysisResultActivity.class);
				switch(message.what){
					case MSG_OIL:
						per = new BigDecimal(((float)oilNum / height / width * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", MSG_OIL);
						break;	
					case MSG_WATER:
						per = new BigDecimal(((float)waterNum / height / width * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", MSG_WATER);
						break;
					case MSG_PIGMENT:
						per = new BigDecimal(((float)pigmentNum / height / width * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", MSG_PIGMENT);
						break;
					case MSG_ELASTIC:
						per = new BigDecimal(((float)elasticNum / height / width * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", MSG_ELASTIC);
						break;
					case MSG_COLLAGEN:
						per = new BigDecimal(((float)collagenNum / height / width * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", MSG_COLLAGEN);
						break;
					default:
						break;
				}
				intent.putExtra("content", per.intValue());
				startActivity(intent);
			}
		};
		return fragmentView;
	}
	
	
	class thread extends Thread{ 
		private int type;
		
		public thread(int type){
			this.type = type;
		}
		
    	public void run() {   
    	    isAna = true;
    	    try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    int[] colors=new int[width];
    	    oilNum = 0;
    	    waterNum = 0;
    	    pigmentNum = 0;
    	    elasticNum = 0;
    	    collagenNum = 0;
    	    for(int i=0;i<height;i++){   
    	        bm.getPixels(colors, 0, width, 0, i, width, 1);   
    	        for(int j=0;j<colors.length;j++){ 
    	        	//int color = (Color.red(colors[j]) + Color.green(colors[j]) + Color.blue(colors[j])) / 3;
    	        	//if(type == MSG_COLLAGEN)
        	        	//colors[j]=Color.rgb(255 - Color.red(colors[j]), 255 - Color.green(colors[j]), 255 - Color.blue(colors[j])); 
    	        	int color = (int)(Color.red(colors[j]) * 0.299 + Color.green(colors[j]) * 0.587 + Color.blue(colors[j]) * 0.114);
    	        	colors[j]=Color.rgb(color,color, color); 
    	        	switch(type){
    	        		case MSG_OIL:
    	        			if(color >= oilMaxColor){
    	    	        		colors[j]=Color.rgb(255, 255, 255);  
    	    	        	}else if(color >= oilMinColor){
    	    	        		oilNum++;
    	    	        		colors[j]=Color.rgb(255, 0, 0); 
    	    	        	}
    	        			break;
    	        		case MSG_WATER:
    	        			if(color <= waterColor){
    	        				waterNum ++;
    	        				colors[j]=Color.rgb(0, 0, 255);
    	        			}
    	        			break;
    	        		case MSG_PIGMENT:
    	        			if(color <= pigmentColor){
    	        				pigmentNum++;
    	        				colors[j]=Color.rgb(0, 255, 0);
    	        			}
    	        			break;
    	        		case MSG_ELASTIC:
    	        			if(color <= elasticMaxColor && color >= elasticMinColor){
    	        				elasticNum++;
    	        				colors[j]=Color.rgb(0, 255, 255);
    	        			}
    	        			break;
    	        		case MSG_COLLAGEN:
    	        			if(color <= collagenMaxColor && color >= collagenMinColor){
    	        				collagenNum++;
    	        				colors[j]=Color.rgb(255, 255, 0);
    	        			}
    	        	}
    	        	
    	        } 
    	        try {
					sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	        if(pm.isMutable())
    	        	pm.setPixels(colors, 0, width, 0, i, width, 1);
    	        filterPic.postInvalidate();
    	    } 
    	    handler.sendEmptyMessage(type);
    	    isAna = false; 	                          
    	}   
    }


	@Override
	public void onPixelAnalysis(int mode) {
		// TODO Auto-generated method stub
		pm=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
    	//pm = bm;
        filterPic.setImageBitmap(pm);
		if(mode == 1){
        	new thread(MSG_WATER).start();
        }else if(mode == 0){ 
        	new thread(MSG_OIL).start();
        }else if(mode == 2){
        	new thread(MSG_PIGMENT).start();
        }else if(mode == 3){
        	new thread(MSG_ELASTIC).start();
        }else if(mode == 4){
        	new thread(MSG_COLLAGEN).start();
        }
	}  

}
