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

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class HallTicketConnect implements Serializable, JSONAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HallTicketConnectID id;

	private Boolean isApplied = false;

	private Boolean isApproved = false;

	private Date appliedOn;

	private Date approvedOn;
	
	private Boolean arrived;

	public HallTicketConnectID getId() {
		return id;
	}

	public void setId(HallTicketConnectID id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		HallTicketConnect other = (HallTicketConnect) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

	public Date getAppliedOn() {
		return appliedOn;
	}

	public void setAppliedOn(Date appliedOn) {
		this.appliedOn = appliedOn;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		HallTicket hallTicket = id.getHallTicket();
		JSONObject obj = new JSONObject();
		obj.put("rollNumber", id.getRollnumber());
		obj.put("hallTicket", hallTicket);
		obj.put("isApplied", isApplied);
		obj.put("arrived", arrived);
		if (appliedOn != null) {
			obj.put("appliedOn", TpoUtil.getDateToStringInddmmyyyyHHmmSS(appliedOn));
		} else {
			obj.put("appliedOn", "N");
		}
		obj.put("isApproved", isApproved);
		if (approvedOn != null) {
			obj.put("approvedOn", TpoUtil.getDateToStringInddmmyyyyHHmmSS(approvedOn));
		} else {
			obj.put("approvedOn", "N");
		}
		return obj.toString();
	}

	public Boolean getArrived() {
		return arrived;
	}

	public void setArrived(Boolean arrived) {
		this.arrived = arrived;
	}
	
	

}