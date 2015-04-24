package net.ememed.doctor2.activity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.util.IMManageTool;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.baike.OtherBaikeActivity;
import net.ememed.doctor2.db.BankConfigTable;
import net.ememed.doctor2.db.ConfigTable;
import net.ememed.doctor2.db.NewsTypeTable;
import net.ememed.doctor2.db.PositionConfigTable;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.LoginEntry;
import net.ememed.doctor2.entity.LoginInfo;
import net.ememed.doctor2.entity.NewsTypeEntry;
import net.ememed.doctor2.entity.NewsTypeInfo;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.fragment.ConsultInfoFragment;
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
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

public class SplashActivity extends BasicActivity implements BasicUIEvent, EMLoginCallBack{
	private ImageView splash;
	boolean isFirstIn = false;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	private boolean isCover = true;
	IMManageTool manageTool;
	
	private String web_action, ext, other_doctorid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		splash = (ImageView) findViewById(R.id.splash);
		
		Intent i_getvalue = getIntent();  
		String action = i_getvalue.getAction();  
		  
		if(Intent.ACTION_VIEW.equals(action)){  
		    Uri uri = i_getvalue.getData();  
		    if(uri != null){  
		    	web_action = uri.getQueryParameter("action"); 
		    	ext = uri.getQueryParameter("ext");
		    	other_doctorid = uri.getQueryParameter("other_doctorid");
		    }  
		}
		
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
		isCover = SharePrefUtil.getMsgNotifyBoolean("isCover");
		if (isCover) {
//			SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);
//			Editor edit = preferences.edit();
//			edit.putBoolean("isFirstIn", true);
//			edit.commit();
			SharePrefUtil.putBoolean(Conast.LOGIN, false);
			SharePrefUtil.putBoolean(Conast.VALIDATED, false);
			SharePrefUtil.putBoolean(Conast.FLAG, false);

			SharePrefUtil.putString(Conast.BANK_ID, "");
			SharePrefUtil.putString(Conast.BANK_HOLDER, "");
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

			SharePrefUtil.putString(Conast.PIC_CERT_POSI, "");
			SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, "");

			SharePrefUtil.putString(Conast.SPECIALITY, "");
			SharePrefUtil.putString(Conast.HOSPITAL_NAME, "");
			SharePrefUtil.putString(Conast.ROOM_NAME, "");
			SharePrefUtil.putBoolean("isCover", false);
			SharePrefUtil.commit();
		}
		splash.setAnimation(anim);
		init();
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (SharePrefUtil.getBoolean("savepwd") && SharePrefUtil.getBoolean(Conast.LOGIN) && SharePrefUtil.getBoolean(Conast.VALIDATED)
						&& !TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_ID))
						&& !TextUtils.isEmpty(SharePrefUtil.getString("account"))
						&& !TextUtils.isEmpty(SharePrefUtil.getString("pwd"))) {
					JPushInterface.init(getApplicationContext());
					JPushInterface.resumePush(getApplicationContext());
					/*Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);*/
					
					if ((NetWorkUtils.detect(SplashActivity.this))) {
						login();
					} else {
						handler.sendEmptyMessage(IResult.NET_ERROR);
					}
				} else {
					if (!isFirstIn) {
						// 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
						startActivity(intent);
						finish();

					}
				}
				
				//XXX 测试完毕记得删掉
