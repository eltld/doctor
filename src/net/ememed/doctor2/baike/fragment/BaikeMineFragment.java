package net.ememed.doctor2.baike.fragment;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.baike.BaikeHomeActivity;
import net.ememed.doctor2.baike.BaikeVisitorActivity;
import net.ememed.doctor2.baike.MyAttentionActivity;
import net.ememed.doctor2.baike.MyFansActivity;
import net.ememed.doctor2.baike.RewardListActivity;
import net.ememed.doctor2.baike.entity.BaikeMineEntry;
import net.ememed.doctor2.baike.entity.BaikeMineInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaikeMineFragment extends Fragment implements Callback, OnClickListener{
	private BaikeHomeActivity activity = null;
	private Handler mHandler = null;
//	private BaikeMemberInfo info;
	private View totalView;
	
	private ImageView btn_back;
	private CircleImageView iv_photo;
	private TextView tv_name;
	private TextView tv_professional;
	private TextView tv_zhuanke;
	private TextView tv_hospital;
	private TextView tv_shanchang;
	private TextView tv_attention_num;
	private TextView tv_doctor_fans_num;
	private TextView tv_user_fans_num; 
	private CheckBox follow;
	private TextView tv_today_visit_num;
	private TextView tv_total_visit_num;
	private TextView tv_redpoint_attention;
	private TextView tv_redpoint_doctor_fans;
	private TextView tv_redpoint_user_fans;
	
	private LinearLayout ll_doctor_fans_area;
	private LinearLayout ll_user_fans_area;
	private LinearLayout ll_attention_area;
	
	private TextView tv_money;
	private TextView tv_visitor;
	private TextView tv_red_point_visitor;
	private TextView tv_praise;
	private TextView tv_comment;
	private TextView tv_share;
	private BaikeMineInfo baikeMineInfo = null;
	private LinearLayout ll_money;
	private LinearLayout ll_visitor;
	
	public BaikeMineFragment(){
		this.activity = (BaikeHomeActivity) getActivity();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (BaikeHomeActivity) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		totalView = inflater.inflate(R.layout.fragment_baike_mine, null);
		setupView(totalView);
		return totalView;
	}
	
	/*@Override
	public void onResume() {
		super.onResume();
		getMyJoinInfo();
	}*/
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			getMyJoinInfo();
		} else {
			MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_myjoin_info);
		}
	}
	
	private void setupView(View view){
		btn_back = (ImageView) view.findViewById(R.id.btn_back);
		TextView tv_shuoshuo_total = (TextView) view.findViewById(R.id.tv_shuoshuo_total);
		tv_shuoshuo_total.setVisibility(View.GONE);
		TextView tv_menu = (TextView) view.findViewById(R.id.tv_menu);
		tv_menu.setVisibility(View.GONE);
		ImageView iv_down_arrow = (ImageView) view.findViewById(R.id.iv_down_arrow);
		iv_down_arrow.setVisibility(View.GONE);
		TextView top_title = (TextView) view.findViewById(R.id.top_title);
		top_title.setText("个人中心");
		ImageView iv_next = (ImageView) view.findViewById(R.id.iv_next);
		iv_next.setVisibility(View.GONE);
		
		iv_photo = (CircleImageView) view.findViewById(R.id.iv_photo);
		tv_name = (TextView) view.findViewById(R.id.tv_name);
		tv_professional = (TextView) view.findViewById(R.id.tv_professional);
		tv_zhuanke = (TextView) view.findViewById(R.id.tv_zhuanke);
		tv_hospital = (TextView) view.findViewById(R.id.tv_hospital);
		tv_shanchang = (TextView) view.findViewById(R.id.tv_shanchang);
		tv_attention_num = (TextView) view.findViewById(R.id.tv_attention_num);
		tv_doctor_fans_num = (TextView) view.findViewById(R.id.tv_doctor_fans_num);
		tv_user_fans_num = (TextView) view.findViewById(R.id.tv_user_fans_num);
		follow = (CheckBox) view.findViewById(R.id.follow);
		tv_today_visit_num = (TextView) view.findViewById(R.id.tv_today_visit_num);
		tv_total_visit_num = (TextView) view.findViewById(R.id.tv_total_visit_num);
		tv_redpoint_attention = (TextView) view.findViewById(R.id.tv_redpoint_attention);
		tv_redpoint_doctor_fans = (TextView) view.findViewById(R.id.tv_redpoint_doctor_fans);
		tv_redpoint_user_fans = (TextView) view.findViewById(R.id.tv_redpoint_user_fans);
		ll_doctor_fans_area = (LinearLayout) view.findViewById(R.id.ll_doctor_fans_area);
		ll_user_fans_area = (LinearLayout) view.findViewById(R.id.ll_user_fans_area);
		ll_attention_area = (LinearLayout) view.findViewById(R.id.ll_attention_area);
		
		tv_money = (TextView) view.findViewById(R.id.tv_money);
		tv_visitor = (TextView) view.findViewById(R.id.tv_visitor);
		tv_red_point_visitor = (TextView) view.findViewById(R.id.tv_red_point_visitor);
		tv_praise = (TextView) view.findViewById(R.id.tv_praise);
		tv_comment = (TextView) view.findViewById(R.id.tv_comment);
		tv_share = (TextView) view.findViewById(R.id.tv_share);
		
		ll_money = (LinearLayout) view.findViewById(R.id.ll_money);
		ll_visitor = (LinearLayout) view.findViewById(R.id.ll_visitor);
		
		totalView.setVisibility(View.GONE);
		
		addListener();
	}
	
	
	private void addListener() {
		ll_doctor_fans_area.setOnClickListener(this);
		ll_user_fans_area.setOnClickListener(this);
		ll_attention_area.setOnClickListener(this);
		ll_money.setOnClickListener(this);
		ll_visitor.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		
	}
	
	private void showPersonInfo(BaikeMineInfo memberInfo) {
		tv_name.setText(memberInfo.getREALNAME());
		tv_professional.setText(memberInfo.getPROFESSIONAL());
		tv_hospital.setText(memberInfo.getHOSPITALNAME());
		tv_shanchang.setText(memberInfo.getSPECIALITY());
		tv_attention_num.setText(memberInfo.getAttention_total_num()+"");
		tv_doctor_fans_num.setText(memberInfo.getFans_doctor_total_num()+"");
		tv_user_fans_num.setText(memberInfo.getFans_user_total_num()+"");
		tv_today_visit_num.setText(memberInfo.getToday_bkvisitors_total_num()+"");
		tv_total_visit_num.setText(memberInfo.getBAIKE_VISITORS()+"");

		
		if ((!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS)))
				&& "1".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))
				&& (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR)))) {
			activity.imageLoader.displayImage(SharePrefUtil.getString(Conast.AVATAR), iv_photo, Util.getOptions_avatar());
		} else {
			iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
		}

		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))
				&& "1".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))) {
			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Type))) {
				if ("1".equals(SharePrefUtil.getString(Conast.Doctor_Type))) {
					tv_zhuanke.setText(getString(R.string.doctor_type_quanke));
				} else {
					tv_zhuanke.setText(getString(R.string.doctor_type_zhuanke));
				}
			}
		} else {
			tv_zhuanke.setText("未认证");
			tv_zhuanke.setBackgroundResource(R.drawable.shape_circle_rectangle_gray);
		}

		// 新增关注的红点
