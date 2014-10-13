package com.jiuzhansoft.ehealthtec.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jiuzhansoft.ehealthtec.lens.iris.Organ;

public class XMLPullParserUtils {

	private static final String ROOT_NAME = "organList";
	private static final String ELEMENT_NAME="organ";
	
	private static final String ELEMENT_NAME_ORGAN_ID = "organid";
	private static final int ELEMENT_INDEX_FOR_ORGAN_ID = 0;
	private static final String ELEMENT_NAME_ORGAN_NAME = "name";
	private static final int ELEMENT_INDEX_FOR_ORGAN_NAME = 1;
	private static final String ELEMENT_NAME_RING_ID = "ringId";
	private static final int ELEMENT_INDEX_FOR_RING_ID = 2;
	private static final String ELEMENT_NAME_IN_RADUIS = "inRaduis";
	private static final int ELEMENT_INDEX_FOR_IN_RADUIS = 3;
	private static final String ELEMENT_NAME_OUT_RADUIS = "outRaduis";
	private static final int ELEMENT_INDEX_FOR_OUT_RADUIS = 4;
	private static final String ELEMENT_NAME_START_ANGLE = "startAngle";
	private static final int ELEMENT_INDEX_FOR_START_ANGLE = 5;
	private static final String ELEMENT_NAME_ANGLE = "angle";
	private static final int ELEMENT_INDEX_FOR_ANGLE = 6;
	
	private InputStream input = null;
	private List<Organ> all = null;
	
	public XMLPullParserUtils(InputStream input){
		this.input = input;
	}
	
	public void parseXml() throws Exception {
		String elementName = null;
		Organ item = null;
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		parser.setInput(this.input, "UTF-8");
		int eventType = parser.getEventType();
		//如果文档没有结束，继续进行下面的解析
		while(eventType != XmlPullParser.END_DOCUMENT){
			if(XmlPullParser.START_DOCUMENT == eventType){
				//实例化list对象
				this.all = new ArrayList<Organ>();
			}else if (eventType == XmlPullParser.START_TAG) {
				elementName = parser.getName();
				if(ELEMENT_NAME.equals(elementName)){
					//实例化子对象
					item = new Organ();
				}
			}else if(XmlPullParser.END_TAG == eventType){
				elementName = parser.getName();
				if(ELEMENT_NAME.equals(elementName)){
					this.all.add(item);
					item = null;
				}
				elementName = null;
			}else if(XmlPullParser.TEXT == eventType){
				//System.out.println("----------------value=" + parser.getText());
				switch(getElementIndex(elementName)){
				case ELEMENT_INDEX_FOR_ORGAN_ID:
					item.setOrganId(Integer.parseInt(parser.getText().trim()));
					break;
				case ELEMENT_INDEX_FOR_ORGAN_NAME:
					item.setName(parser.getText().trim());
					break;
				case ELEMENT_INDEX_FOR_RING_ID:
					item.setRingId(Integer.parseInt(parser.getText().trim()));
					break;
				case ELEMENT_INDEX_FOR_IN_RADUIS:
					item.setInRaduis(Float.parseFloat(parser.getText().trim()));
					break;
				case ELEMENT_INDEX_FOR_OUT_RADUIS:
					item.setOutRaduis(Float.parseFloat(parser.getText().trim()));
					break;
				case ELEMENT_INDEX_FOR_START_ANGLE:
					item.setStartAngle(Float.parseFloat(parser.getText().trim()));
					break;
				case ELEMENT_INDEX_FOR_ANGLE:
					item.setAngle(Float.parseFloat(parser.getText().trim()));
					break;
				default:
					break;
				}
			}
			eventType = parser.next();
		}
		
	}

	private int getElementIndex(String elementName) {
		int elementIndex = -1;
		if(ELEMENT_NAME_ORGAN_ID.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_ORGAN_ID;
		}else if(ELEMENT_NAME_ORGAN_NAME.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_ORGAN_NAME;
		}else if(ELEMENT_NAME_RING_ID.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_RING_ID;
		}else if(ELEMENT_NAME_IN_RADUIS.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_IN_RADUIS;
		}else if(ELEMENT_NAME_OUT_RADUIS.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_OUT_RADUIS;
		}else if(ELEMENT_NAME_START_ANGLE.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_START_ANGLE;
		}else if(ELEMENT_NAME_ANGLE.equals(elementName)){
			elementIndex = ELEMENT_INDEX_FOR_ANGLE;
		}
		return elementIndex;
	}

	public List<Organ> getAll() {
		return all;
	}
	
	
	

}