//				Intent intent = new Intent(SplashActivity.this,LogonResultActivity.class);
//				intent.putExtra("account", "18500000007");
//				intent.putExtra("pwd", "123456");
//				startActivity(intent);
//				finish();
				//XXX 
			}
		});
		
		UICore.eventTask(this, this, IResult.SYSN_CONFIG, null, null);// 加载数据入口，跳转到execute(int
		// obj)
		manageTool = IMManageTool.getInstance(getApplicationContext());
		manageTool.setEMLoginCallBack(this);
	}
	
	
	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		
		 if (mes == IResult.SYSN_CONFIG) {
				ConfigTable configTable = new ConfigTable();
				if (null == configTable.getDepartmentGroups()
						|| configTable.getDepartmentGroups().size() == 0) {
					sysn_config();
				}
			}
	}
	
	// 登录
	private void login() {
		loading("登录中...");
	
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", SharePrefUtil.getString("account"));
		params.put("strpwd", SharePrefUtil.getString("pwd"));
		params.put("imei", PublicUtil.getDeviceUuid(SplashActivity.this));
		params.put("appversion", PublicUtil.getVersionName(SplashActivity.this));
	
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.login,
				LoginInfo.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						// loginIM(response);
						Message message = new Message();
						message.obj = response;
						message.what = IResult.LOGIN;
						handler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
	
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.LOGIN_ERROR;
						handler.sendMessage(message);
					}
				});
	}
	
	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.LOGIN:
				LoginInfo login = (LoginInfo) msg.obj;
				if (null != login) {
					if (login.getSuccess() == 1) {
						LoginEntry doctor = login.getData();
						if (null != doctor) {
							Intent intent;
							if(web_action != null && web_action.equals("activity")){
								if(ext != null && ext.equals("baike_home")){
									if(other_doctorid != null){
										intent = new Intent(this, OtherBaikeActivity.class);
										intent.putExtra("other_doctor_id", other_doctorid);
										startActivity(intent);
										SplashActivity.this.finish();
										return;
									}
								}
							}
							
							saveDoctor(doctor);
							intent = new Intent(this,MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							SplashActivity.this.finish();
						}
					} else {
						if (TextUtils.isEmpty(login.getErrormsg())) {
							showToast("登录失败");
							destroyDialog();
						} else {
							Toast.makeText(this, login.getErrormsg(),
									Toast.LENGTH_SHORT).show();
							destroyDialog();
						}
						Intent intent = new Intent(SplashActivity.this, MainActivity.class); 
						startActivity(intent);
						SplashActivity.this.finish();
					}
				}
				break;
			case IResult.LOGIN_ERROR:
				destroyDialog();
				showToast("登录失败");
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
				break;
			case IResult.NET_ERROR:
				destroyDialog();
				showToast("网络异常");
				Intent intent2 = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent2);
				SplashActivity.this.finish();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	// 保存医生
	private void saveDoctor(LoginEntry doctor) {
		SharePrefUtil.saveDoctor(doctor);
		// 调用sdk登录方法登录聊天服务器 密码（id + 用户名 + ememedim）
		loginIM();
	}

	public void loginIM() {
		try {
			manageTool.loginIM(
					SharePrefUtil.getString(Conast.Doctor_ID),
					MD5.getMD5(SharePrefUtil.getString(Conast.Doctor_ID)
							+ SharePrefUtil.getString(Conast.MEMBER_NAME)
							+ "ememedim"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private void init() {
		// 读取SharedPreferences中需要的数据
		// 使用SharedPreferences来记录程序的使用次数
		SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
		isFirstIn = preferences.getBoolean("isFirstIn", true);
	}


	@Override
	public void onSuccess() {
		destroyDialog();
		handler.sendEmptyMessage(IResult.OK);
		System.out.println("Lonin Activity 登入环信成功");
	}


	@Override
	public void onProgress(int progress, String str) {
		// TODO Auto-generated method stub
	
	}


	@Override
	public void onError(int age1, String age2) {
		// TODO Auto-generated method stub
		destroyDialog();
		if (age1 == EMCallBack.ERROR_EXCEPTION_INVALID_PASSWORD_USERNAME) {
			reg_to_im();
		}else{
			System.out.println("环信登入异常："+age2);
		}
	}
	
	private void reg_to_im() {
		try {
			if (NetWorkUtils.detect(getApplicationContext())) {
				new Thread() {
					public void run() {
						ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("token",
								SharePrefUtil.getString(Conast.ACCESS_TOKEN)));
						params.add(new BasicNameValuePair("channel", "android"));
						params.add(new BasicNameValuePair("memberid",
								SharePrefUtil.getString(Conast.Doctor_ID)));
						params.add(new BasicNameValuePair("utype", "doctor"));
						try {
							String result = HttpUtil
									.getString(HttpUtil.URI
											+ HttpUtil.reg_to_im, params,
											HttpUtil.POST);
						} catch (IOException e) {
							e.printStackTrace();
						}
					};
				}.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sysn_config() {
		try {

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("Channel", "android"));// 新增
			String content = HttpUtil.getString(HttpUtil.URI
					+ HttpUtil.sync_config, params, HttpUtil.POST);
			content = TextUtil.substring(content, "{");
			if (content != null) {
				JSONObject obj = new JSONObject(content);
				JSONObject resultObj = obj.getJSONObject("data");

				JSONObject roomsObj = resultObj.getJSONObject("rooms");
				ConfigTable configTable = new ConfigTable();
				configTable.clearTable();
				Iterator arrays = roomsObj.keys();
				String keyName = "";
				String keyValue = "";
				while (arrays.hasNext()) {
					keyName = arrays.next().toString();
					Logger.dout("keyName:" + keyName);
					JSONArray roomArray = roomsObj.getJSONArray(keyName);
					for (int i = 0; i < roomArray.length(); i++) {
						Logger.dout("keyValue:" + roomArray.getString(i));
						keyValue = roomArray.getString(i);
						configTable.saveDepartment(keyName, keyValue);
					}
				}
				/***** 无耻的分割线 获取职称 ***/
				PositionConfigTable positionTable = new PositionConfigTable();
				positionTable.clearTable();
				JSONArray positionObj = resultObj.getJSONArray("professional");
				if (null != positionObj && positionObj.length() > 0) {
					for (int i = 0; i < positionObj.length(); i++) {
						positionTable.savePositionName(null,
								positionObj.getString(i));
					}
				}
				/***** 无耻的分割线 获取银行列表 ***/
				BankConfigTable banTable = new BankConfigTable();
				JSONArray banksObj = resultObj.getJSONArray("banks");
				banTable.clearTable();
				if (null != banksObj && banksObj.length() > 0) {
					for (int i = 0; i < banksObj.length(); i++) {
						banTable.savePositionName(banksObj.getString(i));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
