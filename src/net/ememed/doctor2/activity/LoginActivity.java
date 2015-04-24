package net.ememed.doctor2.activity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.db.BankConfigTable;
import net.ememed.doctor2.db.ConfigTable;
import net.ememed.doctor2.db.PositionConfigTable;
import net.ememed.doctor2.entity.ChatUser;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.LoginEntry;
import net.ememed.doctor2.entity.LoginInfo;
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

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.util.HanziToPinyin;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.util.IMManageTool;

/***
 * 登录薏米医生完 要注册im服务器 调用环信登录接口
 * 
 * @author chen
 */
public class LoginActivity extends BasicActivity implements BasicUIEvent,
		EMLoginCallBack {
	private static final int REQUEST_REGISTER = 1;
	private CheckBox ckSavePwd;
	private EditText etAccount, etPwd;
	private TextView btn_look_for_pwd;
	private PullToRefreshLayout mPullToRefreshLayout;
	private ImageView btn_back;
	IMManageTool manageTool;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.login_v2);
		UICore.eventTask(this, this, IResult.SYSN_CONFIG, null, null);// 加载数据入口，跳转到execute(int
																		// obj)
		manageTool = IMManageTool.getInstance(getApplicationContext());
		manageTool.setEMLoginCallBack(this);
	}

	@Override
	protected void setupView() {
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setup(mPullToRefreshLayout);
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.title_login));
		ckSavePwd = (CheckBox) findViewById(R.id.ck_save_pwd);
		etAccount = (EditText) findViewById(R.id.et_account);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setVisibility(View.GONE);

		btn_look_for_pwd = (TextView) findViewById(R.id.btn_look_for_pwd);
		btn_look_for_pwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		btn_look_for_pwd.getPaint().setAntiAlias(true);// 抗锯齿
		btn_look_for_pwd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				seekPwd();
			}
		});
		// 自动弹出软键盘
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) LoginActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0,
							InputMethodManager.HIDE_NOT_ALWAYS);
					getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
					etAccount.setFocusable(true);
					etAccount.setFocusableInTouchMode(true);
					etAccount.requestFocus();
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_login) {

			final String userName = etAccount.getText().toString().trim();
			final String pwd = etPwd.getText().toString().trim();
			if ("".equals(userName) || "".equals(pwd)) {
				showToast("账户或密码不能为空");
				return;
			}
			
			savePwd(userName, pwd);
			if ((NetWorkUtils.detect(LoginActivity.this))) {
				login();
			} else {
				handler.sendEmptyMessage(IResult.NET_ERROR);
			}
		} else if (id == R.id.btn_logon) {
			logon();// 注册
		} else if (id == R.id.btn_look_for_pwd) {
			seekPwd();
		} /*else if (id == R.id.btn_back) {
			finish();
		}*/
	}

	// 找回密码
	private void seekPwd() {
		Intent intent = new Intent(this, SeekPwdActivity.class);
		startActivity(intent);
	}

	// 登录
	private void login() {
		loading("登录中...");
		final String userName = etAccount.getText().toString().trim();
		final String pwd = etPwd.getText().toString().trim();
		savePwd(userName, pwd);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", userName);
		params.put("strpwd", pwd);
		params.put("imei", PublicUtil.getDeviceUuid(LoginActivity.this));
		params.put("appversion", PublicUtil.getVersionName(LoginActivity.this));

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
						message.what = IResult.NET_ERROR;
						handler.sendMessage(message);
					}
				});

	}

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		if (mes == IResult.LOGIN) {
			if ((NetWorkUtils.detect(LoginActivity.this))) {
				login();
			} else {
				handler.sendEmptyMessage(IResult.NET_ERROR);
			}
		} else if (mes == IResult.SYSN_CONFIG) {
			ConfigTable configTable = new ConfigTable();
			if (null == configTable.getDepartmentGroups()
					|| configTable.getDepartmentGroups().size() == 0) {
				sysn_config();
			}
		}
	}

	// 保存密码
	private void savePwd(String userName, String pwd) {
		boolean savePwd = ckSavePwd.isChecked();

		if (savePwd) {
			SharePrefUtil.putBoolean("savepwd", true);
			SharePrefUtil.putString("account", userName);
			SharePrefUtil.putString("pwd", pwd);
		} else {
			SharePrefUtil.putBoolean("savepwd", false);
		}
		SharePrefUtil.commit();
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

	// 注册
	private void logon() {
		Intent intent = new Intent(this, LogonsActivity.class);
		startActivityForResult(intent, REQUEST_REGISTER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_REGISTER:
			if (RESULT_OK == resultCode && null != data) {
				String account = data.getStringExtra("account");
				String pwd = data.getStringExtra("pwd");
				JPushInterface.init(getApplicationContext());
				JPushInterface.resumePush(getApplicationContext());
				if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)) {
					ckSavePwd.setChecked(true);
					etAccount.setText(account);
					etPwd.setText(pwd);

					savePwd(account, pwd);

					Intent intent = new Intent(LoginActivity.this,
							RegisterSuccessActivity.class);
					intent.putExtra("account", etAccount.getText().toString()
							.trim());
					intent.putExtra("pwd", etPwd.getText().toString().trim());
					intent.putExtra("need_load_info", false);
					startActivity(intent);
					finish();
				}
			}
			break;
		default:
			break;
		}
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
							saveDoctor(doctor);
							
							//XXX 测试完记得删除
