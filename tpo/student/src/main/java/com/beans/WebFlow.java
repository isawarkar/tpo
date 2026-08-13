/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.util.CCPConstant;
import com.util.FbMessageUtil;
import com.util.ResourceID;
import com.util.TpoUtil;
import com.util.WebFlowTabName;


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
		if (Student.getStudent().getTheme() != null) {
			return "studentHomePage" + Student.getStudent().getTheme();
		} else {
			//default
			return "newStudent";
		}
	}

	public String firstPage() {
		String page = null;
		selectedPage = WebFlowTabName.PI;
		StudentRegistrationBean studentRegistrationBean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());
		if (Student.getStudent() != null && Student.getStudent().getTheme() != null && !"T0".equals(Student.getStudent().getTheme())) {
			if (CCPConstant.CREATE.equals(studentRegistrationBean.getCurrentMode())
					&& !(WebFlowTabName.PI.equals(selectedPage) || WebFlowTabName.AI.equals(selectedPage))) {
				ImageBean imageBean = (ImageBean) TpoUtil.getManagedBean(ImageBean.class.getSimpleName());
				if (imageBean != null && imageBean.isImageCorrect()) {
					selectedPage = WebFlowTabName.PI;
					page = "personalInfoNew";
				} else {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel(ResourceID.Error28));
					selectedPage = WebFlowTabName.MI;
					page = "studentHome";
				}
			} else {
				page = "personalInfoNew";
			}
		} else {
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
		}
		return page;
	}

	public String secondPage() {
		selectedPage = WebFlowTabName.AI;
		if (Student.getStudent() != null  && Student.getStudent().getTheme() != null && !"T0".equals(Student.getStudent().getTheme())) {
			return "percentInfoNew";
		} else {
			return "percentInfo";
		}
	}

	public String thirdPage() {
		String page = null;
		StudentRegistrationBean studentRegistrationBean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());
		if (Student.getStudent() != null  && Student.getStudent().getTheme() != null && !"T0".equals(Student.getStudent().getTheme())) {
			if ("10".equals(studentRegistrationBean.getCurrentCourse())
					|| "12".equals(studentRegistrationBean.getCurrentCourse())) {
				selectedPage = WebFlowTabName.CI;
				page = "contactAndachivmentsNew";
			} else {
				selectedPage = WebFlowTabName.BI;
				page = "backlogInfoNew";
			}
		} else {
			if ("10".equals(studentRegistrationBean.getCurrentCourse())
					|| "12".equals(studentRegistrationBean.getCurrentCourse())) {
				selectedPage = WebFlowTabName.CI;
				page = "contactAndachivments";
			} else {
				selectedPage = WebFlowTabName.BI;
				page = "backlogInfo";
			}
		}
		return page;
	}

	public String thirdPagePrevius() {
		String page = null;
		StudentRegistrationBean studentRegistrationBean = (StudentRegistrationBean) TpoUtil
				.getManagedBean(StudentRegistrationBean.class.getSimpleName());
		if (Student.getStudent() != null  && Student.getStudent().getTheme() != null && !"T0".equals(Student.getStudent().getTheme())) {
			if ("10".equals(studentRegistrationBean.getCurrentCourse())
					|| "12".equals(studentRegistrationBean.getCurrentCourse())) {
				selectedPage = WebFlowTabName.AI;
				page = "percentInfoNew";
			} else {
				selectedPage = WebFlowTabName.BI;
				page = "backlogInfoNew";
			}
		} else {
			if ("10".equals(studentRegistrationBean.getCurrentCourse())
					|| "12".equals(studentRegistrationBean.getCurrentCourse())) {
				selectedPage = WebFlowTabName.AI;
				page = "percentInfo";
			} else {
				selectedPage = WebFlowTabName.BI;
				page = "backlogInfo";
			}
		}
		return page;
	}

	public String contactPage() {
		selectedPage = WebFlowTabName.CI;
		if (Student.getStudent() != null  && Student.getStudent().getTheme() != null && !"T0".equals(Student.getStudent().getTheme())) {
			return "contactAndachivmentsNew";
		} else {
			return "contactAndachivments";
		}
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
		if (Student.getStudent() != null  && Student.getStudent().getTheme() != null && !"T0".equals(Student.getStudent().getTheme())) {
			return "allDocumentsNew";
		} else {
			return "allDocuments";
		}
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
