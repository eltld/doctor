package net.ememed.doctor2.activity;

import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.AppContext;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

public class SaveResumeActivity extends BasicActivity {
	private EditText et_public_str;
	private Button btn_addhealth;
	private TextView top_title;
	private String resumeStr;	//个人详情
	private String from;
	private String exist_str;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_save_skill);
		from = getIntent().getStringExtra("from");
		exist_str = getIntent().getStringExtra("exist_str");
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		TextView tv_hint = (TextView) findViewById(R.id.tv_hint);
		tv_hint.setText("请描述您的教育经历、从业经历、学术成就等情况，对提高知名度很有帮助！");
		super.setupView();
		et_public_str = (EditText) findViewById(R.id.et_public_str);
		et_public_str.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("个人详情");
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) SaveResumeActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0,
							InputMethodManager.HIDE_NOT_ALWAYS);
					et_public_str.setFocusable(true);
					et_public_str.setFocusableInTouchMode(true);
					et_public_str.requestFocus();
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!TextUtils.isEmpty(exist_str)) {
			et_public_str.setText(exist_str);
		}

	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			if (!TextUtils.isEmpty(from)&& from.equals(RegisterSuccessActivity.class.getSimpleName())) {
				goBack2PreActivity();
			} else {
				setResume();
			}
		} else {
		}
	}

	private void goBack2PreActivity() {
		resumeStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(resumeStr)) {
			showToast("不能为空");
			return;
		}

		Intent intent = new Intent(SaveResumeActivity.this,
				RegisterSuccessActivity.class);
		intent.putExtra("resumeStr", resumeStr);
		setResult(RESULT_OK, intent);
		finish();

	}

	private void setResume() {
		resumeStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(resumeStr)) {
			showToast("不能为空");
			return;
		}
		
		if(resumeStr.length() > 1500) {
			showToast("内容超出1500汉字长度限制");
			return;
		}
		
		if(NetWorkUtils.detect(SaveResumeActivity.this)){
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("resume", resumeStr);
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_doctor_resume, PersonInfo.class, params,
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

							showToast(error.getMessage());

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
//				Log.d("resume", map_2.getErrormsg());
				int success_2 = (Integer) map_2.getSuccess();
				if (success_2 == 0) {
					showToast((String) map_2.getErrormsg());
					return;
				} else {
					showToast(getString(R.string.service_setting_time_success));
					SharePrefUtil.putString(Conast.RESUME, resumeStr);
					SharePrefUtil.commit();
//					Log.d("resume", SharePrefUtil.getString(Conast.RESUME));
					Intent intent = new Intent(SaveResumeActivity.this,
							PersonInfoActivity.class);
					intent.putExtra("setting_type",
							SaveResumeActivity.class.getSimpleName());
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
