package net.ememed.doctor2.entity;

import java.util.List;

public class GroupContactEntityItem {

	private String GROUPID;
	private String DOCTORID;
	private String GROUPNAME;
	private String CREATE_TIME;
	private String UPDATE_TIME;
	private String STATUS;
	private String RNUM;
	private List<PatientsEntity> PATIENTS;

	public String getGROUPID() {
		return GROUPID;
	}

	public void setGROUPID(String gROUPID) {
		GROUPID = gROUPID;
	}

	public String getDOCTORID() {
		return DOCTORID;
	}

	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
	}

	public String getGROUPNAME() {
		return GROUPNAME;
	}

	public void setGROUPNAME(String gROUPNAME) {
		GROUPNAME = gROUPNAME;
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

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getRNUM() {
		return RNUM;
	}

	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}

	public List<PatientsEntity> getPATIENTS() {
		return PATIENTS;
	}

	public void setPATIENTS(List<PatientsEntity> pATIENTS) {
		PATIENTS = pATIENTS;
	}

	
	

}
