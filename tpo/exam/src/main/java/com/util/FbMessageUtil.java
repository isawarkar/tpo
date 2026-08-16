/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Uddanda Technologies
 */
@Component
public class FbMessageUtil  {

	
	@Value("${defaultLocal:en}")
	private String defaultLocal;

	private static ResourceBundle labels;
	private static Locale locale;

	@PostConstruct
	public void init() {
	    locale = new Locale(defaultLocal);
	    labels = ResourceBundle.getBundle("com.exam.fbmessages", locale);
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
		FbMessageUtil.locale = locale;
		labels = ResourceBundle.getBundle("com.exam.fbmessages",locale);
	}
	
	
}
