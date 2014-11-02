package com.jiuzhansoft.ehealthtec.iris.old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.lens.iris.IrisDataCache;
import com.jiuzhansoft.ehealthtec.lens.iris.IrisDetailInfoActivity;
import com.jiuzhansoft.ehealthtec.lens.iris.Organ;
import com.jiuzhansoft.ehealthtec.log.Log;

@SuppressLint("WrongCall")
public class IrisImageView extends View {
	private static final String TAG = "IrisImageView";
	
	public static final int  OBJECT_COUNT = 2;
	public static final int OBJECT_CONTENT_INDEX = 0;
	public static final int OBJECT_MODELE_INDEX = 1;
	public static final int OBJECT_COMPONENT_INDEX = 2;
	
	private List<TouchObject> objList = null;
	//第一个是左眼，第二个是右眼
	private TouchObject[] completedObjList = null;
	private boolean isSetGalleryWidth[];
	private int galleryHeight[];
	private boolean needHandle = false;
	//用于标识图片是否已经合并
	private boolean[] isMerged;
	private PopupWindow pop = null;
	//private Timer mTimer = null;
	//private MyTimerTask myTimerTask = null;
	
	private boolean isCompleted[];
	private int colorId;
	private float[] current_Raduis, current_MinRaduis, current_MidRaduis;
	
	private float[] x1, y1, x2, y2, x3, y3, x4, y4;
	
	private Canvas canvas;
	private int getindex;
	
	public void setIndex(int index){
		getindex = index;
	}
	
	// 合并完成后合并中心点距标尺中心xy差值 (点击事件之前)
	private float[] dXx, dYy;
	private float[] get_r, d2, d4;
	private float[] get_minr, get_midr;
	
	public void isCompleted(boolean isCompleted){
		this.isCompleted[getindex] = isCompleted;
	}
	
	public void setColorId(int colorId){
		this.colorId = colorId;
	}
 	
	//Constructor
	public IrisImageView(Context context) {
		this(context, null);
	}

