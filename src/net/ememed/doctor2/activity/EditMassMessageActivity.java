package net.ememed.doctor2.activity;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.DeliverContactList;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.entity.MessageBackupFieldEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditMassMessageActivity extends BasicActivity {
	private List<ContactEntry> contacts = null;
	private TextView btn_send;

	Object object = new Object();

	private EditText et_message;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_edit_mass_message);
		contacts = (List<ContactEntry>) getIntent().getSerializableExtra(
				"contact_mass_message");
	}

	@Override
	protected void setupView() {
		super.setupView();

		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("发送消息");
		btn_send = (TextView) findViewById(R.id.tv_menu);
		et_message = (EditText) findViewById(R.id.et_message);
		btn_send.setVisibility(View.VISIBLE);
		btn_send.setText("发送");

		TextView tv_top = (TextView) findViewById(R.id.tv_top);
		tv_top.setText("消息将分别发送给" + contacts.size() + "位患者");

		TextView tv_names = (TextView) findViewById(R.id.tv_names);
		tv_names.setText(setPatientNames());
	}

	@Override
	protected void addListener() {
		super.addListener();

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String msg = et_message.getText().toString().trim();
				if (TextUtils.isEmpty(msg)) {
					showToast("说点什么吧！");
				} else {
					if (msg.length() < 10) {
						showToast("群发内容不可以少于10个字");
					} else {
						sendAllMSG(msg, contacts);
					}
				}
			}
		});
	}

	/**
	 * 显示需要群发消息的患者的名字，最多显示6个名字
	 */
	final int MAX_NUM_MANES = 6;

	private String setPatientNames() {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < contacts.size(); i++) {
			if (MAX_NUM_MANES == i) {
				str.append("等");
				break;
			} else {
				str.append(contacts.get(i).getREALNAME());
				if (i < contacts.size() - 1 && i < MAX_NUM_MANES - 1) {
					str.append("、");
				}
			}
		}
		return str.toString();
	}

	public void doClick(View view) {
		if (R.id.btn_back == view.getId()) {
			finish();
		}
	}

	public void sendAllMSG(String msg, List<ContactEntry> contacts) {
		for (int i = 0; i < contacts.size(); i++) {
			ContactEntry entry = contacts.get(i);
			String userid = entry.getMEMBERID();
			sendEMMessage(
					msg,
					userid,
					generateExtMessage(userid, entry.getMEMBERNAME(),
							entry.getAVATAR()));
		}

	}

	public void sendEMMessage(String msg, String toChatId, String ext) {
		// String ext = generateExtMessage(entry, isSystemMsg, orderType);
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
		TextMessageBody txtBody = new TextMessageBody(msg);
		// 设置消息body
		message.addBody(txtBody);
		if (!TextUtils.isEmpty(ext))
			message.setAttribute("ext", ext);
		// 设置要发给谁,用户username或者群聊groupid
		message.setReceipt(toChatId);
		// 把messgage加到conversation中
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(toChatId);
		conversation.addMessage(message);
		EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
			@Override
			public void onSuccess() {
				System.out.println("系统发送成功");
				isSendSuccess();
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

	public String generateExtMessage(String userid, String name, String avatar) {
		String extStr = null;
		MessageBackupFieldEntry msgEntry = new MessageBackupFieldEntry();
		msgEntry.setUSERID(userid);
		msgEntry.setCHANNEL("android");
		msgEntry.setISSYSTEMMSG("0");
		msgEntry.setDOCTORID(SharePrefUtil.getString(Conast.Doctor_ID));
		msgEntry.setDoctor_avatar(SharePrefUtil.getString(Conast.AVATAR));
		msgEntry.setUser_avatar(avatar);
		msgEntry.setUser_name(name);
		msgEntry.setDoctor_name(SharePrefUtil.getString(Conast.Doctor_Name));
		msgEntry.setORDERID("-1");// 待定
		msgEntry.setSERVICEID("");
		msgEntry.setORDERTYPE("");
		Gson gson = new Gson();
		extStr = gson.toJson(msgEntry);
		System.out.println("josn =  " + extStr);
		return extStr;
	}

	int count = 0;

	public void isSendSuccess() {
		synchronized (object) {
			count++;
			if (count == contacts.size()) {
				finish();
				Intent intent = new Intent(this,MainActivity.class);
				startActivity(intent);
			}else{
				showToast("发送失败,请重新发送");
			}
		}

	}

	public void add_im_mass_log() {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("content", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("type", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("receivers", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("channel", "android");
			params.put("appversion", PublicUtil.getVersionName(this));
			params.put("result", SharePrefUtil.getString(Conast.Doctor_ID));

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.add_im_mass_log, MemberInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
						}
					});
		} else {
		}
	}
}
