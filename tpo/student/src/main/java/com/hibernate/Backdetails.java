/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.hibernate;

import java.io.Serializable;
import java.util.Date;

import com.util.FbResourceUtil;

/**
 * @author Uddanda Technologies
 */
public class Backdetails implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private Registration registration;

	private Integer backLog;

	private String backDetails;

	private Integer passMoreThenOneAttempt;

	private Integer numberOfBacklogs;

	private Integer baGroup;

	private Boolean blackList = false;

	private Short educationGap;

	private Date lastUpdated;

	private String lastUpdatedBy;

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (backLog == 1) {
			str.append("<br> <font color=#990066 size=3>"+FbResourceUtil.getLabel("Present_Backlog")+" </font>= "+FbResourceUtil.getLabel("YES")+"");
		} else {
			str.append("<br> <font color=#990066 size=3>"+FbResourceUtil.getLabel("Present_Backlog")+" </font>= "+FbResourceUtil.getLabel("NO")+"");
		}
		str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Details_of_Backlog")+"</font>= "
				+ backDetails);

		if (passMoreThenOneAttempt == 1) {
			str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Any_semester_passed_in_more_than_one_attempt")+"</font>= "+FbResourceUtil.getLabel("YES")+"");
		} else {
			str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Any_semester_passed_in_more_than_one_attempt")+" </font>= "+FbResourceUtil.getLabel("NO")+"");
		}
		str.append("<br> <font color=#990066 size=3>"+FbResourceUtil.getLabel("Number_of_Backlog")+"</font>= "
				+ numberOfBacklogs);
		if (baGroup == 1) {
			str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Are_you_in_BA_group_Year_or_Sem_back")+" </font>= "+FbResourceUtil.getLabel("YES")+"");
		} else {
			str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Are_you_in_BA_group_Year_or_Sem_back")+" </font>= "+FbResourceUtil.getLabel("NO")+"");
		}
		if (blackList) {
			str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Black_Listed")+" </font>= "+FbResourceUtil.getLabel("YES")+"");
		} else {
			str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Black_Listed")+" </font>= "+FbResourceUtil.getLabel("NO")+"");
		}

		str.append("<br> <font color=#990066 size=3> "+FbResourceUtil.getLabel("Education_Gap_in_Years")+"</font>= "
				+ educationGap);
		return str.toString();
	}

	// Property accessors

	public String getRollnumber() {
		return this.rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public Registration getRegistration() {
		return this.registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public Integer getBackLog() {
		return this.backLog;
	}

	public void setBackLog(Integer backLog) {
		this.backLog = backLog;
	}

	public String getBackDetails() {
		return this.backDetails;
	}

	public void setBackDetails(String backDetails) {
		this.backDetails = backDetails;
	}

	public Integer getPassMoreThenOneAttempt() {
		return this.passMoreThenOneAttempt;
	}

	public void setPassMoreThenOneAttempt(Integer passMoreThenOneAttempt) {
		this.passMoreThenOneAttempt = passMoreThenOneAttempt;
	}

	public Integer getNumberOfBacklogs() {
		return this.numberOfBacklogs;
	}

	public void setNumberOfBacklogs(Integer numberOfBacklogs) {
		this.numberOfBacklogs = numberOfBacklogs;
	}

	public Integer getBaGroup() {
		return this.baGroup;
	}

	public void setBaGroup(Integer baGroup) {
		this.baGroup = baGroup;
	}

	/**
	 * @return the blackList
	 */
	public synchronized Boolean getBlackList() {
		return blackList;
	}

	/**
	 * @param blackList
	 *            the blackList to set
	 */
	public synchronized void setBlackList(Boolean blackList) {
		this.blackList = blackList;
	}

	/**
	 * @return the educationGap
	 */
	public Short getEducationGap() {
		return educationGap;
	}

	/**
	 * @param educationGap
	 *            the educationGap to set
	 */
	public void setEducationGap(Short educationGap) {
		this.educationGap = educationGap;
	}

}