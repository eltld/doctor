package net.ememed.doctor2.util;

import net.ememed.doctor2.R;

public class Conast {
	public final static int ACCEPT = 1;
	public final static int REJECT = 2;
	public final static int TRANSFER = 3;
	public final static int MEMBER_DOCUMENT = 4;

	public final static String ACCESS_TOKEN = "token";
	public final static String AVATAR = "avatar";
	public final static String AVATAR_UNAUDIT = "avatar_unaudit";
	public final static String Doctor_ID = "doctorId";
	public final static String Doctor_Name = "doctorName";//真实姓名
	public final static String Doctor_Type = "doctorType";
	public final static String Doctor_Professional = "doctorprofessional";
	//public final static String Doctor_laboratory_Phone = "doctorlaboratoryphone";	//就职科室电话
	public final static String USER_TYPE = "utype";//用户类型:docotr/user
	
	public final static String LOGIN = "login";
	public final static String VALIDATED = "validated";	//审核是否通过
	public final static String AUDIT_STATUS = "audit_status"; //审核状态 /** 资质审核状态0=未上传，1=审核通过，2=待审，3=有误 */
	public final static String AUDIT_REFUSE = "audit_refuse";	//审核失败的原因
	public final static String AUDIT_TIME = "audit_time";	//审核通过时间
	
	public final static String PIC_IDENTITY = "pic_id";
	public final static String PIC_CERT_POSI = "pic_cert";
	public final static String PIC_CERT_OPPSI = "pic_oppsi";
	public final static String PIC_LICENCE_POSITIVE = "pic_licence_positive";
	public final static String PIC_LICENCE_OPPOSITE = "pic_licence_opposite";
	public final static String PIC_BREAST_PLATE = "pic_breast_plate";
	public final static String PIC_WORK_PERMIT = "pic_work_permit";
	
	public final static String CERTIY_TYPE_CERT = "certificate_type_certificate";			//医师执业证书
	public final static String CERTIY_TYPE_LICENCE = "certificate_type_licence";			//医师资格证
	public final static String CERTIY_TYPE_BREST_PLATE = "certificate_type_breast_plate";	//胸牌
	public final static String CERTIY_TYPE_WORK_PERMIT = "certificate_type_work_permit";	//工作证
	
	
	
	public final static String BANK_ID = "bank_id";
	public final static String BANK_NUMBER = "bank_number";
	public final static String BANK_HOLDER = "bank_holder";
	public final static String BANK_NAME = "bank_name";
	public static final String BANK_BRANCH = "bank_branch";
	
	public final static String EMAIL_STR = "email_str";
	public final static String SPECIALITY = "speciality";
	public final static String RESUME = "resume";
	public final static String ROOM_PHONE = "room_phone";
	
	/**用于记录当天是否首次启动**/
	public final static String FIRST_START_ONE_DAY_TIME = "first_start_one_day_time";
	public final static String HAS_SUGGEST_UPDATE = "has_suggest_update";
	
	public static final String USER_NAME = "user_name";
	
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MEMBER_NAME = "member_name";//登录名
	public static final String MOBILE = "mobile";//电话;
	public static final String CARDNUMBER = "cardnumber";
	public static final String EM_SETTING_SAVE_MSG_NOTIFY = "em_setting_msg_notification";
	//财务详情
	public static final String TOTAL = "total";
	public static final String AVAILABLE = "available";
	public static final String FREEZE = "freeze";
	public static final String AVAILABLE_NEW = "available_new";
	public static final String FLAG = "flag";//判断银行卡是否设置
	public static final String HOSPITAL_NAME = "hospital_name";
	public static final String ROOM_NAME = "room_name";
	public static final String MBCID = "mbcid";
	public static final String USER_MONEY = "user_money";
	
	
	public static final String YOUJIANGYAOQING = "youjiangyaoqing";
	public static boolean RETURN_TO_CONVERSION_TAB = false;
	public static final String QQ_APPID = "1102305084";
	
	//服务开通项
	public static String FLAG_CONSULT_PICTURE = "flag_consult_picture";	//图文咨询
	public static String FLAG_CONSULT_PHONE = "flag_consult_phone";		//预约通话
	public static String FLAG_PRIVATE_DOCTOR = "flag_private_doctor";
	public static String FLAG_SHANGMEN = "flag_shangmen";
	public static String FLAG_JINJI_JIAHAO = "flag_jinji_jiahao";
	public static String FLAG_JINJI_ZHUYUAN = "flag_jinji_zhuyuan";
	public static String FLAG_OTHER_SERVICE = "flag_other_service";
	public static String IS_SAVE_SERVICE_INFO = "is_save_service_info";	//本地是否已经保存了用户服务开通情况的信息
	
	public static String FLAG_FIRST_INVITE_AFTER_PERMIT = "flag_first_invite_patient_after_verify_success";//资料审核通过后首次点击邀请患者
	
	public static String IS_ALREADY_ACCESS_BAIKE = "is_already_access_baike";//首次进入百科(false表示首次进入， true表示已经进入过)
	
	public static String YIMIHUA_TOTAL = "yimihua_total";//薏米花
	public static String SIGN_IN="sign_in";   //签到
	public static String YIMIHUA_EXCHANGE_RATIO="yimihua_exchange_ratio";   //薏米花兑换比例
	public static String SIGN_IN_RECORD="yimihua_sign_in_record";   //签到记录
	
}
