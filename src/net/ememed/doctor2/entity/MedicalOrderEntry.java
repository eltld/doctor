package net.ememed.doctor2.entity;

public class MedicalOrderEntry {

	private String CREATETIME;
	private String CONTENT;
	private String ATTACHMENT;
	
	public void setCREATETIME(String CREATETIME_) {
		CREATETIME = CREATETIME_;
	}
	
	public String getCREATETIME() {
		return CREATETIME;
	}
	
	public void setCONTENT(String CONTENT_) {
		CONTENT = CONTENT_;
	}
	
	public String getCONTENT() {
		return CONTENT;
	}
	
	public void setATTACHMENT(String ATTACHMENT_) {
		ATTACHMENT = ATTACHMENT_;
	}
	
	public String getATTACHMENT() {
		return ATTACHMENT;
	}
	

}
