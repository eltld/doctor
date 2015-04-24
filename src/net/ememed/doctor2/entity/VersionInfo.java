package net.ememed.doctor2.entity;

import java.io.Serializable;

public class VersionInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8645442001187639262L;
	private int success;
	private String errormsg;
	private VersionEntry data;
	
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
	public VersionEntry getData() {
		return data;
	}
	public void setData(VersionEntry data) {
		this.data = data;
	}
	

}
