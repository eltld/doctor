package net.ememed.doctor2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import net.ememed.doctor2.entity.Time;

import android.os.SystemClock;

public class TimeUtil {
	public static ArrayList<String> getTimes(ArrayList<String> times,
			String time) {
		ArrayList<Time> ts = new ArrayList<Time>();
		for (String st : times) {
			Time t = getTime(st);
			ts.add(t);
		}
		Time t1 = getTime(time);
		addTime(t1, ts);
		Collections.sort(ts, new Comparator<Time>() {

			@Override
			public int compare(Time o1, Time o2) {
				return o1.startTime - o2.startTime;
			}
		});
		times.clear();
		for (Time t : ts) {
			times.add(parseTime(t.startTime) + "-" + parseTime(t.endTime));
		}

		return times;
	}

	public static Time getTime(String sTime) {
		Time t = new Time();
		String[] args = sTime.split("-");
		t.startTime = replace(args[0]);
		t.endTime = replace(args[1]);
		return t;
	}

	public static int replace(String str) {
		return Integer.parseInt(str.replace(":", ""));
	}

	public static long parseDateTime2long(String dateTime) {
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");  
		Date dt2 = null;
		try {
			dt2 = sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
		return dt2.getTime() / 1000;   //得到秒数，Date类型的getTime()返回毫秒数
	}
	
	public static String parseTime(int time) {
		String st = time < 1000 ? "0" + time : "" + time;
		String hh = st.substring(0, 2);

		String mm = st.substring(2);
		return hh + ":" + mm;
	}
	/**把日期转换成 yyyy-MM-dd格式*/
	public static String parseFullDateTime2YMD(String dateTime) {
		String date = "";
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt2 = sdf.parse(dateTime);
			date = sdf.format(dt2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static String parseTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String date = sdf.format(new Date(time*1000));	
		return date;
	}

	public static String parseDateTime(long time,String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String date = sdf.format(new Date(time*1000));	
		return date;
	}
	
	public static void addTime(Time t, ArrayList<Time> ts) {
		int startTime;
		int endTime;
		int startPosi;
		int endPosi;
		Time t1 = new Time();
		if (t != null) {
			for (int i = 0; i < ts.size(); i++) {

				if (t.startTime < ts.get(i).startTime) {
					startTime = t.startTime;
					startPosi = i;
					endPosi = i;
					endTime = t.startTime;
					for (int j = i; j < ts.size(); j++) {
						if (t.endTime < ts.get(j).startTime) {
							endTime = t.endTime;

							if (i == j) {
								ts.add(t);
								return;
							} else {
								endTime = t.endTime;
								endPosi = j - 1;
								break;
							}
						} else if (t.endTime >= ts.get(j).startTime
								&& t.endTime <= ts.get(j).endTime) {
							endTime = ts.get(j).endTime;
							endPosi = j;
							break;
						} else if (t.endTime > ts.get(j).endTime) {
							if (j == ts.size() - 1) {
								endTime = t.endTime;
								endPosi = j;
								break;
							}
							continue;
						}
					}
					remove(ts, startPosi, endPosi);
					t1.startTime = startTime;
					t1.endTime = endTime;
					break;
				} else if (t.startTime >= ts.get(i).startTime
						&& t.startTime <= ts.get(i).endTime) {
					startTime = ts.get(i).startTime;
					startPosi = i;
					endPosi = i;
					endTime = ts.get(i).startTime;
					for (int j = i; j < ts.size(); j++) {
						if (t.endTime < ts.get(j).startTime) {
							endTime = t.endTime;
							endPosi = j - 1;
							break;
						}
						if (t.endTime <= ts.get(j).endTime
								&& t.endTime >= ts.get(j).startTime) {
							if (i == j) {
								return;
							}
							endTime = ts.get(j).endTime;
							endPosi = j;
							break;
						} else if (t.endTime > ts.get(j).endTime) {
							if (j == ts.size() - 1) {
								endTime = t.endTime;
								endPosi = j;
								break;
							}
							continue;
						}
					}
					remove(ts, startPosi, endPosi);
					t1.startTime = startTime;
					t1.endTime = endTime;

					break;
				}
				if (t.startTime > ts.get(i).endTime) {
					if (i == ts.size() - 1) {
						ts.add(t);
						return;
					}
					continue;
				}
			}
			ts.add(t1);
		}

	}

	public static void remove(ArrayList<Time> ts, int startRow, int endRow) {

		ArrayList<Time> times = new ArrayList<Time>();
		for (int i = startRow; i <= endRow; i++) {
			times.add(ts.get(i));
		}
		ts.removeAll(times);
	}

	/**判断是否为当天*/
	public static boolean checkIsNeedUpdate() {

		try {
			long time = net.ememed.doctor2.util.SharePrefUtil.getLong(Conast.FIRST_START_ONE_DAY_TIME);
			TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
			Calendar calendar = Calendar.getInstance(t);
			calendar.setTimeInMillis(System.currentTimeMillis());
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			if (time > 0) {
				Calendar calendar2 = Calendar.getInstance(t);
				calendar2.setTimeInMillis(time);
				int year2 = calendar2.get(Calendar.YEAR);
				int month2 = calendar2.get(Calendar.MONTH);
				int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
				if (calendar.compareTo(calendar2) > 0) {
					if (year == year2 && month == month2 && day == day2) {
						return false;
					}
					net.ememed.doctor2.util.SharePrefUtil.putLong(Conast.FIRST_START_ONE_DAY_TIME,System.currentTimeMillis());
					net.ememed.doctor2.util.SharePrefUtil.commit();
					return true;
				}
			} else {
				net.ememed.doctor2.util.SharePrefUtil.putLong(Conast.FIRST_START_ONE_DAY_TIME,System.currentTimeMillis());
				net.ememed.doctor2.util.SharePrefUtil.commit();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
