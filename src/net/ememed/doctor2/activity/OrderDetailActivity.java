package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.DoctorChatActivity.RefurbishBroadCast;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MessageBackupFieldEntry;
import net.ememed.doctor2.entity.MessageSystemEntry;
import net.ememed.doctor2.entity.OrderDetailInfo;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.util.TimeUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.ColoredRatingBar;
import net.ememed.doctor2.widget.InScrollGridView;
import net.ememed.doctor2.widget.MenuDialog;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.MessageSystemEvent;
import de.greenrobot.event.MessageSystemEvent.ServiceType;

/***
 * 订单流转页
 * 
 * 1.预约通话 -- 需要设置时间 2.线下服务 -- 需要设置价格 3.私人医生服务 -- 设置时间
 * 
 * @author chen
 * 
 */
public class OrderDetailActivity extends BasicActivity {

	private CircleImageView image_person;
	private ImageView public_image_service;
	private TextView public_text_service;
	private TextView tv_time_request;
	private OrderListEntry orderEntry;
	private String ordertype;
	private TextView tv_contant_request;
	private TextView tv_order_name;
	private LinearLayout ll_order_price;
	private TextView tv_price;
	private TextView tv_ordersn;
	/** 用于 时间 */
	private long[] time_array;
	private boolean hasSetOfflinePrice = false;
	private boolean hasSetCallTime = false;
	private String user_name;
	private String user_avatar;
	
	private String price;
	private String orderName;
	
	Intent broadCastIntent;
	
	
	
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		user_name = getIntent().getStringExtra("user_name");
		user_avatar = getIntent().getStringExtra("user_avatar");
		orderEntry = (OrderListEntry) getIntent().getSerializableExtra("order");
		if (orderEntry != null) {
			ordertype = orderEntry.getORDERTYPE();
		} else {
			ordertype = getIntent().getStringExtra("ORDERTYPE");
		}

