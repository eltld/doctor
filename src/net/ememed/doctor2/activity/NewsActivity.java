package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CustomViewPager;
import com.viewpagerindicator.TabPageIndicator;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.db.NewsTypeTable;
import net.ememed.doctor2.entity.NewsTypeEntry;
import net.ememed.doctor2.entity.NewsTypeInfo;
import net.ememed.doctor2.fragment.ConsultInfoFragment;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsActivity extends BasicActivity implements OnPageChangeListener{

	private Handler mHandler;
	private FrameLayout mContentView;
	private PagerAdapter mPagerAdapter;
	private CustomViewPager mViewPager;
	private TabPageIndicator mIndicator;
	private TextView tv_title;
	private ImageView btn_back;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {

		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_news);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText("新闻资讯");
		
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager(),this);
		
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mViewPager = (CustomViewPager) findViewById(R.id.pager);
		NewsTypeTable newTypeTable = new NewsTypeTable();
		ArrayList<NewsTypeEntry> newsTypes = newTypeTable.getNewsTypes();
		if (null == newsTypes || newsTypes.size() == 0) {
			NewsTypeEntry enTypeEntry1 = new NewsTypeEntry();//首次进入，目前先手动保存几个tab进去 ，避免用户首次使用一片空白，另外fragment里边嵌套fragment，TabPageIndicator绘制高度不正确，回头有时间再修改
			
			enTypeEntry1.setID("0");
			enTypeEntry1.setTITLE("热点");
			newTypeTable.saveNewsType(enTypeEntry1);
			NewsTypeEntry enTypeEntry2 = new NewsTypeEntry();
			enTypeEntry2.setID("1");
			enTypeEntry2.setTITLE("新闻");
			newTypeTable.saveNewsType(enTypeEntry2);
			NewsTypeEntry enTypeEntry3 = new NewsTypeEntry();
			enTypeEntry3.setID("4");
			enTypeEntry3.setTITLE("活动");
			newTypeTable.saveNewsType(enTypeEntry3);
			NewsTypeEntry enTypeEntry4 = new NewsTypeEntry();
			enTypeEntry4.setID("3");
			enTypeEntry4.setTITLE("人文");
			newTypeTable.saveNewsType(enTypeEntry4);
			newsTypes = newTypeTable.getNewsTypes();
			NewsTypeEntry enTypeEntry5 = new NewsTypeEntry();
			enTypeEntry5.setID("2");
			enTypeEntry5.setTITLE("学术");
			newTypeTable.saveNewsType(enTypeEntry5);
			
			newsTypes = newTypeTable.getNewsTypes();
		}
		getNewsType();
		for (int i = 0; i < newsTypes.size(); i++) {
			Bundle bundle = new Bundle();
			bundle.putString("news_title", newsTypes.get(i).getTITLE());
			bundle.putString("type_id", newsTypes.get(i).getID());
			mPagerAdapter.addFragment(newsTypes.get(i).getTITLE(),
					ConsultInfoFragment.class, bundle);
		}
	
		mViewPager.setOffscreenPageLimit(newsTypes.size());// 缓存多少个页面
		mViewPager.setAdapter(mPagerAdapter);
		mIndicator.setViewPager(mViewPager);
	}
	
	private void getNewsType() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "doctor");
		params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_news_type,
				NewsTypeInfo.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						if (null != response) {
							NewsTypeInfo respEntry = (NewsTypeInfo) response;
							if (respEntry.getSuccess() == 1) {
								List<NewsTypeEntry> typsList = respEntry
										.getData();
								// 保存资讯目录
								NewsTypeTable table = new NewsTypeTable();
								table.clearTable();
								for (int i = 0; i < typsList.size(); i++) {
									table.saveNewsType(typsList.get(i));
								}
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
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
			Fragment fra = Fragment.instantiate(mContext,
					fragmentInfo.clss.getName(), fragmentInfo.args);
			if (!fra_list.contains(fra))
				fra_list.add(fra);
			return fra;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragments.get(position).tag;
		}
		/*@Override  
	    public int getItemPosition(Object object) {
	        return PagerAdapter.POSITION_NONE;  
	    } */
		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
