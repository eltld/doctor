package net.ememed.doctor2.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.OrderDetailActivity.GridTimeAdapter.ViewHolder;
import net.ememed.doctor2.activity.adapter.GridTimeAdapter;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MemberDetailEntry;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.entity.MessageBackupFieldEntry;
import net.ememed.doctor2.entity.OrderDetailInfo;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.fragment.DoctorChatFragment;
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
import net.ememed.doctor2.widget.InScrollGridView;
import net.ememed.doctor2.widget.MenuDialog;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nineoldandroids.view.ViewPropertyAnimator;

import de.greenrobot.event.MessageSystemEvent;

public class DoctorChatActivity extends BasicActivity {

	private DoctorChatFragment mChatFragment;
	private TextView tv_title = null;
	private NewMessageBroadcastReceiver receiver;

	public String tochat_userId;
	public String title;
	private String user_avatar;
	public String orderid;
	private String DESC_TEXT;
	private String ORDERTYPE;

	private LinearLayout content;
	private CheckBox checkBox;
	private CircleImageView iv_userhead;
	private TextView user_name;
	private TextView user_sex;
	private TextView user_age;
	private TextView remarks;
	private TextView introduce;
	private OrderListEntry olEntity;
	private Button set_call_time;
	private LinearLayout price_layout;

	private EditText price_edit;

	private String price;

	private String orderName;
	RefurbishBroadCast broadCast;

