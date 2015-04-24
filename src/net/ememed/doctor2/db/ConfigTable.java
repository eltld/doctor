package net.ememed.doctor2.db;

import java.util.ArrayList;
import java.util.List;
import net.ememed.doctor2.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * 获取科室
 * @author chen
 */
public class ConfigTable {

	private final static String TABLE_CONFIG_DEPARTMENTS = "config_departments";
	
	private final static String department_group = "department_group";
	private final static String department_name = "department_name";
	
	private DBManagerImpl db = null;
	
	public ConfigTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONFIG_DEPARTMENTS)) {
			createConfigTable();
		}
	}
	
	public ConfigTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONFIG_DEPARTMENTS)) {
			createConfigTable();
		}
	}
	
	private void createConfigTable() {
		String createSql = "create table if not exists "+TABLE_CONFIG_DEPARTMENTS+" (id integer primary key autoincrement,"
				+ department_group + " varchar,"+department_name+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_CONFIG_DEPARTMENTS);
	}
	
	public ArrayList<String> getDepartmentNames(String str_department_group) {
		Cursor cursor = null;
		ArrayList<String> departmentnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONFIG_DEPARTMENTS + " where "+department_group+" = ?", new String[]{ str_department_group });
			if (cursor != null) {
				departmentnames = new ArrayList<String>();
				while (cursor.moveToNext()) {
					departmentnames.add(cursor.getString(cursor.getColumnIndex(department_name)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return departmentnames;
	}
	
	/**获取科室名  过滤重复数据*/
	public ArrayList<String> getDepartmentGroups() {
		Cursor cursor = null;
		ArrayList<String> departmentgroups = null;
		try {
			cursor = db.find(db.getConnection(), "select distinct "+department_group+" from " + TABLE_CONFIG_DEPARTMENTS, null);
			if (cursor != null) {
				departmentgroups = new ArrayList<String>();
				while (cursor.moveToNext()) {
					departmentgroups.add(cursor.getString(cursor.getColumnIndex(department_group)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return departmentgroups;
	}
	
	public void saveDepartment(String str_department_group,String str_department_name) {
		ContentValues values = new ContentValues();
		values.put(department_group, str_department_group);
		values.put(department_name, str_department_name);
		db.save(db.getConnection(), TABLE_CONFIG_DEPARTMENTS, values);
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_CONFIG_DEPARTMENTS, null, null);
	}
}
