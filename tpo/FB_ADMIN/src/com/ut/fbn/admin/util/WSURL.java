package com.ut.fbn.admin.util;

public final class WSURL {

	public static String localhost = null;

	public static String preUrl_WS;
	public static String LOGIN_WS;
	public static String STUDENT_ARRIVED_WS;
	public static String CHANAGE_PASS_WS;
	public static String FORGOT_PASSWORD;
	public static String PROFILE_IMAGE_WS;
	public static String BASE_PATH;
	public static String COMPANY_LIST_WS;
	public static String OPPENING_LIST_WS;
	public static String OPPENING_LIST_BY_COMPANY_WS;
	public static String STUDENT_LIST_BY_COMPANY_WS;
	public static String UPLOAD_IMAGE_WS;
	public static String APPROVE_OR_REJECT;
	public static String DELETE_COMPANY;
	public static String DELETE_OPENING;
	public static void setLocalhost(String host) {
		localhost = host;
		BASE_PATH = localhost + "/fb/";
		preUrl_WS = BASE_PATH + "FresherBuddyAdminService/";
		LOGIN_WS = preUrl_WS + "validateAdminLogin/";
		CHANAGE_PASS_WS = preUrl_WS + "changePassword/";
		FORGOT_PASSWORD = preUrl_WS + "forgotPassword/";
		STUDENT_ARRIVED_WS = preUrl_WS + "studentArrived/";
		PROFILE_IMAGE_WS = preUrl_WS + "downloadAdminProfileImage/";
		OPPENING_LIST_WS = preUrl_WS + "oppeningList/";
		COMPANY_LIST_WS = preUrl_WS + "companyList/";
		OPPENING_LIST_BY_COMPANY_WS = preUrl_WS + "aplliedListForCompany/";
		STUDENT_LIST_BY_COMPANY_WS = preUrl_WS + "hallticketListByID/";
		UPLOAD_IMAGE_WS = preUrl_WS + "uploadProfileImage/";
		APPROVE_OR_REJECT = preUrl_WS + "approveOrReject/";
		DELETE_COMPANY = preUrl_WS + "deleteCompany/";
		DELETE_OPENING = preUrl_WS + "deleteOpening/";
	}
	
}
