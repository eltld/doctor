package net.ememed.doctor2.db;

import java.util.ArrayList;
import net.ememed.doctor2.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PositionConfigTable {


	private final static String TABLE_CONFIG_POSITION = "config_position";
	
	private final static String position_group = "position_group";
	private final static String position_name = "position_name";
	
	private DBManagerImpl db = null;
	
	public PositionConfigTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONFIG_POSITION)) {
			createConfigTable();
		}
	}
	
	public PositionConfigTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONFIG_POSITION)) {
			createConfigTable();
		}
	}
	
	private void createConfigTable() {
		String createSql = "create table if not exists "+TABLE_CONFIG_POSITION+" (id integer primary key autoincrement,"
				+ position_group + " varchar,"+position_name+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_CONFIG_POSITION);
	}
	
	public ArrayList<String> getDepartmentNames(String str_position_group) {
		Cursor cursor = null;
		ArrayList<String> positionnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONFIG_POSITION + " where "+position_group+" = ?", new String[]{ str_position_group });
			if (cursor != null) {
				positionnames = new ArrayList<String>();
				while (cursor.moveToNext()) {
					positionnames.add(cursor.getString(cursor.getColumnIndex(position_name)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return positionnames;
	}
	
	public String[] getAllPositionNames() {
		Cursor cursor = null;
		String[] positionnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONFIG_POSITION , null);
			if (cursor != null) {
				positionnames = new String[cursor.getCount()];
				while (cursor.moveToNext()) {
					positionnames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(position_name));
//					positionnames.add(cursor.getString(cursor.getColumnIndex(position_name)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return positionnames;
	}
	/**获取职称组名  过滤重复数据*/
	public ArrayList<String> getPositionGroups() {
		Cursor cursor = null;
		ArrayList<String> positiongroups = null;
		try {
			cursor = db.find(db.getConnection(), "select distinct "+position_group+" from " + TABLE_CONFIG_POSITION, null);
			if (cursor != null) {
				positiongroups = new ArrayList<String>();
				while (cursor.moveToNext()) {
					positiongroups.add(cursor.getString(cursor.getColumnIndex(position_group)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return positiongroups;
	}
	
	public void savePositionName(String str_position_group,String str_position_name) {
		ContentValues values = new ContentValues();
		values.put(position_group, str_position_group);
		values.put(position_name, str_position_name);
		db.save(db.getConnection(), TABLE_CONFIG_POSITION, values);
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_CONFIG_POSITION, null, null);
	}

}
