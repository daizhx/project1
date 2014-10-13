package com.jiuzhansoft.ehealthtec.lens.iris;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.jiuzhansoft.ehealthtec.http.utils.LanguageUtil;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.xml.XMLPullParserUtils;

import android.content.Context;

public class IrisDataCache {

	private static final String TAG = "IrisDataCache";
	private static final String LEFT_EYE_DATA = "lefteye.xml";
	private static final String RIGHT_EYE_DATA = "righteye.xml";
	private static final String LEFT_EYE_DATA_EN = "lefteye_en.xml";
	private static final String RIGHT_EYE_DATA_EN = "righteye_en.xml";
	private static IrisDataCache _instance = new IrisDataCache();
	
	private static final int EYE_MODULE_RADIUS = 232;
	private static final int STANDARD_EYE_MODULE_RADIUS = 90;
	//左眼的数据缓冲
	private List<Organ> leftIrisData = null;
	private List<Organ> rightIrisData = null;
	private Context context = null;
	private float currentRaduis;
	private float getMaxRaduis, getScale;
	
	private IrisDataCache(){
		
	}
	public static IrisDataCache getInstance(){
		return _instance;
	}
	
	public float getMaxRaduis(List list){
		Iterator<Organ> iter = list.iterator();
		while(iter.hasNext()){
			Organ currentOrgan = iter.next();
			// getMaxRaduis = currentRaduis;
			currentRaduis = currentOrgan.getOutRaduis();
			if(currentRaduis > getMaxRaduis)
				getMaxRaduis = currentRaduis;
		}
		return getMaxRaduis;
	}
	/**
	 * 根据相关的坐标信息获取器官id
	 * @param x--点击的x坐标值
	 * @param y--点击的y坐标值
	 * @param center_x--目前的中心点x坐标值
	 * @param center_y--目前的中心点y坐标值
	 * @param isLeftIris--是否左眼，true：左眼；false：右眼
	 * @return
	 */
	public Organ getOrganIdByPositionInfo(float x, float y,
			final float center_x, final float center_y, final float outRaduis, 
			final float minRaduis, final float midRaduis, final float scale_x,
			final float scale_y, final boolean isLeftIris) {
		XMLPullParserUtils utils = null;
		InputStream is = null;
		if (isLeftIris) {
			// 如果数据还没有进行加载，从文件中加载数据
			if (this.leftIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + LEFT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(LEFT_EYE_DATA_EN);
					else
						is = context.getAssets().open(LEFT_EYE_DATA);
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.leftIrisData = utils.getAll();
					getMaxRaduis(leftIrisData);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else {
			if (this.rightIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + RIGHT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(RIGHT_EYE_DATA_EN);
					else
						is = context.getAssets().open(RIGHT_EYE_DATA);						
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.rightIrisData = utils.getAll();
					getMaxRaduis(rightIrisData);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		// 计算角度
		// float angle = Math.atan(d)
		double angle = Math.atan2(center_y - y, x - center_x) * 180 / Math.PI;
		if (angle < 0) {
			// 将该角度转换为正值
			angle = 360 + angle;
		}
		if (Log.D) {
			Log.d(TAG, "角度是:" + angle);
		}
		// 计算到中心点的距离
		double distance = Math.sqrt((x - center_x) * (x - center_x)
				+ (y - center_y) * (y - center_y));
		if (Log.D) {
			Log.d(TAG, "到原点（" + center_x + "," + center_y + "）的距离是：" + distance);
		}
		return this.findOrganInfo(isLeftIris, angle, distance,outRaduis, minRaduis, midRaduis, scale_x,scale_y);
	}
	
	// getinr, getoutr:xml读取的内外径
	// current_R:合并后虹膜标尺的最外半径
	// thisinr:合并后虹膜标尺的最内主环半径
	// thismidr:合并后虹膜标尺第二主环半径
	private float[] calculateInAndOutR(float getinr, 
			float getoutr, float current_R, float getscale,
			float thisinr, float thismidr){
		float getvalues[] = new float[2];
		float inr = 0f;//getRealRadius(getinr,getscale)*(current_R/(getMaxRaduis*getscale));
		float outr = 0f;//getRealRadius(getoutr,getscale)*(current_R/(getMaxRaduis*getscale));
		if(getinr == 16f && getoutr == 20f){
			inr = thisinr;
			outr = thisinr + ((thismidr - thisinr)*2)/17;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}else if(getinr == 20f && getoutr == 50f){
			inr = thisinr + ((thismidr - thisinr)*2)/17;
			outr = thismidr;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}else if(getinr == 50f && getoutr == 51f){
			inr = thismidr;
			outr = thismidr + (current_R - thismidr)/40;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		else if(getinr == 51f && getoutr == 55f){
			inr = thismidr + (current_R - thismidr)/40;
			outr = thismidr + (current_R - thismidr)/8;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		else if(getinr == 55f && getoutr == 70f){
			inr = thismidr + (current_R - thismidr)/8;
			outr = thismidr + (current_R - thismidr)/2;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		else if(getinr == 70f && getoutr == 80f){
			inr = thismidr + (current_R - thismidr)/2;
			outr = thismidr + ((current_R - thismidr)*3)/4;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}else if(getinr == 80f && getoutr == 85f){
			inr = thismidr + ((current_R - thismidr)*3)/4;
			outr = thismidr + ((current_R - thismidr)*7)/8;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		} else if(getinr == 85f && getoutr == 90f){
			inr = thismidr + ((current_R - thismidr)*7)/8;
			// outr = thismidr + ((current_R - thismidr)*7)/8;
			outr = current_R;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		return getvalues;
		// Log.d(TAG, "getRealInRadius:"+ inr+"\tgetRealOutRadius:"+outr+"\tgetinr:"+getinr+"\tgetour:"+getoutr);
	}
	
	private Organ findOrganInfo(boolean isLeftIris,double angle,double distance,
			float out_Raduis, float in_Raduis, float mid_Raduis, float scale_x,float scale_y) {
		Organ organ = null;
		List<Organ> findedIrisData = null;
		if(isLeftIris){
			findedIrisData = this.leftIrisData;
		}else{
			findedIrisData = this.rightIrisData;
		}
		if(Log.D){
			String temp = (isLeftIris == true)?"左眼":"右眼";
			Log.d(TAG, "判断的是:"+ temp);
		}
		//判断指定的点是否落在指定的区域
		float getOranValues[] = new float[2];
		float start_angle = 0f, end_angle = 0f;
		for (Organ item : findedIrisData) {
			if(getMaxRaduis != 0){
				getOranValues = calculateInAndOutR(item.getInRaduis(), item.getOutRaduis(), 
						out_Raduis, scale_x, in_Raduis, mid_Raduis);
				float getRealInRadius = getOranValues[0], getRealOutRadius=getOranValues[1];
				// Log.d(TAG, "getRealInRadius:"+ getRealInRadius+"\tgetRealOutRadius:"+getRealOutRadius);
				start_angle = item.getStartAngle();
				end_angle = item.getEndAngle();
				start_angle = (start_angle+270)%360;
				end_angle = (end_angle+270)%360;
				if(end_angle < start_angle)
					end_angle = end_angle+360;
				else if(end_angle == start_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}
				if(angle > start_angle){
					if(angle < end_angle && distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}else if((angle+360) < end_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}/*else if((start_angle %360) == 270 && (end_angle % 360) == 270){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}*/
				/*if (angle > start_angle && angle < end_angle
						&& distance > getRealInRadius
						&& distance < getRealOutRadius) {
					organ = item;
					//organId = item.getOrganId();
					if(Log.D){
						Log.d(TAG, "找到指定的区域，器官id=" + item.getOrganId() + "name="
								+ item.getName());
					}
					break;
				}*/
			}else{
				getMaxRaduis = 90;
				getOranValues = calculateInAndOutR(item.getInRaduis(), item.getOutRaduis(), 
						out_Raduis, scale_x, in_Raduis, mid_Raduis);
				float getRealInRadius = getOranValues[0], getRealOutRadius=getOranValues[1];
				start_angle = item.getStartAngle();
				end_angle = item.getEndAngle();
				start_angle = (start_angle+270)%360;
				end_angle = (end_angle+270)%360;
				if(end_angle < start_angle)
					end_angle = end_angle+360;
				else if(end_angle == start_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}
				if(angle > start_angle){
					if(angle < end_angle && distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}else if((angle+360) < end_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}/*else if((start_angle %360) == 270 && (end_angle % 360) == 270){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}*/
				/*if (angle > start_angle && angle < end_angle
						&& distance > getRealInRadiusgetRealRadius(item.getInRaduis(),scale_x)*(out_Raduis/(getMaxRaduis))*scale_x
						&& distance < getRealOutRadiusgetRealRadius(item.getOutRaduis(),scale_x)*(out_Raduis/(getMaxRaduis))*scale_x) {
					organ = item;
					//organId = item.getOrganId();
					if(Log.D){
						Log.d(TAG, "找到指定的区域，器官id=" + item.getOrganId() + "name="
								+ item.getName());
					}
					break;
				}*/
			}
		}
		
		return organ;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	/**
	 * 先不考虑放大的倍数
	 * @param radius
	 * @return
	 */
	public float getRealRadius(float radius,float scale_factor){
		// return ((float)EYE_MODULE_RADIUS/(float)STANDARD_EYE_MODULE_RADIUS)*radius*scale_factor;
		return radius*scale_factor;
	}
	
	public void initIrisDataByIndex(int index){
		XMLPullParserUtils utils = null;
		InputStream is = null;
		if (index == IrisInspectionActivity.LEFT_IRIS_EYE_ID) {//初始化左眼虹膜相关的数据
			// 如果数据还没有进行加载，从文件中加载数据
			if (this.leftIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + LEFT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(LEFT_EYE_DATA_EN);
					else
						is = context.getAssets().open(LEFT_EYE_DATA);
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.leftIrisData = utils.getAll();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else if(index == IrisInspectionActivity.RIGHT_IRIS_EYE_ID) {
			if (this.rightIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + RIGHT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(RIGHT_EYE_DATA_EN);
					else
						is = context.getAssets().open(RIGHT_EYE_DATA);
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.rightIrisData = utils.getAll();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

}
