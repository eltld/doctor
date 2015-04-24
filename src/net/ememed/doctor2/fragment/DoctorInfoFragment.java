package net.ememed.doctor2.fragment;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.OrderLoadedEvent;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.ContactInfoActivity;
import net.ememed.doctor2.activity.PatientEditGroupActivity;
import net.ememed.doctor2.activity.PhotoDetailActivity;
import net.ememed.doctor2.activity.RemarkInfoActivity;
import net.ememed.doctor2.activity.ShowBigImage;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.MedicalOrderEntry;
import net.ememed.doctor2.entity.MemberDetailEntry;
import net.ememed.doctor2.entity.MemberInfo;
import net.ememed.doctor2.entity.PatientGroupBean;
import net.ememed.doctor2.entity.RemarkInfoBean;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.SwitchButton;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorInfoFragment extends Fragment implements Handler.Callback,
		OnClickListener {

	private ContactInfoActivity activity = null;
	private Handler mHandler;

	private TextView memberName;
	private TextView memberGender;
	private TextView memberAge;
	private TextView memberMobile;
	// private RefreshListView medicalListView;
	private LinearLayout basicInfoLayout;
//	private FrameLayout fl_history_timeline;
	private ImageView orange_star_icon;
	private TextView description_txt;
	private LinearLayout ll_member_group;
	private TextView user_group;
	private LinearLayout ll_remark_name;
	private LinearLayout ll_description;
	private TextView remark_name_txt;
	private SwitchButton is_star;

	private SwitchButton bt_switch_text_micor;

	private String[] group_name; // 组名
	private String[] group_id;

	MemberDetailEntry data = null;
	private boolean flag = false;
	private boolean micorflag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
		activity = (ContactInfoActivity) getActivity();
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (ContactInfoActivity) activity;
	}

	@Override
	public boolean handleMessage(Message msg) {
		try {
			activity.destroyDialog();
			switch (msg.what) {
			case IResult.MEMBER_INFO:
				MemberInfo info = (MemberInfo) msg.obj;
				data = info.getData();
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
				activity.setTitle(data.getNOTE_NAME() != null ? data
						.getNOTE_NAME() : data.getREALNAME());

				if (data.getWEITALK_STATUS().equals("1")) {
					bt_switch_text_micor.setChecked(false);
				} else {
					bt_switch_text_micor.setChecked(true);
				}
				micorflag = true;

				if (!TextUtils.isEmpty(data.getBIRTHDAY())) {
					String[] birthday = data.getBIRTHDAY().split("-");
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
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
				if (data.getIS_STAR() != null && data.getIS_STAR().equals("1")) {
					orange_star_icon.setVisibility(View.VISIBLE);
				} else {
					orange_star_icon.setVisibility(View.GONE);
				}
				description_txt.setText(data.getDESCRIPTION());
				remark_name_txt.setText(data.getNOTE_NAME());

				// medicalListView.addHeaderView(basicInfoLayout);
				OrderLoadedEvent event = null;
				if (null != data.getOPEN_ORDER()
						&& data.getOPEN_ORDER().size() > 0) {
					activity.mIndicator.setNewMessageTips(0, data
							.getOPEN_ORDER().size());
					event = new OrderLoadedEvent(true, data.getOPEN_ORDER());
				} else {
					activity.mIndicator.setNewMessageTips(0, 0);
					event = new OrderLoadedEvent(false, null);
				}
				EventBus.getDefault().postSticky(event);

				if (null != data.getMEDICAL_RECORDS()
						&& data.getMEDICAL_RECORDS().size() > 0) {
//					fl_history_timeline.setVisibility(View.VISIBLE);
				}
				// medicalListView.setAdapter(new MedicalAdapter(data
				// .getMEDICAL_RECORDS()));
				user_group.setText(data.getGROUPNAME() == null ? "未分组" : data
						.getGROUPNAME());
				ll_member_group.setOnClickListener(this);
				
				if (data.getIS_STAR().equals("1")) {
					is_star.setChecked(false);
				}else{
					is_star.setChecked(true);
				}
				

				break;
			case IResult.ERROR:
				activity.showToast(IMessage.NET_ERROR);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			flag = true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ll_remark_name: // 备注
			if(data == null){
				activity.showToast("数据异常，请稍后再试");
				return;
			}
			intent = new Intent(activity, RemarkInfoActivity.class);
			intent.putExtra("tochat_userId", activity.tochat_userId);
			intent.putExtra("tochat_title", "设置备注");
			intent.putExtra("note_name", data.getNOTE_NAME() == null ? ""
					: data.getNOTE_NAME());
			intent.putExtra("description", data.getDESCRIPTION() == null ? ""
					: data.getDESCRIPTION());
			startActivity(intent);
			break;

		case R.id.ll_description: // 描述
			intent = new Intent(activity, RemarkInfoActivity.class);
			intent.putExtra("tochat_userId", activity.tochat_userId);
			intent.putExtra("tochat_title", "设置描述");
			intent.putExtra("note_name", data.getNOTE_NAME() == null ? ""
					: data.getNOTE_NAME());
			intent.putExtra("description", data.getDESCRIPTION() == null ? ""
					: data.getDESCRIPTION());
			startActivity(intent);
			break;

		case R.id.ll_member_group: // 设置分组
			intent = new Intent(activity, PatientEditGroupActivity.class);
			intent.putExtra("userid", activity.tochat_userId);
			intent.putExtra("user_group_name", data.getGROUPNAME());
			intent.putExtra("editType",
					PatientEditGroupActivity.EDIT_USER_GROUP);
//			intent.putExtra("group_info", (Serializable) pgb.getData());
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cus_info, null);
		memberName = (TextView) view.findViewById(R.id.member_name);
		memberGender = (TextView) view.findViewById(R.id.member_gender);
		memberAge = (TextView) view.findViewById(R.id.member_age);
		memberMobile = (TextView) view.findViewById(R.id.member_mobile);
		basicInfoLayout = (LinearLayout) view
				.findViewById(R.id.ll_member_basic_info);
//		fl_history_timeline = (FrameLayout) view
//				.findViewById(R.id.fl_history_timeline);
		orange_star_icon = (ImageView) view.findViewById(R.id.orange_star_icon);
		ll_description = (LinearLayout) view.findViewById(R.id.ll_description);
		ll_description.setOnClickListener(this);
		description_txt = (TextView) view.findViewById(R.id.description_txt); // 描述
		ll_member_group = (LinearLayout) view
				.findViewById(R.id.ll_member_group);
		ll_member_group.setOnClickListener(this);
		user_group = (TextView) view.findViewById(R.id.user_group); // 分组
		ll_remark_name = (LinearLayout) view.findViewById(R.id.ll_remark_name); // 备注名
		ll_remark_name.setOnClickListener(this);
		remark_name_txt = (TextView) view.findViewById(R.id.remark_name_txt);
		is_star = (SwitchButton) view.findViewById(R.id.bt_switch_text_isstar);
		is_star.setChecked(true);
		bt_switch_text_micor = (SwitchButton) view
				.findViewById(R.id.bt_switch_text_micor);

		setStarListener();
//		getUserInfo();
		return view;
	}

	private void setStarListener() {
		is_star.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (flag) {
					if (isChecked) {
						setIsStar(0);
						orange_star_icon.setVisibility(View.GONE);
					} else {
						setIsStar(1);
						orange_star_icon.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		bt_switch_text_micor
				.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (micorflag) {
							if (isChecked) {
								blockWeitalkUsers(-1 + "");
							} else {
								blockWeitalkUsers(1 + "");
							}
						}
					}

				});
	}

	public void blockWeitalkUsers(String stata) {

		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("block_memberid", activity.tochat_userId);
			params.put("block_status", stata);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.block_weitalk_users, RemarkInfoBean.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							activity.destroyDialog();
							RemarkInfoBean rib = (RemarkInfoBean) response;
							if (rib.getSuccess().equals("1")) {
							}

							activity.showToast(rib.getErrormsg());
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							activity.destroyDialog();
							activity.showToast("网络异常");
						}
					});
		} else {
			activity.showToast("网络未连接");
		}
	}

	public void setIsStar(int is_star) {
		if (NetWorkUtils.detect(activity)) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("userid", activity.tochat_userId);
			params.put("is_star", is_star + "");
			System.out.println("userid" + activity.tochat_userId + "is_star"
					+ is_star);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_patient_star, RemarkInfoBean.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							activity.destroyDialog();
							RemarkInfoBean rib = (RemarkInfoBean) response;
							if (rib.getSuccess().equals("1")) {//成功
								getActivity().sendBroadcast(new Intent(ActionFilter.REQUEST_PATIENT_TONGXUNLU));
							}

							activity.showToast(rib.getErrormsg());
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							activity.destroyDialog();
							activity.showToast("没有数据。。。");
							// Message message = new Message();
							// message.obj = error.getMessage();
							// message.what = IResult.ERROR_MARK_UNREAD_MSG;
							// handler.sendMessage(message);
						}
					});
		} else {
			// activity.handler.sendEmptyMessage(IResult.NET_ERROR);
			activity.showToast("未连接网络");
		}
	}

	private void getUserInfo() {
		if (NetWorkUtils.detect(getActivity())) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("any_user_id", activity.tochat_userId);

			System.out.println("参数  params = " + params);

			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_member_info, MemberInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.MEMBER_INFO;
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
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}
	}

	/**
	 * @deprecated
	 */
	public void getPatientGroup() {
		if (NetWorkUtils.detect(getActivity())) {
			activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("page", "1");
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_patients_group_list, PatientGroupBean.class,
					params, new Listener() {

						@Override
						public void onResponse(Object arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.obj = arg0;
							msg.what = IResult.PATIENT_GROUP;
							mHandler.sendMessage(msg);
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.obj = arg0;
							msg.what = IResult.DATA_ERROR;
							mHandler.sendMessage(msg);
						}

					});
		}
	}

	public void onDestroy() {
		super.onDestroy();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_member_info);
		EventBus.getDefault().removeStickyEvent(OrderLoadedEvent.class);
	};

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
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.member_info_item, null);
				holder = new ViewHolder();
				holder.mTime = (TextView) convertView
						.findViewById(R.id.medical_history_time);
				holder.mcontent = (TextView) convertView
						.findViewById(R.id.medical_history_comment);
				holder.mAttachment = (ImageView) convertView
						.findViewById(R.id.medical_history_attachment);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (null != mList && 0 != mList.size()) {
				final MedicalOrderEntry entry = mList.get(position);

				holder.mTime.setText(entry.getCREATETIME());
				holder.mcontent.setText(entry.getCONTENT());
				if (TextUtils.isEmpty(entry.getATTACHMENT())) {
					holder.mAttachment.setVisibility(View.GONE);
				} else {
					holder.mAttachment.setVisibility(View.VISIBLE);
					try {
						activity.imageLoader.displayImage(
								entry.getATTACHMENT(), holder.mAttachment,
								Util.getOptions_little_pic());
					} catch (Exception e) {
						// TODO: handle exception
					}
					holder.mAttachment
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {

									try {
										if (!TextUtils.isEmpty(entry
												.getATTACHMENT())) {
											Intent bigPicIntent = new Intent(
													activity,
													ShowBigImage.class);
											bigPicIntent.putExtra("remotepath",
													entry.getATTACHMENT());
											activity.startActivity(bigPicIntent);
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
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			if(null == data){
				getUserInfo();
			}
		}else{
			
		}
	}

}
