package net.ememed.doctor2.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import net.ememed.doctor2.R;
import net.ememed.doctor2.activity.CommunicationGroupCreateActivity.OnInputCallBack;
import net.ememed.doctor2.activity.UploadPaperActivity.PhotoType;
import net.ememed.doctor2.config.Constants;
import net.ememed.doctor2.util.BitmapUtil;
import net.ememed.doctor2.util.BitmapUtils;
import net.ememed.doctor2.util.FileUtil;
import net.ememed.doctor2.util.UICore;
import net.ememed.doctor2.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import eu.janmuller.android.simplecropimage.CropImage;

public class CommunicationGroupCreatePictureFragment extends Fragment implements OnInputCallBack,
		OnClickListener {

	private static final int REQUEST_CAMERA = 100;
	private static final int REQUEST_GALLERY = 101;
	private static final int REQUEST_CROP = 102;

	private static final String TIPS = "为您的群上传一个<font color='#ff5c03'>心仪的头像</font>吧！";
	private ImageView iv_portrait;
	private TextView tv_name;
	private Button btn_upload;
	private String mPath;

	public Uri photoUri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.fragment_communication_group_create_picture, null);
		tv_name = (TextView) rootView.findViewById(R.id.tv_name);
		btn_upload = (Button) rootView.findViewById(R.id.btn_upload);
		iv_portrait = (ImageView) rootView.findViewById(R.id.iv_portrait);
		btn_upload.setOnClickListener(this);
		tv_name.setText(Html.fromHtml(TIPS));
		return rootView;
	}

	@Override
	public String getInput() {
		return mPath;
	}

	@Override
	public boolean hasInput() {
		return TextUtils.isEmpty(mPath) ? false : true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_upload:
			showPickPhotoDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_CAMERA:
			startCropImage(photoUri);
			break;
		case REQUEST_GALLERY:
			Uri mediaUri = data.getData();
			Uri pathUri = BitmapUtils.getUri(getActivity(), mediaUri);
			startCropImage(pathUri);
			break;
		case REQUEST_CROP:
			mPath = data.getStringExtra(CropImage.IMAGE_PATH);
			if (mPath == null) {
				return;
			}
			Bitmap bitmap = BitmapFactory.decodeFile(mPath);
			iv_portrait.setImageBitmap(bitmap);
			break;
		default:
			break;
		}
	}

	private void startCropImage(Uri uri) {
		Intent intent = new Intent(getActivity(), CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, uri.getPath());
		intent.putExtra(CropImage.SCALE, true);
		intent.putExtra("outputX", 200);// 图片宽度
		intent.putExtra("outputY", 200);// 图片高度
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		startActivityForResult(intent, REQUEST_CROP);
	}

	/**
	 * 显示拍照提示框
	 */
	private void showPickPhotoDialog() {
		final String[] items = { getString(R.string.apply_hosp_photo),
				getString(R.string.apply_hosp_xiangce) };

		new AlertDialog.Builder(getActivity()).setTitle(R.string.choose_photo_title)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							if (Util.isExitsSdcard())
								takePicture();
							break;
						case 1:
							openGallery();
							break;
						}
					}
				}).show();
	}

	private void takePicture() {
		try {
			photoUri = Uri
					.fromFile(new File(Constants.getPictureDir(), Constants.getPictureName()));
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, REQUEST_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openGallery() {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_GALLERY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
