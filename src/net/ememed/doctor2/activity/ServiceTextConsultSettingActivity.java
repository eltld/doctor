package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.entity.TextConsultSettingEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.widget.SwitchButton;

import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 服务设置--文字咨询
 * 
 * @author chen
 * */
public class ServiceTextConsultSettingActivity extends BasicActivity {

	private SwitchButton bt_switch;
	private EditText et_price;
	private String title = null; 
	private TextConsultSettingEntry setting_detail = null;
	private Button btn_addhealth;
	private PullToRefreshLayout mPullToRefreshLayout;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.service_textconsult_setting);
		title = getIntent().getStringExtra("title");
		setting_detail = (TextConsultSettingEntry) getIntent().getSerializableExtra("setting_content");
	}

	@Override
	protected void setupView() {
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(title);
		btn_addhealth = (Button)findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		super.setupView();
		bt_switch = (SwitchButton) findViewById(R.id.bt_switch);
		et_price = (EditText) findViewById(R.id.et_price);
			
		if (setting_detail.getEnable_textconsult() == 1) {
			bt_switch.setChecked(true);
		} else {
			bt_switch.setChecked(false);
		}
		
		et_price.setText(setting_detail.getPrice_textconsult());

		et_price.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable edt) {
				String temp = edt.toString();
				int posDot = temp.indexOf(".");
				if (posDot < 0)
					return;
				if (temp.length() - posDot - 1 > 2) {
					edt.delete(posDot + 3, posDot + 4);
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});	
		
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable().setup(mPullToRefreshLayout);
	}

	@Override
	protected void addListener() {
		super.addListener();
		bt_switch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					enableTextConsult(1);
				} else {
					enableTextConsult(0);
				}
			}
		});
	}

	private void setTextConsultSetting(){
		final String str_price = et_price.getText().toString();
		if (TextUtils.isEmpty(str_price)) {
			showToast(getString(R.string.service_setting_price));
			return;
		}
		if (TextUtil.isPriceIllegal(str_price)) {
			showToast(getString(R.string.service_setting_price_illegal));
			return;
		}
		
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString("doctorId"));
		params.put("price", str_price);

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_text_consult,
				ResultInfo.class, params, new Response.Listener() {
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
		
	}
	private int operation_result;
	/**是否开启文字咨询
	 * 1.开启
	 * 0.关闭
	 * */
	private void enableTextConsult(final int operation){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("enable", operation+"");

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_text_consult,
				ResultInfo.class, params, new Response.Listener() {

					@Override
					public void onResponse(Object response) {
						operation_result = operation;
						Message message = new Message();
						message.obj = response;
						message.what = IResult.FINISHED;
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
	
	public void doClick(View view) {
		
		int id = view.getId();
		if (id == R.id.btn_back) {
			Intent intent = new Intent(ServiceTextConsultSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_detail);
			intent.putExtra("setting_type", ServiceTextConsultSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		} else if (id == R.id.btn_addhealth) {
			setTextConsultSetting();
		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ServiceTextConsultSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_detail);
			intent.putExtra("setting_type", ServiceTextConsultSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {

				case IResult.FINISHED://设置开关
					destroyDialog();
					ResultInfo map = (ResultInfo) msg.obj;
					int success = map.getSuccess();
					if(success == 0){
						showToast(map.getErrormsg());
						return;
					} else {
//						System.out.println("setting success ");
						setting_detail.setEnable_textconsult(operation_result);
					}
					break;
				case IResult.SERVICE_SET:// 服务设置
					destroyDialog();
					ResultInfo map_2 = (ResultInfo) msg.obj;
					int success2 = map_2.getSuccess();
					if(success2 == 0){
						showToast(map_2.getErrormsg());
						return;
					} else {
						showToast(getString(R.string.service_setting_time_success));
						setting_detail.setPrice_textconsult(et_price.getText().toString());
						Intent intent = new Intent(ServiceTextConsultSettingActivity.this,ServiceAllSettingActivity.class);
						intent.putExtra("setting_content", setting_detail);
						intent.putExtra("setting_type", ServiceTextConsultSettingActivity.class.getSimpleName());
						setResult(RESULT_OK, intent);
						finish();
					}
					break;
				 case IResult.NET_ERROR:
					 destroyDialog();
					 showToast(IMessage.NET_ERROR);
					 break;
				default:
						break;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

}
