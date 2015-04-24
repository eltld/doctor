package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class ZhuyuanSettingEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 502086706083290031L;
	private int enable_zhuyuan;
	private String price_zhuyuan;
	private String week_repeat;
	private String start_time;
	private String end_time;
	private int zhuyuan_time_status;
	public int getEnable_zhuyuan() {
		return enable_zhuyuan;
	}
	public void setEnable_zhuyuan(int enable_zhuyuan) {
		this.enable_zhuyuan = enable_zhuyuan;
	}
	public String getPrice_zhuyuan() {
		return price_zhuyuan;
	}
	public void setPrice_zhuyuan(String price_zhuyuan) {
		this.price_zhuyuan = price_zhuyuan;
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
	public int getZhuyuan_time_status() {
		return zhuyuan_time_status;
	}
	public void setZhuyuan_time_status(int zhuyuan_time_status) {
		this.zhuyuan_time_status = zhuyuan_time_status;
	}
	
}
