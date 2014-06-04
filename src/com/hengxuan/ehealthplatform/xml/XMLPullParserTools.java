package com.hengxuan.ehealthplatform.xml;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XMLPullParserTools {

	public XMLPullParserTools() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * just get a string from InputStream
	 * @param is,encode
	 * @return string
	 * @throws XmlPullParserException 
	 */
	public String parseXml(InputStream is, String encode, String tag) throws Exception{
		String str = null;
		String elementName = null;
		XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
		XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
		xmlPullParser.setInput(is, encode);
		int eventType = xmlPullParser.getEventType();
		while(eventType != XmlPullParser.END_DOCUMENT){
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.END_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				elementName = xmlPullParser.getName();
				
				break;
			case XmlPullParser.TEXT:	
				if(elementName.equals(tag)){
					return xmlPullParser.getText();
				}
				break;
			default:
				break;
			}
			eventType = xmlPullParser.next();
		}
		return str;
	}
}
