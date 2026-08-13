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
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import tpo.beans.NewsLetterBean;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class NewsLetterEmailValidator extends EmailValidator {

	public NewsLetterEmailValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException {
		super.validate(facesContext, uIComponent, object);
		String email = (String) object;

		NewsLetterBean bean = (NewsLetterBean) TpoUtil.getManagedBean(NewsLetterBean.class.getSimpleName());
		if (bean.isRecordExist(email)) {
			if (uIComponent instanceof HtmlInputText) {
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				
				HtmlCommandButton button  = (HtmlCommandButton) uIComponent
						.getParent().getChildren().get(2);
				HtmlOutputLabel label  = (HtmlOutputLabel) uIComponent
						.getParent().getChildren().get(3);
				button.setDisabled(true);
				button.setStyle("display:none;");
				label.setStyle("display:none;");
				FacesMessage message = new FacesMessage();
				message.setSummary(FbMessageUtil.getLabel("Email_address_already_exist"));
				coreInputText.setStyle("background-color: red;color:white;border-color: white;");
				throw new ValidatorException(message);
			}
		}else{
			HtmlCommandButton button  = (HtmlCommandButton) uIComponent
					.getParent().getChildren().get(2);
			button.setDisabled(false);
			button.setStyle("display:block;");
			
			HtmlOutputLabel label  = (HtmlOutputLabel) uIComponent
					.getParent().getChildren().get(3);
			label.setStyle("display:block;color:green;");
		}
	}
}
