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

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.lowagie.text.pdf.codec.Base64;

import tpo.imageservice.client.FileUploadUtility;
import tpo.util.IMAGECONS;
import tpo.util.TpoUtil;

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
		FileUploadUtility fileUploadUtility = (FileUploadUtility) TpoUtil.getManagedBean("fileUploadUtility");
		if (fileName1 != null) {
			obj.put("fileName1", fileName1);
			String base64EncodedData = Base64.encodeBytes(fileUploadUtility
					.downloadFile("/download", noticeName + "_" + fileName1, IMAGECONS.notice));
			obj.put("file1", base64EncodedData);
		} else {
			obj.put("fileName1", null);
			obj.put("file1", null);

		}

		if (fileName2 != null) {
			obj.put("fileName2", fileName2);
			String base64EncodedData = Base64.encodeBytes(fileUploadUtility
					.downloadFile("/download", noticeName + "_" + fileName2, IMAGECONS.notice));
			obj.put("file2", base64EncodedData);
		} else {
			obj.put("fileName2", null);
			obj.put("file2", null);

		}

		if (fileName3 != null) {
			obj.put("fileName3", fileName3);
			String base64EncodedData = Base64.encodeBytes(fileUploadUtility
					.downloadFile("/download", noticeName + "_" + fileName3, IMAGECONS.notice));
			obj.put("file3", base64EncodedData);
		} else {
			obj.put("fileName3", null);
			obj.put("file3", null);

		}

		if (fileName4 != null) {
			obj.put("fileName4", fileName4);
			String base64EncodedData = Base64.encodeBytes(fileUploadUtility
					.downloadFile("/download", noticeName + "_" + fileName4, IMAGECONS.notice));
			obj.put("file4", base64EncodedData);
		} else {
			obj.put("fileName4", null);
			obj.put("file4", null);

		}

		if (fileName5 != null) {
			obj.put("fileName5", fileName5);
			String base64EncodedData = Base64.encodeBytes(fileUploadUtility
					.downloadFile("/download", noticeName + "_" + fileName5, IMAGECONS.notice));
			obj.put("file5", base64EncodedData);
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