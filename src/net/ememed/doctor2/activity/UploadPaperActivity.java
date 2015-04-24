package net.ememed.doctor2.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.IResult;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.FileUtil;
import net.ememed.doctor2.util.ObtainPicturesCall;
import net.ememed.doctor2.util.PictureUtil;
import net.ememed.doctor2.widget.MenuDialog;

import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.RegisterSuccessEvent;
import de.greenrobot.event.UploadImgSuccessEvent;
import eu.janmuller.android.simplecropimage.CropImage;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UploadPaperActivity extends BasicActivity {
	private static final int REQUEST_REGISTER = 1;
	private static final int REQUEST_CODE_TAKE_PICTURE = 2;
	private static final int REQUEST_CODE_CROP_IMAGE = 3;
	private static final int REQUEST_CODE_GALLERY = 4;
	private static final int RETURN_IMAGE = 5;
	private static final String TAG = UploadPaperActivity.class.getSimpleName();
	private ObtainPicturesCall mPicturesCall = new ObtainPicturesCall(this);
	private File picFile;
	private Uri photoUri;
	private ImageView iv_thumb;
	private boolean hasImage;
	private TextView tv_register_tips_1;
	private TextView tv_register_tips_2;
	private TextView tv_register_tips_3;
	private String pathLaest;

	private volatile PhotoType mPhotoType = PhotoType.CER_PHOTO;
	private Button btn_take_photo;

	public enum PhotoType {
		/** 执业证 */
		CER_PHOTO,
		/** 身份证 */
		CARD_PHOTO, 
		/** 执业证背面 */
		CER_PHOTO_OPPOSITE,
		/** 医师资格证正面 */
		LICENCE_PHOTO_POSITIVE,
		/** 医师资格证背面 */
		LICENCE_PHOTO_OPPOSITE,
		/** 胸牌*/
		BREAST_PLATE_PHOTO,
		/** 工作证*/
		WORK_PERMIT_PHOTO,
		
		/** 头像*/
		AVATAR_IMAGE,
	}

	@Override
	protected void onCreateContent(Bundle savedInstanceState) {
		super.onCreateContent(savedInstanceState);
		setContentView(R.layout.register_upload_file);
		mPhotoType = (PhotoType) getIntent().getSerializableExtra(
				"take_photo_type");
		EventBus.getDefault().registerSticky(this, UploadImgSuccessEvent.class);
	}

	@Override
	protected void setupView() {
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText(getString(R.string.title_register_verify));
		iv_thumb = (ImageView) findViewById(R.id.iv_thumb);
		tv_register_tips_1 = (TextView) findViewById(R.id.register_camera_tips_1);
		tv_register_tips_2 = (TextView) findViewById(R.id.register_camera_tips_2);
		tv_register_tips_3 = (TextView) findViewById(R.id.register_camera_tips_3);
		btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
		
		/** 显示的界面内容默认是给医生执业证书正面拍照的，给其他证件拍照时，改变界面文字*/
		if (mPhotoType == PhotoType.CARD_PHOTO) {			//身份证拍照
			tv_register_tips_1
					.setText(getString(R.string.register_camera_tips_idcard_1));
			tv_register_tips_2
					.setText(getString(R.string.register_camera_tips_idcard_2));
			tv_register_tips_3
					.setText(getString(R.string.register_camera_tips_idcard_3));
			btn_take_photo.setText(getString(R.string.register_camera_idcard));
			iv_thumb.setImageResource(R.drawable.verify_idcard_file);

		} else if(mPhotoType == PhotoType.BREAST_PLATE_PHOTO){	//胸牌拍照
			tv_register_tips_1
					.setText(getString(R.string.register_camera_tips_breast_plate_1));
			tv_register_tips_2
					.setText(getString(R.string.register_camera_tips_breast_plate_2));
			tv_register_tips_3
					.setText(getString(R.string.register_camera_tips_breast_plate_3));
			btn_take_photo.setText(getString(R.string.register_camera_breast_plate));
		} else if(mPhotoType == PhotoType.WORK_PERMIT_PHOTO){	//工作证拍照
			tv_register_tips_1
					.setText(getString(R.string.register_camera_tips_work_permit_1));
			tv_register_tips_2
					.setText(getString(R.string.register_camera_tips_work_permit_2));
			tv_register_tips_3
					.setText(getString(R.string.register_camera_tips_work_permit_3));
			btn_take_photo.setText(getString(R.string.register_camera_work_permit));
		} else if (mPhotoType == PhotoType.CER_PHOTO_OPPOSITE) { 	//执业证书反面拍照
			iv_thumb.setImageResource(R.drawable.verify_cer_opposite);
		} else if (mPhotoType == PhotoType.LICENCE_PHOTO_OPPOSITE) { //医生资格证反面拍照
			iv_thumb.setImageResource(R.drawable.verify_cer_opposite);
		}
	}

	public void doClick(View view) {
		int id = view.getId();
		if (id == R.id.btn_take_photo) {
			// mPicturesCall.showAlertDialog(0);
			doPickPhotoAction();
		} else if (id == R.id.btn_back) {
			finish();
		} else {
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyUp(keyCode, event);
	}

	public void onEvent(UploadImgSuccessEvent testEvent) {
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().removeStickyEvent(UploadImgSuccessEvent.class);
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
		case REQUEST_CODE_TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				startCropImage();
			}
			break;
		case REQUEST_CODE_CROP_IMAGE:
			try {
				if (resultCode == RESULT_OK) {
					if (photoUri != null) {
						hasImage = true;
						Intent intent = new Intent(UploadPaperActivity.this,
								VerifyPaperActivity.class);
						PictureUtil.save(picFile.getAbsolutePath(), picFile, UploadPaperActivity.this);
						byte[] imgDatas = FileUtil.getBytes(picFile);
						intent.putExtra("img_data", imgDatas);
						intent.putExtra("take_photo_type", mPhotoType);
						startActivity(intent);
						finish();
					}
				} else {
					if (null != picFile && picFile.exists()) {
						picFile.delete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case REQUEST_CODE_GALLERY:
			try {
				InputStream inputStream = getContentResolver().openInputStream(
						data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(
						picFile);
				BitmapUtil.copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();
				hasImage = true;
				Intent intent = new Intent(UploadPaperActivity.this,
						VerifyPaperActivity.class);
				PictureUtil.save(picFile.getAbsolutePath(), picFile, UploadPaperActivity.this);
				byte[] imgDatas = FileUtil.getBytes(picFile);
				intent.putExtra("img_data", imgDatas);
				intent.putExtra("take_photo_type", mPhotoType);
				startActivity(intent);
				finish();
//				startCropImage();
			} catch (Exception e) {
				// Log.e(TAG, "Error while creating temp file", e);
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
			case IResult.FAILURE:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResult(msg);
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

	/** 4.0 */
	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {

			File uploadFileDir = new File(FileUtil.PATH, FileUtil.IMG_UPLOAD);
			if (!uploadFileDir.exists()) {
				uploadFileDir.mkdirs();
			}

			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				Log.e("XX", "拍照1");
				picFile = new File(uploadFileDir, UUID.randomUUID().toString()
						+ ".jpeg");
			} else {
				Log.e("XX", "拍照2");
				picFile = new File(getFilesDir() + FileUtil.ROOT_DIRECTORY
						+ "/" + FileUtil.IMG_UPLOAD, UUID.randomUUID()
						.toString() + ".jpeg");
			}
			Log.e("XX", "拍照3");
			photoUri = Uri.fromFile(picFile);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
		} catch (ActivityNotFoundException e) {
			// Log.d(TAG, "cannot take picture", e);
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

	/** for4.3 */
	private void openGallery() {
		try {
			File pictureFileDir = new File(FileUtil.PATH, FileUtil.IMG_UPLOAD);
			if (!pictureFileDir.exists()) {
				pictureFileDir.mkdirs();
			}
			picFile = new File(pictureFileDir, UUID.randomUUID().toString()
					+ ".jpeg");
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

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

}