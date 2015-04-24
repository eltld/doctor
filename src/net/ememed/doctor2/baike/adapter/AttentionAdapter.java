package net.ememed.doctor2.baike.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.adapter.FansAdapter.ViewHolder;
import net.ememed.doctor2.baike.entity.MyAttentionInfo;
import net.ememed.doctor2.baike.entity.MyFansInfo;
import net.ememed.doctor2.entity.DoctorInfoEntry;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AttentionAdapter extends BaseAdapter{
	private List<MyAttentionInfo> listItems;
	private BasicActivity activity;
	
	public AttentionAdapter(List<MyAttentionInfo> listItems, BasicActivity activity){
		if(null == listItems){
			listItems = new ArrayList<MyAttentionInfo>();
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
		
		MyAttentionInfo info = listItems.get(position);
		DoctorInfoEntry doctorInfo = info.getDOCTORINFO();
		activity.imageLoader.displayImage(doctorInfo.getAVATAR(), holder.civ_photo, Util.getOptions_avatar());
	
		if(!TextUtils.isEmpty(doctorInfo.getREALNAME())){
			holder.tv_name.setText(doctorInfo.getREALNAME());
		} else {
			holder.tv_name.setText("");
		}
		if(!TextUtils.isEmpty(doctorInfo.getPROFESSIONAL())){
			holder.tv_zhuanke.setText(doctorInfo.getPROFESSIONAL());
		}else{
			holder.tv_zhuanke.setText("");
		}
		if(!TextUtils.isEmpty(doctorInfo.getHOSPITALNAME())){
			holder.tv_content.setText(doctorInfo.getHOSPITALNAME());
		}else{
			holder.tv_content.setText("");
		}
		if(!TextUtils.isEmpty(info.getHAVE_NEW_MSG()) && "1".equals(info.getHAVE_NEW_MSG())){
			holder.iv_red_point.setVisibility(View.VISIBLE);
		} else {
			holder.iv_red_point.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public void change(List<MyAttentionInfo> lists) {
		if (lists == null) {
			lists = new ArrayList<MyAttentionInfo>();
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
		ImageView iv_redPoint;
		ImageView iv_red_point;
	}
}
