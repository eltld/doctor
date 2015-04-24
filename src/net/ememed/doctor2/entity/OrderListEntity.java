package net.ememed.doctor2.entity;

import java.util.List;

public class OrderListEntity {
	private int success;
	private String errormsg;
	private List<OrderListEntry> data;
	private int count;
	private int pages;
	private OrderOpenCount order_open_count;
	private String unread_question_num;
	private String baike_status;
	
	
	public String getUnread_question_num() {
		return unread_question_num;
	}
	public void setUnread_question_num(String unread_question_num) {
		this.unread_question_num = unread_question_num;
	}
	public String getBaike_status() {
		return baike_status;
	}
	public void setBaike_status(String baike_status) {
		this.baike_status = baike_status;
	}
	public OrderOpenCount getOrder_open_count() {
		return order_open_count;
	}
	public void setOrder_open_count(OrderOpenCount order_open_count) {
		this.order_open_count = order_open_count;
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
	public List<OrderListEntry> getData() {
		return data;
	}
	public void setData(List<OrderListEntry> data) {
		this.data = data;
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
}
 