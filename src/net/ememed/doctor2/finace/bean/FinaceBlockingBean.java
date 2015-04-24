package net.ememed.doctor2.finace.bean;

import java.io.Serializable;
import java.util.List;

public class FinaceBlockingBean implements Serializable{
	private int success;
	private int pages;
	private int count;
	private String errormsg;
	private List<FinaceBlocking> data;

	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
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
	public List<FinaceBlocking> getData() {
		return data;
	}
	public void setData(List<FinaceBlocking> data) {
		this.data = data;
	}
	
}
