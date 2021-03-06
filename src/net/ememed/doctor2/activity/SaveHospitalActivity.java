package net.ememed.doctor2.activity;

import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class SaveHospitalActivity extends BasicActivity {
	private EditText et_public_str;
	private Button btn_addhealth;
	private TextView top_title;
	private String from;
	private String exist_str;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_save_name);
		from = getIntent().getStringExtra("from");
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
		et_public_str.setInputType(InputType.TYPE_CLASS_TEXT);
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.set_hospital));
		if (!TextUtils.isEmpty(exist_str)) {
			et_public_str.setText(exist_str);
		}
		
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) SaveHospitalActivity.this
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
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			if (!TextUtils.isEmpty(from) && from.equals(RegisterSuccessActivity.class.getSimpleName())) {
				goBack2PreAct();
			} else {
				setHospitalname();	
			}
		} else {
		}
	}

	private void goBack2PreAct() {
		final String hospitalnameStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(hospitalnameStr)) {
			showToast("不能为空");
			return;
		}
		Intent intent = new Intent(SaveHospitalActivity.this,RegisterSuccessActivity.class);
		intent.putExtra("hospital", hospitalnameStr);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void setHospitalname() {
		final String hospitalnameStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(hospitalnameStr)) {
			showToast("不能为空");
			return;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("hospitalname", hospitalnameStr);
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		MyApplication.volleyHttpClient.postWithParams(
				HttpUtil.set_doctor_hospitalname, PersonInfo.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {

						// showToast(getString(R.string.service_setting_time_success));
						// Intent intent = new
						// Intent(SaveHospitalActivity.this,PersonInfoActivity.class);
						// intent.putExtra("hospitalnameStr", hospitalnameStr);
						// intent.putExtra("setting_type",
						// SaveHospitalActivity.class.getSimpleName());
						// setResult(RESULT_OK, intent);
						// finish();
						
						SharePrefUtil.putString(Conast.HOSPITAL_NAME, et_public_str.getText().toString());
						SharePrefUtil.commit();
						
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
					String hospitalnameStr = et_public_str.getText().toString();
					Intent intent = new Intent(SaveHospitalActivity.this,
							PersonInfoActivity.class);
					intent.putExtra("hospitalnameStr", hospitalnameStr);
					intent.putExtra("setting_type",
							SaveHospitalActivity.class.getSimpleName());
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
