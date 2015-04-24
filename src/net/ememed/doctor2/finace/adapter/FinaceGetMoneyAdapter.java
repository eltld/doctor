package net.ememed.doctor2.finace.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.finace.bean.FinaceIncomeBean;
import net.ememed.doctor2.util.Util;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FinaceGetMoneyAdapter extends BaseAdapter {
	private List<FinaceIncomeBean> listItems;
	private Activity activity;
	
	public FinaceGetMoneyAdapter(List<FinaceIncomeBean> recentincome, Activity activity) {
		super();
		this.listItems = recentincome;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (listItems == null) {
			return 0;
		} else {
			return listItems.size();
		}
	}

	@Override
	public Object getItem(int position) {

		if (this.listItems == null) {
			return null;
		}
		return this.listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(R.layout.activity_finace_list_item, null);
		}
		TextView money = (TextView) convertView.findViewById(R.id.money);
		TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
		TextView user_thing = (TextView) convertView.findViewById(R.id.user_thing);
		TextView user_time = (TextView) convertView.findViewById(R.id.user_time);	
		
		money.setText("￥" + listItems.get(position).getMONEY());
//		user_name.setText(listItems.get(position).getORDERTYPE_NAME());
	/*	if (listItems.get(position).getLOGTYPE() == 1) {
			user_thing.setText("系统自动");
		} else if (listItems.get(position).getLOGTYPE() == 2){
			user_thing.setText("账户充值");
		} else if (listItems.get(position).getLOGTYPE() == 3){
			user_thing.setText("用户提现");
		} else if (listItems.get(position).getLOGTYPE() == 4){
			user_thing.setText("订单支付");
		} else if (listItems.get(position).getLOGTYPE() == 5){
			user_thing.setText("管理员手动修改");
		} else if (listItems.get(position).getLOGTYPE() == 7){
			user_thing.setText("扣除冻结现金");
		} else if (listItems.get(position).getLOGTYPE() == 8){
			user_thing.setText("打款给客户");
		} else if (listItems.get(position).getLOGTYPE() == 9){
			user_thing.setText("");
		} else if (listItems.get(position).getLOGTYPE() == 10){
			user_thing.setText("退款给客户");
		} else if (listItems.get(position).getLOGTYPE() == 11){
			user_thing.setText("医生服务费用");
		} else if (listItems.get(position).getLOGTYPE() == 12){
			user_thing.setText("医生资金结算");
		}*/
		user_thing.setText(listItems.get(position).getNOTES_DESC());
		user_time.setText(listItems.get(position).getCREATETIME());
		return convertView;
	}

	

	public void change(List<FinaceIncomeBean> lists) {
		if (lists == null) {
			lists = new ArrayList<FinaceIncomeBean>();
		}
		this.listItems = lists;
		notifyDataSetChanged();
	}

	public void add(List<FinaceIncomeBean> list) {
		if (listItems == null) {
			listItems = new ArrayList<FinaceIncomeBean>();
		}
		this.listItems.addAll(list);
		notifyDataSetChanged();
	}
	
	public void clear(){
		this.listItems.clear();
	}

	public void setList(List list) {
		this.listItems = list;
	}

}
