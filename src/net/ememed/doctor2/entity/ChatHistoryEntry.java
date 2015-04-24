package net.ememed.doctor2.entity;

import java.util.List;

public class ChatHistoryEntry {
	
	
	private String HAVEORDER;
	private String ID;
	private String SENDER;
	private String RECEIVER;
	private String MSG_ID;
	private String ORDERID;
	private String SERVICEID;
	private String PACKET_BUY_ID;
	private String USERID;
	private String DOCTORID;
	
	private String CHANNEL;
	private String ORDERTYPE;
	private String TYPE;
	private String CONTENT;
	private String SENDTIME;
	
	private List<MessageBackupFieldEntry> EXT;
	
	private String CREATETIME;
	private String ISSYSTEMMSG;
	private List<ImageEntry> CONTENT_EXT;
	private String DOWNLOAD_STATUS;
	private String DOWNLOAD_COUNT;
	private String LOCAL_FILE;
	private String LOCAL_IMAGE_THUMB;
	private String RNUM;
	private String FILE_URL;
	public String getHAVEORDER() {
		return HAVEORDER;
	}
	public void setHAVEORDER(String hAVEORDER) {
		HAVEORDER = hAVEORDER;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getSENDER() {
		return SENDER;
	}
	public void setSENDER(String sENDER) {
		SENDER = sENDER;
	}
	public String getRECEIVER() {
		return RECEIVER;
	}
	public void setRECEIVER(String rECEIVER) {
		RECEIVER = rECEIVER;
	}
	public String getMSG_ID() {
		return MSG_ID;
	}
	public void setMSG_ID(String mSG_ID) {
		MSG_ID = mSG_ID;
	}
	public String getORDERID() {
		return ORDERID;
	}
	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}
	public String getSERVICEID() {
		return SERVICEID;
	}
	public void setSERVICEID(String sERVICEID) {
		SERVICEID = sERVICEID;
	}
	public String getPACKET_BUY_ID() {
		return PACKET_BUY_ID;
	}
	public void setPACKET_BUY_ID(String pACKET_BUY_ID) {
		PACKET_BUY_ID = pACKET_BUY_ID;
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
	public String getCHANNEL() {
		return CHANNEL;
	}
	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}
	public String getORDERTYPE() {
		return ORDERTYPE;
	}
	public void setORDERTYPE(String oRDERTYPE) {
		ORDERTYPE = oRDERTYPE;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getSENDTIME() {
		return SENDTIME;
	}
	public void setSENDTIME(String sENDTIME) {
		SENDTIME = sENDTIME;
	}

	public List<MessageBackupFieldEntry> getEXT() {
		return EXT;
	}
	public void setEXT(List<MessageBackupFieldEntry> eXT) {
		EXT = eXT;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getISSYSTEMMSG() {
		return ISSYSTEMMSG;
	}
	public void setISSYSTEMMSG(String iSSYSTEMMSG) {
		ISSYSTEMMSG = iSSYSTEMMSG;
	}

	public List<ImageEntry> getCONTENT_EXT() {
		return CONTENT_EXT;
	}
	public void setCONTENT_EXT(List<ImageEntry> cONTENT_EXT) {
		CONTENT_EXT = cONTENT_EXT;
	}
	public String getDOWNLOAD_STATUS() {
		return DOWNLOAD_STATUS;
	}
	public void setDOWNLOAD_STATUS(String dOWNLOAD_STATUS) {
		DOWNLOAD_STATUS = dOWNLOAD_STATUS;
	}
	public String getDOWNLOAD_COUNT() {
		return DOWNLOAD_COUNT;
	}
	public void setDOWNLOAD_COUNT(String dOWNLOAD_COUNT) {
		DOWNLOAD_COUNT = dOWNLOAD_COUNT;
	}
	public String getLOCAL_FILE() {
		return LOCAL_FILE;
	}
	public void setLOCAL_FILE(String lOCAL_FILE) {
		LOCAL_FILE = lOCAL_FILE;
	}
	public String getLOCAL_IMAGE_THUMB() {
		return LOCAL_IMAGE_THUMB;
	}
	public void setLOCAL_IMAGE_THUMB(String lOCAL_IMAGE_THUMB) {
		LOCAL_IMAGE_THUMB = lOCAL_IMAGE_THUMB;
	}
	public String getRNUM() {
		return RNUM;
	}
	public void setRNUM(String rNUM) {
		RNUM = rNUM;
	}
	public String getFILE_URL() {
		return FILE_URL;
	}
	public void setFILE_URL(String fILE_URL) {
		FILE_URL = fILE_URL;
	}
	
}
