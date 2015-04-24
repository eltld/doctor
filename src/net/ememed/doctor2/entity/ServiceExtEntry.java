package net.ememed.doctor2.entity;

import java.io.Serializable;

public class ServiceExtEntry implements Serializable {
	private String URL;
	private String CHECK_RIGHT_URL;
	private String PHONE_NUMBER;//": "4009003333"
	
	public void setURL(String URL_) {
		URL = URL_;
	}
	
	public String getURL() {
		return URL;
	}
	
	public void setCHECK_RIGHT_URL(String CHECK_RIGHT_URL_) {
		CHECK_RIGHT_URL = CHECK_RIGHT_URL_;
	}
	
	public String getCHECK_RIGHT_URL() {
		return CHECK_RIGHT_URL;
	}

	public String getPHONE_NUMBER() {
		return PHONE_NUMBER;
	}

	public void setPHONE_NUMBER(String pHONE_NUMBER) {
		PHONE_NUMBER = pHONE_NUMBER;
	}
	

}
