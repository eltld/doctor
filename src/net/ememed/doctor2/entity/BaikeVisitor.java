package net.ememed.doctor2.entity;

import java.io.Serializable;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * 百科-访问者
 * 
 * @author pch
 * 
 */
public class BaikeVisitor implements Serializable {

	@SerializedName("MEMBERID")
	private String memberId;
	@SerializedName("AVATAR")
	private String portrait;
	@SerializedName("UTYPE")
	private String type;
	@SerializedName("REALNAME")
	private String name;
	@SerializedName("CREATE_TIME")
	private String time;
	@SerializedName("ALLOWFREECONSULT")
	private String grade;
	@SerializedName("NOTE_NAME")
	private String noteName;
	@SerializedName("DESCRIPTION")
	private String description;
	@SerializedName("IS_STAR")
	private String star;

	public String getMemberId() {
		return memberId;
	}

	public String getPortrait() {
		return portrait;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getTime() {
		return time;
	}

	public String getGrade() {
		return grade;
	}

	public boolean isDoctor() {
		return type.equals("doctor") ? true : false;
	}

	public String getNoteName() {
		return noteName;
	}

	public String getDescription() {
		return description;
	}

	public String getStar() {
		return TextUtils.isEmpty(star) ? "0" : "1";
	}

}
