package net.ememed.doctor2.baike.entity;

import net.ememed.doctor2.entity.DoctorInfoEntry;
import net.ememed.doctor2.entity.MemberInfo;

public class MyAttentionInfo {
	private String ATTENTIONID;  // => 2
    private String MEMBERID;  // => 5226
    private String UTYPE;  // => doctor
    private String OTHER_MEMBERID;  // => 3551
    private String OTHER_UTYPE;  // => doctor
    private String READ_STATUS;  // => 0
    private String CREATE_TIME;  // => 2015-01-23 17:23:12
    private String UPDATE_TIME;  // => 2015-01-23 17:23:12
    private String HAVE_NEW_MSG;  // => 0
    private String RNUM;  // => 1
    private DoctorInfoEntry  DOCTORINFO;
    
    
	public DoctorInfoEntry getDOCTORINFO() {
		return DOCTORINFO;
	}
	public void setDOCTORINFO(DoctorInfoEntry dOCTORINFO) {
		DOCTORINFO = dOCTORINFO;
	}
	public String getATTENTIONID() {
		return ATTENTIONID;
	}
	public void setATTENTIONID(String aTTENTIONID) {
		ATTENTIONID = aTTENTIONID;
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
	public String getOTHER_MEMBERID() {
		return OTHER_MEMBERID;
	}
	public void setOTHER_MEMBERID(String oTHER_MEMBERID) {
		OTHER_MEMBERID = oTHER_MEMBERID;
	}
	public String getOTHER_UTYPE() {
		return OTHER_UTYPE;
	}
	public void setOTHER_UTYPE(String oTHER_UTYPE) {
		OTHER_UTYPE = oTHER_UTYPE;
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
	public String getHAVE_NEW_MSG() {
		return HAVE_NEW_MSG;
	}
	public void setHAVE_NEW_MSG(String hAVE_NEW_MSG) {
		HAVE_NEW_MSG = hAVE_NEW_MSG;
	}
	public String getRNUM() {
		return RNUM;
	}
	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}
}
