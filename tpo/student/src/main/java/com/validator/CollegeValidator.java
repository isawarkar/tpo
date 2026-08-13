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

import com.dao.CommonDBBean;
import com.util.FbMessageUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class CollegeValidator implements Validator {

	public CollegeValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent,
			Object object) throws ValidatorException {
		String collegeCode = (String) object;

		CommonDBBean bean = (CommonDBBean) TpoUtil
				.getManagedBean(CommonDBBean.class.getSimpleName());
		if (!bean.isCollegeCodeCorrect(collegeCode)) {
			FacesMessage message = new FacesMessage();
			message.setSummary(FbMessageUtil.getLabel("College_code_is_not_correct_code",collegeCode));
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText dropDownField = (HtmlInputText) uIComponent;
				dropDownField
						.setStyle("background-color: red;color:white;border-color: white;");
			}
			throw new ValidatorException(message);
		} else {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText dropDownField = (HtmlInputText) uIComponent;
				dropDownField
						.setStyle("background-color: green;color:white;border-color: white;");
			}
		}
	}
}
