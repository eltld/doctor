package net.ememed.doctor2.activity;

import java.util.ArrayList;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ServiceEvaluationActivity.PagerAdapter;
import net.ememed.doctor2.activity.ServiceEvaluationActivity.PagerAdapter.FragmentInfo;
import net.ememed.doctor2.db.NewsTypeTable;
import net.ememed.doctor2.entity.NewsTypeEntry;
import net.ememed.doctor2.fragment.ConsultInfoFragment;
import net.ememed.doctor2.fragment.HomeFragment;
import net.ememed.doctor2.fragment.ServiceEvaluationFragment;
import net.ememed.doctor2.widget.MyProgressDialog;

import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CustomViewPager;
import com.viewpagerindicator.TabPageIndicator;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConsultInfoActivity extends BasicActivity implements OnPageChangeListener{
	private PagerAdapter mPagerAdapter = null;
	private CustomViewPager mViewPager = null;
	private TabPageIndicator mIndicator = null;
	private Button btn;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_viewpager_setting_pwd);
		btn = (Button) findViewById(R.id.btn_addhealth);
		btn.setVisibility(View.GONE);
		TextView tv_title = (TextView)findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.contact_ememed));
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
		NewsTypeTable newTypeTable = new NewsTypeTable();
		if (null != newTypeTable.getNewsTypes() && newTypeTable.getNewsTypes().size() > 0) {
			 ArrayList<NewsTypeEntry>  newsTypes = newTypeTable.getNewsTypes();
			for (int i = 0; i < newsTypes.size(); i++) {
				Bundle bundle = new Bundle();
				bundle.putString("news_title", newsTypes.get(i).getTITLE());
				bundle.putString("type_id",newsTypes.get(i).getID());
				mPagerAdapter.addFragment(newsTypes.get(i).getTITLE(), ConsultInfoFragment.class, bundle);
			}
			mViewPager = (CustomViewPager) findViewById(R.id.pager);
			mViewPager.setOffscreenPageLimit(newsTypes.size());//缓存多少个页面 
			mViewPager.setAdapter(mPagerAdapter);
			mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
			mIndicator.setViewPager(mViewPager);	
		} else {
			
		}
		
//		mIndicator.setOnPageChangeListener(this);
		
	}
	
	public void doClick(View view){
		if (view.getId() == R.id.btn_back) {
			finish();
		} 
	}
	
	class PagerAdapter extends FragmentPagerAdapter {

		private Context mContext;
		private final ArrayList<FragmentInfo> fragments = new ArrayList<FragmentInfo>();
		private FragmentManager fm;
		private ArrayList<Fragment> fra_list;
		
		protected final class FragmentInfo {
			private final String tag;
	        private final Class<?> clss;
	        private final Bundle args;

	        protected  FragmentInfo(String _tag, Class<?> _class, Bundle _args) {
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
		
		
		public void clearFragment(){
			FragmentTransaction ft = fm.beginTransaction();
			for(Fragment f:fra_list){
				ft.remove(f);
			}
			ft.commit();
			ft=null;
			fm.executePendingTransactions();//立刻执行以上命令（commit）
		}
		
		public void addFragment(String tag, Class<?> clss, Bundle args) {
			FragmentInfo fragmentInfo = new FragmentInfo(tag, clss, args);
			fragments.add(fragmentInfo);
		}

		@Override
		public Fragment getItem(int arg0) {
			FragmentInfo fragmentInfo = fragments.get(arg0);
			Fragment fra = Fragment.instantiate(mContext, fragmentInfo.clss.getName(), fragmentInfo.args);
			if(!fra_list.contains(fra))
				fra_list.add(fra);
//			if (fragmentInfo.clss.getName().equals(CircleInfoFragment.class.getName())) {
//				mCircleFragment = (CircleInfoFragment) fra;
//			} else if (fragmentInfo.clss.getName().equals(CircleAddressBookFragment.class.getName())) {
//				mAddressBookFragment = (CircleAddressBookFragment) fra;
//			}
			return fra;
		}
		@Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).tag;
        }
		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void setupView() {
		super.setupView();
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
