package net.ememed.doctor2.entity;

import java.util.List;

public class GroupListInfo {

	private int success;// => 1
	private String errormsg;// =>
	private List<GroupListEntry> data;// => Array

	private int count;// => 2
	private int pages;// => 1
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
	public List<GroupListEntry> getData() {
		return data;
	}
	public void setData(List<GroupListEntry> data) {
		this.data = data;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	
}
