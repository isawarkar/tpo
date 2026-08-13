/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.util;

/**
 * @author Uddanda Technologies
 */
public class CCPConstant {

	public static String DATE_FORMAT_ddMMyyyyhhmm = "dd/MM/yyyy hh:mm";
	public static String DATE_FORMAT_ddMMyyyyhhmm_24 = "dd/MM/yyyy HH:mm";
	public static String EMPTY = "";
	public static String CREATE = "Create";
	public static String UPDATE = "Update";
	public static String VIEW = "View";

	public static String APP_NAME = "Freshers Buddy";

	// roles

	public static final String MENU = "Menu";

	public static final String TAB = "TAB";

	// public static final String ROLE = "Role";

	public static final String ADMIN = "Admin";

	public static final String USER = "User";

	public static final String COLLEGE = "College User";

	public static final String COMPANY = "Company User";

	public static final String SUPERUSER = "Super User";

	public static String SINGLE = "Single";
	public static String MULTIPLE = "Multiple";
	public static String COMPLIE = "Compile";

	public static String PERCENT = "Percent";
	public static String NUMBER = "Number";
	public static final String REGQUERY_UTIL = "reg query ";

	public static String Qualified = "Qualified";
	public static String Disqualified = "Disqualified";
	public static String QualifiedInHonours = "Qualified in Honours";
	public static String QualifiedinFirstClass = "Qualified in First Class";
	
	public static String TEST_STARTED = "Test Started";

	public static String APPROVED = "Approved";
	public static String PENDING = "Pending";
	public static String SELECTED = "Selected";
	public static String Black_Listed = "Black Listed";
	public static String TOTAL = "Total";
	
	public static String EFFORT_SUBMMITED = "S";
	public static String EFFORT_REJECTED = "R";
	public static String EFFORT_APPROVED= "A";
	
	public static String URGENT= "Urgent";
	public static String HIGH= "High";
	public static String MEDIUM= "Medium";
	public static String LOW= "Low";

	public static String getSecValue() {
		return REGQUERY_UTIL + "\"HKCU\\Software\\SYSTEM_RELATED\" /v SYSTEM_RELATED";
	}
}
