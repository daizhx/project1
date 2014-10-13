package com.jiuzhansoft.ehealthtec.lens.hair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController.PointInfo;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController.PositionAndScale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;




public class HairTouchView extends ImageView implements
		MultiTouchController.MultiTouchObjectCanvas {

	public class Img {

		private Bitmap bitmap;
		private float centerX;
		private float centerY;
		private int displayHeight;
		private int displayWidth;
		private Drawable drawable;
		private boolean firstLoad;
		private int height;
		private float maxX;
		private float maxY;
		private float minX;
		private float minY;
		private float scaleX;
		private float scaleY;
		private int width;
		private Paint paint;
		private int flash_number;
		
		public void setFlashNumber(int number) {
			this.flash_number += number;
			if(this.flash_number>11)
				this.flash_number = 0;
			
		}
		private void getMetrics(Resources resources) {
			DisplayMetrics displaymetrics = resources.getDisplayMetrics();
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayWidth = Math.max(displaymetrics.widthPixels,
						displaymetrics.heightPixels);
			} else {
				displayWidth = Math.min(displaymetrics.widthPixels,
						displaymetrics.heightPixels);
			}
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayHeight = Math.min(displaymetrics.widthPixels,
						displaymetrics.heightPixels);
			} else {
				displayHeight = Math.max(displaymetrics.widthPixels,
						displaymetrics.heightPixels) - galleryHeight;
			}
		}

		private void resetScreenMargin() {
			if (width * scaleX > displayWidth) {
				SCREEN_MARGIN_WIDTH_LEFT = (float) displayWidth;
				SCREEN_MARGIN_WIDTH_RIGHT = (float) displayWidth;
			} else {
				SCREEN_MARGIN_WIDTH_LEFT = width * scaleX;
				SCREEN_MARGIN_WIDTH_RIGHT = width * scaleX;
			}
			
			if (height * scaleY > displayHeight) {
				SCREEN_MARGIN_HEIGHT_TOP = (float) (displayHeight);
				SCREEN_MARGIN_HEIGHT_BOTTOM = (float) (displayHeight);
			} else {
				SCREEN_MARGIN_HEIGHT_TOP = height * scaleY;
				SCREEN_MARGIN_HEIGHT_BOTTOM = height * scaleY;
			}
		}

		private boolean setPos(float cx, float cy, float sx, float sy) {
			resetScreenMargin();
			if (sx == sy && (double) sx > 0.5D && sx < 8F) {
				float f4 = (float) (width / 2) * sx;
				float f5 = (float) (height / 2) * sy;
				float f6 = cx - f4;
				float f7 = cy - f5;
				float f8 = cx + f4;
				float f9 = cy + f5;
				if (f6 > (displayWidth - SCREEN_MARGIN_WIDTH_RIGHT)) {
					minX = displayWidth - SCREEN_MARGIN_WIDTH_RIGHT;
					maxX = minX + f4 * 2F;
				} else {
					if (f8 < SCREEN_MARGIN_WIDTH_LEFT) {
						maxX = SCREEN_MARGIN_WIDTH_LEFT;
						minX = maxX - f4 * 2F;
					} else {
						minX = f6;
						maxX = f8;
					}
				}
				if (f7 > (displayHeight - SCREEN_MARGIN_HEIGHT_BOTTOM)) {
					minY = displayHeight - SCREEN_MARGIN_HEIGHT_BOTTOM;
					maxY = minY + f5 * 2F;
				} else {
					if (f9 < SCREEN_MARGIN_HEIGHT_TOP) {
						maxY = SCREEN_MARGIN_HEIGHT_TOP;
						minY = maxY - f5 * 2F;
					} else {
						minY = f7;
						maxY = f9;
					}
				}
				centerX = minX + (maxX - minX) / 2F;
				centerY = minY + (maxY - minY) / 2F;
				scaleX = sx;
				scaleY = sy;
			}
			return true;
		}
	
		public boolean containsPoint(float f, float f1) {
			boolean flag = false;
			if ((f >= minX) && (f <= maxX) && (f1 >= minY) && (f1 <= maxY)) {
				flag = true;
			}
			return flag;
		} 

		public void draw(Canvas canvas) {
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			drawable.draw(canvas);
			
			if(drawLine){
				Iterator<PointF[]> iter = pointList.iterator();
				while(iter.hasNext()){
					PointF[] pointss = (PointF[])iter.next();
					paint.setColor(Color.rgb(0, 255, 0));
					canvas.drawCircle(pointss[0].x, pointss[0].y, 10.0f, paint);
					canvas.drawLine(pointss[0].x, pointss[0].y, pointss[1].x, pointss[1].y, paint);
					canvas.drawCircle(pointss[1].x, pointss[1].y, 10.0f, paint);
					paint.setColor(Color.rgb(255, 0, 0));
					canvas.drawText(String.format("%.2f", PIXEL_TO_MM * 1440 / (img.maxX - img.minX) *(int)(Math.sqrt((pointss[1].x - pointss[0].x) * (pointss[1].x - pointss[0].x) + (pointss[1].y - pointss[0].y) * (pointss[1].y - pointss[0].y)) / 2)), pointss[0].x - 20, pointss[0].y - 20, paint);
				}
				if(drawStart){
					canvas.drawLine(startPoint.x - 10, startPoint.y, startPoint.x + 10, startPoint.y, paint);
					canvas.drawLine(startPoint.x, startPoint.y - 15, startPoint.x, startPoint.y + 15, paint);
				}else{
					paint.setColor(Color.rgb(0, 255, 0));
					canvas.drawCircle(startPoint.x, startPoint.y, 10.0f, paint);
					canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
					canvas.drawCircle(endPoint.x, endPoint.y, 10.0f, paint);
					paint.setColor(Color.rgb(255, 0, 0));
					canvas.drawText(String.format("%.2f", PIXEL_TO_MM * 1440 / (img.maxX - img.minX) * (int)(Math.sqrt((endPoint.x - startPoint.x) * (endPoint.x - startPoint.x) + (endPoint.y - startPoint.y) * (endPoint.y - startPoint.y)) / 2)), startPoint.x - 20, startPoint.y - 20, paint);
				}
				
			}
		}

		public float getCenterX() {
			return centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public Drawable getDrawable() {
			return drawable;
		}

		public int getHeight() {
			return height;
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMaxY() {
			return maxY;
		}

		public float getMinX() {
			return minX;
		}

		public float getMinY() {
			return minY;
		}

		public float getScaleX() {
			return scaleX;
		}

		public float getScaleY() {
			return scaleY;
		}

		public int getWidth() {
			return width;
		}

		public void load(Resources resources) {
			getMetrics(resources);
			drawable = new BitmapDrawable(bitmap);
			width = drawable.getIntrinsicWidth();
			height = drawable.getIntrinsicHeight();
			if (firstLoad) {
				firstLoad = false;
				setPos(displayWidth / 2, displayHeight / 2, 1F, 1F);
			}
		}
		
		public boolean setPos(
				MultiTouchController.PositionAndScale positionandscale) {
			float f2;
			float f3;
			if ((mUIMode & 2) != 0)
				f2 = positionandscale.getScaleX();
			else
				f2 = positionandscale.getScale();
			if ((mUIMode & 2) != 0)
				f3 = positionandscale.getScaleY();
			else
				f3 = positionandscale.getScale();
			
			return setPos(positionandscale.getXOff(),
					positionandscale.getYOff(), f2, f3);
		}

		public void unload() {
			drawable = null;
		}

		public void zoomIn() {
			if (setPos(centerX, centerY, scaleX - 0.8F, scaleY - 0.8F))
				invalidate();
		}

		public void zoomOut() {
			if (setPos(centerX, centerY, scaleX + 0.8F, scaleY + 0.8F))
				invalidate();
		}

		public Img(Bitmap bitmap1, Resources resources) {
			super();
			bitmap = bitmap1;
			firstLoad = true;
			getMetrics(resources);
			paint = new Paint();
			paint.setStrokeWidth(5.0f);
			paint.setTextSize(25.0f);
		}
	}

	private static float SCREEN_MARGIN_HEIGHT_BOTTOM = 0F;
	private static float SCREEN_MARGIN_HEIGHT_TOP = 0F;
	private static float SCREEN_MARGIN_WIDTH_LEFT = 0F;
	private static float SCREEN_MARGIN_WIDTH_RIGHT = 0F;
	private MultiTouchController.PointInfo currTouchPoint;
	private int galleryHeight;
	private Img img;
	private boolean mShowDebugInfo;
	private int mUIMode;
	private MultiTouchController multiTouchController;
	
	private PointF startPoint = new PointF();
	private PointF endPoint = new PointF();
	private PointF startPoint2 = new PointF();
	private PointF startPoint3 = new PointF();
	private boolean drawStart = false;
	private boolean drawLine = false;	
	private boolean drawEnd = false;
	private boolean showDialog = false;
	
	public void setDrawStart(boolean start){
		drawStart = start;
		startPoint.set(((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth() / 2, ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight() / 2);
		img.paint.setColor(Color.rgb(255, 0, 0));
		postInvalidate();
	}
	
	
	public void setDrawLine(boolean draw){
		drawEnd = false;
		drawLine = draw;
		startPoint.set(((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth() / 2, ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight() / 2);
		endPoint.set(0, 0);
		startPoint2.set(0, 0);
		pointList.clear();
		postInvalidate();
		textView.setText(getResources().getString(R.string.mean_radius));
	}
	public boolean getDrawLine(){
		return drawLine;
	}
	
	public boolean canAnalysis(){
		return pointList.size() > 0;
	}
	
	private float poreRadius;
	
	public float getPoreRadius(){
		return poreRadius;
	}
	
	public void setData(){
		Iterator<PointF[]> iter = pointList.iterator();
		float radiusSum = 0;
		while(iter.hasNext()){
			PointF[] pointss = (PointF[])iter.next();
			radiusSum += PIXEL_TO_MM * 1440 / (img.maxX - img.minX) * (int)(Math.sqrt((pointss[1].x - pointss[0].x) * (pointss[1].x - pointss[0].x) + (pointss[1].y - pointss[0].y) * (pointss[1].y - pointss[0].y)) / 2);
		}
		poreRadius = radiusSum / pointList.size();
		textView.setText(getResources().getString(R.string.mean_radius) + String.format("%.2f", poreRadius) + "mm");
		
	}
	public static float PIXEL_TO_MM = 0.007f;
	private List<PointF[]> pointList = new ArrayList<PointF[]>();
	private PointF[] points;
	
	private int analysisMode = 4;
	public void setAnalysisMode(int mode){
		analysisMode = mode;
	}
	public int getAnalysisMode(){
		return analysisMode;
	}
	
	private TextView textView;
	public void setTextView(TextView text){
		textView = text;
	}
	
	static 
	{
		SCREEN_MARGIN_WIDTH_RIGHT = 100F;
		SCREEN_MARGIN_WIDTH_LEFT = 100F;
		SCREEN_MARGIN_HEIGHT_BOTTOM = 100F;
		SCREEN_MARGIN_HEIGHT_TOP = 100F;
	}

	public HairTouchView(Context context) {
		this(context, null);
	}

	public HairTouchView(Context context, AttributeSet attributeset) {
		this(context, attributeset, 0);
	}

	public HairTouchView(Context context, AttributeSet attributeset, int i) {
		super(context, attributeset, i);
		img = null;
		multiTouchController = new MultiTouchController(this);
		currTouchPoint = multiTouchController.new PointInfo();
		mShowDebugInfo = true;
		mUIMode = 1;
		poreRadius = 0;
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		currTouchPoint.isDown();
	}

	/*
	 * public Img getDraggableObjectAtPoint(MultiTouchController.PointInfo
	 * pointinfo) { return img; }
	 */

	public Img getImg() {
		return img;
	}

	public Img getDraggableObjectAtPoint(MultiTouchController.PointInfo pointinfo)
	{
		return img;
	}
	
	/*
	@Override
	public volatile Object getDraggableObjectAtPoint(PointInfo pointinfo) {
		return getDraggableObjectAtPoint(pointinfo);
	}
	*/
	
	public void getPositionAndScale(Img img1,
			MultiTouchController.PositionAndScale positionandscale) {
		boolean flag = false;
		boolean flag1;
		float f4;
		boolean flag2;
		if ((mUIMode & 2) == 0)
			flag1 = true;
		else
			flag1 = false;
		f4 = (img1.getScaleX() + img1.getScaleY()) / 2F;
		if ((mUIMode & 2) != 0)
			flag2 = true;
		else
			flag2 = false;
		if ((mUIMode & 1) != 0)
			flag = true;
		positionandscale.set(img1.getCenterX(), img1.getCenterY(), flag1, f4,
				flag2, img1.getScaleX(), img1.getScaleY(), flag);
	}

	@Override
	public void getPositionAndScale(Object obj,
			PositionAndScale positionandscale) {
		getPositionAndScale((Img) obj, positionandscale);
	}

	public void init(Context context, Bitmap bitmap, int i) {
		Resources resources = context.getResources();
		galleryHeight = i;
		SCREEN_MARGIN_HEIGHT_BOTTOM = SCREEN_MARGIN_HEIGHT_BOTTOM
				+ (float) galleryHeight + 32F;

		img = new Img(bitmap, resources);
		loadImages(context);
		//setBackgroundColor(Color.argb(255,15,79,131));
	}

	public void loadImages(Context context) {
		img.load(context.getResources());
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		img.draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if(drawEnd)
			return true;
		if(showDialog){
			return true;
		}
		if(!drawLine)
			return false;
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				startPoint2.set(event.getX(), event.getY());
				startPoint3.set(startPoint);
				break;
			case MotionEvent.ACTION_MOVE:
				
				if(drawStart){
					startPoint.set(startPoint3.x + event.getX() - startPoint2.x, startPoint3.y + event.getY() - startPoint2.y);
				}else{
					endPoint.set(startPoint.x + event.getX() - startPoint2.x, startPoint.y + event.getY() - startPoint2.y);
				}		
				postInvalidate();
				break;
			case MotionEvent.ACTION_UP:
				if(drawStart){
					startPoint.set(startPoint3.x + event.getX() - startPoint2.x, startPoint3.y + event.getY() - startPoint2.y);
				}else{
					endPoint.set(startPoint.x + event.getX() - startPoint2.x, startPoint.y + event.getY() - startPoint2.y);
					points = new PointF[2];
					points[0] = new PointF();
					points[1] = new PointF();
					points[0].set(startPoint.x, startPoint.y);
					points[1].set(endPoint.x, endPoint.y);
					pointList.add(points);
					setData();
				}		
				postInvalidate();
				showDialog = true;
				if(drawStart){
					final AlertDialog dialog = (new AlertDialog.Builder(getContext())).create();
					
					dialog.setMessage(getResources().getString(R.string.whether_start_point));
					dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							showDialog = false;
							drawStart = false;
							dialog.dismiss();
						}
					});
					dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							showDialog = false;
							drawStart = true;
							dialog.dismiss();
						}
					});
					dialog.show();
				}else{
					final AlertDialog dialog = (new AlertDialog.Builder(getContext())).create();
					
					dialog.setMessage(getResources().getString(R.string.whether_continue_mark));
					dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							showDialog = false;
							startPoint.set(((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth() / 2, ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight() / 2);
							drawStart = true;
							postInvalidate();
							dialog.dismiss();
						}
					});
					dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							showDialog = false;
							drawEnd = true;;
							dialog.dismiss();
						}
					});
					dialog.show();
				}
				break;
			default:
				break;
		}
		return true;
	}

	public MultiTouchController getMultiTouchController() {
		return multiTouchController;
	}

	public void selectObject(Img img1, MultiTouchController.PointInfo pointinfo) {
		currTouchPoint.set(pointinfo);
		invalidate();
	}

	@Override
	public void selectObject(Object obj, PointInfo pointinfo) {
		selectObject((Img) obj, pointinfo);

	}

	public boolean setPositionAndScale(Img img1,
			MultiTouchController.PositionAndScale positionandscale,
			MultiTouchController.PointInfo pointinfo) {
		currTouchPoint.set(pointinfo);
		boolean flag = img1.setPos(positionandscale);
		if (flag)
			invalidate();
		return flag;
	}

	@Override
	public boolean setPositionAndScale(Object obj,
			PositionAndScale positionandscale, PointInfo pointinfo) {
		return setPositionAndScale((Img) obj, positionandscale, pointinfo);
	}
	
	public void trackballClicked()
	{
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}
	public void point_flash_number(int number)
	{
		img.setFlashNumber(number);
	}
	
}

