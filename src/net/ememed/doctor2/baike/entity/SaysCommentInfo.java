package net.ememed.doctor2.baike.entity;

import java.io.Serializable;

public class SaysCommentInfo implements Serializable{
	private String COMMENTID; // => 2
	private String SAYSID; // => 31
	private String DOCTORID; // => 5228
	private String MEMBERID; // => 5228
	private String UTYPE; // => doctor
	private String CONTENT; // => 还好
	private String REFER_COMMENTID; // => 
	private String ANONYMOUS; // => 0
	private String STATUS; // => 1
	private String CREATE_TIME; // => 2015-01-23 16:43:24
	private String UPDATE_TIME; // => 2015-01-23 16:43:24
	private String READ_STATUS; // => 1
	private String REALNAME; // => 
	private String AVATAR; // => 
	
	private ReferCommentInfo REFER_COMMENT;
	
	public String getAVATAR() {
		return AVATAR;
	}
	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}
	public String getCOMMENTID() {
		return COMMENTID;
	}
	public void setCOMMENTID(String cOMMENTID) {
		COMMENTID = cOMMENTID;
	}
	public String getSAYSID() {
		return SAYSID;
	}
	public void setSAYSID(String sAYSID) {
		SAYSID = sAYSID;
	}
	public String getDOCTORID() {
		return DOCTORID;
	}
	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
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
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getREFER_COMMENTID() {
		return REFER_COMMENTID;
	}
	public void setREFER_COMMENTID(String rEFER_COMMENTID) {
		REFER_COMMENTID = rEFER_COMMENTID;
	}
	public String getANONYMOUS() {
		return ANONYMOUS;
	}
	public void setANONYMOUS(String aNONYMOUS) {
		ANONYMOUS = aNONYMOUS;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
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
	public String getREAD_STATUS() {
		return READ_STATUS;
	}
	public void setREAD_STATUS(String rEAD_STATUS) {
		READ_STATUS = rEAD_STATUS;
	}
	public String getREALNAME() {
		return REALNAME;
	}
	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
	public ReferCommentInfo getREFER_COMMENT() {
		return REFER_COMMENT;
	}
	public void setREFER_COMMENT(ReferCommentInfo rEFER_COMMENT) {
		REFER_COMMENT = rEFER_COMMENT;
	}
	
	
}
