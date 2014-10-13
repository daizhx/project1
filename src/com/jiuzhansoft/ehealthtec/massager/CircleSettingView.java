package com.jiuzhansoft.ehealthtec.massager;

import java.io.InputStream;
import java.lang.ref.SoftReference;

import com.jiuzhansoft.ehealthtec.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class CircleSettingView extends View {
	private static final String TAG = "CircleSettingView";
	private Paint paint;
	private Path path;
	private int mMaxValue;
	private String mUnit = "";

	private int mWidth;
	private int mHeight;
	
	
	private float pivotX;
	private float pivotY;
	private float r1;
	private float r2;
	private float r3;
	private float handLength;
	private double handAngle;
	private float handEndX;
	private float handEndY;
	
	private int circleSteps;
	
	private boolean isHandPressed;
	
	private RectF mRectF = new RectF();
	private RectF mRectF2 = new RectF();
	private RectF mHintRect = new RectF();
	
	private Bitmap hintBg;
	
	private int mValue;
	private int currentStep = 1;
	private int stepValue;
	private double stepAngle;
	private OnValueChangeListener mOnValueChangeListener;
	
	
	public interface OnValueChangeListener{
		void onValueChanged(int value);
	}
	
	public CircleSettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ClockSetting, 0, 0);
		try {
			mMaxValue = a.getInt(R.styleable.ClockSetting_maxValue, 0);
			circleSteps = a.getInt(R.styleable.ClockSetting_valueSteps, 0);
			mUnit = a.getString(R.styleable.ClockSetting_unit);
			if(mUnit == null){
				mUnit = "";
			}
		}finally{
			a.recycle();
		}
		stepValue = mMaxValue/circleSteps;
		stepAngle = (2*Math.PI)/circleSteps;
		
		paint = new Paint();
		paint.setAntiAlias(true);
		//for hint text
