package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;
	
public class BankCardInfo implements Serializable {
	private String MBCID;// "465",
	private String BANKCARDNUM;// "1234567890123456789",
	private String MEMBERID;// "97924",
	private String HOLDER;// "测试",
	private String BANKNAME;// "中国建设银行",
	private String CREATETIME;// "2014-08-29 17:38:33",
	private String UPDATETIME;// null,
	private String CHANNEL;// "android",
	private String BANKBRANCH;// "广州",
	private String CARDNUMBER;// "431022199102220371"
	private String ALIPAY_ACCOUNT; //支付宝
	
	public String getALIPAY_ACCOUNT() {
		return ALIPAY_ACCOUNT;
	}

	public void setALIPAY_ACCOUNT(String aLIPAY_ACCOUNT) {
		ALIPAY_ACCOUNT = aLIPAY_ACCOUNT;
	}

	public String getCARDNUMBER() {
		return CARDNUMBER;
	}

	public void setCARDNUMBER(String cARDNUMBER) {
		CARDNUMBER = cARDNUMBER;
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

	public String getCHANNEL() {
		return CHANNEL;
	}

	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}

	public String getBANKBRANCH() {
		return BANKBRANCH;
	}

	public void setBANKBRANCH(String bANKBRANCH) {
		BANKBRANCH = bANKBRANCH;
	}

}
