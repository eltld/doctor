package net.ememed.doctor2.baike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.greenrobot.event.ShareEnd;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.baike.adapter.ShuoshuoAdapter;
import net.ememed.doctor2.baike.entity.BaikeMemberInfo;
import net.ememed.doctor2.baike.entity.BaikeShuoshuoInfo;
import net.ememed.doctor2.baike.entity.OtherBaikeEntry;
import net.ememed.doctor2.baike.fragment.BaikeHomeFragment;
import net.ememed.doctor2.entity.CommonResponseEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.receiver.ActionFilter;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.ShareSdkUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.RefreshListView;
import net.ememed.doctor2.widget.RefreshListView.IOnRefreshListener;
import net.ememed.doctor2.widget.RefreshListView.IOnLoadMoreListener;

public class OtherBaikeActivity extends BasicActivity{
//	private final int REQUEST_SAYS_DETAIL = 2;
	private TextView top_title;
	private ShareSdkUtil share;
	private RefreshListView listView;
	private ShuoshuoAdapter adapter;
	private List<BaikeShuoshuoInfo> shuoshuoList = null;
	
	private int page = 1;
	private BaikeMemberInfo memberInfo = null;
	private boolean isRefresh = true;
	
	private String other_doctor_id = null;
	private String from = "";
	private int praise_position = -1;
	private FrameLayout fl_topLayout;
	private InnerReceiver receiver;
	private IntentFilter filter;
	private LinearLayout ll_right_fun_3;
	private TextView tv_right_fun_3;
	private ImageView iv_right_fun_3;
	private View headView;
	private ImageView iv_share;
	private int total_shuoshuo = -1;
	
	private CircleImageView iv_photo;
	private TextView tv_name;
	private TextView tv_professional;
	private TextView tv_zhuanke;
	private TextView tv_hospital;
	private TextView tv_shanchang;
	private TextView tv_shuoshuo_total;
	private TextView tv_attention_num;
	private TextView tv_doctor_fans_num;
	private TextView tv_user_fans_num;
	private CheckBox follow;
	private LinearLayout ll_person_info;
	private TextView tv_today_visit_num;
	private TextView tv_total_visit_num;
	
