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
	
	/**
	 * 获取缓存文件路径
	 * @param context
	 * @param uniqueName 文件名
	 * @return
	 */
	public static File getDiskCacheFile(Context context,String dir, String uniqueName){
		String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
		if(dir != null){
			File dir2 =  new File(cachePath + File.separator + dir);
			if(!dir2.exists()){
				dir2.mkdir();
			}
			try {
				String path = dir2.getCanonicalPath()+ File.separator+uniqueName;
				return new File(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String path = cachePath + File.separator + uniqueName;
		return new File(path);
	}
	
	public static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
}

