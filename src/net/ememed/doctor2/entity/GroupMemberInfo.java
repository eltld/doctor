package net.ememed.doctor2.entity;

import android.text.TextUtils;
import android.view.TextureView;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;

public class GroupMemberInfo {
	private String MEMBERID; // => 97918
    private String UTYPE; // => doctor
    private String EMAIL; // => 425hler@qq.com
    private String EMAILSTATUS; // => 0
    private String STATUS; // => -3
    private String REALNAME; // => 小号(薏米)
    private String REGTIME; // => 2014-08-28 13:50:52
    private String INCOMEMONEY; // => 4.15
    private String USERMONEY; // => 10537.05
    private String FREEZEMONEY; // => 157.61
    private String NEWPM; // => 0
    private String AVATAR; // => http://www.ememed.net/uploads/avatar/20150205/avatar_97918_1423107272_gVmuv4TqhK.jpg
    private String ISTESTER; // => 1
    private String OPENID; // => 
    private String SINAOPENID; // => 
    private String REG_CHANNEL; // => ios
    private String LAST_CHANNEL; // => ios
    private String BOSSTEST; // => 0
    private String TOKEN; // => 2110dc26b1cf523289a351be320f6d07
    private String LNG; // => 
    private String LAT; // => 
    private String GEO_AREAID; // => 
    private String AVATAR_UNAUDIT; // => http://www.ememed.net/uploads/avatar/20150215/avatar_97918_1423969368_NgXY7cw271.jpg
    private String AVATAR_AUDITSTATUS; // => 2
    private String AVATAR_AUDITTIME; // => 2015-02-05 11:35:37
    private String MESSAGE_SETTING; // => {"SMS_ORDER":"1","SMS_APPLY_CASH":"1","SMS_ACTIVITY":"1"}
    private String INVITE_NUMBER; // => 5doaboks
    private String INVITER_ID; // => 
    private String INVITER_UTYPE; // => 
    private String LAST_LOGINIP; // => 113.96.11.219
    private String LAST_LOGINTIME; // => 2015-04-20 17:38:08
    private String LOGIN_TIMES; // => 115
    private String POINTS_TOTAL; // => 95
    private String POINTS_NOW; // => 95
    private String SIGN_TIMES; // => 1
    private String SIGN_LASTTIME; // => 2015-04-20 11:47:06
    private String SEX; // => 1
    private String PROFESSIONAL; // => 副主任药师
    private String CARDTYPE; // => 
    private String CARDNUMBER; // => 
    private String CARDFILE; // => 
    private String DOCTORCODE; // => 
    private String UPDATETIME; // => 2015-04-20 16:20:27
    private String SPECIALITY; // => 头像是我助理。
    private String HOSPITALNAME; // => 广州体院
    private String ROOMNAME; // => 运动医学科
    private String CERTIFICATE; // => http://www.ememed.net/uploads/certificate/20150104/certificate_97918_1420340437_XjREsaKCrt.jpg
    private String CERTIFICATEHOME; // => http://www.ememed.net/uploads/certificate/20150116/certificate_97918_1421377923_hR3Ec1tgS4.jpg
    private String CERTIFICATENUM; // => 12345678
    private String RECOMMEND; // => 0
    private String ALLOWFREECONSULT; // => 1
    private String MOBILE; // => 13555555555
    private String BIRTHDAY; // => 1970-01-01
    private String AUDITSTATUS; // => 1
    private String AUDITREFUSE; // => 
    private String AUDITTIME; // => 2014-12-15 12:25:22
    private String DOCTORSTATUS; // => 1
    private String HOSPITALID; // => 
    private String RESUME; // => 我是医生。
    private String AREAID; // => 
    private String ROOMPHONE; // => 
    private String QUALIFY; // => 
    private String QUALIFYHOME; // => 
    private String CHESTCARD; // => 
    private String EMPLOYEECARD; // => 
    private String BAIKE_VISITORS; // => 97
    
    public GroupMemberInfo() {
	}
    
