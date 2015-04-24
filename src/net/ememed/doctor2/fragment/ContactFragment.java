package net.ememed.doctor2.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BusinessCardActivity;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.activity.NoticeListActivity;
import net.ememed.doctor2.activity.OrderClassifyActivity;
import net.ememed.doctor2.activity.PatientGroupActivity;
import net.ememed.doctor2.activity.MassMessageActivity;
import net.ememed.doctor2.activity.PatientTongxunluActivity;
import net.ememed.doctor2.activity.SearchPatientActivity;
import net.ememed.doctor2.activity.PersonInfoActivity;
import net.ememed.doctor2.activity.RegisterSuccessActivity;
import net.ememed.doctor2.activity.adapter.PatientGroupAllExListAdapter;
import net.ememed.doctor2.db.ContactTable;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.ContactInfo;
import net.ememed.doctor2.entity.DeliverContactList;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.OrderInformation;
import net.ememed.doctor2.entity.PatientGroupBean;
import net.ememed.doctor2.entity.PatientGroupDataBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

/**
 * 联系人tab
 * 
 * @author taro chyaohui@gmail.com
 */
public class ContactFragment extends Fragment implements Callback,
		BasicUIEvent, OnClickListener {
	private static final String TAG = ContactFragment.class.getSimpleName();
	public static final int EXEU_GET_DATA = 0;

	private Handler handler = null;
	private MainActivity activity = null;

	private EditText search_edt;
	private ImageView search;
	private AlterNoteNameReceiver receiver;;
	

	public ContactFragment() {
		this.activity = (MainActivity) getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		
		receiver = new AlterNoteNameReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionFilter.ALETER_NOTE_NAME);
		activity.registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		activity.unregisterReceiver(receiver);
	}

	int i = 0;
	private PullToRefreshLayout mPullToRefreshLayout;

	private ContactAdapter adapter;
	private RefreshListView listview;
	private LinearLayout ll_net_unavailable;
	private LinearLayout ll_empty;
	private LinearLayout edit_layout;
	private LinearLayout ll_patient_tongxunlu;
	private boolean refresh = true;
	private int page = 1;
	private int totalPage;
	private ImageView iv_bg_empty;
	public RelativeLayout errorItem;
	private AlertDialog myDialog;
	private Button cancel;
	private LinearLayout ll_search_box;  //搜索
	private LinearLayout contact_fragment_list_group;
	private LinearLayout ll_consult_all;
	private List<ContactEntry> contacts = new ArrayList<ContactEntry>();
	private PopupWindow popup;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_list_view, null);
		// View view = inflater.inflate(R.layout.contact_fragment_list_view,
		// null);
		errorItem = (RelativeLayout) view.findViewById(R.id.rl_error_item);

		TextView top_title = (TextView) view.findViewById(R.id.top_title);

		top_title.setText(getString(R.string.act_title_patient));
		ImageView btn_back = (ImageView) view.findViewById(R.id.btn_back);

		edit_layout = (LinearLayout) view.findViewById(R.id.edit_layout);
		btn_back.setVisibility(View.INVISIBLE);
		ImageView lv_more = (ImageView) view.findViewById(R.id.iv_right_fun_2);
		lv_more.setVisibility(View.VISIBLE);
		initPopupWindow();
		lv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != popup) {
					popup.showAsDropDown(v, 0, 40);
				}
			}
		});

		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.ptr_layout);
		ll_net_unavailable = (LinearLayout) view
				.findViewById(R.id.ll_net_unavailable);
		ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
		search_edt = (EditText) view.findViewById(R.id.search_edt);
		search = (ImageView) view.findViewById(R.id.search);
		cancel = (Button) view.findViewById(R.id.cancel);
		ll_net_unavailable.setOnClickListener(this);

		search.setOnClickListener(this);
		cancel.setOnClickListener(this);
		LayoutParams lps = ll_net_unavailable.getLayoutParams();
		if (lps == null)
			lps = new LayoutParams(LayoutParams.MATCH_PARENT,
					activity.contentHeight);
		lps.height = activity.contentHeight;
		ll_net_unavailable.setLayoutParams(lps);

		iv_bg_empty = (ImageView) view.findViewById(R.id.iv_bg_empty);
		iv_bg_empty.setOnClickListener(this);

		ActionBarPullToRefresh.from(activity).allChildrenArePullable()
				.setup(mPullToRefreshLayout);
		ContactTable table = new ContactTable();
		ArrayList<ContactEntry> list = table.getAllPositionNames();
		listview = (RefreshListView) view.findViewById(R.id.ptr_listview);
		View listHeadView = View.inflate(activity,
				R.layout.contact_fragment_list_view, null);
		ll_search_box = (LinearLayout) listHeadView
				.findViewById(R.id.ll_search_box);
		ll_search_box.setOnClickListener(this);
		contact_fragment_list_group = (LinearLayout) listHeadView
				.findViewById(R.id.contact_fragment_list_group);
		contact_fragment_list_group.setOnClickListener(this);
		ll_consult_all = (LinearLayout) listHeadView
				.findViewById(R.id.ll_consult_all);
		ll_consult_all.setOnClickListener(this);
		ll_patient_tongxunlu = (LinearLayout) listHeadView
				.findViewById(R.id.ll_patient_tongxunlu); 
		ll_patient_tongxunlu.setOnClickListener(this);
		listview.addHeaderView(listHeadView);
		adapter = new ContactAdapter(list);
		// Set the List Adapter to display the sample items
		listview.setAdapter(adapter);
		addListener();
		if (!NetWorkUtils.detect(activity)) {
			activity.showToast(getString(R.string.network_unavailable));
		}
		setSearchBoxhint();
		edit_layout.setOnClickListener(this);
		search_edt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					String search_tx = search_edt.getText().toString().trim();
					if (search_tx == null || search_tx.equals("")) {
						activity.showToast("搜索关键字为空");
					} else {
						closeKeyboard(activity);
						List<ContactEntry> entries = getListData(
								adapter.getdata(), search_tx);
						if (entries.size() == 0) {
							activity.showToast("没有数据");
							adapter.change(getListData(adapter.getdata(),
									search_tx));
						} else {
							adapter.change(getListData(adapter.getdata(),
									search_tx));

						}
					}
				}

				return false;
			}
		});

		search_edt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s.length() == 0) {
					cancel.setVisibility(View.GONE);
				} else {
					cancel.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		search_edt.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					search_edt.setHint("");
					search_edt.setGravity(Gravity.LEFT);
					search.setVisibility(View.VISIBLE);
				} else {
					setSearchBoxhint();
					search.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		if (SharePrefUtil.getBoolean(Conast.LOGIN)) {
			refresh();
		}

		return view;
	}

	/**
	 * 初始化下拉框
	 */
	private void showAlertDialogue() {
		if (null == myDialog) {
			myDialog = new AlertDialog.Builder(activity).create();
			View view = LayoutInflater.from(activity).inflate(
					R.layout.dialog_cannot_private_patient, null);
			view.setMinimumHeight(MyApplication.getInstance().canvasHeight * 3 / 5);
			view.setMinimumWidth(MyApplication.getInstance().canvasWidth * 3 / 4);
			myDialog.setCanceledOnTouchOutside(true);
			Button btn = (Button) view.findViewById(R.id.button1);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 直接跳到完善资料的部分
					Intent intent = new Intent(activity, RegisterSuccessActivity.class);
					startActivity(intent);
					myDialog.dismiss();
				}
			});
			myDialog.setView(view);
		}

		myDialog.show();
	}

	private void initPopupWindow() {
		LinearLayout ll_menu = (LinearLayout) activity.getLayoutInflater()
				.inflate(R.layout.mass_message_popup_menu, null);
		LinearLayout ll_invite_patient = (LinearLayout) ll_menu
				.findViewById(R.id.ll_invite_patient);
		LinearLayout ll_mass_message = (LinearLayout) ll_menu
				.findViewById(R.id.ll_mass_message);

		ll_invite_patient.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != popup) {
					popup.dismiss();

					if (false == SharePrefUtil
							.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT
									+ SharePrefUtil.getString(Conast.Doctor_ID))) {
						if (true == SharePrefUtil.getBoolean(Conast.VALIDATED)) {
							startActivity(new Intent(activity,
									PersonInfoActivity.class));
						} else {
							if (!TextUtils.isEmpty(SharePrefUtil
									.getString(Conast.AUDIT_STATUS))) {
								if ("0".equals(SharePrefUtil
										.getString(Conast.AUDIT_STATUS))) {
									showAlertDialogue();
								} else {
									startActivity(new Intent(activity,
											RegisterSuccessActivity.class));
								}
							}
						}
					} else {
						startActivity(new Intent(activity,
								BusinessCardActivity.class));
					}
				}
			}
		});

		ll_mass_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null != popup) {
					popup.dismiss();
					Intent intent = new Intent(activity,
							MassMessageActivity.class);
					DeliverContactList deliverContactList = new DeliverContactList(
							contacts);
					intent.putExtra("contacts", deliverContactList);
					startActivity(intent);
				}
			}
		});

		popup = new PopupWindow(ll_menu,
				MyApplication.getInstance().canvasWidth * 2 / 5,
				LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(true);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	private void addListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!NetWorkUtils.detect(activity)) {
					activity.showToast(getString(R.string.network_unavailable));
				}

				// ContactEntry item = (ContactEntry)
				// adapter.getItem(position-2);
				ContactEntry item = (ContactEntry) parent.getAdapter().getItem(
						position);
				Intent intent = new Intent(activity, ContactInfoActivity.class);
				intent.putExtra("title", item.getREALNAME());
				intent.putExtra("tochat_userId", item.getMEMBERID());
				intent.putExtra("user_avatar", item.getAVATAR());
				intent.putExtra("is_star", item.getIS_STAR() == null ? "0"
						: item.getIS_STAR());
				intent.putExtra("note_name", item.getNOTE_NAME());
				intent.putExtra("description", item.getDESCRIPTION());
				startActivity(intent);
			}
		});

		listview.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				refresh();
			}
		});

		listview.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		handler = new Handler(this);
		Logger.dout(TAG + "onCreate");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// fragment可见时执行加载数据或者进度条等
			Logger.dout(TAG + "isVisibleToUser");
			// mPullToRefreshLayout.setRefreshing(true);
			// refresh();
			UICore.eventTask(this, activity, EXEU_GET_DATA, null, null);// 加载数据入口
		} else {
			// 不可见时不执行操作
			Logger.dout(TAG + "unVisibleToUser");
		}
	}

	public void refresh() {
		refresh = true;
		page = 1;
		getContactList();
	}

	private void loadMore() {
		refresh = false;
		page++;
		getContactList();
	}

	private void getContactList() {

		if (NetWorkUtils.detect(activity)) {
			if(adapter.getCount() <= 0)
				activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_contact_list, ContactInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_CONTACT_LIST;
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

//	@Override
//	public void onResume() {
//		super.onResume();
//		Logger.dout(TAG + "onResume");
//		if (null != adapter) {
//			adapter.notifyDataSetChanged();
//		}
//	}

	@Override
	public void onPause() {
		super.onStop();
	}

	public void cancelLoadingBar() {
		if (null != mPullToRefreshLayout && mPullToRefreshLayout.isRefreshing()) {
			mPullToRefreshLayout.setRefreshComplete();
		}
	}

	ContactInfo info = null;

	@Override
	public boolean handleMessage(Message msg) {
		try {
			activity.destroyDialog();
			listview.onRefreshComplete();
			switch (msg.what) {
			case IResult.GET_CONTACT_LIST:
				mPullToRefreshLayout.setRefreshComplete();
				info = (ContactInfo) msg.obj;
				ll_net_unavailable.setVisibility(View.GONE);
				if (null != info && info.getSuccess() == 1) {
					totalPage = info.getPages();
					if (refresh) {
						if (info.getData() == null
								|| info.getData().size() == 0) {
							ll_empty.setVisibility(View.VISIBLE);
							listview.setVisibility(View.GONE);
							ContactTable table = new ContactTable();
							table.clearTable();
							contacts.clear();
						} else {
							ll_empty.setVisibility(View.GONE);
							listview.setVisibility(View.VISIBLE);
							ContactTable table = new ContactTable();
							table.clearTable();
							table.savePositionName(info.getData());
							adapter.change(info.getData());
							contacts.clear();
							contacts.addAll(info.getData());
						}
					} else {
						adapter.add(info.getData());
						contacts.addAll(info.getData());
					}
					if (page < totalPage) {
						listview.onLoadMoreComplete(false);
					} else {
						listview.onLoadMoreComplete(true);
					}
				} else {
					ll_empty.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				}
				break;
			case IResult.DATA_ERROR:
				mPullToRefreshLayout.setRefreshComplete();
				listview.setVisibility(View.GONE);
				ll_empty.setVisibility(View.VISIBLE);
				ll_net_unavailable.setVisibility(View.GONE);
				break;
			case IResult.NET_ERROR:
				mPullToRefreshLayout.setRefreshComplete();
				listview.setVisibility(View.GONE);
				ll_net_unavailable.setVisibility(View.VISIBLE);
				ll_empty.setVisibility(View.GONE);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void execute(int mes, Object obj) {
		switch (mes) {

		case EXEU_GET_DATA:
			refresh();
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_contact_list);
	}

	/**
	 * 联系人自定义
	 * 
	 * @author ASIMO
	 */
	private class ContactAdapter extends BaseAdapter {
		List<ContactEntry> listItems;

		public ContactAdapter(List<ContactEntry> listItems) {
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return (listItems != null && listItems.size() > 0) ? listItems
					.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return (listItems != null && listItems.size() > 0) ? listItems
					.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.contact_item, null);
				holder.image_person = (CircleImageView) convertView
						.findViewById(R.id.image_person);
				holder.image_person.setImageResource(R.drawable.avatar_medium);
				holder.tv_user_name = (TextView) convertView
						.findViewById(R.id.tv_user_name);
				holder.tv_consult_info = (TextView) convertView
						.findViewById(R.id.tv_consult_info);
				holder.unreadLabel = (TextView) convertView
						.findViewById(R.id.unreadLabel);
				holder.iv_fans = (ImageView) convertView
						.findViewById(R.id.iv_fans);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactEntry entry = (ContactEntry) getItem(position);
			if (null != entry) {
				holder.tv_user_name
						.setText(TextUtils.isEmpty(entry.getNOTE_NAME()) ? entry
								.getREALNAME() : entry.getNOTE_NAME());
				if (!TextUtils.isEmpty(entry.getISSYSTEMMSG())
						&& "1".equals(entry.getISSYSTEMMSG())) {
					holder.tv_consult_info.setText(activity
							.getString(R.string.system_msg));
				} else {
					holder.tv_consult_info.setText(entry.getCONTENT());
				}

				activity.imageLoader.displayImage(entry.getAVATAR(),
						holder.image_person, Util.getOptions_pic());
				// 获取与此用户/群组的会话
				EMConversation conversation = EMChatManager.getInstance()
						.getConversation(entry.getMEMBERID());
				if (conversation.getUnreadMsgCount() > 0) {
					// 显示与此用户的消息未读数
					holder.unreadLabel.setText(String.valueOf(conversation
							.getUnreadMsgCount()));
					holder.unreadLabel.setVisibility(View.VISIBLE);
				} else {
					holder.unreadLabel.setVisibility(View.INVISIBLE);
				}

				if ("1".equals(entry.getIS_MY_FANS())) {
					holder.iv_fans.setVisibility(View.VISIBLE);
				} else {
					holder.iv_fans.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		public void change(List<ContactEntry> lists) {
			if (lists == null) {
				lists = new ArrayList<ContactEntry>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void add(List<ContactEntry> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}

		public List<ContactEntry> getdata() {

			return this.listItems;
		}
	}

	class ViewHolder {
		public TextView unreadLabel;
		CircleImageView image_person;
		TextView tv_user_name;
		TextView tv_consult_info;
		ImageView iv_fans;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ll_net_unavailable) {
			getContactList();
		} else if (v.getId() == R.id.iv_bg_empty) {
			mPullToRefreshLayout.setRefreshing(true);
			refresh();
		} else if (v.getId() == R.id.search) {
			closeKeyboard(activity);
			String search_tx = search_edt.getText().toString().trim();
			if (search_tx == null || search_tx.equals("")) {
				activity.showToast("搜索关键字为空");
			} else {

				List<ContactEntry> entries = getListData(adapter.getdata(),
						search_tx);
				if (entries.size() == 0) {
					activity.showToast("没有数据");
					adapter.change(getListData(adapter.getdata(), search_tx));
				} else {
					adapter.change(getListData(adapter.getdata(), search_tx));
				}
			}
		} else if (v.getId() == R.id.cancel) {
			search_edt.setText("");
			search_edt.clearFocus();
			listview.requestFocus();
			listview.requestFocusFromTouch();
			closeKeyboard(activity);
			if (info != null && info.getData().size() > 0) {
				adapter.change(info.getData());
			} else {
				adapter.change(null);
			}
		} else if (v.getId() == R.id.ll_search_box) {    //搜索
			Intent intent = new Intent(activity, SearchPatientActivity.class);
			intent.putExtra("patientgroup", (Serializable) info.getData());
			startActivity(intent);
		} else if (v.getId() == R.id.ll_patient_tongxunlu) { // 患者通讯录
			if (info.getData() != null && info.getData().size() != 0) {
				Intent intent = new Intent(activity,
						PatientTongxunluActivity.class);
				intent.putExtra("tongxunlu", (Serializable) info.getData());
				startActivity(intent);
			} else {
				activity.showToast("你没有患者好友哦!");
			}

		} else if (v.getId() == R.id.contact_fragment_list_group) { // 患者分组
			if (info.getData() != null && info.getData().size() != 0){
				Intent intent = new Intent(activity, PatientGroupActivity.class);
				intent.putExtra("patientgroup", (Serializable) info.getData());
				startActivity(intent);
			}
		} else if (v.getId() == R.id.ll_consult_all) { // 所有服务订单
			Intent intent = new Intent(activity, OrderClassifyActivity.class);
			OrderInformation info = new OrderInformation("所有服务订单", "",
					R.drawable.icon_consult_picture, true);
			intent.putExtra("order_info", info);
			startActivity(intent);
		}
	}

	private void setSearchBoxhint() {
		// 根据资源ID获得资源图像的Bitmap对象
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.order_search);
		// 根据Bitmap对象创建ImageSpan对象
		ImageSpan imageSpan = new ImageSpan(activity, bitmap);
		// 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
		SpannableString spannableString = new SpannableString(
				getString(R.string.search_order));
		// 用ImageSpan对象替换face
		spannableString.setSpan(imageSpan, 0, 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 将随机获得的图像追加到EditText控件的最后
		// et_search_box.setText(spannableString);
		search_edt.setHint(spannableString);
		search_edt.setGravity(Gravity.CENTER);
		search_edt.setHintTextColor(Color.parseColor("#a4a4a4"));

	}

	public List<ContactEntry> getListData(List<ContactEntry> oidlist,
			String search) {
		List<ContactEntry> list = new ArrayList<ContactEntry>();
		for (int i = 0; i < oidlist.size(); i++) {
			if (oidlist.get(i).getREALNAME().contains(search)) {
				list.add(oidlist.get(i));
			}
		}
		return list;
	}

	public void closeKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 得到InputMethodManager的实例
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(activity.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	class AlterNoteNameReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(null != info){
				String doctor_id = intent.getStringExtra("user_id");
				String note_name = intent.getStringExtra("note_name");
				List<ContactEntry> list = info.getData();
				for(int i = 0; i < list.size(); i++){
					if(list.get(i).getMEMBERID().equals(doctor_id)){
						list.get(i).setNOTE_NAME(note_name);
//						info.setData(list);
						adapter.change(info.getData());
						new Thread(new Runnable() {
							@Override
							public void run() {
								ContactTable table = new ContactTable();
								table.clearTable();
								table.savePositionName(info.getData());
								contacts.clear();
								contacts.addAll(info.getData());
							}
						}).start();
						break;
					}
				}
			}
		}
	}

}
