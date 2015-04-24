package net.ememed.doctor2.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.UploadPaperActivity.PhotoType;
import net.ememed.doctor2.db.PositionConfigTable;
import net.ememed.doctor2.entity.Doctor;
import net.ememed.doctor2.entity.DoctorInfo;
import net.ememed.doctor2.entity.DoctorInfoEntry;
import net.ememed.doctor2.entity.IMessage;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.Login;
import net.ememed.doctor2.entity.ResultInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.BitmapPutUtil;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.FileUtil;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.PictureUtil;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import net.ememed.doctor2.widget.MenuDialog;
import net.ememed.doctor2.widget.VerifyPaperDialog;
import cn.sharesdk.framework.authorize.ResizeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.UploadImgSuccessEvent;
import eu.janmuller.android.simplecropimage.CropImage;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 注册完成界面 分三种UI状态
 * 
 * verify_type == -1 帐户尚未激活 verify_type == 2 待审核 verify_type == 3 审核不通过
 * 
 * 点击重新审核，界面会变成重新提交资料
 * */
public class RegisterSuccessActivity extends BasicActivity implements
		BasicUIEvent {
	private static final int REQUEST_REGISTER = 1;
	private static final int REQUEST_CHOOSE_ROOM = 2;
	private static final int REQUEST_EDIT_SKILL = 3;
	private static final int REQUEST_EDIT_HOSIPITAL = 4;
	private static final int REQUEST_EDIT_CRE_NUMBER = 5;
	private static final int REQUEST_EDIT_ID_NUMBER = 6;
	private static final int REQUEST_UPLOAD_PHOTO_POSITIVE = 7; // 上传执业证书首页
	private static final int REQUEST_UPLOAD_PHOTO_OPPOSITE = 8; // 上传执业证书次页
	private final static int PICTURE_REQUEST = 9;
	private final static int CAMERA_REQUEST = 10;
	private static final int REQUEST_CODE_CROP_IMAGE = 11;
	private static final int REQUEST_CODE_GALLERY = 12;
	private static final int REQUEST_CODE_TAKE_PICTURE = 13;

	private static final int REQUEST_UPLOAD_PHOTO_BREAST_PLATE = 14; // 上传胸牌照片
	private static final int REQUEST_UPLOAD_PHOTO_WORK_PERMIT = 15; // 上传工作证
	private static final int REQUEST_UPLOAD_PHOTO_LICENCE_POSITIVE = 16; // 上传医师资格证首页
	private static final int REQUEST_UPLOAD_PHOTO_LICENCE_OPPOSITE = 17; // 上传医师资格证次页

	private static final int REQUEST_EDIT_EMAIL = 18; // email
	private static final int REQUEST_EDIT_LABORATORY_PHONE = 19; // 就职科室电话
	private static final int REQUEST_EDIT_DETAIL_INFO = 20; // email

	private Uri uri = null;// 选择头像

	private String account = null;
	private String pwd = null;
	private String from = null;
	private boolean isLoadinfo; // 是否需要从服务器取医生信息

	private ImageView iv_verify_icon;
	private TextView tv_verify_title;
	private TextView tv_verify_content;
	private LinearLayout ll_verify_title;
	private TextView tv_verify_failed_reason;
	private TextView tv_hospital_laboratory;// 科室
	private TextView tv_realskilled; // 擅长
	private TextView tv_hospital; // 医院
	private TextView tv_realcert_number; // 执业证书编号
	private TextView tv_email; // email
	private TextView tv_laboratory_phone; // 就职科室电话
	private TextView tv_detail_info; // 个人详情

	private TextView tv_title; // 标题
	private TextView tv_failed_reason; // 审核失败的原因

	private Spinner spinner_position;
	private TextView tv_id_number;
	private Spinner spinner_sex;
	private Spinner spinner_certificate_type;

	private LinearLayout layout_verifing; // 正在审核界面
	private LinearLayout layout_verify_failed; // 审核失败界面
	private LinearLayout layout_verify_success; // 审核成功界面

	private ImageView photo_cert_positive; // 医师执业证书首页图片
	private ImageView photo_cert_opposite; // 医师执业证书次页图片
	/*
	 * private ImageView photo_licence_positive; //医师资格证首页图片 private ImageView
	 * photo_licence_opposite; //医师资格证次页图片 private ImageView photo_breast_plate;
	 * //胸牌图片 private ImageView photo_work_permit; //工作证图片
	 */
	private LinearLayout ll_register_detail;
	private LinearLayout ll_register_result_tips;
	private Button btn_register_result;
	private LinearLayout ll_detail_register_title;
	private Bitmap bm_avatar;
	private byte[] bytes_avatar;
	private ImageView doctor_img;
	private boolean hasImage;

	private LinearLayout ll_realcert_positive; // 执业证书首页
	private LinearLayout ll_realcert_opposite; // 执业证书次页
	private LinearLayout ll_realcert_number; // 执业证书编码
	/*
	 * private LinearLayout ll_licence_positive; //医师资格证首页 private LinearLayout
	 * ll_licence_oppsite; //医师资格证次页 private LinearLayout ll_breast_plate; //胸牌
	 * private LinearLayout ll_work_permit; //工作证
	 */
	private View line_realcert_positive; // 执业证书首页下面的分割线
	private View line_realcert_opposite; // 执业证书次页
	// private View line_realcert_number; //执业证书编码
	/*
	 * private View line_licence_positive; //医师资格证首页 private View
	 * line_licence_oppsite; //医师资格证次页 private View line_breast_plate; //胸牌
	 * private View line_work_permit; //工作证
	 */
	private LinearLayout linear_layout1; // 灰色半透明覆盖部分
	private LinearLayout linear_layout2; // 灰色半透明覆盖部分
	private LinearLayout linear_layout3; // 灰色半透明覆盖部分
	private LinearLayout linear_layout4; // 灰色半透明覆盖部分

	private volatile PhotoType mPhotoType = PhotoType.CER_PHOTO;
	private DoctorInfoEntry infoEntry = null;
	private ScrollView scv_info;

	private TextView tv_cert_positive;
	private TextView tv_cert_opposite;

	private PhotoType cur_cache_cert_posi = null; // 当前本地缓存（photoBytes）中的正面照片类型
	private PhotoType cur_cache_cert_oppo = null; // 当前本地缓存（photoBytes_opposite）中的反面面照片类型

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.register_success);
		// verify_type = getIntent().getIntExtra("verify_type", 0);
		isLoadinfo = getIntent().getBooleanExtra("need_load_info", true);
		account = getIntent().getStringExtra("account");
		pwd = getIntent().getStringExtra("pwd");
		from = getIntent().getStringExtra("from");
		EventBus.getDefault().registerSticky(this, UploadImgSuccessEvent.class);
	}

	@Override
	protected void setupView() {
		tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.title_add_infomation));

		tv_failed_reason = (TextView) findViewById(R.id.tv_failed_reason);

		ll_register_detail = (LinearLayout) findViewById(R.id.ll_register_detail);
		ll_register_result_tips = (LinearLayout) findViewById(R.id.ll_register_result_tips);

		doctor_img = (ImageView) findViewById(R.id.iv_avatar);
		photo_cert_positive = (ImageView) findViewById(R.id.cert_positive);
		photo_cert_opposite = (ImageView) findViewById(R.id.cert_opposite);
		/*
		 * photo_licence_positive = (ImageView)
		 * findViewById(R.id.licence_positive); photo_licence_opposite =
		 * (ImageView) findViewById(R.id.licence_opposite); photo_breast_plate =
		 * (ImageView) findViewById(R.id.pic_breast_plate); photo_work_permit =
		 * (ImageView) findViewById(R.id.pic_work_permit);
		 */

		// ll_detail_register_title =
		// (LinearLayout)findViewById(R.id.ll_verify_title);
		ll_verify_title = (LinearLayout) findViewById(R.id.ll_register_result_title);
		iv_verify_icon = (ImageView) findViewById(R.id.iv_register_tips_gou);
		tv_verify_title = (TextView) findViewById(R.id.tv_uploaded_title);
		tv_verify_content = (TextView) findViewById(R.id.tv_verify_content);
		tv_verify_failed_reason = (TextView) findViewById(R.id.tv_verify_failed_reason);
		btn_register_result = (Button) findViewById(R.id.btn_register_result);

		layout_verifing = (LinearLayout) findViewById(R.id.layout_verifing);
		layout_verify_failed = (LinearLayout) findViewById(R.id.layout_verify_failed);
		layout_verify_success = (LinearLayout) findViewById(R.id.layout_verify_success);
		scv_info = (ScrollView) findViewById(R.id.scv_info);

		tv_cert_positive = (TextView) findViewById(R.id.tv_cert_positive);
		tv_cert_opposite = (TextView) findViewById(R.id.tv_cert_opposite);

		linear_layout1 = (LinearLayout) findViewById(R.id.linear_layout1);
		linear_layout2 = (LinearLayout) findViewById(R.id.linear_layout2);
		linear_layout3 = (LinearLayout) findViewById(R.id.linear_layout3);
		linear_layout4 = (LinearLayout) findViewById(R.id.linear_layout4);

		setVerifyStateView();

		tv_hospital_laboratory = (TextView) findViewById(R.id.tv_hospital_laboratory);
		tv_realcert_number = (TextView) findViewById(R.id.tv_realcert_number);
		tv_hospital = (TextView) findViewById(R.id.tv_hospital);
		tv_realskilled = (TextView) findViewById(R.id.tv_realskilled);
		tv_id_number = (TextView) findViewById(R.id.tv_id_number);
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_laboratory_phone = (TextView) findViewById(R.id.tv_laboratory_phone);
		tv_detail_info = (TextView) findViewById(R.id.tv_detail_info);

		ll_realcert_positive = (LinearLayout) findViewById(R.id.lt_realcert_positive);
		ll_realcert_opposite = (LinearLayout) findViewById(R.id.lt_realcert_opposite);
		/*
		 * ll_licence_positive = (LinearLayout)
		 * findViewById(R.id.lt_medical_licence_positive); ll_licence_oppsite =
		 * (LinearLayout) findViewById(R.id.lt_medical_licence_oppsite);
		 */
		ll_realcert_number = (LinearLayout) findViewById(R.id.lt_realcert_number);
		/*
		 * ll_breast_plate = (LinearLayout) findViewById(R.id.lt_breast_plate);
		 * ll_work_permit = (LinearLayout) findViewById(R.id.lt_work_permit);
		 */

		line_realcert_positive = (View) findViewById(R.id.line_realcert_positive);
		line_realcert_opposite = (View) findViewById(R.id.line_realcert_opposite);
		/*
		 * line_licence_positive = (View)
		 * findViewById(R.id.line_medical_licence_positive);
		 * line_licence_oppsite = (View)
		 * findViewById(R.id.line_medical_licence_oppsite);
		 */
		// line_realcert_number = (View)
		// findViewById(R.id.line_realcert_number);
		/*
		 * line_breast_plate = (View) findViewById(R.id.line_breast_plate);
		 * line_work_permit = (View) findViewById(R.id.line_work_permit);
		 */

		PositionConfigTable positionTable = new PositionConfigTable();
		String[] positionNames = positionTable.getAllPositionNames();
		final int length = positionNames.length;
		String[] positionNames2 = new String[length + 1];
		positionNames2[0] = "请选择";
		for (int i = 0; i < length; i++) {
			positionNames2[i + 1] = positionNames[i];
		}

		spinner_position = (Spinner) findViewById(R.id.spinner_position);
		ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, positionNames2);
		// 设置下拉列表的风格
		_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_position.setAdapter(_Adapter);
		spinner_position.setSelection(0);

		spinner_sex = (Spinner) findViewById(R.id.spinner_sex); // 性别选择下拉框
		spinner_certificate_type = (Spinner) findViewById(R.id.spinner_certificate_type); // 证件类型选择下拉框
		spinner_certificate_type
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// 首次进入界面会触发一次
						setCertificateLayout(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		if (isLoadinfo) {
			getPersonInfo();
		}
	}

	private void setVerifyStateView() {
		layout_verifing.setVisibility(View.GONE);
		layout_verify_failed.setVisibility(View.GONE);
		layout_verify_success.setVisibility(View.GONE);
		if (from == null
				|| !from.equals(PersonInfoActivity.class.getSimpleName())) {
			if (!TextUtils
					.isEmpty(SharePrefUtil.getString(Conast.AUDIT_STATUS))) {
				tv_title.setText("资料审核");

				if (SharePrefUtil.getString(Conast.AUDIT_STATUS).equals("2")) {
					layout_verifing.setVisibility(View.VISIBLE);
					scv_info.setVisibility(View.GONE);
				} else if (SharePrefUtil.getString(Conast.AUDIT_STATUS).equals(
						"3")) {
					layout_verify_failed.setVisibility(View.VISIBLE);
					scv_info.setVisibility(View.GONE);
					tv_failed_reason.setText(SharePrefUtil
							.getString(Conast.AUDIT_REFUSE));
				}
			}
		}
	}

	/**
	 * 根据选择的证书调整页面布局
	 * 
	 * @param position
	 */
	private void setCertificateLayout(int position) {
		ll_realcert_positive.setVisibility(View.GONE);
		ll_realcert_opposite.setVisibility(View.GONE);
		line_realcert_positive.setVisibility(View.GONE);
		line_realcert_opposite.setVisibility(View.GONE);

		if (1 == position) {
			ll_realcert_positive.setVisibility(View.VISIBLE);
			ll_realcert_opposite.setVisibility(View.VISIBLE);
			line_realcert_positive.setVisibility(View.VISIBLE);
			line_realcert_opposite.setVisibility(View.VISIBLE);
			tv_cert_positive.setText(getString(R.string.set_cert_positive));
			tv_cert_opposite.setText(getString(R.string.set_cert_opposite));

			// 先看内存中是否存在byte数据，再看网络数据
			if (cur_cache_cert_posi == PhotoType.CER_PHOTO
					&& photoBytes != null) {
				photo_cert_positive.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes));
			} else {
				photo_cert_positive
						.setImageResource(R.drawable.certificate_default);
			}

			if (cur_cache_cert_oppo == PhotoType.CER_PHOTO_OPPOSITE
					&& photoBytes_opposite != null) {
				photo_cert_opposite.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes_opposite));
			} else {
				photo_cert_opposite
						.setImageResource(R.drawable.certificate_default);
			}

			// 网络数据
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getCERTIFICATEHOME())) {// 执业证书正面
				imageLoader.displayImage(infoEntry.getCERTIFICATEHOME(),
						photo_cert_positive, Util.getOptions_big_avatar());
			}
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getCERTIFICATE())) { // 执业证书反面
				imageLoader.displayImage(infoEntry.getCERTIFICATE(),
						photo_cert_opposite, Util.getOptions_big_avatar());
			}

		} else if (2 == position) {
			ll_realcert_positive.setVisibility(View.VISIBLE);
			ll_realcert_opposite.setVisibility(View.VISIBLE);
			line_realcert_positive.setVisibility(View.VISIBLE);
			line_realcert_opposite.setVisibility(View.VISIBLE);
			tv_cert_positive.setText(getString(R.string.set_licence_positive));
			tv_cert_opposite.setText(getString(R.string.set_licence_opposite));

			// 先本地，再网络
			if (cur_cache_cert_posi == PhotoType.LICENCE_PHOTO_POSITIVE
					&& photoBytes != null) {
				photo_cert_positive.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes));
			} else {
				photo_cert_positive
						.setImageResource(R.drawable.certificate_default);
			}

			if (cur_cache_cert_oppo == PhotoType.LICENCE_PHOTO_OPPOSITE
					&& photoBytes_opposite != null) {
				photo_cert_opposite.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes_opposite));
			} else {
				photo_cert_opposite
						.setImageResource(R.drawable.certificate_default);
			}

			// 网络数据
			// 网络数据
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getQUALIFYHOME())) {// 执业证书正面
				imageLoader.displayImage(infoEntry.getQUALIFYHOME(),
						photo_cert_positive, Util.getOptions_big_avatar());
			}
			if (infoEntry != null && !TextUtils.isEmpty(infoEntry.getQUALIFY())) { // 执业证书反面
				imageLoader.displayImage(infoEntry.getQUALIFY(),
						photo_cert_opposite, Util.getOptions_big_avatar());
			}

		} else if (3 == position) {
			/*
			 * ll_realcert_positive.setVisibility(View.VISIBLE);
			 * line_realcert_positive.setVisibility(View.VISIBLE);
			 * tv_cert_positive.setText(getString(R.string.set_breast_plate));
			 * 
			 * //本地数据 if(cur_cache_cert_posi == PhotoType.BREAST_PLATE_PHOTO &&
			 * photoBytes != null){
			 * photo_cert_positive.setImageBitmap(BitmapUtil
			 * .bytes2Bimap(photoBytes)); } else {
			 * photo_cert_positive.setImageResource
			 * (R.drawable.certificate_default); }
			 * 
			 * //网络数据 if (infoEntry!= null &&
			 * !TextUtils.isEmpty(infoEntry.getCHESTCARD())){
			 * imageLoader.displayImage(infoEntry.getCHESTCARD(),
			 * photo_cert_positive, Util.getOptions_big_avatar()); }
			 */

			ll_realcert_positive.setVisibility(View.VISIBLE);
			line_realcert_positive.setVisibility(View.VISIBLE);
			tv_cert_positive.setText(getString(R.string.set_work_permit));

			// 本地数据
			if (cur_cache_cert_posi == PhotoType.WORK_PERMIT_PHOTO
					&& photoBytes != null) {
				photo_cert_positive.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes));
			} else {
				photo_cert_positive
						.setImageResource(R.drawable.certificate_default);
			}

			// 网络数据
			// 网络数据
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getEMPLOYEECARD())) {
				imageLoader.displayImage(infoEntry.getEMPLOYEECARD(),
						photo_cert_positive, Util.getOptions_big_avatar());
			}

		}
	}

	@Override
	public void execute(int mes, Object obj) {
		if (mes == IResult.UPLOAD_PIC) {
			byte[] bytes = (byte[]) obj;
			setImage(bytes);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
			finish();
			Intent intent = new Intent(RegisterSuccessActivity.this,
					MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		} else {
			return false;
		}
	}

	public void doClick(View view) {
		Intent intent = null;
		int id = view.getId();
		if (id == R.id.btn_back) {
			finish();
			intent = new Intent(RegisterSuccessActivity.this,
					MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (id == R.id.lt_hospital_laboratory) { // 就职科室
			intent = new Intent(RegisterSuccessActivity.this,
					RoomsListActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			startActivityForResult(intent, REQUEST_CHOOSE_ROOM);
		} else if (id == R.id.btn_submit) { // 提交审核
			register_detail();
		} else if (id == R.id.lt_hospital) { // 就职医院
			intent = new Intent(RegisterSuccessActivity.this,
					SaveHospitalActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_hospital.getText().toString());
			startActivityForResult(intent, REQUEST_EDIT_HOSIPITAL);
		} else if (id == R.id.lt_realskilled) { // 擅长
			intent = new Intent(RegisterSuccessActivity.this,
					SaveSkilledActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_realskilled.getText().toString());
			startActivityForResult(intent, REQUEST_EDIT_SKILL);
		} else if (id == R.id.lt_realcert_number) { // 医师执业证书编号
			intent = new Intent(RegisterSuccessActivity.this,
					SaveCertNumberActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_realcert_number.getText()
					.toString());
			startActivityForResult(intent, REQUEST_EDIT_CRE_NUMBER);
		} else if (id == R.id.lt_id_number) { // 身份证号
			intent = new Intent(RegisterSuccessActivity.this,
					SaveCertNumberActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_id_number.getText().toString());
			intent.putExtra("title", getString(R.string.set_id_num_title));
			startActivityForResult(intent, REQUEST_EDIT_ID_NUMBER);
		} else if (id == R.id.lt_email) { // Email
			intent = new Intent(RegisterSuccessActivity.this,
					SaveEmailActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_email.getText().toString());
			startActivityForResult(intent, REQUEST_EDIT_EMAIL);
		} else if (id == R.id.lt_laboratory_phone) { // 就职科室电话
			intent = new Intent(RegisterSuccessActivity.this,
					SaveRoomPhoneActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_laboratory_phone.getText()
					.toString());
			startActivityForResult(intent, REQUEST_EDIT_LABORATORY_PHONE);
		} else if (id == R.id.lt_detail_info) { // 详细信息
			intent = new Intent(RegisterSuccessActivity.this,
					SaveResumeActivity.class);
			intent.putExtra("from",
					RegisterSuccessActivity.class.getSimpleName());
			intent.putExtra("exist_str", tv_detail_info.getText().toString());
			startActivityForResult(intent, REQUEST_EDIT_DETAIL_INFO);
		} else if (id == R.id.lt_realcert_positive) { // 执业证书正面
			switch (getChoiceCertificateaType()) {
			case 1:
				mPhotoType = PhotoType.CER_PHOTO;
				break;
			case 2:
				mPhotoType = PhotoType.LICENCE_PHOTO_POSITIVE;
				break;
			/*
			 * case 3: mPhotoType = PhotoType.BREAST_PLATE_PHOTO; break;
			 */
			case 3:
				mPhotoType = PhotoType.WORK_PERMIT_PHOTO;
				
				
				break;
			default:
				break;
			}
			showImage = photo_cert_positive;
			doPickPhotoAction();
		} else if (id == R.id.lt_realcert_opposite) { // 执业证书背面
			switch (getChoiceCertificateaType()) {
			case 1:
				mPhotoType = PhotoType.CER_PHOTO_OPPOSITE;
				break;
			case 2:
				mPhotoType = PhotoType.LICENCE_PHOTO_OPPOSITE;
				
				break;
			default:
				break;
			}
			showImage = photo_cert_opposite;
			doPickPhotoAction();
		}
		// XXX 此处需修改逻辑(已修改，可删除)
		/*
		 * else if (id == R.id.lt_medical_licence_positive) {//医师资格证正面
		 * mPhotoType = PhotoType.LICENCE_PHOTO_POSITIVE; doPickPhotoAction(); }
		 * else if (id == R.id.lt_medical_licence_oppsite) { //医师资格证背面
		 * mPhotoType = PhotoType.LICENCE_PHOTO_OPPOSITE; doPickPhotoAction(); }
		 * else if (id == R.id.lt_breast_plate) { //胸牌 mPhotoType =
		 * PhotoType.BREAST_PLATE_PHOTO; doPickPhotoAction(); } else if (id ==
		 * R.id.lt_work_permit) { //工作证 mPhotoType =
		 * PhotoType.WORK_PERMIT_PHOTO; doPickPhotoAction(); }
		 */else if (id == R.id.cert_positive) { // 正面照片

			switch (getChoiceCertificateaType()) {
			case 1:
				mPhotoType = PhotoType.CER_PHOTO;
				break;
			case 2:
				mPhotoType = PhotoType.LICENCE_PHOTO_POSITIVE;
				break;
			/*
			 * case 3: mPhotoType = PhotoType.BREAST_PLATE_PHOTO; break;
			 */
			case 3:
				mPhotoType = PhotoType.WORK_PERMIT_PHOTO;
				break;
			default:
				break;
			}

			dealPhotoClick(photoBytes, mPhotoType,
					REQUEST_UPLOAD_PHOTO_POSITIVE);
		} else if (id == R.id.cert_opposite) { // 反面照片
			switch (getChoiceCertificateaType()) {
			case 1:
				mPhotoType = PhotoType.CER_PHOTO_OPPOSITE;
				break;
			case 2:
				mPhotoType = PhotoType.LICENCE_PHOTO_OPPOSITE;
				break;
			default:
				break;
			}
			dealPhotoClick(photoBytes_opposite, mPhotoType,
					REQUEST_UPLOAD_PHOTO_OPPOSITE);
		}
		// XXX 此处需修改逻辑（已修改，可删除）
		/*
		 * else if (id == R.id.licence_positive) { //医师资格证正面照片
		 * dealPhotoClick(licencePhotoBytes, PhotoType.LICENCE_PHOTO_POSITIVE,
		 * REQUEST_UPLOAD_PHOTO_LICENCE_POSITIVE); } else if (id ==
		 * R.id.licence_opposite) { //医师资格证反面照片
		 * dealPhotoClick(licencePhotoBytes_opposite,
		 * PhotoType.LICENCE_PHOTO_OPPOSITE,
		 * REQUEST_UPLOAD_PHOTO_LICENCE_OPPOSITE); } else if (id ==
		 * R.id.pic_breast_plate) { //胸牌照片 dealPhotoClick(breastPlatePhotoByte,
		 * PhotoType.BREAST_PLATE_PHOTO, REQUEST_UPLOAD_PHOTO_BREAST_PLATE); }
		 * else if (id == R.id.pic_work_permit) { //工作证照片
		 * dealPhotoClick(workPermitPhotoByte, PhotoType.WORK_PERMIT_PHOTO,
		 * REQUEST_UPLOAD_PHOTO_WORK_PERMIT); }
		 */else if (id == R.id.lt_avatar) { // 头像
			mPhotoType = PhotoType.AVATAR_IMAGE;
			setDoctorImage();
		} else if (id == R.id.iv_avatar) { // 头像照片

			if (TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))) {
				if (TextUtils.isEmpty(SharePrefUtil
						.getString(Conast.AVATAR_UNAUDIT))) {
					mPhotoType = PhotoType.AVATAR_IMAGE;
					setDoctorImage();
				} else {
					intent = new Intent(this, ShowBigImage.class);
					intent.putExtra("uri", photoUri);
					intent.putExtra("remotepath",
							SharePrefUtil.getString(Conast.AVATAR_UNAUDIT));
					startActivity(intent);
				}
			} else {
				intent = new Intent(this, ShowBigImage.class);
				intent.putExtra("uri", photoUri);
				intent.putExtra("remotepath",
						SharePrefUtil.getString(Conast.AVATAR));
				startActivity(intent);
			}
		}

		// 正在审核页面的两个按钮
		else if (id == R.id.btn_add_information_1) {
			layout_verifing.setVisibility(View.GONE);
			scv_info.setVisibility(View.VISIBLE);
			tv_title.setText(getString(R.string.title_add_infomation));
		} else if (id == R.id.btn_close_1) {
			finish();
			intent = new Intent(RegisterSuccessActivity.this,
					MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		// 审核失败界面的一个按钮
		else if (id == R.id.btn_add_information_2) {
			layout_verify_failed.setVisibility(View.GONE);
			scv_info.setVisibility(View.VISIBLE);
			tv_title.setText(getString(R.string.title_add_infomation));
		}
		// 审核成功界面的两个按钮
		else if (id == R.id.btn_invite_patient_3) {
			startActivity(new Intent(RegisterSuccessActivity.this,
					BusinessCardActivity.class));
			finish();
		} else if (id == R.id.btn_info_preview_3) {
			startActivity(new Intent(RegisterSuccessActivity.this,
					PersonInfoActivity.class));
			finish();
		}
		// 灰色半透明背景
		else if (id == R.id.linear_layout4) {
			clearFrontView();
		}
	}

	/**
	 * 获取用户选择的证件类型(spinner_certificate_type下拉框的选择) 1:医生执业证书 2:医师资格证 3:胸牌 4:工作证
	 */
	private int getChoiceCertificateaType() {

		return spinner_certificate_type.getSelectedItemPosition();
	}

	/**
	 * 点击照片的处理
	 * 
	 * @param bytes
	 *            照片内容 byte数组
	 * @param photoType
	 *            照片类型
	 * @param requestType
	 *            请求类型
	 */
	private void dealPhotoClick(byte[] bytes, PhotoType photoType,
			final int requestType) {
		Intent intent = null;
		if ((cur_cache_cert_oppo == photoType || cur_cache_cert_posi == photoType)
				&& null != bytes) {
			/*
			 * intent = new Intent(this,ShowBigImage.class);
			 * intent.putExtra("thumbnail", bytes); startActivity(intent);
			 */

			File fileDir = new File(FileUtil.PATH, FileUtil.IMG_UPLOAD);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			String state = Environment.getExternalStorageState();

			try {
				File file = null;
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					file = new File(fileDir, UUID.randomUUID().toString()
							+ ".jpeg");
				} else {
					return;
				}

				FileOutputStream out = new FileOutputStream(file);
				out.write(bytes);
				out.flush();
				out.close();
				Uri uri = Uri.fromFile(file);

				intent = new Intent(this, ShowBigImage.class);
				intent.putExtra("uri", uri);
				startActivity(intent);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("测试", e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			String remotepath = getPhotoUri(photoType);
			if (null != remotepath) {
				intent = new Intent(this, ShowBigImage.class);
				intent.putExtra("remotepath", remotepath);
				startActivity(intent);
			} else {
				mPhotoType = photoType;
				doPickPhotoAction();
			}
		}
	}

	/**
	 * 判断对应的照片是否存在
	 * @param photoType
	 * @return
	 */
	private String getPhotoUri(PhotoType photoType) {
		if (infoEntry != null) {
			if (photoType == PhotoType.CER_PHOTO
					&& !TextUtils.isEmpty(infoEntry.getCERTIFICATEHOME()))
				return infoEntry.getCERTIFICATEHOME();
			else if (photoType == PhotoType.CER_PHOTO_OPPOSITE
					&& !TextUtils.isEmpty(infoEntry.getCERTIFICATE()))
				return infoEntry.getCERTIFICATE();
			else if (photoType == PhotoType.LICENCE_PHOTO_POSITIVE
					&& !TextUtils.isEmpty(infoEntry.getQUALIFYHOME()))
				return infoEntry.getQUALIFYHOME();
			else if (photoType == PhotoType.LICENCE_PHOTO_OPPOSITE
					&& !TextUtils.isEmpty(infoEntry.getQUALIFY()))
				return infoEntry.getQUALIFY();
			else if (photoType == PhotoType.BREAST_PLATE_PHOTO
					&& !TextUtils.isEmpty(infoEntry.getCHESTCARD()))
				return infoEntry.getCHESTCARD();
			else if (photoType == PhotoType.WORK_PERMIT_PHOTO
					&& !TextUtils.isEmpty(infoEntry.getEMPLOYEECARD()))
				return infoEntry.getEMPLOYEECARD();
			else
				return null;
		} else {
			return null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				startCropImage();
			}
			break;
		case REQUEST_CODE_CROP_IMAGE:
			if (PhotoType.AVATAR_IMAGE == mPhotoType) {
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
							hasImage = true;
							bm_avatar = BitmapUtil.getBitmapByUri(this,
									photoUri);
							bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);
							doctor_img.setImageBitmap(bm_avatar);
							if (bytes_avatar != null) {
								UICore.eventTask(RegisterSuccessActivity.this,
										RegisterSuccessActivity.this,
										IResult.UPLOAD_PIC, "提交头像中...",
										bytes_avatar);
							}
						}
					} else {
						if (null != picFile && picFile.exists()) {
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
							hasImage = true;
							Intent intent = new Intent(
									RegisterSuccessActivity.this,
									VerifyPaperActivity.class);
							PictureUtil.save(picFile.getAbsolutePath(),
									picFile, RegisterSuccessActivity.this);
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
			break;
		case REQUEST_CODE_GALLERY:
			if (PhotoType.AVATAR_IMAGE == mPhotoType) {
				try {
					InputStream inputStream = getContentResolver()
							.openInputStream(data.getData());
					FileOutputStream fileOutputStream = new FileOutputStream(
							picFile);
					BitmapUtil.copyStream(inputStream, fileOutputStream);
					fileOutputStream.close();
					inputStream.close();
					startCropImage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					InputStream inputStream = getContentResolver()
							.openInputStream(data.getData());
					FileOutputStream fileOutputStream = new FileOutputStream(
							picFile);
					BitmapUtil.copyStream(inputStream, fileOutputStream);
					fileOutputStream.close();
					inputStream.close();
					hasImage = true;
					Intent intent = new Intent(RegisterSuccessActivity.this,
							VerifyPaperActivity.class);
					PictureUtil.save(picFile.getAbsolutePath(), picFile,
							RegisterSuccessActivity.this);
					byte[] imgDatas = FileUtil.getBytes(picFile);
					intent.putExtra("img_data", imgDatas);
					intent.putExtra("take_photo_type", mPhotoType);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;
		case REQUEST_REGISTER:
			if (RESULT_OK == resultCode && null != data) {
				String account = data.getStringExtra("account");
				String pwd = data.getStringExtra("pwd");
			}
			break;
		case REQUEST_CHOOSE_ROOM:
			if (RESULT_OK == resultCode && null != data) {
				String room = data.getStringExtra("roomname");
				tv_hospital_laboratory.setText(room);
			}
			break;
		case REQUEST_EDIT_SKILL:
			if (RESULT_OK == resultCode && null != data) {
				String skill = data.getStringExtra("specialityStr");
				tv_realskilled.setText(skill);
			}
			break;
		case REQUEST_EDIT_HOSIPITAL:
			if (RESULT_OK == resultCode && null != data) {
				String hospital = data.getStringExtra("hospital");
				tv_hospital.setText(hospital);
			}
			break;
		case REQUEST_EDIT_CRE_NUMBER:
			if (RESULT_OK == resultCode && null != data) {
				String cer_number = data.getStringExtra("cer_number");
				tv_realcert_number.setText(cer_number);
			}
			break;
		case REQUEST_EDIT_ID_NUMBER:
			if (RESULT_OK == resultCode && null != data) {
				String id_number = data.getStringExtra("cer_number");
				tv_id_number.setText(id_number);
			}
			break;
		case REQUEST_EDIT_EMAIL:
			if (RESULT_OK == resultCode && null != data) {
				String email = data.getStringExtra("cer_number");
				tv_email.setText(email);
			}
			break;
		case REQUEST_EDIT_LABORATORY_PHONE:
			if (RESULT_OK == resultCode && null != data) {
				String phone = data.getStringExtra("cer_number");
				tv_laboratory_phone.setText(phone);
			}
			break;
		case REQUEST_EDIT_DETAIL_INFO:
			if (RESULT_OK == resultCode && null != data) {
				String info = data.getStringExtra("resumeStr");
				tv_detail_info.setText(info);
			}
			break;
		case PICTURE_REQUEST:
			if (resultCode == RESULT_OK && null != data) {
				uri = data.getData();
				if (uri == null) {
					Toast.makeText(this, "图片拍摄失败，请重新拍摄", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				bm_avatar = BitmapUtil.getBitmapByUri(this, uri);
				bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);

				if (bytes_avatar != null) {
					UICore.eventTask(RegisterSuccessActivity.this,
							RegisterSuccessActivity.this, IResult.UPLOAD_PIC,
							"提交头像中...", bytes_avatar);
				}
			}
			break;
		case CAMERA_REQUEST:
			if (resultCode == RESULT_OK) {

				bm_avatar = BitmapUtil.getBitmapByUri(this, uri);
				bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);

				if (bytes_avatar != null) {
					UICore.eventTask(RegisterSuccessActivity.this,
							RegisterSuccessActivity.this, IResult.UPLOAD_PIC,
							"提交头像中...", bytes_avatar);
				}
			}
			break;
		default:
			break;
		}
	}

	private void startCropImage() {
		Intent intent = new Intent(this, CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, picFile.getPath());
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra("outputX", 1000);
		intent.putExtra("outputY", 1000);
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}

	
	
	ImageView showImage;
	@Override
	public void onCameraPath(String path) {
		// TODO Auto-generated method stub
		super.onCameraPath(path);

		if (PhotoType.AVATAR_IMAGE == mPhotoType) {

			try {
				bm_avatar = BitmapPutUtil.getimage(path);
				bytes_avatar = BitmapUtil.Bitmap2Bytes(bm_avatar);
				doctor_img.setImageBitmap(bm_avatar);
				if (bytes_avatar != null) {
					UICore.eventTask(RegisterSuccessActivity.this,
							RegisterSuccessActivity.this, IResult.UPLOAD_PIC,
							"提交头像中...", bytes_avatar);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
//			try {
				hasImage = true;
//				Intent intent = new Intent(RegisterSuccessActivity.this,
//						VerifyPaperActivity.class);
				// PictureUtil.save(picFile.getAbsolutePath(), picFile,
				// RegisterSuccessActivity.this);
				
				VerifyPaperDialog verifyPaperDialog = new VerifyPaperDialog(this, R.style.wheelDialog);
				verifyPaperDialog.show();
				
				bm_avatar = BitmapPutUtil.getimage(path);
				byte[] imgDatas = BitmapUtil.Bitmap2Bytes(bm_avatar);
				
				
				
				verifyPaperDialog.set_doctor_certificate(imgDatas, mPhotoType,showImage);
//				intent.putExtra("img_data", imgDatas);
//				intent.putExtra("take_photo_type", mPhotoType);
//				startActivity(intent);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}

	}

	// XXX 需要修改逻辑(根据选择的证件来判断)--已修改，可删除
	private boolean judgeCertificateIsUpload() {

		if (0 == getChoiceCertificateaType()) {
			return false;
		} else if (1 == getChoiceCertificateaType()) {
			// 先看以前是否上传成功过该证件
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getCERTIFICATEHOME())
					&& !TextUtils.isEmpty(infoEntry.getCERTIFICATE())) {
				return true;
			}

			// 再看当前是否上传
			if (TextUtils
					.isEmpty(SharePrefUtil.getString(Conast.PIC_CERT_POSI))
					|| TextUtils.isEmpty(SharePrefUtil
							.getString(Conast.PIC_CERT_OPPSI))) {
				return false;
			} else {
				return true;
			}
		} else if (2 == getChoiceCertificateaType()) {
			// 先看以前是否上传成功过该证件
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getQUALIFYHOME())
					&& !TextUtils.isEmpty(infoEntry.getQUALIFY())) {
				return true;
			}

			// 再看当前是否上传
			if (TextUtils.isEmpty(SharePrefUtil
					.getString(Conast.PIC_LICENCE_POSITIVE))
					|| TextUtils.isEmpty(SharePrefUtil
							.getString(Conast.PIC_LICENCE_OPPOSITE))) {
				return false;
			} else {
				return true;
			}
		} /*
		 * else if(3 == getChoiceCertificateaType()){
		 * 
		 * //先看以前是否上传成功过该证件 if(infoEntry != null &&
		 * !TextUtils.isEmpty(infoEntry.getCHESTCARD())){ return true; }
		 * 
		 * //再看当前是否上传
		 * if(TextUtils.isEmpty(SharePrefUtil.getString(Conast.PIC_BREAST_PLATE
		 * ))){ return false; } else { return true; } }
		 */else if (3 == getChoiceCertificateaType()) {
			// 先看以前是否上传成功过该证件
			if (infoEntry != null
					&& !TextUtils.isEmpty(infoEntry.getEMPLOYEECARD())) {
				return true;
			}

			// 再看当前是否上传
			if (TextUtils.isEmpty(SharePrefUtil
					.getString(Conast.PIC_WORK_PERMIT))) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * if (View.VISIBLE == ll_realcert_positive.getVisibility() &&
		 * TextUtils.isEmpty(SharePrefUtil.getString(Conast.PIC_CERT_POSI))) {
		 * return false; } if (View.VISIBLE ==
		 * ll_realcert_opposite.getVisibility() &&
		 * TextUtils.isEmpty(SharePrefUtil.getString(Conast.PIC_CERT_OPPSI))) {
		 * return false; }
		 * 
		 * Log.e("测试", SharePrefUtil.getString(Conast.PIC_LICENCE_POSITIVE)); if
		 * (View.VISIBLE == ll_licence_positive.getVisibility() &&
		 * TextUtils.isEmpty
		 * (SharePrefUtil.getString(Conast.PIC_LICENCE_POSITIVE))) { return
		 * false; } Log.e("测试",
		 * SharePrefUtil.getString(Conast.PIC_LICENCE_OPPOSITE)); if
		 * (View.VISIBLE == ll_licence_oppsite.getVisibility() &&
		 * TextUtils.isEmpty
		 * (SharePrefUtil.getString(Conast.PIC_LICENCE_OPPOSITE))) { return
		 * false; } if (View.VISIBLE == ll_breast_plate.getVisibility() &&
		 * TextUtils.isEmpty(SharePrefUtil.getString(Conast.PIC_BREAST_PLATE)))
		 * { return false; } if (View.VISIBLE == ll_work_permit.getVisibility()
		 * &&
		 * TextUtils.isEmpty(SharePrefUtil.getString(Conast.PIC_WORK_PERMIT))) {
		 * return false; }
		 */
		return true;
	}

	/***
	 * 提交审核
	 */
	private void register_detail() {

		if (isLoadinfo == false) {
			if (TextUtils.isEmpty(SharePrefUtil.getString(Conast.AVATAR))
					&& TextUtils.isEmpty(SharePrefUtil
							.getString(Conast.AVATAR_UNAUDIT))) {
				showToast(getString(R.string.need_select_all));
				return;
			}
		}

		if (!judgeCertificateIsUpload()) {
			showToast("请上传证件照片！");
			return;
		}

		final String hospital = tv_hospital.getText().toString().trim();
		final String labStr = tv_hospital_laboratory.getText().toString()
				.trim();
		final String skilled = tv_realskilled.getText().toString().trim();
		final String positionStr = spinner_position.getSelectedItem()
				.toString();
		final String sexStr = spinner_sex.getSelectedItem().toString();
		final String emailStr = tv_email.getText().toString();
		final String resumeStr = tv_detail_info.getText().toString();
		final String roomPhone = tv_laboratory_phone.getText().toString();

		if (TextUtils.isEmpty(sexStr) || sexStr.equals("请选择")) {
			showToast(getString(R.string.need_select_all));
			return;
		}
		if (TextUtils.isEmpty(positionStr) || positionStr.equals("请选择")) {
			showToast(getString(R.string.need_select_all));
			return;
		}
		if (TextUtils.isEmpty(hospital)) {
			showToast(getString(R.string.need_select_all));
			return;
		}
		if (TextUtils.isEmpty(labStr)) {
			showToast(getString(R.string.need_select_all));
			return;
		}
		if (TextUtils.isEmpty(skilled)) {
			showToast(getString(R.string.need_select_all));
			return;
		}
		loading(null);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN));
		params.put("doctorid", SharePrefUtil.getString(Conast.Doctor_ID));
		// params.put("certificatenum", cer_num);
		// params.put("cardnumber", id_num);
		params.put("sex",
				sexStr.equals(getString(R.string.add_userseek_boy)) ? "1" : "0");
		params.put("professional", positionStr);
		params.put("hospitalname", hospital);
		params.put("roomname", labStr);
		params.put("speciality", skilled);
		params.put("email", emailStr);
		params.put("roomphone", roomPhone);
		params.put("resume", resumeStr);

		MyApplication.volleyHttpClient.postWithParams(
				HttpUtil.set_doctor_detail_info, ResultInfo.class, params,
				new Response.Listener() {
					@Override
					public void onResponse(Object response) {

						Message message = new Message();
						message.obj = response;
						message.what = IResult.REGISTER;
						handler.sendMessage(message);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Message message = new Message();
						message.obj = error.getMessage();
						message.what = IResult.NET_ERROR;
						handler.sendMessage(message);
					}
				});
	}

	@Override
	protected void onResult(Message msg) {

		try {
			switch (msg.what) {
			case IResult.LOGIN:
				Login login = (Login) msg.obj;
				if (login.isSuccess()) {
					Toast.makeText(RegisterSuccessActivity.this, "登录成功",
							Toast.LENGTH_SHORT).show();
					Doctor doctor = (Doctor) login.getObj();
					finish();
				} else {
					Toast.makeText(this, login.getReason(), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			case IResult.REGISTER:
				destroyDialog();
				ResultInfo resultInfo = (ResultInfo) msg.obj;
				if (null != resultInfo) {
					if (resultInfo.getSuccess() == 1) {

						showToast("提交成功");
						saveDoctorInfo();
						if (!"1".equals(SharePrefUtil
								.getString(Conast.AUDIT_STATUS))) {
							SharePrefUtil.putString(Conast.AUDIT_STATUS, "2");
							SharePrefUtil.commit();
						}
						layout_verifing.setVisibility(View.VISIBLE);
					} else {
						showToast(resultInfo.getErrormsg());
					}
				}
				break;
			case IResult.FAILURE:
				showToast("上传头像失败");
				break;
			case IResult.UPLOAD_PIC:
				HashMap<String, Object> map_avatar = (HashMap<String, Object>) msg.obj;
				if (null != map_avatar
						&& ((Integer) map_avatar.get("success") == 1)) {
					showToast("上传头像成功");
					doctor_img.setImageBitmap(bm_avatar);
					SharePrefUtil.putString(Conast.AVATAR_UNAUDIT,
							(String) map_avatar.get("avatar"));
					SharePrefUtil.commit();
				}
				break;
			case IResult.PERSON_INFO:
				destroyDialog();
				DoctorInfo info = (DoctorInfo) msg.obj;
				if (null != info) {
					if (info.getSuccess() == 1) {
						infoEntry = info.getData();
						updateNativePref(infoEntry);
						setVeryfyState(infoEntry);
						updateDoctorView(infoEntry);// 设置个人资料
					} else {
						showToast(info.getErrormsg());
					}
				}
				break;
			case IResult.NET_ERROR:
				showToast(IMessage.NET_ERROR);
				break;
			case IResult.DATA_ERROR:
				break;
			case IResult.CLEAR_FRONT_VIEW:
				clearFrontView();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}

	/**
	 * 更新本地SharedPreferencce
	 */
	private void updateNativePref(DoctorInfoEntry doctorInfoEntry) {

		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATEHOME())) {
			SharePrefUtil.putString(Conast.PIC_CERT_POSI,
					doctorInfoEntry.getCERTIFICATEHOME());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATE())) {
			SharePrefUtil.putString(Conast.PIC_CERT_OPPSI,
					doctorInfoEntry.getCERTIFICATE());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFYHOME())) {
			SharePrefUtil.putString(Conast.PIC_LICENCE_POSITIVE,
					doctorInfoEntry.getQUALIFYHOME());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFY())) {
			SharePrefUtil.putString(Conast.PIC_LICENCE_OPPOSITE,
					doctorInfoEntry.getQUALIFY());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getCHESTCARD())) {
			SharePrefUtil.putString(Conast.PIC_BREAST_PLATE,
					doctorInfoEntry.getCHESTCARD());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getEMPLOYEECARD())) {
			SharePrefUtil.putString(Conast.PIC_WORK_PERMIT,
					doctorInfoEntry.getEMPLOYEECARD());
		}
	}

	private void setVeryfyState(DoctorInfoEntry doctorInfoEntry) {
		// if(!SharePrefUtil.getBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID))){
		if (!SharePrefUtil.getString(Conast.AUDIT_STATUS).equals(
				doctorInfoEntry.getAUDITSTATUS())) {
			if ("1".equals(doctorInfoEntry.getAUDITSTATUS())) {
				SharePrefUtil.putBoolean(Conast.VALIDATED, true);
				SharePrefUtil.putString(Conast.AUDIT_STATUS, "1");
				// SharePrefUtil.putBoolean(Conast.FLAG_FIRST_INVITE_AFTER_PERMIT+SharePrefUtil.getString(Conast.Doctor_ID),
				// true);
			} else if ("2".equals(doctorInfoEntry.getAUDITSTATUS())) {
				SharePrefUtil.putString(Conast.AUDIT_STATUS, "2");
			} else if ("3".equals(doctorInfoEntry.getAUDITSTATUS())) {
				SharePrefUtil.putString(Conast.AUDIT_STATUS, "3");
				SharePrefUtil.putString(Conast.AUDIT_REFUSE,
						doctorInfoEntry.getAUDITREFUSE());
				tv_failed_reason.setText(doctorInfoEntry.getAUDITREFUSE());
			}
			SharePrefUtil.commit();
		}
	}

	// 保存医生
	private void saveDoctorInfo() {
		SharePrefUtil.putString(Conast.SPECIALITY, tv_realskilled.getText()
				.toString().trim());
		SharePrefUtil.putString(Conast.Doctor_Professional, spinner_position
				.getSelectedItem().toString());
		SharePrefUtil.putString(Conast.HOSPITAL_NAME, tv_hospital.getText()
				.toString().trim());
		SharePrefUtil.putString(Conast.ROOM_NAME, tv_hospital_laboratory
				.getText().toString().trim());
		SharePrefUtil.commit();
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

	private byte[] photoBytes = null;
	private byte[] photoBytes_opposite = null;
	/*
	 * private byte[] licencePhotoBytes = null; private byte[]
	 * licencePhotoBytes_opposite = null; private byte[] breastPlatePhotoByte =
	 * null; private byte[] workPermitPhotoByte = null;
	 */
	private File picFile;
	private Uri photoUri;

	public void onEvent(UploadImgSuccessEvent testEvent) {
		if (null != testEvent) {
			PhotoType type = testEvent.getPhotoType();
			if (PhotoType.CER_PHOTO == type
					|| PhotoType.LICENCE_PHOTO_POSITIVE == type
					|| PhotoType.BREAST_PLATE_PHOTO == type
					|| PhotoType.WORK_PERMIT_PHOTO == type) {
				photoBytes = testEvent.getBytes();
				cur_cache_cert_posi = type;
				cur_cache_cert_oppo = null;
				photo_cert_positive.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes));
			} else if (PhotoType.CER_PHOTO_OPPOSITE == type
					|| PhotoType.LICENCE_PHOTO_OPPOSITE == type) {
				photoBytes_opposite = testEvent.getBytes();
				cur_cache_cert_oppo = type;
				photo_cert_opposite.setImageBitmap(BitmapUtil
						.bytes2Bimap(photoBytes_opposite));
			}
			// XXX （此处逻辑待修改 --已修改，可删除）
			/*
			 * else if (testEvent.getPhotoType() ==
			 * PhotoType.LICENCE_PHOTO_POSITIVE) { licencePhotoBytes =
			 * testEvent.getBytes();
			 * photo_licence_positive.setImageBitmap(BitmapUtil
			 * .bytes2Bimap(licencePhotoBytes)); } else if
			 * (testEvent.getPhotoType() == PhotoType.LICENCE_PHOTO_OPPOSITE){
			 * licencePhotoBytes_opposite = testEvent.getBytes();
			 * photo_licence_opposite
			 * .setImageBitmap(BitmapUtil.bytes2Bimap(licencePhotoBytes_opposite
			 * )); } else if (testEvent.getPhotoType() ==
			 * PhotoType.BREAST_PLATE_PHOTO){ breastPlatePhotoByte =
			 * testEvent.getBytes();
			 * photo_breast_plate.setImageBitmap(BitmapUtil
			 * .bytes2Bimap(breastPlatePhotoByte)); } else if
			 * (testEvent.getPhotoType() == PhotoType.WORK_PERMIT_PHOTO){
			 * workPermitPhotoByte = testEvent.getBytes();
			 * photo_work_permit.setImageBitmap
			 * (BitmapUtil.bytes2Bimap(workPermitPhotoByte)); }
			 */
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().removeStickyEvent(UploadImgSuccessEvent.class);
	}

	// 设置头像
	private void setDoctorImage() {
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
								// takePicture();
								basictakePicture(false);

							}
							break;
						case 1:
							basicopenGallery(false);
							// openGallery();
							break;
						}
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	/** 4.0 */
	// private void takePicture() {
	//
	// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	//
	// try {
	//
	// File uploadFileDir = new File(FileUtil.PATH,FileUtil.IMG_UPLOAD);
	// if (!uploadFileDir.exists()) {
	// uploadFileDir.mkdirs();
	// }
	//
	// String state = Environment.getExternalStorageState();
	// if (Environment.MEDIA_MOUNTED.equals(state)) {
	// picFile = new File(uploadFileDir,UUID.randomUUID().toString()+".jpeg");
	// } else {
	// picFile = new File(getFilesDir()+ FileUtil.ROOT_DIRECTORY +"/"+
	// FileUtil.IMG_UPLOAD, UUID.randomUUID().toString()+".jpeg");
	// }
	//
	// photoUri = Uri.fromFile(picFile);
	// intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
	// intent.putExtra("return-data", true);
	//
	// if(mPhotoType == PhotoType.AVATAR_IMAGE){
	// startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE); //头像需要剪切图片
	// } else{
	// startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE); //其他照片不需要剪切
	// }
	// } catch (ActivityNotFoundException e) {
	// // Log.d(TAG, "cannot take picture", e);
	// }
	// }

	/** for4.3 */
	// private void openGallery() {
	// try {
	// File pictureFileDir = new File(FileUtil.PATH, FileUtil.IMG_UPLOAD);
	// if (!pictureFileDir.exists()) {
	// pictureFileDir.mkdirs();
	// }
	// picFile = new File(pictureFileDir, UUID.randomUUID().toString()+".jpeg");
	// if (!picFile.exists()) {
	// picFile.createNewFile();
	// }
	// photoUri = Uri.fromFile(picFile);
	// Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	// photoPickerIntent.setType("image/*");
	// startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// 设置图像
	private void setImage(final byte[] bytes) {
		try {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("doctorid", SharePrefUtil
					.getString(Conast.Doctor_ID)));
			params.add(new BasicNameValuePair("ext", "jpg"));
			params.add(new BasicNameValuePair("dir", "1"));
			params.add(new BasicNameValuePair("token", SharePrefUtil
					.getString(Conast.ACCESS_TOKEN)));
			String json = HttpUtil.uploadFile(HttpUtil.URI
					+ HttpUtil.set_doctor_avatar, params, bytes);
			json = net.ememed.doctor2.util.TextUtil.substring(json, "{");
			// System.out.println("json=" + json);
			HashMap<String, Object> map = new HashMap<String, Object>();
			JSONObject obj = new JSONObject(json);
			map.put("success", obj.getInt("success"));
			map.put("errormsg", obj.getString("errormsg"));
			JSONObject data_obj = obj.getJSONObject("data");
			if (null != data_obj) {
				String avatar = data_obj.getString("AVATAR");
				map.put("avatar", avatar);
			}
			sendMessage(IResult.UPLOAD_PIC, map);
		} catch (Exception e) {
			e.printStackTrace();
			sendMessage(IResult.FAILURE, null);
		}
	}

	/**
	 * 从服务器获取医生相关信息
	 */
	private void getPersonInfo() {
		if (NetWorkUtils.detect(RegisterSuccessActivity.this)) {
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

	/**
	 * 更新界面
	 * 
	 * @param doctorInfoEntry
	 */
	private void updateDoctorView(DoctorInfoEntry doctorInfoEntry) {
		// XXX 测试完毕记得删除
		// doctorInfoEntry.setAUDITSTATUS("2");
		// doctorInfoEntry.setAUDITREFUSE("普卡二批看我恶评开通平台可破空调人口论坛个卡通人论坛卡萨丁联发科无色了刚看完；来干嘛加我开始了开发");
		// XXX

		if (isLoadinfo == false) {
			spinner_sex.setSelection(0);
		} else {
			if (!TextUtils.isEmpty(doctorInfoEntry.getSEX())) {
				if ("1".equals(doctorInfoEntry.getSEX())) {
					spinner_sex.setSelection(2);
				} else if ("0".equals(doctorInfoEntry.getSEX())) {
					spinner_sex.setSelection(1);
				} else {
					spinner_sex.setSelection(0);
				}
			}
		}

		if (!TextUtils.isEmpty(doctorInfoEntry.getEMAIL())) {
			tv_email.setText(doctorInfoEntry.getEMAIL());
		}
		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATENUM())) {
			tv_realcert_number.setText(doctorInfoEntry.getCERTIFICATENUM());
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
		if (!TextUtils.isEmpty(doctorInfoEntry.getRESUME())) {// 预留详情字段
			tv_detail_info.setText(doctorInfoEntry.getRESUME());
		}

		if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR_AUDITSTATUS())) { // 头像
			// 头像通过审核，则显示正式头像
			if ("1".equals(doctorInfoEntry.getAVATAR_AUDITSTATUS())) {
				if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR())) {
					imageLoader.displayImage(doctorInfoEntry.getAVATAR(),
							doctor_img, Util.getOptions_big_avatar());
					SharePrefUtil.putString(Conast.AVATAR,
							doctorInfoEntry.getAVATAR());
					SharePrefUtil.commit();
				}
			} else { // 头像未通过审核，则显示待审核的头像
				if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR_UNAUDIT())) {
					imageLoader.displayImage(
							doctorInfoEntry.getAVATAR_UNAUDIT(), doctor_img,
							Util.getOptions_big_avatar());
					SharePrefUtil.putString(Conast.AVATAR_UNAUDIT,
							doctorInfoEntry.getAVATAR_UNAUDIT());
					SharePrefUtil.commit();
				}
			}
		}

		if (!TextUtils.isEmpty(doctorInfoEntry.getPROFESSIONAL())) {
			for (int i = 0; i < spinner_position.getCount(); i++) {
				if (doctorInfoEntry.getPROFESSIONAL().equals(
						spinner_position.getItemAtPosition(i).toString())) {
					spinner_position.setSelection(i);
					break;
				}
			}
		}

		// 显示哪种证件
		if (isLoadinfo == false) {
			spinner_certificate_type.setSelection(0);
			setCertificateLayout(0);
		} else {
			judgeCertificateType(doctorInfoEntry);
		}

		// int type1 = getChoiceCertificateaType(); //1:医生执业证书，2：医师资格证，3：胸牌，
		// 4：工作证
		// if (View.VISIBLE == ll_realcert_positive.getVisibility()){ //执业证书正面
		// if(1 == type1 &&
		// !TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATEHOME())){
		// imageLoader.displayImage(doctorInfoEntry.getCERTIFICATEHOME(),
		// photo_cert_positive, Util.getOptions_big_avatar());
		// } else if (2 == type1 &&
		// !TextUtils.isEmpty(doctorInfoEntry.getQUALIFYHOME())){
		// imageLoader.displayImage(doctorInfoEntry.getQUALIFYHOME(),
		// photo_cert_positive, Util.getOptions_big_avatar());
		// } else if (3 == type1 &&
		// !TextUtils.isEmpty(doctorInfoEntry.getCHESTCARD())){
		// imageLoader.displayImage(doctorInfoEntry.getCHESTCARD(),
		// photo_cert_positive, Util.getOptions_big_avatar());
		// } else if (4 == type1 &&
		// !TextUtils.isEmpty(doctorInfoEntry.getEMPLOYEECARD())){
		// imageLoader.displayImage(doctorInfoEntry.getEMPLOYEECARD(),
		// photo_cert_positive, Util.getOptions_big_avatar());
		// }
		// }
		// if (View.VISIBLE == ll_realcert_opposite.getVisibility()){
		// if(1 == type1 &&
		// !TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATE())) { //执业证书反面
		// imageLoader.displayImage(doctorInfoEntry.getCERTIFICATE(),
		// photo_cert_opposite, Util.getOptions_big_avatar());
		// } else if(2 == type1 &&
		// !TextUtils.isEmpty(doctorInfoEntry.getQUALIFY())) { //执业证书反面
		// imageLoader.displayImage(doctorInfoEntry.getQUALIFY(),
		// photo_cert_opposite, Util.getOptions_big_avatar());
		// }
		// }

		// XXX 此处暂时屏蔽，等待逻辑修改(已修改，可删除)
		/*
		 * if(View.VISIBLE == ll_licence_positive.getVisibility() &&
		 * (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFYHOME()))){ //医师资格证首页
		 * imageLoader.displayImage(doctorInfoEntry.getQUALIFYHOME(),
		 * photo_licence_positive, Util.getOptions_big_avatar()); }
		 * if(View.VISIBLE == ll_licence_oppsite.getVisibility() &&
		 * (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFY()))){ //医师资格证次页
		 * imageLoader.displayImage(doctorInfoEntry.getQUALIFY(),
		 * photo_licence_opposite, Util.getOptions_big_avatar()); }
		 * if(View.VISIBLE == ll_breast_plate.getVisibility() &&
		 * (!TextUtils.isEmpty(doctorInfoEntry.getCHESTCARD()))){ //胸牌
		 * imageLoader.displayImage(doctorInfoEntry.getCHESTCARD(),
		 * photo_breast_plate, Util.getOptions_big_avatar()); } if(View.VISIBLE
		 * == ll_work_permit.getVisibility() &&
		 * (!TextUtils.isEmpty(doctorInfoEntry.getEMPLOYEECARD()))){ //工作证
		 * imageLoader.displayImage(doctorInfoEntry.getEMPLOYEECARD(),
		 * photo_work_permit, Util.getOptions_big_avatar()); }
		 */

		// 科室电话
		if (!TextUtils.isEmpty(doctorInfoEntry.getROOMPHONE())) {
			tv_laboratory_phone.setText(doctorInfoEntry.getROOMPHONE());
		}

		// 如果头像审核成功，则存储Conast.AVATAR
		if (!TextUtils.isEmpty(doctorInfoEntry.getAUDITSTATUS())) {
			if ("1".equals(doctorInfoEntry.getAUDITSTATUS())) {
				if (!TextUtils.isEmpty(doctorInfoEntry.getAVATAR())) {
					SharePrefUtil.putString(Conast.AVATAR,
							doctorInfoEntry.getAVATAR());
					SharePrefUtil.commit();
				}
			}
		}
	}

	/**
	 * 判断需要显示的证件类型
	 * 
	 * @param doctorInfoEntry
	 */
	private void judgeCertificateType(DoctorInfoEntry doctorInfoEntry) {
		if (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATEHOME())
				&& (!TextUtils.isEmpty(doctorInfoEntry.getCERTIFICATE()))) {
			spinner_certificate_type.setSelection(1);
			setCertificateLayout(1);
			return;
		} else if (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFYHOME())
				&& (!TextUtils.isEmpty(doctorInfoEntry.getQUALIFY()))) {
			spinner_certificate_type.setSelection(2);
			setCertificateLayout(2);
			return;
		} else if (!TextUtils.isEmpty(doctorInfoEntry.getCHESTCARD())) {
			spinner_certificate_type.setSelection(3);
			setCertificateLayout(3);
			return;
		}/*
		 * else if(!TextUtils.isEmpty(doctorInfoEntry.getEMPLOYEECARD())){
		 * spinner_certificate_type.setSelection(4); setCertificateLayout(4);
		 * return; }
		 */else {
			spinner_certificate_type.setSelection(0);
			setCertificateLayout(0);
			return;
		}
	}

	/**
	 * 去除前景（灰色覆盖层）
	 */
	private void clearFrontView() {
		linear_layout1.setVisibility(View.INVISIBLE);
		linear_layout2.setVisibility(View.INVISIBLE);
		linear_layout3.setVisibility(View.INVISIBLE);
		linear_layout4.setVisibility(View.INVISIBLE);
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
								basictakePicture(false);
							}
							break;
						case 1:
							basicopenGallery(false);
							break;
						}
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

}
