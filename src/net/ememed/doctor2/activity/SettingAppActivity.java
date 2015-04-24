package net.ememed.doctor2.activity;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.VersionInfo;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.MenuDialog;
import net.ememed.doctor2.widget.SwitchButton;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
/**应用设置*/
public class SettingAppActivity extends BasicActivity{
	private TextView top_title;
	private SwitchButton bt_switch_system_notice;
	private EMChatOptions chatOptions;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.layout_setting_app_info);
	}

	@Override
	protected void setupView() {
		super.setupView();
		chatOptions = EMChatManager.getInstance().getChatOptions();
		top_title = (TextView)findViewById(R.id.top_title);
		top_title.setText(getString(R.string.act_title_stting_app));
		bt_switch_system_notice = (SwitchButton)findViewById(R.id.bt_switch_system_notice);
		bt_switch_system_notice.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					chatOptions.setNotificationEnable(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					SharePrefUtil.putBoolean(Conast.EM_SETTING_SAVE_MSG_NOTIFY, false);
					SharePrefUtil.commit();
				} else {
					chatOptions.setNotificationEnable(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					SharePrefUtil.putBoolean(Conast.EM_SETTING_SAVE_MSG_NOTIFY, true);
					SharePrefUtil.commit();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}


	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.lt_edit_pwd) {
			startActivity(new Intent(this,SettingPwdActivity.class));
		} else if (id == R.id.btn_back) {
			finish();
		} else if(id == R.id.lt_check_version_update) {
			update();
		}
	}
	
	public void update() {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在检查更新");
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.forceUpdate(this);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				if (isFinishing())
					return;
				pd.dismiss();
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(SettingAppActivity.this, updateInfo);
					break;
				case UpdateStatus.No: // has no update
					Toast.makeText(SettingAppActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.NoneWifi: // none wifi
					Toast.makeText(SettingAppActivity.this, "没有wifi连接， 只在wifi下更新",
							Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.Timeout: // time out
					Toast.makeText(SettingAppActivity.this, "网络请求超时", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
	}
	
//	private void checkVersionAndUpdate () {
//		try {
//			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("imei", PublicUtil.getDeviceUuid(SettingAppActivity.this));
//			params.put("appversion", PublicUtil.getVersionName(SettingAppActivity.this));
//			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
//			params.put("membertype", "doctor");
//			params.put("appid", "5");//正式是5  测试是2
//			params.put("channel", "android");
//			params.put("mobileparam", MyApplication.getInstance().getMobileParam(this));
//			MyApplication.volleyHttpClient.postWithParams(
//					HttpUtil.get_version_info, VersionInfo.class, params,
//					new Response.Listener() {
//						@Override
//						public void onResponse(Object response) {
//							VersionInfo info = (VersionInfo) response;
//							
//							if (info.getSuccess() == 1) {
//								int newVersionCode = Integer.valueOf(info.getData().getVERSIONCODE());
//								int oldVersionCode = PublicUtil.getVersionCode(SettingAppActivity.this);
////								Log.d("chenhj,setting", "oldVersionCode: " + oldVersionCode);
//								if (newVersionCode > oldVersionCode) {
//									alertVersionUpdate(info);
//								} else {
//									SettingAppActivity.this.showToast("您已经是最新版本，无需升级");
//								}
//							}
//						}
//					}, new Response.ErrorListener() {
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_version_info);
//						}
//					});
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}
	
	private void alertVersionUpdate(final VersionInfo info) {
		View view = LayoutInflater.from(this).inflate(R.layout.version_update_info, null);
		final TextView tvVersionName = (TextView) view.findViewById(R.id.tv_version_name);
		final TextView tvTime = (TextView) view.findViewById(R.id.tv_update_time);
		final TextView tv_upgrade_title = (TextView) view.findViewById(R.id.tv_upgrade_title);
		tv_upgrade_title.setVisibility(View.GONE);
		final TextView tvSize = (TextView) view.findViewById(R.id.tv_size);
		tvVersionName.setText("薏米医生(Android) "+ info.getData().getVERSIONNAME());
		tvTime.setText("更新时间:" + info.getData().getUPDATETIME());
		tvSize.setText("文件大小:" + info.getData().getAPPSIZE());
		final TextView tv_new_feature = (TextView) view.findViewById(R.id.tv_new_feature);
		boolean not_force_update = info.getData().getForce_update() == 1 ? false: true;// 是否强制升级
		MenuDialog.Builder alert = new MenuDialog.Builder(this);
		MenuDialog dialog = null;
		if (TextUtils.isEmpty(info.getData().getCONTENT())) {
			tv_upgrade_title.setVisibility(View.GONE);
			tv_new_feature.setVisibility(View.GONE);
		} else {
			tv_upgrade_title.setVisibility(View.VISIBLE);
			tv_new_feature.setVisibility(View.VISIBLE);
			tv_new_feature.setText(info.getData().getCONTENT());
		}
		if (not_force_update) {
			dialog = alert.setTitle(getString(R.string.main_notice))
					.setContentView(view)
					.setPositiveButton(getString(R.string.main_shenji),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										Uri uri = Uri.parse(info.getData().getAPPFILE());
										Intent intent = new Intent(Intent.ACTION_VIEW, uri);
										startActivity(intent);	
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							})
					.setNegativeButton(getString(R.string.main_next),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
		} else {
			dialog = alert
					.setTitle(getString(R.string.main_notice))
					.setContentView(view)
					.setPositiveButton(getString(R.string.main_shenji),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										Uri uri = Uri.parse(info.getData().getAPPFILE());
										Intent intent = new Intent(Intent.ACTION_VIEW, uri);
										startActivity(intent);	
										finish();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).create();
		}

		dialog.setCanceledOnTouchOutside(not_force_update);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (KeyEvent.KEYCODE_BACK == keyCode) {
					return true;
				}
				return false;
			}
		});
		dialog.show();
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
}
