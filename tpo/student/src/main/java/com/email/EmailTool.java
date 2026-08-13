/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.email;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beans.UIBackingBean;
import com.util.Encryption;
import com.util.FbMessageUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class EmailTool extends Mailer implements TransportListener {

	private final static String DEFAULT_MIME_TYPE = "text/html;charset=UTF-8";
	private Logger logger = LoggerFactory.getLogger(EmailTool.class);

	private boolean messageDelivered = false;
	private Session session;

	/*
	 * 
	 * PUBLIC METHODS
	 */
	public EmailTool() {
		// do nothing
	}

	/**
	 * 
	 * @param session
	 */
	public EmailTool(Session session) {
		setSession(session);
	}

	/**
	 * 
	 * @param newSession
	 */
	public void setSession(Session newSession) {
		this.session = newSession;
	}

	public void messageDelivered(TransportEvent e) {
		messageDelivered = true;
		logger.debug("Delivered:<br>" + messageStatus(e));
	}

	public void messageNotDelivered(TransportEvent e) {
		messageDelivered = false;
		logger.debug("Not Delivered:<br>" + messageStatus(e));
	}

	public void messagePartiallyDelivered(TransportEvent e) {
		messageDelivered = true;
		logger.debug("Partially Delivered:<br>" + messageStatus(e));
	}

	private String messageStatus(TransportEvent e) {
		Address[] addresses = null;
		StringBuffer sb = new StringBuffer();

		sb.append("Sent Addresses:<br>");
		addresses = e.getValidSentAddresses();
		if (addresses != null)
			for (int i = 0; i < addresses.length; i++) {
				sb.append("    ").append(addresses[i].toString()).append("<br>");
			}

		sb.append("Unsent Addresses:<br>");
		addresses = e.getValidUnsentAddresses();
		if (addresses != null)
			for (int i = 0; i < addresses.length; i++) {
				sb.append("    ").append(addresses[i].toString()).append("<br>");
			}

		sb.append("Invalid Addresses:<br>");
		addresses = e.getInvalidAddresses();
		if (addresses != null)
			for (int i = 0; i < addresses.length; i++) {
				sb.append("    ").append(addresses[i].toString()).append("<br>");
			}

		return sb.toString();
	}

	/**
	 * 
	 * @return
	 */
	private Session getSession() {
		if (this.session == null) {
			/*
			 * Properties mailProps = new Properties();
			 * mailProps.put("mail.smtp.host", "localhost"); return
			 * Session.getInstance(mailProps, null);
			 */
			session = getSeesionForGmail();
		}

		return this.session;
	}

	public void sendEmail(String from, String to, String body, String subject, Message.RecipientType recipientType,
			String envirnment) throws MessagingException, AddressException {
		try {
			if (envirnment != null && envirnment.equals("LAN")) {
				EmailObject emailObject = new EmailObject();
				emailObject.setRecipients(parseInternetAddresses(to));
				emailObject.setSubject(subject);
				emailObject.setMessage(body);
				emailObject.setFrom(parseInternetAddresses(from));
				URL url = new URL("http://" + TpoUtil.HOSTNAME + "/student/servlet/SendEmailOnLAN?");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.setDoOutput(true);

				ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
				objOut.writeObject(emailObject);
				objOut.flush();
				objOut.close();

				InputStream inStream = connection.getInputStream();
				BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
				String line = "";
				while ((line = input.readLine()) != null)
					if (!Encryption.getEncryptedString("TRUE").equals(line)) {
						throw new ServletException("Email Can not be sent");
					}
			} else if (envirnment != null && envirnment.equalsIgnoreCase("server")) {
				sendEmail(parseInternetAddresses(from), parseInternetAddresses(to), body, subject, recipientType);
			}
		} catch (UnknownHostException e) {
			UIBackingBean.setSuccessMessage(FbMessageUtil
					.getLabel("Your_operation_done_successfully_E_mail_is_not_sent_because_Internet_is_not_available"));
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (ProtocolException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (ServletException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param body
	 * @param subject
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public synchronized void sendEmail(Address[] from, Address[] to, String body, String subject,
			Message.RecipientType recipientType) throws MessagingException {
		/* classic */
		Session session = getSession();
		Transport transport = session.getTransport("smtp");

		transport.addTransportListener(this);

		MimeMessage msg = new MimeMessage(session);
		msg.addFrom(from);
		msg.addRecipients(recipientType, to);
		try {
			msg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msg.setContent(body, DEFAULT_MIME_TYPE);
		msg.setHeader("Content-Type", DEFAULT_MIME_TYPE);
		msg.setSentDate(new Date());
		if (logger.isDebugEnabled())
			logger.debug("From:" + from + "<br>To:" + to + "<br>Body:" + body);

		if (!transport.isConnected())
			transport.connect();

		transport.sendMessage(msg, to);
		transport.close();
	}

	/**
	 * This method is used to send an email with an attachment
	 * 
	 * @param from
	 *            - String containing senders' email.It can contain more than
	 *            one addresses seperated by 'comma'
	 * @param to
	 *            - String containing receivers' email.It can contain more than
	 *            one addresses seperated by 'comma'
	 * @param body
	 *            - String message to be sent in the mail
	 * @param subject
	 *            - String Subject of the Email
	 * @param file
	 *            - File object for the attachment to be sent
	 * @throws MessagingException
	 */
	public void sendEmailWithAttachment(String from, String to, String body, String subject, List<File> fileList,
			String envirnment) throws MessagingException {
		try {
			if (envirnment != null && envirnment.equals("LAN")) {
				EmailObject emailObject = new EmailObject();
				emailObject.setRecipients(parseInternetAddresses(to));
				emailObject.setSubject(subject);
				emailObject.setMessage(body);
				emailObject.setFrom(parseInternetAddresses(from));
				emailObject.setFile(fileList.get(0));
				URL url = new URL("http://" + TpoUtil.HOSTNAME + "/student/servlet/SendEmailOnLAN?");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.setDoOutput(true);

				ObjectOutputStream objOut = new ObjectOutputStream(connection.getOutputStream());
				objOut.writeObject(emailObject);
				objOut.flush();
				objOut.close();

				InputStream inStream = connection.getInputStream();
				BufferedReader input = new BufferedReader(new InputStreamReader(inStream));
				String line = "";
				while ((line = input.readLine()) != null)
					if (!Encryption.getEncryptedString("TRUE").equals(line)) {
						throw new ServletException("Email Can not be sent");
					}
			} else if (envirnment != null && envirnment.equalsIgnoreCase("server")) {
				sendEmailWithAttachment(parseInternetAddresses(from), parseInternetAddresses(to), body, subject, fileList);
			}
		} catch (UnknownHostException e) {
			UIBackingBean.setSuccessMessage(FbMessageUtil
					.getLabel("Your_operation_done_successfully_E_mail_is_not_sent_because_Internet_is_not_available"));
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (ProtocolException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (ServletException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to send an email with an attachment
	 * 
	 * @param from
	 *            - array of javax.mail.Address objects
	 * @param to
	 *            - array of javax.mail.Address objects
	 * @param body
	 *            - String message to be sent in the mail
	 * @param subject
	 *            - String Subject of the Email
	 * @param file
	 *            - File object for the attachment to be sent
	 * @throws MessagingException
	 */
	public synchronized void sendEmailWithAttachment(Address[] from, Address[] to, String body, String subject,
			List<File> fileList) throws MessagingException {
		/* classic */
		Session session = getSession();
		Transport transport = session.getTransport("smtp");
		transport.addTransportListener(this);

		// Constructing the message
		MimeMessage message = new MimeMessage(session);
		message.addFrom(from);
		message.addRecipients(Message.RecipientType.TO, to);
		message.addRecipients(Message.RecipientType.BCC, parseInternetAddresses(TpoUtil.ADMIN_EMAIL));
		try {
			message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		message.setSentDate(new Date());

		// creating the Multipart,which shall contain message body,& attatchment
		Multipart multipart = new MimeMultipart();
		// Creating the message(text) body
		BodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(body, DEFAULT_MIME_TYPE);
		bodyPart.setHeader("Content-Type", DEFAULT_MIME_TYPE);
		multipart.addBodyPart(bodyPart);

		// creating the attatchment part of the message body
		if(fileList != null) {
		for(File file :fileList){
			bodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(file);
			bodyPart.setDataHandler(new DataHandler(source));
			bodyPart.setFileName(file.getName());
			multipart.addBodyPart(bodyPart);
		}
		}
		// setting the content of the Message to Multipart
		message.setContent(multipart);

		if (!transport.isConnected()) {
			transport.connect();
		}

		// sending the Message
		transport.sendMessage(message, to);
		transport.close();
	}

	// TODO this will not return the correct status since the sendMail
	// method above is asynchronous and this is synchronous. It is possible
	// that this method gets called before the processing in sendMail
	// finishes giving a false result. In order to fix, you will need to
	// put a wait state in this method to wait a specified amount of time
	// for the sendMail transport logic to actually do its thing!
	public boolean getStatus() {
		return messageDelivered; // READ COMMENT, BUG!
	}

	/*
	 * 
	 * PRIVATE METHODS
	 */
	private Address[] parseInternetAddresses(String addresses) throws AddressException {
		StringTokenizer st = new StringTokenizer(addresses, ",");
		Address ias[] = new Address[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			ias[i] = (Address) new InternetAddress(st.nextToken());
		}
		return ias;
	}
}
