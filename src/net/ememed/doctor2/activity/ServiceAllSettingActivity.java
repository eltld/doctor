package net.ememed.doctor2.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.CallSettingEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.JiahaoSettingEntry;
import net.ememed.doctor2.entity.PacketPeriod;
import net.ememed.doctor2.entity.PacketSettingEntry;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.entity.ServiceSettingInfo;
import net.ememed.doctor2.entity.ShangmenSettingEntry;
import net.ememed.doctor2.entity.TextConsultSettingEntry;
import net.ememed.doctor2.entity.TijianSettingEntry;
import net.ememed.doctor2.entity.ZhuyuanSettingEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.widget.InScrollGridView;
import net.ememed.doctor2.widget.MenuDialog;
import net.ememed.doctor2.widget.SwitchButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 服务设置 v2 */
public class ServiceAllSettingActivity extends BasicActivity implements OnRefreshListener {
	private ServiceSettingInfo serviceInfo;
	private PullToRefreshLayout mPullToRefreshLayout;

	private SwitchButton bt_switch_text_consult;
	private SwitchButton bt_switch_phone_consult;
//	private SwitchButton bt_switch_text_shangmen;
	private SwitchButton bt_switch_jiuyi;
	private SwitchButton bt_switch_zhuyuan;
	private SwitchButton bt_switch_private_doctor;
	private SwitchButton bt_switch_text_micro;// 免费微聊开关
	private TextView tv_price_week, tv_price_month, tv_price_three_month, tv_service_phone_price,
			tv_setting_text_price;
	private LinearLayout ll_text_setting;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.servicesetting_update_v2);
		EventBus.getDefault().registerSticky(this, RegisterSuccessEvent.class);
	}

	// @Override
	protected void setupView() {

		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.activity_title_service_set));
		bt_switch_text_consult = (SwitchButton) findViewById(R.id.bt_switch_text_consult);
		bt_switch_phone_consult = (SwitchButton) findViewById(R.id.bt_switch_phone_consult);
//		bt_switch_text_shangmen = (SwitchButton) findViewById(R.id.bt_switch_text_shangmen);
		bt_switch_jiuyi = (SwitchButton) findViewById(R.id.bt_switch_jiuyi);
		bt_switch_zhuyuan = (SwitchButton) findViewById(R.id.bt_switch_zhuyuan);
		bt_switch_private_doctor = (SwitchButton) findViewById(R.id.bt_switch_private_doctor);
		bt_switch_text_micro = (SwitchButton) findViewById(R.id.bt_switch_text_micro);
		bt_switch_text_consult.setChecked(true);
		bt_switch_phone_consult.setChecked(true);
//		bt_switch_text_shangmen.setChecked(true);
		bt_switch_jiuyi.setChecked(true);
		bt_switch_zhuyuan.setChecked(true);
		bt_switch_private_doctor.setChecked(true);

		tv_price_week = (TextView) findViewById(R.id.tv_price_week);
		tv_price_month = (TextView) findViewById(R.id.tv_price_month);
		tv_price_three_month = (TextView) findViewById(R.id.tv_price_three_month);
		tv_service_phone_price = (TextView) findViewById(R.id.tv_service_phone_price);
		tv_setting_text_price = (TextView) findViewById(R.id.tv_setting_text_price);

		ll_text_setting = (LinearLayout) findViewById(R.id.ll_text_setting);
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Type))
				&& "1".equals(SharePrefUtil.getString(Conast.Doctor_Type))) {
			ll_text_setting.setVisibility(View.GONE);
		} else {
			ll_text_setting.setVisibility(View.VISIBLE);
		}

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable().listener(this)
				.setup(mPullToRefreshLayout);
		getServiceSetInfo();
		super.setupView();
	}

	private void getServiceSetInfo() {
		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.getServiceSet,
					ServiceSettingInfo.class, params, new Response.Listener() {
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
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}

	}

	/** 该flag是为了防止调用setChecked方法的时候 自动进入 onCheckedChanged回调方法 */
	private boolean flag = false;

	@Override
	protected void addListener() {
		bt_switch_text_consult
				.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (flag) {
							if (isChecked) {
								enableTextConsult(0);
							} else {
								enableTextConsult(1);
							}
						}
					}
				});

		bt_switch_phone_consult
				.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (flag) {
							if (isChecked) {
								enablePhoneConsultSetting(0);
							} else {
								enablePhoneConsultSetting(1);
							}
						}
					}
				});

