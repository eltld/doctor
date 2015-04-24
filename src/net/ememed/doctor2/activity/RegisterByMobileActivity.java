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
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.LoginEntry;
import net.ememed.doctor2.entity.LoginInfo;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.entity.PhoneVerifyInfo;
import net.ememed.doctor2.entity.RegisterEntry;
import net.ememed.doctor2.entity.RegisterInfo;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.exception.MyException;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;
import de.greenrobot.event.util.IMManageTool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterByMobileActivity extends BasicActivity implements BasicUIEvent,
		EMLoginCallBack {
	private static final int REQUEST_REGISTER = 1;
	private CheckBox ckServiceItem;
	private EditText etAccount, etPwd;
	private TextView tv_protocol;
	private EditText et_verify_code;
	private EditText et_inviteCode;
	private Button btn_second;
	private Handler mTimeHandler;
	private int verifyCode;
	private ImageView iv_show_pswd;
	private boolean isShowingPwd;
	private EditText etRealName;
	IMManageTool manageTool;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.register_by_mobile);
		UICore.eventTask(this, this, IResult.SYSN_CONFIG, null, null);// 加载数据入口，跳转到execute(int
		// obj)
		manageTool = IMManageTool.getInstance(getApplicationContext());
		manageTool.setEMLoginCallBack(this);
	}

	@Override
	protected void setupView() {
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.title_register));
		ckServiceItem = (CheckBox) findViewById(R.id.ckServiceItem);
		ckServiceItem.setChecked(true);
		etRealName = (EditText) findViewById(R.id.et_realname);
		etAccount = (EditText) findViewById(R.id.et_account);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		et_verify_code = (EditText) findViewById(R.id.et_verify_code);
		et_inviteCode = (EditText) findViewById(R.id.et_invite_code);

		btn_second = (Button) findViewById(R.id.btn_second);
		tv_protocol = (TextView) findViewById(R.id.tv_protocol);
		tv_protocol.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		tv_protocol.getPaint().setAntiAlias(true);// 抗锯齿
		tv_protocol.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegisterByMobileActivity.this, WebViewActivity.class);
				intent.putExtra("title", "薏米网服务条款");
				intent.putExtra("url", HttpUtil.regClause);
				startActivity(intent);
			}
		});

		iv_show_pswd = (ImageView) findViewById(R.id.iv_show_pwd);

		HandlerThread mHandlerThread = new HandlerThread("count", 5);
		mHandlerThread.start();// 开始计时
		mTimeHandler = new Handler(mHandlerThread.getLooper());
		// 显示软键盘
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) RegisterByMobileActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					etRealName.setFocusable(true);
					etRealName.setFocusableInTouchMode(true);
					etRealName.requestFocus();
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		if (mes == IResult.PHONE_VERIFY_CODE) {
			phone_verify_code((String) obj);
		} else if (mes == IResult.REGISTER) {
			register();
		} else if (mes == IResult.SYSN_CONFIG) {
			ConfigTable configTable = new ConfigTable();
			if (null == configTable.getDepartmentGroups()
					|| configTable.getDepartmentGroups().size() == 0) {
				sysn_config();
			}
		}
	}

	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_second) {
			if (NetWorkUtils.detect(RegisterByMobileActivity.this)) {
				if (TextUtils.isEmpty(etAccount.getText().toString())) {
					showToast(getString(R.string.register_account_length_null));
				} else {
					String accountNum = etAccount.getText().toString().trim();
					if (accountNum.length() == 11) {
						btn_second.setClickable(false);
						btn_second.setEnabled(false);
						btn_second.setSelected(true);
						btn_second.setTextColor(getResources().getColor(R.color.light_gray));
						btn_second.setText("正在获取");
						UICore.eventTask(this, this, IResult.PHONE_VERIFY_CODE, null, etAccount
								.getText().toString().trim());
					} else {
						showToast(getString(R.string.register_account_length));
					}
				}
			} else {
				showToast(getString(R.string.net_error));
			}
		} else if (id == R.id.btn_register) {
			if (NetWorkUtils.detect(RegisterByMobileActivity.this)) {
				if (TextUtils.isEmpty(etRealName.getText().toString())
						|| etRealName.getText().toString().trim().length() == 0) {
					showToast("请输入您的真实姓名");
					return;
				}
				if (TextUtils.isEmpty(etAccount.getText().toString())
						|| etAccount.getText().toString().trim().length() == 0) {
					showToast("请输入手机号");
					return;
				}
				if (TextUtils.isEmpty(et_verify_code.getText().toString())
						|| et_verify_code.getText().toString().trim().length() == 0) {
					showToast("请输入验证码");
					return;
				}
//				String verify_code_sms = et_verify_code.getText().toString();
//				if (!verify_code_sms.equals(verifyCode + "")) {
//					showToast("验证码不正确,请重新获取");
//					return;
//				}

				if (TextUtils.isEmpty(etPwd.getText().toString())
						|| etPwd.getText().toString().trim().length() == 0) {
					showToast("请输入密码");
					return;
				}

				if (!ckServiceItem.isChecked()) {
					showToast("请阅读并同意薏米服务条款");
					return;
				}
				UICore.eventTask(RegisterByMobileActivity.this, RegisterByMobileActivity.this,
						IResult.REGISTER, "注册中...", null);

			} else {
				showToast(getString(R.string.net_error));
			}
		} else if (id == R.id.btn_back) {
			finish();
		} else if (id == R.id.iv_show_pwd) {
			if (isShowingPwd) {
				// 隐藏密码
				isShowingPwd = false;
				etPwd.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				iv_show_pswd.setImageResource(R.drawable.ic_eye_close);

			} else {
				// 显示密码
				isShowingPwd = true;
				etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				iv_show_pswd.setImageResource(R.drawable.ic_eye_open);
			}
		}
	}

	private void phone_verify_code(final String phoneNum) {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", phoneNum);
		params.put("utype", "doctor");
		params.put("channel", "android");
		params.put("appversion", PublicUtil.getVersionName(this));

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.new_phone_verify_code,
				PhoneVerifyInfo.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {

						Message message = new Message();
						message.obj = response;
						message.what = IResult.PHONE_VERIFY_CODE;
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

	private void register() {
		String realname = etRealName.getText().toString().trim();
		String pwd = etPwd.getText().toString().trim();
		String mobile = etAccount.getText().toString().trim();
		int freeConsult = SharePrefUtil.getInt("freeConsult");
		
		String inviteCode = et_inviteCode.getText().toString();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("strpwd", pwd));
		params.add(new BasicNameValuePair("channel", "android"));
		params.add(new BasicNameValuePair("realname", realname));
		params.add(new BasicNameValuePair("allow_free_consult", freeConsult + ""));
		params.add(new BasicNameValuePair("verify_code", et_verify_code.getText().toString()));
		params.add(new BasicNameValuePair("appversion", PublicUtil.getVersionName(this)));
		params.add(new BasicNameValuePair("invite_number", inviteCode));

		String content;
		try {
			content = HttpUtil.getString(HttpUtil.URI + HttpUtil.register, params, HttpUtil.POST);
			content = TextUtil.substring(content, "{");
			Gson gson = new Gson();
			RegisterInfo reason = gson.fromJson(content, RegisterInfo.class);
			Message msg = Message.obtain();
			msg.what = IResult.RESULT;
			msg.obj = reason;
			handler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
			Message message = new Message();
			message.obj = e.getMessage();
			message.what = IResult.LOGON_ERROR;
			handler.sendMessage(message);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_REGISTER:
			if (RESULT_OK == resultCode && null != data) {
				String account = data.getStringExtra("account");
				String pwd = data.getStringExtra("pwd");
			}
			break;
		default:
			break;
		}
	}

	// 保存密码
	private void savePwd(String userName, String pwd) {
		SharePrefUtil.putBoolean("savepwd", true);
		SharePrefUtil.putString("account", userName);
		SharePrefUtil.putString("pwd", pwd);
		SharePrefUtil.commit();
	}

	// 登录
	private void login() {
		loading("登录中...");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("username", etAccount.getText().toString().trim());
		params.put("strpwd", etPwd.getText().toString().trim());
		params.put("imei", PublicUtil.getDeviceUuid(RegisterByMobileActivity.this));
		params.put("appversion", PublicUtil.getVersionName(RegisterByMobileActivity.this));

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.login, LoginInfo.class, params,
				new Response.Listener() {
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
			case IResult.RESULT:
				destroyDialog();
				RegisterInfo register = (RegisterInfo) msg.obj;
				if (null != register) {
					if (register.getSuccess() == 1) {
						btn_second.setTextColor(getResources().getColor(R.color.black));
						btn_second.setText("获取验证码");
						btn_second.setClickable(true);
						btn_second.setEnabled(true);
						btn_second.setSelected(false);
						String pwd = etPwd.getText().toString().trim();
						String mobile = etAccount.getText().toString().trim();
						savePwd(mobile, pwd);
						// logonSuccess
						// SharePrefUtil.putBoolean(Conast.LOGIN, true);
						SharePrefUtil.putString(Conast.Doctor_ID, register.getData().getMEMBERID());
						SharePrefUtil.putString(Conast.Doctor_Name, register.getData()
								.getREALNAME());
						SharePrefUtil.putString(Conast.MEMBER_NAME, register.getData()
								.getREALNAME());
						SharePrefUtil.putString(Conast.Doctor_Type, register.getData()
								.getALLOWFREECONSULT());// 1为全科 0为专科
						SharePrefUtil.putString(Conast.ACCESS_TOKEN, register.getData().getTOKEN());
						SharePrefUtil.putString(Conast.AVATAR, register.getData().getAVATAR());
						SharePrefUtil.putString(Conast.MOBILE, register.getData().getMOBILE());
						SharePrefUtil.putString(Conast.CARDNUMBER, register.getData()
								.getCARDNUMBER());
						SharePrefUtil.commit();

						EventBus.getDefault().postSticky(new RegisterSuccessEvent());

						if ((NetWorkUtils.detect(RegisterByMobileActivity.this))) {
							login();
						} else {
							handler.sendEmptyMessage(IResult.NET_ERROR);
						}

						/*
						 * Intent intent = new
						 * Intent(RegisterByMobileActivity.this,LogonResultActivity.class);
						 * intent.putExtra("account", etAccount.getText().toString().trim());
						 * intent.putExtra("pwd", etPwd.getText().toString().trim());
						 * startActivity(intent); finish();
						 */
					} else {
						showToast(register.getErrormsg());
					}
				}
				break;
			case IResult.LOGIN:
				LoginInfo login = (LoginInfo) msg.obj;
				if (null != login) {
					if (login.getSuccess() == 1) {
						LoginEntry doctor = login.getData();
						if (null != doctor) {
							saveDoctor(doctor);
							Intent intent = new Intent(RegisterByMobileActivity.this,
									LogonResultActivity.class);
							intent.putExtra("account", etAccount.getText().toString().trim());
							intent.putExtra("pwd", etPwd.getText().toString().trim());
							startActivity(intent);
							finish();
						}
					} else {
						if (TextUtils.isEmpty(login.getErrormsg())) {
							showToast("登录失败");
							destroyDialog();
							startActivity(new Intent(RegisterByMobileActivity.this,
									LoginActivity.class));
							finish();
						} else {
							Toast.makeText(this, login.getErrormsg(), Toast.LENGTH_SHORT).show();
							destroyDialog();
							startActivity(new Intent(RegisterByMobileActivity.this,
									LoginActivity.class));
							finish();
						}
					}
				}
				break;
			case IResult.LOGIN_ERROR:
				showToast("登录失败");
				destroyDialog();
				startActivity(new Intent(RegisterByMobileActivity.this, LoginActivity.class));
				finish();
				break;
			case IResult.PHONE_VERIFY_CODE:
				PhoneVerifyInfo phoneVerify = (PhoneVerifyInfo) msg.obj;
				if (phoneVerify.getSuccess() == 1) {
					verifyCode = phoneVerify.getData().getCODE();
					btn_second.setClickable(false);
					btn_second.setEnabled(false);
					btn_second.setSelected(true);
					btn_second.setTextColor(getResources().getColor(R.color.light_gray));
					time = 120;
					terminateCount = false;
					mTimeHandler.post(oneSecondThread);
					showToast("验证码已发送,请注意查收短信");
				} else {
					if (!TextUtils.isEmpty(phoneVerify.getErrormsg())) {
						showToast(phoneVerify.getErrormsg());
					}
					btn_second.setTextColor(getResources().getColor(R.color.black));
					btn_second.setText("获取验证码");
					btn_second.setClickable(true);
					btn_second.setEnabled(true);
					btn_second.setSelected(false);
				}
				break;
			case IResult.FAILURE:
				Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
				break;
			case IResult.LOGON_ERROR:
				Toast.makeText(this, (String) msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case IResult.NET_ERROR:
				destroyDialog();
				break;
			case IResult.DATA_ERROR:
				Toast.makeText(this, "获取失败，请检查网络", Toast.LENGTH_SHORT).show();
				btn_second.setTextColor(getResources().getColor(R.color.black));
				btn_second.setText("获取验证码");
				btn_second.setClickable(true);
				btn_second.setEnabled(true);
				btn_second.setSelected(false);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	private int time = 120;
	private boolean terminateCount = false;

	Thread oneSecondThread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				if (time > 0 && !terminateCount) {
					time--;
					Thread.sleep(1000);
					Message msg = new Message();
					msg.arg1 = time;
					uiHandler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});

	Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (time > 0 && !terminateCount) {
				btn_second.setText(msg.arg1 + " 秒");
				mTimeHandler.post(oneSecondThread);
			} else {
				btn_second.setTextColor(getResources().getColor(R.color.black));
				btn_second.setText("重新获取验证码");
				btn_second.setClickable(true);
				btn_second.setEnabled(true);
				btn_second.setSelected(false);
				terminateCount = true;
			}
		}
	};

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
							+ SharePrefUtil.getString(Conast.MEMBER_NAME) + "ememedim"));
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

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().removeStickyEvent(RegisterSuccessEvent.class);
	}

	@Override
	public void onSuccess() {
		destroyDialog();
		handler.sendEmptyMessage(IResult.OK);
		System.out.println("Lonin Activity 登入环信成功");
	}

	@Override
	public void onProgress(int progress, String str) {

	}

	@Override
	public void onError(int age1, String age2) {
		destroyDialog();
		if (age1 == EMCallBack.ERROR_EXCEPTION_INVALID_PASSWORD_USERNAME) {
			reg_to_im();
		} else {
			System.out.println("环信登入异常：" + age2);
		}
	}

	private void sysn_config() {
		try {

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("Channel", "android"));// 新增
			String content = HttpUtil.getString(HttpUtil.URI + HttpUtil.sync_config, params,
					HttpUtil.POST);
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
						positionTable.savePositionName(null, positionObj.getString(i));
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

	private void reg_to_im() {
		try {
			if (NetWorkUtils.detect(getApplicationContext())) {
				new Thread() {
					public void run() {
						ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("token", SharePrefUtil
								.getString(Conast.ACCESS_TOKEN)));
						params.add(new BasicNameValuePair("channel", "android"));
						params.add(new BasicNameValuePair("memberid", SharePrefUtil
								.getString(Conast.Doctor_ID)));
						params.add(new BasicNameValuePair("utype", "doctor"));
						try {
							String result = HttpUtil.getString(HttpUtil.URI + HttpUtil.reg_to_im,
									params, HttpUtil.POST);
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
}
