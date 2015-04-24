package net.ememed.doctor2.message_center.entity;

public class SmsInfo {
	private SmsDetail lastest;		//最后一条消息
	private int unread_count;			//未读消息数
	
	public SmsDetail getLastest() {
		return lastest;
	}
	
	public void setLastest(SmsDetail lastest) {
		this.lastest = lastest;
	}
	
	public int getUnread_count() {
		return unread_count;
	}
	
	public void setUnread_count(int unread_count) {
		this.unread_count = unread_count;
	}
	
}
