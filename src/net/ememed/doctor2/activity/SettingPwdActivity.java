package net.ememed.doctor2.activity;

import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.fragment.PwdSettingFragment;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.widget.MyProgressDialog;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CustomViewPager;
import com.viewpagerindicator.TabPageIndicator;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingPwdActivity extends BasicActivity
// implements OnPageChangeListener
{

	// private PagerAdapter mPagerAdapter = null;
	// private CustomViewPager mViewPager = null;
	// private TabPageIndicator mIndicator = null;
	private MyProgressDialog dialog;
	// private Button btn;
	private EditText et_old_pwd;
	private EditText et_new_pwd;
	private EditText et_confirm_pwd;
	private Button btn_save;
	private String oldPwd;
	private String newPwd;
	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// setContentView(R.layout.activity_viewpager_setting_pwd);
		setContentView(R.layout.fragment_pwdsetting);
		// btn = (Button) findViewById(R.id.btn_addhealth);
		// btn.setVisibility(View.GONE);
		et_old_pwd = (EditText) findViewById(R.id.et_old_pwd);
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_confirm_pwd = (EditText) findViewById(R.id.et_confirm_pwd);
		btn_save = (Button) findViewById(R.id.btn_save);
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.act_title_stting_pwd));
		manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		// String[] titles = getResources().getStringArray(R.array.pwd_type);
		// mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);

		// for (int i = 0; i < titles.length; i++) {
		// Bundle bundle = new Bundle();
		// bundle.putString("fragmentKey", titles[i]);
		// mPagerAdapter.addFragment(titles[i], PwdSettingFragment.class,
		// bundle);
		// }
		// mViewPager = (CustomViewPager) findViewById(R.id.pager);
		// mViewPager.setOffscreenPageLimit(2);//缓存多少个页面
		// mViewPager.setAdapter(mPagerAdapter);
		// mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		// mIndicator.setViewPager(mViewPager);
		// mIndicator.setOnPageChangeListener(this);

	}

	@Override
	protected void getData() {
		super.getData();
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

	/**修改密码*/
	private void amendPwd() {
		try {
			oldPwd = et_old_pwd.getText().toString();
			newPwd = et_new_pwd.getText().toString();
			String confirmPwd = et_confirm_pwd.getText().toString();

			if ("".equals(oldPwd)) {
				showToast("原密码不能为空");
				throw new Exception("原密码不能为空");
			}
			if ("".equals(newPwd)) {
				showToast("新密码不能为空");
				throw new Exception("新密码不能为空");
			}
			if (newPwd.length() < 4 || newPwd.length() > 16) {
				showToast("密码长度错误");
				throw new Exception("密码长度错误");
			}
			if (!newPwd.equals(confirmPwd)) {
				showToast("密码确认不一致");
				throw new Exception("密码确认不一致");
			}

			if (NetWorkUtils.detect(this)) {
				hideKeyboard();
				loading(null);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("token",
						SharePrefUtil.getString(Conast.ACCESS_TOKEN));
				params.put("doctorid",
						SharePrefUtil.getString(Conast.Doctor_ID));
				params.put("oldpwd", oldPwd);
				params.put("newpwd", newPwd);
				params.put("pwdtype", "1");
				MyApplication.volleyHttpClient.postWithParams(
						HttpUtil.set_doctor_password, PersonInfo.class, params,
						new Response.Listener() {
							@Override
							public void onResponse(Object response) {

								Message message = new Message();
								message.obj = response;
								message.what = IResult.RESULT;
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
			} else {
				handler.sendEmptyMessage(IResult.NET_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			finish();
		} else if (view.getId() == R.id.btn_save){
			amendPwd();
		}
	}

	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.RESULT:
				destroyDialog();
				PersonInfo map_2 = (PersonInfo) msg.obj;
				int success_2 = map_2.getSuccess();
				if (success_2 == 0) {
					showToast("原密码输入不正确");
					et_old_pwd.setText("");
//					et_new_pwd.setText("");
//					et_confirm_pwd.setText("");
				} else {
					showToast("密码修改成功");
//					SharePrefUtil.putString("newpwd", newPwd);
//					SharePrefUtil.putString("oldpwd", oldPwd);
//					SharePrefUtil.commit();
				}
				break;
			case IResult.END:

				break;
			case IResult.NET_ERROR:
				showMessage(IMessage.NET_ERROR);
				break;

			case IResult.DATA_ERROR:
				showMessage(IMessage.DATA_ERROR);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	// class PagerAdapter extends FragmentPagerAdapter {
	//
	// private Context mContext;
	// private final ArrayList<FragmentInfo> fragments = new
	// ArrayList<FragmentInfo>();
	// private FragmentManager fm;
	// private ArrayList<Fragment> fra_list;
	//
	// protected final class FragmentInfo {
	// private final String tag;
	// private final Class<?> clss;
	// private final Bundle args;
	//
	// protected FragmentInfo(String _tag, Class<?> _class, Bundle _args) {
	// tag = _tag;
	// clss = _class;
	// args = _args;
	// }
	// }
	//
	// public PagerAdapter(FragmentManager fm, Context context) {
	// super(fm);
	// this.fm = fm;
	// this.mContext = context;
	// fra_list = new ArrayList<Fragment>();
	// }
	//
	//
	// public void clearFragment(){
	// FragmentTransaction ft = fm.beginTransaction();
	// for(Fragment f:fra_list){
	// ft.remove(f);
	// }
	// ft.commit();
	// ft=null;
	// fm.executePendingTransactions();//立刻执行以上命令（commit）
	// }
	//
	// public void addFragment(String tag, Class<?> clss, Bundle args) {
	// FragmentInfo fragmentInfo = new FragmentInfo(tag, clss, args);
	// fragments.add(fragmentInfo);
	// }
	//
	// @Override
	// public Fragment getItem(int arg0) {
	// FragmentInfo fragmentInfo = fragments.get(arg0);
	// Fragment fra = Fragment.instantiate(mContext,
	// fragmentInfo.clss.getName(), fragmentInfo.args);
	// if(!fra_list.contains(fra))
	// fra_list.add(fra);
	// // if
	// (fragmentInfo.clss.getName().equals(CircleInfoFragment.class.getName()))
	// {
	// // mCircleFragment = (CircleInfoFragment) fra;
	// // } else if
	// (fragmentInfo.clss.getName().equals(CircleAddressBookFragment.class.getName()))
	// {
	// // mAddressBookFragment = (CircleAddressBookFragment) fra;
	// // }
	// return fra;
	// }
	// @Override
	// public CharSequence getPageTitle(int position) {
	// return fragments.get(position).tag;
	// }
	// @Override
	// public int getCount() {
	// return fragments.size();
	// }
	//
	// }

	// @Override
	// public void onPageScrollStateChanged(int arg0) {
	// }
	//
	// @Override
	// public void onPageScrolled(int arg0, float arg1, int arg2) {
	//
	// }
	//
	// @Override
	// public void onPageSelected(int arg0) {
	//
	// }
	/** 信息提示 */
	public void showMessage(String msg) {
		Builder builder = new Builder(this);
		Dialog dialog = builder
				.setTitle(getString(R.string.system_info))
				.setMessage(msg)
				.setPositiveButton(getString(R.string.add_health_record_know),
						null).create();
		dialog.setCancelable(false);
		if (!finish) {
			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish = true;
	}

	private boolean finish = false;

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
