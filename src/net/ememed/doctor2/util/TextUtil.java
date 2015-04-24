package net.ememed.doctor2.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ememed.doctor2.R;

import android.content.Context;
import android.text.TextUtils;

public class TextUtil {

	public static Context ctx_for_getResString;

	public static void setCtxForGetResString(Context ctx) {
		ctx_for_getResString = ctx;
	}

	/**
	 * 获取res字符串资源
	 * 
	 * @param ctx
	 * @param str
	 * @return
	 */
	public static String R(String str) {
		if (ctx_for_getResString == null)
			return str;
		String rs = "";
		int i = ctx_for_getResString.getResources().getIdentifier(str,
				"string", ctx_for_getResString.getPackageName());
		rs = ctx_for_getResString.getResources().getString(i);
		return rs;
	}

	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String htmlEncode(String s) {
		StringBuilder sb = new StringBuilder();
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;"); //$NON-NLS-1$ 
				break;
			case '>':
				sb.append("&gt;"); //$NON-NLS-1$ 
				break;
			case '&':
				sb.append("&amp;"); //$NON-NLS-1$ 
				break;
			case '\'':
				sb.append("&apos;"); //$NON-NLS-1$ 
				break;
			case '"':
				sb.append("&quot;"); //$NON-NLS-1$ 
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 检测英文，数字，下划线
	 * 
	 * @param Account
	 * @return
	 */
	public static boolean checkLoginAccount(String account) {
		boolean valid = true;
		if (TextUtils.isEmpty(account))
			return false;
		for (int i = 0; i < account.length(); i++) {
			char ch = account.charAt(i);
			if (Character.isLetterOrDigit(ch) || ch == '_') {
				continue;
			} else {
				valid = false;
				break;
			}
		}
		return valid;
	}

	public static boolean isValidEmail(String email) {
		if (TextUtils.isEmpty(email))
			return false;
		return email
				.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
	}

	public static boolean notEmpty(String str) {
		if (str != null && !str.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public static String subString(String str) {
		String string = null;
		try {
			string = str.substring(str.indexOf("{"));
		} catch (Exception e) {
			string = "";
		}
		return string;
	}

	public static String substring(String content, String ch) {
		if (content != null) {
			try {
				return content.substring(content.indexOf(ch));
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	public static String getEnBycn(String dayOfWeek) {
		String day = null;
		if ("星期一".equals(dayOfWeek)) {
			day = "monday";
		} else if ("星期二".equals(dayOfWeek)) {
			day = "tuesday";
		} else if ("星期三".equals(dayOfWeek)) {
			day = "wednesday";
		} else if ("星期四".equals(dayOfWeek)) {
			day = "thursday";
		} else if ("星期五".equals(dayOfWeek)) {
			day = "friday";
		} else if ("星期六".equals(dayOfWeek)) {
			day = "saturday";
		} else if ("星期日".equals(dayOfWeek)) {
			day = "sunday";
		}
		return day;
	}

	/** 验证价格是否合法 */
	public static boolean isPriceIllegal(String price) {
		if (TextUtils.isEmpty(price)) {
			return false;
		}
		Pattern pattern = Pattern
				.compile("^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");
		Matcher matcher = pattern.matcher(price);
		return !matcher.matches();
	}

	/** 只允许输入数字 */
	public static boolean isNumber(String number) {
		if (TextUtils.isEmpty(number)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher(number);
		return !matcher.matches();
	}

	/**
	 * 替换字符串 例如 “李小明”替换成 “**明”
	 * */
	public static String replaceText2Pwd(String name) {
		if (TextUtils.isEmpty(name)) {
			return "";
		}
		StringBuffer point = new StringBuffer();
		for (int i = 0; i < name.length() - 1; i++) {
			point.append("*");
		}
		return name.replace(name.substring(0, name.length() - 1), point);
	}
	/**
	 * 输入中文或者英文
	 * @param name
	 * @return
	 */
	public static boolean isNotName(String name){
		String regexStr = "[\u4E00-\u9FA5]{2,15}|[a-zA-Z]{2,50}";
		Pattern p = Pattern.compile(regexStr);  
		Matcher m = p.matcher(name);  
		return !m.matches();
		
	}
	/**
	 * 判断字符串是否为电话号码
	 */
	public static boolean isMobileNumber(String number){
		String regexStr = "^1[34578][0-9]\\d{8}$";
		Pattern p = Pattern.compile(regexStr);  
		Matcher m = p.matcher(number);  
		return m.matches();
		
	}
}
