package com.hengxuan.ehealthplatform.http.utils;

import android.content.Context;
import android.view.Display;

public class DPIUtils {

	private static Display defaultDisplay;
	private static float mDensity;

	public static int dip2px(float paramFloat) {
		return (int) (mDensity * paramFloat + 0.5F);
	}

	public static int getHeight() {
		return defaultDisplay.getHeight();
	}

	public static int getWidth() {
		return defaultDisplay.getWidth();
	}

	public static int percentHeight(float paramFloat) {
		return (int) (defaultDisplay.getHeight() * paramFloat);
	}

	public static int percentWidth(float paramFloat) {
		return (int) (defaultDisplay.getWidth() * paramFloat);
	}

	public static int px2dip(Context paramContext, float paramFloat) {
		float f = mDensity;
		return (int) (paramFloat / f + 0.5F);
	}

	public static void setDefaultDisplay(Display display) {
		defaultDisplay = display;
	}

	public static void setDensity(float density) {
		mDensity = density;
	}

	public static float getDensity(){
		return mDensity;
	}
}
