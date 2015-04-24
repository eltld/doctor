package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class ServiceSettingInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5306319512189864744L;
	private int success;	
	private String errormsg;
	private ServiceSettingEntry Data;
	
	public ServiceSettingInfo() {
	}
	public ServiceSettingEntry getData() {
		return Data;
	}
	public void setData(ServiceSettingEntry data) {
		Data = data;
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


}
