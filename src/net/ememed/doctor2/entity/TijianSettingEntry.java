package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class TijianSettingEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1124252707960415212L;
	private int enable_tijian;
	private String price_tijian;
	public int getEnable_tijian() {
		return enable_tijian;
	}
	public void setEnable_tijian(int enable_tijian) {
		this.enable_tijian = enable_tijian;
	}
	public String getPrice_tijian() {
		return price_tijian;
	}
	public void setPrice_tijian(String price_tijian) {
		this.price_tijian = price_tijian;
	}
	
}
