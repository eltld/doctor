package net.ememed.doctor2.entity;

import java.util.Map;

public class InviteEntry {
	
	private String invite_rule;
	private int month_count;
	private int history_count;
	private String invite_number;
	private Map<String, String> invite_desc;
	private String qr_img;//": "http://www.ememed.net/uploads/qr/98828.png"
	public String getInvite_rule() {
		return invite_rule;
	}

	public void setInvite_rule(String invite_rule) {
		this.invite_rule = invite_rule;
	}
	
	public int getMonth_count() {
		return month_count;
	}

	public void setMonth_count(int month_count) {
		this.month_count = month_count;
	}
	
	public int getHistory_count() {
		return history_count;
	}

	public void setHistory_count(int history_count) {
		this.history_count = history_count;
	}
	
	public String getInvite_number() {
		return invite_number;
	}

	public void setInvite_number(String invite_number) {
		this.invite_number = invite_number;
	}
	
	public Map<String, String> getInvite_desc() {
		return invite_desc;
	}

	public void setInvite_desc(Map<String, String> invite_desc) {
		this.invite_desc = invite_desc;
	}

	public String getQr_img() {
		return qr_img;
	}

	public void setQr_img(String qr_img) {
		this.qr_img = qr_img;
	}
	
	
	
}
