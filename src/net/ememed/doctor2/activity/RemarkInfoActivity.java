package net.ememed.doctor2.activity;

import java.util.HashMap;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.RemarkInfoBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RemarkInfoActivity extends BasicActivity implements Callback{
	
	private TextView top_title;
	private Button btn_addhealth;
	private EditText set_name;
	private EditText set_describe;
	private LinearLayout ll_net_unavailable;
	private LinearLayout ll_remark;
	private LinearLayout ll_describe;
	
	private String tochat_userId;
	private String tochat_title;
	private String note_name="";
	private String description="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remark_info_set);
		tochat_userId=getIntent().getStringExtra("tochat_userId");
		tochat_title=getIntent().getStringExtra("tochat_title");
		note_name=getIntent().getStringExtra("note_name");
		description=getIntent().getStringExtra("description");
		initView();
	}
	
	private void initView(){
		top_title=(TextView) findViewById(R.id.top_title);
		top_title.setText(tochat_title);
		btn_addhealth=(Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setText("保存");
		btn_addhealth.setBackgroundResource(R.drawable.transparent_none);
		btn_addhealth.setVisibility(View.VISIBLE);
		set_name=(EditText) findViewById(R.id.remark_info_set_name);
		set_name.setText(note_name);
		if(note_name!=null){
			set_name.setSelection(note_name.length());
		}
		set_describe=(EditText) findViewById(R.id.remark_info_set_describe);
		set_describe.setText(description);
		if(description!=null){
			set_describe.setSelection(description.length());
		}
		ll_net_unavailable=(LinearLayout) findViewById(R.id.ll_net_unavailable);
		ll_remark=(LinearLayout) findViewById(R.id.ll_remark_name);
		ll_describe=(LinearLayout) findViewById(R.id.ll_describe);
		if(tochat_title.equals("设置备注")){
			ll_remark.setVisibility(View.VISIBLE);
			ll_describe.setVisibility(View.GONE);
		}else{
			ll_remark.setVisibility(View.GONE);
			ll_describe.setVisibility(View.VISIBLE);
		}
	}
	
	private void setData(){
		if(note_name==""|| note_name==null){
			note_name=set_name.getText().toString();
		}
		if(description=="" || description==null){
			description=set_describe.getText().toString();
		}
		
	}
	
	public void setRemarkInfo(){
		if(NetWorkUtils.detect(this)){
			HashMap<String , String> params=new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("userid", tochat_userId);
			params.put("note_name", set_name.getText().toString());
			params.put("description",set_describe.getText().toString());
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_patient_note, RemarkInfoBean.class, params, new Listener() {

				@Override
				public void onResponse(Object arg0) {
					// TODO Auto-generated method stub
					RemarkInfoBean rib=(RemarkInfoBean) arg0;
					if(rib.getSuccess().equals("1")){
						showToast(rib.getErrormsg());
						Intent intent = new Intent(ActionFilter.ALETER_NOTE_NAME);
						intent.putExtra("user_id", tochat_userId);
						intent.putExtra("note_name", set_name.getText().toString());
						sendBroadcast(intent);
						RemarkInfoActivity.this.finish();
					}else{
						showToast(rib.getErrormsg());
					}
				}
			},new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					Message msg=new Message();
					msg.obj=arg0;
					msg.what=IResult.DATA_ERROR;
					handler.sendMessage(msg);
				}
			});
		}else{
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case IResult.DATA_ERROR:
			
			break;
			
		case IResult.NET_ERROR:
			ll_remark.setVisibility(View.GONE);
			ll_net_unavailable.setVisibility(View.VISIBLE);
			break;
			
		default:
			break;
		}
		return false;
	}
	
	public void doClick(View v){
		if(v.getId()==R.id.btn_addhealth){
			System.out.println("note_name-------->"+note_name+"------->description"+description);
			setRemarkInfo();
		}else if(v.getId()==R.id.btn_back){
			finish();
		}
	}
}
