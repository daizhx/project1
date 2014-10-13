package com.jiuzhansoft.ehealthtec.log;

import com.jiuzhansoft.ehealthtec.config.Configuration;


public class Log {

	public static boolean D;
	public static boolean E;
	public static boolean I;
	public static boolean V;
	public static boolean W;
	public static boolean T;
	private static boolean printLog = Boolean.parseBoolean(Configuration
			.getProperty("printLog", "false"));

	static {
		if (printLog) {

			D = Boolean.parseBoolean(Configuration.getProperty("debugLog",
					"false"));
			E = Boolean.parseBoolean(Configuration.getProperty("errorLog",
					"false"));
			I = Boolean.parseBoolean(Configuration.getProperty("infoLog",
					"false"));
			V = Boolean.parseBoolean(Configuration.getProperty("viewLog",
					"false"));
			W = Boolean.parseBoolean(Configuration.getProperty("warnLog",
					"false"));
			T = Boolean.parseBoolean(Configuration.getProperty("testLog",
					"false"));
		}
	}
	
	public static void t(String paramString1, String paramString2) {
		if (printLog) {
			// android.util.Log.d(paramString1, paramString2);
			System.out.println(paramString1 + "--" + paramString2);
		}
	}

	public static void d(String paramString1, String paramString2) {
		if (printLog) {
			android.util.Log.d(paramString1, paramString2);
		}
	}

	public static void d(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (printLog) {
			// android.util.Log.d(paramString1, paramString2, paramThrowable);
			System.out.println(paramString1 + "--" + paramString2 + "throwable info: " + paramThrowable.getMessage());
			
		}
	}

	public static void e(String paramString1, String paramString2) {
		if (printLog) {
			android.util.Log.e(paramString1, paramString2);
		}
	}

	public static void e(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (printLog) {
			android.util.Log.e(paramString1, paramString2, paramThrowable);
		}
	}

	public static void i(String paramString1, String paramString2) {
		if (printLog) {
			// android.util.Log.i(paramString1, paramString2);
			System.out.println(paramString1 + "--" + paramString2);
		}
	}

	public static void i(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (printLog) {
			// android.util.Log.i(paramString1, paramString2, paramThrowable);
			System.out.println(paramString1 + "--" + paramString2 + "throwable info: " + paramThrowable.getMessage());
		}
	}

	public static void v(String paramString1, String paramString2) {
		if (printLog) {
			android.util.Log.v(paramString1, paramString2);
		}
	}


	public static void v(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (printLog) {
			android.util.Log.v(paramString1, paramString2, paramThrowable);
		}
	}

	public static void w(String paramString1, String paramString2) {
		if (printLog) {
			android.util.Log.w(paramString1, paramString2);
		}
	}

	public static void w(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (printLog) {
			android.util.Log.w(paramString1, paramString2, paramThrowable);
		}
	}

	public static void w(String paramString, Throwable paramThrowable) {
		if (printLog) {
			android.util.Log.w(paramString, paramThrowable);
		}
	}

}
