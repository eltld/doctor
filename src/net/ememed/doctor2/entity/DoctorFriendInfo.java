package net.ememed.doctor2.entity;

import android.text.TextUtils;

public class DoctorFriendInfo {
	private String MEMBERID; // => 5226
	private String FRIENDID; // => 85618
	private String FRIEND_UTYPE; // => doctor
	private String ADDTIME; // => 2014-10-21 11:37:59
	private String LEAVETIME; // =>
	private String STATUS; // => 1
	private String REALNAME; // => 陈龙 - 测试
	private String AVATAR; // =>
							// http://www.ememed.net/uploads/avatar/20140425/avatar_85618_1398396799_ryo9lMsTO8.jpg
	private String MOBILE; // => 13802735116\\

	private String IS_ATTENTION; // 1,
	private String IS_MY_FANS; // 1

	public String getIS_ATTENTION() {
		return IS_ATTENTION;
	}

	/**
	 * 是否关注
	 * 
	 * @return
	 */
	public boolean isAttention() {
		return !TextUtils.isEmpty(IS_ATTENTION) && IS_ATTENTION.equals("1");
	}

	public void setIS_ATTENTION(String iS_ATTENTION) {
		IS_ATTENTION = iS_ATTENTION;
	}

	public String getIS_MY_FANS() {
		return IS_MY_FANS;
	}

	public void setIS_MY_FANS(String iS_MY_FANS) {
		IS_MY_FANS = iS_MY_FANS;
	}

	public String getMEMBERID() {
		return MEMBERID;
	}

	public void setMEMBERID(String mEMBERID) {
		MEMBERID = mEMBERID;
	}

	public String getFRIENDID() {
		return FRIENDID;
	}

	public void setFRIENDID(String fRIENDID) {
		FRIENDID = fRIENDID;
	}

	public String getFRIEND_UTYPE() {
		return FRIEND_UTYPE;
	}

	public void setFRIEND_UTYPE(String fRIEND_UTYPE) {
		FRIEND_UTYPE = fRIEND_UTYPE;
	}

	public String getADDTIME() {
		return ADDTIME;
	}

	public void setADDTIME(String aDDTIME) {
		ADDTIME = aDDTIME;
	}

	public String getLEAVETIME() {
		return LEAVETIME;
	}

	public void setLEAVETIME(String lEAVETIME) {
		LEAVETIME = lEAVETIME;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public String getREALNAME() {
		return REALNAME;
	}

	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}

	public String getAVATAR() {
		return AVATAR;
	}

	public void setAVATAR(String aVATAR) {
		AVATAR = aVATAR;
	}

	public String getMOBILE() {
		return MOBILE;
	}

	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}

}
