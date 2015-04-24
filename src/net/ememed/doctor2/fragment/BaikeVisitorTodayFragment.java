package net.ememed.doctor2.fragment;

import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.entity.BaikeVisitor;
import net.ememed.doctor2.entity.BaikeVisitorDate;
import net.ememed.doctor2.entity.BaikeVisitorDateList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BaikeVisitorTodayFragment extends Fragment implements OnItemClickListener {

	private QuickAdapter<BaikeVisitor> mAdapter;
	private TextView tv_tips;
	private ListView mListView;
	private View layoutView;

	public static Fragment newInstance(BaikeVisitorDateList dateList) {
		BaikeVisitorTodayFragment fragment = new BaikeVisitorTodayFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("data", dateList);
		fragment.setArguments(bundle);
		return fragment;
	}

	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.avatar_large)
			.showImageForEmptyUri(R.drawable.avatar_large).showImageOnFail(R.drawable.avatar_large)
			.resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisk(true).build();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_visitor_today, null);
		layoutView = rootView.findViewById(R.id.layout_listView);
		tv_tips = (TextView) rootView.findViewById(R.id.tv_tips);
		mListView = (ListView) rootView.findViewById(R.id.listView);

		if (getArguments() != null) {
			BaikeVisitorDateList dateList = (BaikeVisitorDateList) getArguments().getSerializable(
					"data");
			if (dateList.getDates().isEmpty()) {
				tv_tips.setVisibility(View.VISIBLE);
				layoutView.setVisibility(View.GONE);
			}
			List<BaikeVisitor> visitorList = null;
			if (dateList == null || dateList.getDates() == null || dateList.getDates().isEmpty()) {
				// no date
			} else
				visitorList = dateList.getDates().get(0).getVisitors();
			mAdapter = new QuickAdapter<BaikeVisitor>(getActivity(),
					R.layout.list_item_visitor_child, visitorList) {

				@Override
				protected void convert(BaseAdapterHelper helper, BaikeVisitor item) {
					helper.setImageUrl(R.id.iv_portrait, item.getPortrait(), options)
							.setText(R.id.tv_name, item.getName())
							.setText(R.id.tv_time, item.getTime())
							.setText(R.id.tv_grade, item.getGrade());
				}
			};
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BaikeVisitor visitor = mAdapter.getItem(position);
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
			startActivity(intent);
		}
		startActivity(intent);
	}
}
