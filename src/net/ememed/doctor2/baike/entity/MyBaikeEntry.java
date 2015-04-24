package net.ememed.doctor2.baike.entity;

import java.util.List;

public class MyBaikeEntry {
	private int success;
	private String errormsg;
	private int count;
	private int pages;
	private List<BaikeShuoshuoInfo> data;
	private BaikeMemberInfo memberinfo;
	
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
	public List<BaikeShuoshuoInfo> getData() {
		return data;
	}
	public void setData(List<BaikeShuoshuoInfo> data) {
		this.data = data;
	}
	public BaikeMemberInfo getMemberinfo() {
		return memberinfo;
	}
	public void setMemberinfo(BaikeMemberInfo memberinfo) {
		this.memberinfo = memberinfo;
	}
	
	
}
