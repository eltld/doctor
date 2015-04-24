package net.ememed.doctor2.fragment;

import java.util.HashMap;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BusinessCardActivity;
import net.ememed.doctor2.activity.MainActivity;
import net.ememed.doctor2.activity.NewsActivity;
import net.ememed.doctor2.activity.OrderClassifyActivity;
import net.ememed.doctor2.activity.PersonInfoActivity;
import net.ememed.doctor2.activity.QuestionPoolActivity;
import net.ememed.doctor2.activity.RegisterSuccessActivity;
import net.ememed.doctor2.activity.RemindOpenVerifyActivity;
import net.ememed.doctor2.activity.ServiceAllSettingActivity;
import net.ememed.doctor2.activity.YimiScoreActivity;
import net.ememed.doctor2.baike.BaikeHomeActivity;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.OrderInformation;
import net.ememed.doctor2.entity.OrderListEntity;
import net.ememed.doctor2.entity.OrderOpenCount;
import net.ememed.doctor2.entity.ServiceSettingEntry;
import net.ememed.doctor2.entity.ServiceSettingInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.Logger;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 2.0新版主页tab
 * 
 * @author llj
 */
public class HomePageFragment extends Fragment implements BasicUIEvent, Callback, OnClickListener{
	private static final String TAG = HomePageFragment.class.getSimpleName();
	public static final int EXEU_GET_DATA = 0;
	private final String MYTAG = "llj,HomepageFragment";
	private MainActivity activity = null;
	private Handler mHandler = null;
	private View convertView;				// 弹出窗口的控件需要通过 convertView.findViewByid 来获得示例
	
	private String doctorID;
	private String auditStatus = null;		//资料审核状态
	
	private CircleImageView image_person;	//头像
	private LinearLayout ll_audit_lab;		//红色的认证标签
	private TextView  tv_audit_text;		//认证标签上显示的文本
	private RelativeLayout rl_doctor_info;	//医生信息布局框（姓名，职务，医院）
	private TextView doctor_name;			//真实姓名
	private View view_cut_line;				//分割竖线
	private TextView doctor_position;		//职位
	private TextView doctor_hospital;		//就职医院
	
	private FrameLayout fl_consult_picture;//图文咨询
	private FrameLayout fl_consult_phone;	//预约通话
	private FrameLayout fl_private_doctor;	//私人医生服务
//	private FrameLayout fl_shangmen;		//上门会诊
	private FrameLayout fl_jinji_jiahao;	//预约加号
	private FrameLayout fl_jinji_zhuyuan;	//预约住院
//	private FrameLayout fl_other_service;	//其他服务
	private FrameLayout fl_service_setting;//服务设置
	private FrameLayout fl_question_pool; //一问一答
	private ImageView img_ememed_grade;  //薏米花
	
	private boolean flag_consult_picture = false;  //图文咨询服务开通标记		
	private boolean flag_consult_phone = false;    //预约通话服务开通标记		
	private boolean flag_private_doctor = false;   //私人医生服务服务开通标记		
	private boolean flag_shangmen = false;         //上门会诊服务开通标记		
	private boolean flag_jinji_jiahao = false;     //预约加号服务开通标记
	private boolean flag_jinji_zhuyuan = false;    //预约住院服务开通标记		
	private boolean flag_other_service = true;     //其他服务开通标记(默认开通)
	
	private LinearLayout ll_red_dot_1;			//红点	
	private LinearLayout ll_red_dot_2;			
	private LinearLayout ll_red_dot_3;			
//	private LinearLayout ll_red_dot_4;			
	private LinearLayout ll_red_dot_5;			
	private LinearLayout ll_red_dot_6;			
	private LinearLayout ll_red_dot_7;	
	
	private TextView tv_red_dot_1;		//红点上的数字	
	private TextView tv_red_dot_2;
	private TextView tv_red_dot_3;
//	private TextView tv_red_dot_4;
	private TextView tv_red_dot_5;
	private TextView tv_red_dot_6;
//	private TextView tv_red_dot_7;
	
