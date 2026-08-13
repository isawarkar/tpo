/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.hibernate;

import java.io.Serializable;
import java.util.Date;

import com.util.FbResourceUtil;

/**
 * @author Uddanda Technologies
 */
public class Contactinfo implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private Registration registration;

	private String phoneNumber;

	private String mobileNumber;

	private Boolean numberVerified;

	private String presentAddress;

	private String presentCity;

	private String presentState;

	private String permanentAddress;

	private String permanentCity;

	private String permanentState;

	private Double hieght;

	private Integer weight;

	private String glassPowerLeft;

	private String glassPowerRight;

	private Date lastUpdated;

	private String lastUpdatedBy;

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(
				"<br><br><font color=green size=4><b>"+FbResourceUtil.getLabel("This_is_your_Contact_Information")+"</b></font><br>");

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Phone_Number")+" </font>= "
				+ phoneNumber);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Mobile_Number")+" </font>= "
				+ mobileNumber);
		
		if (numberVerified) {
			str.append("<br> <font color=green size=3> "+FbResourceUtil.getLabel("Verified_Mobile_Number")+" </font>= "+FbResourceUtil.getLabel("YES")+"");
		} else {
			str.append("<br> <font color=red size=3> "+FbResourceUtil.getLabel("Verified_Mobile_Number")+"</font>= "+FbResourceUtil.getLabel("NO")+"");
		}

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Present_Address")+" </font>= "
				+ presentAddress);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Present_City")+" </font>= "
				+ presentCity);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Present_State")+" </font>= "
				+ presentState);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Permanent_Address")+" </font>= "
				+ permanentAddress);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Permanent_City")+"</font>= "
				+ permanentCity);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Permanent_State")+"</font>= "
				+ permanentState);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Height")+" </font>= " + hieght);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Weight")+"Weight </font>= " + weight);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Glass_Power_L")+" </font>= "
				+ glassPowerLeft);

		str.append("<br> <font color=navy size=3> "+FbResourceUtil.getLabel("Glass_Power_R")+"</font>= "
				+ glassPowerRight);

		return str.toString();
	}

	// Property accessors

	public String getRollnumber() {
		return this.rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public Registration getRegistration() {
		return this.registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPresentAddress() {
		return this.presentAddress;
	}

	public void setPresentAddress(String presentAddress) {
		this.presentAddress = presentAddress;
	}

	public String getPresentCity() {
		return this.presentCity;
	}

	public void setPresentCity(String presentCity) {
		this.presentCity = presentCity;
	}

	public String getPresentState() {
		return this.presentState;
	}

	public void setPresentState(String presentState) {
		this.presentState = presentState;
	}

	public String getPermanentAddress() {
		return this.permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public String getPermanentCity() {
		return this.permanentCity;
	}

	public void setPermanentCity(String permanentCity) {
		this.permanentCity = permanentCity;
	}

	public String getPermanentState() {
		return this.permanentState;
	}

	public void setPermanentState(String permanentState) {
		this.permanentState = permanentState;
	}

	public Double getHieght() {
		return this.hieght;
	}

	public void setHieght(Double hieght) {
		this.hieght = hieght;
	}

	public Integer getWeight() {
		return this.weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getGlassPowerLeft() {
		return this.glassPowerLeft;
	}

	public void setGlassPowerLeft(String glassPowerLeft) {
		this.glassPowerLeft = glassPowerLeft;
	}

	public String getGlassPowerRight() {
		return this.glassPowerRight;
	}

	public void setGlassPowerRight(String glassPowerRight) {
		this.glassPowerRight = glassPowerRight;
	}

	public Boolean getNumberVerified() {
		return numberVerified;
	}

	public void setNumberVerified(Boolean numberVerified) {
		this.numberVerified = numberVerified;
	}

}