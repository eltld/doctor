package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.entity.ServiceSettingInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

public class SaveEmailActivity extends BasicActivity implements BasicUIEvent{
	private EditText et_public_str;
	private Button btn_addhealth;
	private String emailStr;
	private TextView top_title;
	private String exist_str;
	private String from;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_save_name);
		exist_str = getIntent().getStringExtra("exist_str");
		from = getIntent().getStringExtra("from");
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		super.setupView();
		et_public_str = (EditText) findViewById(R.id.et_public_str);
		et_public_str.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
		et_public_str.setInputType(InputType.TYPE_CLASS_TEXT);
		top_title = (TextView)findViewById(R.id.top_title);
		top_title.setText(getString(R.string.set_email_address));
		if (!TextUtils.isEmpty(exist_str)) {
			et_public_str.setText(exist_str);
		}
		//自动弹出软键盘
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) SaveEmailActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
					et_public_str.setFocusable(true);
					et_public_str.setFocusableInTouchMode(true);
					et_public_str.requestFocus();
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		et_public_str.setSelection(et_public_str.getText().length());
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			if (!TextUtils.isEmpty(from) && from.equals(RegisterSuccessActivity.class.getSimpleName())) {
				goBack2PreActivity();
			} else {
				setEmail();
			}	
			
		} else {
		}
	}
	
	private void goBack2PreActivity() {
		final String certStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(certStr)) {
			showToast("不能为空");
			return;
		}
		
		if (!TextUtil.isValidEmail(certStr)) {
			showToast("请正确输入email格式");
			return;
		}
		
		Intent intent = new Intent(SaveEmailActivity.this,RegisterSuccessActivity.class);
		intent.putExtra("cer_number", certStr);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void setEmail(){
		emailStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(emailStr)) {
			showToast("不能为空");
			return;
		}
		
		if (!TextUtil.isValidEmail(emailStr)) {
			showToast("请正确输入email格式");
			return;
		}
		if (NetWorkUtils.detect(SaveEmailActivity.this)){
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("email", emailStr);
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_doctor_email,
					PersonInfo.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							
							Message message = new Message();
							message.obj = response;
							message.what = IResult.PERSON_INFO;
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
		}else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.PERSON_INFO:
				destroyDialog();
				PersonInfo map_2 = (PersonInfo) msg.obj;
				int success_2 = map_2.getSuccess();
				if(success_2 == 0){
					showToast((String) map_2.getErrormsg());
					return;
				} else {
					showToast(getString(R.string.service_setting_time_success));
					Intent intent = new Intent(SaveEmailActivity.this,PersonInfoActivity.class);
					SharePrefUtil.putString(Conast.EMAIL_STR, emailStr);
					SharePrefUtil.commit();
					intent.putExtra("setting_type", SaveEmailActivity.class.getSimpleName());
					setResult(RESULT_OK, intent);
					finish();
				}
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
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
