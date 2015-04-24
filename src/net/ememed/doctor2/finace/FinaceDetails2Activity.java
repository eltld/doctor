package net.ememed.doctor2.finace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.FinaceDetails;
import net.ememed.doctor2.entity.FinaceEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.finace.adapter.FinaceBlockingAdapter;
import net.ememed.doctor2.finace.adapter.FinaceGetMoneyAdapter;
import net.ememed.doctor2.finace.bean.FinaceBlocking;
import net.ememed.doctor2.finace.bean.FinaceBlockingBean;
import net.ememed.doctor2.finace.bean.FinaceIncome;
import net.ememed.doctor2.finace.bean.FinaceIncomeBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

public class FinaceDetails2Activity extends BasicActivity {
	
	private TextView top_title;
	private TextView tv_available_now;
	private FinaceDetails fd;
	private LinearLayout ll_your_cash;
	private LinearLayout ll_your_cash_now;
	private LinearLayout rl_txt;
	private TextView txt_cash;
	private TextView txt_cash_now;
	private TextView txt_notice_cash;
	private TextView finace_latest_income;
	private TextView finace_latest_blocking;
/*	private TextView cash_more1;
	private TextView cash_more2;*/
//	private ListViewForScrollView forScrollView1, forScrollView2;
	private RefreshListView forScrollView1, forScrollView2;
	private FinaceGetMoneyAdapter finaceGetMoneyAdapter;
	private FinaceBlockingAdapter finaceBlockingAdapter;
	private TextView btn_money;
	private Button btn_cash;
	private String balance;
	private ViewPager viewPager;
	private ArrayList<View> listViews = new ArrayList<View>();
	private LayoutInflater inflater;
	private View view1, view2;
	private String from = null;
	private int page = 1;
	private boolean isRefresh = false;
	
	private TextView tv_menu;
	private RefreshReceiver receiver;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_details2);
		from = getIntent().getStringExtra("from");
		inflater = LayoutInflater.from(this);
		view1 = inflater.inflate(R.layout.activity_finace_viewpage, null);
		view2 = inflater.inflate(R.layout.activity_finace_viewpage, null);
		/*cash_more1 = (TextView) view1.findViewById(R.id.cash_more);
		cash_more2 = (TextView) view2.findViewById(R.id.cash_more);*/
	/*	forScrollView1 = (ListViewForScrollView) view1.findViewById(R.id.lv_contact_class);
		forScrollView2 = (ListViewForScrollView) view2.findViewById(R.id.lv_contact_class);*/
		forScrollView1 = (RefreshListView) view1.findViewById(R.id.lv_contact_class);
		forScrollView2 = (RefreshListView) view2.findViewById(R.id.lv_contact_class);
		initView();
		initViewPager();
		getDoctorAccount();
		
		finaceBlockingAdapter = new FinaceBlockingAdapter(null, FinaceDetails2Activity.this);
		finaceGetMoneyAdapter = new FinaceGetMoneyAdapter(null, FinaceDetails2Activity.this);
		
		receiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter(ActionFilter.REQUEST_FINACE_DETAIL);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	/**
	 * 初化底部ViewPager
	 */
	private void initViewPager() {
		forScrollView1.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getIncomeData();
			}
		});
		
		forScrollView1.setOnLoadMoreListener(new IOnLoadMoreListener() {
			
			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getIncomeData();
			}
		});
		
		forScrollView2.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getBlickingData();
			}
		});
		
		forScrollView2.setOnLoadMoreListener(new IOnLoadMoreListener() {
			
			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getBlickingData();
			}
		});
		
		listViews.add(view1);
		listViews.add(view2);
		MyViewPagerAdapter vpa = new MyViewPagerAdapter(listViews);
		viewPager.setAdapter(vpa);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			public void onPageScrolled(int i, float f, int i1) {
			}

			public void onPageScrollStateChanged(int i) {
			}

			public void onPageSelected(int i) {
				page = 1;
				
				if (i == 0) {
					wordStyle1();
				} else {
					wordStyle2();
				}

				if (i == 0) {
					isRefresh = false;
					getIncomeData();
				} else if (i == 1){
					isRefresh = false;
					getBlickingData();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(from != null && from.equals(FinaceFinallyActivity.class.getSimpleName())){
			from = "";
			viewPager.setCurrentItem(1);
			wordStyle2();
			page = 1;
			isRefresh = false;
			getBlickingData();
		}
	}
	
	private void initView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.finance_details));
		tv_available_now = (TextView) findViewById(R.id.tv_available_now);	
		ll_your_cash = (LinearLayout) findViewById(R.id.ll_your_cash);
		ll_your_cash_now = (LinearLayout) findViewById(R.id.ll_your_cash_now);
		rl_txt = (LinearLayout) findViewById(R.id.rl_txt_blocking);
		txt_cash = (TextView) findViewById(R.id.txt_cash);
		txt_cash_now = (TextView) findViewById(R.id.txt_cash_now);
		txt_notice_cash = (TextView) findViewById(R.id.cash_100);
		btn_money = (TextView) findViewById(R.id.btn_money);
		btn_money.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
		btn_money.getPaint().setAntiAlias(true);//抗锯齿
		btn_cash = (Button) findViewById(R.id.btn_cash);
		viewPager = (ViewPager) findViewById(R.id.vPager);
		
		tv_menu = (TextView) findViewById(R.id.tv_menu);
		tv_menu.setVisibility(View.VISIBLE);
		tv_menu.setText("收款账户");
		tv_menu.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				 switch (event.getAction()) { 	  
	    	        case MotionEvent.ACTION_DOWN:	//按下
	    	        	tv_menu.setTextColor(FinaceDetails2Activity.this.getResources().getColor(R.color.tab_light_gray));
	    	            break;
	    	        case MotionEvent.ACTION_UP://抬起
	    	        	tv_menu.setTextColor(FinaceDetails2Activity.this.getResources().getColor(R.color.white));
	    	        	startActivity(new Intent(FinaceDetails2Activity.this, BankcardListActivity.class));
	    	            break;
	    	        default:
	    	            break;
	    	  }
				
				return true;
			}
			
			
		});
		
	/*	cash_more1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Utils.startActivity(FinaceDetails2Activity.this, FinaceRecentIncomeActivity.class);
				page += 1;
				isRefresh = false;
				getIncomeData();
			}
		});
		cash_more2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Utils.startActivity(FinaceDetails2Activity.this, FinaceRecentCashActivity.class);
				page += 1;
				isRefresh = false;
				getIncomeData();
			}
		});*/
