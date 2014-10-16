package com.jiuzhansoft.ehealthtec.http.constant;

public class ConstSysConfig {
	public static final String SYS_CUST_CLIENT = "gyAndroidClient";
	public static final String SYS_SERVICE_COOKIES = "cookies";
	public static final int INIT_POOL_SIZE = 5;
	public static final int MAX_POOL_SIZE = 5;
	public static final int ATTEMPTS = 2;
	public static final int ATTEMPTS_TIME = 0;
	public static final int CONNECT_TIMEOUT = 20000;
	public static final int READ_TIMEOUT = 20000;
//	public static final String HOST_IP = "182.254.137.149:8080";
//	public static final String SERVER_NAME = "/client/rest/";
	public static final String HOST_IP = "182.254.137.149:9000";
//    public static final String HOST_IP = "127.0.0.1:8080";
	public static final String SERVER_NAME = "/ehtrest/api/";
	public static final boolean USE_TOKEN = true;

	public static final String REQUEST_METHOD = "GET";
	
	public static final String APP_KEY = "EHTAPPKEY3";
	public static final String SECRET = "SECRET3";
	
	public static final String SYS_USER_NAME = "user_name";
}
