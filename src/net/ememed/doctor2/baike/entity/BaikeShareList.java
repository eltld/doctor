package net.ememed.doctor2.baike.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.ememed.doctor2.entity.BaseEntity;

public class BaikeShareList extends BaseEntity {

	@SerializedName("data")
	private List<BaikeShare> list;
	private String count;
	private Integer pages;

	public List<BaikeShare> getList() {
		return list;
	}

	public String getCount() {
		return count;
	}

	public Integer getPages() {
		return pages == null ? 1 : pages;
	}

}
