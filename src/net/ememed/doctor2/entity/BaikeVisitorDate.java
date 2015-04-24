package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BaikeVisitorDate implements Serializable{

	private String date;
	@SerializedName("list")
	private List<BaikeVisitor> visitors;

	public String getDate() {
		return date;
	}

	public List<BaikeVisitor> getVisitors() {
		return visitors;
	}

}
