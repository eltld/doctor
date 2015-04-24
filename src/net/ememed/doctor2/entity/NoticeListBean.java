package net.ememed.doctor2.entity;

import java.util.List;

public class NoticeListBean {
	private String success;
	private String errormsg;
	private List<NoticeListDataBean> data;
	public String getSuccess() {
		return success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public List<NoticeListDataBean> getData() {
		return data;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public void setData(List<NoticeListDataBean> data) {
		this.data = data;
	}

}
