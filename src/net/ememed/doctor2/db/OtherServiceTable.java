package net.ememed.doctor2.db;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import net.ememed.doctor2.entity.ServiceExtEntry;
import net.ememed.doctor2.entity.ServiceListEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class OtherServiceTable {
private final static String TABLE_SERVICE = "tb_service";
	
//	private final static String MEMBERID = "position_group";
//
//	private static final String USER_ACCOUNT = "user_account";
	private String DOCTOR_ID = "doctoc_id";
	
	private String SERVICE_ID = "id";;
	private String SERVICE_NAME = "service_name";
	private String SERVICE_ICON = "icon";
	private String SERVICE_TYPE = "type";
	private String SERVICE_APPKEY = "appkey";
	private String SERVICE_VERSION = "version";
	private String SERVICE_DESCRIBE = "describe";
	private String SERVICE_STATUS = "status";
	private String SERVICE_ORDERBY = "orderby";
	private String SERVICE_EXT_URL = "url";
	private String SERVICE_EXT_CHECK = "check_right_url";
	private String SERVICE_EXT_PHONENUMBER = "phone_number";
	private String SERVICE_ISTHIRDPARTY = "isthirdparty";
	private String SERVICE_CREATETIME = "createtime";
	private String SERVICE_UPDATETIME = "updatetime";
	private String SERVICE_APP_TYPE = "app_type";
	private String SERVICE_ON_CLINK = "onclink";
	
	private Gson gson = new Gson();
	private DBManagerImpl db = null;
	
	public OtherServiceTable() {
		if (db == null) {
			db = DBManager.get();
		}
		if (!db.isTableExits(db.getConnection(), TABLE_SERVICE)) {
			createServiceTable();
		}
		if (!db.isColumnExits(db.getConnection(), TABLE_SERVICE, SERVICE_ON_CLINK)) {
			db.addColumn(db.getConnection(), TABLE_SERVICE, SERVICE_ON_CLINK, "varchar");
		}
	}
	
	public OtherServiceTable(Context context) {
		if (db == null) {
			db = DBManager.get(context);
		}
		if (!db.isTableExits(db.getConnection(), TABLE_SERVICE)) {
			createServiceTable();
		}
		
		if (!db.isColumnExits(db.getConnection(), TABLE_SERVICE, SERVICE_ON_CLINK)) {
			db.addColumn(db.getConnection(), TABLE_SERVICE, SERVICE_ON_CLINK, "VARCHAR");
		}
	}
	
	private void createServiceTable() {
		String createSql = "create table if not exists "+TABLE_SERVICE+" (_id integer primary key autoincrement,"+DOCTOR_ID+" varchar,"
				+SERVICE_ID+" varchar,"+ SERVICE_NAME + " varchar,"+SERVICE_ICON+" varchar,"+SERVICE_TYPE+" varchar,"
				+SERVICE_APPKEY+" varchar,"+SERVICE_VERSION+" varchar,"+SERVICE_DESCRIBE+" varchar,"+SERVICE_STATUS+" varchar,"
				+SERVICE_ORDERBY+" varchar,"+SERVICE_EXT_URL+" varchar,"+SERVICE_EXT_CHECK+" varchar,"+SERVICE_EXT_PHONENUMBER+" varchar,"
				+SERVICE_ISTHIRDPARTY+" varchar, "
				+SERVICE_ON_CLINK+" varchar,"
				+SERVICE_CREATETIME+" varchar,"+SERVICE_UPDATETIME+" varchar,"+SERVICE_APP_TYPE+" varchar)";
		db.creatTable(db.getConnection(), createSql, TABLE_SERVICE);
	}
	
	public ArrayList<ServiceListEntry> getServiceContent(String doctorID) {
		Cursor cursor = null;
		ArrayList<ServiceListEntry> serviceContent = null;
		try {
			cursor = db.find(db.getConnection(), "select * from " + TABLE_SERVICE + " where "+DOCTOR_ID+" = ?" , 
					new String[]{doctorID});
			if (cursor != null) {
				serviceContent = new ArrayList<ServiceListEntry>();
				ServiceListEntry entry = null;
				while (cursor.moveToNext()) {
					entry = new ServiceListEntry();
					entry.setID(cursor.getString(cursor.getColumnIndex(SERVICE_ID)));
					entry.setSERVICE_NAME(cursor.getString(cursor.getColumnIndex(SERVICE_NAME)));
					entry.setICON(cursor.getString(cursor.getColumnIndex(SERVICE_ICON)));
					entry.setTYPE(cursor.getString(cursor.getColumnIndex(SERVICE_TYPE)));
					entry.setAPPKEY(cursor.getString(cursor.getColumnIndex(SERVICE_APPKEY)));
					entry.setVERSION(cursor.getString(cursor.getColumnIndex(SERVICE_VERSION)));
					entry.setDESCRIBE(cursor.getString(cursor.getColumnIndex(SERVICE_DESCRIBE)));
					entry.setSTATUS(cursor.getString(cursor.getColumnIndex(SERVICE_STATUS)));
					entry.setORDERBY(cursor.getString(cursor.getColumnIndex(SERVICE_ORDERBY)));
					
					ServiceExtEntry ext = new ServiceExtEntry();
					ext.setURL(cursor.getString(cursor.getColumnIndex(SERVICE_EXT_URL)));
					ext.setCHECK_RIGHT_URL(cursor.getString(cursor.getColumnIndex(SERVICE_EXT_CHECK)));
					ext.setPHONE_NUMBER(cursor.getString(cursor.getColumnIndex(SERVICE_EXT_PHONENUMBER)));
					entry.setEXT(ext);
					
					entry.setISTHIRDPARTY(cursor.getString(cursor.getColumnIndex(SERVICE_ISTHIRDPARTY)));
					entry.setCREATETIME(cursor.getString(cursor.getColumnIndex(SERVICE_CREATETIME)));
					entry.setUPDATETIME(cursor.getString(cursor.getColumnIndex(SERVICE_UPDATETIME)));
					entry.setAPP_TYPE(cursor.getString(cursor.getColumnIndex(SERVICE_APP_TYPE)));
					
					serviceContent.add(entry);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			serviceContent = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return serviceContent;
	}
	
	public String getServiceItemVersion(String doctorID, String serviceID) {
		Cursor cursor = null;
		String version = null;
		try {
			cursor = db.find(db.getConnection(), "select "+SERVICE_VERSION+" from " + TABLE_SERVICE + " where "+DOCTOR_ID+" = ? and "
						+ SERVICE_ID + "= ? ", new String[]{doctorID, serviceID});
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					version = cursor.getString(0);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			version = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return version;
	}
	public boolean getServiceItemOnClick(String doctorID, String serviceID) {
		Cursor cursor = null;
		String isflag = null;
		boolean result = true;
		try {
			cursor = db.find(db.getConnection(), "select "+SERVICE_ON_CLINK+" from " + TABLE_SERVICE + " where "+DOCTOR_ID+" = ? and "
					+ SERVICE_ID + "= ? ", new String[]{doctorID, serviceID});
			
			if (cursor != null) {
				while (cursor.moveToNext()) {
					isflag = cursor.getString(0);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isflag = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		if(!TextUtils.isEmpty(isflag) && "false".equals(isflag)){
			result = false;
		}
		return result;
	}
	public void setServiceItemOnClick(String doctorID, String serviceID,boolean onClick) {
		ContentValues values = new ContentValues();
		values.put(SERVICE_ON_CLINK, onClick+"");
		boolean update = db.update(db.getConnection(), TABLE_SERVICE, values, SERVICE_ID+"=?", new String[]{serviceID});
		if(update){
			
		}
	}
	
	public void saveServiceContent(ServiceListEntry entry, String doctorID) {
		
			ContentValues values = new ContentValues();
			
			values.put(DOCTOR_ID, doctorID);
			
			values.put(SERVICE_ID, entry.getID());
			values.put(SERVICE_NAME, entry.getSERVICE_NAME());
			values.put(SERVICE_ICON, entry.getICON());
			values.put(SERVICE_TYPE, entry.getTYPE());
			values.put(SERVICE_APPKEY, entry.getAPPKEY());
			if(null == entry.getVERSION()) {
				values.put(SERVICE_VERSION, 0);
			}
			else {
				values.put(SERVICE_VERSION, entry.getVERSION());
			}
			values.put(SERVICE_DESCRIBE, entry.getDESCRIBE());
			values.put(SERVICE_STATUS, entry.getSTATUS());
			values.put(SERVICE_ORDERBY, entry.getORDERBY());
			if(null != entry.getEXT()) {
				values.put(SERVICE_EXT_URL, entry.getEXT().getURL());
				values.put(SERVICE_EXT_CHECK, entry.getEXT().getCHECK_RIGHT_URL());
				values.put(SERVICE_EXT_PHONENUMBER, entry.getEXT().getPHONE_NUMBER());
			}
			values.put(SERVICE_ISTHIRDPARTY, entry.getISTHIRDPARTY());
			values.put(SERVICE_CREATETIME, entry.getCREATETIME());
			values.put(SERVICE_UPDATETIME, entry.getUPDATETIME());
			values.put(SERVICE_APP_TYPE, entry.getAPP_TYPE());
			
			((DBManager) db).updateAndInsert(db.getConnection(), TABLE_SERVICE, values, SERVICE_ID+"=?", new String[]{entry.getID()});
		
	}
	
	public void updateService(ServiceListEntry entry, String doctorID, String serviceID) {
		
		String whereClause = DOCTOR_ID + "= ? and " + SERVICE_ID + "= ? ";
		
		ContentValues values = new ContentValues();
		
		values.put(SERVICE_NAME, entry.getSERVICE_NAME());
		values.put(SERVICE_ICON, entry.getICON());
		values.put(SERVICE_TYPE, entry.getTYPE());
		values.put(SERVICE_APPKEY, entry.getAPPKEY());
		if(null == entry.getVERSION()) {
			values.put(SERVICE_VERSION, 0);
		}
		else {
			values.put(SERVICE_VERSION, entry.getVERSION());
		}
		values.put(SERVICE_DESCRIBE, entry.getDESCRIBE());
		values.put(SERVICE_STATUS, entry.getSTATUS());
		values.put(SERVICE_ORDERBY, entry.getORDERBY());
		
		if(null != entry.getEXT()) {
			values.put(SERVICE_EXT_URL, entry.getEXT().getURL());
			values.put(SERVICE_EXT_CHECK, entry.getEXT().getCHECK_RIGHT_URL());
			values.put(SERVICE_EXT_PHONENUMBER, entry.getEXT().getPHONE_NUMBER());
		}
		
		values.put(SERVICE_ISTHIRDPARTY, entry.getISTHIRDPARTY());
		values.put(SERVICE_CREATETIME, entry.getCREATETIME());
		values.put(SERVICE_UPDATETIME, entry.getUPDATETIME());
		values.put(SERVICE_APP_TYPE, entry.getAPP_TYPE());
		
		db.update(db.getConnection(), TABLE_SERVICE, values, whereClause, new String[]{doctorID, serviceID});
	}
	
	public void clearTable() {
		db.delete(db.getConnection(), TABLE_SERVICE, null, null);
	}

	public List<String> getServiceIds() {
		List<String> ids = null;
		String findSql = "select "+SERVICE_ID+" from "+TABLE_SERVICE;;
		Cursor find = db.find(db.getConnection(), findSql, null);
		if(find!=null && find.moveToNext()){
			ids = new ArrayList<String>();
			ids.add(find.getString(0));
			while(find.moveToNext()){
				ids.add(find.getString(0));
			}
		}
		return ids;
	}

	public void clearItem(String id) {
		// TODO Auto-generated method stub
		db.delete(db.getConnection(), TABLE_SERVICE, SERVICE_ID+"=?", new String[]{id});
	}
}
