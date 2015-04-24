package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class User implements Serializable{
	private int MEMBERID;
	private String MOBILE;
	public int getMEMBERID() {
		return MEMBERID;
	}
	public void setMEMBERID(int mEMBERID) {
		MEMBERID = mEMBERID;
	}
	public String getMOBILE() {
		return MOBILE;
	}
	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}
}
