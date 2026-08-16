package tpo.locale;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.admin.backup.CollegeConnectBackUp;
import tpo.beans.Parent;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.TpoUtil;

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
		CollegeConnectBackUp backUp = (CollegeConnectBackUp) TpoUtil.getManagedBean("collegeConnectBackUp");
		if (backUp != null) {
			backUp.setDateAndTime(null);
		}
		// assign new value to localeCode
		defaultLocal = e.getNewValue().toString();

	}

	public void setEnglish() {
		CollegeConnectBackUp backUp = (CollegeConnectBackUp) TpoUtil.getManagedBean("collegeConnectBackUp");
		if (backUp != null) {
			backUp.setDateAndTime(null);
		}
		defaultLocal = "en";
		countryCode = "us";
		countryLanguage = "English";

	}

	public void setHindi() {
		CollegeConnectBackUp backUp = (CollegeConnectBackUp) TpoUtil.getManagedBean("collegeConnectBackUp");
		if (backUp != null) {
			backUp.setDateAndTime(null);
		}
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

	public void downloadFbn() {
		OutputStream servletOutputStream = null;
		HttpServletResponse response = null;
		FacesContext facesContext = null;
		try {
			String path = null;
			if(isUnix()) {
				path = "/home/ubuntu/indwaar/FBN.apk";
			}else {
			 path = "D:\\home\\FBN.apk";
			}
			byte[] encoded = Files.readAllBytes(Paths.get(path));

			facesContext = FacesContext.getCurrentInstance();
			response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
			servletOutputStream = response.getOutputStream();

			response.setContentType("application/vnd.android.package-archive");
			response.setHeader("Content-Disposition", "attachment; filename=FBN.apk");
			servletOutputStream.write(encoded);
		} catch (IOException e) {
		} finally {
			try {
				if (servletOutputStream != null) {
					servletOutputStream.close();
					facesContext.responseComplete();
				}
			} catch (IOException e) {
			}
		}
	}

	private boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

}