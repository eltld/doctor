package net.ememed.doctor2.baike.entity;

public class BaikeMineEntry {
	private int success;
	private String errormsg;
	private BaikeMineInfo data;
	
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
	public BaikeMineInfo getData() {
		return data;
	}
	public void setData(BaikeMineInfo data) {
		this.data = data;
	}
	
	
}
