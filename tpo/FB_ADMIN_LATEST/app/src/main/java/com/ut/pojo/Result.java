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
public class Result implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String loginname;

	private String attempt;

	private String totalnumbers;

	private String result;

	private String testName;

	private String dateTaken;

	private String totalTimeTaken;

	private String numberOfQuestion;

	private String totalTime;

	private String createdBy;

	private boolean certificateAvialable;
	
	private byte[] certificate;

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getAttempt() {
		return attempt;
	}

	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}

	public String getTotalnumbers() {
		return totalnumbers;
	}

	public void setTotalnumbers(String totalnumbers) {
		this.totalnumbers = totalnumbers;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(String dateTaken) {
		this.dateTaken = dateTaken;
	}

	public String getTotalTimeTaken() {
		return totalTimeTaken;
	}

	public void setTotalTimeTaken(String totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}

	public String getNumberOfQuestion() {
		return numberOfQuestion;
	}

	public void setNumberOfQuestion(String numberOfQuestion) {
		this.numberOfQuestion = numberOfQuestion;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public boolean getCertificateAvialable() {
		return certificateAvialable;
	}

	public void setCertificateAvialable(boolean certificateAvialable) {
		this.certificateAvialable = certificateAvialable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attempt == null) ? 0 : attempt.hashCode());
		result = prime * result + (certificateAvialable ? 1231 : 1237);
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((dateTaken == null) ? 0 : dateTaken.hashCode());
		result = prime * result + ((loginname == null) ? 0 : loginname.hashCode());
		result = prime * result + ((numberOfQuestion == null) ? 0 : numberOfQuestion.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((testName == null) ? 0 : testName.hashCode());
		result = prime * result + ((totalTime == null) ? 0 : totalTime.hashCode());
		result = prime * result + ((totalTimeTaken == null) ? 0 : totalTimeTaken.hashCode());
		result = prime * result + ((totalnumbers == null) ? 0 : totalnumbers.hashCode());
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
		Result other = (Result) obj;
		if (attempt == null) {
			if (other.attempt != null)
				return false;
		} else if (!attempt.equals(other.attempt))
			return false;
		if (certificateAvialable != other.certificateAvialable)
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (dateTaken == null) {
			if (other.dateTaken != null)
				return false;
		} else if (!dateTaken.equals(other.dateTaken))
			return false;
		if (loginname == null) {
			if (other.loginname != null)
				return false;
		} else if (!loginname.equals(other.loginname))
			return false;
		if (numberOfQuestion == null) {
			if (other.numberOfQuestion != null)
				return false;
		} else if (!numberOfQuestion.equals(other.numberOfQuestion))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (testName == null) {
			if (other.testName != null)
				return false;
		} else if (!testName.equals(other.testName))
			return false;
		if (totalTime == null) {
			if (other.totalTime != null)
				return false;
		} else if (!totalTime.equals(other.totalTime))
			return false;
		if (totalTimeTaken == null) {
			if (other.totalTimeTaken != null)
				return false;
		} else if (!totalTimeTaken.equals(other.totalTimeTaken))
			return false;
		if (totalnumbers == null) {
			if (other.totalnumbers != null)
				return false;
		} else if (!totalnumbers.equals(other.totalnumbers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String cert = "NO";
		if(certificateAvialable) {
			cert = "YES";
		}
		buffer.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Test Name:</font>" + testName).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Result:</font>" + result).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>No of Attempt:</font>" + attempt).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Total Number's:</font>" + totalnumbers).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Date Taken:</font>" + dateTaken).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Total Time(MM):</font>" + totalTime).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Total Time Taken(MM):</font>" + totalTimeTaken).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Number Of Question:</font>" + numberOfQuestion)
				/*
				 * .append("<br>").
				 * append("&nbsp;&nbsp;&nbsp;<font color='blue'>Created By:</font>" + createdBy)
				 */
				.append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Certificate Avialable:</font>" + cert);
		return buffer.toString();
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	
}