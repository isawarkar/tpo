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
public class Achivements implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private Registration registration;

	private String acedamic;

	private String sports;

	private String others;

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
		StringBuilder str = new StringBuilder(
				"<br><br><font color=green size=4><b>"+FbResourceUtil.getLabel("This_is_your_Achievements_Information")+"</b></font><br>");
		str.append("<br> <font color=#0000CC size=3> "+FbResourceUtil.getLabel("Academic")+" </font>= "
				+ acedamic);

		str.append("<br> <font color=#0000CC size=3> "+FbResourceUtil.getLabel("Sports")+"</font>= "
				+ sports);

		str.append("<br> <font color=#0000CC size=3> "+FbResourceUtil.getLabel("Others")+"</font>= "
				+ others);

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

	public String getAcedamic() {
		return this.acedamic;
	}

	public void setAcedamic(String acedamic) {
		this.acedamic = acedamic;
	}

	public String getSports() {
		return this.sports;
	}

	public void setSports(String sports) {
		this.sports = sports;
	}

	public String getOthers() {
		return this.others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

}