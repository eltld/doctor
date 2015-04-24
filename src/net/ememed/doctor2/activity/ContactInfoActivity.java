package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.RemarkInfoBean;
import net.ememed.doctor2.fragment.DoctorChatFragment;
import net.ememed.doctor2.fragment.DoctorInfoFragment;
import net.ememed.doctor2.fragment.DoctorOrderFragment;
import net.ememed.doctor2.message_center.MessageCenterActivity;
import net.ememed.doctor2.message_center.MessageSettingActivity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CustomViewPager;
import com.viewpagerindicator.TabPageIndicator;

import de.greenrobot.event.MessageSystemEvent;

/**
 * 聊天 - 订单 - 详情 页
 * 
 * 注意事项
 * 
 * 1.onNewIntent 配合 manifest的singleTask属性 做到当前Anctivity唯一性 2.在该界面发送系统消息
 * 
 * @author chen
 */
public class ContactInfoActivity extends BasicActivity implements OnPageChangeListener {

	private DoctorInfoFragment fragment;
	private PagerAdapter mPagerAdapter = null;
	private CustomViewPager mViewPager = null;
	public TabPageIndicator mIndicator = null;
	private TextView tv_title = null;
	private static ContactInfoActivity activity;
	// private DoctorChatFragment mChatFragment;
	private ImageView iv_right_fun;
	// private DoctorChatFragment mChatFragment;
	private DoctorOrderFragment mOrderFragment;
	private Button btn;
	TextView txt_is_star;

	public String title;
	public String tochat_userId;
	public String user_avatar;
	public String note_name; // 备注名
	public String description; // 备注描述
	private PopupWindow popup;
	
	private TextView iv_red_dot;

