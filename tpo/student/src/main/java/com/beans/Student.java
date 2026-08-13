/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

import javax.faces.event.ValueChangeEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dao.CommonDBBean;
import com.util.TpoUtil;


/**
 * @author Uddanda Technologies
 */
@Component("Student")
@Scope("session")
public class Student {

	private String rollNumber;
	private String rollNumberP;
	private String userName;
	private String createBy;
	private String theme;
	private Integer colorCode = 1;

	@Autowired
	private CommonDBBean commonDBBean;

	public static Student getStudent() {
		Student user = (Student) TpoUtil.getManagedBean(Student.class.getSimpleName());
		return user;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}



	/**
	 * @return the rollNumber
	 */
	public String getRollNumber() {
		return rollNumber;
	}

	/**
	 * @param rollNumber the rollNumber to set
	 */
	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String updateTheme(String theme) {
		commonDBBean.updateTheme(rollNumber, theme);
		setTheme(theme);
		StudentLogin ccpLoginBean = (StudentLogin) TpoUtil.getManagedBean(StudentLogin.class.getSimpleName());
		if (ccpLoginBean != null) {
			return ccpLoginBean.goToStudentHomePage();
		}
		return "";
	}
	
	public String updateColor(Integer color) {
		commonDBBean.updateColor(rollNumber, color);
		setColorCode(color);
		StudentLogin ccpLoginBean = (StudentLogin) TpoUtil.getManagedBean(StudentLogin.class.getSimpleName());
		if (ccpLoginBean != null) {
			return ccpLoginBean.goToStudentHomePage();
		}
		return "";
	}
	
	public String changeTheme(ValueChangeEvent e){
		return updateTheme(e.getNewValue().toString());
	}

	public Integer getColorCode() {
		return colorCode;
	}

	public void setColorCode(Integer colorCode) {
		this.colorCode = colorCode;
	}

	
}
