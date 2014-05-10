package com.hengxuan.ehealthplatform.http.utils;

import com.hengxuan.ehealthplatform.application.EHTApplication;
import com.hengxuan.ehealthplatform.log.Log;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.telephony.TelephonyManager;


public class NetUtils {

	  private static final int NO_NET = 0x7fffffff;
	  private static final int ROAMING = 0x7ffffffc;
	  private static final int UNKNOWN = 0x7ffffffe;
	  private static final int WIFI = 0x7ffffffd;

	  public static String getProxyHost()
	  {
		  String s;
			if (getType() != TelephonyManager.NETWORK_TYPE_EDGE)
			{
				s = null;
			} else
			{
				String s1 = Proxy.getDefaultHost();
				if (Log.D)
				{
					String s2 = (new StringBuilder("getProxyHost() -->> ")).append(s1).toString();
					Log.d("Temp", s2);
				}
				s = s1;
			}
			return s;
	  }

	  public static int getType()
	  {
	    int i = NO_NET;
	    NetworkInfo networkinfo = ((ConnectivityManager)EHTApplication.getInstance().getSystemService("connectivity")).getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isConnected())
		{
			if (Log.D)
				Log.d("Temp", "netInfo.getType() == ConnectivityManager.NO_NET -->> ");
		}
		else
		{
			if (networkinfo.getType() != TelephonyManager.NETWORK_TYPE_UNKNOWN)
			{
				if (networkinfo.getType() == TelephonyManager.NETWORK_TYPE_GPRS)
				{
					if (Log.D)
						Log.d("Temp", "netInfo.getType() == ConnectivityManager.TYPE_WIFI -->> ");
					i = WIFI;
				} else
				{
					if (Log.D)
						Log.d("Temp", "netInfo.getType() == ConnectivityManager.UNKNOWN -->> ");
					i = UNKNOWN;
				}
			}
			else
			{
				if (Log.D)
					Log.d("Temp", "netInfo.getType() == ConnectivityManager.TYPE_MOBILE -->> ");
				i = ((TelephonyManager)EHTApplication.getInstance().getSystemService("phone")).getNetworkType();
				if (Log.D)
				{
					String s = (new StringBuilder("getType() result -->> ")).append(i).toString();
					Log.d("Temp", s);
				}
				if (Log.D)
				{
					StringBuilder stringbuilder = new StringBuilder("getType() result toTypeName() -->> ");
					String s1 = toTypeName(i);
					String s2 = stringbuilder.append(s1).toString();
					Log.d("Temp", s2);
				}
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
