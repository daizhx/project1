package com.jiuzhansoft.ehealthtec.http.utils;

import java.util.Locale;

public class LanguageUtil {

	public static int ZHCN=1;
	public static int ZHTW=2;
	public static int EN=3;
	public static int getLanguage(){
		String language=Locale.getDefault().getLanguage();
        String country=Locale.getDefault().getCountry();
		if("en".equals(language)){
			return EN;
		}
		if("zh".equals(language)){
			if("CN".equals(country)){
				return ZHCN;
			}else if("TW".equals(country)){
				return ZHTW;
			}			
		}
		return EN;
	}

}
