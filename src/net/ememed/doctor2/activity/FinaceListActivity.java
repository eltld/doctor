package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.ModePopAdapter;
import net.ememed.doctor2.entity.FinaceDetails;
import net.ememed.doctor2.entity.FinaceEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.IncomeExpend;
import net.ememed.doctor2.entity.IncomeExpendEntry;
import net.ememed.doctor2.entity.NewsItem;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class FinaceListActivity extends BasicActivity implements
		OnRefreshListener {
//	private PullToRefreshLayout mPullToRefreshLayout;
	private FinaceAdapter adapter;
	private RefreshListView listview;

	private List<IncomeExpendEntry> listItems;
	private boolean refresh = true;
	private int page = 1;
	private LinearLayout ll_empty;
	private LinearLayout ll_net_failed;

	// private PopupWindow mModePopupWindow;
	// private View mModeView;
	// private ListView mModeListView;
	private int mChooseId = 0;

	private TextView tv_fiance_up;
	private LinearLayout tv_fiance;
	private ImageView iv_fiance_pic;
	private ImageView bg;

	private int[] mModeIcon = { R.drawable.pic_income_expend,
			R.drawable.pic_doctor_expend, R.drawable.pic_doctor_income,
			R.drawable.pic_doctor_freeze };
	private String[] mModeName = { "收入/支出", "收入", "支出", "冻结/解冻" };
	private int totalpages = 1;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_list);
	}

	@Override
	protected void setupView() {
		super.setupView();
//		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
//		ActionBarPullToRefresh.from(this).allChildrenArePullable()
//				.listener(this).setup(mPullToRefreshLayout);
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		ll_net_failed = (LinearLayout) findViewById(R.id.ll_net_unavailable);
		bg = (ImageView) findViewById(R.id.overlay);
		tv_fiance_up = (TextView) findViewById(R.id.tv_fiance_up);
		tv_fiance_up.setText("收入/支出");
		// getDoctorIncome();
		// getDoctorExpend();
		// getDoctorFreeze();
		listview = (RefreshListView) findViewById(R.id.ptr_listview);
		adapter = new FinaceAdapter(null);
		listview.setAdapter(adapter);

		// mModeView = getLayoutInflater()
		// .inflate(R.layout.mode_popupwindow, null);
		//
		// mModeListView = (ListView)
		// mModeView.findViewById(R.id.mode_pop_list);
		//
		// tv_fiance_up = (TextView) findViewById(R.id.tv_fiance_up);
		// iv_fiance_pic = (ImageView) findViewById(R.id.iv_fiance_pic);
		// iv_fiance_pic.setBackgroundResource(R.drawable.finace_below_pic);
		// tv_fiance = (LinearLayout) findViewById(R.id.tv_fiance);
	}

	@Override
	protected void getData() {
		getIncomeExpend(page);
		super.getData();
	}

	/**
	 * 弹出popupWindow 框
	 * 
	 * @param chooseId
	 */
	// private void initModePopupWindow(int chooseId) {
	// ModePopAdapter adapter = new ModePopAdapter(this, mModeIcon, mModeName,
	// chooseId);
	// mModeListView.setAdapter(adapter);
	//
	// if (mModePopupWindow == null) {
	// mModePopupWindow = new PopupWindow(mModeView,
	// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
	// mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
	// mModePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
	// {
	//
	// @Override
	// public void onDismiss() {
	// bg.setVisibility(View.GONE);
	// iv_fiance_pic.setBackgroundResource(R.drawable.finace_below_pic);
	// }
	// });
	// }
	// if (mModePopupWindow.isShowing()) {
	// mModePopupWindow.dismiss();
	// } else {
	// bg.setVisibility(View.VISIBLE);
	// iv_fiance_pic.setBackgroundResource(R.drawable.finace_up_pic);
	// mModePopupWindow.showAsDropDown(tv_fiance_up, 0, 0);
	// }
	// }

	@Override
	protected void addListener() {
		super.addListener();
		listview.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});

		listview.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				// TODO Auto-generated method stub
				refresh();
			}
		});

		// tv_fiance.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// initModePopupWindow(mChooseId);
		// }
		// });

		// mModeListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View view,
		// int position, long id) {
		// mChooseId = position;
		// tv_fiance_up.setText(mModeName[position]);
		// iv_fiance_pic
		// .setBackgroundResource(R.drawable.finace_below_pic);
		// mModePopupWindow.dismiss();
		// switch (position) {
		// case 0:
		// refresh = true;
		// getIncomeExpend(page);
		// break;
		// case 1:
		// refresh = true;
		// getDoctorIncome(page);
		// break;
		// case 2:
		// refresh = true;
		// getDoctorExpend(page);
		// break;
		// case 3:
		// refresh = true;
		// getDoctorFreeze(page);
		// break;
		// default:
		// break;
		// }
		// }
		//
		// });
	}

	protected void loadMore() {
		refresh = false;
		page++;
		getIncomeExpend(page);
		// switch (mChooseId) {
		// case 0:
		// getIncomeExpend(page);
		// break;
		// case 1:
		// getDoctorIncome(page);
		// break;
		// case 2:
		// getDoctorExpend(page);
		// break;
		// case 3:
		// getDoctorFreeze(page);
		// break;
		// default:
		// break;
		// }
	}

	private void refresh() {
		page = 1;
		refresh = true;
		getIncomeExpend(page);
		// switch (mChooseId) {
		// case 0:
		// getIncomeExpend(page);
		// break;
		// case 1:
		// getDoctorIncome(page);
		// break;
		// case 2:
		// getDoctorExpend(page);
		// break;
		// case 3:
		// getDoctorFreeze(page);
		// break;
		// default:
		// break;
		// }
		// TODO 刷新财务
		// FIXME 刷新财务
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		}
		// else if (view.getId() == R.id.btn_tijiao){
		// Intent intent = new
		// Intent(FinaceDetailsActivity.this,MoneyApplyActivity.class);
		// startActivity(intent);
		// }
	}

	/**
	 * 收入/支出
	 */
	private void getIncomeExpend(int page) {
		if (NetWorkUtils.detect(FinaceListActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_income_expend_list, IncomeExpend.class,
					params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.INCOME_EXPEND;
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

	/**
	 * 收入
	 */
	private void getDoctorIncome(int page) {
		if (NetWorkUtils.detect(FinaceListActivity.this)) {

			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_income_list, IncomeExpend.class,
					params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DOCTOR_INCOME;
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

	/**
	 * 支出
	 */
	private void getDoctorExpend(int page) {
		if (NetWorkUtils.detect(FinaceListActivity.this)) {

			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_expend_list, IncomeExpend.class,
					params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DOCTOR_EXPEND;
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

	/**
	 * 冻结/解冻
	 */
	private void getDoctorFreeze(int page) {
		if (NetWorkUtils.detect(FinaceListActivity.this)) {

			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_freeze_list, IncomeExpend.class,
					params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DOCTOR_FREEZE;
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
			switch (msg.what) {
			case IResult.INCOME_EXPEND:
				destroyDialog();
				listview.onRefreshComplete();
				IncomeExpend incomeExpend = (IncomeExpend) msg.obj;
				if (null != incomeExpend) {
					if (incomeExpend.getSuccess() == 1) {
						listItems = incomeExpend.getData();
						if (null != listItems && listItems.size() > 0) {
							ll_empty.setVisibility(View.GONE);
							listview.setVisibility(View.VISIBLE);
							ll_net_failed.setVisibility(View.GONE);
							if (refresh) {
								adapter.change(listItems);
							} else {
								adapter.add(listItems);
							}
							if (page < totalpages) {
								listview.onLoadMoreComplete(false);
							} else {
								listview.onLoadMoreComplete(true);
							}
						} else {
							ll_empty.setVisibility(View.VISIBLE);
							listview.setVisibility(View.GONE);
						}
					} else {
						ll_empty.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE);
					}
				} else {
					ll_empty.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				}

				break;
			case IResult.DOCTOR_INCOME:
				destroyDialog();
				IncomeExpend doctorIncome = (IncomeExpend) msg.obj;
				if (null != doctorIncome) {
					if (doctorIncome.getSuccess() == 1) {
						listItems = doctorIncome.getData();
						if (null != listItems && listItems.size() > 0) {
							ll_empty.setVisibility(View.GONE);
							listview.setVisibility(View.VISIBLE);
							ll_net_failed.setVisibility(View.GONE);
							if (refresh) {
								adapter.change(listItems);
							} else {
								adapter.add(listItems);
							}

							if (page < totalpages) {
								listview.onLoadMoreComplete(false);
							} else {
								listview.onLoadMoreComplete(true);
							}
						} else {
							ll_empty.setVisibility(View.VISIBLE);
							listview.setVisibility(View.GONE);
						}
					} else {
						ll_empty.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE);
					}
				} else {
					ll_empty.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				}
				break;
			case IResult.DOCTOR_EXPEND:
				destroyDialog();
				IncomeExpend doctorExpend = (IncomeExpend) msg.obj;
				if (null != doctorExpend) {
					if (doctorExpend.getSuccess() == 1) {
						listItems = doctorExpend.getData();
						if (null != listItems && listItems.size() > 0) {
							ll_empty.setVisibility(View.GONE);
							listview.setVisibility(View.VISIBLE);
							ll_net_failed.setVisibility(View.GONE);
							if (refresh) {
								adapter.change(listItems);
							} else {
								adapter.add(listItems);
							}

							if (page < totalpages) {
								listview.onLoadMoreComplete(false);
							} else {
								listview.onLoadMoreComplete(true);
							}
						} else {
							ll_empty.setVisibility(View.VISIBLE);
							listview.setVisibility(View.GONE);
						}
					} else {
						ll_empty.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE);
					}
				} else {
					ll_empty.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				}
				break;
			case IResult.DOCTOR_FREEZE:
				destroyDialog();
				IncomeExpend doctor_freeze = (IncomeExpend) msg.obj;
				if (null != doctor_freeze) {
					if (doctor_freeze.getSuccess() == 1) {
						listItems = doctor_freeze.getData();
						if (null != listItems && listItems.size() > 0) {
							ll_empty.setVisibility(View.GONE);
							listview.setVisibility(View.VISIBLE);
							ll_net_failed.setVisibility(View.GONE);
							if (refresh) {
								adapter.change(listItems);
							} else {
								adapter.add(listItems);
							}

							if (page < totalpages) {
								listview.onLoadMoreComplete(false);
							} else {
								listview.onLoadMoreComplete(true);
							}
						} else {
							ll_empty.setVisibility(View.VISIBLE);
							listview.setVisibility(View.GONE);
						}
					} else {
						ll_empty.setVisibility(View.VISIBLE);
						listview.setVisibility(View.GONE);
					}
				} else {
					ll_empty.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				}
				break;
			case IResult.NET_ERROR:
				destroyDialog();
				showToast(IMessage.NET_ERROR);
				ll_net_failed.setVisibility(View.VISIBLE);
				ll_empty.setVisibility(View.GONE);
				listview.setVisibility(View.GONE);
				break;
			case IResult.DATA_ERROR:
				ll_net_failed.setVisibility(View.GONE);
				ll_empty.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	@Override
	public void onRefreshStarted(View view) {
//		mPullToRefreshLayout.setRefreshComplete();
		refresh();
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

	private class FinaceAdapter extends BaseAdapter {
		List<IncomeExpendEntry> listItems;

		public FinaceAdapter(List<IncomeExpendEntry> listItems) {
			if (listItems == null) {
				listItems = new ArrayList<IncomeExpendEntry>();
			}
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return (listItems == null || listItems.size() == 0) ? null
					: listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(FinaceListActivity.this)
						.inflate(R.layout.finace_income_item, null);
				holder.tv_consult_info = (TextView) convertView
						.findViewById(R.id.tv_consult_info);
				holder.tv_finace_money = (TextView) convertView
						.findViewById(R.id.tv_finace_money);
				holder.tv_finace_title = (TextView) convertView
						.findViewById(R.id.tv_finace_title);
				holder.tv_user_time = (TextView) convertView
						.findViewById(R.id.tv_user_time);
				holder.tv_fiance_money_title = (TextView) convertView
						.findViewById(R.id.tv_fiance_money_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.tv_consult_info.setText((String)
			// listItems.get(position).get("info"));
			IncomeExpendEntry item = listItems.get(position);
			if (null != item) {
				holder.tv_consult_info.setText("￥" + String.format("%.2f", item.getMONEY()));
				// if (mChooseId == 0 || mChooseId == 1 || mChooseId ==2) {
				holder.tv_fiance_money_title.setText("可提现金额:");
				// } else if (mChooseId == 3) {
				// holder.tv_fiance_money_title.setText("冻结资金:");
				// holder.tv_finace_money.setText(SharePrefUtil.getString("freeze"));
				// }
				double usermoney = item.getUSERMONEY();
				holder.tv_finace_money.setText("￥" + usermoney);
				holder.tv_finace_title.setText(item.getORDERTYPE_NAME());
				holder.tv_user_time.setText(item.getCREATETIME());
			}
			return convertView;
		}

		public void change(List<IncomeExpendEntry> lists) {
			if (lists == null) {
				lists = new ArrayList<IncomeExpendEntry>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void add(List<IncomeExpendEntry> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		public TextView tv_user_time;
		TextView tv_finace_title;
		TextView tv_finace_money;
		TextView tv_consult_info;
		TextView tv_fiance_money_title;
	}
}
