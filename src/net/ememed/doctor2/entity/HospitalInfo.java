package net.ememed.doctor2.entity;

import java.io.Serializable;

import android.text.TextUtils;

public class HospitalInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String HOSPITALID; // 3932",
	private String HOSPITALCODE; // ": null,
	private String HOSPITALNAME; // ,
	private String ADDRESS; // \u5c71\u897f\u7701\u592a\u539f\u5e02\u674f\u82b1\u5cad\u533a\u5e9c\u4e1c\u885713\u53f7",
	private String POSTCODE; // null,
	private String HOSTEL; // 0351-3172466",
	private String CONTEXT;
	private String GRADE;

	public String getHOSPITALID() {
		return HOSPITALID;
	}

	public void setHOSPITALID(String hOSPITALID) {
		HOSPITALID = hOSPITALID;
	}

	public String getHOSPITALCODE() {
		return HOSPITALCODE;
	}

	public void setHOSPITALCODE(String hOSPITALCODE) {
		HOSPITALCODE = hOSPITALCODE;
	}

	public String getHOSPITALNAME() {
		return TextUtils.isEmpty(HOSPITALNAME) ? "" : HOSPITALNAME;
	}

	public void setHOSPITALNAME(String hOSPITALNAME) {
		HOSPITALNAME = hOSPITALNAME;
	}

	public String getADDRESS() {
		return ADDRESS;
	}

	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}

	public String getPOSTCODE() {
		return POSTCODE;
	}

	public void setPOSTCODE(String pOSTCODE) {
		POSTCODE = pOSTCODE;
	}

	public String getHOSTEL() {
		return HOSTEL;
	}

	public void setHOSTEL(String hOSTEL) {
		HOSTEL = hOSTEL;
	}

	public String getCONTEXT() {
		return CONTEXT;
	}

	public void setCONTEXT(String cONTEXT) {
		CONTEXT = cONTEXT;
	}

	public String getGRADE() {
		return GRADE;
	}

	public void setGRADE(String gRADE) {
		GRADE = gRADE;
	}

}