	private boolean isSetTime = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_doctorchat);
		mChatFragment = (DoctorChatFragment) Fragment.instantiate(this,
				DoctorChatFragment.class.getName());
		connectionIM();
		tochat_userId = getIntent().getExtras().getString("tochat_userId");
		title = getIntent().getExtras().getString("title");
		user_avatar = getIntent().getExtras().getString("user_avatar");
		orderid = getIntent().getExtras().getString("orderid");
		DESC_TEXT = getIntent().getExtras().getString("DESC_TEXT");
		ORDERTYPE = getIntent().getExtras().getString("ORDERTYPE");
		olEntity = (OrderListEntry) getIntent().getExtras().getSerializable(
				"order");
		isSetTime = false;
		Bundle bundle_chat = new Bundle();
		bundle_chat.putString("tochat_userId", tochat_userId);
		bundle_chat.putString("title", title);
		bundle_chat.putString("user_avatar", user_avatar);
		bundle_chat.putString("orderid", orderid);
		bundle_chat.putString("ORDERTYPE", olEntity.getORDERTYPE());
		bundle_chat.putString("STATE", olEntity.getSTATE());
		mChatFragment.setArguments(bundle_chat);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_layout, mChatFragment).commit();
		tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(title);
		registerIMRecevier();

		content = (LinearLayout) findViewById(R.id.top_content);
		checkBox = (CheckBox) findViewById(R.id.top_totle_ioc);
		checkBox.setChecked(true);

		iv_userhead = (CircleImageView) findViewById(R.id.iv_userhead);
		user_name = (TextView) findViewById(R.id.user_name);
		user_sex = (TextView) findViewById(R.id.user_sex);
		user_age = (TextView) findViewById(R.id.user_age);
		remarks = (TextView) findViewById(R.id.remarks);
		introduce = (TextView) findViewById(R.id.introduce);
		price_edit = (EditText) findViewById(R.id.price_edit);
		set_call_time = (Button) findViewById(R.id.set_call_time);
		price_layout = (LinearLayout) findViewById(R.id.price_layout);

		price_edit.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL
				| InputType.TYPE_CLASS_NUMBER);

		price_edit.addTextChangedListener(new TextWatcher() {

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

		broadCast = new RefurbishBroadCast();
		IntentFilter filter = new IntentFilter(
				RefurbishBroadCast.class.getName());
		registerReceiver(broadCast, filter);
		getUserInfo();
		orderType(ORDERTYPE);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (mChatFragment.adapter != null) {
			mChatFragment.adapter.refresh();
		}
	}

	public void doClick(View view) {

		switch (view.getId()) {

		case R.id.btn_back:
			finish();
			break;
		case R.id.price_bn:// 设置价格
			price = price_edit.getText().toString().trim();
			setServicePrice(price_edit.getText().toString());
			break;
		case R.id.set_call_time:
			if (isSetTime) {
				Intent intent = new Intent(DoctorChatActivity.this,
						OrderDetailActivity.class);
				intent.putExtra("user_name", title);
				intent.putExtra("user_avatar", user_avatar);
				intent.putExtra("order", olEntity);
				startActivity(intent);
				return;
			}
			if ("2".equals(olEntity.getSTATE())) {
				Intent intent = new Intent(DoctorChatActivity.this,
						OrderDetailActivity.class);
				intent.putExtra("user_name", title);
				intent.putExtra("user_avatar", user_avatar);
				intent.putExtra("order", olEntity);
				startActivity(intent);
			} else {
				if (ORDERTYPE.equals("2")) {
					UICore.eventTask(this, this, IResult.GET_CALL_TIME,
							"获取时间中..", null);
				} else {
					Intent intent = new Intent(DoctorChatActivity.this,
							OrderDetailActivity.class);
					intent.putExtra("user_name", title);
					intent.putExtra("user_avatar", user_avatar);
					intent.putExtra("order", olEntity);
					startActivity(intent);

				}
			}

			break;
		case R.id.toptitle_img_right:

			Intent intent = new Intent(DoctorChatActivity.this,
					OrderDetailActivity.class);
			intent.putExtra("user_name", title);
			intent.putExtra("user_avatar", user_avatar);
			intent.putExtra("order", olEntity);
			startActivity(intent);

		case R.id.top_layout:
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				ViewPropertyAnimator.animate(content)
						.translationY(-content.getHeight()).setDuration(500);
			} else {
				checkBox.setChecked(true);
				ViewPropertyAnimator.animate(content).translationY(0)
						.setDuration(500);
			}
			break;
		case R.id.doctor_info:

			Intent intent2 = new Intent(this, DoctorInfoActivity.class);
			intent2.putExtra("tochat_userId", tochat_userId);
			startActivity(intent2);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播
		try {
			unregisterReceiver(receiver);
			unregisterReceiver(broadCast);
			receiver = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			unregisterReceiver(ackMessageReceiver);
			ackMessageReceiver = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerIMRecevier() {

		// 注册接收消息广播
		receiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(5);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个消息送达的BroadcastReceiver
		IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(
				EMChatManager.getInstance()
						.getDeliveryAckMessageBroadcastAction());
		deliveryAckMessageIntentFilter.setPriority(5);
		registerReceiver(deliveryAckMessageReceiver,
				deliveryAckMessageIntentFilter);

		// 注册一个监听连接状态的listener
		// EMChatManager.getInstance().addConnectionListener(new
		// MyConnectionListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	/**
	 * 消息广播接收者
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String username = intent.getStringExtra("from");
			String msgid = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			EMMessage message = EMChatManager.getInstance().getMessage(msgid);

			if (null != message) {
				// 如果是群聊消息，获取到group id
				message.setAcked(true);
				message.setUnread(false);
				message.setListened(false);
				if (message.getChatType() == ChatType.GroupChat) {
					username = message.getTo();
				}
				if (null != mChatFragment && null != mChatFragment.adapter
						&& null != mChatFragment.listView) {
					mChatFragment.adapter.addEMMessage(message);
					mChatFragment.adapter.refresh();
					mChatFragment.listView.setSelection(mChatFragment.listView
							.getCount() - 1);
				}

			}
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					// 获取自定义的属性，第2个参数为返回的默认值
					String receive_custom_msg = msg.getStringAttribute(
							"attribute1", null);
					if (!TextUtils.isEmpty(receive_custom_msg)) {
					}
					msg.isAcked = true;
				}
			}
			abortBroadcast();
			if (null != mChatFragment && null != mChatFragment.adapter) {
				mChatFragment.adapter.notifyDataSetChanged();
			}

		}
	};

	private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			abortBroadcast();

			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isDelivered = true;
				}
			}
			mChatFragment.adapter.notifyDataSetChanged();
		}
	};

	private void sendSystemMsgOperation() {
		final MessageSystemEvent event = (MessageSystemEvent) getIntent()
				.getSerializableExtra("event");
		if (null != event) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (null != mChatFragment) {
						mChatFragment.receiveEventAndSendMsg(event);
					}
				}
			}, 500);
		}
	}

	private void getUserInfo() {// 获取医生信息
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("any_user_id", tochat_userId);

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_member_info, MemberInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.MEMBER_INFO;
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
		switch (msg.what) {
		case IResult.MEMBER_INFO:
			destroyDialog();
			MemberInfo info = (MemberInfo) msg.obj;
			if (info != null) {
				MemberDetailEntry entry = info.getData();
				user_name.setText(entry.getREALNAME());
				String sex = entry.getSEX();
				if (sex.equals("1")) {
					user_sex.setText("男");
				} else {
					user_sex.setText("女");
				}
				String BIRTHDAY = entry.getBIRTHDAY();
				if (BIRTHDAY == null || BIRTHDAY.equals("")) {
					user_age.setText("未填写生日");
				} else {
					SimpleDateFormat format = new SimpleDateFormat("yyyy");
					String time = format.format(new Date());
					int age = Integer.parseInt(time)
							- Integer.parseInt(BIRTHDAY.substring(0, 4));
					user_age.setText(age + "岁");
				}
				imageLoader.displayImage(entry.getAVATAR(), iv_userhead,
						Util.getOptions_pic());
				remarks.setText(DESC_TEXT);
			}
			break;
		case IResult.DATA_ERROR:
			destroyDialog();
			break;

		case IResult.SET_SERVICE_PRICE:
			ResultInfo entry_result = (ResultInfo) msg.obj;
			destroyDialog();
			if (null != entry_result) {
				if (entry_result.getSuccess() == 1) {
					// getUserOrderInfo(olEntity.getORDERID());
					showToast("设置价格成功");
					String doctor = "您已设置" + orderName + "服务的价格为：" + price
							+ "元，请在用户支付成功后安排具体服务";
					String user = title + "医生已设置" + orderName + "服务的价格为："
							+ price + "元，请前往订单页付款。";
					String systemMSG = toStringJSON(doctor, user);
					sendEMMessage(systemMSG, olEntity);
					disSetPriceView();
				} else {
					showToast(entry_result.getErrormsg());
				}
			}
			break;
		case IResult.SHOW_TIME_DIALOG:
			setCallTime();
			break;
		case IResult.SET_CALL_TIME:
			destroyDialog();
			ResultInfo entry_time = (ResultInfo) msg.obj;
			if (null != entry_time) {
				if (entry_time.getSuccess() == 1) {

					isSetTime = true;
					set_call_time.setText("通话时间为:"
							+ TimeUtil.parseDateTime(selectedTime,
									"yyyy-MM-dd HH:mm"));
					String doctor = "已设定通话时间为 "
							+ TimeUtil.parseDateTime(selectedTime,
									"yyyy-MM-dd HH:mm") + " ，届时请您保持电话通畅。";
					String user = "医生已设定通话时间为 "
							+ TimeUtil.parseDateTime(selectedTime,
									"yyyy-MM-dd HH:mm") + " ，届时请您保持电话通畅。";
					String systemMSG = toStringJSON(doctor, user);
					sendEMMessage(systemMSG, olEntity);
					showToast(entry_time.getErrormsg());
					getUserOrderInfo(olEntity.getORDERID());
				} else {
					showToast(entry_time.getErrormsg());
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return true;
	}

	public void orderType(String type) {
		// ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"名医加号",'4'=>"上门会诊",'14'=>"住院直通车",'15'=>"私人医生服务服务",'16'=>"其他服务"
		if (type.equals("1")) {// "图文咨询"
			// introduce.setText(R.string.chat_order_text_hint);
			introduce.setText(Html.fromHtml(PublicUtil
					.getServiceContentByid(type)));
			set_call_time.setText(PublicUtil.getServiceState(
					olEntity.getORDERTYPE(), olEntity.getSTATE()));
			if (olEntity.getSTATE().equals("2")) {
				set_call_time
						.setBackgroundResource(R.drawable.set_call_time_bg1);
			}
		} else if (type.equals("2")) {// "预约通话"
			// introduce.setText(R.string.chat_order_phone_hint);
			introduce.setText(Html.fromHtml(PublicUtil
					.getServiceContentByid(type)));
			if ("2".equals(olEntity.getSTATE())) {
				set_call_time.setText(PublicUtil.getServiceState(
						olEntity.getORDERTYPE(), olEntity.getSTATE())
						+ TimeUtil.parseDateTime(
								Long.valueOf(olEntity.getSERVICE_CALL_TIME()),
								"yyyy-MM-dd HH:mm"));
			} else {
				// set_call_time.setText(PublicUtil.getServiceState(
				// olEntity.getORDERTYPE(), olEntity.getSTATE()));
			}

		} else if (type.equals("3") || type.equals("4") || type.equals("14")
				|| type.equals("16")) {
			if ("1".equals(olEntity.getSTATE())) {
				// introduce.setText(R.string.chat_order_text_hint);// 名医加号 改为
				// 预约加号
				introduce.setText(Html.fromHtml(PublicUtil
						.getServiceContentByid(type)));
				showSetPriceView();
			} else {
				// introduce.setText(R.string.chat_order_text_hint);// 名医加号 改为
				// 预约加号
				introduce.setText(Html.fromHtml(PublicUtil
						.getServiceContentByid(type)));
				set_call_time.setText(PublicUtil.getServiceState(
						olEntity.getORDERTYPE(), olEntity.getSTATE()));
			}
			if (type.equals("3")) {
				orderName = "预约加号";
			} else if (type.equals("4")) {
				orderName = "上门会诊";
			} else if (type.equals("14")) {
				orderName = "预约住院";
			} else if (type.equals("16")) {
				orderName = "自定义";
			}
		} else if (type.equals("15")) {// 私人医生服务服务
			// introduce.setText(R.string.chat_order_private_hint);
			introduce.setText(Html.fromHtml(PublicUtil
					.getServiceContentByid(type)));
			set_call_time.setText(PublicUtil.getServiceState(
					olEntity.getORDERTYPE(), olEntity.getSTATE()));

		}

	}

	private void setServicePrice(String price) {
		UICore.eventTask(DoctorChatActivity.this, DoctorChatActivity.this,
				IResult.SET_SERVICE_PRICE, "设置价格中..", price);
	}

	String offline_price = null;

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		if (mes == IResult.SET_SERVICE_PRICE) {
			offline_price = (String) obj;
			setO2OPrice(olEntity.getORDERID(), offline_price);
		} else if (mes == IResult.GET_CALL_TIME) {
			get_call_time_list();
		} else if (mes == IResult.SET_CALL_TIME) {
			set_call_time(orderid, String.valueOf(selectedTime));
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

	private void getUserOrderInfo(String orderid) {// 这个要不要 无用,,,
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

	public void showSetPriceView() {
		price_layout.setVisibility(View.VISIBLE);
		set_call_time.setVisibility(View.GONE);
	}

	public void disSetPriceView() {
		price_layout.setVisibility(View.GONE);
		set_call_time.setVisibility(View.VISIBLE);
		set_call_time.setText("等待用户支付...");
	}

	public void disTopContent() {
		checkBox.setChecked(false);
		ViewPropertyAnimator.animate(content)
				.translationY(-content.getHeight()).setDuration(500);
	}

	public void sendEMMessage(String textMSG, OrderListEntry orderEntry) {
		System.out.println("系统消息发送");
		String ext = generatBackupMessage(orderEntry);
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.TXT);
		TextMessageBody txtBody = new TextMessageBody(textMSG);
		// 设置消息body
		message.addBody(txtBody);
		if (!TextUtils.isEmpty(ext))
			message.setAttribute("ext", ext);

		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(orderEntry.getUSERID());
		// 把messgage加到conversation中
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(orderEntry.getUSERID());
		conversation.addMessage(message);

		mChatFragment.adapter.addEMMessage(message);
		mChatFragment.adapter.refresh();
		mChatFragment.listView
				.setSelection(mChatFragment.listView.getCount() - 1);

		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

			@Override
			public void onSuccess() {
				System.out.println("系统发送成功");

			}

			@Override
			public void onError(int code, String error) {
				System.out.println("系统发送失败");
			}

			@Override
			public void onProgress(int progress, String status) {
			}
		});
	}

	/***
	 * 附加到聊天消息中的备用字段
	 * 
	 * @return
	 */
	public String generatBackupMessage(OrderListEntry orderEntry) {
		String extStr = null;
		MessageBackupFieldEntry msgEntry = new MessageBackupFieldEntry();
		msgEntry.setUSERID(orderEntry.getUSERID());
		msgEntry.setORDERTYPE(orderEntry.getORDERTYPE());
		msgEntry.setCHANNEL("android");
		msgEntry.setISSYSTEMMSG("1");
		msgEntry.setDOCTORID(SharePrefUtil.getString(Conast.Doctor_ID));
		msgEntry.setDoctor_avatar(SharePrefUtil.getString(Conast.AVATAR));
		msgEntry.setUser_avatar(user_avatar);
		msgEntry.setUser_name(title);
		msgEntry.setDoctor_name(SharePrefUtil.getString(Conast.Doctor_Name));
		msgEntry.setORDERID(orderEntry.getORDERID());
		Gson gson = new Gson();
		extStr = gson.toJson(msgEntry);

		System.out.println("josn =  " + extStr);

		return extStr;
	}

	private String toStringJSON(String doctor, String user) {

		JsonObject obj = new JsonObject();
		obj.addProperty("doctor_msg", doctor);
		obj.addProperty("user_msg", user);
		System.out.println("obj.toString() = " + obj.toString());
		return obj.toString();

	}

	private long[] time_array;
	long selectedTime = 0L;

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

	private MenuDialog dialog = null;

	private void setCallTime() {

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
			timelist_origin.add(TimeUtil.parseTime(time_array[i]));
		}
		if (timelist_origin.contains("08:30")
				&& !"08:30".equals(timelist_origin.get(0))) {// 按照08：30
																// 分离两个list
			final ArrayList<String> timelist1 = new ArrayList<String>();// 当天的时间列表
			ArrayList<String> timelist2 = new ArrayList<String>();// 第二天的时间列表
			
			int seperateIndex = timelist_origin.indexOf("08:30");
			for (int i = 0; i < seperateIndex; i++) {
				timelist1.add(timelist_origin.get(i));
			}
			for (int i = seperateIndex; i < timelist_origin.size(); i++) {
				timelist2.add(timelist_origin.get(i));
			}
			
			
			
			gv_time_2.setVisibility(View.VISIBLE);
			tv_date_today_2.setVisibility(View.VISIBLE);
			tv_date_today.setText(TimeUtil.parseDateTime(time_array[0],
					"yyyy-MM-dd"));
			tv_date_today_2.setText(TimeUtil.parseDateTime(
					time_array[seperateIndex], "yyyy-MM-dd"));
			final GridTimeAdapter adapter1 = new GridTimeAdapter(timelist1,
					this);
			gv_time_1.setAdapter(adapter1);
			final GridTimeAdapter adapter2 = new GridTimeAdapter(timelist2,
					this);
			gv_time_2.setAdapter(adapter2);
			gv_time_1
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
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
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							selectedTime = time_array[position
									+ timelist1.size()];
							adapter2.setSeclection(position);
							adapter2.notifyDataSetChanged();
							adapter1.setSeclection(-1);
							adapter1.notifyDataSetChanged();
						}
					});

		}else{
			tv_date_today.setText(TimeUtil.parseDateTime(
					time_array[0], "yyyy-MM-dd"));
			final GridTimeAdapter1 adapter = new GridTimeAdapter1(
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
							public void onClick(DialogInterface dialog,
									int which) {
								if (selectedTime > 0) {
									// if (orderEntry != null) {
									UICore.eventTask(DoctorChatActivity.this,
											DoctorChatActivity.this,
											IResult.SET_CALL_TIME, "设置时间中..",
											null);
									// } else {
									// Toast.makeText(
									// getApplicationContext(),
									// "数据加载中请稍候", 0)
									// .show();
									// }
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

	public class RefurbishBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String time = intent.getExtras().getString("time");
			set_call_time.setText("通话时间为:" + time);
			isSetTime = true;

			String textMSG = intent.getExtras().getString("textMSG");
			OrderListEntry orderEntry = (OrderListEntry) intent.getExtras()
					.getSerializable("data");

			System.out.println("系统消息发送");
			String ext = generatBackupMessage(orderEntry);
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
			TextMessageBody txtBody = new TextMessageBody(textMSG);
			// 设置消息body
			message.addBody(txtBody);
			if (!TextUtils.isEmpty(ext))
				message.setAttribute("ext", ext);

			// 设置要发给谁,用户username或者群聊groupid
			message.setReceipt(orderEntry.getUSERID());
			// 把messgage加到conversation中
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(orderEntry.getUSERID());
			conversation.addMessage(message); //

			mChatFragment.adapter.addEMMessage(message);
			mChatFragment.adapter.refresh();
			mChatFragment.listView.setSelection(mChatFragment.listView
					.getCount() - 1);

			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					System.out.println("系统发送成功");
				}

				@Override
				public void onError(int code, String error) {
					System.out.println("系统发送失败");
				}

				@Override
				public void onProgress(int progress, String status) {
				}

			});

		}

	}
	
	
	public class GridTimeAdapter1 extends BaseAdapter {

		private ArrayList<String> titles;

		public GridTimeAdapter1(ArrayList<String> titles) {
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
				holder.service_time.setTextColor(DoctorChatActivity.this
						.getResources().getColor(R.color.white));
				holder.service_time
						.setBackgroundResource(R.drawable.ic_login_pressed);
			} else {
				holder.service_time.setTextColor(DoctorChatActivity.this
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

}
