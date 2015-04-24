package net.ememed.doctor2.entity;

import java.io.Serializable;

import net.ememed.doctor2.R;

public class ServicePacketSetInfo {

	private int success;	
	private String errormsg;
	private PacketSettingEntry data;
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
	public PacketSettingEntry getData() {
		return data;
	}
	public void setData(PacketSettingEntry data) {
		this.data = data;
	}
}
