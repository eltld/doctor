package net.ememed.doctor2.db;

import java.util.ArrayList;
import java.util.List;

import net.ememed.doctor2.entity.ContactEntry;
import net.ememed.doctor2.entity.DoctorFriendInfo;
import net.ememed.doctor2.util.Conast;
import net.ememed.doctor2.util.SharePrefUtil;
import android.content.ContentValues;
import android.database.Cursor;

public class DoctorFriendTable {
	private static String TABLE_DOCTOR_FRIEND = "table_doctor_friend";
	
	private String USER_ACCOUNT = "user_account";	//用户账号，用于区分在同一手机上登录的不同用户

	private String MEMBERID = "member_id"; // => 5226
    private String FRIENDID = "friend_id"; // => 85618
    private String FRIEND_UTYPE = "friend_utype"; // => doctor
    private String ADDTIME = "add_time"; // => 2014-10-21 11:37:59
    private String LEAVETIME = "leave_time"; // => 
    private String STATUS = "status"; // => 1
    private String REALNAME = "real_name"; // => 陈龙 - 测试
    private String AVATAR = "avatar"; // => http://www.ememed.net/uploads/avatar/20140425/avatar_85618_1398396799_ryo9lMsTO8.jpg
    private String MOBILE = "mobile"; // => 13802735116\\
    
    private String IS_ATTENTION = "is_attention"; // 1,
    private String IS_MY_FANS = "is_my_fans"; // 1
    
    private DBManagerImpl db = null;
    
    public DoctorFriendTable(){
    	if(db == null){
    		db = DBManager.get();
    	}
    	
    	if (!db.isTableExits(db.getConnection(), TABLE_DOCTOR_FRIEND)) {
    		createDoctorFriendTable();
		}
    }
    
    
    public void createDoctorFriendTable() {
		String createSql = "create table if not exists " + TABLE_DOCTOR_FRIEND + " (id integer primary key autoincrement," + MEMBERID +" varchar,"
				+ FRIENDID +" varchar,"+ FRIEND_UTYPE +" varchar,"+ ADDTIME +" varchar,"+ LEAVETIME +" varchar,"
				+ STATUS +" varchar,"+ REALNAME +" varchar," + AVATAR +" varchar,"+ MOBILE +" varchar,"+ IS_ATTENTION +" varchar,"
				+ IS_MY_FANS +" varchar," + USER_ACCOUNT + " varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_DOCTOR_FRIEND);
	}
    
    public ArrayList<DoctorFriendInfo> getDoctorFriends(){
    	Cursor cursor = null;
    	ArrayList<DoctorFriendInfo> friends = null;
    	try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_DOCTOR_FRIEND + " where " + USER_ACCOUNT + "=?", new String[]{SharePrefUtil.getString(Conast.Doctor_ID)});
			if(cursor != null){
				friends = new ArrayList<DoctorFriendInfo>();
				DoctorFriendInfo info = null;
				while(cursor.moveToNext()){
					info = new DoctorFriendInfo();
					info.setADDTIME(cursor.getString(cursor.getColumnIndex(ADDTIME)));
					info.setAVATAR(cursor.getString(cursor.getColumnIndex(AVATAR)));
					info.setFRIEND_UTYPE(cursor.getString(cursor.getColumnIndex(FRIEND_UTYPE)));
					info.setFRIENDID(cursor.getString(cursor.getColumnIndex(FRIENDID)));
					info.setIS_ATTENTION(cursor.getString(cursor.getColumnIndex(IS_ATTENTION)));
					info.setIS_MY_FANS(cursor.getString(cursor.getColumnIndex(IS_MY_FANS)));
					info.setLEAVETIME(cursor.getString(cursor.getColumnIndex(LEAVETIME)));
					info.setMEMBERID(cursor.getString(cursor.getColumnIndex(MEMBERID)));
					info.setMOBILE(cursor.getString(cursor.getColumnIndex(MOBILE)));
					info.setREALNAME(cursor.getString(cursor.getColumnIndex(REALNAME)));
					info.setSTATUS(cursor.getString(cursor.getColumnIndex(STATUS)));
					friends.add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
    	
    	return friends;
    }
    
    public void saveDoctorFriends(List<DoctorFriendInfo> list) {
		DoctorFriendInfo entry = null;
		for (int i = 0; i < list.size(); i++) {
			entry = list.get(i);
			ContentValues values = new ContentValues();
			
			values.put(USER_ACCOUNT, SharePrefUtil.getString(Conast.Doctor_ID));
			values.put(MEMBERID, entry.getMEMBERID());
			values.put(AVATAR, entry.getAVATAR());
			values.put(FRIENDID, entry.getFRIENDID());
			values.put(ADDTIME, entry.getADDTIME());
			values.put(LEAVETIME, entry.getLEAVETIME());
			values.put(STATUS, entry.getSTATUS());
			values.put(REALNAME, entry.getREALNAME());
			values.put(MOBILE, entry.getMOBILE());
			values.put(IS_ATTENTION, entry.getIS_ATTENTION());
			values.put(IS_MY_FANS, entry.getIS_MY_FANS());
			
			db.save(db.getConnection(), TABLE_DOCTOR_FRIEND, values);	
		}
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_DOCTOR_FRIEND, null, null);
	}
    
	/**
	 * 清除当前用户的相关数据
	 */
	public void clearCurrentUserMsg(){
		db.delete(db.getConnection(), TABLE_DOCTOR_FRIEND, USER_ACCOUNT + "=?", new String[]{SharePrefUtil.getString(Conast.Doctor_ID)});
	}
   
}
