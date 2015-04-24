package net.ememed.doctor2.entity;

import java.util.List;

/**
 * 积分赚取记录与积分兑换记录共用一个model
 * @author llj
 *
 */
public class YimiHuaExchangeRecordEntity extends BaseEntity{
	private List<YimiHuaExchangeRecordInfo> data;
	private int count;
	private int pages;

	public List<YimiHuaExchangeRecordInfo> getData() {
		return data;
	}

	public void setData(List<YimiHuaExchangeRecordInfo> data) {
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
