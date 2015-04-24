package net.ememed.doctor2.entity;

public class VersionEntry {
	
	private int VID;
	private String VTID;
	private String VERSIONNAME;
	private String VERSIONCODE;
	private String PLATFORM;
	private String UPDATEDATE;
	
	private String UPGRADEMODE;
	private String CONTENT;
	private String THIRDCONTENT;
	private String APPFILE;
	private String APPSIZE;
	private String ICONFILE;
	private String CREATETIME;
	private String UPDATETIME;
	private String TITLE;
	private Extend appconfig;
	
	private int force_update;
	
	public int getVID() {
		return VID;
	}
	public void setVID(int vID) {
		VID = vID;
	}
	public String getVTID() {
		return VTID;
	}
	public void setVTID(String vTID) {
		VTID = vTID;
	}
	public String getVERSIONNAME() {
		return VERSIONNAME;
	}
	public void setVERSIONNAME(String vERSIONNAME) {
		VERSIONNAME = vERSIONNAME;
	}
	public String getVERSIONCODE() {
		return VERSIONCODE;
	}
	public void setVERSIONCODE(String vERSIONCODE) {
		VERSIONCODE = vERSIONCODE;
	}
	public String getPLATFORM() {
		return PLATFORM;
	}
	public void setPLATFORM(String pLATFORM) {
		PLATFORM = pLATFORM;
	}
	public String getUPDATEDATE() {
		return UPDATEDATE;
	}
	public void setUPDATEDATE(String uPDATEDATE) {
		UPDATEDATE = uPDATEDATE;
	}
	public String getUPGRADEMODE() {
		return UPGRADEMODE;
	}
	public void setUPGRADEMODE(String uPGRADEMODE) {
		UPGRADEMODE = uPGRADEMODE;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getTHIRDCONTENT() {
		return THIRDCONTENT;
	}
	public void setTHIRDCONTENT(String tHIRDCONTENT) {
		THIRDCONTENT = tHIRDCONTENT;
	}
	public String getAPPFILE() {
		return APPFILE;
	}
	public void setAPPFILE(String aPPFILE) {
		APPFILE = aPPFILE;
	}
	public String getAPPSIZE() {
		return APPSIZE;
	}
	public void setAPPSIZE(String aPPSIZE) {
		APPSIZE = aPPSIZE;
	}
	public String getICONFILE() {
		return ICONFILE;
	}
	public void setICONFILE(String iCONFILE) {
		ICONFILE = iCONFILE;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getUPDATETIME() {
		return UPDATETIME;
	}
	public void setUPDATETIME(String uPDATETIME) {
		UPDATETIME = uPDATETIME;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public int getForce_update() {
		return force_update;
	}
	public void setForce_update(int force_update) {
		this.force_update = force_update;
	}
	public Extend getAppconfig() {
		return appconfig;
	}
	public void setAppconfig(Extend appconfig) {
		this.appconfig = appconfig;
	}
	
	
	
}
