package net.ememed.doctor2.activity;

import java.util.HashMap;

import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.QuestionPoolAdapter;
import net.ememed.doctor2.entity.BalanceEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.QuestionPool;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.SettlementDialog;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.content.Intent;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

public class QuestionPoolActivity extends BasicActivity implements OnRefreshListener,
		Handler.Callback {

	QuestionPoolAdapter adapter;
	private RefreshListView question_pool_list;
	private TextView top_title;
	private TextView tv_menu;
	int page=1;
	private String balancePlan;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_pool_renling);
		question_pool_list = (RefreshListView) findViewById(R.id.question_pool_list);
		adapter = new QuestionPoolAdapter(this, imageLoader);
		question_pool_list.setAdapter(adapter);
		top_title = (TextView) findViewById(R.id.top_title);
		tv_menu = (TextView) findViewById(R.id.tv_menu);
		top_title.setText("一问一答");
		tv_menu.setText("结算方案");
		tv_menu.setVisibility(View.VISIBLE);
		question_pool_list.setOnRefreshListener(new IOnRefreshListener() {
			public void OnRefresh() {
				refresh();
			}
		});
		question_pool_list.setOnLoadMoreListener(new IOnLoadMoreListener() {
			public void OnLoadMore() {
				loadMore();
			}
		});
		
		page=1;
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
			break;
		case IResult.NET_ERROR:
			this.destroyDialog();
			break;
		default:
			break;
		}

		return false;
	}
	
	public void getQuestionList(String page) {
		if (NetWorkUtils.detect(this)) {
			this.loading(null);
			question_pool_list.onRefreshComplete();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page);
			System.out.println("params = " + params);
			MyApplication.volleyHttpClient.postOAuthWithParams(
					HttpUtil.get_unclaim_list, QuestionPool.class, params,
					new Listener() {

						@Override
						public void onResponse(Object arg0) {
							QuestionPoolActivity.this.destroyDialog();
							setData((QuestionPool) arg0);
							
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							QuestionPoolActivity.this.destroyDialog();
						}
					});

		}else{
			showToast("网络异常，请检查网络设置");
		}
	}

	public void setData(QuestionPool pool) {
		if (page < Integer.parseInt(pool.getPages())) {
			question_pool_list.onLoadMoreComplete(false);
		} else {
			question_pool_list.onLoadMoreComplete(true);
		}
		if (pool.getSuccess().equals("1")) {
			if(page==1){
				adapter.setData(pool.getData());
			}else{
				adapter.addData(pool.getData());
			}
		} else {
			showToast(pool.getErrormsg());
		}
	}
	
	public void balancePlan(){
		if (NetWorkUtils.detect(this)) {
			this.loading(null);
			question_pool_list.onRefreshComplete();
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			System.out.println("params = " + params);
			MyApplication.volleyHttpClient.postOAuthWithParams(
					HttpUtil.balance_plan, BalanceEntity.class, params,
					new Listener() {

						@Override
						public void onResponse(Object arg0) {
							QuestionPoolActivity.this.destroyDialog();
							BalanceEntity balanceEntity = (BalanceEntity) arg0;
							if(balanceEntity!=null){
								if(balanceEntity.getSuccess().trim().equals("1")){
									balancePlan = balanceEntity.getData();
									showBalancePlanDialog();
								}
							}
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							QuestionPoolActivity.this.destroyDialog();
						}
					});

		}else{
			showToast("网络异常，请检查网络设置");
		}
	}

	public void doClick(View view) {
		
		
		switch (view.getId()) {
		case R.id.question_pool_rl_renling:
			Intent intent = new Intent();
			intent.setClass(QuestionPoolActivity.this, ClaimListActivity.class);
			startActivity(intent);

			break;
//		case R.id.answer_layout:
//			Intent intent1 = new Intent();
//			intent1.setClass(QuestionPoolActivity.this, PeerAnswerActivity.class);
//			startActivity(intent1);
//			
//			break;
			
		case R.id.btn_back:
			finish();
			break;
		case R.id.tv_menu://结算方案
			if (balancePlan == null) {
				balancePlan();
			}else {
				showBalancePlanDialog();
			}
			break;

		default:
			break;
		}
	}
	
	
	private void showBalancePlanDialog() {
		SettlementDialog dialog = new SettlementDialog(QuestionPoolActivity.this, R.style.MyDialogStyle);
		dialog.show();
		dialog.setParams(CanvasWidth);
		dialog.setText(balancePlan);
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		
	}
}
