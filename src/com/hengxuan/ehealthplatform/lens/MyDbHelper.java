package com.hengxuan.ehealthplatform.lens;

import com.hengxuan.ehealthplatform.lens.MyDataBaseContract;
import com.hengxuan.ehealthplatform.lens.MyDataBaseContract.ImagesInfo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDbHelper extends SQLiteOpenHelper {
	
	public MyDbHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context,MyDataBaseContract.DATABASE_NAME,null,MyDataBaseContract.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(MyDataBaseContract.SQL_CREATE_IMAGES_INFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
	

}
