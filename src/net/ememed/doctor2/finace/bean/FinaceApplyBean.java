package net.ememed.doctor2.finace.bean;

public class FinaceApplyBean {
	private int success;
	private String errormsg;
	private FinaceInfo data;
	
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
	public FinaceInfo getData() {
		return data;
	}
	public void setData(FinaceInfo data) {
		this.data = data;
	}
	
}
