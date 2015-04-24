package net.ememed.doctor2.activity;

import java.io.Serializable;
import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.MyImageGridViewAdapter;
import net.ememed.doctor2.entity.MessageBackupFieldEntry;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.fragment.GroupChatFragment;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class GroupsChatActivity extends BasicActivity {
	private GroupChatFragment mChatFragment;
	private TextView tv_title = null;
	private NewMessageBroadcastReceiver receiver;

	public String tochat_userId;
	public String title;
	private String user_avatar;
	private String mGroupNum;
	private boolean mIsOwner;
	// public String orderid;
	// public String questionid;
	// public String status ="";

	private CheckBox checkBox;
	private LinearLayout content;
	private GridView gridView;

	private ImageView iv_topRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_preechat);
		mChatFragment = (GroupChatFragment) Fragment.instantiate(this,
				GroupChatFragment.class.getName());
		connectionIM();
		tochat_userId = getIntent().getExtras().getString("tochat_userId");
		title = getIntent().getExtras().getString("title");
		user_avatar = getIntent().getExtras().getString("user_avatar");
		mGroupNum = getIntent().getExtras().getString("group_num");
		mIsOwner = getIntent().getExtras().getBoolean("isOwner");
		// status = getIntent().getExtras().getString("status");
		// orderid = getIntent().getExtras().getString("orderid");
		// questionid = getIntent().getExtras().getString("questionid");
		Bundle bundle_chat = new Bundle();
		bundle_chat.putString("tochat_userId", tochat_userId);
		bundle_chat.putString("title", title);
		bundle_chat.putString("user_avatar", user_avatar);
		// bundle_chat.putString("orderid", orderid);
		// bundle_chat.putString("questionid", questionid);
		// bundle_chat.putString("status", status);
		mChatFragment.setArguments(bundle_chat);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_layout, mChatFragment, GroupChatFragment.class.getName())
				.commit();
		tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(title);
		registerIMRecevier();
		content = (LinearLayout) findViewById(R.id.top_content);
		checkBox = (CheckBox) findViewById(R.id.top_totle_ioc);
		checkBox.setVisibility(View.GONE);

		iv_topRight = (ImageView) findViewById(R.id.iv_top_right);
		iv_topRight.setVisibility(View.VISIBLE);
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
		case R.id.top_layout:
			if (checkBox.isChecked()) {
				checkBox.setChecked(false);
				ViewPropertyAnimator.animate(content).translationY(-content.getHeight())
						.setDuration(500);
			} else {
				checkBox.setChecked(true);
				ViewPropertyAnimator.animate(content).translationY(0).setDuration(500);
			}
			break;
		case R.id.iv_top_right:
			int type = mIsOwner ? GroupDetailActivity.TYPE_OWNER : GroupDetailActivity.TYPE_JOINER;
			GroupDetailActivity.startAction(this, mGroupNum, type);
			break;
		default:
			break;
		}
	}

	public void disTopContent() {
		checkBox.setChecked(true);
		ViewPropertyAnimator.animate(content).translationY(0).setDuration(500);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播
		try {
			unregisterReceiver(receiver);
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
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance()
				.getNewMessageBroadcastAction());
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		intentFilter.setPriority(5);
		registerReceiver(receiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(5);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
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
				if (message.getChatType() == ChatType.GroupChat) {
					username = message.getTo();
				}
				if (null != mChatFragment && null != mChatFragment.adapter
						&& null != mChatFragment.listView) {
					mChatFragment.adapter.addEMMessage(message);
					mChatFragment.adapter.refresh();
					mChatFragment.listView.setSelection(mChatFragment.listView.getCount() - 1);
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
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					// 获取自定义的属性，第2个参数为返回的默认值
					String receive_custom_msg = msg.getStringAttribute("attribute1", null);
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

	@Override
	protected void onResult(Message msg) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void sendEMMessage(String textMSG, OrderListEntry orderEntry) {
		System.out.println("系统消息发送");
		String ext = generatBackupMessage(orderEntry);
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		TextMessageBody txtBody = new TextMessageBody(textMSG);
		// 设置消息body
		message.addBody(txtBody);
		if (!TextUtils.isEmpty(ext))
			message.setAttribute("ext", ext);

		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(orderEntry.getUSERID());
		// 把messgage加到conversation中
		EMConversation conversation = EMChatManager.getInstance().getConversation(
				orderEntry.getUSERID());
		conversation.addMessage(message);

		mChatFragment.adapter.addEMMessage(message);
		mChatFragment.adapter.refresh();
		mChatFragment.listView.setSelection(mChatFragment.listView.getCount() - 1);

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
		return extStr;
	}

}
