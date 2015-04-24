package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.entity.PhoneVerifyInfo;
import net.ememed.doctor2.entity.Reason;
import net.ememed.doctor2.entity.User;
import net.ememed.doctor2.entity.ValiCode;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

//找回密码
public class SeekPwdActivity extends BasicActivity implements OnRefreshListener,BasicUIEvent{

	//
	// private RadioGroup rg;
	// private TextView tvValiType, tvPE;

	private ViewFlipper flipper;
	private EditText etPhoneNum, etValicode, etNewPwd, etConfirmNewPwd;
	private Button btnGetValiCode;
	private String phone, email;
	private String userid;
	private int type = 1;
	private String valiCode = "";
	private Handler mTimeHandler;
	private InputMethodManager manager;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.seek_pwd);
	}

	@Override
	protected void setupView() {

		TextView tvTitle = (TextView) findViewById(R.id.top_title);
		tvTitle.setText(getString(R.string.activity_title_find_pw));
		flipper = (ViewFlipper) findViewById(R.id.viewFlipper);

		etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
		etPhoneNum.setText(SharePrefUtil.getString(Conast.MOBILE));
		etValicode = (EditText) findViewById(R.id.et_valicode);
		btnGetValiCode = (Button) findViewById(R.id.btn_get_valicode);
		etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
		etConfirmNewPwd = (EditText) findViewById(R.id.et_confirm_new_pwd);
		manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		HandlerThread mHandlerThread = new HandlerThread("count", 5);
		mHandlerThread.start();//开始计时
		mTimeHandler = new Handler(mHandlerThread.getLooper());
		//自动弹出数字软键盘
		try {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager inputManager = (InputMethodManager) SeekPwdActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.toggleSoftInput(0,
							InputMethodManager.HIDE_NOT_ALWAYS);
					etPhoneNum.setFocusable(true);
					etPhoneNum.setFocusableInTouchMode(true);
					etPhoneNum.requestFocus();
				}
			}, 200L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_next1) {
			valiValiCode();
		} else if (id == R.id.btn_get_valicode) {
			getValiCode();
		}else if (id == R.id.btn_refer) {
			resetPwd();
		} else if (id == R.id.btn_login) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (id == R.id.btn_back) {
			finish();
		}
	}

	@Override
	protected void addListener() {
		// rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(RadioGroup group, int checkedId) {
		// if (checkedId == R.id.rb_telphone_vali) {
		// type = 1;
		// tvPE.setText(phone);
		// tvValiType.setText("电话号码");
		// } else if (checkedId == R.id.rb_email_vali) {
		// type = 2;
		// tvPE.setText(email);
		// tvValiType.setText("电子邮箱");
		// }
		//
		// }
		// });
	}
	
	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/** 获取用户电话 */
	private void getNext() {
		String userPhone = etPhoneNum.getText().toString().trim();
		if (NetWorkUtils.detect(SeekPwdActivity.this)) {
			loading(getString(R.string.progressdialog_loading));
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("mobile", userPhone);
			params.put("utype", "doctor");
			params.put("verify_code", valiCode);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.check_mobile_code, Reason.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {

							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_USER;
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
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/** 获取验证码 */
	private void getValiCode() {
		String userPhone = etPhoneNum.getText().toString().trim();
		if (NetWorkUtils.detect(SeekPwdActivity.this)) {
			if (TextUtils.isEmpty(userPhone) ||  userPhone.length() < 11) {
				Toast.makeText(this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
				return;
			} else {
				if (userPhone.length() == 11) {
					hideKeyboard();
					btnGetValiCode.setClickable(false);//获取验证码不可点击
					btnGetValiCode.setEnabled(false);
					btnGetValiCode.setSelected(true);
					btnGetValiCode.setTextColor(getResources().getColor(R.color.light_gray));
					btnGetValiCode.setText("正在获取");
					showToast(getString(R.string.get_vali_code));
					
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("mobile", etPhoneNum.getText().toString().trim());
					params.put("utype", "doctor");
					params.put("type", type + "");
					params.put("appversion", PublicUtil.getVersionName(this));
					MyApplication.volleyHttpClient.postWithParams(
							HttpUtil.get_validate_code, ValiCode.class, params,
							new Response.Listener() {
								@Override
								public void onResponse(Object response) {

									Message message = new Message();
									message.obj = response;
									message.what = IResult.GET_VALI_CODE;
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
				} else {
					showToast(getString(R.string.register_account_length));
				}
			}
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/** 验证验证码是否正确 */
	private void valiValiCode() {
		if (TextUtils.isEmpty(etPhoneNum.getText().toString())) {
			showToast(getString(R.string.register_account_length_null));
			return;
		}
		
		if ("".equals(valiCode)) {
			showToast("请获取验证码");
			return;
		}

		String valiCode2 = etValicode.getText().toString().trim();
		if (!valiCode.equals(valiCode2)) {
			showToast("验证码填写不正确");
		} else {
			getNext();
			// flipper.showNext();
		}
	}

	/** 重置密码 */
	private void resetPwd() {
		final String newPwd = etNewPwd.getText().toString();
		final String confirmNewPwd = etConfirmNewPwd.getText().toString();
		if (TextUtils.isEmpty(etNewPwd.getText().toString())
				||  etNewPwd.getText().toString().length() == 0) {
			showToast("请输入密码");
			return;
		}
		
		if (TextUtils.isEmpty(etConfirmNewPwd.getText().toString())
				|| etConfirmNewPwd.getText().toString().length() == 0) {
			showToast("请输入确认密码");
			return;
		}
		
		if (etNewPwd.getText().toString().length() < 4 || etNewPwd.getText().toString().length() > 16) {
			showToast("密码长度错误");
		}
		
		if (!etNewPwd.getText().toString().equals(etConfirmNewPwd.getText().toString())) {
			showToast("两次密码输入不一致");
			return;
		}
		if (NetWorkUtils.detect(SeekPwdActivity.this)) {
			loading(getString(R.string.progressdialog_loading));
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("memberid", userid);
			params.put("newpwd", newPwd);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.reset_password, PersonInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {

							Message message = new Message();
							message.obj = response;
							message.what = IResult.RESET_PWD;
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
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.GET_USER:
				destroyDialog();
				Reason reason = (Reason) msg.obj;
				if (null != reason) {
					if (reason.getSuccess() == 1) {
						User user = reason.getData();
						flipper.showNext();//执行到下一步
					} else {
						showToast((String) reason.getErrormsg());
					}
				} else {
					showToast("您的网络不稳定!");
				}
				break;
			case IResult.GET_VALI_CODE:
				ValiCode valiCode2 = (ValiCode) msg.obj;
				if (null != valiCode2) {
					if (valiCode2.getSuccess() == 1) {
						valiCode = valiCode2.getValidateCode() + "";
						userid = valiCode2.getMemberid();
						btnGetValiCode.setClickable(false);
						btnGetValiCode.setEnabled(false);
						btnGetValiCode.setSelected(true);
						btnGetValiCode.setTextColor(getResources().getColor(R.color.light_gray));;
						time = 120;
						terminateCount = false;
						mTimeHandler.post(oneSecondThread);//获取验证码倒计时
						Toast.makeText(this, "验证码发送到您的手机，请注意查收短信",
								Toast.LENGTH_SHORT).show();
					} else {
						btnGetValiCode.setTextColor(getResources().getColor(
								R.color.black));
						btnGetValiCode.setText("获取验证码");
						btnGetValiCode.setClickable(true);
						btnGetValiCode.setEnabled(true);
						btnGetValiCode.setSelected(false);;
						showToast((String) valiCode2.getErrormsg());
					}
				} else {
					showToast("您的网络不稳定!");
				}
				break;

			case IResult.RESET_PWD:
				hideKeyboard();
				destroyDialog();
				PersonInfo resetPwd = (PersonInfo) msg.obj;
				if (null != resetPwd) {
					if (resetPwd.getSuccess() == 1) {
						Toast.makeText(this, "密码重置成功", Toast.LENGTH_SHORT)
								.show();
						flipper.showNext();//执行到下一步
					} else {
						showToast((String) resetPwd.getErrormsg());
					}
				} else {
					showToast("您的网络不稳定!");
				}
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
				break;
			case IResult.DATA_ERROR:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	private int time = 120;//获取验证码时间限制
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
					uiHandler.sendMessage(msg);//send Message
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	});

	Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {// handle Messagae
			super.handleMessage(msg);
			if (time > 0 && !terminateCount) {
				btnGetValiCode.setText(msg.arg1 + " 秒");
				mTimeHandler.post(oneSecondThread);
			} else {
				btnGetValiCode.setTextColor(getResources().getColor(
						R.color.black));
				btnGetValiCode.setText("重新获取验证码");
				btnGetValiCode.setClickable(true);
				btnGetValiCode.setEnabled(true);
				btnGetValiCode.setSelected(false);
				terminateCount = true;
			}
		}
	};
	

	public void execute(int mes, Object obj) {
		if (mes == IResult.NEXT) {
			getNext();
		} else if (mes == IResult.RESET_PWD){
			resetPwd();
		}
	};

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
	public void onRefreshStarted(View view) {
	}
}
