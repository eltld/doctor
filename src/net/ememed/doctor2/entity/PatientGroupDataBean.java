package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

public class PatientGroupDataBean implements Serializable{
	private String GROUPID;
	private String DOCTORID;
	private String GROUPNAME;
	private String CREATE_TIME;
	private String UPDATE_TIME;
	private String STATUS;
	private String RNUM;
	private List<PatientsUserBean> PATIENTS;
	public String getGROUPID() {
		return GROUPID;
	}
	public String getDOCTORID() {
		return DOCTORID;
	}
	public String getGROUPNAME() {
		return GROUPNAME;
	}
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public String getUPDATE_TIME() {
		return UPDATE_TIME;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public String getRNUM() {
		return RNUM;
	}
	public List<PatientsUserBean> getPATIENTS() {
		return PATIENTS;
	}
	public void setGROUPID(String gROUPID) {
		GROUPID = gROUPID;
	}
	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
	}
	public void setGROUPNAME(String gROUPNAME) {
		GROUPNAME = gROUPNAME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public void setUPDATE_TIME(String uPDATE_TIME) {
		UPDATE_TIME = uPDATE_TIME;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}
	public void setPATIENTS(List<PatientsUserBean> pATIENTS) {
		PATIENTS = pATIENTS;
	}
}
