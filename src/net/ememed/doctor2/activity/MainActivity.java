package net.ememed.doctor2.activity;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.db.InviteMessgeDao;
import net.ememed.doctor2.db.UserDao;
import net.ememed.doctor2.entity.ChatUser;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.InviteMessage;
import net.ememed.doctor2.entity.MobileParam;
import net.ememed.doctor2.entity.InviteMessage.InviteMesageStatus;
import net.ememed.doctor2.entity.VersionInfo;
import net.ememed.doctor2.fragment.CommunicationFragment;
import net.ememed.doctor2.fragment.ContactFragment;
import net.ememed.doctor2.fragment.DoctorFriendFragment;
import net.ememed.doctor2.fragment.HomePageFragment;
import net.ememed.doctor2.fragment.InfoFragment;
import net.ememed.doctor2.fragment.MineFragment;
import net.ememed.doctor2.fragment.ServiceFragment;
import net.ememed.doctor2.interfac.EMLoginCallBack;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.AppContext;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.MD5;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.MenuDialog;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.LogonSuccessEvent;
import de.greenrobot.event.util.IMManageTool;

/***
 * 主页
 * 
 * @author chen
 */
public class MainActivity extends BasicActivity implements EMLoginCallBack {
	protected static final String TAG = MainActivity.class.getSimpleName();

	protected static final int EXEU_GET_DATA = 0;

	private ImageButton tab_home;
	private ImageButton tab_people;
	private ImageButton tab_service;
	private ImageButton tab_me;
	private ImageButton tab_friend;
	private TextView tv_tab_contact;
	private TextView tv_tab_home;
	private TextView tv_tab_service;
	private TextView tv_tab_me;
	private TextView tv_tab_friend;

