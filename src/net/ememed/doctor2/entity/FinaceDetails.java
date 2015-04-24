package net.ememed.doctor2.entity;

import java.io.Serializable;

public class FinaceDetails implements Serializable{
	private int success;
	private String errormsg;
	private FinaceEntry data;

	public FinaceEntry getData() {
		return data;
	}
	public void setData(FinaceEntry data) {
		this.data = data;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
}
