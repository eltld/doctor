package net.ememed.doctor2.entity;

import net.ememed.doctor2.R;

public class LoginEntry {
	private String MEMBERID;
	private String MEMBERNAME;
	private String UTYPE;
	
	private String EMAIL;
	private String EMAILSTATUS;
	private String STATUS;
	private String REALNAME;
	private String REGTIME;
	private String INCOMEMONEY;
	private String USERMONEY;
	private String FREEZEMONEY;
	private String NEWPM;
	private String AVATAR;
	private String AVATAR_UNAUDIT;
	
	private String PAYPASSWORD;
	
	private String ISTESTER;
	private String OPENID;
	private String SINAOPENID;
	private String REG_CHANNEL;
	private String LAST_CHANNEL;
	
	private String BOSSTEST;
	private String TOKEN;
	private String PROFESSIONAL;
	private String CARDTYPE;
	private String CARDNUMBER;
	
	private String CARDFILE;
	private String DOCTORCODE;
	private String SPECIALITY;
	private String HOSPITALNAME;
	private String ROOMNAME;
	private String CERTIFICATE;
	private String CERTIFICATENUM;
	private String CERTIFICATEHOME;
	
	private String RECOMMEND;
	private String ALLOWFREECONSULT;
	private String MOBILE;
	private String BIRTHDAY;
	/**资质审核状态0=未上传，1=审核通过，2=待审，3=有误*/
	private String AUDITSTATUS;
	/**资质审核失败原因*/
	private String AUDITREFUSE;
	private String AUDITTIME;
	/**服务开通信息**/
	private ServiceSettingEntry service_setting;
	
	
	public String getAVATAR_UNAUDIT() {
		return AVATAR_UNAUDIT;
	}
	public void setAVATAR_UNAUDIT(String aVATAR_UNAUDIT) {
		AVATAR_UNAUDIT = aVATAR_UNAUDIT;
	}
	public String getAUDITTIME() {
		return AUDITTIME;
	}
	public void setAUDITTIME(String aUDITTIME) {
		AUDITTIME = aUDITTIME;
	}
	public ServiceSettingEntry getService_setting() {
		return service_setting;
	}
	public void setService_setting(ServiceSettingEntry service_setting) {
		this.service_setting = service_setting;
	}
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getMEMBERNAME() {
		return MEMBERNAME;
	}
	public void setMEMBERNAME(String mEMBERNAME) {
		MEMBERNAME = mEMBERNAME;
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
	public String getPAYPASSWORD() {
		return PAYPASSWORD;
	}
	public void setPAYPASSWORD(String pAYPASSWORD) {
		PAYPASSWORD = pAYPASSWORD;
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
	public String getCERTIFICATEHOME() {
		return CERTIFICATEHOME;
	}
	public void setCERTIFICATEHOME(String cERTIFICATEHOME) {
		CERTIFICATEHOME = cERTIFICATEHOME;
	}
	
}
