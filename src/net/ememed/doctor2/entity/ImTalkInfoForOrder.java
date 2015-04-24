package net.ememed.doctor2.entity;

import java.io.Serializable;

public class ImTalkInfoForOrder implements Serializable{
	String TYPE;   		//txt(文字)，img（图片），audio（语音）
	String CONTENT;		//当TYPE=txt的时候，为文字内容
	String SENDTIME;  	//发送时间
	
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getSENDTIME() {
		return SENDTIME;
	}
	public void setSENDTIME(String sENDTIME) {
		SENDTIME = sENDTIME;
	}
}