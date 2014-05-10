package com.hengxuan.ehealthplatform.http.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.hengxuan.ehealthplatform.log.Log;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CacheFileTableDBHelper {

	public static void create(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase
				.execSQL("CREATE TABLE cache_file('id' INTEGER PRIMARY KEY  NOT NULL ,first_name TEXT,last_name TEXT,clean_time DATETIME DEFAULT CURRENT_TIMESTAMP,dir_path TEXT,dir_space INTEGER)");
		paramSQLiteDatabase
				.execSQL("CREATE INDEX clean_time_index ON cache_file(clean_time)");
		paramSQLiteDatabase
				.execSQL("CREATE INDEX name_index ON cache_file(first_name, last_name)");
	}

	public synchronized static void delete(CacheFileItem paramCacheFile) {
		if (Log.D)
			Log.d("Temp", "CacheFileTable delete() -->> ");

		try {
			SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
			String str[] = new String[2];
			str[0] = paramCacheFile.getFirstName();
			str[1] = paramCacheFile.getLastName();

			sqlitedatabase.delete("cache_file",
					"first_name = ? AND last_name = ?", str);
			if (Log.D)
				Log.d("Temp", "CacheFileTable delete() -->> ok");

			DBHelperUtil.closeDatabase();

		} catch (Exception exception) {
			exception.printStackTrace();
			DBHelperUtil.closeDatabase();
		}
	}

	public static ArrayList<CacheFileItem> getListByClean() {
		if (Log.D)
			Log.d("Temp", "CacheFileTable getListByClean() -->> ");

		ArrayList<CacheFileItem> localArrayList = new ArrayList<CacheFileItem>();
		Cursor localCursor = null;
		SQLiteDatabase localSQLiteDatabase = DBHelperUtil.getDatabase();

		try {
			String[] arrayOfString1 = new String[5];
			arrayOfString1[0] = "first_name";
			arrayOfString1[1] = "last_name";
			arrayOfString1[2] = "clean_time";
			arrayOfString1[3] = "dir_path";
			arrayOfString1[4] = "dir_space";
			String[] arrayOfString2 = new String[1];
			arrayOfString2[0] = new Date().toLocaleString();
			localCursor = localSQLiteDatabase.query("cache_file",
					arrayOfString1, "clean_time < ?", arrayOfString2, null,
					null, null);
			if ((localCursor != null) && (localCursor.moveToFirst())) {
				do {
					CacheFileItem localCacheFile = new CacheFileItem();
					localCacheFile
							.setFirstName(localCursor.getString(localCursor
									.getColumnIndex("first_name")));
					localCacheFile
							.setLastName(localCursor.getString(localCursor
									.getColumnIndex("last_name")));
					localCacheFile.setCleanTime(FormatUtils
							.parseDate(localCursor.getString(localCursor
									.getColumnIndex("clean_time"))));
					String str4 = localCursor.getString(localCursor
							.getColumnIndex("dir_path"));
					FileService.Directory localDirectory = new FileService.Directory(
							str4, localCursor.getInt(localCursor
									.getColumnIndex("dir_space")));
					localCacheFile.setDirectory(localDirectory);
					localArrayList.add(localCacheFile);
				} while (localCursor.moveToNext());
			}

			if (Log.D)
				Log.d("Temp", "CacheFileTable getListByClean() -->> ok");

			if (localCursor != null && !localCursor.isClosed())
				localCursor.close();
			DBHelperUtil.closeDatabase();

		} catch (Exception exception1) {
			exception1.printStackTrace();
			if (localCursor != null) {
				if (!localCursor.isClosed())
					localCursor.close();
			}
			DBHelperUtil.closeDatabase();

		}

		return localArrayList;
	}

	public synchronized static void insertOrUpdate(CacheFileItem paramCacheFile) {
		if (Log.D)
			Log.d("Temp", "CacheFileTable insertOrUpdate() -->> ");

		SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("first_name", paramCacheFile.getFirstName());
		contentvalues.put("last_name", paramCacheFile.getLastName());

		String s2 = paramCacheFile.getCleanTime().toLocaleString();
		contentvalues.put("clean_time", s2);

		FileService.Directory directory = paramCacheFile.getDirectory();
		contentvalues.put("dir_path", directory.getPath());
		Integer integer = Integer.valueOf(directory.getSpace());
		contentvalues.put("dir_space", integer);
		String s4 = "first_name = ? AND last_name = ?";

		String as[] = new String[2];
		as[0] = paramCacheFile.getFirstName();
		as[1] = paramCacheFile.getLastName();
		Cursor cursor = sqlitedatabase.query("cache_file", null, s4, as, null,
				null, null);
		try {
			if (cursor == null || cursor.getCount() <= 0)
				sqlitedatabase.insert("cache_file", null, contentvalues);
			else
				sqlitedatabase.update("cache_file", contentvalues, s4, as);

			if (Log.D)
				Log.d("Temp", "CacheFileTable insertOrUpdate() -->> ok");

			if (cursor != null && !cursor.isClosed())
				cursor.close();

			DBHelperUtil.closeDatabase();

		} catch (Exception exception) {
			exception.printStackTrace();
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			DBHelperUtil.closeDatabase();
		}
	}

	public static boolean isExpired(File paramFile) {
		if (Log.D)
			Log.d("Temp", "CacheFileTable isExpired() -->> ");

		boolean flag = true;
		Cursor cursor = null;
		CacheFileItem cachefile = new CacheFileItem(paramFile);
		SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
		String as[] = new String[2];
		as[0] = cachefile.getFirstName();
		as[1] = cachefile.getLastName();

		try {
			cursor = sqlitedatabase.query("cache_file", null,
					"first_name = ? AND last_name = ?", as, null, null, null);
			if (cursor == null || cursor.getCount() <= 0
					|| !cursor.moveToFirst())
				flag = false;
			else {
				int i = cursor.getColumnIndex("clean_time");
				long l = FormatUtils.parseDate(cursor.getString(i)).getTime();
				long l1 = (new Date()).getTime();

				if (l <= l1)
					flag = false;

				if (Log.D) {
					String s2 = (new StringBuilder(
							"CacheFileTable isExpired() -->> ")).append(l)
							.toString();
					Log.d("Temp", s2);
				}
			}

			if (cursor != null && !cursor.isClosed())
				cursor.close();
			DBHelperUtil.closeDatabase();
		} catch (Exception exception) {
			exception.printStackTrace();
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			DBHelperUtil.closeDatabase();
		}

		return flag;
	}

	public static void upgrade(SQLiteDatabase paramSQLiteDatabase,
			int paramInt1, int paramInt2) {
		paramSQLiteDatabase.execSQL("drop index if exists clean_time_index");
		paramSQLiteDatabase.execSQL("drop index if exists name_index");
		paramSQLiteDatabase.execSQL("drop table if exists cache_file");
	}

}
