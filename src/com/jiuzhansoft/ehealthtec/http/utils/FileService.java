package com.jiuzhansoft.ehealthtec.http.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFileProp;
import com.jiuzhansoft.ehealthtec.http.constant.ConstSysConfig;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;


public class FileService {


	private static Directory imageDir;
	private static Directory jsonDir;
	private static int jsonDirState;

	public FileService() {
	}
	/**
	 * 清除缓冲
	 */
	public static void clearCacheFiles() {
		ArrayList<CacheFileItem> fileList = CacheFileTableDBHelper
				.getListByClean();
		int i = 0;
		do {
			int j = fileList.size();
			if (i >= j)
				return;

			CacheFileItem item = (CacheFileItem) fileList.get(i);
			Directory dir = item.getDirectory();
			if ((dir.getSpace() == ConstFileProp.STORAGE_INTERNAL)
					|| ((dir.getSpace() == ConstFileProp.STORAGE_EXTERNAL) && (externalMemoryAvailable()))) {
				boolean delFlag = item.getFile().delete();
				if (Log.D) {
					Log.d("Temp", item.getName());
				}

				if (delFlag)
					CacheFileTableDBHelper.delete(item);
			}
			i++;
		} while (true);
	}

	public static boolean externalMemoryAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 格式户尺寸的现实格式
	 * @param size
	 * @return
	 */
	public static String formatSize(long size) {
		String unitName = null;
		if (size >= 1024L) {
			unitName = "KiB";
			size /= 1024L;
			if (size >= 1024L) {
				unitName = "MiB";
				size /= 1024L;
			}
		}
		String sizeName = Long.toString(size);
		StringBuilder sb = new StringBuilder(sizeName);
		int i = sb.length() - 3;
		do {
			if (i <= 0) {
				if (unitName != null)
					sb.append(unitName);
				return sb.toString();
			}
			sb.insert(i, ',');

			i -= 3;
		} while (true);
	}
	/**
	 * 格式户尺寸的现实格式
	 * @param size
	 * @return
	 */
	public static String formatSize2(long size) {
		String unitName = null;
		float f = Long.valueOf(size).floatValue();
		if (f >= 1024.0F) {
			unitName = "KB";
			f /= 1024.0F;
			if (f >= 1024.0F) {
				unitName = "MB";
				f /= 1024.0F;
			}
		}
		DecimalFormat df = new DecimalFormat(".00");
		StringBuilder sb = new StringBuilder(df.format(f));
		int i = sb.indexOf(".") - 3;
		do {
			if (i <= 0) {
				if (unitName != null)
					sb.append(unitName);
				return sb.toString();
			}
			sb.insert(i, ',');
			i += -3;
		} while (true);
	}

	public static long getAvailableExternalMemorySize() {
		long l2 = 0;
		if (externalMemoryAvailable()) {
			String str = Environment.getExternalStorageDirectory().getPath();
			StatFs localStatFs = new StatFs(str);
			long l1 = localStatFs.getBlockSize();
			l2 = localStatFs.getAvailableBlocks() * l1;
		} else {
			l2 = 65535L;
		}

		return l2;
	}
	/**
	 * 获取手机内存中可用的存储空间大小
	 * @return
	 */
	public static long getAvailableInternalMemorySize() {
		String dataPath = Environment.getDataDirectory().getPath();
		StatFs statFs = new StatFs(dataPath);
		long blockSize = statFs.getBlockSize();
		return (long) statFs.getAvailableBlocks() * blockSize;
	}

	public static Directory getDirectory(int type) {
		Directory dir = null;
		switch (type) {
		case ConstFileProp.INTERNAL_TYPE_CACHE:
			dir = getImageDirectory();
			break;
		case ConstFileProp.INTERNAL_TYPE_FILE:
			dir = getJsonDirectory();
			break;
		}
		return dir;
	}

