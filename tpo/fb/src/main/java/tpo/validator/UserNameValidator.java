/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.validator;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import tpo.dao.CommonDBBean;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class UserNameValidator implements Validator {

	public UserNameValidator() {
	}

	public void validate(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException {
		String userName = (String) object;

		CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
		if (bean.isUserExist(userName)) {
			if (uIComponent instanceof HtmlInputText) {
				List<UIComponent> list = uIComponent.getParent().getParent().getChildren();

				/*
				 * for(UIComponent c : list ) { if(c instanceof HtmlCommandButton) { int i =
				 * list.indexOf(c); logger.error(i); } }
				 */

				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				FacesMessage message = new FacesMessage();
				message.setSummary(FbMessageUtil.getLabel("Info1"));
				coreInputText.setStyle("background-color: red;color:white;border-color: white;");
				coreInputText.setTitle(message.getSummary());
				HtmlCommandButton button = (HtmlCommandButton) list.get(24);
				button.setDisabled(true);
				button.setTitle(message.getSummary());
				throw new ValidatorException(message);

			}
		} else {
			if (uIComponent instanceof HtmlInputText) {
				List<UIComponent> list = uIComponent.getParent().getParent().getChildren();
				HtmlCommandButton button = (HtmlCommandButton) list.get(24);
				button.setDisabled(false);
				HtmlInputText coreInputText = (HtmlInputText) uIComponent;
				coreInputText.setStyle("background-color: green;color:white;border-color: white;");

			}
		}
	}
}
