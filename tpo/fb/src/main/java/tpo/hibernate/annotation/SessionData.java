package tpo.hibernate.annotation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sessiondata")
public class SessionData {
	
	@Id
	@Column(name = "enrollmentNumber")
	private String enrollmentNumber;
	
	@Column(name = "timeLeft")
	private Double timeLeft;
	
	@Column(name = "totalQuestions")
	private int totalQuestions;
	
	@Column(name = "answersSubmitted")
	private int answersSubmitted;
	
	@Column(name = "correctAns")
	private int correctAns;
	
	@Column(name = "percentageOrNumber")
	private Double  percentageOrNumber;
	
	@Column(name = "result")
	private String result;
	
	@Column(name = "attempt")
	private int attempt;
	
	@Column(name = "createdBy")
	private String createdBy;
	
	@Column(name = "createdDate")
	private Date createdDate;

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((enrollmentNumber == null) ? 0 : enrollmentNumber.hashCode());
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
		SessionData other = (SessionData) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (enrollmentNumber == null) {
			if (other.enrollmentNumber != null)
				return false;
		} else if (!enrollmentNumber.equals(other.enrollmentNumber))
			return false;
		return true;
	}

	public String getEnrollmentNumber() {
		return enrollmentNumber;
	}

	public void setEnrollmentNumber(String enrollmentNumber) {
		this.enrollmentNumber = enrollmentNumber;
	}

	public Double getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(Double timeLeft) {
		this.timeLeft = timeLeft;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(int totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public int getAnswersSubmitted() {
		return answersSubmitted;
	}

	public void setAnswersSubmitted(int answersSubmitted) {
		this.answersSubmitted = answersSubmitted;
	}

	public int getCorrectAns() {
		return correctAns;
	}

	public void setCorrectAns(int correctAns) {
		this.correctAns = correctAns;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Double getPercentageOrNumber() {
		return percentageOrNumber;
	}

	public void setPercentageOrNumber(Double percentageOrNumber) {
		this.percentageOrNumber = percentageOrNumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
	
}
