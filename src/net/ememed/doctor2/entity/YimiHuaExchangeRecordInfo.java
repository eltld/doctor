package net.ememed.doctor2.entity;

/**
 * 积分赚取记录与积分兑换记录共用一个model
 * @author llj
 *
 */
public class YimiHuaExchangeRecordInfo{
	
	//共用
	private String CREATE_TIME; // => 2015-04-15 15:04:04
	
	//积分兑换
	private String CHARGEID; // => 3
    private String MEMBERID; // => 104038
    private String UTYPE; // => doctor
    private String POINTS; // => 1000
    private String POINTS_NOW; // => 1000
    private String MONEY; // => 50
    private String USERMONEY; // => 100
    private String RNUM; // => 1
	
    //低分赚取
    private String POINTS_DESC; // => +40
    private String TYPE; // => 1
    private String LOGID; // => 8
    private String LOGTITLE; // => 签到
    private String LOGTYPE; // => 1
    private String LOGTYPE_DESC; // => 签到
	public String getCREATE_TIME() {
		return CREATE_TIME;
	}
	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}
	public String getCHARGEID() {
		return CHARGEID;
	}
	public void setCHARGEID(String cHARGEID) {
		CHARGEID = cHARGEID;
	}
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getUTYPE() {
		return UTYPE;
	}
	public void setUTYPE(String uTYPE) {
		UTYPE = uTYPE;
	}
	public String getPOINTS() {
		return POINTS;
	}
	public void setPOINTS(String pOINTS) {
		POINTS = pOINTS;
	}
	public String getPOINTS_NOW() {
		return POINTS_NOW;
	}
	public void setPOINTS_NOW(String pOINTS_NOW) {
		POINTS_NOW = pOINTS_NOW;
	}
	public String getMONEY() {
		return MONEY;
	}
	public void setMONEY(String mONEY) {
		MONEY = mONEY;
	}
	public String getUSERMONEY() {
		return USERMONEY;
	}
	public void setUSERMONEY(String uSERMONEY) {
		USERMONEY = uSERMONEY;
	}
	public String getRNUM() {
		return RNUM;
	}
	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}
	public String getPOINTS_DESC() {
		return POINTS_DESC;
	}
	public void setPOINTS_DESC(String pOINTS_DESC) {
		POINTS_DESC = pOINTS_DESC;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getLOGID() {
		return LOGID;
	}
	public void setLOGID(String lOGID) {
		LOGID = lOGID;
	}
	public String getLOGTITLE() {
		return LOGTITLE;
	}
	public void setLOGTITLE(String lOGTITLE) {
		LOGTITLE = lOGTITLE;
	}
	public String getLOGTYPE() {
		return LOGTYPE;
	}
	public void setLOGTYPE(String lOGTYPE) {
		LOGTYPE = lOGTYPE;
	}
	public String getLOGTYPE_DESC() {
		return LOGTYPE_DESC;
	}
	public void setLOGTYPE_DESC(String lOGTYPE_DESC) {
		LOGTYPE_DESC = lOGTYPE_DESC;
	}
}
