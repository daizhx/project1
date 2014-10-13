package com.jiuzhansoft.ehealthtec.http.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.jiuzhansoft.ehealthtec.http.HttpGroup;


public class IOUtil {

	private static int bufferSize = 16384;
	
	public IOUtil()
	{
	}
	
	public interface ProgressListener
	{
		public abstract void notify(int i, int j);
	}	

	public static byte[] readAsBytes(InputStream inputstream, ProgressListener progresslistener)
		throws Exception
	{
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		byte abyte1[] = new byte[bufferSize];
		int i = 0;
		int j = 0;
		while(true)
		{
			j = inputstream.read(abyte1);
			if (j != -1)
			{
				bytearrayoutputstream.write(abyte1, 0, j);
				i += j;
				if (progresslistener != null)
					progresslistener.notify(j, i);
			}
			else
			{
				byte abyte2[] = bytearrayoutputstream.toByteArray();		
				if (bytearrayoutputstream != null)
				{
					try
					{
						bytearrayoutputstream.close();
					}
					catch (Exception exception2) { }
				}
				return abyte2;
			}
		}
		
	}

	public static void readAsFile(InputStream inputstream, FileOutputStream fileoutputstream, ProgressListener progresslistener, HttpGroup.StopController stopcontroller)
		throws Exception
	{
		byte abyte0[] =  new byte[bufferSize];
		int i = 0;
		int j = 0;
		boolean flag = false;
		if(inputstream == null || fileoutputstream == null)
			return ;
		while(true)
		{
			j = inputstream.read(abyte0);
			if (j == -1)
			{
		   			break;
			}
			else
			{
		    		flag = stopcontroller.isStop();
		    		if (!flag)
		    		{
					fileoutputstream.write(abyte0, 0, j);
					i += j;
					if (progresslistener != null)
						progresslistener.notify(j, i);
		    		}
		    		else
		    			break;
			}
	    }
		if (fileoutputstream != null)
			try
			{
				fileoutputstream.close();
			}
			catch (Exception exception2) { }
	}

	public static String readAsString(InputStream inputstream, String s)
		throws Exception
	{
		return readAsString(inputstream, s, null);
	}

	public static String readAsString(InputStream inputstream, String s, ProgressListener progresslistener)
		throws Exception
	{
		String s1;
		try
		{
			byte abyte0[] = readAsBytes(inputstream, progresslistener);
			s1 = new String(abyte0, s);
		}
		catch (UnsupportedEncodingException unsupportedencodingexception)
		{
			s1 = null;
		}
		return s1;
	}


}
