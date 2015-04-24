package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

public class FinaceEntry{
	private double available;
	private double freeze;
	private double balance;
	private double total;
	private int apply_cash_status;
	private List<RecentinEntry> recentincome;
	private List<BankCardInfo> bankcardlist;
	
	public int getApply_cash_status() {
		return apply_cash_status;
	}
	public void setApply_cash_status(int apply_cash_status) {
		this.apply_cash_status = apply_cash_status;
	}
	public double getAvailable() {
		return available;
	}
	public void setAvailable(double available) {
		this.available = available;
	}
	public double getFreeze() {
		return freeze;
	}
	public void setFreeze(double freeze) {
		this.freeze = freeze;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public List<RecentinEntry> getRecentincome() {
		return recentincome;
	}
	public void setRecentincome(List<RecentinEntry> recentincome) {
		this.recentincome = recentincome;
	}
	public List<BankCardInfo> getBankcardlist() {
		return bankcardlist;
	}
	public void setBankcardlist(List<BankCardInfo> bankcardlist) {
		this.bankcardlist = bankcardlist;
	}
}
