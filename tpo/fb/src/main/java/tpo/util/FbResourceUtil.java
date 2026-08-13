/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Uddanda Technologies
 */
public class FbResourceUtil {

	
	static ResourceBundle labels;
	static Locale locale;
	static {
		locale = new Locale(SystemUtil.getLabel("defaultLocal"));
		labels = ResourceBundle.getBundle("com.fb.fbresource",locale);
	}
	

	public static String getLabel(String key) {
		String value = labels.getString(key);
		if (value != null) {
			return value;
		}
		return key;
	}
	
	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		if(!FbResourceUtil.locale.toString().equals(locale.toString())){
		 FbResourceUtil.locale = locale;
		labels = ResourceBundle.getBundle("com.fb.fbresource",locale);
		}
	}
}
