package net.ememed.doctor2.util;

import java.io.File;
import java.util.UUID;

import net.ememed.doctor2.MyApplication;
import net.ememed.doctor2.widget.MenuDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;

public class ObtainPicturesCall {
	public File mFilePictures, mTemporaryPictures;
	private Activity mActivity;
	public MenuDialog alertDialog;

	public static final int SELECT_CAMER = 0x12;
	public static final int PHOTORESOULT = 0x23;
	public static final int SELECT_PICTURE = 0x33;
	public static final int CAMER_AND_CROP = 0x34;
	public static final int DO_AGAIN = 11;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private int which = 0;

	public int id = 0;

	public ObtainPicturesCall(Activity activity) {
		this.mActivity = activity;
	}

	public void showAlertDialog(int id) {
		this.id = id;
		if (alertDialog == null)
			initAlertDialog();
		alertDialog.show();
	}

	public void initAlertDialog() {
		final Context dialogContext = new ContextThemeWrapper(mActivity, android.R.style.Theme_Light);
		String[] choices = new String[2];
		choices[0] = "拍照";
		choices[1] = "相册";

		MenuDialog.Builder builder = new MenuDialog.Builder(dialogContext);
		alertDialog = builder.setTitle("上传病历").setItems(choices, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				switch (which) {
				case 0:
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
						cameraPictures();
					}
					break;
				case 1:
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					intent.putExtra("return-data", true);
					mActivity.startActivityForResult(intent, 168);
					// Utils.startActivityForResult(mActivity, SelectPhotoActivity.class, 168);
					break;
				}
			}
		}).create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();

		// CharSequence[] items = { "照相", "手机相册" };
		// alertDialog = new AlertDialog.Builder(mActivity).setTitle("选择图片来源")
		// .setItems(items, new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// if (which == 0) {
		// cameraPictures();
		// } else {
		// Utils.startActivityForResult(mActivity, SelectPhotoActivity.class, 168);
		// // systemPictures();
		// }
		// }
		// }).create();
	}

	/**
	 * 照相
	 */
	public void cameraPictures() {
		try {
			which = 0;
			mFilePictures = Tools.getBlankFile();
			Uri imageFileUri = Uri.fromFile(mFilePictures); // 获取文件的Uri
			Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// final Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			// mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFilePictures));
			// mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			mIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
			// mIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFilePictures));
			mActivity.startActivityForResult(mIntent, SELECT_CAMER);
		} catch (Exception e) {
			Util.showDialog(mActivity, "请检测是否存在SD卡.", "提示");
		}
	}

	/**
	 * 手机相册
	 */
	public void systemPictures() {
		which = 1;
		Intent intent = new Intent();
		intent.setType(IMAGE_UNSPECIFIED);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		mActivity.startActivityForResult(intent, SELECT_PICTURE);
	}

	/**
	 * 调用文件管理系统，选取图片.
	 * 
	 * @param data
	 */
	public void getImagePath(Intent data) {
		Uri uri = data.getData();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = null;
		try {
			if (cursor == null)
				cursor = mActivity.managedQuery(uri, proj, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				mTemporaryPictures = new File(cursor.getString(column_index));
			} else if (uri != null) {
				File file = new File(uri.getPath());
				if (file.exists())
					mTemporaryPictures = file;
			}
			if (!isImageFile(mTemporaryPictures)) {
				throw new Exception();
			}
			mFilePictures = mTemporaryPictures;
		} catch (Exception e) {
			MyApplication.toastMakeTextLong("图片格式错误，请重新操作！");
			systemPictures();
		} finally {
			// 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
			if (cursor != null && Integer.parseInt(Build.VERSION.SDK) < 14) {
				try {
					cursor.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void doAgain() {
		if (which == 0) {
			cameraPictures();
		} else {
			systemPictures();
		}
	}

	public boolean isExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public boolean isImageFile(File file) {
		Bitmap bitmap = null;
		try {
			if (file == null || !file.exists())
				return false;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options); // 此时返回bm为空
			options.inJustDecodeBounds = false;
			options.inSampleSize = Util.computeSampleSize(options, 10, 100);
			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			if (bitmap != null)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		} finally {
			Util.imageRecycled(bitmap);
		}
	}
	
	

}
