package net.ememed.doctor2.message_center;

import java.util.HashMap;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.message_center.entity.SmsSettingDetail;
import net.ememed.doctor2.message_center.entity.SmsSettingEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.SwitchButton;

public class MessageSettingActivity extends BasicActivity{
	
	private SwitchButton switch_order_sms; 
	private SwitchButton switch_cash_sms; 
	private SwitchButton switch_activity_sms; 
	private TextView top_title;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		
		setContentView(R.layout.sms_setting);
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		switch_order_sms = (SwitchButton) findViewById(R.id.bt_switch_order_sms);
		switch_cash_sms = (SwitchButton) findViewById(R.id.bt_switch_cash_sms);
		switch_activity_sms = (SwitchButton) findViewById(R.id.bt_switch_activity_sms);
		
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("消息设置");
		
		getMessageSetting();
	}
	
	/** 该flag是为了防止调用setChecked方法的时候 自动进入 onCheckedChanged回调方法 */
	private boolean flag = false;
	
	@Override
	protected void addListener() {
		switch_order_sms.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (flag) {
					if (isChecked) {
						setMessageSwitch(0, -1, -1);
					} else {
						setMessageSwitch(1, -1, -1);
					}
				}
			}
		});

		switch_cash_sms.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (flag) {
					if (isChecked) {
						setMessageSwitch(-1, 0, -1);
					} else {
						setMessageSwitch(-1, 1, -1);
					}
				}
			}
		});

		switch_activity_sms.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (flag) {
					if (isChecked) {
						setMessageSwitch(-1, -1, 0);
					} else {
						setMessageSwitch(-1, -1, 1);
					}
				}
			}
		});

		super.addListener();
	}
	
	
	
	private void getMessageSetting(){
		if (NetWorkUtils.detect(MessageSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_message_setting, SmsSettingEntry.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_MESSAGE_SETTING;
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
	 * 设置短信开关 (1:开启，0：关闭， -1： 不传此参数)
	 * @param sms_order
	 * @param sms_apply_cash
	 * @param sms_activity
	 */
	private void setMessageSwitch(int sms_order, int sms_apply_cash, int sms_activity){
		if (NetWorkUtils.detect(MessageSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			
			if(-1 != sms_order){
				params.put("sms_order", ""+sms_order);
			}
			if(-1 != sms_apply_cash){
				params.put("sms_apply_cash", ""+sms_apply_cash);
			}
			if(-1 != sms_activity){
				params.put("sms_activity", ""+sms_activity);
			}
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_message_setting, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SET_MESSAGE_SETTING;
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
			case IResult.GET_MESSAGE_SETTING:
				SmsSettingEntry response = (SmsSettingEntry) msg.obj;
				if (null != response) {
					if (response.getSuccess() == 1) {
						setSwitch(response.getData());
						flag = true;
					} else {
						showToast(response.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
				break;
			case IResult.SET_MESSAGE_SETTING:
				CommonResponseEntity response2 = (CommonResponseEntity) msg.obj;
				if (null != response2) {
					if (response2.getSuccess() == 1) {
						showToast("设置成功");
					} else {
						showToast(response2.getErrormsg());
					}
				} else {
					showToast("设置失败");
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

	/**
	 * 设置选择开关
	 */
	private void setSwitch(SmsSettingDetail setting) {
		if("1".equals(setting.getSMS_ORDER())){
			switch_order_sms.setChecked(false);
		}else{
			switch_order_sms.setChecked(true);
		}
		
		if("1".equals(setting.getSMS_APPLY_CASH())){
			switch_cash_sms.setChecked(false);
		}else{
			switch_cash_sms.setChecked(true);
		}
		
		if("1".equals(setting.getSMS_ACTIVITY())){
			switch_activity_sms.setChecked(false);
		}else{
			switch_activity_sms.setChecked(true);
		}
	}
	
	public void doClick(View view) {
		if(R.id.btn_back == view.getId()){
			finish();
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_message_setting);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_message_setting);
	}
}
