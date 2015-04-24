package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

public class OrderListEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5016153605638271894L;
	private String ORDERID;
	private String ORDERSN;
	private String ORDERTYPE;
	private String USERID;
	private String DOCTORID;
	private String ORDERSTATUS;
	private String PAYSTATUS;
	private String CONTRACTOR;
	private String TELPHONE;
	private String MOBILE;
	private String EMAIL;
	private String PAYID;
	private String PAYNAME;
	private String GOODSAMOUNT;
	private String PAYFEE;
	private String DISCOUNT;
	private String ORDERAMOUNT; // 订单价格
	private String ADDTIME;
	private String CONFIRMTIME;
	private String PAYTIME;
	private String BALANCE;
	private String BALANCETIME;
	private String BANKDEDUCTTYPE;
	private String DEPOSITBACKTYPE;
	private String BACKFORORDERID;
	private String CHANNELID;
	private String CHANNELNAME;
	private String FROMTYPE;
	private String FROMOTHER;
	private String MEMO;
	private String FREE;
	private String SMSSENDED;
	private String ORIGINALPRICE;
	private String CHANNEL;
	private String COUPONNUM;
	private String BALANCEPRICE;
	private String OVERTIME;
	private String RNUM;
	private String SERVICE_CALL_TIME;
	private String SERVICEID;
	private String STATE;

	// llj-新增字段2014-11-28
	private String ORDERTYPENAME; // 服务类型名称
	private String PACKET_BUY_ORDERID; // 有值时表示私人服务
	private String AVATAR; // 头像
	private String DESC_TEXT; // 咨询文本
	private ImTalkInfoForOrder IM_TALK; // 最后一条聊天记录

	private String DSRATTITUDE;
	private String EVALUATION;
	private String RATECONTENT;
	private String DESC_PIC;
	private String QUESTIONID;
	private String STATE_DESC;

	/*
	 * STATE字段的含义 图文咨询： 1待处理 2服务结束
	 * 
	 * 预约通话： 1待处理 2等待通话 3服务结束
	 * 
	 * 私人医生服务服务： 1服务期内 2服务结束
	 * 
	 * 预约住院、预约加号、上门会诊、自定义业务： 1待处理 2等待用户支付 3用户支付成功 4服务结束
	 */

	
	
	public String getDESC_TEXT() {
		return DESC_TEXT;
	}

	public String getDSRATTITUDE() {
		return DSRATTITUDE;
	}

	public void setDSRATTITUDE(String dSRATTITUDE) {
		DSRATTITUDE = dSRATTITUDE;
	}

	public String getEVALUATION() {
		return EVALUATION;
	}

	public void setEVALUATION(String eVALUATION) {
		EVALUATION = eVALUATION;
	}

	public String getRATECONTENT() {
		return RATECONTENT;
	}

	public void setRATECONTENT(String rATECONTENT) {
		RATECONTENT = rATECONTENT;
	}

	public String getDESC_PIC() {
		return DESC_PIC;
	}

	public void setDESC_PIC(String dESC_PIC) {
		DESC_PIC = dESC_PIC;
	}

	public String getQUESTIONID() {
		return QUESTIONID;
	}

	public void setQUESTIONID(String qUESTIONID) {
		QUESTIONID = qUESTIONID;
	}

	public String getSTATE_DESC() {
		return STATE_DESC;
	}

	public void setSTATE_DESC(String sTATE_DESC) {
		STATE_DESC = sTATE_DESC;
	}

	public ImTalkInfoForOrder getIM_TALK() {
		return IM_TALK;
	}

	public void setIM_TALK(ImTalkInfoForOrder iM_TALK) {
		IM_TALK = iM_TALK;
	}

	public void setDESC_TEXT(String dESC_TEXT) {
		DESC_TEXT = dESC_TEXT;
	}

	public String getAVATAR() {
		return AVATAR;
	}

	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}

	public String getSTATE() {
		return STATE;
	}

	public String getORDERTYPENAME() {
		return ORDERTYPENAME;
	}

	public void setORDERTYPENAME(String oRDERTYPENAME) {
		ORDERTYPENAME = oRDERTYPENAME;
	}

	public String getPACKET_BUY_ORDERID() {
		return PACKET_BUY_ORDERID;
	}

	public void setPACKET_BUY_ORDERID(String pACKET_BUY_ORDERID) {
		PACKET_BUY_ORDERID = pACKET_BUY_ORDERID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setSTATE(String sTATE) {
		STATE = sTATE;
	}

	public String getORDERID() {
		return ORDERID;
	}

	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}

	public String getORDERSN() {
		return ORDERSN;
	}

	public void setORDERSN(String oRDERSN) {
		ORDERSN = oRDERSN;
	}

	public String getORDERTYPE() {
		return ORDERTYPE;
	}

	public void setORDERTYPE(String oRDERTYPE) {
		ORDERTYPE = oRDERTYPE;
	}

	public String getUSERID() {
		return USERID;
	}

	public void setUSERID(String uSERID) {
		USERID = uSERID;
	}

	public String getDOCTORID() {
		return DOCTORID;
	}

	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
	}

	public String getORDERSTATUS() {
		return ORDERSTATUS;
	}

	public void setORDERSTATUS(String oRDERSTATUS) {
		ORDERSTATUS = oRDERSTATUS;
	}

	public String getPAYSTATUS() {
		return PAYSTATUS;
	}

	public void setPAYSTATUS(String pAYSTATUS) {
		PAYSTATUS = pAYSTATUS;
	}

	public String getCONTRACTOR() {
		return CONTRACTOR;
	}

	public void setCONTRACTOR(String cONTRACTOR) {
		CONTRACTOR = cONTRACTOR;
	}

	public String getTELPHONE() {
		return TELPHONE;
	}

	public void setTELPHONE(String tELPHONE) {
		TELPHONE = tELPHONE;
	}

	public String getMOBILE() {
		return MOBILE;
	}

	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}

	public String getPAYID() {
		return PAYID;
	}

	public void setPAYID(String pAYID) {
		PAYID = pAYID;
	}

	public String getPAYNAME() {
		return PAYNAME;
	}

	public void setPAYNAME(String pAYNAME) {
		PAYNAME = pAYNAME;
	}

	public String getGOODSAMOUNT() {
		return GOODSAMOUNT;
	}

	public void setGOODSAMOUNT(String gOODSAMOUNT) {
		GOODSAMOUNT = gOODSAMOUNT;
	}

	public String getPAYFEE() {
		return PAYFEE;
	}

	public void setPAYFEE(String pAYFEE) {
		PAYFEE = pAYFEE;
	}

	public String getDISCOUNT() {
		return DISCOUNT;
	}

	public void setDISCOUNT(String dISCOUNT) {
		DISCOUNT = dISCOUNT;
	}

	public String getORDERAMOUNT() {
		return ORDERAMOUNT;
	}

	public void setORDERAMOUNT(String oRDERAMOUNT) {
		ORDERAMOUNT = oRDERAMOUNT;
	}

	public String getADDTIME() {
		return ADDTIME;
	}

	public void setADDTIME(String aDDTIME) {
		ADDTIME = aDDTIME;
	}

	public String getCONFIRMTIME() {
		return CONFIRMTIME;
	}

	public void setCONFIRMTIME(String cONFIRMTIME) {
		CONFIRMTIME = cONFIRMTIME;
	}

	public String getPAYTIME() {
		return PAYTIME;
	}

	public void setPAYTIME(String pAYTIME) {
		PAYTIME = pAYTIME;
	}

	public String getBALANCE() {
		return BALANCE;
	}

	public void setBALANCE(String bALANCE) {
		BALANCE = bALANCE;
	}

	public String getBALANCETIME() {
		return BALANCETIME;
	}

	public void setBALANCETIME(String bALANCETIME) {
		BALANCETIME = bALANCETIME;
	}

	public String getBANKDEDUCTTYPE() {
		return BANKDEDUCTTYPE;
	}

	public void setBANKDEDUCTTYPE(String bANKDEDUCTTYPE) {
		BANKDEDUCTTYPE = bANKDEDUCTTYPE;
	}

	public String getDEPOSITBACKTYPE() {
		return DEPOSITBACKTYPE;
	}

	public void setDEPOSITBACKTYPE(String dEPOSITBACKTYPE) {
		DEPOSITBACKTYPE = dEPOSITBACKTYPE;
	}

	public String getBACKFORORDERID() {
		return BACKFORORDERID;
	}

	public void setBACKFORORDERID(String bACKFORORDERID) {
		BACKFORORDERID = bACKFORORDERID;
	}

	public String getCHANNELID() {
		return CHANNELID;
	}

	public void setCHANNELID(String cHANNELID) {
		CHANNELID = cHANNELID;
	}

	public String getCHANNELNAME() {
		return CHANNELNAME;
	}

	public void setCHANNELNAME(String cHANNELNAME) {
		CHANNELNAME = cHANNELNAME;
	}

	public String getFROMTYPE() {
		return FROMTYPE;
	}

	public void setFROMTYPE(String fROMTYPE) {
		FROMTYPE = fROMTYPE;
	}

	public String getFROMOTHER() {
		return FROMOTHER;
	}

	public void setFROMOTHER(String fROMOTHER) {
		FROMOTHER = fROMOTHER;
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}

	public String getFREE() {
		return FREE;
	}

	public void setFREE(String fREE) {
		FREE = fREE;
	}

	public String getSMSSENDED() {
		return SMSSENDED;
	}

	public void setSMSSENDED(String sMSSENDED) {
		SMSSENDED = sMSSENDED;
	}

	public String getORIGINALPRICE() {
		return ORIGINALPRICE;
	}

	public void setORIGINALPRICE(String oRIGINALPRICE) {
		ORIGINALPRICE = oRIGINALPRICE;
	}

	public String getCHANNEL() {
		return CHANNEL;
	}

	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}

	public String getCOUPONNUM() {
		return COUPONNUM;
	}

	public void setCOUPONNUM(String cOUPONNUM) {
		COUPONNUM = cOUPONNUM;
	}

	public String getBALANCEPRICE() {
		return BALANCEPRICE;
	}

	public void setBALANCEPRICE(String bALANCEPRICE) {
		BALANCEPRICE = bALANCEPRICE;
	}

	public String getOVERTIME() {
		return OVERTIME;
	}

	public void setOVERTIME(String oVERTIME) {
		OVERTIME = oVERTIME;
	}

	public String getRNUM() {
		return RNUM;
	}

	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}

	public String getSERVICE_CALL_TIME() {
		return SERVICE_CALL_TIME;
	}

	public void setSERVICE_CALL_TIME(String sERVICE_CALL_TIME) {
		SERVICE_CALL_TIME = sERVICE_CALL_TIME;
	}

	public String getSERVICEID() {
		return SERVICEID;
	}

	public void setSERVICEID(String sERVICEID) {
		SERVICEID = sERVICEID;
	}

}