//							SharePrefUtil.putBoolean(Conast.LOGIN, true);
//							SharePrefUtil.putString(Conast.Doctor_ID, doctor.getMEMBERID());
//							SharePrefUtil.putString(Conast.Doctor_Name, doctor.getREALNAME());
//							SharePrefUtil.putString(Conast.MEMBER_NAME, doctor.getREALNAME());
//							SharePrefUtil.putString(Conast.Doctor_Type, doctor.getALLOWFREECONSULT());//1为全科 0为专科
//							SharePrefUtil.putString(Conast.ACCESS_TOKEN, doctor.getTOKEN());
//							SharePrefUtil.putString(Conast.AVATAR, doctor.getAVATAR());
//							SharePrefUtil.putString(Conast.MOBILE, doctor.getMOBILE());
//							SharePrefUtil.putString(Conast.CARDNUMBER, doctor.getCARDNUMBER());
//							SharePrefUtil.commit();
//							
//							Intent intent = new Intent(LoginActivity.this,LogonResultActivity.class);
//							intent.putExtra("account", etAccount.getText().toString().trim());
//							intent.putExtra("pwd", etPwd.getText().toString().trim());
//							startActivity(intent);
//							finish();
							//XXX
						}
					} else {
						if (TextUtils.isEmpty(login.getErrormsg())) {

						} else {
							Toast.makeText(this, login.getErrormsg(),
									Toast.LENGTH_SHORT).show();
							destroyDialog();
						}
					}
				}
				break;
			case IResult.TEST:
				destroyDialog();
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case IResult.LOGON_ERROR:
				destroyDialog();
				Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
				break;
			case IResult.NET_ERROR:
				destroyDialog();
				showToast("登录失败");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
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

	@Override
	protected void onResume() {
		super.onResume();
		String account = SharePrefUtil.getString("account");
		String pwd = SharePrefUtil.getString("pwd");
		boolean savePwd = SharePrefUtil.getBoolean("savepwd");
		if (!savePwd) {
			return;
		}
		ckSavePwd.setChecked(true);
		etAccount.setText(account);
		etPwd.setText(pwd);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, ChatUser user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Conast.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	@Override
	public void onSuccess() {
		destroyDialog();
		System.out.println("Lonin Activity 登入环信成功");
/*		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();*/
		Intent intent = new Intent(this,MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("is_new_register", true);
		startActivity(intent);
		finish();
	}

	@Override
	public void onProgress(int progress, String str) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(int age1, String age2) {
		// TODO Auto-generated method stub
		// Toast.makeText(LoginActivity.this, "登录失败 :"+age2,
		// Toast.LENGTH_SHORT).show();
		destroyDialog();
		if (age1 == EMCallBack.ERROR_EXCEPTION_INVALID_PASSWORD_USERNAME) {
			reg_to_im();
		}else{
			System.out.println(age1+" 环信登入异常："+age2);
		}
	}
}
