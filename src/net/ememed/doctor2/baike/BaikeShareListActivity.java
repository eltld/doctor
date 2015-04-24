package net.ememed.doctor2.baike;

import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.AboutUsActivity;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.ClaimListActivity;
import net.ememed.doctor2.activity.LoginActivity;
import net.ememed.doctor2.baike.entity.BaikeShare;
import net.ememed.doctor2.baike.entity.BaikeShareList;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.MenuDialog;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.chat.EMChatManager;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.LogonSuccessEvent;

public class BaikeShareListActivity extends BasicActivity implements OnItemClickListener {

	private static final String[] TITLE = { "", "我赞过的", "我评论的", "我分享的" };
	private RefreshListView mListView;
	private QuickAdapter<BaikeShare> mAdapter;
	private int mPage;
	private TextView tv_count;
	private static final String[] FORMAT_COUNT = { "", "我赞过的总共%s条", "我评论的总共%s条", "我分享的总共%s条" };
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baike_share);

		if (getIntent() != null)
			type = getIntent().getIntExtra("type", 0);

		((TextView) findViewById(R.id.tv_top_title)).setText(TITLE[type]);

		tv_count = (TextView) findViewById(R.id.tv_count);
		mListView = (RefreshListView) findViewById(R.id.listView);
		mAdapter = (type == 2) ? mAdapterComment : mAdapterShare;
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(new IOnRefreshListener() {

			public void OnRefresh() {
				refresh();
			}
		});
		mListView.setOnLoadMoreListener(new IOnLoadMoreListener() {

			public void OnLoadMore() {
				loadMore();
			}
		});
		mListView.setOnItemClickListener(this);

		refresh();
	}

	private final QuickAdapter<BaikeShare> mAdapterComment = new QuickAdapter<BaikeShare>(this,
			R.layout.list_item_comment) {

		@Override
		protected void convert(BaseAdapterHelper helper, final BaikeShare item) {
			helper.setText(R.id.tv_say_title, item.getSayTitle())
					.setText(R.id.tv_say_content, item.getSayContent())
					.setText(R.id.tv_say_name, item.getSayName())
					.setText(R.id.tv_say_time, item.getSayTime())
					.setText(R.id.tv_time, String.format("评论于 %s", item.getCommentTime()))
					.setText(R.id.tv_content, item.getCommentContent())
					.setText(R.id.tv_refer_name, item.getReferName2())
					.setText(R.id.tv_refer_name_say, item.getReferName())
					.setText(R.id.tv_refer_comment, item.getReferComment());
			helper.setVisible(R.id.tv_refer_name_say, item.existReferName()).setVisible(
					R.id.tv_refer_comment, item.existReferComment());
			helper.getView(R.id.iv_delete).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new MenuDialog.Builder(BaikeShareListActivity.this)
							.setTitle("确定删除此条评论吗？")
							.setPositiveButton(getString(R.string.confirm_yes),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											Message message = new Message();
											message.what = IResult.DELETE_COMMENT_INNER;
											message.obj = item.getCommentId();
											handler.sendMessage(message);
										}
									})
							.setNegativeButton(getString(R.string.confirm_no),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).show();
				}
			});
		}
	};

	private final QuickAdapter<BaikeShare> mAdapterShare = new QuickAdapter<BaikeShare>(this,
			R.layout.list_item_share) {

		@Override
		protected void convert(BaseAdapterHelper helper, BaikeShare item) {
			helper.setText(R.id.tv_say_title, item.getSayTitle())
					.setText(R.id.tv_say_content, item.getSayContent())
					.setText(R.id.tv_say_name, item.getSayName())
					.setText(R.id.tv_say_time, item.getSayTime())
					.setText(R.id.tv_time, item.getCommentTime());
			if (type == 1) {
				helper.setText(R.id.tv_content, "赞了一个~");
			} else if (type == 3) {
				if (item.getCommentContent().startsWith("分享百科首页至")) {
					helper.setTextHtml(R.id.tv_content, String.format(
							"<font color=#666666>分享百科首页至 </font>%s", item.getCommentContent()
									.replace("分享百科首页至", "")));
					helper.setVisible(R.id.tv_say_content, false);
				} else {
					helper.setTextHtml(R.id.tv_content, String.format(
							"<font color=#666666>分享至 </font>%s",
							item.getCommentContent().replace("分享至", "")));
					helper.setVisible(R.id.tv_say_content, true);
				}
			}
		}
	};

	private void requestShareList() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("page", String.valueOf(mPage));
		params.put("type", String.valueOf(type));// 1我赞过的，2我评论过的，3我分享过的
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.JOIN_LIST, BaikeShareList.class,
				params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.RESULT;
						handler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.NET_ERROR;
						handler.sendMessage(message);
					}
				});
	}

	private void delelteComment(String commentid) {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("commentid", commentid);
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.delete_comment,
				CommonResponseEntity.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.DELETE_COMMENT;
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

	@Override
	protected void onResult(Message msg) {
		try {
			mListView.onRefreshComplete();
			switch (msg.what) {
			case IResult.RESULT:
				destroyDialog();
				BaikeShareList dateList = (BaikeShareList) msg.obj;
				tv_count.setText(String.format(FORMAT_COUNT[type], dateList.getCount()));
				if (!dateList.isSuccess())
					return;
				if (mPage == 1)
					mAdapter.clear();
				mAdapter.addAll(dateList.getList());
				mListView.onLoadMoreComplete(mPage < dateList.getPages() ? false : true);
				break;
			case IResult.DELETE_COMMENT_INNER:
				delelteComment(String.valueOf(msg.obj));
				break;
			case IResult.DELETE_COMMENT:
				destroyDialog();
				CommonResponseEntity response4 = (CommonResponseEntity) msg.obj;
				if (null != response4) {
					if (1 == response4.getSuccess()) {
						showToast("删除评论成功");
						setResult(RESULT_OK);
						refresh();
					} else {
						showToast(response4.getErrormsg());
					}
				}
				break;
			case IResult.END:

				break;
			case IResult.NET_ERROR:
				destroyDialog();
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	public void refresh() {
		mPage = 1;
		requestShareList();
	}

	public void loadMore() {
		mPage++;
		requestShareList();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = null;
		BaikeShare info = mAdapter.getItem(position - 1);
		if (3 == type && info.getCommentContent().startsWith("分享百科首页至")) {
			intent = new Intent(this, OtherBaikeActivity.class);
			intent.putExtra("other_doctor_id", info.getAuthorId());
		} else {
			intent = new Intent(this, SayDetailsActivity.class);
			intent.putExtra("says_id", info.getSayId());
			intent.putExtra("name", info.getSayName());
			boolean flag = info.getAuthorId().equals(SharePrefUtil.getString(Conast.Doctor_ID)) ? true
					: false;
			intent.putExtra("is_my_baike", flag);
		}
		startActivity(intent);
	}

	@Override
	public void onStop() {
		super.onStop();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.JOIN_LIST);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.delete_comment);
	}
}
