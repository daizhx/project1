package com.jiuzhansoft.ehealthtec.lens.hair;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class HairView extends ImageView{
	
	HairAnalysisActivity mContext;
	
	private int screenWidth,screenHeight;
	private List<PointF> pointList = new ArrayList<PointF>();
	private String picPath;
	private int width,height;
	private boolean canBuild, analysis;
	private Region pathRegion;
	
	private Bitmap resizeBitmap;
	private Path clip;
	private RectF rf;
	private Paint mPaint, circlePaint;
	
	public static final int HAIR_WATER = 1;
	public static final int HAIR_GLOSS = 2;
	public static final int HAIR_ELASTIC = 3;
	public static final int HAIR_DETECTION = 4;
	private int analysisMode;
	
	private int totalPixelNum;
	private int waterNum;
	public static int waterColor = 76;
	private int glossNum;
	public static int glossMinColor = 170;
	public static int glossMaxColor = 230;
	private int elasticNum;
	public static int elasticMinColor = 111;
	public static int elasticMaxColor = 146;
	
	public interface OnCanAnalysisListener{
		void onCanAnalysis();
	}
	private OnCanAnalysisListener onCanAnalysisListener;
	
	public void setOnCanAnalysisListener(OnCanAnalysisListener listener){
		onCanAnalysisListener = listener;
	}
	
	private Handler handler;
	public HairView(Context context){
		super(context);
		mContext = (HairAnalysisActivity) context;
		clip = new Path();
		pathRegion = new Region();
		mPaint = new Paint();  
		mPaint.setAntiAlias(true);  
		mPaint.setStyle(Style.STROKE); 
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(5.0f);
		mPaint.setTextSize(25.0f);
		
		circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.RED);
		circlePaint.setStrokeWidth(5.0f);
		
		handler = new Handler(){
			public void handleMessage(Message message){
				BigDecimal per = null;
				Intent intent = new Intent(mContext, HairAnalysisResultActivity.class);
				switch(analysisMode){
					case HAIR_WATER:
						per = new BigDecimal(((float)waterNum / totalPixelNum * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", HAIR_WATER);
						break;
					case HAIR_GLOSS:
						per = new BigDecimal(((float)glossNum / totalPixelNum * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", HAIR_GLOSS);
						break;
					case HAIR_ELASTIC:
						per = new BigDecimal(((float)elasticNum / totalPixelNum * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", HAIR_ELASTIC);
						break;
					default:
						break;
				}
				intent.putExtra("content", per.intValue());
				mContext.startActivity(intent);
			}
		};
	}
	
	public HairView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = (HairAnalysisActivity) context;
		clip = new Path();
		pathRegion = new Region();
		mPaint = new Paint();  
		mPaint.setAntiAlias(true);  
		mPaint.setStyle(Style.STROKE); 
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(5.0f);
		mPaint.setTextSize(25.0f);
		
		circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.RED);
		circlePaint.setStrokeWidth(5.0f);
		
		handler = new Handler(){
			public void handleMessage(Message message){
				BigDecimal per = null;
				Intent intent = new Intent(mContext, HairAnalysisResultActivity.class);
				switch(analysisMode){
					case HAIR_WATER:
						per = new BigDecimal(((float)waterNum / totalPixelNum * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", HAIR_WATER);
						break;
					case HAIR_GLOSS:
						per = new BigDecimal(((float)glossNum / totalPixelNum * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", HAIR_GLOSS);
						break;
					case HAIR_ELASTIC:
						per = new BigDecimal(((float)elasticNum / totalPixelNum * 100)).setScale(0, BigDecimal.ROUND_HALF_UP);
						intent.putExtra("mode", HAIR_ELASTIC);
						break;
					default:
						break;
				}
				intent.putExtra("content", per.intValue());
				mContext.startActivity(intent);
			}
		};
	}
	
	public void setBounds(int width, int height){
		screenWidth = width;
		screenHeight = height;
		resizeBitmap = createBitmap();
	}
	
	public void setPicPath(String path){
		picPath = path;
	}
	
	public void setBuild(boolean build){
		canBuild = build;
		if(analysis == true)
			delete();
	}
	
	public void setAnalysis(boolean ana, int mode){
		analysis = ana;
		analysisMode = mode;
		resizeBitmap = createBitmap();
		postInvalidate();
		new thread().start();
	}
	
	public void cancel(){
		if(analysis == true){
			resizeBitmap = createBitmap();
			analysis = false;
		}
		if(pointList.size() > 0){
			pointList.remove(pointList.size() - 1);
			postInvalidate();
		}
	}
	
	public void delete(){
		pointList.clear();
		if(analysis == true){
			resizeBitmap = createBitmap();
			analysis = false;
		}
		postInvalidate();
	}
	
	public boolean canAnalysis(){
		return pointList.size() >= 3;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(analysis == true){
			canvas.drawBitmap(resizeBitmap, (screenWidth - resizeBitmap.getWidth()) / 2, (screenHeight - resizeBitmap.getHeight()) / 2, mPaint);
			canvas.clipPath(clip);
			canvas.drawPath(clip, mPaint);	
			return;
		}
		
		canvas.drawBitmap(resizeBitmap, (screenWidth - resizeBitmap.getWidth()) / 2, (screenHeight - resizeBitmap.getHeight()) / 2, mPaint); 
		clip.reset();  
		Iterator<PointF> iter = pointList.iterator();
		int i = 0;
		while(iter.hasNext()){
			PointF point = (PointF)iter.next();
			if(i == 0)
				clip.moveTo(point.x, point.y);	
			clip.lineTo(point.x, point.y);
			canvas.drawCircle(point.x, point.y, 5, circlePaint);
			i++;
		}
		clip.close();
		rf = new RectF();
		clip.computeBounds(rf, true);
		pathRegion.setPath(clip, new Region((int)rf.left, (int)rf.top, (int)rf.right, (int)rf.bottom));
		//canvas.save();//保存画布   
		canvas.clipPath(clip, Region.Op.DIFFERENCE); 
		canvas.drawColor(Color.argb(100, 0, 0, 0)); 
		//canvas.save(Canvas.ALL_SAVE_FLAG );
		//canvas.restore(); 
		canvas.drawPath(clip, mPaint);
	
		
	}
	public Bitmap createBitmap(){
		Bitmap bitmap = BitmapFactory.decodeFile(picPath);
		float scale = Math.min((float)screenWidth / bitmap.getWidth(), (float)screenHeight / bitmap.getHeight());
		Matrix matrix = new Matrix(); 
		matrix.postScale(scale, scale);
		bitmap= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		return bitmap;
	}
	class thread extends Thread{   
    	public void run() { 
    		totalPixelNum = 0;
    		waterNum = 0;
    		glossNum = 0;
    		elasticNum = 0;
    	    int[] colors=new int[width];
    	    for(int i=(int)rf.top - (screenHeight - resizeBitmap.getHeight()) / 2;i<(int)rf.bottom - (screenHeight - resizeBitmap.getHeight()) / 2;i++){   
    	        resizeBitmap.getPixels(colors, 0, width, 0, i, width, 1);   
    	        for(int j=0;j<colors.length;j++){ 
    	        	if(pathRegion.contains((screenWidth - resizeBitmap.getWidth()) / 2 + j, (screenHeight - resizeBitmap.getHeight()) / 2 + i)){
    	        		totalPixelNum++;
    	        		int color = (int)(Color.red(colors[j]) * 0.3 + Color.green(colors[j]) * 0.59 + Color.blue(colors[j]) * 0.11);
    	        		colors[j]=Color.rgb(color,color, color); 
    	        		switch(analysisMode){
    	        			case HAIR_WATER:
    	        				if(color <= waterColor){
    	        					waterNum ++;
        	        				colors[j]=Color.rgb(0, 0, 255);
    	        				}
    	        				break;
    	        			case HAIR_GLOSS:
    	        				if(color >= glossMaxColor){
        	    	        		colors[j]=Color.rgb(255, 255, 255);  
        	    	        	}else if(color >= glossMinColor){
        	    	        		glossNum++;
        	    	        		colors[j]=Color.rgb(255, 0, 0); 
        	    	        	}
    	        				break;
    	        			case HAIR_ELASTIC:
    	        				if(color <= elasticMaxColor && color >= elasticMinColor){
    	        					elasticNum++;
    	        					colors[j]=Color.rgb(0, 255, 0);
    	        				}
    	        				break;
    	        			default:
    	        				break;
    	        		}
    	        	}
    	        }   
    	        resizeBitmap.setPixels(colors, 0, width, 0, i, width, 1);
    	        postInvalidate();
    	        try {
					sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	    resizeBitmap = createBitmap();
    	    postInvalidate();
    	    handler.sendEmptyMessage(0);
    	}   
    }   
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
			
			
				break;
			case MotionEvent.ACTION_MOVE:
			
			
				break;
			case MotionEvent.ACTION_UP:
				if(analysis == true || canBuild == false)
					return false;
				PointF point = new PointF();
				point.set(event.getX(), event.getY());
				pointList.add(point);
				postInvalidate();
				//添加选区已经创建的通知
				if(canAnalysis()){
					if(onCanAnalysisListener != null){
						onCanAnalysisListener.onCanAnalysis();
					}
				}
				break;
			default:
				break;
		}
		return true;
	}
	
}
