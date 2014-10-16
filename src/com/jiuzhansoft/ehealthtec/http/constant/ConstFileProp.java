package com.jiuzhansoft.ehealthtec.http.constant;

public class ConstFileProp {

	public static final int STORAGE_UNKNOWN = -1;
	public static final int STORAGE_INTERNAL = 1;
	public static final int STORAGE_EXTERNAL = 2;
    
	public static final long BIG_SIZE_THRESHOLD = 0x20000000L;
	public static final int ERROR = 255;
	
	public static final String IMAGE_CHILD_DIR = "/image";
	public static final String JSON_CHILD_DIR = "/json";
	
	public static final int INTERNAL_TYPE_FILE = 1;
	public static final int INTERNAL_TYPE_CACHE = 2;
	
	public static final int IMAGE_DIR = 1;
	public static final int JSON_DIR = 2;
	public static final String SHARED_PREFERENCES_JSON_DIR = "jsonFileCachePath";
	public static final String SHARED_PREFERENCES_JSON_DIR_STATE = "jsonFileCachePathState";
	public static final String aplcationDir = "/eht";

}
