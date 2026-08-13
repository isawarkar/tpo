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
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.openfaces.component.command.CommandButton;

import com.dao.CommonDBBean;
import com.util.FbResourceUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class EnrollmentNumberValidator implements Validator {

	public EnrollmentNumberValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent,
			Object object) throws ValidatorException {
		String rollNumber = (String) object;

		CommonDBBean bean = (CommonDBBean) TpoUtil
				.getManagedBean(CommonDBBean.class.getSimpleName());
		if (bean.isRecordExist(rollNumber)) {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				if ("loginForm:enrollmentNo".equals(uIComponent.getClientId())) {
					
					HtmlOutputLabel label  = (HtmlOutputLabel) uIComponent
							.getParent().getChildren().get(4);
					if (label != null) {
						label.setStyle("display:block;");
					}
					
					HtmlInputSecret secret = (HtmlInputSecret) uIComponent
							.getParent().getChildren().get(5);
					if (secret != null) {
						secret.setDisabled(false);
						secret.setTitle(FbResourceUtil.getLabel("Please_enter_Password"));
						secret.setStyle("display:block;");
					}
					CommandButton button = (CommandButton) uIComponent
							.getParent().getChildren().get(8);
					if (button != null) {
						button.setDisabled(false);
						button.setValue(FbResourceUtil.getLabel("Sign_In"));
						button.setTitle(FbResourceUtil.getLabel("Please_click_here_to_login"));
						button.setStyle("display:block;");
					}
					coreInputText
							.setStyle("background-color: green;color:white;border-color: white;");
					coreInputText.setTitle(FbResourceUtil.getLabel("Valid_Enrollment_No"));
				} else {
					FacesMessage message = new FacesMessage();
					message.setSummary(FbResourceUtil.getLabel("Enrollment_No_already_exist"));
					coreInputText
							.setStyle("background-color: red;color:white;border-color: white;");
					throw new ValidatorException(message);
					
				}
			}
		} else {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				if ("loginForm:enrollmentNo".equals(uIComponent.getClientId())) {
					HtmlOutputLabel label  = (HtmlOutputLabel) uIComponent
							.getParent().getChildren().get(4);
					if (label != null) {
						label.setStyle("display:none;");
					}
					
					HtmlInputSecret secret = (HtmlInputSecret) uIComponent
							.getParent().getChildren().get(5);
					if (secret != null) {
						secret.setDisabled(true);
						secret.setTitle(FbResourceUtil.getLabel("Text_Field_is_disabled_Please_enter_enrollment_number_first"));
						secret.setStyle("display:none;");
					}
					CommandButton button = (CommandButton) uIComponent
							.getParent().getChildren().get(8);
					if (button != null) {
						button.setDisabled(true);
						button.setValue("Disabled");
						button.setTitle(FbResourceUtil.getLabel("Button_is_disabled_Please_enter_enrollment_number_first"));
						button.setStyle("display:none;");
					}
					coreInputText
							.setStyle("background-color: red;color:white;border-color: white;");
					coreInputText.setTitle(FbResourceUtil.getLabel("Enrollment_No_does_not_exist"));
				} else {
					coreInputText
							.setStyle("background-color: green;color:white;border-color: white;");
				}

			}
		}
	}
}
