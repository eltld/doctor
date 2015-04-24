package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.FragmentViewPageAddpter;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.DeliverContactList;
import net.ememed.doctor2.finace.MyViewPagerAdapter;
import net.ememed.doctor2.fragment.AllContactFragment;
import net.ememed.doctor2.fragment.GroupContactFragment;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MassMessageActivity extends BasicActivity implements OnPageChangeListener,OnClickListener{

	private final int PAGER_GROUP = 0;
	private final int PAGER_ALL_CONTACT = 1;
	private ViewPager viewPager;
	private ArrayList<Fragment> views = new ArrayList<Fragment>();
	private TextView tab_group, tab_all;
	private AllContactFragment contactFragment;
	private GroupContactFragment groupContactFragment;
	private FragmentViewPageAddpter addpter;
	
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_mass_message);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		viewPager = (ViewPager) findViewById(R.id.vPager);
		contactFragment = (AllContactFragment) Fragment.instantiate(this, AllContactFragment.class.getName());
		groupContactFragment =new GroupContactFragment();//(GroupContactFragment) Fragment.instantiate(this, GroupContactFragment.class.getName());
		views.add(groupContactFragment);
		views.add(contactFragment);
		addpter = new FragmentViewPageAddpter(getSupportFragmentManager(),views);
		viewPager.setAdapter(addpter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setOffscreenPageLimit(3);// 缓存多少个页面
		tab_group = (TextView) findViewById(R.id.tab_group);
		tab_all = (TextView) findViewById(R.id.tab_all);
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				changeTab(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		tab_group.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeTab(0);
			}
		});
		
		tab_all.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeTab(1);
			}
		});
		
		//所有患者分页的全选checkbox
		
	}
	

	
	private void changeTab(int position){
		tab_group.setTextColor(getResources().getColor(R.color.white));
		tab_all.setTextColor(getResources().getColor(R.color.white));
		tab_group.setBackgroundResource(0);
		tab_all.setBackgroundResource(0);
		
		if(PAGER_GROUP == position){
			viewPager.setCurrentItem(PAGER_GROUP);
			tab_group.setTextColor(getResources().getColor(R.color.topbg_green));
			tab_group.setBackgroundResource(R.drawable.tab_menu_left);
		} else if (PAGER_ALL_CONTACT == position){
			viewPager.setCurrentItem(PAGER_ALL_CONTACT);
			tab_all.setTextColor(getResources().getColor(R.color.topbg_green));
			tab_all.setBackgroundResource(R.drawable.tab_menu_right);
		}
	}
	
	
	
	public void doClick(View view) {
		if(R.id.btn_back == view.getId()){
			finish();
		}
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

	@Override
	public void onClick(View v) {
		
	}

}
