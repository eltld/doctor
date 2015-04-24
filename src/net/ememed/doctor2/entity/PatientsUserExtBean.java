package net.ememed.doctor2.entity;

import java.io.Serializable;

public class PatientsUserExtBean  implements Serializable{
	private String CHANNEL;
	private String DOCTORID;
	private String ISSYSTEMMSG;
	private String ORDERID;
	private String doctor_avatar;
	private String doctor_name;
	private String user_avatar;
	public String getCHANNEL() {
		return CHANNEL;
	}
	public String getDOCTORID() {
		return DOCTORID;
	}
	public String getISSYSTEMMSG() {
		return ISSYSTEMMSG;
	}
	public String getORDERID() {
		return ORDERID;
	}
	public String getDoctor_avatar() {
		return doctor_avatar;
	}
	public String getDoctor_name() {
		return doctor_name;
	}
	public String getUser_avatar() {
		return user_avatar;
	}
	public void setCHANNEL(String cHANNEL) {
		CHANNEL = cHANNEL;
	}
	public void setDOCTORID(String dOCTORID) {
		DOCTORID = dOCTORID;
	}
	public void setISSYSTEMMSG(String iSSYSTEMMSG) {
		ISSYSTEMMSG = iSSYSTEMMSG;
	}
	public void setORDERID(String oRDERID) {
		ORDERID = oRDERID;
	}
	public void setDoctor_avatar(String doctor_avatar) {
		this.doctor_avatar = doctor_avatar;
	}
	public void setDoctor_name(String doctor_name) {
		this.doctor_name = doctor_name;
	}
	public void setUser_avatar(String user_avatar) {
		this.user_avatar = user_avatar;
	}
}
