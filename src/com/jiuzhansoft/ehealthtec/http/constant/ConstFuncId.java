package com.jiuzhansoft.ehealthtec.http.constant;

public class ConstFuncId {

	public static final String FUNCTION_ID = "functionId";
	

//	public static final String HUMANACUPUNCTUREPOINTS= "getAcupuncturesByImageId";
//	public static final String HUMANPOINTSSEARCH= "getAcupuncturesByDiseaseName";
//	public static final String SEARCH_TIP = "getNameAndAcupunctureCountByDiseaseName";
//	public static final String ADVERTISEMENT = "advertisement.getAdvertisementInfos";
//	public static final String COMMONDIEASELIST = "getCommonDiseaseListByAgeRange";
//	public static final String IRISHISTORYLIST = "iris.getIrisHistoryList";
	//查询虹膜疾病信息
	public static final String IRISINFOBYPARTANDCOLOR="camera/get_analysis_iris";
	//获取皮肤分析结果
	public static final String SKANANALYSISRESULT="camera/get_analysis_by_type";
	//获取毛发分析结果
	public static final String HAIRANALYSISRESULT="camera/get_analysis_by_type";
	//用户登录接口
	public static final String FUNCTION_ID_FOR_USER_LOGIN = "user/login";
	//用户注册接口
//	public static final String FUNCTION_ID_FOR_USER_INFO = "user/register";
	public static final String FUNCTION_ID_FOR_USER_REGISTER = "user/register";
//	public static final String FUNCTION_ID_FOR_USER_RESETPASSWORD = "user.resetPassword";
//	public static final String FUNCTION_ID_FOR_USER_CHANGEPASSWORD = "user.changePassword";
	//编辑用户信息
	public static final String FUNCTION_ID_FOR_USER_EDIT = "user/edit_userInfo";
	//common相关的function id
//	public static final String REG_DEVICE = "common.regDevice";
//	public static final String REG_SERVER_CONFIG = "common.serverConfig";
//	public static final String REG_VERSION = "commen.clientVersion";

	// 设备序列号
	public static final String SERIALNUM = "equipment.serialNum";
	
	//添加虹膜信息至体检报告
	public static final String ADDTOSERVER = "camera/save_iris_report";
	//查询虹膜检测报告历史记录
	public static final String DATELIST = "camera/get_Iris_report_by_user";
//	public static final String CONTENTLIST = "iris.getIrisHistoryList";
	//保存皮肤体检报告到服务器
	public static final String ADDSKANRESULTTOSERVER = "camera/save_skin_hair_report";
	//查询皮肤体检报告
	public static final String SKANDATELIST = "camera/get_report_by_user";
//	public static final String SKANCONTENTLIST = "skin.getSkinHistoryList";
	//保存毛发体检报告到服务器
	public static final String ADDHAIRRESULTTOSERVER = "camera/save_skin_hair_report";
	//查询毛发体检报告
	public static final String HAREDATELIST = "camera/get_report_by_user";
//	public static final String HARECONTENTLIST = "hair.getHairHistoryList";
	//保存血压数据
	public static final String BLOODPRESSUREADDTOSERVER = "bloodpressurre/insertBloodpressureInfo";
	//获取血压测量历史数据
	public static final String BLOODPRESSUREGETHISTORYLIST = "bloodpressurre/getBloodpressureHistoryList";
	public static final String BLOODPRESSUREDELETE = "bloodpressure.deleteBloodpressureInfo";
	public static final String BLOODPRESSURELOCALTOSERVER = "bloodpressure.insertBloodpressureInfoList";
	//保存体脂称报告
	public static final String BODYFATADDTOSERVER = "fatweigh/save_fatweigh_report";
	//查询体脂称报告
	public static final String BODYFATREPORT = "fatweigh/get_fatweigh_report_by_user";
	
	//获取health tip的服务器接口
	public static final String TODAY_TIP = "avdNew/getNewInfo";
	//获取health tips list的服务器接口
	public static final String HEALTH_TIPS = "avdNew/getAllInfo";
}
