package net.ememed.doctor2.entity;

import java.io.Serializable;
import net.ememed.doctor2.R;

public class Problem implements Serializable{
	private static final long serialVersionUID = -2358761615403014687L;
	private int ID;
	public String TITLE;
	public String CONTENT;
	private String PIC;
	private String FURL;
	private String PICURL;
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getPIC() {
		return PIC;
	}
	public void setPIC(String pIC) {
		PIC = pIC;
	}
	public String getFURL() {
		return FURL;
	}
	public void setFURL(String fURL) {
		FURL = fURL;
	}
	public String getPICURL() {
		return PICURL;
	}
	public void setPICURL(String pICURL) {
		PICURL = pICURL;
	}
	
}
