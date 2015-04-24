/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ememed.doctor2.util;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.ememed.doctor2.R;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Collection of utility functions used in this package.
 */
public class Util {
	public static final int DIRECTION_LEFT = 0;
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_UP = 2;
	public static final int DIRECTION_DOWN = 3;

	private static OnClickListener sNullOnClickListener;

	private Util() {
	}

	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	public static String getVersionName(Context context) {
		String versionName = "1.0";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			return versionName;
		}
		return versionName;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// importance:
			// The relative importance level that the system places
			// on this process.
			// May be one of IMPORTANCE_FOREGROUND, IMPORTANCE_VISIBLE,
			// IMPORTANCE_SERVICE, IMPORTANCE_BACKGROUND, or IMPORTANCE_EMPTY.
			// These constants are numbered so that "more important" values are
			// always smaller than "less important" values.
			// processName:
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals("net.ememed.doctor2")
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				Logger.dout("=========> isAppOnForeground!! ");
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 判断程序是否在前台
	 * 
	 * @return
	 */
	public static boolean isTopActivity(Context context) {
		try {
			ActivityManager am = (ActivityManager) context
					.getSystemService(context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
			String packageName = "net.ememed.doctor2";
			if (null != tasksInfo && tasksInfo.size() > 0) {
				// 应用程序位于堆栈的顶层
				if (packageName.equals(tasksInfo.get(0).topActivity
						.getPackageName())) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	// Rotates the bitmap by the specified degree.
	// If a new bitmap is created, the original bitmap is recycled.
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	@SuppressWarnings("static-access")
	public static boolean canConnect(Context context) {
		try {
			// get connectivity manager
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			// check if we have active network and that network is connected, if
			// not abort
			if (manager.getActiveNetworkInfo() == null
					|| !manager.getActiveNetworkInfo().isConnected()) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// we can use the internet connection
		return true;
	}

	public static Bitmap getBitmapFromSdCard(String path) {
		try {

			BitmapFactory.Options options = new BitmapFactory.Options(); // 压缩图片
			options.inSampleSize = 5;
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			Bitmap bitmapTumb = ThumbnailUtils.extractThumbnail(bitmap, 128,
					128);
			bitmap.recycle();
			return bitmapTumb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据手机的分辨率从 px(像素)的单位转成为 dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

//	private static ImageLoadingListener animateFirstListener = null;

	/**
	 * 图片加载第一次显示监听器
	 * 
	 * @author Administrator
	 * 
	 */

//	public static ImageLoadingListener getImageLoadingListenerInstance() {
//		if (animateFirstListener == null) {
//			animateFirstListener = new Util.AnimateFirstDisplayListener();
//		}
//		return animateFirstListener;
//	}
//
//	public static class AnimateFirstDisplayListener extends
//			SimpleImageLoadingListener {
//
//		static final List<String> displayedImages = Collections
//				.synchronizedList(new LinkedList<String>());
//		@Override
//		 public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//			String message = null;
//			switch (failReason.getType()) { // 获取图片失败类型
//			case IO_ERROR: // 文件I/O错误
//				message = "Input/Output error";
//				break;
//			case DECODING_ERROR: // 解码错误
//				message = "Image can't be decoded";
//				break;
//			case NETWORK_DENIED: // 网络延迟
//				message = "Downloads are denied";
//				break;
//			case OUT_OF_MEMORY: // 内存不足
//				message = "Out Of Memory error";
//				break;
//			case UNKNOWN: // 原因不明
//				message = "Unknown error";
//				break;
//			}
//		}
//		@Override
//		public void onLoadingComplete(String imageUri, View view,
//				Bitmap loadedImage) {
//			if (loadedImage != null) {
//				ImageView imageView = (ImageView) view;
//				// 是否第一次显示
//				boolean firstDisplay = !displayedImages.contains(imageUri);
//				if (firstDisplay) {
//					// 图片淡入效果
//					FadeInBitmapDisplayer.animate(imageView, 500);
//					displayedImages.add(imageUri);
//				} else {
//					imageView.setImageBitmap(loadedImage);
//				}
//			}
//		}
//	}

	static DisplayImageOptions options_avatar = null;
	static DisplayImageOptions options_big_avatar = null;
	static DisplayImageOptions options_pic = null;
	static DisplayImageOptions options_big_pic = null;
	static DisplayImageOptions options_little_pic = null;
	static DisplayImageOptions options_bottletypes = null;
	static DisplayImageOptions options_viewpager = null;
	
	private static DisplayImageOptions options_EmptyImg;
	private static DisplayImageOptions options_cover;


	public static DisplayImageOptions getOptions_avatar() {
		if (options_avatar == null) {
			options_avatar = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.avatar_small)// 加载开始默认的图片
					.showImageForEmptyUri(R.drawable.avatar_small) // url爲空會显示该图片，自己放在drawable里面的
					.showImageOnFail(R.drawable.avatar_small) // 加载图片出现问题，会显示该图片
					.cacheInMemory(true) // 缓存用
					.cacheOnDisk(true) // 缓存用
					.bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0)) //图片圆角显示，值为整数
					.build();
		}
		return options_avatar;
	}

	public static DisplayImageOptions getOptions_pic() {

		if (options_pic == null) {
			options_pic = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.album_nophoto)
					.showImageForEmptyUri(R.drawable.album_nophoto)
					.showImageOnFail(R.drawable.album_nophoto).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0))
					.build();
		}
		return options_pic;
	}

	public static DisplayImageOptions getOptions_big_pic() {

		if (options_big_pic == null) {
			options_big_pic = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.big_error_pic)
					.showImageForEmptyUri(R.drawable.big_error_pic)
					.showImageOnFail(R.drawable.big_error_pic).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0))
					.build();
		}
		return options_big_pic;
	}

	public static DisplayImageOptions getOptions_little_pic() {

		if (options_little_pic == null) {
			options_little_pic = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.little_error_pic)
					.showImageForEmptyUri(R.drawable.little_error_pic)
					.showImageOnFail(R.drawable.little_error_pic)
					.imageScaleType(ImageScaleType.EXACTLY) 
					.cacheInMemory().cacheOnDisc()
					.bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0))
					.build();
		}
		return options_little_pic;
	}

	public static DisplayImageOptions getOptions_big_avatar() {
		if (options_big_avatar == null) {
			options_big_avatar = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.avatar_large)
					.showImageForEmptyUri(R.drawable.avatar_large)
					.showImageOnFail(R.drawable.avatar_large).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0))
					.build();
		}
		return options_big_avatar;
	}

	public static DisplayImageOptions getOptions_EmptyImg() {
		if (options_EmptyImg == null) {
			options_EmptyImg = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.transparent_none)
					.showImageForEmptyUri(R.drawable.transparent_none)
					.showImageOnFail(R.drawable.transparent_none)
					.cacheInMemory().imageScaleType(ImageScaleType.NONE)
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0))
					.build();
		}
		return options_EmptyImg;
	}
	
	public static DisplayImageOptions getOptions_for_viewPager() {

		if (options_viewpager == null) {
			options_viewpager = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.transparent_none)
					.showImageForEmptyUri(R.drawable.transparent_none)
					.showImageOnFail(R.drawable.transparent_none).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565)// 防止内存溢出的，图片太多就这这个。还有其他设置
					// .displayer(new RoundedBitmapDisplayer(0))
					.build();
		}
		return options_viewpager;
	}
	

	private static final String TAG = Util.class.getSimpleName();
	
	/**
	 * 显示基本的AlertDialog
	 * 
	 * @param context
	 * @param content
	 * @param title
	 */
	public static void showDialog(Context context, String content, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setIcon(R.drawable.icon);
		builder.setTitle(title);
		builder.setMessage(content);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// setTitle("点击了对话框上的Button1");
			}
		});
		builder.show();
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	/**
	 * 回收bitmap
	 */
	public static void imageRecycled(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled())
			bitmap.recycle();
		bitmap = null;
	}
	
	/**
	 * 保留两位小数
	 */
	public static String m2(double f) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (f == 0) {
			return "0.00";
		} else {
			return df.format(f);
		}
        
    }
	
    /**
     * 隐藏输入法
     *
     * @param context
     * @param view
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 显示输入法
     *
     * @param context
     * @param view
     */
    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

}
