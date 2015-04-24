package net.ememed.doctor2.baike;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.adapter.FragmentViewPageAddpter;
import net.ememed.doctor2.baike.entity.BaikeMemberInfo;
import net.ememed.doctor2.baike.fragment.BaikeHomeFragment;
import net.ememed.doctor2.baike.fragment.BaikeMineFragment;
import net.ememed.doctor2.baike.fragment.BaikeRelateMeFragment;
import net.ememed.doctor2.network.HttpUtil;

public class BaikeHomeActivity extends BasicActivity implements OnPageChangeListener{
//	private FrameLayout home_baike_layout;
	private ViewPager viewPager;
	private FragmentViewPageAddpter addpter;
	private ArrayList<Fragment> views = new ArrayList<Fragment>();
	
	private ImageButton pic_tab_home;
	private ImageButton pic_tab_relate_me;
	private ImageButton pic_tab_mine;
	private TextView tv_baike_home;
	private TextView tv_baike_relate_me;
	private TextView tv_baike_mine;

	private FragmentManager fm = getSupportFragmentManager();

//	private Fragment lastFragment = null;
	private BaikeHomeFragment baikeHomeFragment = null;
	private BaikeRelateMeFragment baikeRelateMeFragment = null;
	private BaikeMineFragment baikeMineFragment = null;
//	private int currentTabIndex = 0;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);

