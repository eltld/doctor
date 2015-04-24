package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class ServiceSettingEntry implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5498630913957512036L;
	private TextConsultSettingEntry textconsult_setting;
	private CallSettingEntry call_setting;
	private TijianSettingEntry tijian_setting;
	private ShangmenSettingEntry shangmen_setting;
	private JiahaoSettingEntry jiahao_setting;
	private ZhuyuanSettingEntry zhuyuan_setting;
	private PacketSettingEntry packet_setting;
	private FreetalkSetting freetalk_setting;
	
	public ServiceSettingEntry() {
		super();
	}
	
	public TextConsultSettingEntry getTextconsult_setting() {
		if (null == textconsult_setting) {
			textconsult_setting = new TextConsultSettingEntry();
		}
		return textconsult_setting;
	}
	public void setTextconsult_setting(TextConsultSettingEntry textconsult_setting) {
		this.textconsult_setting = textconsult_setting;
	}
	
	public CallSettingEntry getCall_setting() {
		if (null == call_setting) {
			call_setting = new CallSettingEntry();
		}
		return call_setting;
	}
	public void setCall_setting(CallSettingEntry call_setting) {
		this.call_setting = call_setting;
	}
	
	public TijianSettingEntry getTijian_setting() {
		if (tijian_setting == null ) {
			tijian_setting = new TijianSettingEntry();
		}
		return tijian_setting;
	}
	public void setTijian_setting(TijianSettingEntry tijian_setting) {
		this.tijian_setting = tijian_setting;
	}
	public ShangmenSettingEntry getShangmen_setting() {
		if (null == shangmen_setting) {
			shangmen_setting = new ShangmenSettingEntry();
		}
		return shangmen_setting;
	}
	public void setShangmen_setting(ShangmenSettingEntry shangmen_setting) {
		this.shangmen_setting = shangmen_setting;
	}
	public JiahaoSettingEntry getJiahao_setting() {
		if (null == jiahao_setting) {
			jiahao_setting = new JiahaoSettingEntry();
		}
		return jiahao_setting;
	}
	public void setJiahao_setting(JiahaoSettingEntry jiahao_setting) {
		this.jiahao_setting = jiahao_setting;
	}
	public ZhuyuanSettingEntry getZhuyuan_setting() {
		if (null == zhuyuan_setting) {
			zhuyuan_setting = new ZhuyuanSettingEntry();
		}
		return zhuyuan_setting;
	}
	public void setZhuyuan_setting(ZhuyuanSettingEntry zhuyuan_setting) {
		this.zhuyuan_setting = zhuyuan_setting;
	}
	public PacketSettingEntry getPacket_setting() {
		return packet_setting;
	}
	public void setPacket_setting(PacketSettingEntry packet_setting) {
		this.packet_setting = packet_setting;
	}

	public FreetalkSetting getFreetalk_setting() {
		return freetalk_setting;
	}

	public void setFreetalk_setting(FreetalkSetting freetalk_setting) {
		this.freetalk_setting = freetalk_setting;
	}
	
}
