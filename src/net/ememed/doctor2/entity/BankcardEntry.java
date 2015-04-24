package net.ememed.doctor2.entity;

import java.io.Serializable;

public class BankcardEntry {
	
	private String MBCID;//	银行卡ID
	private String BANKCARDNUM;	//银行卡号
	private String MEMBERID;	//医生ID
	private String HOLDER;	//持卡人名字
	private String BANKNAME;//	银行名称
	private String CREATETIME;//	创建时间
	private String UPDATETIME;//	更新时间
	private String CHANNEL;
	
	public String getCHANNEL() {
		return CHANNEL;
	}
	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}
	public String getMBCID() {
		return MBCID;
	}
	public void setMBCID(String mBCID) {
		MBCID = mBCID;
	}
	public String getBANKCARDNUM() {
		return BANKCARDNUM;
	}
	public void setBANKCARDNUM(String bANKCARDNUM) {
		BANKCARDNUM = bANKCARDNUM;
	}
	public String getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getHOLDER() {
		return HOLDER;
	}
	public void setHOLDER(String hOLDER) {
		HOLDER = hOLDER;
	}
	public String getBANKNAME() {
		return BANKNAME;
	}
	public void setBANKNAME(String bANKNAME) {
		BANKNAME = bANKNAME;
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
	
}
