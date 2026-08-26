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
public class FeeReminder implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Double amountPaid;
	private String paidOn;
	private Double amountDue;
	private String dueOn;
	
	



	public Integer getId() {
		return id;
	}





	public void setId(Integer id) {
		this.id = id;
	}





	public Double getAmountPaid() {
		return amountPaid;
	}





	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}





	public String getPaidOn() {
		return paidOn;
	}





	public void setPaidOn(String paidOn) {
		this.paidOn = paidOn;
	}





	public Double getAmountDue() {
		return amountDue;
	}





	public void setAmountDue(Double amountDue) {
		this.amountDue = amountDue;
	}





	public String getDueOn() {
		return dueOn;
	}





	public void setDueOn(String dueOn) {
		this.dueOn = dueOn;
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amountDue == null) ? 0 : amountDue.hashCode());
		result = prime * result + ((amountPaid == null) ? 0 : amountPaid.hashCode());
		result = prime * result + ((dueOn == null) ? 0 : dueOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((paidOn == null) ? 0 : paidOn.hashCode());
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
		FeeReminder other = (FeeReminder) obj;
		if (amountDue == null) {
			if (other.amountDue != null)
				return false;
		} else if (!amountDue.equals(other.amountDue))
			return false;
		if (amountPaid == null) {
			if (other.amountPaid != null)
				return false;
		} else if (!amountPaid.equals(other.amountPaid))
			return false;
		if (dueOn == null) {
			if (other.dueOn != null)
				return false;
		} else if (!dueOn.equals(other.dueOn))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (paidOn == null) {
			if (other.paidOn != null)
				return false;
		} else if (!paidOn.equals(other.paidOn))
			return false;
		return true;
	}





	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Reminder ID : </font>" + id).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Last Amount Paid:</font>" + amountPaid).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Last Paid On:</font>" + paidOn).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Amount Due:</font>" + amountDue).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Due On:</font>" + dueOn).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Note:Pay before Due date to avoid late fee.</font>").append("<br>");
				return buffer.toString();
	}

}