/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.admin.beans.AdminUser;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.ResourceID;
import tpo.util.TpoUtil;
import tpo.util.WebFlowTabName;

/**
 * @author Uddanda Technologies
 */
@Component("WebFlow")
@Scope("session")
public class WebFlow {

	private String selectedPage = WebFlowTabName.MI;

	public String mainPageNew() {
		selectedPage = WebFlowTabName.MI;
		return "studentHomePageNew";
	}

	public String mainPage() {
		selectedPage = WebFlowTabName.MI;
		if (AdminUser.getUser().getUserName() != null) {
			return "newStudent";
		}
		return "";
	}

	public String firstPage() {
		String page = null;
		selectedPage = WebFlowTabName.PI;
		StudentRegistrationBean studentRegistrationBean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());

		if (CCPConstant.CREATE.equals(studentRegistrationBean.getCurrentMode())
				&& !(WebFlowTabName.PI.equals(selectedPage) || WebFlowTabName.AI.equals(selectedPage))) {
			ImageBean imageBean = (ImageBean) TpoUtil.getManagedBean(ImageBean.class.getSimpleName());
			if (imageBean != null && imageBean.isImageCorrect()) {
				selectedPage = WebFlowTabName.PI;
				page = "personalInfo";
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error28));
				selectedPage = WebFlowTabName.MI;
				page = "newStudent";
			}
		} else {
			page = "personalInfo";
		}
		return page;
	}

	public String secondPage() {
		selectedPage = WebFlowTabName.AI;

		return "percentInfo";
	}

	public String thirdPage() {
		String page = null;
		StudentRegistrationBean studentRegistrationBean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());

		if ("10".equals(studentRegistrationBean.getCurrentCourse())
				|| "12".equals(studentRegistrationBean.getCurrentCourse())) {
			selectedPage = WebFlowTabName.CI;
			page = "contactAndachivments";
		} else {
			selectedPage = WebFlowTabName.BI;
			page = "backlogInfo";
		}
		return page;
	}

	public String thirdPagePrevius() {
		String page = null;
		StudentRegistrationBean studentRegistrationBean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());

		if ("10".equals(studentRegistrationBean.getCurrentCourse())
				|| "12".equals(studentRegistrationBean.getCurrentCourse())) {
			selectedPage = WebFlowTabName.AI;
			page = "percentInfo";
		} else {
			selectedPage = WebFlowTabName.BI;
			page = "backlogInfo";
		}
		return page;
	}

	public String contactPage() {
		selectedPage = WebFlowTabName.CI;

		return "contactAndachivments";
	}

	public String allDocumentPage() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
		selectedPage = WebFlowTabName.AD;
		return "allDocumentsXadmin";
	}

	public String allDocumentPageForStudent() {
		Pagination pagination = Pagination.getPagination();
		pagination.resetCurrentPage();
		selectedPage = WebFlowTabName.AD;
		return "allDocuments";
	}

	/**
	 * @return the selectedPage
	 */
	public synchronized String getSelectedPage() {
		return selectedPage;
	}

	/**
	 * @param selectedPage the selectedPage to set
	 */
	public synchronized void setSelectedPage(String selectedPage) {
		this.selectedPage = selectedPage;
	}

}
