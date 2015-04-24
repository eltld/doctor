package net.ememed.doctor2.activity;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.baike.BaikeHomeActivity;
import net.ememed.doctor2.entity.DoctorSignInEntity;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.YimiHuaHomeData;
import net.ememed.doctor2.entity.YimiHuaHomeEntity;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 薏米花首页
 * @author Administrator
 *
 */
public class YimiScoreActivity extends BasicActivity implements OnClickListener{
	
	private String str_tip1 = "已签到%s天，明天签到可领取%s薏米花";
	private Button btn_exchange;  //薏米花兑换
	private Button btn_sign;	//签到
	private Button btn_share;	//分享
	private Button btn_invite_patient; 
	private Button btn_invite_doctor;
	private LinearLayout btn_yimi_score_query;
	private LinearLayout btn_yimi_score_more;
	
	private TextView tv_score;
	
	public TextView txt_sign_info;
	
	private YimiHuaHomeData homeData;
	private AlertDialog myDialog, myDialog2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yimi_score);
		initView();
	}
	
	private void initView(){
		
		TextView top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("薏米花");
		
		btn_exchange=(Button) findViewById(R.id.btn_yimi_score_exchange);
		btn_exchange.setOnClickListener(this);
		btn_sign=(Button) findViewById(R.id.btn_yimi_score_sign);
		btn_sign.setOnClickListener(this);
		btn_share=(Button) findViewById(R.id.btn_yimi_score_share);
		btn_share.setOnClickListener(this);
		btn_invite_patient=(Button) findViewById(R.id.btn_invite_patient);
		btn_invite_patient.setOnClickListener(this);
		btn_invite_doctor=(Button) findViewById(R.id.btn_invite_doctor);
		btn_invite_doctor.setOnClickListener(this);
		btn_yimi_score_query=(LinearLayout) findViewById(R.id.ll_yimi_score_query);
		btn_yimi_score_query.setOnClickListener(this);
		btn_yimi_score_more=(LinearLayout) findViewById(R.id.ll_yimi_score_more);
		btn_yimi_score_more.setOnClickListener(this);
		
		tv_score = (TextView) findViewById(R.id.yimi_score_quantity);
		
		
		txt_sign_info=(TextView) findViewById(R.id.txt_sign_info);
		txt_sign_info.setText(SharePrefUtil.getString(Conast.SIGN_IN_RECORD));
		
		getYimihuaHomeInfo();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		tv_score.setText(String.valueOf(SharePrefUtil.getInt(Conast.YIMIHUA_TOTAL)));
	}
	
	private void signIn() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.sign_in,
				DoctorSignInEntity.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.SIGN_IN;
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
	 * 获取薏米花首页信息
	 */
	private void getYimihuaHomeInfo() {
		if (!NetWorkUtils.detect(this)) {
			handler.sendEmptyMessage(IResult.NET_ERROR);
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_yimihua_home,
				YimiHuaHomeEntity.class, params, new Response.Listener() {
					@Override
					public void onResponse(Object response) {
						Message message = new Message();
						message.obj = response;
						message.what = IResult.GET_YIMIHUA_HOME;
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
	
	private void onfresh(){
		getYimihuaHomeInfo();
	}
	
	@Override
	protected void onResult(Message msg) {
		try {
			destroyDialog();
			switch (msg.what) {
			case IResult.SIGN_IN:
				DoctorSignInEntity entry = (DoctorSignInEntity) msg.obj;
				if(!entry.isSuccess()){
					showToast(entry.getErrormsg());
					return;
				}
				setPressedUnable();
				break;
			case IResult.GET_YIMIHUA_HOME:
				YimiHuaHomeEntity home = (YimiHuaHomeEntity) msg.obj;
				if(!home.isSuccess()){
					showToast(home.getErrormsg());
					return;
				}
				homeData = home.getData();
				if(homeData != null){
					SharePrefUtil.putInt(Conast.YIMIHUA_TOTAL, homeData.getPoints_now());
					SharePrefUtil.commit();
					updataView();
				}
				break;
			case IResult.NET_ERROR:
				showToast(getString(R.string.net_error));
				break;
			case IResult.DATA_ERROR:
				showToast("数据错误");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	private void updataView(){
		tv_score.setText(String.valueOf(homeData.getPoints_now()));
		if(!TextUtils.isEmpty(homeData.getIs_signed()) && "1".equals(homeData.getIs_signed())){
			setPressedUnable();
		}
		String sign_info=String.format(str_tip1, String.valueOf(homeData.getSign_times()), String.valueOf(homeData.getNext_points()));
		SharePrefUtil.putString(Conast.SIGN_IN_RECORD, sign_info);
		SharePrefUtil.commit();
		txt_sign_info.setText(sign_info);
		SharePrefUtil.putString(Conast.SIGN_IN,sign_info+","+homeData.getIs_signed());
		SharePrefUtil.commit();
	}
	int count=0;
	private void setPressedUnable() {
//		btn_sign.setPressed(true);
		btn_sign.setBackgroundResource(R.drawable.bg_score_go_p);
		btn_sign.setTextColor(getResources().getColor(R.color.grayness));
		btn_sign.setText("已签到");
		btn_sign.setEnabled(false);
		if(count==0){
			onfresh();
		}
		count++;
	}

	/**
	 * 初始化分享说说的提示框
	 */
	private void showShareShuoshuoDialogue() {
		if (null == myDialog) {
			myDialog = new AlertDialog.Builder(this).create();
			View view = LayoutInflater.from(this).inflate(R.layout.share_shuoshuo_alert_dialog, null);

			 android.view.ViewGroup.LayoutParams lps = view.getLayoutParams();

			if (lps == null) {
				lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			lps.width = MyApplication.getInstance().canvasWidth * 3 / 5;
			lps.height = LayoutParams.WRAP_CONTENT;
			view.setLayoutParams(lps);

			myDialog.setCanceledOnTouchOutside(true);
			Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);

			btn_confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivity(new Intent(YimiScoreActivity.this,BaikeHomeActivity.class));
					myDialog.dismiss();
				}
			});
			myDialog.setView(view);
		}

		myDialog.show();
	}
	
	/**
	 * 显示邀请患者下拉框
	 */
	private void showInvitePatientDialogue(){
		if(null == myDialog2){
			myDialog2 = new AlertDialog.Builder(this).create();
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_cannot_private_patient, null);
			android.view.ViewGroup.LayoutParams lps = view.getLayoutParams();
			
			if(lps == null){
				lps = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			lps.height = LayoutParams.WRAP_CONTENT;
			lps.width = LayoutParams.WRAP_CONTENT;
			view.setLayoutParams(lps);
			
			myDialog2.setCanceledOnTouchOutside(true);
			Button btn = (Button) view.findViewById(R.id.button1);
			btn.setOnClickListener(new OnClickListener() {
		
				@Override
				public void onClick(View v) {
					//直接跳到完善资料的部分
					Intent intent = new Intent(YimiScoreActivity.this, RegisterSuccessActivity.class);
					startActivity(intent);
					myDialog2.dismiss();
				}
			});
			myDialog2.setView(view);
		}
		
		myDialog2.show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_yimi_score_exchange:   //积分兑换
			startActivity(new Intent(this,ScoreExchangeActivity.class));
			break;
		case R.id.btn_yimi_score_sign:    //签到
			signIn();
			break;
		case R.id.btn_yimi_score_share:     //分享
			showShareShuoshuoDialogue();
			break;
		case R.id.btn_invite_patient:   //邀请患者
			if(false == SharePrefUtil.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID))){
				if(true == SharePrefUtil.getBoolean(Conast.VALIDATED)){
					startActivity(new Intent(this, PersonInfoActivity.class));
				} else {
					if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
						if("0".equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
							showInvitePatientDialogue();
						} else {
							startActivity(new Intent(this, RegisterSuccessActivity.class));
						}
					}
				}
			} else {
				startActivity(new Intent(this, BusinessCardActivity.class));
			}
			break;
		case R.id.btn_invite_doctor:   //邀请同行
			startActivity(new Intent(this,InviteActivity.class));
			break;
		case R.id.ll_yimi_score_query:   //查询明细
			ExchangeRecordActivity.startAction(this, 0);
			break;
		case R.id.ll_yimi_score_more:   //获取更多积分
			startActivity(new Intent(this,EarnMorePointActivity.class));
			break;

		default:
			break;
		}
	}
}