	private ImageView iv_consult_picture;	//未开通标签
	private ImageView iv_consult_phone;	
	private ImageView iv_private_doctor;	
//	private ImageView iv_shangmen;		
	private ImageView iv_jinji_jiahao;	
	private ImageView iv_jinji_zhuyuan;
	
	private LinearLayout ll_tab1;
	
	private LinearLayout ll_news;			//新闻资讯
	private LinearLayout ll_invite_patient;	//邀请患者
	private PopupWindow popup = null;
	private ServiceSettingInfo serviceInfo = null;
	private OrderOpenCount orderOpenCount;
	
	private TextView tv_baike;
	
	private AlertDialog myDialog;		//如果医生资料未审核通过，点击邀请患者时弹出的对话框
	
//	private boolean getServiceDateFlag = false;		//获取服务设置数据标记，获取成功置为true;
	private ImageView iv_red_point_baike;
		
	public HomePageFragment() {
		this.activity = (MainActivity) getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mHandler = new Handler(this);
		
		if(!SharePrefUtil.getBoolean(Conast.IS_SAVE_SERVICE_INFO)){
			getServiceSetInfo();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// fragment可见时执行加载数据或者进度条等
			Logger.dout(TAG + "isVisibleToUser");
		} else {
			// 不可见时不执行操作
			Logger.dout(TAG + "unVisibleToUser");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		doctorID = SharePrefUtil.getString(Conast.Doctor_ID);
		View view = inflater.inflate(R.layout.fragment_home_page, null);
		
		setupView(view);
		return view;
	}
	
	
	private void setupView(View view) {
		
		ll_tab1 = (LinearLayout) view.findViewById(R.id.ll_tab1);
		
		iv_red_point_baike = (ImageView) view.findViewById(R.id.iv_red_point_baike);
		
		//头像
		image_person = (CircleImageView) view.findViewById(R.id.civ_doctor_photo);
		image_person.setOnClickListener(this);
		
		rl_doctor_info = (RelativeLayout)view.findViewById(R.id.rl_doctor_info);
		
		auditStatus = SharePrefUtil.getString(Conast.AUDIT_STATUS);
		//根据bug 279 删除此逻辑
//		//根据医生的资料认证状态显示头像，认证通过则显示自身头像，未通过显示默认头像
//		if(auditStatus.equals("1")){
//			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))) {
//				activity.imageLoader.displayImage(SharePrefUtil.getString(Conast.AVATAR), image_person, Util.getOptions_avatar());
//			} else {
//				image_person.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
//			}
//		} else {
//			image_person.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
//		}
		String portrait = SharePrefUtil.getString(Conast.AVATAR);
		if (TextUtils.isEmpty(portrait)) 
			portrait = SharePrefUtil.getString(Conast.AVATAR_UNAUDIT);
		Log.i("测试", portrait);
		ImageLoader.getInstance().displayImage(portrait, image_person, Util.getOptions_avatar());
		
		//红色的开启认证标签
		/** 资质审核状态0=未上传，1=审核通过，2=待审，3=有误 */
		ll_audit_lab = (LinearLayout) view.findViewById(R.id.ll_audit_lab);
		ll_audit_lab.setOnClickListener(this);

		
		//认证标签上的文字
		tv_audit_text = (TextView) view.findViewById(R.id.tv_audit_lab);
	//	setLabText();
		
		doctor_name = (TextView) view.findViewById(R.id.tv_doctor_name);
		doctor_position = (TextView) view.findViewById(R.id.tv_doctor_position);
		doctor_hospital = (TextView) view.findViewById(R.id.tv_doctor_hospital);
		view_cut_line = (View) view.findViewById(R.id.view_cut_line);
		if(auditStatus.equals("1")){
			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Name))) {
				doctor_name.setText(SharePrefUtil.getString(Conast.Doctor_Name));
			}
			
