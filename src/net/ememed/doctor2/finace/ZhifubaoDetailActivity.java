package net.ememed.doctor2.finace;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.BankCard;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;

public class ZhifubaoDetailActivity extends BasicActivity{
	
	private Button btn_unbind;
	private TextView tv_name;
	private TextView tv_zhifubao_num;
	
	private String name;
	private String zhifubao_num;
	private String mbcid;
	
	private TextView top_title;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.zhifubao_detail);
		
		name = getIntent().getStringExtra("name");
		zhifubao_num = getIntent().getStringExtra("zhifubao_num");
		mbcid = getIntent().getStringExtra("mbcid");
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("支付宝详情");
		
		btn_unbind = (Button) findViewById(R.id.btn_unbind);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_zhifubao_num = (TextView) findViewById(R.id.tv_zhifubao_num);
		
		tv_name.setText(name);
		tv_zhifubao_num.setText(zhifubao_num);
	}
	
	
	public void doClick(View view) {
		
		if(R.id.btn_back == view.getId()){
			finish();
		}
		
		if(R.id.btn_unbind == view.getId()){
			unBindBankCard();
		}
	}
	
	private void unBindBankCard(){
		if (NetWorkUtils.detect(ZhifubaoDetailActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("mbcid", mbcid);
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.unbind_bankcard, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.UNBIND_BANKCARD;
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
		super.onResult(msg);
		
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.UNBIND_BANKCARD:
				CommonResponseEntity response = (CommonResponseEntity) msg.obj;
				if (null != response) {
					if (response.getSuccess() == 1) {
						BankCardInfoCommon.getInstance().deleteZhifubao(mbcid);
						showToast("解绑成功");
						sendBroadcast(new Intent(ActionFilter.REQUEST_FINACE_DETAIL));
						ZhifubaoDetailActivity.this.finish();
					} else {
						showToast(response.getErrormsg());
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
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.unbind_bankcard);
	}
}
