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
public class Userdetails implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;

	private Logindetails logindetails;

	private String firstName;

	private String lastName;

	private String address;

	private String email;

	private Long mobleNo;

	private String gender;

	private String website;

	
	private String details;
	private Boolean numberVerified;
	private Boolean emailVarified;

	// Constructors

	/** default constructor */
	public Userdetails() {
	}

	/** minimal constructor */
	public Userdetails(String userName, Logindetails logindetails, String firstName, String lastName, String email,
			Long mobleNo) {
		this.userName = userName;
		this.logindetails = logindetails;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobleNo = mobleNo;
	}

	/** full constructor */
	public Userdetails(String userName, Logindetails logindetails, String firstName, String lastName, String address,
			String email, Long mobleNo, String gender) {
		this.userName = userName;
		this.logindetails = logindetails;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.email = email;
		this.mobleNo = mobleNo;
		this.gender = gender;
	}

	// Property accessors

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Logindetails getLogindetails() {
		return this.logindetails;
	}

	public void setLogindetails(Logindetails logindetails) {
		this.logindetails = logindetails;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMobleNo() {
		return this.mobleNo;
	}

	public void setMobleNo(Long mobleNo) {
		this.mobleNo = mobleNo;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Boolean getNumberVerified() {
		return numberVerified;
	}

	public void setNumberVerified(Boolean numberVerified) {
		this.numberVerified = numberVerified;
	}

	public Boolean getEmailVarified() {
		return emailVarified;
	}

	public void setEmailVarified(Boolean emailVarified) {
		this.emailVarified = emailVarified;
	}
	

}