package net.ememed.doctor2.entity;

public class YimiHuaHomeData {
	private int points_total;  // => 255
    private int points_now;  // => 255
    private int sign_times;  // => 3
    private String sign_lasttime;  // => 2015-04-14 16:49:32
    private int next_points;  // => 45
    private String is_signed;  // => 0
    
	public int getPoints_total() {
		return points_total;
	}
	public void setPoints_total(int points_total) {
		this.points_total = points_total;
	}
	public int getPoints_now() {
		return points_now;
	}
	public void setPoints_now(int points_now) {
		this.points_now = points_now;
	}
	public int getSign_times() {
		return sign_times;
	}
	public void setSign_times(int sign_times) {
		this.sign_times = sign_times;
	}
	public String getSign_lasttime() {
		return sign_lasttime;
	}
	public void setSign_lasttime(String sign_lasttime) {
		this.sign_lasttime = sign_lasttime;
	}
	public int getNext_points() {
		return next_points;
	}
	public void setNext_points(int next_points) {
		this.next_points = next_points;
	}
	public String getIs_signed() {
		return is_signed;
	}
	public void setIs_signed(String is_signed) {
		this.is_signed = is_signed;
	}
}
