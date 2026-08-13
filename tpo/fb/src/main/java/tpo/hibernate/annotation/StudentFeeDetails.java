/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate.annotation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Entity
@Table(name = "studentfeedetails")
public class StudentFeeDetails implements Serializable,JSONAware {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "rollnumber")
	private String rollNumber;

	@Column(name = "amountPaid")
	private Double amountPaid;

	@Column(name = "paidOn")
	private Date paidOn;

	@Column(name = "amountDue")
	private Double amountDue;
	
	@Column(name = "dueOn")
	private Date dueOn;
	
	@Column(name = "reminderOn")
	private Boolean reminderOn;
	
	@Column(name = "createdBy")
	private String createdBy;

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Date getPaidOn() {
		return paidOn;
	}

	public void setPaidOn(Date paidOn) {
		this.paidOn = paidOn;
	}

	public Double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Double amountDue) {
		this.amountDue = amountDue;
	}

	public Date getDueOn() {
		return dueOn;
	}

	public void setDueOn(Date dueOn) {
		this.dueOn = dueOn;
	}

	public Boolean getReminderOn() {
		return reminderOn;
	}

	public void setReminderOn(Boolean reminderOn) {
		this.reminderOn = reminderOn;
	}

	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amountDue == null) ? 0 : amountDue.hashCode());
		result = prime * result + ((amountPaid == null) ? 0 : amountPaid.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((dueOn == null) ? 0 : dueOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((paidOn == null) ? 0 : paidOn.hashCode());
		result = prime * result + ((reminderOn == null) ? 0 : reminderOn.hashCode());
		result = prime * result + ((rollNumber == null) ? 0 : rollNumber.hashCode());
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
		StudentFeeDetails other = (StudentFeeDetails) obj;
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
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
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
		if (reminderOn == null) {
			if (other.reminderOn != null)
				return false;
		} else if (!reminderOn.equals(other.reminderOn))
			return false;
		if (rollNumber == null) {
			if (other.rollNumber != null)
				return false;
		} else if (!rollNumber.equals(other.rollNumber))
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("amountPaid", amountPaid);
		obj.put("paidOn", TpoUtil.getDateToStringYYYYMMddHHmmss(paidOn));
		obj.put("amountDue", amountDue);
		obj.put("dueOn", TpoUtil.getDateToStringYYYYMMddHHmmss(dueOn));
		return obj.toString();
	}

	
}