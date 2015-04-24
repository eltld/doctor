package net.ememed.doctor2.entity;

public class InviteInfo {
	private int success;
	private String errormsg;
	private InviteEntry data;
	
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

	public InviteEntry getData() {
		return data;
	}
	
	public void setData(InviteEntry data) {
		this.data = data;
	}
}
