package com.jiuzhansoft.ehealthtec.http.utils;

import com.jiuzhansoft.ehealthtec.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ExceptionDrawable extends Drawable {

	  private final Bitmap bitmap;
	  private final int height;
	  private Paint paint;
	  private final String text;
	  private final int width;

	  public ExceptionDrawable(Context context, String s)
		{
			Paint paint1 = new Paint();
			paint = paint1;
			paint.setColor(Color.GRAY);
			Paint paint2 = paint;
			android.graphics.Paint.Style style = android.graphics.Paint.Style.FILL;
			paint2.setStyle(style);
			Paint paint3 = paint;
			float f = DPIUtils.dip2px(12F);
			paint3.setTextSize(f);
			Paint paint4 = paint;
			android.graphics.Paint.Align align = android.graphics.Paint.Align.CENTER;
			paint4.setTextAlign(align);
			paint.setAntiAlias(true);
			text = s;
			Bitmap bitmap1 = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.icon)).getBitmap();
			bitmap = bitmap1;
			int i = bitmap.getWidth();
			width = i;
			int j = bitmap.getHeight();
			height = j;
		}

	  public void draw(Canvas paramCanvas)
	  {
	    Rect localRect = getBounds();
	    int i = localRect.right;
	    int j = localRect.width() / 2;
	    float f1 = i - j;
	    int k = localRect.bottom;
	    int l = localRect.height() / 2;
	    float f2 = k - l;
	    String str = this.text;
	    Paint localPaint1 = this.paint;
	    paramCanvas.drawText(str, f1, f2, localPaint1);
	    Bitmap localBitmap = this.bitmap;
	    float f3 = this.width / 2;
	    float f4 = f1 - f3;
	    float f5 = this.height / 2;
	    float f6 = f2 - f5;
	    float f7 = DPIUtils.dip2px(10.0F);
	    float f8 = f6 + f7;
	    Paint localPaint2 = this.paint;
	    paramCanvas.drawBitmap(localBitmap, f4, f8, localPaint2);
	  }

	  public int getOpacity()
	  {
	    return 0;
	  }

	  public void setAlpha(int paramInt)
	  {
	  }

	  public void setColorFilter(ColorFilter paramColorFilter)
	  {
	  }

}
