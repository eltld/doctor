package net.ememed.doctor2.fragment;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BaiduMapActivity;
import net.ememed.doctor2.activity.GroupsChatActivity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.ImInfo;
import net.ememed.doctor2.entity.MemberOrderEntry;
import net.ememed.doctor2.entity.MessageBackupFieldEntry;
import net.ememed.doctor2.entity.SetProfile;
import net.ememed.doctor2.fragment.adapter.GroupsMessageAdapter;
import net.ememed.doctor2.interfac.LoadInterface;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.ImageUtils;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.MD5;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.MenuDialog;
import net.ememed.doctor2.widget.MyCloseQuestionDialog;
import net.ememed.doctor2.widget.PasteEditText;
import net.ememed.doctor2.widget.RefurbishListView;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.NetUtils;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.MessageSystemEvent;
import de.greenrobot.event.OrderLoadedEvent;
import de.greenrobot.event.util.TransformEMMessage;

/***
 * 聊天页
 * 
 * @author chen
 * 
 */
public class GroupChatFragment extends Fragment implements Handler.Callback,
		OnClickListener, OnRefreshListener, BasicUIEvent ,LoadInterface{

	private String TAG = DoctorChatFragment.class.getSimpleName();

	private static final int REQUEST_CODE_PICK_PICTURE = 1;
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_RESEND_NET_DISK = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;

	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;

	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;

	public static final String COPY_IMAGE = "EASEMOBIMG";
	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;
	public RefurbishListView listView;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	private LinearLayout expressionContainer;
	private LinearLayout btnContainer;
	private ImageView locationImgview;
	private View more;
	private int position;
	private ClipboardManager clipboard;
	private InputMethodManager manager;
	private Drawable[] micImages;
	private int chatType;
	private EMConversation conversation;
	// 给谁发送消息
	private String toChatUsername;
	private VoiceRecorder voiceRecorder;
	public GroupsMessageAdapter adapter;
	private File cameraFile;
	public static int resendPos;
	private RelativeLayout edittext_layout;
	private ProgressBar loadmorePB;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	
	
	private LinearLayout bottomLayout; //照相  相片  关闭问题
	private LinearLayout cameraLayout; //照相  
	private LinearLayout prislayout; //  相片  
	private LinearLayout problem_layout; // 关闭问题
	public LinearLayout rl_bottom; // 输入框布局


	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			micImage.setImageDrawable(micImages[msg.what]);
		}
	};
	private GroupsChatActivity activity = null;
	private Handler mHandler;
	private RelativeLayout ll_view_content;
	private Button bt_more;
	private ImageView btn_take_picture;
	private ImageView btn_picture;
	private Button btn_send;
	public TextView tv_error;
	private String user_avatar;
	private String orderid = "";
	private String tochat_userId;
