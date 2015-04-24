package net.ememed.doctor2.entity;

import net.ememed.doctor2.R;

public class UploadVerifyInfo {
	private int Success;	
	private String Errormsg;
	private UploadVerifyEntry Data;
	public int getSuccess() {
		return Success;
	}
	public void setSuccess(int success) {
		Success = success;
	}
	public String getErrormsg() {
		return Errormsg;
	}
	public void setErrormsg(String errormsg) {
		Errormsg = errormsg;
	}
	public UploadVerifyEntry getData() {
		return Data;
	}
	public void setData(UploadVerifyEntry data) {
		Data = data;
	}
	
}
