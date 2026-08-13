/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate;

import java.io.Serializable;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.lowagie.text.pdf.codec.Base64;

import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class Company implements Serializable, JSONAware {

	private static final long serialVersionUID = 1L;

	private Integer companyID;
	private String companyname;
	private Date dateofvisit;
	private String profile;
	private String domain;
	private String website;
	private String linkedIn;
	private String twiter;
	private String glassdoor;
	private String facebook;
	private Integer total;
	private String email;
	private String remarks;
	private String createdBy;
	private String packageOffering;
	
	private byte[] logo = null;

	public Integer getCompanyID() {
		return companyID;
	}

	public void setCompanyID(Integer companyID) {
		this.companyID = companyID;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public Date getDateofvisit() {
		return dateofvisit;
	}

	public void setDateofvisit(Date dateofvisit) {
		this.dateofvisit = dateofvisit;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}


	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getTwiter() {
		return twiter;
	}

	public void setTwiter(String twiter) {
		this.twiter = twiter;
	}

	public String getGlassdoor() {
		return glassdoor;
	}

	public void setGlassdoor(String glassdoor) {
		this.glassdoor = glassdoor;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getPackageOffering() {
		return packageOffering;
	}

	public void setPackageOffering(String packageOffering) {
		this.packageOffering = packageOffering;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyID == null) ? 0 : companyID.hashCode());
		result = prime * result + ((companyname == null) ? 0 : companyname.hashCode());
		result = prime * result + ((dateofvisit == null) ? 0 : dateofvisit.hashCode());
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
		Company other = (Company) obj;
		if (companyID == null) {
			if (other.companyID != null)
				return false;
		} else if (!companyID.equals(other.companyID))
			return false;
		if (companyname == null) {
			if (other.companyname != null)
				return false;
		} else if (!companyname.equals(other.companyname))
			return false;
		if (dateofvisit == null) {
			if (other.dateofvisit != null)
				return false;
		} else if (!dateofvisit.equals(other.dateofvisit))
			return false;
		return true;
	}
	
	

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("companyID", companyID);
		obj.put("companyname", companyname);
		obj.put("dateofvisit", TpoUtil.getDateToStringYYYYMMddHHmmss(dateofvisit));
		obj.put("profile", profile);
		//TO-DO
		if(logo == null || logo.length == 0) {
			logo = TpoUtil.getFBFileLogo();
		}
		obj.put("logo",  Base64.encodeBytes(logo));
		obj.put("domain", domain);
		obj.put("website", website);
		obj.put("linkedIn", linkedIn);
		obj.put("twiter", twiter);
		obj.put("glassdoor", glassdoor);
		obj.put("facebook", facebook);
		obj.put("email", email);
		obj.put("remarks", remarks);

		return obj.toString();
	}
}