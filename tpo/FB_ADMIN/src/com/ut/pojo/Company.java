/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.ut.pojo;

import java.io.Serializable;

/**
 * @author Uddanda Technologies
 */
public class Company implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer companyID;
	private String companyname;
	private String profile;
	private byte[] logo;
	private String domain;
	private String website;
	private String linkedIn;
	private String twiter;
	private String glassdoor;
	private String facebook;
	private String email;
	private String remarks;

	public Integer getCompanyID() {
		return companyID;
	}

	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getTwiter() {
		return twiter;
	}

	public void setTwiter(String twiter) {
		this.twiter = twiter;
	}

	public String getGlassdoor() {
		return glassdoor;
	}

	public void setGlassdoor(String glassdoor) {
		this.glassdoor = glassdoor;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyID == null) ? 0 : companyID.hashCode());
		result = prime * result + ((companyname == null) ? 0 : companyname.hashCode());
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
		Company other = (Company) obj;
		if (companyID == null) {
			if (other.companyID != null)
				return false;
		} else if (!companyID.equals(other.companyID))
			return false;
		if (companyname == null) {
			if (other.companyname != null)
				return false;
		} else if (!companyname.equals(other.companyname))
			return false;
		return true;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getProfile() {
		return profile;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<font color='red'>E-mail</font>:" + email).append("<br>")
				.append("<div style='text-align: justify;text-justify: inter-word;'>").append(profile)
				.append("<u><h1>Remarks</h1></u>").append("<div style='text-align: justify;text-justify: inter-word;'>").append(remarks).append("</div>");
		return buffer.toString();
	}

}