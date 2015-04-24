package net.ememed.doctor2.fragment;

import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.FindDoctorFriendActivity;
import net.ememed.doctor2.activity.InviteActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.entity.DoctorFriendEntry;
import net.ememed.doctor2.entity.DoctorFriendInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

public class CommunicationFriendFragment extends Fragment implements Callback, OnClickListener,
		OnItemClickListener, IOnRefreshListener {

	private final int STATUS_LISTVIEW = 0x0;
	private final int STATUS_EMPTY = 0x01;
	private final int STATUS_UNAVAILABLE = 0x10;

	private RefreshListView mListview;
	private QuickAdapter<DoctorFriendInfo> adapter;
	private RefreshReceiver mRefreshReceiver;
	private TextView tv_invite, tv_seach;

	private Handler handler = null;
	private MainActivity activity = null;

	private View layout_empty, layout_unavailable;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		mRefreshReceiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter(ActionFilter.REQUEST_DOCTOR_FRIEND_LIST);
		activity.registerReceiver(mRefreshReceiver, filter);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		handler = new Handler(this);
		View rootView = inflater.inflate(R.layout.fragment_communication_friend, null);
		View header = inflater.inflate(R.layout.layout_communication_friend_head, null);
		tv_invite = (TextView) header.findViewById(R.id.tv_invite);
		tv_seach = (TextView) header.findViewById(R.id.tv_seach);
		tv_invite.setOnClickListener(this);
		tv_seach.setOnClickListener(this);
		mListview = (RefreshListView) rootView.findViewById(R.id.ptr_listview);
		mListview.addHeaderView(header);
		adapter = new QuickAdapter<DoctorFriendInfo>(getActivity(),
				R.layout.list_item_communication_friend) {

			@Override
			protected void convert(BaseAdapterHelper helper, DoctorFriendInfo item) {
				helper.setImageUrl(R.id.iv_portrait, item.getAVATAR())
						.setText(R.id.tv_name, item.getREALNAME())
						.setVisible(R.id.iv_attention, item.isAttention());
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

	/**
	 * 请求医生好友列表
	 */
	private void requestDoctorFriendList() {
		if (!NetWorkUtils.detect(activity)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_my_friend_list,
				DoctorFriendEntry.class, params, new Response.Listener() {
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
	}

	public void onEvent() {
	}

	@Override
	public boolean handleMessage(Message msg) {
		try {
			mListview.onRefreshComplete();
			switch (msg.what) {
			case IResult.GET_DOCTOR_FRIEND:
				final DoctorFriendEntry info = (DoctorFriendEntry) msg.obj;
				if (info == null) {// 无数据
					return false;
				}
				if (!info.isSuccess()) {// 服务器报错
					showViewStatus(STATUS_UNAVAILABLE);
					return false;
				}
				if (info.isEmpty()) {// 数据未空
					showViewStatus(STATUS_EMPTY);
					return false;
				}
				showViewStatus(STATUS_LISTVIEW);
				adapter.clear();
				adapter.addAll(info.getData());
				break;
			case IResult.DATA_ERROR:
				Toast.makeText(getActivity(), "数据加载出错", Toast.LENGTH_SHORT).show();
				break;
			case IResult.NET_ERROR:
				Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
//				showViewStatus(STATUS_UNAVAILABLE);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_invite:
			startActivity(new Intent(getActivity(), InviteActivity.class));
			break;
		case R.id.tv_seach:
			startActivity(new Intent(getActivity(), FindDoctorFriendActivity.class));
			break;
		case R.id.iv_net_failed:
			OnRefresh();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		activity.unregisterReceiver(mRefreshReceiver);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DoctorFriendInfo info = (DoctorFriendInfo) parent.getAdapter().getItem(position);
		Intent intent = new Intent(activity, OtherBaikeActivity.class);
		intent.putExtra("other_doctor_id", info.getFRIENDID());
		intent.putExtra("from", CommunicationFriendFragment.class.getSimpleName());
		startActivity(intent);
	}

	/**
	 * 界面刷新广播
	 * 
	 * @author pch
	 * 
	 */
	private class RefreshReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			OnRefresh();
		}
	}

	@Override
	public void OnRefresh() {
		requestDoctorFriendList();
	}

	private void showViewStatus(int status) {
		int empty = (status & STATUS_EMPTY) == 0 ? View.GONE : View.VISIBLE;
		int unavailable = (status & STATUS_UNAVAILABLE) == 0 ? View.GONE : View.VISIBLE;
		layout_empty.setVisibility(empty);
		layout_unavailable.setVisibility(unavailable);
	}
}
