/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate;

import java.io.Serializable;

/**
 * @author Uddanda Technologies
 */
public class HallTicketConnectID implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private HallTicket hallTicket;

	public String getRollnumber() {
		return rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public HallTicket getHallTicket() {
		return hallTicket;
	}

	public void setHallTicket(HallTicket hallTicket) {
		this.hallTicket = hallTicket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hallTicket == null) ? 0 : hallTicket.hashCode());
		result = prime * result
				+ ((rollnumber == null) ? 0 : rollnumber.hashCode());
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
		HallTicketConnectID other = (HallTicketConnectID) obj;
		if (hallTicket == null) {
			if (other.hallTicket != null)
				return false;
		} else if (!hallTicket.equals(other.hallTicket))
			return false;
		if (rollnumber == null) {
			if (other.rollnumber != null)
				return false;
		} else if (!rollnumber.equals(other.rollnumber))
			return false;
		return true;
	}

}