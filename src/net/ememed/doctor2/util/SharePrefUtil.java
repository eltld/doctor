package net.ememed.doctor2.util;

import java.io.Serializable;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.LoginEntry;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class SharePrefUtil implements Serializable{
	private static SharedPreferences pref;
	private static Editor edit;
	static {
		pref = PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext.getApplicationContext());
		edit = pref.edit();
	}
	public static void putBoolean(String key, boolean value) {
		edit.putBoolean(key, value);
	}

	public static void putInt(String key, int value) {
		edit.putInt(key, value);
	}

	public static void putString(String key, String value) {
		edit.putString(key, value);
	}

	public static void putFloat(String key, float value) {
		edit.putFloat(key, value);
	}

	public static void putLong(String key, long value) {
		edit.putLong(key, value);
	}

	public static String getString(String key) {
//		if (key.equals(Conast.ACCESS_TOKEN)) {
//			return "8888";//调试用通用token
//		}
		return pref.getString(key, "");
	}

	public static int getInt(String key) {
		return pref.getInt(key, 0);
	}
	public static boolean getBoolean(String key) {
		return pref.getBoolean(key, false);
	}
	public static boolean getMsgNotifyBoolean(String key) {
		return pref.getBoolean(key, true);
	}
	public static float getFloat(String key) {
		return pref.getFloat(key, 0);
	}

	public static long getLong(String key) {
		return pref.getLong(key, 0);
	}

	public static void commit() {
		edit.commit();
	}
	
	public static void saveDoctor(LoginEntry doctor){
		SharePrefUtil.putBoolean(Conast.LOGIN, true);
		SharePrefUtil.putString(Conast.Doctor_ID, doctor.getMEMBERID());
		SharePrefUtil.putString(Conast.MEMBER_NAME, doctor.getMEMBERNAME());
		SharePrefUtil.putString(Conast.Doctor_Name, doctor.getREALNAME());
		SharePrefUtil.putString(Conast.Doctor_Type,
				doctor.getALLOWFREECONSULT());// 1为全科
		SharePrefUtil.putString(Conast.ACCESS_TOKEN, doctor.getTOKEN());
		SharePrefUtil.putString(Conast.AVATAR, doctor.getAVATAR());
		SharePrefUtil.putString(Conast.AVATAR_UNAUDIT, doctor.getAVATAR_UNAUDIT());
		
		SharePrefUtil.putString(Conast.MOBILE, doctor.getMOBILE());
		SharePrefUtil.putString(Conast.PIC_IDENTITY, doctor.getCARDFILE());
		SharePrefUtil.putString(Conast.PIC_CERT_POSI,
				doctor.getCERTIFICATEHOME());
		SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, doctor.getCERTIFICATE());
		SharePrefUtil.putString(Conast.CARDNUMBER, doctor.getCARDNUMBER());
		SharePrefUtil.putString(Conast.SPECIALITY, doctor.getSPECIALITY());
		SharePrefUtil.putString(Conast.Doctor_Professional,
				doctor.getPROFESSIONAL());
		SharePrefUtil.putString(Conast.HOSPITAL_NAME, doctor.getHOSPITALNAME());
		SharePrefUtil.putString(Conast.ROOM_NAME, doctor.getROOMNAME());
		SharePrefUtil.putString(Conast.AUDIT_TIME, doctor.getAUDITTIME());
		SharePrefUtil.putString(Conast.USER_MONEY, doctor.getUSERMONEY());
		SharePrefUtil.putString(Conast.USER_TYPE, doctor.getUTYPE());
		
		//服务项相关 图文资讯、预约通话。。。。
		ServiceSettingEntry serviceEntry = doctor.getService_setting();
		if(serviceEntry != null){
			if(serviceEntry.getTextconsult_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_CONSULT_PICTURE, serviceEntry.getTextconsult_setting().getEnable_textconsult());
			}
			if(serviceEntry.getCall_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_CONSULT_PHONE, serviceEntry.getCall_setting().getEnable_call());
			}
			
			if(serviceEntry.getPacket_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_PRIVATE_DOCTOR, 1);
			} else {
				SharePrefUtil.putInt(Conast.FLAG_PRIVATE_DOCTOR, 0);
			}
			
			if(serviceEntry.getShangmen_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_SHANGMEN, serviceEntry.getShangmen_setting().getEnable_shangmen());
			}
			
			if(serviceEntry.getJiahao_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_JINJI_JIAHAO, serviceEntry.getJiahao_setting().getEnable_jiahao());
			}
			
			if(serviceEntry.getZhuyuan_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_JINJI_ZHUYUAN, serviceEntry.getZhuyuan_setting().getEnable_zhuyuan());
			}
			
			SharePrefUtil.putInt(Conast.FLAG_OTHER_SERVICE, 1);
			
			/**此处添加Conast.IS_SAVE_SERVICE_INFO主要是为了解决升级2.1版本导致的首页服务项开通了也会显示未开通状态的bug，
			 * 因为服务开通情况相关的标记是新添加，若升级版本后是自动登录，没有经过此处登录接口，则本地不存在服务开通情况的标记*/
			SharePrefUtil.putBoolean(Conast.IS_SAVE_SERVICE_INFO, true);
		}

		//审核是否通过
		if (!TextUtils.isEmpty(doctor.getAUDITSTATUS()) && "1".equals(doctor.getAUDITSTATUS())) {
			SharePrefUtil.putBoolean(Conast.VALIDATED, true);
		}
		//审核状态 /** 资质审核状态0=未上传，1=审核通过，2=待审，3=有误 */
		if (!TextUtils.isEmpty(doctor.getAUDITSTATUS())) {
			SharePrefUtil.putString(Conast.AUDIT_STATUS, doctor.getAUDITSTATUS());
			
			//审核失败的原因
			if(doctor.getAUDITSTATUS().equals("3")){
				SharePrefUtil.putString(Conast.AUDIT_REFUSE, doctor.getAUDITREFUSE());
			}
		}
		
		if(!MyApplication.judgeAuditTimeIsInThreeDays(doctor.getAUDITTIME())){
			SharePrefUtil.putBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+doctor.getMEMBERID(), true);
		}
		
		SharePrefUtil.commit();
	}
	
	public static void cleanDoctor(){
		putBoolean(Conast.LOGIN, false);
		putBoolean(Conast.VALIDATED, false);
		putBoolean(Conast.FLAG, false);

		putString(Conast.BANK_ID, "");
		putString(Conast.BANK_HOLDER, "");
		putString(Conast.BANK_NAME, "");
		putString(Conast.BANK_NUMBER, "");

		putString(Conast.Doctor_ID, "");
		putString(Conast.Doctor_Name, "");
		putString(Conast.MEMBER_NAME, "");
		putString(Conast.Doctor_Type, "");
		putString(Conast.ACCESS_TOKEN, "");
		putString(Conast.AVATAR, "");
		putString(Conast.AVATAR_UNAUDIT, "");
		putString(Conast.USER_TYPE, "");
		
		putString(Conast.EMAIL_STR, "");
		putString(Conast.SPECIALITY, "");
		putString(Conast.MOBILE, "");

		putString(Conast.TOTAL, "");
		putString(Conast.AVAILABLE, "");
		putString(Conast.FREEZE, "");
		putString(Conast.AVAILABLE_NEW, "");

		putString(Conast.PIC_CERT_POSI, "");
		putString(Conast.PIC_CERT_OPPSI, "");
		putString(Conast.SPECIALITY, "");
		putString(Conast.HOSPITAL_NAME, "");
		putString(Conast.ROOM_NAME, "");
		putString(Conast.AUDIT_TIME, "");
		putString(Conast.USER_MONEY, "");
		
		putString(Conast.AUDIT_STATUS, "");
		putString(Conast.AUDIT_TIME, "");
		putString(Conast.AVATAR_UNAUDIT, "");
		putString(Conast.AVATAR,"");
		putString(Conast.AVATAR_UNAUDIT,"");
		
		putInt(Conast.FLAG_CONSULT_PICTURE,0);
		putInt(Conast.FLAG_CONSULT_PHONE,0);
		putInt(Conast.FLAG_PRIVATE_DOCTOR,0);
		putInt(Conast.FLAG_SHANGMEN,0);
		putInt(Conast.FLAG_JINJI_JIAHAO,0);
		putInt(Conast.FLAG_JINJI_ZHUYUAN,0);
		putInt(Conast.FLAG_OTHER_SERVICE,1);
		
		putBoolean(Conast.IS_SAVE_SERVICE_INFO,false);
		putString(Conast.SIGN_IN,"");
		
		putInt(Conast.YIMIHUA_TOTAL, 0);

		commit();
	}
}
