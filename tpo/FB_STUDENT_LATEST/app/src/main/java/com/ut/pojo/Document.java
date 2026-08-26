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
public class Document implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String documentName;

	private String uploadedDate;

	private boolean isVerifiedWithOrgnal;

	private String verifiedDate;

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(String uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public boolean isVerifiedWithOrgnal() {
		return isVerifiedWithOrgnal;
	}

	public void setVerifiedWithOrgnal(boolean isVerifiedWithOrgnal) {
		this.isVerifiedWithOrgnal = isVerifiedWithOrgnal;
	}

	public String getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(String verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentName == null) ? 0 : documentName.hashCode());
		result = prime * result + (isVerifiedWithOrgnal ? 1231 : 1237);
		result = prime * result + ((uploadedDate == null) ? 0 : uploadedDate.hashCode());
		result = prime * result + ((verifiedDate == null) ? 0 : verifiedDate.hashCode());
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
		Document other = (Document) obj;
		if (documentName == null) {
			if (other.documentName != null)
				return false;
		} else if (!documentName.equals(other.documentName))
			return false;
		if (isVerifiedWithOrgnal != other.isVerifiedWithOrgnal)
			return false;
		if (uploadedDate == null) {
			if (other.uploadedDate != null)
				return false;
		} else if (!uploadedDate.equals(other.uploadedDate))
			return false;
		if (verifiedDate == null) {
			if (other.verifiedDate != null)
				return false;
		} else if (!verifiedDate.equals(other.verifiedDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String verified = "NO";
		if (isVerifiedWithOrgnal) {
			verified = "YES";
		}
		buffer.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Document Name:</font>" + documentName).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Uploaded Date:</font>" + uploadedDate).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>Verified:</font>" + verified).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='blue'>VerifiedDate:</font>" + verifiedDate).append("<br>");
		return buffer.toString();
	}

}