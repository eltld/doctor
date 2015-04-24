package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.greenrobot.event.EventBus;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.FindDoctorFriendActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.db.ContactTable;
import net.ememed.doctor2.db.DoctorFriendTable;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.DoctorFriendEntry;
import net.ememed.doctor2.entity.DoctorFriendInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DoctorFriendFragment extends Fragment implements Callback, OnClickListener{
	private final int READ_LOCAL_DATA_COMPLETED = 1001;
	
	private Handler handler = null;
	private MainActivity activity = null;
	private PullToRefreshLayout mPullToRefreshLayout;
	private LinearLayout ll_net_unavailable;
	private LinearLayout ll_empty;
	private ImageView iv_bg_empty;
	private RefreshListView listview;
	private ContactAdapter adapter;
	private InnerReceiver receiver;
	private IntentFilter filter;
	private TextView txtview;
	private List<DoctorFriendInfo> friends = null;
	private List<DoctorFriendInfo> localfriends = null;	//从本地数据库读取的

	
	public DoctorFriendFragment() {
		this.activity = (MainActivity) getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		
		receiver = new InnerReceiver();
		filter = new IntentFilter();
		filter.addAction(ActionFilter.REQUEST_DOCTOR_FRIEND_LIST);
		activity.registerReceiver(receiver, filter);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.doctor_friend_list_view, null);
		TextView top_title = (TextView) view.findViewById(R.id.top_title);
		
		top_title.setText(getString(R.string.act_title_doctor_friend));
		ImageView btn_back = (ImageView) view.findViewById(R.id.btn_back);
		btn_back.setVisibility(View.INVISIBLE);
		Button btn_addhealth = (Button) view.findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundColor(Color.TRANSPARENT);
		btn_addhealth.setText("查找好友");
		btn_addhealth.setTextSize((float) 15);
		LayoutParams layoutParams = btn_addhealth.getLayoutParams();
		layoutParams.width=LayoutParams.WRAP_CONTENT;
		btn_addhealth.setLayoutParams(layoutParams);
		
		Drawable left = getResources().getDrawable(R.drawable.add_friend);
		left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
		btn_addhealth.setCompoundDrawables(left , null, null, null);
		btn_addhealth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//XXX 添加查找医生的界面
				startActivity(new Intent(activity, FindDoctorFriendActivity.class));
			}
		});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				DoctorFriendTable table = new DoctorFriendTable();
				localfriends = table.getDoctorFriends();
				
				if(localfriends != null && localfriends.size() > 0){
					handler.sendEmptyMessage(READ_LOCAL_DATA_COMPLETED);
				}
			}
		}).start();

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		ll_net_unavailable = (LinearLayout) view.findViewById(R.id.ll_net_unavailable);
		ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
		ll_net_unavailable.setOnClickListener(this);
		LayoutParams lps = ll_net_unavailable.getLayoutParams();
		if (lps == null)
			lps = new LayoutParams(LayoutParams.MATCH_PARENT, activity.contentHeight);
		lps.height = activity.contentHeight;
		ll_net_unavailable.setLayoutParams(lps);

		iv_bg_empty = (ImageView) view.findViewById(R.id.iv_bg_empty);
		iv_bg_empty.setOnClickListener(this);

		ActionBarPullToRefresh.from(activity).allChildrenArePullable().setup(mPullToRefreshLayout);
		/*ContactTable table = new ContactTable();
		ArrayList<DoctorFriendInfo> list = table.getAllPositionNames();*/
		
		listview = (RefreshListView) view.findViewById(R.id.ptr_listview);
		adapter = new ContactAdapter(friends);
		listview.setAdapter(adapter);
		addListener();
		if (!NetWorkUtils.detect(activity)) {
			activity.showToast(getString(R.string.network_unavailable));
		}
		
		refresh();
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	private void addListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!NetWorkUtils.detect(activity)) {
					activity.showToast(getString(R.string.network_unavailable));
				}
				
				DoctorFriendInfo info = (DoctorFriendInfo) parent.getAdapter().getItem(position);
				Intent intent = new Intent(activity, OtherBaikeActivity.class);
				intent.putExtra("other_doctor_id", info.getFRIENDID());
				intent.putExtra("from", DoctorFriendFragment.class.getSimpleName());
				startActivity(intent);
			}
		});

		listview.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				refresh();
			}
		});

		/*listview.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});*/
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		handler = new Handler(this);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// fragment可见时执行加载数据或者进度条等
			// mPullToRefreshLayout.setRefreshing(true);
			// refresh();
			//UICore.eventTask(this, activity, EXEU_GET_DATA, null, null);// 加载数据入口
		} else {
			// 不可见时不执行操作
			//Logger.dout(TAG + "unVisibleToUser");
		}
	}

	public void refresh() {
		getDoctorFriendList();
	}

	/*private void loadMore() {
		getDoctorFriendList();
	}*/

	private void getDoctorFriendList() {

		if (NetWorkUtils.detect(activity)) {
			if(null == localfriends && null == friends){
				activity.loading(null);
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_my_friend_list, DoctorFriendEntry.class, params, new Response.Listener() {
				@Override
				public void onResponse(Object response) {
					Message message = new Message();
					message.obj = response;
					message.what = IResult.GET_DOCTOR_FRIEND;
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
	
	public void onEvent() {
		

	}

	@Override
	public void onResume() {
		super.onResume();
		/*if (null != adapter) {
			adapter.notifyDataSetChanged();
		}*/
		/*if (SharePrefUtil.getBoolean(Conast.LOGIN)) {
			refresh();
		}*/
	}

	@Override
	public void onPause() {
		super.onStop();
	}

	public void cancelLoadingBar() {
		if (null != mPullToRefreshLayout && mPullToRefreshLayout.isRefreshing()) {
			mPullToRefreshLayout.setRefreshComplete();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		try {
			activity.destroyDialog();
			listview.onRefreshComplete();
			switch (msg.what) {
			case IResult.GET_DOCTOR_FRIEND:
				mPullToRefreshLayout.setRefreshComplete();
				final DoctorFriendEntry info = (DoctorFriendEntry) msg.obj;
				ll_net_unavailable.setVisibility(View.GONE);
				if (null != info && info.getSuccess() == 1) {
					if (info.getData() == null || info.getData().size() == 0) {
						ll_empty.setVisibility(View.VISIBLE);
						txtview=(TextView) ll_empty.findViewById(R.id.tv_notice);
						txtview.setText("你还没有好友哦！");
						listview.setVisibility(View.GONE);
						DoctorFriendTable table = new DoctorFriendTable();
						table.clearCurrentUserMsg();
					} else {
						ll_empty.setVisibility(View.GONE);
						listview.setVisibility(View.VISIBLE);
						friends = info.getData();
						adapter.change(friends);
						
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								DoctorFriendTable table = new DoctorFriendTable();
								table.clearCurrentUserMsg();
								table.saveDoctorFriends(info.getData());
							}
						}).start();
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
			case READ_LOCAL_DATA_COMPLETED:
				if(null == friends)
					adapter.change(localfriends);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		List<DoctorFriendInfo> listItems;

		public ContactAdapter(List<DoctorFriendInfo> listItems) {
			if(null == listItems){
				this.listItems = new ArrayList<DoctorFriendInfo>();
			}
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return (listItems != null && listItems.size() > 0) ? listItems.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return (listItems != null && listItems.size() > 0) ? listItems.get(position) : null;
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
				convertView = LayoutInflater.from(activity).inflate(R.layout.doctor_friend_item, null);
				holder.image_person = (CircleImageView) convertView.findViewById(R.id.image_person);
				//holder.image_person.setImageResource(R.drawable.avatar_medium);
				holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
				//holder.tv_consult_info = (TextView) convertView.findViewById(R.id.tv_consult_info);
				//holder.unreadLabel = (ImageView) convertView.findViewById(R.id.unreadLabel);
				//holder.tv_private_doctor = (TextView) convertView.findViewById(R.id.tv_private_doctor);
				holder.iv_attention = (ImageView) convertView.findViewById(R.id.iv_attention);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			DoctorFriendInfo info = (DoctorFriendInfo) getItem(position);
			if (null != info) {
				holder.tv_user_name.setText(info.getREALNAME());
				activity.imageLoader.displayImage(info.getAVATAR(), holder.image_person, Util.getOptions_pic());
				if("1".equals(info.getIS_ATTENTION())){
					holder.iv_attention.setVisibility(View.VISIBLE);
				} else {
					holder.iv_attention.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		public void change(List<DoctorFriendInfo> lists) {
			if (lists == null) {
				lists = new ArrayList<DoctorFriendInfo>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void add(List<DoctorFriendInfo> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		//public ImageView unreadLabel;
		CircleImageView image_person;
		TextView tv_user_name;
		ImageView iv_attention;
		//TextView tv_consult_info;
		//TextView tv_private_doctor;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		activity.unregisterReceiver(receiver);
	}
	
	private class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ActionFilter.REQUEST_DOCTOR_FRIEND_LIST)){
				getDoctorFriendList();
			}
		}
	}
}
