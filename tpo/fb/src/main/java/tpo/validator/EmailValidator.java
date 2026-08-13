/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import tpo.beans.UIBackingBean;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class EmailValidator implements Validator {
	public EmailValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException {
		String enteredEmail = (String) object;
		if (enteredEmail != null && !"".equals(enteredEmail)) {
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(enteredEmail);
			boolean matchFound = m.matches();

			if (!matchFound) {
				FacesMessage message = new FacesMessage();
				message.setSummary(FbMessageUtil.getLabel("Invalid_E_mail_ID"));
				if (uIComponent instanceof HtmlInputText) {
					if (uIComponent.getParent() != null) {
						if (uIComponent.getParent().getParent() != null) {
							if (uIComponent.getParent().getParent().getChildren() != null) {
								List<UIComponent> list = uIComponent.getParent().getParent().getChildren();
								for (UIComponent c : list) {
									if (c instanceof HtmlCommandButton) {
										/*
										 * int i = list.indexOf(c); logger.error(i);
										 */
										HtmlCommandButton button = (HtmlCommandButton) c;
										button.setDisabled(true);
										button.setTitle(message.getSummary());
									}
								}
							}
						}
					}
					HtmlInputText coreInputText = (HtmlInputText) uIComponent;
					coreInputText.setStyle("background-color: red;color:white;border-color: white;");
					UIBackingBean bean = (UIBackingBean) TpoUtil.getManagedBean(UIBackingBean.class.getSimpleName());
					if (bean != null) {
						bean.setError(message.getSummary() + ":" + enteredEmail);
					}
				}
				throw new ValidatorException(message);
			} else {
				if (uIComponent instanceof HtmlInputText) {
					if (uIComponent.getParent() != null) {
						if (uIComponent.getParent().getParent() != null) {
							if (uIComponent.getParent().getParent().getChildren() != null) {
								List<UIComponent> list = uIComponent.getParent().getParent().getChildren();
								for (UIComponent c : list) {
									if (c instanceof HtmlCommandButton) {
										/*
										 * int i = list.indexOf(c); logger.error(i);
										 */
										HtmlCommandButton button = (HtmlCommandButton) c;
										button.setDisabled(false);
									}
								}
							}
						}
					}
					HtmlInputText coreInputText = (HtmlInputText) uIComponent;
					coreInputText.setStyle("background-color: green;color:white;border-color: white;");
				}
			}
		}
	}
}
