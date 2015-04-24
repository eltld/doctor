package net.ememed.doctor2.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.IDCardUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

public class SaveCertNumberActivity extends BasicActivity {
	private EditText et_public_str;
	private Button btn_addhealth;
	private TextView top_title;
	private String from;
	private String title;
	private CharSequence exist_str;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_save_name);
		from = getIntent().getStringExtra("from");
		title = getIntent().getStringExtra("title");
		exist_str = getIntent().getStringExtra("exist_str");
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		super.setupView();
		et_public_str = (EditText) findViewById(R.id.et_public_str);
		top_title = (TextView) findViewById(R.id.top_title);
		if (TextUtils.isEmpty(title)) {
			top_title.setText(getString(R.string.set_realcert_number));	
			et_public_str.setInputType(InputType.TYPE_CLASS_TEXT);
		} else {
			top_title.setText(title);
		}
		if (!TextUtils.isEmpty(exist_str)) {
			et_public_str.setText(exist_str);
		}
		if(null != title && title.equals(getString(R.string.set_id_num_title))) {
			et_public_str.setInputType(InputType.TYPE_CLASS_TEXT);
		}
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			if (!TextUtils.isEmpty(from) && from.equals(RegisterSuccessActivity.class.getSimpleName())) {
				goBack2PreActivity();
			} else {
				setCertNum();
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
		
		//处理是身份证号的情况
		if(null != title && title.equals(getString(R.string.set_id_num_title))) {
			String hint = IDCardUtil.IDCardValidate(certStr);
			if(!hint.equals("YES")) {
				showToast(hint);
				return;
			}
		}
		
		Intent intent = new Intent(SaveCertNumberActivity.this,RegisterSuccessActivity.class);
		intent.putExtra("cer_number", certStr);
		setResult(RESULT_OK, intent);
		finish();
		
	}

	private void setCertNum() {
		final String certStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(certStr)) {
			showToast("不能为空");
			return;
		}

		if(TextUtil.isNumber(certStr)){
			showToast("请输入数字");
			return;
		}
		
		//处理是身份证号的情况
		if(null != title && title.equals(getString(R.string.set_id_num_title))) {
			String hint = IDCardUtil.IDCardValidate(certStr);
			if(!hint.equals("YES")) {
				showToast(hint);
				return;
			}
		}
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("certificatenum", certStr);
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		MyApplication.volleyHttpClient.postWithParams(
				HttpUtil.set_doctor_certificatenum, PersonInfo.class, params,
				new Response.Listener() {
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
						message.what = IResult.NET_ERROR;
						handler.sendMessage(message);
					}
				});
	}

	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.PERSON_INFO:
				PersonInfo map_2 = (PersonInfo) msg.obj;
				int success_2 = (Integer) map_2.getSuccess();
				if (success_2 == 0) {
					showToast((String) map_2.getErrormsg());
					return;
				} else {
					showToast(getString(R.string.service_setting_time_success));
					String certStr = et_public_str.getText().toString();
					Intent intent = new Intent(SaveCertNumberActivity.this,
							PersonInfoActivity.class);
					intent.putExtra("certStr", certStr);
					intent.putExtra("setting_type",
							SaveCertNumberActivity.class.getSimpleName());
					setResult(RESULT_OK, intent);
					finish();
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {

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

