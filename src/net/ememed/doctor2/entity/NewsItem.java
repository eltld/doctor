package net.ememed.doctor2.entity;

public class NewsItem {
	private String ID;
	private String TITLE;
	private String SUBTITLE;
	private String PIC;
	private String PICEXT1;
	private String PICEXT2;
	private String PICEXT3;
	private String TYPE;
	private String FURL;
	private String UPDATETIME;
	private String ALLOWCOMMENT;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getTITLE() {
		return TITLE;
	}
	public void setTITLE(String tITLE) {
		TITLE = tITLE;
	}
	public String getPIC() {
		return PIC;
	}
	public void setPIC(String pIC) {
		PIC = pIC;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public String getFURL() {
		return FURL;
	}
	public void setFURL(String fURL) {
		FURL = fURL;
	}
	public String getUPDATETIME() {
		return UPDATETIME;
	}
	public void setUPDATETIME(String uPDATETIME) {
		UPDATETIME = uPDATETIME;
	}
	public String getSUBTITLE() {
		return SUBTITLE;
	}
	public void setSUBTITLE(String sUBTITLE) {
		SUBTITLE = sUBTITLE;
	}
	
	public String getPICEXT1() {
		return PICEXT1;
	}
	public void setPICEXT1(String pICEXT1) {
		PICEXT1 = pICEXT1;
	}
	
	public String getPICEXT2() {
		return PICEXT2;
	}
	public void setPICEXT2(String pICEXT2) {
		PICEXT2 = pICEXT2;
	}
	
	public String getPICEXT3() {
		return PICEXT3;
	}
	public void setPICEXT3(String pICEXT3) {
		PICEXT3 = pICEXT3;
	}
	public String getALLOWCOMMENT() {
		return ALLOWCOMMENT;
	}
	public void setALLOWCOMMENT(String aLLOWCOMMENT) {
		ALLOWCOMMENT = aLLOWCOMMENT;
	}
	
}
