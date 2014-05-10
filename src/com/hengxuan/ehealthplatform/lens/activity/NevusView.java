package com.hengxuan.ehealthplatform.lens.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hengxuan.ehealthplatform.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NevusView extends ImageView {

	
	private int screenWidth,screenHeight;
	private List<PointF> pointList = new ArrayList<PointF>();
	private static String picPath;
	private int width,height;
	private boolean canBuild, analysis;
	private Region pathRegion;
	
	NevusAnalysisActivity mContext;
	
	private static Bitmap resizeBitmap;
	
	public static String getOriginalBitmap(){
		return picPath;
	}
	
	public static Bitmap getResizeBitmap(){
		return resizeBitmap;
	}
	
	private Path clip;
	private RectF rf;
	private Paint mPaint, circlePaint;
	
	public static final int HAIR_WATER = 1;
	public static final int HAIR_GLOSS = 2;
	public static final int HAIR_ELASTIC = 3;
	public static final int HAIR_DETECTION = 4;
	
	private int totalPixelNum;
	
	private int deepCount;
	private int lightCount;
	
	private Handler handler;
	public NevusView(Context context){
		super(context);
	}
	
	public NevusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = (NevusAnalysisActivity) context;
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
				mContext.setButtonEnable(true);
				final AlertDialog alertdialog = (new AlertDialog.Builder(mContext)).create();
				alertdialog.show();
				Window window = alertdialog.getWindow();
				window.setContentView(R.layout.my_alertdialog);
//				float rate = (float)lightCount / deepCount;
				float rate = (float)deepCount / lightCount;
				String result = "";
				if(rate <= 50)
					result = mContext.getResources().getString(R.string.low_risk);
				else if(rate < 100)
					result = mContext.getResources().getString(R.string.moderate_risk);
				else
					result = mContext.getResources().getString(R.string.high_risk);
				((TextView) window.findViewById(R.id.msgtv)).setText(mContext.getResources().getString(R.string.analysis_result) + "\n" + result);
				((LinearLayout) window.findViewById(R.id.btnll)).setVisibility(View.VISIBLE);
				((Button) window.findViewById(R.id.btn3)).setVisibility(View.GONE);
				Button button1 = (Button) window.findViewById(R.id.btn1);
				button1.setText(getResources().getString(R.string.file));
				button1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						saveResizeBitmap();
						Intent intent = new Intent(mContext, SaveFileActivity.class);
						mContext.startActivity(intent);
						alertdialog.dismiss();
						
					}
				});
				Button button2 = (Button) window.findViewById(R.id.btn2);
				button2.setText(getResources().getString(R.string.return_text));
				// button2.setOnClickListener(dialogKeyListener2);
				button2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						alertdialog.cancel();
					}
				});
			}
		};
	}
	
	public void saveResizeBitmap(){
		this.setDrawingCacheEnabled(true);
		resizeBitmap = this.getDrawingCache();
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
	
	public void setAnalysis(boolean ana){
		analysis = ana;
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
		//canvas.save();//±£´æ»­²¼   
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
    		deepCount = 0;
    		lightCount = 0;
    	    int[] colors=new int[width];
    	    for(int i=(int)rf.top - (screenHeight - resizeBitmap.getHeight()) / 2;i<(int)rf.bottom - (screenHeight - resizeBitmap.getHeight()) / 2;i++){
    	        resizeBitmap.getPixels(colors, 0, width, 0, i, width, 1);
    	        for(int j=0;j<colors.length;j++){
    	        	if(pathRegion.contains((screenWidth - resizeBitmap.getWidth()) / 2 + j, (screenHeight - resizeBitmap.getHeight()) / 2 + i)){
    	        		totalPixelNum++;
    	        		int color = (int)(Color.red(colors[j]) * 0.3 + Color.green(colors[j]) * 0.59 + Color.blue(colors[j]) * 0.11);
    	        		colors[j]=Color.rgb(color,color, color);
    	        		if(color <= 94){
    	        			colors[j] = Color.rgb(0, 100, 0);
    	        			deepCount++;
    	        		}else if(color >= 95 && color <= 124){
    	        			colors[j] = Color.rgb(100, 0, 100);
    	        			lightCount++;
    	        		}else if(color >= 125 && color <= 140){
    	        			colors[j] = Color.rgb(100, 0, 0);
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
    	    //resizeBitmap = createBitmap();
    	    //postInvalidate();
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
				break;
			default:
				break;
		}
		return true;
	}
	

}
