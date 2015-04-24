package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.adapter.ViewPagerAdapter;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.entity.ShangmenSettingEntry;
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
 * 服务设置--上门会诊服务
 * 
 * @author chen
 * */
public class ServiceShangmenSettingActivity extends BasicActivity implements ViewPager.OnPageChangeListener{

	private SwitchButton bt_switch;
	private EditText et_shangmen_price;
	private EditText et_shangmen_area;
	private TextView tv_start_time;
	private TextView tv_end_time;
	
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
	private LinearLayout ll_time_setting_detail;
	private ShangmenSettingEntry setting_detail;
	private Button btn_addhealth;
	private String fromActivity;
	private LinearLayout ll_switch_shangmen;
	private LinearLayout ll_price_shangmen;
	private PullToRefreshLayout mPullToRefreshLayout;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.service_shangmen_setting);
		title = getIntent().getStringExtra("title");
		fromActivity = getIntent().getStringExtra("from");
		setting_detail = (ShangmenSettingEntry)getIntent().getSerializableExtra("setting_content");
	}

	@Override
	protected void setupView() {
		
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(title);
		btn_addhealth = (Button)findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		
		super.setupView();

		ll_switch_shangmen = (LinearLayout) findViewById(R.id.ll_switch_shangmen);
		bt_switch = (SwitchButton) findViewById(R.id.bt_switch);
		ll_price_shangmen  = (LinearLayout) findViewById(R.id.ll_price_shangmen);
		et_shangmen_price = (EditText) findViewById(R.id.et_shangmen_price);
		et_shangmen_area = (EditText) findViewById(R.id.et_shangmen_area);
		tv_start_time = (TextView) findViewById(R.id.tv_start_time);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		iv_time_anytime_setting_dot = (ImageView) findViewById(R.id.iv_time_anytime_setting_dot);
		iv_time_custom_setting_dot = (ImageView) findViewById(R.id.iv_time_custom_setting_dot);
		iv_time_anytime_setting_dot.setEnabled(true);
		iv_time_custom_setting_dot.setEnabled(false);
		
		et_shangmen_price.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable edt) {
				String temp = edt.toString();
				int posDot = temp.indexOf(".");
				if (posDot < 0)
					return;
				if (temp.length() - posDot - 1 > 2) {
					edt.delete(posDot + 3, posDot + 4);
				}
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});		
		
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

		if (setting_detail.getEnable_shangmen() == 1) {
			bt_switch.setChecked(true);
		} else {
			bt_switch.setChecked(false);
		}
		et_shangmen_price.setText(setting_detail.getPrice_shangmen());
		et_shangmen_area.setText(setting_detail.getLocation_shangmen());
		tv_start_time.setText(setting_detail.getStart_time());
		tv_end_time.setText(setting_detail.getEnd_time());
		if (setting_detail.getShangmen_time_status() == 1) {
			ll_time_setting_detail.setVisibility(View.GONE);
			iv_time_anytime_setting_dot.setEnabled(true);
			iv_time_custom_setting_dot.setEnabled(false);
		} else if (setting_detail.getShangmen_time_status() == 2) {
			ll_time_setting_detail.setVisibility(View.VISIBLE);
			iv_time_anytime_setting_dot.setEnabled(false);
			iv_time_custom_setting_dot.setEnabled(true);
		} else {
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
		
		if (!TextUtils.isEmpty(fromActivity) && fromActivity.equals(ServicePackageSettingActivity.class.getSimpleName())) {
			ll_switch_shangmen.setVisibility(View.GONE);
			ll_price_shangmen.setVisibility(View.GONE);
			btn_addhealth.setVisibility(View.VISIBLE);
		}
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable().setup(mPullToRefreshLayout);
	
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

	@Override
	protected void addListener() {
		super.addListener();
		bt_switch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					enableShangmen(1);
				} else {
					enableShangmen(0);
				}
			}
		});
	}

	public void doClick(View view) {

		int id = view.getId();
		if (id == R.id.btn_back) {
			Intent intent = new Intent(ServiceShangmenSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_detail);
			intent.putExtra("setting_type", ServiceShangmenSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		} else if (id == R.id.ll_time_anytime_setting) {
			setting_detail.setShangmen_time_status(1);
			iv_time_anytime_setting_dot.setEnabled(true);
			iv_time_custom_setting_dot.setEnabled(false);
			ll_time_setting_detail.setVisibility(View.GONE);
		} else if (id == R.id.ll_time_custom_setting) {
			setting_detail.setShangmen_time_status(2);
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
			setShangmen();
		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(ServiceShangmenSettingActivity.this,ServiceAllSettingActivity.class);
			intent.putExtra("setting_content", setting_detail);
			intent.putExtra("setting_type", ServiceShangmenSettingActivity.class.getSimpleName());
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private String price = null;
	private String location_shangmen = null;
	private String start_time = null;
	private String end_time = null;
	private StringBuffer week_repeat = null;
	
	private void setShangmen() {
		price = et_shangmen_price.getText().toString();
		location_shangmen = et_shangmen_area.getText().toString();
		if(!TextUtils.isEmpty(fromActivity) && fromActivity.equals(ServiceAllSettingActivity.class.getSimpleName())){
			if (TextUtils.isEmpty(price)) {
				showToast(getString(R.string.service_setting_price));
				return;
			}	
			if (TextUtil.isPriceIllegal(price)) {
				showToast(getString(R.string.service_setting_price_illegal));
				return;
			}
		}
		if (TextUtils.isEmpty(location_shangmen)) {
			showToast(getString(R.string.service_setting_shangmen));
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
		
		if (setting_detail.getShangmen_time_status() == 2) {
			
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
			
		} else if (setting_detail.getShangmen_time_status() == 1){
			
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
		params.put("price", price);
		params.put("shangmen_time_status", setting_detail.getShangmen_time_status()+"");
		params.put("start_time", start_time_result);
		params.put("end_time", end_time_result);
		params.put("week_repeat",  week_repeat.toString());
		params.put("location_shangmen", location_shangmen);

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_shangmen,
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
	private int operation_result;
	/**
	 * 是否启用上门设置
	 * 0、关闭
	 * 1、启用
	 * */
	private void enableShangmen(final int operation){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("enable", operation+"");

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.enable_shangmen,
				ResultInfo.class, params, new Response.Listener() {

					@Override
					public void onResponse(Object response) {
						operation_result = operation;
						Message message = new Message();
						message.obj = response;
						message.what = IResult.FINISHED;
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
		super.onResult(msg);

		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.FINISHED:// 设置开关
				
				ResultInfo map = (ResultInfo) msg.obj;
				int success = map.getSuccess();
				if(success == 0){
					Toast.makeText(this, map.getErrormsg(), Toast.LENGTH_SHORT).show();
					return;
				} else {
					setting_detail.setEnable_shangmen(operation_result);
//					System.out.println("setting success ");
				}
				
				break;
			case IResult.SERVICE_SET:// 服务设置
				ResultInfo map_2 = (ResultInfo) msg.obj;
				int success_2 = map_2.getSuccess();
				if(success_2 == 0){
					Toast.makeText(this, map_2.getErrormsg(), Toast.LENGTH_SHORT).show();
					return;
				} else {
					showToast(getString(R.string.service_setting_time_success));
					setting_detail.setStart_time(start_time);
					setting_detail.setEnd_time(end_time);
					setting_detail.setLocation_shangmen(location_shangmen);
					setting_detail.setPrice_shangmen(price);
					setting_detail.setWeek_repeat(week_repeat.toString());
					
					Intent intent = null;
					if (!TextUtils.isEmpty(fromActivity) && fromActivity.equals(ServicePackageSettingActivity.class.getSimpleName())) {
						intent = new Intent(ServiceShangmenSettingActivity.this,ServicePackageSettingActivity.class);
					} else {
						intent = new Intent(ServiceShangmenSettingActivity.this,ServiceAllSettingActivity.class);
					}
					intent.putExtra("setting_content", setting_detail);
					intent.putExtra("setting_type", ServiceShangmenSettingActivity.class.getSimpleName());
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
