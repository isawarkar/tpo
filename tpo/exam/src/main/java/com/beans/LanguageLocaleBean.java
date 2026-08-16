package com.beans;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.util.FbMessageUtil;
import com.util.FbResourceUtil;
import com.util.TpoUtil;


@Component("LanguageLocaleBean")
@Scope("session")
public class LanguageLocaleBean extends Parent implements Serializable {

	private static String OS = System.getProperty("os.name").toLowerCase();
	
	private static final long serialVersionUID = 1L;

	
	private String countryCode = "us";

	private String countryLanguage = "English";

	private Locale locale;

	private Integer colorCode = 1;

	public Locale getLocale() {
	    Locale newLocale = new Locale(defaultLocal);

	        locale = newLocale;
	        FbMessageUtil.setLocale(locale);
	        FbResourceUtil.setLocale(locale);
	  
	    return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @return the defaultLocal
	 */
	public String getDefaultLocal() {
		return defaultLocal;
	}

	/**
	 * @param defaultLocal the defaultLocal to set
	 */
	public void setDefaultLocal(String defaultLocal) {
		this.defaultLocal = defaultLocal;
	}

	public static LanguageLocaleBean getLanguageBean() {
		LanguageLocaleBean bean = (LanguageLocaleBean) TpoUtil.getManagedBean(LanguageLocaleBean.class.getSimpleName());
		return bean;
	}

	public String setSelectedLocal(ActionEvent event) {
		defaultLocal = event.getComponent().getId();
		return defaultLocal;
	}

	public String changeTheme(ValueChangeEvent e) {
		colorCode = Integer.valueOf(e.getNewValue().toString());
		return "CCPHomePage";
	}

	public void countryLocaleCodeChanged(ValueChangeEvent e) {
		defaultLocal = e.getNewValue().toString();

	}

	public void setEnglish() {
		defaultLocal = "en";
		countryCode = "us";
		countryLanguage = "English";

	}

	public void setHindi() {
	defaultLocal = "in";
		countryCode = "in";
		countryLanguage = "Hindi";
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryLanguage() {
		return countryLanguage;
	}

	public void setCountryLanguage(String countryLanguage) {
		this.countryLanguage = countryLanguage;
	}

	public Integer getColorCode() {
		return colorCode;
	}

	public void setColorCode(Integer colorCode) {
		this.colorCode = colorCode;
	}



	private boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

}