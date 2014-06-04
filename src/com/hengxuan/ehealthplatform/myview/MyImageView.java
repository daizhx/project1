package com.hengxuan.ehealthplatform.myview;

import com.hengxuan.ehealthplatform.log.Log;

import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * support drag and scale,copy from inet
 * @author daizhx
 *
 */
public class MyImageView extends ImageView{
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    /**位图对象*/
    private Bitmap bitmap = null;
    /** 屏幕的分辨率*/
    private DisplayMetrics dm;

    /** 最小缩放比例*/
    float minScaleR = 1.0f;
    
    /** 最大缩放比例*/
    static final float MAX_SCALE = 15f;

    /** 初始状态*/
    static final int NONE = 0;
    /** 拖动*/
    static final int DRAG = 1;
    /** 缩放*/
    static final int ZOOM = 2;
    
    /** 当前模式*/
    int mode = NONE;


    /** 存储float类型的x，y值，就是你点下的坐标的X和Y*/
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    
    public MyImageView(Context context) {
		super(context);
		setupView();
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}
	
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}
	public void setupView(){
		Context context = getContext();
		//获取屏幕分辨率,需要根据分辨率来使用图片居中
		dm = context.getResources().getDisplayMetrics();
		
		//根据MyImageView来获取bitmap对象
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd != null){
			bitmap = bd.getBitmap();
		}
		
		//设置ScaleType为ScaleType.MATRIX，这一步很重要
		
		this.setImageBitmap(bitmap);
//		this.setScaleType(ScaleType.MATRIX);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			savedMatrix.set(this.getImageMatrix());
            prev.set(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			if(mode == DRAG){
                matrix.set(savedMatrix);
                this.setScaleType(ScaleType.MATRIX);
                matrix.postTranslate(event.getX() - prev.x, event.getY()
                        - prev.y);
			}else if(mode == ZOOM){
				 float newDist = distance(event);
	                if (newDist > 10f) {
	                    matrix.set(savedMatrix);
	                    float scale = newDist / dist;
	                    this.setScaleType(ScaleType.MATRIX);
	                    matrix.postScale(scale, scale, mid.x, mid.y);
	                }
			}
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;	
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			dist = distance(event);
			if(dist > 10f){
				midPoint(mid, event);
                mode = ZOOM;
			}
			break;
		}
		
		
		this.setImageMatrix(matrix);
		return true;
	}
	
	
    
    /**
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
            	//Log.d("", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScaleR);
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
            	//Log.d("", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
                matrix.set(savedMatrix);
            }
        }
        
    }
    
    
    /**
     * 两点的距离
     */
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}

