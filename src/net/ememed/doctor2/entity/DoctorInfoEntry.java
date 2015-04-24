package net.ememed.doctor2.entity;

import java.io.Serializable;

public class DoctorInfoEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int MEMBERID;// => 88920
	private String MEMBERNAME;// => 15011721234
	private String EMAIL;//
	private String REALNAME;//朱杰锋（测试）
	private String AVATAR;// => http://www.ememed.net/uploads/avatar/20140722/avatar_88920_1406008201_QB8K8aD5mp.jpg
	private String PROFESSIONAL;  //=> 主任医师
	private String CARDTYPE;// => 0
	private String CARDNUMBER;
	private String CARDFILE;
	private String SPECIALITY;// => 保健，用药
	private String HOSPITALNAME;//=> 广州长安医院
	private String ROOMNAME;//=> 营养科
	private String CERTIFICATE;//=> http://www.ememed.net/uploads/certificate/20140722/certificate_1406009822_DcZ4h2VEb9.jpg
	private String CERTIFICATEHOME;//=> http://www.ememed.net/uploads/certificate/20140722/certificate_1406012345_c0AQuqJ7ET.jpg
	private String CERTIFICATENUM;
	private String MOBILE; //=> 15011721234
	private String SEX;//=> 1
	private String RESUME;
	
	//新加
	private String QUALIFY;		//医师资格证书反面
	private String QUALIFYHOME;	//医师资格证书正面
	private String CHESTCARD;	//胸牌
	private String EMPLOYEECARD;//工作证
	private String ROOMPHONE;	//科室电话
	
	private String AUDITSTATUS;	//资料审核状态
	private String AUDITREFUSE;	//审核失败原因
	
	private String AVATAR_UNAUDIT; 		//新上传待审核头像，审核通过后留空
	private String AVATAR_AUDITSTATUS;	//头像审核状态
	
	private String IS_ATTENTION;		//是否添加关注, 
	private String ALLOWFREECONSULT;	
	private String share_url; // => http://gateway20/normal/baike/home/5226
	
	public String getShare_url() {
		return share_url;
	}
	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}
	public String getALLOWFREECONSULT() {
		return ALLOWFREECONSULT;
	}
	public void setALLOWFREECONSULT(String aLLOWFREECONSULT) {
		ALLOWFREECONSULT = aLLOWFREECONSULT;
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
	public String getAUDITREFUSE() {
		return AUDITREFUSE;
	}
	public void setAUDITREFUSE(String aUDITREFUSE) {
		AUDITREFUSE = aUDITREFUSE;
	}
	public String getAUDITSTATUS() {
		return AUDITSTATUS;
	}
	public void setAUDITSTATUS(String aUDITSTATUS) {
		AUDITSTATUS = aUDITSTATUS;
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
	public String getROOMPHONE() {
		return ROOMPHONE;
	}
	public void setROOMPHONE(String rOOMPHONE) {
		ROOMPHONE = rOOMPHONE;
	}
	public int getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(int mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getMEMBERNAME() {
		return MEMBERNAME;
	}
	public void setMEMBERNAME(String mEMBERNAME) {
		MEMBERNAME = mEMBERNAME;
	}
	public String getEMAIL() {
		return EMAIL;
	}
	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}
	public String getREALNAME() {
		return REALNAME;
	}
	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
	public String getAVATAR() {
		return AVATAR;
	}
	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
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
	public String getMOBILE() {
		return MOBILE;
	}
	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}
	public String getSEX() {
		return SEX;
	}
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getCERTIFICATEHOME() {
		return CERTIFICATEHOME;
	}
	public void setCERTIFICATEHOME(String cERTIFICATEHOME) {
		CERTIFICATEHOME = cERTIFICATEHOME;
	}
	public String getRESUME() {
		return RESUME;
	}
	public void setRESUME(String rESUME) {
		RESUME = rESUME;
	}
	public String getIS_ATTENTION() {
		return IS_ATTENTION;
	}
	public void setIS_ATTENTION(String iS_ATTENTION) {
		IS_ATTENTION = iS_ATTENTION;
	}
	
}
