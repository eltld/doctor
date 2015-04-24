package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

public class PeerEntrty implements Serializable{
	
	private String success;
	private String errormsg;
	private List<PeerInfo> data;
	private String Count;
	private String pages;
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	public List<PeerInfo> getData() {
		return data;
	}
	public void setData(List<PeerInfo> data) {
		this.data = data;
	}
	public String getCount() {
		return Count;
	}
	public void setCount(String count) {
		Count = count;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	
	

}
