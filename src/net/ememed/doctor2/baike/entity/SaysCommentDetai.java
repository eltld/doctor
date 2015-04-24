package net.ememed.doctor2.baike.entity;

import java.io.Serializable;
import java.util.List;

public class SaysCommentDetai implements Serializable{
	private int total; // => 1
	private int total_unread; // => 0
	private List<SaysCommentInfo> list;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getTotal_unread() {
		return total_unread;
	}
	public void setTotal_unread(int total_unread) {
		this.total_unread = total_unread;
	}
	public List<SaysCommentInfo> getList() {
		return list;
	}
	public void setList(List<SaysCommentInfo> list) {
		this.list = list;
	}
	
	
	
}
