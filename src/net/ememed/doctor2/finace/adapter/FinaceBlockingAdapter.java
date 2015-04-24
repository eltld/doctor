package net.ememed.doctor2.finace.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.RecentinEntry;
import net.ememed.doctor2.finace.bean.FinaceBlocking;
import net.ememed.doctor2.util.Util;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FinaceBlockingAdapter extends BaseAdapter {
	private List<FinaceBlocking> listItems;
	private Activity activity;
	
	public FinaceBlockingAdapter(ArrayList<FinaceBlocking> recentincome, Activity activity) {
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
		ImageView imageView1 = (ImageView) convertView.findViewById(R.id.image1);
		ImageView imageView2 = (ImageView) convertView.findViewById(R.id.image2);
		imageView1.setVisibility(View.GONE);
		imageView2.setVisibility(View.VISIBLE);
		money.setText("￥" + listItems.get(position).getAPPLY_MONEY());
		if (listItems.get(position).getSTATUS() == 0) {
			user_name.setText("提现处理中...");
			user_name.setTextColor(activity.getResources().getColor(R.color.tab_light_green));
			imageView2.setImageResource(R.drawable.pic_cash_ing);
		} else if (listItems.get(position).getSTATUS() == 1) {
			user_name.setText("提现成功");
			imageView2.setImageResource(R.drawable.pic_cash_success);
			user_name.setTextColor(activity.getResources().getColor(R.color.rose_hermosa));
		} else if (listItems.get(position).getSTATUS() == -1) {
			user_name.setText("提现不成功");
			imageView2.setImageResource(R.drawable.pic_cash_failed);
		}
		
		user_thing.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(listItems.get(position).getALIPAY_ACCOUNT())){
			user_thing.setText("支付宝");
		} else {
			String str1 = listItems.get(position).getBANKCARDNUM();
			String str = "";
			if(str1.length() > 4){
				str ="尾号"+ str1.substring(str1.length()-4)+" "+ listItems.get(position).getBANKNAME();
			} else {
				str = "尾号"+ str1 + " "+ listItems.get(position).getBANKNAME();
			}
			
			user_thing.setText(str);
		}
		
		user_time.setText(listItems.get(position).getUPDATETIME());
		return convertView;
	}

	public void change(List<FinaceBlocking> lists) {
		if (lists == null) {
			lists = new ArrayList<FinaceBlocking>();
		}
		this.listItems = lists;
		notifyDataSetChanged();
	}

	public void add(List<FinaceBlocking> list) {
		if (listItems == null) {
			listItems = new ArrayList<FinaceBlocking>();
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