//		bt_switch_text_shangmen
//				.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						if (flag) {
//							if (isChecked) {
//								enableShangmen(0);
//							} else {
//								enableShangmen(1);
//							}
//						}
//					}
//				});

		bt_switch_jiuyi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (flag) {
					if (isChecked) {
						enableJiuyi(0);
					} else {
						enableJiuyi(1);
					}
				}
			}
		});

		bt_switch_zhuyuan.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (flag) {
					if (isChecked) {
						enableZhuyuan(0);
					} else {
						enableZhuyuan(1);
					}
				}
			}
		});

		bt_switch_private_doctor
				.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (flag) {
							if (isChecked) {
								enablePacket(0);// 关闭
							} else {
								enablePacket(1);// 开启
							}
						}
					}
				});

		bt_switch_text_micro.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (flag) {
					if (isChecked) {
						micro(0);
					} else {
						micro(1);
					}
				}

			}
		});
		super.addListener();
	}

	public void micro(int operation) {

		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_freetalk,
					ResultInfo.class, params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							destroyDialog();
							ResultInfo info = (ResultInfo) response;
							showToast(info.getErrormsg());
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}

	}

	/**
	 * 是否启用私人医生服务 0、关闭 1、启用
	 * */
	private void enablePacket(final int operation) {
		if ("".equals(tv_price_week.getText().toString())
				&& "".equals(tv_price_month.getText().toString())
				&& "".equals(tv_price_three_month.getText().toString())) {
			showMessage(getString(R.string.service_setting_no_price));
			bt_switch_private_doctor.setChecked(true);
			return;
		}

		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");
			params.put("packet_id", packet_id == 0 ? serviceInfo.getData().getPacket_setting()
					.getPacket_id()
					+ "" : packet_id + "");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_packet, ResultInfo.class,
					params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							operation_result = operation;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.FINISHED_PACKET;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 是否启用预约住院 0、关闭 1、启用
	 * */
	private void enableZhuyuan(final int operation) {

		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_zhuyuan,
					ResultInfo.class, params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							operation_result = operation;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.FINISHED_ZHUYUAN;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 是否启用预约加号 0、关闭 1、启用
	 * */
	private void enableJiuyi(final int operation) {

		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_jiahao, ResultInfo.class,
					params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							operation_result = operation;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.FINISHED_JIUYI;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}

	}

	/**
	 * 是否启用上门设置 0、关闭 1、启用
	 * */
	private void enableShangmen(final int operation) {

		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_shangmen,
					ResultInfo.class, params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							operation_result = operation;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.FINISHED_SHANGMEN;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 是否启用预约通话设置 0、关闭 1、启用
	 * */
	private void enablePhoneConsultSetting(final int operation) {
		if (TextUtils.isEmpty(tv_service_phone_price.getText().toString())) {
			showMessage(getString(R.string.service_setting_no_price));
			bt_switch_phone_consult.setChecked(true);
			return;
		}
		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_phone_consult,
					ResultInfo.class, params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							operation_result = operation;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.FINISHED_PHONE;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	private int operation_result;
	private int packet_id;

	/**
	 * 是否开启文字咨询 1.开启 0.关闭
	 * */
	private void enableTextConsult(final int operation) {
		if (TextUtils.isEmpty(tv_setting_text_price.getText().toString())) {
			showMessage(getString(R.string.service_setting_no_price));
			bt_switch_text_consult.setChecked(true);
			return;
		}
		if (NetWorkUtils.detect(ServiceAllSettingActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_text_consult,
					ResultInfo.class, params, new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							operation_result = operation;
							Message message = new Message();
							message.obj = response;
							message.what = IResult.FINISHED;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							message.what = IResult.NET_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.btn_setting_text_price) {
			if (null == serviceInfo || null == serviceInfo.getData()
					|| serviceInfo.getData().equals("")) {
				showToast("网络加载未完成，请稍等");
				return;
			}
			Intent intent = new Intent(this, TextConsultPriceActivity.class);
			intent.putExtra("text_price", tv_setting_text_price.getText().toString());
			startActivity(intent);
		} else if (view.getId() == R.id.btn_setting_phone_price) {
			if (null == serviceInfo || null == serviceInfo.getData()
					|| serviceInfo.getData().equals("")) {
				showToast("网络加载未完成，请稍等");
				return;
			}
			Intent intent = new Intent(this, PhoneConsultPriceActivity.class);
			intent.putExtra("phone_price", tv_service_phone_price.getText().toString());
			startActivity(intent);
		} else if (view.getId() == R.id.btn_setting_private_price) {
			if (null == serviceInfo || null == serviceInfo.getData()
					|| serviceInfo.getData().equals("")) {
				showToast("网络加载未完成，请稍等");
				return;
			}
			Intent intent = new Intent(this, PrivateDoctorPriceActivity.class);
			intent.putExtra("packet_setting", serviceInfo.getData());
			intent.putExtra("packet_setting_week_price", tv_price_week
					.getText().toString());
			intent.putExtra("packet_setting_month_price", tv_price_month
					.getText().toString());
			intent.putExtra("packet_setting_three_month_price",
					tv_price_three_month.getText().toString());
			intent.putExtra("packet_setting", (Serializable) serviceInfo.getData());
			intent.putExtra("packet_setting_week_price", tv_price_week.getText().toString());
			intent.putExtra("packet_setting_month_price", tv_price_month.getText().toString());
			intent.putExtra("packet_setting_three_month_price", tv_price_three_month.getText()
					.toString());
			startActivity(intent);
		}
	}

	public void onEvent(RegisterSuccessEvent testEvent) {
		Logger.dout("register success");
		if (testEvent.getStatus() == 1) {
			if ("".equals(testEvent.getPhoneConsultPrice())
					&& TextUtils.isEmpty(testEvent.getPhoneConsultPrice())) {
				bt_switch_phone_consult.setChecked(true);
			}
			tv_service_phone_price.setText(testEvent.getPhoneConsultPrice());
		} else if (testEvent.getStatus() == 2) {
			if (TextUtils.isEmpty(testEvent.getPrivateDoctorWeekPrice())
					&& TextUtils.isEmpty(testEvent.getPrivateDoctorMonthPrice())
					&& TextUtils.isEmpty(testEvent.getPrivateDoctorThreemonthPrice())) {
				bt_switch_private_doctor.setChecked(true);
			}
			tv_price_week.setText(testEvent.getPrivateDoctorWeekPrice());
			tv_price_month.setText(testEvent.getPrivateDoctorMonthPrice());
			tv_price_three_month.setText(testEvent.getPrivateDoctorThreemonthPrice());
			packet_id = testEvent.getPacket_id();
		} else {
			if ("".equals(testEvent.getTextConsultPrice())
					&& TextUtils.isEmpty(testEvent.getTextConsultPrice())) {
				bt_switch_text_consult.setChecked(true);
			}
			tv_setting_text_price.setText(testEvent.getTextConsultPrice());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().removeStickyEvent(RegisterSuccessEvent.class);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.dout(" onResume");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onRefreshStarted(View view) {
		mPullToRefreshLayout.setRefreshComplete();
		getServiceSetInfo();
	}

	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);

		try {
			switch (msg.what) {
			case IResult.SERVICE_SET:
				destroyDialog();
				serviceInfo = (ServiceSettingInfo) msg.obj;
				if (serviceInfo != null) {
					if (serviceInfo.getSuccess() == 1) {
						ServiceSettingEntry data = serviceInfo.getData();
						if (null != data) {
							setService(data);
						}
						flag = true;
					} else {
						showToast(serviceInfo.getErrormsg());
					}
				} else {
					showToast("加载数据失败");
				}
				break;
			case IResult.FINISHED:// 设置图文资讯开关
				destroyDialog();
				ResultInfo resultInfo = (ResultInfo) msg.obj;
				destroyDialog();
				if (resultInfo != null) {
					if (resultInfo.getSuccess() == 1) {
						serviceInfo.getData().getTextconsult_setting()
								.setEnable_textconsult(operation_result);
						SharePrefUtil.putInt(Conast.FLAG_CONSULT_PICTURE, operation_result);
						SharePrefUtil.commit();
						showToast(resultInfo.getErrormsg());
					} else {
						showToast(resultInfo.getErrormsg());
					}
				} else {
					showToast("加载数据失败");
				}
				break;
			case IResult.FINISHED_PHONE:// 设置预约通话开关
				destroyDialog();
				ResultInfo resultInfo_phone = (ResultInfo) msg.obj;
				destroyDialog();
				if (resultInfo_phone != null) {
					if (resultInfo_phone.getSuccess() == 1) {
						serviceInfo.getData().getCall_setting().setEnable_call(operation_result);
						SharePrefUtil.putInt(Conast.FLAG_CONSULT_PHONE, operation_result);
						SharePrefUtil.commit();
						showToast(resultInfo_phone.getErrormsg());
					} else {
						showToast(resultInfo_phone.getErrormsg());
					}
				} else {
					showToast("加载数据失败");
				}
				break;
			case IResult.FINISHED_SHANGMEN:// 设置上门会诊开关
				destroyDialog();
				ResultInfo resultInfo_shangmen = (ResultInfo) msg.obj;
				destroyDialog();
				if (resultInfo_shangmen != null) {
					if (resultInfo_shangmen.getSuccess() == 1) {
						serviceInfo.getData().getShangmen_setting()
								.setEnable_shangmen(operation_result);
						SharePrefUtil.putInt(Conast.FLAG_SHANGMEN, operation_result);
						SharePrefUtil.commit();
						showToast(resultInfo_shangmen.getErrormsg());
					} else {
						showToast(resultInfo_shangmen.getErrormsg());
					}
				} else {
					showToast("加载数据失败");
				}
				break;
			case IResult.FINISHED_JIUYI:// 设置预约加号开关
				destroyDialog();
				ResultInfo resultInfo_jiuyi = (ResultInfo) msg.obj;
				destroyDialog();
				if (resultInfo_jiuyi != null) {
					if (resultInfo_jiuyi.getSuccess() == 1) {
						serviceInfo.getData().getJiahao_setting()
								.setEnable_jiahao(operation_result);
						SharePrefUtil.putInt(Conast.FLAG_JINJI_JIAHAO, operation_result);
						SharePrefUtil.commit();
						showToast(resultInfo_jiuyi.getErrormsg());
					} else {
						showToast(resultInfo_jiuyi.getErrormsg());
					}
				} else {
					showToast("加载数据失败");
				}
				break;
			case IResult.FINISHED_ZHUYUAN:// 设置预约住院开关
				destroyDialog();
				ResultInfo resultInfo_zhuyuan = (ResultInfo) msg.obj;
				destroyDialog();
				if (resultInfo_zhuyuan != null) {
					if (resultInfo_zhuyuan.getSuccess() == 1) {
						serviceInfo.getData().getZhuyuan_setting()
								.setEnable_zhuyuan(operation_result);
						SharePrefUtil.putInt(Conast.FLAG_JINJI_ZHUYUAN, operation_result);
						SharePrefUtil.commit();
						showToast(resultInfo_zhuyuan.getErrormsg());
					} else {
						showToast(resultInfo_zhuyuan.getErrormsg());
					}
				} else {
					showToast("加载数据失败");
				}
				break;
			case IResult.FINISHED_PACKET:// 设置私人医生服务开关
				destroyDialog();
				ResultInfo resultInfo_packet = (ResultInfo) msg.obj;
				destroyDialog();
				if (resultInfo_packet != null) {
					if (resultInfo_packet.getSuccess() == 1) {
						serviceInfo.getData().getPacket_setting().setStatus(operation_result + "");
						SharePrefUtil.putInt(Conast.FLAG_PRIVATE_DOCTOR, operation_result);
						SharePrefUtil.commit();
						showToast(resultInfo_packet.getErrormsg());
					} else {
						showToast(getString(R.string.net_error));
					}
				} else {
					showToast("加载数据失败");
				}
				break;

			case IResult.NET_ERROR:
				destroyDialog();
				showToast(getString(R.string.net_error));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean finish = false;

	/** 信息提示 */
	private void showMessage(String msg) {
		Builder builder = new Builder(this);
		Dialog dialog = builder.setTitle(getString(R.string.system_info)).setMessage(msg)
				.setPositiveButton(getString(R.string.add_health_record_know), null).create();
		dialog.setCancelable(false);
		if (!finish) {
			dialog.show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		finish = true;
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.getServiceSet);
	}

	private void setService(ServiceSettingEntry data) {
		if (data.getTextconsult_setting().getEnable_textconsult() == 1) {
			bt_switch_text_consult.setChecked(false);
		} else {
			bt_switch_text_consult.setChecked(true);
		}

		if (data.getFreetalk_setting().getEnable_freetalk().equals("1")) {
			bt_switch_text_micro.setChecked(false);
		} else {
			bt_switch_text_micro.setChecked(true);
		}

		if (data.getCall_setting().getEnable_call() == 1) {
			bt_switch_phone_consult.setChecked(false);
		} else {
			bt_switch_phone_consult.setChecked(true);
		}

//		if (data.getShangmen_setting().getEnable_shangmen() == 1) {
//			bt_switch_text_shangmen.setChecked(false);
//		} else {
//			bt_switch_text_shangmen.setChecked(true);
//		}

		if (data.getJiahao_setting().getEnable_jiahao() == 1) {
			bt_switch_jiuyi.setChecked(false);
		} else {
			bt_switch_jiuyi.setChecked(true);
		}

		if (data.getZhuyuan_setting().getEnable_zhuyuan() == 1) {
			bt_switch_zhuyuan.setChecked(false);
		} else {
			bt_switch_zhuyuan.setChecked(true);
		}
		if (null != data.getPacket_setting() && null != data.getPacket_setting().getStatus()) {
			if (data.getPacket_setting().getStatus().equals("1")) {
				bt_switch_private_doctor.setChecked(false);// 开启
			} else {
				bt_switch_private_doctor.setChecked(true);// 关闭
			}
			List<PacketPeriod> packet_period_list = data.getPacket_setting()
					.getPacket_period_list();
			for (int i = 0; i < packet_period_list.size(); i++) {
				if (i == 0) {
					tv_price_week.setText(packet_period_list.get(i).getPacket_period_price());
				} else if (i == 1) {
					tv_price_month.setText(packet_period_list.get(i).getPacket_period_price());
				} else {
					tv_price_three_month
							.setText(packet_period_list.get(i).getPacket_period_price());
				}
			}
		} else {
			bt_switch_private_doctor.setChecked(true);// 关闭
		}
		tv_service_phone_price.setText(data.getCall_setting().getPrice_call());
		tv_setting_text_price.setText(data.getTextconsult_setting().getPrice_textconsult());

	}
}
