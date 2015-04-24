package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.entity.MyFansInfo;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FansAdapter extends BaseAdapter{
	private List<MyFansInfo> listItems;
	private BasicActivity activity;
	
	public FansAdapter(List<MyFansInfo> listItems, BasicActivity activity){
		if(null == listItems){
			listItems = new ArrayList<MyFansInfo>();
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.fans_item, null);
			holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.tv_zhuanke = (TextView) convertView.findViewById(R.id.tv_zhuanke);
			holder.iv_red_point = (ImageView) convertView.findViewById(R.id.iv_red_point);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		MyFansInfo info = listItems.get(position);
		
		if(info.getUTYPE().equals("doctor")){
			if(!TextUtils.isEmpty(info.getDOCTORINFO().getALLOWFREECONSULT())){
				holder.tv_zhuanke.setVisibility(View.VISIBLE);
				if("1".equals(info.getDOCTORINFO().getALLOWFREECONSULT())){
					holder.tv_zhuanke.setText("全科医生");
				} else {
					holder.tv_zhuanke.setText("专科医生");
				}
			} else {
				holder.tv_zhuanke.setVisibility(View.GONE);
			}
			
			activity.imageLoader.displayImage(info.getDOCTORINFO().getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
			holder.tv_name.setText(info.getDOCTORINFO().getREALNAME());
			
			holder.tv_content.setText(info.getDOCTORINFO().getHOSPITALNAME());
			
			if(!TextUtils.isEmpty(info.getHAVE_NEW_MSG()) && "1".equals(info.getHAVE_NEW_MSG())){
				holder.iv_red_point.setVisibility(View.VISIBLE);
			} else {
				holder.iv_red_point.setVisibility(View.GONE);
			}
		} else {
			holder.tv_zhuanke.setVisibility(View.GONE);
			activity.imageLoader.displayImage(info.getUSERINFO().getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
			holder.tv_name.setText(info.getUSERINFO().getREALNAME());
		}
		return convertView;
	}
	
	public void change(List<MyFansInfo> lists) {
		if (lists == null) {
			lists = new ArrayList<MyFansInfo>();
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
		TextView tv_content;
		TextView tv_zhuanke;
		ImageView iv_red_point;
	}
}
