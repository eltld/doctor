package net.ememed.doctor2.message_center.entity;

public class SmsClassifyEntity {
	private int success;
	private String errormsg;
	private SmsClassifyList data;
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
	public SmsClassifyList getData() {
		return data;
	}
	public void setData(SmsClassifyList data) {
		this.data = data;
	}
	
	
}
