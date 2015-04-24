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
import android.widget.ImageView;
import android.widget.TextView;

public class BankcardAdapter extends BaseAdapter {
	
	private List<Map<String, Object>> listItems;
	private Activity activity;
	
	public BankcardAdapter(List<Map<String, Object>> listItems, Activity activity){
		if(null == listItems){
			listItems = new ArrayList<Map<String, Object>>();
		}
		
		this.listItems = listItems;
		this.activity = activity;
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
		
		Map<String, Object> map = listItems.get(position);
		String title = (String) map.get("title");
		
		if(title.equals("银行卡")){
			convertView = LayoutInflater.from(activity).inflate(R.layout.account_type, null);
			((TextView)convertView.findViewById(R.id.tv_account_type)).setText("银行卡");
			return convertView;
		} else if(title.equals("支付宝")){
			convertView = LayoutInflater.from(activity).inflate(R.layout.account_type, null);
			((TextView)convertView.findViewById(R.id.tv_account_type)).setText("支付宝");
			return convertView;
		}
		
		convertView = LayoutInflater.from(activity).inflate(R.layout.bankcard_item, null);
		
		TextView tv_bankName = (TextView) convertView.findViewById(R.id.tv_bankname);
		TextView tv_bankcard_num = (TextView) convertView.findViewById(R.id.tv_bankcard_num);
		ImageView iv_bank_pic = (ImageView) convertView.findViewById(R.id.iv_bank_pic);
		
		
		BankCardInfo tempInfo = (BankCardInfo)listItems.get(position).get("card");
		if(null != tempInfo.getALIPAY_ACCOUNT()){
			if(!TextUtils.isEmpty(tempInfo.getHOLDER())){
				tv_bankName.setText(tempInfo.getHOLDER());
			}else{
				tv_bankName.setText("支付宝");
			}
			tv_bankcard_num.setText(tempInfo.getALIPAY_ACCOUNT());
			iv_bank_pic.setImageResource(R.drawable.finace_zhifubao);
		}else{
			tv_bankName.setText(tempInfo.getBANKNAME());
			String cardId = tempInfo.getBANKCARDNUM();
			if(cardId.length() > 4){
				String str = cardId.substring(cardId.length() - 4);
				tv_bankcard_num.setText("尾号"+str+"  储蓄卡");
			}
		}
		return convertView;
	}
	
	public void change(List<Map<String, Object>> lists) {
		if (lists == null) {
			lists = new ArrayList<Map<String, Object>>();
		}
		this.listItems = lists;
		notifyDataSetChanged();
	}
	
	
	public void clear(){
		this.listItems.clear();
	}
}
