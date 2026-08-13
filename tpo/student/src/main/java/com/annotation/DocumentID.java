package com.annotation;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DocumentID implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "rollnumber")
	private String rollnumber;

	@Column(name = "documentName")
	private String documentName;
	
    public DocumentID() {

    }

    public DocumentID(String rollnumber, String documentName) {
        this.rollnumber = rollnumber;
        this.documentName = documentName;
    }

	public String getRollnumber() {
		return rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentName == null) ? 0 : documentName.hashCode());
		result = prime * result + ((rollnumber == null) ? 0 : rollnumber.hashCode());
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
		DocumentID other = (DocumentID) obj;
		if (documentName == null) {
			if (other.documentName != null)
				return false;
		} else if (!documentName.equals(other.documentName))
			return false;
		if (rollnumber == null) {
			if (other.rollnumber != null)
				return false;
		} else if (!rollnumber.equals(other.rollnumber))
			return false;
		return true;
	}

	
}