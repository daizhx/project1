package com.jiuzhansoft.ehealthtec.http.utils;

import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.log.Log;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.telephony.TelephonyManager;


public class NetUtils {

	  private static final int NO_NET = 0x7fffffff;
	  private static final int ROAMING = 0x7ffffffc;
	  private static final int UNKNOWN = 0x7ffffffe;
	  private static final int WIFI = 0x7ffffffd;

	  public static String getProxyHost(Context context)
	  {
		  String s;
			if (getType(context) != TelephonyManager.NETWORK_TYPE_EDGE)
			{
				s = null;
			} else
			{
				String s1 = Proxy.getDefaultHost();
				s = s1;
			}
			return s;
	  }

	  /**
	   * 
	   * @param context
	   * @return
	   */
	  public static int getType(Context context)
	  {
	    int i = NO_NET;
	    NetworkInfo networkinfo = ((ConnectivityManager)context.getSystemService("connectivity")).getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isConnected())
		{
//				Log.d("Temp", "netInfo.getType() == ConnectivityManager.NO_NET -->> ");
		}
		else
		{
			if (networkinfo.getType() != TelephonyManager.NETWORK_TYPE_UNKNOWN)
			{
				if (networkinfo.getType() == TelephonyManager.NETWORK_TYPE_GPRS)
				{
					i = WIFI;
				} else
				{
					i = UNKNOWN;
				}
			}
			else
			{
				i = ((TelephonyManager)context.getSystemService("phone")).getNetworkType();
			}
			
		}
		return i;
	  }

	  public static String toTypeName(int paramInt)
	  {
	    switch (paramInt)
	    {
	    
	    case TelephonyManager.NETWORK_TYPE_GPRS:
	    	return "GPRS";
	    case TelephonyManager.NETWORK_TYPE_EDGE:
	    	return "EDGE";
	    case TelephonyManager.NETWORK_TYPE_UMTS:
	    	return "UMTS";
	    case TelephonyManager.NETWORK_TYPE_CDMA:
	    	return "CDMA";
	    case TelephonyManager.NETWORK_TYPE_EVDO_0:
	    	return "CDMA - EvDo rev. 0";
	    case TelephonyManager.NETWORK_TYPE_EVDO_A:
	    	return "CDMA - EvDo rev. A";
	    case TelephonyManager.NETWORK_TYPE_1xRTT:
	    	return "CDMA - 1xRTT";
	    case TelephonyManager.NETWORK_TYPE_HSDPA:
	    	return "HSDPA";
	    case TelephonyManager.NETWORK_TYPE_HSUPA:
	    	return "HSUPA";
	    case TelephonyManager.NETWORK_TYPE_HSPA:
	    	return "HSPA"; 
	    case TelephonyManager.NETWORK_TYPE_IDEN:
	    	return "IDEN";
	    case WIFI:
	    	return "WIFI";
	    case NO_NET:
	    	return "NO_NET";
	    default:
	    	return "UNKNOWN";
	    }
	  }

}
