/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Uddanda Technologies
 */
public class College implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String collegeName;

	private String collegeFullName;

	private String place;

	private String university;

	private Date dateOfOpening;

	private String emailAddress;

	private String siteAddress;

	private String address;

	private Logindetails logindetails;

	// Constructors

	/** default constructor */
	public College() {
	}

	/** minimal constructor */
	public College(String collegeName, String place, String University,
			Date dateOfOpening, String emailAddress, String address) {
		this.collegeName = collegeName;
		this.place = place;
		this.university = University;
		this.dateOfOpening = dateOfOpening;
		this.emailAddress = emailAddress;
		this.address = address;
	}

	/** full constructor */
	public College(String collegeName, String place, String University,
			Date dateOfOpening, String emailAddress, String siteAddress,
			String address) {
		this.collegeName = collegeName;
		this.place = place;
		this.university = University;
		this.dateOfOpening = dateOfOpening;
		this.emailAddress = emailAddress;
		this.siteAddress = siteAddress;
		this.address = address;
	}

	// Property accessors

	public String getCollegeName() {
		return this.collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getUniversity() {
		return this.university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public Date getDateOfOpening() {
		return this.dateOfOpening;
	}

	public void setDateOfOpening(Date dateOfOpening) {
		this.dateOfOpening = dateOfOpening;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getSiteAddress() {
		return this.siteAddress;
	}

	public void setSiteAddress(String siteAddress) {
		this.siteAddress = siteAddress;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Logindetails getLogindetails() {
		return logindetails;
	}

	public void setLogindetails(Logindetails logindetails) {
		this.logindetails = logindetails;
	}

	public String getCollegeFullName() {
		return collegeFullName;
	}

	public void setCollegeFullName(String collegeFullName) {
		this.collegeFullName = collegeFullName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collegeName == null) ? 0 : collegeName.hashCode());
		result = prime * result
				+ ((dateOfOpening == null) ? 0 : dateOfOpening.hashCode());
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
		College other = (College) obj;
		if (collegeName == null) {
			if (other.collegeName != null)
				return false;
		} else if (!collegeName.equals(other.collegeName))
			return false;
		if (dateOfOpening == null) {
			if (other.dateOfOpening != null)
				return false;
		} else if (!dateOfOpening.equals(other.dateOfOpening))
			return false;
		return true;
	}

}