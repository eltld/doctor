package net.ememed.doctor2.entity;

import java.util.List;

public class NoticeListDataBean {
	private String ID;
	private String TYPE;
	private String GROUPNUM;
	private String SENDER;
	private String RECEIVER;
	private String STATUS;
	private String RESULT;
	private String MSGDESC;
	private String CREATETIME;
	private String DEALTIME;
	private String NOTICE_TYPE;
	private String IS_FRIEND;
    private NoticeDoctorInfoBean DOCTOR_INFO;
    private NoticeHospitalInfoBean HOSPITAL_INFO;
	public String getID() {
		return ID;
	}
	public String getTYPE() {
		return TYPE;
	}
	public String getGROUPNUM() {
		return GROUPNUM;
	}
	public String getSENDER() {
		return SENDER;
	}
	public String getRECEIVER() {
		return RECEIVER;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public String getRESULT() {
		return RESULT;
	}
	public String getMSGDESC() {
		return MSGDESC;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public String getDEALTIME() {
		return DEALTIME;
	}
	public String getNOTICE_TYPE() {
		return NOTICE_TYPE;
	}
	public String getIS_FRIEND() {
		return IS_FRIEND;
	}
	public NoticeDoctorInfoBean getDOCTOR_INFO() {
		return DOCTOR_INFO;
	}
	public NoticeHospitalInfoBean getHOSPITAL_INFO() {
		return HOSPITAL_INFO;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public void setGROUPNUM(String gROUPNUM) {
		GROUPNUM = gROUPNUM;
	}
	public void setSENDER(String sENDER) {
		SENDER = sENDER;
	}
	public void setRECEIVER(String rECEIVER) {
		RECEIVER = rECEIVER;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public void setRESULT(String rESULT) {
		RESULT = rESULT;
	}
	public void setMSGDESC(String mSGDESC) {
		MSGDESC = mSGDESC;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public void setDEALTIME(String dEALTIME) {
		DEALTIME = dEALTIME;
	}
	public void setNOTICE_TYPE(String nOTICE_TYPE) {
		NOTICE_TYPE = nOTICE_TYPE;
	}
	public void setIS_FRIEND(String iS_FRIEND) {
		IS_FRIEND = iS_FRIEND;
	}
	public void setDOCTOR_INFO(NoticeDoctorInfoBean dOCTOR_INFO) {
		DOCTOR_INFO = dOCTOR_INFO;
	}
	public void setHOSPITAL_INFO(NoticeHospitalInfoBean hOSPITAL_INFO) {
		HOSPITAL_INFO = hOSPITAL_INFO;
	}
}
