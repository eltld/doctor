package net.ememed.doctor2.message_center.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ememed.doctor2.R;
import net.ememed.doctor2.message_center.entity.SmsDetail;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {
		
		private List<SmsDetail> listItems;
		private Activity activity;
		
		public MessageListAdapter(List<SmsDetail> listItems, Activity activity){
			if(null == listItems){
				listItems = new ArrayList<SmsDetail>();
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
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_message, null);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			SmsDetail sms = (SmsDetail) listItems.get(position);
			if(!TextUtils.isEmpty(sms.getSMS_TYPE_NAME())){
				holder.tv_title.setText("【"+sms.getSMS_TYPE_NAME()+"】");
				holder.tv_time.setText(sms.getADDTIME());
				holder.tv_content.setText(sms.getCONTEXT());
			}
			return convertView;
		}
		
		public void change(List<SmsDetail> lists) {
			if (lists == null) {
				lists = new ArrayList<SmsDetail>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}
		
		
		public void clear(){
			this.listItems.clear();
		}
		
		class ViewHolder{
			private TextView tv_title; 
			private TextView tv_time; 
			private TextView tv_content; 
		}
	}
