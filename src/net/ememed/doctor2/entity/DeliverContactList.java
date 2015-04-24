package net.ememed.doctor2.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeliverContactList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ContactEntry> entry;
	
	public DeliverContactList(List<ContactEntry> entry){
		this.entry = entry;
	}

	public List<ContactEntry> getEntry() {
		return entry;
	}

	public void setEntry(List<ContactEntry> entry) {
		this.entry = entry;
	}
}
