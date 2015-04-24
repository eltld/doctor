package net.ememed.doctor2.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class BanksTable {
private final static String TABLE_CONFIG_BANKS = "config_banks";
	
	private final static String position_group = "banks_group";
	private final static String position_name = "banks_name";
	
	private DBManagerImpl db = null;
	
	public BanksTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONFIG_BANKS)) {
			createBanksTable();
		}
	}
	
	public BanksTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONFIG_BANKS)) {
			createBanksTable();
		}
	}
	
	private void createBanksTable() {
		String createSql = "create table if not exists "+TABLE_CONFIG_BANKS+" (id integer primary key autoincrement,"
				+ position_group + " varchar,"+position_name+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_CONFIG_BANKS);
	}
	
	public ArrayList<String> getDepartmentNames(String str_banks_group) {
		Cursor cursor = null;
		ArrayList<String> banksnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONFIG_BANKS + " where "+position_group+" = ?", new String[]{ str_banks_group });
			if (cursor != null) {
				banksnames = new ArrayList<String>();
				while (cursor.moveToNext()) {
					banksnames.add(cursor.getString(cursor.getColumnIndex(position_name)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return banksnames;
	}
	
	public String[] getAllBanksNames() {
		Cursor cursor = null;
		String[] banksnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONFIG_BANKS , null);
			if (cursor != null) {
				banksnames = new String[cursor.getCount()];
				while (cursor.moveToNext()) {
					banksnames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(position_name));
//					banksnames.add(cursor.getString(cursor.getColumnIndex(position_name)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return banksnames;
	}
	/**获取银行组名  过滤重复数据*/
	public ArrayList<String> getBanksGroups() {
		Cursor cursor = null;
		ArrayList<String> banksgroups = null;
		try {
			cursor = db.find(db.getConnection(), "select distinct "+position_group+" from " + TABLE_CONFIG_BANKS, null);
			if (cursor != null) {
				banksgroups = new ArrayList<String>();
				while (cursor.moveToNext()) {
					banksgroups.add(cursor.getString(cursor.getColumnIndex(position_group)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return banksgroups;
	}
	
	public void saveBanksName(String str_banks_group,String str_banks_name) {
		ContentValues values = new ContentValues();
		values.put(position_group, str_banks_group);
		values.put(position_name, str_banks_name);
		db.save(db.getConnection(), TABLE_CONFIG_BANKS, values);
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_CONFIG_BANKS, null, null);
	}
}
