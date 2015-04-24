package net.ememed.doctor2.db;

import java.util.List;

import net.ememed.doctor2.R;
import net.ememed.doctor2.util.AppContext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库管理类
 * @author chen
 *
 */
public class DBManager extends SQLiteOpenHelper implements DBManagerImpl {

	private static String DB_NAME = "ememed_doctor.db"; // 数据库名称

	private static int DB_VERSION = 2; // 版本号  

	private SQLiteDatabase db; //
	
	private static DBManager dbManager = null;

	public static DBManager get() {
		if (null == dbManager) {
			dbManager = new DBManager(AppContext.getContext());
		}
		dbManager.checkDb();
		return dbManager;
	}
	
	public static DBManager get(Context conetxt) {
		if (null == dbManager) {
			dbManager = new DBManager(conetxt);
		}
		dbManager.checkDb();
		return dbManager;
	}
	
	 private void checkDb(){
		 if(null == db || !db.isOpen()){
			db = getWritableDatabase(); //读写方式打开数据库		
		 }
	 }
	 
	public DBManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		db = getWritableDatabase();
	}
	

	/**
	  * 添加操作
	  * @param db          数据库   提供操作者     
	  * @param insertSql   对应插入字段   如：insert into person(name,age) values(?,?)
	  * @param obj         对应值                  如： new Object[]{person.getName(),person.getAge()};
	  * @return
	  */
	public boolean save(Object dbobj, String insertSql, Object obj[]) {
		try {

			SQLiteDatabase db = (SQLiteDatabase) dbobj;
			db.execSQL(insertSql, obj);

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}
	
	/**
	  * 添加操作
	  * @param db          数据库   提供操作者     
	  * @param tableName   表名
	  * @param values      集合对象
	  * @return
	  */
	public boolean save(Object dbobj, String tableName, ContentValues values) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {

			db.insert(tableName, null, values);

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}

	/**
	  * 更新操作
	  * @param db  数据库   提供操作者     
	  * @param updateSql  对应跟新字段    如： update person set name=?,age=? where personid=?"
	  * @param obj        对应值                   如：  new Object[]{person.getName(),person.getAge(),person.getPersonid()};
	  */
	public boolean update(SQLiteDatabase db, String updateSql, Object obj[]) {
		try {
			db.execSQL(updateSql, obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			closeConnection(db);
		}
		return true;
	}
	 /**
	  * 更新操作
	  * @param db  数据库   提供操作者     
	  * @param table
	  * @param values
	  * @param whereClause
	  * @param whereArgs
	  * @return
	  */
	public boolean update(Object dbobj, String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {

			db.update(table, values, whereClause, whereArgs);

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}
	/**
	 * 更新操作,如果没有就掺入
	 * @param db  数据库   提供操作者     
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public boolean updateAndInsert(Object dbobj, String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {
			
			int update = db.update(table, values, whereClause, whereArgs);
			if (update==0) {
				db.insert(table, null, values);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}
	 /**
	  * 删除
	  * @param    db   数据库   提供操作者   
	  * @param    deleteSql   对应跟新字段    如： "personid=?"
	  * @param    obj[]       对应值                   如：   new Object[]{person.getPersonid()};
	  * @return                
	  */
	public boolean delete(Object dbobj, String table, String deleteSql,
			String obj[]) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {

			db.delete(table, deleteSql, obj);

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}
	/**
	 * 查询操作
	 * @param db         
	 * @param findSql  对应查询字段    如： select * from person limit ?,?
	 * @param obj      对应值                   如：  new String[]{String.valueOf(fristResult),String.valueOf(maxResult)}
	 * @return
	 */
	public Cursor find(Object dbobj, String findSql, String obj[]) {

		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {
			Cursor cursor = db.rawQuery(findSql, obj);
			return cursor;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public boolean execSQL(Object dbobj, String sql) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {

			db.execSQL(sql);

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	 /**
	  * 创建表  
	  * @param db    
	  * @param createTableSql      
	  * @return
	  */
	public boolean creatTable(SQLiteDatabase db, String createTableSql) {
		try {

			db.execSQL(createTableSql);

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}

	public boolean creatTable(Object dbobj, String createTableSql,
			String tablename) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {

			db.execSQL(createTableSql);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}

	public boolean deleteTable(Object dbobj, String tableName) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {
			db.execSQL("DROP TABLE IF EXISTS  " + tableName);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}
	 
	
	public boolean deleteTable2(SQLiteDatabase db, String tableName) {
		try {
			db.execSQL("DROP TABLE IF EXISTS  " + tableName);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	 /**
	 * 判断表是否存在
	 * @param tablename
	 * @return
	 */
	public boolean isTableExits(Object dbobj, String tablename) {
		// boolean result=true;//表示不存在
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {

			String str = "select count(*) xcount  from  " + tablename;
			db.rawQuery(str, null).close();
		} catch (Exception ex) {
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}
	
	public boolean isTableExits(DBManager edb, SQLiteDatabase db,
			String tablename) {
		if (null == edb)
			return false;
		try {

			String str = "select count(*) xcount  from  " + tablename;
			db.rawQuery(str, null).close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	 /***
	  * 关闭 获取SQLite数据库连接
	  * @return  SQLiteDatabase
	  */
	public SQLiteDatabase getConnection() {
		if (!db.isOpen()) {
			db = getWritableDatabase(); // 读写方式获取SQLiteDatabase
		}
		return db;
	}
	 /***
	  * 关闭 SQLite数据库连接
	  * @return
	  */
	public void closeConnection(SQLiteDatabase db) {
		try {
			if (db != null && db.isOpen())
				db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			if (db != null && db.isOpen())
				db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		String sql_language_1 = "ALTER TABLE tablename ADD COLUMN columnname varchar(15)";
//		db.execSQL(sql_language_1);		
		switch(oldVersion){
		case 1:
			db.execSQL("DROP TABLE IF EXISTS "+ ContactTable.TABLE_CONTACT);
			break;
		default:
		}
	}

	@Override
	public boolean saveAll(Object dbobj, String tableName, List<ContentValues> values) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		try {
			db.beginTransaction(); 
			for (ContentValues value : values) {
				db.insert(tableName, null, value);
			}
			db.setTransactionSuccessful(); 
	        db.endTransaction(); 
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
//			closeConnection(db);
		}
		return true;
	}

	@Override
	public boolean isColumnExits(Object dbobj, String tableName, String ColumnName) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		Cursor rawQuery = db.rawQuery("select sql from sqlite_master where tbl_name='"+tableName+"';", null);
		if(rawQuery!=null && rawQuery.moveToNext()){
			String string = rawQuery.getString(0);
			if(string.contains(" "+ColumnName+" ")){
				return true;
			}else{
				return false;
			}
		}
		
		
		return false;
	}

	@Override
	public void addColumn(Object dbobj, String tableName, String ColumnName, String type) {
		SQLiteDatabase db = (SQLiteDatabase) dbobj;
		String sql="ALTER TABLE "+tableName+" ADD COLUMN "+ColumnName+" "+type+";";
		db.execSQL(sql);
		
	}
	
	
	
}
