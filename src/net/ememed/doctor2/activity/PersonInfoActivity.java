package net.ememed.doctor2.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.UploadImgSuccessEvent;
import eu.janmuller.android.simplecropimage.CropImage;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.UploadPaperActivity.PhotoType;
import net.ememed.doctor2.db.PositionConfigTable;
import net.ememed.doctor2.entity.DoctorInfo;
import net.ememed.doctor2.entity.DoctorInfoEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.PersonInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.FileUtil;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PictureUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.CircleImageView;
import net.ememed.doctor2.widget.MenuDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PersonInfoActivity extends BasicActivity implements BasicUIEvent,
		OnRefreshListener {

	private TextView top_title;
	private int sex;		
	private int professional_attribute;	//专业属性1：全科，2：专科
	private int position;	//职称
	
	private TextView tv_realsex;
	private TextView tv_realname;
	private TextView tv_realcall;
	private TextView tv_realemail;
	private TextView tv_realcert_number;
	private TextView tv_hospital;
	private TextView tv_hospital_laboratory;
	private TextView tv_realskilled;
	private TextView tv_resume;
	private TextView tv_laboratory_phone;
	private CircleImageView doctor_img;
	
	private LinearLayout ll_realcert_number;
	private LinearLayout ll_realcert_positive;
	private LinearLayout ll_realcert_opposite;
	private LinearLayout ll_licence_positive;
	private LinearLayout ll_licence_opposite;
	private LinearLayout ll_breast_plate;
	private LinearLayout ll_work_permit;
	
	private View line_realcert_number;
	private View line_realcert_positive;
	private View line_realcert_opposite;
	private View line_licence_positive;
	private View line_licence_opposite;
	private View line_breast_plate;
	private View line_work_permit;

	private Uri uri;
	private static final int REQUEST_SINGLE_SERVICE = 1;
	private final static int PICTURE_REQUEST = 2;
	private final static int CAMERA_REQUEST = 3;
	private static final int REQUEST_CODE_CROP_IMAGE = 4;
	private static final int REQUEST_CODE_GALLERY = 5;
	private static final int REQUEST_CODE_TAKE_PICTURE = 6;
	
	private static final int REQUEST_UPLOAD_PHOTO_POSITIVE = 7;		//上传执业证书首页
	private static final int REQUEST_UPLOAD_PHOTO_OPPOSITE = 8;		//上传执业证书次页
	private static final int REQUEST_UPLOAD_PHOTO_BREAST_PLATE = 9;	//上传胸牌照片
	private static final int REQUEST_UPLOAD_PHOTO_WORK_PERMIT = 10;		//上传工作证
	private static final int REQUEST_UPLOAD_PHOTO_LICENCE_POSITIVE = 11;	//上传医师资格证首页
	private static final int REQUEST_UPLOAD_PHOTO_LICENCE_OPPOSITE = 12;	//上传医师资格证次页
	
	private Bitmap bm_avatar;
	private byte[] bytes_avatar;
	private PullToRefreshLayout mPullToRefreshLayout;
	private TextView tv_position;
	private TextView tv_spec;
	private ImageView photo_cert_positive;
	private ImageView photo_cert_opposite;
	private ImageView photo_licence_positive;
	private ImageView photo_licence_opposite;
	private ImageView photo_breast_plate;
	private ImageView photo_work_permit;
	
	private LinearLayout layout_verifing; 			//正在审核界面
	private LinearLayout layout_verify_failed; 		//审核失败界面
	private LinearLayout layout_verify_success; 	//审核成功界面
	
	private LinearLayout linear_layout1;	//灰色半透明覆盖部分
	private LinearLayout linear_layout2;	//灰色半透明覆盖部分
	private LinearLayout linear_layout3;	//灰色半透明覆盖部分
	private LinearLayout linear_layout4;	//灰色半透明覆盖部分
	
	private TextView tv_failed_reason;		//审核失败的原因
	
	private DoctorInfoEntry infoEntry = null;
	
	private PhotoType mPhotoType = PhotoType.CER_PHOTO;

	private String state_enter = null;		//刚进入界面时本地获取到的资料审核状态
	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.doctor_person_info);
		EventBus.getDefault().registerSticky(this, UploadImgSuccessEvent.class);
		state_enter = SharePrefUtil.getString(Conast.AUDIT_STATUS);
		tv_failed_reason = (TextView) findViewById(R.id.tv_failed_reason);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EventBus.getDefault().removeStickyEvent(UploadImgSuccessEvent.class);
	}

	@Override
	protected void setupView() {
		super.setupView();
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(this).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		top_title = (TextView) findViewById(R.id.top_title);
		top_title.setText(getString(R.string.person_info));
		tv_realsex = (TextView) findViewById(R.id.tv_realsex);
		tv_realname = (TextView) findViewById(R.id.tv_realname);
		tv_realcall = (TextView) findViewById(R.id.tv_realcall);
		tv_realemail = (TextView) findViewById(R.id.tv_realemail);
		tv_realcert_number = (TextView) findViewById(R.id.tv_realcert_number);
		tv_hospital = (TextView) findViewById(R.id.tv_hospital);
		tv_hospital_laboratory = (TextView) findViewById(R.id.tv_hospital_laboratory);
		tv_realskilled = (TextView) findViewById(R.id.tv_realskilled);
		tv_resume = (TextView) findViewById(R.id.tv_doctor_resume);
		tv_laboratory_phone = (TextView) findViewById(R.id.tv_laboratory_phone);
		
		ll_realcert_number = (LinearLayout) findViewById(R.id.lt_realcert_number);
		ll_realcert_positive = (LinearLayout) findViewById(R.id.lt_realcert_positive);
		ll_realcert_opposite = (LinearLayout) findViewById(R.id.lt_realcert_opposite);
		ll_licence_positive = (LinearLayout) findViewById(R.id.lt_medical_licence_positive);
		ll_licence_opposite = (LinearLayout) findViewById(R.id.lt_medical_licence_opposite);
		ll_breast_plate = (LinearLayout) findViewById(R.id.lt_breast_plate);
		ll_work_permit = (LinearLayout) findViewById(R.id.lt_work_permit);
		
		line_realcert_number = (View) findViewById(R.id.line_realcert_number);
		line_realcert_positive = (View) findViewById(R.id.line_realcert_positive);
		line_realcert_opposite = (View) findViewById(R.id.line_realcert_opposite);
		line_licence_positive = (View) findViewById(R.id.line_medical_licence_positive);
		line_licence_opposite = (View) findViewById(R.id.line_medical_licence_opposite);
		line_breast_plate = (View) findViewById(R.id.line_breast_plate);
		line_work_permit = (View) findViewById(R.id.line_work_permit);

		doctor_img = (CircleImageView) findViewById(R.id.iv_getuser_AVATAR);
		photo_cert_positive = (ImageView) findViewById(R.id.cert_positive);
		photo_cert_opposite = (ImageView) findViewById(R.id.cert_opposite);
		photo_licence_positive = (ImageView) findViewById(R.id.licence_positive);
		photo_licence_opposite = (ImageView) findViewById(R.id.licence_opposite);
		photo_breast_plate = (ImageView) findViewById(R.id.pic_breast_plate);
		photo_work_permit = (ImageView) findViewById(R.id.pic_work_permit);
		
		layout_verifing = (LinearLayout) findViewById(R.id.layout_verifing);
		layout_verify_failed = (LinearLayout) findViewById(R.id.layout_verify_failed);
		layout_verify_success = (LinearLayout) findViewById(R.id.layout_verify_success);
		
		linear_layout1 = (LinearLayout) findViewById(R.id.linear_layout1);
		linear_layout2 = (LinearLayout) findViewById(R.id.linear_layout2);
		linear_layout3 = (LinearLayout) findViewById(R.id.linear_layout3);
		linear_layout4 = (LinearLayout) findViewById(R.id.linear_layout4);

		tv_position = (TextView) findViewById(R.id.tv_position);
		tv_spec = (TextView) findViewById(R.id.tv_spec);

		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.EMAIL_STR))) {
			tv_realemail.setText(SharePrefUtil.getString(Conast.EMAIL_STR));
		}
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.SPECIALITY))) {
			tv_realskilled.setText(SharePrefUtil.getString(Conast.SPECIALITY));
		}
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.RESUME))) {
			tv_resume.setText(SharePrefUtil.getString(Conast.RESUME));
			
			System.out.println("SharePrefUtil.getString(Conast.RESUME) = "+SharePrefUtil.getString(Conast.RESUME));
		}
		if (!TextUtils.isEmpty(SharePrefUtil.getString(Conast.Doctor_Type))) {
			if ("1".equals(SharePrefUtil.getString(Conast.Doctor_Type))) {
				tv_spec.setText(getString(R.string.doctor_type_quanke));
			} else {
				tv_spec.setText(getString(R.string.doctor_type_zhuanke));
			}
		}
		if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.ROOM_PHONE))) {	//科室电话
			tv_laboratory_phone.setText(SharePrefUtil.getString(Conast.ROOM_PHONE));
		}
		
		setVerifyStateView();
	}
	
	private void loadPersonInfo() {
		
	}
	
	private void setVerifyStateView() {
		layout_verifing.setVisibility(View.GONE); 
		layout_verify_failed.setVisibility(View.GONE); 
		layout_verify_success.setVisibility(View.GONE); 
		
		if(!TextUtils.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
			if(false == SharePrefUtil.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID)) 
					&& state_enter.equals("1")){
				layout_verify_success.setVisibility(View.VISIBLE);
				mPullToRefreshLayout.setVisibility(View.GONE);
				SharePrefUtil.putBoolean(Conast.VALIDATED, true);
				SharePrefUtil.putBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID), true);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(5000);
							Message message = new Message();
							message.what = IResult.CLEAR_FRONT_VIEW;
							handler.sendMessage(message);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				
			} else if(SharePrefUtil.getString(Conast.AUDIT_STATUS).equals("2")){
				layout_verifing.setVisibility(View.VISIBLE);
				mPullToRefreshLayout.setVisibility(View.GONE);
			} else if(SharePrefUtil.getString(Conast.AUDIT_STATUS).equals("3")){
				layout_verify_failed.setVisibility(View.VISIBLE);
				mPullToRefreshLayout.setVisibility(View.GONE);
				String audit_refuse =  SharePrefUtil.getString(Conast.AUDIT_REFUSE);
				if(audit_refuse!=null){
					tv_failed_reason.setText(audit_refuse);
				}
			}
			SharePrefUtil.commit();
		}
	}


	@Override
	protected void getData() {
		requestPersonInfo();
		super.getData();
	}

	private void requestPersonInfo() {
		if (NetWorkUtils.detect(PersonInfoActivity.this)) {
			loading(null);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.get_doctor_info, DoctorInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
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
	
	@Override
	public void onRefreshStarted(View view) {
		mPullToRefreshLayout.setRefreshComplete();
		requestPersonInfo();
	}

	public void doClick(View view) {
		Intent intent = null;
		if (view.getId() == R.id.btn_back) {
			finish();
			return;
		}else if (view.getId() == R.id.lt_user_img) {			// 头像
			mPhotoType = PhotoType.AVATAR_IMAGE;
			//setDoctorImage();
			doPickPhotoAction();
			return;
		}else if (view.getId() == R.id.lt_realsex) {				// 性别
			 setSex();
			 return;
		}else if (view.getId() == R.id.lt_realcall) { //修改手机号
			startActivity(new Intent(this, AlertMobileActivity.class));
		}else if (view.getId() == R.id.lt_realemail) {			// Email
			 intent = new Intent(this, SaveEmailActivity.class);
			 intent.putExtra("exist_str", tv_realemail.getText().toString());
			 intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if (view.getId() == R.id.lt_realcert_number) {	// 证书编号
			 intent = new Intent(this, SaveCertNumberActivity.class);
			 intent.putExtra("exist_str", tv_realcert_number.getText().toString());
			 intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if (view.getId() == R.id.lt_hospital) {			// 就职医院
			 intent = new Intent(this, SaveHospitalActivity.class);
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if (view.getId() == R.id.lt_hospital_laboratory) {// 就职科室
			/* intent = new Intent(this, SaveHospitalLabActivity.class);
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);*/
			 intent = new Intent(PersonInfoActivity.this,RoomsListActivity.class);
				intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
				startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if (view.getId() == R.id.lt_realskilled) {		// 擅长
			 intent = new Intent(this, SaveSkilledActivity.class);
			 intent.putExtra("exist_str", tv_realskilled.getText().toString());
			 intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if(view.getId() == R.id.lt_doctoe_resume) {		//个人详情
			 intent = new Intent(this, SaveResumeActivity.class);
			 intent.putExtra("exist_str", tv_resume.getText().toString());
			 intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if(view.getId() == R.id.lt_laboratory_phone){	//科室电话
			 intent = new Intent(this, SaveRoomPhoneActivity.class);
			 intent.putExtra("exist_str", tv_laboratory_phone.getText().toString());
			 intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			 startActivityForResult(intent, REQUEST_SINGLE_SERVICE);
		 }else if(view.getId() == R.id.lt_prof_attributes){		//专业属性（全科/专科）
			 setProfessionalAttributes();
		 }else if(view.getId() == R.id.lt_realposition){		//职称
			 setPosition();
		 }else if(view.getId() == R.id.lt_realcert_positive){	//执业证书首页
			mPhotoType = PhotoType.CER_PHOTO;
			doPickPhotoAction();
		 }else if (view.getId()== R.id.lt_realcert_opposite) {		//执业证书背面
			mPhotoType = PhotoType.CER_PHOTO_OPPOSITE;
			doPickPhotoAction();
		 } else if (view.getId() == R.id.lt_medical_licence_positive) {//医师资格证正面
			mPhotoType = PhotoType.LICENCE_PHOTO_POSITIVE;
			doPickPhotoAction();
		 } else if (view.getId() == R.id.lt_medical_licence_opposite) {	//医师资格证背面
			mPhotoType = PhotoType.LICENCE_PHOTO_OPPOSITE;
			doPickPhotoAction();
		} else if (view.getId() == R.id.lt_breast_plate) { 		//胸牌
			mPhotoType = PhotoType.BREAST_PLATE_PHOTO;
			doPickPhotoAction();
		}  else if (view.getId() == R.id.lt_work_permit) { 		//工作证
			mPhotoType = PhotoType.CER_PHOTO_OPPOSITE;
			doPickPhotoAction();
		}  else if (view.getId() == R.id.cert_positive) {		//执业证书正面照片
			dealPhotoClick(photoBytes, PhotoType.CER_PHOTO, REQUEST_UPLOAD_PHOTO_POSITIVE);
		} else if (view.getId() == R.id.cert_opposite) {		//执业证书反面照片
			dealPhotoClick(photoBytes_opposite, PhotoType.CER_PHOTO_OPPOSITE, REQUEST_UPLOAD_PHOTO_OPPOSITE);
		} else if (view.getId() == R.id.licence_positive) {		//医师资格证正面照片
			dealPhotoClick(licencePhotoBytes, PhotoType.LICENCE_PHOTO_POSITIVE, REQUEST_UPLOAD_PHOTO_LICENCE_POSITIVE);
		} else if (view.getId() == R.id.licence_opposite) {		//医师资格证反面照片
			dealPhotoClick(licencePhotoBytes_opposite, PhotoType.LICENCE_PHOTO_OPPOSITE, REQUEST_UPLOAD_PHOTO_LICENCE_OPPOSITE);
		} else if (view.getId() == R.id.pic_breast_plate) {	//胸牌照片
			dealPhotoClick(breastPlatePhotoByte, PhotoType.BREAST_PLATE_PHOTO, REQUEST_UPLOAD_PHOTO_BREAST_PLATE);
		} else if (view.getId() == R.id.pic_work_permit) {	//工作证照片
			dealPhotoClick(workPermitPhotoByte, PhotoType.WORK_PERMIT_PHOTO, REQUEST_UPLOAD_PHOTO_WORK_PERMIT);
		} else if (view.getId() == R.id.iv_getuser_AVATAR){	//头像照片
			/*if (!TextUtils.isEmpty(infoEntry.getAVATAR_AUDITSTATUS())){	//头像
				//XXX需添加功能：如果刚提交的照片，只需显示本地图片，无需从网络加载
				//头像通过审核，则显示正式头像
				if("1".equals(infoEntry.getAVATAR_AUDITSTATUS())){
					if (!TextUtils.isEmpty(infoEntry.getAVATAR())) {
						String remotepath = infoEntry.getAVATAR();
						intent = new Intent(this,ShowBigImage.class);
						intent.putExtra("remotepath", remotepath);
						startActivity(intent);
					}
				} else { //头像未通过审核，则显示待审核的头像
					if (!TextUtils.isEmpty(infoEntry.getAVATAR_UNAUDIT())) {
						String remotepath = infoEntry.getAVATAR_UNAUDIT();
						intent = new Intent(this,ShowBigImage.class);
						intent.putExtra("remotepath", remotepath);
						startActivity(intent);
					}
				}
			}
			*/
			if (TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))) {
				if(TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR_UNAUDIT))){
					mPhotoType = PhotoType.AVATAR_IMAGE;
					//setDoctorImage();
					doPickPhotoAction();
				}
				else{
					intent = new Intent(this,ShowBigImage.class);
					intent.putExtra("uri", photoUri);
					intent.putExtra("remotepath", SharePrefUtil.getString(Conast.AVATAR_UNAUDIT));
					startActivity(intent);
				}
			}else {
				intent = new Intent(this,ShowBigImage.class);
				intent.putExtra("uri", photoUri);
				intent.putExtra("remotepath", SharePrefUtil.getString(Conast.AVATAR));
				startActivity(intent);
			}
		}
		
		//正在审核页面的两个按钮
		else if (view.getId() == R.id.btn_add_information_1){
			intent = new Intent(PersonInfoActivity.this, RegisterSuccessActivity.class);
			intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			startActivity(intent);
		} else if(view.getId() == R.id.btn_close_1){
			finish();
		}
		
		//审核失败界面的一个按钮
		else if (view.getId() == R.id.btn_add_information_2){
			layout_verify_failed.setVisibility(View.GONE);
			intent = new Intent(PersonInfoActivity.this, RegisterSuccessActivity.class);
			intent.putExtra("from", PersonInfoActivity.class.getSimpleName());
			startActivity(intent);
		}
		//审核成功界面的两个按钮
		else if(view.getId() == R.id.btn_invite_patient_3){
			startActivity(new Intent(PersonInfoActivity.this, BusinessCardActivity.class));
			finish();
		} else if (view.getId() == R.id.btn_info_preview_3){
			layout_verify_success.setVisibility(View.GONE);
			mPullToRefreshLayout.setVisibility(View.VISIBLE);
		}
		//灰色半透明背景
		else if(view.getId() == R.id.linear_layout4){
			clearFrontView();
		}
	}
	
	/**
	 * 去除前景（灰色覆盖层）
	 */
	private void clearFrontView(){
		linear_layout1.setVisibility(View.INVISIBLE);
		linear_layout2.setVisibility(View.INVISIBLE);
		linear_layout3.setVisibility(View.INVISIBLE);
		linear_layout4.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置职称
	 */
	private String tempPosition;
	private void setPosition() {
		PositionConfigTable positionTable = new PositionConfigTable();
		 final String[] items = positionTable.getAllPositionNames();
		 for(int i= 0; i < items.length; i++){
			 if(tv_position.getText().toString().trim().equals(items[i])){
				 position = i;
			 }
		 }
		 
		 Builder builder = new Builder(this);
		 builder.setTitle(getString(R.string.getuser_set_position))
			.setSingleChoiceItems(items, position, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!tv_position.getText().toString().trim().equals(items[which].toString().trim())){
							tv_position.setText(items[which]);
							position = which;
							tempPosition = items[which];
							setDoctorPosition(items[which]);
						}
						dialog.cancel();
					}
				}).show();
	}
	
	/**
	 * 向服务器提交医生职称(主任医师，副主任医师。。。)
	 * @param position
	 */
	private void setDoctorPosition(String position){
		if (NetWorkUtils.detect(PersonInfoActivity.this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("professional", position);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_doctor_professional, PersonInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
						
							SharePrefUtil.putString(Conast.Doctor_Professional, tempPosition);
							SharePrefUtil.commit();	
							
							Message message = handler.obtainMessage();
							message.obj = response;
							message.what = IResult.SET_DOCTOR_PROFESSIONAL;
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
	
	// 设置性别
		private void setSex() {
			final String[] items = new String[] {getString(R.string.add_userseek_girl), getString(R.string.add_userseek_boy) };
			Builder builder = new Builder(this);
			sex = "男".equals(tv_realsex.getText())?1:0;
			builder.setTitle(getString(R.string.getuser_setsex))
			.setSingleChoiceItems(items, sex, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!tv_realsex.getText().toString().trim().equals(items[which].toString().trim())){
							tv_realsex.setText(items[which]);
							sex = which;
							setDoctorSex(which);
						}
						dialog.cancel();
					}
				}).show();
		}
	
	private void setDoctorSex(int sex){
		if (NetWorkUtils.detect(PersonInfoActivity.this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("sex", ""+sex);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_doctor_sex, PersonInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = handler.obtainMessage();
							message.obj = response;
							message.what = IResult.SET_DOCTOR_SEX;
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

	// 设置专业属性
	private void setProfessionalAttributes() {
		final String[] items = new String[] {getString(R.string.doctor_type_zhuanke), getString(R.string.doctor_type_quanke) };
		Builder builder = new Builder(this);
		professional_attribute = "全科医生".equals(tv_spec.getText().toString().trim())?1:0;
		builder.setTitle(getString(R.string.getuser_set_attri))
			.setSingleChoiceItems(items, professional_attribute, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!tv_spec.getText().toString().trim().equals(items[which].toString().trim())){
							tv_spec.setText(items[which]);
							professional_attribute = which;
							setDoctorProfessionalAttribute(which);
						}
						dialog.cancel();
					}
				}).show();
	}
	
	//向服务器提交专业属性(全科，专科)
	private void setDoctorProfessionalAttribute(final int attr){
		if (NetWorkUtils.detect(PersonInfoActivity.this)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
			params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
			params.put("allowfreeconsult", ""+attr);
			MyApplication.volleyHttpClient.postWithParams(
					HttpUtil.set_doctor_allowfreeconsult, PersonInfo.class, params,
					new Response.Listener() {
						@Override
						public void onResponse(Object response) {
							Message message = handler.obtainMessage();
							message.obj = response;
							message.what = IResult.SET_DOCTOR_ALLOW_FREE_CONSULT;
							handler.sendMessage(message);
							
							SharePrefUtil.putString(Conast.Doctor_Type, ""+attr);
							SharePrefUtil.commit();
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
	
/*	// 设置头像
	private void setDoctorImage() {
		Builder builder = new Builder(this);
		String[] items = new String[] { getString(R.string.apply_hosp_xiangce),
				getString(R.string.apply_hosp_photo) };
		builder.setTitle(getString(R.string.apply_hosp_choose))
				.setSingleChoiceItems(items, -1, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						switch (which) {
						case 0:
							intent = new Intent();
							 开启Pictures画面Type设定为image 
							intent.setType("image/*");
							 使用Intent.ACTION_GET_CONTENT这个Action 
							intent.setAction(Intent.ACTION_GET_CONTENT);
							 取得相片后返回本画面 
							startActivityForResult(intent, PICTURE_REQUEST);
							break;

						case 1:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							File dir = new File(FileUtil.PATH);
							if (!dir.exists()) {
								dir.mkdirs();
							}
							File photo = new File(dir, System
									.currentTimeMillis() + ".jpg");
							uri = Uri.fromFile(photo);
							intent.putExtra("aspectX", 1);
							intent.putExtra("aspectY", 1);
							// outputX outputY 是裁剪图片宽高
							intent.putExtra("outputX", 64);
							intent.putExtra("outputY", 64);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
							startActivityForResult(intent, CAMERA_REQUEST);
							break;
						}
						dialog.dismiss();
					}
				}).show();
	}
	*/
	// 设置头像
		/*private void setDoctorImage() {
			final Context dialogContext = new ContextThemeWrapper(this,
					android.R.style.Theme_Light);
			String[] choices;
			choices = new String[2];
			choices[0] = getString(R.string.apply_hosp_photo); // 拍照
			choices[1] = getString(R.string.apply_hosp_xiangce); // 从相册中选择
			
			MenuDialog.Builder builder = new MenuDialog.Builder(dialogContext);
			MenuDialog dialog = builder.setTitle(R.string.choose_photo_title).setItems(choices, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					switch (which) {
					case 0:
						String status = Environment
								.getExternalStorageState();
						if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
							takePicture();
						}
						break;
					case 1:
						openGallery();
						break;
					}
				}
			}).create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		}*/
		
		/**4.0*/
	    private void takePicture() {

	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	        try {
	        	
	        	File uploadFileDir = new File(FileUtil.PATH,FileUtil.IMG_UPLOAD);
				if (!uploadFileDir.exists()) {
					uploadFileDir.mkdirs();
				}
	        	
	         	String state = Environment.getExternalStorageState();
	        	if (Environment.MEDIA_MOUNTED.equals(state)) {
	        		picFile = new File(uploadFileDir,UUID.randomUUID().toString()+".jpeg");
	        	} else {
	        	}
	        	
	        	photoUri = Uri.fromFile(picFile);
	            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
	            intent.putExtra("return-data", true);
	            
	            if(mPhotoType == PhotoType.AVATAR_IMAGE){
	            	startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);		//头像需要剪切图片
	            } else{
	            	startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);		//其他照片不需要剪切
	            }
	        } catch (ActivityNotFoundException e) {
//	            Log.d(TAG, "cannot take picture", e);
	        }
	    }
	    
	    /**for4.3*/
		private void openGallery() {
			try {
				File pictureFileDir = new File(FileUtil.PATH, FileUtil.IMG_UPLOAD);
				if (!pictureFileDir.exists()) {
					pictureFileDir.mkdirs();
				}
				picFile = new File(pictureFileDir, UUID.randomUUID().toString()+".jpeg");
				if (!picFile.exists()) {
					picFile.createNewFile();
				}
				photoUri = Uri.fromFile(picFile);
		        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		        photoPickerIntent.setType("image/*");
		        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	
	private byte[] photoBytes = null;
	private byte[] photoBytes_opposite = null;
	private byte[] licencePhotoBytes = null;
	private byte[] licencePhotoBytes_opposite = null;
	private byte[] breastPlatePhotoByte = null;
	private byte[] workPermitPhotoByte = null;
	private File picFile;
	private Uri photoUri;
	
	public void onEvent(UploadImgSuccessEvent testEvent){
	    if (null != testEvent) {
	    	if (testEvent.getPhotoType() == PhotoType.CER_PHOTO) {
	    		photoBytes = testEvent.getBytes();
	    		photo_cert_positive.setImageBitmap(BitmapUtil.bytes2Bimap(photoBytes));		
			} else if (testEvent.getPhotoType() == PhotoType.CER_PHOTO_OPPOSITE){
				photoBytes_opposite = testEvent.getBytes();
				photo_cert_opposite.setImageBitmap(BitmapUtil.bytes2Bimap(photoBytes_opposite));	
			} else if (testEvent.getPhotoType() == PhotoType.LICENCE_PHOTO_POSITIVE) {
	    		licencePhotoBytes = testEvent.getBytes();
	    		photo_licence_positive.setImageBitmap(BitmapUtil.bytes2Bimap(licencePhotoBytes));		
			} else if (testEvent.getPhotoType() == PhotoType.LICENCE_PHOTO_OPPOSITE){
				licencePhotoBytes_opposite = testEvent.getBytes();
				photo_licence_opposite.setImageBitmap(BitmapUtil.bytes2Bimap(licencePhotoBytes_opposite));	
			} else if (testEvent.getPhotoType() == PhotoType.BREAST_PLATE_PHOTO){
				breastPlatePhotoByte = testEvent.getBytes();
				photo_breast_plate.setImageBitmap(BitmapUtil.bytes2Bimap(breastPlatePhotoByte));	
			} else if (testEvent.getPhotoType() == PhotoType.WORK_PERMIT_PHOTO){
				workPermitPhotoByte = testEvent.getBytes();
				photo_work_permit.setImageBitmap(BitmapUtil.bytes2Bimap(workPermitPhotoByte));	
			}
	    }
	}

	private void startCropImage() {
		if (picFile != null) {
			Intent intent = new Intent(this, CropImage.class);
			intent.putExtra(CropImage.IMAGE_PATH, picFile.getPath());
			intent.putExtra(CropImage.SCALE, true);
			intent.putExtra("outputX", 1000);
			intent.putExtra("outputY", 1000);
			intent.putExtra(CropImage.ASPECT_X, 1);
			intent.putExtra(CropImage.ASPECT_Y, 1);
			startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == REQUEST_SINGLE_SERVICE) {
				if (resultCode == RESULT_OK) {
					String setting_type = data.getStringExtra("setting_type");
					if (setting_type.equals(SaveNameActivity.class
							.getSimpleName())) {
						tv_realname.setText(data.getStringExtra("nameStr"));
					} else if (setting_type.equals(SaveCallActivity.class
							.getSimpleName())) {
						tv_realcall.setText(data.getStringExtra("callStr"));
					} else if (setting_type.equals(SaveEmailActivity.class
							.getSimpleName())) {
						tv_realemail.setText(SharePrefUtil
								.getString(Conast.EMAIL_STR));
					} else if (setting_type.equals(SaveCertNumberActivity.class
							.getSimpleName())) {
						tv_realcert_number.setText(data
								.getStringExtra("certStr"));
					} else if (setting_type.equals(SaveHospitalActivity.class
							.getSimpleName())) {
						tv_hospital.setText(data
								.getStringExtra("hospitalnameStr"));
					}/* else if (setting_type.equals(SaveHospitalLabActivity.class.getSimpleName())) {
						tv_hospital_laboratory.setText(data
								.getStringExtra("roomnameStr"));
					}*/ else if (setting_type.equals(SaveSkilledActivity.class
							.getSimpleName())) {
						tv_realskilled.setText(SharePrefUtil
								.getString(Conast.SPECIALITY));
					} else if (setting_type.equals(SaveResumeActivity.class
							.getSimpleName())) {
						tv_resume.setText(SharePrefUtil
								.getString(Conast.RESUME));
					} else if (setting_type.equals(SaveRoomPhoneActivity.class
							.getSimpleName())) {
						tv_laboratory_phone.setText(SharePrefUtil
								.getString(Conast.ROOM_PHONE));
					} else if (setting_type.equals(RoomsListActivity.class.getSimpleName())){
						tv_hospital_laboratory.setText(data.getStringExtra("roomname"));
					}
				}
			} else if (requestCode == PICTURE_REQUEST) {
				if (resultCode == RESULT_OK && null != data) {
					uri = data.getData();
					if (uri == null) {
						Toast.makeText(this, "图片拍摄失败，请重新拍摄", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					bm_avatar = BitmapUtil.getBitmapByUri(this, uri);
					//doctor_img.setImageBitmap(bm_avatar);
					bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);
					/*if (bytes_avatar != null) {
						setImage(bytes);
					}*/
					
					if (bytes_avatar != null) {
						UICore.eventTask(PersonInfoActivity.this, PersonInfoActivity.this, IResult.UPLOAD_PIC, null, bytes_avatar);
						
					}
				}
			} else if (requestCode == CAMERA_REQUEST) {
				if(resultCode == RESULT_OK){
					
					bm_avatar = BitmapUtil.getBitmapByUri(this, uri);
					bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);
					
					/*if (bytes != null) {
						setImage(bytes);
					}*/
					
					if (bytes_avatar != null) {
						UICore.eventTask(PersonInfoActivity.this, PersonInfoActivity.this, IResult.UPLOAD_PIC, null, bytes_avatar);
					}
				}
			} else if(requestCode == REQUEST_CODE_TAKE_PICTURE){
				if (resultCode == RESULT_OK) {
					  startCropImage();
				}
			} else if(requestCode == REQUEST_CODE_CROP_IMAGE){
				if(PhotoType.AVATAR_IMAGE == mPhotoType){
					if (data == null) {
						return;
					}
		            final String path = data.getStringExtra(CropImage.IMAGE_PATH);
		            if (path == null) {
		                return;
		            }
					try {
						if (resultCode == RESULT_OK) {
							if (photoUri != null) {
								bm_avatar = BitmapUtil.getBitmapByUri(this, photoUri);
								bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);
								doctor_img.setImageBitmap(bm_avatar);
								if (bytes_avatar != null) {
									UICore.eventTask(PersonInfoActivity.this, PersonInfoActivity.this, IResult.UPLOAD_PIC, null, bytes_avatar);
								}
							}
						} else {
							if (null!= picFile && picFile.exists()) {
								picFile.delete();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						if (resultCode == RESULT_OK) {
							if (photoUri != null) {
								Intent intent = new Intent(PersonInfoActivity.this, VerifyPaperActivity.class);
								PictureUtil.save(picFile.getAbsolutePath(), picFile, PersonInfoActivity.this);
								byte[] imgDatas = FileUtil.getBytes(picFile);
								intent.putExtra("img_data", imgDatas);
								intent.putExtra("take_photo_type", mPhotoType);
								startActivity(intent);
							}
						} else {
							if (null != picFile && picFile.exists()) {
								picFile.delete();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if(requestCode == REQUEST_CODE_GALLERY){
				if(PhotoType.AVATAR_IMAGE == mPhotoType){
					try {
		                  InputStream inputStream = getContentResolver().openInputStream(data.getData());
		                  FileOutputStream fileOutputStream = new FileOutputStream(picFile);
		                  BitmapUtil.copyStream(inputStream, fileOutputStream);
		                  fileOutputStream.close();
		                  inputStream.close();
		                  startCropImage();
		              } catch (Exception e) {
		            	  e.printStackTrace();
		              }
				}else {
					try {
						InputStream inputStream = getContentResolver().openInputStream(
								data.getData());
						FileOutputStream fileOutputStream = new FileOutputStream(
								picFile);
						BitmapUtil.copyStream(inputStream, fileOutputStream);
						fileOutputStream.close();
						inputStream.close();
						Intent intent = new Intent(PersonInfoActivity.this,
								VerifyPaperActivity.class);
						PictureUtil.save(picFile.getAbsolutePath(), picFile, PersonInfoActivity.this);
						byte[] imgDatas = FileUtil.getBytes(picFile);
						intent.putExtra("img_data", imgDatas);
						intent.putExtra("take_photo_type", mPhotoType);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(int mes, Object obj) {
		if(mes == IResult.UPLOAD_PIC){
			//destroyDialog();
			byte[] bytes = (byte[]) obj;
			setImage(bytes);
		} 
	}

	// 设置图像
	private void setImage(final byte[] bytes) {
		new Thread() {
			public void run() {
				try {
					ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("doctorid", SharePrefUtil
							.getString(Conast.Doctor_ID)));
					params.add(new BasicNameValuePair("ext", "jpg"));
					params.add(new BasicNameValuePair("dir", "1"));
					params.add(new BasicNameValuePair("token", SharePrefUtil
							.getString(Conast.ACCESS_TOKEN)));
//					System.out.println("params" + params);
					String json = HttpUtil.uploadFile(HttpUtil.URI
							+ HttpUtil.set_doctor_avatar, params, bytes);
					json = net.ememed.doctor2.util.TextUtil
							.substring(json, "{");
//					System.out.println("json=" + json);
					HashMap<String, Object> map = new HashMap<String, Object>();
					JSONObject obj = new JSONObject(json);
					map.put("success", obj.getInt("success"));
					map.put("errormsg", obj.getString("errormsg"));
					JSONObject obj_data = (JSONObject) obj.get("data");
					String avatar = obj_data.getString("AVATAR");
					map.put("avatar", avatar);
					sendMessage(IResult.END, map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.END:
				destroyDialog();
				HashMap<String, Object> map_2 = (HashMap<String, Object>) msg.obj;
				int success_2 = (Integer) map_2.get("success");
				if (success_2 == 0) {
					showToast((String) map_2.get("errormsg"));
					return;
				} else {
					showToast((String) map_2.get("errormsg"));
					String avater;
					avater = (String) map_2.get("avatar");
					SharePrefUtil.putString(Conast.AVATAR_UNAUDIT, (String) map_2.get("avatar"));
					SharePrefUtil.commit();
				}
				break;
			case IResult.PERSON_INFO:
				destroyDialog();
				DoctorInfo info = (DoctorInfo) msg.obj;
				if (null != info) {
					if (info.getSuccess() == 1) {
						infoEntry = info.getData();
						setVerifyState(infoEntry);
						updateDoctorView(infoEntry);//设置个人资料
					} else {
						showToast(info.getErrormsg());
					}
				}
				break;
			case IResult.SET_DOCTOR_SEX:		//设置性别
				PersonInfo personInfo = (PersonInfo) msg.obj;
				if(null != personInfo){
					if(1 == personInfo.getSuccess()){
						showToast(personInfo.getErrormsg());
					} else {
						showToast("设置性别失败，请稍后再试！");
					}
				}
				break;
				
			case IResult.SET_DOCTOR_ALLOW_FREE_CONSULT:	//设置专业属性
				PersonInfo personInfo1 = (PersonInfo) msg.obj;
				if(null != personInfo1){
					if(1 == personInfo1.getSuccess()){
						showToast(personInfo1.getErrormsg());
					} else {
						showToast("设置专业属性失败，请稍后再试！");
					}
				}
				break;
			case IResult.SET_DOCTOR_PROFESSIONAL:
				PersonInfo personInfo2 = (PersonInfo) msg.obj;
				if(null != personInfo2){
					if(1 == personInfo2.getSuccess()){
						showToast(personInfo2.getErrormsg());
					} else {
						showToast("设置职称失败，请稍后再试！");
					}
				}
				break;
			case IResult.UPLOAD_PIC:
				HashMap<String,Object> map_avatar = (HashMap<String,Object>)msg.obj;
				if (null != map_avatar && ((Integer)map_avatar.get("success") == 1)) {
					showToast("上传头像成功");
					doctor_img.setImageBitmap(bm_avatar);
					SharePrefUtil.putString(Conast.AVATAR_UNAUDIT, (String)map_avatar.get("avatar"));
					SharePrefUtil.commit();
				}
				break;	
			case IResult.CLEAR_FRONT_VIEW:
				clearFrontView();
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
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

	private void setVerifyState(DoctorInfoEntry doctorInfoEntry) {
		//if(!SharePrefUtil.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID))){
		if(!doctorInfoEntry.getAUDITSTATUS().equals(SharePrefUtil.getString(Conast.AUDIT_STATUS))){
			if("1".equals(doctorInfoEntry.getAUDITSTATUS())){
				SharePrefUtil.putBoolean(Conast.VALIDATED, true);
				SharePrefUtil.putString(Conast.AUDIT_STATUS, "1");
				//SharePrefUtil.putBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID), true);
			}else if("2".equals(doctorInfoEntry.getAUDITSTATUS())){
				SharePrefUtil.putString(Conast.AUDIT_STATUS, "2");
			} else if("3".equals(doctorInfoEntry.getAUDITSTATUS())){
				SharePrefUtil.putString(Conast.AUDIT_STATUS, "3");
				SharePrefUtil.putString(Conast.AUDIT_REFUSE, doctorInfoEntry.getAUDITREFUSE());
				tv_failed_reason.setText(doctorInfoEntry.getAUDITREFUSE());
			}
			SharePrefUtil.commit();
		}
	}

	
	
	//XXX
	private void updateDoctorView(DoctorInfoEntry doctorInfoEntry) {
		//判断需要显示哪种证件
		judgeCertificateType(doctorInfoEntry);
		
		if (!TextUtils.isEmpty(doctorInfoEntry.getSEX())) {
			if ("1".equals(doctorInfoEntry.getSEX())) {
				tv_realsex.setText(getString(R.string.add_userseek_boy));
			} else {
				tv_realsex.setText(getString(R.string.add_userseek_girl));
			}
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getREALNAME())) {
			tv_realname.setText(doctorInfoEntry.getREALNAME());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getMOBILE())) {
			tv_realcall.setText(doctorInfoEntry.getMOBILE());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getEMAIL())) {
			tv_realemail.setText(doctorInfoEntry.getEMAIL());
		}
		
		/** 如果证书编码区域有显示（显示执业证书时才显示编号），并且服务端返回有编号，才显示。否则，隐藏。*/
		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATENUM()) && ll_realcert_number.getVisibility() == View.VISIBLE) {
			tv_realcert_number.setText(doctorInfoEntry.getCERTIFICATENUM());
		}
		else{
			ll_realcert_number.setVisibility(View.GONE);
			line_realcert_number.setVisibility(View.GONE);
		}
		
		if (!TextUtils.isEmpty(doctorInfoEntry.getHOSPITALNAME())) {
			tv_hospital.setText(doctorInfoEntry.getHOSPITALNAME());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getROOMNAME())) {
			tv_hospital_laboratory.setText(doctorInfoEntry.getROOMNAME());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getSPECIALITY())) {
			tv_realskilled.setText(doctorInfoEntry.getSPECIALITY());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getRESUME())) {//预留详情字段
			tv_resume.setText(doctorInfoEntry.getRESUME());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR_AUDITSTATUS())){	//头像
			//头像通过审核，则显示正式头像
			if("1".equals(doctorInfoEntry.getAVATAR_AUDITSTATUS())){
				if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR())) {
					imageLoader.displayImage(doctorInfoEntry.getAVATAR(), doctor_img, Util.getOptions_big_avatar());
					SharePrefUtil.putString(Conast.AVATAR, doctorInfoEntry.getAVATAR());
					SharePrefUtil.commit();
				}
			} else { //头像未通过审核，则显示待审核的头像
				if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR_UNAUDIT())) {
					imageLoader.displayImage(doctorInfoEntry.getAVATAR_UNAUDIT(), doctor_img, Util.getOptions_big_avatar());
					SharePrefUtil.putString(Conast.AVATAR_UNAUDIT, doctorInfoEntry.getAVATAR_UNAUDIT());
					SharePrefUtil.commit();
					Log.i("测试", SharePrefUtil.getString(Conast.AVATAR_UNAUDIT));
				}
			}
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getPROFESSIONAL())) {	//职称
			tv_position.setText(doctorInfoEntry.getPROFESSIONAL());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATEHOME())  && ll_realcert_positive.getVisibility() == View.VISIBLE) {	//执业证书正面
			imageLoader.displayImage(doctorInfoEntry.getCERTIFICATEHOME(), photo_cert_positive, Util.getOptions_big_avatar());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATE()) && ll_realcert_opposite.getVisibility() == View.VISIBLE) {		//执业证书反面
			imageLoader.displayImage(doctorInfoEntry.getCERTIFICATE(), photo_cert_opposite, Util.getOptions_big_avatar());
		}
		if(!TextUtils.isEmpty(doctorInfoEntry.getQUALIFYHOME()) && ll_licence_positive.getVisibility() == View.VISIBLE){		//医师资格证首页
			imageLoader.displayImage(doctorInfoEntry.getQUALIFYHOME(), photo_licence_positive, Util.getOptions_big_avatar());
		}
		if(!TextUtils.isEmpty(doctorInfoEntry.getQUALIFY()) && ll_licence_opposite.getVisibility() == View.VISIBLE){		//医师资格证次页
			imageLoader.displayImage(doctorInfoEntry.getQUALIFY(), photo_licence_opposite, Util.getOptions_big_avatar());
		}
		if(!TextUtils.isEmpty(doctorInfoEntry.getCHESTCARD())  && ll_breast_plate.getVisibility() == View.VISIBLE){		//胸牌
			imageLoader.displayImage(doctorInfoEntry.getCHESTCARD(), photo_breast_plate, Util.getOptions_big_avatar());
		}
		if(!TextUtils.isEmpty(doctorInfoEntry.getEMPLOYEECARD())  && ll_work_permit.getVisibility() == View.VISIBLE){	//工作证
			imageLoader.displayImage(doctorInfoEntry.getEMPLOYEECARD(), photo_work_permit, Util.getOptions_big_avatar());
		}
		if(!TextUtils.isEmpty(doctorInfoEntry.getROOMPHONE())){		//科室电话
			tv_laboratory_phone.setText(doctorInfoEntry.getROOMPHONE());
		}
	}

	/**
	 * 判断需要显示的证件类型
	 * @param doctorInfoEntry
	 */
	private void judgeCertificateType(DoctorInfoEntry doctorInfoEntry) {
		if(!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATEHOME()) && (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATE()))){
			setCertificateLayout(0);
			return;
		}else if(!TextUtils.isEmpty(doctorInfoEntry.getQUALIFYHOME()) && (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFY()))){
			setCertificateLayout(1);
			return;
		}else if(!TextUtils.isEmpty(doctorInfoEntry.getCHESTCARD())){
			setCertificateLayout(2);
			return;
		}else if(!TextUtils.isEmpty(doctorInfoEntry.getEMPLOYEECARD())){
			setCertificateLayout(3);
			return;
		}else {
			setCertificateLayout(0);
			return;
		}
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
	public void onDestroy() {
		super.onDestroy();
		MyApplication.volleyHttpClient.cancelRequest(HttpUtil.get_doctor_info);
	}
	/**
	 * 根据选择的证书调整页面布局
	 * @param position
	 */
	private void setCertificateLayout(int position) {
		ll_realcert_number.setVisibility(View.GONE);
		ll_realcert_positive.setVisibility(View.GONE);
		ll_realcert_opposite.setVisibility(View.GONE);
		ll_licence_positive.setVisibility(View.GONE);
		ll_licence_opposite.setVisibility(View.GONE);
		ll_breast_plate.setVisibility(View.GONE);
		ll_work_permit.setVisibility(View.GONE);
		
		line_realcert_number.setVisibility(View.GONE);
		line_realcert_positive.setVisibility(View.GONE);
		line_realcert_opposite.setVisibility(View.GONE);
		line_licence_positive.setVisibility(View.GONE);
		line_licence_opposite.setVisibility(View.GONE);
		line_breast_plate.setVisibility(View.GONE);
		line_work_permit.setVisibility(View.GONE);
		
		if(0 == position){
			ll_realcert_positive.setVisibility(View.VISIBLE);
			ll_realcert_opposite.setVisibility(View.VISIBLE);
			ll_realcert_number.setVisibility(View.VISIBLE);
			line_realcert_positive.setVisibility(View.VISIBLE);
			line_realcert_opposite.setVisibility(View.VISIBLE);
			line_realcert_number.setVisibility(View.VISIBLE);
		} else if(1 == position){
			ll_licence_positive.setVisibility(View.VISIBLE);
			ll_licence_opposite.setVisibility(View.VISIBLE);
			line_licence_positive.setVisibility(View.VISIBLE);
			line_licence_opposite.setVisibility(View.VISIBLE);
		} else if(2 == position){
			ll_breast_plate.setVisibility(View.VISIBLE);
			line_breast_plate.setVisibility(View.VISIBLE);
		} else if (3 == position){
			ll_work_permit.setVisibility(View.VISIBLE);
			line_work_permit.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 点击照片的处理
	 * @param bytes	照片内容 byte数组
	 * @param photoType 照片类型
	 * @param requestType 请求类型
	 */
	private void dealPhotoClick(byte[] bytes, PhotoType photoType, final int requestType){
		Intent intent = null;
		if(null != bytes){
			/*intent = new Intent(this,ShowBigImage.class);
			intent.putExtra("thumbnail", bytes);
			startActivity(intent);*/
			
			File fileDir = new File(FileUtil.PATH, FileUtil.IMG_UPLOAD);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			String state = Environment.getExternalStorageState();
			
			try {
				File file = null;
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					file = new File(fileDir, UUID.randomUUID().toString() + ".jpeg");
				}else{
					return;
				}
			
				FileOutputStream out = new FileOutputStream(file);
				out.write(bytes);
		        out.flush();
		        out.close();
		        Uri uri = Uri.fromFile(file);
		        
		        intent = new Intent(this,ShowBigImage.class);
				intent.putExtra("uri", uri);
				startActivity(intent);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 } else {
			String remotepath = getPhotoUri(photoType);
			if(null != remotepath){
				intent = new Intent(this,ShowBigImage.class);
				intent.putExtra("remotepath", remotepath);
				startActivity(intent);
			} else {
				/*intent = new Intent(PersonInfoActivity.this,UploadPaperActivity.class);
				intent.putExtra("take_photo_type", photoType);
				startActivityForResult(intent, requestType);*/	
				
				mPhotoType =photoType;
				doPickPhotoAction();
			}
		 }
	}
	
	/**
	 * 判断对应的照片是否存在
	 * @param photoType
	 * @return
	 */
	private String getPhotoUri(PhotoType photoType){
		if(photoType == PhotoType.CER_PHOTO && !TextUtils.isEmpty(infoEntry.getCERTIFICATEHOME()))return infoEntry.getCERTIFICATEHOME();
		else if(photoType == PhotoType.CER_PHOTO_OPPOSITE && !TextUtils.isEmpty(infoEntry.getCERTIFICATE()))return infoEntry.getCERTIFICATE();
		else if(photoType == PhotoType.LICENCE_PHOTO_POSITIVE && !TextUtils.isEmpty(infoEntry.getQUALIFYHOME()))return infoEntry.getQUALIFYHOME();
		else if(photoType == PhotoType.LICENCE_PHOTO_OPPOSITE && !TextUtils.isEmpty(infoEntry.getQUALIFY()))return infoEntry.getQUALIFY();
		else if(photoType == PhotoType.BREAST_PLATE_PHOTO && !TextUtils.isEmpty(infoEntry.getCHESTCARD()))return infoEntry.getCHESTCARD();
		else if(photoType == PhotoType.WORK_PERMIT_PHOTO && !TextUtils.isEmpty(infoEntry.getEMPLOYEECARD()))return infoEntry.getEMPLOYEECARD();
		else return null;
	}
	
	private void doPickPhotoAction() {
		final Context dialogContext = new ContextThemeWrapper(this,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.apply_hosp_photo); // 拍照
		choices[1] = getString(R.string.apply_hosp_xiangce); // 从相册中选择

		MenuDialog.Builder builder = new MenuDialog.Builder(dialogContext);
		MenuDialog dialog = builder.setTitle(R.string.choose_photo_title)
				.setItems(choices, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								takePicture();
							}
							break;
						case 1:
							openGallery();
							break;
						}
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
}
