package net.ememed.doctor2.entity;

import java.util.List;

public class NewsContentEntry {
	private List<NewsItem> newslist;
	private List<NewsItem> focuslist;
	public List<NewsItem> getNewslist() {
		return newslist;
	}
	public void setNewslist(List<NewsItem> newslist) {
		this.newslist = newslist;
	}
	public List<NewsItem> getFocuslist() {
		return focuslist;
	}
	public void setFocuslist(List<NewsItem> focuslist) {
		this.focuslist = focuslist;
	}
	
}
