package de.greenrobot.event.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.ParseException;

public class TimeTool {

	public static long getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d;
		long l = 0;
		try {
			d = sdf.parse(user_time);
			l = d.getTime();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));

		return re_StrTime;

	}
}
