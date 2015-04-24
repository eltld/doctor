package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.ViewPagerAdapter;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.JiahaoSettingEntry;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.widget.SwitchButton;

import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 服务设置--加号服务
 * 
 * @author chen
 * */
public class ServiceJiahaoSettingActivity extends BasicActivity implements ViewPager.OnPageChangeListener{

	private EditText et_jiahao_location;
	private EditText et_jiahao_num;
	private TextView tv_start_time;
	private TextView tv_end_time;
	private SwitchButton bt_switch;

	private ViewPager vp;
	private ArrayList<View> day_views;
	private ImageView[] dots;
	private int currentIndex = 0;
	private CheckBox checkbox_day_1;
	private CheckBox checkbox_day_2;
	private CheckBox checkbox_day_3;
	private CheckBox checkbox_day_4;
	private CheckBox checkbox_day_5;
	private CheckBox checkbox_day_6;
	private CheckBox checkbox_day_7;
	private ViewPagerAdapter vpAdapter;
	private ImageView iv_time_anytime_setting_dot;
	private ImageView iv_time_custom_setting_dot;
	private String title;
	private View ll_time_setting_detail;
	private JiahaoSettingEntry setting_detail;
	private Button btn_addhealth;
	private String fromActivity;
	private PullToRefreshLayout mPullToRefreshLayout;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.service_jiahao_setting);
		title = getIntent().getStringExtra("title");
		fromActivity = getIntent().getStringExtra("from");
		setting_detail = (JiahaoSettingEntry)getIntent().getSerializableExtra("setting_content");
	}

	@Override
	protected void setupView() {
		super.setupView();
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(title);
		btn_addhealth = (Button)findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setup(mPullToRefreshLayout);
		
		bt_switch = (SwitchButton) findViewById(R.id.bt_switch);
		et_jiahao_location = (EditText) findViewById(R.id.et_jiahao_location);
		et_jiahao_num = (EditText) findViewById(R.id.et_jiahao_num);
		tv_start_time = (TextView) findViewById(R.id.tv_start_time);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		iv_time_anytime_setting_dot = (ImageView) findViewById(R.id.iv_time_anytime_setting_dot);
		iv_time_custom_setting_dot = (ImageView) findViewById(R.id.iv_time_custom_setting_dot);
		iv_time_anytime_setting_dot.setEnabled(true);
		iv_time_custom_setting_dot.setEnabled(false);
		
		ll_time_setting_detail = (LinearLayout) findViewById(R.id.ll_time_setting_detail);
		
		day_views = new ArrayList<View>();
		View view_day_page1 = LayoutInflater.from(this).inflate(R.layout.layout_day_select, null);
		View view_day_page2 = LayoutInflater.from(this).inflate(R.layout.layout_day_select_2, null);
		day_views.add(view_day_page1);	
		day_views.add(view_day_page2);
		checkbox_day_1 = (CheckBox)view_day_page1.findViewById(R.id.checkbox1);
		checkbox_day_2 = (CheckBox)view_day_page1.findViewById(R.id.checkbox2);
		checkbox_day_3 = (CheckBox)view_day_page1.findViewById(R.id.checkbox3);
		checkbox_day_4 = (CheckBox)view_day_page1.findViewById(R.id.checkbox4);
		checkbox_day_5 = (CheckBox)view_day_page1.findViewById(R.id.checkbox5);
		checkbox_day_6 = (CheckBox)view_day_page2.findViewById(R.id.checkbox6);
		checkbox_day_7 = (CheckBox)view_day_page2.findViewById(R.id.checkbox7);
		
		vpAdapter = new ViewPagerAdapter(day_views);
		vp = (ViewPager) findViewById(R.id.viewpager_day);
		vp.setAdapter(vpAdapter);
		vp.setOnPageChangeListener(this);
		LinearLayout ll_day_dot = (LinearLayout) findViewById(R.id.ll_day_dot);

		dots = new ImageView[day_views.size()];

		for (int i = 0; i < day_views.size(); i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            iv.setPadding(15, 15, 15, 15);
            iv.setClickable(true);
            iv.setImageResource(R.drawable.iv_day_dot);
            ll_day_dot.addView(iv);
			dots[i] = iv;
			dots[i].setEnabled(true);
		}
		currentIndex  = 0;
		dots[currentIndex].setEnabled(false);
		
		tv_start_time.setText(setting_detail.getStart_time());
		tv_end_time.setText(setting_detail.getEnd_time());
		et_jiahao_num.setText(setting_detail.getJiahao_num()+"");
		et_jiahao_location.setText(setting_detail.getJiahao_hospital());
		if (setting_detail.getJiahao_time_status() == 1) {
			ll_time_setting_detail.setVisibility(View.GONE);
			iv_time_anytime_setting_dot.setEnabled(true);
			iv_time_custom_setting_dot.setEnabled(false);
		} else if (setting_detail.getJiahao_time_status() == 2) {
			ll_time_setting_detail.setVisibility(View.VISIBLE);
			iv_time_anytime_setting_dot.setEnabled(false);
			iv_time_custom_setting_dot.setEnabled(true);
		} else {
			btn_addhealth.setVisibility(View.VISIBLE);
			ll_time_setting_detail.setVisibility(View.GONE);
			iv_time_anytime_setting_dot.setEnabled(false);
			iv_time_custom_setting_dot.setEnabled(false);
		}
		if (!TextUtils.isEmpty(setting_detail.getWeek_repeat())) {
			String[] day_repeat = setting_detail.getWeek_repeat().split(",");
			if (null != day_repeat) {
				for (int i = 0; i < day_repeat.length; i++) {
					if (day_repeat[i].equals("1")) {
						checkbox_day_1.setChecked(true);
					} else if (day_repeat[i].equals("2")){
						checkbox_day_2.setChecked(true);
					} else if (day_repeat[i].equals("3")){
						checkbox_day_3.setChecked(true);
					} else if (day_repeat[i].equals("4")){
						checkbox_day_4.setChecked(true);
					} else if (day_repeat[i].equals("5")){
						checkbox_day_5.setChecked(true);
					} else if (day_repeat[i].equals("6")){
						checkbox_day_6.setChecked(true);
					} else if (day_repeat[i].equals("7")){
						checkbox_day_7.setChecked(true);
					}
				}
			}
		}
		
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				try {
					InputMethodManager localInputMethodManager = (InputMethodManager) getSystemService("input_method");
					IBinder localIBinder = getWindow().peekDecorView().getWindowToken();
					localInputMethodManager.hideSoftInputFromWindow(localIBinder, 0);	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 300);
		
	}

	private String start_time = null;
	private String end_time = null;
	private StringBuffer week_repeat = null;
	private String jiahao_num  = null;
	private String jiahao_hospital = null;
	
	private void setJiahao(){
		jiahao_num = et_jiahao_num.getText().toString();
		jiahao_hospital = et_jiahao_location.getText().toString();
		
		if (TextUtils.isEmpty(jiahao_hospital)) {
			showToast(getString(R.string.service_setting_shangmen));
			return;
		}
		if (TextUtils.isEmpty(jiahao_num)) {
		 	showToast(getString(R.string.service_setting_jiahao_num));
		 	return;
		}
		if (TextUtil.isPriceIllegal(jiahao_num)) {
			showToast(getString(R.string.service_setting_jiahao_num_illegal));
			return;
		}
		week_repeat = new StringBuffer();
		if (checkbox_day_1.isChecked()) {
			week_repeat.append("1");	
			week_repeat.append(",");	
		}
		if (checkbox_day_2.isChecked()){
			week_repeat.append("2");	
			week_repeat.append(",");
		}
		if (checkbox_day_3.isChecked()){
			week_repeat.append("3");
			week_repeat.append(",");
		}
		if (checkbox_day_4.isChecked()){
			week_repeat.append("4");
			week_repeat.append(",");
		}
		if (checkbox_day_5.isChecked()){
			week_repeat.append("5");
			week_repeat.append(",");
		}
		if (checkbox_day_6.isChecked()){
			week_repeat.append("6");
			week_repeat.append(",");
		} 
		if (checkbox_day_7.isChecked()){
			week_repeat.append("7");
			week_repeat.append(",");
		}
		if (week_repeat.toString().length()>0) {
			week_repeat.delete(week_repeat.toString().length()-1,week_repeat.toString().length());	
		}
		
		if (setting_detail.getJiahao_time_status() == 2) {

			if(TextUtils.isEmpty(week_repeat.toString())){
				showToast(getString(R.string.service_setting_time_not_null_date));
				return;
			}
			if (TextUtils.isEmpty(tv_start_time.getText().toString())) {
				showToast(getString(R.string.service_setting_time_not_null_start));
				return;
			}
			if (TextUtils.isEmpty(tv_end_time.getText().toString())) {
				showToast(getString(R.string.service_setting_time_not_null_end));
				return;
			}	

		} else if(setting_detail.getJiahao_time_status() == 1) {
		} else {
			showToast(getString(R.string.service_setting_time));
			return;
		}
		
		start_time = tv_start_time.getText().toString();
		end_time = tv_end_time.getText().toString();
		
		String start_time_result = "";
		String end_time_result = "";
		if (!TextUtils.isEmpty(start_time)) {
			start_time_result = start_time.replace(":", "");	
		}
		if (!TextUtils.isEmpty(end_time)) {
			end_time_result = end_time.replace(":", "");	
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("start_time", start_time_result);
		params.put("end_time", end_time_result);
		params.put("week_repeat", week_repeat.toString());
		params.put("jiahao_num", jiahao_num);
		params.put("jiahao_hospital", jiahao_hospital);
		params.put("jiahao_time_status", setting_detail.getJiahao_time_status()+"");

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_jiahao,
				ResultInfo.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.SERVICE_SET;
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
	/**
	 * 是否启用加号设置
	 * 0、关闭
	 * 1、启用
	 * */
	private void enableJiahaoSetting(final int operation){
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					setting_detail.setEnable_jiahao(operation);
//					clientContent.enableJiahao(SharePrefUtil.getString("doctorId"), operation+"");
//				} catch (IOException e) {
//					handler.sendEmptyMessage(IResult.NET_ERROR);
//				} catch (Exception e) {
//					handler.sendEmptyMessage(IResult.DATA_ERROR);
//				}
//				super.run();
//			}
//		}.start();
	}
	@Override
	protected void addListener() {
		super.addListener();
		bt_switch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					enableJiahaoSetting(1);
				} else {
					enableJiahaoSetting(0);
				}
			}
		});
	}

	public void doClick(View view) {
		
		int id = view.getId();
		if (id == R.id.btn_back) {
			Intent intent = new Intent(ServiceJiahaoSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_detail);
			intent.putExtra("setting_type", ServiceJiahaoSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		} else if (id == R.id.ll_time_anytime_setting) {
			setting_detail.setJiahao_time_status(1);
			iv_time_anytime_setting_dot.setEnabled(true);
			iv_time_custom_setting_dot.setEnabled(false);
			ll_time_setting_detail.setVisibility(View.GONE);
		} else if (id == R.id.ll_time_custom_setting) {
			setting_detail.setJiahao_time_status(2);
			iv_time_anytime_setting_dot.setEnabled(false);
			iv_time_custom_setting_dot.setEnabled(true);
			ll_time_setting_detail.setVisibility(View.VISIBLE);
		} else if (id == R.id.ll_time_start) {
			//用来获取日期和时间的  
			Dialog dialog = null;
			Calendar calendar = Calendar.getInstance();
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {  
			      
			    @Override  
			    public void onTimeSet(TimePicker timerPicker,  
			            int hourOfDay, int minute) {  
			    	tv_start_time.setText(hourOfDay + ":" + (minute<10?("0"+minute):minute));  
			    }  
			};
			dialog = new TimePickerDialog(this, timeListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);   //是否为二十四制  
			dialog.show();
		} else if (id == R.id.ll_time_end) {
			//用来获取日期和时间的  
			Dialog dialog_end = null;
			Calendar calendar_end = Calendar.getInstance();
			TimePickerDialog.OnTimeSetListener timeListener_end = new TimePickerDialog.OnTimeSetListener() {  
			      
			    @Override  
			    public void onTimeSet(TimePicker timerPicker,  
			            int hourOfDay, int minute) {  
			    	tv_end_time.setText(hourOfDay + ":" + (minute<10?("0"+minute):minute));  
			    }
			};
			dialog_end = new TimePickerDialog(this, timeListener_end,calendar_end.get(Calendar.HOUR_OF_DAY),calendar_end.get(Calendar.MINUTE),true);   //是否为二十四制  
			dialog_end.show();
		} else if (id == R.id.btn_addhealth) {
			setJiahao();
		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ServiceJiahaoSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_detail);
			intent.putExtra("setting_type", ServiceJiahaoSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
				case IResult.FINISHED://设置开关
					HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
					int success = (Integer) map.get("success");
					if(success == 0){
						Toast.makeText(this, (CharSequence) map.get("errormsg"), Toast.LENGTH_SHORT).show();
						return;
					} else {
//						System.out.println("setting success ");
						if (setting_detail.getEnable_jiahao() == 1) {
							btn_addhealth.setVisibility(View.VISIBLE);
						} else if (setting_detail.getEnable_jiahao() == 0) {
							btn_addhealth.setVisibility(View.GONE);
						}
					}
					break;
				case IResult.SERVICE_SET:// 服务设置
					ResultInfo map_2 = (ResultInfo) msg.obj;
					int success2 = map_2.getSuccess();
					if(success2 == 0){
						showToast(map_2.getErrormsg());
						return;
					} else {
						showToast(getString(R.string.service_setting_time_success));
						setting_detail.setJiahao_num(Integer.valueOf(jiahao_num));
						setting_detail.setJiahao_hospital(jiahao_hospital);
						setting_detail.setStart_time(start_time);
						setting_detail.setEnd_time(end_time);
						
						Intent intent = null;
						if (!TextUtils.isEmpty(fromActivity) && fromActivity.equals(ServicePackageSettingActivity.class.getSimpleName())) {
							intent = new Intent(ServiceJiahaoSettingActivity.this,ServicePackageSettingActivity.class);
						} else {
							intent = new Intent(ServiceJiahaoSettingActivity.this,ServiceAllSettingActivity.class);
						}
						intent.putExtra("setting_content", setting_detail);
						intent.putExtra("setting_type", ServiceJiahaoSettingActivity.class.getSimpleName());
						setResult(RESULT_OK, intent);
						finish();
					}
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		 setCurrentDot(arg0);
	}
	
	private void setCurrentDot(int position) {
		if (position < 0 || position > day_views.size() - 1
				|| currentIndex == position) {
			return;
		}
		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = position;
	}
	
	/**选择 日期*/
	private String getSelectedDays(){
		StringBuffer str_day = new StringBuffer();
		if (checkbox_day_1.isChecked()) {
			str_day.append(1);
			str_day.append(",");
		}
		if (checkbox_day_2.isChecked()) {
			str_day.append(2);
			str_day.append(",");
		}
		if (checkbox_day_3.isChecked()) {
			str_day.append(3);
			str_day.append(",");
		}
		if (checkbox_day_4.isChecked()) {
			str_day.append(4);
			str_day.append(",");
		}
		if (checkbox_day_5.isChecked()) {
			str_day.append(5);
			str_day.append(",");
		}
		if (checkbox_day_6.isChecked()) {
			str_day.append(6);
			str_day.append(",");
		}
		if (checkbox_day_7.isChecked()) {
			str_day.append(7);
			str_day.append(",");
		}
		return str_day.length()>0?(str_day.toString().substring(0, str_day.length()-1)):"";
	}
}
