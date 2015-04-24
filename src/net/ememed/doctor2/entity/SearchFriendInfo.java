package net.ememed.doctor2.entity;

import java.io.Serializable;

public class SearchFriendInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String MEMBERID;  // => 55096
    private String AVATAR;  // => 
    private String REALNAME;  // => 陈力
    private String GEO_AREAID;  // => 0
    private String HOSPITALID;  // => 729
    private String AREAID;  // => 2152
    private String RNUM;  // => 10
    private String SAME_HOSPITAL;  // => 0
    private String SAME_AREA;  // => 0
    private String SAME_GEO_AREA;  // => 0
    private String IS_BOOK_FRIEND;  // => 0
    private String FRIENDID;  // => 0
    private DoctorInfoEntry DOCTOR_INFO;
    private HospitalInfo HOSPITAL_INFO;
    
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
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
	public String getGEO_AREAID() {
		return GEO_AREAID;
	}
	public void setGEO_AREAID(String gEO_AREAID) {
		GEO_AREAID = gEO_AREAID;
	}
	public String getHOSPITALID() {
		return HOSPITALID;
	}
	public void setHOSPITALID(String hOSPITALID) {
		HOSPITALID = hOSPITALID;
	}
	public String getAREAID() {
		return AREAID;
	}
	public void setAREAID(String aREAID) {
		AREAID = aREAID;
	}
	public String getRNUM() {
		return RNUM;
	}
	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}
	public String getSAME_HOSPITAL() {
		return SAME_HOSPITAL;
	}
	public void setSAME_HOSPITAL(String sAME_HOSPITAL) {
		SAME_HOSPITAL = sAME_HOSPITAL;
	}
	public String getSAME_AREA() {
		return SAME_AREA;
	}
	public void setSAME_AREA(String sAME_AREA) {
		SAME_AREA = sAME_AREA;
	}
	public String getSAME_GEO_AREA() {
		return SAME_GEO_AREA;
	}
	public void setSAME_GEO_AREA(String sAME_GEO_AREA) {
		SAME_GEO_AREA = sAME_GEO_AREA;
	}
	public String getIS_BOOK_FRIEND() {
		return IS_BOOK_FRIEND;
	}
	public void setIS_BOOK_FRIEND(String iS_BOOK_FRIEND) {
		IS_BOOK_FRIEND = iS_BOOK_FRIEND;
	}
	public String getFRIENDID() {
		return FRIENDID;
	}
	public void setFRIENDID(String fRIENDID) {
		FRIENDID = fRIENDID;
	}
	public DoctorInfoEntry getDOCTOR_INFO() {
		return DOCTOR_INFO;
	}
	public void setDOCTOR_INFO(DoctorInfoEntry dOCTOR_INFO) {
		DOCTOR_INFO = dOCTOR_INFO;
	}
	public HospitalInfo getHOSPITAL_INFO() {
		return HOSPITAL_INFO;
	}
	public void setHOSPITAL_INFO(HospitalInfo hOSPITAL_INFO) {
		HOSPITAL_INFO = hOSPITAL_INFO;
	}
	
	
}
