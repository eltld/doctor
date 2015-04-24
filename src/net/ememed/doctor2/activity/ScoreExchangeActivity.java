package net.ememed.doctor2.activity;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.baike.entity.ScoreExchangeResultEntry;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.ScoreDataBean;
import net.ememed.doctor2.entity.ScoreExchangeBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreExchangeActivity extends BasicActivity implements OnClickListener{
	
	private String str_money = "¥ %s";
	private String str_score = "%s薏米花";
	
	private Button[] btn_score_exchange = new Button[4]; 
	private TextView[] txt_score_exchange = new TextView[4];
	private TextView[] txt_money = new TextView[4];
	private TextView[] txt_score = new TextView[4];

	private TextView tv_total_score;
	
	private TextView top_title;
	private Button btn_addhealth;
	
	private List<ScoreDataBean> ratio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_exchange);
		
		initView();
	}
	
	
	
	private void initView(){
		
		tv_total_score = (TextView) findViewById(R.id.tv_total_score);
		tv_total_score.setText(String.valueOf(SharePrefUtil.getInt(Conast.YIMIHUA_TOTAL)));
		
		btn_score_exchange[0]=(Button) findViewById(R.id.btn_score_exchange1);
		btn_score_exchange[0].setOnClickListener(this);
		btn_score_exchange[1]=(Button) findViewById(R.id.btn_score_exchange2);
		btn_score_exchange[1].setOnClickListener(this);
		btn_score_exchange[2]=(Button) findViewById(R.id.btn_score_exchange3);
		btn_score_exchange[2].setOnClickListener(this);
		btn_score_exchange[3]=(Button) findViewById(R.id.btn_score_exchange4);
		btn_score_exchange[3].setOnClickListener(this);
		
		txt_score_exchange[0]=(TextView) findViewById(R.id.txt_score_exchange1);
		txt_score_exchange[1]=(TextView) findViewById(R.id.txt_score_exchange2);
		txt_score_exchange[2]=(TextView) findViewById(R.id.txt_score_exchange3);
		txt_score_exchange[3]=(TextView) findViewById(R.id.txt_score_exchange4);
		
		txt_money[0] = (TextView) findViewById(R.id.tv_money_1);
		txt_money[1] = (TextView) findViewById(R.id.tv_money_2);
		txt_money[2] = (TextView) findViewById(R.id.tv_money_3);
		txt_money[3] = (TextView) findViewById(R.id.tv_money_4);
		
		txt_score[0] = (TextView) findViewById(R.id.tv_score_1);
		txt_score[1] = (TextView) findViewById(R.id.tv_score_2);
		txt_score[2] = (TextView) findViewById(R.id.tv_score_3);
		txt_score[3] = (TextView) findViewById(R.id.tv_score_4);
		
		top_title=(TextView) findViewById(R.id.top_title);
		top_title.setText("薏米花兑换");
		btn_addhealth=(Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setText("兑换记录");
		btn_addhealth.setBackgroundResource(R.drawable.transparent_none);
		
		String exchange_ratio = SharePrefUtil.getString(Conast.YIMIHUA_EXCHANGE_RATIO);
		if(!TextUtils.isEmpty(exchange_ratio)){
			Gson gson = new Gson();
			ScoreExchangeBean bean = gson.fromJson(exchange_ratio, ScoreExchangeBean.class);
			ratio = bean.getData();
			updateView();
		}
		
		getPoint();
	}

	private void getPoint(){
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
//		loading(null);
		
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_points_charge_config,
				ScoreExchangeBean.class, null, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_RATIO;
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
	}
	
	private void scoreExchange(String money, String score){
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("money", money);
		params.put("points", score);
		params.put("app_version", MyApplication.getAppVersion());
		
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.exchange_score,
				ScoreExchangeResultEntry.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.EXCHANGE_SCORE;
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
	}
	
	
	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.GET_RATIO:
				ScoreExchangeBean entry = (ScoreExchangeBean) msg.obj;
				if(TextUtils.isEmpty(entry.getSuccess()) || "0".equals(entry.getSuccess())){
					showToast(entry.getErrormsg());
					return;
				}
				
				Gson gson = new Gson();
				String exchange_ratio = gson.toJson(entry);
				SharePrefUtil.putString(Conast.YIMIHUA_EXCHANGE_RATIO, exchange_ratio);
				SharePrefUtil.commit();
				ratio = entry.getData();
				updateView();
				break;
			case IResult.EXCHANGE_SCORE:
				ScoreExchangeResultEntry entry2 =  (ScoreExchangeResultEntry) msg.obj;
				if(!entry2.isSuccess()){
					showToast(entry2.getErrormsg());
					return;
				}
				
				showToast(entry2.getErrormsg());
				
				if(null != entry2.getData()){
					tv_total_score.setText(entry2.getData().getPOINTS_NOW());
					SharePrefUtil.putInt(Conast.YIMIHUA_TOTAL, Integer.parseInt(entry2.getData().getPOINTS_NOW()));
					SharePrefUtil.commit();
					updateView();
				}
				
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("数据错误");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	private void updateView(){
		for(int i = 0; i < txt_money.length; i++){
			txt_money[i].setText(String.format(str_money, ratio.get(i).getMoney()));
			txt_score[i].setText(String.format(str_score, ratio.get(i).getYimihua()));
			if(Integer.parseInt(ratio.get(i).getYimihua()) <= SharePrefUtil.getInt(Conast.YIMIHUA_TOTAL)){
				txt_score_exchange[i].setVisibility(View.GONE);
				btn_score_exchange[i].setVisibility(View.VISIBLE);
			} else {
				txt_score_exchange[i].setVisibility(View.VISIBLE);
				btn_score_exchange[i].setVisibility(View.GONE);
			}
		}
	}
	int i=0;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.btn_score_exchange1:
//			scoreExchange(ratio.get(0).getMoney(), ratio.get(0).getYimihua());
//			break;
//		case R.id.btn_score_exchange2:
//			scoreExchange(ratio.get(1).getMoney(), ratio.get(1).getYimihua());
//			break;
//		case R.id.btn_score_exchange3:
//			scoreExchange(ratio.get(2).getMoney(), ratio.get(2).getYimihua());
//			break;
//		case R.id.btn_score_exchange4:
//			scoreExchange(ratio.get(3).getMoney(), ratio.get(3).getYimihua());
//			break;
//		default:
//			break;
//		}
		if (v.getId()==R.id.btn_score_exchange1) {
			scoreExchange(ratio.get(0).getMoney(), ratio.get(0).getYimihua());
			i=0;
		}
		else if(v.getId()==R.id.btn_score_exchange2){
			scoreExchange(ratio.get(1).getMoney(), ratio.get(1).getYimihua());
			i=1;
		}
		else if(v.getId()==R.id.btn_score_exchange3){
			scoreExchange(ratio.get(2).getMoney(), ratio.get(2).getYimihua());
			i=2;
				}
		else if(v.getId()==R.id.btn_score_exchange4){
			scoreExchange(ratio.get(3).getMoney(), ratio.get(3).getYimihua());
			i=3;
		}
		SharePrefUtil.putString(Conast.USER_MONEY, Float.parseFloat(SharePrefUtil.getString(Conast.USER_MONEY))+Integer.parseInt(ratio.get(i).getMoney())+"");
		SharePrefUtil.putInt(Conast.YIMIHUA_TOTAL, SharePrefUtil.getInt(Conast.YIMIHUA_TOTAL)-Integer.parseInt(ratio.get(i).getYimihua()));
		SharePrefUtil.commit();
	}
	
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_addhealth:
			ExchangeRecordActivity.startAction(this, 1);
			break;
		default:
			break;
		}
	}
}
