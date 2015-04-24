package net.ememed.doctor2.finace.bean;

public class FinaceInfo {
	private String available;	//可用余额
	private String freeze;		//冻结金额
	private String balance;		
	private String total;
	private String apply_cash_status; //可提现状态  1表示可提现	
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String getFreeze() {
		return freeze;
	}
	public void setFreeze(String freeze) {
		this.freeze = freeze;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getApply_cash_status() {
		return apply_cash_status;
	}
	public void setApply_cash_status(String apply_cash_status) {
		this.apply_cash_status = apply_cash_status;
	}
	
	
}
