/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.annotation;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Entity
@Table(name = "documentlist")
public class DocumentList implements Serializable, JSONAware {

	@EmbeddedId
	private DocumentID documentID;

	@Column(name = "document")
	private Blob document;
	
	@Column(name = "uploadedDate")
	private Date uploadedDate;
	
	@Column(name = "isVerifiedWithOrgnal")
	private Boolean isVerifiedWithOrgnal;
	
	@Column(name = "verifiedDate")
	private Date verifiedDate;

	public DocumentID getDocumentID() {
		return documentID;
	}

	public void setDocumentID(DocumentID documentID) {
		this.documentID = documentID;
	}

	public Blob getDocument() {
		return document;
	}

	public void setDocument(Blob document) {
		this.document = document;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Boolean getIsVerifiedWithOrgnal() {
		return isVerifiedWithOrgnal;
	}

	public void setIsVerifiedWithOrgnal(Boolean isVerifiedWithOrgnal) {
		this.isVerifiedWithOrgnal = isVerifiedWithOrgnal;
	}

	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("documentName", documentID.getDocumentName());
		obj.put("uploadedDate", TpoUtil.getDateToStringYYYYMMddHHmmss(uploadedDate));
		obj.put("isVerifiedWithOrgnal", isVerifiedWithOrgnal);
		if(isVerifiedWithOrgnal) {
		obj.put("verifiedDate", TpoUtil.getDateToStringYYYYMMddHHmmss(verifiedDate));
		}else {
			obj.put("verifiedDate","NA");
		}
		return obj.toString();
	}
	
	
}