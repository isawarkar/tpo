package com.ut.util;

public final class WSURL {

	public static String localhost = null;

	public static String preUrl_WS;
	public static String EVENT_WS;
	public static String NOTIC_WS;
	public static String ELIGIBLE_EVENT_WS;
	public static String LOGIN_WS;
	public static String HALLTICKET_APPLY_WS;
	public static String STUDENT_ARRIVED_WS;
	public static String DOWNLOAD_HALLTICKET_WS;
	public static String DOWNLOAD_REGISTRATION_FORM_WS;
	public static String DOWNLOAD_RESUME_WS;
	public static String DOWNLOAD_IMAGE_WS;
	public static String UPLOAD_IMAGE_WS;
	public static String CHANAGE_PASS_WS;
	public static String FORGOT_PASSWORD;
	public static String RESULT_LIST_WS;
	public static String DOCUMENT_LIST_WS;
	public static String DOWNLOAD_DOCUMENT_WS;
	public static String VERIFY_NUMBER;
	public static String FEE_REMINDER_LIST;
	
	public static String BASE_PATH;

	public static void setLocalhost(String host) {
		localhost = host;
		BASE_PATH = localhost + "/student/";
		preUrl_WS = BASE_PATH + "FresherBuddyStudentService/";
		
		LOGIN_WS = preUrl_WS + "validateMyLogin/";
		CHANAGE_PASS_WS = preUrl_WS + "changePassword/";
		FORGOT_PASSWORD = preUrl_WS + "forgotPassword/";
		
		
		EVENT_WS = preUrl_WS + "eventList/";
		NOTIC_WS = preUrl_WS + "noticList/";
		ELIGIBLE_EVENT_WS = preUrl_WS + "eligibleEventList/";
		RESULT_LIST_WS = preUrl_WS + "resultList/";
		DOCUMENT_LIST_WS = preUrl_WS + "documentList/";
		DOWNLOAD_DOCUMENT_WS = preUrl_WS + "downloadDocument/";
		VERIFY_NUMBER = preUrl_WS + "verifyNumber/";
		FEE_REMINDER_LIST = preUrl_WS + "studentFeetList/";
		HALLTICKET_APPLY_WS = preUrl_WS + "apply/";
		STUDENT_ARRIVED_WS = preUrl_WS + "studentArrived/";
		DOWNLOAD_HALLTICKET_WS = preUrl_WS + "download/";
		DOWNLOAD_REGISTRATION_FORM_WS = preUrl_WS + "downloadRegistrationForm/";
		DOWNLOAD_RESUME_WS = preUrl_WS + "downloadResume/";
		DOWNLOAD_IMAGE_WS = preUrl_WS + "downloadProfile/";
		UPLOAD_IMAGE_WS = preUrl_WS + "uploadProfileImage/";
	}
}
