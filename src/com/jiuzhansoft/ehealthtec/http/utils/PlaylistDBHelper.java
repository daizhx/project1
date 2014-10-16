//package com.jiuzhansoft.ehealthtec.http.utils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.jiuzhansoft.ehealthtec.http.others.MusicFileInfo;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//public class PlaylistDBHelper {
//
//
//	public static void create(SQLiteDatabase paramSQLiteDatabase) {
//		paramSQLiteDatabase
//				.execSQL("CREATE TABLE playlist_info('id' INTEGER PRIMARY KEY  NOT NULL ,filepath TEXT UNIQUE, musicname TEXT,artist TEXT,play TEXT)");
//	}
//
//	public synchronized static void delete(MusicFileInfo paramFileInfo) {
//		try {
//			SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
//			String str[] = new String[1];
//			str[0] = paramFileInfo.getFilepath();
//			sqlitedatabase.delete("playlist_info", "filepath = ?", str);
//			DBHelperUtil.closeDatabase();
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		} finally {
//			DBHelperUtil.closeDatabase();
//		}
//		
//	}
//
//	public synchronized static void deleteall() {
//		try {
//			DBHelperUtil.getDatabase().delete("playlist_info", "1=1", null);
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		} finally {
//			DBHelperUtil.closeDatabase();
//		}
//	}
//
//	public static ArrayList<MusicFileInfo> getMusicFileList() {
//		ArrayList<MusicFileInfo> localArrayList = new ArrayList<MusicFileInfo>();
//		Cursor localCursor = null;
//		SQLiteDatabase localSQLiteDatabase = DBHelperUtil.getDatabase();
//		try {
//			String[] arrayOfString = new String[4];
//			arrayOfString[0] = "filepath";
//			arrayOfString[1] = "musicname";
//			arrayOfString[2] = "artist";
//			arrayOfString[3] = "play";
//			localCursor = localSQLiteDatabase.query("playlist_info",
//					arrayOfString, null, null, null, null, null);
//			localCursor.moveToFirst();
//			if ((localCursor != null) && (localCursor.getCount() != 0)) {
//				int k = 0;
//				while (k < localCursor.getCount()) {
//					localCursor.moveToPosition(k);
//					MusicFileInfo musicFileInfo = new MusicFileInfo();
//					musicFileInfo.setFilepath(localCursor.getString(localCursor
//							.getColumnIndexOrThrow("filepath")));
//					musicFileInfo.setMusicname(localCursor
//							.getString(localCursor
//									.getColumnIndexOrThrow("musicname")));
//					musicFileInfo.setArtist(localCursor.getString(localCursor
//							.getColumnIndexOrThrow("artist")));
//					musicFileInfo.setPlay_flag((Boolean.valueOf(localCursor
//							.getString(localCursor
//									.getColumnIndexOrThrow("play")))));
//					localArrayList.add(musicFileInfo);
//					
//					k++;
//				}
//			}
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		}
//		if (localCursor != null) {
//			if (!localCursor.isClosed()) {
//				localCursor.close();
//			}
//		}
//		DBHelperUtil.closeDatabase();
//		
//		return localArrayList;
//	}
//
//	public static ArrayList<MusicFileInfo> getMusicPlayFileList() {
//		ArrayList<MusicFileInfo> localArrayList = new ArrayList<MusicFileInfo>();
//		Cursor localCursor = null;
//		SQLiteDatabase localSQLiteDatabase = DBHelperUtil.getDatabase();
//		try {
//			String[] arrayOfString = new String[4];
//			arrayOfString[0] = "filepath";
//			arrayOfString[1] = "musicname";
//			arrayOfString[2] = "artist";
//			arrayOfString[3] = "play";
//			String[] arrayOfString2 = new String[1];
//			arrayOfString2[0] = String.valueOf(true);
//			localCursor = localSQLiteDatabase
//					.query("playlist_info", arrayOfString, "play = ?",
//							arrayOfString2, null, null, null);
//			localCursor.moveToFirst();
//			if ((localCursor != null) && (localCursor.getCount() != 0)) {
//				int k = 0;
//				while (k < localCursor.getCount()) {
//					localCursor.moveToPosition(k);
//					MusicFileInfo musicFileInfo = new MusicFileInfo();
//					musicFileInfo.setFilepath(localCursor.getString(localCursor
//							.getColumnIndexOrThrow("filepath")));
//					musicFileInfo.setMusicname(localCursor
//							.getString(localCursor
//									.getColumnIndexOrThrow("musicname")));
//					musicFileInfo.setArtist(localCursor.getString(localCursor
//							.getColumnIndexOrThrow("artist")));
//					musicFileInfo.setPlay_flag((Boolean.valueOf(localCursor
//							.getString(localCursor
//									.getColumnIndexOrThrow("play")))));
//					localArrayList.add(musicFileInfo);
//					k++;
//				}
//			}
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		}
//		if (localCursor != null) {
//			if (!localCursor.isClosed()) {
//				localCursor.close();
//			}
//		}
//		DBHelperUtil.closeDatabase();
//		
//		return localArrayList;
//	}
//
//	public synchronized static void insert(MusicFileInfo paramFileInfo) {
//		try {
//			SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
//			ContentValues contentvalues = new ContentValues();
//			contentvalues.put("filepath", paramFileInfo.getFilepath());
//			contentvalues.put("musicname", paramFileInfo.getMusicname());
//			contentvalues.put("artist", paramFileInfo.getArtist());
//			contentvalues.put("play", String.valueOf(paramFileInfo.getPlay_flag()));
//			sqlitedatabase.insert("playlist_info", null, contentvalues);
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		}
//		
//		DBHelperUtil.closeDatabase();
//	}
//
//	public synchronized static void insertList(List<MusicFileInfo> arrayList) {
//		if (arrayList != null && arrayList.size() > 0) {
//			SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
//			int i = 0;
//			while (i < arrayList.size()) {
//				try {
//					ContentValues contentvalues = new ContentValues();
//					contentvalues.put("filepath", arrayList.get(i)
//							.getFilepath());
//					contentvalues.put("musicname", arrayList.get(i)
//							.getMusicname());
//					contentvalues.put("artist", arrayList.get(i).getArtist());
//					contentvalues.put("play", String.valueOf(arrayList.get(i).getPlay_flag()));
//					sqlitedatabase.insert("playlist_info", null, contentvalues);
//
//				} catch (Exception exception) {
//					exception.printStackTrace();
//				}
//				i++;
//			}
//			DBHelperUtil.closeDatabase();
//		}
//
//	}
//
//	public synchronized static void updateList(List<MusicFileInfo> arrayList) {
//		if (arrayList != null && arrayList.size() > 0) {
//			SQLiteDatabase sqlitedatabase = DBHelperUtil.getDatabase();
//			ContentValues localContentValues;
//			int i = 0;
//			while (i < arrayList.size()) {
//				try {
//					localContentValues = new ContentValues();
//					String ss = String.valueOf(arrayList.get(i).getPlay_flag());
//					localContentValues.put("play", ss);
//					String[] arrayOfString = new String[1];
//					arrayOfString[0] = arrayList.get(i).getFilepath();
//					Cursor localCursor = sqlitedatabase
//							.query("playlist_info", null, "filepath=?",
//									arrayOfString, null, null, null);
//					if (localCursor != null)
//						sqlitedatabase
//								.update("playlist_info", localContentValues,
//										"filepath=?", arrayOfString);
//
//				} catch (Exception exception) {
//					exception.printStackTrace();
//				}
//				i++;
//			}
//			DBHelperUtil.closeDatabase();
//		}
//
//	}
//
//	public static void upgrade(SQLiteDatabase paramSQLiteDatabase) {
//		paramSQLiteDatabase.execSQL("drop index if exists filepath");
//		paramSQLiteDatabase.execSQL("drop table if exists playlist_info");
//	}
//
//}