//		if (memberInfo.getAttention_unread_num() > 0) {
//			tv_redpoint_attention.setVisibility(View.VISIBLE);
//		} else {
			tv_redpoint_attention.setVisibility(View.GONE);
//		}

		// 新增医生粉丝的红点
//		if (memberInfo.getFans_doctor_unread_num()> 0) {
//			tv_redpoint_doctor_fans.setVisibility(View.VISIBLE);
//			tv_redpoint_doctor_fans.setText("+" + memberInfo.getFans_doctor_unread_num());
//		} else {
			tv_redpoint_doctor_fans.setVisibility(View.GONE);
//		}
		
		// 新增患者粉丝的红点
//		if (memberInfo.getFans_doctor_unread_num()> 0) {
//			tv_redpoint_user_fans.setVisibility(View.VISIBLE);
//			tv_redpoint_user_fans.setText("+" + memberInfo.getFans_user_unread_num());
//		} else {
			tv_redpoint_doctor_fans.setVisibility(View.GONE);
//		}
		
		//已认证图标
		if(!TextUtils.isEmpty(memberInfo.getAUDITSTATUS()) && "1".equals(memberInfo.getAUDITSTATUS())){
			follow.setChecked(true);
		} else {
			follow.setChecked(false);
		}
		
		tv_money.setText("￥"+memberInfo.getBounty_total_money());
		tv_visitor.setText(memberInfo.getBAIKE_VISITORS()+"人");
		tv_praise.setText(memberInfo.getMypraise_total_num()+"个");
		tv_comment.setText(memberInfo.getMycomment_total_num()+"条");
		tv_share.setText(memberInfo.getMyshare_total_num()+"次");
		if(memberInfo.getNew_bkvisitors_total_num() > 0){
			tv_red_point_visitor.setVisibility(View.VISIBLE);
			int num = memberInfo.getNew_bkvisitors_total_num();
			tv_red_point_visitor.setText(num > 99 ? "N" : (num+""));
		} else {
			tv_red_point_visitor.setVisibility(View.INVISIBLE);
		}
	}
	
	private void getMyJoinInfo() {
		if (NetWorkUtils.detect(activity)) {
			if(null == baikeMineInfo){
				activity.loading(null);
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_myjoin_info, BaikeMineEntry.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_MY_JOIN_INFO;
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
	

	@Override
	public boolean handleMessage(Message msg) {
		activity.destroyDialog();
		try {
			switch (msg.what) {
			case IResult.GET_MY_JOIN_INFO:
				BaikeMineEntry entry = (BaikeMineEntry) msg.obj;
				if (null != entry) {
					if (entry.getSuccess() == 1) {
						totalView.setVisibility(View.VISIBLE);
						baikeMineInfo = entry.getData();
						showPersonInfo(baikeMineInfo);
					}
				}
				break;
			case IResult.DATA_ERROR:
				activity.destroyDialog();
				break;
			case IResult.NET_ERROR:
				activity.showToast("网络异常");
				activity.destroyDialog();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void visitor(View v) {
		
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()){
		case R.id.ll_doctor_fans_area:
			tv_redpoint_doctor_fans.setVisibility(View.GONE);
			intent = new Intent(activity, MyFansActivity.class);
			intent.putExtra("fans_type", 1);
			startActivity(intent);
//			startActivityForResult(intent3, REQUEST_DOCTOR_FANS_LIST);
			break;
		case R.id.ll_user_fans_area:
			tv_redpoint_user_fans.setVisibility(View.GONE);
			intent = new Intent(activity, MyFansActivity.class);
			intent.putExtra("fans_type", 2);
			startActivity(intent);
//			startActivityForResult(intent4, REQUEST_USER_FANS_LIST);
			break;
		case R.id.ll_attention_area:
			tv_redpoint_attention.setVisibility(View.GONE);
//			startActivityForResult(new Intent(activity, MyAttentionActivity.class), REQUEST_ATTENTION_LIST);
			intent = new Intent(activity, MyAttentionActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_money:
			intent = new Intent(activity, RewardListActivity.class);
			intent.putExtra("total_money", baikeMineInfo.getBounty_total_money());
			startActivity(intent);
			break;
		case R.id.ll_visitor:
			tv_red_point_visitor.setVisibility(View.INVISIBLE);
			activity.startActivity(new Intent(activity, BaikeVisitorActivity.class));
			break;
		case R.id.btn_back:
			activity.finish();
			break;
		default:
			break;
		}
	}
}
