package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class TextConsultSettingEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6293633698554790496L;
	private int enable_textconsult;
	private String price_textconsult;
	public int getEnable_textconsult() {
		return enable_textconsult;
	}
	public void setEnable_textconsult(int enable_textconsult) {
		this.enable_textconsult = enable_textconsult;
	}
	public String getPrice_textconsult() {
		return price_textconsult;
	}
	public void setPrice_textconsult(String price_textconsult) {
		this.price_textconsult = price_textconsult;
	}
	
}