		if ("1".equals(ordertype)) {// 文字
			orderName = "图文咨询";
			setContentView(R.layout.contact_info_textconsult);
		} else if ("2".equals(ordertype)) {// 电话
			orderName = "预约通话";
			setContentView(R.layout.contact_info_callconsult);
		} else if ("3".equals(ordertype)) {// 加号
			orderName = "预约加号";
			setContentView(R.layout.contact_info_jiahao);
		} else if ("4".equals(ordertype)) {// 上门
			orderName = "上门会诊";
			setContentView(R.layout.contact_info_shangmen);
		} else if ("14".equals(ordertype)) {// 住院
			orderName = "预约住院";
			setContentView(R.layout.contact_info_in_hospital);
		} else if ("15".equals(ordertype)) {// 私人医生服务
			orderName = "私人医生服务";
			setContentView(R.layout.contact_info_sirenyisheng);
		} else {
			orderName = "";
			setContentView(R.layout.contact_info_in_custom);
		}
		

	}

	@Override
	protected void setupView() {
		super.setupView();
		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.act_order_title));
		tv_order_name = (TextView) findViewById(R.id.tv_order_name);
		image_person = (CircleImageView) findViewById(R.id.image_person);
		public_image_service = (ImageView) findViewById(R.id.public_image_service);
		public_text_service = (TextView) findViewById(R.id.public_text_service);
		tv_contant_request = (TextView) findViewById(R.id.tv_contant_request);
		tv_time_request = (TextView) findViewById(R.id.tv_time_request);
		tv_ordersn = (TextView) findViewById(R.id.tv_ordersn);

		public_image_service.setImageResource(PublicUtil
				.getServiceDrawableByServiceid(ordertype));
		public_text_service.setText(PublicUtil.getServiceNameByid(ordertype));
		tv_contant_request.setText(Html.fromHtml(PublicUtil
				.getServiceContentByid(ordertype)));
		ll_order_price = (LinearLayout) findViewById(R.id.ll_order_price);
		tv_price = (TextView) findViewById(R.id.tv_price);
		if (orderEntry == null) {
			loading(null);
			getUserOrderInfo(getIntent().getStringExtra("ORDERID"));
		} else {
			initData();
		}
	}

	private void initData() {

		tv_time_request.setText(orderEntry.getADDTIME());
		tv_ordersn.setText(orderEntry.getORDERSN());
		if (!TextUtils.isEmpty(orderEntry.getGOODSAMOUNT())) {
			// ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"名医加号",'4'=>"上门会诊",'14'=>"住院直通车",'15'=>"私人医生服务服务",'16'=>"其他服务"
			if ("0".equals(orderEntry.getGOODSAMOUNT())) {
				if ("1".equals(orderEntry.getORDERTYPE())
						|| "2".equals(orderEntry.getORDERTYPE())
						|| "15".equals(orderEntry.getORDERTYPE())) {
					ll_order_price.setVisibility(View.VISIBLE);
					tv_price.setText(getString(R.string.text_order_price_free));
				} else {
					ll_order_price.setVisibility(View.GONE);
				}
			} else {
				ll_order_price.setVisibility(View.VISIBLE);
				tv_price.setText("￥" + orderEntry.getGOODSAMOUNT());
			}
		}

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
	protected void getData() {
		super.getData();
		if (orderEntry != null)
			getUserOrderInfo(orderEntry.getORDERID());
	}

	@Override
	protected void onResult(Message msg) {
		try {
			super.onResult(msg);
			switch (msg.what) {
			case IResult.RESULT:
				destroyDialog();
				entry = (OrderDetailInfo) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						updateContentView(entry);
						if (orderEntry == null) {
							orderEntry = new OrderListEntry();
							orderEntry.setADDTIME(entry.getData().getADDTIEM());
							orderEntry.setORDERSN(entry.getData().getORDERSN());
							orderEntry.setGOODSAMOUNT(entry.getData().getGOODSAMOUNT());
							orderEntry.setORDERID(entry.getData().getORDERID());
							orderEntry.setSERVICEID(entry.getData().getSERVICEID());
							orderEntry.setUSERID(entry.getData().getUSERID());
							initData();
						}
					} else {
						showToast(entry.getErrormsg());
					}
				} else {

				}
				break;
			case IResult.SET_SERVICE_PRICE:
				ResultInfo entry_result = (ResultInfo) msg.obj;
				if (null != entry_result) {
					if (entry_result.getSuccess() == 1) {
						Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
						btn_service_setting.setVisibility(View.GONE);
						getUserOrderInfo(orderEntry.getORDERID());
						
						
						String doctor = "您已设置"+orderName+"服务的价格为："+price+"元，请在用户支付成功后安排具体服务";
						String user =  "医生已设置"+orderName+"服务的价格为："+price+"元，请前往订单页付款。)";
						String systemMSG = toStringJSON(doctor, user);
						sendEMMessage(systemMSG, orderEntry);
						showToast("设置价格成功");
						
						
						hasSetOfflinePrice = true;
					} else {
						showToast(entry_result.getErrormsg());
					}
				}
				break;
			case IResult.SET_CALL_TIME:
				destroyDialog();
				ResultInfo entry_time = (ResultInfo) msg.obj;
				if (null != entry_time) {
					if (entry_time.getSuccess() == 1) {
						
						Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
						btn_service_setting.setVisibility(View.GONE);
						
						String doctor = "已设定通话时间为 "+TimeUtil.parseDateTime(selectedTime, "yyyy-MM-dd HH:mm")+" ，届时请您保持电话通畅。";
						String user = "医生已设定通话时间为 "+TimeUtil.parseDateTime(selectedTime, "yyyy-MM-dd HH:mm")+" ，届时请您保持电话通畅。";
						String systemMSG = toStringJSON(doctor, user);
						broadCastIntent = new Intent(RefurbishBroadCast.class.getName());
						broadCastIntent.putExtra("time", TimeUtil.parseDateTime(selectedTime, "yyyy-MM-dd HH:mm"));
						sendEMMessage(systemMSG, orderEntry);
						showToast(entry_time.getErrormsg());
						getUserOrderInfo(orderEntry.getORDERID());
						
						
						

						hasSetCallTime = true;

					} else {
						showToast(entry_time.getErrormsg());
					}
				}
				break;
			case IResult.DATA_ERROR:
				destroyDialog();
				break;
			case IResult.NET_ERROR:

				break;
			case IResult.SHOW_TIME_DIALOG:
				setServiceTimeDialog();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取到订单详情或者订单有变化的时候 更新数据
	 * 
	 * @param entry
	 */
	public void updateContentView(OrderDetailInfo entry) {
		try {

			imageLoader.displayImage(entry.getData().getAVATAR(), image_person,
					Util.getOptions_pic());
			tv_order_name.setText(entry.getData().getANY_USER_NAME());
			if ("1".equals(ordertype)) {// 文字
				TextView tv_seek_step1 = (TextView) findViewById(R.id.tv_seek_step1);
				TextView tv_seek_step2 = (TextView) findViewById(R.id.tv_seek_step2);
				TextView tv_seek_step3 = (TextView) findViewById(R.id.tv_seek_step3);
				Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
				if (!TextUtils.isEmpty(entry.getData().getSTATE())) {
					if ("1".equals(entry.getData().getSTATE())) {
						btn_converstion.setText(getString(R.string.bt_consult));
						btn_converstion.setClickable(true);
						btn_converstion
								.setBackgroundResource(R.drawable.bt_login);
						tv_seek_step1.setSelected(false);
						tv_seek_step2.setSelected(true);
					} else if ("2".equals(entry.getData().getSTATE())) {
						btn_converstion
								.setText(getString(R.string.bt_consult_finished));
						btn_converstion.setClickable(false);
						btn_converstion
								.setBackgroundResource(R.drawable.btn_style_gray);
						tv_seek_step1.setSelected(false);
						tv_seek_step3.setSelected(true);
					}
				}
			} else if ("2".equals(ordertype)) {// 电话
				updateCallServiceView(entry);
			} else if ("3".equals(ordertype)) {// 加号

				if (!TextUtils.isEmpty(entry.getData().getSTATE())) {

					ImageView iv_step_1 = (ImageView) findViewById(R.id.iv_step_1);
					ImageView iv_step_2 = (ImageView) findViewById(R.id.iv_step_2);
					ImageView iv_step_3 = (ImageView) findViewById(R.id.iv_step_3);
					ImageView iv_step_4 = (ImageView) findViewById(R.id.iv_step_4);
					ImageView iv_step_5 = (ImageView) findViewById(R.id.iv_step_5);
					ImageView line_step_1 = (ImageView) findViewById(R.id.line_step_1);
					ImageView line_step_2 = (ImageView) findViewById(R.id.line_step_2);
					ImageView line_step_3 = (ImageView) findViewById(R.id.line_step_3);
					ImageView line_step_4 = (ImageView) findViewById(R.id.line_step_4);

					Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
					Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
					LinearLayout ll_state_time = (LinearLayout) findViewById(R.id.ll_state_time);
					LinearLayout ll_evaluate_star = (LinearLayout) findViewById(R.id.ll_evaluate_star);
					TextView tv_evalute_title = (TextView) findViewById(R.id.tv_evalute_title);
					TextView tv_evaluate = (TextView) findViewById(R.id.tv_evaluate);
					ColoredRatingBar rtb_service_goods = (ColoredRatingBar) findViewById(R.id.rtb_service_goods);
					if ("1".equals(entry.getData().getSTATE())) {// 待处理
						ll_state_time.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.VISIBLE);
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
					} else if ("2".equals(entry.getData().getSTATE())) {// 等待用户支付
						ll_state_time.setVisibility(View.VISIBLE);
						ll_evaluate_star.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.GONE);
						tv_evaluate.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_waiting));
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
					} else if ("3".equals(entry.getData().getSTATE())) {// 3用户支付成功
																		// 请务必在服务后要求用户评价！
						ll_state_time.setVisibility(View.VISIBLE);
						btn_service_setting.setVisibility(View.GONE);
						ll_evaluate_star.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_success));
						tv_evaluate.setVisibility(View.VISIBLE);
						tv_evaluate
								.setText(getString(R.string.text_pay_success_detail));
						ll_order_price.setVisibility(View.VISIBLE);
						tv_price.setText("￥" + entry.getData().getGOODSAMOUNT());
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
						iv_step_3.setSelected(true);
						line_step_3.setSelected(true);
					} else if ("4".equals(entry.getData().getSTATE())) {// 服务结束
						ll_state_time.setVisibility(View.VISIBLE);
						btn_converstion
								.setText(getString(R.string.bt_consult_finished));
						btn_converstion.setClickable(false);
						btn_converstion
								.setBackgroundResource(R.drawable.btn_style_gray);
						btn_service_setting.setVisibility(View.GONE);
						ll_order_price.setVisibility(View.VISIBLE);
						tv_price.setText("￥" + entry.getData().getGOODSAMOUNT());
						iv_step_5.setSelected(true);
						line_step_4.setSelected(true);
						if (!TextUtils
								.isEmpty(entry.getData().getDSRATTITUDE())) {
							rtb_service_goods.setRating(Integer.valueOf(entry
									.getData().getDSRATTITUDE()) / 2);
						}
						if (!TextUtils
								.isEmpty(entry.getData().getRATECONTENT())) {
							tv_evaluate.setVisibility(View.VISIBLE);
							tv_evaluate.setText(entry.getData()
									.getRATECONTENT());
							tv_evaluate.setTextSize(16);
							tv_evaluate.setTextColor(getResources().getColor(
									R.color.grayness));
						}
					} else {
						ll_state_time.setVisibility(View.GONE);
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
					}
				}
			} else if ("4".equals(ordertype)) {// 上门

				if (!TextUtils.isEmpty(entry.getData().getSTATE())) {
					ImageView iv_step_1 = (ImageView) findViewById(R.id.iv_step_1);
					ImageView iv_step_2 = (ImageView) findViewById(R.id.iv_step_2);
					ImageView iv_step_3 = (ImageView) findViewById(R.id.iv_step_3);
					ImageView iv_step_4 = (ImageView) findViewById(R.id.iv_step_4);
					ImageView iv_step_5 = (ImageView) findViewById(R.id.iv_step_5);
					ImageView line_step_1 = (ImageView) findViewById(R.id.line_step_1);
					ImageView line_step_2 = (ImageView) findViewById(R.id.line_step_2);
					ImageView line_step_3 = (ImageView) findViewById(R.id.line_step_3);
					ImageView line_step_4 = (ImageView) findViewById(R.id.line_step_4);

					Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
					Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
					LinearLayout ll_state_time = (LinearLayout) findViewById(R.id.ll_state_time);
					LinearLayout ll_evaluate_star = (LinearLayout) findViewById(R.id.ll_evaluate_star);
					TextView tv_evalute_title = (TextView) findViewById(R.id.tv_evalute_title);
					TextView tv_evaluate = (TextView) findViewById(R.id.tv_evaluate);
					ColoredRatingBar rtb_service_goods = (ColoredRatingBar) findViewById(R.id.rtb_service_goods);
					if ("1".equals(entry.getData().getSTATE())) {// 待处理
						ll_state_time.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.VISIBLE);
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
					} else if ("2".equals(entry.getData().getSTATE())) {// 等待用户支付
						ll_state_time.setVisibility(View.VISIBLE);
						ll_evaluate_star.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.GONE);
						tv_evaluate.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_waiting));
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
					} else if ("3".equals(entry.getData().getSTATE())) {// 3用户支付成功
																		// 请务必在服务后要求用户评价！
						ll_state_time.setVisibility(View.VISIBLE);
						btn_service_setting.setVisibility(View.GONE);
						ll_evaluate_star.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_success));
						tv_evaluate.setVisibility(View.VISIBLE);
						tv_evaluate
								.setText(getString(R.string.text_pay_success_detail));
						ll_order_price.setVisibility(View.VISIBLE);
						tv_price.setText("￥" + entry.getData().getGOODSAMOUNT());
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
						iv_step_3.setSelected(true);
						line_step_3.setSelected(true);
					} else if ("4".equals(entry.getData().getSTATE())) {// 服务结束
						ll_state_time.setVisibility(View.VISIBLE);
						btn_converstion
								.setText(getString(R.string.bt_consult_finished));
						btn_converstion.setClickable(false);
						btn_converstion
								.setBackgroundResource(R.drawable.btn_style_gray);
						btn_service_setting.setVisibility(View.GONE);
						ll_order_price.setVisibility(View.VISIBLE);
						tv_price.setText("￥" + entry.getData().getGOODSAMOUNT());
						iv_step_5.setSelected(true);
						line_step_4.setSelected(true);
						if (!TextUtils
								.isEmpty(entry.getData().getDSRATTITUDE())) {
							rtb_service_goods.setRating(Integer.valueOf(entry
									.getData().getDSRATTITUDE()) / 2);
						}
						if (!TextUtils
								.isEmpty(entry.getData().getRATECONTENT())) {
							tv_evaluate.setVisibility(View.VISIBLE);
							tv_evaluate.setText(entry.getData()
									.getRATECONTENT());
							tv_evaluate.setTextSize(16);
							tv_evaluate.setTextColor(getResources().getColor(
									R.color.grayness));
						}
					} else {
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						ll_state_time.setVisibility(View.GONE);
					}
				}
			} else if ("14".equals(ordertype)) {// 住院

				if (!TextUtils.isEmpty(entry.getData().getSTATE())) {
					ImageView iv_step_1 = (ImageView) findViewById(R.id.iv_step_1);
					ImageView iv_step_2 = (ImageView) findViewById(R.id.iv_step_2);
					ImageView iv_step_3 = (ImageView) findViewById(R.id.iv_step_3);
					ImageView iv_step_4 = (ImageView) findViewById(R.id.iv_step_4);
					ImageView iv_step_5 = (ImageView) findViewById(R.id.iv_step_5);
					ImageView line_step_1 = (ImageView) findViewById(R.id.line_step_1);
					ImageView line_step_2 = (ImageView) findViewById(R.id.line_step_2);
					ImageView line_step_3 = (ImageView) findViewById(R.id.line_step_3);
					ImageView line_step_4 = (ImageView) findViewById(R.id.line_step_4);

					Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
					Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
					LinearLayout ll_state_time = (LinearLayout) findViewById(R.id.ll_state_time);
					LinearLayout ll_evaluate_star = (LinearLayout) findViewById(R.id.ll_evaluate_star);
					TextView tv_evalute_title = (TextView) findViewById(R.id.tv_evalute_title);
					TextView tv_evaluate = (TextView) findViewById(R.id.tv_evaluate);
					ColoredRatingBar rtb_service_goods = (ColoredRatingBar) findViewById(R.id.rtb_service_goods);
					if ("1".equals(entry.getData().getSTATE())) {// 待处理
						ll_state_time.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.VISIBLE);
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
					} else if ("2".equals(entry.getData().getSTATE())) {// 等待用户支付
						ll_state_time.setVisibility(View.VISIBLE);
						ll_evaluate_star.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.GONE);
						tv_evaluate.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_waiting));
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
					} else if ("3".equals(entry.getData().getSTATE())) {// 3用户支付成功
																		// 请务必在服务后要求用户评价！
						ll_state_time.setVisibility(View.VISIBLE);
						btn_service_setting.setVisibility(View.GONE);
						ll_evaluate_star.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_success));
						tv_evaluate.setVisibility(View.VISIBLE);
						tv_evaluate
								.setText(getString(R.string.text_pay_success_detail));
						ll_order_price.setVisibility(View.VISIBLE);
						tv_price.setText("￥" + entry.getData().getGOODSAMOUNT());
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
						iv_step_3.setSelected(true);
						line_step_3.setSelected(true);
					} else if ("4".equals(entry.getData().getSTATE())) {// 服务结束
						ll_state_time.setVisibility(View.VISIBLE);
						btn_converstion
								.setText(getString(R.string.bt_consult_finished));
						btn_converstion.setClickable(false);
						btn_converstion
								.setBackgroundResource(R.drawable.btn_style_gray);
						btn_service_setting.setVisibility(View.GONE);
						ll_order_price.setVisibility(View.VISIBLE);
						tv_price.setText("￥" + entry.getData().getGOODSAMOUNT());
						iv_step_5.setSelected(true);
						line_step_4.setSelected(true);
						if (!TextUtils
								.isEmpty(entry.getData().getDSRATTITUDE())) {
							rtb_service_goods.setRating(Integer.valueOf(entry
									.getData().getDSRATTITUDE()) / 2);
						}
						if (!TextUtils
								.isEmpty(entry.getData().getRATECONTENT())) {
							tv_evaluate.setVisibility(View.VISIBLE);
							tv_evaluate.setText(entry.getData()
									.getRATECONTENT());
							tv_evaluate.setTextSize(16);
							tv_evaluate.setTextColor(getResources().getColor(
									R.color.grayness));
						}
					} else {
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						ll_state_time.setVisibility(View.GONE);
					}
				}
			} else if ("15".equals(ordertype)) {// 私人医生服务
				if (!TextUtils.isEmpty(entry.getData().getSTATE())) {
					LinearLayout ll_time_service = (LinearLayout) findViewById(R.id.ll_time_service);
					LinearLayout ll_service_call = (LinearLayout) findViewById(R.id.ll_service_call);

					TextView tv_service_time_title = (TextView) findViewById(R.id.tv_service_time_title);
					TextView tv_time_start = (TextView) findViewById(R.id.tv_time_start);
					TextView tv_time_end = (TextView) findViewById(R.id.tv_time_end);
					TextView tv_call_time = (TextView) findViewById(R.id.tv_call_time);
					Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
					if (TextUtils.isEmpty(entry.getData()
							.getSERVICE_START_TIME())
							|| TextUtils.isEmpty(entry.getData()
									.getSERVICE_END_TIME())) {
						ll_time_service.setVisibility(View.GONE);
						tv_service_time_title.setVisibility(View.GONE);
					} else {
						ll_time_service.setVisibility(View.VISIBLE);
						tv_service_time_title.setVisibility(View.VISIBLE);
						tv_time_start.setText(entry.getData()
								.getSERVICE_START_TIME());
						tv_time_end.setText(entry.getData()
								.getSERVICE_END_TIME());
					}
					Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
					if ("1".equals(entry.getData().getSTATE())) {// 服务期内
						btn_converstion.setText(getString(R.string.bt_consult));
						btn_converstion.setClickable(true);
						btn_converstion
								.setBackgroundResource(R.drawable.bt_login);

						try {
							long now_time = new Date().getTime();
							String TIME_STATUS = entry.getData()
									.getSERVICE_CALL_TIME_STATUS();
							int time_status = Integer.parseInt(TIME_STATUS);
							switch (time_status) {
							case 0:
								if (TextUtils.isEmpty(entry.getData()
										.getSERVICE_CALL_TIME())) {
									btn_service_setting
											.setVisibility(View.GONE);
									ll_service_call.setVisibility(View.GONE);
								} else if (now_time > Long.parseLong(entry
										.getData().getSERVICE_CALL_TIME()
										+ "000")) {
									btn_service_setting
											.setVisibility(View.GONE);
									ll_service_call.setVisibility(View.GONE);
								} else if (now_time <= Long.parseLong(entry
										.getData().getSERVICE_CALL_TIME()
										+ "000")) {
									btn_service_setting
											.setVisibility(View.GONE);
									ll_service_call.setVisibility(View.VISIBLE);
									String parseDateTime = TimeUtil
											.parseDateTime(Long.parseLong(entry
													.getData()
													.getSERVICE_CALL_TIME()),
													"yy/MM/dd(HH:mm)");
									tv_call_time
											.setText(Html
													.fromHtml("通话时间：<font color='red'>"
															+ parseDateTime
															+ "</font>"));
								}
								break;
							case 1:
								btn_service_setting.setVisibility(View.VISIBLE);
								ll_service_call.setVisibility(View.GONE);
								break;
							case 2:
								btn_service_setting.setVisibility(View.VISIBLE);
								ll_service_call.setVisibility(View.GONE);
								break;
							case -1:
								btn_service_setting.setVisibility(View.VISIBLE);
								ll_service_call.setVisibility(View.GONE);
								break;
							default:
								break;
							}
						} catch (Exception e) {
							btn_service_setting.setVisibility(View.VISIBLE);
							ll_service_call.setVisibility(View.GONE);
						}

					} else if ("2".equals(entry.getData().getSTATE())) {// 服务期结束
						btn_converstion
								.setText(getString(R.string.bt_consult_finished));
						btn_converstion.setClickable(false);
						btn_converstion
								.setBackgroundResource(R.drawable.btn_style_gray);
						btn_service_setting.setVisibility(View.GONE);
						ll_service_call.setVisibility(View.GONE);
					}
				}
			} else {
				if (!TextUtils.isEmpty(entry.getData().getSTATE())) {

					ImageView iv_step_1 = (ImageView) findViewById(R.id.iv_step_1);
					ImageView iv_step_2 = (ImageView) findViewById(R.id.iv_step_2);
					ImageView iv_step_3 = (ImageView) findViewById(R.id.iv_step_3);
					ImageView iv_step_4 = (ImageView) findViewById(R.id.iv_step_4);
					ImageView iv_step_5 = (ImageView) findViewById(R.id.iv_step_5);
					ImageView line_step_1 = (ImageView) findViewById(R.id.line_step_1);
					ImageView line_step_2 = (ImageView) findViewById(R.id.line_step_2);
					ImageView line_step_3 = (ImageView) findViewById(R.id.line_step_3);
					ImageView line_step_4 = (ImageView) findViewById(R.id.line_step_4);

					Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
					Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
					LinearLayout ll_state_time = (LinearLayout) findViewById(R.id.ll_state_time);
					LinearLayout ll_evaluate_star = (LinearLayout) findViewById(R.id.ll_evaluate_star);
					TextView tv_evalute_title = (TextView) findViewById(R.id.tv_evalute_title);
					TextView tv_evaluate = (TextView) findViewById(R.id.tv_evaluate);
					ColoredRatingBar rtb_service_goods = (ColoredRatingBar) findViewById(R.id.rtb_service_goods);
					if ("1".equals(entry.getData().getSTATE())) {// 待处理
						ll_state_time.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.VISIBLE);
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
					} else if ("2".equals(entry.getData().getSTATE())) {// 等待用户支付
						ll_state_time.setVisibility(View.VISIBLE);
						ll_evaluate_star.setVisibility(View.GONE);
						btn_service_setting.setVisibility(View.GONE);
						tv_evaluate.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_waiting));
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
					} else if ("3".equals(entry.getData().getSTATE())) {// 3用户支付成功
																		// 请务必在服务后要求用户评价！
						ll_state_time.setVisibility(View.VISIBLE);
						btn_service_setting.setVisibility(View.GONE);
						ll_evaluate_star.setVisibility(View.GONE);
						tv_evalute_title
								.setText(getString(R.string.text_pay_success));
						tv_evaluate.setVisibility(View.VISIBLE);
						tv_evaluate
								.setText(getString(R.string.text_pay_success_detail));
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						iv_step_2.setSelected(true);
						line_step_2.setSelected(true);
						iv_step_3.setSelected(true);
						line_step_3.setSelected(true);
					} else if ("4".equals(entry.getData().getSTATE())) {// 服务结束
						ll_state_time.setVisibility(View.VISIBLE);
						btn_converstion
								.setText(getString(R.string.bt_consult_finished));
						btn_converstion.setClickable(false);
						btn_converstion
								.setBackgroundResource(R.drawable.btn_style_gray);
						btn_service_setting.setVisibility(View.GONE);
						iv_step_5.setSelected(true);
						line_step_4.setSelected(true);

						if (!TextUtils
								.isEmpty(entry.getData().getDSRATTITUDE())) {
							rtb_service_goods.setRating(Integer.valueOf(entry
									.getData().getDSRATTITUDE()) / 2);
						}
						if (!TextUtils
								.isEmpty(entry.getData().getRATECONTENT())) {
							tv_evaluate.setVisibility(View.VISIBLE);
							tv_evaluate.setText(entry.getData()
									.getRATECONTENT());
							tv_evaluate.setTextSize(16);
							tv_evaluate.setTextColor(getResources().getColor(
									R.color.grayness));
						}
					} else {
						iv_step_1.setSelected(true);
						line_step_1.setSelected(true);
						ll_state_time.setVisibility(View.GONE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateCallServiceView(OrderDetailInfo entry) {
		Button btn_converstion = (Button) findViewById(R.id.btn_converstion);
		Button btn_service_setting = (Button) findViewById(R.id.btn_service_setting);
		TextView tv_time_jiahao = (TextView) findViewById(R.id.tv_time_jiahao);

		ImageView iv_step_1 = (ImageView) findViewById(R.id.iv_step_1);
		ImageView iv_step_2 = (ImageView) findViewById(R.id.iv_step_2);
		ImageView iv_step_3 = (ImageView) findViewById(R.id.iv_step_3);
		ImageView iv_step_4 = (ImageView) findViewById(R.id.iv_step_4);
		ImageView line_step_1 = (ImageView) findViewById(R.id.line_step_1);
		ImageView line_step_2 = (ImageView) findViewById(R.id.line_step_2);
		ImageView line_step_3 = (ImageView) findViewById(R.id.line_step_3);

		if (!TextUtils.isEmpty(entry.getData().getSTATE())) {
			LinearLayout ll_state_time = (LinearLayout) findViewById(R.id.ll_state_time);
			if ("1".equals(entry.getData().getSTATE())) {// 待处理
				// 获取通话时间
				String service_CALL_TIME = entry.getData()
						.getSERVICE_CALL_TIME();
				String service_CALL_TIME_STATUS = entry.getData()
						.getSERVICE_CALL_TIME_STATUS();
				if (TextUtils.isEmpty(service_CALL_TIME)) {
					UICore.eventTask(OrderDetailActivity.this,
							OrderDetailActivity.this, IResult.GET_CALL_TIME,
							null, null);
					ll_state_time.setVisibility(View.GONE);
					btn_service_setting.setVisibility(View.VISIBLE);
					iv_step_1.setSelected(true);
					line_step_1.setSelected(true);
				} else {
					ll_state_time.setVisibility(View.VISIBLE);
					tv_time_jiahao.setText(TimeUtil.parseDateTime(Long
							.valueOf(entry.getData().getSERVICE_CALL_TIME()),
							"yyyy-MM-dd HH:mm"));
					btn_service_setting.setVisibility(View.GONE);
					iv_step_1.setSelected(true);
					line_step_1.setSelected(true);
					iv_step_2.setSelected(true);
					line_step_2.setSelected(true);
				}

			} else if ("2".equals(entry.getData().getSTATE())) {// 预约成功，通话开始时间为：YY-MM-DD
																// h:m
				ll_state_time.setVisibility(View.VISIBLE);
				tv_time_jiahao.setText(TimeUtil.parseDateTime(
						Long.valueOf(entry.getData().getSERVICE_CALL_TIME()),
						"yyyy-MM-dd HH:mm"));
				btn_service_setting.setVisibility(View.GONE);
				iv_step_1.setSelected(true);
				line_step_1.setSelected(true);
				iv_step_2.setSelected(true);
				line_step_2.setSelected(true);
			} else if ("3".equals(entry.getData().getSTATE())) {// 3服务结束
				ll_state_time.setVisibility(View.GONE);
				btn_converstion
						.setText(getString(R.string.bt_consult_finished));
				btn_converstion.setClickable(false);
				btn_converstion
						.setBackgroundResource(R.drawable.btn_style_gray);
				btn_service_setting.setVisibility(View.GONE);

				iv_step_4.setSelected(true);
				line_step_3.setSelected(true);
			} else {
				iv_step_1.setSelected(true);
				line_step_1.setSelected(true);
			}
		}
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			return2ContactInfoUI();
		} else if (view.getId() == R.id.btn_converstion) {
			// ContactInfoActivity activity = ContactInfoActivity.getActivity();
			if (entry == null || orderEntry == null) {
				Toast.makeText(getApplicationContext(), "数据加载中请稍后", 0).show();
				return;
			}
			Conast.RETURN_TO_CONVERSION_TAB = true;
			return2ContactInfoUI();
		} else if (view.getId() == R.id.btn_service_setting) {
			if ("2".equals(ordertype) || "15".equals(ordertype)) {
				setServiceTimeDialog();
			} else {
				setServicePriceDialog();
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent e) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return2ContactInfoUI();
		}
		return super.onKeyUp(keyCode, e);
	}

	public void return2ContactInfoUI() {
		try {
			if (hasSetOfflinePrice) {
				if (null != entry) {
					MessageBackupFieldEntry msgEntry = new MessageBackupFieldEntry();
					msgEntry.setCHANNEL("android");
					msgEntry.setISSYSTEMMSG("1");
					msgEntry.setORDERID(orderEntry.getORDERID());
					msgEntry.setSERVICEID(orderEntry.getSERVICEID());
					msgEntry.setUSERID(orderEntry.getUSERID());
					msgEntry.setDOCTORID(SharePrefUtil
							.getString(Conast.Doctor_ID));
					msgEntry.setORDERTYPE(ordertype);
					msgEntry.setDoctor_avatar(SharePrefUtil
							.getString(Conast.AVATAR));
					if (null != entry && null != entry.getData()) {
						msgEntry.setUser_avatar(entry.getData().getAVATAR());
					}
					msgEntry.setUser_name(user_name);
					msgEntry.setDoctor_name(SharePrefUtil
							.getString(Conast.Doctor_Name));

					MessageSystemEntry systemEntry = new MessageSystemEntry();
					systemEntry.setUser_msg(getString(
							R.string.text_sys_offline_set_price_to_user)
							.replace("{service}",
									PublicUtil.getServiceNameByid(ordertype))
							.replace("{price}", offline_price));
					systemEntry.setDoctor_msg(getString(
							R.string.text_sys_offline_set_price_to_doctor)
							.replace("{service}",
									PublicUtil.getServiceNameByid(ordertype))
							.replace("{price}", offline_price));
					MessageSystemEvent event = new MessageSystemEvent(
							ServiceType.SERVICE_JIAHAO, msgEntry, systemEntry);
					// Intent intent = new Intent(this,
					// ContactInfoActivity.class);
					// intent.putExtra("event", event);
					// intent.putExtra("tochat_userId",
					// entry.getData().getUSERID());
					// intent.putExtra("title",
					// entry.getData().getANY_USER_NAME());
					// intent.putExtra("user_avatar",
					// entry.getData().getAVATAR());
					// startActivity(intent);
					finish();
				}
			} else if (hasSetCallTime) {
				if (null != entry) {
					MessageBackupFieldEntry msgEntry = new MessageBackupFieldEntry();
					msgEntry.setCHANNEL("android");
					msgEntry.setISSYSTEMMSG("1");
					msgEntry.setORDERID(orderEntry.getORDERID());
					msgEntry.setSERVICEID(orderEntry.getSERVICEID());
					msgEntry.setUSERID(orderEntry.getUSERID());
					msgEntry.setDOCTORID(SharePrefUtil
							.getString(Conast.Doctor_ID));
					msgEntry.setORDERTYPE("2");
					msgEntry.setDoctor_avatar(SharePrefUtil
							.getString(Conast.AVATAR));
					if (null != entry && null != entry.getData()) {
						msgEntry.setUser_avatar(entry.getData().getAVATAR());
					}
					msgEntry.setUser_name(user_name);
					msgEntry.setDoctor_name(SharePrefUtil
							.getString(Conast.Doctor_Name));

					MessageSystemEntry systemEntry = new MessageSystemEntry();
					systemEntry
							.setDoctor_msg((getString(R.string.text_sys_phone_set_time_to_doctor))
									.replace("{date}", TimeUtil.parseDateTime(
											selectedTime, "yyyy-MM-dd HH:mm")));
					systemEntry
							.setUser_msg((getString(R.string.text_sys_phone_set_time_to_user))
									.replace("{date}", TimeUtil.parseDateTime(
											selectedTime, "yyyy-MM-dd HH:mm")));
					MessageSystemEvent event = new MessageSystemEvent(
							ServiceType.SERVICE_PHONE, msgEntry, systemEntry);
					// Intent intent = new Intent(this,
					// ContactInfoActivity.class);
					// intent.putExtra("event", event);
					// intent.putExtra("tochat_userId",
					// entry.getData().getUSERID());
					// intent.putExtra("title",
					// entry.getData().getANY_USER_NAME());
					// intent.putExtra("user_avatar",
					// entry.getData().getAVATAR());
					// startActivity(intent);
					finish();
				}
			} else {
				if (null != entry) {
					// Intent intent = new Intent(this,
					// ContactInfoActivity.class);
					// intent.putExtra("tochat_userId",
					// entry.getData().getUSERID());
					// intent.putExtra("user_avatar",
					// entry.getData().getAVATAR());
					// intent.putExtra("title",
					// entry.getData().getANY_USER_NAME());
					// startActivity(intent);
					finish();
				} else {
					finish();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_order_info);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_oto_service);
		// EventBus.getDefault().unregister(MessageSystemEvent.class);
	}

	private void getUserOrderInfo(String orderid) {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("orderid", orderid);

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_order_info, OrderDetailInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.RESULT;
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

	private void setO2OPrice(String orderid, String price) {
		if (NetWorkUtils.detect(this)) {

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("doctorid", SharePrefUtil
					.getString(Conast.Doctor_ID)));
			params.add(new BasicNameValuePair("channel", "android"));
			params.add(new BasicNameValuePair("token", SharePrefUtil
					.getString(Conast.ACCESS_TOKEN)));
			params.add(new BasicNameValuePair("orderid", orderid));
			params.add(new BasicNameValuePair("price", price));

			String content;
			try {
				content = HttpUtil.getString(HttpUtil.URI
						+ HttpUtil.set_oto_service, params, HttpUtil.POST);
				content = TextUtil.substring(content, "{");
				Gson gson = new Gson();
				ResultInfo reason = gson.fromJson(content, ResultInfo.class);
				Message msg = Message.obtain();
				msg.what = IResult.SET_SERVICE_PRICE;
				msg.obj = reason;
				handler.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
				Message message = new Message();
				message.obj = e.getMessage();
				message.what = IResult.DATA_ERROR;
				handler.sendMessage(message);
			}

		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	private void set_call_time(String orderid, String call_time) {
		if (NetWorkUtils.detect(this)) {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("doctorid", SharePrefUtil
					.getString(Conast.Doctor_ID)));
			params.add(new BasicNameValuePair("channel", "android"));
			params.add(new BasicNameValuePair("token", SharePrefUtil
					.getString(Conast.ACCESS_TOKEN)));
			params.add(new BasicNameValuePair("orderid", orderid));
			params.add(new BasicNameValuePair("call_time", call_time));

			System.out.println(HttpUtil.URI+HttpUtil.set_call_time+"/n"+params);
			
			String content;
			try {
				content = HttpUtil.getString(HttpUtil.URI
						+ HttpUtil.set_call_time, params, HttpUtil.POST);
				content = TextUtil.substring(content, "{");
				Gson gson = new Gson();
				ResultInfo reason = gson.fromJson(content, ResultInfo.class);
				Message msg = Message.obtain();
				msg.what = IResult.SET_CALL_TIME;
				msg.obj = reason;
				handler.sendMessage(msg);
				
				
				System.out.println("设置通话时间："+content);
				
			} catch (IOException e) {
				e.printStackTrace();
				Message message = new Message();
				message.obj = e.getMessage();
				message.what = IResult.DATA_ERROR;
				handler.sendMessage(message);
			}
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	private void get_call_time_list() {
		if (NetWorkUtils.detect(this)) {
			String content;
			try {
				ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("Channel", "android"));// 新增
				params.add(new BasicNameValuePair("token", SharePrefUtil
						.getString(Conast.ACCESS_TOKEN)));// 新增
				params.add(new BasicNameValuePair("doctorid", SharePrefUtil
						.getString(Conast.Doctor_ID)));// 新增
				content = HttpUtil.getString(HttpUtil.URI
						+ HttpUtil.get_call_time_list, params, HttpUtil.POST);
				content = TextUtil.substring(content, "{");
				if (content != null) {
					JSONObject obj = new JSONObject(content);
					JSONArray timeArray = obj.getJSONArray("data");
					time_array = new long[timeArray.length()];
					for (int i = 0; i < timeArray.length(); i++) {
						Logger.dout("keyValue:" + timeArray.getString(i));
						time_array[i] = Long.valueOf(timeArray.getString(i));
					}
					Message message = new Message();
					message.what = IResult.SHOW_TIME_DIALOG;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			// handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	String offline_price = null;

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		if (mes == IResult.SET_SERVICE_PRICE) {
			offline_price = (String) obj;
			setO2OPrice(orderEntry.getORDERID(), offline_price);
		} else if (mes == IResult.GET_CALL_TIME) {
			get_call_time_list();
		} else if (mes == IResult.SET_CALL_TIME) {
			set_call_time(orderEntry.getORDERID(), String.valueOf(selectedTime));
		}
	}

	private void setServicePriceDialog() {
		final EditText editText = new EditText(this);
		editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_CLASS_NUMBER);
		editText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable edt) {
				String temp = edt.toString();// 为了保留两位小数
				int posDot = temp.indexOf(".");
				if (temp.contains(".")
						&& (posDot == 0 || posDot == temp.length() - 1)) {
					return;
				}
				if (temp.contains(".")) {
					if (temp.length() - posDot - 1 > 2) {
						edt.delete(posDot + 3, posDot + 4);
					}
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});
		MenuDialog.Builder alert = new MenuDialog.Builder(this);
		MenuDialog dialog = alert
				.setTitle(getString(R.string.text_set_price_dialog_title))
				.setContentView(editText)
				.setPositiveButton(
						getString(R.string.text_set_price_dialog_submit),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								price = editText.getText().toString();
								if (!TextUtils.isEmpty(price)) {
									UICore.eventTask(OrderDetailActivity.this,
											OrderDetailActivity.this,
											IResult.SET_SERVICE_PRICE,
											"设置价格中..", price);
								}
							}
						})
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (KeyEvent.KEYCODE_BACK == keyCode) {
					return true;
				}
				return false;
			}
		});
		dialog.show();
	}

	private MenuDialog dialog = null;
	long selectedTime = 0L;
	private OrderDetailInfo entry;

	/***
	 * 每天的8：30到晚上10：30是服务时间 后台按照下单时间返回 每隔15分钟（or30分钟？？）一个时间点
	 */
	private void setServiceTimeDialog() {
		try {
			if (time_array == null || time_array.length == 0) {
				UICore.eventTask(OrderDetailActivity.this,
						OrderDetailActivity.this, IResult.GET_CALL_TIME,
						"获取时间中..", null);
			} else {

				if (dialog == null || !dialog.isShowing()) {
					if (!hasSetCallTime) {
						View time_content = LayoutInflater.from(this).inflate(
								R.layout.dialog_time_set, null);
						InScrollGridView gv_time_1 = (InScrollGridView) time_content
								.findViewById(R.id.gv_time);
						TextView tv_date_today = (TextView) time_content
								.findViewById(R.id.tv_date_today);
						InScrollGridView gv_time_2 = (InScrollGridView) time_content
								.findViewById(R.id.gv_time_2);
						TextView tv_date_today_2 = (TextView) time_content
								.findViewById(R.id.tv_date_today_2);

						ArrayList<String> timelist_origin = new ArrayList<String>();
						for (int i = 0; i < time_array.length; i++) {
							timelist_origin.add(TimeUtil
									.parseTime(time_array[i]));
						}
						if (timelist_origin.contains("08:30")
								&& !"08:30".equals(timelist_origin.get(0))) {// 按照08：30
																				// 分离两个list
							final ArrayList<String> timelist1 = new ArrayList<String>();// 当天的时间列表
							ArrayList<String> timelist2 = new ArrayList<String>();// 第二天的时间列表
							int seperateIndex = timelist_origin
									.indexOf("08:30");
							for (int i = 0; i < seperateIndex; i++) {
								timelist1.add(timelist_origin.get(i));
							}
							for (int i = seperateIndex; i < timelist_origin
									.size(); i++) {
								timelist2.add(timelist_origin.get(i));
							}
							gv_time_2.setVisibility(View.VISIBLE);
							tv_date_today_2.setVisibility(View.VISIBLE);
							tv_date_today.setText(TimeUtil.parseDateTime(
									time_array[0], "yyyy-MM-dd"));
							tv_date_today_2.setText(TimeUtil.parseDateTime(
									time_array[seperateIndex], "yyyy-MM-dd"));
							final GridTimeAdapter adapter1 = new GridTimeAdapter(
									timelist1);
							gv_time_1.setAdapter(adapter1);
							final GridTimeAdapter adapter2 = new GridTimeAdapter(
									timelist2);
							gv_time_2.setAdapter(adapter2);
							gv_time_1
									.setOnItemClickListener(new AdapterView.OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long arg3) {
											selectedTime = time_array[position];
											adapter1.setSeclection(position);
											adapter1.notifyDataSetChanged();
											adapter2.setSeclection(-1);
											adapter2.notifyDataSetChanged();
										}
									});
							gv_time_2
									.setOnItemClickListener(new AdapterView.OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long arg3) {
											selectedTime = time_array[position
													+ timelist1.size()];
											adapter2.setSeclection(position);
											adapter2.notifyDataSetChanged();
											adapter1.setSeclection(-1);
											adapter1.notifyDataSetChanged();
										}
									});

						} else {// 不需要分离时间，只有一个list
							tv_date_today.setText(TimeUtil.parseDateTime(
									time_array[0], "yyyy-MM-dd"));
							final GridTimeAdapter adapter = new GridTimeAdapter(
									timelist_origin);
							gv_time_1.setAdapter(adapter);
							gv_time_1
									.setOnItemClickListener(new AdapterView.OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long arg3) {
											selectedTime = time_array[position];
											adapter.setSeclection(position);
											adapter.notifyDataSetChanged();
										}
									});
						}

						MenuDialog.Builder alert = new MenuDialog.Builder(this);
						dialog = alert
								.setContentView(time_content)
								.setPositiveButton(
										getString(R.string.text_set_price_dialog_submit),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (selectedTime > 0) {
													if (orderEntry != null) {
														UICore.eventTask(
																OrderDetailActivity.this,
																OrderDetailActivity.this,
																IResult.SET_CALL_TIME,
																"设置时间中..", null);
													} else {
														Toast.makeText(
																getApplicationContext(),
																"数据加载中请稍候", 0)
																.show();
													}
												}
											}
										})
								.setNegativeButton(getString(R.string.cancel),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).create();
						dialog.setCanceledOnTouchOutside(true);
						dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

							@Override
							public boolean onKey(DialogInterface dialog,
									int keyCode, KeyEvent event) {
								if (KeyEvent.KEYCODE_BACK == keyCode) {
									return true;
								}
								return false;
							}
						});
						dialog.show();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class GridTimeAdapter extends BaseAdapter {

		private ArrayList<String> titles;

		public GridTimeAdapter(ArrayList<String> titles) {
			super();
			this.titles = titles;
		}

		@Override
		public int getCount() {
			return titles == null ? 0 : titles.size();
		}

		@Override
		public Object getItem(int position) {
			return titles == null ? null : titles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				holder = new ViewHolder();
				view = LayoutInflater.from(getApplicationContext()).inflate(
						R.layout.u_dialog_bt_time_item, null);
				holder.service_time = (Button) view
						.findViewById(R.id.bt_service_time);
				view.setTag(holder);
			}
			holder.service_time.setText(titles.get(position));
			// 点击改变选中listItem的背景色
			if (clickTemp == position) {
				holder.service_time.setTextColor(OrderDetailActivity.this
						.getResources().getColor(R.color.white));
				holder.service_time
						.setBackgroundResource(R.drawable.ic_login_pressed);
			} else {
				holder.service_time.setTextColor(OrderDetailActivity.this
						.getResources().getColor(R.color.black));
				holder.service_time
						.setBackgroundResource(R.drawable.button_white_bg);
			}
			return view;
		}

		private int clickTemp = -1;

		// 标识选择的Item
		public void setSeclection(int position) {
			clickTemp = position;
		}

		public void change(ArrayList<String> titles) {
			this.titles = titles;
			notifyDataSetChanged();
		}

		public class ViewHolder {
			Button service_time;
		}
	}

	public void sendEMMessage(String textMSG, OrderListEntry orderEntry) {
		
		broadCastIntent.putExtra("textMSG", textMSG);
		broadCastIntent.putExtra("data", orderEntry);
		sendBroadcast(broadCastIntent);
		
//		System.out.println("系统消息发送");
//		String ext = generatBackupMessage(orderEntry);
//		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
//		TextMessageBody txtBody = new TextMessageBody(textMSG);
//		// 设置消息body
//		message.addBody(txtBody);
//		if (!TextUtils.isEmpty(ext))
//			message.setAttribute("ext", ext);
//
//		// 设置要发给谁,用户username或者群聊groupid
//		message.setReceipt(orderEntry.getUSERID());
//		// 把messgage加到conversation中
//		EMConversation conversation = EMChatManager.getInstance()
//				.getConversation(orderEntry.getUSERID());
//		conversation.addMessage(message); //
//		messageInterface.notifyEMMessageSetChanged(message);
//		
//		
//		
//		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
//
//			@Override
//			public void onSuccess() {
//				System.out.println("系统发送成功");
//			}
//
//			@Override
//			public void onError(int code, String error) {
//				System.out.println("系统发送失败");
//			}
//
//			@Override
//			public void onProgress(int progress, String status) {
//			}
//
//		});

	}

	/***
	 * 附加到聊天消息中的备用字段
	 * 
	 * @return
	 */
	public String generatBackupMessage(OrderListEntry orderEntry) {
		String extStr = null;
		MessageBackupFieldEntry msgEntry = new MessageBackupFieldEntry();
		msgEntry.setUSERID( orderEntry.getUSERID());
		msgEntry.setORDERTYPE(orderEntry.getORDERTYPE());
		msgEntry.setCHANNEL("android");
		msgEntry.setISSYSTEMMSG("1");
		msgEntry.setDOCTORID(SharePrefUtil.getString(Conast.Doctor_ID));
		msgEntry.setDoctor_avatar(SharePrefUtil.getString(Conast.AVATAR));
		msgEntry.setUser_avatar(user_avatar);
		msgEntry.setUser_name(user_name);
		msgEntry.setDoctor_name(SharePrefUtil.getString(Conast.Doctor_Name));
		msgEntry.setORDERID(orderEntry.getORDERID());
		Gson gson = new Gson();
		extStr = gson.toJson(msgEntry);
		return extStr;
	}
	private String toStringJSON(String doctor,String user){
		
		JsonObject obj = new JsonObject();
		obj.addProperty("doctor_msg", doctor);
		obj.addProperty("user_msg", user);
		System.out.println("obj.toString() = "+obj.toString());
		return obj.toString();
		
	}
}
