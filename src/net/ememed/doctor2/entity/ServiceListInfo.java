package net.ememed.doctor2.entity;

import java.util.ArrayList;
import java.util.List;

public class ServiceListInfo {
	private int success;	
	private String errormsg;
	private List<ServiceListEntry> data;
	
	public List<ServiceListEntry> getData() {
		return data;
	}
	public void setData(List<ServiceListEntry> data) {
		this.data = data;
	}
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
	
}
