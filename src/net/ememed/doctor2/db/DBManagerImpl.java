package net.ememed.doctor2.db;

import java.util.List;
import net.ememed.doctor2.R;

import android.content.ContentValues;
import android.database.Cursor;
/**
 * 
 * @author chen
 *
 */
public interface DBManagerImpl {
	public boolean save(Object db, String tableName, ContentValues values);

	public boolean update(Object db, String table, ContentValues values,
			String whereClause, String[] whereArgs);

	public boolean deleteTable(Object dbobj, String tableName);

	public boolean delete(Object db, String table, String deleteSql,
			String obj[]);

	public Cursor find(Object db, String findSql, String obj[]);

	public boolean execSQL(Object db, String sql);

	public boolean creatTable(Object db, String createTableSql, String tablename);

	public void closeConnection();

	public Object getConnection();

	public boolean isTableExits(Object dbobj, String tablename);
	
	public boolean saveAll(Object db, String tableName, List<ContentValues> values);
	public boolean isColumnExits(Object db, String tableName, String ColumnName);
	public void addColumn(Object db, String tableName, String ColumnName,String type);
}
