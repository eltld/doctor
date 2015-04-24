package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.activity.DoctorChatActivity;
import net.ememed.doctor2.activity.OrderDetailActivity;
import net.ememed.doctor2.activity.PreeChatActivity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.OrderListEntity;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TimeUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;

import de.greenrobot.event.MessageSystemEvent;

/***
 * 医生详情页 订单tab
 * 
 * @author chen
 */
public class DoctorOrderFragment extends Fragment implements Handler.Callback,
		OnClickListener, OnRefreshListener {

	private ContactInfoActivity activity = null;
	private Handler mHandler;
	// private PullToRefreshLayout mPullToRefreshLayout;
	private View ll_view_content;
	private RefreshListView list_view;
	private ContactAdapter adapter;
	private List<OrderListEntry> listItems;

	private int totalpages = 1;
	private int page = 1;

	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private boolean needToRefreshList = false;

	private String title;
	private String tochat_userId;
	private String user_avatar;
	EMConversation conversation;
	
	private Button btn_addhealth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
		activity = (ContactInfoActivity) getActivity();

		title = getArguments().getString("title");
		tochat_userId = getArguments().getString("tochat_userId");
		user_avatar = getArguments().getString("user_avatar");

		conversation = EMChatManager.getInstance().getConversation(
				tochat_userId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ContactInfoActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.root_layout, null);
		FrameLayout mContentView = (FrameLayout) view
				.findViewById(R.id.mainView);

		// mPullToRefreshLayout = (PullToRefreshLayout)
		// view.findViewById(R.id.ptr_layout);
		// ActionBarPullToRefresh.from(activity).allChildrenArePullable().listener(this).setup(mPullToRefreshLayout);
		ll_view_content = (View) LayoutInflater.from(activity).inflate(
				R.layout.fragment_doctor_order, null);
		ll_empty = (LinearLayout) ll_view_content.findViewById(R.id.ll_empty);
		btn_addhealth = (Button) ll_view_content.findViewById(R.id.btn_addhealth);
		ll_net_unavailable = (LinearLayout) ll_view_content
				.findViewById(R.id.ll_net_unavailable);
		mContentView.addView(ll_view_content);
		list_view = (RefreshListView) ll_view_content
				.findViewById(R.id.ptr_listview);
		adapter = new ContactAdapter(null);
		// Set the List Adapter to display the sample items
		list_view.setAdapter(adapter);

		addListener();

		refresh();
		return view;
	}

	private void addListener() {
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					OrderListEntry olEntity = (OrderListEntry) adapter
							.getItem(position - 1);

					if (olEntity.getORDERTYPE().equals("15")) {
						Intent intent = new Intent(getActivity(),
								OrderDetailActivity.class);
						intent.putExtra("user_name", title);
						intent.putExtra("user_avatar", user_avatar);
						intent.putExtra("order", olEntity);
						startActivity(intent);
					} else if (olEntity.getORDERTYPE().equals("17")) {

						Intent intent = new Intent(getActivity(),
								PreeChatActivity.class);
						intent.putExtra("tochat_userId", tochat_userId);
						intent.putExtra("user_avatar", user_avatar);
//						if (olEntity.getSTATE().equals("3")
//								|| olEntity.getSTATE().equals("4")) {
//							intent.putExtra("status", "1");
//						} else {
							intent.putExtra("status", olEntity.getSTATE());
//						}
						intent.putExtra("title", title);
						intent.putExtra("orderid", olEntity.getORDERID());
						intent.putExtra("questionid", olEntity.getQUESTIONID());
						startActivity(intent);

					} else {
						Intent intent = new Intent(activity,
								DoctorChatActivity.class);
						intent.putExtra("title", title);
						intent.putExtra("tochat_userId", tochat_userId);
						intent.putExtra("user_avatar", user_avatar);
						intent.putExtra("orderid", olEntity.getORDERID());
						intent.putExtra("DESC_TEXT", olEntity.getDESC_TEXT());
						intent.putExtra("ORDERTYPE", olEntity.getORDERTYPE());
						intent.putExtra("order", olEntity);
						startActivity(intent);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		list_view.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				refresh();
			}
		});
		list_view.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// if(needToRefreshList){
		// refresh();
		// }
	}

	private String TAG = DoctorOrderFragment.class.getSimpleName();

	/***
	 * 订单修改过 需要重新刷新
	 * 
	 * @param event
	 */
	public void receiveEvent(MessageSystemEvent event) {
		if (event.getType() != null) {
			needToRefreshList = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
	}

	public void refresh() {
		page = 1;
		getUserOrderList(page);
	}

	protected void loadMore() {
		page++;
		getUserOrderList(page);
	}

	private void getUserOrderList(int page, boolean isLoading) {
		if (NetWorkUtils.detect(activity)) {
			if (isLoading)
				activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("any_user_id", activity.tochat_userId);
			params.put("type", "0");// 获取类型 0 所有订单 1 已结束订单 2 还在进行中的订单
			params.put("page", page + "");

			System.out.println("订单params = "+params);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_order_list, OrderListEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.ORDER_LIST;
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
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	private void getUserOrderList(int page) {
		getUserOrderList(page, true);
	}
	
	public void refreshListView(){
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean handleMessage(Message msg) {
		try {
			switch (msg.what) {
			case IResult.ORDER_LIST:
				needToRefreshList = false;
				list_view.onRefreshComplete();
				activity.destroyDialog();
				OrderListEntity olEntity = (OrderListEntity) msg.obj;
				if (null != olEntity) {
					if (olEntity.getSuccess() == 1) {
						if (page ==1) {
							adapter.change(olEntity.getData());
						} else {
							adapter.add(olEntity.getData());
						}
						if (page < olEntity.getPages()) {
							list_view.onLoadMoreComplete(false);
						} else {
							list_view.onLoadMoreComplete(true);
						}

					} else {
						activity.showToast(olEntity.getErrormsg());
						ll_empty.setVisibility(View.VISIBLE);
						list_view.setVisibility(View.GONE);
					}
				} else {
					activity.showToast(IMessage.DATA_ERROR);
					ll_empty.setVisibility(View.VISIBLE);
					list_view.setVisibility(View.GONE);
				}
				break;
			case IResult.NET_ERROR:
				activity.showToast(IMessage.NET_ERROR);
				ll_net_unavailable.setVisibility(View.VISIBLE);
				list_view.setVisibility(View.GONE);
				break;
			case IResult.DATA_ERROR:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_order_list);
	}

	@Override
	public void onRefreshStarted(View view) {
		refresh();
	}

	private class ContactAdapter extends BaseAdapter {
		List<OrderListEntry> listItems;

		public ContactAdapter(List<OrderListEntry> listItems) {
			if (listItems == null) {
				listItems = new ArrayList<OrderListEntry>();
			}
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return (listItems == null || listItems.size() == 0) ? null
					: listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(activity).inflate(R.layout.fragment_doctor_order_item, null);
				holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
				holder.tv_consult_state = (TextView) convertView.findViewById(R.id.tv_consult_state);
				holder.tv_consult_date = (TextView) convertView.findViewById(R.id.tv_consult_date);
				holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
				holder.information_tx = (TextView) convertView.findViewById(R.id.information_tx);
				holder.iv_service_icon = (ImageView) convertView.findViewById(R.id.iv_service_icon);
				holder.iv_red_dot = (TextView) convertView.findViewById(R.id.iv_red_dot);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			OrderListEntry orderListEntry = listItems.get(position);
			if(orderListEntry != null) {
				
				holder.tv_user_name.setText(orderListEntry.getORDERTYPENAME());
				holder.tv_consult_state.setText(orderListEntry.getSTATE_DESC());
//				if(orderListEntry.getPACKET_BUY_ORDERID() != null){
//					holder.tv_user_name.setText(PublicUtil.getServiceNameByid(orderListEntry.getORDERTYPE())+"(私人医生服务)");
//				}else{
//					holder.tv_user_name.setText(PublicUtil.getServiceNameByid(orderListEntry.getORDERTYPE()));
//				}
//				if ("2".equals(orderListEntry.getORDERTYPE()) && "2".equals(orderListEntry.getSTATE())) {
//					holder.tv_consult_state.setText(PublicUtil.getServiceState(orderListEntry.getORDERTYPE(),orderListEntry.getSTATE())+TimeUtil.parseDateTime(Long.valueOf(orderListEntry.getSERVICE_CALL_TIME()),"yyyy-MM-dd HH:mm"));
//				} else {
//					holder.tv_consult_state.setText(PublicUtil.getServiceState(orderListEntry.getORDERTYPE(),orderListEntry.getSTATE()));
//				}
				
				//服务未结束则显示红点
//				if("服务结束".equals(PublicUtil.getServiceState(orderListEntry.getORDERTYPE(),orderListEntry.getSTATE()))||"已完成".equals(orderListEntry.getSTATE_DESC())){
//					holder.iv_red_dot.setVisibility(View.GONE);
//				} else {
//					holder.iv_red_dot.setVisibility(View.VISIBLE);
//				}
				
				
				if (conversation.getUnreadMsgCount() > 0) {
//					 显示与此用户的消息未读数
					 int n = getUnreadNumber(orderListEntry.getORDERID());
					if(n>0){
						holder.iv_red_dot.setText(String.valueOf(n));
						holder.iv_red_dot.setVisibility(View.VISIBLE);
					}else {
						holder.iv_red_dot.setVisibility(View.INVISIBLE);
					}
				} else {
					holder.iv_red_dot.setVisibility(View.INVISIBLE);
				}
				
				
				if (!TextUtils.isEmpty(orderListEntry.getADDTIME())) {
					holder.tv_consult_date.setText(TimeUtil.parseFullDateTime2YMD(orderListEntry.getADDTIME()));
				}
				
				if (TextUtils.isEmpty(orderListEntry.getGOODSAMOUNT())) {
					holder.tv_price.setVisibility(View.GONE);
				} else{
//					ordertype订单类型：'1'=>"图文咨询",'2'=>"预约通话",'3'=>"名医加号",'4'=>"上门会诊",'14'=>"住院直通车",'15'=>"私人医生服务服务",'16'=>"其他服务"
					if (Double.valueOf(orderListEntry.getGOODSAMOUNT()) > 0) {
						holder.tv_price.setVisibility(View.VISIBLE);
						holder.tv_price.setText("￥"+orderListEntry.getGOODSAMOUNT());
					} else {
						if ("1".equals(orderListEntry.getORDERTYPE()) || "2".equals(orderListEntry.getORDERTYPE()) || "15".equals(orderListEntry.getORDERTYPE())) {
							holder.tv_price.setVisibility(View.VISIBLE);
							holder.tv_price.setText(getString(R.string.text_order_price_free));
						} else {
							holder.tv_price.setVisibility(View.GONE);
						}
					}
				}
				holder.iv_service_icon.setImageResource(PublicUtil.getServiceDrawableByServiceid(orderListEntry.getORDERTYPE()));
				if("17".equals(orderListEntry.getORDERTYPE())){
					holder.iv_service_icon.setImageResource(R.drawable.question_mianfei);
				}
				String information_tx = orderListEntry.getDESC_TEXT();
				if(information_tx==null|| information_tx.equals("")){
					holder.information_tx.setVisibility(View.INVISIBLE);
				}else{
					holder.information_tx.setText(orderListEntry.getDESC_TEXT());
				}
			}
			return convertView;
		}

		public void change(List<OrderListEntry> lists) {
			if (lists == null) {
				lists = new ArrayList<OrderListEntry>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void add(List<OrderListEntry> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		public TextView tv_price;
		public ImageView iv_service_icon;
		public TextView tv_consult_date;
		public TextView tv_user_name;
		public TextView tv_consult_state;
		public TextView information_tx;
		public TextView iv_red_dot;
	}

	public int getUnreadNumber(String orderId) {

		int unCount = 0;
		List<EMMessage> emData = conversation.getAllMessages();
		for (int i = 0; i < emData.size(); i++) {
			EMMessage message = emData.get(i);
			if (message.isUnread()||message.isListened()) {
				try {
					String ext = message.getStringAttribute("ext");
					JSONObject jsonObject = new JSONObject(ext);
					String  id  = jsonObject.getString("ORDERID");
					if(orderId.equals(id)){
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

}
