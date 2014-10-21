package com.jiuzhansoft.ehealthtec.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;

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

	
	/**
	 * 将log信息写到sd卡中
	 * @param msg
	 */
    public static void writeLogToSd(Context content, String msg){
        long time = System.currentTimeMillis();
        DateFormat df = new DateFormat();
        CharSequence timeStr = df.format("yyyy MM dd hh:mm:ss", time);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = null;
            try {
                path = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator + "eht" + File.separator + "log";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            File file = new File(path);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(file, true));
                out.write(timeStr +" " +  msg + "\n");
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
        	//没有sd卡时，写到内部文件系统中
        	String file = "crash" + System.currentTimeMillis() + ".log";
        	try {
				FileOutputStream fos = content.openFileOutput(file, Context.MODE_PRIVATE);
				fos.write(msg.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    }
}
