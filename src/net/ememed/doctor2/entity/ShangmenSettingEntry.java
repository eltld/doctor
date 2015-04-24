package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class ShangmenSettingEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1909281621267845057L;
	private int enable_shangmen;
	private String price_shangmen;
	private int shangmen_time_status;
	private String location_shangmen;
	private String week_repeat;
	private String start_time;
	private String end_time;
	public int getEnable_shangmen() {
		return enable_shangmen;
	}
	public void setEnable_shangmen(int enable_shangmen) {
		this.enable_shangmen = enable_shangmen;
	}
	public String getPrice_shangmen() {
		return price_shangmen;
	}
	public void setPrice_shangmen(String price_shangmen) {
		this.price_shangmen = price_shangmen;
	}
	public String getLocation_shangmen() {
		return location_shangmen;
	}
	public void setLocation_shangmen(String location_shangmen) {
		this.location_shangmen = location_shangmen;
	}
	public String getWeek_repeat() {
		return week_repeat;
	}
	public void setWeek_repeat(String week_repeat) {
		this.week_repeat = week_repeat;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public int getShangmen_time_status() {
		return shangmen_time_status;
	}
	public void setShangmen_time_status(int shangmen_time_status) {
		this.shangmen_time_status = shangmen_time_status;
	}
}
