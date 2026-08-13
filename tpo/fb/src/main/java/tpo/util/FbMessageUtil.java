/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Uddanda Technologies
 */
public class FbMessageUtil {

	static ResourceBundle labels;
	static Locale locale;
	static {
		locale = new Locale(SystemUtil.getLabel("defaultLocal"));
		labels = ResourceBundle.getBundle("com.fb.fbmessages",locale);
	}

	public static String getLabel(String key) {
		String text;
		try {
			text = labels.getString(key);
		} catch (MissingResourceException e) {
			text = "?? key " + key + " not found ??";
		}
		return text;
	}

	public static String getLabel(String key, Object params[]) {
		String text;
		try {
			text = labels.getString(key);
		} catch (MissingResourceException e) {
			text = "?? key " + key + " not found ??";
		}

		if (params != null) {
			MessageFormat mf = new MessageFormat(text);
			text = mf.format(params, new StringBuffer(), null).toString();
		}

		return text;
	}
	
	public static String getLabel(String key, Object param) {
		String str[] = new String[1];
		str[0] = String.valueOf(param);
		return getLabel(key, str);
	}

	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		if(!FbMessageUtil.locale.toString().equals(locale.toString())){
		FbMessageUtil.locale = locale;
		labels = ResourceBundle.getBundle("com.fb.fbmessages",locale);
		}
	}
	
	
}
