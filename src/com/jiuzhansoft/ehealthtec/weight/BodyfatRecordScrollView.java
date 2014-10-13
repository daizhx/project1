package com.jiuzhansoft.ehealthtec.weight;

import java.util.List;

import com.jiuzhansoft.ehealthtec.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class BodyfatRecordScrollView extends ImageView{
	
	public static final int SHOW_YEAR = 1;
	public static final int SHOW_MONTH = 2;
	private int showMode = 2;
	private float width;
	private float height;
	private int index = 30;
	
	private float maxValue;
	private float maxNormalValue;
	private float minNormalValue;
	private float perValue;
	private float bottomHeight;
	
	public void setDate(String date){
		if(showMode == 1)
			index = 12;
		else{
			int month = Integer.parseInt(date.split("-")[1]);
			if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
				index = 31;
			}else if(month == 2){
				int year = Integer.parseInt(date.split("-")[0]);
				if(year % 4 == 0){
					index = 29;
				}else{
					index = 28;
				}
			}else{
				index = 30;
			}
		}
		moveX = 0;
		invalidate();
	}
	
	public void setMaxValue(float max){
		maxValue = max;
	}
	
	public void setMaxNormalValue(float max){
		maxNormalValue = max;
	}
	
	public void setMinNormalValue(float min){
		minNormalValue = min;
	}
	
	public void setShowMode(int mode){
		showMode = mode;
	}
	
	public void setList(List<Integer> list1, List<Float> list2){
		dataIndex = list1;
		data = list2;
		moveX = 0;
		invalidate();
	}
	
	private List<Integer> dataIndex;
	private List<Float> data;
	
	private Bitmap back;

	public BodyfatRecordScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		back = BitmapFactory.decodeResource(getResources(), R.drawable.bodyfat_record_show_back);
	}

	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);		
		
		width = this.getMeasuredWidth();
		height = this.getMeasuredHeight();		
		bottomHeight = height / 13;
		perValue = (height - bottomHeight) / maxValue;
		
		reAssignMoveX();
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);  
		paint.setColor(Color.rgb(162, 204, 72)); 
		paint.setStrokeWidth(bottomHeight / 16);
		
		if(minNormalValue != 0)
			canvas.drawLine(0, height - bottomHeight - minNormalValue * perValue, width, height - bottomHeight - minNormalValue * perValue, paint);
		if(maxNormalValue != 0)
			canvas.drawLine(0, height - bottomHeight - maxNormalValue * perValue, width, height - bottomHeight - maxNormalValue * perValue, paint);
		
		paint.setTextSize(back.getHeight() / 4);
		paint.setColor(getResources().getColor(R.color.gray)); 
		
		if(dataIndex != null){
			for(int i = 0; i < dataIndex.size(); i++){
				if(dataIndex.size() > 1 && i > 0){
					canvas.drawLine(dataIndex.get(i) * width / 5 - width / 10 + moveX, height -  bottomHeight - data.get(i) * perValue, dataIndex.get(i - 1) * width / 5 - width / 10 + moveX, height -  bottomHeight - data.get(i - 1) * perValue, paint);
				}
			}
			
			for(int i = 0; i < dataIndex.size(); i++){			
				canvas.drawBitmap(back, dataIndex.get(i) * width / 5 - width / 10 - back.getWidth() / 2 + moveX, height -  bottomHeight - data.get(i) * perValue - back.getHeight() * 0.925f, paint);
				canvas.drawText(data.get(i) + "", dataIndex.get(i) * width / 5 - width / 10 - (data.get(i) + "").length() * back.getHeight() / 16 + moveX, height -  bottomHeight - data.get(i) * perValue - back.getHeight() * 5 / 9, paint);	
			}
		}

		paint.setTextSize(bottomHeight / 3 * 2);
		for(int i = 0; i < index; i++){
			canvas.drawText(i + 1 + "", (i + 1) * width / 5 - width / 10 - (i + 1 + "").length() * bottomHeight / 6 + moveX, height - bottomHeight / 4, paint);
		}
		
		for(int i = 0; i < index - 1; i++){
			canvas.drawCircle((i + 1) * width / 5 - bottomHeight / 16 + moveX, height - bottomHeight / 2, bottomHeight / 8, paint);
		}		
	}
	
	private float moveX;
	private float eventX1;
	private float eventX2;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                eventX1 = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                eventX2 = event.getX();
                moveX = moveX + (eventX2 - eventX1);          
                eventX1 = eventX2;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
        }
        invalidate();
        return true;
	}
	
	private void reAssignMoveX(){
		if(moveX > 0)
			moveX = 0;
		else{
			if(moveX < - width / 5 * (index - 5))
				moveX = - width / 5 * (index - 5);
		}	
	}
	
	
}
