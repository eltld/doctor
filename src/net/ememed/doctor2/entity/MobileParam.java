package net.ememed.doctor2.entity;

public class MobileParam {
	public String mobile_operator;			//运营商（如：中国移动）
	public String mobile_network;			//网络制式（如：4G）
	public String mobile_alias;				//机器别名（如：king）
	public String mobile_model;				//机器型号（如：三星、iphone）
	public String mobile_version;			//机器版本（如：iphone5s）
	public String mobile_sysversion;		//手机系统版本（如：android4.2）
	
	public String getMobile_operator() {
		return mobile_operator;
	}
	public void setMobile_operator(String mobile_operator) {
		this.mobile_operator = mobile_operator;
	}
	public String getMobile_network() {
		return mobile_network;
	}
	public void setMobile_network(String mobile_network) {
		this.mobile_network = mobile_network;
	}
	public String getMobile_alias() {
		return mobile_alias;
	}
	public void setMobile_alias(String mobile_alias) {
		this.mobile_alias = mobile_alias;
	}
	public String getMobile_model() {
		return mobile_model;
	}
	public void setMobile_model(String mobile_model) {
		this.mobile_model = mobile_model;
	}
	public String getMobile_version() {
		return mobile_version;
	}
	public void setMobile_version(String mobile_version) {
		this.mobile_version = mobile_version;
	}
	public String getMobile_sysversion() {
		return mobile_sysversion;
	}
	public void setMobile_sysversion(String mobile_sysversion) {
		this.mobile_sysversion = mobile_sysversion;
	}
}
