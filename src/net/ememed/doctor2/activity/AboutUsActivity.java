package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import cn.jpush.android.api.JPushInterface;
import com.easemob.chat.EMChatManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.LogonSuccessEvent;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.util.UICore;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author fxx
 *
 */

public class AboutUsActivity extends BasicActivity implements BasicUIEvent{
	private TextView top_title;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.about_us);
	}

	@Override
	protected void setupView() {
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.about_us));
		super.setupView();
		
		TextView tv_version = (TextView)findViewById(R.id.tv_version);
		tv_version.setText(getString(R.string.app_version)+PublicUtil.getVersionName(this));
		
	}

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		if (mes == IResult.LOGOUT) {
			
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("doctorid", SharePrefUtil.getString(Conast.Doctor_ID)));
			params.add(new BasicNameValuePair("channel", "android"));
			params.add(new BasicNameValuePair("imei", PublicUtil.getDeviceUuid(AboutUsActivity.this)));
			params.add(new BasicNameValuePair("appversion", PublicUtil.getVersionName(AboutUsActivity.this)));
			
			String content;    
			try {
				content = HttpUtil.getString(HttpUtil.URI + HttpUtil.logout,params, HttpUtil.POST);
				content = TextUtil.substring(content, "{");
				Gson gson = new Gson();
				PersonInfo reason = gson.fromJson(content, PersonInfo.class);
				Message msg = Message.obtain();
				msg.what = IResult.LOGOUT;
				msg.obj = reason;
				handler.sendMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
				Message message = new Message();
				message.obj = e.getMessage();
				message.what = IResult.DATA_ERROR;
				handler.sendMessage(message);
			}
		}
	}
	
	@Override
	protected void onResult(Message msg) {
		super.onResult(msg);
		try {
			switch (msg.what) {
			case IResult.LOGOUT:
				PersonInfo reason = (PersonInfo) msg.obj;
				Toast.makeText(this, reason.getErrormsg(), Toast.LENGTH_SHORT).show();
				if (reason.getSuccess() == 1) {
					//退出聊天服务器
					new Thread(){
						public void run() {
							if (null != EMChatManager.getInstance() && EMChatManager.getInstance().isConnected()) {
								EMChatManager.getInstance().logout();	
							}	
						};
					}.start();
					NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					nm.cancel(R.string.app_name);
					nm.cancel(R.drawable.ic_launcher);
					nm.cancelAll();
					Intent intent = new Intent(AboutUsActivity.this,LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					/*SharePrefUtil.putBoolean(Conast.LOGIN, false);
					SharePrefUtil.putBoolean(Conast.VALIDATED, false);
					SharePrefUtil.putBoolean(Conast.FLAG, false);

					SharePrefUtil.putString(Conast.BANK_ID, "");
			    	SharePrefUtil.putString(Conast.BANK_HOLDER,"");
			    	SharePrefUtil.putString(Conast.BANK_NAME, "");
			    	SharePrefUtil.putString(Conast.BANK_NUMBER, "");
			    	
			    	SharePrefUtil.putString(Conast.Doctor_ID, "");
			    	SharePrefUtil.putString(Conast.Doctor_Name, "");
			    	SharePrefUtil.putString(Conast.MEMBER_NAME, "");
			    	SharePrefUtil.putString(Conast.Doctor_Type, "");
			    	SharePrefUtil.putString(Conast.ACCESS_TOKEN, "");
		    		SharePrefUtil.putString(Conast.AVATAR, "");
		    		SharePrefUtil.putString(Conast.EMAIL_STR, "");
					SharePrefUtil.putString(Conast.SPECIALITY, "");
					SharePrefUtil.putString(Conast.MOBILE, "");
					
					SharePrefUtil.putString(Conast.TOTAL, "");
		    		SharePrefUtil.putString(Conast.AVAILABLE, "");
					SharePrefUtil.putString(Conast.FREEZE, "");
					SharePrefUtil.putString(Conast.AVAILABLE_NEW, "");
					
					SharePrefUtil.putString(Conast.PIC_CERT_POSI,"");
					SharePrefUtil.putString(Conast.PIC_CERT_OPPSI,"");
					
					SharePrefUtil.putString(Conast.SPECIALITY, "");
					SharePrefUtil.putString(Conast.HOSPITAL_NAME, "");
					SharePrefUtil.putString(Conast.ROOM_NAME, "");
					
					SharePrefUtil.commit();*/
					
					SharePrefUtil.cleanDoctor();
					startActivity(intent);
					
					EventBus.getDefault().postSticky(new LogonSuccessEvent());
					finish();
				}
				break;
			case IResult.DATA_ERROR:
				showToast("注销失败，请检查网络再重试");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.lt_share) {
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TITLE, getResources().getText(R.string.u_share_title));
			sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.u_share_content));
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
		} else if (view.getId() == R.id.lt_encourage) {
			Uri uri = Uri.parse("market://details?id=net.ememed.doctor2"); 
			Intent it = new Intent(Intent.ACTION_VIEW, uri); 
			startActivity(it); 
		} else if (view.getId() == R.id.bt_logout) {
			logout();
		} else if (view.getId() == R.id.lt_respon) {
			Intent intent = new Intent(AboutUsActivity.this,WebViewActivity.class);
			intent.putExtra("title", "免责声明");
			intent.putExtra("url",HttpUtil.NO_PRESS);
			startActivity(intent);
		} else if (view.getId() == R.id.lt_function_intro){
			Intent intent = new Intent(AboutUsActivity.this,IntroduceListActivity.class);
			startActivity(intent);
		} else if (view.getId() == R.id.lt_problem) {
			Intent intent = new Intent(AboutUsActivity.this,ProblemActivity.class);
			startActivity(intent);
		} else if(view.getId() == R.id.lt_feedback) {
			Intent intent = new Intent(AboutUsActivity.this,FeedbackActivity.class);
			startActivity(intent);
		}
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().removeStickyEvent(LogonSuccessEvent.class);
	}

	// 注销
	private void logout() {
		boolean login = SharePrefUtil.getBoolean(Conast.LOGIN);
		if (login) {
			Builder builder = new Builder(AboutUsActivity.this);
			builder.setTitle("提示").setMessage("您确定注销登录吗?")
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							JPushInterface.stopPush(getApplicationContext());
							UICore.eventTask(AboutUsActivity.this, AboutUsActivity.this, IResult.LOGOUT, "退出中...", null);

						}
					}).setNegativeButton("取消", null).show();
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	
}
