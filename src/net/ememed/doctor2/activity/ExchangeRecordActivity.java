package net.ememed.doctor2.activity;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.YimiHuaExchangeRecordInfo;
import net.ememed.doctor2.entity.YimiHuaExchangeRecordEntity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

public class ExchangeRecordActivity extends BasicActivity{
	
	private RefreshListView lv_record;
	private List<YimiHuaExchangeRecordInfo> list;
	private int mPage = 1;
	private int list_type;	//0表示积分赚取明细， 1表示兑换记录
	
	private QuickAdapter<YimiHuaExchangeRecordInfo> mAdapter;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.activity_exchange_record);
		list_type = getIntent().getIntExtra("list_type", 0);
		
		mAdapter = (list_type == 0) ? mEarnAdapter : mExchangeAdapter;
	}
	
	/**
	 * 启动ExchangeRecordActivity
	 * @param context
	 * @param list_type	0：赚取明细 ，  1：兑换记录
	 */
	public static void startAction(Context context, int list_type){
		Intent intent = new Intent(context, ExchangeRecordActivity.class);
		intent.putExtra("list_type", list_type);
		context.startActivity(intent);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		TextView top_title = (TextView) findViewById(R.id.top_title);
		if(0 == list_type){
			top_title.setText("赚取明细");
		} else {
			top_title.setText("兑换记录");
		}
		
		lv_record = (RefreshListView) findViewById(R.id.lv_exchange_record);
		lv_record.setAdapter(mAdapter);
		
		refresh();
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		
		lv_record.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				refresh();
			}
		});
		
		lv_record.setOnLoadMoreListener(new IOnLoadMoreListener() {
			
			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});
	}
	
	public void refresh() {
		mPage = 1;
		if(0 == list_type){
			requestEarnList();
		} else {
			requestExchangeList();
		}
	}

	public void loadMore() {
		mPage++;
		if(0 == list_type){
			requestEarnList();
		} else {
			requestExchangeList();
		}
	}
	
	private void requestExchangeList() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("page", String.valueOf(mPage));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_score_exchange_record, YimiHuaExchangeRecordEntity.class,
				params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_EXCHANGE_RECORD;
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
	
	private void requestEarnList() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("page", String.valueOf(mPage));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_score_earn_record, YimiHuaExchangeRecordEntity.class,
				params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_EXCHANGE_RECORD;
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
	
	private final QuickAdapter<YimiHuaExchangeRecordInfo> mExchangeAdapter = new QuickAdapter<YimiHuaExchangeRecordInfo>(this,
			R.layout.item_yimihua_exchange_record) {

		@Override
		protected void convert(BaseAdapterHelper helper, final YimiHuaExchangeRecordInfo item) {
			helper.setText(R.id.tv_money, String.format("现金%s", item.getMONEY()))
					.setText(R.id.tv_time, item.getCREATE_TIME())
					.setText(R.id.tv_score, "-"+item.getPOINTS());
		}
	};
	
	private final QuickAdapter<YimiHuaExchangeRecordInfo> mEarnAdapter = new QuickAdapter<YimiHuaExchangeRecordInfo>(this,
			R.layout.item_yimihua_exchange_record) {

		@Override
		protected void convert(BaseAdapterHelper helper, final YimiHuaExchangeRecordInfo item) {
			helper.setText(R.id.tv_money, item.getLOGTITLE())
					.setText(R.id.tv_time, item.getCREATE_TIME())
					.setText(R.id.tv_score, item.getPOINTS_DESC());
		}
	};
	
	@Override
	protected void onResult(Message msg) {
		try {
			lv_record.onRefreshComplete();
			destroyDialog();
			switch (msg.what) {
			case IResult.GET_EXCHANGE_RECORD:
				YimiHuaExchangeRecordEntity data = (YimiHuaExchangeRecordEntity) msg.obj;
				if(data.getCount()==0){
					showToast("没有数据");
					return;
				}
				if (!data.isSuccess())
					return;
				
				if (mPage == 1)
					mAdapter.clear();
				mAdapter.addAll(data.getData());
				lv_record.onLoadMoreComplete(mPage < data.getPages() ? false : true);
				
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("获取数据出错");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

}
