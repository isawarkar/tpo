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
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class HallTicket implements Serializable, JSONAware {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer hallTicketId;

	private Integer companyID;

	private String companyName;
	
	private String userName;

	private Date date;

	private String time;

	private Boolean isActive;

	private String packageOffering;
	
	private Date lastDateToApply;
	
	private String criteria;
	
	private Boolean allowDigitalSignature;
	
	private String digitalSignature;
	
	private String interviewLocation;
	
	private String postingLocation;
	
	private String role;
	
	private Company company;
	
	private Set<HallTicketConnect> hallTicketConnect = new HashSet<HallTicketConnect>(0);
	
	int totalApplied;
	int totalArrived;
	int totalApproved;
	int totalShortListed;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Set<HallTicketConnect> getHallTicketConnect() {
		return hallTicketConnect;
	}

	public void setHallTicketConnect(Set<HallTicketConnect> hallTicketConnect) {
		this.hallTicketConnect = hallTicketConnect;
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
		result = prime * result
				+ ((hallTicketId == null) ? 0 : hallTicketId.hashCode());
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
		if (hallTicketId == null) {
			if (other.hallTicketId != null)
				return false;
		} else if (!hallTicketId.equals(other.hallTicketId))
			return false;
		return true;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("hallTicketId", hallTicketId);
		obj.put("companyID", companyID);
		obj.put("companyName", companyName);
		obj.put("userName", userName);
		obj.put("date", TpoUtil.getDateToStringInddmmyyyy(date));
		obj.put("time", time);
		obj.put("packageOffering", packageOffering);
		obj.put("lastDateToApply", TpoUtil.getDateToStringInddmmyyyy(lastDateToApply));
		obj.put("criteria", criteria);
		obj.put("interviewLocation", interviewLocation);
		obj.put("postingLocation", postingLocation);
		obj.put("role", role);
		obj.put("company",company);
		obj.put("totalApplied","<font color='red' size='20'>" + totalApplied +"</font>");
		obj.put("totalArrived","<font color='yellow' size='20'>" + totalArrived +"</font>");
		obj.put("totalApproved","<font color='blue' size='20'>" + totalApproved +"</font>");
		obj.put("totalShortlisted","<font color='pink' size='20'>" + totalShortListed +"</font>");
		
		return obj.toString();
	}

	public Date getLastDateToApply() {
		return lastDateToApply;
	}

	public void setLastDateToApply(Date lastDateToApply) {
		this.lastDateToApply = lastDateToApply;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public Boolean getAllowDigitalSignature() {
		return allowDigitalSignature;
	}

	public void setAllowDigitalSignature(Boolean allowDigitalSignature) {
		this.allowDigitalSignature = allowDigitalSignature;
	}

	public String getDigitalSignature() {
		return digitalSignature;
	}

	public void setDigitalSignature(String digitalSignature) {
		this.digitalSignature = digitalSignature;
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

	public Integer getCompanyID() {
		return companyID;
	}

	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
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

	public int getTotalApplied() {
		return totalApplied;
	}

	public void setTotalApplied(int totalApplied) {
		this.totalApplied = totalApplied;
	}

	public int getTotalArrived() {
		return totalArrived;
	}

	public void setTotalArrived(int totalArrived) {
		this.totalArrived = totalArrived;
	}

	public int getTotalApproved() {
		return totalApproved;
	}

	public void setTotalApproved(int totalApproved) {
		this.totalApproved = totalApproved;
	}

	public int getTotalShortListed() {
		return totalShortListed;
	}

	public void setTotalShortListed(int totalShortListed) {
		this.totalShortListed = totalShortListed;
	}
	
	
	

	
}