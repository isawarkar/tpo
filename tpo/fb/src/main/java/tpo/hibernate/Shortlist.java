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
public class Shortlist implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileName;

	private String companyName;

	private Date dateOfShort;

	private String createdBy;
	

	// Constructors

	/** default constructor */
	public Shortlist() {
	}

	/** minimal constructor */
	public Shortlist(String fileName) {
		this.fileName = fileName;
	}

	/** full constructor */
	public Shortlist(String fileName, String companyName, Date dateOfShort) {
		this.fileName = fileName;
		this.companyName = companyName;
		this.dateOfShort = dateOfShort;
	}

	// Property accessors

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getDateOfShort() {
		return this.dateOfShort;
	}

	public void setDateOfShort(Date dateOfShort) {
		this.dateOfShort = dateOfShort;
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
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
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
		Shortlist other = (Shortlist) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}
}