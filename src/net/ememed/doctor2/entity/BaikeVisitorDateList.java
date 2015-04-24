package net.ememed.doctor2.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BaikeVisitorDateList extends BaseEntity {

	@SerializedName("data")
	private List<BaikeVisitorDate> dates;
	@SerializedName("baike_visitors")
	private String visitors;
	@SerializedName("baike_visitors_today")
	private String visitorsToday;

	public List<BaikeVisitorDate> getDates() {
		return dates;
	}

	public String getVisitors() {
		return visitors;
	}

	public String getVisitorsToday() {
		return visitorsToday;
	}

}
