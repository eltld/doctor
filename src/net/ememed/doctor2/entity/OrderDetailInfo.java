package net.ememed.doctor2.entity;

public class OrderDetailInfo {
	private int success;	
	private String errormsg;
	private OrderDetailEntry data;
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
	public OrderDetailEntry getData() {
		return data;
	}
	public void setData(OrderDetailEntry data) {
		this.data = data;
	}
	
}
