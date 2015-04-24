package net.ememed.doctor2.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.GroupCreateActivity;
import net.ememed.doctor2.activity.GroupDetailsActivity;
import net.ememed.doctor2.activity.GroupFindActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.entity.GroupListEntry;
import net.ememed.doctor2.entity.GroupListInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.entity.MyContactEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 联系人群聊的分页
 * @author huangjk
 *
 */
public class GroupsFragment extends Fragment  implements Callback{
	
	private FrameLayout mContentView;
	
	private MainActivity activity;

	private LinearLayout ll_create_group;

	private LinearLayout ll_find_group;

	private RefreshListView rlv_group_list;
	
	private int page = 0;
	
	private Handler mHandler;

	private LinearLayout ll_empty;

	private LinearLayout ll_net_failed;

	private GroupAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = (MainActivity) getActivity();
		View view = inflater.inflate(R.layout.root_layout, null);
		mContentView = (FrameLayout) view.findViewById(R.id.mainView);
		View v = View.inflate(getActivity(), R.layout.fragment_groups, null);
		mContentView.addView(v);
		ll_create_group = (LinearLayout) v.findViewById(R.id.ll_create_group);
		ll_find_group = (LinearLayout) v.findViewById(R.id.ll_find_group);
		rlv_group_list = (RefreshListView) v.findViewById(R.id.rlv_group_list);
		ll_empty = (LinearLayout) v.findViewById(R.id.ll_empty);
		ll_net_failed = (LinearLayout) v.findViewById(R.id.ll_net_unavailable);
		adapter = new GroupAdapter(null, activity);
		rlv_group_list.setAdapter(adapter);
		addListener();
		getGroupList(page);
		return view;
	}
	/**
	 * 获取群组信息
	 */
	private void getGroupList(int page) {
		// TODO Auto-generated method stub
		if (NetWorkUtils.detect(getActivity())) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page+"");


			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.GET_GROUP_LIST, GroupListInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GROUP_LIST;
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

	private void addListener() {
		ll_create_group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				activity.showToast("ll_create_group");
				Intent i = new Intent(getActivity(),GroupCreateActivity.class);
				startActivity(i);
				
			}
		});
		ll_find_group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				activity.showToast("ll_find_group");
				Intent i = new Intent(getActivity(),GroupFindActivity.class);
				startActivity(i);
				
			}
		});
		rlv_group_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GroupListEntry item = (GroupListEntry) adapter.getItem(position);
				if(item!=null){
					Intent i = new Intent(activity, GroupDetailsActivity.class);
					i.putExtra("item", item);
					startActivity(i);
				}
				
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		activity.destroyDialog();
		switch (msg.what) {
		case IResult.GROUP_LIST:
			GroupListInfo info = (GroupListInfo) msg.obj;
			if(info.getSuccess()==1){
				List<GroupListEntry> data = info.getData();
				adapter.change(data);
			}else{
				activity.showToast(info.getErrormsg());
			}
			
			break;

		case IResult.NET_ERROR:
		
			activity.showToast(IMessage.NET_ERROR);
			ll_net_failed.setVisibility(View.VISIBLE);
			ll_empty.setVisibility(View.GONE);
			rlv_group_list.setVisibility(View.GONE);
			break;
		case IResult.DATA_ERROR:
			ll_net_failed.setVisibility(View.GONE);
			ll_empty.setVisibility(View.VISIBLE);
			rlv_group_list.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		return false;
	}
	class GroupAdapter extends BaseAdapter{
		
		private BasicActivity activity;
		private List<GroupListEntry> data;

		public GroupAdapter(List<GroupListEntry> data,BasicActivity activity ){
			if(data==null)
				data = new ArrayList<GroupListEntry>();
			this.data = data;
			this.activity = activity;
			
		}
		public void change(List<GroupListEntry> data){
			if(data==null)
				data = new ArrayList<GroupListEntry>();
			this.data = data;
			notifyDataSetChanged();
		}
		public void add(List<GroupListEntry> data){
			if(data==null)
				data = new ArrayList<GroupListEntry>();
			this.data.addAll(data);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(activity, R.layout.item_my_contact, null);
				holder.group_log = (CircleImageView) convertView.findViewById(R.id.civ_contact_log);
				holder.group_name = (TextView) convertView.findViewById(R.id.tv_contact_name);
				holder.tv_contact_src = (TextView) convertView.findViewById(R.id.tv_contact_src);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			GroupListEntry Entry = data.get(position);
			holder.group_name.setText(Entry.getGROUPNAME());
			holder.tv_contact_src.setText(Entry.getGROUPNUM());
			return convertView;
		}
	}
		
	
	
	class ViewHolder {
		public TextView tv_contact_src;
		public TextView group_name;
		CircleImageView group_log;
	}

}
