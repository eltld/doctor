package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.adapter.RewardAdapter;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.baike.entity.MyBaikeEntry;
import net.ememed.doctor2.baike.entity.RewardListEntry;
import net.ememed.doctor2.baike.entity.RewardListInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

public class RewardListActivity extends BasicActivity{
	private RefreshListView listView;
	private RewardAdapter adapter;
	private int page = 1;
	private List<RewardListInfo> rewardList = null;
	private boolean isRefresh = true;
	private String total_money;
	private TextView tv_money;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.activity_reward_list);
		rewardList = new ArrayList<RewardListInfo>();
		total_money = getIntent().getStringExtra("total_money");
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		((TextView)findViewById(R.id.top_title)).setText("赏金");
		
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_money.setText("￥ "+ total_money);
		
		listView = (RefreshListView) findViewById(R.id.reward_list);
		adapter = new RewardAdapter(null, this);
		listView.setAdapter(adapter);
		
		getRewardList();
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		
		listView.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getRewardList();
			}
		});
		
		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getRewardList();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				Intent intent = new Intent(RewardListActivity.this, RewardDetailActivity.class);
				intent.putExtra("reward_info", (RewardListInfo)arg0.getAdapter().getItem(position));
				startActivity(intent);
			}
		});
	}
	
	private void getRewardList(){
		if (NetWorkUtils.detect(this)) {
			loading(null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_bounty_list, RewardListEntry.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_REWARD_LIST;
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
			case IResult.GET_REWARD_LIST:
				destroyDialog();
				listView.onRefreshComplete();
				RewardListEntry entry = (RewardListEntry) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						List<RewardListInfo> list = entry.getData();
						
						if(null != list && list.size() > 0){
							if(isRefresh ){
								rewardList = list;
							} else {
								rewardList.addAll(list);
							}
							if(!TextUtils.isEmpty(entry.getTotal_money())){
								tv_money.setText(entry.getTotal_money());
							}
							adapter.change(rewardList);
						}
						if (page < entry.getPages()) {
							listView.onLoadMoreComplete(false);
						} else {
							listView.onLoadMoreComplete(true);
						}
						
						setResult(RESULT_OK);
						
					} else {
						showToast(entry.getErrormsg());
					}
				}
				break;
			case IResult.DATA_ERROR:
				listView.onRefreshComplete();
				destroyDialog();
				break;
			case IResult.NET_ERROR:
				showToast("网络异常");
				listView.onRefreshComplete();
				destroyDialog();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	public void doClick(View view){
		if(R.id.btn_back == view.getId()){
			finish();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_bounty_list);
	}
}

