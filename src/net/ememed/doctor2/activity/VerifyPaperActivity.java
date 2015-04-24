package net.ememed.doctor2.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.UploadPaperActivity.PhotoType;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.entity.UploadVerifyInfo;
import net.ememed.doctor2.network.HttpUtil;
import net.ememed.doctor2.util.BasicUIEvent;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.NetWorkUtils;
import net.ememed.doctor2.util.SharePrefUtil;
import net.ememed.doctor2.util.UICore;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;
import de.greenrobot.event.UploadImgSuccessEvent;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**上传证书页面*/
public class VerifyPaperActivity extends BasicActivity  {
	private static final int REQUEST_REGISTER = 1;
	private LinearLayout ll_file_upload_success;
	private LinearLayout ll_uploading;
	private ProgressBar progressBar;
	private TextView tv_upload_status;
	private LinearLayout ll_upload_again;
	private Button btn_upload_again;
	private byte[] img_data;
	private boolean uploadSuccess;
	private ImageView iv_loading;
	private PhotoType mPhotoType;
	private TextView tv_uploaded_note;
	private EditText et_work_address;
	private LinearLayout ll_work_address_input;
	private boolean isSetWorkAddressSuccess = false;
	private TextView tv_uploaded_title;

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.register_verify_file);
		img_data = getIntent().getByteArrayExtra("img_data");
		mPhotoType = (PhotoType) getIntent().getSerializableExtra("take_photo_type");
		UICore.eventTask(this, this, IResult.UPLOAD_PIC, null, null);
	}

	@Override
	protected void setupView() {
		TextView tv_title = (TextView)findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.title_register_verify));
		ll_file_upload_success = (LinearLayout)findViewById(R.id.ll_file_upload_success);
		tv_uploaded_title = (TextView)findViewById(R.id.tv_uploaded_title);
		tv_uploaded_note  = (TextView)findViewById(R.id.tv_uploaded_note);
		ll_work_address_input =  (LinearLayout)findViewById(R.id.ll_work_address_input);
		et_work_address = (EditText)findViewById(R.id.et_work_address); 
		ll_uploading = (LinearLayout)findViewById(R.id.ll_uploading);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		iv_loading = (ImageView)findViewById(R.id.iv_loading);
		tv_upload_status = (TextView)findViewById(R.id.tv_upload_status);
		ll_upload_again = (LinearLayout)findViewById(R.id.ll_upload_again);
		btn_upload_again = (Button)findViewById(R.id.btn_upload_again);
		
	}

	@Override
	public void execute(int mes, Object obj) {
		super.execute(mes, obj);
		switch (mes) {
		case IResult.UPLOAD_PIC:
			if (NetWorkUtils.detect(VerifyPaperActivity.this)) {
				set_doctor_certificate(img_data,mPhotoType);
			} else {
				handler.sendEmptyMessage(IResult.NET_ERROR);
			}
			break;

		default:
			break;
		}
	}
	
	/** 1.2   新  设置医生职业证书 */
	private void set_doctor_certificate(final byte[] imageData,PhotoType local_uploadType) {
			//服务端uploadType：1为执业证书、2为身份证、3为医师资格证、4为胸牌、5为工作证
			//文件上传目录，uploadtype=1时dir=3，uploadtype=2时dir=4，uploadtype=3时dir=11，uploadtype=4时dir=12，uploadtype=5时dir=13
			//本地uploadType（local_updateType）的枚举值为：0：执业证书首页，1：身份证，2：执业证书次页，3：医师资格证首页，4：医师资格证次页，5：胸牌， 6：工作证
			/**此处需要将这三者之间的关系对应好，此处设计有点混乱，本地的uploadType和服务端的uploadType不一致。
			 * 本地把医生执业证书和医师资格证的首页次页分成了不同的PhotoType，而服务端却把其统一了，另用参数区分
			 * 首页和次页，应用程序设计应尽量与服务端保持一致，此处设计。。。戳！哎……就这样处理吧！！*/
		int[] uploadTypes = {1,2,1,3,3,4,5};	
		int[] dirs = {3,4,3,11,11,12,13};
			
			try {
				ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("doctorid", SharePrefUtil.getString(Conast.Doctor_ID)));
				params.add(new BasicNameValuePair("ext", "jpg"));
				params.add(new BasicNameValuePair("token", SharePrefUtil.getString(Conast.ACCESS_TOKEN)));
				//params.add(new BasicNameValuePair("dir", "3"));//文件上传目录，uploadtype=2时此处指定=4，uploadtype=1时此处指定=3
				int dir = dirs[local_uploadType.ordinal()];
				params.add(new BasicNameValuePair("dir", ""+dir));	//enum类型转换为int,再从数组取出对应的dir
				int uploadType = uploadTypes[local_uploadType.ordinal()];
				params.add(new BasicNameValuePair("uploadtype", ""+uploadType));//1为执业证书、2为身份证、3为医师资格证、4为胸牌、5为工作证
				
				if(PhotoType.CER_PHOTO == local_uploadType || PhotoType.LICENCE_PHOTO_POSITIVE == local_uploadType)
					params.add(new BasicNameValuePair("certificatehome","1"));
				else
					params.add(new BasicNameValuePair("certificatehome","0"));
					
				String json = HttpUtil.uploadFile(HttpUtil.URI + HttpUtil.set_doctor_certificate, params, imageData);
				json = net.ememed.doctor2.util.TextUtil.substring(json, "{");
//				System.out.println("json=" + json);
				HashMap<String, Object> map = new HashMap<String, Object>();
				JSONObject obj = new JSONObject(json);
				map.put("success", obj.getInt("success"));
				map.put("errormsg", obj.getString("errormsg"));
				JSONObject data_obj = obj.getJSONObject("data");
				if (null != data_obj) {
					map.put("cert_url", data_obj.getString("URL"));	
				}
				sendMessage(IResult.SUCCESS, map);
			} 
			catch (IOException e) {
				handler.sendEmptyMessage(IResult.NET_ERROR);
			} 
			catch (Exception e) {
				handler.sendEmptyMessage(IResult.DATA_ERROR);
				e.printStackTrace();
			}
	}
	
	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_upload_again) {
			if (uploadSuccess) {
				if (mPhotoType == PhotoType.CER_PHOTO) {
					EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
					finish();
				} else if (mPhotoType == PhotoType.CER_PHOTO_OPPOSITE){
					EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
					finish();
				} else if (mPhotoType == PhotoType.LICENCE_PHOTO_POSITIVE){
					EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
					finish();
				} else if (mPhotoType == PhotoType.LICENCE_PHOTO_OPPOSITE){
					EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
					finish();
				} else if (mPhotoType == PhotoType.BREAST_PLATE_PHOTO){
					EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
					finish();
				} else if (mPhotoType == PhotoType.WORK_PERMIT_PHOTO){
					EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
					finish();
				} else if (mPhotoType == PhotoType.CARD_PHOTO) {
					if (isSetWorkAddressSuccess) {// 身份证需要设置执业地点
						finish();
					} else {
						if (TextUtils.isEmpty(et_work_address.getText()
								.toString())) {
							showToast(getString(R.string.register_toast_set_work_address));
							return;
						}
//						set_doctor_address();
					}
				}

			} else {
				if (NetWorkUtils.detect(VerifyPaperActivity.this)) {
					iv_loading.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
					ll_file_upload_success.setVisibility(View.GONE);
					ll_uploading.setVisibility(View.VISIBLE);
					ll_upload_again.setVisibility(View.INVISIBLE);
					btn_upload_again.setVisibility(View.GONE);
					tv_upload_status.setText(getString(R.string.register_uploading_file));
					UICore.eventTask(this, this, IResult.UPLOAD_PIC, null, null);
				} else {
					handler.sendEmptyMessage(IResult.NET_ERROR);
				}
			}
		} else if (id == R.id.btn_back) {
			finish();
		} else {
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
//		EventBus.getDefault().removeStickyEvent(UploadImgSuccessEvent.class);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_REGISTER:
			if (RESULT_OK == resultCode && null != data) {
				String account = data.getStringExtra("account");
				String pwd = data.getStringExtra("pwd");
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResult(Message msg) {
		try {
			switch (msg.what) {
			case IResult.SUCCESS:
				HashMap<String, Object> map = (HashMap<String, Object>)msg.obj;
				if (null != map) {
					if ((Integer)map.get("success") == 0) {//提交失败
						EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
						finish();
						Toast.makeText(this, (String)map.get("errormsg"), Toast.LENGTH_SHORT).show();
					} else {
						
						if (mPhotoType == PhotoType.CER_PHOTO) {
							//执业证书正面提交成功
							uploadSuccess = true;
							SharePrefUtil.putString(Conast.PIC_CERT_POSI, (String)map.get("cert_url"));
							SharePrefUtil.putString(Conast.PIC_LICENCE_POSITIVE, "");
							SharePrefUtil.putString(Conast.PIC_LICENCE_OPPOSITE, "");
							SharePrefUtil.putString(Conast.PIC_BREAST_PLATE, "");
							SharePrefUtil.putString(Conast.PIC_WORK_PERMIT, "");
							SharePrefUtil.commit();
							EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
							finish();
							Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
						} else if (mPhotoType == PhotoType.CER_PHOTO_OPPOSITE){
							//执业证书反面提交成功
							uploadSuccess = true;
							SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, (String)map.get("cert_url"));
							SharePrefUtil.putString(Conast.PIC_LICENCE_POSITIVE, "");
							SharePrefUtil.putString(Conast.PIC_LICENCE_OPPOSITE, "");
							SharePrefUtil.putString(Conast.PIC_BREAST_PLATE, "");
							SharePrefUtil.putString(Conast.PIC_WORK_PERMIT, "");
							SharePrefUtil.commit();
							EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
							finish();
							Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
						} else if (mPhotoType == PhotoType.LICENCE_PHOTO_POSITIVE){
							//医师资格证首页提交成功
							uploadSuccess = true;
							SharePrefUtil.putString(Conast.PIC_LICENCE_POSITIVE, (String)map.get("cert_url"));
							SharePrefUtil.putString(Conast.PIC_CERT_POSI, "");
							SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, "");
							SharePrefUtil.putString(Conast.PIC_BREAST_PLATE, "");
							SharePrefUtil.putString(Conast.PIC_WORK_PERMIT, "");
							SharePrefUtil.commit();
							EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
							finish();
							Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
						} else if (mPhotoType == PhotoType.LICENCE_PHOTO_OPPOSITE){
							//医师资格证次页提交成功
							uploadSuccess = true;
							SharePrefUtil.putString(Conast.PIC_LICENCE_OPPOSITE, (String)map.get("cert_url"));
							SharePrefUtil.putString(Conast.PIC_CERT_POSI, "");
							SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, "");
							SharePrefUtil.putString(Conast.PIC_BREAST_PLATE, "");
							SharePrefUtil.putString(Conast.PIC_WORK_PERMIT, "");
							SharePrefUtil.commit();
							EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
							finish();
							Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
						} else if (mPhotoType == PhotoType.BREAST_PLATE_PHOTO){
							//胸牌提交成功
							uploadSuccess = true;
							SharePrefUtil.putString(Conast.PIC_BREAST_PLATE, (String)map.get("cert_url"));
							SharePrefUtil.putString(Conast.PIC_CERT_POSI, "");
							SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, "");
							SharePrefUtil.putString(Conast.PIC_LICENCE_POSITIVE, "");
							SharePrefUtil.putString(Conast.PIC_LICENCE_OPPOSITE, "");
							SharePrefUtil.putString(Conast.PIC_WORK_PERMIT, "");
							SharePrefUtil.commit();
							EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
							finish();
							Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
						} else if (mPhotoType == PhotoType.WORK_PERMIT_PHOTO){
							//工作证提交成功
							uploadSuccess = true;
							SharePrefUtil.putString(Conast.PIC_WORK_PERMIT, (String)map.get("cert_url"));
							SharePrefUtil.putString(Conast.PIC_CERT_POSI, "");
							SharePrefUtil.putString(Conast.PIC_CERT_OPPSI, "");
							SharePrefUtil.putString(Conast.PIC_LICENCE_POSITIVE, "");
							SharePrefUtil.putString(Conast.PIC_LICENCE_OPPOSITE, "");
							SharePrefUtil.putString(Conast.PIC_BREAST_PLATE, "");
							SharePrefUtil.commit();
							EventBus.getDefault().postSticky(new UploadImgSuccessEvent(img_data,mPhotoType));
							finish();
							Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
						} else if (mPhotoType == PhotoType.CARD_PHOTO){
							//身份证提交成功
							ImageView iv_back = (ImageView)findViewById(R.id.btn_back);	
							iv_back.setVisibility(View.GONE);
							uploadSuccess = true;
							ll_file_upload_success.setVisibility(View.VISIBLE);
							tv_uploaded_note.setText(getString(R.string.register_success_not_set_work_address));
							ll_work_address_input.setVisibility(View.VISIBLE);
							ll_uploading.setVisibility(View.GONE);
							ll_upload_again.setVisibility(View.VISIBLE);
							btn_upload_again.setText(getString(R.string.bt_upload_work_address));
						} 
						
					}
				}
				break;
			case IResult.STATUS:
				HashMap<String, Object> map_result = (HashMap<String, Object>) msg.obj;
				int success = (Integer) map_result.get("success");
				if(success == 0){
					Toast.makeText(this, (CharSequence) map_result.get("errormsg"), Toast.LENGTH_SHORT).show();
					return;
				} else {
					isSetWorkAddressSuccess  = true;
					ll_file_upload_success.setVisibility(View.VISIBLE);
					ll_uploading.setVisibility(View.GONE);
					ll_upload_again.setVisibility(View.VISIBLE);
					btn_upload_again.setText(getString(R.string.bt_upload_success));
					tv_uploaded_title.setText(getString(R.string.register_upload_work_address));
					tv_uploaded_note.setText(getString(R.string.register_success_assisant));
					ll_work_address_input.setVisibility(View.GONE);

					ImageView iv_back = (ImageView)findViewById(R.id.btn_back);	
					iv_back.setVisibility(View.VISIBLE);
				}
				
				break;
			case IResult.FAILURE:
				break;
			case IResult.NET_ERROR:
				iv_loading.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				ll_upload_again.setVisibility(View.VISIBLE);
				tv_upload_status.setText(getString(R.string.register_upload_failed));
				btn_upload_again.setText(getString(R.string.register_uploading_again));
				break;
			case IResult.DATA_ERROR:
				iv_loading.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				ll_upload_again.setVisibility(View.VISIBLE);
				tv_upload_status.setText(getString(R.string.register_upload_failed));
				btn_upload_again.setText(getString(R.string.register_uploading_again));
				btn_upload_again.setVisibility(View.VISIBLE);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPhotoType == PhotoType.CARD_PHOTO && uploadSuccess && !isSetWorkAddressSuccess) {
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
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
}
