package net.ememed.doctor2.entity;

public class CommonResponseEntity {
	private int success;	
	private String errormsg;
	private String data;
	
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
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	} 
	
	
}