	private FrameLayout home_main_layout;
	private FragmentManager fm = getSupportFragmentManager();
	public int contentHeight;
	@Override
	protected void onBeforeCreate(Bundle savedInstanceState) {
		super.onBeforeCreate(savedInstanceState);
		getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		AppContext.setContext(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 弹出键盘不重新布局以避免底部上移
		EventBus.getDefault().registerSticky(this, LogonSuccessEvent.class);
		EMChatManager.getInstance().loadAllConversations();
		
	}

	@Override
	protected void onAfterCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onAfterCreate(savedInstanceState);
	}
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);
		updateVersion();
		checkAppVersion();
		registerEMReceiver();
		connectionIM();
	}
	
	/**
	 * update by umeng
	 */
	private void updateVersion() {
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
	}

	public void onEvent(LogonSuccessEvent logonEvent) {
		finish();
	}

	@Override
	protected void setupView() {
		super.setupView();

		View view_head_layout = LayoutInflater.from(this).inflate(
				R.layout.top_bar_doctor_green, null);

		ImageView btn_back = (ImageView) view_head_layout
				.findViewById(R.id.btn_back);
		home_tab_people_count_image = (ImageView) findViewById(R.id.home_tab_people_count_image);
		home_tab_server_count_image = (ImageView) findViewById(R.id.home_tab_server_count_image);
		btn_back.setVisibility(View.GONE);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		actionBar.setCustomView(view_head_layout, layoutParams);
		actionBar.hide();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		/*homeFragment = new InfoFragment();*/
		homeFragment = new HomePageFragment();
		ft.add(R.id.home_main_layout, homeFragment, "homefragment");
		ft.show(homeFragment);

		serviceFragment = new ServiceFragment();
		ft.add(R.id.home_main_layout, serviceFragment,
				ServiceFragment.class.getSimpleName());
		ft.hide(serviceFragment);

		ft.commit();
		lastFragment = homeFragment;

		home_main_layout = (FrameLayout) findViewById(R.id.home_main_layout);
		home_main_layout.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if (home_main_layout.getHeight() != 0) {
							home_main_layout.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
							contentHeight = home_main_layout.getHeight()
									- Util.dip2px(getApplicationContext(), 48);
						}
					}

				});
		/** 顶部 和 底部 的组件 */
		tab_home = (ImageButton) findViewById(R.id.home_tab_home);
		tab_people = (ImageButton) findViewById(R.id.home_tab_people);
		tab_service = (ImageButton) findViewById(R.id.home_tab_service);
		tab_me = (ImageButton) findViewById(R.id.home_tab_me);
		tab_friend = (ImageButton) findViewById(R.id.home_tab_friend);
		tab_home.setSelected(true);

		tv_tab_home = (TextView) findViewById(R.id.tv_tab_bottom_home);
		tv_tab_contact = (TextView) findViewById(R.id.tv_tab_bottom_contact);
		tv_tab_service = (TextView) findViewById(R.id.tv_tab_bottom_service);
		tv_tab_me = (TextView) findViewById(R.id.tv_tab_bottom_me);
		tv_tab_friend = (TextView) findViewById(R.id.tv_tab_bottom_friend);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		CountImage();
		Logger.dout("主页start");
	}

	public void setServerRed(boolean isflag) {
		if (isflag) {
			home_tab_server_count_image.setVisibility(View.VISIBLE);
		} else {
			home_tab_server_count_image.setVisibility(View.GONE);
		}
	}

	private void CountImage() {

		// String userName = EMChatManager.getInstance().getCurrentUser();
		// if(userName==null){
		// return;
		// }
		// if (EMChatManager.getInstance() != null
		// && !TextUtils.isEmpty(userName)) {
		int unreadMsgsCount = EMChatManager.getInstance().getUnreadMsgsCount();
		// upd by 04/17 , 去掉红点提示
//		if (unreadMsgsCount > 0) {// 有
//			home_tab_people_count_image.setVisibility(View.VISIBLE);
//		} else {// 没有
//			home_tab_people_count_image.setVisibility(View.GONE);
//		}
		// }
	}

	public void doClick(View view) {
		try {
			int id = view.getId();
			if (id == R.id.rl_tab_home) {
				changeTabAction(R.id.rl_tab_home);
			} else if (id == R.id.rl_tab_contact) {
				changeTabAction(R.id.rl_tab_contact);
			} else if (id == R.id.ll_tab_service) {
				changeTabAction(R.id.ll_tab_service);
			} else if (id == R.id.ll_tab_me) {
				changeTabAction(R.id.ll_tab_me);
			} else if (id == R.id.rl_tab_friend){
				changeTabAction(R.id.rl_tab_friend);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Fragment lastFragment = null;
	/*private InfoFragment homeFragment = null;*/
	private HomePageFragment homeFragment = null;
	private ContactFragment contactFragment = null;
	private DoctorFriendFragment doctorFriendFragment = null;
	private CommunicationFragment mCommunicationFragment;
	private ServiceFragment serviceFragment = null;
	private MineFragment mineFragment = null;
	private int currentTabIndex = 0;

	private void changeTabAction(int layoutId) {
		if (fm == null)
			fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (layoutId == R.id.rl_tab_home) {
			if (lastFragment != homeFragment) {
				ft.hide(lastFragment);
				if (homeFragment == null) {
					/*homeFragment = new InfoFragment();*/
					homeFragment = new HomePageFragment();
					ft.add(R.id.home_main_layout, homeFragment, "homefragment");
				}
				ft.show(homeFragment);
				ft.commit();
				lastFragment = homeFragment;

				tab_home.setSelected(true);
				tab_me.setSelected(false);
				tab_people.setSelected(false);
				tab_service.setSelected(false);
				tab_friend.setSelected(false);

				tv_tab_home.setTextColor(getResources().getColor(
						R.color.text_color_green));
				tv_tab_contact.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_service.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_me.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_friend.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				currentTabIndex = 0;
				if (null != contactFragment) {
					contactFragment.cancelLoadingBar();
				}
			}

		} else if (layoutId == R.id.rl_tab_contact) {
			if (lastFragment != contactFragment) {
				// getSupportActionBar().show();
				ft.hide(lastFragment);
				if (contactFragment == null) {
					contactFragment = new ContactFragment();
					ft.add(R.id.home_main_layout, contactFragment,
							ContactFragment.class.getSimpleName());
				}
				ft.show(contactFragment);
				ft.commit();
				lastFragment = contactFragment;

				tab_people.setSelected(true);
				tab_me.setSelected(false);
				tab_home.setSelected(false);
				tab_service.setSelected(false);
				tab_friend.setSelected(false);
				tv_tab_home.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_contact.setTextColor(getResources().getColor(
						R.color.text_color_green));
				tv_tab_service.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_me.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_friend.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				currentTabIndex = 1;
			}
		}else if (layoutId == R.id.rl_tab_friend) {
			//update by pch, 04/20. ****************************************************************
//			if (lastFragment != mCommunicationFragment) {
//				destroyDialog();
//				// 如何隐藏ActionBar的标题栏部分
//				ft.hide(lastFragment);
//				if (mCommunicationFragment == null) {
//					mCommunicationFragment = new CommunicationFragment();
//					ft.add(R.id.home_main_layout, mCommunicationFragment,
//							CommunicationFragment.class.getSimpleName());
//				}
//				ft.show(mCommunicationFragment);
//				ft.commit();
//				lastFragment = mCommunicationFragment;
//
//				tab_friend.setSelected(true);
//				tab_me.setSelected(false);
//				tab_service.setSelected(false);
//				tab_home.setSelected(false);
//				tab_people.setSelected(false);
//
//				tv_tab_home.setTextColor(getResources().getColor(
//						R.color.tab_light_gray));
//				tv_tab_contact.setTextColor(getResources().getColor(
//						R.color.tab_light_gray));
//				tv_tab_service.setTextColor(getResources().getColor(
//						R.color.tab_light_gray));
//				tv_tab_me.setTextColor(getResources().getColor(
//						R.color.text_color_gray));
//				tv_tab_friend.setTextColor(getResources().getColor(
//						R.color.text_color_green));
//				currentTabIndex = 2;
//				if (null != contactFragment) {
//					contactFragment.cancelLoadingBar();
//				}
//			}
			// upd end ****************************************************************************
			if (lastFragment != doctorFriendFragment) {
				destroyDialog();
				// 如何隐藏ActionBar的标题栏部分
				ft.hide(lastFragment);
				if (doctorFriendFragment == null) {
					doctorFriendFragment = new DoctorFriendFragment();
					ft.add(R.id.home_main_layout, doctorFriendFragment,
							DoctorFriendFragment.class.getSimpleName());
				}
				ft.show(doctorFriendFragment);
				ft.commit();
				lastFragment = doctorFriendFragment;

				tab_friend.setSelected(true);
				tab_me.setSelected(false);
				tab_service.setSelected(false);
				tab_home.setSelected(false);
				tab_people.setSelected(false);

				tv_tab_home.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_contact.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_service.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_me.setTextColor(getResources().getColor(
						R.color.text_color_gray));
				tv_tab_friend.setTextColor(getResources().getColor(
						R.color.text_color_green));
				currentTabIndex = 2;
				if (null != contactFragment) {
					contactFragment.cancelLoadingBar();
				}
			}
		} else if (layoutId == R.id.ll_tab_service) {
			if (lastFragment != serviceFragment) {
				ft.hide(lastFragment);
				if (serviceFragment == null) {
					serviceFragment = new ServiceFragment();
					ft.add(R.id.home_main_layout, serviceFragment,
							ServiceFragment.class.getSimpleName());
				}
				ft.show(serviceFragment);
				ft.commit();
				lastFragment = serviceFragment;

				tab_service.setSelected(true);
				tab_me.setSelected(false);
				tab_home.setSelected(false);
				tab_people.setSelected(false);
				tab_friend.setSelected(false);

				tv_tab_home.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_contact.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_service.setTextColor(getResources().getColor(
						R.color.text_color_green));
				tv_tab_me.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_friend.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				currentTabIndex = 3;
				if (null != contactFragment) {
					contactFragment.cancelLoadingBar();
				}
			}
		} else if (layoutId == R.id.ll_tab_me) {
			if (lastFragment != mineFragment) {
				destroyDialog();
				// 如何隐藏ActionBar的标题栏部分
				ft.hide(lastFragment);
				if (mineFragment == null) {
					mineFragment = new MineFragment();
					ft.add(R.id.home_main_layout, mineFragment,
							MineFragment.class.getSimpleName());
				}
				ft.show(mineFragment);
				ft.commit();
				lastFragment = mineFragment;

				tab_me.setSelected(true);
				tab_service.setSelected(false);
				tab_home.setSelected(false);
				tab_people.setSelected(false);
				tab_friend.setSelected(false);

				tv_tab_home.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_contact.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_service.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				tv_tab_me.setTextColor(getResources().getColor(
						R.color.text_color_green));
				tv_tab_friend.setTextColor(getResources().getColor(
						R.color.tab_light_gray));
				currentTabIndex = 4;
				if (null != contactFragment) {
					contactFragment.cancelLoadingBar();
				}
			}
		} 
	}

	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		if (msg.what == IResult.CHECK_VERSION) {
			VersionInfo versionInfo = (VersionInfo) msg.obj;
			if (versionInfo.getData().getAppconfig() != null) {
				SharePrefUtil.putInt(Conast.YOUJIANGYAOQING, versionInfo
						.getData().getAppconfig().getYoujiangyaoqing());
				// SharePrefUtil.putInt(Conast.YOUJIANGYAOQING,1);
				SharePrefUtil.commit();
			}
//			if (versionInfo.getSuccess() == 1) {
//				if(null != versionInfo.getData().getVERSIONCODE()){
//					int newVersionCode = Integer.valueOf(versionInfo.getData()
//							.getVERSIONCODE());
//					int oldVersionCode = PublicUtil
//							.getVersionCode(MainActivity.this);
//					if (newVersionCode > oldVersionCode) {
//						alertVersionUpdate(versionInfo);
//					}
//				}
//			}
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		// setServerRed();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		MyApplication.width = metric.widthPixels; // 屏幕宽度（像素）
		MyApplication.height = metric.heightPixels;
		MobclickAgent.onResume(this);
		Logger.dout("主页onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void checkAppVersion() {
		try {
			// if (TimeUtil.checkIsNeedUpdate()) {//该标记代表每天第一次启动
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("imei", PublicUtil.getDeviceUuid(MainActivity.this));
			params.put("appversion", PublicUtil.getVersionName(MainActivity.this));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("membertype", "doctor");
			params.put("appid", "5");// 
			params.put("channel", "android");
			params.put("mobileparam", MyApplication.getInstance().getMobileParam(this));
		
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_version_info, VersionInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.CHECK_VERSION;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							MyApplication.volleyHttpClient
									.cancelRequest(HttpUtil.get_version_info);
							// Message message = new Message();
							// message.obj = error.getMessage();
							// message.what = IResult.NET_ERROR;
							// handler.sendMessage(message);
						}
					});

			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private NewMessageBroadcastReceiver msgReceiver;

	private void registerEMReceiver() {
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager
				.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);
		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager
				.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.dout(TAG + " onDestroy() ");
//		SharedPreferencesUtil.getInstance(getApplicationContext()).saveBoolean(
//				true);
		// 注销广播接收者
		if (null != EMChatManager.getInstance()) {
			try {
				unregisterReceiver(msgReceiver);
				msgReceiver = null;
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
		EventBus.getDefault().removeStickyEvent(LogonSuccessEvent.class);
	}

	/**
	 * 新消息广播接收者
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);
			Logger.dout(TAG
					+ " EMChatManager NewMessageBroadcastReceiver  onReceive 已接收");
			//
			// 刷新bottom bar消息未读数
			// updateUnreadLabel();
			CountImage();
			if (currentTabIndex == 1) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (contactFragment != null) {
					contactFragment.refresh();
				}
			}
			// 注销广播，否则在ChatActivity中会收到这个广播
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
						// System.out.println("receive_custom_msg:"
						// + receive_custom_msg);
					}
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	private BroadcastReceiver contactInviteReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 请求理由
			final String reason = intent.getStringExtra("reason");
			final boolean isResponse = intent.getBooleanExtra("isResponse",
					false);
			// 消息发送方username
			final String from = intent.getStringExtra("username");
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(from)) {
					return;
				}
			}

			InviteMessage msg = new InviteMessage();
			msg.setFrom(from);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			// sdk暂时只提供同意好友请求方法，不同意选项可以参考微信增加一个忽略按钮。
			if (!isResponse) {
				// Log.d(TAG, from + "请求加你为好友,reason: " + reason);
				// 设成未验证
				msg.setStatus(InviteMesageStatus.NO_VALIDATION);
				msg.setInviteFromMe(false);
			} else {
				// Log.d(TAG, from + "同意了你的好友请求");
				// 对方已同意你的请求
				msg.setStatus(InviteMesageStatus.AGREED);
				msg.setInviteFromMe(true);
			}
			// 保存msg
			inviteMessgeDao.saveMessage(msg);
			// 未读数加1
			ChatUser user = MyApplication.getInstance().getContactList()
					.get(Conast.NEW_FRIENDS_USERNAME);
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
			// 提示有新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			abortBroadcast();
		}
	};

	private void alertVersionUpdate(final VersionInfo info) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.version_update_info, null);
		final TextView tvVersionName = (TextView) view
				.findViewById(R.id.tv_version_name);
		final TextView tvTime = (TextView) view
				.findViewById(R.id.tv_update_time);
		final TextView tv_upgrade_title = (TextView) view
				.findViewById(R.id.tv_upgrade_title);
		tv_upgrade_title.setVisibility(View.GONE);
		final TextView tvSize = (TextView) view.findViewById(R.id.tv_size);
		final TextView tv_new_feature = (TextView) view
				.findViewById(R.id.tv_new_feature);
		tvVersionName
				.setText("薏米网(Android) " + info.getData().getVERSIONNAME());
		tvTime.setText("更新时间:" + info.getData().getUPDATETIME());
		tvSize.setText("文件大小:" + info.getData().getAPPSIZE());
		if (TextUtils.isEmpty(info.getData().getCONTENT())) {
			tv_upgrade_title.setVisibility(View.GONE);
			tv_new_feature.setVisibility(View.GONE);
		} else {
			tv_upgrade_title.setVisibility(View.VISIBLE);
			tv_new_feature.setVisibility(View.VISIBLE);
			tv_new_feature.setText(info.getData().getCONTENT());
		}
		boolean not_force_update = "1".equals(info.getData().getUPGRADEMODE());// 是否强制升级

		MenuDialog.Builder alert = new MenuDialog.Builder(this);
		MenuDialog dialog = null;

		if (!not_force_update) {// 不强制升级
			dialog = alert
					.setTitle(getString(R.string.main_notice))
					.setContentView(view)
					.setPositiveButton(getString(R.string.main_shenji),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										Uri uri = Uri.parse(info.getData()
												.getAPPFILE());
										Intent intent = new Intent(
												Intent.ACTION_VIEW, uri);
										startActivity(intent);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							})
					.setNegativeButton(getString(R.string.main_next),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
		} else {// 强制升级
			dialog = alert
					.setTitle(getString(R.string.main_notice))
					.setContentView(view)
					.setPositiveButton(getString(R.string.main_shenji),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										Uri uri = Uri.parse(info.getData()
												.getAPPFILE());
										Intent intent = new Intent(
												Intent.ACTION_VIEW, uri);
										startActivity(intent);
										finish();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).create();
		}

		dialog.setCanceledOnTouchOutside(not_force_update);
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

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	private ImageView home_tab_people_count_image;
	private ImageView home_tab_server_count_image;

	/***
	 * 联系人变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Map<String, ChatUser> localUsers = MyApplication.getInstance()
					.getContactList();
			Map<String, ChatUser> toAddUsers = new HashMap<String, ChatUser>();
			for (String username : usernameList) {
				ChatUser user = new ChatUser();
				user.setUsername(username);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				if (username.equals(Conast.NEW_FRIENDS_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				// 暂时有个bug，添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			// 刷新ui
			// if (currentTabIndex == 1)
			// contactListFragment.refresh();
		}

		@Override
		public void onContactDeleted(List<String> usernameList) {
			// 删除联系人
			Map<String, ChatUser> localUsers = MyApplication.getInstance()
					.getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			// 刷新ui
			// if (currentTabIndex == 1)
			// contactListFragment.refresh();
			// updateUnreadLabel();
		}

		@Override
		public void onContactAgreed(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onContactInvited(String arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onContactRefused(String arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, ChatUser user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Conast.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	@Override
	public void onSuccess() {
		System.out.println("环信服务器登入成功.....");
	}

	@Override
	public void onProgress(int progress, String str) {

	}

	@Override
	public void onError(int age1, String age2) {
		System.out.println("环信服务器登入异常.....");
	}
	
	// 双击退出
	long[] mHits = new long[2];
	@Override
	public void onBackPressed() {
		System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
		mHits[mHits.length - 1] = SystemClock.uptimeMillis();
		if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {
			 super.onBackPressed();
			 finish();
		} else {
			Toast.makeText(getApplicationContext(), "再按一次退出程序", 0).show();
		}
	}

}
