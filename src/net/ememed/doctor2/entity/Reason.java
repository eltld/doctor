package net.ememed.doctor2.entity;

import java.util.ArrayList;
import net.ememed.doctor2.R;

public class Reason {
	private int success;
	private String errormsg;
	private User data;

	public User getData() {
		return data;
	}
	public void setData(User data) {
		this.data = data;
	}
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
}
