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
public class Student implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private String rollnumber;

	private Boolean isApplied = false;

	private Boolean isApproved = false;
	
	private String appliedOn;

	private String approvedOn;
	
	private HallTicket hallTicket;
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hallTicket == null) ? 0 : hallTicket.hashCode());
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
		Student other = (Student) obj;
		if (hallTicket == null) {
			if (other.hallTicket != null)
				return false;
		} else if (!hallTicket.equals(other.hallTicket))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String applied = isApplied ? "YES" : "NO";
		String approved = isApproved ? "YES" : "NO";
		buffer.append(hallTicket.toString()).append("<br>").append("&nbsp;&nbsp;&nbsp;<font color='red'>Applied:</font>" + applied)
		.append("<br>")
		.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Approved:</font>" + approved);
		if(!"N".equals(appliedOn)) {
			buffer.append("<br>")
			.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Applied On:</font>" + appliedOn);	
		}
		if(!"N".equals(approvedOn)) {
			buffer.append("<br>")
			.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Approved On:</font>" + approvedOn);	
		}
		return buffer.toString();
	}

	public Boolean getIsApplied() {
		return isApplied;
	}

	public void setIsApplied(Boolean isApplied) {
		this.isApplied = isApplied;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getAppliedOn() {
		return appliedOn;
	}

	public void setAppliedOn(String appliedOn) {
		this.appliedOn = appliedOn;
	}

	public String getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(String approvedOn) {
		this.approvedOn = approvedOn;
	}

	public HallTicket getHallTicket() {
		return hallTicket;
	}

	public void setHallTicket(HallTicket hallTicket) {
		this.hallTicket = hallTicket;
	}

	public String getRollnumber() {
		return rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}
	
	

}