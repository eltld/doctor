package net.ememed.doctor2.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MedicalOrderEntry;
import net.ememed.doctor2.entity.MemberDetailEntry;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.RefreshListView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.OrderLoadedEvent;

public class DoctorInfoActivity extends BasicActivity{
	

	private TextView memberName;
	private TextView memberGender;
	private TextView memberAge;
	private TextView memberMobile;
	private RefreshListView medicalListView;
	private LinearLayout basicInfoLayout;
	private FrameLayout fl_history_timeline;
	
	private String tochat_userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_doctor_info);
		memberName = (TextView)  findViewById(R.id.member_name);
		memberGender = (TextView)  findViewById(R.id.member_gender);
		memberAge = (TextView)  findViewById(R.id.member_age);
		memberMobile = (TextView)  findViewById(R.id.member_mobile);
		medicalListView = (RefreshListView)  findViewById(R.id.medical_record_list);
		basicInfoLayout = (LinearLayout)  findViewById(R.id.ll_member_basic_info);
		fl_history_timeline = (FrameLayout)  findViewById(R.id.fl_history_timeline);
		tochat_userId = getIntent().getExtras().getString("tochat_userId");
		getUserInfo();
		
	}
	
	public void doClick(View view){
		
		switch (view.getId()) {
		case R.id.btn_back:
			
			finish();
			
			break;

		default:
			break;
		}
		
	}
	
	
	@Override
	protected void onResult(Message msg) {
		// TODO Auto-generated method stub
		super.onResult(msg);
		try {
			switch (msg.what) {
			case IResult.MEMBER_INFO:
				destroyDialog();
				MemberInfo info = (MemberInfo) msg.obj;
				MemberDetailEntry data = info.getData();
				switch (Integer.parseInt(data.getSEX())) {
					case 0:
						memberGender.setText("女");
						break;
					case 1:
						memberGender.setText("男");
						break;
				}
				memberName.setText(data.getREALNAME());
				memberMobile.setText(data.getMOBILE());
				setTitle(data.getREALNAME());
				if (!TextUtils.isEmpty(data.getBIRTHDAY())) {
					String[] birthday = data.getBIRTHDAY().split("-");
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String[] currentDate = dateFormat.format(
							new Date(System.currentTimeMillis())).split("-");
					int age = Integer.parseInt(currentDate[0])
							- Integer.parseInt(birthday[0]);
					if (Integer.parseInt(currentDate[1]) < Integer
							.parseInt(birthday[1])) {
						age--;
					} else if (Integer.parseInt(currentDate[1]) == Integer
							.parseInt(birthday[1])) {
						if (Integer.parseInt(currentDate[2]) < Integer
								.parseInt(birthday[2])) {
							age--;
						}
					}
					memberAge.setText(age + "");	
				}
				OrderLoadedEvent event = null;
				if (null != data.getOPEN_ORDER() && data.getOPEN_ORDER().size()>0) {
					event = new OrderLoadedEvent(true,data.getOPEN_ORDER());
				} else {
					event = new OrderLoadedEvent(false,null);
				}
				EventBus.getDefault().postSticky(event);
				
				if (null != data.getMEDICAL_RECORDS() && data.getMEDICAL_RECORDS().size()>0) {
					fl_history_timeline.setVisibility(View.VISIBLE);
				}
				medicalListView.setAdapter(new MedicalAdapter(data
						.getMEDICAL_RECORDS()));
				break;
			case IResult.ERROR:
				showToast(IMessage.NET_ERROR);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getUserInfo() {
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("any_user_id", tochat_userId);

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_member_info, MemberInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.MEMBER_INFO;
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
	
	private class MedicalAdapter extends BaseAdapter {

		private List<MedicalOrderEntry> mList;

		MedicalAdapter(List<MedicalOrderEntry> list) {
			if (null == list) {
				mList = new ArrayList<MedicalOrderEntry>();
			} else {
				mList = list;
			}
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			if (null != mList && 0 != mList.size()) {
				return mList.get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(DoctorInfoActivity.this).inflate(R.layout.member_info_item, null);
				holder = new ViewHolder();
				holder.mTime = (TextView) convertView.findViewById(R.id.medical_history_time);
				holder.mcontent = (TextView) convertView.findViewById(R.id.medical_history_comment);
				holder.mAttachment = (ImageView) convertView.findViewById(R.id.medical_history_attachment);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if(null != mList && 0 != mList.size()) {
				final MedicalOrderEntry entry = mList.get(position);
				
				holder.mTime.setText(entry.getCREATETIME());
				holder.mcontent.setText(entry.getCONTENT());
				if (TextUtils.isEmpty(entry.getATTACHMENT())) {
					holder.mAttachment.setVisibility(View.GONE);
				} else {
					holder.mAttachment.setVisibility(View.VISIBLE);
					try {
						DoctorInfoActivity.this.imageLoader.displayImage(entry.getATTACHMENT(),holder.mAttachment, Util.getOptions_little_pic());
					} catch (Exception e) {
						// TODO: handle exception
					}
					holder.mAttachment.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							try {
								if (!TextUtils.isEmpty(entry.getATTACHMENT())) {
									Intent bigPicIntent = new Intent(DoctorInfoActivity.this,ShowBigImage.class);
									bigPicIntent.putExtra("remotepath", entry.getATTACHMENT());
									DoctorInfoActivity.this.startActivity(bigPicIntent);	
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
			return convertView;
		}

	}

	private class ViewHolder {
		private TextView mTime;
		private TextView mcontent;
		private ImageView mAttachment;
	}

}
