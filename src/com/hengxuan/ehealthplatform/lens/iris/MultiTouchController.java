package com.hengxuan.ehealthplatform.lens.iris;

import java.lang.reflect.Method;

import com.hengxuan.ehealthplatform.log.Log;

import android.view.MotionEvent;


public class MultiTouchController {


	private static int ACTION_POINTER_INDEX_SHIFT = 0;
	private static int ACTION_POINTER_UP = 0;
	public static final boolean DEBUG = false;
	private static final long EVENT_SETTLE_TIME_INTERVAL = 20L;
	private static final float MAX_MULTITOUCH_DIM_JUMP_SIZE = 40F;
	private static final float MAX_MULTITOUCH_POS_JUMP_SIZE = 30F;
	public static final int MAX_TOUCH_POINTS = 20;
	private static final float MIN_MULTITOUCH_SEPARATION = 30F;
	
	private static final int MODE_NOTHING = 0;
	private static final int MODE_DRAG = 1;	
	private static final int MODE_PINCH = 2;
	//MotionEvent各个方法定义
	private static Method m_getHistoricalPressure;
	private static Method m_getHistoricalX;
	private static Method m_getHistoricalY;
	private static Method m_getPointerCount;
	private static Method m_getPointerId;
	private static Method m_getPressure;
	private static Method m_getX;
	private static Method m_getY;
	
	public static final boolean multiTouchSupported;
	private static final int pointerIds[];
	private static final float pressureVals[];
	private static final float xVals[];
	private static final float yVals[];
	private boolean handleSingleTouchEvents;
	private PointInfo mCurrPt;
	private float mCurrPtAng;
	private float mCurrPtDiam;
	private float mCurrPtHeight;
	private float mCurrPtWidth;
	private float mCurrPtX;
	private float mCurrPtY;
	private PositionAndScale mCurrXform;
	private int mMode;
	private PointInfo mPrevPt;
	private long mSettleEndTime;
	private long mSettleStartTime;
	MultiTouchObjectCanvas objectCanvas;
	private Object selectedObject;
	private float startPosX;
	private float startPosY;
	private float startScaleOverPinchDiam;
	private float startScaleXOverPinchWidth;
	private float startScaleYOverPinchHeight;

	static {
		boolean flag = false;
		try {
			ACTION_POINTER_UP = 6;
			ACTION_POINTER_INDEX_SHIFT = 8;
			//通过反射获取各个设置函数
			m_getPointerCount = MotionEvent.class.getMethod("getPointerCount",
					new Class[0]);
			m_getPointerId = MotionEvent.class.getMethod("getPointerId",
					new Class[] { Integer.TYPE });
			m_getPressure = MotionEvent.class.getMethod("getPressure",
					new Class[] { Integer.TYPE });
			m_getHistoricalX = MotionEvent.class.getMethod("getHistoricalX",
					new Class[] { Integer.TYPE, Integer.TYPE });
			m_getHistoricalY = MotionEvent.class.getMethod("getHistoricalY",
					new Class[] { Integer.TYPE, Integer.TYPE });
			m_getHistoricalPressure = MotionEvent.class.getMethod(
					"getHistoricalPressure", new Class[] { Integer.TYPE,
							Integer.TYPE });

			m_getX = MotionEvent.class.getMethod("getX",
					new Class[] { Integer.TYPE });
			m_getY = MotionEvent.class.getMethod("getY",
					new Class[] { Integer.TYPE });
			flag = true;
		} catch (Exception e) {
			Log.e("MultiTouchController", "static initializer failed", e);
		}
		multiTouchSupported = flag;
		if (multiTouchSupported) {
			try {
				ACTION_POINTER_UP = MotionEvent.class.getField(
						"ACTION_POINTER_UP").getInt(null);
				ACTION_POINTER_INDEX_SHIFT = MotionEvent.class.getField(
						"ACTION_POINTER_INDEX_SHIFT").getInt(null);
			} catch (Exception exception1) {
			}
		}
		xVals = new float[MAX_TOUCH_POINTS];
		yVals = new float[MAX_TOUCH_POINTS];
		pressureVals = new float[MAX_TOUCH_POINTS];
		pointerIds = new int[MAX_TOUCH_POINTS];
	}

