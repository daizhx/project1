package com.jiuzhansoft.ehealthtec.lens.iris;

import com.jiuzhansoft.ehealthtec.http.utils.DPIUtils;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController.PointInfo;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController.PositionAndScale;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


public class TouchObject implements
MultiTouchController.MultiTouchObjectCanvas{

	private static String TAG = "TouchObject";
	//状态栏高度
	private static final float BOTTOM_FIX = 0F;
	private static float SCREEN_MARGIN_HEIGHT_TOP = 0F;
	private static float SCREEN_MARGIN_HEIGHT_BOTTOM = 0F;
	private static float SCREEN_MARGIN_WIDTH_LEFT = 0F;
	private static float SCREEN_MARGIN_WIDTH_RIGHT = 0F;
	//是否旋转的tag标识
	private static final int UI_MODE_ROTATE = 1;
	//是否各向异性的tag标识
	private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
	//目前的触摸点坐标
	private MultiTouchController.PointInfo currTouchPoint;
	//画廊的高度--可以为0
	private int galleryHeight;
	//对应的图片信息
	private Img img;
	//是否显示调试信息
	private boolean mShowDebugInfo;
	//ui显示的模式
	private int mUIMode;
	//多点触屏的处理类
	private MultiTouchController multiTouchController;
	private TouchImageViewSelector imgViewSelector = null;
	
	private View view = null;
	private boolean isHandlerTouchEvent = false;
	private boolean isShow = true;

	static {//初始设为100F
		SCREEN_MARGIN_WIDTH_LEFT = 100F;
		SCREEN_MARGIN_WIDTH_RIGHT = 100F;
		SCREEN_MARGIN_HEIGHT_TOP = 100F;
		SCREEN_MARGIN_HEIGHT_BOTTOM = 100F;
		
	}
	//consructor
	public TouchObject(View view) {
		this.view = view;
		this.img = null;
		this.multiTouchController = new MultiTouchController(this);
		this.currTouchPoint = multiTouchController.new PointInfo();
		// 显示调试信息
		this.mShowDebugInfo = false;
		this.mUIMode = UI_MODE_ROTATE;
		
	}
	

	public boolean isHandlerTouchEvent() {
		return isHandlerTouchEvent;
	}


	public void setHandlerTouchEvent(boolean isHandlerTouchEvent) {
		this.isHandlerTouchEvent = isHandlerTouchEvent;
	}
	

	public boolean isShow() {
		return isShow;
	}


	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}


	public void setImgViewSelector(TouchImageViewSelector imgViewSelector) {
		this.imgViewSelector = imgViewSelector;
	}


	/**
	 * 初始化
	 * @param context
	 * @param bitmap
	 * @param galleryHeight
	 * @param scale 缩放比例
	 */
	public void init(Context context, Bitmap bitmap, int galleryHeight, float scale) {
		Resources resources = context.getResources();
		this.galleryHeight = galleryHeight;
		SCREEN_MARGIN_HEIGHT_BOTTOM = SCREEN_MARGIN_HEIGHT_BOTTOM
				+ (float) galleryHeight + BOTTOM_FIX;
		// 创建一个Img对象
		this.img = new Img(bitmap, resources, scale);
		this.loadImages(context);
		// 设置ImageView的背景颜色--目前设为透明
		//super.setBackgroundColor(Color.TRANSPARENT);
	}
	
	//设置gallery height
	public void setGalleryHeight(Resources res,int galleryHeight){
		if(Log.D){
			Log.d(TAG, "reset gallery height according mainactivity metrics");
		}
		this.galleryHeight = galleryHeight;
		this.img.getMetrics(res);
		this.img.setPos(this.img.getDisplayWidth()/2, this.img.getDisplayHeight()/2, 1F, 1F);		
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		currTouchPoint.isDown();
	}
	//获取对应的图片
	public Img getImg() {
		return img;
	}
	/**
	 * 获取目前正在拖动的对象
	 */
	@Override
	public Img getDraggableObjectAtPoint(
			MultiTouchController.PointInfo pointInfo) {
		return img;
	}
	
	public void getPositionAndScale(Img img,
			MultiTouchController.PositionAndScale positionAndScale) {
		boolean rotateFlag = false;
		boolean isNoAnisotropic;
		float avgScale;
		boolean isAnisotropic;
		//各向异性的反逻辑
		if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0)//说明该标志没有设置
			isNoAnisotropic = true;
		else
			isNoAnisotropic = false;
		//计算x方向和y方向的平均缩放比率
		avgScale = (img.getScaleX() + img.getScaleY()) / 2F;
		//各向异性的正逻辑
		if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0)
			isAnisotropic = true;
		else
			isAnisotropic = false;
		if ((mUIMode & UI_MODE_ROTATE) != 0)
			rotateFlag = true;
		positionAndScale.set(img.getCenterX(), img.getCenterY(), isNoAnisotropic, avgScale,
				isAnisotropic, img.getScaleX(), img.getScaleY(), rotateFlag);
	}

	@Override
	public void getPositionAndScale(Object obj,
			PositionAndScale positionAndScale) {
		getPositionAndScale((Img) obj, positionAndScale);
	}
	/**
	 * 加载图像
	 * @param context
	 */
	public void loadImages(Context context) {
		img.load(context.getResources());
	}
	public void draw(Canvas canvas) {
		//绘制指定的图像
		img.draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}
	/**
	 * 触屏处理函数
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return this.multiTouchController.onTouchEvent(event);
	}
	public void selectObject(Img img, PointInfo pointInfo) {
		//设置目前的触摸点信息
		currTouchPoint.set(pointInfo);
		//通知重新绘制该控件
		this.view.invalidate();
	}
	/**
	 * 根据指定的点选择对象
	 */
	@Override
	public void selectObject(Object obj, PointInfo pointInfo) {
		selectObject((Img) obj, pointInfo);

	}
	/**
	 * 设置位置和缩放比例
	 * @param img
	 * @param positionandscale -位置和缩放比例
	 * @param pointinfo
	 * @return
	 */
	public boolean setPositionAndScale(Img img,
			PositionAndScale positionAndScale,
			PointInfo pointInfo) {
		//设置触摸点的信息
		currTouchPoint.set(pointInfo);
		//设置位置和缩放比例
		boolean isSuccess = img.setPos(positionAndScale);
		if (isSuccess){
			//return true;
			//重新显示imageview
			this.view.invalidate();
		}
		return isSuccess;
	}
	/**
	 * 设置位置和缩放比例
	 */
	@Override
	public boolean setPositionAndScale(Object obj,
			PositionAndScale positionAndScale, PointInfo pointInfo) {
		return setPositionAndScale((Img)obj, positionAndScale, pointInfo);
	}

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		this.view.invalidate();
	}

	/**
	 * 图片对象定义
	 * 
	 * @author Jerry
	 * 
	 */
	public class Img {
		// 待显示的图片
		private Bitmap bitmap;
		// 图片显示的中心点x坐标
		private float centerX;
		// 图片显示的中心点y坐标
		private float centerY;
		// 显示区域的高度
		private int displayHeight;
		// 显示区域的宽度
		private int displayWidth;
		// 图片drawable对象
		private Drawable drawable;
		// 是否第一次加载
		private boolean firstLoad;
		// 原始宽度
		private int width;
		// 原始高度
		private int height;
		// x方向的最大坐标
		private float maxX;
		// y方向的最大坐标
		private float maxY;
		// x方向的最小坐标
		private float minX;
		// y方向的最小坐标
		private float minY;
		// x方向的缩放比例
		private float scaleX;
		// y方向的缩放比例
		private float scaleY;
		
		private float maxScale;

		// Constructor
		public Img(Bitmap bitmap, Resources resouces, float scale) {
			super();
			this.bitmap = bitmap;
			// 标志第一次加载为true
			firstLoad = true;
			getMetrics(resouces);
			maxScale = scale;
		}
		
		/**
		 * 重新设置屏幕的显示边距
		 */
		private void resetScreenMargin() {
			//重新设置左右边距
			if (width * scaleX > IrisAnalysisActivity.mContainerWidth) {//如果大于屏幕的宽度，则设为屏幕宽度
				SCREEN_MARGIN_WIDTH_LEFT = (float) IrisAnalysisActivity.mContainerWidth;
				SCREEN_MARGIN_WIDTH_RIGHT = (float) IrisAnalysisActivity.mContainerWidth;
			} else {//否则设为实际的大小
				SCREEN_MARGIN_WIDTH_LEFT = width * scaleX;
				SCREEN_MARGIN_WIDTH_RIGHT = width * scaleX;
			}
			//重新设置上下边距
			if (height * scaleY > IrisAnalysisActivity.mContainerHeight) {
				SCREEN_MARGIN_HEIGHT_TOP = (float) (IrisAnalysisActivity.mContainerHeight - BOTTOM_FIX);
				SCREEN_MARGIN_HEIGHT_BOTTOM = (float) (IrisAnalysisActivity.mContainerHeight + BOTTOM_FIX);
			} else {
				SCREEN_MARGIN_HEIGHT_TOP = height * scaleY - BOTTOM_FIX;
				SCREEN_MARGIN_HEIGHT_BOTTOM = height * scaleY + BOTTOM_FIX;
			}
		}
		/**
		 * 设置图片的位置和缩放信息
		 * @param screen_center_x the center of content
		 * @param screen_center_y
		 * @param scale_x
		 * @param scale_y
		 * @return
		 */
		public boolean setPos(float screen_center_x, float screen_center_y, float scale_x, float scale_y) {
			if(scale_x >maxScale)
				scale_x = maxScale;
			if(scale_y > maxScale)
				scale_y = maxScale;
			if(scale_x < 0.5)
				scale_x = (float) 0.5;
			if(scale_y < 0.5)
				scale_y = (float) 0.5;
			scaleX = scale_x;
			scaleY = scale_y;
			
			resetScreenMargin();
			//这里只处理等比例缩放的问题--并且缩放比例在0.5-8之间
			if (scale_x == scale_y && (double) scale_x >= 0.5D && scale_x <= maxScale) {
				//计算缩放后的图片宽度的一半
				float scaled_half_width = (float) (width / 2) * scale_x;
				float scaled_half_height = (float) (height / 2) * scale_y;
				float left_margin = screen_center_x - scaled_half_width;
				float top_margin = screen_center_y - scaled_half_height;
				float right_margin = IrisAnalysisActivity.mContainerWidth - screen_center_x - scaled_half_width;
				float bottom_margin = IrisAnalysisActivity.mContainerHeight - screen_center_y - scaled_half_height;
				
				minX = left_margin;
				maxX = minX + scaled_half_width * 2F;
				if (left_margin < 0) {//图片的有边界移除屏幕，调节坐标将图片显示在屏幕之内
					minX = 0;//不让图像移除屏幕
					maxX = minX + scaled_half_width * 2F;
				} 
				if (right_margin < 0) {
					maxX = IrisAnalysisActivity.mContainerWidth;
					minX = maxX - scaled_half_width * 2F;
				}
				
				minY = top_margin;
				maxY = minY + scaled_half_height * 2F;
				if (top_margin < 0) {
					minY = 0;
					maxY = minY + scaled_half_height * 2F;
				}
				if (bottom_margin < 0) {
						maxY = IrisAnalysisActivity.mContainerHeight;
						minY = maxY - scaled_half_height * 2F;
				}

				//计算图片的中间位置
				centerX = minX + (maxX - minX) / 2F;
				centerY = minY + (maxY - minY) / 2F;
				
				//scaleX = scale_x;
				//scaleY = scale_y;
				return true;
			}
			return false;
		}
		/**
		 * 设置图片的位置和缩放信息
		 * @param positionAndScale
		 * @return
		 */
		public boolean setPos(PositionAndScale positionAndScale) {
			float scale_x;
			float scale_y;
			// 如果不是等比例缩放--取x方向的缩放比例
			if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
				scale_x = positionAndScale.getScaleX();
			} else {// 否则取另一个值
				scale_x = positionAndScale.getScale();
			}
			// 如果不是等比例缩放--取y方向的缩放比例
			if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
				scale_y = positionAndScale.getScaleY();
			} else {
				scale_y = positionAndScale.getScale();
			}

			return setPos(positionAndScale.getXOff(),
					positionAndScale.getYOff(), scale_x, scale_y);
		}

		/**
		 * 获取显示区域的大小并且进行设置--横屏时不显示下面的gallery
		 * 
		 * @param resources
		 */
		public void getMetrics(Resources resources) {
			DisplayMetrics metrics = resources.getDisplayMetrics();
			// 确定最大显示宽度--如果是横屏
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// 获取宽度,高度中的最大值
				this.displayWidth = Math.max(metrics.widthPixels,
						metrics.heightPixels);
			} else {// 如果是竖屏，获取宽度,高度中的最小值
				this.displayWidth = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			}
			// 确定最大显示高度--如果是横屏
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// 获取宽度,高度中的最小值
				this.displayHeight = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			} else {// 如果是竖屏，获取宽度,高度中的最大值
				this.displayHeight = Math.max(metrics.widthPixels,
						metrics.heightPixels)
						- TouchObject.this.galleryHeight;
			}
			
		}

		public boolean containsPoint(float f, float f1) {
			boolean flag = false;
			if ((f >= minX) && (f <= maxX) && (f1 >= minY) && (f1 <= maxY)) {
				flag = true;
			}
			return flag;
		}

		public void draw(Canvas canvas) {
			canvas.save();
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			try{
				drawable.draw(canvas);				
			}catch(Exception e){
				e.printStackTrace();
			}
			canvas.restore();
		}

		public void load(Resources resources) {
			getMetrics(resources);
			drawable = new BitmapDrawable(bitmap);
			//设置图片的固有宽度
			width = drawable.getIntrinsicWidth();
			//设置图片的固有高度
			height = drawable.getIntrinsicHeight();
			if (firstLoad) {//如果是第一次加载，则将显示的位置指定在可显示区域的中间位置
				if(Log.D){
					Log.d(TAG, "first load------------++++++++++++");
				}
				firstLoad = false;
				float density = DPIUtils.getDensity();
				
				setPos(IrisAnalysisActivity.mContainerWidth/2, IrisAnalysisActivity.mContainerHeight/2, density, density);
//				width = width*3;
//				height = height*3;
//				setPos(0, 0, 1F, 1F);
			}
		}

		public void unload() {
			drawable = null;
		}

		public void zoomIn() {
			if (setPos(centerX, centerY, scaleX - 0.5F, scaleY - 0.5F)){
				
			}
			TouchObject.this.view.invalidate();
		}

		public void zoomOut() {
			if (setPos(centerX, centerY, scaleX + 0.5F, scaleY + 0.5F)){
				
			}
			TouchObject.this.view.invalidate();
		}
		
		public void zoomToMerge(){
			if (setPos(centerX, centerY, 0.5F, 0.5F)){
				
			}
			TouchObject.this.view.invalidate();
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

		public Bitmap getBitmap() {
			return bitmap;
		}

		public int getDisplayHeight() {
			return displayHeight;
		}

		public int getDisplayWidth() {
			return displayWidth;
		}
		
		
	}
	
	public interface TouchImageViewSelector {
		public boolean getCurrentImageViewSelected();
		public boolean next();
	}


}
