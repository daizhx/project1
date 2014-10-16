package com.jiuzhansoft.ehealthtec.http.utils;

public class EhtWebUtil {

    public static String sgin(String appKey,String timestamp,String secret,String token,String param){
       String signature = "";
       try {
            StringBuffer sbf = new StringBuffer();
            sbf.append(secret).append("appKey").append(appKey).append("timestamp")
                .append(timestamp).append(secret).append(token).append(param);
            signature  = Md5Encrypt.MD5(sbf.toString());
        } catch (Exception e) {
        	
        }
        return signature;
    }
}
