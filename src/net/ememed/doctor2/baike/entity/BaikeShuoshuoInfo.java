package net.ememed.doctor2.baike.entity;

import java.io.Serializable;
import java.util.List;

public class BaikeShuoshuoInfo implements Serializable{
	 private String SAYSID; // => 33
     private String TITLE; // => 测试医生说说
     private String CONTENT; // => 测试医生说说的内容
     private String CONTENT_SHOW; 
     private String DOCTORID; // => 5226
     private String HITS; // => 0
     private String PRAISE_NUM; // => 0
     private String COMMENT_NUM; // => 0
     private String STATUS; // => 1
     private String CREATE_TIME; // => 2015-01-23 15:41:20
     private String UPDATE_TIME; // => 2015-01-23 15:41:20
     private String RNUM; // => 1
     private String IS_NEW; // => 1
     private boolean IS_PRAISED;
     private String SHARE_COUNT;
     private String REALNAME;
     
     private List<String> PICS;
     private List<String> PICS_THUMB;
     
 	private String SHARE_URL; // => http://gateway20/normal/baike/says/5228/31
 	private String DETAIL_URL; // => http://gateway20/normal/baike/says_no_comment/5228/31
 	private SaysCommentDetai COMMENT_LIST;  // => Array
 	
	public String getSHARE_URL() {
		return SHARE_URL;
	}

	public void setSHARE_URL(String sHARE_URL) {
		SHARE_URL = sHARE_URL;
	}

	public String getDETAIL_URL() {
		return DETAIL_URL;
	}

	public void setDETAIL_URL(String dETAIL_URL) {
		DETAIL_URL = dETAIL_URL;
	}

	public SaysCommentDetai getCOMMENT_LIST() {
		return COMMENT_LIST;
	}

	public void setCOMMENT_LIST(SaysCommentDetai cOMMENT_LIST) {
		COMMENT_LIST = cOMMENT_LIST;
	}

	public String getREALNAME() {
		return REALNAME;
	}

	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}

	public String getSHARE_COUNT() {
		return SHARE_COUNT;
	}

	public void setSHARE_COUNT(String sHARE_COUNT) {
		SHARE_COUNT = sHARE_COUNT;
	}

	public String getCONTENT_SHOW() {
		return CONTENT_SHOW;
	}

	public void setCONTENT_SHOW(String cONTENT_SHOW) {
		CONTENT_SHOW = cONTENT_SHOW;
	}

	public boolean getIS_PRAISED() {
		return IS_PRAISED;
	}

	public void setIS_PRAISED(boolean iS_PRAISED) {
		IS_PRAISED = iS_PRAISED;
	}

	public String getSAYSID() {
		return SAYSID;
	}

	public void setSAYSID(String sAYSID) {
		SAYSID = sAYSID;
	}

	public String getTITLE() {
		return TITLE;
	}

	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}

	public String getCONTENT() {
		return CONTENT;
	}

	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}

	public String getDOCTORID() {
		return DOCTORID;
	}

	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
	}

	public String getHITS() {
		return HITS;
	}

	public void setHITS(String hITS) {
		HITS = hITS;
	}

	public String getPRAISE_NUM() {
		return PRAISE_NUM;
	}

	public void setPRAISE_NUM(String pRAISE_NUM) {
		PRAISE_NUM = pRAISE_NUM;
	}

	public String getCOMMENT_NUM() {
		return COMMENT_NUM;
	}

	public void setCOMMENT_NUM(String cOMMENT_NUM) {
		COMMENT_NUM = cOMMENT_NUM;
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

	public String getRNUM() {
		return RNUM;
	}

	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}

	public String getIS_NEW() {
		return IS_NEW;
	}

	public void setIS_NEW(String iS_NEW) {
		IS_NEW = iS_NEW;
	}

	public List<String> getPICS() {
		return PICS;
	}

	public void setPICS(List<String> pICS) {
		PICS = pICS;
	}

	public List<String> getPICS_THUMB() {
		return PICS_THUMB;
	}

	public void setPICS_THUMB(List<String> pICS_THUMB) {
		PICS_THUMB = pICS_THUMB;
	}
}
