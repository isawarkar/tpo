/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.beans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import tpo.beans.Parent;
import tpo.imageservice.client.FileUploadUtility;
import tpo.util.IMAGECONS;
import tpo.util.SystemUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Component("AdminUser")
@Scope("session")
public class AdminUser extends Parent {

	private String role;
	private String userName;
	private String childUserName;
	private Boolean tabSelected;
	private Boolean menuSelected;
	private Boolean etsLogin = false;
	private String lastLogin;
	private String email;
	private String expString;
	private String parent;
	private List<String> userList;
	private List<String> collegeList;
	private String fullName;

	private byte[] profilePic;
	private byte[] logo;

	private int noOfNotification;

	@Autowired
	FileUploadUtility fileUploadUtility;

	public static AdminUser getUser() {
		AdminUser user = (AdminUser) TpoUtil.getManagedBean(AdminUser.class.getSimpleName());
		return user;
	}

	/**
	 * @return the role
	 */
	public synchronized String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public synchronized void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the userName
	 */
	public synchronized String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public synchronized void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the tabSelected
	 */
	public Boolean getTabSelected() {
		return tabSelected;
	}

	/**
	 * @param tabSelected the tabSelected to set
	 */
	public void setTabSelected(Boolean tabSelected) {
		this.tabSelected = tabSelected;
	}

	/**
	 * @return the menuSelected
	 */
	public Boolean getMenuSelected() {
		return menuSelected;
	}

	/**
	 * @param menuSelected the menuSelected to set
	 */
	public void setMenuSelected(Boolean menuSelected) {
		this.menuSelected = menuSelected;
	}

	/**
	 * @return the lastLogin
	 */
	public synchronized String getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin the lastLogin to set
	 */
	public synchronized void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * @return the etsLogin
	 */
	public Boolean getEtsLogin() {
		return etsLogin;
	}

	/**
	 * @param etsLogin the etsLogin to set
	 */
	public void setEtsLogin(Boolean etsLogin) {
		this.etsLogin = etsLogin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getChildUserName() {
		return childUserName;
	}

	public void setChildUserName(String childUserName) {
		this.childUserName = childUserName;
	}

	public String getExpString() {
		return expString;
	}

	public void setExpString(String expString) {
		this.expString = expString;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public void clear() {
		role = null;
		userName = null;
		childUserName = null;
		tabSelected = false;
		menuSelected = false;
		etsLogin = false;
		lastLogin = null;
		email = null;
		expString = null;
		parent = null;
		userList = null;
		collegeList = null;
	}

	public List<String> getUserList() {
		return userList;
	}

	public void setUserList(List<String> userList) {
		this.userList = userList;
	}

	public List<String> getCollegeList() {
		return collegeList;
	}

	public void setCollegeList(List<String> collegeList) {
		this.collegeList = collegeList;
	}

	public String getEnvirnment() {
		return SystemUtil.getLabel("envirnment");
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getNoOfNotification() {
		return noOfNotification;
	}

	public void setNoOfNotification(int noOfNotification) {
		this.noOfNotification = noOfNotification;
	}

	public String switchUI() {
		return "adminDashboardNew";
	}

	
	public byte[] getProfilePic() {
		if (profilePic == null) {
			profilePic = fileUploadUtility.downloadFile(getImageServiceUrl() + "/downloadImage", userName,
					IMAGECONS.userprofilepics);
		}
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	public byte[] getLogo() {
		if (logo == null) {
			logo = fileUploadUtility.downloadFile(getImageServiceUrl() + "/downloadImage", userName, IMAGECONS.userlogo);
		}
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

}