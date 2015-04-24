package net.ememed.doctor2.finace.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.finace.bean.FinaceBlocking;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BankcardAdapter2 extends BaseAdapter {
	
	private List<BankCardInfo> listItems;
	private Activity activity;
	public int currentChoice = -1;
	
	public BankcardAdapter2(List<BankCardInfo> listItems, Activity activity){
		if(null == listItems){
			listItems = new ArrayList<BankCardInfo>();
		}
		
		this.listItems = listItems;
		this.activity = activity;
	}
	
	

	public int getCurrentChoice() {
		return currentChoice;
	}



	public void setCurrentChoice(int currentChoice) {
		this.currentChoice = currentChoice;
	}



	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(null == convertView){
			convertView = LayoutInflater.from(activity).inflate(R.layout.bankcard_item2, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_bank = (TextView) convertView.findViewById(R.id.tv_bank);
			viewHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			viewHolder.btn_bank = (ImageButton) convertView.findViewById(R.id.btn_bank);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		BankCardInfo tempInfo = listItems.get(position);
		if(position == currentChoice){
			viewHolder.btn_bank.setBackgroundResource(R.drawable.finace_gou);			
		}else{
			viewHolder.btn_bank.setBackgroundResource(R.drawable.finace_circle);			
		}
		if(null != tempInfo.getALIPAY_ACCOUNT()){
			viewHolder.tv_bank.setText("支付宝");
			viewHolder.tv_num.setText(tempInfo.getALIPAY_ACCOUNT());
			viewHolder.iv_pic.setImageResource(R.drawable.finace_zhifubao_2);
		}else{
			viewHolder.tv_bank.setText(tempInfo.getBANKNAME());
			viewHolder.iv_pic.setImageResource(R.drawable.finace_yinlian);
			String cardId = tempInfo.getBANKCARDNUM();
			if(cardId.length() > 4){
				String str = cardId.substring(cardId.length() - 4);
				viewHolder.tv_num.setText("尾号"+str+"  储蓄卡");
			}
		}
		return convertView;
	}
	
	public void change(List<BankCardInfo> lists) {
		if (lists == null) {
			lists = new ArrayList<BankCardInfo>();
		}
		this.listItems = lists;
		notifyDataSetChanged();
	}
	
	
	public void clear(){
		this.listItems.clear();
	}
	
	class ViewHolder{
		TextView tv_bank;
		TextView tv_num;
		ImageView iv_pic;
		ImageButton btn_bank;
	}
}
