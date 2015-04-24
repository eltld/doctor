package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.OrderDetailActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class GridTimeAdapter extends BaseAdapter{

	private ArrayList<String> titles;
	private Context context;

	public GridTimeAdapter(ArrayList<String> titles,Context context) {
		super();
		this.titles = titles;
		this.context = context;
	}

	@Override
	public int getCount() {
		return titles == null ? 0 : titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles == null ? null : titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView != null) {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		} else {
			holder = new ViewHolder();
			view = LayoutInflater.from(context.getApplicationContext()).inflate(
					R.layout.u_dialog_bt_time_item, null);
			holder.service_time = (Button) view
					.findViewById(R.id.bt_service_time);
			view.setTag(holder);
		}
		holder.service_time.setText(titles.get(position));
		// 点击改变选中listItem的背景色
		if (clickTemp == position) {
			holder.service_time.setTextColor(context
					.getResources().getColor(R.color.white));
			holder.service_time
					.setBackgroundResource(R.drawable.ic_login_pressed);
		} else {
			holder.service_time.setTextColor(context
					.getResources().getColor(R.color.black));
			holder.service_time
					.setBackgroundResource(R.drawable.button_white_bg);
		}
		return view;
	}

	private int clickTemp = -1;

	// 标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
	}

	public void change(ArrayList<String> titles) {
		this.titles = titles;
		notifyDataSetChanged();
	}

	public class ViewHolder {
		Button service_time;
	}
	
}
