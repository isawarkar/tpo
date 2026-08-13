/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Uddanda Technologies
 */
public class Exam implements Serializable {
	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String testname;
	private Integer passingcriteria;
	private Integer startrange;
	private Integer endrange;
	private Integer noOfQuestions;
	private Integer minute;
	private String createdBy;
	private String resultType = "Percent";
	
	private Double negativeMark;
	private Integer firstClassMark;
	private Integer honoursMark;
	private Integer numberOfQuestions;
	
	private Boolean allowCertDownload;
	private Date validFrom;
	private Date validTo;
	private Boolean showResult;
	// Constructors

	/** default constructor */
	public Exam() {
	}

	/** minimal constructor */
	public Exam(String testname) {
		this.testname = testname;
	}

	// Property accessors

	public String getTestname() {
		return this.testname;
	}

	public void setTestname(String testname) {
		this.testname = testname;
	}

	public Integer getPassingcriteria() {
		return this.passingcriteria;
	}

	public void setPassingcriteria(Integer passingcriteria) {
		this.passingcriteria = passingcriteria;
	}

	public Integer getStartrange() {
		return this.startrange;
	}

	public void setStartrange(Integer startrange) {
		this.startrange = startrange;
	}

	public Integer getEndrange() {
		return this.endrange;
	}

	public void setEndrange(Integer endrange) {
		this.endrange = endrange;
	}

	/**
	 * @return the noOfQuestions
	 */
	public synchronized Integer getNoOfQuestions() {
		return noOfQuestions;
	}

	/**
	 * @param noOfQuestions
	 *            the noOfQuestions to set
	 */
	public synchronized void setNoOfQuestions(Integer noOfQuestions) {
		this.noOfQuestions = noOfQuestions;
	}

	/**
	 * @return the minute
	 */
	public synchronized Integer getMinute() {
		return minute;
	}

	/**
	 * @param minute
	 *            the minute to set
	 */
	public synchronized void setMinute(Integer minute) {
		this.minute = minute;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((testname == null) ? 0 : testname.hashCode());
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
		Exam other = (Exam) obj;
		if (testname == null) {
			if (other.testname != null)
				return false;
		} else if (!testname.equals(other.testname))
			return false;
		return true;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public Double getNegativeMark() {
		return negativeMark;
	}

	public void setNegativeMark(Double negativeMark) {
		this.negativeMark = negativeMark;
	}

	public Integer getFirstClassMark() {
		return firstClassMark;
	}

	public void setFirstClassMark(Integer firstClassMark) {
		this.firstClassMark = firstClassMark;
	}

	public Integer getHonoursMark() {
		return honoursMark;
	}

	public void setHonoursMark(Integer honoursMark) {
		this.honoursMark = honoursMark;
	}

	public Boolean getAllowCertDownload() {
		return allowCertDownload;
	}

	public void setAllowCertDownload(Boolean allowCertDownload) {
		this.allowCertDownload = allowCertDownload;
	}

	public Integer getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(Integer numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Boolean getShowResult() {
		return showResult;
	}

	public void setShowResult(Boolean showResult) {
		this.showResult = showResult;
	}
	
	public String getClassName(){
		if(validTo !=null && validTo.before(Calendar.getInstance().getTime())){
			return "blinking";
		}
		return "";
	}
	

}