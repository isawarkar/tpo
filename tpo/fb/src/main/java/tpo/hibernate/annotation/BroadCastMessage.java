/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate.annotation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Uddanda Technologies
 */
@Entity
@Table(name = "broadcastmessage")
public class BroadCastMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "userName")
	private String userName;

	
	@Column(name = "broadcastMessageTitle")
	private String broadcastMessageTitle;

	@Column(name = "broadcastMessage")
	private String broadcastMessage;
	
	@Column(name = "messageSeverity")
	private String messageSeverity;
	
	@Column(name = "validFrom")
	private Date validFrom;
	
	@Column(name = "validTill")
	private Date validTill;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBroadcastMessageTitle() {
		return broadcastMessageTitle;
	}

	public void setBroadcastMessageTitle(String broadcastMessageTitle) {
		this.broadcastMessageTitle = broadcastMessageTitle;
	}

	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	public void setBroadcastMessage(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	public String getMessageSeverity() {
		return messageSeverity;
	}

	public void setMessageSeverity(String messageSeverity) {
		this.messageSeverity = messageSeverity;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTill() {
		return validTill;
	}

	public void setValidTill(Date validTill) {
		this.validTill = validTill;
	}
	
	

	
}