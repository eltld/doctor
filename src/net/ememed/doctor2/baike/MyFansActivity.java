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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.activity.FindDoctorFriendActivity;
import net.ememed.doctor2.baike.adapter.FansAdapter;
import net.ememed.doctor2.baike.entity.MyFansEntry;
import net.ememed.doctor2.baike.entity.MyFansInfo;
import net.ememed.doctor2.baike.entity.RewardListInfo;
import net.ememed.doctor2.entity.DoctorFriendInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.SearchFriendInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

public class MyFansActivity extends BasicActivity{
	private RefreshListView listView;
	private int page = 1;
	private boolean isRefresh = true;
	private List<MyFansInfo> fansList = null;
	private FansAdapter adapter;
	private int type;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_fans_list);
		fansList = new ArrayList<MyFansInfo>();
		type = getIntent().getIntExtra("fans_type", 0);
	}
	
	@Override
	protected void setupView() {
		super.setupView();

		((TextView)findViewById(R.id.top_title)).setText("粉丝");
		listView = (RefreshListView) findViewById(R.id.listview1);
		adapter = new FansAdapter(null, this);
		listView.setAdapter(adapter);
		getMyFans(type);
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		
		listView.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getMyFans(type);
			}
		});
		
		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {
			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getMyFans(type);
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = null;
				MyFansInfo info = (MyFansInfo) parent.getAdapter().getItem(position);
				if("doctor".equals(info.getUTYPE())){
					intent = new Intent(MyFansActivity.this, OtherBaikeActivity.class);
					intent.putExtra("other_doctor_id", info.getDOCTORINFO().getMEMBERID()+"");
					startActivity(intent);
				} else {
					intent = new Intent(MyFansActivity.this, ContactInfoActivity.class);
					intent.putExtra("title", info.getUSERINFO().getREALNAME());
					intent.putExtra("tochat_userId", info.getUSERINFO().getMEMBERID());
					intent.putExtra("user_avatar", info.getUSERINFO().getAVATAR());
					if(TextUtils.isEmpty(info.getIS_STAR())){
						intent.putExtra("is_star", "0");
					} else {
						intent.putExtra("is_star", info.getIS_STAR());
					}
					intent.putExtra("note_name", info.getNOTE_NAME());
					intent.putExtra("description", info.getDESCRIPTION());
					intent.putExtra("index", 1);
					startActivity(intent);
				}
			}
		});
	}
	
	private void getMyFans(int type){
		if (NetWorkUtils.detect(this)) {
			loading(null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");
			params.put("type", ""+type);	//type 粉丝类型（0全部，1医生粉，2患者粉）

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_fans_list, MyFansEntry.class, params,
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
				MyFansEntry entry = (MyFansEntry) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						List<MyFansInfo> list = entry.getData();
						
						if(null != list && list.size() > 0){
							if(isRefresh ){
								fansList = list;
							} else {
								fansList.addAll(list);
							}
							adapter.change(fansList);
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
}
