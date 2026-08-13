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
public class ResultId implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String loginname;

	private Integer attempt;

	// Constructors

	/** default constructor */
	public ResultId() {
	}

	/** full constructor */
	public ResultId(String loginname, Integer attempt) {
		this.loginname = loginname;
		this.attempt = attempt;
	}

	// Property accessors

	public String getLoginname() {
		return this.loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public Integer getAttempt() {
		return this.attempt;
	}

	public void setattempt(Integer attempt) {
		this.attempt = attempt;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ResultId))
			return false;
		ResultId castOther = (ResultId) other;

		return ((this.getLoginname() == castOther.getLoginname()) || (this
				.getLoginname() != null && castOther.getLoginname() != null && this
				.getLoginname().equals(castOther.getLoginname())))
				&& ((this.getAttempt() == castOther.getAttempt()) || (this
						.getAttempt() != null && castOther.getAttempt() != null && this
						.getAttempt().equals(castOther.getAttempt())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getLoginname() == null ? 0 : this.getLoginname().hashCode());
		result = 37 * result
				+ (getAttempt() == null ? 0 : this.getAttempt().hashCode());
		return result;
	}

}