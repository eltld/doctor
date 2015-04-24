package net.ememed.doctor2.entity;

import net.ememed.doctor2.R;

public class ValiCode {

	private int success;	
	private String errormsg;
	private int validateCode;
	private String mobile;
	private String memberid;
	
	public int getValidateCode() {
		return validateCode;
	}
	public void setValidateCode(int validateCode) {
		this.validateCode = validateCode;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMemberid() {
		return memberid;
	}
	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}
	
}
