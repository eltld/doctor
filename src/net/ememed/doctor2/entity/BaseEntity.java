package net.ememed.doctor2.entity;

import java.io.Serializable;

public class BaseEntity implements Serializable{

	private Integer success;
	private String errormsg;

	public int getSuccess() {
		return success;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public boolean isSuccess() {
		return (success != null && success == 1) ? true : false;
	}
}