	// private NewMessageBroadcastReceiver receiver;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.doctor_contact_detail);
		connectionIM();
		activity = this;
		title = getIntent().getStringExtra("title");
		user_avatar = getIntent().getStringExtra("user_avatar");
		tochat_userId = getIntent().getStringExtra("tochat_userId");
		note_name = getIntent().getStringExtra("note_name");
		description = getIntent().getStringExtra("description");
		sendSystemMsgOperation();
	}

	public static ContactInfoActivity getActivity() {
		return activity;
	}

	@Override
	protected void setupView() {
		super.setupView();

		btn = (Button) findViewById(R.id.btn_addhealth);
		btn.setVisibility(View.VISIBLE);
		iv_red_dot = (TextView) findViewById(R.id.iv_red_dot);
		setRedShow();
		btn.setText("免费微聊");
		btn.setTextSize(16);
		btn.setBackgroundResource(R.color.transparent);
		tv_title = (TextView) findViewById(R.id.top_title);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);

		Bundle bundle_order = new Bundle();
		bundle_order.putString("fragmentKey", getString(R.string.frag_title_order));
		bundle_order.putString("tochat_userId", tochat_userId);
		bundle_order.putString("user_avatar", user_avatar);
		bundle_order.putString("title", title);
		mPagerAdapter.addFragment(getString(R.string.frag_title_order), DoctorOrderFragment.class,
				bundle_order);

		Bundle bundle_info = new Bundle();
		bundle_info.putString("fragmentKey", getString(R.string.frag_title_info));
		mPagerAdapter.addFragment(getString(R.string.frag_title_info), DoctorInfoFragment.class,
				bundle_order);

		mViewPager = (CustomViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(3);// 缓存多少个页面
		mViewPager.setAdapter(mPagerAdapter);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);

		registerEMReceiver();

		if (getIntent() != null) {
			int index = getIntent().getIntExtra("index", 0);
			mViewPager.setCurrentItem(index == 0 ? 0 : 1);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		String username = intent.getStringExtra("tochat_userId");
		// System.out.println("onNewIntent username : "+username);
		if (tochat_userId.equals(username)) {

			super.onNewIntent(intent);
			setIntent(intent);
			sendSystemMsgOperation();
		} else {
			finish();
			startActivity(intent);
		}
	}
	private void sendSystemMsgOperation() {
		final MessageSystemEvent event = (MessageSystemEvent) getIntent().getSerializableExtra(
				"event");
		if (null != event) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (null != mViewPager) {
						mViewPager.setCurrentItem(0);
					}
					if (null != mOrderFragment) {
						if (event.getType() != null) {
							mOrderFragment.refresh();
						}
					}
				}
			}, 500);
		}
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.btn_addhealth) {
			// XXX 免费微聊入口
			Intent intent = new Intent(ContactInfoActivity.this, MicroChatActivity.class);
			intent.putExtra("tochat_userId", tochat_userId);
			intent.putExtra("title", title);
			intent.putExtra("user_avatar", user_avatar);
			startActivity(intent);
		} else if (view.getId() == R.id.iv_right_fun) {
			if (popup != null) {
				popup.showAsDropDown(iv_right_fun, -155, 13);
			}

		}
	}

	protected static final String TAG = ContactInfoActivity.class.getSimpleName();

	class PagerAdapter extends FragmentPagerAdapter {

		private Context mContext;
		private final ArrayList<FragmentInfo> fragments = new ArrayList<FragmentInfo>();
		private FragmentManager fm;
		private ArrayList<Fragment> fra_list;

		protected final class FragmentInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			protected FragmentInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		public PagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.fm = fm;
			this.mContext = context;
			fra_list = new ArrayList<Fragment>();
		}

		public void clearFragment() {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : fra_list) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();// 立刻执行以上命令（commit）
		}

		public void addFragment(String tag, Class<?> clss, Bundle args) {
			FragmentInfo fragmentInfo = new FragmentInfo(tag, clss, args);
			fragments.add(fragmentInfo);
		}

		@Override
		public Fragment getItem(int arg0) {
			FragmentInfo fragmentInfo = fragments.get(arg0);
			Fragment fra = Fragment.instantiate(mContext, fragmentInfo.clss.getName(),
					fragmentInfo.args);
			if (!fra_list.contains(fra))
				fra_list.add(fra);
			if (fragmentInfo.clss.getName().equals(DoctorChatFragment.class.getName())) {
				// mChatFragment = (DoctorChatFragment) fra;
			} else if (fragmentInfo.clss.getName().equals(DoctorOrderFragment.class.getName())) {
				mOrderFragment = (DoctorOrderFragment) fra;
			}
			return fra;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragments.get(position).tag;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	/** 信息提示 */
	public void showMessage(String msg) {
		Builder builder = new Builder(this);
		Dialog dialog = builder.setTitle(getString(R.string.system_info)).setMessage(msg)
				.setPositiveButton(getString(R.string.add_health_record_know), null).create();
		dialog.setCancelable(false);
		if (!finish) {
			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activity = null;
		finish = true;
		if (null != EMChatManager.getInstance()) {
			try {
				unregisterReceiver(msgReceiver);
				msgReceiver = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean finish = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		if (Conast.RETURN_TO_CONVERSION_TAB) {
			Conast.RETURN_TO_CONVERSION_TAB = false;
			if (null != mViewPager && mViewPager.getCurrentItem() != 0) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (null != mViewPager) {
							mViewPager.setCurrentItem(0);
						}
					}
				}, 500);
			}
		}
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

	/**
	 * 新消息广播接收者
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			// 注销广播，否则在ChatActivity中会收到这个广播
			setRedShow();
			mOrderFragment.refreshListView();
			abortBroadcast();
		}
	}

	private NewMessageBroadcastReceiver msgReceiver;

	private void registerEMReceiver() {
		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance()
				.getNewMessageBroadcastAction());
		intentFilter.setPriority(4);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

//	public void setIsStar() {
//		if (NetWorkUtils.detect(this)) {
//			loading(null);
//			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
//			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
//			params.put("userid", tochat_userId);
//			params.put("is_star", is_star + "");
//			System.out.println("userid" + tochat_userId + "is_star" + is_star);
//			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_patient_star,
//					RemarkInfoBean.class, params, new Response.Listener() {
//						@Override
//						public void onResponse(Object response) {
//							activity.destroyDialog();
//							RemarkInfoBean rib = (RemarkInfoBean) response;
//							if (rib.getSuccess().equals("1")) {
//								if (is_star == 0) {
//									is_star = 1;
//									showToast(rib.getErrormsg());
//									txt_is_star.setText("标为星标");
//								} else {
//									is_star = 0;
//									showToast(rib.getErrormsg());
//									txt_is_star.setText("取消星标");
//								}
//							}
//						}
//					}, new Response.ErrorListener() {
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							activity.destroyDialog();
//							// Message message = new Message();
//							// message.obj = error.getMessage();
//							// message.what = IResult.ERROR_MARK_UNREAD_MSG;
//							// handler.sendMessage(message);
//						}
//					});
//		} else {
//			handler.sendEmptyMessage(IResult.NET_ERROR);
//		}
//	}
//
//	public void isStar() {
//		if (is_star == 1) {
//			is_star = 0;
//			txt_is_star.setText("取消星标");
//
//		} else {
//			is_star = 1;
//			txt_is_star.setText("标为星标");
//
//		}
//	}
//
//	private void initPupupWindow() {
//		LinearLayout ll_menu = (LinearLayout) getLayoutInflater().inflate(
//				R.layout.doctor_contact_detail_popup, null);
//		LinearLayout ll_remarks_set = (LinearLayout) ll_menu.findViewById(R.id.ll_remarks_set);
//		LinearLayout ll_star_set = (LinearLayout) ll_menu.findViewById(R.id.ll_star_set);
//		txt_is_star = (TextView) ll_menu.findViewById(R.id.txt_is_star);
//
//		ll_remarks_set.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (null != popup) {
//					popup.dismiss();
//					Intent intent = new Intent(ContactInfoActivity.this, RemarkInfoActivity.class);
//					intent.putExtra("tochat_userId", tochat_userId);
//					startActivity(intent);
//				}
//			}
//		});
//
//		ll_star_set.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (null != popup) {
//					popup.dismiss();
//					setIsStar();
//				}
//			}
//		});
//
//		popup = new PopupWindow(ll_menu, MyApplication.getInstance().canvasWidth * 7 / 20,
//				LayoutParams.WRAP_CONTENT);
//		popup.setOutsideTouchable(true);
//		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		popup.setFocusable(true);
//	}
	EMConversation conversation;
	public int getUnreadNumber() {
		int unCount = 0;
		List<EMMessage> emData = conversation.getAllMessages();
		for (int i = 0; i < emData.size(); i++) {
			EMMessage message = emData.get(i);
			if (message.isUnread()||message.isListened()) {
				try {
					String ext = message.getStringAttribute("ext");
					JSONObject jsonObject = new JSONObject(ext);
					String  id  = jsonObject.getString("ORDERID");
					if("-1".equals(id)){
						unCount++;
					}
				} catch (EaseMobException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return unCount;
	}
	
	public void setRedShow(){
		conversation = EMChatManager.getInstance().getConversation(
				tochat_userId);
		if (conversation.getUnreadMsgCount() > 0) {
//			 显示与此用户的消息未读数
			 int n = getUnreadNumber();
			if(n>0){
				iv_red_dot.setText(String.valueOf(n));
				iv_red_dot.setVisibility(View.VISIBLE);
			}else {
				iv_red_dot.setVisibility(View.INVISIBLE);
			}
		} else {
			iv_red_dot.setVisibility(View.INVISIBLE);
		}
	}

}