			view_cut_line.setVisibility(View.VISIBLE);
			doctor_position.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Professional))) {
				doctor_position.setText(SharePrefUtil.getString(Conast.Doctor_Professional));
			}
			
			if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.HOSPITAL_NAME))) {
				doctor_hospital.setText(SharePrefUtil.getString(Conast.HOSPITAL_NAME));
			}
			
		} else {
			view_cut_line.setVisibility(View.GONE);
			doctor_position.setVisibility(View.GONE);
			doctor_name.setText("医生资质待认证");
			if(!TextUtils.isEmpty(SharePrefUtil.getString("account")))
			{
				//电话显示格式为【130****1234】
				String phone = SharePrefUtil.getString("account");
				phone = phone.substring(0, 3) + "****" + phone.substring(7);
				doctor_hospital.setText("【"+ phone +"】");
			}
		}

		fl_consult_picture = (FrameLayout) view.findViewById(R.id.fl_consult_picture);
		fl_consult_picture.setOnClickListener(this);
		fl_consult_phone = (FrameLayout) view.findViewById(R.id.fl_consult_phone);
		fl_consult_phone.setOnClickListener(this);
		fl_private_doctor = (FrameLayout) view.findViewById(R.id.fl_private_doctor);
		fl_private_doctor.setOnClickListener(this);
//		fl_shangmen = (FrameLayout) view.findViewById(R.id.fl_shangmen);
//		fl_shangmen.setOnClickListener(this);
		fl_jinji_jiahao = (FrameLayout) view.findViewById(R.id.fl_jinji_jiahao);
		fl_jinji_jiahao.setOnClickListener(this);
		fl_jinji_zhuyuan = (FrameLayout) view.findViewById(R.id.fl_jinji_zhuyuan);
		fl_jinji_zhuyuan.setOnClickListener(this);
//		fl_other_service = (FrameLayout) view.findViewById(R.id.fl_other_service);
//		fl_other_service.setOnClickListener(this);
		fl_service_setting = (FrameLayout) view.findViewById(R.id.fl_service_setting);
		fl_service_setting.setOnClickListener(this);
		fl_question_pool = (FrameLayout) view.findViewById(R.id.fl_question_pool);
		fl_question_pool.setOnClickListener(this);
		img_ememed_grade=(ImageView) view.findViewById(R.id.img_ememed_grade);
		img_ememed_grade.setOnClickListener(this);
		
		ll_news = (LinearLayout) view.findViewById(R.id.ll_news);
		ll_news.setOnClickListener(this);
		ll_invite_patient = (LinearLayout) view.findViewById(R.id.ll_invite_patient);
		ll_invite_patient.setOnClickListener(this);
		
		iv_consult_picture = (ImageView) view.findViewById(R.id.flag_1);
		iv_consult_phone = (ImageView) view.findViewById(R.id.flag_2);
		iv_private_doctor = (ImageView) view.findViewById(R.id.flag_3);
//		iv_shangmen = (ImageView) view.findViewById(R.id.flag_4);
		iv_jinji_jiahao = (ImageView) view.findViewById(R.id.flag_5);
		iv_jinji_zhuyuan = (ImageView) view.findViewById(R.id.flag_6);
		
		ll_red_dot_1 = (LinearLayout) view.findViewById(R.id.ll_red_dot_1);
		ll_red_dot_2 = (LinearLayout) view.findViewById(R.id.ll_red_dot_2);
		ll_red_dot_3 = (LinearLayout) view.findViewById(R.id.ll_red_dot_3);
//		ll_red_dot_4 = (LinearLayout) view.findViewById(R.id.ll_red_dot_4);
		ll_red_dot_5 = (LinearLayout) view.findViewById(R.id.ll_red_dot_5);
		ll_red_dot_6 = (LinearLayout) view.findViewById(R.id.ll_red_dot_6);
