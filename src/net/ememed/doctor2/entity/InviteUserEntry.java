package net.ememed.doctor2.entity;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class InviteUserEntry {

	private Map<String, String> invite_desc;// ] => Array
	// (
	// [1] => 点击加入就送30元，提高医生专业服务收入的医患交流APP！http://ememed.net/f/s/xui8ituu/1.html
	// [2] => 点击加入就送30元，提高医生专业服务收入的医患交流APP！http://ememed.net/f/s/xui8ituu/2.html
	// [3] =>
	// Hi,我发现【薏米网】不错,提高医生专业服务收入的医患交流APP！加入即送30元！http://ememed.net/f/s/xui8ituu/3.html
	// )

	private String qr_img;// ] => http://www.ememed.net/uploads/qr/56898.png
	@SerializedName("invite_number")
	private String inviteNumber;

	public Map<String, String> getInvite_desc() {
		return invite_desc;
	}

	public void setInvite_desc(Map<String, String> invite_desc) {
		this.invite_desc = invite_desc;
	}

	public String getQr_img() {
		return qr_img;
	}

	public void setQr_img(String qr_img) {
		this.qr_img = qr_img;
	}

	public String getInviteNumber() {
		return inviteNumber;
	}
	
	

}
