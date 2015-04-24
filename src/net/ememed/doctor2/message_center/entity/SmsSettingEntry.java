
package net.ememed.doctor2.message_center.entity;

public class SmsSettingEntry {
	private int success;
	private String errormsg;
	private SmsSettingDetail data;
	
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
	public SmsSettingDetail getData() {
		return data;
	}
	public void setData(SmsSettingDetail data) {
		this.data = data;
	}
}
