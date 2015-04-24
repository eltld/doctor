package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.FinaceDetails;
import net.ememed.doctor2.entity.FinaceEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.entity.RecentinEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.RefreshListView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**财务详情*/
public class FinaceDetailsActivity extends BasicActivity implements
		OnRefreshListener {
	private TextView top_title;
	private LinearLayout lv_contact_class;
	private PullToRefreshLayout mPullToRefreshLayout;

//	private TextView tv_total;
//	private TextView tv_available;
//	private TextView tv_freeze;
	private TextView tv_available_now;

	private TextView tv_user_name;
	private TextView tv_consult_info;
	private FinaceDetails fd;
	private TextView tv_finance_income_title;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_finace_details);
	}

	@Override
	protected void setupView() {
		super.setupView();

		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.finance_details));

//		tv_total = (TextView) findViewById(R.id.tv_total);
//		tv_available = (TextView) findViewById(R.id.tv_available);
//		tv_freeze = (TextView) findViewById(R.id.tv_freeze);
		tv_available_now = (TextView) findViewById(R.id.tv_available_now);

//		tv_total.setText(SharePrefUtil.getString("total"));
//		tv_available.setText(SharePrefUtil.getString("available"));
//		tv_freeze.setText(SharePrefUtil.getString("freeze"));
		tv_available_now.setText(SharePrefUtil.getString("available_now"));

		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		lv_contact_class = (LinearLayout) findViewById(R.id.ll_finace_content);
	}

	@Override
	protected void getData() {
		getDoctorAccount();
		super.getData();
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.btn_money) {
			if(null == fd|| null == fd.getData() || fd.getData().equals("")) {
				showToast("网络加载未完成，请稍等");
				return;
			}
			Intent intent = new Intent(FinaceDetailsActivity.this,
					DrawCashActivity.class);
			startActivity(intent);
		}

		// else if (view.getId() == R.id.btn_tijiao){
		// Intent intent = new
		// Intent(FinaceDetailsActivity.this,MoneyApplyActivity.class);
		// startActivity(intent);
		// }
	}

	@Override
	public void onRefreshStarted(View view) {
		mPullToRefreshLayout.setRefreshComplete();
		getDoctorAccount();
	}

	private void getDoctorAccount() {
		if (NetWorkUtils.detect(FinaceDetailsActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_account, FinaceDetails.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.DOCTOR_ACCOUNT;
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
			switch (msg.what) {
			case IResult.DOCTOR_ACCOUNT:
				destroyDialog();
				fd = (FinaceDetails) msg.obj;
				int success_2 = fd.getSuccess();
				if (success_2 == 0) {
					showToast((String) fd.getErrormsg());
					return;
				} else {
					FinaceEntry data = fd.getData();
					if (null != data) {
//						tv_total.setText("￥" + data.getTotal());
//						tv_available.setText("可用金额" + data.getAvailable());
//						tv_freeze.setText("冻结金额" + data.getFreeze());
						tv_available_now.setText("￥" + data.getAvailable());
						SaveFiance(data);
						setupRecentinViews(data.getRecentincome());
						setupMoreViews();
					}

				}
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
				destroyDialog();
				break;
			case IResult.DATA_ERROR:
				// ll_net_failed.setVisibility(View.GONE);
				// ll_empty.setVisibility(View.VISIBLE);
				// listview.setVisibility(View.GONE);
				destroyDialog();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	private void SaveFiance(FinaceEntry data) {
		SharePrefUtil.putString(Conast.TOTAL, "￥" + data.getTotal());
		SharePrefUtil.putString(Conast.AVAILABLE, "可用金额:" + data.getAvailable());
		SharePrefUtil.putString(Conast.FREEZE, "冻结金额:" + data.getFreeze());
		SharePrefUtil.putString(Conast.AVAILABLE_NEW, "￥" + data.getAvailable());
		SharePrefUtil.commit();
	}

	/**
	 * 更多
	 */
	private void setupMoreViews() {
		RelativeLayout child_footer = (RelativeLayout) LayoutInflater
				.from(this).inflate(R.layout.finace_income_item_footer, null);
		lv_contact_class.addView(child_footer);
		child_footer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FinaceDetailsActivity.this,
						FinaceListActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setupRecentinViews(List<RecentinEntry> list) {
		lv_contact_class.removeAllViews();
		RelativeLayout child_header = (RelativeLayout) LayoutInflater
				.from(this).inflate(R.layout.finace_income_item_header, null);
		lv_contact_class.addView(child_header);
		for (int i = 0; i < list.size(); i++) {
			RelativeLayout child_finace = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.finace_recent_income, null);
			ImageView iv_image = (ImageView) child_finace.findViewById(R.id.iv_image);
			tv_consult_info = (TextView) child_finace.findViewById(R.id.tv_consult_info);
			tv_user_name = (TextView) child_finace.findViewById(R.id.tv_user_name);
			tv_finance_income_title = (TextView) child_finace.findViewById(R.id.tv_finance_income_title);
			iv_image.setImageResource(R.drawable.icon_finace_details);
//			if (list.get(i).getORDERTYPE().equals("1")) {
//				iv_image.setImageResource(R.drawable.service_setting_text_consult);
//			} else if (list.get(i).getORDERTYPE().equals("2")) {
//				iv_image.setImageResource(R.drawable.service_setting_phone_consult);
//			} else if(list.get(i).getORDERTYPE().equals("3")){
//				iv_image.setImageResource(R.drawable.service_setting_jiuyi);
//			} else if(list.get(i).getORDERTYPE().equals("4")){
//				iv_image.setImageResource(R.drawable.service_setting_shangmen);
//			}else if(list.get(i).getORDERTYPE().equals("14")){
//				iv_image.setImageResource(R.drawable.service_setting_zhuyuan);
//			}else if(list.get(i).getORDERTYPE().equals("15")){
//				iv_image.setImageResource(R.drawable.service_setting_phone_consult);
//			}else if(list.get(i).getORDERTYPE().equals("16")){
//				iv_image.setImageResource(R.drawable.service_setting_custom);
//			}
			tv_user_name.setText((String) list.get(i).getCREATETIME());
			tv_finance_income_title.setText(list.get(i).getORDERTYPE_NAME());
			tv_consult_info.setText("￥" + list.get(i).getMONEY());
			lv_contact_class.addView(child_finace);
		}
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_doctor_account);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private class FinaceAdapter extends BaseAdapter {
		List<Map<String, Object>> listItems;

		public FinaceAdapter(List<Map<String, Object>> listItems) {
			this.listItems = listItems;
		}

		@Override
		public int getCount() {
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
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
				convertView = LayoutInflater.from(FinaceDetailsActivity.this)
						.inflate(R.layout.finace_income_item, null);
				holder.iv_image = (ImageView) convertView
						.findViewById(R.id.iv_image);
				holder.tv_consult_info = (TextView) convertView
						.findViewById(R.id.tv_consult_info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 1) {
				holder.iv_image
						.setImageResource(R.drawable.ic_service_setting_jiahao);
			} else if (position == 0) {
				holder.iv_image
						.setImageResource(R.drawable.ic_service_setting_phone);
			} else {
				holder.iv_image
						.setImageResource(R.drawable.ic_service_setting_textconsult);
			}

			holder.tv_consult_info.setText((String) listItems.get(position)
					.get("info"));

			return convertView;
		}
	}

	class ViewHolder {
		ImageView iv_image;
		TextView tv_consult_info;
	}
}
