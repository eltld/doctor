package net.ememed.doctor2.finace;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.finace.adapter.FinaceGetMoneyAdapter;
import net.ememed.doctor2.finace.bean.FinaceIncome;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

public class FinaceRecentIncomeActivity extends BasicActivity implements
		OnRefreshListener {
	private TextView top_title;
	private RefreshListView lvCustomEvas;
	private FinaceGetMoneyAdapter finaceGetMoneyAdapter;
	private int totalpages = 1;
	private boolean refresh = true;
	private int page = 1;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_recent_income);
		initView();
		initServiceData();
	}


	private void initServiceData() {
		
		if (NetWorkUtils.detect(FinaceRecentIncomeActivity.this)) {
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
							message.what = 0x13;
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

	private void initView() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("近期收益");
		lvCustomEvas = (RefreshListView) findViewById(R.id.lv_contact_class);
		finaceGetMoneyAdapter = new FinaceGetMoneyAdapter(null, FinaceRecentIncomeActivity.this);
		lvCustomEvas.setAdapter(finaceGetMoneyAdapter);
		lvCustomEvas.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				refresh();
			}
		});
		lvCustomEvas.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		}
	}

	@Override
	public void onRefreshStarted(View view) {

	}
	
	protected void loadMore() {
		page++;
		initServiceData();
	}

	private void refresh() {
		refresh = false;
		initServiceData();
	}

	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			lvCustomEvas.onRefreshComplete();
			switch (msg.what) {
			case 0x13:
				Log.e("XX", "33333333333333");
				FinaceIncome fi = (FinaceIncome) msg.obj;
//				int success2 = fi.getSuccess();
				if (null != fi) {
					if (fi.getSuccess() == 1) {
						totalpages = fi.getCount() / 20 + 1;
						if (refresh) {
							if (fi.getData() == null || fi.getData().size() == 0) {
								lvCustomEvas.setVisibility(View.GONE);
							} else {
								lvCustomEvas.setVisibility(View.VISIBLE);
								finaceGetMoneyAdapter.add(fi.getData());
							}
						} else {

						}
						if (page < totalpages) {
							lvCustomEvas.onLoadMoreComplete(false);
							refresh = true;
						} else {
							lvCustomEvas.onLoadMoreComplete(true);
							refresh = false;
						}
					}
				}
				
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
}
