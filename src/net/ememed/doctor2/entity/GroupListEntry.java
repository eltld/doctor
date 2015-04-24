package net.ememed.doctor2.entity;

import java.io.Serializable;

public class GroupListEntry implements Serializable{

	private String GROUPID;// => 6
	private String GROUPNAME;// => 群组1
	private String GROUPDESC;// => 测试群组功能创建
	private String GROUPNUM;// => 49681731
	private String LOGO;// =>
	private String ISPUBLIC;// => 1
	private String APPROVAL;// => 1
	private String MEMBER_LIMIT;// => 10
	private String TAGS;// => 群组标签1
	private String IM_GROUPID;// => 1411976974236171
	private String OWNERID;// => 5226
	private String OWNERNAME;// => 刘宇萍(薏米)
	
	public String getGROUPID() {
		return GROUPID;
	}
	public void setGROUPID(String gROUPID) {
		GROUPID = gROUPID;
	}
	public String getGROUPNAME() {
		return GROUPNAME;
	}
	public void setGROUPNAME(String gROUPNAME) {
		GROUPNAME = gROUPNAME;
	}
	public String getGROUPDESC() {
		return GROUPDESC;
	}
	public void setGROUPDESC(String gROUPDESC) {
		GROUPDESC = gROUPDESC;
	}
	public String getGROUPNUM() {
		return GROUPNUM;
	}
	public void setGROUPNUM(String gROUPNUM) {
		GROUPNUM = gROUPNUM;
	}
	public String getLOGO() {
		return LOGO;
	}
	public void setLOGO(String lOGO) {
		LOGO = lOGO;
	}
	public String getISPUBLIC() {
		return ISPUBLIC;
	}
	public void setISPUBLIC(String iSPUBLIC) {
		ISPUBLIC = iSPUBLIC;
	}
	public String getAPPROVAL() {
		return APPROVAL;
	}
	public void setAPPROVAL(String aPPROVAL) {
		APPROVAL = aPPROVAL;
	}
	public String getMEMBER_LIMIT() {
		return MEMBER_LIMIT;
	}
	public void setMEMBER_LIMIT(String mEMBER_LIMIT) {
		MEMBER_LIMIT = mEMBER_LIMIT;
	}
	public String getTAGS() {
		return TAGS;
	}
	public void setTAGS(String tAGS) {
		TAGS = tAGS;
	}
	public String getIM_GROUPID() {
		return IM_GROUPID;
	}
	public void setIM_GROUPID(String iM_GROUPID) {
		IM_GROUPID = iM_GROUPID;
	}
	public String getOWNERID() {
		return OWNERID;
	}
	public void setOWNERID(String oWNERID) {
		OWNERID = oWNERID;
	}
	public String getOWNERNAME() {
		return OWNERNAME;
	}
	public void setOWNERNAME(String oWNERNAME) {
		OWNERNAME = oWNERNAME;
	}
	
}
