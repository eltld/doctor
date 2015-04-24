package net.ememed.doctor2.entity;

import java.util.List;

public class SearchGroupEntity extends BaseEntity{
	private List<SearchGroupInfo> data;
	private int count;
	private int pages;
	
	public List<SearchGroupInfo> getData() {
		return data;
	}

	public void setData(List<SearchGroupInfo> data) {
		this.data = data;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	} 
}
