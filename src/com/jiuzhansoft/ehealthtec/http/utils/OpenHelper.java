package com.jiuzhansoft.ehealthtec.http.utils;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

	  public OpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt)
	  {
	    super(paramContext, paramString, paramCursorFactory, paramInt);
	  }

	  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
	  {
//	    PlaylistDBHelper.create(paramSQLiteDatabase);
	  }

	  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
	  {
	    if (paramInt1 >= paramInt2)
	      return;
	    
//	    PlaylistDBHelper.upgrade(paramSQLiteDatabase);
	    onCreate(paramSQLiteDatabase);
	  }

}
