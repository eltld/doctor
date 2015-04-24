package net.ememed.doctor2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.ememed.doctor2.MyApplication;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Tools {
	/**
	 * 生成空白文件文件，
	 * 
	 * @return
	 */
	public static File getBlankFile() {
		File file = null;
		try {
			FileOutputStream fileOutputStream = null;
			String filePath = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
			if (Tools.isExistSDCard()) { // 存在SC卡，
				final File photoDir = new File(Environment.getExternalStorageDirectory() + "/yimiyisheng/temp/");
				if (!photoDir.exists()) {
					photoDir.mkdirs();// 创建照片的存储目录
				}
				file = new File(photoDir, filePath);
			} else { // 不存在时，保存在手机内存中
				fileOutputStream = MyApplication.getInstance().openFileOutput(filePath, Context.MODE_WORLD_WRITEABLE);
				fileOutputStream.write(0);
				fileOutputStream.close();
				file = new File(MyApplication.getInstance().getFilesDir().getPath() + "/" + filePath);
			}
		} catch (Exception e) {
			Log.e("tools", e.getMessage(), e);
			return file;
		}
		return file;
	}
	
	/**
	 * 是否存在SD卡
	 * 
	 * @return
	 */
	public static boolean isExistSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
}
