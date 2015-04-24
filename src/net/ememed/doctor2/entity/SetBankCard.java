package net.ememed.doctor2.entity;

public class SetBankCard {
	private int success;
	private String errormsg;
	private String MBCID;
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public String getMBCID() {
		return MBCID;
	}
	public void setMBCID(String mBCID) {
		MBCID = mBCID;
	}
	
}
