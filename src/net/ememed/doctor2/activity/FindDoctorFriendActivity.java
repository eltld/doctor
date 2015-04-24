package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.DoctorFriendEntry;
import net.ememed.doctor2.entity.DoctorFriendInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.SearchFriendEntry;
import net.ememed.doctor2.entity.SearchFriendInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class FindDoctorFriendActivity extends BasicActivity {
	private TextView top_title;
	private EditText et_search;
	private int page = 1;
	private List<SearchFriendInfo> searchList = null;
	// private DoctorAdapter adapter;
	private RefreshListView listView;
	private ImageButton btn_search;
	private String keyWord = null;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);

		setContentView(R.layout.activity_find_doctor_friend);
		searchList = new ArrayList<SearchFriendInfo>();
	}

	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("查找好友");

		// btn_search = (ImageButton) findViewById(R.id.btn_search);
		// btn_search.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// String tempKeyword = et_search.getText().toString().trim();
		// if(TextUtils.isEmpty(tempKeyword)) {
		// showToast("搜索关键字为空");
		// } else {
		// page = 1;
		// keyWord = et_search.getText().toString().trim();
		// searchDoctorFriend(keyWord);
		// }
		// }
		// });

		et_search = (EditText) findViewById(R.id.et_search);
		listView = (RefreshListView) findViewById(R.id.listView);
		// adapter = new DoctorAdapter(null);
		listView.setAdapter(mAdapter);

		getDefaultDoctorFriend();
	}

	@Override
	protected void addListener() {
		super.addListener();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO 进入其他医生的百科首页
				Intent intent = new Intent(FindDoctorFriendActivity.this, OtherBaikeActivity.class);
				SearchFriendInfo info = (SearchFriendInfo) parent.getAdapter().getItem(position);
				intent.putExtra("other_doctor_id", info.getMEMBERID());
				startActivity(intent);
			}
		});

		listView.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				page = 1;
				// searchDoctorFriend(keyWord);
				if (keyWord != null) { // 有输入关键字加载更多
					searchDoctorFriend(keyWord);
				} else { // 不输入关键字加载更多
					getDefaultDoctorFriend();
				}
			}
		});

		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				page++;
				if (keyWord != null) { // 有输入关键字加载更多
					searchDoctorFriend(keyWord);
				} else { // 不输入关键字加载更多
					getDefaultDoctorFriend();
				}
			}
		});

		// 输入法回车键变搜索
		/*
		 * et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		 * 
		 * @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		 * 
		 * if (actionId == EditorInfo.IME_ACTION_SEND || (event!=null && event.getKeyCode() ==
		 * KeyEvent.KEYCODE_ENTER && event.ACTION_DOWN == event.getAction())) { String tempKeyword =
		 * et_search.getText().toString().trim(); if(TextUtils.isEmpty(tempKeyword)) {
		 * showToast("搜索关键字为空"); } else { page = 1; keyWord = et_search.getText().toString().trim();
		 * searchDoctorFriend(keyWord); } return true; }
		 * 
		 * return false; } });
		 */
	}

	private void searchDoctorFriend(String keyword) {

		// if(TextUtils.isEmpty(keyword)){
		// showToast("请输入要查找的关键字");
		// return;
		// }

		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("keyword", keyword);
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.search_wait_contact,
					SearchFriendEntry.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SEARCH_DOCTOR_FRIEND;
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

	private void getDefaultDoctorFriend() {

		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", page + "");
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_wait_contact_member_list,
					SearchFriendEntry.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SEARCH_DOCTOR_FRIEND;
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

	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		try {
			destroyDialog();
			listView.onRefreshComplete();
			switch (msg.what) {
			case IResult.SEARCH_DOCTOR_FRIEND:
				SearchFriendEntry response = (SearchFriendEntry) msg.obj;
				if (null != response) {
					if (1 == response.getSuccess()) {
						List<SearchFriendInfo> info = response.getData();
						if (null != info && info.size() > 0) {
							if (page == 1)
								mAdapter.clear();
							mAdapter.addAll(info);
							listView.onLoadMoreComplete(page < response.getPages() ? false : true);
						} else {
							mAdapter.clear();
							listView.onLoadMoreComplete(true);
							showToast("未查找到您需要的内容");
						}
					} else {
						showToast(response.getErrormsg());
					}
				}
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("数据出错！");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {
		if (R.id.btn_back == view.getId()) {
			finish();
		} else if (R.id.ll_search == view.getId()) {
			String tempKeyword = et_search.getText().toString().trim();
			if (TextUtils.isEmpty(tempKeyword)) {
				showToast("搜索关键字不能为空");
			} else {
				page = 1;
				keyWord = et_search.getText().toString().trim();
				searchDoctorFriend(keyWord);
			}
		}

	}

	private QuickAdapter<SearchFriendInfo> mAdapter = new QuickAdapter<SearchFriendInfo>(this,
			R.layout.doctor_friend_item) {

		@Override
		protected void convert(BaseAdapterHelper helper, SearchFriendInfo item) {
			helper.setImageUrl(R.id.image_person, item.getAVATAR())
					.setText(R.id.tv_user_name, item.getREALNAME())
					.setText(
							R.id.tv_consult_info,
							item.getDOCTOR_INFO() == null ? "" : item.getDOCTOR_INFO()
									.getHOSPITALNAME());
		}
	};

	/**
	 * 联系人自定义
	 * 
	 * @author ASIMO
	 */
	private class DoctorAdapter extends BaseAdapter {
		List<SearchFriendInfo> listItems;

		public DoctorAdapter(List<SearchFriendInfo> listItems) {
			if (null == listItems) {
				this.listItems = new ArrayList<SearchFriendInfo>();
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
				convertView = LayoutInflater.from(FindDoctorFriendActivity.this).inflate(
						R.layout.doctor_friend_item, null);
				holder.image_person = (CircleImageView) convertView.findViewById(R.id.image_person);
				// holder.image_person.setImageResource(R.drawable.avatar_medium);
				holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
				holder.tv_consult_info = (TextView) convertView.findViewById(R.id.tv_consult_info);
				// holder.unreadLabel = (ImageView) convertView.findViewById(R.id.unreadLabel);
				// holder.tv_private_doctor = (TextView)
				// convertView.findViewById(R.id.tv_private_doctor);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			SearchFriendInfo info = (SearchFriendInfo) getItem(position);
			if (null != info) {
				holder.tv_user_name.setText(info.getREALNAME());
				imageLoader.displayImage(info.getAVATAR(), holder.image_person,
						Util.getOptions_pic());
				if (info.getHOSPITAL_INFO() != null) {
					if (!TextUtils.isEmpty(info.getHOSPITAL_INFO().getHOSPITALNAME())) {
						holder.tv_consult_info.setText(info.getHOSPITAL_INFO().getHOSPITALNAME());
					}
				}
			}
			return convertView;
		}

		public void change(List<SearchFriendInfo> lists) {
			if (lists == null) {
				lists = new ArrayList<SearchFriendInfo>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void addAll(List<SearchFriendInfo> list) {
			if (list == null)
				return;
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}

		public void clear() {
			listItems.clear();
			notifyDataSetChanged();
		}

		class ViewHolder {
			// public ImageView unreadLabel;
			CircleImageView image_person;
			TextView tv_user_name;
			TextView tv_consult_info;
			// TextView tv_private_doctor;
		}
	}

}
