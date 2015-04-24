package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.List;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.entity.RewardListInfo;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RewardAdapter extends BaseAdapter{

	private List<RewardListInfo> listItems;
	private BasicActivity activity;

	public RewardAdapter(List<RewardListInfo> listItems, BasicActivity activity){
		if(null == listItems){
			listItems = new ArrayList<RewardListInfo>();
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.reward_item, null);
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		RewardListInfo info = listItems.get(position);
		activity.imageLoader.displayImage(info.getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
		holder.tv_name.setText(info.getREALNAME());
		holder.tv_money.setText("￥"+info.getMONEY());
		holder.tv_content.setText(info.getCONTENT());
		holder.tv_time.setText(info.getCREATE_TIME());
		return convertView;
	}

	public void change(List<RewardListInfo> lists) {
		if (lists == null) {
			lists = new ArrayList<RewardListInfo>();
		}
		this.listItems = lists;
		notifyDataSetChanged();
	}


	public void clear(){
		this.listItems.clear();
	}


	class ViewHolder{
		CircleImageView civ_photo;
		TextView tv_name;
		TextView tv_money;
		TextView tv_content;
		TextView tv_time;
	}
}


