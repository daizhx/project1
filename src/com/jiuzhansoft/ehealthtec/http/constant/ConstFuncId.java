package com.jiuzhansoft.ehealthtec.http.constant;

public class ConstFuncId {

	public static final String FUNCTION_ID = "functionId";
	

//	public static final String HUMANACUPUNCTUREPOINTS= "getAcupuncturesByImageId";
//	public static final String HUMANPOINTSSEARCH= "getAcupuncturesByDiseaseName";
//	public static final String SEARCH_TIP = "getNameAndAcupunctureCountByDiseaseName";
//	public static final String ADVERTISEMENT = "advertisement.getAdvertisementInfos";
//	public static final String COMMONDIEASELIST = "getCommonDiseaseListByAgeRange";
//	public static final String IRISHISTORYLIST = "iris.getIrisHistoryList";
	//��ѯ��Ĥ������Ϣ
	public static final String IRISINFOBYPARTANDCOLOR="camera/get_analysis_iris";
	//��ȡƤ���������
	public static final String SKANANALYSISRESULT="camera/get_analysis_by_type";
	//��ȡë���������
	public static final String HAIRANALYSISRESULT="camera/get_analysis_by_type";
	//�û���¼�ӿ�
	public static final String FUNCTION_ID_FOR_USER_LOGIN = "user/login";
	//�û�ע��ӿ�
//	public static final String FUNCTION_ID_FOR_USER_INFO = "user/register";
	public static final String FUNCTION_ID_FOR_USER_REGISTER = "user/register";
//	public static final String FUNCTION_ID_FOR_USER_RESETPASSWORD = "user.resetPassword";
//	public static final String FUNCTION_ID_FOR_USER_CHANGEPASSWORD = "user.changePassword";
	//�༭�û���Ϣ
	public static final String FUNCTION_ID_FOR_USER_EDIT = "user/edit_userInfo";
	//common��ص�function id
//	public static final String REG_DEVICE = "common.regDevice";
//	public static final String REG_SERVER_CONFIG = "common.serverConfig";
//	public static final String REG_VERSION = "commen.clientVersion";

	// �豸���к�
	public static final String SERIALNUM = "equipment.serialNum";
	
	//��Ӻ�Ĥ��Ϣ����챨��
	public static final String ADDTOSERVER = "camera/save_iris_report";
	//��ѯ��Ĥ��ⱨ����ʷ��¼
	public static final String DATELIST = "camera/get_Iris_report_by_user";
//	public static final String CONTENTLIST = "iris.getIrisHistoryList";
	//����Ƥ����챨�浽������
	public static final String ADDSKANRESULTTOSERVER = "camera/save_skin_hair_report";
	//��ѯƤ����챨��
	public static final String SKANDATELIST = "camera/get_report_by_user";
//	public static final String SKANCONTENTLIST = "skin.getSkinHistoryList";
	//����ë����챨�浽������
	public static final String ADDHAIRRESULTTOSERVER = "camera/save_skin_hair_report";
	//��ѯë����챨��
	public static final String HAREDATELIST = "camera/get_report_by_user";
//	public static final String HARECONTENTLIST = "hair.getHairHistoryList";
	//����Ѫѹ����
	public static final String BLOODPRESSUREADDTOSERVER = "bloodpressurre/insertBloodpressureInfo";
	//��ȡѪѹ������ʷ����
	public static final String BLOODPRESSUREGETHISTORYLIST = "bloodpressurre/getBloodpressureHistoryList";
	public static final String BLOODPRESSUREDELETE = "bloodpressure.deleteBloodpressureInfo";
	public static final String BLOODPRESSURELOCALTOSERVER = "bloodpressure.insertBloodpressureInfoList";
	//������֬�Ʊ���
	public static final String BODYFATADDTOSERVER = "fatweigh/save_fatweigh_report";
	//��ѯ��֬�Ʊ���
	public static final String BODYFATREPORT = "fatweigh/get_fatweigh_report_by_user";
	
	//��ȡhealth tip�ķ������ӿ�
	public static final String TODAY_TIP = "avdNew/getNewInfo";
	//��ȡhealth tips list�ķ������ӿ�
	public static final String HEALTH_TIPS = "avdNew/getAllInfo";
}
