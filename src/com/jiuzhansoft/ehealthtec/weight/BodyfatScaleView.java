package com.jiuzhansoft.ehealthtec.weight;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BodyfatScaleView extends ImageView{
	private float weight = 0.0f;
	private float weighto = 0.0f;
	private int width, height;
	public BodyfatScaleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	private int weightLength;
	private boolean modePound;
	
	public void setWeight(float weight){
		this.weighto = weight;
		this.weight = weight;
	}
	
	public void setModePound(boolean pound){
		modePound = pound;
		if(pound){
			weight = Float.parseFloat(new DecimalFormat("0.0").format(weight * 2.2));
		}else{
			weight = weighto;
		}
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		width = this.getMeasuredWidth();
		height = this.getMeasuredHeight();
		weightLength = (weight + "").length();
		Paint paint = new Paint();
		paint.setAntiAlias(true);  
		paint.setColor(Color.BLACK); 
		paint.setStrokeWidth(10.0f);
		paint.setTextSize(height / 3);
		
		canvas.drawText(weight + "", width / 2 - weightLength * height / 13, height / 3, paint);
		
		paint.setStrokeWidth(height * 0.01f);
		paint.setTextSize(height / 8);
		
		int one = (int)(weight * 10) % 10;
		if(one == 0){
			canvas.drawLine(width / 2, height * 0.76f, width / 2, height * 0.55f, paint);
			if(one == 0)
				canvas.drawText((int)weight + "", width / 2 - ((int)weight + "").length() * height / 29, height * 0.52f, paint);
		}else{
			canvas.drawLine(width / 2, height * 0.76f, width / 2, height * 0.7f, paint);
		}
		for(int i = 0; i < 11; i++){
			if((one + i + 1) % 10 == 0){
				canvas.drawLine(width / 2 + width / 24 * (i + 1), height * getRadio(i), width / 2 + width / 24 * (i + 1), height * 0.55f, paint);
				if((one + i + 1) / 10 == 1){
					canvas.drawText((int)(weight + 1) + "", width / 2 + width / 24 * (i + 1) - ((int)(weight + 1) + "").length() * height / 29, height * 0.52f, paint);
				}else{
					canvas.drawText((int)(weight + 2) + "", width / 2 + width / 24 * (i + 1) - ((int)(weight + 2) + "").length() * height / 29, height * 0.52f, paint);
				}
			}else{
				canvas.drawLine(width / 2 + width / 24 * (i + 1), height * getRadio(i), width / 2 + width / 24 * (i + 1), height * 0.7f , paint);
			}
			if((one - 1 - i) % 10 == 0){
				canvas.drawLine(width / 2 - width / 24 * (i + 1), height * getRadio(i), width / 2 - width / 24 * (i + 1), height * 0.55f, paint);
				if((one - 1 - i) / 10 == 0){
					if(one == 0)
						canvas.drawText((int)(weight - 1) + "", width / 2 - width / 24 * (i + 1) - ((int)(weight - 1) + "").length() * height / 29, height * 0.52f, paint);
					else
						canvas.drawText((int)weight + "", width / 2 - width / 24 * (i + 1) - ((int)weight + "").length() * height / 29, height * 0.52f, paint);
				}else{
					if(weight - 1 < 0)
						canvas.drawText(-1 + "", width / 2 - width / 24 * (i + 1) - ((int)(weight - 1) + "").length() * height / 29, height * 0.52f, paint);
					else
						canvas.drawText((int)(weight - 1) + "", width / 2 - width / 24 * (i + 1) - ((int)(weight - 1) + "").length() * height / 29, height * 0.52f, paint);
				}
			}else{
				canvas.drawLine(width / 2 - width / 24 * (i + 1), height * getRadio(i), width / 2 - width / 24 * (i + 1), height * 0.7f, paint);
			}
		}
	}
	public float getRadio(int i){
		float radio = 0.96f;
		
		switch(i){
			case 0:
				radio = 0.88f;
				break;
			case 1:
				radio = 0.89f;
				break;
			case 2:
				radio = 0.90f;
				break;
			case 3:
				radio = 0.92f;
				break;
			case 4:
				radio = 0.95f;
				break;
			default:				
				break;
		}
		
		return radio;
	}
	
}