	public IrisImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//this.mTimer = new Timer();
		needHandle = false;
		this.completedObjList = new TouchObject[2];
		//this.initObjList();
		x1 = new float[2];
		y1 = new float[2];
		x2 = new float[2];
		y2 = new float[2];
		x3 = new float[2];
		y3 = new float[2];
		x4 = new float[2];
		y4 = new float[2];
		dXx = new float[2];
		dYy = new float[2];
		get_r = new float[2];
		d2 = new float[2];
		d4 = new float[2];
		get_minr = new float[2];
		get_midr = new float[2];
		current_Raduis = new float[2];
		current_MinRaduis = new float[2];
		current_MidRaduis = new float[2];
		isMerged = new boolean[2];
		isSetGalleryWidth = new boolean[2];
		isSetGalleryWidth[0] = true;
		isSetGalleryWidth[1] = true;
		galleryHeight = new int[2];
		galleryHeight[0] = -1;
		galleryHeight[1] = -1;
		isCompleted = new boolean[2];
		p = new int[2];
		reportxl = new int[2];
		reportxr = new int[2];
		reportyt = new int[2];
		reportyb = new int[2];
		
	}
	
	public boolean isMerge(){
		return isMerged[getindex];
	}
	
	public void initObjList(String[] iris_image_paths,int iris_index){
		if(Log.D){
			Log.d(TAG, "+++++++++++initObjList+++++++++++++++++");
		}
		if(this.objList == null){
			this.objList = new ArrayList<TouchObject>();
		}else{
			//删除所有的元素
			this.objList.clear();
		}
		
		if (this.completedObjList[iris_index] == null) {
			TouchObject to = new TouchObject(this);
			if (iris_image_paths[iris_index] == null) {// 如果为空则设置默认图片
				// to.init(this.getContext(), BitmapFactory.decodeResource(
						// this.getResources(), R.drawable.eye_test), 0);
				Bitmap getBm = BaseActivity.readBitmap(getContext(), R.drawable.eye_test);
				to.init(this.getContext(), getBm, iris_index, 4.0f);
			} else {
				
				BitmapFactory.Options bfOptions=new BitmapFactory.Options();
			    bfOptions.inDither=false;                     
			    bfOptions.inPurgeable=true;                 
			    bfOptions.inInputShareable=true;             
			    bfOptions.inTempStorage=new byte[32 * 1024]; 


			    File file=new File(iris_image_paths[iris_index]);
			    FileInputStream fs=null;
			    try {
			        fs = new FileInputStream(file);
			    } catch (FileNotFoundException e) {
			        e.printStackTrace();
			    }

			    try {
			        if(fs!=null) {
			        	Bitmap bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
			        	to.init(this.getContext(), bm, 0, 3.0f);
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    } finally{ 
			        if(fs!=null) {
			            try {
			                fs.close();
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        }
			    }
				
				//BitmapFactory.Options opt = new BitmapFactory.Options();
				//opt.inSampleSize = 4;
				//Bitmap bmp = BitmapFactory
						//.decodeFile(iris_image_paths[iris_index], opt);
			    //to.init(this.getContext(), bmp, 0);
			}
			to.setHandlerTouchEvent(true);
			this.objList.add(to);
			// model pic
			/*to = new TouchObject(this);
			if(iris_index == 0){
				to.init(this.getContext(), BitmapFactory.decodeResource(
						this.getResources(), R.drawable.eye_model_left), 0);				
			}else if(iris_index == 1){
				to.init(this.getContext(), BitmapFactory.decodeResource(
						this.getResources(), R.drawable.eye_model_right), 0);
			}
			to.setHandlerTouchEvent(false);
			this.objList.add(to);*/
		}else{
			this.completedObjList[iris_index].setHandlerTouchEvent(true);
			this.objList.add(this.completedObjList[iris_index]);
		}
		
		//test pic
		
	}
	
	public void resetGalleryWidth(int height){
		if(this.isSetGalleryWidth[getindex] == false){
			return;
		}
		for(Iterator<TouchObject> it = this.objList.iterator();it.hasNext();){
			TouchObject to = it.next();
			to.setGalleryHeight(this.getResources(), height);
		}
		this.galleryHeight[getindex] = height;
		this.isSetGalleryWidth[getindex] = false;
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		canvas.drawColor(Color.rgb(170, 170, 170));
//		super.onDraw(canvas);
//		/*for(Iterator<TouchObject> it = this.objList.iterator();it.hasNext();){
//			TouchObject to = it.next();
//			if(to.isShow()){
//				to.onDraw(canvas);
//			}
//		}*/
//		TouchObject to = this.objList.get(0);
//		if(to.isShow())
//			to.onDraw(canvas);
//	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.rgb(170, 170, 170));
		super.onDraw(canvas);
		TouchObject to = this.objList.get(0);
		if(to.isShow()){
			to.onDraw(canvas);
		}
		
	}
	/**
	 * 触屏处理函数
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isHandled = false;
		
		//return super.onTouchEvent(event);
		for(Iterator<TouchObject> it = this.objList.iterator();it.hasNext();){
			TouchObject to = it.next();
			if(to.isHandlerTouchEvent() == true){
				isHandled = to.onTouchEvent(event);
			}
		}
		if(this.isMerged[getindex] == false){
			return true;
		}
		/*float prePointX = -1f;
		float prePointY = -1f;
		// 合并后图片中心点距离合并后图片左边距
		float getD_disX = (this.objList.get(0).getImg().getWidth())/2;
		// 合并后标尺圆中心到左边距距离
		float getd_disX = x3 - (this.objList.get(0).getImg().getMinX());
		// 求得大小边距比例
		float getRatioX = getD_disX / getd_disX;
		
		// 合并后图片中心点距离合并后图片底边距
		float getD_disY = (this.objList.get(0).getImg().getHeight())/2;
		// 合并后标尺圆中心到底边距距离
		float getd_disY = (this.objList.get(0).getImg().getMaxY()) - y3;
		// 求得大小边距比例
		float getRatioY = getD_disY / getd_disY;
		
		float oldDistance = -1f;*/
		
		// 记录当前手势是拖拽还是缩放
		/*int NONE = 0;
		int DRAG = 1;
		int ZOOM = 2;
		int mode = NONE;*/
		
		Log.i("mEvent", "x3 = "+x3+"  y3 = "+y3);
		//获取当前位置的信息
		//switch (event.getAction() & MotionEvent.ACTION_MASK) { 
		switch (event.getAction()) { 
		// 第一根手指点下
		case MotionEvent.ACTION_DOWN:
			/*prePointX = event.getX();
			prePointY = event.getY();
			mode = DRAG;*/
			removePop();
			this.needHandle = true;
			break;
		// 第二根手指点下 (添加)
		/*case MotionEvent.ACTION_POINTER_DOWN:
			oldDistance = (float) Math.sqrt((event.getX(0) - event.getX(1))
					* (event.getX(0) - event.getX(1)) + (event.getY(0) - event.getY(1))
					* (event.getY(0) - event.getY(1)));
			if(oldDistance > 10f){ 
                mode = ZOOM;  
			}
			this.needHandle = false;
			break;*/
		case MotionEvent.ACTION_MOVE:
			// this.needHandle = false;
			// 拖拽(补充)
			/*if(mode == DRAG){
				if(prePointX != -1f && prePointY != -1f){
					if(event.getX() >= prePointX)
						x3 = x3 + event.getX() - prePointX;
					else if(event.getX() < prePointX)
						x3 = x3 - (prePointX - event.getX());
					if(event.getY() >= prePointY)
						y3 = y3 + event.getY() - prePointY;
					else if(event.getY() < prePointY)
						y3 = y3 - (prePointY - event.getY());
				}
			}
			
			// 缩放模式  (补充)
			if(mode == ZOOM){
				float newDist =  (float) Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) + (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));   
				if (newDist > 10f){
					float scale = newDist / oldDistance;
					x3 = (getD_disX * scale) / getRatioX;
					x3 = x3 + this.objList.get(0).getImg().getMinX();
					
					y3 = (getD_disY * scale) / getRatioY;
					y3 = this.objList.get(0).getImg().getMaxY() - y3;
				}
			}*/
			this.needHandle = true;
			break;
		case MotionEvent.ACTION_CANCEL:
			this.needHandle = false;
			break;
		case MotionEvent.ACTION_UP:
			// mode = NONE;
			if (this.needHandle == true) {
				// 判断点击的位置信息
				if(Log.I){
					Log.i(TAG, "touch point info x = " + event.getX() + ",y="
							+ event.getY());
					Log.i(TAG, "中心点坐标：point.x = " + this.objList.get(0).getImg().getCenterX() + ",y=" + this.objList.get(0).getImg().getCenterY());
					Log.i(TAG, "x-scale factor=" + this.objList.get(0).getImg().getScaleX());
					Log.i(TAG, "y-scale factor=" + this.objList.get(0).getImg().getScaleY());
				}
				
				IrisDataCache dataCache = IrisDataCache.getInstance();
				dataCache.setContext(this.getContext());
				float scale_x = this.objList.get(0).getImg().getScaleX();
				float scale_y = this.objList.get(0).getImg().getScaleY();
				/*current_Raduis = this.objList.get(0).getImg().getCenterX() - this.objList.get(0).getImg().getMinX();
				current_Raduis = current_Raduis * scale_x;*/
				/*Bitmap mStandardBitmap = new BitmapFactory().decodeResource(getResources(), R.drawable.eye_model_left);
				current_Raduis = mStandardBitmap.getWidth();
				current_Raduis = current_Raduis/2;*/
				/*if(x3 != 0 || y3 != 0){
				}else{
					Toast.makeText(getContext(), getResources().getString(R.string.not_range), Toast.LENGTH_SHORT).show();
				}*/
				// Log.i("mEvent", "x3 = "+x3+"  y3 = "+y3);
				/*event.getHistoricalX(pos);
					event.getHistoricalY(pos)*/
				
				/*Organ organ = dataCache.getOrganIdByPositionInfo(event.getX(),
							event.getY(),
							this.objList.get(0).getImg().getCenterX(), this.objList
							.get(0).getImg().getCenterY(),current_Raduis, scale_x,scale_y, true);*/
				/*if(x3 <= this.objList.get(0).getImg().getCenterX())
						x3 = this.objList.get(0).getImg().getCenterX() - (dXx * scale_x);
					else
						x3 = this.objList.get(0).getImg().getCenterX() + (dXx * scale_x);
					
					if(y3 <= this.objList.get(0).getImg().getCenterY())
						y3 = this.objList.get(0).getImg().getCenterY() - (dYy * scale_x);
					else
						y3 = this.objList.get(0).getImg().getCenterY() + (dYy * scale_x);*/
				
				/*// 2-19
					if(x1 <= x2)
						x3 = this.objList.get(0).getImg().getCenterX() - (dXx * scale_x);
					else
						x3 = this.objList.get(0).getImg().getCenterX() + (dXx * scale_x);*/
				if(x3[getindex] != 0 && y3[getindex] != 0){
					x3[getindex] = this.objList.get(0).getImg().getCenterX() - (dXx[getindex] * scale_x);				
					/*if(y1 <= y2)
						y3 = this.objList.get(0).getImg().getCenterY() - (dYy * scale_x);
					else
						y3 = this.objList.get(0).getImg().getCenterY() + (dYy * scale_x);*/
					/*// 2-19
					if(y1 <= y2)
						y3 = this.objList.get(0).getImg().getCenterY() - (dYy * scale_y);
					else
						y3 = this.objList.get(0).getImg().getCenterY() + (dYy * scale_y);*/
					y3[getindex] = this.objList.get(0).getImg().getCenterY() - (dYy[getindex] * scale_y);
					
					// dx34 = Math.abs(x3-this.objList.get(0).getImg().getCenterX());
					// current_Raduis = current_Raduis * scale_x;
					current_Raduis[getindex] = this.objList.get(0).getImg().getCenterX() - this.objList.get(0).getImg().getMinX();
					current_Raduis[getindex] = (current_Raduis[getindex] * get_r[getindex]) / d2[getindex];
					// current_Raduis = y5 - y3;
					// current_Raduis = current_Raduis*scale_x;
					// current_Raduis = current_Raduis * scale_x;
					// current_Raduis = (dx34*get_r)/dx12;
					// current_Raduis = get_r*d4/d2;
					// current_Raduis = current_Raduis*scale_x;
					current_MinRaduis[getindex] = (current_Raduis[getindex]*get_minr[getindex]) / get_r[getindex];
					current_MidRaduis[getindex] = (current_Raduis[getindex]*get_midr[getindex]) / get_r[getindex];					
					Organ organ = dataCache.getOrganIdByPositionInfo(event.getX(),
							event.getY(), x3[getindex], y3[getindex], current_Raduis[getindex], 
							current_MinRaduis[getindex], current_MidRaduis[getindex], scale_x,scale_y, 
							getindex == 0 ? true: false);
					if (organ != null) {
						if (Log.D) {
							Log.d(TAG, "organ id=" + organ.getOrganId());
						}
						// 显示弹出框
						if(isCompleted[getindex])
							this.displayPopupInfo(organ, event.getX(), event.getY());
					}
					
				}
			}
			this.needHandle = false;
			break;
		/*case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			this.needHandle = false; //(补充)
			break;*/
		}
		return true;
	}
	
	private void intentData(int currentColor, Organ organ){
		if(currentColor != 0 ){
			Intent it = new Intent(IrisImageView.this.getContext(),IrisDetailInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("organ_info", organ);
			bundle.putInt("colorId", currentColor);
			it.putExtras(bundle);					
			IrisImageView.this.getContext().startActivity(it);					
		}else{
			Toast.makeText(getContext(), getResources().getString(R.string.nocolor), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void displayPopupInfo(final Organ organ,float x, float y) {
		//PopupWindow popWin = null;
		View popView = null;
		popView = LayoutInflater.from(this.getContext()).inflate(R.layout.overlay_pop, null);		
		popView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Log.D){
					Log.d(TAG, "在弹出框中按下按钮操作！！！");
				}
				if(colorId != 0 ){
						Intent it = new Intent(IrisImageView.this.getContext(),IrisDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("organ_info", organ);
						bundle.putInt("colorId",colorId);
						it.putExtras(bundle);					
						IrisImageView.this.getContext().startActivity(it);					
					}else{
						Toast.makeText(getContext(), getResources().getString(R.string.nocolor), Toast.LENGTH_SHORT).show();
					}
			}
		});
		TextView title = (TextView)popView.findViewById(R.id.map_bubbleTitle);
		TextView content = (TextView)popView.findViewById(R.id.map_bubbleText);
		title.setText(organ.getOrganId()+"");
		content.setText(organ.getName());
		popView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if(this.pop == null){
			try{
				this.pop = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				this.pop.setClippingEnabled(false);
				this.pop.showAtLocation(this, Gravity.LEFT | Gravity.TOP, (int)x-popView.getMeasuredWidth()/2, (int)y+this.galleryHeight[getindex]-popView.getMeasuredHeight());			
				this.pop.setFocusable(true);				
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			if(pop.isShowing()){
				this.pop.dismiss();
				this.pop = null;
			}else{
				this.pop = null;
				this.pop = new PopupWindow(popView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);	
				this.pop.setClippingEnabled(false);
				pop.showAtLocation(this, Gravity.LEFT | Gravity.TOP, (int)x-popView.getMeasuredWidth()/2, (int)y+this.galleryHeight[getindex]-popView.getMeasuredHeight());				
				this.pop.setFocusable(true);				
			}
		}
		
		/*if(this.mTimer != null){
			if(this.myTimerTask != null){
				this.myTimerTask.cancel();
			}
			this.myTimerTask = new MyTimerTask();
			this.mTimer.schedule(myTimerTask, 3000);
		}*/
	}

	private float getDoubleNum(float x){
		return x*x;
	}
	
	public String getX_Y(float x1, float y1, float x2, float y2, float x4, float y4, float getScale){
		
		// getDoubleNum(x2 - x1) + getDoubleNum(this.y2 - this.y1) = getDoubleNum(getScale)*(getDoubleNum(x4 - x3) + getDoubleNum(y4 - y3));
		
		return x3+","+y3;
	}
	
	public float getNumber_Minus(float x1, float x2){
		return x2 - x1;
	}
	
	public float getNumber_Plus(float x1, float x2){
		return x2 + x1;
	}
	
	public float getNumber_Division(float x1, float x2){
		return x2/x1;
	}
	
	private int p[];
	private int[] reportxl, reportxr, reportyt, reportyb;
	private void removeTransparent(Bitmap bmp){
    	int getw = bmp.getWidth();
    	int geth = bmp.getHeight();
    	reportxr[getindex] = getw-1;
    	reportyb[getindex] = geth-1;
    	for(int i=0; i<getw; i++){
    		for(int j=0; j<geth; j++){
    			p[getindex] = bmp.getPixel(i, j);
    			if((p[getindex]&0xff000000) != 0){
    				reportxl[getindex] = i;
    				break;
    			}
    		}
    		if(0 != reportxl[getindex])
    			break;
    	}
    	for(int i=getw-1; i>reportxl[getindex]; i--){
    		for(int j=0; j<geth; j++){
    			p[getindex] = bmp.getPixel(i, j);
    			if((p[getindex]&0xff000000) != 0){
    				reportxr[getindex] = i;
    				break;
    			}
    		}
    		if(reportxr[getindex] != getw-1)
    			break;
    	}
    	for(int i=0; i<geth; i++){
    		for(int j=0; j<getw; j++){
    			p[getindex] =bmp.getPixel(j, i);
    			if((p[getindex]&0xff000000) != 0){
    				reportyt[getindex] = i;
    				break;
    			}
    		}
    		if(reportyt[getindex] != 0)
    			break;
    	}
    	for(int i=geth-1; i>reportyt[getindex]; i--){
    		for(int j=0; j<getw; j++){
    			p[getindex] =bmp.getPixel(j, i);
    			if((p[getindex]&0xff000000) != 0){
    				reportyb[getindex] = i;
    				break;
    			}
    		}
    		if(reportyb[getindex] != geth-1)
    			break;
    	}
    }
	
	private Bitmap getBitmapFromView(float getCenterX, float getCenterY, int getRLen, View mView){
    	/*Bitmap getBitmap = Bitmap.createBitmap((getRLen * 2), (getRLen * 2), Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(getBitmap);
    	canvas.translate((getCenterX - getRLen), (getCenterY - getRLen));
    	mView.draw(canvas);*/
		mView.setDrawingCacheEnabled(true);
		Bitmap getBitmap = mView.getDrawingCache();
		/*if(null == getBitmap)
			Log.i("getBitmap", "getBitmap is null");*/
		removeTransparent(getBitmap);
		Bitmap dropTransparent = Bitmap.createBitmap(getBitmap, reportxl[getindex], reportyt[getindex], (reportxr[getindex]-reportxl[getindex]), (reportyb[getindex]-reportyt[getindex]));
    	return dropTransparent;
    }
	
	public void setTouchEventHandler(int index,int iris_index, View getCurrentView, PointF getP, float getR, float getMinR, float getMidR) {
		if(index == 2){//合并前两张图片
			if(Log.D){
				Log.d(TAG,"合并图片！！");
			}
			getindex = iris_index;
			TouchObject to = new TouchObject(this);
			Bitmap retBmp = this.mergeBitmap(getCurrentView, getP, getR);
			//this.saveBitmap(retBmp);  //1-24
			if(this.isSetGalleryWidth[getindex] == false){
				to.init(this.getContext(), retBmp, this.galleryHeight[getindex], 10.0f);
			}else{
				to.init(this.getContext(), retBmp, 0, 10.0f);
			}
			to.setHandlerTouchEvent(true);  //1-24
			
			// float getScale = this.objList.get(1).getImg().getScaleX();
			// x1 = this.objList.get(1).getImg().getCenterX();
			// y1 = this.objList.get(1).getImg().getCenterY();
			x2[iris_index] = this.objList.get(0).getImg().getCenterX();
			y2[iris_index] = this.objList.get(0).getImg().getCenterY();
			
			// 合并前标尺圆半径
			// get_r = x1 - this.objList.get(1).getImg().getMinX();
			// 合并前标尺圆最左边到虹膜左边距离
			float d1_leftx = this.objList.get(1).getImg().getMinX() - this.objList.get(0).getImg().getMinX();
			// 合并前虹膜右边到标尺圆右边距离
			float d1_rightx = this.objList.get(0).getImg().getMaxX() - this.objList.get(1).getImg().getMaxX();
			// 合并前虹膜顶部到标尺圆顶部距离
			float d2_topy = this.objList.get(0).getImg().getMaxY() - this.objList.get(1).getImg().getMaxY();
			// 合并前标尺圆底部到虹膜底部距离
			float d2_bottomy = this.objList.get(1).getImg().getMinY() - this.objList.get(0).getImg().getMinY();
			
			// 补充代码(2-18)
			get_r[getindex] = getR;
			get_minr[getindex] = getMinR;
			get_midr[getindex] = getMidR;
			d1_leftx = (getP.x - getR - 2) - this.objList.get(0).getImg().getMinX();
			d1_rightx = this.objList.get(0).getImg().getMaxX() - (getP.x + getR + 2);
			d2_topy = (getP.y - getR - 2) - this.objList.get(0).getImg().getMinY();
			d2_bottomy = this.objList.get(0).getImg().getMaxY() - (getP.y + getR + 2);
			x1[iris_index] = getP.x;
			// 合并前标尺圆心到虹膜左边距离
			float d1 = x1[iris_index] - this.objList.get(0).getImg().getMinX();
			// 合并前虹膜中心到虹膜左边距离
			d2[getindex] = x2[iris_index] - this.objList.get(0).getImg().getMinX();
			
			// 补充代码(2-18)
			y1[iris_index] = getP.y;
			// 合并前标尺圆心到虹膜底部距离(运算顺序改)
			float h1 = this.objList.get(0).getImg().getMaxY() - y1[iris_index];
			// 合并前虹膜中心到虹膜底部距离(运算顺序改)
			// float h2 = this.objList.get(0).getImg().getMinY() - y2;
			float h2 = this.objList.get(0).getImg().getMaxY() - y2[iris_index];
			// current_Raduis = get_r1 / getScale;
			
			boolean isInternal = true;
			if(d1_leftx < 0 && d1_rightx > 0){
				isInternal = false;
				d1 = getR;
				d2[getindex] =(this.objList.get(0).getImg().getMaxX() - this.objList.get(0).getImg().getMinX() - d1_leftx)/2;
			}else if(d1_leftx < 0 && d1_rightx < 0){
				isInternal = false;
				d2[getindex] = getR;
			}else if(d1_leftx > 0 && d1_rightx < 0){
				isInternal = false;
				d1 = getR;
				d2[getindex] =(this.objList.get(0).getImg().getMaxX() - this.objList.get(0).getImg().getMinX() - d1_rightx)/2;
			}
			
			if(!isInternal){
				if(d2_bottomy >0 && d2_topy > 0){}
				else if(d2_bottomy < 0 && d2_topy > 0){
					h1 = getR;
					h2 =(this.objList.get(0).getImg().getMaxY() - this.objList.get(0).getImg().getMinY() - d2_bottomy)/2;
				}else if(d2_bottomy < 0 && d2_topy <0){}
				else if(d2_bottomy > 0 && d2_topy < 0){
					h1 = getR;
					h2 =(this.objList.get(0).getImg().getMaxY() - this.objList.get(0).getImg().getMinY() - d2_topy)/2;
				}				
			}
			
			//移除原始的两张图
			this.objList.remove(1);
			this.objList.remove(0);
			//将新的图片增加到列表中
			this.objList.add(to);
			
			x4[iris_index] = this.objList.get(0).getImg().getCenterX();
			y4[iris_index] = this.objList.get(0).getImg().getCenterY();			
			// 合并后虹膜中心到虹膜左边距离
			d4[getindex] = x4[iris_index] - this.objList.get(0).getImg().getMinX();
			// 合并后虹膜中心到虹膜底部距离(运算顺序改)
			// float h4 = y4 - this.objList.get(0).getImg().getMinY();
			float h4 = this.objList.get(0).getImg().getMaxY() - y4[iris_index];
			
			/*x3 = this.objList.get(0).getImg().getMinX() + (d1 * d4)/d2;
			y3 = this.objList.get(0).getImg().getMaxY() - (h1 * h4)/h2;
			y5 = (y4*(y1+getR))/y2;
			// dx12 = Math.abs(x1-x2);
			dYy5 = this.objList.get(0).getImg().getCenterY() - y5;*/
			
			if(isInternal){
				if(d1_leftx > 0 && d1_rightx > 0 && d2_topy > 0 && d2_bottomy > 0){
					x3[iris_index] = this.objList.get(0).getImg().getMinX() + (d1 * d4[getindex])/d2[getindex];
					y3[iris_index] = this.objList.get(0).getImg().getMaxY() - (h1 * h4)/h2;
				}
			}else if(d1_leftx < 0 && d1_rightx > 0){
				x3[iris_index] = this.objList.get(0).getImg().getMinX() + (d1*d4[getindex])/d2[getindex];
			}else if(d1_leftx < 0 && d1_rightx < 0){
				x3[iris_index] = this.objList.get(0).getImg().getCenterX();	
			}else if(d1_leftx > 0 && d1_rightx < 0){
				x3[iris_index] = this.objList.get(0).getImg().getMaxX() - (d1*d4[getindex])/d2[getindex];
			}
			
			if(!isInternal){
				if(d2_bottomy >0 && d2_topy > 0){
					y3[iris_index] = this.objList.get(0).getImg().getMaxY() - (h1 * h4)/h2;				
				}
				else if(d2_bottomy < 0 && d2_topy > 0){
					y3[iris_index] = this.objList.get(0).getImg().getMaxY() - (h1 * h4)/h2;
				}else if(d2_bottomy < 0 && d2_topy <0){
					y3[iris_index] = this.objList.get(0).getImg().getCenterY();
				}else if(d2_bottomy > 0 && d2_topy < 0){
					y3[iris_index] = this.objList.get(0).getImg().getMinY() + (h1 * h4)/h2;
				}				
			}

			if(x3[iris_index] != 0  && y3[iris_index] != 0){
				dXx[getindex] = this.objList.get(0).getImg().getCenterX() - x3[iris_index];
				dYy[getindex] = this.objList.get(0).getImg().getCenterY() - y3[iris_index];				
			}
			this.completedObjList[iris_index] = to;
			this.postInvalidate();
			this.isMerged[getindex] = true;
			return;
		}
		int i = 0;
		for(Iterator<TouchObject> it = this.objList.iterator();it.hasNext();){
			TouchObject to = it.next();
			if(index == i){
				to.setHandlerTouchEvent(true);
			}else{
				to.setHandlerTouchEvent(false);
			}
			i++;
		}
	}
	
	private Bitmap mergeBitmap(View getView, PointF getP, float getR){
		
		View getStandardView = getView;		
		PointF centerPoint = getP;
		float getStandardR = getR;
		Bitmap standardBmp = null;
		if(getStandardR != 0f){
			standardBmp = getBitmapFromView(centerPoint.x, centerPoint.y, (int)getStandardR, getStandardView);
			TouchObject to = new TouchObject(this);
			if(standardBmp != null)
				to.init(getContext(), standardBmp, 0, 1.0f);
			to.setHandlerTouchEvent(true);
			this.objList.add(to);
			// Log.i("isExsit","objList.size() ="+objList.size());
		}
		
		x1[getindex] = getP.x;
		y1[getindex] = getP.y;
		//this.objList.get(1).getImg().zoomToMerge();
		//this.objList.get(0).getImg().zoomToMerge();
		//Bitmap newb = null;
		//获取合并后的图像的宽度
		/*float  min_position_x  = Math.min(this.objList.get(0).getImg().getMinX(),
				this.objList.get(1).getImg().getMinX());
		float  max_position_x  = Math.max(this.objList.get(0).getImg().getMaxX(),
				this.objList.get(1).getImg().getMaxX());*/
		float  min_position_x  = Math.min(this.objList.get(0).getImg().getMinX(), reportxl[getindex]);
		float  max_position_x  = Math.max(this.objList.get(0).getImg().getMaxX(), reportxr[getindex]);
		//计算合并后的图片的宽度
		 int bmpWidth = (int)(max_position_x - min_position_x);
		 // bmpWidth = (int) (this.objList.get(0).getImg().getMaxX() - this.objList.get(0).getImg().getMinX());
		//获取合并后的图像的高度
		/*int min_position_y = (int)Math.min(this.objList.get(0).getImg().getMinY(),
				this.objList.get(1).getImg().getMinY());
		int max_position_y = (int)Math.max(this.objList.get(0).getImg().getMaxY(),
				this.objList.get(1).getImg().getMaxY());*/
		 int min_position_y = (int)Math.min(this.objList.get(0).getImg().getMinY(), reportyt[getindex]);
			int max_position_y = (int)Math.max(this.objList.get(0).getImg().getMaxY(), reportyb[getindex]);
		//计算合并后的图片的高度
		int bmpHeight = (int)(max_position_y - min_position_y);
		// bmpHeight = (int) (this.objList.get(0).getImg().getMaxY() - this.objList.get(0).getImg().getMinY());
		//根据图片大小创建一张空的图片
		Bitmap newb = Bitmap.createBitmap(bmpWidth,bmpHeight,Config.ARGB_8888);
		canvas = new Canvas(newb);
		//计算合并前的每张图片的偏移值
		float object_position_x = this.objList.get(0).getImg().getMinX();
		// float model_position_x = this.objList.get(1).getImg().getMinX();// =0;
		float object_position_y = this.objList.get(0).getImg().getMinY();
		// float model_position_y = this.objList.get(1).getImg().getMinY();// =0;
		// Log.e("isrun", "model_position_x = "+model_position_x+"\tmodel_position_y = "+model_position_y);
		// model_position_x = model_position_x + (x1 - x1_offset);
		// model_position_y = model_position_y + (y1 - y1_offset);
		float object_offset_x = 0.0f;
		float object_offset_y = 0.0f;
		float model_offset_x = 0.0f;
		float model_offset_y = 0.0f;
		/*//分别计算虹膜图片和标尺图片的x方向的偏移值
		if(object_position_x > model_position_x){
			//说明object小于model,那么下面的偏移位置是object的
			model_offset_x = 0.0f;
			object_offset_x = object_position_x - model_position_x;
		}else{
			object_offset_x = 0.0f;
			model_offset_x = model_position_x - object_position_x;
		}
		//分别计算虹膜图片和标尺图片的y方向的偏移值
		if(object_position_y > model_position_y){
			model_offset_y = 0.0f;
			object_offset_y = object_position_y - model_position_y;
		}else{
			object_offset_y = 0.0f;
			model_offset_y = model_position_y - object_position_y;
		}*/
		
		//分别计算虹膜图片和标尺图片的x方向的偏移值
		if(object_position_x > reportxl[getindex]){
			//标尺在左边
			model_offset_x = 0.0f;
			object_offset_x = object_position_x - reportxl[getindex];
		}else{
			object_offset_x = 0.0f;
			model_offset_x = reportxl[getindex] - object_position_x;
		}
		//分别计算虹膜图片和标尺图片的y方向的偏移值
		if(object_position_y > reportyt[getindex]){
			model_offset_y = 0.0f;
			object_offset_y = object_position_y - reportyt[getindex];
		}else{
			object_offset_y = 0.0f;
			model_offset_y = reportyt[getindex] - object_position_y;
		}
		
		//先画虹膜图片
		//获取放大后的虹膜图片
		Bitmap irisBmp  = this.getScaledBitmap(0);
		//this.objList.get(0).getImg().getBitmap();
		
		//首先将虹膜图片画到画布上面
		canvas.drawBitmap(irisBmp, object_offset_x,object_offset_y, null);
		irisBmp.recycle();
		//释放用户方法后的虹膜图片
		//获取用户处理后的标尺图片
		canvas.drawBitmap(standardBmp, model_offset_x, model_offset_y, null);
		// canvas.drawBitmap(standardBmp, object_offset_x, object_offset_y, null);
		// canvas.drawBitmap(standardImg, matrix, drawPaint);
		//释放用户处理后的标尺图片
		// standardImg.recycle();
		standardBmp.recycle();
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		//if(Log.D){
			//Log.d(TAG, "---dest_witdh=" + newb.getWidth() + ",---dest_height="
				//	+ newb.getHeight());
		//}
		
		/*int objectXL = (int) (this.objList.get(0).getImg().getMinX()+0.5);
		int objectXR = (int) (this.objList.get(0).getImg().getMaxX()+0.5);
		int objectYT = (int) (this.objList.get(0).getImg().getMinY()+0.5);
		int objectYB = (int) (this.objList.get(0).getImg().getMaxY()+0.5);
		Drawable d[] = new Drawable[2];
        d[0] = new BitmapDrawable(irisBmp);
        d[1] = new BitmapDrawable(standardBmp);
        LayerDrawable ld = new LayerDrawable(d);
        ld.setLayerInset(0, objectXL, objectYT, objectXR, objectYB);
        ld.setLayerInset(1, reportxl, reportyt, reportxr, reportyb);
        Bitmap bmfromld = */
		//释放合并后的图片
		/*if(rBitmap.getWidth() != newb.getWidth()){
			newb.recycle();
		}*/
		//返回经过处理后的图片
		/*Bitmap rBitmap = this.getScaledBitmapByModelScale(newb);
		return rBitmap;*/
		return newb;
	}
	public void zoomIn(){
		for(Iterator<TouchObject> it = this.objList.iterator();it.hasNext();){
			TouchObject to = it.next();
			if(to.isHandlerTouchEvent() == true){
				to.getImg().zoomIn();
			}
		}
	}
	public void zoomOut(){
		for(Iterator<TouchObject> it = this.objList.iterator();it.hasNext();){
			TouchObject to = it.next();
			if(to.isHandlerTouchEvent() == true){
				to.getImg().zoomOut();
			}
		}
	}
	
	private Bitmap getScaledBitmap(int index){
		Bitmap scaledBitmap = null;
		TouchObject to = this.objList.get(index);
		float dest_width = (int)(to.getImg().getMaxX() - to.getImg().getMinX());
		float dest_height = (int)(to.getImg().getMaxY() - to.getImg().getMinY());
		/*if(index == 1){
			dest_width = (float) (dest_width * 1.5);
			dest_height = (float) (dest_height * 1.5);
		}*/
		if(Log.D){
			Log.d(TAG, "dest_witdh=" + dest_width + ",dest_height=" + dest_height);
		}
		scaledBitmap = Bitmap.createScaledBitmap(to.getImg().getBitmap(), (int)dest_width, (int)dest_height, true);
		//release 已经处理过的图片
		// to.getImg().getBitmap().recycle();
		return scaledBitmap;
	}
	/**
	 * 把合并后的图片缩放到原始标尺的大小
	 * @param bmp
	 * @return
	 */
	private Bitmap getScaledBitmapByModelScale(Bitmap bmp){
		Bitmap returnBmp = null;
		//获取标尺图片的原始宽高
		float scale_x = this.objList.get(1).getImg().getScaleX();
		float scale_y = this.objList.get(1).getImg().getScaleY();
		if(Math.abs(scale_x - 1.0) > 0.1){
			returnBmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()/scale_x + 0.5f), (int)(bmp.getHeight()/scale_y + 0.5f), true);
			// returnBmp = Bitmap.createScaledBitmap(bmp, (int)(bmp.getWidth()/scale_x), (int)(bmp.getHeight()/scale_y), true);
			//释放原始的图片
			bmp.recycle();
		}else{
			returnBmp = bmp;
		}
		return returnBmp;
	}
	
	private void saveBitmap(Bitmap bitmap){
		String filename = Environment.getExternalStorageDirectory().getPath()
				+ "/huo.png";
		File f = new File(filename);
		FileOutputStream fos = null;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void removePop(){
		if(IrisImageView.this.pop != null) {
			try{
				IrisImageView.this.pop.dismiss();
				IrisImageView.this.pop = null;						
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
	}
	/*private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			//取消显示的提示框
			if(IrisImageView.this.myTimerTask != null){
				IrisImageView.this.myTimerTask.cancel();
				removePop();
			}
			
		}
		
	}*/
	
	public boolean isMergeCompleted(int index){
		if(this.completedObjList[index] != null){
			return true;
		}else{
			return false;
		}
	}
}
