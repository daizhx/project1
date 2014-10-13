package com.jiuzhansoft.ehealthtec.http.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

	  public static Date parseDate(String paramString)
	  {
	    Date localDate2;
	    try
	    {
	      localDate2 = new SimpleDateFormat("yyyy-MM-dd aahh:mm:ss").parse(paramString);
	    }
	    catch (ParseException localParseException)
	    {
	      localParseException.printStackTrace();
	      localDate2 = null;
	    }
	    
	    return localDate2;
	  }

}
