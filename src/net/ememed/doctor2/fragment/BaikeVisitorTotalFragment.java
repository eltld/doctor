package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.entity.BaikeVisitor;
import net.ememed.doctor2.entity.BaikeVisitorDate;
import net.ememed.doctor2.entity.BaikeVisitorDateList;
import net.ememed.doctor2.widget.CircleImageView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class BaikeVisitorTotalFragment extends Fragment {

	private ExpandableListView mExpandableListView;
	private TextView tv_tips;
	private VisitorExpandableListAdapter mAdapter;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.avatar_large)
			.showImageForEmptyUri(R.drawable.avatar_large).showImageOnFail(R.drawable.avatar_large)
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisk(true).build();

	public static Fragment newInstance(BaikeVisitorDateList dateList) {
		BaikeVisitorTotalFragment fragment = new BaikeVisitorTotalFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("data", dateList);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_visitor_total, null);
		tv_tips = (TextView) rootView.findViewById(R.id.tv_tips);
		mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.exp_listView);
		if (getArguments() != null) {
			BaikeVisitorDateList dateList = (BaikeVisitorDateList) getArguments().getSerializable(
					"data");
			if (dateList.getDates().isEmpty()) {
				tv_tips.setVisibility(View.VISIBLE);
				mExpandableListView.setVisibility(View.GONE);
			}
			mAdapter = new VisitorExpandableListAdapter(getActivity(), dateList.getDates());
		}
		mExpandableListView.setAdapter(mAdapter);
		for (int i = 0; i < mAdapter.getGroupCount(); i++) {
			mExpandableListView.expandGroup(i);
		}
		mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id) {
				return true;
			}
		});
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
					int childPosition, long id) {
				BaikeVisitor visitor = mAdapter.getChild(groupPosition, childPosition);
				Intent intent = null;
				if (visitor.isDoctor()) {
					intent = new Intent(getActivity(), OtherBaikeActivity.class);
					intent.putExtra("other_doctor_id", visitor.getMemberId());
				} else {
					intent = new Intent(getActivity(), ContactInfoActivity.class);
					intent.putExtra("title", visitor.getName());
					intent.putExtra("tochat_userId", visitor.getMemberId());
					intent.putExtra("user_avatar", visitor.getPortrait());
					intent.putExtra("is_star", visitor.getStar());
					intent.putExtra("note_name", visitor.getNoteName());
					intent.putExtra("description", visitor.getDescription());
					intent.putExtra("index", 1);
				}
				startActivity(intent);
				return false;
			}
		});
		return rootView;
	}

	public class VisitorExpandableListAdapter extends BaseExpandableListAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
		private List<BaikeVisitorDate> mBaikeVisitorDates;

		public VisitorExpandableListAdapter(Context context,
				List<BaikeVisitorDate> baikeVisitorDates) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			setBaikeVisitorDates(baikeVisitorDates);
		}

		private void setBaikeVisitorDates(List<BaikeVisitorDate> baikeVisitorDates) {
			mBaikeVisitorDates = new ArrayList<BaikeVisitorDate>();
			for (int i = 0; i < baikeVisitorDates.size(); i++) {
				if (!baikeVisitorDates.get(i).getVisitors().isEmpty()) {
					mBaikeVisitorDates.add(baikeVisitorDates.get(i));
				}
			}
		}

		@Override
		public int getGroupCount() {
			return mBaikeVisitorDates.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return getGroup(groupPosition).getVisitors().size();
		}

		@Override
		public BaikeVisitorDate getGroup(int groupPosition) {
			return mBaikeVisitorDates.get(groupPosition);
		}

		@Override
		public BaikeVisitor getChild(int groupPosition, int childPosition) {
			return getGroup(groupPosition).getVisitors().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
				ViewGroup parent) {
			TextView tv_title;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_visitor_group, null);
				tv_title = (TextView) convertView.findViewById(R.id.tv_date);
				convertView.setTag(tv_title);
			} else {
				tv_title = (TextView) convertView.getTag();
			}
			tv_title.setText(getGroup(groupPosition).getDate());
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
				View convertView, ViewGroup parent) {
			ViewHolderChild viewHolderChild;
			if (convertView == null) {
				viewHolderChild = new ViewHolderChild();
				convertView = mInflater.inflate(R.layout.list_item_visitor_child, null);
				viewHolderChild.iv_portrait = (CircleImageView) convertView
						.findViewById(R.id.iv_portrait);
				viewHolderChild.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				viewHolderChild.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				viewHolderChild.tv_grade = (TextView) convertView.findViewById(R.id.tv_grade);
				convertView.setTag(viewHolderChild);
			} else {
				viewHolderChild = (ViewHolderChild) convertView.getTag();
			}
			BaikeVisitor visitor = getChild(groupPosition, childPosition);
			ImageLoader.getInstance().displayImage(visitor.getPortrait(),
					viewHolderChild.iv_portrait, options);
			viewHolderChild.tv_name.setText(visitor.getName());
			viewHolderChild.tv_time.setText(visitor.getTime());
			viewHolderChild.tv_grade.setText(visitor.getGrade());

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		class ViewHolderChild {
			CircleImageView iv_portrait;
			TextView tv_name;
			TextView tv_time;
			TextView tv_grade;
		}

	}
}
