package net.ememed.doctor2.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.PeerAnswerAdapter;
import net.ememed.doctor2.entity.PeerEntrty;
import net.ememed.doctor2.entity.PeerInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class PeerAnswerActivity extends BasicActivity implements OnItemClickListener{
	
	private RefreshListView question_list;
	private PeerAnswerAdapter answerAdapter;
	private TextView top_title;
	int page=1;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.activity_peer_answer);
		initView();
		getQuestionList(""+1);
	}
	
	public void initView(){
		question_list = (RefreshListView) findViewById(R.id.question_list);
		answerAdapter = new PeerAnswerAdapter(this,imageLoader,this.options);
		question_list.setAdapter(answerAdapter);
		top_title=(TextView) findViewById(R.id.top_title);
		top_title.setText("同行解答");
		question_list.setOnItemClickListener(this);
		question_list.setOnRefreshListener(new IOnRefreshListener() {
			
			@Override
			public void OnRefresh() {
				// TODO Auto-generated method stub
				refresh();
			}
		});
		question_list.setOnLoadMoreListener(new IOnLoadMoreListener() {
			
			@Override
			public void OnLoadMore() {
				// TODO Auto-generated method stub
				loadMore();
			}
		});
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
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		page=1;
		getQuestionList(page+"");
	}
	
	public void getQuestionList(String page) {
		if (NetWorkUtils.detect(this)) {
			this.loading(null);
			question_list.onRefreshComplete();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("page", page);
			System.out.println("params = "+params);
			MyApplication.volleyHttpClient.postOAuthWithParams(
					HttpUtil.get_peer_list, PeerEntrty.class, params,
					new Listener() {

						@Override
						public void onResponse(Object arg0) {
							PeerAnswerActivity.this.destroyDialog();
							PeerEntrty qp = (PeerEntrty) arg0;
							if (qp.getSuccess().equals("1")) {
								setData(qp);
							}else{
								showToast(qp.getErrormsg());
							}
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							PeerAnswerActivity.this.destroyDialog();
						}
					});
		}
	}
	
	public void setData(PeerEntrty peer){
		if(page<Integer.parseInt(peer.getPages())){
			question_list.onLoadMoreComplete(false);
		}else {
			question_list.onLoadMoreComplete(true);
		}
		answerAdapter.setData(peer.getData());
		if (peer.getSuccess().equals("1")) {
			if(page==1){
				answerAdapter.setData(peer.getData());
			}else{
				answerAdapter.addData(peer.getData());
			}
		} else {
			showToast(peer.getErrormsg());
		}
	}
	
	public void doClick(View view){
		if(view.getId()==R.id.btn_back){
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		PeerInfo pree=(PeerInfo) answerAdapter.getItem(arg2-1);
		Intent intent=new Intent(this,PreeChatActivity.class);
		intent.putExtra("title", pree.getREALNAME());
		intent.putExtra("tochat_userId", pree.getUSERID());
		intent.putExtra("user_avatar", pree.getAVATAR());
		intent.putExtra("orderid", pree.getORDERID());
		intent.putExtra("questionid", pree.getQUESTIONID());
		intent.putExtra("status", pree.getSTATUS());
		List<String> pics = pree.getPICS();
		intent.putExtra("pics", (Serializable)pics);
		startActivity(intent);
	}
}
