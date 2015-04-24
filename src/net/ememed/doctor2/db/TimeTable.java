package net.ememed.doctor2.db;

import java.util.Calendar;
import java.util.TimeZone;
import net.ememed.doctor2.R;

import android.database.Cursor;

/**
 * 
 * @author chen
 * 
 */
public class TimeTable {
	
	private static final String TAB_TIME = "timetab";// 表名
	private static final String COL_TIME = "first_time";// 时间
	private static final String COL_NUM = "first_number";// 次数   每天前三次显示拍照提示
	private static TimeTable timeDALEx = null;

	private TimeTable() {
		
	}

	/**
	 * 获取实例
	 * @return
	 */
	public static TimeTable get() {
		if (null == timeDALEx) {
			timeDALEx = new TimeTable();
			DBManager db = DBManager.get();
			db.creatTable(db.getConnection(), "CREATE TABLE IF NOT EXISTS "
					+ TAB_TIME + " (id integer primary key autoincrement, "+COL_NUM + " integer," + COL_TIME + " LONG )");
		}
		return timeDALEx;
	}

	/**
	 * 获得时间
	 * 
	 * @param type
	 * @return
	 */
	public long getFirstTime() {
		long downloadlength = 0;
		try {
			DBManager db = DBManager.get();
			Cursor cursor = db.find(db.getConnection(), "select " + COL_TIME + " from " + TAB_TIME, null);
			if (null != cursor) {
				if (cursor.moveToFirst())
					downloadlength = cursor.getLong(cursor.getColumnIndex(COL_TIME));
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return downloadlength;
	}
	/**
	 * 获得拍照多少次
	 * @return
	 */
	public int getPhotoNum() {
		if (check()) {
			savePhotoNum(0);
		} 
		int photoNmu = 0;
		try {
			DBManager db = DBManager.get();
			Cursor cursor = db.find(db.getConnection(), "select " + COL_NUM + " from " + TAB_TIME, null);
			if (null != cursor) {
				if (cursor.moveToFirst())
					photoNmu = cursor.getInt(cursor.getColumnIndex(COL_NUM));
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return photoNmu;
	}
	

	/**
	 * 保存拍照次数
	 * 
	 * @param type
	 * @param firstTime
	 * @return
	 */
	public boolean savePhotoNum(int num) {
		DBManager db = DBManager.get();
		db.getConnection().beginTransaction();
		Cursor cursor = db.find(db.getConnection(), "select * from " + TAB_TIME, null);
		
		try {
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					db.getConnection().execSQL("update " + TAB_TIME + " set " + COL_NUM + "=?",
							new Object[] { num });
					db.getConnection().setTransactionSuccessful();
				} else {
					db.getConnection().execSQL("insert into " + TAB_TIME + "(" + COL_NUM + " ) values(?)",
							new Object[] { num });
					db.getConnection().setTransactionSuccessful();
				}
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			cursor.close();
			db.getConnection().endTransaction();
			db = null;
		}
		return true;
	}

	/**
	 * 保存
	 * 
	 * @param type
	 * @param firstTime
	 * @return
	 */
	private boolean save(long firstTime) {
		DBManager db = DBManager.get();
		db.getConnection().beginTransaction();
		try {
			db.getConnection().execSQL("insert into " + TAB_TIME + "(" + COL_TIME + " ) values(?)",
					new Object[] { firstTime });
			db.getConnection().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			db.getConnection().endTransaction();
			db = null;
		}
		return true;
	}

	/**
	 * 更新
	 * 
	 * @param type
	 * @param firstTime
	 * @return
	 */
	private boolean update(long firstTime) {
		DBManager db = DBManager.get();
		db.getConnection().beginTransaction();
		try {
			db.getConnection().execSQL("update " + TAB_TIME + " set " + COL_TIME + "=?",
							new Object[] { firstTime });
			db.getConnection().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			db.getConnection().endTransaction();
			db = null;
		}
		return true;
	}

	/**
	 * 保存或更新
	 * 
	 * @param type
	 * @param firstTime
	 * @return
	 */
	public boolean saveOrUpdate(long firstTime) {
		DBManager db = DBManager.get();
		Cursor cursor = db.find(db.getConnection(), "select * from " + TAB_TIME, null);
		try {
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					update(firstTime);
				} else {
					save(firstTime);
				}
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			cursor.close();
			db = null;
		}
		return true;
	}

	/**
	 * 删除字段
	 * 
	 * @param type
	 */
	public boolean delete() {
		DBManager db = DBManager.get();
		return db.delete(db.getConnection(), TAB_TIME, null, null);
	}

	/**
	 * 检测是否需更新  ||  是每天第一次启动
	 * 
	 * @param type
	 * @return
	 */
	public boolean check() {

		try {
			long time = getFirstTime();
			TimeZone t = TimeZone.getTimeZone("GMT+08:00");// 获取东8区TimeZone
			Calendar calendar = Calendar.getInstance(t);
			calendar.setTimeInMillis(System.currentTimeMillis());
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
//			int hour = calendar.get(Calendar.HOUR_OF_DAY);
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
					saveOrUpdate(calendar.getTimeInMillis());
					return true;
				}
			} else {
				saveOrUpdate(calendar.getTimeInMillis());
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
