package net.ememed.doctor2.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.text.TextUtils;

//List<BankCardInfo>
public class BankCard {
	private int success;
	private String errormsg;
	private List<BankCardInfo> data;
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

	public List<BankCardInfo> getData() {
		return data;
	}

	public void setData(List<BankCardInfo> data) {
		this.data = data;
	}


}
