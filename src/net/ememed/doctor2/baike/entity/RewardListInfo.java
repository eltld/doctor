package net.ememed.doctor2.baike.entity;

import java.io.Serializable;

public class RewardListInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String 	BOUNTYID; //打赏记录ID
	private String 	ORDERID;  //订单ID
	private String 	DOCTORID; //受打赏的医生ID
	private String 	RECORD_TYPE; //关联的类型（1说说，可扩展其它类型）
	private String 	RECORDID; //关联的ID（如说说ID）
	private String 	MEMBERID; //打赏人ID
	private String 	UTYPE;  //打赏人类型 doctor|user
	private String 	MONEY;  //打赏金额
	private String 	CONTENT;  //留言内容
	private String 	READ_STATUS; //阅读状态（0未读，1已读）
	private String 	CREATE_TIME; //创建时间
	private String 	UPDATE_TIME; //更新时间
	private String  AVATAR;
	private String  REALNAME;
	private String  ISMYFANS;
	
	public String getBOUNTYID() {
		return BOUNTYID;
	}
	public void setBOUNTYID(String bOUNTYID) {
		BOUNTYID = bOUNTYID;
	}
	public String getORDERID() {
		return ORDERID;
	}
	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}
	public String getDOCTORID() {
		return DOCTORID;
	}
	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
	}
	public String getRECORD_TYPE() {
		return RECORD_TYPE;
	}
	public void setRECORD_TYPE(String rECORD_TYPE) {
		RECORD_TYPE = rECORD_TYPE;
	}
	public String getRECORDID() {
		return RECORDID;
	}
	public void setRECORDID(String rECORDID) {
		RECORDID = rECORDID;
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
	public String getMONEY() {
		return MONEY;
	}
	public void setMONEY(String mONEY) {
		MONEY = mONEY;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getREAD_STATUS() {
		return READ_STATUS;
	}
	public void setREAD_STATUS(String rEAD_STATUS) {
		READ_STATUS = rEAD_STATUS;
	}
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}
	public void setUPDATE_TIME(String uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}
	public String getAVATAR() {
		return AVATAR;
	}
	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}
	public String getREALNAME() {
		return REALNAME;
	}
	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
	public String getISMYFANS() {
		return ISMYFANS;
	}
	public void setISMYFANS(String iSMYFANS) {
		ISMYFANS = iSMYFANS;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
