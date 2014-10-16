package com.jiuzhansoft.ehealthtec.http.utils;


import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * 3DES���ܹ�����
* @ClassName: Des3 
* @Description: TODO 
* @author: Administrator 
* @date: 2014��9��17��
*
 */
public class Des3 {
    
    // �ӽ���ͳһʹ�õı��뷽ʽ
    private final static String encoding = "utf-8";

    public static final String DES3_KEY = "!@#$%^&*!@#$%^&*!@#$%^&*";
    public static final String DES3_IV = "!@#$%^&*";

   /**
    * 3DES ����
    * @param plainText ��Ҫ���ܵ��ַ���
    * @param secretKey ��Կ
    * @param iv ����
    * @return
    * @throws Exception
    */
    public static String encode(String plainText,String secretKey,String iv) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64.encode(encryptData);
    }

    /**
     * 3DES����
     * @param encryptText ��Ҫ���ܵ��ַ���
     * @param secretKey ��Կ
     * @param iv ����
     * @return
     * @throws Exception
     */
    public static String decode(String encryptText,String secretKey,String iv) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));

        return new String(decryptData, encoding);
    }
    
//   public static void main(String[] args) throws Exception {
//
//       String okgogog = "123";
//       String rt = encode(okgogog, DES3_KEY, DES3_IV);
//       System.out.println(rt);
//      System.out.println(decode("UkxKjcDlpY0=", DES3_KEY, DES3_IV));
//   }
}
