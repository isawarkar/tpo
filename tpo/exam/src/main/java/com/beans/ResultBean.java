package com.beans;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hibernate.Exam;
import com.util.TpoUtil;

@Component("ResultBean")
@Scope("session")
public class ResultBean {

	private String name;
	private String criteria;
	private double percent;
	private String result;
	private String resultIn;
	private Exam exam;
	private byte[] certificate;
	
	private String certFileName;
	
	private String error;
	
	private String firstName;

	private String lastName;

	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	
	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}

	public String getResultIn() {
		return resultIn;
	}

	public void setResultIn(String resultIn) {
		this.resultIn = resultIn;
	}

	
	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public void dowanloadCertificate() {
		TpoUtil.renderPDFFile(certificate, certFileName);
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public String getCertFileName() {
		return certFileName;
	}

	public void setCertFileName(String certFileName) {
		this.certFileName = certFileName;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	
	
}
