package net.ememed.doctor2.db;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.entity.NewsItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ConsultInfoTable {
	
	private final String TABLE_CONSULTINFO = "consult_info";
	
	private final String NEWSTYPE = "phonetypeid";
	private final String PAGE = "page";
	
	private final String ISLIST = "islist";   //1:newsList， 2:focusList
	
	private final String ID = "id";
	private final String TITLE = "title";
	private final String SUBTITLE = "subtitle";
	private final String PIC = "pic";
	private final String PICEXT1 = "picext1";
	private final String PICEXT2 = "picext2";
	private final String PICEXT3 = "picext3";
	private final String TYPE = "type";
	private final String FURL = "furl";
	private final String UPDATETIME = "updatetime";
	private final String ALLOWCOMMENT = "allowcomment";
	
	private DBManagerImpl db = null;
	
	public ConsultInfoTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONSULTINFO)) {
			createConsultInfoTable();
		}
		if(!db.isColumnExits(db.getConnection(), TABLE_CONSULTINFO, ALLOWCOMMENT)){
			db.addColumn(db.getConnection(), TABLE_CONSULTINFO, ALLOWCOMMENT, "VARCHAR");
		}
		
	}
	
	public ConsultInfoTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONSULTINFO)) {
			createConsultInfoTable();
		}
		if(!db.isColumnExits(db.getConnection(), TABLE_CONSULTINFO, ALLOWCOMMENT)){
			db.addColumn(db.getConnection(), TABLE_CONSULTINFO, ALLOWCOMMENT, "VARCHAR");
		}
	}
	
	private void createConsultInfoTable() {
		String createSql = "create table if not exists "+TABLE_CONSULTINFO+" (_id integer primary key autoincrement,"+NEWSTYPE+" varchar,"
				+PAGE+" varchar,"+ ID + " varchar,"+ISLIST+" varchar,"+TITLE+" varchar,"+SUBTITLE+" varchar,"+PIC+" varchar,"+PICEXT1+" varchar,"
				+PICEXT2+" varchar,"+PICEXT3+" varchar," + TYPE+" varchar,"+FURL+" varchar,"+UPDATETIME+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_CONSULTINFO);
	}
	
	public void saveConsultInfo(NewsItem entry, String newsType, String page, boolean isList) {
		ContentValues values = new ContentValues();
		
		values.put(NEWSTYPE, newsType);
		values.put(PAGE, page);
		
		if(isList) {
			//表征为newlist
			values.put(ISLIST, "1");
		}
		else {
			//表征为focuslist
			values.put(ISLIST, "2");
		}
		
		values.put(ID, entry.getID());
		values.put(TITLE, entry.getTITLE());
		values.put(SUBTITLE, entry.getSUBTITLE());
		values.put(PIC, entry.getPIC());
		values.put(PICEXT1, entry.getPICEXT1());
		values.put(PICEXT2, entry.getPICEXT2());
		values.put(PICEXT3, entry.getPICEXT3());
		values.put(TYPE, entry.getTYPE());
		values.put(FURL, entry.getFURL());
		values.put(UPDATETIME, entry.getUPDATETIME());
		values.put(ALLOWCOMMENT,entry.getALLOWCOMMENT());
		
//		Log.d("chenhj,table", "save");
		db.save(db.getConnection(), TABLE_CONSULTINFO, values);	
	}
	
	public List<NewsItem> getConsultInfoContent(String newsType, String page, boolean isList) {
		Cursor cursor = null;
		ArrayList<NewsItem> ConsultInfoContent = null;
		try {
			String isLIST;
			if(isList) {
				isLIST = "1";
			}
			else {
				isLIST = "2";
			}
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONSULTINFO + " where "+NEWSTYPE+" = ? and "
					+ PAGE + " = ? and " + ISLIST + " = ? " , new String[]{newsType, page, isLIST});
			if (cursor != null) {
				ConsultInfoContent = new ArrayList<NewsItem>();
				NewsItem entry = null;
				while (cursor.moveToNext()) {
					entry = new NewsItem();
					
					entry.setID(cursor.getString(cursor.getColumnIndex(ID)));
					entry.setTITLE(cursor.getString(cursor.getColumnIndex(TITLE)));
					entry.setSUBTITLE(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
					entry.setPIC(cursor.getString(cursor.getColumnIndex(PIC)));
					entry.setPICEXT1(cursor.getString(cursor.getColumnIndex(PICEXT1)));
					entry.setPICEXT2(cursor.getString(cursor.getColumnIndex(PICEXT2)));
					entry.setPICEXT3(cursor.getString(cursor.getColumnIndex(PICEXT3)));
					entry.setTYPE(cursor.getString(cursor.getColumnIndex(TYPE)));
					entry.setFURL(cursor.getString(cursor.getColumnIndex(FURL)));
					entry.setUPDATETIME(cursor.getString(cursor.getColumnIndex(UPDATETIME)));
					entry.setALLOWCOMMENT(cursor.getString(cursor.getColumnIndex(ALLOWCOMMENT)));
					
					ConsultInfoContent.add(entry);
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ConsultInfoContent = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
//		Log.d("chenhj,table", "get0");
		return ConsultInfoContent;
	}
	
	public List<NewsItem> getNewsList(String newsType, String page) {
		Cursor cursor = null;
		ArrayList<NewsItem> ConsultInfoContent = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONSULTINFO + " where "+NEWSTYPE+" = ? and "
					+ PAGE + " = ? and " + ISLIST + " = ? " , new String[]{newsType, page, "1"});
			if (cursor != null) {
				ConsultInfoContent = new ArrayList<NewsItem>();
				NewsItem entry = null;
				while (cursor.moveToNext()) {
					entry = new NewsItem();
					
					entry.setID(cursor.getString(cursor.getColumnIndex(ID)));
					entry.setTITLE(cursor.getString(cursor.getColumnIndex(TITLE)));
					entry.setSUBTITLE(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
					entry.setPIC(cursor.getString(cursor.getColumnIndex(PIC)));
					entry.setPICEXT1(cursor.getString(cursor.getColumnIndex(PICEXT1)));
					entry.setPICEXT2(cursor.getString(cursor.getColumnIndex(PICEXT2)));
					entry.setPICEXT3(cursor.getString(cursor.getColumnIndex(PICEXT3)));
					entry.setTYPE(cursor.getString(cursor.getColumnIndex(TYPE)));
					entry.setFURL(cursor.getString(cursor.getColumnIndex(FURL)));
					entry.setUPDATETIME(cursor.getString(cursor.getColumnIndex(UPDATETIME)));
					entry.setALLOWCOMMENT(cursor.getString(cursor.getColumnIndex(ALLOWCOMMENT)));
					
					ConsultInfoContent.add(entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ConsultInfoContent = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
//		Log.d("chenhj,table", "getNews: " + ConsultInfoContent.size());
		return ConsultInfoContent;
	}
	
	public List<NewsItem> getFocusList(String newsType, String page) {
		Cursor cursor = null;
		ArrayList<NewsItem> ConsultInfoContent = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONSULTINFO + " where "+NEWSTYPE+" = ? and "
					+ PAGE + " = ? and " + ISLIST + " = ? " , new String[]{newsType, page, "2"});
			if (cursor != null) {
				ConsultInfoContent = new ArrayList<NewsItem>();
				NewsItem entry = null;
				while (cursor.moveToNext()) {
					entry = new NewsItem();
					
					entry.setID(cursor.getString(cursor.getColumnIndex(ID)));
					entry.setTITLE(cursor.getString(cursor.getColumnIndex(TITLE)));
					entry.setSUBTITLE(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
					entry.setPIC(cursor.getString(cursor.getColumnIndex(PIC)));
					entry.setPICEXT1(cursor.getString(cursor.getColumnIndex(PICEXT1)));
					entry.setPICEXT2(cursor.getString(cursor.getColumnIndex(PICEXT2)));
					entry.setPICEXT3(cursor.getString(cursor.getColumnIndex(PICEXT3)));
					entry.setTYPE(cursor.getString(cursor.getColumnIndex(TYPE)));
					entry.setFURL(cursor.getString(cursor.getColumnIndex(FURL)));
					entry.setUPDATETIME(cursor.getString(cursor.getColumnIndex(UPDATETIME)));
					entry.setALLOWCOMMENT(cursor.getString(cursor.getColumnIndex(ALLOWCOMMENT)));
					
					ConsultInfoContent.add(entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ConsultInfoContent = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
//		Log.d("chenhj,table", "getFocus: " + ConsultInfoContent.size());
		return ConsultInfoContent;
	}
	
	public int getTotalPage(String newsType) {
		//线性查找，提高效率需要将page字段改为整形
		Cursor cursor = null;
		int totalPage = -1;
		try {
			cursor = db.find(db.getConnection(), "select "+PAGE+" from " + TABLE_CONSULTINFO + " where "+NEWSTYPE+" = ? "
					, new String[]{newsType});
			if(cursor != null) {
				while(cursor.moveToNext()) {
					int temp = Integer.parseInt(cursor.getString(0));
					if(temp > totalPage) {
						totalPage = temp;
					}
				}
			}
		}
		catch(Exception e) {
			totalPage = -1;
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return totalPage;
	}
	
	public void clearAtType(String newsType) {
		db.delete(db.getConnection(), TABLE_CONSULTINFO, NEWSTYPE + " = ? "
				, new String[] {newsType});
	}
	
	public void clearAtTypePage(String newsType, String page) {
		db.delete(db.getConnection(), TABLE_CONSULTINFO, NEWSTYPE + " = ? and " + PAGE + " = ? "
				, new String[] {newsType, page});
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_CONSULTINFO, null, null);
	}
	
}
