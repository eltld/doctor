package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.BaikeVisitorDateList;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.fragment.BaikeVisitorTodayFragment;
import net.ememed.doctor2.fragment.BaikeVisitorTotalFragment;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class BaikeVisitorActivity extends BasicActivity {

	private static final int[] RADIO_BUTTONS = { R.id.rb_total, R.id.rb_today };
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private RadioGroup mRadioGroup;
	private RadioButton rb_total, rb_today;
	private static final String FROMAT_TOTAL = "总访问量(%s)";
	private static final String FROMAT_TODAY = "今日访问量(%s)";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_baike_visitor);

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		rb_total = (RadioButton) findViewById(R.id.rb_total);
		rb_today = (RadioButton) findViewById(R.id.rb_today);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mRadioGroup.check(RADIO_BUTTONS[position]);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		requestVirsitorList();
	}

	class TabAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragments;

		public TabAdapter(FragmentManager fm, BaikeVisitorDateList dateList) {
			super(fm);
			fragments = new ArrayList<Fragment>();
			fragments.add(BaikeVisitorTotalFragment.newInstance(dateList));
			fragments.add(BaikeVisitorTodayFragment.newInstance(dateList));
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}
	
	public void onBack(View v) {
		finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_total:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.rb_today:
			mViewPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}

	private void requestVirsitorList() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.VISITOR_LIST,
				BaikeVisitorDateList.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.RESULT;
						handler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.NET_ERROR;
						handler.sendMessage(message);
					}
				});
	}

	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.RESULT:
				destroyDialog();
				BaikeVisitorDateList dateList = (BaikeVisitorDateList) msg.obj;
				if (!dateList.isSuccess())
					return;
				updateTabsTitle(dateList.getVisitors(), dateList.getVisitorsToday());
				mAdapter = new TabAdapter(getSupportFragmentManager(), dateList);
				mViewPager.setAdapter(mAdapter);
				break;
			case IResult.END:

				break;
			case IResult.NET_ERROR:
				break;
			case IResult.DATA_ERROR:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	private void updateTabsTitle (String total, String today) {
		rb_total.setText(String.format(FROMAT_TOTAL, total));
		rb_today.setText(String.format(FROMAT_TODAY, today));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.VISITOR_LIST);
	}

}