	private Boolean isOperateAttentionOrCancel = false;
	
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.activity_other_baike);
		shuoshuoList = new ArrayList<BaikeShuoshuoInfo>();
		other_doctor_id = getIntent().getStringExtra("other_doctor_id");
		from = getIntent().getStringExtra("from");
		
		receiver = new InnerReceiver();
		filter = new IntentFilter();
		filter.addAction(MyApplication.WECHAT_SHARE_SUCCESS);
		filter.addAction(BaikeHomeFragment.UPDATE_MY_SAYS_LIST);
		registerReceiver(receiver, filter);
	}
	
	public void onEvent(ShareEnd event) {
		destroyDialog();
	}
	
	@Override
	protected void setupView() {
		super.setupView();
		fl_topLayout = (FrameLayout) findViewById(R.id.fl_top_title);
		
		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("个人百科");
		
		ll_right_fun_3 = (LinearLayout) findViewById(R.id.ll_right_fun_3);
		if(other_doctor_id.equals(SharePrefUtil.getString(Conast.Doctor_ID))){
			ll_right_fun_3.setVisibility(View.GONE);
		} else {
			ll_right_fun_3.setVisibility(View.VISIBLE);
			tv_right_fun_3 = (TextView) findViewById(R.id.tv_right_fun_3);
			iv_right_fun_3 = (ImageView) findViewById(R.id.iv_right_fun_3);
		}
		
		iv_share = (ImageView) findViewById(R.id.iv_share);
		iv_share.setVisibility(View.GONE);
		
		listView = (RefreshListView) findViewById(R.id.listView);
		adapter = new ShuoshuoAdapter(null, this, handler, false);
		headView = LayoutInflater.from(this).inflate(R.layout.activity_other_baike_head, null);
		
		/////////////////////////////////////////////////////////////////////
		iv_photo = (CircleImageView) headView.findViewById(R.id.iv_photo_2);
		tv_name = (TextView) headView.findViewById(R.id.tv_name_2);
		tv_professional = (TextView) headView.findViewById(R.id.tv_professional_2);
		tv_zhuanke = (TextView) headView.findViewById(R.id.tv_zhuanke_2);
		tv_hospital = (TextView) headView.findViewById(R.id.tv_hospital_2);
		tv_shanchang = (TextView) headView.findViewById(R.id.tv_shanchang_2);
		tv_shuoshuo_total = (TextView) headView.findViewById(R.id.tv_shuoshuo_total_2);
		tv_attention_num = (TextView) headView.findViewById(R.id.tv_attention_num_2);
		tv_doctor_fans_num = (TextView) headView.findViewById(R.id.tv_doctor_fans_num_2);
		tv_user_fans_num = (TextView) headView.findViewById(R.id.tv_user_fans_num_2);
		follow = (CheckBox) headView.findViewById(R.id.follow);
		ll_person_info = (LinearLayout) headView.findViewById(R.id.ll_person_info_2);
		tv_today_visit_num = (TextView) headView.findViewById(R.id.tv_today_visit_num);
		tv_total_visit_num = (TextView) headView.findViewById(R.id.tv_total_visit_num);
		ll_person_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(OtherBaikeActivity.this, DoctorSimpleInfoActivity.class);
				intent.putExtra("other_doctor_id", memberInfo.getMEMBERID());
				startActivity(intent);
			}
		});
		/////////////////////////////////////////////////////////////////////
		listView.addHeaderView(headView);
		listView.setAdapter(adapter);
		listView.setVisibility(View.GONE);
		
		getOtherBaikeInfo();
	}
	
	@Override
	protected void addListener() {
		super.addListener();
		listView.setOnRefreshListener(new IOnRefreshListener() {
			@Override
			public void OnRefresh() {
				page = 1;
				isRefresh = true;
				getOtherBaikeInfo();
			}
		});
		listView.setOnLoadMoreListener(new IOnLoadMoreListener() {
			
			@Override
			public void OnLoadMore() {
				page += 1;
				isRefresh = false;
				getOtherBaikeInfo();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(position > 1){
//					BaikeShuoshuoInfo info = (BaikeShuoshuoInfo)adapter.getItem(position -1);
					BaikeShuoshuoInfo info = (BaikeShuoshuoInfo)parent.getAdapter().getItem(position);
					Intent intent = new Intent(OtherBaikeActivity.this, SayDetailsActivity.class);
					intent.putExtra("says_id", info.getSAYSID());
					intent.putExtra("name", memberInfo.getREALNAME());
//					startActivityForResult(intent, REQUEST_SAYS_DETAIL);
					startActivity(intent);
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void getOtherBaikeInfo(){
		if (NetWorkUtils.detect(this)) {
			if(null ==  memberInfo){
				loading(null);
			}
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("other_doctorid", other_doctor_id);
			params.put("page", ""+page);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_other_baike, OtherBaikeEntry.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_OTHER_BAIKE;
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
	
	private void setShare(int channel_type){
		if (NetWorkUtils.detect(this)) {
//			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("other_doctorid",memberInfo.getMEMBERID()+"");
			params.put("channel_type", ""+channel_type);
			
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_share, CommonResponseEntity.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.SET_SHARE;
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
	
	private void setAttention(){
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("other_memberid", other_doctor_id);
			params.put("other_utype", "doctor");
			params.put("is_friendship", "1");    //是否需要同时进行加好友的操作  0 不需要 1 需要

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_attention, CommonResponseEntity.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.ADD_ATTENTION;
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
	
	private void cancelAttention(){
		if (NetWorkUtils.detect(this)) {
			loading(null);
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("other_memberid", other_doctor_id);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.cancel_attention, CommonResponseEntity.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.CANCEL_ATTENTION;
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
	
	private void givePraise(String says_id){
		if (NetWorkUtils.detect(this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("saysid", says_id);
		
			MyApplication.volleyHttpClient.postWithParams(HttpUtil.set_praise, CommonResponseEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GIVE_PRAISE;
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
	
	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.GET_OTHER_BAIKE:
				listView.onRefreshComplete();
				OtherBaikeEntry entry = (OtherBaikeEntry) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						listView.setVisibility(View.VISIBLE);
						iv_share.setVisibility(View.VISIBLE);
						memberInfo = entry.getMemberinfo();
						total_shuoshuo = entry.getCount();
						List<BaikeShuoshuoInfo> temp = entry.getData();
						if(null != temp && temp.size() > 0){
							if(isRefresh){
								shuoshuoList = temp;
							} else {
								shuoshuoList.addAll(temp);
							}
						}
						
						adapter.change(shuoshuoList, memberInfo);
						
						showPersonInfo();
							
						if (page < entry.getPages()) {
							listView.onLoadMoreComplete(false);
						} else {
							listView.onLoadMoreComplete(true);
						}
						
					} else {
						showToast(entry.getErrormsg());
					}
					
					if(!memberInfo.getMEMBERID().equals(SharePrefUtil.getString(Conast.Doctor_ID))){
						if("1".equals(memberInfo.getIS_ATTENTION())){
							tv_right_fun_3.setText("已关注");
							iv_right_fun_3.setImageDrawable(getResources().getDrawable(R.drawable.pic_baike_gou));
						} else {
							tv_right_fun_3.setText("关注");
							iv_right_fun_3.setImageDrawable(getResources().getDrawable(R.drawable.pic_baike_plus));
						}
					}
					
					destroyDialog();
				}
				break;
			case IResult.ADD_ATTENTION:
				destroyDialog();
				CommonResponseEntity entry2 = (CommonResponseEntity) msg.obj;
				if (null != entry2) {
					if (entry2.getSuccess() == 1) {
//						iv_attention.setImageDrawable(getResources().getDrawable(R.drawable.pic_baike_gou));
//						tv_attention.setText("已关注");
//						tv_attention.setTextColor(getResources().getColor(R.color.black));
						tv_right_fun_3.setText("已关注");
						iv_right_fun_3.setImageDrawable(getResources().getDrawable(R.drawable.pic_baike_gou));
						memberInfo.setIS_ATTENTION("1");
						memberInfo.setFans_total_num(memberInfo.getFans_total_num()+1);
						memberInfo.setFans_doctor_total_num(memberInfo.getFans_doctor_total_num()+1);
						tv_doctor_fans_num.setText("医生粉丝  "+memberInfo.getFans_doctor_total_num());
						isOperateAttentionOrCancel = true;
					} else {
						showToast(entry2.getErrormsg());
					}
				}
				break;
			case IResult.CANCEL_ATTENTION:
				destroyDialog();
				CommonResponseEntity entry3 = (CommonResponseEntity) msg.obj;
				if (null != entry3) {
					if (entry3.getSuccess() == 1) {
//						iv_attention.setImageDrawable(getResources().getDrawable(R.drawable.pic_baike_plus));
//						tv_attention.setText("关注");
//						tv_attention.setTextColor(getResources().getColor(R.color.TextColorOrange));
						tv_right_fun_3.setText("关注");
						iv_right_fun_3.setImageDrawable(getResources().getDrawable(R.drawable.pic_baike_plus));
						memberInfo.setIS_ATTENTION("0");
						memberInfo.setFans_total_num(memberInfo.getFans_total_num()-1);
						memberInfo.setFans_doctor_total_num(memberInfo.getFans_doctor_total_num()-1);
						tv_doctor_fans_num.setText("医生粉丝  "+memberInfo.getFans_doctor_total_num());
						isOperateAttentionOrCancel = true;
					} else {
						showToast(entry3.getErrormsg());
					}
				}
				break;
			case IResult.GIVE_PRAISE_INNER:
				int id= msg.arg1;
				praise_position = msg.arg2;
				givePraise(""+id);
				break;
			case IResult.GIVE_PRAISE:
				destroyDialog();
				CommonResponseEntity response2 = (CommonResponseEntity)msg.obj;
				if (null != response2) {
					if (1 == response2.getSuccess()) {
						if(praise_position >= 0){
							int num = Integer.parseInt(shuoshuoList.get(praise_position).getPRAISE_NUM());
							shuoshuoList.get(praise_position).setPRAISE_NUM((num+1)+"");
							shuoshuoList.get(praise_position).setIS_PRAISED(true);
							adapter.change(shuoshuoList);
						}
					} else {
						showToast(response2.getErrormsg());
					}
				}
				praise_position = -1;
				break;
			case IResult.DATA_ERROR:
				listView.onRefreshComplete();
				destroyDialog();
				break;
			case IResult.NET_ERROR:
				showToast("网络异常");
				listView.onRefreshComplete();
				destroyDialog();
				break;
			case ShareSdkUtil.onError:
				showToast("分享失败");
				break;
			case ShareSdkUtil.onComplete:
				showToast("分享成功");
				setShare(msg.arg1);
				break;
			case ShareSdkUtil.onCancel:
				showToast("取消分享");
				break;
			case IResult.SET_SHARE:
				destroyDialog();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			destroyDialog();
		}
		super.onResult(msg);
	}
	
	private void showPersonInfo(){
		tv_name.setText(memberInfo.getREALNAME());
		tv_professional.setText(memberInfo.getPROFESSIONAL());
		tv_hospital.setText(memberInfo.getHOSPITALNAME());
		tv_shanchang.setText(memberInfo.getSPECIALITY());
		tv_attention_num.setText("关注  "+ memberInfo.getAttention_total_num());
		tv_doctor_fans_num.setText("医生粉丝  "+ memberInfo.getFans_doctor_total_num());
		tv_user_fans_num.setText("患者粉丝  "+ memberInfo.getFans_user_total_num());
		tv_shuoshuo_total.setText("全部说说 （"+total_shuoshuo+"）");
		imageLoader.displayImage(memberInfo.getAVATAR(), iv_photo, Util.getOptions_avatar());
		tv_today_visit_num.setText(memberInfo.getBaike_visitors_today());
		tv_total_visit_num.setText(memberInfo.getBAIKE_VISITORS());
		
		//已认证图标
		if(!TextUtils.isEmpty(memberInfo.getAUDITSTATUS()) && "1".equals(memberInfo.getAUDITSTATUS())){
			follow.setChecked(true);
		} else {
			follow.setChecked(false);
		}
	}
	
	public void doClick(View view){
		switch(view.getId()){
		case R.id.btn_back:
			finish();
			break;
		case R.id.ll_right_fun_3:
			/*tv_attention = (TextView) findViewById(R.id.tv_attention);
			iv_attention = (ImageView) findViewById(R.id.iv_attention);*/
//			TextView tv_right_fun_3 = (TextView) findViewById(R.id.tv_right_fun_3);
//			ImageView iv_right_fun_3 = (ImageView) findViewById(R.id.iv_right_fun_3);
			
			if("1".equals(memberInfo.getIS_ATTENTION())){
				cancelAttention();
			} else {
				setAttention();
			}
			break;
		case R.id.iv_share:
			String name;
			String title;
			String text = null;
			String pic = null;
			String url = null;
			String hospital = null;
			String professional = null;
			String speciality = null;
			name = memberInfo.getREALNAME();
			title = "薏米网-"+name;
			if(!TextUtils.isEmpty(memberInfo.getHOSPITALNAME()))
				hospital = memberInfo.getHOSPITALNAME();
			if(!TextUtils.isEmpty(memberInfo.getPROFESSIONAL()))
			    professional = memberInfo.getPROFESSIONAL();
			if(!TextUtils.isEmpty(memberInfo.getSPECIALITY()))
			    speciality = memberInfo.getSPECIALITY();
			
			text = hospital+ ","+ professional+","+speciality;
			
			if(!TextUtils.isEmpty(memberInfo.getAVATAR())){
				pic = memberInfo.getAVATAR();
			}
			
			if(!TextUtils.isEmpty(memberInfo.getShare_url())){
				url = memberInfo.getShare_url();
			}
			
			if(null == share){
				share = new ShareSdkUtil(this, handler, ShareSdkUtil.SHARE_BAIKE_HOME, memberInfo.getMEMBERID());
			}
			share.initModePopupWindow( fl_topLayout, title, text, url, pic);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/*if (REQUEST_SAYS_DETAIL == requestCode) {
			page = 1;
			getOtherBaikeInfo();
		} */
	}
	
	private class InnerReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MyApplication.WECHAT_SHARE_SUCCESS)){
				int type = intent.getIntExtra("share_type", ShareSdkUtil.SHARE_TO_WECHAT);
				int source = intent.getIntExtra("share_source", ShareSdkUtil.SHARE_BAIKE_SAYS);
				String doctor_id = intent.getStringExtra("share_doctor_id");
				if(source == ShareSdkUtil.SHARE_BAIKE_HOME && doctor_id.equals(memberInfo.getMEMBERID())){
					setShare(type);
					ShareSdkUtil.setShare_doctor_id(null);
				}
			}else if(intent.getAction().equals(BaikeHomeFragment.UPDATE_MY_SAYS_LIST)){
				
				BaikeShuoshuoInfo detail = (BaikeShuoshuoInfo) intent.getSerializableExtra("says_detail");
				boolean isMyBaike = intent.getBooleanExtra("is_my_baike", false);
				
				if(shuoshuoList != null && shuoshuoList.size() > 0 && (!isMyBaike)){
					for(int i = 0; i < shuoshuoList.size(); i++){
						BaikeShuoshuoInfo temp = shuoshuoList.get(i);
						if(temp.getSAYSID().equals(detail.getSAYSID())){
							detail.setCONTENT_SHOW(temp.getCONTENT_SHOW());
							detail.setIS_NEW(temp.getIS_NEW());
							detail.setRNUM(temp.getRNUM());
							shuoshuoList.set(i, detail);
							adapter.change(shuoshuoList);
							break;
						}
					}
				} 
			}
		}
	}
	
	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(isOperateAttentionOrCancel 
			 /*&& DoctorFriendFragment.class.getSimpleName().equals(from)*/){
			Intent intent = new Intent(ActionFilter.REQUEST_DOCTOR_FRIEND_LIST);
			sendBroadcast(intent);
		}
		
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_other_baike);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_attention);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_praise);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.cancel_attention);
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.set_share);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
