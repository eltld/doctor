package net.ememed.doctor2.finace.bean;

import java.util.List;

public class FinaceIncome {
	private int success;
	private String errormsg;
	private List<FinaceIncomeBean> data;
	private int count;
	private int pages;

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
	public List<FinaceIncomeBean> getData() {
		return data;
	}
	public void setData(List<FinaceIncomeBean> data) {
		this.data = data;
	}
	
}
