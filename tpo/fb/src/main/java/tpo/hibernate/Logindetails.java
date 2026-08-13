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
import java.util.HashSet;
import java.util.Set;

/**
 * @author Uddanda Technologies
 */
public class Logindetails implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName;

	private String password;

	private String role;

	private Boolean active;

	private Date lastLogin;

	private Integer loginAttempt;

	private Date validTill;

	private String createdBy;

	private Userdetails userdetails;

	private Boolean logoDisplay = false;
	
	private Boolean ui;

	private Set<College> colleges = new HashSet<College>(0);
	private Set<EmployeeEfforts> employeeEffortses = new HashSet<EmployeeEfforts>(0);

	// Constructors

	/** default constructor */
	public Logindetails() {
	}

	/** minimal constructor */
	public Logindetails(String userName, String role, Boolean active,
			Date lastLogin, Integer loginAttempt, Date validTill) {
		this.userName = userName;
		this.role = role;
		this.active = active;
		this.lastLogin = lastLogin;
		this.loginAttempt = loginAttempt;
		this.validTill = validTill;
	}

	/** full constructor */
	public Logindetails(String userName, String password, String role,
			Boolean active, Date lastLogin, Integer loginAttempt,
			Date validTill, Userdetails userdetails) {
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.active = active;
		this.lastLogin = lastLogin;
		this.loginAttempt = loginAttempt;
		this.validTill = validTill;
		this.userdetails = userdetails;
	}

	// Property accessors

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Integer getLoginAttempt() {
		return this.loginAttempt;
	}

	public void setLoginAttempt(Integer loginAttempt) {
		this.loginAttempt = loginAttempt;
	}

	public Date getValidTill() {
		return validTill;
	}

	public void setValidTill(Date validTill) {
		this.validTill = validTill;
	}

	public Userdetails getUserdetails() {
		return this.userdetails;
	}

	public void setUserdetails(Userdetails userdetails) {
		this.userdetails = userdetails;
	}

	public Set<College> getColleges() {
		return colleges;
	}

	public void setColleges(Set<College> colleges) {
		this.colleges = colleges;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getLogoDisplay() {
		return logoDisplay;
	}

	public void setLogoDisplay(Boolean logoDisplay) {
		this.logoDisplay = logoDisplay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		Logindetails other = (Logindetails) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	public Set<EmployeeEfforts> getEmployeeEffortses() {
		return employeeEffortses;
	}

	public void setEmployeeEffortses(Set<EmployeeEfforts> employeeEffortses) {
		this.employeeEffortses = employeeEffortses;
	}
	
	public String getClassName(){
		if(validTill !=null && validTill.before(Calendar.getInstance().getTime())){
			return "blinking";
		}
		return "";
	}

	public Boolean getUi() {
		return ui;
	}

	public void setUi(Boolean ui) {
		this.ui = ui;
	}

	
	
}