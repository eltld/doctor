package net.ememed.doctor2.baike.entity;

import java.io.Serializable;

public class BaikeMemberInfo implements Serializable{
	 private String MEMBERID;  //107936private String ,
     private String MEMBERNAME;  //emfYwYguB7private String ,
     private String PASSWORD;  //e10adc3949ba59abbe56e057f20f883eprivate String ,
     private String UTYPE;  //doctorprivate String ,
     private String EMAIL;  //hhgffghgg@hhh.comprivate String ,
     private String EMAILSTATUS;  //0private String ,
     private String STATUS;  //-3private String ,
     private String REALNAME;  //李良杰2（薏米）private String ,
     private String REGTIME;  //2014-12-11 11:12:06private String ,
     private String INCOMEMONEY;  //0private String ,
     private String USERMONEY;  //9681private String ,
     private String FREEZEMONEY;  //1429.41private String ,
     private String NEWPM;  //0private String ,
     private String AVATAR;  //null,
     private String PAYPASSWORD;  //e10adc3949ba59abbe56e057f20f883eprivate String ,
     private String ISTESTER;  //1private String ,
     private String OPENID;  //null,
     private String SINAOPENID;  //null,
     private String REG_CHANNEL;  //androidprivate String ,
     private String LAST_CHANNEL;  //androidprivate String ,
     private String BOSSTEST;  //0private String ,
     private String TOKEN;  //9f4f2b5228cc65448e053e2a18bfb807private String ,
     private String LNG;  //null,
     private String LAT;  //null,
     private String GEO_AREAID;  //null,
     private String AVATAR_UNAUDIT;  //http://www.ememed.net/uploads/avatar/20150130/avatar_107936_1422605319_opVegyq76Q.jpgprivate String ,
     private String AVATAR_AUDITSTATUS;  //-1private String ,
     private String AVATAR_AUDITTIME;  //null,
     private String MESSAGE_SETTING;  //{\private String SMS_ORDER\private String :\private String 0\private String ,\private String SMS_APPLY_CASH\private String :\private String 0\private String ,\private String SMS_ACTIVITY\private String :\private String 1\private String }private String ,
     private String SEX;  //1private String ,
     private String PROFESSIONAL;  //主管药师private String ,
     private String CARDTYPE;  //null,
     private String CARDNUMBER;  //null,
     private String CARDFILE;  //null,
     private String DOCTORCODE;  //null,
     private String SPECIALITY;  //啦啦啦啦啦private String ,
     private String HOSPITALNAME;  //测试private String ,
     private String ROOMNAME;  //心理咨询与治疗门诊private String ,
     private String CERTIFICATE;  //http://www.ememed.net/uploads/certificate/20150130/certificate_107936_1422608972_OCSmz4jiAE.jpgprivate String ,
     private String CERTIFICATEHOME;  //http://www.ememed.net/uploads/certificate/20150130/certificate_107936_1422609016_D6faWnaIjc.jpgprivate String ,
     private String CERTIFICATENUM;  //null,
     private String RECOMMEND;  //0private String ,
     private String ALLOWFREECONSULT;  //1private String ,
     private String MOBILE;  //18500000001private String ,
     private String BIRTHDAY;  //null,
     private String AUDITSTATUS;  //1private String ,
     private String AUDITREFUSE;  //null,
     private String AUDITTIME;  //2014-12-11 11:39:19private String ,
     private String DOCTORSTATUS;  //1private String ,
     private String HOSPITALID;  //null,
     private String RESUME;  //他他36没课了是啊啊啊啊啊啊啊啊啊啊啊啊啊.private String ,
     private String AREAID;  //null,
     private String ROOMPHONE;  //66666666private String ,
     private String QUALIFY;  //null,
     private String QUALIFYHOME;  //null,
     private String CHESTCARD;  //null,
     private String EMPLOYEECARD;  //null,
	
	private String bounty_total_money; // => 0
    private int bounty_unread_num; // => 0
    private String attention_total_num; // => 1
    private int attention_unread_num; // => 1
   
    private int fans_total_num; // => 2
    private int fans_unread_num; // => 2
    private int fans_user_total_num;// => 1
    private int fans_user_unread_num; //=> 0
    private int fans_doctor_total_num; //=> 1
    private int fans_doctor_unread_num; //=>
    
    private String share_url; // => http://gateway20/normal/baike/home/5226
    
    private String IS_ATTENTION;
    
    private String BAIKE_VISITORS;
    private String baike_visitors_today;
    
    
    public int getFans_user_total_num() {
		return fans_user_total_num;
	}
	public void setFans_user_total_num(int fans_user_total_num) {
		this.fans_user_total_num = fans_user_total_num;
	}
	public int getFans_user_unread_num() {
		return fans_user_unread_num;
	}
	public void setFans_user_unread_num(int fans_user_unread_num) {
		this.fans_user_unread_num = fans_user_unread_num;
	}
	public int getFans_doctor_total_num() {
		return fans_doctor_total_num;
	}
	public void setFans_doctor_total_num(int fans_doctor_total_num) {
		this.fans_doctor_total_num = fans_doctor_total_num;
	}
	public int getFans_doctor_unread_num() {
		return fans_doctor_unread_num;
	}
	public void setFans_doctor_unread_num(int fans_doctor_unread_num) {
		this.fans_doctor_unread_num = fans_doctor_unread_num;
	}
    
