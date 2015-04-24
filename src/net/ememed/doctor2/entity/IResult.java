package net.ememed.doctor2.entity;

import android.R.integer;
import net.ememed.doctor2.R;

public interface IResult {
	public static final int NET_ERROR=0;
	public static final int DATA_ERROR=1;
	public static final int RESULT = 2;
	public static final int END = 3;
	public static final int ERROR = 4;
	public static final int OK = 5;
	
	public static final int SERVICE_LIST = 16;
	public static final int PROBLEM_LIST = 17;
	public static final int PAGE_COUNT = 18;
	public static final int SERVICE_SET = 19;
	public static final int FINISHED = 20;
	public static final int LOGIN = 21;
	public static final int FAILURE = 22;
	public static final int LOGON_ERROR = 23;
	public static final int GET_VALI_CODE = 24;
	public static final int STATUS = 25;
	public static final int SUCCESS = 26;
	public static final int EXEU_CHANGE_PIC = 27;
	public static final int LOGOUT = 28;
	public static final int PHONE_VERIFY_CODE = 29;
	public static final int CONFIG = 30;
	public static final int SYSN_CONFIG = 31;
	
	public static final int PERSON_INFO = 32;
	public static final int SET_DOCTOR_NAME = 33;
	public static final int DOCTOR_IMAGE = 34;
	public static final int UPLOAD_PIC = 35;
	
	public static final int GET_USER = 36;
	public static final int RESET_PWD = 37;
	public static final int REGISTER = 38;
	
	public static final int TEST = 100;
	public static final int CHECK_VERSION = 101;
	public static final int NEWS_LIST = 102;
	
	public static final int DOCTOR_ACCOUNT = 103;
	public static final int INCOME_EXPEND = 104;
	public static final int DOCTOR_INCOME = 105;
	public static final int DOCTOR_EXPEND = 106;
	public static final int DOCTOR_FREEZE = 107;
	public static final int BANK_CARD = 108;
	public static final int BASE_PROFILE = 109;
	
	public static final int FINISHED_PHONE = 120;
	public static final int FINISHED_SHANGMEN = 121;
	public static final int FINISHED_JIUYI = 122;
	public static final int FINISHED_ZHUYUAN = 123;
	public static final int FINISHED_PACKET = 124;
	public static final int GET_CONTACT_LIST = 125;
	public static final int ORDER_LIST = 126;
	public static final int SET_SERVICE_PRICE = 127;
	public static final int SET_CALL_TIME = 128;
	public static final int GET_CALL_TIME = 129;
	
	public static final int MEMBER_INFO = 130;
	public static final int GET_OTHER_SERVICE = 131;
	public static final int SHOW_TIME_DIALOG = 132;
	public static final int SEND_SYS_MSG = 133;
	public static final int RELOGIN = 134;
	public static final int UN_LOGIN = 135;
	public static final int LOCAL_CONSULT_INFO = 136;
	public static final int GET_IM_HISTORY = 137;
	public static final int NEXT = 138;
	public static final int GET_BANKCARD = 139;
	public static final int NO_CARD = 140;
	public static final int INVITE_INFO = 140;
	public static final int ADD_NEWS_COMMENT = 141;
	public static final int CREATE_GROUP_SUCCEE = 142;
	public static final int GROUP_LIST = 143;
	public static final int EXIT_TO_GROUP = 147;
	public static final int Share_Cancel = 144;
	public static final int Share_Complete = 145;
	public static final int Share_Error = 146;
	
	public static final int CLEAR_FRONT_VIEW = 147;
	public static final int SET_DOCTOR_SEX = 148;
	public static final int SET_DOCTOR_ALLOW_FREE_CONSULT = 149;
	public static final int SET_DOCTOR_PROFESSIONAL = 150;

	public static final int GET_DOCTOR_APPLY_CASH = 151;	//提现列表

