/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import tpo.dao.CommonDBBean;
import tpo.util.FbResourceUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class EnrollmentNumberValidatorInvert implements Validator {

	public EnrollmentNumberValidatorInvert() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException {
		String rollNumber = (String) object;

		CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
		if (!bean.isRecordExist(rollNumber)) {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				FacesMessage message = new FacesMessage();
				message.setSummary(FbResourceUtil.getLabel("Enrollment_No_does_not_exist"));
				coreInputText.setStyle("background-color: red;color:white;border-color: white;");
				throw new ValidatorException(message);
			}
		} else {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				coreInputText.setStyle("background-color: green;color:white;border-color: white;");

			}
		}
	}
}