	private static Directory getDirectoryByBigSize(String path) {
		if (Log.D)
			Log.d("Temp", "getDirectoryByBigSize() -->> ");

		File file;
		Directory dir = null;
		//首先判断手机内存
		if (getTotalInternalMemorySize() > ConstFileProp.BIG_SIZE_THRESHOLD) {
			if (Log.D)
				Log.d("Temp", "getDirectoryByBigSize() -->> INTERNAL");

			file = getInternalDirectory(path);
			dir = new Directory(file);
		//判断sdcard
		} else if (getTotalExternalMemorySize() > ConstFileProp.BIG_SIZE_THRESHOLD) {
			if (Log.D)
				Log.d("Temp", "getDirectoryByBigSize() -->> EXTERNAL");

			file = getExternalDirectory(path);
			dir = new Directory(file);
		}
		return dir;
	}

	public static File getExternalDirectory(String path) {
		if (Log.D)
			Log.d("Temp", "getExternalDirectory() -->> ");
		StringBuilder sb = new StringBuilder("/guanyi");

		String dirName = sb.append((path != null)?path:"").toString();
		File file = new File(Environment.getExternalStorageDirectory(), dirName);
		if (!file.exists()) //如果目录不存在，则创建目录
			file.mkdirs();
		return file;
	}
	/**
	 * 获取存储图片的缓冲目录
	 * @return
	 */
	private static Directory getImageDirectory() {
		if (!externalMemoryAvailable())//sdcard必须存在，否则没法缓冲图片
			return null;

		File imageDir = getExternalDirectory("/image");

		Directory dir = new Directory(imageDir,
				ConstFileProp.STORAGE_EXTERNAL);

		return dir;
	}

	public static File getInternalDirectory(String path) {
		return getInternalDirectory(path,
				ConstFileProp.INTERNAL_TYPE_CACHE);
	}

	public static File getInternalDirectory(String path, int type) {
		if (Log.D)
			Log.d("Temp", "getInternalDirectory() -->> ");

		File parentFile = null;

		switch (type) {
		case ConstFileProp.INTERNAL_TYPE_FILE:
			parentFile = EHTApplication.getInstance().getFilesDir();
			break;
		case ConstFileProp.INTERNAL_TYPE_CACHE:
			parentFile = EHTApplication.getInstance().getCacheDir();
			break;
		}

		File file = new File(parentFile, (path != null)?path:"");
		if (!file.exists())
			file.mkdirs();
		
		if(Log.D) {
			StringBuilder sb = new StringBuilder("getInternalDirectory() dir.getAbsolutePath() -->> ");
			sb.append(file.getAbsolutePath());
			Log.d("Temp", sb.toString());
		}
		return file;
	}
	/**
	 * 获取保存json数据缓冲的目录
	 * @return
	 */
	private static Directory getJsonDirectory() {
		if (Log.D) {
			StringBuilder stringbuilder = new StringBuilder(
					"getJsonDirectory() jsonDirState -->> ");
			String s1 = stringbuilder.append(jsonDirState).toString();
			Log.d("Temp", s1);
		}
		if (Log.D) {
			StringBuilder stringbuilder1 = new StringBuilder(
					"getJsonDirectory() jsonDir -->> ");
			String s2 = stringbuilder1.append(jsonDir).toString();
			Log.d("Temp", s2);
		}
		Directory retDir;
		if (jsonDirState == ConstFileProp.STORAGE_UNKNOWN)
			retDir = null;
		else if (jsonDir != null) {
			retDir = jsonDir;
		} else {
			SharedPreferences sharedPreferences = EHTApplication
					.getInstance().getSharedPreferences(
							ConstSysConfig.SYS_CUST_CLIENT,
							Activity.MODE_PRIVATE);
			// 获取json file cache path
			String jsonPath = sharedPreferences.getString("jsonFileCachePath",
					null);
			// 获取json file cache 类型
			jsonDirState = sharedPreferences
					.getInt("jsonFileCachePathState", 0);
			if (jsonPath == null) {
				if (Log.D)
					Log.d("Temp", "getJsonDirectory() no preferences -->> ");

				Directory dir = getDirectoryByBigSize("/json");
				if (dir == null) {
					if (Log.D)
						Log.d("Temp", "getJsonDirectory() no big size -->> ");
					jsonDirState = ConstFileProp.STORAGE_UNKNOWN;
					retDir = null;
				} else {
					if (Log.D)
						Log.d("Temp", "getJsonDirectory() has big size -->> ");

					jsonDir = dir;
					jsonDirState = dir.getSpace();
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("jsonFileCachePath", jsonDir.getDir()
							.getAbsolutePath());
					editor.putInt("jsonFileCachePathState", jsonDirState);
					editor.commit();
					retDir = jsonDir;
				}
			} else {
				if (Log.D)
					Log.d("Temp", "getJsonDirectory() is preferences -->> ");

				if (jsonDirState == ConstFileProp.STORAGE_EXTERNAL
						&& !externalMemoryAvailable()) {
					if (Log.D)
						Log.d("Temp", "getJsonDirectory() no external -->> ");
					jsonDirState = ConstFileProp.STORAGE_UNKNOWN;
					retDir = null;
				} else {
					if (Log.D) {
						String s5 = (new StringBuilder(
								"getJsonDirectory() jsonFileCachePath -->> "))
								.append(jsonPath).toString();
						Log.d("Temp", s5);
					}

					int i;
					if (jsonDirState == ConstFileProp.STORAGE_EXTERNAL)
						i = ConstFileProp.STORAGE_EXTERNAL;
					else
						i = ConstFileProp.STORAGE_INTERNAL;

					File file = new File(jsonPath);
					jsonDir = new FileService.Directory(file, i);

					File file1 = jsonDir.getDir();
					if (!file1.exists())
						file1.mkdirs();
					retDir = jsonDir;
				}
			}
		}
		return retDir;
	}
	/**
	 * 获取外存储器的总大小
	 * @return
	 */
	public static long getTotalExternalMemorySize() {
		long retSize = 0;
		if (externalMemoryAvailable()) {
			StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			retSize = statFs.getBlockCount() * statFs.getBlockSize();
			if (Log.D) {
				Log.d("Temp", "getTotalExternalMemorySize() -->> " + retSize);
			}
		} else {
			retSize = 65535;
		}

		return retSize;
	}
	/**
	 * 获取内存储器的总大小
	 * @return
	 */

