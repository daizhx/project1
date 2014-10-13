package com.jiuzhansoft.ehealthtec.lens;

import android.provider.BaseColumns;

public final class MyDataBaseContract {
	//if you change your database schema,you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "eht.db";
	
	public MyDataBaseContract() {
		// TODO Auto-generated constructor stub
	}
	
	public static abstract class ImagesInfo implements BaseColumns{
		public static final String TABLE_NAME = "imagesinfo";
		//owner
		public static final String COLUMN_NAME_OWNER = "name";
		//description
		public static final String COLUMN_NAME_TAG = "tag";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_DATA = "path";
	}
	
	public static final String SQL_CREATE_IMAGES_INFO =
			"CREATE TABLE " + ImagesInfo.TABLE_NAME + " (" + ImagesInfo._ID + " INTEGER PRIMARY KEY,"
			+ ImagesInfo.COLUMN_NAME_OWNER + " TEXT," +ImagesInfo.COLUMN_NAME_TAG + " TEXT,"+ ImagesInfo.COLUMN_NAME_DATE + " DATETIME," + ImagesInfo.COLUMN_NAME_DATA + " TEXT,"
			+ ImagesInfo.COLUMN_NAME_TYPE + " INTEGER)";
	
	public static final String SQL_DELETE_IMAGES_INFO = "DROP TABLE IF EXISTS " + ImagesInfo.TABLE_NAME;
	
}
