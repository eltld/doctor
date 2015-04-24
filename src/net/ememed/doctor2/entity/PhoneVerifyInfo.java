package net.ememed.doctor2.entity;

import net.ememed.doctor2.R;

public class PhoneVerifyInfo {
	private int success;
	private String errormsg;
	private PhoneVerifyEntry data;

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

	public PhoneVerifyEntry getData() {
		return data;
	}

	public void setData(PhoneVerifyEntry data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success == 1 ? true : false;
	}

}
