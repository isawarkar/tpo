/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class UIBackingBean {
	private String panelColor;

	private String successMessage;

	private String errorMessage;

	private String infoMessage;

	private Boolean successMessageBool;

	private Boolean errorMessageBool;

	private Boolean infoMessageBool;

	public void clear() {
		panelColor = "";
		successMessageBool = false;
		errorMessageBool = false;
		infoMessageBool = false;
	}

	public void setInfo(String message) {
		panelColor = "blue";
		successMessage = "";
		errorMessage = "";
		infoMessage = message;
		successMessageBool = false;
		errorMessageBool = false;
		infoMessageBool = true;

	}

	public void setError(String message) {
		panelColor = "red";
		successMessage = "";
		errorMessage = message;
		infoMessage = "";
		successMessageBool = false;
		errorMessageBool = true;
		infoMessageBool = false;

	}

	public void setSuccess(String message) {
		panelColor = "green";
		successMessage = message;
		errorMessage = "";
		infoMessage = "";
		successMessageBool = true;
		errorMessageBool = false;
		infoMessageBool = false;

	}

	public static void setErrorMessage(String error) {
		UIBackingBean bean = (UIBackingBean) TpoUtil
				.getManagedBean(UIBackingBean.class.getSimpleName());
		if (bean != null)
			bean.setError(error);
	}

	public static void setInfoMessage(String info) {
		UIBackingBean bean = (UIBackingBean) TpoUtil
				.getManagedBean(UIBackingBean.class.getSimpleName());
		if (bean != null)
			bean.setInfo(info);
	}

	public static void setSuccessMessage(String success) {
		UIBackingBean bean = (UIBackingBean) TpoUtil
				.getManagedBean(UIBackingBean.class.getSimpleName());
		if (bean != null)
			bean.setSuccess(success);
	}

	/**
	 * @return the successMessageBool
	 */
	public synchronized Boolean getSuccessMessageBool() {
		return successMessageBool;
	}

	/**
	 * @param successMessageBool
	 *            the successMessageBool to set
	 */
	public synchronized void setSuccessMessageBool(Boolean successMessageBool) {
		this.successMessageBool = successMessageBool;
	}

	/**
	 * @return the errorMessageBool
	 */
	public synchronized Boolean getErrorMessageBool() {
		return errorMessageBool;
	}

	/**
	 * @param errorMessageBool
	 *            the errorMessageBool to set
	 */
	public synchronized void setErrorMessageBool(Boolean errorMessageBool) {
		this.errorMessageBool = errorMessageBool;
	}

	/**
	 * @return the infoMessageBool
	 */
	public synchronized Boolean getInfoMessageBool() {
		return infoMessageBool;
	}

	/**
	 * @param infoMessageBool
	 *            the infoMessageBool to set
	 */
	public synchronized void setInfoMessageBool(Boolean infoMessageBool) {
		this.infoMessageBool = infoMessageBool;
	}

	/**
	 * @return the successMessage
	 */
	public synchronized String getSuccessMessage() {
		return successMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public synchronized String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return the infoMessage
	 */
	public synchronized String getInfoMessage() {
		return infoMessage;
	}

	/**
	 * @return the panelColor
	 */
	public synchronized String getPanelColor() {
		return panelColor;
	}

	/**
	 * @param panelColor
	 *            the panelColor to set
	 */
	public synchronized void setPanelColor(String panelColor) {
		this.panelColor = panelColor;
	}

}
