package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.CommunicationGroupCreateActivity;
import net.ememed.doctor2.activity.GroupSearchActivity;
import net.ememed.doctor2.activity.GroupsChatActivity;
import net.ememed.doctor2.activity.NoticeListActivity;
import net.ememed.doctor2.entity.GroupList;
import net.ememed.doctor2.entity.GroupNotice;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.EmUtils;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class CommunicationGroupFragment extends Fragment implements OnClickListener,
		OnItemClickListener, IOnRefreshListener, IOnLoadMoreListener {

	private static final int REQUEST_CREATE_GROUP = 100;
	private static final String[] GROUP_TYPE = { "我创建的群组", "我加入的群组" };

	private final int STATUS_LISTVIEW = 0x0;
	private final int STATUS_EMPTY = 0x01;
	private final int STATUS_UNAVAILABLE = 0x10;

	private RefreshListView mListview;
	private QuickAdapter<GroupList.Group> adapter;
	private TextView tv_create, tv_seach;

	private View layout_notice;
	private TextView tv_noticeMessage, tv_noticeCount, tv_noticeTime;

	private View layout_empty, layout_unavailable;
	private int mPage;

	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnFail(R.drawable.avatar_large).showImageForEmptyUri(R.drawable.avatar_large)
			.showImageOnLoading(R.drawable.avatar_large).resetViewBeforeLoading(true)
			.cacheInMemory(true).cacheOnDisk(true).build();

	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			OnRefresh();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		IntentFilter filter = new IntentFilter(ActionFilter.REQUEST_DOCTOR_GROUP_LIST);
		activity.registerReceiver(mRefreshReceiver, filter);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(mRefreshReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_communication_friend, null);
		View header = inflater.inflate(R.layout.layout_communication_group_head, null);
		layout_notice = header.findViewById(R.id.layout_notice);
		layout_notice.setOnClickListener(this);
		tv_noticeMessage = (TextView) header.findViewById(R.id.tv_notice_message);
		tv_noticeTime = (TextView) header.findViewById(R.id.tv_notice_time);
		tv_noticeCount = (TextView) header.findViewById(R.id.tv_notice_count);

		tv_create = (TextView) header.findViewById(R.id.tv_create);
		tv_seach = (TextView) header.findViewById(R.id.tv_seach);
		tv_create.setOnClickListener(this);
		tv_seach.setOnClickListener(this);
		mListview = (RefreshListView) rootView.findViewById(R.id.ptr_listview);
		mListview.addHeaderView(header);
		adapter = new QuickAdapter<GroupList.Group>(getActivity(),
				R.layout.list_item_communication_group) {

			@Override
			protected void convert(BaseAdapterHelper helper, GroupList.Group item) {
				// set group type
				helper.setVisible(R.id.tv_type,
						getPositionForCreate(item.isOwner()) == helper.getPosition());
				helper.setText(R.id.tv_type, GROUP_TYPE[item.isOwner() ? 0 : 1]);

				helper.setImageUrl(R.id.iv_portrait, item.getPortrait(), options).setText(
						R.id.tv_name,
						String.format("%s（%s）", item.getGroupName(), item.getGroupNum()));

				EMConversation conversation = item.getEmConversation();
				if (conversation == null)
					return;
				if (conversation.getMsgCount() != 0) {
					// 把最后一条消息的内容作为item的message内容
					EMMessage lastMessage = conversation.getLastMessage();
					helper.setText(R.id.tv_message,
							EmUtils.getMessageDigest(lastMessage, getActivity()));
				}
			}

			/**
			 * if isCreater=true return the first create group position, if isCreater=false return
			 * the first uncreate group position
			 * 
			 * @param isCreater
			 * @return
			 */
			private int getPositionForCreate(boolean isCreater) {
				for (int i = 0; i < adapter.getCount(); i++) {
					if (adapter.getItem(i).isOwner() == isCreater)
						return i;
				}
				return -1;
			}
		};
		mListview.setAdapter(adapter);
		mListview.setOnItemClickListener(this);
		mListview.setOnRefreshListener(this);

		layout_empty = rootView.findViewById(R.id.layout_empty);
		layout_unavailable = rootView.findViewById(R.id.layout_unavailable);
		layout_unavailable.findViewById(R.id.iv_net_failed).setOnClickListener(this);

		OnRefresh();
		return rootView;
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			try {
				mListview.onRefreshComplete();
				switch (msg.what) {
				case IResult.GET_GROUP_LIST:
					final GroupList groupList = (GroupList) msg.obj;
					if (groupList == null) {// 无数据
						return false;
					}
					if (!groupList.isSuccess()) {// 服务器报错
						// showViewStatus(STATUS_UNAVAILABLE);
						Toast.makeText(getActivity(), "数据加载出错", Toast.LENGTH_SHORT).show();
						return false;
					}
					if (groupList.isEmpty()) {// 数据为空
						// showViewStatus(STATUS_EMPTY);
						Toast.makeText(getActivity(), "数据加载出错", Toast.LENGTH_SHORT).show();
						return false;
					}
					showViewStatus(STATUS_LISTVIEW);
					if (mPage == 1)
						adapter.clear();
					updateGroupListWithRecentChat(groupList.getGroups());
					adapter.addAll(groupList.getGroups());
					break;
				case IResult.GET_GROUP_NOTICE_LAST:
					final GroupNotice groupNotice = (GroupNotice) msg.obj;
					if (groupNotice == null) {
						return false;
					}
					if (!groupNotice.isSuccess()) {
						return false;
					}
					GroupNotice.Notice notice = groupNotice.getNotice();
					if (notice == null) {// 无最新通知
						layout_notice.setVisibility(View.GONE);
						return false;
					}
					tv_noticeCount.setVisibility(notice.getCount().equals("0") ? View.GONE
							: View.VISIBLE);
					tv_noticeMessage.setText(notice.getMessage());
					tv_noticeCount.setText(notice.getCount());
					tv_noticeTime.setText(notice.getTime());
					layout_notice.setVisibility(View.VISIBLE);
					break;
				case IResult.DATA_ERROR:
					Toast.makeText(getActivity(), "数据加载出错", Toast.LENGTH_SHORT).show();
					break;
				case IResult.NET_ERROR:
					Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
					// showViewStatus(STATUS_UNAVAILABLE);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	});

	/**
	 * 请求群组列表
	 */
	private void requestGroupList() {
		if (!NetWorkUtils.detect(getActivity())) {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("page", String.valueOf(mPage));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.GET_GROUP_LIST, GroupList.class,
				params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_GROUP_LIST;
						mHandler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.DATA_ERROR;
						mHandler.sendMessage(message);
					}
				});
	}

	/**
	 * 请求群组通知
	 */
	private void requestGroupNotify() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.GET_GROUP_NOTICE_LAST,
				GroupNotice.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_GROUP_NOTICE_LAST;
						mHandler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.DATA_ERROR;
						mHandler.sendMessage(message);
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_create:
			startActivityForResult(
					new Intent(getActivity(), CommunicationGroupCreateActivity.class),
					REQUEST_CREATE_GROUP);
			break;
		case R.id.tv_seach:
			startActivity(new Intent(getActivity(), GroupSearchActivity.class));
			break;
		case R.id.iv_net_failed:
			OnRefresh();
			break;
		case R.id.layout_notice:
			startActivity(new Intent(getActivity(), NoticeListActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), GroupsChatActivity.class);
		GroupList.Group group = adapter.getItem(position - 2);
		intent.putExtra("group_num", group.getGroupNum());
		intent.putExtra("tochat_userId", group.getEasemobGroupId());
		intent.putExtra("user_avatar", group.getPortrait());
		intent.putExtra("title", group.getGroupName());
		intent.putExtra("isOwner", group.isOwner());
		startActivity(intent);
	}

	@Override
	public void OnRefresh() {
		mPage = 1;
		requestGroupList();
		requestGroupNotify();
	}

	@Override
	public void OnLoadMore() {
		mPage++;
		requestGroupList();
	}

	private void showViewStatus(int status) {
		int empty = (status & STATUS_EMPTY) == 0 ? View.GONE : View.VISIBLE;
		int unavailable = (status & STATUS_UNAVAILABLE) == 0 ? View.GONE : View.VISIBLE;
		layout_empty.setVisibility(empty);
		layout_unavailable.setVisibility(unavailable);
	}

	/**
	 * 根据本地群组会话记录向数据列表添加数据
	 */
	private void updateGroupListWithRecentChat(List<GroupList.Group> groups) {
		List<EMConversation> conversations = loadGroupConversationsWithRecentChat();
		int count = groups.size();
		for (int i = 0; i < count; i++) {
			GroupList.Group group = groups.get(i);
			for (EMConversation conversation : conversations) {
				if (group.getEasemobGroupId().equals(conversation.getUserName())) {
					group.setEmConversation(conversation);
				}
			}
		}
		// 排序
		sortGroupListByLastChatTime(groups);
	}

	/**
	 * 获取所有群组会话
	 * 
	 * @param context
	 * @return
	 */
	private List<EMConversation> loadGroupConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance()
				.getAllConversations();
		List<EMConversation> list = new ArrayList<EMConversation>();
		// 过滤掉messages size为0的conversation
		for (EMConversation conversation : conversations.values()) {
			if (conversation.getAllMessages().size() != 0 && conversation.getIsGroup())
				list.add(conversation);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param groupList
	 */
	private void sortGroupListByLastChatTime(List<GroupList.Group> groupList) {
		Collections.sort(groupList, new Comparator<GroupList.Group>() {
			@Override
			public int compare(final GroupList.Group group1, final GroupList.Group group2) {
				if (group1.isOwner() != group2.isOwner()) {
					return 0;
				}
				if (group1.getEmConversation() == null && group2.getEmConversation() == null) {
					return 0;
				}
				if (group1.getEmConversation() == null) {
					return 1;
				}
				if (group2.getEmConversation() == null) {
					return -1;
				}

				EMMessage con1LastMessage = group1.getEmConversation().getLastMessage();
				EMMessage con2LastMessage = group2.getEmConversation().getLastMessage();
				if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
					return 0;
				} else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

}
