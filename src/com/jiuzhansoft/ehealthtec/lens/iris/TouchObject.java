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
	//״̬���߶�
	private static final float BOTTOM_FIX = 0F;
	private static float SCREEN_MARGIN_HEIGHT_TOP = 0F;
	private static float SCREEN_MARGIN_HEIGHT_BOTTOM = 0F;
	private static float SCREEN_MARGIN_WIDTH_LEFT = 0F;
	private static float SCREEN_MARGIN_WIDTH_RIGHT = 0F;
	//�Ƿ���ת��tag��ʶ
	private static final int UI_MODE_ROTATE = 1;
	//�Ƿ�������Ե�tag��ʶ
	private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
	//Ŀǰ�Ĵ���������
	private MultiTouchController.PointInfo currTouchPoint;
	//���ȵĸ߶�--����Ϊ0
	private int galleryHeight;
	//��Ӧ��ͼƬ��Ϣ
	private Img img;
	//�Ƿ���ʾ������Ϣ
	private boolean mShowDebugInfo;
	//ui��ʾ��ģʽ
	private int mUIMode;
	//��㴥���Ĵ�����
	private MultiTouchController multiTouchController;
	private TouchImageViewSelector imgViewSelector = null;
	
	private View view = null;
	private boolean isHandlerTouchEvent = false;
	private boolean isShow = true;

	static {//��ʼ��Ϊ100F
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
		// ��ʾ������Ϣ
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
	 * ��ʼ��
	 * @param context
	 * @param bitmap
	 * @param galleryHeight
	 * @param scale ���ű���
	 */
	public void init(Context context, Bitmap bitmap, int galleryHeight, float scale) {
		Resources resources = context.getResources();
		this.galleryHeight = galleryHeight;
		SCREEN_MARGIN_HEIGHT_BOTTOM = SCREEN_MARGIN_HEIGHT_BOTTOM
				+ (float) galleryHeight + BOTTOM_FIX;
		// ����һ��Img����
		this.img = new Img(bitmap, resources, scale);
		this.loadImages(context);
		// ����ImageView�ı�����ɫ--Ŀǰ��Ϊ͸��
		//super.setBackgroundColor(Color.TRANSPARENT);
	}
	
	//����gallery height
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
	//��ȡ��Ӧ��ͼƬ
	public Img getImg() {
		return img;
	}
	/**
	 * ��ȡĿǰ�����϶��Ķ���
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
		//�������Եķ��߼�
		if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0)//˵���ñ�־û������
			isNoAnisotropic = true;
		else
			isNoAnisotropic = false;
		//����x�����y�����ƽ�����ű���
		avgScale = (img.getScaleX() + img.getScaleY()) / 2F;
		//�������Ե����߼�
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
	 * ����ͼ��
	 * @param context
	 */
	public void loadImages(Context context) {
		img.load(context.getResources());
	}
	public void draw(Canvas canvas) {
		//����ָ����ͼ��
		img.draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}
	/**
	 * ����������
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return this.multiTouchController.onTouchEvent(event);
	}
	public void selectObject(Img img, PointInfo pointInfo) {
		//����Ŀǰ�Ĵ�������Ϣ
		currTouchPoint.set(pointInfo);
		//֪ͨ���»��Ƹÿؼ�
		this.view.invalidate();
	}
	/**
	 * ����ָ���ĵ�ѡ�����
	 */
	@Override
	public void selectObject(Object obj, PointInfo pointInfo) {
		selectObject((Img) obj, pointInfo);

	}
	/**
	 * ����λ�ú����ű���
	 * @param img
	 * @param positionandscale -λ�ú����ű���
	 * @param pointinfo
	 * @return
	 */
	public boolean setPositionAndScale(Img img,
			PositionAndScale positionAndScale,
			PointInfo pointInfo) {
		//���ô��������Ϣ
		currTouchPoint.set(pointInfo);
		//����λ�ú����ű���
		boolean isSuccess = img.setPos(positionAndScale);
		if (isSuccess){
			//return true;
			//������ʾimageview
			this.view.invalidate();
		}
		return isSuccess;
	}
	/**
	 * ����λ�ú����ű���
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
	 * ͼƬ������
	 * 
	 * @author Jerry
	 * 
	 */
	public class Img {
		// ����ʾ��ͼƬ
		private Bitmap bitmap;
		// ͼƬ��ʾ�����ĵ�x����
		private float centerX;
		// ͼƬ��ʾ�����ĵ�y����
		private float centerY;
		// ��ʾ����ĸ߶�
		private int displayHeight;
		// ��ʾ����Ŀ��
		private int displayWidth;
		// ͼƬdrawable����
		private Drawable drawable;
		// �Ƿ��һ�μ���
		private boolean firstLoad;
		// ԭʼ���
		private int width;
		// ԭʼ�߶�
		private int height;
		// x������������
		private float maxX;
		// y������������
		private float maxY;
		// x�������С����
		private float minX;
		// y�������С����
		private float minY;
		// x��������ű���
		private float scaleX;
		// y��������ű���
		private float scaleY;
		
		private float maxScale;

		// Constructor
		public Img(Bitmap bitmap, Resources resouces, float scale) {
			super();
			this.bitmap = bitmap;
			// ��־��һ�μ���Ϊtrue
			firstLoad = true;
			getMetrics(resouces);
			maxScale = scale;
		}
		
		/**
		 * ����������Ļ����ʾ�߾�
		 */
		private void resetScreenMargin() {
			//�����������ұ߾�
			if (width * scaleX > IrisAnalysisActivity.mContainerWidth) {//���������Ļ�Ŀ�ȣ�����Ϊ��Ļ���
				SCREEN_MARGIN_WIDTH_LEFT = (float) IrisAnalysisActivity.mContainerWidth;
				SCREEN_MARGIN_WIDTH_RIGHT = (float) IrisAnalysisActivity.mContainerWidth;
			} else {//������Ϊʵ�ʵĴ�С
				SCREEN_MARGIN_WIDTH_LEFT = width * scaleX;
				SCREEN_MARGIN_WIDTH_RIGHT = width * scaleX;
			}
			//�����������±߾�
			if (height * scaleY > IrisAnalysisActivity.mContainerHeight) {
				SCREEN_MARGIN_HEIGHT_TOP = (float) (IrisAnalysisActivity.mContainerHeight - BOTTOM_FIX);
				SCREEN_MARGIN_HEIGHT_BOTTOM = (float) (IrisAnalysisActivity.mContainerHeight + BOTTOM_FIX);
			} else {
				SCREEN_MARGIN_HEIGHT_TOP = height * scaleY - BOTTOM_FIX;
				SCREEN_MARGIN_HEIGHT_BOTTOM = height * scaleY + BOTTOM_FIX;
			}
		}
		/**
		 * ����ͼƬ��λ�ú�������Ϣ
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
			//����ֻ����ȱ������ŵ�����--�������ű�����0.5-8֮��
			if (scale_x == scale_y && (double) scale_x >= 0.5D && scale_x <= maxScale) {
				//�������ź��ͼƬ��ȵ�һ��
				float scaled_half_width = (float) (width / 2) * scale_x;
				float scaled_half_height = (float) (height / 2) * scale_y;
				float left_margin = screen_center_x - scaled_half_width;
				float top_margin = screen_center_y - scaled_half_height;
				float right_margin = IrisAnalysisActivity.mContainerWidth - screen_center_x - scaled_half_width;
				float bottom_margin = IrisAnalysisActivity.mContainerHeight - screen_center_y - scaled_half_height;
				
				minX = left_margin;
				maxX = minX + scaled_half_width * 2F;
				if (left_margin < 0) {//ͼƬ���б߽��Ƴ���Ļ���������꽫ͼƬ��ʾ����Ļ֮��
					minX = 0;//����ͼ���Ƴ���Ļ
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

				//����ͼƬ���м�λ��
				centerX = minX + (maxX - minX) / 2F;
				centerY = minY + (maxY - minY) / 2F;
				
				//scaleX = scale_x;
				//scaleY = scale_y;
				return true;
			}
			return false;
		}
		/**
		 * ����ͼƬ��λ�ú�������Ϣ
		 * @param positionAndScale
		 * @return
		 */
		public boolean setPos(PositionAndScale positionAndScale) {
			float scale_x;
			float scale_y;
			// ������ǵȱ�������--ȡx��������ű���
			if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
				scale_x = positionAndScale.getScaleX();
			} else {// ����ȡ��һ��ֵ
				scale_x = positionAndScale.getScale();
			}
			// ������ǵȱ�������--ȡy��������ű���
			if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
				scale_y = positionAndScale.getScaleY();
			} else {
				scale_y = positionAndScale.getScale();
			}

			return setPos(positionAndScale.getXOff(),
					positionAndScale.getYOff(), scale_x, scale_y);
		}

		/**
		 * ��ȡ��ʾ����Ĵ�С���ҽ�������--����ʱ����ʾ�����gallery
		 * 
		 * @param resources
		 */
		public void getMetrics(Resources resources) {
			DisplayMetrics metrics = resources.getDisplayMetrics();
			// ȷ�������ʾ���--����Ǻ���
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// ��ȡ���,�߶��е����ֵ
				this.displayWidth = Math.max(metrics.widthPixels,
						metrics.heightPixels);
			} else {// �������������ȡ���,�߶��е���Сֵ
				this.displayWidth = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			}
			// ȷ�������ʾ�߶�--����Ǻ���
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// ��ȡ���,�߶��е���Сֵ
				this.displayHeight = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			} else {// �������������ȡ���,�߶��е����ֵ
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
			//����ͼƬ�Ĺ��п��
			width = drawable.getIntrinsicWidth();
			//����ͼƬ�Ĺ��и߶�
			height = drawable.getIntrinsicHeight();
			if (firstLoad) {//����ǵ�һ�μ��أ�����ʾ��λ��ָ���ڿ���ʾ������м�λ��
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
