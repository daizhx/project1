package com.jiuzhansoft.ehealthtec.http.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Encrypt {


	private static final char DIGITS[] = {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0
	};

	public Md5Encrypt()
	{
	}

	public static char[] encodeHex(byte abyte0[])
	{
		int i = abyte0.length;
		char ac[] = new char[i << 1];
		int j = 0;
		int k = 0;
		do
		{
			if (j >= i)
				return ac;
			int l = k + 1;
			char ac1[] = DIGITS;
			int i1 = (abyte0[j] & 0xf0) >>> 4;
			char c = ac1[i1];
			ac[k] = c;
			k = l + 1;
			char ac2[] = DIGITS;
			int j1 = abyte0[j] & 0xf;
			char c1 = ac2[j1];
			ac[l] = c1;
			j++;
		} while (true);
	}

	//不使用
	public static String md5(String s)
	{
		MessageDigest messagedigest;
		MessageDigest messagedigest1;
		char ac[];
		try
		{
			messagedigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException nosuchalgorithmexception)
		{
			throw new IllegalStateException("System doesn't support MD5 algorithm.");
		}
		messagedigest1 = messagedigest;
		try
		{
			byte abyte0[] = s.getBytes("utf-8");
			messagedigest1.update(abyte0);
		}
		catch (UnsupportedEncodingException unsupportedencodingexception)
		{
			throw new IllegalStateException("System doesn't support your  EncodingException.");
		}
		ac = encodeHex(messagedigest1.digest());
		return new String(ac);
	}

//	public static final int CACHE_MODE_AUTO = 0;
//	public static final int CACHE_MODE_ONLY_CACHE = 1;
//	public static final int CACHE_MODE_ONLY_NET = 2;

	/**
	 * MD5加密
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};       

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