//		ll_red_dot_7 = (LinearLayout) view.findViewById(R.id.ll_red_dot_7);
		
		tv_red_dot_1 = (TextView) view.findViewById(R.id.tv_red_dot_1);
		tv_red_dot_2 = (TextView) view.findViewById(R.id.tv_red_dot_2);
		tv_red_dot_3 = (TextView) view.findViewById(R.id.tv_red_dot_3);
//		tv_red_dot_4 = (TextView) view.findViewById(R.id.tv_red_dot_4);
		tv_red_dot_5 = (TextView) view.findViewById(R.id.tv_red_dot_5);
		tv_red_dot_6 = (TextView) view.findViewById(R.id.tv_red_dot_6);
//		tv_red_dot_7 = (TextView) view.findViewById(R.id.tv_red_dot_7);
		
		tv_baike = (TextView) view.findViewById(R.id.tv_baike);
		tv_baike.setOnClickListener(this);
	
		Logger.dout("进入弹出窗口初始化");
		//初始化认证提示框
		initAuditPopupWindows();
		Logger.dout("弹出窗口初始化完成");
		
		getOrderListByOrderType(1);
		auditStatus = SharePrefUtil.getString(Conast.AUDIT_STATUS);
		setLabText();
		setServiceLab();
	}

	private void setLabText() {
		/*当医生账户审核通过之前，为默认头像。同时该时存在【开启认证】状态，点击开启认证，
		 * 进入资质资料提交页面。提交认证后至认证通过之前，状态显示为【认证中…】、认证失败
		 * 显示【认证失败】，点击可重新认证。认证通过后，标记消失，头像切换为认证头像。未认
		 * 证之前，点击【开启认证】进入认证流程，头像点击无效果。认证成功后，点击头像及右侧
		 * 姓名等文字区块进入个人资料页面。
		 */
		if(auditStatus.equals("1")){
			ll_audit_lab.setVisibility(View.GONE);
		} else{
			ll_audit_lab.setVisibility(View.VISIBLE);
			if(auditStatus.equals("0")){
				tv_audit_text.setText("开启认证");
			} else if(auditStatus.equals("2")){
				tv_audit_text.setText("认证中...");
			} else if(auditStatus.equals("3")){
				tv_audit_text.setText("认证失败");
			}
		}
	}
	
	@Override
	public void execute(int mes, Object obj) {
		switch (mes) {
		case EXEU_GET_DATA:

			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	//'1'=>'图文咨询','2'=>'预约通话','3'=>'预约加号','4'=>'上门会诊',
	//'5'=>'院内陪诊','7'=>'免费挂号','11'=>'体检报告解读','12'=>'个人健康季评',
	//'14'=>'预约住院','15'=>'私人医生服务','16'=>'其他服务','30'=>'科研调查奖励'
	@Override
	public void onClick(View v) {
		Intent intent = null;
		OrderInformation info = null;
		
		switch(v.getId()){
		case R.id.civ_doctor_photo:
		case R.id.rl_doctor_info:
		case R.id.ll_audit_lab:
			if(auditStatus.equals("1")){		//审核通过，跳转到个人资料的页面
				intent = new Intent(activity, PersonInfoActivity.class);
				startActivity(intent);
			} else if(auditStatus.equals("2") || auditStatus.equals("3")){	//正在审核或审核失败，跳转到完善资料页面
				intent = new Intent(activity, RegisterSuccessActivity.class);
				startActivity(intent);
			} else if(auditStatus.equals("0")){ //未提交审核
				startActivity(new Intent(activity, RemindOpenVerifyActivity.class));
			}
			break;
		case R.id.fl_consult_picture:	//图文咨询
			intent = new Intent(activity, OrderClassifyActivity.class);
			info = new OrderInformation("图文咨询订单", "1", R.drawable.icon_consult_picture, flag_consult_picture);
			intent.putExtra("order_info", info);
			startActivity(intent);
			break;
		case R.id.fl_consult_phone:		//预约通话
			intent = new Intent(activity, OrderClassifyActivity.class);
			info = new OrderInformation("预约通话订单", "2", R.drawable.icon_consult_phone, flag_consult_phone);
			intent.putExtra("order_info", info);
			startActivity(intent);
			break;
		case R.id.fl_private_doctor:	//私人医生服务
			intent = new Intent(activity, OrderClassifyActivity.class);
			info = new OrderInformation("私人医生服务订单", "15", R.drawable.icon_private_doctor, flag_private_doctor);
			intent.putExtra("order_info", info);
			startActivity(intent);
			break;
//		case R.id.fl_shangmen:			//上门会诊
//			intent = new Intent(activity, OrderClassifyActivity.class);
//			info = new OrderInformation("上门会诊订单", "4", R.drawable.icon_shangmen, flag_shangmen);
//			intent.putExtra("order_info", info);
//			startActivity(intent);
//			break;
		case R.id.fl_jinji_jiahao:		//预约加号
			intent = new Intent(activity, OrderClassifyActivity.class);
			info = new OrderInformation("预约加号订单", "3", R.drawable.icon_jinji_jiahao, flag_jinji_jiahao);
			intent.putExtra("order_info", info);
			startActivity(intent);
			break;
		case R.id.fl_jinji_zhuyuan:		//预约住院
			intent = new Intent(activity, OrderClassifyActivity.class);
			info = new OrderInformation("预约住院订单", "14", R.drawable.icon_jinji_zhuyuan, flag_jinji_zhuyuan);
			intent.putExtra("order_info", info);
			startActivity(intent);
			break;
//		case R.id.fl_other_service:		//其他服务
//			intent = new Intent(activity, OrderClassifyActivity.class);
//			info = new OrderInformation("其他服务订单", "16", R.drawable.icon_other_service, flag_other_service);
//			intent.putExtra("order_info", info);
//			startActivity(intent);
//			break;
		case R.id.fl_service_setting:	//服务设置
			startActivity(new Intent(activity, ServiceAllSettingActivity.class));
			break;
		case R.id.ll_invite_patient:	//邀请患者
			//XXX测试完毕记得删除
//			SharePrefUtil.putBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID), false);
//			SharePrefUtil.putBoolean(Conast.VALIDATED, false);
//			SharePrefUtil.putString(Conast.AUDIT_STATUS, "0");
//			SharePrefUtil.commit();
			//XXX
			
			if(false == SharePrefUtil.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID))){
				if(true == SharePrefUtil.getBoolean(Conast.VALIDATED)){
					//startActivity(new Intent(activity, RegisterSuccessActivity.class));
					startActivity(new Intent(activity, PersonInfoActivity.class));
				} else {
					if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
						if("0".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
							showAlertDialogue();
						} else {
							startActivity(new Intent(activity, RegisterSuccessActivity.class));
						}
					}
				}
			} else {
				startActivity(new Intent(activity, BusinessCardActivity.class));
			}
			break;
		case R.id.ll_news:			    //新闻资讯
			intent = new Intent(activity, NewsActivity.class);
			startActivity(intent);
			break;
		case R.id.fl_question_pool:       //一问一答
			intent = new Intent(activity, QuestionPoolActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_baike:    //个人百科
//			intent = new Intent(activity, PersonBaikeActivity.class);
			intent = new Intent(activity, BaikeHomeActivity.class);
			startActivity(intent);
			break;
		case R.id.img_ememed_grade:
			startActivity(new Intent(activity,YimiScoreActivity.class));
			break;
		default:
				break;
		}
	}

	private void gotoAudit() {
		//XXX未认证之前，点击此处是否跳到RegisterSuccessActivity
		Intent intent;
		if(auditStatus.equals("0")){	
			intent = new Intent(activity, RegisterSuccessActivity.class);
			intent.putExtra("verify_type", -1);
			intent.putExtra("verify_title", "帐户尚未激活");
			intent.putExtra("verify_content", "启用医生服务需要验证身份");
			startActivity(intent);
		} else if(auditStatus.equals("2")){
			intent = new Intent(activity, RegisterSuccessActivity.class);
			intent.putExtra("verify_type", 2);
			intent.putExtra("verify_title", "验证信息已提交成功");
			intent.putExtra("verify_content", "薏米助理会在2个工作日内为您核实身份,并给予您通知");
			startActivity(intent);
		} else if(auditStatus.equals("3")){
			intent = new Intent(activity, RegisterSuccessActivity.class);
			intent.putExtra("verify_type", 3);
			intent.putExtra("verify_title", "身份验证失败");
			intent.putExtra("verify_content", SharePrefUtil.getString(Conast.AUDIT_REFUSE));
			startActivity(intent);
		}
	}
	
	/**
	 * 获取服务设置信息
	 */
	private void getServiceSetInfo() {
		if (NetWorkUtils.detect(activity)) {
			//activity.loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.getServiceSet, ServiceSettingInfo.class, params, new Response.Listener() {
				@Override
				public void onResponse(Object response) {

					Message message = new Message();
					message.obj = response;
					message.what = IResult.SERVICE_SET;
					mHandler.sendMessage(message);
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					Message message = new Message();
					message.obj = error.getMessage();
					message.what = IResult.NET_ERROR;
					mHandler.sendMessage(message);
				}
			});
		} else {
			mHandler.sendEmptyMessage(IResult.NET_ERROR);
		}

	}
	
	/**
	 * 初始化认证提示窗口
	 */
	private void initAuditPopupWindows() {
		convertView = View.inflate(activity, R.layout.audit_tip_window, null);

		popup = new PopupWindow(convertView, MyApplication.getInstance().canvasWidth *3/4, LayoutParams.WRAP_CONTENT);
		popup.setOutsideTouchable(false);
		popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popup.setFocusable(true);
		
		Button btn_cancel = (Button) convertView.findViewById(R.id.btn_cancel);
		Button btn_confirm = (Button) convertView.findViewById(R.id.btn_confirm);
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(popup!= null && popup.isShowing()){
					popup.dismiss();
				}
			}
		});
		
		btn_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popup.dismiss();
				gotoAudit();
			}
		});
	}
	
	/**
	 * 设置服务未开通标签
	 */
	private void setServiceLab() {
		//SharedPreferences preferences = activity.getSharedPreferences(AppContext.Preferences_userinfo, Activity.MODE_APPEND);
		
		if(1 == SharePrefUtil.getInt(Conast.FLAG_CONSULT_PICTURE)) {
			iv_consult_picture.setVisibility(View.GONE);
			flag_consult_picture = true;
		} else {
			iv_consult_picture.setVisibility(View.VISIBLE);
		}

		if (1 == SharePrefUtil.getInt(Conast.FLAG_CONSULT_PHONE)) {
			iv_consult_phone.setVisibility(View.GONE);
			flag_consult_phone = true;
		} else {
			iv_consult_phone.setVisibility(View.VISIBLE);
		}

		/*if (1 == SharePrefUtil.getInt(Conast.FLAG_SHANGMEN)) {
			iv_shangmen.setVisibility(View.GONE);
			flag_shangmen = true;
		} else {
			iv_shangmen.setVisibility(View.VISIBLE);
		}*/

		Logger.dout("测试2"+SharePrefUtil.getInt(Conast.FLAG_JINJI_JIAHAO));
		if (1 == SharePrefUtil.getInt(Conast.FLAG_JINJI_JIAHAO)) {
			iv_jinji_jiahao.setVisibility(View.GONE);
			flag_jinji_jiahao = true;
		} else {
			iv_jinji_jiahao.setVisibility(View.VISIBLE);
		}

		Logger.dout("测试2"+SharePrefUtil.getInt(Conast.FLAG_JINJI_ZHUYUAN));
		if (1 == SharePrefUtil.getInt(Conast.FLAG_JINJI_ZHUYUAN)) {
			iv_jinji_zhuyuan.setVisibility(View.GONE);
			flag_jinji_zhuyuan = true;
		} else {
			iv_jinji_zhuyuan.setVisibility(View.VISIBLE);
		}
		
		if (1 == SharePrefUtil.getInt(Conast.FLAG_PRIVATE_DOCTOR)) {
			iv_private_doctor.setVisibility(View.GONE);
			flag_private_doctor = true;
		} else {
			iv_private_doctor.setVisibility(View.VISIBLE);
		} 
	}
	
	/**
	 * 获取订单，目的是拿到当前所有类型的未完成的订单的数量，以便显示红点，此处额外获取了很多垃圾数据，浪费流量，也会造成获取数据时间过长！！！
	 * @param orderType
	 * @param page
	 * @param isLoading
	 */
	private void getOrderListByOrderType(int page){
		
		if (NetWorkUtils.detect(activity)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("type", "2");//获取类型 0 所有订单 1 已结束订单 2 还在进行中的订单
			params.put("page", page + "");

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_order_list, OrderListEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.ORDER_LIST;
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
		try {
			switch (msg.what) {
			case IResult.ORDER_LIST:
				OrderListEntity olEntity = (OrderListEntity) msg.obj;
				if (null != olEntity) {
					if (olEntity.getSuccess() == 1) {
						orderOpenCount = olEntity.getOrder_open_count();
						showOpenOrderCount();
						//个人百科的红点设置
						if(!TextUtils.isEmpty(olEntity.getBaike_status()) && "1".equals(olEntity.getBaike_status())){
							iv_red_point_baike.setVisibility(View.VISIBLE);
						} else {
							iv_red_point_baike.setVisibility(View.GONE);
						}
					} else {
						activity.showToast(olEntity.getErrormsg());
					}
				} else {
					activity.showToast(IMessage.DATA_ERROR);
					
				}
				break;
			case IResult.SERVICE_SET:
				ServiceSettingInfo serviceSettingInfo = (ServiceSettingInfo)msg.obj;
				if (serviceSettingInfo.getSuccess() == 1) {
					saveServiceSetting(serviceSettingInfo.getData());
					setServiceLab();
				} else {
					activity.showToast(serviceSettingInfo.getErrormsg());
				}
				break;
			case IResult.NET_ERROR:
				activity.showToast(IMessage.NET_ERROR);
				break;
			case IResult.DATA_ERROR:
				Logger.dout(msg.getData().toString());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void saveServiceSetting(ServiceSettingEntry serviceEntry) {
		if(serviceEntry != null){
			if(serviceEntry.getTextconsult_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_CONSULT_PICTURE, serviceEntry.getTextconsult_setting().getEnable_textconsult());
			}
			if(serviceEntry.getCall_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_CONSULT_PHONE, serviceEntry.getCall_setting().getEnable_call());
			}
			
			if(serviceEntry.getPacket_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_PRIVATE_DOCTOR, 1);
			} else {
				SharePrefUtil.putInt(Conast.FLAG_PRIVATE_DOCTOR, 0);
			}
			
			if(serviceEntry.getShangmen_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_SHANGMEN, serviceEntry.getShangmen_setting().getEnable_shangmen());
			}
			
			if(serviceEntry.getJiahao_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_JINJI_JIAHAO, serviceEntry.getJiahao_setting().getEnable_jiahao());
			}
			
			if(serviceEntry.getZhuyuan_setting() != null){
				SharePrefUtil.putInt(Conast.FLAG_JINJI_ZHUYUAN, serviceEntry.getZhuyuan_setting().getEnable_zhuyuan());
			}
			
			SharePrefUtil.putInt(Conast.FLAG_OTHER_SERVICE, 1);
			SharePrefUtil.putBoolean(Conast.IS_SAVE_SERVICE_INFO, true);
			SharePrefUtil.commit();
		}
	}

	/**
	 * 红点显示各类型开放订单的数量
	 */
	private void showOpenOrderCount()
	{
		if(orderOpenCount.getO_1() > 0){
			ll_red_dot_1.setVisibility(View.VISIBLE);
			tv_red_dot_1.setText(""+orderOpenCount.getO_1());
		}else{
			ll_red_dot_1.setVisibility(View.GONE);
		}
		
		if(orderOpenCount.getO_2() > 0){
			ll_red_dot_2.setVisibility(View.VISIBLE);
			tv_red_dot_2.setText(""+orderOpenCount.getO_2());
		}else{
			ll_red_dot_2.setVisibility(View.GONE);
		}
		
		if(orderOpenCount.getO_15() > 0){
			ll_red_dot_3.setVisibility(View.VISIBLE);
			tv_red_dot_3.setText(""+orderOpenCount.getO_15());
		}else{
			ll_red_dot_3.setVisibility(View.GONE);
		}
		
		/*if(orderOpenCount.getO_4() > 0){
			ll_red_dot_4.setVisibility(View.VISIBLE);
			tv_red_dot_4.setText(""+orderOpenCount.getO_4());
		}else{
			ll_red_dot_4.setVisibility(View.GONE);
		}*/
		
		if(orderOpenCount.getO_3() > 0){
			ll_red_dot_5.setVisibility(View.VISIBLE);
			tv_red_dot_5.setText(""+orderOpenCount.getO_3());
		}else{
			ll_red_dot_5.setVisibility(View.GONE);
		}
		
		if(orderOpenCount.getO_14() > 0){
			ll_red_dot_6.setVisibility(View.VISIBLE);
			tv_red_dot_6.setText(""+orderOpenCount.getO_14());
		}else{
			ll_red_dot_6.setVisibility(View.GONE);
		}
		
		/*if(orderOpenCount.getO_16() > 0){
			ll_red_dot_7.setVisibility(View.VISIBLE);
			tv_red_dot_7.setText(""+orderOpenCount.getO_16());
		}else{
			ll_red_dot_7.setVisibility(View.GONE);
		}*/
	}
	
	
	/**
	 * 初始化下拉框
	 */
	private void showAlertDialogue(){
		if(null == myDialog){
			myDialog = new AlertDialog.Builder(activity).create();
			View view = LayoutInflater.from(activity).inflate(R.layout.dialog_cannot_private_patient, null);
			/*view.setMinimumHeight(MyApplication.getInstance().canvasHeight*3/5);
			view.setMinimumWidth(MyApplication.getInstance().canvasWidth*3/4);*/
			LayoutParams lps = view.getLayoutParams();
			
			if(lps == null){
				lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			lps.height = LayoutParams.WRAP_CONTENT;
			lps.width = LayoutParams.WRAP_CONTENT;
			view.setLayoutParams(lps);
			
			myDialog.setCanceledOnTouchOutside(true);
			Button btn = (Button) view.findViewById(R.id.button1);
			btn.setOnClickListener(new OnClickListener() {
		
				@Override
				public void onClick(View v) {
//					if("2".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
//						startActivity(new Intent(activity, VerifingActivity.class));
//					} else if("3".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
//						startActivity(new Intent(activity, VerifyFailedActivity.class));
//					} else if("0".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
						//直接跳到完善资料的部分
						Intent intent = new Intent(activity, RegisterSuccessActivity.class);
						//intent.putExtra("need_load_info", false);
						startActivity(intent);
//					}
					myDialog.dismiss();
				}
			});
			myDialog.setView(view);
		}
		
		myDialog.show();
	}
}
	
	
