package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

public class PatientsUserBean implements Serializable{
	private String ID;
	private String MEMBERID;
	private String NOTE_NAME;
	private String DESCRIPTION;
	private String IS_STAR;
	private String UPDATE_TIME;
	private String HAVEORDER;
	private String MSG_ID;
	private String TYPE;
	private String CONTENT;
	private PatientsUserExtBean EXT;
    private String SENDTIME;
    private String ISSYSTEMMSG;
    private ContentExtBean CONTENT_EXT;
    private String AVATAR;
    private String UTYPE;
    private String REALNAME;
	public String getID() {
		return ID;
	}
	public String getMEMBERID() {
		return MEMBERID;
	}
	public String getNOTE_NAME() {
		return NOTE_NAME;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public String getIS_STAR() {
		return IS_STAR;
	}
	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}
	public String getHAVEORDER() {
		return HAVEORDER;
	}
	public String getMSG_ID() {
		return MSG_ID;
	}
	public String getTYPE() {
		return TYPE;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public List<PatientsUserExtBean> getEXT() {
		return (List<PatientsUserExtBean>) EXT;
	}
	public String getSENDTIME() {
		return SENDTIME;
	}
	public String getISSYSTEMMSG() {
		return ISSYSTEMMSG;
	}
	public ContentExtBean getCONTENT_EXT() {
		return CONTENT_EXT;
	}
	public String getAVATAR() {
		return AVATAR;
	}
	public String getUTYPE() {
		return UTYPE;
	}
	public String getREALNAME() {
		return REALNAME;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public void setNOTE_NAME(String nOTE_NAME) {
		NOTE_NAME = nOTE_NAME;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public void setIS_STAR(String iS_STAR) {
		IS_STAR = iS_STAR;
	}
	public void setUPDATE_TIME(String uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}
	public void setHAVEORDER(String hAVEORDER) {
		HAVEORDER = hAVEORDER;
	}
	public void setMSG_ID(String mSG_ID) {
		MSG_ID = mSG_ID;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public void setEXT(PatientsUserExtBean eXT) {
		EXT = eXT;
	}
	public void setSENDTIME(String sENDTIME) {
		SENDTIME = sENDTIME;
	}
	public void setISSYSTEMMSG(String iSSYSTEMMSG) {
		ISSYSTEMMSG = iSSYSTEMMSG;
	}
	public void setCONTENT_EXT(ContentExtBean cONTENT_EXT) {
		CONTENT_EXT = cONTENT_EXT;
	}
	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}
	public void setUTYPE(String uTYPE) {
		UTYPE = uTYPE;
	}
	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
}
