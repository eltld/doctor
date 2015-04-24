package net.ememed.doctor2.entity;

import net.ememed.doctor2.R;

public class RegisterInfo {
	private int success;	
	private String errormsg;
	private RegisterEntry data;

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
	public RegisterEntry getData() {
		return data;
	}
	public void setData(RegisterEntry data) {
		this.data = data;
	}
}
