/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.hibernate;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.lowagie.text.pdf.codec.Base64;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class Result implements Serializable, JSONAware {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ResultId id;

	private Double totalnumbers;

	private String result;

	private String testName;

	private Date dateTaken;

	private Double totalTimeTaken;

	private Integer numberOfQuestion;

	private Double totalTime;

	private String questions;

	private String createdBy;

	private boolean showResult;

	private boolean certificateAvialable;

	// Constructors

	/** default constructor */
	public Result() {
	}

	/** minimal constructor */
	public Result(ResultId id, Timestamp dateTaken, Double totalTimeTaken, Integer numberOfQuestion) {
		this.id = id;
		this.dateTaken = dateTaken;
		this.totalTimeTaken = totalTimeTaken;
		this.numberOfQuestion = numberOfQuestion;
	}

	/** full constructor */
	public Result(ResultId id, Double totalnumbers, String result, String testName, Timestamp dateTaken,
			Double totalTimeTaken, Integer numberOfQuestion) {
		this.id = id;
		this.totalnumbers = totalnumbers;
		this.result = result;
		this.testName = testName;
		this.dateTaken = dateTaken;
		this.totalTimeTaken = totalTimeTaken;
		this.numberOfQuestion = numberOfQuestion;
	}

	// Property accessors

	public ResultId getId() {
		return this.id;
	}

	public void setId(ResultId id) {
		this.id = id;
	}

	public Double getTotalnumbers() {
		return this.totalnumbers;
	}

	public void setTotalnumbers(Double totalnumbers) {
		this.totalnumbers = totalnumbers;
	}

	public String getResult() {
		return this.result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTestName() {
		return this.testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public Date getDateTaken() {
		return this.dateTaken;
	}

	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}

	public Double getTotalTimeTaken() {
		return this.totalTimeTaken;
	}

	public void setTotalTimeTaken(Double totalTimeTaken) {
		this.totalTimeTaken = totalTimeTaken;
	}

	public Integer getNumberOfQuestion() {
		return this.numberOfQuestion;
	}

	public void setNumberOfQuestion(Integer numberOfQuestion) {
		this.numberOfQuestion = numberOfQuestion;
	}

	/**
	 * @return the totalTime
	 */
	public synchronized Double getTotalTime() {
		return totalTime;
	}

	/**
	 * @param totalTime the totalTime to set
	 */
	public synchronized void setTotalTime(Double totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * @return the questions
	 */
	public String getQuestions() {
		return questions;
	}

	/**
	 * @param questions the questions to set
	 */
	public void setQuestions(String questions) {
		this.questions = questions;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((dateTaken == null) ? 0 : dateTaken.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((numberOfQuestion == null) ? 0 : numberOfQuestion.hashCode());
		result = prime * result + ((questions == null) ? 0 : questions.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (numberOfQuestion == null) {
			if (other.numberOfQuestion != null)
				return false;
		} else if (!numberOfQuestion.equals(other.numberOfQuestion))
			return false;
		if (questions == null) {
			if (other.questions != null)
				return false;
		} else if (!questions.equals(other.questions))
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

	public boolean isShowResult() {
		return showResult;
	}

	public void setShowResult(boolean showResult) {
		this.showResult = showResult;
	}

	public boolean isCertificateAvialable() {
		return certificateAvialable;
	}

	public void setCertificateAvialable(boolean certificateAvialable) {
		this.certificateAvialable = certificateAvialable;
	}

	private byte[] certificate = null;
	
	

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("enrollmentNo", id.getLoginname());
		obj.put("attempt", id.getAttempt());
		obj.put("totalnumbers", totalnumbers);
		obj.put("result", result);
		obj.put("testName", testName);
		obj.put("dateTaken", TpoUtil.getDateToStringYYYYMMddHHmmss(dateTaken));
		obj.put("totalTimeTaken", totalTimeTaken);
		obj.put("numberOfQuestion", numberOfQuestion);
		obj.put("totalTime", totalTime);
		obj.put("createdBy", createdBy);
		if (certificate != null) {
			obj.put("certificateAvialable", true);
			obj.put("certificate", Base64.encodeBytes(certificate));
		} else {
			obj.put("certificateAvialable", false);
			obj.put("certificate", "");
		}
		return obj.toString();
	}
}