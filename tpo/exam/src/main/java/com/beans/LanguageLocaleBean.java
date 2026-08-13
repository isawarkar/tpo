package com.beans;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.util.FbMessageUtil;
import com.util.FbResourceUtil;
import com.util.SystemUtil;
import com.util.TpoUtil;

@Component("LanguageLocaleBean")
@Scope("session")
public class LanguageLocaleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String defaultLocal = null;

	private String countryCode = "us";

	private String countryLanguage = "English";

	private Locale locale;

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		if (defaultLocal == null) {
			defaultLocal = SystemUtil.getLabel("defaultLocal");
		}
		locale = new Locale(defaultLocal);
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

}