//		path = new Path();
		
		hintBg = readBitmap(context, R.drawable.hint_bg);
		
		
		//initial the hand
		handAngle = currentStep*stepAngle;
		mValue = mMaxValue/circleSteps;
	}
	
	public void setMaxValue(int i){
		mMaxValue = i;
	}
	public void setValueSteps(int i){
		circleSteps = i;
	}
	
	public void setCurrentValue(int i){
		mValue = i;
		currentStep = mValue/stepValue;
		handAngle = currentStep*stepAngle;
	}
	
	public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener){
		mOnValueChangeListener = onValueChangeListener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
//		paint.setColor(0xA5D9EE);
		paint.setColor(Color.rgb(0xA5, 0xD9, 0xEE));
		canvas.drawCircle(pivotX, pivotY, r1 - 3, paint);
		
		//draw inner circle
		paint.setStyle(Paint.Style.FILL);
//		paint.setColor(0xDFDFDF);
		paint.setColor(Color.rgb(0xDF, 0xDF, 0xDF));
//		float sweepAngle = (float)((handAngle/(2*Math.PI))*360);
		canvas.drawArc(mRectF, -90, 360, true, paint);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((r3 - r2)*2 + 1);
//		paint.setColor(0xA5DAEC);
		paint.setColor(Color.rgb(0xA5, 0xDA, 0xEC));
		float sweepAngle = (float)((handAngle/(2*Math.PI))*360);
		canvas.drawArc(mRectF2, sweepAngle-90, 360 - (sweepAngle-90), false, paint);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((r3 - r2)*2 + 1);
//		paint.setColor(Color.RED);
		paint.setColor(Color.rgb(0xC5, 0xEB, 0xF6));
		canvas.drawArc(mRectF2, -90, sweepAngle, false, paint);
		
		paint.setStrokeWidth(2);
		paint.setColor(Color.BLACK);
		canvas.drawLine(pivotX, pivotY, pivotX, r1 - handLength, paint);
		
		//draw hand
		if(isHandPressed){
			paint.setStrokeWidth(4);
		}
		
		handEndX = r1 + (float)(handLength*Math.sin(handAngle));
		handEndY = r1 - (float)(handLength*Math.cos(handAngle));
		canvas.drawLine(pivotX, pivotY, handEndX, handEndY, paint);
		
		//draw hint
		float width = handLength/2;
		float height = handLength/5;
		
		mHintRect.set((handEndX+pivotX)/2, (handEndY+pivotY)/2 - height, (handEndX+pivotX)/2 + width, (handEndY+pivotY)/2);
		canvas.drawBitmap(hintBg, null, mHintRect, null);
		
		//draw text
		path = new Path();
		path.moveTo((handEndX+pivotX)/2, (handEndY+pivotY)/2  - height);
		path.lineTo((handEndX+pivotX)/2 + width, (handEndY+pivotY)/2  - height);
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(height/3);
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setStrokeWidth(0);
		
//		mValue = (int)(handAngle/((2*Math.PI)/circleSteps))*2;
		
		canvas.drawTextOnPath(mValue + mUnit, path, width/5, height/3 + height/5, paint);
		
	}
	
	private void calculateValue(){
		
		currentStep = (int)(handAngle/stepAngle);
		double baseAngle = currentStep*stepAngle;
		double tempAngle = handAngle - baseAngle;
		if(Math.abs(tempAngle) < stepAngle/2){
			handAngle = baseAngle;
		}else{
			handAngle = baseAngle + stepAngle;
			currentStep++;
		}
		mValue = currentStep*stepValue;
		currentStep = mValue/stepValue;
		handAngle = currentStep*stepAngle;
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		mHeight = mWidth = (w <= h ? w : h);
		Log.d(TAG, "mHeight="+mHeight);
		
		r1 = pivotY = pivotX = ((float)mWidth)/2;
		handLength = r1 -6 - 3;
		
		r2=(2*r1)/3;
		mRectF.set(r1 - r2, r1 - r2, r1 + r2, r1 + r2);
		r3 = r2 + (r1 - r2)/6;
		mRectF2.set(r1 - r3, r1 - r3, r1 + r3, r1 + r3);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onMeasure: widthMeasureSpec="+widthMeasureSpec+",heightMeasureSpec="+heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			handActionDown(event);
			return true;
		case MotionEvent.ACTION_MOVE:
			handActionMove(event);
			return true;
		case MotionEvent.ACTION_UP:
			handActionUP(event);
			return true;
		case MotionEvent.ACTION_CANCEL:
			Log.d(TAG, "event was CANCEL");
			return true;
		case MotionEvent.ACTION_OUTSIDE:
			Log.d(TAG, "event was OUTSIDE");
			return true;
		default:
			return super.onTouchEvent(event);
		}
		
	}
	
	private void printSamples(MotionEvent ev){
		final int historySize = ev.getHistorySize();
		final int pointerCount = ev.getPointerCount();
		
		for (int h = 0; h < historySize; h++) {
	         Log.d(TAG, "At time1 :"+ ev.getHistoricalEventTime(h));
	         for (int p = 0; p < pointerCount; p++) {
	             Log.d(TAG,"  pointer1 :"+
	                 ev.getPointerId(p) +"x="+ ev.getHistoricalX(p, h) + "y="+ ev.getHistoricalY(p, h));
	         }
	     }
	     Log.d(TAG,"At time2 :" + ev.getEventTime());
	     for (int p = 0; p < pointerCount; p++) {
	         Log.d(TAG, "  pointer2 :"+
	             ev.getPointerId(p)+"x="+ ev.getX(p)+"y="+ ev.getY(p));
	     }
	}
	
	private void handActionUP(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();
		Log.d(TAG, "action UP:" + x + "," + y);
		
		if(isHandPressed){
			isHandPressed = false;
			//handAngle = calculatorAngle(x, y);
			double tempAngle = calculatorAngle(x, y);
			if(Math.abs(handAngle - tempAngle) >= Math.PI){
				return;
			}
			handAngle = tempAngle;
			calculateValue();
			invalidate();
			if(mOnValueChangeListener != null){
				mOnValueChangeListener.onValueChanged(mValue);
			}
		}
	}

	private void handActionMove(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();
		Log.d(TAG, "action MOVE:" + x + "," + y);
		
		if(isHandPressed){
			double tempAngle = calculatorAngle(x, y);
			if(Math.abs(tempAngle - handAngle) >= Math.PI ){
				if(Math.abs(handAngle - tempAngle) >= (2*Math.PI - Math.PI/15)){
					return;
				}
				
				isHandPressed = false;
				invalidate();
				return;
			}
			handAngle = tempAngle;
			invalidate();
		}
	}

	private void handActionDown(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();
		Log.d(TAG, "action Down:" + x + "," + y);

		double angle = calculatorAngle(x, y);
		if(Math.abs(angle - handAngle) < Math.PI/15){
			isHandPressed = true;
			//handAngle = angle;
			invalidate();
		}else if(Math.abs(angle -handAngle) > (2*Math.PI - Math.PI/15)){
			isHandPressed = true;
			invalidate();
		}else{
			
		}
	}
	
	private double calculatorAngle(float x, float y){
		double angle = 0;
		if(x > pivotX){
			if(y < pivotY){
				//第一象限
				angle = Math.atan((double)(x-pivotX)/(double)(pivotY - y));
			}else{//y>=pivotY 第二象限
				angle = Math.PI/2 + Math.atan((double)(y - pivotY)/(double)(x - pivotX));
			}
		}else{
			if(y > pivotY){
				angle = Math.PI + Math.atan((double)(pivotX - x)/(double)(y - pivotY));
			}else{//第四象限
				if(pivotX == x){
					angle = 2*Math.PI;//or 0;
				}else{
					angle = Math.PI + Math.PI/2 + Math.atan((double)(pivotY - y)/(double)(pivotX - x));
				}
			}
		}
		return angle;
	}

	public static Bitmap readBitmap(Context context, int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		SoftReference<Bitmap> softreference = new SoftReference<Bitmap>(bitmap);
		return softreference.get();
	}
	
	public static Drawable readDrawable(Context context, int resId){
		Bitmap bitmap = readBitmap(context, resId);
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		SoftReference<BitmapDrawable> softreference = new SoftReference<BitmapDrawable>(bd);
		return softreference.get();
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}


}
