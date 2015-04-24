package net.ememed.doctor2.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import u.aly.br;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.BaseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.fragment.CommunicationGroupCreateNameFragment;
import net.ememed.doctor2.fragment.CommunicationGroupCreatePictureFragment;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.BitmapUtils;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.FileUtil;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UserPreferenceWrapper;
import net.ememed.doctor2.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class CommunicationGroupCreateActivity extends BasicActivity implements OnClickListener {

	private static final String[] TITLES = { "填写群名称", "上传群头像" };
	private static final String[] BUTTONS = { "下一步", "创建" };
	private Fragment[] mFragments = { new CommunicationGroupCreateNameFragment(),
			new CommunicationGroupCreatePictureFragment() };
	private TextView tv_topTitle;
	private TextView tv_topRight;
	private int mCurrentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_group_create);
		tv_topTitle = (TextView) findViewById(R.id.tv_top_title);
		tv_topRight = (TextView) findViewById(R.id.tv_top_right);
		tv_topRight.setVisibility(View.VISIBLE);
		tv_topRight.setOnClickListener(this);

		setDefaultFragment();
	}

	private void setDefaultFragment() {
		mCurrentIndex = 0;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.id_content, mFragments[mCurrentIndex]);
		ft.commit();
		tv_topTitle.setText(TITLES[mCurrentIndex]);
		tv_topRight.setText(BUTTONS[mCurrentIndex]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_top_right:
			nextPager();
			break;
		default:
			break;
		}
	}

	private void nextPager() {
		OnInputCallBack inputCallBack = ((OnInputCallBack) mFragments[0]);
		switch (mCurrentIndex) {
		case 0:
			if (!inputCallBack.hasInput()) {
				Toast.makeText(this, "请输入群的名字", Toast.LENGTH_SHORT).show();
				return;
			}
			Util.hideKeyboard(this, tv_topTitle);
			mCurrentIndex++;
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.id_content, mFragments[mCurrentIndex]);
			ft.addToBackStack(null);
			ft.commit();
			tv_topTitle.setText(TITLES[mCurrentIndex]);
			tv_topRight.setText(BUTTONS[mCurrentIndex]);
			break;
		case 1:
			final OnInputCallBack inputCallBack1 = ((OnInputCallBack) mFragments[1]);
			if (inputCallBack1.hasInput()) {
				loading("");
				new Thread(new Runnable() {

					@Override
					public void run() {
						requestUploadPortrait(inputCallBack1.getInput());
					}
				}).start();

			} else {
				String groupName = inputCallBack.getInput();
				requestCreateGroup(groupName, null);
			}
			break;
		default:
			break;
		}

	}

	private void prevPager() {

	}

	public interface OnInputCallBack {
		public String getInput();

		public boolean hasInput();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		switch (mCurrentIndex) {
		case 0:

			break;
		case 1:
			mCurrentIndex--;
			tv_topTitle.setText(TITLES[mCurrentIndex]);
			tv_topRight.setText(BUTTONS[mCurrentIndex]);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBack(View v) {
		switch (mCurrentIndex) {
		case 0:
			setResult(Activity.RESULT_OK);
			finish();
			break;
		case 1:
			FragmentManager fm = getSupportFragmentManager();
			fm.popBackStack();
			mCurrentIndex--;
			tv_topTitle.setText(TITLES[mCurrentIndex]);
			tv_topRight.setText(BUTTONS[mCurrentIndex]);
			break;
		default:
			break;
		}
	}

	/**
	 * 请求创建群
	 */
	private void requestCreateGroup(String groupName, String portraitUrl) {
		if (!NetWorkUtils.detect(this)) {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading("");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("groupname", groupName);
		params.put("desc", "群描述");
		params.put("public", "1"); // 是否公开群 1:公开，2:不公开
		params.put("approval", "0"); // 加入群是否需要批准 1:需要批准 0:不需要
		params.put("member_limit", "50"); // 允许成员人数，0不限制
		params.put("tags", ""); // 群标签，搜索用
		if (!TextUtils.isEmpty(portraitUrl)) {
			params.put("logo", portraitUrl); // 群LOGO地址
		}

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.REGISTER_GROUP, BaseEntity.class,
				params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.CREATE_GROUP;
						mHandler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.DATA_ERROR;
						mHandler.sendMessage(message);
					}
				});
	}

	/**
	 * 请求上传群头像
	 * 
	 * @param imageData
	 */
	private void requestUploadPortrait(String path) {
		try {
			byte[] imgDatas = FileUtil.getBytes(new File(path));

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN)));
			params.add(new BasicNameValuePair("userid", UserPreferenceWrapper.getMemberId()));
			params.add(new BasicNameValuePair("ext", "jpg"));
			params.add(new BasicNameValuePair("dir", "10"));

			String json = HttpUtil.uploadFile(HttpUtil.URI + HttpUtil.SET_GROUP_LOGO, params,
					imgDatas);
			HashMap<String, Object> map = new HashMap<String, Object>();
			JSONObject obj = new JSONObject(json);
			map.put("success", obj.getInt("success"));
			map.put("errormsg", obj.getString("errormsg"));
			JSONObject data_obj = obj.getJSONObject("data");
			if (null != data_obj) {
				map.put("url", data_obj.getString("grouplogo_pic"));
			}
			Message message = new Message();
			message.obj = map;
			message.what = IResult.SET_GROUP_LOGO;
			mHandler.sendMessage(message);
		} catch (IOException e) {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
			e.printStackTrace();
		} catch (Exception e) {
			mHandler.sendEmptyMessage(IResult.DATA_ERROR);
			e.printStackTrace();
		}
	}

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case IResult.CREATE_GROUP:
					destroyDialog();
					final BaseEntity entity = (BaseEntity) msg.obj;
					showToast(entity.getErrormsg());
					if (entity.isSuccess()) {
						sendBroadcast(new Intent(ActionFilter.REQUEST_DOCTOR_GROUP_LIST));
						finish();
					}
					break;
				case IResult.SET_GROUP_LOGO:
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					String portraitUrl = (String) map.get("url");
					OnInputCallBack inputCallBack = ((OnInputCallBack) mFragments[0]);
					String groupName = inputCallBack.getInput();
					requestCreateGroup(groupName, portraitUrl);
					break;
				case IResult.DATA_ERROR:
					destroyDialog();
					showToast("服务器数据错误");
				case IResult.NET_ERROR:
					destroyDialog();
					showToast("网络未连接");
					break;
				default:
					destroyDialog();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	});

}
