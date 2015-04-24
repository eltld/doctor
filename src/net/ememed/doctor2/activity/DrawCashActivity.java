package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.BankCard;
import net.ememed.doctor2.entity.BankCardInfo;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.OrderListEntity;
import net.ememed.doctor2.entity.OrderListEntry;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.util.TimeUtil;
import net.ememed.doctor2.widget.RefreshListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonParseException;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/**关于提现*/
public class DrawCashActivity extends BasicActivity {
	private static final int REQUEST_SET_BANK = 1;
	private TextView top_title;
	private LinearLayout ll_add_card;
	private LinearLayout ll_card_info;
	private TextView tv_bank_name;
	private TextView tv_bank_end_card;
	private TextView tv_holder;
	private RefreshListView list_view;
	private CardAdapter adapter;
	private List<BankCardInfo> data;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_draw_cash);
	}

	@Override
	protected void getData() {
		// 判断银行卡本地是否存在
		if (SharePrefUtil.getBoolean(Conast.FLAG)) {
			ll_add_card.setVisibility(View.GONE);
			ll_card_info.setVisibility(View.VISIBLE);
			tv_bank_name.setText(SharePrefUtil.getString(Conast.BANK_NAME));
			if (null != SharePrefUtil.getString(Conast.BANK_NUMBER)
					&& SharePrefUtil.getString(Conast.BANK_NUMBER).length() >= 4) {
				tv_bank_end_card
						.setText("尾号"
								+ SharePrefUtil.getString(Conast.BANK_NUMBER)
										.substring(
												SharePrefUtil.getString(
														Conast.BANK_NUMBER)
														.length() - 4));
			}
			tv_holder.setText(TextUtil.replaceText2Pwd(SharePrefUtil
					.getString(Conast.BANK_HOLDER)));
		} else {
			getBankcard();
		}
		super.getData();
	}

	private void getBankcard() {
		if (NetWorkUtils.detect(DrawCashActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_bankcard, BankCard.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {

							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_BANKCARD;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = new Message();
							message.obj = error.getMessage();
							if (error.getCause() instanceof JsonParseException) {
								message.what = IResult.NO_CARD;
							} else {
								message.what = IResult.DATA_ERROR;
							}
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	@Override
	protected void setupView() {
		super.setupView();
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.about_money));
		ll_add_card = (LinearLayout) findViewById(R.id.ll_add_card);
		ll_card_info = (LinearLayout) findViewById(R.id.ll_card_info);
		// list_view = (RefreshListView)
		// findViewById(R.id.ll_card_info_listview);
		//
		// adapter = new CardAdapter(null);
		// // Set the List Adapter to display the sample items
		// list_view.setAdapter(adapter);
		// ll_card_info = (LinearLayout) findViewById(R.id.ll_card_info);
		tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
		tv_bank_end_card = (TextView) findViewById(R.id.tv_bank_end_card);
		tv_holder = (TextView) findViewById(R.id.tv_holder);
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.ll_add_card) {
			Intent intent = new Intent(this, BankCardActivity.class);
			startActivityForResult(intent, REQUEST_SET_BANK);
		} else if (view.getId() == R.id.ll_card_info) {
			Intent intent = new Intent(this, BankCardActivity.class);
			if (!SharePrefUtil.getBoolean(Conast.FLAG)) {
				intent.putExtra("data", data.get(data.size() - 1));
			}
			startActivityForResult(intent, REQUEST_SET_BANK);
		}
	}

	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
//			case IResult.GET_BANKCARD:
//				BankCard bankCard = (BankCard) msg.obj;
//				if (null != bankCard) {
//					if (bankCard.getSuccess() == 1) {
//						// if(refresh){
//						data = bankCard.getData();
//						if (null != data && data.size() > 0) {
//							ll_add_card.setVisibility(View.GONE);
//							ll_card_info.setVisibility(View.VISIBLE);
//							tv_bank_name.setText(data.get(data.size() - 1)
//									.getBANKNAME());
//							if (null != data.get(data.size() - 1)
//									.getBANKCARDNUM()) {
//								tv_bank_end_card
//										.setText("尾号"
//												+ data.get(data.size() - 1)
//														.getBANKCARDNUM()
//														.substring(
//																data.get(
//																		data.size() - 1)
//																		.getBANKCARDNUM()
//																		.length() - 4));
//							}
//							tv_holder.setText(TextUtil.replaceText2Pwd(data
//									.get(data.size() - 1).getHOLDER()));
//						} else {
//							ll_add_card.setVisibility(View.VISIBLE);
//						}
//
//					} else {
//						ll_add_card.setVisibility(View.VISIBLE);
//						showToast(bankCard.getErrormsg());
//						list_view.setVisibility(View.GONE);
//					}
//				} else {
//					showToast(IMessage.DATA_ERROR);
//					list_view.setVisibility(View.GONE);
//				}
//				break;
			case IResult.NO_CARD:
				ll_add_card.setVisibility(View.VISIBLE);
				break;
			case IResult.NET_ERROR:
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SET_BANK) {
			if (resultCode == RESULT_OK) {
				// getBankcard();
				ll_add_card.setVisibility(View.GONE);
				ll_card_info.setVisibility(View.VISIBLE);
				tv_bank_name.setText(SharePrefUtil.getString(Conast.BANK_NAME));
				if (null != SharePrefUtil.getString(Conast.BANK_NUMBER)) {
					tv_bank_end_card.setText("尾号"
							+ SharePrefUtil.getString(Conast.BANK_NUMBER)
									.substring(
											SharePrefUtil.getString(
													Conast.BANK_NUMBER)
													.length() - 4));
				}
				tv_holder.setText(TextUtil.replaceText2Pwd(SharePrefUtil
						.getString(Conast.BANK_HOLDER)));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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

	private class CardAdapter extends BaseAdapter {
		List<BankCardInfo> listItems;

		public CardAdapter(List<BankCardInfo> listItems) {
			if (listItems == null) {
				listItems = new ArrayList<BankCardInfo>();
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
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(DrawCashActivity.this)
						.inflate(R.layout.activity_card_list, null);
				holder.tv_bank_name = (TextView) convertView
						.findViewById(R.id.tv_bank_name);
				holder.tv_bank_end_card = (TextView) convertView
						.findViewById(R.id.tv_bank_end_card);
				holder.tv_holder = (TextView) convertView
						.findViewById(R.id.tv_holder);
				holder.ll_card_info = (LinearLayout) convertView
						.findViewById(R.id.ll_card_info);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BankCardInfo bankCardInfo = listItems.get(position);

			if (null != bankCardInfo) {
				ll_add_card.setVisibility(View.GONE);
				ll_card_info.setVisibility(View.VISIBLE);
				holder.tv_bank_name.setText(bankCardInfo.getBANKNAME());
				if (null != bankCardInfo.getBANKCARDNUM()) {
					holder.tv_bank_end_card
							.setText("尾号"
									+ bankCardInfo.getBANKCARDNUM().substring(
											bankCardInfo.getBANKCARDNUM()
													.length() - 4));
				}
				holder.tv_holder.setText(TextUtil.replaceText2Pwd(bankCardInfo
						.getHOLDER()));
			} else {
				ll_add_card.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		public void change(List<BankCardInfo> lists) {
			if (lists == null) {
				lists = new ArrayList<BankCardInfo>();
			}
			this.listItems = lists;
			notifyDataSetChanged();
		}

		public void add(List<BankCardInfo> list) {
			this.listItems.addAll(list);
			notifyDataSetChanged();
		}
	}

	class ViewHolder {
		public TextView tv_bank_name;
		public TextView tv_bank_end_card;
		public TextView tv_holder;
		public LinearLayout ll_card_info;
	}
}
