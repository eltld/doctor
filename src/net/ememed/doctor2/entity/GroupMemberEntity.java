package net.ememed.doctor2.entity;

public class GroupMemberEntity {
	 private String MEMBERID;// => 98828
     private String CREATETIME;// => 1427359817
     private String STATUS;// => 1
     private HospitalInfo HOSPITAL_INFO;
     private GroupMemberInfo MEMBERINFO; // => Array
     
     public boolean isButton; //业务使用属性，非button
     
     public GroupMemberEntity() {
 	}
     
	public GroupMemberEntity(String avater) {
		isButton = true;
		MEMBERINFO = new GroupMemberInfo(avater);
	}
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public GroupMemberInfo getMEMBERINFO() {
		return MEMBERINFO;
	}
	public void setMEMBERINFO(GroupMemberInfo mEMBERINFO) {
		MEMBERINFO = mEMBERINFO;
	}
	public HospitalInfo getHOSPITAL_INFO() {
		return HOSPITAL_INFO;
	}
	public void setHOSPITAL_INFO(HospitalInfo hOSPITAL_INFO) {
		HOSPITAL_INFO = hOSPITAL_INFO;
	}
}
