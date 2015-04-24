package net.ememed.doctor2.entity;

import java.io.Serializable;

public class ContentExtSizeBean  implements Serializable{
	private String width;
	private String height;
	public String getWidth() {
		return width;
	}
	public String getHeight() {
		return height;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public void setHeight(String height) {
		this.height = height;
	}
}
