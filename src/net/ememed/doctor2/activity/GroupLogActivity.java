package net.ememed.doctor2.activity;

import java.security.acl.Group;
import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.GroupSucceeEvent;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.CreateGroupInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PhoneVerifyInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GroupLogActivity extends BasicActivity {
	
	private Button btn_addhealth;
	private Bundle groupInfo;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_upload_log);
		groupInfo = getIntent().getExtras();
		EventBus.getDefault().register(this,GroupSucceeEvent.class);
		
	}
	/**
	 * 创建群组后调用
	 * @param event
	 */
	public void onEvent(GroupSucceeEvent event) {
		finish();
	}
	
	@Override
	protected void setupView() {
		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("上传群头像");
		btn_addhealth = (Button) findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setText("下一步");
		
		
	}
	
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.btn_addhealth:
			Intent i = new Intent(this, GroupDetailsActivity.class);
			startActivity(i);
//			GroupCreate();
			break;
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 上传群LOG；
	 * 进去创建群组成功后走下一步完善资料
	 */
	private void GroupCreate() {
//		Intent i = new Intent(this,UploadLogActivity.class);
//		i.putExtras(groupInfo);
//		startActivity(i);
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
//			params.put("groupname", groupInfo.getString("group_name"));
			params.put("groupname", "");
			params.put("desc", "");
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("public", "1");
			params.put("approval", "0");
			params.put("member_limit", "50");
			params.put("tags", "");
			params.put("logo", "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.REGISTER_GROUP,
					CreateGroupInfo.class, params, new Response.Listener() {
						@Override
						public void onResponse(Object response) {

							Message message = new Message();
							message.obj = response;
							message.what = IResult.CREATE_GROUP_SUCCEE;
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
		}
	}
	
	@Override
	protected void onResult(Message msg) {
		destroyDialog();
		switch (msg.what) {
		case IResult.CREATE_GROUP_SUCCEE:
			CreateGroupInfo info = (CreateGroupInfo) msg.obj;
			if(info.getSuccess()==1){
				Intent i = new Intent(this, GroupSucceeActivity.class);
				i.putExtra("CreateGroupEntry", info.getData());
				startActivity(i);
			}else{
				showToast(info.getErrormsg());
			}

			break;
		case IResult.DATA_ERROR:
			Toast.makeText(this, "获取失败，请检查网络", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