	public GroupMemberInfo(String avater) {
		AVATAR = avater;
		REALNAME = "";
	}
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getUTYPE() {
		return UTYPE;
	}
	public void setUTYPE(String uTYPE) {
		UTYPE = uTYPE;
	}
	public String getEMAIL() {
		return EMAIL;
	}
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	public String getEMAILSTATUS() {
		return EMAILSTATUS;
	}
	public void setEMAILSTATUS(String eMAILSTATUS) {
		EMAILSTATUS = eMAILSTATUS;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getREALNAME() {
		return REALNAME;
	}
	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
	public String getREGTIME() {
		return REGTIME;
	}
	public void setREGTIME(String rEGTIME) {
		REGTIME = rEGTIME;
	}
	public String getINCOMEMONEY() {
		return INCOMEMONEY;
	}
	public void setINCOMEMONEY(String iNCOMEMONEY) {
		INCOMEMONEY = iNCOMEMONEY;
	}
	public String getUSERMONEY() {
		return USERMONEY;
	}
	public void setUSERMONEY(String uSERMONEY) {
		USERMONEY = uSERMONEY;
	}
	public String getFREEZEMONEY() {
		return FREEZEMONEY;
	}
	public void setFREEZEMONEY(String fREEZEMONEY) {
		FREEZEMONEY = fREEZEMONEY;
	}
	public String getNEWPM() {
		return NEWPM;
	}
	public void setNEWPM(String nEWPM) {
		NEWPM = nEWPM;
	}
	public String getAVATAR() {
		return AVATAR;
	}
	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}
	public String getISTESTER() {
		return ISTESTER;
	}
	public void setISTESTER(String iSTESTER) {
		ISTESTER = iSTESTER;
	}
	public String getOPENID() {
		return OPENID;
	}
	public void setOPENID(String oPENID) {
		OPENID = oPENID;
	}
	public String getSINAOPENID() {
		return SINAOPENID;
	}
	public void setSINAOPENID(String sINAOPENID) {
		SINAOPENID = sINAOPENID;
	}
	public String getREG_CHANNEL() {
		return REG_CHANNEL;
	}
	public void setREG_CHANNEL(String rEG_CHANNEL) {
		REG_CHANNEL = rEG_CHANNEL;
	}
	public String getLAST_CHANNEL() {
		return LAST_CHANNEL;
	}
	public void setLAST_CHANNEL(String lAST_CHANNEL) {
		LAST_CHANNEL = lAST_CHANNEL;
	}
	public String getBOSSTEST() {
		return BOSSTEST;
	}
	public void setBOSSTEST(String bOSSTEST) {
		BOSSTEST = bOSSTEST;
	}
	public String getTOKEN() {
		return TOKEN;
	}
	public void setTOKEN(String tOKEN) {
		TOKEN = tOKEN;
	}
	public String getLNG() {
		return LNG;
	}
	public void setLNG(String lNG) {
		LNG = lNG;
	}
	public String getLAT() {
		return LAT;
	}
	public void setLAT(String lAT) {
		LAT = lAT;
	}
	public String getGEO_AREAID() {
		return GEO_AREAID;
	}
	public void setGEO_AREAID(String gEO_AREAID) {
		GEO_AREAID = gEO_AREAID;
	}
	public String getAVATAR_UNAUDIT() {
		return AVATAR_UNAUDIT;
	}
	public void setAVATAR_UNAUDIT(String aVATAR_UNAUDIT) {
		AVATAR_UNAUDIT = aVATAR_UNAUDIT;
	}
	public String getAVATAR_AUDITSTATUS() {
		return AVATAR_AUDITSTATUS;
	}
	public void setAVATAR_AUDITSTATUS(String aVATAR_AUDITSTATUS) {
		AVATAR_AUDITSTATUS = aVATAR_AUDITSTATUS;
	}
	public String getAVATAR_AUDITTIME() {
		return AVATAR_AUDITTIME;
	}
	public void setAVATAR_AUDITTIME(String aVATAR_AUDITTIME) {
		AVATAR_AUDITTIME = aVATAR_AUDITTIME;
	}
	public String getMESSAGE_SETTING() {
		return MESSAGE_SETTING;
	}
	public void setMESSAGE_SETTING(String mESSAGE_SETTING) {
		MESSAGE_SETTING = mESSAGE_SETTING;
	}
	public String getINVITE_NUMBER() {
		return INVITE_NUMBER;
	}
	public void setINVITE_NUMBER(String iNVITE_NUMBER) {
		INVITE_NUMBER = iNVITE_NUMBER;
	}
	public String getINVITER_ID() {
		return INVITER_ID;
	}
	public void setINVITER_ID(String iNVITER_ID) {
		INVITER_ID = iNVITER_ID;
	}
	public String getINVITER_UTYPE() {
		return INVITER_UTYPE;
	}
	public void setINVITER_UTYPE(String iNVITER_UTYPE) {
		INVITER_UTYPE = iNVITER_UTYPE;
	}
	public String getLAST_LOGINIP() {
		return LAST_LOGINIP;
	}
	public void setLAST_LOGINIP(String lAST_LOGINIP) {
		LAST_LOGINIP = lAST_LOGINIP;
	}
	public String getLAST_LOGINTIME() {
		return LAST_LOGINTIME;
	}
	public void setLAST_LOGINTIME(String lAST_LOGINTIME) {
		LAST_LOGINTIME = lAST_LOGINTIME;
	}
	public String getLOGIN_TIMES() {
		return LOGIN_TIMES;
	}
	public void setLOGIN_TIMES(String lOGIN_TIMES) {
		LOGIN_TIMES = lOGIN_TIMES;
	}
	public String getPOINTS_TOTAL() {
		return POINTS_TOTAL;
	}
	public void setPOINTS_TOTAL(String pOINTS_TOTAL) {
		POINTS_TOTAL = pOINTS_TOTAL;
	}
	public String getPOINTS_NOW() {
		return POINTS_NOW;
	}
	public void setPOINTS_NOW(String pOINTS_NOW) {
		POINTS_NOW = pOINTS_NOW;
	}
	public String getSIGN_TIMES() {
		return SIGN_TIMES;
	}
	public void setSIGN_TIMES(String sIGN_TIMES) {
		SIGN_TIMES = sIGN_TIMES;
	}
	public String getSIGN_LASTTIME() {
		return SIGN_LASTTIME;
	}
	public void setSIGN_LASTTIME(String sIGN_LASTTIME) {
		SIGN_LASTTIME = sIGN_LASTTIME;
	}
	public String getSEX() {
		return SEX;
	}
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getPROFESSIONAL() {
		return PROFESSIONAL;
	}
	public void setPROFESSIONAL(String pROFESSIONAL) {
		PROFESSIONAL = pROFESSIONAL;
	}
	public String getCARDTYPE() {
		return CARDTYPE;
	}
	public void setCARDTYPE(String cARDTYPE) {
		CARDTYPE = cARDTYPE;
	}
	public String getCARDNUMBER() {
		return CARDNUMBER;
	}
	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
	}
	public String getCARDFILE() {
		return CARDFILE;
	}
	public void setCARDFILE(String cARDFILE) {
		CARDFILE = cARDFILE;
	}
	public String getDOCTORCODE() {
		return DOCTORCODE;
	}
	public void setDOCTORCODE(String dOCTORCODE) {
		DOCTORCODE = dOCTORCODE;
	}
	public String getUPDATETIME() {
		return UPDATETIME;
	}
	public void setUPDATETIME(String uPDATETIME) {
		UPDATETIME = uPDATETIME;
	}
	public String getSPECIALITY() {
		return SPECIALITY;
	}
	public void setSPECIALITY(String sPECIALITY) {
		SPECIALITY = sPECIALITY;
	}
	public String getHOSPITALNAME() {
		return HOSPITALNAME;
	}
	public void setHOSPITALNAME(String hOSPITALNAME) {
		HOSPITALNAME = hOSPITALNAME;
	}
	public String getROOMNAME() {
		return ROOMNAME;
	}
	public void setROOMNAME(String rOOMNAME) {
		ROOMNAME = rOOMNAME;
	}
	public String getCERTIFICATE() {
		return CERTIFICATE;
	}
	public void setCERTIFICATE(String cERTIFICATE) {
		CERTIFICATE = cERTIFICATE;
	}
	public String getCERTIFICATEHOME() {
		return CERTIFICATEHOME;
	}
	public void setCERTIFICATEHOME(String cERTIFICATEHOME) {
		CERTIFICATEHOME = cERTIFICATEHOME;
	}
	public String getCERTIFICATENUM() {
		return CERTIFICATENUM;
	}
	public void setCERTIFICATENUM(String cERTIFICATENUM) {
		CERTIFICATENUM = cERTIFICATENUM;
	}
	public String getRECOMMEND() {
		return RECOMMEND;
	}
	public void setRECOMMEND(String rECOMMEND) {
		RECOMMEND = rECOMMEND;
	}
	public String getALLOWFREECONSULT() {
		return ALLOWFREECONSULT;
	}
	public void setALLOWFREECONSULT(String aLLOWFREECONSULT) {
		ALLOWFREECONSULT = aLLOWFREECONSULT;
	}
	public String getMOBILE() {
		return MOBILE;
	}
	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}
	public String getBIRTHDAY() {
		return BIRTHDAY;
	}
	public void setBIRTHDAY(String bIRTHDAY) {
		BIRTHDAY = bIRTHDAY;
	}
	public String getAUDITSTATUS() {
		return AUDITSTATUS;
	}
	public void setAUDITSTATUS(String aUDITSTATUS) {
		AUDITSTATUS = aUDITSTATUS;
	}
	public String getAUDITREFUSE() {
		return AUDITREFUSE;
	}
	public void setAUDITREFUSE(String aUDITREFUSE) {
		AUDITREFUSE = aUDITREFUSE;
	}
	public String getAUDITTIME() {
		return AUDITTIME;
	}
	public void setAUDITTIME(String aUDITTIME) {
		AUDITTIME = aUDITTIME;
	}
	public String getDOCTORSTATUS() {
		return DOCTORSTATUS;
	}
	public void setDOCTORSTATUS(String dOCTORSTATUS) {
		DOCTORSTATUS = dOCTORSTATUS;
	}
	public String getHOSPITALID() {
		return HOSPITALID;
	}
	public void setHOSPITALID(String hOSPITALID) {
		HOSPITALID = hOSPITALID;
	}
	public String getRESUME() {
		return RESUME;
	}
	public void setRESUME(String rESUME) {
		RESUME = rESUME;
	}
	public String getAREAID() {
		return AREAID;
	}
	public void setAREAID(String aREAID) {
		AREAID = aREAID;
	}
	public String getROOMPHONE() {
		return ROOMPHONE;
	}
	public void setROOMPHONE(String rOOMPHONE) {
		ROOMPHONE = rOOMPHONE;
	}
	public String getQUALIFY() {
		return QUALIFY;
	}
	public void setQUALIFY(String qUALIFY) {
		QUALIFY = qUALIFY;
	}
	public String getQUALIFYHOME() {
		return QUALIFYHOME;
	}
	public void setQUALIFYHOME(String qUALIFYHOME) {
		QUALIFYHOME = qUALIFYHOME;
	}
	public String getCHESTCARD() {
		return CHESTCARD;
	}
	public void setCHESTCARD(String cHESTCARD) {
		CHESTCARD = cHESTCARD;
	}
	public String getEMPLOYEECARD() {
		return EMPLOYEECARD;
	}
	public void setEMPLOYEECARD(String eMPLOYEECARD) {
		EMPLOYEECARD = eMPLOYEECARD;
	}
	public String getBAIKE_VISITORS() {
		return BAIKE_VISITORS;
	}
	public void setBAIKE_VISITORS(String bAIKE_VISITORS) {
		BAIKE_VISITORS = bAIKE_VISITORS;
	}
	
	public boolean isOwner() {
		return MEMBERID!= null &&MEMBERID.equals(SharePrefUtil.getString(Conast.Doctor_ID)) ? true : false;
	}
    
	public String getExistAvater() {
		return TextUtils.isEmpty(AVATAR) ? AVATAR_UNAUDIT : AVATAR;
	}
    
}
