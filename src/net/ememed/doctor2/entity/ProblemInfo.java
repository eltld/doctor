package net.ememed.doctor2.entity;

import java.util.List;

public class ProblemInfo {
	private int success;	
	private String errormsg;
	private List<Problem> data;
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
	public List<Problem> getData() {
		return data;
	}
	public void setData(List<Problem> data) {
		this.data = data;
	}
	
}
