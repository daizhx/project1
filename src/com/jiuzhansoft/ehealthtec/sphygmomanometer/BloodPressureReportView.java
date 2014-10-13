package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BloodPressureReportView extends ImageView{
	
	private ArrayList sysList; 
	private ArrayList diaList; 
	
	public void setList(ArrayList sys, ArrayList dia){
		sysList = sys;
		diaList = dia;
	}
	
	public BloodPressureReportView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(sysList == null || diaList == null)
			return;
		if(sysList.size() == 0 || diaList.size() == 0)
			return;
		Paint paint = new Paint();
		paint.setAntiAlias(true);  
		paint.setColor(Color.GRAY); 
		paint.setStrokeWidth(5.0f);
		paint.setTextSize(25.0f);		
		for(int i = 0; i < sysList.size(); i++){
			if(i >= diaList.size())
				continue;
			canvas.drawCircle((float)38 / 513 * getWidth() + (float)394 / 513 * getWidth() * Integer.parseInt(sysList.get(i).toString()) / 200, 
					(float)65 / 321 * getHeight() + (float)237 / 321 * getHeight() * (120 - Integer.parseInt(diaList.get(i).toString())) / 120, getWidth() / 100, paint);
		}
	}
	
	

}
