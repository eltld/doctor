package net.ememed.doctor2.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.ememed.doctor2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PatientGroupAllExListAdapter extends BaseExpandableListAdapter {
	private List<String> group;
	private List<List<String[]>> child;
	private Context context;
	private LinearLayout ll_group;
	private LinearLayout ll_child;
	private TextView grouptxt;
	private ImageView avatar;
	private TextView name;
	private TextView content;
	private ImageView group_selector_icon;

	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public PatientGroupAllExListAdapter(Context context,
			ImageLoader imageLoader, DisplayImageOptions options) {
		super();
		// TODO Auto-generated constructor stub
		// this.group=group;
		// this.child=child;
		this.context = context;
		this.imageLoader = imageLoader;
		this.options = options;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return child.get(arg0).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return arg1;
	}

	@Override
	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		if (child == null) {
			return 0;
		}
		return child.get(arg0).size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return group.get(arg0);
	}

	@Override
	public int getGroupCount() {
		if (group == null) {
			return 0;
		}
		return group.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		ll_group = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.patient_group_item_group, null);
		grouptxt = (TextView) ll_group.findViewById(R.id.patient_group_txt);
		grouptxt.setText(group.get(arg0));
		group_selector_icon = (ImageView) ll_group
				.findViewById(R.id.group_selector_icon);
		if (arg1) {
			group_selector_icon.setBackgroundResource(R.drawable.group_zk_icon);
		} else {
			group_selector_icon.setBackgroundResource(R.drawable.group_ss_icon);
		}
		return ll_group;
	}

	@Override
	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		// TODO Auto-generated method stub
		ll_child = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.patient_group_item, null);
		avatar = (ImageView) ll_child.findViewById(R.id.patient_group_avatar);
		imageLoader.displayImage(child.get(arg0).get(arg1)[0], avatar, options);
		name = (TextView) ll_child.findViewById(R.id.patient_group_name);
		name.setText(child.get(arg0).get(arg1)[5] == null ? child.get(arg0)
				.get(arg1)[1] : child.get(arg0).get(arg1)[5]);
		content = (TextView) ll_child.findViewById(R.id.patient_group_content);
		content.setText(child.get(arg0).get(arg1)[2]);

		return ll_child;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public void setData(List<String> group, List<List<String[]>> child) {
		if (group == null) {
			group = new ArrayList<String>();
			child = new ArrayList<List<String[]>>();
		}
		this.group = group;
		this.child = child;
		notifyDataSetChanged();
	}

}
