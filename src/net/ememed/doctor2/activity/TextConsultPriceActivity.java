package net.ememed.doctor2.activity;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TextConsultPriceActivity extends BasicActivity {
	private EditText et_setting_price_order;
	private Button btn_addhealth;
	private TextView top_title;
	
	private String text_price;
	private boolean finish = false;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_setting_price);
		text_price = getIntent().getStringExtra("text_price");
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		super.setupView();
		et_setting_price_order = (EditText) findViewById(R.id.et_setting_price_order);
		et_setting_price_order.setText(text_price);
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.service_setting_price_v2));

		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) TextConsultPriceActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0,
							InputMethodManager.HIDE_NOT_ALWAYS);
//					et_setting_price_order.setInputType(InputType.TYPE_CLASS_NUMBER); 
					et_setting_price_order.setFocusable(true);
//					et_setting_price_order.setFocusableInTouchMode(true);
					et_setting_price_order.requestFocus();
					et_setting_price_order.setSelection(et_setting_price_order.getText().length());
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			setTextConsultSetting();
		}
	}

	private void setTextConsultSetting() {
		final String str_price = et_setting_price_order.getText().toString();
		
		if (TextUtil.isPriceIllegal(str_price)) {
			showToast(getString(R.string.service_setting_price_illegal));
			return;
		}

		if(NetWorkUtils.detect(TextConsultPriceActivity.this)){
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString("doctorId"));
			params.put("price", str_price);

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_text_consult, ResultInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {

							Message message = new Message();
							message.obj = response;
							message.what = IResult.SERVICE_SET;
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

		}else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	@Override
	protected void onResult(Message msg) {
		destroyDialog();
		switch (msg.what) {
		case IResult.SERVICE_SET:// 服务设置
			ResultInfo resultInfo = (ResultInfo) msg.obj;
			if(resultInfo != null){
				if (resultInfo.getSuccess() == 1) {
					RegisterSuccessEvent event = new RegisterSuccessEvent();
					event.setTextConsultPrice(et_setting_price_order.getText().toString());
					EventBus.getDefault().postSticky(event);
					if (TextUtils.isEmpty(et_setting_price_order.getText().toString())) {
						enableTextConsult(0);
						showMessage(getString(R.string.service_setting_price));
						return;
					}
					finish();
				}else {
					showToast(resultInfo.getErrormsg());
				}
			}else {
				showToast("加载数据失败");
			}
			break;
		case IResult.DATA_ERROR:
			showToast(getString(R.string.text_get_data_none));
			break;
		case IResult.NET_ERROR:
			showToast(getString(R.string.net_error));
			break;
		default:
			break;
		}
		super.onResult(msg);
	}

	/** 信息提示 */
	public void showMessage(String msg) {
		Builder builder = new Builder(this);
		Dialog dialog = builder.setTitle(getString(R.string.system_info))
				.setMessage(msg)
				.setPositiveButton(getString(R.string.bt_ok), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton(getString(R.string.bt_cancel), null)
				.create();
		dialog.setCancelable(false);
		if (!finish ) {
			dialog.show();
		}
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
	
	@Override
	protected void onStop() {
		super.onStop();
		finish = true;
		EventBus.getDefault().removeStickyEvent(RegisterSuccessEvent.class);
	}
	
	
	/**
	 * 是否开启文字咨询 1.开启 0.关闭
	 * */
	private void enableTextConsult(final int operation) {
		
		if (NetWorkUtils.detect(TextConsultPriceActivity.this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("enable", operation + "");

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.enable_text_consult, ResultInfo.class, params,
					new Response.Listener() {

						@Override
						public void onResponse(Object response) {
							ResultInfo resultInfo = (ResultInfo) response;
							if (null != resultInfo) {
								if(resultInfo.getSuccess() != 1)
									showToast(resultInfo.getErrormsg());
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							showToast(IMessage.NET_ERROR);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
}
