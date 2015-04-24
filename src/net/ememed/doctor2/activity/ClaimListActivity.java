package net.ememed.doctor2.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.ClaimListAdapter;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.QuestionPool;
import net.ememed.doctor2.entity.QuestionPoolInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class ClaimListActivity extends BasicActivity implements
		Handler.Callback ,OnItemClickListener{

	private RefreshListView listView;
	private ClaimListAdapter adapter;
	public List<QuestionPoolInfo> list;
	int page=1;
	private TextView top_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_pool_listview);
		listView = (RefreshListView) findViewById(R.id.question_pool_list);
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("我认领的问题");
		listView.setOnItemClickListener(this);
		adapter = new ClaimListAdapter(ClaimListActivity.this,ClaimListActivity.this.imageLoader,
				ClaimListActivity.this.options);
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new IOnRefreshListener() {

			public void OnRefresh() {
				refresh();
			}
		});
		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {

			public void OnLoadMore() {
				loadMore();
			}
		});
		getQuestionList(page+"");
	}
	public void refresh(){
		page=1;
		getQuestionList("1");
	}
	public void loadMore(){
		page++;
		getQuestionList(page+"");
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case IResult.DATA_ERROR:
			this.destroyDialog();
			showToast("未找到数据");
			break;
		case IResult.NET_ERROR:
			this.destroyDialog();
			showToast("未找到网络");
		default:
			break;
		}
		return false;
	}

	public void getQuestionList(String page) {
		if (NetWorkUtils.detect(this)) {
			this.loading(null);

			listView.onRefreshComplete();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page);
			
			System.out.println("params = "+ params);
			
			MyApplication.volleyHttpClient.postOAuthWithParams(
					HttpUtil.get_claim_list, QuestionPool.class, params,
					new Listener() {
						@Override
						public void onResponse(Object arg0) {
							ClaimListActivity.this.destroyDialog();
							QuestionPool qp = (QuestionPool) arg0;
							if (qp != null) {
								if (ClaimListActivity.this.page < Integer.parseInt(qp.getPages())) {
									listView.onLoadMoreComplete(false);
								} else {
									listView.onLoadMoreComplete(true);
								}
								if(qp.getData()!=null){
									setData(qp.getData());
								}
							}else{
								showToast(qp.getErrormsg());
							}
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							ClaimListActivity.this.destroyDialog();
						}
					});
		}
	}
	
	
	public void setData(List<QuestionPoolInfo> list){
		adapter.setData(list);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		QuestionPoolInfo questionPoolInfo = (QuestionPoolInfo) adapter.getItem(arg2-1);
		Intent intent = new Intent(this,PreeChatActivity.class);
		intent.putExtra("title", questionPoolInfo.getREALNAME());
		intent.putExtra("tochat_userId", questionPoolInfo.getUSERID());
		intent.putExtra("user_avatar", questionPoolInfo.getAVATAR());
		intent.putExtra("orderid", questionPoolInfo.getORDERID());
		intent.putExtra("questionid", questionPoolInfo.getQUESTIONID());
		intent.putExtra("status", questionPoolInfo.getSTATUS());
		List<String> pics = questionPoolInfo.getPICS();
		intent.putExtra("pics", (Serializable)pics);
		startActivity(intent);
	}
	
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}
	}
}
