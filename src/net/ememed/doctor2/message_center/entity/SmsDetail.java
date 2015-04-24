package net.ememed.doctor2.message_center.entity;

import android.text.TextUtils;

public class SmsDetail {

	private String MSGID;  // => 10
    private String MEMBERID;  // => 106040
    private String UTYPE;  // => user
    private String TYPE;  // => 0
    private String SMS_TYPE;  // => 1
    private String TARGET;  // => 18565122038
    private String CONTEXT;  // => 您已成功购买敖青松（薏米）医生的私人医生服务服务。如需帮助，请致电4009003333
   // private XXXX CONTEXT_EXT;  // => {"username":"\u6556\u9752\u677e\uff08\u6d4b\u8bd5\uff09","date":"2015-01-08 16:01","doctorname":"\u6556\u9752\u677e\uff08\u858f\u7c73\uff09","info":"","price":"1","ext":"","ordertype":"\u79c1\u4eba\u533b\u751f"}
    private String SENDTIME;  // => 2015-01-08 16:01:42
    private String SENDSTATUS;  // => 1
    private String STATUS;  // => 0
    private String ADDTIME;  // => 2015-01-08 16:01:42
    private String UPDATETIME;  // => 2015-01-08 16:01:42
    private String RNUM;	
    private String SMS_TYPE_NAME;	//预约通话

	public String getMSGID() {
		return MSGID;
	}

	public void setMSGID(String mSGID) {
		MSGID = mSGID;
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

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getSMS_TYPE() {
		return SMS_TYPE;
	}

	public void setSMS_TYPE(String sMS_TYPE) {
		SMS_TYPE = sMS_TYPE;
	}

	public String getTARGET() {
		return TARGET;
	}

	public void setTARGET(String tARGET) {
		TARGET = tARGET;
	}

	public String getCONTEXT() {
		return CONTEXT;
	}

	public void setCONTEXT(String cONTEXT) {
		CONTEXT = cONTEXT;
	}

	public String getSENDTIME() {
		return SENDTIME;
	}

	public void setSENDTIME(String sENDTIME) {
		SENDTIME = sENDTIME;
	}

	public String getSENDSTATUS() {
		return SENDSTATUS;
	}

	public void setSENDSTATUS(String sENDSTATUS) {
		SENDSTATUS = sENDSTATUS;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getADDTIME() {
		return ADDTIME;
	}

	public void setADDTIME(String aDDTIME) {
		ADDTIME = aDDTIME;
	}

	public String getUPDATETIME() {
		return UPDATETIME;
	}

	public void setUPDATETIME(String uPDATETIME) {
		UPDATETIME = uPDATETIME;
	}

	public String getRNUM() {
		return RNUM;
	}

	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}

	public String getSMS_TYPE_NAME() {
		return TextUtils.isEmpty(SMS_TYPE_NAME) == true ? "" : SMS_TYPE_NAME;
	}

	public void setSMS_TYPE_NAME(String sMS_TYPE_NAME) {
		SMS_TYPE_NAME = sMS_TYPE_NAME;
	}

}
