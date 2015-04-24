package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.ememed.doctor2.R;

public class PacketSettingEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7282419074724711360L;
	private String service_content;

	
	private int packet_id;
	private int doctor_id;
	private int packet_enable_textconsult;
	private int packet_enable_call;
	private int packet_enable_tijian;
	private int packet_enable_shangmen;
	private int packet_enable_jiahao;
	private int packet_enable_zhuyuan;
	
	private int packet_textconsult_num;
	private int packet_call_num;
	private int packet_tijian_num;
	private int packet_shangmen_num;
	private int packet_zhuyuan_num;
	private int packet_jiahao_num;

	private String status;
	private String createtime;
	private String updatetime;
	
	private List<PacketPeriod> packet_period_list;
	
	
	public String getService_content() {
		return service_content;
	}
	public void setService_content(String service_content) {
		this.service_content = service_content;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getPacket_textconsult_num() {
		return packet_textconsult_num;
	}
	public void setPacket_textconsult_num(int packet_textconsult_num) {
		this.packet_textconsult_num = packet_textconsult_num;
	}
	public int getPacket_call_num() {
		return packet_call_num;
	}
	public void setPacket_call_num(int packet_call_num) {
		this.packet_call_num = packet_call_num;
	}
	public int getPacket_tijian_num() {
		return packet_tijian_num;
	}
	public void setPacket_tijian_num(int packet_tijian_num) {
		this.packet_tijian_num = packet_tijian_num;
	}
	public int getPacket_shangmen_num() {
		return packet_shangmen_num;
	}
	public void setPacket_shangmen_num(int packet_shangmen_num) {
		this.packet_shangmen_num = packet_shangmen_num;
	}
	public int getPacket_zhuyuan_num() {
		return packet_zhuyuan_num;
	}
	public void setPacket_zhuyuan_num(int packet_zhuyuan_num) {
		this.packet_zhuyuan_num = packet_zhuyuan_num;
	}
	public int getPacket_jiahao_num() {
		return packet_jiahao_num;
	}
	public void setPacket_jiahao_num(int packet_jiahao_num) {
		this.packet_jiahao_num = packet_jiahao_num;
	}
	
	public int getPacket_id() {
		return packet_id;
	}
	public void setPacket_id(int packet_id) {
		this.packet_id = packet_id;
	}
	public int getPacket_enable_textconsult() {
		return packet_enable_textconsult;
	}
	public void setPacket_enable_textconsult(int packet_enable_textconsult) {
		this.packet_enable_textconsult = packet_enable_textconsult;
	}
	public int getPacket_enable_call() {
		return packet_enable_call;
	}
	public void setPacket_enable_call(int packet_enable_call) {
		this.packet_enable_call = packet_enable_call;
	}
	public int getPacket_enable_tijian() {
		return packet_enable_tijian;
	}
	public void setPacket_enable_tijian(int packet_enable_tijian) {
		this.packet_enable_tijian = packet_enable_tijian;
	}
	public int getPacket_enable_shangmen() {
		return packet_enable_shangmen;
	}
	public void setPacket_enable_shangmen(int packet_enable_shangmen) {
		this.packet_enable_shangmen = packet_enable_shangmen;
	}
	public int getPacket_enable_jiahao() {
		return packet_enable_jiahao;
	}
	public void setPacket_enable_jiahao(int packet_enable_jiahao) {
		this.packet_enable_jiahao = packet_enable_jiahao;
	}
	public int getPacket_enable_zhuyuan() {
		return packet_enable_zhuyuan;
	}
	public void setPacket_enable_zhuyuan(int packet_enable_zhuyuan) {
		this.packet_enable_zhuyuan = packet_enable_zhuyuan;
	}
	public List<PacketPeriod> getPacket_period_list() {
		return packet_period_list;
	}
	public void setPacket_period_list(List<PacketPeriod> packet_period_list) {
		this.packet_period_list = packet_period_list;
	}
	public int getDoctor_id() {
		return doctor_id;
	}
	public void setDoctor_id(int doctor_id) {
		this.doctor_id = doctor_id;
	}
	
}
