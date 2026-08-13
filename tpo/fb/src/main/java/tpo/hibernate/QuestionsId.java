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

public class QuestionsId implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer qno;

	private String qtype;

	// Constructors

	/** default constructor */
	public QuestionsId() {
	}

	/** full constructor */
	public QuestionsId(Integer qno, String qtype) {
		this.qno = qno;
		this.qtype = qtype;
	}

	// Property accessors

	public Integer getQno() {
		return this.qno;
	}

	public void setQno(Integer qno) {
		this.qno = qno;
	}

	public String getQtype() {
		return this.qtype;
	}

	public void setQtype(String qtype) {
		this.qtype = qtype;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof QuestionsId))
			return false;
		QuestionsId castOther = (QuestionsId) other;

		return ((this.getQno() == castOther.getQno()) || (this.getQno() != null
				&& castOther.getQno() != null && this.getQno().equals(
				castOther.getQno())))
				&& ((this.getQtype() == castOther.getQtype()) || (this
						.getQtype() != null && castOther.getQtype() != null && this
						.getQtype().equals(castOther.getQtype())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getQno() == null ? 0 : this.getQno().hashCode());
		result = 37 * result
				+ (getQtype() == null ? 0 : this.getQtype().hashCode());
		return result;
	}

}