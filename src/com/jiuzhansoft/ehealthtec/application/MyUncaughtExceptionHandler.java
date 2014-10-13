package com.jiuzhansoft.ehealthtec.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.print.attribute.standard.Finishings;

import com.jiuzhansoft.ehealthtec.activity.ErrorActivity;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.StatisticsReportUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Process;
import android.text.format.DateFormat;

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

	public static void writeLogToSd(String msg){
        long time = System.currentTimeMillis();
        DateFormat df = new DateFormat();
        CharSequence timeStr = df.format("yyyy MM dd hh:mm:ss", time);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = null;
            try {
                path = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator + "eht" + File.separator + "log";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            File file = new File(path);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(file, true));
                out.write(timeStr +" " +  msg + "\n");
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	private boolean myUncaughtException(Thread thread, Throwable throwable)
	{
		if (Log.D) { 
			Log.d("MyUncaughtExceptionHandler", "myUncaughtException");
		}
		Intent intent = new Intent(context, ErrorActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		if (Log.D) { 
//			Log.d("MyUncaughtExceptionHandler", "throwable error :"+throwable);
//			throwable.printStackTrace();
//		}
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
		writeLogToSd(throwable.getStackTrace().toString());
		
		//context.startActivity(intent);
		return false;
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
			Process.killProcess(Process.myPid());
			System.exit(0);
		}		
	}

}
