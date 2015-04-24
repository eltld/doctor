package net.ememed.doctor2.entity;

public class IncomeExpendEntry {
	private int LOGID;
	private String LOGSN;
	private double MONEY;
	private double BALANCE;
	private int LOGTYPE;
	private int STATUS;
	private String LOGDESC;
	private String CREATETIME;
	private String ORDERTYPE_NAME;
	private int ORDERTYPE;
	private double USERMONEY;
	
	public double getUSERMONEY() {
		return USERMONEY;
	}
	public void setUSERMONEY(double uSERMONEY) {
		USERMONEY = uSERMONEY;
	}
	public int getLOGID() {
		return LOGID;
	}
	public void setLOGID(int lOGID) {
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
	public double getBALANCE() {
		return BALANCE;
	}
	public void setBALANCE(double bALANCE) {
		BALANCE = bALANCE;
	}
	public int getLOGTYPE() {
		return LOGTYPE;
	}
	public void setLOGTYPE(int lOGTYPE) {
		LOGTYPE = lOGTYPE;
	}
	public int getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(int sTATUS) {
		STATUS = sTATUS;
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
	public int getORDERTYPE() {
		return ORDERTYPE;
	}
	public void setORDERTYPE(int oRDERTYPE) {
		ORDERTYPE = oRDERTYPE;
	}
	public String getORDERTYPE_NAME() {
		return ORDERTYPE_NAME;
	}
	public void setORDERTYPE_NAME(String oRDERTYPE_NAME) {
		ORDERTYPE_NAME = oRDERTYPE_NAME;
	}
	
}
