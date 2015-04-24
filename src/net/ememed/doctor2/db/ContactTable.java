package net.ememed.doctor2.db;

import java.util.ArrayList;
import java.util.List;
import net.ememed.doctor2.R;
import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ContactTable {


	public final static String TABLE_CONTACT = "tb_contact";
	
	private final static String MEMBERID = "position_group";

	private static final String USER_ACCOUNT = "user_account";
	private String HAVEORDER= "haveorder";;
	private String MSG_ID = "msg_id";
	private String TYPE = "type";
	private String CONTENT = "content";
	private String SENDTIME = "sendtime";
	private String ISSYSTEMMSG = "is_system_msg";
	private String AVATAR ="avatar";
	private String UTYPE = "utype";
	private String PROFESSIONAL = "professional";
	private String HOSPITALNAME = "hospitalname";
	private String REALNAME = "realname";
	private String MEMBERNAME = "membername";
	
	private String HAS_NEW_MES = "has_new_msg";
	private String NEW_MES_NUM = "new_msg_num";
	
	private String IS_ATTENTION = "is_attention";
    private String IS_MY_FANS = "is_my_fans";
    
    private String IS_STAR = "is_star"; //标星（1是，0否）
    private String NOTE_NAME = "note_name"; //备注姓名
    private String DESCRIPTION = "description"; //患者描述
    private String GROUPID = "group_id"; //患者分组ID
    
	
	private DBManagerImpl db = null;
	
	public ContactTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONTACT)) {
			createConfigTable();
		}
	}
	
	public ContactTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_CONTACT)) {
			createConfigTable();
		}
	}
	
	private void createConfigTable() {
		String createSql = "create table if not exists "+TABLE_CONTACT+" (id integer primary key autoincrement,"+USER_ACCOUNT+" varchar,"
				+ MEMBERID + " varchar,"+HAVEORDER+" varchar,"+MSG_ID+" varchar,"+TYPE+" varchar,"+CONTENT+" varchar,"
				+SENDTIME+" varchar,"+ISSYSTEMMSG+" varchar,"+UTYPE+" varchar,"+PROFESSIONAL+" varchar,"+HOSPITALNAME+" varchar,"
				+REALNAME+" varchar,"+MEMBERNAME+" varchar,"+HAS_NEW_MES+" varchar,"+NEW_MES_NUM+" integer,"+AVATAR+" varchar,"
				+IS_STAR+" varchar,"+NOTE_NAME+" varchar,"+DESCRIPTION+" varchar,"+GROUPID+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_CONTACT);
	}
	
	public ArrayList<ContactEntry> getAllPositionNames() {
		Cursor cursor = null;
		ArrayList<ContactEntry> positionnames = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_CONTACT +" where "+USER_ACCOUNT+"=?", new String[]{SharePrefUtil.getString(Conast.Doctor_ID)});
			if (cursor != null) {
				positionnames = new ArrayList<ContactEntry>();
				ContactEntry entry = null;
				while (cursor.moveToNext()) {
					entry = new ContactEntry();
					entry.setMEMBERID(cursor.getString(cursor.getColumnIndex(MEMBERID)));
					entry.setAVATAR(cursor.getString(cursor.getColumnIndex(AVATAR)));
					entry.setHAVEORDER(cursor.getString(cursor.getColumnIndex(HAVEORDER)));
					entry.setMSG_ID(cursor.getString(cursor.getColumnIndex(MSG_ID)));
					entry.setTYPE(cursor.getString(cursor.getColumnIndex(TYPE)));
					entry.setCONTENT(cursor.getString(cursor.getColumnIndex(CONTENT)));
					entry.setSENDTIME(cursor.getString(cursor.getColumnIndex(SENDTIME)));
					entry.setISSYSTEMMSG(cursor.getString(cursor.getColumnIndex(ISSYSTEMMSG)));
					entry.setUTYPE(cursor.getString(cursor.getColumnIndex(UTYPE)));
					entry.setPROFESSIONAL(cursor.getString(cursor.getColumnIndex(PROFESSIONAL)));
					entry.setHOSPITALNAME(cursor.getString(cursor.getColumnIndex(HOSPITALNAME)));
					entry.setREALNAME(cursor.getString(cursor.getColumnIndex(REALNAME)));
					entry.setMEMBERNAME(cursor.getString(cursor.getColumnIndex(MEMBERNAME)));
					entry.setHAS_NEW_MES(cursor.getString(cursor.getColumnIndex(HAS_NEW_MES)));
					entry.setHAS_NEW_MES(cursor.getString(cursor.getColumnIndex(NEW_MES_NUM)));
					
					entry.setIS_STAR(cursor.getString(cursor.getColumnIndex(IS_STAR)));
					entry.setNOTE_NAME(cursor.getString(cursor.getColumnIndex(NOTE_NAME)));
					entry.setDESCRIPTION(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
					entry.setGROUPID(cursor.getString(cursor.getColumnIndex(GROUPID)));
					
					positionnames.add(entry);
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
	
	public void updateMessageNum(String memberid,int msg_num) {
		Cursor cursor = null;
		try {
			ContentValues values = new ContentValues();
			values.put(NEW_MES_NUM, msg_num);
			
			cursor = db.find(db.getConnection(),"select * from  "+TABLE_CONTACT +" where  "+ MEMBERID +"=? and " + USER_ACCOUNT + "=? ",new String[]{memberid,SharePrefUtil.getString(Conast.Doctor_ID)});
		    if(null!=cursor&&cursor.moveToNext()) {
			  db.update(db.getConnection(),TABLE_CONTACT, values, MEMBERID+"=? and "+ USER_ACCOUNT + "=? ",
					  new String[]{memberid,SharePrefUtil.getString(Conast.Doctor_ID)});
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}
	
	
	public void savePositionName(List<ContactEntry> list) {
		ContactEntry entry = null;
		for (int i = 0; i < list.size(); i++) {
			entry = list.get(i);
			ContentValues values = new ContentValues();
			values.put(USER_ACCOUNT, SharePrefUtil.getString(Conast.Doctor_ID));
			values.put(MEMBERID, entry.getMEMBERID());
			values.put(AVATAR, entry.getAVATAR());
			values.put(HAVEORDER, entry.getHAVEORDER());
			values.put(MSG_ID, entry.getMSG_ID());
			values.put(TYPE, entry.getTYPE());
			values.put(CONTENT, entry.getCONTENT());
			values.put(SENDTIME, entry.getSENDTIME());
			values.put(ISSYSTEMMSG, entry.getISSYSTEMMSG());
			values.put(UTYPE, entry.getUTYPE());
			values.put(PROFESSIONAL, entry.getPROFESSIONAL());
			values.put(HOSPITALNAME, entry.getHOSPITALNAME());
			values.put(REALNAME, entry.getREALNAME());
			values.put(MEMBERNAME, entry.getMEMBERNAME());
			values.put(IS_STAR, entry.getIS_STAR());
			values.put(NOTE_NAME, entry.getNOTE_NAME());
			values.put(DESCRIPTION, entry.getDESCRIPTION());
			values.put(GROUPID, entry.getGROUPID());
			db.save(db.getConnection(), TABLE_CONTACT, values);	
		}
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_CONTACT, null, null);
	}

}
