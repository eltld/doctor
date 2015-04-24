package net.ememed.doctor2.entity;

import java.util.List;

public class GroupDetailData {
	private String GROUPID;					//群id
    private String GROUPNAME;				//群名称
    private String MEMBERID;				//创建者id
    private String STATUS; 					// => 1
    private String CREATETIME;				//群创建时间
    private String DISMISSTIME;				//群解散时间 
    private String GROUPNUM;				//薏米群组的群号
    private String LOGO;					//群LOGO地址
    private String GROUPDESC;				//群组描述
    private String ISPUBLIC;				//是否是公开群, 1：公开 0：不公开
    private String APPROVAL;				//加入公开群是否需要批准 1：需要批准 0：不需要
    private String MEMBER_LIMIT;			//群人数限制
    private String TAGS;					//群标签
    private String IM_GROUPID; 				//环信群Id
    
    private GroupMemberList member_list;	//成员列表

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

	public String getMEMBERID() {
		return MEMBERID;
	}

	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getCREATETIME() {
		return CREATETIME;
	}

	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}

	public String getDISMISSTIME() {
		return DISMISSTIME;
	}

	public void setDISMISSTIME(String dISMISSTIME) {
		DISMISSTIME = dISMISSTIME;
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

	public String getGROUPDESC() {
		return GROUPDESC;
	}

	public void setGROUPDESC(String gROUPDESC) {
		GROUPDESC = gROUPDESC;
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

	public GroupMemberList getMember_list() {
		return member_list;
	}

	public void setMember_list(GroupMemberList member_list) {
		this.member_list = member_list;
	}
}