	public MultiTouchController(MultiTouchObjectCanvas multitouchobjectcanvas) {
		this(multitouchobjectcanvas, true);
	}

	public MultiTouchController(MultiTouchObjectCanvas multitouchobjectcanvas,
			boolean flag) {
		selectedObject = null;
		mCurrXform = new PositionAndScale();
		mMode = MODE_NOTHING;
		mCurrPt = new PointInfo();
		mPrevPt = new PointInfo();
		handleSingleTouchEvents = flag;
		objectCanvas = multitouchobjectcanvas;
	}

	private void anchorAtThisPositionAndScale() {
		if (selectedObject != null) {
			float f = 0.0f;
			//获取位置和关于缩放的信息
			objectCanvas.getPositionAndScale(selectedObject, mCurrXform);
			if (!(mCurrXform.updateScale)) {
				f = 1F;
			} else {
				if (mCurrXform.scale == 0F)
					f = 1F;
				else
					f = mCurrXform.scale;
			}

			extractCurrPtInfo();
			startPosX = (mCurrPtX - mCurrXform.xOff) * (1F / f);
			startPosY = (mCurrPtY - mCurrXform.yOff) * (1F / f);
			//直径方向的缩放
			startScaleOverPinchDiam = mCurrXform.scale / mCurrPtDiam;
			//x轴方向的缩放
			startScaleXOverPinchWidth = mCurrXform.scaleX / mCurrPtWidth;
			//y轴方向的缩放
			startScaleYOverPinchHeight = mCurrXform.scaleY / mCurrPtHeight;
		}
	}

	private void decodeTouchEvent(int i, float af[], float af1[], float af2[],
			int ai[], int j, boolean flag, long l) {
		PointInfo pointinfo = mPrevPt;
		mPrevPt = mCurrPt;
		mCurrPt = pointinfo;
		mCurrPt.set(i, af, af1, af2, ai, j, flag, l);
		multiTouchController();
	}
	/**
	 * 设置多点触屏，放大，角度信息
	 */
	private void extractCurrPtInfo() {
		mCurrPtX = mCurrPt.getX();
		mCurrPtY = mCurrPt.getY();
		float f2;
		if (!mCurrXform.updateScale)
			f2 = 0F;
		else
			f2 = mCurrPt.getMultiTouchDiameter();
		mCurrPtDiam = Math.max(21.3F, f2);
		if (!mCurrXform.updateScaleXY)
			f2 = 0F;
		else
			f2 = mCurrPt.getMultiTouchWidth();
		mCurrPtWidth = Math.max(30F, f2);
		if (!mCurrXform.updateScaleXY)
			f2 = 0F;
		else
			f2 = mCurrPt.getMultiTouchHeight();
		mCurrPtHeight = Math.max(30F, f2);
		if (!mCurrXform.updateAngle)
			mCurrPtAng = 0F;
		else
			mCurrPtAng = mCurrPt.getMultiTouchAngle();
	}

