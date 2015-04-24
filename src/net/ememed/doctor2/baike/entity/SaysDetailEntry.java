package net.ememed.doctor2.baike.entity;

public class SaysDetailEntry {
	private int success;
	private String errormsg;
	private BaikeShuoshuoInfo data;
	
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
	public BaikeShuoshuoInfo getData() {
		return data;
	}
	public void setData(BaikeShuoshuoInfo data) {
		this.data = data;
	}
}
