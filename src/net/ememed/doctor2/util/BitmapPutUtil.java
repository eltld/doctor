package net.ememed.doctor2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapPutUtil {

	public static Bitmap compressImage(Bitmap image) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 20;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
		
	}

	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;

		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 480f;
		
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);
	}

	public static Bitmap comp(Bitmap image, float hh, float ww) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			int be = 1;
			be = (int) (newOpts.outWidth / ww);
			System.out.println("be=" + be);
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;
			isBm = new ByteArrayInputStream(baos.toByteArray());
			bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
			System.out.println(bitmap.getWidth() + ":" + bitmap.getHeight());
			return compressImage(bitmap);
		}
		
		return null;
	}

	public static int convertpxTodip(Context context, int px) {
		float scale = context.getResources().getDisplayMetrics().density;

		return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));

	}

	public static int convertdipTopx(Context context, int dip) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}

	private static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.8f, 0.8f);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}
	
	
	public static Bitmap getPathBitmap(String path,Context context){
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		int number = convertdipTopx(context, 50);
		int be = 1;
		if(w>h){
			be = h/number;
		}else{
			be = w/number;
		}
		
//		float hh = 800f;
//		float ww = 480f;
//		
//		if (w > h && w > ww) {
//			be = (int) (newOpts.outWidth / ww);
//		} else if (w < h && h > hh) {
//			be = (int) (newOpts.outHeight / hh);
//		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(path, newOpts);
		return getSquareBitmap(bitmap);
	}
	
	
	public static Bitmap getSquareBitmap(Bitmap bitmap){
		
		if(bitmap==null)
			return null;
		int oidHeight = bitmap.getHeight();
		int oidWidth = bitmap.getWidth();
		if(oidWidth>oidHeight){
			int difference =  (oidWidth-oidHeight)/2;
			bitmap = Bitmap.createBitmap(bitmap, difference, 0, oidHeight, oidHeight);
		}else{
			int difference =  (oidHeight-oidWidth)/2;
			bitmap = Bitmap.createBitmap(bitmap, 0, difference, oidWidth, oidWidth);
		}
		
		
		return bitmap;
	}

}
