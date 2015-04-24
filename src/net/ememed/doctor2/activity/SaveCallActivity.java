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
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

public class SaveCallActivity extends BasicActivity {
	private EditText et_public_str;
	private Button btn_addhealth;
	private TextView top_title;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_save_name);
	}

	@Override
	protected void setupView() {
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		super.setupView();
		et_public_str = (EditText) findViewById(R.id.et_public_str);
		top_title = (TextView)findViewById(R.id.top_title);
		top_title.setText(getString(R.string.set_call));
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			setCall();
		} else {
		}
	}

	private void setCall(){
		String callStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(callStr)) {
			showToast("号码不能为空");
			return;
		}
		showToast(getString(R.string.service_setting_time_success));
		SharePrefUtil.putString("call", callStr);
		SharePrefUtil.commit();
		Intent intent = new Intent(SaveCallActivity.this,PersonInfoActivity.class);
		intent.putExtra("callStr", SharePrefUtil.getString(callStr));
		intent.putExtra("setting_type", SaveCallActivity.class.getSimpleName());
		setResult(RESULT_OK, intent);
		finish();
		
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
