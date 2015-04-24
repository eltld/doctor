package net.ememed.doctor2.entity;


public class RecentinEntry{
	
	private String LOGID;
	private String LOGSN;
	private double MONEY;
	private String LOGDESC;
	private String CREATETIME;
	private String ORDERID;
	private String ORDERTYPE;
	private String ORDERTYPE_NAME;
	
	private int RNUM;
	public String getLOGID() {
		return LOGID;
	}
	public void setLOGID(String lOGID) {
		LOGID = lOGID;
	}
	public String getLOGSN() {
		return LOGSN;
	}
	public void setLOGSN(String lOGSN) {
		LOGSN = lOGSN;
	}
	public double getMONEY() {
		return MONEY;
	}
	public void setMONEY(double mONEY) {
		MONEY = mONEY;
	}
	public String getLOGDESC() {
		return LOGDESC;
	}
	public void setLOGDESC(String lOGDESC) {
		LOGDESC = lOGDESC;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getORDERID() {
		return ORDERID;
	}
	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}
	public String getORDERTYPE() {
		return ORDERTYPE;
	}
	public void setORDERTYPE(String oRDERTYPE) {
		ORDERTYPE = oRDERTYPE;
	}
	public int getRNUM() {
		return RNUM;
	}
	public void setRNUM(int rNUM) {
		RNUM = rNUM;
	}
	public String getORDERTYPE_NAME() {
		return ORDERTYPE_NAME;
	}
	public void setORDERTYPE_NAME(String oRDERTYPE_NAME) {
		ORDERTYPE_NAME = oRDERTYPE_NAME;
	}
	
	
	
}
