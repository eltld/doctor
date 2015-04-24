package net.ememed.doctor2.activity;

import java.util.HashMap;
import java.util.List;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.PatientGroupAddBean;
import net.ememed.doctor2.entity.PatientGroupBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PatientGroupAddActivity extends BasicActivity {
	
	private PatientGroupAddBean pab = null;
	private TextView top_title;
	private String title;
	private EditText setGroup_edit;
	private Button btn_addhealth;
	private String edit;
	private List<PatientGroupBean> list_group;
	private String groupid="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_group_add);
		title = getIntent().getStringExtra("title");
		groupid=getIntent().getStringExtra("groupid");
		initView();
	}

	private void initView() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(title);
		setGroup_edit=(EditText) findViewById(R.id.setGroup_edit);
		btn_addhealth=(Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundResource(android.R.color.transparent);
		btn_addhealth.setText("完成");
	}

	private void setGroupName(String editStr) {
		if (NetWorkUtils.detect(this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("groupname", editStr);
			params.put("groupid", groupid);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_patients_group_info,
					PatientGroupAddBean.class, params, new Listener() {

						@Override
						public void onResponse(Object arg0) {
							pab = (PatientGroupAddBean) arg0;
							showToast(pab.getErrormsg());
							if (pab.getSuccess().equals("1")) {
								setResult(RESULT_OK);
								finish();
							} 
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
						}
					});
		}
	}
	
	public void doClick(View v){
		if(v.getId()==R.id.btn_back){
			finish();
		}else if(v.getId()==R.id.btn_addhealth){
			String group_name=setGroup_edit.getText().toString();
			setGroupName(group_name);
		}
	}
}
