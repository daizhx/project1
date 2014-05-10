package com.hengxuan.ehealthplatform.http.constant;

public class ConstFuncId {

	public static final String FUNCTION_ID = "functionId";
	
	
	public static final String HUMANACUPUNCTUREPOINTS= "getAcupuncturesByImageId";
	public static final String HUMANPOINTSSEARCH= "getAcupuncturesByDiseaseName";
	public static final String SEARCH_TIP = "getNameAndAcupunctureCountByDiseaseName";
	public static final String ADVERTISEMENT = "advertisement.getAdvertisementInfos";
	public static final String COMMONDIEASELIST = "getCommonDiseaseListByAgeRange";
	public static final String IRISHISTORYLIST = "iris.getIrisHistoryList";
	public static final String IRISINFOBYPARTANDCOLOR="getIrisInfoByPartAndColor";
	public static final String SKANANALYSISRESULT="skin.getAnalysisResult";
	public static final String HAIRANALYSISRESULT="hair.getAnalysisResult";
	
	//get user info function id
	public static final String FUNCTION_ID_FOR_USER_LOGIN = "user.login";
	public static final String FUNCTION_ID_FOR_USER_INFO = "user.getUserInfo";
	public static final String FUNCTION_ID_FOR_USER_REGISTER = "user.register";
	public static final String FUNCTION_ID_FOR_USER_RESETPASSWORD = "user.resetPassword";
	public static final String FUNCTION_ID_FOR_USER_CHANGEPASSWORD = "user.changePassword";
	public static final String FUNCTION_ID_FOR_USER_EDIT = "user.editInfo";
	//common相关的function id
	public static final String REG_DEVICE = "common.regDevice";
	public static final String REG_SERVER_CONFIG = "common.serverConfig";
	public static final String REG_VERSION = "commen.clientVersion";

	// 设备序列号
	public static final String SERIALNUM = "equipment.serialNum";
	
	// 添加至体检报告
	public static final String ADDTOSERVER = "iris.insertIrisHistoryList";
	public static final String DATELIST = "iris.getIrisHistoryDateList";
	public static final String CONTENTLIST = "iris.getIrisHistoryList";
	public static final String ADDSKANRESULTTOSERVER = "skin.insertSkinHistoryList";
	public static final String SKANDATELIST = "skin.getSkinHistoryDateList";
	public static final String SKANCONTENTLIST = "skin.getSkinHistoryList";
	public static final String ADDHAIRRESULTTOSERVER = "hair.insertHairHistoryList";
	public static final String HAREDATELIST = "hair.getHairHistoryDateList";
	public static final String HARECONTENTLIST = "hair.getHairHistoryList";
	public static final String BLOODPRESSUREADDTOSERVER = "bloodpressure.insertBloodpressureInfo";
	public static final String BLOODPRESSUREGETHISTORYLIST = "bloodpressure.getBloodpressureHistoryList";
	public static final String BLOODPRESSUREDELETE = "bloodpressure.deleteBloodpressureInfo";
	public static final String BLOODPRESSURELOCALTOSERVER = "bloodpressure.insertBloodpressureInfoList";
	public static final String BODYFATADDTOSERVER = "fatweigh.insertFatWeighInfo";
	public static final String BODYFATREPORT = "fatweigh.getFatWeighHistoryList";
}
