package net.ememed.doctor2.entity;


import java.util.ArrayList;

public class ChatBodyEntry {
//	String msgBody = "{"from":"6998","to":"88988","bodies":[{"type":"txt","msg":"滚滚滚"}],"ext":{"ext":{"CHANNEL":"android","ISSYSTEMMSG":"0","ORDERTYPE":"16","DOCTORID":"88988","ORDERID":"9783","USERID":"6998","SERVICEID":"7468"}}}";
	private String from;
	private String to;
	private ArrayList<ChatBodyItemEntry> bodies;
	private ChatBodyItemExtEntry ext;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public ChatBodyItemExtEntry getExt() {
		return ext;
	}

	public void setExt(ChatBodyItemExtEntry ext) {
		this.ext = ext;
	}

	public ArrayList<ChatBodyItemEntry> getBodies() {
		return bodies;
	}

	public void setBodies(ArrayList<ChatBodyItemEntry> bodies) {
		this.bodies = bodies;
	}
	
}
