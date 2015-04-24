package net.ememed.doctor2.config;

import java.io.File;
import java.util.UUID;

import net.ememed.doctor2.util.FileUtil;
import net.ememed.doctor2.util.Util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

public class Constants {

	// 图片保存路径
	public static final String DIR_UPLOAD = "upload";
	public static final String APP_NAME = "ememeddoctor";
	public static final String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory()
			+ File.separator + APP_NAME + File.separator + DIR_UPLOAD;

	public static final int TICKER_CHECK_CODE = 90;

	/**
	 * 获取图片保存目录
	 * 
	 * @return
	 */
	public static String getPictureDir() {
//		if (!Util.isExitsSdcard())
//			return activity.getFilesDir() + File.separator + APP_NAME + File.separator + DIR_UPLOAD;

		File destDir = new File(DEFAULT_SAVE_IMAGE_PATH);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return DEFAULT_SAVE_IMAGE_PATH;
	}

	public static String getPictureName() {
		return UUID.randomUUID().toString() + ".jpg";
	}
}
