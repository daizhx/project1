package com.jiuzhansoft.ehealthtec.http.utils;

import com.jiuzhansoft.ehealthtec.application.EHTApplication;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBHelperUtil {

	  private static final String DB_NAME = "ehealthtec.db";
	  private static final String TAG = "DBHelperUtil";
	  private static OpenHelper mOpenHelper;
	  private static int versionCode = 1;
	  private Cursor c;
	  private static SQLiteDatabase db;
	  private Context mContext;

	  public DBHelperUtil(Context context)
	  {
	  		db = null;
	    	mContext = context;
	  }

	  public static void closeDatabase()
	  {
		  if(db != null)
		  {
			  db.close();
		  }
	  }

	  public synchronized SQLiteDatabase getDatabase()
	  {
	  	Object obj = null;

		try 
		{
			  if (mOpenHelper == null)
			  {
				  PackageManager packagemanager = mContext.getPackageManager();
				  String s = mContext.getPackageName();
				  try
				  {
				   	versionCode = packagemanager.getPackageInfo(s, 0).versionCode;
				   	Application myapplication = (Application) mContext.getApplicationContext();
					mOpenHelper = new OpenHelper(myapplication, DB_NAME, null, versionCode);
					SQLiteDatabase sqlitedatabase = mOpenHelper.getReadableDatabase();
					obj = (SQLiteDatabase)sqlitedatabase;
				  }
				  catch(Exception exception)
				  {
					exception.printStackTrace();
					
				  }
				   
			  }
			  else
		  	  {
		  		SQLiteDatabase sqlitedatabase = mOpenHelper.getWritableDatabase();
				obj = (SQLiteDatabase)sqlitedatabase;
		  	  }
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
		db = ((SQLiteDatabase) (obj));
		return db;
	  }
	 

}
