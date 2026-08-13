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
public class Registration implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private String firstName;

	private String lastName;

	private String email;

	private Boolean emailVarified;

	private String password;

	private Boolean approved = false;

	private String collegeName;

	private Percentageinfo percentageinfo;

	private Contactinfo contactinfo;

	private Backdetails backdetails;

	private Personalinfo personalinfo;

	private Achivements achivements;

	private Date lastUpdated;

	private String lastUpdatedBy;
	
	private Boolean status;
	
	private String theme;
	
	private Integer color;

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

	// Constructors

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(
				"<br><font color=green size=4><b>"+FbResourceUtil.getLabel("This_is_your_Registration_Information")+"</b></font>");
		str.append("<br> <font color=blue size=3> "+FbResourceUtil.getLabel("Enrollment_No")+" </font>= "
				+ rollnumber);

		str.append("<br> <font color=blue size=3> "+FbResourceUtil.getLabel("First_Name")+" </font>= "
				+ firstName);

		str.append("<br> <font color=blue size=3> "+FbResourceUtil.getLabel("Last_Name")+" </font>= "
				+ lastName);

		str.append("<br> <font color=blue size=3> "+FbResourceUtil.getLabel("E_mail")+" </font>= " + email);
		if (emailVarified) {
			str.append("<br> <font color=green size=3> "+FbResourceUtil.getLabel("Verified_EMail")+" </font>= "+FbResourceUtil.getLabel("YES")+"");
		} else {
			str.append("<br> <font color=red size=3> "+FbResourceUtil.getLabel("Verified_EMail")+"</font>= "+FbResourceUtil.getLabel("NO")+"");
		}
		str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("College_Name")+"</font>= "
				+ collegeName);

		if (approved) {
			str.append("<br> <font color=blue size=3> "+FbResourceUtil.getLabel("Status")+" </font> = "+FbResourceUtil.getLabel("Approved")+"");
		} else {
			str.append("<br> <font color=blue size=3> "+FbResourceUtil.getLabel("Status")+" </font> = "+FbResourceUtil.getLabel("Pending")+"");
		}

		return str.toString();
	}

	/** default constructor */
	public Registration() {
	}

	/** minimal constructor */
	public Registration(String rollnumber, Integer registrationNo,
			String password, Boolean approved) {
		this.rollnumber = rollnumber;
		this.password = password;
		this.approved = approved;
	}

	/** full constructor */
	public Registration(String rollnumber, String firstName, String lastName,
			Integer registrationNo, String email, String password,
			Boolean approved, Percentageinfo percentageinfo,
			Contactinfo contactinfo, Backdetails backdetails,
			Personalinfo personalinfo, Achivements achivements) {
		this.rollnumber = rollnumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.approved = approved;
		this.percentageinfo = percentageinfo;
		this.contactinfo = contactinfo;
		this.backdetails = backdetails;
		this.personalinfo = personalinfo;
		this.achivements = achivements;
	}

	// Property accessors

	public String getRollnumber() {
		return this.rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean getEmailVarified() {
		return emailVarified;
	}

	public void setEmailVarified(Boolean emailVarified) {
		this.emailVarified = emailVarified;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getApproved() {
		return this.approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Percentageinfo getPercentageinfo() {
		return this.percentageinfo;
	}

	public void setPercentageinfo(Percentageinfo percentageinfo) {
		this.percentageinfo = percentageinfo;
	}

	public Contactinfo getContactinfo() {
		return this.contactinfo;
	}

	public void setContactinfo(Contactinfo contactinfo) {
		this.contactinfo = contactinfo;
	}

	public Backdetails getBackdetails() {
		return this.backdetails;
	}

	public void setBackdetails(Backdetails backdetails) {
		this.backdetails = backdetails;
	}

	public Personalinfo getPersonalinfo() {
		return this.personalinfo;
	}

	public void setPersonalinfo(Personalinfo personalinfo) {
		this.personalinfo = personalinfo;
	}

	public Achivements getAchivements() {
		return this.achivements;
	}

	public void setAchivements(Achivements achivements) {
		this.achivements = achivements;
	}

	public String getCollegeName() {
		return this.collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rollnumber == null) ? 0 : rollnumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Registration other = (Registration) obj;
		if (rollnumber == null) {
			if (other.rollnumber != null)
				return false;
		} else if (!rollnumber.equals(other.rollnumber))
			return false;
		return true;
	}

	
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	

}