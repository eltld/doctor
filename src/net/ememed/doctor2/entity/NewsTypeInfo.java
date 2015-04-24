package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

public class NewsTypeInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1899679829495528444L;
	private int success;	
	private String errormsg;
	private List<NewsTypeEntry> data;
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
	public List<NewsTypeEntry> getData() {
		return data;
	}
	public void setData(List<NewsTypeEntry> data) {
		this.data = data;
	}
	
}
