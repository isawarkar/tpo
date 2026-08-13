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

import tpo.beans.ImageBean;
import tpo.beans.StudentRegistrationBean;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class ChaptaValidator implements Validator {

	public ChaptaValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent,
			Object object) throws ValidatorException {
		String text = (String) object;
		HtmlInputText coreInputText = (HtmlInputText) uIComponent;
		StudentRegistrationBean bean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());
		//TestBean testBean = (TestBean)TpoUtil.getManagedBean(TestBean.class.getSimpleName());
		if (bean != null && uIComponent instanceof HtmlInputText) {
			ImageBean imageBean = (ImageBean) TpoUtil
					.getManagedBean(ImageBean.class.getSimpleName());
			if (text != null && text.equals(bean.getVerificationNumber())) {
				imageBean.setImageCorrect(true);
				coreInputText
						.setStyle("background-color: green;color:white;border-color: white;");
				coreInputText.setTitle(FbMessageUtil.getLabel("Good_You_are_a_Human"));
			/*	if(testBean != null){
					testBean.setButtonVarable(false);
				}*/
			} else {
				FacesMessage message = new FacesMessage();
				message.setSummary(FbMessageUtil.getLabel("Please_enter_correct_Image_as_Above"));
				coreInputText
						.setStyle("background-color: red;color:white;border-color: white;");
				coreInputText.setTitle(FbMessageUtil.getLabel("Please_enter_correct_Image_as_Above"));
				imageBean.setImageCorrect(false);
				throw new ValidatorException(message);
			}
		}
	}
}
