package net.ememed.doctor2.baike.entity;

import java.util.List;

public class MyFansEntry {
	private int success;  // => 1
    private String errormsg;  // => 获取粉丝列表成功
    private List<MyFansInfo> data;  // => Array

    private int count;  // => 2
    private int pages;  // => 1

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
	public List<MyFansInfo> getData() {
		return data;
	}
	public void setData(List<MyFansInfo> data) {
		this.data = data;
	}
	
}
