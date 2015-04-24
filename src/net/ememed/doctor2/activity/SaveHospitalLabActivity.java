package net.ememed.doctor2.activity;

import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
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

public class SaveHospitalLabActivity extends BasicActivity {
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
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.set_hospital_laboratory));
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.btn_addhealth) {
			setHospitalLab();
		} else {
		}
	}

	private void setHospitalLab(){
		final String roomnameStr = et_public_str.getText().toString();
		if (TextUtils.isEmpty(roomnameStr)) {
			showToast("不能为空");
			return;
		}
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("roomname", SharePrefUtil.getString(roomnameStr));
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_doctor_roomname,
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
				if(success_2 == 0){
					showToast((String) map_2.getErrormsg());
					return;
				} else {
					showToast(getString(R.string.service_setting_time_success));
					String roomnameStr = et_public_str.getText().toString();
					Intent intent = new Intent(SaveHospitalLabActivity.this,PersonInfoActivity.class);
					intent.putExtra("roomnameStr", roomnameStr);
					intent.putExtra("setting_type", SaveHospitalLabActivity.class.getSimpleName());
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