		setContentView(R.layout.activity_baike_new);
	}

	@Override
	protected void setupView() {
		super.setupView();

//		home_baike_layout = (FrameLayout) findViewById(R.id.home_baike_layout);
		viewPager = (ViewPager) findViewById(R.id.vPager);
		
		pic_tab_home = (ImageButton) findViewById(R.id.pic_tab_home);
		pic_tab_relate_me = (ImageButton) findViewById(R.id.pic_tab_relate_me);
		pic_tab_mine = (ImageButton) findViewById(R.id.pic_tab_mine);
		tv_baike_home = (TextView) findViewById(R.id.tv_baike_home);
		tv_baike_relate_me = (TextView) findViewById(R.id.tv_baike_relate_me);
		tv_baike_mine = (TextView) findViewById(R.id.tv_baike_mine);

		/*FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		baikeHomeFragment = new BaikeHomeFragment();
		ft.add(R.id.home_baike_layout, baikeHomeFragment, BaikeHomeFragment.class.getSimpleName());
		ft.show(baikeHomeFragment);*/

		/*baikeRelateMeFragment = new BaikeRelateMeFragment();
		ft.add(R.id.home_baike_layout, baikeRelateMeFragment,
				BaikeRelateMeFragment.class.getSimpleName());
		ft.hide(baikeRelateMeFragment);*/

		/*lastFragment = baikeHomeFragment;
		ft.commit();*/
		
		baikeHomeFragment = new BaikeHomeFragment();
		views.add(baikeHomeFragment);
		baikeRelateMeFragment = new BaikeRelateMeFragment();
		views.add(baikeRelateMeFragment);
		baikeMineFragment = new BaikeMineFragment();
		views.add(baikeMineFragment);
		addpter = new FragmentViewPageAddpter(getSupportFragmentManager(),views);
		viewPager.setAdapter(addpter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setOffscreenPageLimit(3);// 缓存多少个页面
		
		pic_tab_home.setSelected(true);
		tv_baike_home.setTextColor(getResources().getColor(R.color.topbg_green));
	}

	
	/*public void doClick(View view) {
		try {
			int id = view.getId();
			if (id == R.id.tab_baike_home) {
				changeTabAction(R.id.tab_baike_home);
			} else if (id == R.id.tab_baike_relate_me) {
				changeTabAction(R.id.tab_baike_relate_me);
			} else if (id == R.id.tab_baike_mine) {
				changeTabAction(R.id.tab_baike_mine);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	public void doClick(View view) {
	try {
		int id = view.getId();
		if (id == R.id.tab_baike_home) {
			changeTab(0);
		} else if (id == R.id.tab_baike_relate_me) {
			changeTab(1);
		} else if (id == R.id.tab_baike_mine) {
			changeTab(2);
		} 
	} catch (Exception e) {
		e.printStackTrace();
	}
}

	/*private void changeTabAction(int layoutId) {
		if (fm == null) {
			fm = getSupportFragmentManager();
		}
		FragmentTransaction ft = fm.beginTransaction();
		
		cancelRequest();
		
		if (layoutId == R.id.tab_baike_home) {
			if (lastFragment != baikeHomeFragment) {
				ft.hide(lastFragment);
				if (baikeHomeFragment == null) {
					
					baikeHomeFragment = new BaikeHomeFragment();
					ft.add(R.id.home_baike_layout, baikeHomeFragment,
							BaikeHomeFragment.class.getSimpleName());
				}
				ft.show(baikeHomeFragment);
				ft.commit();
				lastFragment = baikeHomeFragment;

				pic_tab_home.setSelected(true);
				pic_tab_relate_me.setSelected(false);
				pic_tab_mine.setSelected(false);
				tv_baike_home.setTextColor(getResources().getColor(R.color.topbg_green));
				tv_baike_relate_me.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_mine.setTextColor(getResources().getColor(R.color.grayness));
				

				currentTabIndex = 0;
			}

		} else if (layoutId == R.id.tab_baike_relate_me) {

			if (lastFragment != baikeRelateMeFragment) {
				ft.hide(lastFragment);
				if (baikeRelateMeFragment == null) {
					
					baikeRelateMeFragment = new BaikeRelateMeFragment();
					ft.add(R.id.home_baike_layout, baikeRelateMeFragment,
							BaikeRelateMeFragment.class.getSimpleName());
				}
				ft.show(baikeRelateMeFragment);
				ft.commit();
				lastFragment = baikeRelateMeFragment;

				pic_tab_home.setSelected(false);
				pic_tab_relate_me.setSelected(true);
				pic_tab_mine.setSelected(false);
				tv_baike_home.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_mine.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_relate_me.setTextColor(getResources().getColor(R.color.topbg_green));

				currentTabIndex = 1;
			}
		} else if (layoutId == R.id.tab_baike_mine) {
			if (lastFragment != baikeMineFragment) {
				ft.hide(lastFragment);
				if (baikeMineFragment == null) {
					
					baikeMineFragment = new BaikeMineFragment();
					ft.add(R.id.home_baike_layout, baikeMineFragment,
							BaikeMineFragment.class.getSimpleName());
				}
				ft.show(baikeMineFragment);
				ft.commit();
				lastFragment = baikeMineFragment;

				pic_tab_mine.setSelected(true);
				pic_tab_home.setSelected(false);
				pic_tab_relate_me.setSelected(false);
				tv_baike_home.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_relate_me.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_mine.setTextColor(getResources().getColor(R.color.topbg_green));

				currentTabIndex = 2;
			}
		}
	}*/

	private void cancelRequest() {
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_my_baike);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_says_list);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_share);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_says_list);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_relateme_list);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_myjoin_info);
	}

	public void praise(View v) {
		Intent intent = new Intent(this, BaikeShareListActivity.class);
		intent.putExtra("type", 1);
		startActivity(intent);
	}

	public void comment(View v) {
		Intent intent = new Intent(this, BaikeShareListActivity.class);
		intent.putExtra("type", 2);
		startActivity(intent);
	}

	public void share(View v) {
		Intent intent = new Intent(this, BaikeShareListActivity.class);
		intent.putExtra("type", 3);
		startActivity(intent);
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
		changeTab(arg0);
	}
	
	private void changeTab(int position){
		cancelRequest();
		
		if (0 == position) {
			
				viewPager.setCurrentItem(0);
				pic_tab_home.setSelected(true);
				pic_tab_relate_me.setSelected(false);
				pic_tab_mine.setSelected(false);
				tv_baike_home.setTextColor(getResources().getColor(R.color.topbg_green));
				tv_baike_relate_me.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_mine.setTextColor(getResources().getColor(R.color.grayness));
				
//				currentTabIndex = 0;

		} else if (1 == position) {
				viewPager.setCurrentItem(1);
				pic_tab_home.setSelected(false);
				pic_tab_relate_me.setSelected(true);
				pic_tab_mine.setSelected(false);
				tv_baike_home.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_mine.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_relate_me.setTextColor(getResources().getColor(R.color.topbg_green));

//				currentTabIndex = 1;
			
		} else if (2 == position) {
			
			viewPager.setCurrentItem(2);
				pic_tab_mine.setSelected(true);
				pic_tab_home.setSelected(false);
				pic_tab_relate_me.setSelected(false);
				tv_baike_home.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_relate_me.setTextColor(getResources().getColor(R.color.grayness));
				tv_baike_mine.setTextColor(getResources().getColor(R.color.topbg_green));

//				currentTabIndex = 2;
		}
	}
}
