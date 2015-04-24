package net.ememed.doctor2.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.PersonInfoActivity;
import net.ememed.doctor2.activity.SaveEmailActivity;
import net.ememed.doctor2.activity.SettingPwdActivity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public final class PwdSettingFragment extends Fragment implements Handler.Callback,OnClickListener{
	private static final int EXEU_GET_LIST_DATA = 199;
	private static final int EXEU_DATA_NONE = 200;
	
    private String fragmentKey = "";
    private SettingPwdActivity activity = null;
    private Handler mHandler;
    
    int type = 1;
	private TextView tv_none = null;
	private LinearLayout ll_view_content;
	private FrameLayout mContentView = null;
	private EditText et_old_pwd;
	private EditText et_new_pwd;
	private EditText et_confirm_pwd;
	private Button btn_save;
	private String newPwd;
	private String oldPwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
        activity = (SettingPwdActivity) getActivity();
        fragmentKey = getArguments().getString("fragmentKey");
//        System.out.println("TestFragment onCreate fragmentKey : "+fragmentKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        System.out.println("TestFragment onCreateView fragmentKey : "+fragmentKey);

    	View view = inflater.inflate(R.layout.root_layout,null);
    	mContentView = (FrameLayout) view.findViewById(R.id.mainView);

    	ll_view_content = (LinearLayout)LayoutInflater.from(activity).inflate(R.layout.fragment_pwdsetting, null);
    	mContentView.addView(ll_view_content);
    	
    	et_old_pwd = (EditText)ll_view_content.findViewById(R.id.et_old_pwd);
    	et_new_pwd = (EditText)ll_view_content.findViewById(R.id.et_new_pwd);
    	et_confirm_pwd = (EditText)ll_view_content.findViewById(R.id.et_confirm_pwd);
    	btn_save =(Button)ll_view_content.findViewById(R.id.btn_save);
    	btn_save.setOnClickListener(this);
		return view;
    }
    @Override
    public void onResume() {
    	super.onResume();
    }
	
	protected void sendMessage(int what, Object obj) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        System.out.println("TestFragment onSaveInstanceState fragmentKey : "+fragmentKey);
        if("修改登录密码".equals(fragmentKey)){
        	type = 1;
        }else if ("修改语音密码".equals(fragmentKey)){
        	type = 2;
        }
    }
    
	@Override
	public boolean handleMessage(Message msg) {
		try {
			switch (msg.what) {
			case IResult.RESULT:
				activity.destroyDialog();
				PersonInfo map_2 = (PersonInfo) msg.obj;
				int success_2 = map_2.getSuccess();
				if(success_2 == 0){
					activity.showToast((String) map_2.getErrormsg());
					et_old_pwd.setText("");
					et_new_pwd.setText("");
					et_confirm_pwd.setText("");
				} else {
					activity.showToast(getString(R.string.service_setting_time_success));
					SharePrefUtil.putString("newpwd", newPwd);
					SharePrefUtil.putString("oldpwd", oldPwd);
					SharePrefUtil.commit();
				}
				
				break;
			case IResult.END:
				
				break;
			case IResult.NET_ERROR:
				activity.showMessage(IMessage.NET_ERROR);
				break;

			case IResult.DATA_ERROR:
				activity.showMessage(IMessage.DATA_ERROR);
				break;
			default:
				break;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_save){
			amendPwd();
		}
	}
	
	// 修改密码
		private void amendPwd() {
			try {
				oldPwd = et_old_pwd.getText().toString();
				newPwd = et_new_pwd.getText().toString();
				String confirmPwd = et_confirm_pwd.getText().toString();
				
				if ("".equals(oldPwd)) {
					throw new Exception("原密码不能为空");
				}
				if ("".equals(newPwd)) {
					throw new Exception("新密码不能为空");
				}
				if (type == 1) {
					if (newPwd.length() < 4 || newPwd.length() > 16) {
						throw new Exception("密码长度错误");
					}
				} else if (type == 2) {
					if (!net.ememed.doctor2.util.CharUtil.isNum(newPwd)) {
						throw new Exception("语音密码必须为6位数字");
					}
				}
				if (!newPwd.equals(confirmPwd)) {
					throw new Exception("密码确认不一致");
				}

				if (NetWorkUtils.detect(activity)){
					activity.loading(null);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
					params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
					params.put("oldpwd", oldPwd);
					params.put("newpwd", newPwd);
					params.put("pwdtype", type + "");
					MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_doctor_password,
							PersonInfo.class, params, new Response.Listener() {
								@Override
								public void onResponse(Object response) {
									
									Message message = new Message();
									message.obj = response;
									message.what = IResult.RESULT;
									mHandler.sendMessage(message);
								}
							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {

									Message message = new Message();
									message.obj = error.getMessage();
									message.what = IResult.NET_ERROR;
									mHandler.sendMessage(message);
								}
							});
				}else {
					mHandler.sendEmptyMessage(IResult.NET_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
