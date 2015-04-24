package net.ememed.doctor2.message_center.entity;

import java.util.List;

public class SmsListEntry {
	private int success;
	private String errormsg;
	private List<SmsDetail> data;
	private String count;
	private String pages;
	private String curpage;

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

	public List<SmsDetail> getData() {
		return data;
	}

	public void setData(List<SmsDetail> data) {
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

	public String getCurpage() {
		return curpage;
	}

	public void setCurpage(String curpage) {
		this.curpage = curpage;
	}

	public boolean isSuccess() {
		return success == 1 ? true : false;
	}

}
