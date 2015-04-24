package net.ememed.doctor2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import net.ememed.doctor2.R;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;


public class BitmapUtil {
	public static Bitmap getBitmap(InputStream is) {
		Bitmap bm = null;
		if (is != null) {		
			bm = BitmapFactory.decodeStream(is);
		}
		return bm;
	}

	public static Bitmap getBitmap(InputStream is, int width, int height) {
		Bitmap bm = null;
		Bitmap bitmap = getBitmap(is);
		if (bitmap != null) {
			int bmpWidth = bitmap.getWidth();
			int bmpHeight = bitmap.getHeight();
			// 缩放图片的尺寸
			float scaleWidth = (float) width / bmpWidth; // 按固定大小缩放 sWidth
															// 写多大就多大
			float scaleHeight = (float) height / bmpHeight; //
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 发生缩放后的Bitmap对象
			bm = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix,
					false);
			bitmap.recycle();
		}
		return bm;
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}
	
	public static byte[] BitmapTobytes(Bitmap bitmap) {
   	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
   	 bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        return baos.toByteArray();
   }
   
	/**
	 * 通过Uri获取位图
	 */
	public static Bitmap getBitmapByUri(Context context, Uri uri) {
		
		Bitmap bitmap = null;
		if(uri==null){
			return null;
		}
		ContentResolver cr = context.getContentResolver();
		try {
			Bitmap bm = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			bm = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
			if (bm != null) {
				bitmap = BitmapUtil.imageZoom(bm);
			}
		} catch (FileNotFoundException e) {
			return null;
		}
		
		return bitmap;
	}
	
	public static Bitmap imageZoom(Bitmap bitMap) {
		Bitmap bitmap = null;
		if (bitMap != null) {
			// 图片允许最大空间 单位：KB
			double maxSize = 100.00;
			// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] b = baos.toByteArray();
			// 将字节换成KB
			double mid = b.length / 1024;
			// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
			if (mid > maxSize) {
				// 获取bitmap大小 是允许最大大小的多少倍
				double i = mid / maxSize;
				// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
				// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
				bitmap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
						bitMap.getHeight() / Math.sqrt(i));
				bitMap.recycle();
			} else {
				bitmap = bitMap;
			}
		}

		return bitmap;
	}
	
	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);

		return bitmap;
	}
	
	/**
	 * 把Bitmap转Byte
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bm != null) {

			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			
		}

		return baos.toByteArray();
	}
	
	  public static Bitmap bytes2Bimap(byte[] b){
		    if(b.length!=0){
		    	return BitmapFactory.decodeByteArray(b, 0, b.length);
		    } else {
		    	return null;
		    }
	  }
	  public static Bitmap bytesToBitmap(byte[] data) {
			if (data != null) {
				ByteArrayInputStream is = new ByteArrayInputStream(data);
				try {
					BitmapFactory.Options options1 = new BitmapFactory.Options();
					options1.inPurgeable = true;
				    options1.inInputShareable = true;
				    options1.inSampleSize = 1;
				    try {
						BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options1, true);
				    }catch(Exception ex) {
				        ex.printStackTrace();
				    }
					Bitmap bitmap = (new WeakReference<Bitmap>(
							BitmapFactory.decodeStream(is, null, options1))).get();
					Bitmap temp = Bitmap.createBitmap(bitmap);
					return temp;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} 
			return null;
	}
		/**
		 * 将Drawable转化为Bitmap
		 * @param drawable
		 * @return
		 */
		public static Bitmap drawableToBitmap(Drawable drawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		}
}
