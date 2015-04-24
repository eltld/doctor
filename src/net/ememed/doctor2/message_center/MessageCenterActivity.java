package net.ememed.doctor2.message_center;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.SetBankCard;
import net.ememed.doctor2.finace.AddBankcardActivity;
import net.ememed.doctor2.finace.BankCardInfoCommon;
import net.ememed.doctor2.message_center.entity.SmsClassifyEntity;
import net.ememed.doctor2.message_center.entity.SmsClassifyList;
import net.ememed.doctor2.message_center.entity.SmsInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;

public class MessageCenterActivity extends BasicActivity{
	
	private TextView tv_content_1;
	private TextView tv_content_2;
	private TextView tv_content_3;
	
	private TextView tv_time_1;
	private TextView tv_time_2;
	private TextView tv_time_3;
	
	private TextView tv_unread_num_1;
	private TextView tv_unread_num_2;
	private TextView tv_unread_num_3;
	
	private TextView tv_title;
	private ImageView btn_back;
	private PopupWindow popup;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_message_center);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		tv_content_1 = (TextView) findViewById(R.id.tv_content_1);
		tv_content_2 = (TextView) findViewById(R.id.tv_content_2);
		tv_content_3 = (TextView) findViewById(R.id.tv_content_3);
		
		tv_time_1 = (TextView) findViewById(R.id.tv_time_1);
		tv_time_2 = (TextView) findViewById(R.id.tv_time_2);
		tv_time_3 = (TextView) findViewById(R.id.tv_time_3);
		
		tv_unread_num_1 = (TextView) findViewById(R.id.tv_unread_num_1);
		tv_unread_num_2 = (TextView) findViewById(R.id.tv_unread_num_2);
		tv_unread_num_3 = (TextView) findViewById(R.id.tv_unread_num_3);
		
		tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText("消息中心");
		
		btn_back = (ImageView) findViewById(R.id.btn_back);
		
		ImageView iv_right_fun = (ImageView) findViewById(R.id.iv_right_fun_2);
		iv_right_fun.setVisibility(View.VISIBLE);
		iv_right_fun.setImageDrawable(getResources().getDrawable(R.drawable.chat_detailed));
		
		initPopupWindow();	//初始化右上角菜单
		
		getMessageClassify();
	}
	
	private void getMessageClassify(){
		if (NetWorkUtils.detect(MessageCenterActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("type", "0");
			params.put("utype", "doctor");
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_msg_pool_class, SmsClassifyEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_MESSGE_CLASSSIFY;
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
	
	/**
	 * 标记所有消息为已读
	 */
	private void markSmsUnread(){
		if (NetWorkUtils.detect(MessageCenterActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("utype", "doctor");
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.mark_unread_msg, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.MARK_UNREAD_MSG;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.ERROR_MARK_UNREAD_MSG;
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
			case IResult.GET_MESSGE_CLASSSIFY:
				SmsClassifyEntity response = (SmsClassifyEntity) msg.obj;
				if (null != response) {
					if (response.getSuccess() == 1) {
						SmsClassifyList data = response.getData();
						updateView(data);
					} else {
						showToast(response.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
				break;
			case IResult.MARK_UNREAD_MSG:
				CommonResponseEntity response2 = (CommonResponseEntity) msg.obj;
				if(null != response2){
					if (response2.getSuccess() == 1) {
						tv_unread_num_1.setVisibility(View.GONE);
						tv_unread_num_2.setVisibility(View.GONE);
						tv_unread_num_3.setVisibility(View.GONE);
					}
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
	private void updateView(SmsClassifyList data) {
		
		//订单服务短信
		if(data.getSms_order() != null){
			SmsInfo orderMsgInfo = data.getSms_order();
			if(orderMsgInfo.getUnread_count() > 0){		//红点
				tv_unread_num_1.setVisibility(View.VISIBLE);
				tv_unread_num_1.setText(""+orderMsgInfo.getUnread_count());
			}
			
			if(orderMsgInfo.getLastest() != null){
				if(!TextUtils.isEmpty(orderMsgInfo.getLastest().getCONTEXT())){	//最后一条消息内容
					tv_content_1.setText(orderMsgInfo.getLastest().getCONTEXT());
				}
				
				if(!TextUtils.isEmpty(orderMsgInfo.getLastest().getADDTIME())){	//消息的时间
					tv_time_1.setText(orderMsgInfo.getLastest().getADDTIME());
				}
			}
		}
		
		//提现短信
		if(data.getSms_charge() != null){
			SmsInfo chargeMsgInfo = data.getSms_charge();
			if(chargeMsgInfo.getUnread_count() > 0){		//红点
				tv_unread_num_2.setVisibility(View.VISIBLE);
				tv_unread_num_2.setText(""+chargeMsgInfo.getUnread_count());
			}
			
			if(chargeMsgInfo.getLastest() != null){
				if(!TextUtils.isEmpty(chargeMsgInfo.getLastest().getCONTEXT())){	//最后一条消息内容
					tv_content_2.setText(chargeMsgInfo.getLastest().getCONTEXT());
				}
				
				if(!TextUtils.isEmpty(chargeMsgInfo.getLastest().getADDTIME())){	//消息的时间
					tv_time_2.setText(chargeMsgInfo.getLastest().getADDTIME());
				}
			}
		}

		//活动短信
		if(data.getSms_order() != null){
			SmsInfo activityMsgInfo = data.getSms_activity();
			if(activityMsgInfo.getUnread_count() > 0){		//红点
				tv_unread_num_3.setVisibility(View.VISIBLE);
				tv_unread_num_3.setText(""+activityMsgInfo.getUnread_count());
			}
			
			if(activityMsgInfo.getLastest() != null){
				if(!TextUtils.isEmpty(activityMsgInfo.getLastest().getCONTEXT())){	//最后一条消息内容
					tv_content_3.setText(activityMsgInfo.getLastest().getCONTEXT());
				}
				
				if(!TextUtils.isEmpty(activityMsgInfo.getLastest().getADDTIME())){	//消息的时间
					tv_time_3.setText(activityMsgInfo.getLastest().getADDTIME());
				}
			}
		}
	}
	
	public void doClick(View view){
		Intent intent = new Intent(MessageCenterActivity.this, MessageListActivity.class);
		switch(view.getId()){
		case R.id.btn_back:
			finish();
			break;
		case R.id.iv_right_fun_2:
			if(null != popup){
				popup.showAsDropDown(view, 0, 40);
			}
			break;
		case R.id.rl_order_message:			//医生端：1=订单服务类，2=提现通知类，3=活动类。患者端：1=订单服务类，2=充值类，3=活动类
			intent.putExtra("sms_type", "1");
			tv_unread_num_1.setVisibility(View.GONE);
			startActivity(intent);
			break;
		case R.id.rl_charge_message:
			intent.putExtra("sms_type", "2");
			tv_unread_num_2.setVisibility(View.GONE);
			startActivity(intent);
			break;
		case R.id.rl_activity_message:
			intent.putExtra("sms_type", "3");
			tv_unread_num_3.setVisibility(View.GONE);
			startActivity(intent);
			break;
		}
	}
	
	
	private void initPopupWindow() {
		LinearLayout ll_menu = (LinearLayout) getLayoutInflater().inflate(R.layout.msg_list_popup_menu, null);
		LinearLayout ll_sms_set = (LinearLayout) ll_menu.findViewById(R.id.ll_sms_set);
		LinearLayout ll_sms_mark_read = (LinearLayout) ll_menu.findViewById(R.id.ll_sms_mark_read);
	
		
		ll_sms_set.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != popup){
					popup.dismiss();
					Intent intent = new Intent(MessageCenterActivity.this, MessageSettingActivity.class);
					startActivity(intent);
				}
			}
		});
		
		ll_sms_mark_read.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(null != popup){
					popup.dismiss();
					markSmsUnread();
				}
			}
		});
		
		popup = new PopupWindow(ll_menu,MyApplication.getInstance().canvasWidth*2/5, LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_msg_pool_class);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.mark_unread_msg);
	}
	
}
