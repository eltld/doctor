package net.ememed.doctor2.finace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.BankCard;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.finace.adapter.BankcardAdapter;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;

public class BankcardListActivity extends BasicActivity{
	
	private PopupWindow popup;
	private List<BankCardInfo> bankcardInfoList = null;
	private List<Map<String, Object>> allAccountList = null;
	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private BankcardAdapter bankcardAdapter;
	private ListView lv_bankcard;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.activity_bankcard_list);
		allAccountList = new ArrayList<Map<String,Object>>();
		bankcardAdapter = new BankcardAdapter(null, this);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		ll_net_unavailable = (LinearLayout) findViewById(R.id.ll_net_unavailable);
		
		TextView title = (TextView) findViewById(R.id.top_title);
		title.setText("收款账户");
		
		ImageView iv_right_fun = (ImageView) findViewById(R.id.iv_right_fun_2);
		iv_right_fun.setVisibility(View.VISIBLE);
		iv_right_fun.setImageDrawable(getResources().getDrawable(R.drawable.chat_detailed));
		
		lv_bankcard = (ListView) findViewById(R.id.lv_bankcard);
		lv_bankcard.setAdapter(bankcardAdapter);
		lv_bankcard.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String,Object> map = allAccountList.get(position);
				if(map.get("title").equals("银行卡") || map.get("title").equals("支付宝")){
					return;
				}
				
				BankCardInfo info = (BankCardInfo) map.get("card");
				if(null != info){
					Intent intent = null;
					if(!TextUtils.isEmpty(info.getALIPAY_ACCOUNT())){
						intent = new Intent(BankcardListActivity.this, ZhifubaoDetailActivity.class);
						intent.putExtra("name", info.getHOLDER());
						intent.putExtra("zhifubao_num", info.getALIPAY_ACCOUNT());
						intent.putExtra("mbcid", info.getMBCID());
					} else {
						intent = new Intent(BankcardListActivity.this, BankCardDetailActivity.class);
						intent.putExtra("name", info.getHOLDER());
						intent.putExtra("bank_name", info.getBANKNAME());
						intent.putExtra("bankcard_num", info.getBANKCARDNUM());
						intent.putExtra("card_num", info.getCARDNUMBER());
						intent.putExtra("mbcid", info.getMBCID());
					}
					
					startActivity(intent);
				}
				
			}
		});
		
		initAddBankcardPopupWindow();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(null != BankCardInfoCommon.getInstance().getBankcardList() || null != BankCardInfoCommon.getInstance().getZhifubaoList()){
			makeAdapterData();
		}else{
			getBankCardList();	//获取银行卡信息列表
		}
	}
	
	private void initAddBankcardPopupWindow() {
		LinearLayout ll_menu = (LinearLayout) getLayoutInflater().inflate(R.layout.bankcard_type_menu, null);
		LinearLayout ll_add_bankcard = (LinearLayout) ll_menu.findViewById(R.id.ll_add_bankcard);
		LinearLayout ll_add_zhifubao = (LinearLayout) ll_menu.findViewById(R.id.ll_add_zhifubao);
		LinearLayout ll_set = (LinearLayout) ll_menu.findViewById(R.id.ll_set);
		
		ll_add_bankcard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != popup){
					popup.dismiss();
					Intent intent = new Intent(BankcardListActivity.this, AddBankcardActivity.class);
					startActivity(intent);
				}
			}
		});
		
		ll_add_zhifubao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != popup){
					popup.dismiss();
					Intent intent = new Intent(BankcardListActivity.this, AddZhifubaoActivity.class);
					startActivity(intent);
				}
			}
		});
		
		ll_set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showToast("敬请期待！");
				
			}
		});
		
		
		popup = new PopupWindow(ll_menu,MyApplication.getInstance().canvasWidth *2/5, LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
	}
	
	public void doClick(View view){
		if(R.id.iv_right_fun_2 == view.getId()){
			if(null != popup){
				popup.showAsDropDown(view, 0, 40);
			}
		} else if(R.id.btn_back == view.getId()){
			finish();
		} else if(R.id.ll_net_unavailable == view.getId()){
			getBankCardList();
		}
	}
	
	private void getBankCardList() {
		
		if (NetWorkUtils.detect(BankcardListActivity.this)) {
			ll_net_unavailable.setVisibility(View.GONE);
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_bankcard, BankCard.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_BANKCARD;
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
			ll_net_unavailable.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.GET_BANKCARD:
				BankCard bankCard = (BankCard) msg.obj;
				if (null != bankCard) {
					if (bankCard.getSuccess() == 1) {
						bankcardInfoList = bankCard.getData();
						if (null != bankcardInfoList && bankcardInfoList.size() > 0) {
							ll_empty.setVisibility(View.GONE);
							
							BankCardInfoCommon.getInstance().clearbankCardInfo();
							BankCardInfoCommon.getInstance().saveBankCardInfo(bankcardInfoList);
							
							makeAdapterData();	//生成List列表，放入adapter
							
						} else {
							ll_empty.setVisibility(View.VISIBLE);
						}
					} else {
						showToast(bankCard.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("获取数据出错！");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	
	
	private void makeAdapterData() {
		allAccountList.clear();
	
		List<BankCardInfo> bankcardList = BankCardInfoCommon.getInstance().getBankcardList();
		List<BankCardInfo> zhifubaoList = BankCardInfoCommon.getInstance().getZhifubaoList();
		
		if(null != bankcardList && bankcardList.size() > 0){
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("title","银行卡"); 
			allAccountList.add(map1);
			
			for(BankCardInfo cardInfo : bankcardList){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("card", cardInfo);
				map.put("title", "");
				allAccountList.add(map);
			}
		}
		
		if(null != zhifubaoList && zhifubaoList.size() > 0){
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("title","支付宝"); 
			allAccountList.add(map2);
			
			for(BankCardInfo cardInfo : zhifubaoList){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("card", cardInfo);
				map.put("title", "");
				allAccountList.add(map);
			}
		}
		
		bankcardAdapter.change(allAccountList);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_bankcard);
	}
}
