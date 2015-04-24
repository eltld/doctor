package net.ememed.doctor2.entity;

import java.util.List;

public class QuestionPool {
	private String success;
	private String errormsg;
	private List<QuestionPoolInfo> data;
	private String Count;
	private String pages;
	public String getSuccess() {
		return success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public List<QuestionPoolInfo> getData() {
		return data;
	}
	public String getCount() {
		return Count;
	}
	public String getPages() {
		return pages;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public void setData(List<QuestionPoolInfo> data) {
		this.data = data;
	}
	public void setCount(String count) {
		Count = count;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
}
