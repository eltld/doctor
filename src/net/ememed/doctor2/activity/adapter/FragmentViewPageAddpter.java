package net.ememed.doctor2.activity.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentViewPageAddpter extends FragmentPagerAdapter{

	
	private List<Fragment> fragments;
	
	public FragmentViewPageAddpter(FragmentManager m,List<Fragment> fragments) {
		super(m);
		this.fragments = fragments;
	}
	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	
//	private Context mContext;
//	private final ArrayList<FragmentInfo> fragments = new ArrayList<FragmentInfo>();
//	private FragmentManager fm;
//	private ArrayList<Fragment> fra_list;
//
//	protected final class FragmentInfo {
//		private final String tag;
//		private final Class<?> clss;
//		private final Bundle args;
//
//		protected FragmentInfo(String _tag, Class<?> _class, Bundle _args) {
//			tag = _tag;
//			clss = _class;
//			args = _args;
//		}
//	}
//
//	public FragmentViewPageAddpter(FragmentManager fm, Context context) {
//		super(fm);
//		this.fm = fm;
//		this.mContext = context;
//		fra_list = new ArrayList<Fragment>();
//	}
//
//	public void clearFragment() {
//		FragmentTransaction ft = fm.beginTransaction();
//		for (Fragment f : fra_list) {
//			ft.remove(f);
//		}
//		ft.commit();
//		ft = null;
//		fm.executePendingTransactions();// 立刻执行以上命令（commit）
//	}
//
//	public void addFragment(String tag, Class<?> clss, Bundle args) {
//		FragmentInfo fragmentInfo = new FragmentInfo(tag, clss, args);
//		fragments.add(fragmentInfo);
//	}
//
//	@Override
//	public Fragment getItem(int arg0) {
//		FragmentInfo fragmentInfo = fragments.get(arg0);
//
//		Fragment fra = Fragment.instantiate(mContext,
//				fragmentInfo.clss.getName(), fragmentInfo.args);
//		if (!fra_list.contains(fra))
//			fra_list.add(fra);
////		if (fragmentInfo.clss.getName().equals(
////				DoctorChatFragment.class.getName())) {
////			// mChatFragment = (DoctorChatFragment) fra;
////		} else if (fragmentInfo.clss.getName().equals(
////				DoctorOrderFragment.class.getName())) {
////			mOrderFragment = (DoctorOrderFragment) fra;
////		}
//		return fra;
//	}
//
//	@Override
//	public CharSequence getPageTitle(int position) {
//		return fragments.get(position).tag;
//	}
//
//	@Override
//	public int getCount() {
//		return fragments.size();
//	}
	
	
}
