package net.ememed.doctor2.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.CallSettingEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.JiahaoSettingEntry;
import net.ememed.doctor2.entity.PacketPeriod;
import net.ememed.doctor2.entity.PacketSettingEntry;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.entity.ServicePacketSetInfo;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.entity.ShangmenSettingEntry;
import net.ememed.doctor2.entity.TextConsultSettingEntry;
import net.ememed.doctor2.entity.TijianSettingEntry;
import net.ememed.doctor2.entity.ZhuyuanSettingEntry;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.PublicUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.widget.InScrollGridView;
import net.ememed.doctor2.widget.MenuDialog;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 服务套餐设置
 * 
 * @author chen
 * */
public class ServicePackageSettingActivity extends BasicActivity implements OnItemSelectedListener {
	private static final String Tag = ServicePackageSettingActivity.class.getSimpleName();
	private static final int REQUEST_SINGLE_SERVICE = 1;
	private InScrollGridView gvSpace;
	private ServiceSettingEntry packetSettingEntry = null;
	private GridServiceAdapter adapter = null;
	private LinearLayout ll_more_package_cycle_parent;
	private Button btn_addhealth;
	private boolean hasSettingServiceItem;
	private PullToRefreshLayout mPullToRefreshLayout;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.service_package_setting);
		packetSettingEntry = (ServiceSettingEntry) getIntent().getSerializableExtra("packet_setting");
		if (packetSettingEntry == null) {
			packetSettingEntry = new ServiceSettingEntry();
		}
	}

	@Override
	protected void setupView() {
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.activity_title_service_packet_setting));
		btn_addhealth = (Button)findViewById(R.id.btn_addhealth);
		btn_addhealth.setVisibility(View.VISIBLE);
		btn_addhealth.setBackgroundDrawable(null);
		btn_addhealth.setText(getString(R.string.bt_save));
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.setup(mPullToRefreshLayout);
		
		setGridView();
		ll_more_package_cycle_parent =  (LinearLayout) findViewById(R.id.ll_more_package_cycle_parent);
		setupPeriodView();
		super.setupView();

		if(!SharePrefUtil.getBoolean("service_setting_checkbox")){
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(ServicePackageSettingActivity.this,ServiceSettingGuideActivity.class);
					startActivity(intent);
				}
			}, 300);
		}
	}

	public void setupPeriodView() {
		if (null != packetSettingEntry) {
			if (null != packetSettingEntry.getPacket_setting() && null!=packetSettingEntry.getPacket_setting().getPacket_period_list()) {
				for (int i = 0; i < packetSettingEntry.getPacket_setting().getPacket_period_list().size(); i++) {
					PacketPeriod packetPeriod = packetSettingEntry.getPacket_setting().getPacket_period_list().get(i);
					//添加更多周期
					final LinearLayout child_view_cycle = (LinearLayout) LayoutInflater.from(ServicePackageSettingActivity.this).inflate(R.layout.service_package_add_cycle_list_item, null);
					String[] cycle_array = getResources().getStringArray(R.array.service_cycle);
					ArrayAdapter<String> mSpCycleAdapter = new ArrayAdapter<String>(ServicePackageSettingActivity.this,PublicUtil.getSpinnerItemResId(),cycle_array);
					mSpCycleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					Spinner spinner_cycle = (Spinner) child_view_cycle.findViewById(R.id.sp_cycle_list);
					spinner_cycle.setOnItemSelectedListener(this);
					spinner_cycle.setAdapter(mSpCycleAdapter);
					spinner_cycle.setSelection(convertString2SpinnerSelection(packetPeriod));
					
					ImageView iv_delete_cycle  = (ImageView) child_view_cycle.findViewById(R.id.iv_delete_cycle);
					iv_delete_cycle.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ll_more_package_cycle_parent.removeView(child_view_cycle);
						}
					});
					EditText et_cycle_price = (EditText) child_view_cycle.findViewById(R.id.et_cycle_price);
					et_cycle_price.setText(packetPeriod.getPacket_period_price());
					et_cycle_price.addTextChangedListener(new TextWatcher() {
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
					
					ll_more_package_cycle_parent.addView(child_view_cycle);
					ll_more_package_cycle_parent.invalidate();
				}
			}
		}
	}

	private void setGridView() {
		gvSpace = (InScrollGridView) findViewById(R.id.gv_space);
		String[] titles = new String[] { "图文咨询", "预约通话", "上门会诊", "体检报告解读",
				"加号服务", "住院直通车" };
		int[] icons = new int[] { R.drawable.ic_service_setting_textconsult,
				R.drawable.ic_service_setting_phone, R.drawable.ic_service_setting_shangmen,
				R.drawable.ic_service_setting_tijian, R.drawable.ic_service_setting_jiahao,
				R.drawable.ic_service_setting_in_hospital };
		adapter = new GridServiceAdapter(titles, icons);
		gvSpace.setAdapter(adapter);
		gvSpace.setSelector(new ColorDrawable(Color.TRANSPARENT));
	}


	@Override
	protected void addListener() {
		gvSpace.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Logger.dout(ServicePackageSettingActivity.class," setOnItemClickListener!!");
			}
		});
		super.addListener();
	}

	public void doClick(View view) {
		if (view.getId() == R.id.btn_back) {
			if (hasSettingServiceItem) {
				Intent intent = new Intent(ServicePackageSettingActivity.this,ServiceAllSettingActivity.class);
				intent.putExtra("setting_content", packetSettingEntry);
				setResult(RESULT_OK, intent);
			}
			finish();
		} else if (view.getId() == R.id.ll_more_service_package_cycle) {
			addOrDeleteCycleView();
		} else if (view.getId() == R.id.btn_addhealth) {
			saveServicePacketSetting();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasSettingServiceItem) {
				Intent intent = new Intent(ServicePackageSettingActivity.this,ServiceAllSettingActivity.class);
				intent.putExtra("setting_content", packetSettingEntry);
				setResult(RESULT_OK, intent);
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void addOrDeleteCycleView(){
		//添加更多周期
		final LinearLayout child_view_cycle = (LinearLayout) LayoutInflater.from(ServicePackageSettingActivity.this).inflate(R.layout.service_package_add_cycle_list_item, null);
		String[] cycle_array = getResources().getStringArray(R.array.service_cycle);
		ArrayAdapter<String> mSpCycleAdapter = new ArrayAdapter<String>(ServicePackageSettingActivity.this,PublicUtil.getSpinnerItemResId(),cycle_array);
		mSpCycleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		Spinner spinner_cycle = (Spinner) child_view_cycle.findViewById(R.id.sp_cycle_list);
		spinner_cycle.setOnItemSelectedListener(this);
		spinner_cycle.setAdapter(mSpCycleAdapter);
		spinner_cycle.setSelection(0);
		
		ImageView iv_delete_cycle  = (ImageView) child_view_cycle.findViewById(R.id.iv_delete_cycle);
		iv_delete_cycle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll_more_package_cycle_parent.removeView(child_view_cycle);
			}
		});
		EditText et_cycle_price = (EditText) child_view_cycle.findViewById(R.id.et_cycle_price);
		et_cycle_price.addTextChangedListener(new TextWatcher() {
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
		ll_more_package_cycle_parent.addView(child_view_cycle);
		ll_more_package_cycle_parent.invalidate();
	}
	
	private void saveServicePacketSetting() {
		
		ArrayList<PacketPeriod> periodList = new ArrayList<PacketPeriod>();
		periodList.clear();
		for (int i = 0; i < ll_more_package_cycle_parent.getChildCount(); i++) {
			LinearLayout child_view_cycle = (LinearLayout) ll_more_package_cycle_parent.getChildAt(i);
			Spinner spinner_cycle = (Spinner) child_view_cycle.findViewById(R.id.sp_cycle_list);
			EditText et_cycle_price = (EditText) child_view_cycle.findViewById(R.id.et_cycle_price);
			if (et_cycle_price.getText() == null || TextUtils.isEmpty(et_cycle_price.getText().toString())) {
				showToast(getString(R.string.service_setting_packet_price));
				return;
			}
			if (TextUtil.isPriceIllegal(et_cycle_price.getText().toString())) {
				showToast(getString(R.string.service_setting_price_illegal));
				return;
			}
			String select_period = (String) spinner_cycle.getSelectedItem();
			String[] period= convertServicePeriod(select_period);
			PacketPeriod packetPeriodItem = new PacketPeriod();
			packetPeriodItem.setPacket_daytype(period[1]);
			packetPeriodItem.setPacket_daynum(Integer.valueOf(period[0]));
			packetPeriodItem.setPacket_period_price(et_cycle_price.getText().toString());
			periodList.add(packetPeriodItem);
		}
		
		packetSettingEntry.getPacket_setting().setPacket_period_list(periodList);
		Gson gson = new Gson();
		final String convertServicePeriod = gson.toJson(periodList);
		
		loading(null);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		params.put("packet_enable_textconsult", packetSettingEntry.getPacket_setting().getPacket_enable_textconsult()+"");
		params.put("packet_enable_call", packetSettingEntry.getPacket_setting().getPacket_enable_call()+"");
		params.put("packet_enable_tijian", packetSettingEntry.getPacket_setting().getPacket_enable_tijian()+"");
		params.put("packet_enable_shangmen", packetSettingEntry.getPacket_setting().getPacket_enable_shangmen()+"");
		params.put("packet_enable_zhuyuan", packetSettingEntry.getPacket_setting().getPacket_enable_zhuyuan()+"");
		params.put("packet_enable_jiahao", packetSettingEntry.getPacket_setting().getPacket_enable_jiahao()+"");
		params.put("packet_period_list", convertServicePeriod);

		MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_packet,
				ServicePacketSetInfo.class, params, new Response.Listener() {
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
	
		String[] packet_period = new String[2];//[0]=day [1]=type
		private String[] convertServicePeriod(String selectPeriodStr){
		if (!TextUtils.isEmpty(selectPeriodStr)) {
			if(selectPeriodStr.contains("周")){
				packet_period[0] = Integer.valueOf(selectPeriodStr.substring(0, selectPeriodStr.length()-1))*7+"";
				packet_period[1] = "0";
			} else if (selectPeriodStr.contains("月")) {
				packet_period[0] = selectPeriodStr.substring(0, 1);
				packet_period[1] = "1";
			} else if (selectPeriodStr.contains("半年")) {
				packet_period[0] = "6";
				packet_period[1] = "1";
			} else if (selectPeriodStr.contains("年")) {
				packet_period[0] = "1";
				packet_period[1] = "2";
			}
		}
		return packet_period;
	}
	
	private int convertString2SpinnerSelection(PacketPeriod packetPeriod){
		try {
			String[] cycle_array = getResources().getStringArray(R.array.service_cycle);
			String pk_daytype = packetPeriod.getPacket_daytype();
			int pk_daynum = packetPeriod.getPacket_daynum();
			String periodStr = null;
			if (pk_daytype.equals("0")) {
				periodStr = pk_daynum/7+"周";
			} else if (pk_daytype.equals("1")){
				 if(pk_daynum ==6){
					 periodStr = "半年";	 
				 } else {
					 periodStr = pk_daynum + "个月";
				 }
			} else if (pk_daytype.equals("2")){
				periodStr = pk_daynum +"年";
			}
			for (int i = 0; i < cycle_array.length; i++) {
				if (cycle_array[i].contains(periodStr)) {
					return i;
				}
			}
			return 0;			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Logger.dout(ServicePackageSettingActivity.class," onItemSelected!!");
		String cycle_selected =  (String) arg0.getItemAtPosition(position);
		if (null != cycle_selected) {
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
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
			switch (msg.what) {
				case IResult.SERVICE_SET:
					destroyDialog();
					ServicePacketSetInfo service = (ServicePacketSetInfo) msg.obj;
					if (null != service) {
						if (service.getSuccess() == 1) {
							Intent intent = new Intent(ServicePackageSettingActivity.this,ServiceAllSettingActivity.class);
							intent.putExtra("setting_content", packetSettingEntry);
							setResult(RESULT_OK, intent);
							finish();	
						} else {
							showToast(service.getErrormsg());
						}
					}
					break;
				case IResult.DATA_ERROR:
					destroyDialog();
					showToast(IMessage.DATA_ERROR);
					break;
				case IResult.NET_ERROR:
					destroyDialog();
					showToast(IMessage.NET_ERROR);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SINGLE_SERVICE) {
			if(resultCode == RESULT_OK){
				hasSettingServiceItem = true;
				String setting_type = data.getStringExtra("setting_type");
				if (setting_type.equals(ServiceTextConsultSettingActivity.class.getSimpleName())) {
					TextConsultSettingEntry textconsult_setting = (TextConsultSettingEntry) data.getSerializableExtra("setting_content");
					packetSettingEntry.setTextconsult_setting(textconsult_setting);
				} else if (setting_type.equals(ServiceTijianSettingActivity.class.getSimpleName())) {
					TijianSettingEntry tijian_setting = (TijianSettingEntry) data.getSerializableExtra("setting_content");
					packetSettingEntry.setTijian_setting(tijian_setting);
				} else if (setting_type.equals(ServicePhoneConsultSettingActivity.class.getSimpleName())) {
					CallSettingEntry call_setting = (CallSettingEntry)data.getSerializableExtra("setting_content");
					packetSettingEntry.setCall_setting(call_setting);
				} else if (setting_type.equals(ServiceShangmenSettingActivity.class.getSimpleName())) {
					ShangmenSettingEntry shangmeEntry = (ShangmenSettingEntry)data.getSerializableExtra("setting_content");
					packetSettingEntry.setShangmen_setting(shangmeEntry);
				} else if (setting_type.equals(ServiceZhunyuanSettingActivity.class.getSimpleName())) {
					ZhuyuanSettingEntry zhuyuanEntry = (ZhuyuanSettingEntry)data.getSerializableExtra("setting_content");
					packetSettingEntry.setZhuyuan_setting(zhuyuanEntry);
				} else if (setting_type.equals(ServiceJiahaoSettingActivity.class.getSimpleName())) {
					JiahaoSettingEntry jiahaoEntry = (JiahaoSettingEntry)data.getSerializableExtra("setting_content");
					packetSettingEntry.setJiahao_setting(jiahaoEntry);
				}
				adapter.change();
			}
		}
	}
	
	public class GridServiceAdapter extends BaseAdapter {
		
		private String[] titles;
		private int[] icons;
		int enableJiahao;
		int enableTextconsult;
		int enablePhoneconsult;
		int enableTijian;
		int enableShangmen;
		int enableZhuyuan;
		private Class[] classes = new Class[] { ServiceTextConsultSettingActivity.class,
				ServicePhoneConsultSettingActivity.class, ServiceShangmenSettingActivity.class,
				ServiceTijianSettingActivity.class, ServiceJiahaoSettingActivity.class,
				ServiceZhunyuanSettingActivity.class };
		
		public GridServiceAdapter(String[] titles,int[] icons) {
			super();
			
			this.titles = titles;
			this.icons = icons;
			if (null != getServiceSettingInfo()) {
				enableJiahao = packetSettingEntry.getJiahao_setting().getEnable_jiahao();
				enableTextconsult = packetSettingEntry.getTextconsult_setting().getEnable_textconsult();
				enablePhoneconsult = packetSettingEntry.getCall_setting().getEnable_call();
			    enableTijian = packetSettingEntry.getTijian_setting().getEnable_tijian();
				enableShangmen = packetSettingEntry.getShangmen_setting().getEnable_shangmen();
				enableZhuyuan = packetSettingEntry.getZhuyuan_setting().getEnable_zhuyuan();	
			}
		}

		@Override
		public int getCount() {
			return titles == null ? 0 : titles.length;
		}

		@Override
		public Object getItem(int position) {
			return titles == null ? null : titles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Logger.dout(ServicePackageSettingActivity.class," getView()!");
			final ViewHolder holder;
			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.u_gv_service_package_item, null);
				holder.fl_gv_packet = (FrameLayout) convertView.findViewById(R.id.fl_gv_packet);
				holder.service_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.cb_service_tips = (CheckBox) convertView.findViewById(R.id.ckServiceItem);
				holder.service_name = (TextView) convertView.findViewById(R.id.tv_title);
				convertView.setTag(holder);
			}
			holder.service_name.setText(titles[position]);
			holder.service_icon.setImageResource(icons[position]);
			if (null != getServiceSettingInfo()) {
				Logger.dout(ServicePackageSettingActivity.class," null != getServiceSettingInfo()!!");
				/********************选中某项*********************************/
				holder.cb_service_tips.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (isSettingServiceItem(position)) {
							((CheckBox) v).setChecked(false);
							return;
						}
					}
				});
				holder.cb_service_tips.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Logger.dout(ServicePackageSettingActivity.class," onCheckedChanged!!");
						if (isChecked) {
							switch (position) {
								case 0:
									getServiceSettingInfo().setPacket_enable_textconsult(1);
									break;
								case 1:
									getServiceSettingInfo().setPacket_enable_call(1);
									break;
								case 2:
									getServiceSettingInfo().setPacket_enable_shangmen(1);
									break;
								case 3:
									getServiceSettingInfo().setPacket_enable_tijian(1);
									break;
								case 4:
									getServiceSettingInfo().setPacket_enable_jiahao(1);
									break;
								case 5:
									getServiceSettingInfo().setPacket_enable_zhuyuan(1);
									break;
								default:
									break;
							}
						} else {
							switch (position) {
								case 0:
									getServiceSettingInfo().setPacket_enable_textconsult(0);
									break;
								case 1:
									getServiceSettingInfo().setPacket_enable_call(0);
									break;
								case 2:
									getServiceSettingInfo().setPacket_enable_shangmen(0);
									break;
								case 3:
									getServiceSettingInfo().setPacket_enable_tijian(0);
									break;
								case 4:
									getServiceSettingInfo().setPacket_enable_jiahao(0);
									break;
								case 5:
									getServiceSettingInfo().setPacket_enable_zhuyuan(0);
									break;
								default:
									break;
							}
						}
					}
				});
				/********************点击某项，先判断是否启用，若否，则跳转到设置界面***********************/
				holder.fl_gv_packet.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (!isSettingServiceItem(position)){
							holder.cb_service_tips.setChecked(!holder.cb_service_tips.isChecked());
						}
					}

				});
				/********************初始化checkbox状态***********************/
				if (position == 0) {
					if (getServiceSettingInfo().getPacket_enable_textconsult() == 1) {
						holder.cb_service_tips.setChecked(true);
					} else {
						holder.cb_service_tips.setChecked(false);
					}	
				} else if (position == 1) {
					if (getServiceSettingInfo().getPacket_enable_call() == 1) {
						holder.cb_service_tips.setChecked(true);
					} else {
						holder.cb_service_tips.setChecked(false);
					}
				} else if (position == 2) {
					if (getServiceSettingInfo().getPacket_enable_shangmen() == 1) {
						holder.cb_service_tips.setChecked(true);
					} else {
						holder.cb_service_tips.setChecked(false);
					}
				} else if (position == 3) {
					if (getServiceSettingInfo().getPacket_enable_tijian() == 1) {
						holder.cb_service_tips.setChecked(true);
					} else {
						holder.cb_service_tips.setChecked(false);
					}
				} else if (position == 4) {
					if (getServiceSettingInfo().getPacket_enable_jiahao() == 1) {
						holder.cb_service_tips.setChecked(true);
					} else {
						holder.cb_service_tips.setChecked(false);
					}
				} else if (position == 5) {
					if (getServiceSettingInfo().getPacket_enable_zhuyuan() == 1) {
						holder.cb_service_tips.setChecked(true);
					} else {
						holder.cb_service_tips.setChecked(false);
					}
				}
			}
			return convertView;
		}

		public boolean isSettingServiceItem(final int position) {
			switch (position) {
				case 1:
					if (null != packetSettingEntry.getCall_setting()) {
						if(packetSettingEntry.getCall_setting().getCall_time_status()==1 ||packetSettingEntry.getCall_setting().getCall_time_status()==2){
							return false;
						} else {
							showUnSettingDialog(1);
							return true;
						}
					}
					break;
				case 2:
					if (null != packetSettingEntry.getShangmen_setting()) {
						if(packetSettingEntry.getShangmen_setting().getShangmen_time_status()==1 || packetSettingEntry.getShangmen_setting().getShangmen_time_status()==2){
							return false;
						} else {
							showUnSettingDialog(2);
							return true;
						}
					}
					break;
				case 4:
					if (null != packetSettingEntry.getJiahao_setting()) {
						if(packetSettingEntry.getJiahao_setting().getJiahao_time_status()==1 ||packetSettingEntry.getJiahao_setting().getJiahao_time_status()==2){
							return false;
						} else {
							showUnSettingDialog(4);
							return true;
						}
					}
					break;
				case 5:
					if (null != packetSettingEntry.getZhuyuan_setting()) {
						if(packetSettingEntry.getZhuyuan_setting().getZhuyuan_time_status()==1 ||packetSettingEntry.getZhuyuan_setting().getZhuyuan_time_status()==2){
							return false;
						} else {
							showUnSettingDialog(5);
							return true;
						} 
					}
					break;
			}
			return false;
		}

		public void showUnSettingDialog(final int position) {
			MenuDialog.Builder alert = new MenuDialog.Builder(ServicePackageSettingActivity.this);
			MenuDialog dialog = alert.setTitle(R.string.system_info).setMessage(R.string.my_doctor_packet_un_setting_tips)
					.setPositiveButton(getString(R.string.confirm_yes),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Intent intent = new Intent(ServicePackageSettingActivity.this,classes[position]);
									intent.putExtra("title", titles[position]);
									intent.putExtra("from", ServicePackageSettingActivity.class.getSimpleName());
									
									switch (position) {
										case 1:
											intent.putExtra("setting_content", packetSettingEntry.getCall_setting());
											break;
										case 2:
											intent.putExtra("setting_content", packetSettingEntry.getShangmen_setting());
											break;
										case 4:
											intent.putExtra("setting_content", packetSettingEntry.getJiahao_setting());
											break;
										case 5:
											intent.putExtra("setting_content", packetSettingEntry.getZhuyuan_setting());
											break;
										default:
											break;
									}
									startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
								}
							})
					.setNegativeButton(getString(R.string.confirm_no),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		}
		public void change() {
			enableJiahao = packetSettingEntry.getJiahao_setting().getEnable_jiahao();
			enableTextconsult = packetSettingEntry.getTextconsult_setting().getEnable_textconsult();
			enablePhoneconsult = packetSettingEntry.getCall_setting().getEnable_call();
		    enableTijian = packetSettingEntry.getTijian_setting().getEnable_tijian();
			enableShangmen = packetSettingEntry.getShangmen_setting().getEnable_shangmen();
			enableZhuyuan = packetSettingEntry.getZhuyuan_setting().getEnable_zhuyuan();	
			notifyDataSetChanged();
		}
		
		public boolean isServiceOn() {
			int serviceItemOnNum = 0;
			if (getServiceSettingInfo() == null) {
				return false;
			}
			// 初始化checkbox状态
			if (getServiceSettingInfo().getPacket_enable_textconsult() == 1) {
				serviceItemOnNum++;
			}
			if (getServiceSettingInfo().getPacket_enable_call() == 1) {
				serviceItemOnNum++;
			}
			if (getServiceSettingInfo().getPacket_enable_shangmen() == 1) {
				serviceItemOnNum++;
			}
			if (getServiceSettingInfo().getPacket_enable_tijian() == 1) {
				serviceItemOnNum++;
			}
			if (getServiceSettingInfo().getPacket_enable_jiahao() == 1) {
				serviceItemOnNum++;
			}
			if (getServiceSettingInfo().getPacket_enable_zhuyuan() == 1) {
				serviceItemOnNum++;
			}
			if (serviceItemOnNum<2) {//至少要勾选两个服务
				return false;
			} else {
				return true;
			}
		}
		
		public PacketSettingEntry getServiceSettingInfo() {
			return packetSettingEntry.getPacket_setting();
		}

		public class ViewHolder {
			public FrameLayout fl_gv_packet;
			ImageView service_icon;
			CheckBox cb_service_tips;
			TextView service_name;
		}
	}

}
