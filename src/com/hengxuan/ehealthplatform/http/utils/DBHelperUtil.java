package com.hengxuan.ehealthplatform.http.utils;

import com.hengxuan.ehealthplatform.application.EHTApplication;

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

	  public synchronized static SQLiteDatabase getDatabase()
	  {
	  	Object obj = null;

		try 
		{
			  if (mOpenHelper == null)
			  {
				  PackageManager packagemanager = EHTApplication.getInstance().getPackageManager();
				  String s = EHTApplication.getInstance().getPackageName();
				  try
				  {
				   	versionCode = packagemanager.getPackageInfo(s, 0).versionCode;
				   	EHTApplication myapplication = EHTApplication.getInstance();
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
