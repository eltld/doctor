package net.ememed.doctor2.entity;

public class ImInfo {
	private int success;	
	private String errormsg;
	private IMEntry data;
	private String count;
	private String pages;
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
	public IMEntry getData() {
		return data;
	}
	public void setData(IMEntry data) {
		this.data = data;
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
	
}
