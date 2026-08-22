package com.ut.fbn.admin.util;

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
		query = "CREATE TABLE userInfo (userName TEXT NOT NULL,role TEXT NOT NULL,PRIMARY KEY (userName))";
		database.execSQL(query);
		Log.d(LOGCAT, "userInfo Table Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
		String query;
		query = "DROP TABLE IF EXISTS userInfo";
		database.execSQL(query);
		database.execSQL(query);
		onCreate(database);
	}

	public String[] selectUserInfo() {
		String result[] = null;
		String selectQuery = "SELECT  * FROM userInfo";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			result = new String[2];
			result[0] = cursor.getString(0);
			result[1] = cursor.getString(1);

		}
		cursor.close();
		// database.close();
		return result;
	}

	public void insertIntoUserInfo(String userName, String role) {
		deleteUserInfo();
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userName", userName);
		values.put("role", role);
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

	public void clearAll() {
		deleteUserInfo();
	}
}
