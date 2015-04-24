package net.ememed.doctor2.entity;

public class OrderOpenCount {
	//ordertype 想获取的订单类型。'1'=>'文字咨询','2'=>'预约通话','3'=>'名医加号',
	//'4'=>'上门会诊','5'=>'院内陪诊','7'=>'免费挂号','11'=>'体检报告解读',
	//'12'=>'个人健康季评','14'=>'住院直通车','15'=>'私人医生服务服务购买','16'=>'自定义订单'
	
	 private int o_1;		//o后面的数字与订单类型号一一对应	
	 private int o_2;
	 private int o_3;
	 private int o_4;
	 private int o_14;
	 private int o_15;
	 private int o_16;
	 
	public int getO_1() {
		return o_1;
	}
	public void setO_1(int o_1) {
		this.o_1 = o_1;
	}
	public int getO_2() {
		return o_2;
	}
	public void setO_2(int o_2) {
		this.o_2 = o_2;
	}
	public int getO_3() {
		return o_3;
	}
	public void setO_3(int o_3) {
		this.o_3 = o_3;
	}
	public int getO_4() {
		return o_4;
	}
	public void setO_4(int o_4) {
		this.o_4 = o_4;
	}
	public int getO_14() {
		return o_14;
	}
	public void setO_14(int o_14) {
		this.o_14 = o_14;
	}
	public int getO_15() {
		return o_15;
	}
	public void setO_15(int o_15) {
		this.o_15 = o_15;
	}
	public int getO_16() {
		return o_16;
	}
	public void setO_16(int o_16) {
		this.o_16 = o_16;
	}
	 
	 
}
