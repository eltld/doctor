package net.ememed.doctor2.baike.entity;

import java.util.List;

public class RewardListEntry {
	private int  success;
    private String errormsg; 
    private int count;
    private int pages;
    private String total_money;
    private List<RewardListInfo> data;

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

	public String getTotal_money() {
		return total_money;
	}

	public void setTotal_money(String total_money) {
		this.total_money = total_money;
	}

	public List<RewardListInfo> getData() {
		return data;
	}

	public void setData(List<RewardListInfo> data) {
		this.data = data;
	}
    
    
}
