package net.ememed.doctor2.message_center;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.DoctorChatActivity;
import net.ememed.doctor2.activity.OrderClassifyActivity;
import net.ememed.doctor2.activity.OrderDetailActivity;
import net.ememed.doctor2.activity.RegisterSuccessActivity;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.entity.SetBankCard;
import net.ememed.doctor2.finace.AddBankcardActivity;
import net.ememed.doctor2.finace.AddZhifubaoActivity;
import net.ememed.doctor2.finace.BankCardInfoCommon;
import net.ememed.doctor2.finace.BankcardListActivity;
import net.ememed.doctor2.message_center.adapter.MessageListAdapter;
import net.ememed.doctor2.message_center.entity.SmsClassifyEntity;
import net.ememed.doctor2.message_center.entity.SmsClassifyList;
import net.ememed.doctor2.message_center.entity.SmsDetail;
import net.ememed.doctor2.message_center.entity.SmsInfo;
import net.ememed.doctor2.message_center.entity.SmsListEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;

public class MessageListActivity extends BasicActivity {

	private static final String TITLE = "消息中心";
	private RefreshListView mListView;
	private QuickAdapter<SmsDetail> mAdapter;
	private String sms_type;
	private int page = 1;
	private AlertDialog myDialog;
	private LinearLayout ll_empty;
	private LinearLayout ll_net_unavailable;
	private int mDeletePosition;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_message_list);
		sms_type = getIntent().getStringExtra("sms_type");
	}

	@Override
	protected void setupView() {
		super.setupView();
		((TextView) findViewById(R.id.top_title)).setText(TITLE);

		ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
		ll_net_unavailable = (LinearLayout) findViewById(R.id.ll_net_unavailable);

		mAdapter = new QuickAdapter<SmsDetail>(this, R.layout.list_item_message) {

			@Override
			protected void convert(BaseAdapterHelper helper, SmsDetail item) {
				helper.setText(R.id.tv_title, String.format("【%s】", item.getSMS_TYPE_NAME()))
						.setText(R.id.tv_time, item.getADDTIME())
						.setText(R.id.tv_content, item.getCONTEXT());
			}
		};
		mListView = (RefreshListView) findViewById(R.id.sms_listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnLoadMoreListener(new IOnLoadMoreListener() {

			@Override
			public void OnLoadMore() {
				loadMore();
			}
		});

		mListView.setOnRefreshListener(new IOnRefreshListener() {

			@Override
			public void OnRefresh() {
				refresh();
			}
		});
		getMessageList(page); // 获取消息列表
	}

	@Override
	protected void addListener() {
		super.addListener();
		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final int pos = position - 1;
				showAlertDialogue(pos);
				return true;
			}
		});
	}

	/**
	 * 初始化删除信息的提示框
	 */
	private void showAlertDialogue(final int position) {
		if (null == myDialog) {
			myDialog = new AlertDialog.Builder(this).create();
			View view = LayoutInflater.from(this).inflate(R.layout.message_list_alert_dialog, null);

			LayoutParams lps = view.getLayoutParams();

			if (lps == null) {
				lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			lps.width = MyApplication.getInstance().canvasWidth * 3 / 4;
			lps.height = LayoutParams.WRAP_CONTENT;
			view.setLayoutParams(lps);

			myDialog.setCanceledOnTouchOutside(true);
			Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
			Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
			btn_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					myDialog.dismiss();
				}
			});
			btn_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteMessage(mAdapter.getItem(position).getMSGID());
					myDialog.dismiss();
				}
			});
			myDialog.setView(view);
		}

		myDialog.show();
	}

	/*
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	 * {
	 * 
	 * menu.clear(); menu.setHeaderTitle("删除短信"); menu.add(0, 1, 0, "删除");
	 * 
	 * super.onCreateContextMenu(menu, v, menuInfo); }
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) {
	 * 
	 * ContextMenuInfo menuInfo = (ContextMenuInfo) item.getMenuInfo();
	 * AdapterView.AdapterContextMenuInfo info =
	 * (AdapterView.AdapterContextMenuInfo)item.getMenuInfo(); int id =
	 * (int)info.id;//这里的info.id对应的就是数据库中_id的值
	 * 
	 * switch(item.getItemId()) { case 0: Log.i("测试","aaa"); break; case 1: Log.i("测试","bbb");
	 * break; } return super.onContextItemSelected(item); }
	 */

	private void loadMore() {
		page++;
		getMessageList(page);
	}

	private void refresh() {
		page = 1;
		getMessageList(page);
	}

	private void getMessageList(int page) {
		if (NetWorkUtils.detect(MessageListActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("type", "0");
			params.put("sms_type", sms_type);
			params.put("page", "" + page);
			params.put("utype", "doctor");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_msg_pool_list,
					SmsListEntry.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_MESSGE_LIST;
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

	private void deleteMessage(String msgid) {
		if (NetWorkUtils.detect(MessageListActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("msgid", msgid);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.delete_msg,
					CommonResponseEntity.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DELETE_MESSAGE;
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
			switch (msg.what) {
			case IResult.GET_MESSGE_LIST:
				mListView.onRefreshComplete();
				SmsListEntry response = (SmsListEntry) msg.obj;
				if (response == null) {
					showToast(IMessage.DATA_ERROR);
					return;
				}
				if (!response.isSuccess()) {
					showToast(response.getErrormsg());
					return;
				}

				if (page == 1)
					mAdapter.clear();
				mAdapter.addAll(response.getData());
				showEmptyView();
				break;
			case IResult.DELETE_MESSAGE:
				CommonResponseEntity response1 = (CommonResponseEntity) msg.obj;
				if (null != response1) {
					if (response1.getSuccess() == 1) {
						showToast("删除消息成功");
						mAdapter.remove(mDeletePosition);
						showEmptyView();
					} else {
						showToast(response1.getErrormsg());
					}
				} else {
					showToast(IMessage.DATA_ERROR);
				}
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				ll_net_unavailable.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
				break;
			case IResult.DATA_ERROR:
				showToast("获取数据出错！");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showEmptyView() {
		boolean flag = mAdapter.getCount() <= 0;
		int SHOW = flag == true ? View.VISIBLE : View.GONE;
		int HIDE = flag == false ? View.VISIBLE : View.GONE;
		ll_empty.setVisibility(SHOW);
		mListView.setVisibility(HIDE);
	}

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.ll_net_unavailable:
			refresh();
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_msg_pool_list);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.delete_msg);
	}
}
