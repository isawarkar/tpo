package com.ut.fbn.admin.util;

import android.graphics.Color;
import android.os.Environment;

public interface FBNConstants {
	String USERNAME = "userName";
	String NOTICS = "NOTICS";
	Integer NOTIFICATION_TIME_IN_MINUTE = 1;
	int FONT_COLOR = Color.MAGENTA;
	int LIST_BACKGROUND_COLOR = Color.CYAN;

	String MY_PREFS_NAME = "FresherBuddyAdmin";
	
	String FBN_DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
	
	String Home="Home";
	String Scan_QR="Scan QR";
	String Login="Login";
	String About="About";
	String COMPANY_LIST = "COMPANY_LIST";
	String OPPENING_LIST_BY_COMPANY_WS = "OPPENING_LIST_BY_COMPANY_WS";
	String STUDENT_LIST_BY_COMPANY_WS="STUDENT_LIST_BY_COMPANY_WS";
}
