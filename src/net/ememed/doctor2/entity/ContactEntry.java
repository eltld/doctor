package net.ememed.doctor2.entity;


import java.io.Serializable;

public class ContactEntry implements Serializable{

	private String MEMBERID;
	private String HAVEORDER;
	private String MSG_ID;
	private String TYPE;
	private String CONTENT;
	
//	EXT
//	CONTENT_EXT
	
	private String SENDTIME;
	private String ISSYSTEMMSG;
	private String AVATAR;
	private String UTYPE;
	private String PROFESSIONAL;
	private String HOSPITALNAME;
	private String REALNAME;
	private String MEMBERNAME;
	
	private String HAS_NEW_MES;
	private int NEW_MES_NUM;
	
	private String IS_ATTENTION;
    private String IS_MY_FANS;
    
    private String IS_STAR; //标星（1是，0否）
    private String NOTE_NAME; //备注姓名
    private String DESCRIPTION; //患者描述
    private String GROUPID; //患者分组ID
    
    
	
	public String getIS_ATTENTION() {
		return IS_ATTENTION;
	}
	public void setIS_ATTENTION(String iS_ATTENTION) {
		IS_ATTENTION = iS_ATTENTION;
	}
	public String getIS_MY_FANS() {
		return IS_MY_FANS;
	}
	public void setIS_MY_FANS(String iS_MY_FANS) {
		IS_MY_FANS = iS_MY_FANS;
	}
	public String getHAS_NEW_MES() {
		return HAS_NEW_MES;
	}
	public void setHAS_NEW_MES(String hAS_NEW_MES) {
		HAS_NEW_MES = hAS_NEW_MES;
	}
	public int getNEW_MES_NUM() {
		return NEW_MES_NUM;
	}
	public void setNEW_MES_NUM(int nEW_MES_NUM) {
		NEW_MES_NUM = nEW_MES_NUM;
	}
	
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getHAVEORDER() {
		return HAVEORDER;
	}
	public void setHAVEORDER(String hAVEORDER) {
		HAVEORDER = hAVEORDER;
	}
	public String getMSG_ID() {
		return MSG_ID;
	}
	public void setMSG_ID(String mSG_ID) {
		MSG_ID = mSG_ID;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getSENDTIME() {
		return SENDTIME;
	}
	public void setSENDTIME(String sENDTIME) {
		SENDTIME = sENDTIME;
	}
	public String getISSYSTEMMSG() {
		return ISSYSTEMMSG;
	}
	public void setISSYSTEMMSG(String iSSYSTEMMSG) {
		ISSYSTEMMSG = iSSYSTEMMSG;
	}
	public String getAVATAR() {
		return AVATAR;
	}
	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}
	public String getUTYPE() {
		return UTYPE;
	}
	public void setUTYPE(String uTYPE) {
		UTYPE = uTYPE;
	}
	public String getPROFESSIONAL() {
		return PROFESSIONAL;
	}
	public void setPROFESSIONAL(String pROFESSIONAL) {
		PROFESSIONAL = pROFESSIONAL;
	}
	public String getHOSPITALNAME() {
		return HOSPITALNAME;
	}
	public void setHOSPITALNAME(String hOSPITALNAME) {
		HOSPITALNAME = hOSPITALNAME;
	}
	public String getREALNAME() {
		return REALNAME;
	}
	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
	public String getMEMBERNAME() {
		return MEMBERNAME;
	}
	public void setMEMBERNAME(String mEMBERNAME) {
		MEMBERNAME = mEMBERNAME;
	}
	public String getIS_STAR() {
		return IS_STAR;
	}
	public String getNOTE_NAME() {
		return NOTE_NAME;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public String getGROUPID() {
		return GROUPID;
	}
	public void setIS_STAR(String iS_STAR) {
		IS_STAR = iS_STAR;
	}
	public void setNOTE_NAME(String nOTE_NAME) {
		NOTE_NAME = nOTE_NAME;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public void setGROUPID(String gROUPID) {
		GROUPID = gROUPID;
	}
	
}