//		btn_cash.setEnabled(false);
		btn_cash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Bundle data = new Bundle();
				data.putString("balance", balance);
				Utils.startActivity(FinaceDetails2Activity.this, FinaceNowCashActivity.class, data);
			}
		});
		finace_latest_income = (TextView) findViewById(R.id.finace_latest_income); 
//		forScrollView = (ListViewForScrollView) findViewById(R.id.lv_contact_class);
		finace_latest_blocking = (TextView) findViewById(R.id.finace_latest_blocking);  
	
		finace_latest_income.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				viewPager.setCurrentItem(0);
			}
		});
		finace_latest_blocking.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				viewPager.setCurrentItem(1);
			}
		});
		
		btn_money.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Utils.startActivity(FinaceDetails2Activity.this, FinaceWeb2Activity.class);
			}
		});
		
		
	}
	
	private void wordStyle1(){
		finace_latest_income.setTextColor(getResources().getColor(R.color.tab_light_green));
		finace_latest_blocking.setTextColor(getResources().getColor(R.color.tab_light_gray));
	}
	
	private void wordStyle2(){
		finace_latest_income.setTextColor(getResources().getColor(R.color.tab_light_gray));
		finace_latest_blocking.setTextColor(getResources().getColor(R.color.tab_light_green));
	}
	
	private void getBlickingData() {
		forScrollView2.setAdapter(finaceBlockingAdapter);
		if (NetWorkUtils.detect(FinaceDetails2Activity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page+"");
			Log.e("XX", params.toString());
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_blocking, FinaceBlockingBean.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_DOCTOR_APPLY_CASH;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	private void getIncomeData() {
		forScrollView1.setAdapter(finaceGetMoneyAdapter);
		if (NetWorkUtils.detect(FinaceDetails2Activity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page + "");
			Log.e("XX", params.toString());
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.post_doctor_income, FinaceIncome.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Log.e("XX", "11111111111111");
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DOCTOR_INCOME;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e("XX", "aaaaaaaaaaaaaaaa");
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	
	private void getDoctorAccount() {
		if (NetWorkUtils.detect(FinaceDetails2Activity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			Log.e("XX", params.toString());
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_account, FinaceDetails.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DOCTOR_ACCOUNT;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.DOCTOR_ACCOUNT:
				fd = (FinaceDetails) msg.obj;
				int success_2 = fd.getSuccess();
				if (success_2 == 0) {
					showToast((String) fd.getErrormsg());
					return;
				} else {
					FinaceEntry data = fd.getData();
					if (null != data) {
						if (data.getApply_cash_status() == 1) {
							ll_your_cash.setVisibility(View.VISIBLE);
							ll_your_cash_now.setVisibility(View.VISIBLE);
							rl_txt.setVisibility(View.VISIBLE);
							txt_notice_cash.setVisibility(View.GONE);
							txt_cash.setText(Util.m2(data.getAvailable()));
							txt_cash_now.setText(Util.m2(data.getFreeze()));
						} else {
							ll_your_cash.setVisibility(View.GONE);
							ll_your_cash_now.setVisibility(View.GONE);
							rl_txt.setVisibility(View.GONE);
							txt_notice_cash.setVisibility(View.VISIBLE);
						}
//						tv_total.setText("￥" + data.getTotal());
//						tv_available.setText("可用金额" + data.getAvailable());
//						tv_freeze.setText("冻结金额" + data.getFreeze());
						tv_available_now.setText("￥" + Util.m2(data.getAvailable()));
						SharePrefUtil.putString(Conast.USER_MONEY, Util.m2(data.getAvailable()));
						SharePrefUtil.commit();
						if (data.getApply_cash_status() == 0) {
							btn_cash.setEnabled(false);
							btn_cash.setBackgroundResource(R.drawable.btn_gray_normal_shape);
						} else if(data.getApply_cash_status() == 1) {
							btn_cash.setEnabled(true);
							btn_cash.setBackgroundResource(R.drawable.button_bg_selector);
						}
						balance = Util.m2(data.getAvailable());
						SaveFiance(data);
						BankCardInfoCommon.getInstance().setTotalMoney(data.getTotal());
						BankCardInfoCommon.getInstance().saveBankCardInfo(data.getBankcardlist());
						getIncomeData();
					}
				}
				break;
			case IResult.GET_DOCTOR_APPLY_CASH:
				FinaceBlockingBean fb = (FinaceBlockingBean) msg.obj;
				int success = fb.getSuccess();
				if (success == 0) {
					showToast((String) fb.getErrormsg());
					return;
				} else {
					/*if(fb.getPages() > page){
						cash_more2.setVisibility(View.VISIBLE);
					} else {
						cash_more2.setVisibility(View.GONE);
					}*/
					
					if(fb.getPages() > page){
						forScrollView2.onLoadMoreComplete(false, true);
					}else{
						forScrollView2.onLoadMoreComplete(true);
					}
				
					List<FinaceBlocking> data = fb.getData();
					if (null != data) {
						if(page > 1){
							finaceBlockingAdapter.add(data);
						}else {
							finaceBlockingAdapter.change(data);
						}
						
						if(isRefresh){
							forScrollView2.onRefreshComplete();
						}
					}
				}
				break;
			case IResult.DOCTOR_INCOME:
				FinaceIncome fi = (FinaceIncome) msg.obj;
				int success2 = fi.getSuccess();
				if (success2 == 0) {
					showToast((String) fi.getErrormsg());
					return;
				} else {
					/*if(fi.getPages() > page){
						cash_more1.setVisibility(View.VISIBLE);
					} else {
						cash_more1.setVisibility(View.GONE);
					}*/
					
					if(fi.getPages() > page){
						forScrollView1.onLoadMoreComplete(false, true);
					}else{
						forScrollView1.onLoadMoreComplete(true);
					}
				
					List<FinaceIncomeBean> data = fi.getData();
					if (null != data) {
						if(page > 1){
							finaceGetMoneyAdapter.add(data);
						}else{
							finaceGetMoneyAdapter.change(data);
						}
						
						if(isRefresh){
							forScrollView1.onRefreshComplete();
						}
					}
				}
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
				break;
			case IResult.DATA_ERROR:
				// ll_net_failed.setVisibility(View.GONE);
				// ll_empty.setVisibility(View.VISIBLE);
				// listview.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	private void SaveFiance(FinaceEntry data) {
		SharePrefUtil.putString(Conast.TOTAL, "￥" + data.getTotal());
		SharePrefUtil.putString(Conast.AVAILABLE, "可用金额:" + data.getAvailable());
		SharePrefUtil.putString(Conast.FREEZE, "冻结金额:" + data.getFreeze());
		SharePrefUtil.putString(Conast.AVAILABLE_NEW, "￥" + data.getAvailable());
		SharePrefUtil.commit();
	}
	
	
	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}
	
	private class RefreshReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			getDoctorAccount();
		}
	}
}
