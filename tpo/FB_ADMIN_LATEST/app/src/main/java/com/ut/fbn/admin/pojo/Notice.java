/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.ut.fbn.admin.pojo;

/**
 * @author Uddanda Technologies
 */
public class Notice implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String noticeName;

	private String notice;

	private String createdBy;

	private String expiryDate;

	private String expired;

	private boolean studentSpecific;

	private boolean impTag;

	private String fileName1;
	private String fileName2;
	private String fileName3;
	private String fileName4;
	private String fileName5;
	private String file1;
	private String file2;
	private String file3;
	private String file4;
	private String file5;

	public String getNoticeName() {
		return noticeName;
	}

	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((notice == null) ? 0 : notice.hashCode());
		result = prime * result + ((noticeName == null) ? 0 : noticeName.hashCode());
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
		Notice other = (Notice) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (notice == null) {
			if (other.notice != null)
				return false;
		} else if (!notice.equals(other.notice))
			return false;
		if (noticeName == null) {
			if (other.noticeName != null)
				return false;
		} else if (!noticeName.equals(other.noticeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("&nbsp;&nbsp;&nbsp;" + notice).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Expiry Date:</font>" + expiryDate).append("<br>")
				.append("&nbsp;&nbsp;&nbsp;<font color='red'>Expired:</font>" + expired);
		if (studentSpecific) {
			buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;<font color='blue'><b>Specific to You: YES</b></font>");
		}
		if (impTag) {
			buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;<font color='red'><b>High IMP: YES</b></font>");
		}
		if ((fileName1 !=null && !"null".equals(fileName1))
				|| (fileName2 !=null && !"null".equals(fileName2))
				|| (fileName3 !=null && !"null".equals(fileName3))
				|| (fileName4 !=null && !"null".equals(fileName4))
				|| (fileName5 !=null && !"null".equals(fileName5))) {
			buffer.append("<br>").append("&nbsp;&nbsp;&nbsp;<font color='red'><b>Note:This notice has attachment.</font>");
		}

		return buffer.toString();
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public boolean getStudentSpecific() {
		return studentSpecific;
	}

	public void setStudentSpecific(boolean studentSpecific) {
		this.studentSpecific = studentSpecific;
	}

	public boolean isImpTag() {
		return impTag;
	}

	public void setImpTag(boolean impTag) {
		this.impTag = impTag;
	}

	public String getFileName1() {
		return fileName1;
	}

	public void setFileName1(String fileName1) {
		this.fileName1 = fileName1;
	}

	public String getFileName2() {
		return fileName2;
	}

	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}

	public String getFileName3() {
		return fileName3;
	}

	public void setFileName3(String fileName3) {
		this.fileName3 = fileName3;
	}

	public String getFileName4() {
		return fileName4;
	}

	public void setFileName4(String fileName4) {
		this.fileName4 = fileName4;
	}

	public String getFileName5() {
		return fileName5;
	}

	public void setFileName5(String fileName5) {
		this.fileName5 = fileName5;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getFile5() {
		return file5;
	}

	public void setFile5(String file5) {
		this.file5 = file5;
	}

	
}