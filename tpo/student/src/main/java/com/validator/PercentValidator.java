/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.util.FbMessageUtil;

/**
 * @author Uddanda Technologies
 */
public class PercentValidator implements Validator {
	public PercentValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent,
			Object object) throws ValidatorException {
		Double value = (Double) object;
		
		if (value > 99.99) {
			FacesMessage message = new FacesMessage();
			message.setSummary(FbMessageUtil.getLabel("Please_enter_correct_Percent_It_cannot_be_GT_99"));
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				coreInputText
						.setStyle("background-color: red;color:white;border-color: white;");
			}
			throw new ValidatorException(message);
		} if (value > 0.0 && value < 33) {
			FacesMessage message = new FacesMessage();
			message.setSummary(FbMessageUtil.getLabel("Please_enter_correct_Percent_It_cannot_be_LT_33"));
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				coreInputText
						.setStyle("background-color: red;color:white;border-color: white;");
			}
			throw new ValidatorException(message);
		}else {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				coreInputText
						.setStyle("background-color: green;color:white;border-color: white;");
			}
		}
	}
}
