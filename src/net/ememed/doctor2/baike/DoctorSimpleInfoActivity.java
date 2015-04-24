package net.ememed.doctor2.baike;

import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.BasicActivity;
import net.ememed.doctor2.activity.GroupDetailActivity;
import net.ememed.doctor2.baike.entity.BaikeMemberInfo;
import net.ememed.doctor2.entity.ApplyToGroupEntity;
import net.ememed.doctor2.entity.DoctorDetailInfoData;
import net.ememed.doctor2.entity.DoctorDetailInfoEntity;
import net.ememed.doctor2.entity.DoctorInfoEntry;
import net.ememed.doctor2.entity.GroupDetailEntity;
import net.ememed.doctor2.entity.HospitalInfo;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.Util;

public class DoctorSimpleInfoActivity extends BasicActivity {

	// private BaikeMemberInfo memberInfo;
	private DoctorDetailInfoData doctor_detail;

	private String other_doctor_id;
	private String from;
	private String avatar;
	private String name;
	private String professional;
	private String zhuanye;
	private String room;
	private String specially;
	private HospitalInfo hospital_info;

	private TextView top_title;
	private TextView tv_name;
	private TextView tv_room;
	private TextView tv_professional;
	private TextView tv_shanchang;
	private TextView tv_hospital;
	private TextView tv_hospital_info;
	private TextView tv_zhuanke;
	private ImageView iv_photo;
	private TextView btn_chat;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);

		setContentView(R.layout.doctor_simple_info);
		// memberInfo = (BaikeMemberInfo)
		// getIntent().getSerializableExtra("doctor_info");

		other_doctor_id = getIntent().getStringExtra("other_doctor_id");
		from = getIntent().getStringExtra("from");
		avatar = getIntent().getStringExtra("avatar");
		name = getIntent().getStringExtra("name");
		professional = getIntent().getStringExtra("professional");
		zhuanye = getIntent().getStringExtra("zhuanye");
		room = getIntent().getStringExtra("room");
		specially = getIntent().getStringExtra("specially");
		hospital_info = (HospitalInfo) getIntent().getSerializableExtra("hospital");
	}

	@Override
	protected void setupView() {
		super.setupView();

		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText("医生信息");

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_room = (TextView) findViewById(R.id.tv_room);
		tv_professional = (TextView) findViewById(R.id.tv_professional);
		tv_shanchang = (TextView) findViewById(R.id.tv_shanchang);
		tv_hospital = (TextView) findViewById(R.id.tv_hospital);
		tv_hospital_info = (TextView) findViewById(R.id.tv_hospital_info);
		tv_zhuanke = (TextView) findViewById(R.id.tv_zhuanke);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		btn_chat = (TextView) findViewById(R.id.btn_chat);
		btn_chat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 跳转聊天界面
			}
		});
		

		if (from.equals(OtherBaikeActivity.class.getSimpleName())) {
			getDoctorDetailInfo();
		} else if (from.equals(GroupDetailActivity.class.getSimpleName())) {
			imageLoader.displayImage(avatar, iv_photo, Util.getOptions_avatar());
			tv_name.setText(name);
			tv_professional.setText(professional);
			tv_room.setText(room);
			tv_shanchang.setText(specially);
			tv_hospital.setText(hospital_info.getHOSPITALNAME());
			if ("0".equals(zhuanye)) {
				tv_zhuanke.setText("专科医生");
			} else {
				tv_zhuanke.setText("全科医生");
			}
			tv_hospital_info.setText(Html.fromHtml(hospital_info.getCONTEXT()));
		}
	}

	private void getDoctorDetailInfo() {
		if (NetWorkUtils.detect(this)) {
			loading("null");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("memberid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("doctorid", other_doctor_id);

			MyApplication.volleyHttpClient.postWithParams(HttpUtil.get_doctor_detail_info, DoctorDetailInfoEntity.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = new Message();
							message.obj = response;
							message.what = IResult.GET_DOCTOR_DETAIL_INFO;
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
			destroyDialog();

			switch (msg.what) {
			case IResult.GET_DOCTOR_DETAIL_INFO:
				DoctorDetailInfoEntity entry = (DoctorDetailInfoEntity) msg.obj;
				if (!entry.isSuccess()) {
					showToast(entry.getErrormsg());
					return;
				}

				if (entry.getData() != null) {
					doctor_detail = entry.getData();
					updateView();
				}
				break;

			case IResult.DATA_ERROR:
				showToast("数据异常");
				break;
			case IResult.NET_ERROR:
				showToast("网络异常");
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

	private void updateView() {
		DoctorInfoEntry doctor_info = doctor_detail.getDOCTOR_INFO();
		HospitalInfo hospital = doctor_detail.getHOSPITAL_INFO();
		imageLoader.displayImage(doctor_info.getAVATAR(), iv_photo, Util.getOptions_avatar());
		tv_name.setText(doctor_info.getREALNAME());
		tv_professional.setText(doctor_info.getPROFESSIONAL());
		tv_room.setText(doctor_info.getROOMNAME());
		tv_shanchang.setText(doctor_info.getSPECIALITY());
		tv_hospital.setText(hospital.getHOSPITALNAME());
		if ("0".equals(doctor_info.getALLOWFREECONSULT())) {
			tv_zhuanke.setText("专科医生");
		} else {
			tv_zhuanke.setText("全科医生");
		}
		tv_hospital_info.setText(Html.fromHtml(hospital_info.getCONTEXT()));
	}

	public void doClick(View view) {
		if (R.id.btn_back == view.getId()) {
			finish();
		}
	}
}
