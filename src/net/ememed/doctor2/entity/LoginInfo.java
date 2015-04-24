package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class LoginInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5306319512189864744L;
	private int success;	
	private String errormsg;
	private LoginEntry data;
	
	public LoginInfo() {
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

	public LoginEntry getData() {
		return data;
	}

	public void setData(LoginEntry data) {
		this.data = data;
	}
	
	

}
