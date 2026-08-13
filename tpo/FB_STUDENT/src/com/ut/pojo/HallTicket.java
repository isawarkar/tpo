/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.ut.pojo;

/**
 * @author Uddanda Technologies
 */
public class HallTicket implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;

	public Integer hallTicketId;

	public String companyName;

	public String userName;

	public String date;

	public String time;

	public String packageOffering;

	public String lastDateToApply;

	public String criteria;

	private String interviewLocation;

	private String postingLocation;

	private String role;

	private Company company;

	public Integer getHallTicketId() {
		return hallTicketId;
	}

	public void setHallTicketId(Integer hallTicketId) {
		this.hallTicketId = hallTicketId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPackageOffering() {
		return packageOffering;
	}

	public void setPackageOffering(String packageOffering) {
		this.packageOffering = packageOffering;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((hallTicketId == null) ? 0 : hallTicketId.hashCode());
		result = prime * result + ((packageOffering == null) ? 0 : packageOffering.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		HallTicket other = (HallTicket) obj;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (hallTicketId == null) {
			if (other.hallTicketId != null)
				return false;
		} else if (!hallTicketId.equals(other.hallTicketId))
			return false;
		if (packageOffering == null) {
			if (other.packageOffering != null)
				return false;
		} else if (!packageOffering.equals(other.packageOffering))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<u><h1>Company Details</h1></u>").append("<font color='blue'>Date:</font>" + date).append("<br>")
				.append("<font color='red'>Time:</font>" + time).append("<br>")
				.append("<font color='blue'>Package Offering</font>:" + packageOffering).append("<br>")
				.append("<font color='red'>Last Date to Apply</font>:" + lastDateToApply).append("<br>")
				.append("<font color='blue'>Interview Location</font>:" + interviewLocation).append("<br>")
				.append("<font color='red'>Posting Location</font>:" + postingLocation).append("<br>")
				.append(company.toString()).append("<br>")
				.append("<u><h1>Selection Criteria</h1></u>").append("<br>" + criteria);
		return buffer.toString();
	}

	public String getLastDateToApply() {
		return lastDateToApply;
	}

	public void setLastDateToApply(String lastDateToApply) {
		this.lastDateToApply = lastDateToApply;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getInterviewLocation() {
		return interviewLocation;
	}

	public void setInterviewLocation(String interviewLocation) {
		this.interviewLocation = interviewLocation;
	}

	public String getPostingLocation() {
		return postingLocation;
	}

	public void setPostingLocation(String postingLocation) {
		this.postingLocation = postingLocation;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}