	public static final int UNBIND_BANKCARD = 152;	//解绑银行卡
	public static final int SET_BANKCARD = 153;	//解绑银行卡
	public static final int APPLY_CASH = 154;	//提现
	
	//消息中心相关
	public static final int GET_MESSGE_CLASSSIFY = 155;	//获取短信分类
	public static final int GET_MESSGE_LIST = 156;	//获取短信列表
	public static final int DELETE_MESSAGE = 157;	//获取短信列表
	public static final int MARK_UNREAD_MSG = 158;	//标记所有消息为已读
	public static final int ERROR_MARK_UNREAD_MSG = 159;	//标记所有消息为已读失败
	public static final int GET_MESSAGE_SETTING = 160;	//获取短信开关设置
	public static final int SET_MESSAGE_SETTING = 161;	//设置短信开关
	
	public static final int LOGIN_ERROR = 162;	//登录错误

	//个人百科
	public static final int GET_BAIKE_HOME = 163;	//获取个人百科主页信息
	public static final int SET_SAY = 164;	//发表说说
	public static final int GET_SAYS_DETAIL = 165;	//获取说说详情
	public static final int GIVE_PRAISE = 166;	//点赞
	public static final int COMMENT_SHUOSHUO = 167;	//评论说说
	public static final int COMMENT_OTHER = 168;	//点击评论他人说说按钮后发的消息
	public static final int GET_REWARD_LIST = 169;	//获取赏金列表
	public static final int GET_FANS_LIST = 170;	//获取粉丝列表
	public static final int DELETE_COMMENT_INNER = 171;	//删除评论（内部指令）
	public static final int DELETE_COMMENT = 172;	//删除评论（请求服务器）
	public static final int GET_OTHER_BAIKE = 173;	//获取其他人的百科主页
	public static final int ADD_ATTENTION = 174;	//添加关注
	public static final int GET_MY_ATTENTION = 175;	//获取关注列表
	public static final int CANCEL_ATTENTION = 176;	//取消关注
	public static final int GIVE_PRAISE_INNER = 177;//点赞(内部请求)
	public static final int SET_SHARE = 178;  //分享统计
	public static final int DELETE_SAYS = 179;  //删除说说
	public static final int GET_SAYS_LIST = 180;  //说说列表
	public static final int GET_RELATEME_LIST = 181;  //获取赞我的/分享我的/评论我的列表
	public static final int GET_MY_JOIN_INFO = 182;  //我参与过的相关消息
	
	public static final int GET_EXCHANGE_RECORD = 200;  //薏米花兑换记录,积分赚取明细
	public static final int SIGN_IN = 201;  //签到
	public static final int GET_YIMIHUA_HOME = 202;  //获取薏米花首页信息
	public static final int GET_RATIO = 203;  //获取积分兑换比例
	public static final int EXCHANGE_SCORE = 204;  //积分兑换
	
	public static final int GET_DOCTOR_FRIEND = 300;	//获取医生好友列表
	public static final int SEARCH_DOCTOR_FRIEND = 301;	//搜索医生好友
	
	public static final int GET_GROUP_LIST = 350;			//获取群组列表
	public static final int CREATE_GROUP = 351;				//创建群组
	public static final int SET_GROUP_LOGO 	= 352;			//上传群Logo
	public static final int GET_GROUP_NOTICE_LAST = 353;	//获取最新一条群组通知
	public static final int GET_GROUP_NOTICE_LIST = 354;	//获取群组通知列表
	
	public static final int PATIENT_GROUP=400;
	
	public static final int ALERT_MOBILE = 500;
	
	public static final int SEARCH_GROUP = 600;		//查找群
	public static final int GET_GROUP_DETAIL = 601;//获取群资料
	public static final int APPLY_TO_GROUP = 602;//申请入群
	public static final int GET_DOCTOR_DETAIL_INFO = 603;//获取医生详细信息
	public static final int NOTICE_INFO=604;
	public static final int GROUP_EXIT = 605;		//解散群
	public static final int GROUP_KICK = 606;		//踢出群
}