	public String getBAIKE_VISITORS() {
		return BAIKE_VISITORS;
	}
	public void setBAIKE_VISITORS(String bAIKE_VISITORS) {
		BAIKE_VISITORS = bAIKE_VISITORS;
	}
	
	public String getBaike_visitors_today() {
		return baike_visitors_today;
	}
	public void setBaike_visitors_today(String baike_visitors_today) {
		this.baike_visitors_today = baike_visitors_today;
	}
	public String getIS_ATTENTION() {
		return IS_ATTENTION;
	}
	public void setIS_ATTENTION(String iS_ATTENTION) {
		IS_ATTENTION = iS_ATTENTION;
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
	public String getPASSWORD() {
		return PASSWORD;
	}
	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
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
	public String getBounty_total_money() {
		return bounty_total_money;
	}
	public void setBounty_total_money(String bounty_total_money) {
		this.bounty_total_money = bounty_total_money;
	}
	public int getBounty_unread_num() {
		return bounty_unread_num;
	}
	public void setBounty_unread_num(int bounty_unread_num) {
		this.bounty_unread_num = bounty_unread_num;
	}

	public int getAttention_unread_num() {
		return attention_unread_num;
	}
	public void setAttention_unread_num(int attention_unread_num) {
		this.attention_unread_num = attention_unread_num;
	}
	
	public String getAttention_total_num() {
		return attention_total_num;
	}
	public void setAttention_total_num(String attention_total_num) {
		this.attention_total_num = attention_total_num;
	}
	public int getFans_total_num() {
		return fans_total_num;
	}
	public void setFans_total_num(int fans_total_num) {
		this.fans_total_num = fans_total_num;
	}
	public int getFans_unread_num() {
		return fans_unread_num;
	}
	public void setFans_unread_num(int fans_unread_num) {
		this.fans_unread_num = fans_unread_num;
	}
	public String getShare_url() {
		return share_url;
	}
	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

    
    
    	
    	
    	//多余数据暂时不要
			/*[MEMBERID] => 5226
            [MEMBERNAME] => liuyuping
            [PASSWORD] => 33374c88e3a7570c211c203b4795fc24
            [UTYPE] => doctor
            [EMAIL] => 
            [EMAILSTATUS] => 0
            [STATUS] => 1
            [REALNAME] => 刘宇萍(薏米)
            [REGTIME] => 2013-05-02 15:40:41
            [INCOMEMONEY] => 297.92
            [USERMONEY] => 11209.1
            [FREEZEMONEY] => 70041.03
            [NEWPM] => 0
            [AVATAR] => http://www.ememed.net/uploads/avatar/20140729/avatar_5226_1406622921_heGQuhDe6r.jpg
            [PAYPASSWORD] => e10adc3949ba59abbe56e057f20f883e
            [ISTESTER] => 0
            [OPENID] => 
            [SINAOPENID] => 
            [REG_CHANNEL] => 诺和诺德
            [LAST_CHANNEL] => ios
            [BOSSTEST] => 1
            [TOKEN] => 5b0a103062eff7d70c5c8e4eca7f565f
            [SINGAOPENID] => 
            [LASTCHANNEL] => 
            [LNG] => 
            [LAT] => 
            [GEO_AREAID] => 
            [AVATAR_UNAUDIT] => 
            [AVATAR_AUDITSTATUS] => 1
            [AVATAR_AUDITTIME] => 2014-12-02 14:12:49
            [MESSAGE_SETTING] => {"SMS_ORDER":"1","SMS_APPLY_CASH":1,"SMS_ACTIVITY":1}
            [SEX] => 1
            [PROFESSIONAL] => 主任医师
            [CARDTYPE] => 01
            [CARDNUMBER] => 430424198808276843
            [CARDFILE] => http://www.ememed.net/uploads/doctor/20140217/doctor_1392612923_v8Hu2Jkvt2.jpg
            [DOCTORCODE] => 
            [SPECIALITY] => 我所擅长的
            [HOSPITALNAME] => 广东省第一中医院
            [ROOMNAME] => 好科室
            [CERTIFICATE] => http://www.ememed.net/uploads/doctor/20140729/certificate_1406622634_YbBKfP1stS.jpg
            [CERTIFICATEHOME] => http://www.ememed.net/uploads/certificate/20140729/certificate_1406622976_mF9uLJ5k5O.jpg
            [CERTIFICATENUM] => 020544562533
            [RECOMMEND] => 0
            [ALLOWFREECONSULT] => 0
            [MOBILE] => 61
            [BIRTHDAY] => 
            [AUDITSTATUS] => 1
            [AUDITREFUSE] => 
            [AUDITTIME] => 
            [DOCTORSTATUS] => 2
            [HOSPITALID] => 288
            [RESUME] => 我来做简介
            [AREAID] => 3040
            [ROOMPHONE] => 020-112222
            [QUALIFY] => 
            [QUALIFYHOME] => 
            [CHESTCARD] => 
            [EMPLOYEECARD] => 
            [AREA] => 广东省广州市天河区*/
           
}
