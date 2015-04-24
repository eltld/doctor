package net.ememed.doctor2.entity;

import java.util.List;

public class MemberInfo {
	private int success;
	private String errormsg;
	private MemberDetailEntry data;
	
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
	public MemberDetailEntry getData() {
		return data;
	}
	public void setData(MemberDetailEntry data) {
		this.data = data;
	}
}
