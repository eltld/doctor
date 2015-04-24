package net.ememed.doctor2.message_center.entity;

public class SmsClassifyList {
	private SmsInfo sms_order;
	private SmsInfo sms_charge;
	private SmsInfo sms_activity;
	
	
	public SmsInfo getSms_order() {
		return sms_order;
	}
	public void setSms_order(SmsInfo sms_order) {
		this.sms_order = sms_order;
	}
	public SmsInfo getSms_charge() {
		return sms_charge;
	}
	public void setSms_charge(SmsInfo sms_charge) {
		this.sms_charge = sms_charge;
	}
	public SmsInfo getSms_activity() {
		return sms_activity;
	}
	public void setSms_activity(SmsInfo sms_activity) {
		this.sms_activity = sms_activity;
	}
	
	
	
}
