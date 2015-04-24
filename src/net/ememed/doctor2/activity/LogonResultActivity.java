package net.ememed.doctor2.activity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;

import de.greenrobot.event.util.IMManageTool;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.R.layout;
import net.ememed.doctor2.db.BankConfigTable;
import net.ememed.doctor2.db.ConfigTable;
import net.ememed.doctor2.db.PositionConfigTable;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.LoginEntry;
import net.ememed.doctor2.entity.LoginInfo;
import net.ememed.doctor2.entity.OrderInformation;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.interfac.EMLoginCallBack;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.MD5;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.util.UICore;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LogonResultActivity extends BasicActivity{
	private ImageView btn_back;
	private String account = null;
	private String pwd = null;
	IMManageTool manageTool;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_logon_result);
		account = getIntent().getStringExtra("account");
		pwd = getIntent().getStringExtra("pwd");
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.GONE);
	}
	
	public void doClick(View v) {
		switch (v.getId()) {
		case R.id.btn_add_information:
			Intent intent2 = new Intent(LogonResultActivity.this,RegisterSuccessActivity.class);
			//intent2.putExtra("need_load_info", false);
			intent2.putExtra("account", account);
			intent2.putExtra("pwd", pwd);
			startActivity(intent2);
			finish();
			break;
		case R.id.btn_start_use:
			Intent intent = new Intent(LogonResultActivity.this, MainActivity.class);
			intent.putExtra("is_new_register", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		/*	if ((NetWorkUtils.detect(LogonResultActivity.this))) {
				if(SharePrefUtil.getBoolean(Conast.LOGIN)){
					finish();
				}else{
					login();
				}
			} else {
				handler.sendEmptyMessage(IResult.NET_ERROR);
			}*/
			break;
			
		default:
			break;
		}
	}
	
	/*@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			
			case IResult.TEST:
				destroyDialog();
				Intent intent = new Intent(LogonResultActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}*/
	

}