	public static long getTotalInternalMemorySize() {
		StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
		long totalSize = statFs.getBlockCount() * statFs.getBlockSize();
		if (Log.D) {
			String str2 = "getTotalInternalMemorySize() -->> " + totalSize;
			Log.d("Temp", str2);
		}
		return totalSize;
	}

	public static boolean isReady() {
		return externalMemoryAvailable();
	}
	/**
	 * 得到一个输出流，用于保存数据
	 * @param fileguider
	 * @return
	 * @throws FileNotFoundException
	 */
	public static FileOutputStream openFileOutput(FileGuider fileguider)
			throws FileNotFoundException {
		FileOutputStream fileoutputstream = null;
		long l = fileguider.getAvailableSize();

		if (l == 0) { //如果没有可用的空间
			EHTApplication myapplication = EHTApplication.getInstance();
			String s = fileguider.getFileName();
			int k = fileguider.getMode();
			fileoutputstream = myapplication.openFileOutput(s, k);
		} else {
			System.out.println(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			File file = new File(Environment.getExternalStorageDirectory(),
					fileguider.getFileName());
			if (file.exists()) { //如果存在，删除文件
				file.delete();
				try {
					//重新创建一个空的文件
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println(file.getAbsolutePath());
			fileoutputstream = new FileOutputStream(file);
		}

		return fileoutputstream;
	}
	/**
	 * 通过inputstream获取数据，并且保存在byte型的数组中
	 * @param paramFileInputStream
	 * @return
	 * @throws Exception
	 */
	private byte[] readInputStream(FileInputStream inputStream)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		do {
			int i = inputStream.read(buf);
			if (i == -1) {
				inputStream.close();
				baos.close();
				return baos.toByteArray();
			}
			baos.write(buf, 0, i);
		} while (true);
	}

	public static boolean saveToSDCard(Directory dir,
			String filename, String content) {
		return saveToSDCard(dir, filename, content, 0);
	}

	public static boolean saveToSDCard(Directory dir,
			String filename, String content, int offset) {
		if (content == null)
			return false;

		byte[] buf = content.getBytes();
		return saveToSDCard(dir, filename, buf, offset);
	}

	public static boolean saveToSDCard(Directory dir,
			String filename, byte[] buf) {
		if (buf != null) {
			return saveToSDCard(dir, filename, buf, 0);
		}

		return false;
	}
	/**
	 * 保存数据到sd卡中
	 * @param dir
	 * @param filename
	 * @param buf
	 * @param size
	 * @return
	 */
	public static boolean saveToSDCard(Directory dir,
			String filename, byte[] buf, int size) {
		File parent = dir.getDir();
		File file = new File(parent, filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			if (fos != null) {
				fos.write(buf);
				fos.close();
			}
		} catch (IOException exception) {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		return true;
	}
	/**
	 * 读取文件内容
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public String read(String filename) throws Exception {
		FileInputStream fis = EHTApplication.getInstance()
				.openFileInput(filename);
		byte[] buf = readInputStream(fis);
		return new String(buf);
	}

	public byte[] readAsByteArray(String filename) throws Exception {
		FileInputStream fis = EHTApplication.getInstance()
				.openFileInput(filename);
		return readInputStream(fis);
	}

	public void save(String filename, String content) throws Exception {
		FileOutputStream fos = EHTApplication
				.getInstance().openFileOutput(filename, Activity.MODE_PRIVATE);
		byte[] buf = content.getBytes();
		fos.write(buf);
		fos.close();
	}

	public void saveAppend(String filename, String content)
			throws Exception {
		FileOutputStream fos = EHTApplication
				.getInstance().openFileOutput(filename, Activity.MODE_APPEND);
		byte[] buf = content.getBytes();
		fos.write(buf);
		fos.close();
	}

	public void saveReadable(String filename, String content)
			throws Exception {
		FileOutputStream fos = EHTApplication
				.getInstance().openFileOutput(filename,
						Context.MODE_WORLD_READABLE);
		byte[] buf = content.getBytes();
		fos.write(buf);
		fos.close();
	}

	public void saveReadableWriteable(String filename, String content)
			throws Exception {
		FileOutputStream fos = EHTApplication
				.getInstance().openFileOutput(
						filename,
						Context.MODE_WORLD_READABLE
								| Context.MODE_WORLD_WRITEABLE);
		byte[] buf = content.getBytes();
		fos.write(buf);
		fos.close();
	}

	public void saveToSDCard(String filename, String content)
			throws Exception {
		saveToSDCard(null, filename, content);
	}

	public void saveWriteable(String filename, String content)
			throws Exception {
		FileOutputStream fos = EHTApplication
				.getInstance().openFileOutput(filename,
						Context.MODE_WORLD_WRITEABLE);
		byte[] buf = content.getBytes();
		fos.write(buf);
		fos.close();
	}

	/**
	 * Directory class definitions
	 * 
	 * @author Administrator
	 * 
	 */
	public static class Directory {
		private File dir;
		private String path;
		private int space;

		public Directory(File dir) {
			this.dir = dir;
		}

		public Directory(File dir, int space) {
			this.dir = dir;
			this.space = space;
		}

		public Directory(String dirName, int space) {
			this(new File(dirName), space);
		}

		public File getDir() {
			return this.dir;
		}

		public String getPath() {
			if ((path == null) && (dir != null)) {
				path = dir.getAbsolutePath();
			}
			return path;
		}

		public int getSpace() {
			return space;
		}

		public void setDir(File dir) {
			this.dir = dir;
		}

		public void setPath(String dirPath) {
			if (getPath() == null || !getPath().equals(dirPath)) {
				this.dir = new File(dirPath);
				this.path = dirPath;
			}
		}

		public void setSpace(int space) {
			this.space = space;
		}
	}

}
