package net.ememed.doctor2.fragment;

import net.ememed.doctor2.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CommunicationFragment extends Fragment implements OnClickListener,
		OnPageChangeListener {

	private static final int[] TABS = { R.id.rb_friend, R.id.rb_group };
	private ViewPager mViewPager;
	private RadioGroup rg_tab;
	private RadioButton rb_friend, rb_group;

	private CommunicationPagerAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_communication, null);
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
		rg_tab = (RadioGroup) rootView.findViewById(R.id.rg_tab);
		rb_friend = (RadioButton) rootView.findViewById(R.id.rb_friend);
		rb_group = (RadioButton) rootView.findViewById(R.id.rb_group);
		rb_friend.setOnClickListener(this);
		rb_group.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);

		mAdapter = new CommunicationPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_friend:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.rb_group:
			mViewPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		rg_tab.check(TABS[position]);
	}

	private class CommunicationPagerAdapter extends FragmentPagerAdapter {

		private Fragment[] mFragments = { new CommunicationFriendFragment(),
				new CommunicationGroupFragment() };

		public CommunicationPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments[position];
		}

		@Override
		public int getCount() {
			return mFragments.length;
		}
	}

}
