/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.util.FbMessageUtil;

/**
 * @author Uddanda Technologies
 */
public class PasswordValidator implements Validator {

	public void validate(FacesContext facesContext, UIComponent uIComponent,
			Object object) throws ValidatorException {
		String enteredStr = (String) object;
		FacesMessage message = new FacesMessage();
		message.setSummary(FbMessageUtil.getLabel("Please_enter_valid_password"));
		String expression = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
		CharSequence inputStr = enteredStr;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (!matcher.matches()) {
			if (uIComponent instanceof HtmlInputSecret) {
				HtmlInputSecret htmlInputSecret = (HtmlInputSecret) uIComponent;
				htmlInputSecret
						.setStyle("background-color: red;color:white;border-color: white;");
			}
			throw new ValidatorException(message);
		} else {
			if (uIComponent instanceof HtmlInputSecret) {
				HtmlInputSecret htmlInputSecret = (HtmlInputSecret) uIComponent;
				htmlInputSecret
						.setStyle("background-color: green;color:white;border-color: white;");
			}
		}
	}
}
