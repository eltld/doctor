package de.greenrobot.event;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ServiceSettingEntry;

/**
 * Created by chen
 */
public class RegisterSuccessEvent {
	private int status;
	private String textConsultPrice;
	private String phoneConsultPrice;
	private String privateDoctorWeekPrice;
	private String privateDoctorMonthPrice;
	private String privateDoctorThreemonthPrice;
	private ServiceSettingEntry packetSettingEntry;
	private int packet_id;

	public int getPacket_id() {
		return packet_id;
	}

	public void setPacket_id(int packet_id) {
		this.packet_id = packet_id;
	}

	public ServiceSettingEntry getPacketSettingEntry() {
		return packetSettingEntry;
	}

	public void setPacketSettingEntry(ServiceSettingEntry packetSettingEntry) {
		this.packetSettingEntry = packetSettingEntry;
	}

	public String getPhoneConsultPrice() {
		return phoneConsultPrice;
	}

	public String getPrivateDoctorWeekPrice() {
		return privateDoctorWeekPrice;
	}

	public void setPrivateDoctorWeekPrice(String privateDoctorWeekPrice) {
		this.privateDoctorWeekPrice = privateDoctorWeekPrice;
	}

	public String getPrivateDoctorMonthPrice() {
		return privateDoctorMonthPrice;
	}

	public void setPrivateDoctorMonthPrice(String privateDoctorMonthPrice) {
		this.privateDoctorMonthPrice = privateDoctorMonthPrice;
	}

	public String getPrivateDoctorThreemonthPrice() {
		return privateDoctorThreemonthPrice;
	}

	public void setPrivateDoctorThreemonthPrice(String privateDoctorThreemonthPrice) {
		this.privateDoctorThreemonthPrice = privateDoctorThreemonthPrice;
	}

	public void setPhoneConsultPrice(String phoneConsultPrice) {
		this.phoneConsultPrice = phoneConsultPrice;
	}

	public RegisterSuccessEvent() {
    }

	public String getTextConsultPrice() {
		return textConsultPrice;
	}

	public void setTextConsultPrice(String textConsultPrice) {
		this.textConsultPrice = textConsultPrice;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    
}
