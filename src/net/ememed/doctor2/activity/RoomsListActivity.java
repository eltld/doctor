package net.ememed.doctor2.activity;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.db.BanksTable;
import net.ememed.doctor2.db.ConfigTable;
import net.ememed.doctor2.db.PositionConfigTable;
import net.ememed.doctor2.entity.DoctorInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.TextUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.widget.RefreshListView;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RoomsListActivity extends BasicActivity implements BasicUIEvent {
	private RoomListAdapter roomListAdapter;
	private RefreshListView lvRoomList;
	private int currentPage = 1;
	private int totalPage = 1;
	private boolean refresh = true;
	
	private RefreshListView ll_conten_nav;
	private RoomNavAdapter nav_adapter;
	private String from;
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.u_select_hospital);
		from = getIntent().getStringExtra("from");
		UICore.eventTask(this, this, IResult.SYSN_CONFIG, null, null);
	}

	@Override
	protected void setupView() {
		TextView tv_title = (TextView)findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.act_title_choose_room));
		ll_conten_nav = (RefreshListView) findViewById(R.id.ll_conten_nav);
		ll_conten_nav.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 一定要设置这个属性，否则ListView不会刷新 
		ll_conten_nav.setHeaderDividersEnabled(false);
		lvRoomList = (RefreshListView) findViewById(R.id.lv_doctors_guahao);
		lvRoomList.setHeaderDividersEnabled(false);
		roomListAdapter = new RoomListAdapter(this,null);
		lvRoomList.setAdapter(roomListAdapter);
		nav_adapter = new RoomNavAdapter(this,null);
		ll_conten_nav.setAdapter(nav_adapter);
		super.setupView();
	}

	public void doClick(View view){
		if (view.getId() == R.id.btn_back) {
			finish();
		} 
	}

	private String roomNameTemp = null;
	@Override
	protected void addListener() {
		lvRoomList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long arg3) {
				try {
//					System.out.println("onItemClick:"+position);
					String item = (String) roomListAdapter.getItem(position-1);
					Logger.dout("room:"+item);
					Intent intent = null;
					if (!TextUtils.isEmpty(from) && from.equals(RegisterSuccessActivity.class.getSimpleName())) {
						 intent = new Intent(RoomsListActivity.this,RegisterSuccessActivity.class);	
					} else if (!TextUtils.isEmpty(from) && from.equals(PersonInfoActivity.class.getSimpleName())){
						setRoomName(item);
						roomNameTemp = item;
						intent = new Intent(RoomsListActivity.this,PersonInfoActivity.class);
						intent.putExtra("setting_type",RoomsListActivity.class.getSimpleName());
					}
					intent.putExtra("roomname", item);
					setResult(RESULT_OK, intent);
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
		
			}
		});
		ll_conten_nav.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				try {
						nav_adapter.setSelectItem(position - 1);
						nav_adapter.notifyDataSetInvalidated();
						String ememed_cityId = (String)nav_adapter.getItem(position-1);
						ConfigTable table = new ConfigTable();
						List<String> hosp_list = table.getDepartmentNames(ememed_cityId);
						sendMessage(IResult.RESULT, hosp_list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		super.addListener();
	}

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		switch (mes) {
		case IResult.SYSN_CONFIG:
			getHospitalList();
			break;

		default:
			break;
		}
	}
	
	
	private void refresh() {
		currentPage = 1;
		refresh = true;
	}

	private void loadmore() {
		currentPage++;
		refresh = false;
	}
	
	private void getHospitalList(){
		try {
			ConfigTable table = new ConfigTable();
			department_group_list = table.getDepartmentGroups();
			if (null == department_group_list || department_group_list.size() == 0) {
			   sysn_config();
			} else {
				String str_department_group = department_group_list.get(0);
				List<String> room_list = table.getDepartmentNames(str_department_group);
				sendMessage(IResult.RESULT, room_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(IResult.DATA_ERROR);
		}
	}
	@Override
	protected void onResult(Message msg) {
		lvRoomList.onRefreshComplete();
		try {
			switch (msg.what) {
				case IResult.RESULT:
					List<String> map = (List<String>) msg.obj;
					if (map == null || map.size() == 0) {
						showToast(getString(R.string.data_none));
					} else {
						if (refresh) {
							ll_conten_nav.setVisibility(View.VISIBLE);
							nav_adapter.change(department_group_list);
							roomListAdapter.change(map);
						} else {
							roomListAdapter.add(map);
						}
						if (currentPage < totalPage) {
							lvRoomList.onLoadMoreComplete(false);
						} else {
							lvRoomList.onLoadMoreComplete(true);
						}
					}
					break;
				case IResult.PERSON_INFO:
					PersonInfo info = (PersonInfo) msg.obj;
					int success = (Integer) info.getSuccess();
					if (success == 1) {
						showToast(info.getErrormsg());
						return;
					} else {
						showToast("设置科室失败，请稍后再试！");
					}
					break;
				case IResult.NET_ERROR:
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
	
	private void sysn_config() {
		
		try {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("Channel", "android"));// 新增
			String content = HttpUtil.getString(HttpUtil.URI + HttpUtil.sync_config, params, HttpUtil.POST);
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
					Logger.dout("keyName:"+keyName);
					JSONArray roomArray = roomsObj.getJSONArray(keyName);
					for (int i = 0; i < roomArray.length(); i++) {
						Logger.dout("keyValue:"+roomArray.getString(i));
						keyValue = roomArray.getString(i);
						configTable.saveDepartment(keyName, keyValue);
					}
				}
				/*****无耻的分割线   获取职称***/
				PositionConfigTable positionTable = new PositionConfigTable();
				positionTable.clearTable();
				JSONArray positionObj = resultObj.getJSONArray("professional");
				if (null != positionObj && positionObj.length() > 0) {
					for (int i = 0; i < positionObj.length(); i++) {
						positionTable.savePositionName(null, positionObj.getString(i));
					}	
				}
				/*****无耻的分割线   获取銀行名称***/
				BanksTable  banks= new BanksTable();
				banks.clearTable();
				JSONArray banksObj = resultObj.getJSONArray("banks");
				if (null != banksObj && banksObj.length() > 0) {
					for (int i = 0; i < positionObj.length(); i++) {
						banks.saveBanksName(null, banksObj.getString(i));
					}	
				}
				department_group_list = configTable.getDepartmentGroups();
				String str_department_group = department_group_list.get(0);
				ArrayList<String> department_list = configTable.getDepartmentNames(str_department_group);
				sendMessage(IResult.RESULT, department_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private ArrayList<String> department_group_list = null;
	
	public class RoomNavAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private List<String> citys;
		public RoomNavAdapter(Context context,List<String> hosps){
			if(hosps==null){
				hosps = new ArrayList<String>();
			}
			this.citys = hosps;
			this.inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return citys.size();
		}

		@Override
		public Object getItem(int position) {
			return  (null!=citys && citys.size()>0)?citys.get(position):null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.u_room_item, null);
				holder.tvHospName = (TextView) convertView.findViewById(R.id.tv_hosp_name);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();
			}
			String hosp = citys.get(position);
			holder.tvHospName.setText(hosp);
			
			if (position == selectItem) {// 如果当前的行就是ListView中选中的一行，就更改显示样式 
//				System.out.println("getView:"+ position + " selectItem:"+selectItem);
				convertView.setBackgroundColor(Color.WHITE);// 更改整行的背景色 
			} else {
		        convertView.setBackgroundColor(Color.TRANSPARENT);
		    } 
			return convertView;
		}
		
		public  void setSelectItem(int selectItem) {
	        this.selectItem = selectItem;
	    }
	    private int  selectItem = 0;
		
		class ViewHolder{
			TextView tvHospName;
		}
		public void change(List<String> hosps) {
			if(hosps==null){
				hosps=new ArrayList<String>();
			}
			this.citys = hosps;
			notifyDataSetChanged();
		}
		public void add(List<String> hosps) {
			this.citys.addAll(hosps);
			notifyDataSetChanged();
		}
	}
	public class RoomListAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private List<String> hosps;
		public RoomListAdapter(Context context,List<String> hosps){
			if(hosps==null){
				hosps = new ArrayList<String>();
			}
			this.hosps = hosps;
			this.inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return hosps.size();
		}

		@Override
		public Object getItem(int position) {
			return (null!=hosps && hosps.size()>0)?hosps.get(position):null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.u_room_item, null);
				holder.tvHospName = (TextView) convertView.findViewById(R.id.tv_hosp_name);
				holder.tv_grade = (TextView) convertView.findViewById(R.id.tv_grade);
				convertView.setTag(holder);
			} else{
				holder = (ViewHolder) convertView.getTag();
			}
			String hosp = hosps.get(position);
			if (null != hosp) {
				holder.tvHospName.setText(hosp);
			}
			return convertView;
		}

		class ViewHolder{
		    TextView tv_grade;
			TextView tvHospName;
		}
		public void change(List<String> hosps) {
			if(hosps==null){
				hosps=new ArrayList<String>();
			}
			this.hosps = hosps;
			notifyDataSetChanged();
		}
		public void add(List<String> hosps) {
			this.hosps.addAll(hosps);
			notifyDataSetChanged();
		}
	}
	
	private void setRoomName(String roomName) {
		if (NetWorkUtils.detect(RoomsListActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("roomname", roomName);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_doctor_roomname, PersonInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {

							SharePrefUtil.putString(Conast.ROOM_NAME, roomNameTemp);	
							SharePrefUtil.commit();	
							
							Message message = handler.obtainMessage();
							message.obj = response;
							message.what = IResult.PERSON_INFO;
							handler.sendMessage(message);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Message message = handler.obtainMessage();
							message.obj = error.getMessage();
							message.what = IResult.DATA_ERROR;
							handler.sendMessage(message);
						}
					});
		} else {
			handler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}
	
}
