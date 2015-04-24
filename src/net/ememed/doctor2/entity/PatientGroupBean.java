package net.ememed.doctor2.entity;

import java.util.List;

public class PatientGroupBean {
	private String success;
	private String errormsg;
	private List<PatientGroupDataBean> data;
	private String count;
	private String pages;
	public String getSuccess() {
		return success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public List<PatientGroupDataBean> getData() {
		return data;
	}
	public String getCount() {
		return count;
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
	public void setData(List<PatientGroupDataBean> data) {
		this.data = data;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
}
