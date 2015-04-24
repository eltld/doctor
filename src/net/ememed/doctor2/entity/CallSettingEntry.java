package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class CallSettingEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6438091832624923090L;
	private int enable_call;
	private int call_time_status;
	private String price_call;
	private String week_repeat;
	private String start_time;
	private String end_time;
	public int getEnable_call() {
		return enable_call;
	}
	public void setEnable_call(int enable_call) {
		this.enable_call = enable_call;
	}
	public int getCall_time_status() {
		return call_time_status;
	}
	public void setCall_time_status(int call_time_status) {
		this.call_time_status = call_time_status;
	}
	public String getPrice_call() {
		return price_call;
	}
	public void setPrice_call(String price_call) {
		this.price_call = price_call;
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
}