//	private String questionid;
//	private String status;

	private int chatPage = 0;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (GroupsChatActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
		activity = (GroupsChatActivity) getActivity();
		user_avatar = getArguments().getString("user_avatar");
//		orderid = getArguments().getString("orderid");
		tochat_userId = getArguments().getString("tochat_userId");
//		questionid = getArguments().getString("questionid");
		
		
		
		
		// 判断单聊还是群聊
		chatType = activity.getIntent()
				.getIntExtra("chatType", CHATTYPE_SINGLE);

		if (chatType == CHATTYPE_SINGLE) { // 单聊
			toChatUsername = getArguments().getString("tochat_userId");
		} else {
			// 群聊
			toChatUsername = getArguments().getString("tochat_userId");
			// group = EMGroupManager.getInstance().getGroup(toChatUsername);
		}

	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initListener();

		View view = inflater.inflate(R.layout.root_layout, null);
		FrameLayout mContentView = (FrameLayout) view
				.findViewById(R.id.mainView);
		ll_view_content = (RelativeLayout) LayoutInflater.from(activity)
				.inflate(R.layout.activity_fragment_chat, null);
		initView(ll_view_content);
//		status = getArguments().getString("status");
//		System.out.println("status =  "+status);
//		if(status==null||status.equals("5")||status.equals("0")){
//			rl_bottom.setVisibility(View.GONE);
//		}else{
//			rl_bottom.setVisibility(View.VISIBLE);
//		}
		mContentView.addView(ll_view_content);
		setUpView();
		EventBus.getDefault().registerSticky(this, OrderLoadedEvent.class);
		getIMData("0");
		return view;
	}

	private void initListener() {
		if (EMChatManager.getInstance() == null
				|| !EMChatManager.getInstance().isConnected()) {

		}

	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void getIMData(String page) {
		
	

		if (NetWorkUtils.detect(activity)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("any_user_id", tochat_userId);
			params.put("page", page);
			params.put("order_id", orderid);
			System.out.println("聊天参数params: " + params);
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_im_list,
					ImInfo.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object arg0) {
							listView.onRefreshComplete();
							ImInfo imInfo = (ImInfo) arg0;
							TransformEMMessage.getInstance().setLoadInterface(GroupChatFragment.this);
							if(chatPage>0){
								List<EMMessage> imData = TransformEMMessage.getInstance().addEMMessageList(imInfo, SharePrefUtil.getString(Conast.Doctor_ID));
								if(adapter!=null){
									adapter.addEMMessagelist(imData);
								}else{
									adapter = new GroupsMessageAdapter(
											GroupChatFragment.this, activity,
											toChatUsername, chatType, user_avatar,
											orderid, imData);
									// 显示消息
									listView.setAdapter(adapter);
									if (listView.getCount() > 0) {
										listView.setSelection(listView.getCount() - 1);
									}
								}
							}else{
								//处理分页消息，
								List<EMMessage> imData = TransformEMMessage
										.getInstance()
										.transformEM(
												imInfo,
												SharePrefUtil
												.getString(Conast.Doctor_ID),
												toChatUsername, orderid);
								
								adapter = new GroupsMessageAdapter(
										GroupChatFragment.this, activity,
										toChatUsername, chatType, user_avatar,
										orderid, imData);
								// 显示消息
								listView.setAdapter(adapter);
								if (listView.getCount() > 0) {
									listView.setSelection(listView.getCount() - 1);
								}
							}
							

						}

					}

					, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							listView.onRefreshComplete();
						}
					});
		}

	}

	/**
	 * initView
	 */
	protected void initView(View view) {

		recordingContainer = view.findViewById(R.id.recording_container);
		bottomLayout = (LinearLayout) view.findViewById(R.id.bottomLayout);
		rl_bottom = (LinearLayout) view.findViewById(R.id.rl_bottom);
		cameraLayout = (LinearLayout) view.findViewById(R.id.cameraLayout);
		prislayout = (LinearLayout) view.findViewById(R.id.prislayout);
		problem_layout = (LinearLayout) view.findViewById(R.id.problem_layout);
		micImage = (ImageView) view.findViewById(R.id.mic_image);
		recordingHint = (TextView) view.findViewById(R.id.recording_hint);
		listView = (RefurbishListView) view.findViewById(R.id.list);
		mEditTextContent = (PasteEditText) view
				.findViewById(R.id.et_sendmessage);
		buttonSetModeKeyboard = view.findViewById(R.id.btn_set_mode_keyboard);
		edittext_layout = (RelativeLayout) view
				.findViewById(R.id.edittext_layout);
		buttonSetModeVoice = view.findViewById(R.id.btn_set_mode_voice);
		buttonSend = view.findViewById(R.id.btn_send);
		buttonPressToSpeak = view.findViewById(R.id.btn_press_to_speak);
		expressionContainer = (LinearLayout) view
				.findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) view.findViewById(R.id.ll_btn_container);
		locationImgview = (ImageView) view.findViewById(R.id.btn_location);
		loadmorePB = (ProgressBar) view.findViewById(R.id.pb_load_more);
		more = view.findViewById(R.id.more);
		btn_take_picture = (ImageView) view.findViewById(R.id.btn_take_picture);
		btn_picture = (ImageView) view.findViewById(R.id.btn_picture);
		bt_more = (Button) view.findViewById(R.id.bt_more);
		btn_send = (Button) view.findViewById(R.id.btn_send);
		tv_error = (TextView) view.findViewById(R.id.tv_error);
		mEditTextContent.setOnClickListener(this);
		bt_more.setOnClickListener(this);
		prislayout.setOnClickListener(this);
		problem_layout.setOnClickListener(this);
		cameraLayout.setOnClickListener(this);
		buttonSend.setOnClickListener(this);
		btn_take_picture.setOnClickListener(this);
		btn_picture.setOnClickListener(this);
		locationImgview.setOnClickListener(this);
		buttonSetModeVoice.setOnClickListener(this);
		buttonSetModeKeyboard.setOnClickListener(this);
		tv_error.setOnClickListener(this);
		btn_send.setOnClickListener(this);

		// 动画资源文件,用于录制语音时

		micImages = new Drawable[] {
				getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11) };

		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());

		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				activity.disTopContent();
				if (!TextUtils.isEmpty(s)) {
					buttonSetModeVoice.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {
						buttonSetModeVoice.setVisibility(View.VISIBLE);
						buttonSend.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		
	}

	private void setUpView() {
		try {
			clipboard = (ClipboardManager) activity
					.getSystemService(Context.CLIPBOARD_SERVICE);
			manager = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			conversation = EMChatManager.getInstance().getConversation(
					toChatUsername);
			// saveChatHistoryList();
			// 把此会话的未读数置为0
			conversation.resetUnsetMsgCount();
			
			
		/**listView.setOnScrollListener(new ListScrollListener()); 这个方法是加载DB内存里面的东西 */
			
			listView.setonRefreshListener(new net.ememed.doctor2.widget.RefurbishListView.OnRefreshListener() {
				
				@Override
				public void onRefresh() {
					chatPage++;
					getIMData(chatPage + "");
				}

			});


			int count = listView.getCount();
			if (count > 0) {
				listView.setSelection(count - 1);
			}

			listView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					activity.disTopContent();
					hideKeyboard();
					more.setVisibility(View.GONE);
					expressionContainer.setVisibility(View.GONE);
					btnContainer.setVisibility(View.GONE);
					return false;
				}
			});
			// if (null == EMChatManager.getInstance() ||
			// !EMChatManager.getInstance().isConnected()) {
			// loginIM();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/***
	 * 下完订单 发送系统消息
	 * 
	 * @param event
	 */
	public void receiveEventAndSendMsg(MessageSystemEvent event) {

		if (null == EMChatManager.getInstance()
				|| !EMChatManager.getInstance().isConnected()) {
			UICore.eventTask(this, activity, IResult.SEND_SYS_MSG, null, event);
		} else {
			sendSystemMessage(event);
		}
	}

	private List<MemberOrderEntry> orderLists;

	public void onEvent(OrderLoadedEvent event) {
		// orderLists = event.getOrderLists();
		// if (event.hasOpenOrder()) {
		// ll_not_allow_chat.setVisibility(View.GONE);
		// rl_bottom_send.setVisibility(View.VISIBLE);
		// } else {
		// ll_not_allow_chat.setVisibility(View.VISIBLE);
		// rl_bottom_send.setVisibility(View.GONE);
		// }

	}

	/** 发送系统消息 */
	public void sendSystemMessage(MessageSystemEvent event) {
		Gson gson = new Gson();
		String content = gson.toJson(event.getMsgSysEntry());
		// content = content.replaceAll("\"", "\\\\\"");// "冒号
		// 要转义。不然的话textbody会解析出错
		Logger.dout(TAG + " system content:" + content);
		String extStr = gson.toJson(event.getMsgEntry());
		sendText(content, extStr);
	}

	public void execute(int mes, Object obj) {
		if (mes == IResult.GET_IM_HISTORY) {
			// getImHistory();
		} else if (mes == IResult.SEND_SYS_MSG) {
			final MessageSystemEvent event = (MessageSystemEvent) obj;
			try {
				// EMChatManager.getInstance().logout();
				// 调用sdk登录方法登录聊天服务器 密码（id + 用户名 + ememedim）
				EMChatManager.getInstance().login(
						SharePrefUtil.getString(Conast.Doctor_ID),
						MD5.getMD5(SharePrefUtil.getString(Conast.Doctor_ID)
								+ SharePrefUtil.getString(Conast.MEMBER_NAME)
								+ "ememedim"), new EMCallBack() {

							@Override
							public void onSuccess() {
								Logger.dout(TAG + " EMChatManager onSuccess");
								Message msg = new Message();
								msg.what = IResult.SEND_SYS_MSG;
								msg.obj = event;
								mHandler.sendMessage(msg);
							}

							@Override
							public void onProgress(int progress, String status) {
								Logger.dout(TAG + " EMChatManager onProgress");
							}

							@Override
							public void onError(int code, final String message) {
								Logger.dout(TAG + " EMChatManager code:" + code
										+ "onError:" + message);
								Message msg = new Message();
								msg.what = IResult.RELOGIN;
								msg.obj = event;
								mHandler.sendMessage(msg);
							}
						});

				activity.registerIMRecevier();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 转发消息
	 * 
	 * @param forward_msg_id
	 */
	protected void forwardMessage(String forward_msg_id) {
		EMMessage forward_msg = EMChatManager.getInstance().getMessage(
				forward_msg_id);
		EMMessage.Type type = forward_msg.getType();
		switch (type) {
		case TXT:
			// 获取消息内容，发送消息
			String content = ((TextMessageBody) forward_msg.getBody())
					.getMessage();
			String extStr = generatBackupMessage();
			sendText(content, extStr);
			break;
		case IMAGE:
			// 发送图片
			String filePath = ((ImageMessageBody) forward_msg.getBody())
					.getLocalUrl();
			if (filePath != null) {
				File file = new File(filePath);
				if (!file.exists()) {
					// 不存在大图发送缩略图
					filePath = ImageUtils.getThumbnailImagePath(filePath);
				}
				String extStr1 = generatBackupMessage();
				sendPicture(filePath, extStr1);
			}
			break;
		default:
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			activity.setResult(activity.RESULT_OK);
			activity.finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data
						.getIntExtra("position", -1)));
				if (copyMsg.getType() == EMMessage.Type.IMAGE) {
					ImageMessageBody imageBody = (ImageMessageBody) copyMsg
							.getBody();
					// 加上一个特定前缀，粘贴时知道这是要粘贴一个图片
					clipboard.setText(COPY_IMAGE + imageBody.getLocalUrl());
				} else {
					// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
					// ((TextMessageBody) copyMsg.getBody()).getMessage()));
					clipboard.setText(((TextMessageBody) copyMsg.getBody())
							.getMessage());
				}
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data
						.getIntExtra("position", -1));
				conversation.removeMessage(deleteMsg.getMsgId());
				adapter.deleteEMMessage(data.getIntExtra("position", -1));
				adapter.refresh();
				listView.setSelection(data.getIntExtra("position",
						adapter.getCount()) - 1);
				break;

			case RESULT_CODE_FORWARD: // 转发消息
				// EMMessage forwardMsg = (EMMessage)
				// adapter.getItem(data.getIntExtra("position", 0));
				// Intent intent = new Intent(activity,
				// ForwardMessageActivity.class);
				// intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
				// startActivity(intent);
				break;
			default:
				break;
			}
		}
		if (resultCode == activity.RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
				adapter.refresh();
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists()) {
					String extStr = generatBackupMessage();
					sendPicture(cameraFile.getAbsolutePath(), extStr);
				}

			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null)
						sendPicByUri(selectedImage);
				}
			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				if (locationAddress != null && !locationAddress.equals("")) {
					more(more);
					sendLocationMsg(latitude, longitude, "", locationAddress);
				} else {
					Toast.makeText(activity, "无法获取到您的位置信息！", 0).show();
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_VOICE) {
				System.out.println("语音重发消息....");
				resendMessage();
			} else if (requestCode == REQUEST_CODE_PICTURE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_LOCATION) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						String extStr = generatBackupMessage();
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""), extStr);
					}
				}
			} else if (conversation.getMsgCount() > 0) {
				adapter.refresh();
				activity.setResult(activity.RESULT_OK);
			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
				EMChatManager.getInstance().getConversation(toChatUsername);
				adapter.refresh();
			}
		}
	}
	MyCloseQuestionDialog myCloseQuestionDialog;
	@Override
	public void onClick(View v) {
		activity.disTopContent();
		bottomLayout.setVisibility(View.GONE);
		int id = v.getId();
		if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
			String s = mEditTextContent.getText().toString().trim();
			if (TextUtils.isEmpty(s)) {
				activity.showToast(getString(R.string.input_content));
				return;
			}
			String extStr = generatBackupMessage();
			try {
				if (EMChatManager.getInstance() != null
						&& EMChatManager.getInstance().isConnected())
					if (!TextUtils.isEmpty(EMChatManager.getInstance()
							.getCurrentUser()))
						sendText(s, extStr);
					else {
					}
				else {
					if (NetUtils.hasNetwork(getActivity())) {
						activity.showToast("当前网络不可用，请检查网络设置");
					} else {
						activity.showToast("正在登录聊天服务器，请稍后重试");
					}
				}
			} catch (Exception e) {
				Toast.makeText(activity, "发送失败，请稍后重试", 0).show();
			}
		} else if (id == R.id.btn_location) {// 位置
			startActivityForResult(
					new Intent(activity, BaiduMapActivity.class),
					REQUEST_CODE_MAP);
		} else if (id == R.id.et_sendmessage) {
			
			editClick(v);
		} else if (id == R.id.bt_more) {
//			sendPictures();
			hideKeyboard();
			bottomLayout.setVisibility(View.VISIBLE);
		} else if (id == R.id.btn_set_mode_voice) {
			setModeVoice(v);
		} else if (id == R.id.btn_set_mode_keyboard) {
			setModeKeyboard(v);
		} else if (id == R.id.tv_error) {
		}else if (id == R.id.cameraLayout) {
			selectPicFromCamera();
		}else if (id == R.id.prislayout) {
			selectPicFromLocal();
		}else if (id == R.id.problem_layout) {//关闭问题
			myCloseQuestionDialog = new MyCloseQuestionDialog(getActivity(), R.style.mydialog);
			myCloseQuestionDialog.show();
			myCloseQuestionDialog.setButtonOnClickListener(this);
		}else if (id == R.id.ok_bnt) {//关闭问题 事件
			
			results = myCloseQuestionDialog.getText();
			if(TextUtils.isEmpty(results)){
				activity.showToast("请填写诊断结果");
			}else{
//				myCloseQuestionDialog.dismiss();
//				if (NetWorkUtils.detect(activity)){
//					closeQuestion();
//				}else{
//					activity.showToast("网络连接失败，请检查网络设置");
//				}
			}
		}
	}

	private String results;
	
	/***
	 * 组装消息的附加字段
	 * 
	 * @return
	 */
	public String generatBackupMessage() {
		String extStr = null;
		MessageBackupFieldEntry msgEntry = new MessageBackupFieldEntry();
		if (null != orderLists && orderLists.size() > 0) {
			msgEntry.setUSERID(orderLists.get(0).getUSERID());
			msgEntry.setORDERTYPE(orderLists.get(0).getORDERTYPE());
		}
		msgEntry.setCHANNEL("android");
		msgEntry.setISSYSTEMMSG("0");
		msgEntry.setDOCTORID(SharePrefUtil.getString(Conast.Doctor_ID));
		msgEntry.setDoctor_avatar(SharePrefUtil.getString(Conast.AVATAR));
		msgEntry.setUser_avatar(user_avatar);
		msgEntry.setUser_name(activity.title);
		msgEntry.setDoctor_name(SharePrefUtil.getString(Conast.Doctor_Name));
		msgEntry.setORDERID(orderid);
		Gson gson = new Gson();
		extStr = gson.toJson(msgEntry);
		return extStr;
	}

	@Override
	public boolean handleMessage(Message msg) {
		try {
			switch (msg.what) {

			case IResult.BASE_PROFILE:
				// mPullToRefreshLayout.setRefreshComplete();
				activity.destroyDialog();
				SetProfile sp = (SetProfile) msg.obj;
				if (null != sp) {
					if (sp.getSuccess() == 1) {
						activity.showToast("修改个人资料成功");
					} else {
						activity.showToast(sp.getErrormsg());
					}
				} else {
					activity.showToast(IMessage.DATA_ERROR);
				}
				break;
			case IResult.NET_ERROR:
				activity.destroyDialog();
				activity.showToast(IMessage.NET_ERROR);
				break;
			case IResult.DATA_ERROR:
				activity.destroyDialog();
				break;
			case IResult.LOGIN:
				activity.destroyDialog();
				tv_error.setVisibility(View.GONE);
				break;
			case IResult.SEND_SYS_MSG:
				activity.destroyDialog();
				tv_error.setVisibility(View.GONE);
				MessageSystemEvent event = (MessageSystemEvent) msg.obj;
				sendSystemMessage(event);
				break;
			case IResult.RELOGIN:
				activity.destroyDialog();
				MessageSystemEvent event2 = (MessageSystemEvent) msg.obj;
				UICore.eventTask(this, activity, IResult.SEND_SYS_MSG, null,
						event2);
				break;
			default:
				activity.destroyDialog();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onRefreshStarted(View view) {

	}

	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		cameraFile = new File(PathUtil.getInstance().getImagePath(),
				SharePrefUtil.getString(Conast.Doctor_ID)
						+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		startActivityForResult(
				new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
						MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param extStr
	 *            String 附加的字段 用于从顶部栏获取聊天相关信息
	 */
	private void sendText(String content, String extStr) {
		if (NetWorkUtils.detect(activity)) {
			if (content.length() > 0) {
				EMMessage message = EMMessage
						.createSendMessage(EMMessage.Type.TXT);
				// 如果是群聊，设置chattype,默认是单聊
				if (chatType == CHATTYPE_GROUP)
					message.setChatType(ChatType.GroupChat);
				TextMessageBody txtBody = new TextMessageBody(content);
				// 设置消息body
				message.addBody(txtBody);
				if (!TextUtils.isEmpty(extStr)) {
					message.setAttribute("ext", extStr);
				}
				// 设置要发给谁,用户username或者群聊groupid
				message.setReceipt(toChatUsername);
				message.setFrom(SharePrefUtil.getString(Conast.Doctor_ID));
				// 把messgage加到conversation中
				conversation.addMessage(message);
				// 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
				adapter.addEMMessage(message);
				adapter.refresh();
				listView.setSelection(listView.getCount() - 1);
				mEditTextContent.setText("");
				activity.setResult(activity.RESULT_OK);
			}
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length,
			boolean isResend, String extStr) {
		if (!(new File(filePath).exists())) {
			return;
		}
		try {
			final EMMessage message = EMMessage
					.createSendMessage(EMMessage.Type.VOICE);
			String to = toChatUsername;
			message.setReceipt(to);
			message.setFrom(SharePrefUtil.getString(Conast.Doctor_ID));
			if (!TextUtils.isEmpty(extStr)) {
				message.setAttribute("ext", extStr);
			}
			int len = Integer.parseInt(length);

			VoiceMessageBody body = new VoiceMessageBody(new File(filePath),
					len);
			message.addBody(body);
			conversation.addMessage(message);
			adapter.addEMMessage(message);
			adapter.refresh();
			listView.setSelection(listView.getCount() - 1);
			activity.setResult(activity.RESULT_OK);
			// send file
			// sendVoiceSub(filePath, fileName, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath, String extStr) {
		int rowId = 0;
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.IMAGE);
		message.setReceipt(to);
		message.setFrom(SharePrefUtil.getString(Conast.Doctor_ID));
		if (!TextUtils.isEmpty(extStr)) {
			message.setAttribute("ext", extStr);
		}
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		message.addBody(body);
		conversation.addMessage(message);
		adapter.addEMMessage(message);
		listView.setAdapter(adapter);
		adapter.refresh();
		listView.setSelection(listView.getCount() - 1);
		activity.setResult(activity.RESULT_OK);
		// more(more);
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {

		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.getContentResolver().query(selectedImage,
				null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(activity, "找不到图片",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			String extStr = generatBackupMessage();
			sendPicture(picturePath, extStr);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(activity, "找不到图片",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			String extStr = generatBackupMessage();
			sendPicture(file.getAbsolutePath(), extStr);
		}
	}

	/**
	 * 发送位置信息
	 * 
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude,
			String imagePath, String locationAddress) {
		EMMessage message = EMMessage
				.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);
		LocationMessageBody locBody = new LocationMessageBody(locationAddress,
				latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(toChatUsername);
		message.setFrom(SharePrefUtil.getString(Conast.Doctor_ID));
		conversation.addMessage(message);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setSelection(listView.getCount() - 1);
		activity.setResult(activity.RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
		if (NetWorkUtils.detect(activity)) {
			if (EMChatManager.getInstance() != null
					&& EMChatManager.getInstance().isConnected()) {
				if (!TextUtils.isEmpty(EMChatManager.getInstance()
						.getCurrentUser())) {
					EMMessage msg = null;
					msg = conversation.getMessage(resendPos);
					// msg.setBackSend(true);
					msg.status = EMMessage.Status.CREATE;

					adapter.refresh();
					listView.setSelection(resendPos);
				} else {
					activity.showToast("正在登录，请稍后重试");
				}
			} else {
				activity.showToast("正在连接，请稍后重试");
			}
		} else {
			activity.showToast("没有网络，请稍后重试");
		}
	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		edittext_layout.setVisibility(View.GONE);
		hideKeyboard();
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.GONE);
		// buttonSend.setVisibility(View.GONE);
		// buttonSetModeVoice.setVisibility(View.GONE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		expressionContainer.setVisibility(View.GONE);
	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		if (more.getVisibility() == View.GONE) {
			// System.out.println("more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			expressionContainer.setVisibility(View.GONE);
		} else {
			if (expressionContainer.getVisibility() == View.VISIBLE) {
				expressionContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		listView.setSelection(listView.getCount() - 1);
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
		}

	}

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!Util.isExitsSdcard()) {
					Toast.makeText(activity, "发送语音需要sdcard支持！",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername,
							activity.getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(activity, R.string.recoding_fail,
							Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint
							.setText(getString(R.string.release_to_cancel));
					recordingHint
							.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							String extStr = generatBackupMessage();
							sendVoice(voiceRecorder.getVoiceFilePath(),
									voiceRecorder
											.getVoiceFileName(toChatUsername),
									Integer.toString(length), false, extStr);
						}
					} catch (Exception e) {
						Toast.makeText(activity, "发送失败，请检测服务器是否连接",
								Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				try {
					v.setPressed(false);
					recordingContainer.setVisibility(View.INVISIBLE);
					voiceRecorder.discardRecording();
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				return false;
			}
		}
	}

	@Override
	public void onPause() {
		if (voiceRecorder != null) {
			recordingContainer.setVisibility(View.INVISIBLE);
			voiceRecorder.stopRecoding();
		}

		super.onPause();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().removeStickyEvent(OrderLoadedEvent.class);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != adapter) {
			adapter.refresh();
		}
	}

	private void sendPictures() {

		if (NetWorkUtils.detect(activity)) {
			final Context dialogContext = new ContextThemeWrapper(activity,
					android.R.style.Theme_Light);
			String[] choices = new String[2];
			choices[0] = getString(R.string.change_avatar_take_photo);
			choices[1] = getString(R.string.change_avatar_album);

			MenuDialog.Builder builder = new MenuDialog.Builder(dialogContext);
			MenuDialog dialog = builder.setTitle(R.string.picture_send)
					.setItems(choices, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							switch (which) {
							case 0:
								selectPicFromCamera();
								break;
							case 1:
								selectPicFromLocal(); // 点击图片图标
								break;
							}
						}
					}).create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (activity.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * listview滑动监听listener
	 * 
	 */
	private class ListScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (view.getFirstVisiblePosition() == 0 && !isloading
						&& haveMoreData) {
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(adapter
									.getItem(0).getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(
									adapter.getItem(0).getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						adapter.notifyDataSetChanged();
						listView.setSelection(messages.size() - 1);
						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;
				}
				break;
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
	}
	

	@Override
	public void loadSuccess() {
		// TODO Auto-generated method stub
		adapter.notifyDataSetChanged();
	}

}