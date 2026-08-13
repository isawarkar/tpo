/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate.annotation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Uddanda Technologies
 */
@Entity
@Table(name = "investment")
public class InvestmentDetail {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "userName")
	private String userName;
	
	@Column(name = "contactNumber")
	private String contactNumber;
	
	@Column(name = "money")
	private Integer amountPaid;
	
	@Column(name = "currentMonthInt")
	private Double currentMonthInt;
	
	@Column(name = "totalInt")
	private Double totalInt;

	@Column(name = "dueDate")
	private Date dueDate;
	
	@Column(name = "noOfDaysDue")
	private Integer noOfDaysDue=0;

	@Column(name = "address")
	private String address;
	
	@Column(name = "percent")
	private Integer percent = 0;
	
	@Column(name = "reminderOn")
	private Boolean reminderOn;
	
	@Column(name = "extraInt")
	private Double extraInt;
	
	@Column(name = "lastCalculated")
	private Date lastCalculated;
	
	@Column(name = "note")
	private String note;
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Integer getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Integer amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Double getCurrentMonthInt() {
		return currentMonthInt;
	}

	public void setCurrentMonthInt(Double currentMonthInt) {
		this.currentMonthInt = currentMonthInt;
	}

	public Double getTotalInt() {
		return totalInt;
	}

	public void setTotalInt(Double totalInt) {
		this.totalInt = totalInt;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getNoOfDaysDue() {
		return noOfDaysDue;
	}

	public void setNoOfDaysDue(Integer noOfDaysDue) {
		this.noOfDaysDue = noOfDaysDue;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

	public Boolean getReminderOn() {
		return reminderOn;
	}

	public void setReminderOn(Boolean reminderOn) {
		this.reminderOn = reminderOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contactNumber == null) ? 0 : contactNumber.hashCode());
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
		InvestmentDetail other = (InvestmentDetail) obj;
		if (contactNumber == null) {
			if (other.contactNumber != null)
				return false;
		} else if (!contactNumber.equals(other.contactNumber))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	public Double getExtraInt() {
		return extraInt;
	}

	public void setExtraInt(Double extraInt) {
		this.extraInt = extraInt;
	}

	public Date getLastCalculated() {
		return lastCalculated;
	}

	public void setLastCalculated(Date lastCalculated) {
		this.lastCalculated = lastCalculated;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	
}