package net.ememed.doctor2.db;

import java.util.ArrayList;
import net.ememed.doctor2.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
/***
 * 保存银行列表
 * @author chen
 */
public class BankConfigTable {


	private final static String TABLE_BANK = "tb_bank";
	
	private final static String bank_name = "bank_name";
	
	private DBManagerImpl db = null;
	
	public BankConfigTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_BANK)) {
			createConfigTable();
		}
	}
	
//	public BankConfigTable(Context context) {
//		if (db == null) {
//			db = DBManager.get(context);
//		}
//		if (!db.isTableExits(db.getConnection(), TABLE_BANK)) {
//			createConfigTable();
//		}
//	}
	
	private void createConfigTable() {
		String createSql = "create table if not exists "+TABLE_BANK+" (id integer primary key autoincrement,"
				+ bank_name + " varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_BANK);
	}
	
	
	public String[] getAllBankNames() {
		Cursor cursor = null;
		String[] positionnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_BANK , null);
			if (cursor != null) {
				positionnames = new String[cursor.getCount()];
				while (cursor.moveToNext()) {
					positionnames[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(bank_name));
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
	
	public void savePositionName(String bank_name_result) {
		ContentValues values = new ContentValues();
		values.put(bank_name, bank_name_result);
		db.save(db.getConnection(), TABLE_BANK, values);
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_BANK, null, null);
	}

}
