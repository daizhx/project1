package com.jiuzhansoft.ehealthtec.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.print.attribute.standard.Finishings;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.StatisticsReportUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Process;
import android.text.format.DateFormat;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private Context context;
	private Thread.UncaughtExceptionHandler mOldUncaughtExceptionHandler;

	public MyUncaughtExceptionHandler(Context context1)
	{
		context = context1;
		mOldUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	
	/**
	 * always return false,交给系统处理，不能会遇到麻烦
	 * @param thread
	 * @param throwable
	 * @return
	 */
	private boolean myUncaughtException(Thread thread, Throwable ex)
	{
		if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        StackTraceElement[] arr = ex.getStackTrace();
        String report = ex.toString() + "\n\n";
        report += "--------------Stack trace----------\n\n";
        for(int i=0;i<arr.length;i++){
            report += "		" + arr[i].toString() + "\n";
        }
        report += "---------------------------------------\n\n";
        //if the exception was thrown in a background thread inside
        //AsyncTask,then the actual exception can be found with getCause
        report += "-------------cause-------------\n\n";
        Throwable cause = ex.getCause();
        if(cause != null){
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for(int i=0; i<arr.length ; i++){
                report += "     " + arr[i].toString() + "\n";
            }
        }
        report += "-------------cause end-------------\n\n";

//        try {
//            FileOutputStream fos = context.openFileOutput("stact.trace", Context.MODE_PRIVATE);
//            fos.write(report.getBytes());
//            fos.close();
//
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        //错误日志写到SD卡中
        Log.writeLogToSd(context, report);
        //TODO 上传到后台
		return false;
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
			//关闭程序后，会被系统重启，remove
//			Process.killProcess(Process.myPid());
//			System.exit(0);
		}		
	}

}
