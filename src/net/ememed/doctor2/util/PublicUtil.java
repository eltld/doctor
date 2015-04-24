package net.ememed.doctor2.util;

import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

import net.ememed.doctor2.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

@TargetApi(Build.VERSION_CODES.DONUT)
public class PublicUtil {
	

	public static String getVersionName(Context context) {
		String versionName = "2.0";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			return versionName;
		}
		return versionName;
	}
	
	public static String getServiceNameByid(String order_type) {
		String service_name = "未知订单";  
		try {
//			ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"预约加号",'4'=>"上门会诊",'14'=>"预约住院",
//			'11'=>"体检报告解读",'15'=>"私人医生服务",'16'=>"其他服务"
			if (!TextUtils.isEmpty(order_type)) {
				if ("1".equals(order_type)) {
					service_name = "图文咨询";
				} else if ("2".equals(order_type)){
					service_name = "预约通话";
				} else if ("3".equals(order_type)){
					service_name = "预约加号";
				} else if ("4".equals(order_type)){
					service_name = "上门会诊";
				} else if ("14".equals(order_type)){
					service_name = "预约住院";
				} else if ("11".equals(order_type)){
					service_name = "体检报告解读";
				} else if ("15".equals(order_type)){
					service_name = "私人医生服务";
				} else if ("16".equals(order_type)){
					service_name = "其他服务";
				}else if ("17".equals(order_type)){
					service_name = "免费提问";
				}
			}
			return service_name;	
		} catch (Exception e) {
			e.printStackTrace();
			return service_name;
		}
	}

	public static String getServiceState(String order_type,String stateResult) {
		String state = "";  
		try {
//			ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"预约加号",'4'=>"上门会诊",'14'=>"预约住院",
//			'11'=>"体检报告解读",'15'=>"私人医生服务",'16'=>"其他服务"
			if (!TextUtils.isEmpty(order_type) && !TextUtils.isEmpty(stateResult)) {
				if ("1".equals(order_type)) {
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("2".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "通话时间为:";
					} else if ("3".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("3".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "等待用户支付";
					} else if ("3".equals(stateResult)){
						state = "用户支付成功";
					} else if ("4".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("4".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "等待用户支付";
					} else if ("3".equals(stateResult)){
						state = "用户支付成功";
					} else if ("4".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("14".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "等待用户支付";
					} else if ("3".equals(stateResult)){
						state = "用户支付成功";
					} else if ("4".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("11".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "等待用户支付";
					} else if ("3".equals(stateResult)){
						state = "用户支付成功";
					} else if ("4".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("16".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "待处理";
					} else if ("2".equals(stateResult)){
						state = "等待用户支付";
					} else if ("3".equals(stateResult)){
						state = "用户支付成功";
					} else if ("4".equals(stateResult)){
						state = "服务结束";
					}
				} else if ("15".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "服务期内";
					} else if ("2".equals(stateResult)){
						state = "服务结束";
					} 
				} else if ("17".equals(order_type)){
					if ("1".equals(stateResult)) {
						state = "服务期内";
					} else if ("2".equals(stateResult)){
						state = "服务结束";
					} 
				} 
			}
			return state;	
		} catch (Exception e) {
			e.printStackTrace();
			return state;
		}
	}
	
	public static int getServiceDrawableByServiceid(String order_type) {
		int service_drawable = R.drawable.contact_text;  
		try {
//			ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"预约加号",'4'=>"上门会诊",'14'=>"预约住院",
//			'11'=>"体检报告解读",'15'=>"私人医生服务服务",'16'=>"其他服务"
			if (!TextUtils.isEmpty(order_type)) {
				if ("1".equals(order_type)) {
					service_drawable = R.drawable.icon_consult_picture;
				} else if ("2".equals(order_type)){
					service_drawable = R.drawable.icon_consult_phone;
				} else if ("3".equals(order_type)){
					service_drawable = R.drawable.icon_jinji_jiahao;
				} else if ("4".equals(order_type)){
					service_drawable = R.drawable.icon_shangmen_2;
				} else if ("14".equals(order_type)){
					service_drawable = R.drawable.icon_jinji_zhuyuan;
				} else if ("15".equals(order_type)){
					service_drawable = R.drawable.icon_private_doctor_2;
				} else if ("16".equals(order_type)){
					service_drawable = R.drawable.icon_other_service;
				} else if ("17".equals(order_type)){
					service_drawable = R.drawable.question_mianfei;
				}
			}
			return service_drawable;	
		} catch (Exception e) {
			e.printStackTrace();
			return service_drawable;
		}
	}
	
	public static String getServiceContentByid(String order_type) {
		String service_name = "";  
		try {
//			ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"预约加号",'4'=>"上门会诊",'14'=>"预约住院",
//			'11'=>"体检报告解读",'15'=>"私人医生服务",'16'=>"其他服务"
			if (!TextUtils.isEmpty(order_type)) {
				if ("1".equals(order_type)) {
					service_name = "<font color='red'>图文咨询</font><br />您与用户通过发送图片、文字、语音的方式，进行的线上问诊服务。帮助用户初步解答病情是您应尽的责任和义务。<br /><br />注：<br />*图文咨询不可作为疾病诊断的最终依据！<br />*单项单次图文咨询有效期为48小时！";
				} else if ("2".equals(order_type)){
					service_name = "<font color='red'>预约通话</font><br />通过电话沟通的方式进行的远程问诊服务。您需要尽快与用户文字沟通协商该服务的具体时间，并设置服务时间反馈给用户。您可以预约最近8小时内的可服务时间！<br /><br />注：<br />预约通话不可作为疾病诊断的最终依据！";
				} else if ("3".equals(order_type)){
					service_name = "<font color='red'>预约加号</font><br />用户因病情较为紧急而寻求的加急挂号就诊服务。您需要与用户沟通具体的就诊医院、时间、挂号费用及其他费用等问题，并设定合理价格提供给用户购买。<br />用户支付成功后，协助用户具体落实该项服务是您应尽的责任和义务！";
				} else if ("4".equals(order_type)){
					service_name = "<font color='red'>上门会诊</font><br />用户申请上门会诊时，系统已开放聊天功能，您可以与患者沟通上门服务地点、时间、费用以及用户病情等信息。沟通完成后，您需要设定一个价格合理的订单提供给用户购买！<br />用户支付成功后，协助用户具体落实该项服务是您应尽的责任和义务！";
				} else if ("14".equals(order_type)){
					service_name = "<font color='red'>预约住院</font><br />用户因病情紧急而寻求的住院就医服务。您需要与用户进行具体沟通，协商住院的医院、地点、床位费及其他医疗服务费用等问题，并设定合理价格提供给用户购买。<br />用户支付成功后，协助用户具体落实该项服务是您应尽的责任和义务！";
				} else if ("11".equals(order_type)){
					service_name = "体检报告解读";
				} else if ("15".equals(order_type)){
					service_name = "<font color='red'>私人医生服务</font><br />您所开启的一项包括图文咨询、预约通话的套餐服务，您需要根据套餐的内容与用户进行具体的文字沟通，以促成套餐所含各项服务的最终达成！";
				} else if ("16".equals(order_type)){
					service_name = "<font color='red'>其他服务</font><br />是指用户发起的针对疾病康复服务的其他服务请求。请您在看到这条订单请求之后，积极与用户沟通服务的细节，并设置合理价格供用户购买。用户支付成功后，协助用户具体落实该项服务是您应尽的责任和义务！";
				} 
			}
			return service_name;	
		} catch (Exception e) {
			e.printStackTrace();
			return service_name;
		}
	}
	
	public static String getLogtype(String logtype, int ordertype) {
		String service_name = "未知订单";  
		try {
//			LOGTYPE 日志类型 1系统自动, 2 账户充值, 3用户提现, 4订单支付, 5管理员手动修改,
//			7扣除冻结现金, 8打款给客户, 9 => '', 10 退款给客户, 11医生服务费用, 12医生资金结算

			if (!TextUtils.isEmpty(logtype)) {
				if ("1".equals(logtype)) {
					service_name = "系统自动";
				} else if ("2".equals(logtype)){
					service_name = "账户充值";
				} else if ("3".equals(logtype)){
					service_name = "用户提现";
				} else if ("4".equals(logtype)){
//					service_name = "订单支付";
					if(ordertype == 1){
						service_name = "图文咨询";
					}else if (ordertype == 2){
						service_name = "预约通话";
					}else if (ordertype == 3){
						service_name = "预约加号";
					}else if (ordertype == 4){
						service_name = "上门会诊";
					}else if (ordertype == 14){
						service_name = "预约住院";
					}else if (ordertype == 15){
						service_name = "私人医生服务";
					}else if (ordertype == 16){
						service_name = "其他服务";
					}
				} else if ("5".equals(logtype)){
					service_name = "平台支付";
				} else if ("7".equals(logtype)){
					service_name = "扣除冻结现金";
				} else if ("8".equals(logtype)){
					service_name = "打款给客户";
				} else if ("9".equals(logtype)){
					service_name = "";
				}else if ("10".equals(logtype)){
//					service_name = "退款给客户";
					if(ordertype == 1){
						service_name = "图文咨询";
					}else if (ordertype == 2){
						service_name = "预约通话";
					}else if (ordertype == 3){
						service_name = "预约加号";
					}else if (ordertype == 4){
						service_name = "上门会诊";
					}else if (ordertype == 14){
						service_name = "预约住院";
					}else if (ordertype == 15){
						service_name = "私人医生服务";
					}else if (ordertype == 16){
						service_name = "其他服务";
					}
				}else if ("11".equals(logtype)){
//					ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"预约加号",'4'=>"上门会诊",'14'=>"预约住院",
//					'11'=>"体检报告解读",'15'=>"私人医生服务服务",'16'=>"其他服务"
					if(ordertype == 1){
						service_name = "图文咨询";
					}else if (ordertype == 2){
						service_name = "预约通话";
					}else if (ordertype == 3){
						service_name = "预约加号";
					}else if (ordertype == 4){
						service_name = "上门会诊";
					}else if (ordertype == 14){
						service_name = "预约住院";
					}else if (ordertype == 15){
						service_name = "私人医生服务";
					}else if (ordertype == 16){
						service_name = "其他服务";
					}
				}else if ("12".equals(logtype)){
//					service_name = "医生资金结算";
					if(ordertype == 1){
						service_name = "图文咨询";
					}else if (ordertype == 2){
						service_name = "预约通话";
					}else if (ordertype == 3){
						service_name = "预约加号";
					}else if (ordertype == 4){
						service_name = "上门会诊";
					}else if (ordertype == 14){
						service_name = "预约住院";
					}else if (ordertype == 15){
						service_name = "私人医生服务";
					}else if (ordertype == 16){
						service_name = "其他服务";
					}
				}
			}
			return service_name;	
		} catch (Exception e) {
			e.printStackTrace();
			return service_name;
		}
	}
	public static String getDeviceUuid(Context context) {
		try {
			String deviceid = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE))
					.getDeviceId(); 
			
			if (TextUtils.isEmpty(deviceid)) {
				deviceid = JPushInterface.getUdid(context);
			}
			Logger.dout("deviceid:"+deviceid);
			return deviceid;	
		} catch (Exception e) {
			e.printStackTrace();
			return UUID.randomUUID().toString();
		}
	}
	
	public static int getSpinnerItemResId() {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			return android.R.layout.simple_spinner_item;
		} else {
			return R.layout.layout_spinner_item;
		}
	}
	
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			versionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
}
