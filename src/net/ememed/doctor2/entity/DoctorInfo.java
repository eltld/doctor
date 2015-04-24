package net.ememed.doctor2.entity;

public class DoctorInfo {
	private int success;//=> 1
	private String errormsg;//=> 成功
	private DoctorInfoEntry data;//
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
	public DoctorInfoEntry getData() {
		return data;
	}
	public void setData(DoctorInfoEntry data) {
		this.data = data;
	}
	
}
