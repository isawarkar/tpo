/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.hibernate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class Notice implements Serializable, JSONAware {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String noticeName;

	private String notice;

	private Boolean active;

	private String createdBy;

	private Date expiryDate;

	private Boolean impTag;

	private Boolean studentSpecific;

	private String fileName1;
	private String fileName2;
	private String fileName3;
	private String fileName4;
	private String fileName5;

	private int index;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	// Constructors

	/** default constructor */
	public Notice() {
	}

	/** full constructor */
	public Notice(String noticeName, String notice, Boolean active, Date expiryDate) {
		this.noticeName = noticeName;
		this.notice = notice;
		this.active = active;
		this.expiryDate = expiryDate;
	}

	// Property accessors

	public String getNotice() {
		return this.notice;
	}

	public String getNoticeName() {
		return noticeName;
	}

	public void setNoticeName(String noticeName) {
		this.noticeName = noticeName;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getClassName() {
		if (expiryDate != null && expiryDate.before(Calendar.getInstance().getTime())) {
			return "blinking";
		}
		return "";
	}

	public Boolean getStudentSpecific() {
		return studentSpecific;
	}

	public Boolean getImpTag() {
		return impTag;
	}

	public void setImpTag(Boolean impTag) {
		this.impTag = impTag;
	}

	public void setStudentSpecific(Boolean studentSpecific) {
		this.studentSpecific = studentSpecific;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	private String file1;
	private String file2;
	private String file3;
	private String file4;
	private String file5;
	
	

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

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("noticeName", noticeName);
		obj.put("notice", notice);
		obj.put("createdBy", createdBy);
		obj.put("expiryDate", TpoUtil.getDateToStringYYYYMMddHHmmss(expiryDate));
		obj.put("expired", "blinking".equals(getClassName()) ? "YES" : "NO");
		if (studentSpecific != null) {
			obj.put("studentSpecific", studentSpecific);
		} else {
			obj.put("studentSpecific", false);
		}
		if (impTag != null) {
			obj.put("impTag", impTag);
		} else {
			obj.put("impTag", false);
		}
		
		if (fileName1 != null && file1 != null) {
			obj.put("fileName1", fileName1);
			obj.put("file1", file1);
		} else {
			obj.put("fileName1", null);
			obj.put("file1", null);

		}
		
		if (fileName2 != null && file2 != null) {
			obj.put("fileName2", fileName2);
			obj.put("file2", file2);
		} else {
			obj.put("fileName2", null);
			obj.put("file2", null);

		}
		
		if (fileName3 != null && file3 != null) {
			obj.put("fileName3", fileName3);
			obj.put("file3", file3);
		} else {
			obj.put("fileName3", null);
			obj.put("file3", null);

		}
		
		if (fileName4 != null && file4 != null) {
			obj.put("fileName4", fileName4);
			obj.put("file4", file4);
		} else {
			obj.put("fileName4", null);
			obj.put("file4", null);

		}
		
		if (fileName5 != null && file5 != null) {
			obj.put("fileName5", fileName5);
			obj.put("file5", file5);
		} else {
			obj.put("fileName5", null);
			obj.put("file5", null);

		}

		
		return obj.toString();
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
	
	

}