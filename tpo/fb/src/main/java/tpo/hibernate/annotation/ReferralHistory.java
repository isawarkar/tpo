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
@Table(name = "referralhistory")
public class ReferralHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "referred")
	private String referred;
	
	@Column(name = "referredBY")
	private String referredBY;

	@Column(name = "date")
	private Date date;



	public String getReferredBY() {
		return referredBY;
	}

	public void setReferredBY(String referredBY) {
		this.referredBY = referredBY;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getReferred() {
		return referred;
	}

	public void setReferred(String referred) {
		this.referred = referred;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((referred == null) ? 0 : referred.hashCode());
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
		ReferralHistory other = (ReferralHistory) obj;
		if (referred == null) {
			if (other.referred != null)
				return false;
		} else if (!referred.equals(other.referred))
			return false;
		return true;
	}
	
	

	
}