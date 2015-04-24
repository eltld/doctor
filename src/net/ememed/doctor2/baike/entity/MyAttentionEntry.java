package net.ememed.doctor2.baike.entity;

import java.util.List;

public class MyAttentionEntry {
	private int success;
	private String errormsg;
	private List<MyAttentionInfo> data;
	private int pages;
	private int count;
	
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
	public List<MyAttentionInfo> getData() {
		return data;
	}
	public void setData(List<MyAttentionInfo> data) {
		this.data = data;
	}
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
