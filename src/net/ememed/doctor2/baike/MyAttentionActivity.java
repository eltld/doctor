package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.adapter.AttentionAdapter;
import net.ememed.doctor2.baike.adapter.FansAdapter;
import net.ememed.doctor2.baike.entity.MyAttentionEntry;
import net.ememed.doctor2.baike.entity.MyAttentionInfo;
import net.ememed.doctor2.baike.entity.MyFansEntry;
import net.ememed.doctor2.baike.entity.MyFansInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.SearchFriendInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

public class MyAttentionActivity extends BasicActivity{
	private RefreshListView listView;
	private int page = 1;
	private boolean isRefresh = true;
	private List<MyAttentionInfo> attentionList = null;
	private AttentionAdapter adapter;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_my_attention_list);
		attentionList = new ArrayList<MyAttentionInfo>();
	}
	
	@Override
	protected void setupView() {
		super.setupView();

		((TextView)findViewById(R.id.top_title)).setText("我关注的");
		listView = (RefreshListView) findViewById(R.id.listview1);
		adapter = new AttentionAdapter(null, this);
		listView.setAdapter(adapter);
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		
		listView.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getMyAttention();
			}
		});
		
		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getMyAttention();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyAttentionActivity.this, OtherBaikeActivity.class);
				MyAttentionInfo info = (MyAttentionInfo)parent.getAdapter().getItem(position);
				intent.putExtra("other_doctor_id", info.getOTHER_MEMBERID());
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getMyAttention();
	}
	
	private void getMyAttention(){
		if (NetWorkUtils.detect(this)) {
			loading(null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_my_attention, MyAttentionEntry.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_MY_ATTENTION;
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
			case IResult.GET_MY_ATTENTION:
				destroyDialog();
				listView.onRefreshComplete();
				MyAttentionEntry entry = (MyAttentionEntry) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						List<MyAttentionInfo> list = entry.getData();
						
						if(null != list && list.size() > 0){
							if(isRefresh ){
								attentionList = list;
							} else {
								attentionList.addAll(list);
							}
							adapter.change(attentionList);
						}
						if (page < entry.getPages()) {
							listView.onLoadMoreComplete(false);
						} else {
							listView.onLoadMoreComplete(true);
						}
						
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
}
