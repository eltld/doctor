package net.ememed.doctor2.fragment;

import java.util.ArrayList;

import com.viewpagerindicator.CustomViewPager;
import com.viewpagerindicator.CustomViewPager.OnPageChangeListener;
import com.viewpagerindicator.TabPageIndicator;

import net.ememed.doctor2.R;
import net.ememed.doctor2.db.NewsTypeTable;
import net.ememed.doctor2.entity.NewsTypeEntry;
import net.ememed.doctor2.fragment.InfoFragment.PagerAdapter;
import net.ememed.doctor2.fragment.InfoFragment.PagerAdapter.FragmentInfo;
import net.ememed.doctor2.util.BasicUIEvent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 所有联系人的主页
 * @author huangjk
 *
 */
public class ContactTabFragment extends Fragment implements Callback, BasicUIEvent {

	private PagerAdapter adapter;

	private ContactFragment doctorchatF;
	private MyContactFragment myContactF;
	private GroupsFragment groupsF;

	private CustomViewPager mViewPager;
	private PagerAdapter mPagerAdapter;

	private RadioGroup contact_tab_select;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_contact_tab, null);
		mPagerAdapter = new PagerAdapter(getChildFragmentManager(), getActivity());
		mPagerAdapter.addFragment("", ContactFragment.class, new Bundle());
		mPagerAdapter.addFragment("", MyContactFragment.class, new Bundle());
		mPagerAdapter.addFragment("", GroupsFragment.class, new Bundle());

		mViewPager = (CustomViewPager) view.findViewById(R.id.pager);
		contact_tab_select = (RadioGroup) view.findViewById(R.id.rg_contact_tab_select);

		mViewPager.setOffscreenPageLimit(3);// 缓存多少个页面
		mViewPager.setAdapter(mPagerAdapter);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				RadioButton childAt = (RadioButton) contact_tab_select.getChildAt(position);
				childAt.setChecked(true);
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		contact_tab_select.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_patient_tab:// 0
					mViewPager.setCurrentItem(0);
					break;
				case R.id.rb_single_tab:// 1
					mViewPager.setCurrentItem(1);
					break;
				case R.id.rb_groups_tab:// 2
					mViewPager.setCurrentItem(2);
					break;

				default:
					break;
				}

			}
		});
		mViewPager.setCurrentItem(0);
		return view;
	}

	public void cancelLoadingBar() {
		// if (null != mPullToRefreshLayout &&
		// mPullToRefreshLayout.isRefreshing()) {
		// mPullToRefreshLayout.setRefreshComplete();
		// }
		switch (mViewPager.getCurrentItem()) {
		case 0:
			ContactFragment item = (ContactFragment) mPagerAdapter.getItem(0);
			item.cancelLoadingBar();
			break;
		case 1:
			
			break;
		case 2:
			
			break;

		default:
			break;
		}
	}

	@Override
	public void execute(int mes, Object obj) {

	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	public void refresh() {

	}

	class PagerAdapter extends FragmentStatePagerAdapter {

		private Context mContext;
		private final ArrayList<FragmentInfo> fragments = new ArrayList<FragmentInfo>();
		private FragmentManager fm;
		private ArrayList<Fragment> fra_list;

		protected final class FragmentInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			protected FragmentInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		public PagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.fm = fm;
			this.mContext = context;
			fra_list = new ArrayList<Fragment>();
		}

		public void clearFragment() {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : fra_list) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();// 立刻执行以上命令（commit）
		}

		public void addFragment(String tag, Class<?> clss, Bundle args) {
			FragmentInfo fragmentInfo = new FragmentInfo(tag, clss, args);
			fragments.add(fragmentInfo);
		}

		@Override
		public Fragment getItem(int arg0) {
			FragmentInfo fragmentInfo = fragments.get(arg0);
			Fragment fra = Fragment.instantiate(mContext, fragmentInfo.clss.getName(), fragmentInfo.args);
			if (!fra_list.contains(fra))
				fra_list.add(fra);
			return fra;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragments.get(position).tag;
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}
}
