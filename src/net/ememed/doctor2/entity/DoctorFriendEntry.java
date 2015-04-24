package net.ememed.doctor2.entity;

import java.util.List;

public class DoctorFriendEntry {
	private int success;
	private String errormsg;
	private int count;
	private List<DoctorFriendInfo> data;

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<DoctorFriendInfo> getData() {
		return data;
	}

	public void setData(List<DoctorFriendInfo> data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success == 1;
	}

	public boolean isEmpty() {
		return data == null || data.size() == 0;
	}

}
