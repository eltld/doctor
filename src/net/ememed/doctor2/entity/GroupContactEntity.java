package net.ememed.doctor2.entity;

import java.util.List;

public class GroupContactEntity {
	
	private int success;
	private String errormsg;
	private String count;
	private String pages;
	private List<GroupContactEntityItem> data;
	
	
	
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
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public List<GroupContactEntityItem> getData() {
		return data;
	}
	public void setData(List<GroupContactEntityItem> data) {
		this.data = data;
	}
	
	
	
}
