package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.SearchGroupEntity;
import net.ememed.doctor2.entity.SearchGroupInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupSearchActivity extends BasicActivity {
	// private int REQUEST_TO_GROUP_DETAIL = 11;
	private int page = 1;
	private String key_word = "";

	private EditText et_search;
	private ImageView bt_search;
	private RefreshListView lv_group;
	private GroupAdapter groupAdapter;
	private List<SearchGroupInfo> list = new ArrayList<SearchGroupInfo>();

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_find_group);
	}

	@Override
	protected void setupView() {
		super.setupView();

		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("查找群");

		et_search = (EditText) findViewById(R.id.et_search);
		bt_search = (ImageView) findViewById(R.id.bt_search);
		lv_group = (RefreshListView) findViewById(R.id.lv_group);
		groupAdapter = new GroupAdapter();
		lv_group.setAdapter(groupAdapter);
	}

	@Override
	protected void addListener() {
		super.addListener();
		bt_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				key_word = et_search.getText().toString().trim();
				if (TextUtils.isEmpty(key_word)) {
					showToast("请输入群号或者名称");
					return;
				}
				refresh(key_word);
			}
		});

		lv_group.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				if (TextUtils.isEmpty(key_word)) {
					showToast("请输入群号或名称");
					return;
				}
				refresh(key_word);
			}
		});

		lv_group.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				if (TextUtils.isEmpty(key_word)) {
					showToast("请输入群号或名称");
					return;
				}
				loadMore(key_word);
			}
		});

	}

	private void refresh(String keyWord) {
		page = 1;
		getSearchGroupList(keyWord);
	}

	private void loadMore(String keyWord) {
		page += 1;
		getSearchGroupList(keyWord);
	}

	private void getSearchGroupList(String keyWord) {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("keyword", keyWord);
			params.put("page", String.valueOf(page));

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.search_group_list,
					SearchGroupEntity.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.SEARCH_GROUP;
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
		try {
			destroyDialog();
			lv_group.onRefreshComplete();

			switch (msg.what) {
			case IResult.SEARCH_GROUP:
				SearchGroupEntity entry = (SearchGroupEntity) msg.obj;
				if (!entry.isSuccess()) {
					showToast(entry.getErrormsg());
					return;
				}

				if (entry.getData() != null) {
					if (1 == page) {
						list = entry.getData();
					} else {
						list.addAll(entry.getData());
					}
					groupAdapter.notifyDataSetChanged();

					if (page >= entry.getPages()) {
						lv_group.onLoadMoreComplete(true);
					} else {
						lv_group.onLoadMoreComplete(false);
					}

					if (0 == entry.getData().size()) {
						showToast(entry.getErrormsg());
					}
				}
				break;
			case IResult.DATA_ERROR:
				showToast("数据异常");
				break;
			case IResult.NET_ERROR:
				showToast("网络异常");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			destroyDialog();
		}
		super.onResult(msg);
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	 * super.onActivityResult(requestCode, resultCode, data); if(REQUEST_TO_GROUP_DETAIL
	 * ==requestCode){ if(RESULT_OK == resultCode){ String group_num =
	 * data.getStringExtra("group_num"); for(int i = 0; i < list.size(); i++){
	 * if(group_num.equals(list.get(i).getGROUPNUM()) && "0".equals(list.get(i).getIN_GROUP())){
	 * list.get(i).setIN_GROUP("2"); groupAdapter.notifyDataSetChanged(); break; } } } } }
	 */

	class GroupAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(GroupSearchActivity.this).inflate(
						R.layout.item_group_find, null);
				holder.civ_photo = (CircleImageView) convertView.findViewById(R.id.civ_photo);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_group_id = (TextView) convertView.findViewById(R.id.tv_group_id);
				holder.tv_person_num = (TextView) convertView.findViewById(R.id.tv_person_num);
				holder.btn_add_group = (Button) convertView.findViewById(R.id.btn_add_group);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final SearchGroupInfo info = list.get(position);
			imageLoader.displayImage(info.getLOGO(), holder.civ_photo, Util.getOptions_avatar());
			holder.tv_name.setText(info.getGROUPNAME());
			holder.tv_group_id.setText("群号：" + info.getGROUPNUM());
			holder.tv_person_num.setText(info.getMEMBER_LIMIT() + "人");
			if ("1".equals(info.getIN_GROUP())) {
				holder.btn_add_group.setBackgroundResource(R.drawable.btn_gray_normal_shape);
				holder.btn_add_group.setEnabled(false);
				holder.btn_add_group.setText("已加入");
			} else if ("2".equals(info.getIN_GROUP())) {
				holder.btn_add_group.setBackgroundResource(R.drawable.btn_gray_normal_shape);
				holder.btn_add_group.setEnabled(false);
				holder.btn_add_group.setText("申请中");
			} else {
				holder.btn_add_group.setBackgroundResource(R.drawable.btn_green_selector_2);
				holder.btn_add_group.setEnabled(true);
				holder.btn_add_group.setText("申请加入");
			}

			holder.btn_add_group.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GroupDetailActivity.startAction(GroupSearchActivity.this, info.getGROUPNUM(),
							GroupDetailActivity.TYPE_APPLY);
				}
			});

			return convertView;
		}

		class ViewHolder {
			CircleImageView civ_photo;
			TextView tv_name;
			TextView tv_group_id;
			TextView tv_person_num;
			Button btn_add_group;
		}

	}
}
