package tpo.email;

import java.io.File;
import java.io.Serializable;

import javax.mail.Address;

public class EmailObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Address recipients[];
	private String subject;
	private String message;
	private Address from[];
	private File file;

	public Address[] getRecipients() {
		return recipients;
	}

	public void setRecipients(Address[] recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Address[] getFrom() {
		return from;
	}

	public void setFrom(Address[] from) {
		this.from = from;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
