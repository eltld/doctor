package net.ememed.doctor2.entity;

import java.io.Serializable;

public class OrderInformation implements Serializable{
	public String name;	
	public String type; 
	public int iconId;	//R.drawable.aaa
	public boolean isOpen;
	
	public OrderInformation(String name, String type, int iconId, boolean isOpen){
		this.name = name;
		this.type = type;
		this.iconId = iconId;
		this.isOpen = isOpen;
	}
	public OrderInformation(String name, int iconId, boolean isOpen){
		this.name = name;
		this.iconId = iconId;
		this.isOpen = isOpen;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	
	
}
