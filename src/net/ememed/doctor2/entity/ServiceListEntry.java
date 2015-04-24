package net.ememed.doctor2.entity;

import java.io.Serializable;

import com.google.gson.Gson;

public class ServiceListEntry implements Serializable{
	private String ID;
	private String SERVICE_NAME;
	private String ICON;
	private String TYPE;
	private String APPKEY;
	private String VERSION;
	private String DESCRIBE;
	private String STATUS;
	private String ORDERBY;
	private ServiceExtEntry EXT;
	private String ISTHIRDPARTY;
	private String CREATETIME;
	private String UPDATETIME;
	private String APP_TYPE;
	private String APPTYPE;
	
	public String getAPPTYPE() {
		return APPTYPE;
	}

	public void setAPPTYPE(String aPPTYPE) {
		APPTYPE = aPPTYPE;
	}

	public String getID() {
		return ID;
	}
	
	public void setID(String ID_) {
		ID = ID_;
	}
	
	public String getSERVICE_NAME() {
		return SERVICE_NAME;
	}
	
	public void setSERVICE_NAME(String sERVICE_NAME) {
		SERVICE_NAME = sERVICE_NAME;
	}
	
	public String getICON() {
		return ICON;
	}
	
	public void setICON(String ICON_) {
		ICON = ICON_;
	}
	
	public String getAPPKEY() {
		return APPKEY;
	}
	
	public void setAPPKEY(String APPKEY_) {
		APPKEY = APPKEY_;
	}
	
	public String getTYPE() {
		return TYPE;
	}
	
	public void setTYPE(String TYPE_) {
		TYPE = TYPE_;
	}
	
	public String getVERSION() {
		return VERSION;
	}
	
	public void setVERSION(String VERSION_) {
		VERSION = VERSION_;
	}
	
	public String getSTATUS() {
		return STATUS;
	}
	
	public void setSTATUS(String STATUS_) {
		STATUS = STATUS_;
	}
	
	public String getORDERBY() {
		return ORDERBY;
	}
	
	public void setORDERBY(String ORDERBY_) {
		ORDERBY = ORDERBY_;
	}
	

	public ServiceExtEntry getEXT() {
		return EXT;//gson.fromJson(EXT, ServiceExtEntry.class);
	}
	
	public void setEXT(ServiceExtEntry EXT_) {
		EXT = EXT_;
	}
	

	public String getISTHIRDPARTY() {
		return ISTHIRDPARTY;
	}
	
	public void setISTHIRDPARTY(String ISTHIRDPARTY_) {
		ISTHIRDPARTY = ISTHIRDPARTY_;
	}
	

	public String getCREATETIME() {
		return CREATETIME;
	}
	
	public void setCREATETIME(String CREATETIME_) {
		CREATETIME = CREATETIME_;
	}
	

	public String getUPDATETIME() {
		return UPDATETIME;
	}
	
	public void setUPDATETIME(String UPDATETIME_) {
		UPDATETIME = UPDATETIME_;
	}
	

	public String getAPP_TYPE() {
		return APP_TYPE;
	}
	
	public void setAPP_TYPE(String APP_TYPE_) {
		APP_TYPE = APP_TYPE_;
	}
	

	public String getDESCRIBE() {
		return DESCRIBE;
	}
	
	public void setDESCRIBE(String DESCRIBE_) {
		DESCRIBE = DESCRIBE_;
	}
	

}
