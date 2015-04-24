package net.ememed.doctor2.entity;

import java.util.List;

public class ScoreExchangeBean {
	 private String success;
	 private String errormsg;
	 private List<ScoreDataBean> data;
	public String getSuccess() {
		return success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public List<ScoreDataBean> getData() {
		return data;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public void setData(List<ScoreDataBean> data) {
		this.data = data;
	}
}
