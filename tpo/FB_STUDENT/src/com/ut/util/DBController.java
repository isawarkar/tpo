package com.ut.util;

import java.util.ArrayList;
import java.util.List;

import com.ut.pojo.Company;
import com.ut.pojo.FeeReminder;
import com.ut.pojo.HallTicket;
import com.ut.pojo.Notice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBController extends SQLiteOpenHelper {
	private static final String LOGCAT = null;

	public DBController(Context applicationcontext) {
		super(applicationcontext, "androidsqlite.db", null, 1);
		Log.d(LOGCAT, "Created");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String query;
		query = "CREATE TABLE opennings (userName TEXT NOT NULL,hallticketID TEXT NOT NULL,companyName TEXT NOT NULL,date TEXT NOT NULL,time TEXT NOT NULL, packageOffering TEXT NOT NULL,lastDateToApply TEXT NOT NULL,criteria TEXT NOT NULL,interviewLocation TEXT NOT NULL,postingLocation TEXT NOT NULL,role TEXT NOT NULL,companyID INTEGER NOT NULL,domain TEXT NOT NULL,website TEXT NOT NULL,linkedIn TEXT NOT NULL,twiter TEXT NOT NULL,glassdoor TEXT NOT NULL,facebook TEXT NOT NULL,email TEXT NOT NULL,profile TEXT NOT NULL,remarks TEXT NOT NULL,logo BLOB NOT NULL, PRIMARY KEY (userName,hallticketID))";
		database.execSQL(query);
		Log.d(LOGCAT, "opennings Table Created");

		query = "CREATE TABLE notices (noticeName TEXT NOT NULL,notice TEXT NOT NULL,createdBy TEXT NOT NULL,expiryDate TEXT NOT NULL,expired TEXT NOT NULL,studentSpecific INTEGER DEFAULT 0,impTag INTEGER DEFAULT 0,file1 TEXT,fileName1 TEXT,file2 TEXT,fileName2 TEXT,file3 TEXT,fileName3 TEXT,file4 TEXT,fileName4 TEXT,file5 TEXT,fileName5 TEXT, PRIMARY KEY (noticeName,createdBy))";
		database.execSQL(query);
		Log.d(LOGCAT, "notices Table Created");

		query = "CREATE TABLE feedetails (id INTEGER NOT NULL,amountPaid TEXT NOT NULL,paidOn TEXT NOT NULL,amountDue TEXT NOT NULL,dueOn TEXT NOT NULL, PRIMARY KEY (id))";
		database.execSQL(query);
		Log.d(LOGCAT, "feedetails Table Created");

		query = "CREATE TABLE userInfo (enrollmentNo TEXT NOT NULL,PRIMARY KEY (enrollmentNo))";
		database.execSQL(query);
		Log.d(LOGCAT, "userInfo Table Created");

		query = "CREATE TABLE notification (name TEXT NOT NULL,value INTEGER NOT NULL, PRIMARY KEY (name))";
		database.execSQL(query);
		Log.d(LOGCAT, "notification Table Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
		String query;
		query = "DROP TABLE IF EXISTS opennings";
		database.execSQL(query);
		query = "DROP TABLE IF EXISTS userInfo";
		database.execSQL(query);
		query = "DROP TABLE IF EXISTS notification";
		database.execSQL(query);
		query = "DROP TABLE IF EXISTS notices";
		database.execSQL(query);
		query = "DROP TABLE IF EXISTS feedetails";
		database.execSQL(query);
		onCreate(database);
	}

	public void insertEventRequest(HallTicket event) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();
		values.put("userName", event.getUserName());
		values.put("hallticketID", event.getHallTicketId());
		values.put("companyName", event.getCompanyName());
		values.put("date", event.getDate());
		values.put("time", event.getTime());
		values.put("packageOffering", event.getPackageOffering());
		values.put("userName", event.getUserName());
		values.put("lastDateToApply", event.getLastDateToApply());
		values.put("criteria", event.getCriteria());
		values.put("interviewLocation", event.getInterviewLocation());
		values.put("role", event.getPostingLocation());
		values.put("postingLocation", event.getRole());
		Company company = event.getCompany();
		values.put("companyID", company.getCompanyID());
		values.put("domain", company.getDomain());
		values.put("website", company.getWebsite());
		values.put("linkedIn", company.getLinkedIn());
		values.put("twiter", company.getTwiter());
		values.put("glassdoor", company.getGlassdoor());
		values.put("facebook", company.getFacebook());
		values.put("email", company.getEmail());
		values.put("profile", company.getProfile());
		values.put("remarks", company.getRemarks());
		values.put("logo", company.getLogo());
		database.insert("opennings", null, values);
		database.close();
	}

	public void deleteEventRequest(HallTicket event) {
		Log.d(LOGCAT, "delete");
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM  opennings where hallticketID='" + event.getHallTicketId() + "'";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);
	}

	public boolean isEventRead(HallTicket hallTicket) {
		String selectQuery = "SELECT  * FROM opennings where username='" + hallTicket.getUserName() + "' and hallticketID='"
				+ hallTicket.getHallTicketId() + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}

		// return contact list
		return false;
	}

	public void insertNoticeRequest(Notice notice) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();
		values.put("noticeName", notice.getNoticeName());
		values.put("notice", notice.getNotice());
		values.put("createdBy", notice.getCreatedBy());
		values.put("expiryDate", notice.getExpiryDate());
		values.put("expired", notice.getExpired());
		values.put("studentSpecific", notice.getStudentSpecific());
		values.put("impTag", notice.isImpTag());
		values.put("file1", notice.getFile1());
		values.put("fileName1", notice.getFileName1());
		values.put("file2", notice.getFile2());
		values.put("fileName2", notice.getFileName2());
		values.put("file3", notice.getFile3());
		values.put("fileName3", notice.getFileName3());
		values.put("file4", notice.getFile4());
		values.put("fileName4", notice.getFileName4());
		values.put("file5", notice.getFile5());
		values.put("fileName5", notice.getFileName5());
		database.insert("notices", null, values);
		database.close();
	}

	public void insertFeeRequest(FeeReminder feeReminder) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values;
		values = new ContentValues();
		values.put("id", feeReminder.getId());
		values.put("amountPaid", feeReminder.getAmountPaid());
		values.put("paidOn", feeReminder.getPaidOn());
		values.put("amountDue", feeReminder.getAmountDue());
		values.put("dueOn", feeReminder.getDueOn());
		database.insert("feedetails", null, values);
		database.close();
	}

	public void deleteFeeRequest(FeeReminder feeReminder) {
		Log.d(LOGCAT, "delete");
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM  feedetails where id=" + feeReminder.getId() + "";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);
	}

	public void deleteNoticeRequest(Notice notice) {
		Log.d(LOGCAT, "delete");
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM  notices where noticeName='" + notice.getNoticeName() + "' and "
				+ "createdBy='" + notice.getCreatedBy() + "'";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);
	}

	public boolean isNoticeRead(Notice notice) {
		String selectQuery = "SELECT  * FROM notices where noticeName='" + notice.getNoticeName() + "' and createdBy='"
				+ notice.getCreatedBy() + "'";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}

		// return contact list
		return false;
	}

	public boolean isFeeReaded(FeeReminder feeReminder) {
		String selectQuery = "SELECT  * FROM feedetails where id=" + feeReminder.getId() + "";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.getCount() > 0) {
			cursor.close();
			return true;
		}

		// return contact list
		return false;
	}

	public List<HallTicket> getOldEventList() {
		String selectQuery = "SELECT  * FROM opennings";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		List<HallTicket> hallTickets = new ArrayList<HallTicket>(cursor.getCount());
		HallTicket hallTicket;
		Company company;
		while (cursor.moveToNext()) {
			hallTicket = new HallTicket();
			company = new Company();
			hallTicket.setUserName(cursor.getString(0));
			hallTicket.setHallTicketId(cursor.getInt(1));
			hallTicket.setCompanyName(cursor.getString(2));
			hallTicket.setDate(cursor.getString(3));
			hallTicket.setTime(cursor.getString(4));
			hallTicket.setPackageOffering(cursor.getString(5));
			hallTicket.setLastDateToApply(cursor.getString(6));
			hallTicket.setCriteria(cursor.getString(7));
			hallTicket.setInterviewLocation(cursor.getString(8));
			hallTicket.setPostingLocation(cursor.getString(9));
			hallTicket.setRole(cursor.getString(10));
			company.setCompanyID(cursor.getInt(11));
			company.setDomain(cursor.getString(12));
			company.setWebsite(cursor.getString(13));
			company.setLinkedIn(cursor.getString(14));
			company.setTwiter(cursor.getString(15));
			company.setGlassdoor(cursor.getString(16));
			company.setFacebook(cursor.getString(17));
			company.setEmail(cursor.getString(18));
			company.setProfile(cursor.getString(19));
			company.setRemarks(cursor.getString(20));
			company.setLogo(cursor.getBlob(21));
			hallTicket.setCompany(company);
			hallTickets.add(hallTicket);
		}
		cursor.close();
		return hallTickets;
	}

	public List<Notice> getOldNoticeList() {
		String selectQuery = "SELECT  * FROM notices";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		List<Notice> noticeList = new ArrayList<Notice>(cursor.getCount());
		Notice notice;
		while (cursor.moveToNext()) {
			notice = new Notice();
			notice.setNoticeName(cursor.getString(0));
			notice.setNotice(cursor.getString(1));
			notice.setCreatedBy(cursor.getString(2));
			notice.setExpiryDate(cursor.getString(3));
			notice.setExpired(cursor.getString(4));
			notice.setStudentSpecific(cursor.getInt(5) == 0 ? false : true);
			notice.setImpTag(cursor.getInt(6) == 0 ? false : true);
			notice.setFile1(cursor.getString(7));
			notice.setFileName1(cursor.getString(8));
			notice.setFile2(cursor.getString(9));
			notice.setFileName2(cursor.getString(10));
			notice.setFile3(cursor.getString(11));
			notice.setFileName3(cursor.getString(12));
			notice.setFile4(cursor.getString(13));
			notice.setFileName4(cursor.getString(14));
			notice.setFile5(cursor.getString(15));
			notice.setFileName5(cursor.getString(16));
			noticeList.add(notice);
		}
		cursor.close();
		return noticeList;
	}

	public List<FeeReminder> getOldFeeList() {
		String selectQuery = "SELECT  * FROM feedetails";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		List<FeeReminder> noticeList = new ArrayList<FeeReminder>(cursor.getCount());
		FeeReminder feeReminder;
		while (cursor.moveToNext()) {
			feeReminder = new FeeReminder();
			feeReminder.setId(cursor.getInt(0));
			feeReminder.setAmountPaid(cursor.getDouble(1));
			feeReminder.setPaidOn(cursor.getString(2));
			feeReminder.setAmountDue(cursor.getDouble(3));
			feeReminder.setDueOn(cursor.getString(4));
			noticeList.add(feeReminder);
		}
		cursor.close();
		return noticeList;
	}

	public String selectUserInfo() {
		String result = null;
		String selectQuery = "SELECT  * FROM userInfo";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			result = cursor.getString(0);

		}
		cursor.close();
		// database.close();
		return result;
	}

	public void insertIntoUserInfo(String enrollmentNo) {
		deleteUserInfo();
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("enrollmentNo", enrollmentNo);
		database.insert("userInfo", null, values);
		database.close();
	}

	public void deleteUserInfo() {
		Log.d(LOGCAT, "delete");
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM  userInfo";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);
	}

	public void insertIntoNotification(String name, Object value) {
		try {
			SQLiteDatabase database = null;
			ContentValues values = new ContentValues();
			values.put("name", name);
			values.put("value", String.valueOf(value));
			Integer result = selectNotification(name);
			database = this.getWritableDatabase();
			if (result != null) {
				String[] whereArgs = new String[] { String.valueOf(name) };
				database.delete("notification", "name=?", whereArgs);
				database.insert("notification", null, values);
			} else {
				database.insert("notification", null, values);
			}
			database.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public Integer selectNotification(String name) {
		Integer result = null;
		try {
			String selectQuery = "SELECT  * FROM notification where name='" + name + "'";
			SQLiteDatabase database = this.getWritableDatabase();
			Cursor cursor = database.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				result = cursor.getInt(1);

			}
			cursor.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}

	public void clearAll() {
		deleteUserInfo();
		SQLiteDatabase database = this.getWritableDatabase();

		Log.d(LOGCAT, "delete opennings");
		String deleteQuery = "DELETE FROM  opennings";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);

		Log.d(LOGCAT, "delete notices");
		deleteQuery = "DELETE FROM  notices";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);

		Log.d(LOGCAT, "delete notification");
		deleteQuery = "DELETE FROM  notification";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);

		Log.d(LOGCAT, "delete feedetails");
		deleteQuery = "DELETE FROM  feedetails";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);
	}
}
