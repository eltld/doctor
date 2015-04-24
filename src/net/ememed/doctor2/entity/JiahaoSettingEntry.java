package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class JiahaoSettingEntry implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6183852585192905222L;
	private int enable_jiahao;
	private String price_jiahao;
	private int jiahao_time_status;
	private int jiahao_num;
	private String week_repeat;
	private String start_time;
	private String end_time;
	private String jiahao_hospital;
	public int getEnable_jiahao() {
		return enable_jiahao;
	}
	public void setEnable_jiahao(int enable_jiahao) {
		this.enable_jiahao = enable_jiahao;
	}
	public String getPrice_jiahao() {
		return price_jiahao;
	}
	public void setPrice_jiahao(String price_jiahao) {
		this.price_jiahao = price_jiahao;
	}
	public int getJiahao_time_status() {
		return jiahao_time_status;
	}
	public void setJiahao_time_status(int jiahao_time_status) {
		this.jiahao_time_status = jiahao_time_status;
	}
	public int getJiahao_num() {
		return jiahao_num;
	}
	public void setJiahao_num(int jiahao_num) {
		this.jiahao_num = jiahao_num;
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
	public String getJiahao_hospital() {
		return jiahao_hospital;
	}
	public void setJiahao_hospital(String jiahao_hospital) {
		this.jiahao_hospital = jiahao_hospital;
	}
	
}
