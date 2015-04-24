package net.ememed.doctor2.fragment.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;



public class ContactAdapter extends BaseAdapter {

	List<ContactEntry> listItems;
	List<Boolean> isCheckList;

	Context context;
	Button bntNext;
	ImageLoader imageLoader = null;
	public ContactAdapter(List<ContactEntry> listItems,Context context,ImageLoader imageLoader,Button bntNext) {
		if(listItems==null){
			this.listItems = new ArrayList<ContactEntry>();
		}else{
			this.listItems = listItems;
		}
		this.context = context;
		this.bntNext = bntNext;
		this.imageLoader = imageLoader;
		isCheckList = new ArrayList<Boolean>();
		for (int i = 0; i < this.listItems.size(); i++) {
			isCheckList.add(false);
		}
	}

	public void setCheckList() {
		for (int i = 0; i < isCheckList.size(); i++) {
			isCheckList.set(i, true);
		}
		notifyDataSetChanged();
	}

	public void resetCheckList() {
		for (int i = 0; i < isCheckList.size(); i++) {
			isCheckList.set(i, false);
		}
		notifyDataSetChanged();
	}

	public List<Boolean> getCheckList() {
		return this.isCheckList;
	}

	public List<ContactEntry> getListItems() {
		return this.listItems;
	}

	@Override
	public int getCount() {
		if (listItems == null)
			return 0;
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_mass_message_contact, null);
			holder.iv_photo = (CircleImageView) convertView
					.findViewById(R.id.iv_photo);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.cb_choice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ContactEntry entry = listItems.get(position);
		
		
		imageLoader.displayImage(entry.getAVATAR(),
				holder.iv_photo, Util.getOptions_avatar());
		holder.tv_name.setText(entry.getREALNAME());
		if (isCheckList.get(position)) {
			holder.checkBox.setChecked(true);
		} else {
			holder.checkBox.setChecked(false);
		}

		final int pos = position;
		final CheckBox box = holder.checkBox;

		box.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!box.isChecked()) {
					box.setChecked(false);
					isCheckList.set(pos, false);
				} else {
					box.setChecked(true);
					isCheckList.set(pos, true);
				}
				bntNext.setText("下一步("+getCheckNumber()+")");
			}
		});

		return convertView;
	}
	
	public void addData(List<ContactEntry> listItems){
		isCheckList = new ArrayList<Boolean>();
		for (int i = 0; i < listItems.size(); i++) {
			isCheckList.add(false);
		}
		this.listItems.addAll(listItems);
		notifyDataSetChanged();
	}
	
	public int getCheckNumber(){
		
		int count = 0;
		for (int i = 0; i < isCheckList.size(); i++) {
			if(isCheckList.get(i)){
				count++;
			}
		}
		
		return count;
	}
	
	
}

class ViewHolder {
	CircleImageView iv_photo;
	TextView tv_name;
	CheckBox checkBox;
}
