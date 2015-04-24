package net.ememed.doctor2.entity;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CreateGroupInfo {
	private int success;// => 1
	private String errormsg;//] => 群组创建成功
	private CreateGroupEntry data;//] => Array
	Gson gson = new Gson();
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
	public CreateGroupEntry getData() {
		return data;//gson.fromJson(data, CreateGroupEntry.class);
	}
	public void setData(CreateGroupEntry data) {
		this.data = data;
	}
	
	
}
