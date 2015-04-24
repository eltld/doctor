package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class ConfigInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8146854825472930738L;
	private int success;	
	private String errormsg;
	private ConfigEntry data;
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
	public ConfigEntry getData() {
		return data;
	}
	public void setData(ConfigEntry data) {
		this.data = data;
	}
	
}
