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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Uddanda Technologies
 */
@Entity
@Table(name = "complaints")
public class Complaint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "complaintNumber")
	private Integer complaintNumber;

	
	@Column(name = "email")
	private String email;

	@Column(name = "contactNumber")
	private String contactNumber;

	@Column(name = "complaint")
	private String complaint;

	@Column(name = "name")
	private String name;

	@Column(name = "status")
	private Boolean status;
	
	@Column(name = "createdOn")
	private Date createdOn;
	
	@Column(name = "updatedOn")
	private Date updatedOn;


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}


	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Integer getComplaintNumber() {
		return complaintNumber;
	}

	public void setComplaintNumber(Integer complaintNumber) {
		this.complaintNumber = complaintNumber;
	}
	
	

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((complaintNumber == null) ? 0 : complaintNumber.hashCode());
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
		Complaint other = (Complaint) obj;
		if (complaintNumber == null) {
			if (other.complaintNumber != null)
				return false;
		} else if (!complaintNumber.equals(other.complaintNumber))
			return false;
		return true;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	
	
	
}