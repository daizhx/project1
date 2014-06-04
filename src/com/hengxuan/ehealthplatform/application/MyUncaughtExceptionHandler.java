package com.hengxuan.ehealthplatform.application;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import com.hengxuan.ehealthplatform.activity.ErrorActivity;
import com.hengxuan.ehealthplatform.log.Log;
import com.hengxuan.ehealthplatform.utils.StatisticsReportUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {


	private static StringBuffer errorDataBuffer = new StringBuffer();
	private Context context;
	private Thread.UncaughtExceptionHandler mOldUncaughtExceptionHandler;

	public MyUncaughtExceptionHandler(Context context1)
	{
		context = context1;
		mOldUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public static void appendErrorInfo(String s)
	{
		if (Log.D) { 
			Log.d("MyUncaughtExceptionHandler", "appendErrorInfo");
		}
		
		errorDataBuffer.append(s);
	}

	private boolean myUncaughtException(Thread thread, Throwable throwable)
	{
		if (Log.D) { 
			Log.d("MyUncaughtExceptionHandler", "myUncaughtException");
		}
		
		Intent intent = new Intent(context, ErrorActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Log.D) { 
			Log.d("MyUncaughtExceptionHandler", "throwable error :"+throwable);
			throwable.printStackTrace();
		}
		StringWriter stringwriter = new StringWriter();
		PrintWriter printwriter = new PrintWriter(stringwriter);
		if (Log.D) {
			Log.d("MyUncaughtExceptionHandler", "throwable printwriter");
			throwable.printStackTrace(printwriter);
		}
		
		String s = String.valueOf(errorDataBuffer.toString());
		StringBuilder stringbuilder = (new StringBuilder(s)).append("||");
		intent.putExtra("user", StatisticsReportUtil.getReportString(true));
		intent.putExtra("error", stringbuilder.append(stringwriter.toString()).toString());
		if (Log.D) {
			Log.d("MyUncaughtExceptionHandler", "start activity");
		}
		
		context.startActivity(intent);
		return true;
	}

	public static void resetErrorInfo(String s)
	{
		if (Log.D) { 
			Log.d("MyUncaughtExceptionHandler", "resetErrorInfo");
		}
		
		errorDataBuffer.setLength(0);
		errorDataBuffer.append(s);
	}




	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		// TODO Auto-generated method stub
		if (Log.D) { 
			Log.d("MyUncaughtExceptionHandler", "uncaughtException");
		}
		
		if (!myUncaughtException(thread, throwable) && mOldUncaughtExceptionHandler != null)
		{
			mOldUncaughtExceptionHandler.uncaughtException(thread, throwable);
		} else
		{
			Process.killProcess(Process.myTid());
			System.exit(0);
		}		
	}

}
