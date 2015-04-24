package net.ememed.doctor2.db;

import java.util.ArrayList;
import java.util.List;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.NewsEntity;
import net.ememed.doctor2.entity.NewsTypeEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * 保存资讯目录
 * @author chen
 */
public class NewsTypeTable {

	private final static String TABLE_NEWS_TYPE = "tb_news_type";
	
	private final static String news_id = "news_id";
	private final static String news_title = "news_title";
	
	private DBManagerImpl db = null;
	
	public NewsTypeTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_NEWS_TYPE)) {
			createConfigTable();
		}
	}
	
	public NewsTypeTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_NEWS_TYPE)) {
			createConfigTable();
		}
	}
	
	private void createConfigTable() {
		String createSql = "create table if not exists "+TABLE_NEWS_TYPE+" (id integer primary key autoincrement,"
				+ news_id + " varchar,"+news_title+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_NEWS_TYPE);
	}
	
	public ArrayList<NewsTypeEntry> getNewsTypes() {
		Cursor cursor = null;
		ArrayList<NewsTypeEntry> newsTypelist = null;
		NewsTypeEntry entry = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_NEWS_TYPE, null);
			if (cursor != null) {
				newsTypelist = new ArrayList<NewsTypeEntry>();
				
				while (cursor.moveToNext()) {
					entry = new NewsTypeEntry();
					entry.setTITLE(cursor.getString(cursor.getColumnIndex(news_title)));
					entry.setID(cursor.getString(cursor.getColumnIndex(news_id)));
					newsTypelist.add(entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return newsTypelist;
	}
	
	
	public void saveNewsType(NewsTypeEntry enTypeEntry) {
		ContentValues values = new ContentValues();
		values.put(news_id, enTypeEntry.getID());
		values.put(news_title, enTypeEntry.getTITLE());
		db.save(db.getConnection(), TABLE_NEWS_TYPE, values);
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_NEWS_TYPE, null, null);
	}
}
