package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.TijianSettingEntry;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.widget.SwitchButton;

import org.json.JSONException;

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
 * 服务设置--上门会诊服务
 * 
 * @author chen
 * */
public class ServiceTijianSettingActivity extends BasicActivity {

	private SwitchButton bt_switch;
	private EditText et_price;
	private String title;
	private TijianSettingEntry setting_content = null;
	private Button btn_addhealth;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.service_tijian_setting);
		title = getIntent().getStringExtra("title");
		setting_content = (TijianSettingEntry) getIntent().getSerializableExtra("setting_content");
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
		if (setting_content.getEnable_tijian() == 1) {
			bt_switch.setChecked(true);
		} else {
			bt_switch.setChecked(false);
		}
		et_price = (EditText) findViewById(R.id.et_price);
		et_price.setText(setting_content.getPrice_tijian());
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
	}

	@Override
	protected void addListener() {
		super.addListener();
		bt_switch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					enableTijianSetting(1);
				} else {
					enableTijianSetting(0);
				}
			}
		});
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			Intent intent = new Intent(ServiceTijianSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_content);
			intent.putExtra("setting_type", ServiceTijianSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		} else if (view.getId() == R.id.btn_addhealth) {
			setTijianSetting();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ServiceTijianSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_content);
			intent.putExtra("setting_type", ServiceTijianSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 是否启用体检设置
	 * 0、关闭
	 * 1、启用
	 * */
	private void enableTijianSetting(final int operation){
//		new Thread() {
//
//			@Override
//			public void run() {
//				try {
//					clientContent.enableTijian(SharePrefUtil.getString("doctorId"), operation);
//				} catch (IOException e) {
//					handler.sendEmptyMessage(IResult.NET_ERROR);
//				} catch (Exception e) {
//					handler.sendEmptyMessage(IResult.DATA_ERROR);
//				}
//				super.run();
//			}
//		}.start();
	}
	
	private void setTijianSetting(){
		final String str_price = et_price.getText().toString();
		if (TextUtils.isEmpty(str_price)) {
			showToast(getString(R.string.service_setting_price));
			return;
		}
		if (TextUtil.isPriceIllegal(str_price)) {
			showToast(getString(R.string.service_setting_price_illegal));
			return;
		}
//		showProgressDialog(getString(R.string.progressdialog_sending));
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					clientContent.setTijian(SharePrefUtil.getString("doctorId"), str_price);
//				} catch (IOException e) {
//					handler.sendEmptyMessage(IResult.NET_ERROR);
//				} catch (Exception e) {
//					handler.sendEmptyMessage(IResult.DATA_ERROR);
//				}
//				super.run();
//			}
//		}.start();
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
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					int success = (Integer) map.get("success");
					if(success == 0){
						Toast.makeText(this, (CharSequence) map.get("errormsg"), Toast.LENGTH_SHORT).show();
						return;
					} else {
						setting_content.setEnable_tijian((Integer)map.get("enable"));
//						System.out.println("setting success ");
					}
					break;
				case IResult.SERVICE_SET:// 服务设置
					HashMap<String, Object> map_2 = (HashMap<String, Object>) msg.obj;
					int success_2 = (Integer) map_2.get("success");
					if(success_2 == 0){
						showToast((String) map_2.get("errormsg"));
						return;
					} else {
						showToast(getString(R.string.service_setting_time_success));
						setting_content.setPrice_tijian(et_price.getText().toString());
						Intent intent = new Intent(ServiceTijianSettingActivity.this,ServiceAllSettingActivity.class);
						intent.putExtra("setting_content", setting_content);
						intent.putExtra("setting_type", ServiceTijianSettingActivity.class.getSimpleName());
						setResult(RESULT_OK, intent);
						finish();
					}
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
