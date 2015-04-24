package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PacketPeriod;
import net.ememed.doctor2.entity.PacketSettingEntry;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.entity.ServicePacketSetInfo;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;

public class PrivateDoctorPriceActivity extends BasicActivity {
	private EditText et_setting_price_order;
	private Button btn_addhealth;
	private TextView top_title;
	private LinearLayout ll_setting_price_order, ll_setting_price_week, ll_setting_price_month, ll_setting_price_three_month;
	private ServiceSettingEntry packetSettingEntry = null;
	private EditText et_setting_price_week, et_setting_price_month, et_setting_price_three_month;
	private String week_price;
	private String month_price;
	private String three_month_price;
	private boolean finish = false;

	private ServicePacketSetInfo mInfo = null;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_setting_price);

		packetSettingEntry = (ServiceSettingEntry) getIntent().getSerializableExtra("packet_setting");
		week_price = getIntent().getStringExtra("packet_setting_week_price");
		month_price = getIntent().getStringExtra("packet_setting_month_price");
		three_month_price = getIntent().getStringExtra("packet_setting_three_month_price");
		if (packetSettingEntry == null) {
			packetSettingEntry = new ServiceSettingEntry();
		}
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		ll_setting_price_order = (LinearLayout) findViewById(R.id.ll_setting_price_order);
		ll_setting_price_order.setVisibility(View.GONE);
		ll_setting_price_week = (LinearLayout) findViewById(R.id.ll_setting_price_week);
		ll_setting_price_week.setVisibility(View.VISIBLE);
		ll_setting_price_month = (LinearLayout) findViewById(R.id.ll_setting_price_month);
		ll_setting_price_month.setVisibility(View.VISIBLE);
		ll_setting_price_three_month = (LinearLayout) findViewById(R.id.ll_setting_price_three_month);
		ll_setting_price_three_month.setVisibility(View.VISIBLE);
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.service_setting_price_v2));
		et_setting_price_week = (EditText) findViewById(R.id.et_setting_price_week);
		et_setting_price_month = (EditText) findViewById(R.id.et_setting_price_month);
		et_setting_price_three_month = (EditText) findViewById(R.id.et_setting_price_three_month);

		et_setting_price_week.setText(week_price);
		et_setting_price_month.setText(month_price);
		et_setting_price_three_month.setText(three_month_price);
		super.setupView();
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) PrivateDoctorPriceActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					et_setting_price_week.setFocusable(true);
					// et_setting_price_week.setFocusableInTouchMode(true);
					et_setting_price_week.requestFocus();
					et_setting_price_week.setSelection(et_setting_price_week.getText().length());
					et_setting_price_month.setSelection(et_setting_price_month.getText().length());
					et_setting_price_three_month.setSelection(et_setting_price_three_month.getText().length());
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			setPacket();
		}
	}

	private void setPacket() {
		if (NetWorkUtils.detect(PrivateDoctorPriceActivity.this)) {
			ArrayList<PacketPeriod> periodList = new ArrayList<PacketPeriod>();
			periodList.clear();
			PacketPeriod packetPeriodItem = new PacketPeriod();
			if (!TextUtils.isEmpty(et_setting_price_week.getText().toString())) {
				packetPeriodItem.setPacket_daytype("0");
				packetPeriodItem.setPacket_period_price(et_setting_price_week.getText().toString());
				packetPeriodItem.setPacket_daynum(1);
				periodList.add(packetPeriodItem);
			}

			PacketPeriod packetPeriodItem2 = new PacketPeriod();
			if (!TextUtils.isEmpty(et_setting_price_month.getText().toString())) {
				packetPeriodItem2.setPacket_daytype("1");
				packetPeriodItem2.setPacket_daynum(1);
				packetPeriodItem2.setPacket_period_price(et_setting_price_month.getText().toString());
				periodList.add(packetPeriodItem2);
			}

			PacketPeriod packetPeriodItem3 = new PacketPeriod();
			if (!TextUtils.isEmpty(et_setting_price_three_month.getText().toString())) {
				packetPeriodItem3.setPacket_daytype("1");
				packetPeriodItem3.setPacket_daynum(3);
				packetPeriodItem3.setPacket_period_price(et_setting_price_three_month.getText().toString());
				periodList.add(packetPeriodItem3);
			}

			if(null == packetSettingEntry.getPacket_setting())
			{
				packetSettingEntry.setPacket_setting(new PacketSettingEntry());
			}
			
			packetSettingEntry.getPacket_setting().setPacket_period_list(periodList);
			Gson gson = new Gson();
			final String convertServicePeriod = gson.toJson(periodList);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("packet_enable_textconsult", packetSettingEntry.getPacket_setting().getPacket_enable_textconsult() + "");
			params.put("packet_enable_call", packetSettingEntry.getPacket_setting().getPacket_enable_call() + "");
			params.put("packet_period_list", convertServicePeriod);
			loading(null);
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_packet, ServicePacketSetInfo.class, params, new Response.Listener() {
				@Override
				public void onResponse(Object response) {

					Message message = new Message();
					message.obj = response;
					message.what = IResult.SERVICE_SET;
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
		destroyDialog();
		try {
			switch (msg.what) {
			case IResult.SERVICE_SET:
				ServicePacketSetInfo service = (ServicePacketSetInfo) msg.obj;
				mInfo = (ServicePacketSetInfo) msg.obj;
				if (null != service) {
					if (service.getSuccess() == 1) {
						RegisterSuccessEvent event = new RegisterSuccessEvent();
						event.setPrivateDoctorWeekPrice(et_setting_price_week.getText().toString());
						event.setPrivateDoctorMonthPrice(et_setting_price_month.getText().toString());
						event.setPrivateDoctorThreemonthPrice(et_setting_price_three_month.getText().toString());
						event.setPacket_id(service.getData().getPacket_id());
						event.setStatus(2);
						EventBus.getDefault().postSticky(event);
						if (TextUtils.isEmpty(et_setting_price_week.getText().toString()) && TextUtils.isEmpty(et_setting_price_month.getText().toString()) && TextUtils.isEmpty(et_setting_price_three_month.getText().toString())) {
							enablePacket(0);
							showMessage(getString(R.string.service_setting_price));
							return;
						}
						finish();
					} else {
						showToast(service.getErrormsg());
					}
				}
				break;

			case IResult.FINISHED_PACKET:
				break;

			case IResult.DATA_ERROR:
				showToast(getString(R.string.text_get_data_none));
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	/** 信息提示 */
	public void showMessage(String msg) {
		Builder builder = new Builder(this);
		Dialog dialog = builder.setTitle(getString(R.string.system_info)).setMessage(msg).setPositiveButton(getString(R.string.bt_ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		}).setNegativeButton(getString(R.string.bt_cancel), null).create();
		dialog.setCancelable(false);
		if (!finish) {
			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish = true;
		EventBus.getDefault().removeStickyEvent(RegisterSuccessEvent.class);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void enablePacket(final int operation) {

		if (NetWorkUtils.detect(PrivateDoctorPriceActivity.this)) {

			if (null == mInfo || null == mInfo.getData()) {
				return;
			}

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");
			params.put("packet_id", mInfo.getData().getPacket_id() + "");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_packet, ResultInfo.class, params, new Response.Listener() {

				@Override
				public void onResponse(Object response) {

					ResultInfo resultInfo_packet = (ResultInfo) response;
					if (null != resultInfo_packet) {
						if (resultInfo_packet.getSuccess() != 1)
							showToast(resultInfo_packet.getErrormsg());
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					showToast(IMessage.NET_ERROR);
				}
			});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
}
