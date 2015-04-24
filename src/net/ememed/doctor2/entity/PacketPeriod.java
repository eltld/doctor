package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

import android.os.Parcel;
import android.os.Parcelable;

public class PacketPeriod implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7389872214960136516L;
	private int packet_period_id;
	private int packet_daynum;
	private String packet_period_price;
	private String packet_daytype;
	public int getPacket_period_id() {
		return packet_period_id;
	}
	public void setPacket_period_id(int packet_period_id) {
		this.packet_period_id = packet_period_id;
	}
	public int getPacket_daynum() {
		return packet_daynum;
	}
	public void setPacket_daynum(int packet_daynum) {
		this.packet_daynum = packet_daynum;
	}
	public String getPacket_period_price() {
		return packet_period_price;
	}
	public void setPacket_period_price(String packet_period_price) {
		this.packet_period_price = packet_period_price;
	}
	public String getPacket_daytype() {
		return packet_daytype;
	}
	public void setPacket_daytype(String packet_daytype) {
		this.packet_daytype = packet_daytype;
	}
	
}
