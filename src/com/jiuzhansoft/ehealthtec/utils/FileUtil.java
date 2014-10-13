package com.jiuzhansoft.ehealthtec.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class FileUtil {
	private static final String TAG = "FileUtil";

	public static File getCacheFile(String imageUri){
		File cacheFile = null;
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File sdCardDir = Environment.getExternalStorageDirectory();
				String fileName = getFileName(imageUri);
				File dir = new File(sdCardDir.getCanonicalPath()
						+ File.separator + AsynImageLoader.CACHE_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				cacheFile = new File(dir, fileName);
				Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);
			}  
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "getCacheFileError:" + e.getMessage());
		}
		
		return cacheFile;
	}
	
	
	public static File getDiskCacheDir(Context context, String uniqueName){
		String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory()) ||
				!Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}
	
	public static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
}

