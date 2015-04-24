package net.ememed.doctor2.finace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ememed.doctor2.MyApplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;

public class Utils {
	public final static long sdcardCacheMinSize = 1024 * 1024 * 8L; // 8M
	public final static String sdcardCacheDir = Environment
			.getExternalStorageDirectory().getPath()
			+ "/Android/data/"
			+ MyApplication.getInstance().getPackageName() + "/cache/";

	/**
	 * @param activity
	 * @param clazz
	 * @param resultCode
	 */
	public static void startActivityForResult(Activity activity, Class clazz,
			int resultCode) {
		startActivityForResult(activity, clazz, null, resultCode);
	}

	/**
	 * @param activity
	 * @param clazz
	 * @param bundle
	 * @param resultCode
	 */
	public static void startActivityForResult(Activity activity, Class clazz,
			Bundle bundle, int resultCode) {
		Intent intent = new Intent();
		intent.setClass(activity, clazz);

		if (bundle != null) {
			intent.putExtras(bundle);
		}

		activity.startActivityForResult(intent, resultCode);
	}

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
	 * @param context
	 * @param clazz
	 */
	public static void startActivity(Context context, Class clazz) {
		Intent intent = new Intent();
		intent.setClass(context, clazz);
		// 2012-05-15 如果主功能activity在task存在，
		// 将Activity之上的所有Activity结束掉.从而解决后退时出现查询框问题。
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
		// ((Activity) context).finish();

	}

	/**
	 * @param context
	 * @param clazz
	 * @param bundle
	 */
	public static void startActivity(Context context, Class clazz, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(context, clazz);
		// 2012-05-15 如果主功能activity在task存在，
		// 将Activity之上的所有Activity结束掉.从而解决后退时出现查询框问题。
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		if (bundle != null) {
			intent.putExtras(bundle);
		}

		context.startActivity(intent);
		// ((Activity) context).finish();
	}

	/**
	 * UI线程中运(延时的)
	 * 
	 * @param run
	 * @param delayMillis
	 * 
	 */
	public static void runOnUiThread(Runnable run, long delayMillis) {
		if (delayMillis < 100)
			runOnUiThread(run);
		else
			new Handler(Looper.getMainLooper()).postDelayed(run, delayMillis);
	}

	/**
	 * UI线程中运行
	 * 
	 * @param run
	 */
	public static void runOnUiThread(Runnable run) {
		new Handler(Looper.getMainLooper()).post(run);
	}

	/**
	 * 判断身份证是否输入正确
	 */
	public static boolean IDNumIsValid(String number) {// 判断身份证号是否正确
		// 定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
		Pattern idNumPattern = Pattern
				.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		// 通过Pattern获得Matcher
		Matcher idNumMatcher = idNumPattern.matcher(number);
		// 判断用户输入是否为身份证号
		if (idNumMatcher.matches()) {
			System.out.println("您的出生年月日是：");
			// 如果是，定义正则表达式提取出身份证中的出生日期
			Pattern birthDatePattern = Pattern
					.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");// 身份证上的前6位以及出生年月日
			// 通过Pattern获得Matcher
			Matcher birthDateMather = birthDatePattern.matcher(number);
			// 通过Matcher获得用户的出生年月日
			if (birthDateMather.find()) {
				String year = birthDateMather.group(1);
				String month = birthDateMather.group(2);
				String date = birthDateMather.group(3);
				// 输出用户的出生年月日
				System.out.println(year + "年" + month + "月" + date + "日");
			}
			return true;
		} else {
			// 如果不是，输出信息提示用户
			System.out.println("您输入的并不是身份证号");
			return false;
		}
	}

	public static String getBirthday(String number) {
		// 定义判别用户身份证号的正则表达式（要么是15位，要么是18位，最后一位可以为字母）
		Pattern idNumPattern = Pattern
				.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		// 通过Pattern获得Matcher
		Matcher idNumMatcher = idNumPattern.matcher(number);
		// 判断用户输入是否为身份证号
		// if(idNumMatcher.matches()){
		System.out.println("您的出生年月日是：");
		// 如果是，定义正则表达式提取出身份证中的出生日期
		Pattern birthDatePattern = Pattern
				.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");// 身份证上的前6位以及出生年月日
		// 通过Pattern获得Matcher
		Matcher birthDateMather = birthDatePattern.matcher(number);
		// 通过Matcher获得用户的出生年月日
		if (birthDateMather.find()) {
			String year = birthDateMather.group(1);
			String month = birthDateMather.group(2);
			String date = birthDateMather.group(3);
			// 输出用户的出生年月日
			System.out.println(year + "年" + month + "月" + date + "日");
			return year + "-" + month + "-" + date;
		}
		return null;
		// }else{
		// //如果不是，输出信息提示用户
		// System.out.println("您输入的并不是身份证号");
		// return false;
		// }
	}
	
	/**
	 * 处理银行卡输入时4个数字空一格
	 * @param idsExit
	 */
	public static void setEditTextListenerForInputBankCard(final EditText idsExit) {
		idsExit.addTextChangedListener(new TextWatcher(){
			
			int beforeTextLength = 0;  
		    int onTextLength = 0;  
		    boolean isChanged = false;  
  
		    int location = 0;// 记录光标的位置  
		    private char[] tempChar;  
		    private StringBuffer buffer = new StringBuffer();  
		    int konggeNumberB = 0;  
			
			@Override
			public void beforeTextChanged(CharSequence s,
					int start, int count, int after) {
				  beforeTextLength = s.length();  
		            if (buffer.length() > 0) {  
		                buffer.delete(0, buffer.length());  
		            }  
		            konggeNumberB = 0;  
		            for (int i = 0; i < s.length(); i++) {  
		                if (s.charAt(i) ==' ') {  
		                    konggeNumberB++;  
		                }  
		            }  
				
			}

			@Override
			public void onTextChanged(CharSequence s,
					int start, int before, int count) {
				onTextLength = s.length();  
		        buffer.append(s.toString());  
		        if (onTextLength == beforeTextLength || onTextLength <= 3  
		                || isChanged) {  
		            isChanged = false;  
		            return;  
		        }  
		        isChanged = true;  
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				 if (isChanged) {  
		                location = idsExit.getSelectionEnd();  
		                int index = 0;  
		                while (index < buffer.length()) {  
		                    if (buffer.charAt(index) ==' ') {  
		                        buffer.deleteCharAt(index);  
		                    } else {  
		                        index++;  
		                    }  
		                }  
  
		                index = 0;  
		                int konggeNumberC = 0;  
		                while (index < buffer.length()) {  
		                    if ((index == 4 || index == 9 || index == 14 || index == 19)) {  
		                        buffer.insert(index, ' ');  
		                        konggeNumberC++;  
		                    }  
		                    index++;  
		                }  
  
		                if (konggeNumberC > konggeNumberB) {  
		                    location += (konggeNumberC - konggeNumberB);  
		                }  
  
		                tempChar = new char[buffer.length()];  
		                buffer.getChars(0, buffer.length(), tempChar, 0);  
		                String str = buffer.toString();  
		                if (location > str.length()) {  
		                    location = str.length();  
		                } else if (location < 0) {  
		                    location = 0;  
		                }  
  
		                idsExit.setText(str);  
		                Editable etable = idsExit.getText();  
		                Selection.setSelection(etable, location);  
		                isChanged = false;  
		            }  
			}
		});
	}

}