	private void multiTouchController() {
		switch (mMode) {
		case MODE_NOTHING:
			if (mCurrPt.isDown()) {
				selectedObject = objectCanvas
						.getDraggableObjectAtPoint(mCurrPt);
				if (selectedObject != null) {
					mMode = MODE_DRAG;
					objectCanvas.selectObject(selectedObject, mCurrPt);
					anchorAtThisPositionAndScale();
					mSettleEndTime = mCurrPt.getEventTime();
					mSettleStartTime = mCurrPt.getEventTime();
				}
			}
			break;
		case MODE_DRAG: //拖动控件
			if (!mCurrPt.isDown()) {
				mMode = MODE_NOTHING;
				selectedObject = null;
				objectCanvas.selectObject(null, mCurrPt);
			//如果是多点触屏，模式改为双手捏合
			} else if (mCurrPt.isMultiTouch()) {
				mMode = MODE_PINCH;
				anchorAtThisPositionAndScale();
				mSettleStartTime = mCurrPt.getEventTime();
				mSettleEndTime = mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;
			//否则为简单的对象移动
			} else {
				//如果指定的判断时间没有结束，继续设置数值
				if (mCurrPt.getEventTime() < mSettleEndTime)
					anchorAtThisPositionAndScale();
				else //处理捏合或者移动对象的操作
					performDragOrPinch();
			}
			break;
		case MODE_PINCH: //双手捏合控件
			if (!mCurrPt.isMultiTouch() || !mCurrPt.isDown()) {
				//如果目前没有触屏操作，设置模式为nothing状态
				if (!mCurrPt.isDown()) {
					mMode = MODE_NOTHING;
					selectedObject = null;
					objectCanvas.selectObject(null, mCurrPt);
				//如果不是多点触屏，设置为图像拖动状态，并且从新设定相关模式的开始和结束时间，开始时间和结束时间的间隔设为20ms
				} else {
					mMode = MODE_DRAG;
					anchorAtThisPositionAndScale();
					mSettleStartTime = mCurrPt.getEventTime();
					mSettleEndTime = mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;
				}
			}
			if ((Math.abs(mCurrPt.getX() - mPrevPt.getX()) <= MAX_MULTITOUCH_POS_JUMP_SIZE)
					&& (Math.abs(mCurrPt.getY() - mPrevPt.getY()) <= MAX_MULTITOUCH_POS_JUMP_SIZE)
					&& (Math.abs(mCurrPt.getMultiTouchWidth()
							- mPrevPt.getMultiTouchWidth()) * 0.5F <= MAX_MULTITOUCH_DIM_JUMP_SIZE)
					&& (Math.abs(mCurrPt.getMultiTouchHeight()
							- mPrevPt.getMultiTouchHeight()) * 0.5F <= MAX_MULTITOUCH_DIM_JUMP_SIZE)) {
				if (mCurrPt.eventTime < mSettleEndTime)
					anchorAtThisPositionAndScale();
				else //执行双手捏合操作
					performDragOrPinch();
			} else {//重新设定相关的参数
				anchorAtThisPositionAndScale();
				mSettleStartTime = mCurrPt.getEventTime();
				mSettleEndTime = mSettleStartTime + EVENT_SETTLE_TIME_INTERVAL;
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 执行拖动或者捏合操作
	 */
	private void performDragOrPinch() {
		//如果选定对象不为空，执行相关的操作
		if (selectedObject != null) {
			float f;
			if (!mCurrXform.updateScale) {
				f = 1F;
			} else {
				if (mCurrXform.scale == 0F)
					f = 1F;
				else
					f = mCurrXform.scale;
			}

			extractCurrPtInfo();
			//设置缩放或者移动的参数
			mCurrXform.set(mCurrPtX - startPosX * f, mCurrPtY - startPosY * f,
					startScaleOverPinchDiam * mCurrPtDiam,
					startScaleXOverPinchWidth * mCurrPtWidth,
					startScaleYOverPinchHeight * mCurrPtHeight);
			//执行相关的操作
			objectCanvas.setPositionAndScale(selectedObject, mCurrXform,
					mCurrPt);

		}
	}

	protected boolean getHandleSingleTouchEvents() {
		return handleSingleTouchEvents;
	}
	/**
	 * 处理触屏操作相关的事件
	 * @param motionevent
	 * @return
	 */
	public boolean onTouchEvent(MotionEvent motionevent) {
		boolean isHandled = false;
		try {
			int i = 0;

			if (multiTouchSupported) {
				//获取触摸点的个数
				i = ((Integer) m_getPointerCount.invoke(motionevent, new Object[0]))
						.intValue();
			} else {
				i = 1;
			}

			if (mMode != 0 || handleSingleTouchEvents || i != 1) {
				int j = motionevent.getAction();
				int k = motionevent.getHistorySize() / i;
				int l = 0;
				//获取各个触摸点自上次操作以来的历史信息数据
				while (l <= k) {
					boolean flag = true;
					if (l == k)
						flag = false;
					//处理多点触屏的操作
					if (multiTouchSupported && i != 1) {
						int m = 0;
						//获取触摸点个数
						int n = Math.min(i, MAX_TOUCH_POINTS);
						//获取各个触摸点的数据并且保存到本地变量中
						while (m < n) {
							pointerIds[m] = ((Integer) m_getPointerId.invoke(
									motionevent, new Object[]{Integer.valueOf(m)})).intValue();

							if (flag) {
								//获取第m个触摸点对应的第l个触摸点x的历史数据
								xVals[m] = ((Float) m_getHistoricalX.invoke(
										motionevent, new Object[]{Integer.valueOf(m),Integer.valueOf(l)})).floatValue();
								//获取第m个触摸点对应的第l个触摸点y的历史数据
								yVals[m] = ((Float) m_getHistoricalY.invoke(
										motionevent, new Object[]{Integer.valueOf(m),Integer.valueOf(l)})).floatValue();
								//获取第m个触摸点对应的第l个触摸点压力参数的历史数据
								pressureVals[m] = ((Float) m_getHistoricalPressure
										.invoke(motionevent, new Object[]{Integer.valueOf(m),Integer.valueOf(l)}))
										.floatValue();
							} else {
								//获取目前触摸点的x数据
								xVals[m] = ((Float) m_getX.invoke(motionevent,
										new Object[]{Integer.valueOf(m)})).floatValue();
								//获取目前触摸点的y数据
								yVals[m] = ((Float) m_getY.invoke(motionevent,
										new Object[]{Integer.valueOf(m)})).floatValue();
								//获取目前触摸点的压力参数数据
								pressureVals[m] = ((Float) m_getPressure
										.invoke(motionevent, new Object[]{Integer.valueOf(m)}))
										.floatValue();
							}
							m++;
						}
					} else {//针对单点触屏而言的处理
						if (flag) {
							xVals[0] = motionevent.getHistoricalX(l);
							yVals[0] = motionevent.getHistoricalY(l);
							pressureVals[0] = motionevent
							.getHistoricalPressure(l);
						} else {
							xVals[0] = motionevent.getX();
							yVals[0] = motionevent.getY();
							pressureVals[0] = motionevent.getPressure();
						}
					}

					boolean actionFlag = true;
					if (!flag) {
						int j1 = (1 << ACTION_POINTER_INDEX_SHIFT) - 1 & j;
						if (j == MotionEvent.ACTION_UP || j1 == ACTION_POINTER_UP || j == MotionEvent.ACTION_CANCEL) {
							actionFlag = false;
						}
					}

					decodeTouchEvent(i, xVals, yVals, pressureVals, pointerIds,
							(flag == true)?MotionEvent.ACTION_MOVE:j, actionFlag, (flag == false)?motionevent.getEventTime():motionevent.getHistoricalEventTime(l));
					l++;
				}

				isHandled = true;
			}
		} catch (Exception e) {
			Log.e("MultiTouchController", "onTouchEvent() failed", e);
			isHandled = false;
		}
		return isHandled;
	}

	protected void setHandleSingleTouchEvents(boolean flag) {
		handleSingleTouchEvents = flag;
	}
	/**
	 * MultiTouchObjectCanvas interface definition
	 * @author Administrator
	 *
	 */
	public interface MultiTouchObjectCanvas {

		public abstract Object getDraggableObjectAtPoint(PointInfo pointinfo);

		public abstract void getPositionAndScale(Object obj,
				PositionAndScale positionandscale);

		public abstract void selectObject(Object obj, PointInfo pointinfo);

		public abstract boolean setPositionAndScale(Object obj,
				PositionAndScale positionandscale, PointInfo pointinfo);
	}
	/**
	 * 点信息的定义
	 * @author Administrator
	 *
	 */
	public class PointInfo {

		private int action;
		
		private float angle;
		private boolean angleIsCalculated;
		
		private float diameter;
		private boolean diameterIsCalculated;
		
		private float diameterSq;
		private boolean diameterSqIsCalculated;
		
		private float dx;
		private float dy;
		private long eventTime;
		private boolean isDown;
		private boolean isMultiTouch;
		private int numPoints;
		private int pointerIds[];
		private float pressureMid;
		private float pressures[];
		private float xMid;
		private float xs[];
		private float yMid;
		private float ys[];
		
		public PointInfo() {
			//最大20个触摸点
			xs = new float[MAX_TOUCH_POINTS];
			ys = new float[MAX_TOUCH_POINTS];
			pressures = new float[MAX_TOUCH_POINTS];
			pointerIds = new int[MAX_TOUCH_POINTS];
		}

		private void set(int numPoints, float af[], float af1[], float af2[], int ai[],
				int action, boolean isDown, long eventTime) {
			this.eventTime = eventTime;
			this.action = action;
			this.numPoints = numPoints;
			int k = 0;
			while (k < this.numPoints) {
				xs[k] = af[k];
				ys[k] = af1[k];
				pressures[k] = af2[k];
				pointerIds[k] = ai[k];
				k++;
			}

			this.isDown = isDown;

			if (this.numPoints >= 2)
				isMultiTouch = true;
			else
				isMultiTouch = false;

			if (isMultiTouch) {
				//计算各个点的连线的中间点
				xMid = (af[0] + af[1]) / 2;
				yMid = (af1[0] + af1[1]) / 2;
				pressureMid = (af2[0] + af2[1]) / 2;
				//计算两个出点x和y向之间距离的绝对值
				dx = Math.abs(af[1] - af[0]);
				dy = Math.abs(af1[1] - af1[0]);
			} else {
				xMid = af[0];
				yMid = af1[0];
				pressureMid = af2[0];
				dy = 0F;
				dx = 0F;
			}
			angleIsCalculated = false;
			diameterIsCalculated = false;
			diameterSqIsCalculated = false;
		}
		
		/**
		 * 用另一个pointinfo设置目前的对象信息
		 * @param pointinfo
		 */
		public void set(PointInfo pointinfo) {
			numPoints = pointinfo.numPoints;
			int j = 0;
			while (j < numPoints) {
				xs[j] = pointinfo.xs[j];
				ys[j] = pointinfo.ys[j];
				pressures[j] = pointinfo.pressures[j];
				pointerIds[j] = pointinfo.pointerIds[j];
				j++;
			}
			xMid = pointinfo.xMid;
			yMid = pointinfo.yMid;
			pressureMid = pointinfo.pressureMid;
			dx = pointinfo.dx;
			dy = pointinfo.dy;
			diameter = pointinfo.diameter;
			diameterSq = pointinfo.diameterSq;
			angle = pointinfo.angle;
			isDown = pointinfo.isDown;
			action = pointinfo.action;
			isMultiTouch = pointinfo.isMultiTouch;
			diameterIsCalculated = pointinfo.diameterIsCalculated;
			diameterSqIsCalculated = pointinfo.diameterSqIsCalculated;
			angleIsCalculated = pointinfo.angleIsCalculated;
			eventTime = pointinfo.eventTime;
		}
		
		/**
		 * 计算一个整型数值的平方根
		 * @param i
		 * @return 平方根
		 */
		private int julery_isqrt(int x) {
			int temp = 0;
			int v_bit = 15;
			int n = 0;
			int b = 0x8000;
			if (x <= 1) {
				return x;
			}
			do {
				temp = ((n << 1) + b) << (v_bit--);
				if (x >= temp) {
					n += b;
					x -= temp;
				}
				b >>= 1;
				if (b <= 0) {
					break;
				}
			} while (true);
			return n;
		}

		public int getAction() {
			return action;
		}

		public long getEventTime() {
			return eventTime;
		}
		/**
		 * 获取多点触屏的角度
		 * @return
		 */
		public float getMultiTouchAngle() {
			if (!angleIsCalculated) {
				if (!isMultiTouch) {
					angle = 0F;
				} else {
					angle = (float) Math.atan2(ys[1] - ys[0], xs[1] - xs[0]);
				}
				angleIsCalculated = true;
			}
			return angle;
		}
		/**
		 * 计算触摸点到原点距离的平方根
		 * @return
		 */
		public float getMultiTouchDiameter() {
			if (!diameterIsCalculated) {
				if (isMultiTouch) {
					float f = getMultiTouchDiameterSq();
					if (f == 0F) {
						diameter = 0F;
					} else {
						//将计算的距离左移8位--即乘以256
						diameter = (float) julery_isqrt((int)(256F * f)) / 16F;
					}
					if (diameter < dx) {
						diameter = dx;
					}
					if (diameter < dy) {
						diameter = dy;
					}
				} else {
					diameter = 0F;
				}
				diameterIsCalculated = true;
			}
			return diameter;
		}
		/**
		 * 计算触摸点到原点的距离的平方
		 * @return
		 */
		public float getMultiTouchDiameterSq() {
			if (!diameterSqIsCalculated) {
				if (isMultiTouch) {
					diameterSq = dx * dx + dy * dy;
				} else {//如果是单点，则返回0
					diameterSq = 0F;
				}
				diameterSqIsCalculated = true;
			}
			return diameterSq;
		}
		/**
		 * 获取多点触摸多点距离的高度
		 * @return
		 */
		public float getMultiTouchHeight() {
			return (isMultiTouch)?dy:0;
		}
		/**
		 * 获取多点触摸多点距离的宽度
		 * @return
		 */
		public float getMultiTouchWidth() {
			return (isMultiTouch)?dx:0;
		}
		/**
		 * 获取触摸点得个数
		 * @return
		 */
		public int getNumTouchPoints() {
			return numPoints;
		}
		/**
		 * 获取触摸点id的个数
		 * @return
		 */
		public int[] getPointerIds() {
			return pointerIds;
		}

		public float getPressure() {
			return pressureMid;
		}

		public float[] getPressures() {
			return pressures;
		}

		public float getX() {
			return xMid;
		}

		public float[] getXs() {
			return xs;
		}

		public float getY() {
			return yMid;
		}

		public float[] getYs() {
			return ys;
		}

		public boolean isDown() {
			return isDown;
		}

		public boolean isMultiTouch() {
			return isMultiTouch;
		}
	}

	public class PositionAndScale {

		private float scale;
		private float scaleX;
		private float scaleY;
		private boolean updateAngle;
		private boolean updateScale;
		private boolean updateScaleXY;
		private float xOff;
		private float yOff;
		/**
		 * constructor
		 */
		public PositionAndScale() {
		}
		
		protected void set(float offset_x, float offset_y, float fScale, float scale_x, float scale_y) {
			xOff = offset_x;
			yOff = offset_y;

			if (fScale == 0F)
				scale = 1F;
			else
				scale = fScale;

			if (scale_x == 0F)
				scaleX = 1F;
			else
				scaleX = scale_x;

			if (scale_y == 0F)
				scaleY = 1F;
			else
				scaleY = scale_y;
		}
		
		public void set(float offset_x, float offset_y, boolean updateScale, float fScale,
				boolean updateScaleXY, float scale_x, float scale_y, boolean updateAngle) {
			xOff = offset_x;
			yOff = offset_y;
			this.updateScale = updateScale;
			if (fScale == 0F)
				scale = 1F;
			else
				scale = fScale;

			this.updateScaleXY = updateScaleXY;
			if (scale_x == 0F)
				scaleX = 1F;
			else
				scaleX = scale_x;
			if (scale_y == 0F)
				scaleY = 1F;
			else
				scaleY = scale_y;

			this.updateAngle = updateAngle;
		}

		public float getScale() {
			return (!updateScale)?1F:scale;
		}

		public float getScaleX() {
			return (!updateScaleXY)?1F:scaleX;
		}

		public float getScaleY() {
			return (!updateScaleXY)?1F:scaleY;
		}

		public float getXOff() {
			return xOff;
		}
		public float getYOff() {
			return yOff;
		}
	}

}
