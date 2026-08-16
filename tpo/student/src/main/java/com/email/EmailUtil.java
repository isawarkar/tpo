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
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.beans.UIBackingBean;
import com.netflix.discovery.util.SystemUtil;
import com.util.Encryption;
import com.util.FbMessageUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Component("emailUtil")
public class EmailUtil extends EmailTool {

	private Logger logger = LoggerFactory.getLogger(EmailUtil.class);
	
	private String env;


	@Value("${superUserEmail}")
	public String superUserEmail;
	
	@Value("${envirnment}")
	public String envirnment;

	public String getEnvirnment() {
		env = envirnment;
		return env;
	}

	public boolean postMail(List<String> recipients, String subject,
			String message, String from, Message.RecipientType recipientType)
			throws MessagingException {
		InternetAddress[] addressTo = new InternetAddress[recipients.size()];
		int i = 0;
		for (String string : recipients) {
			addressTo[i] = new InternetAddress(string);
			i++;
		}
		
	/*	if(messageProducer != null){
			try {
				messageProducer.sendMessage(message);
				
			} catch (JMSException e) {
				
				e.printStackTrace();
			}
		}*/
		return postMail(addressTo, subject, message, from, recipientType);
	}

	public boolean postMail(Address recipients[], String subject,
			String message, String from, Message.RecipientType recipientType)
			throws MessagingException {
		try {
			if (env != null) {
				Address frm[] = new Address[1];
				frm[0] = (Address) new InternetAddress(from);

				if ("LAN".equalsIgnoreCase(env)) {
					EmailObject emailObject = new EmailObject();
					emailObject.setRecipients(recipients);
					emailObject.setSubject(subject);
					emailObject.setMessage(message);
					emailObject.setFrom(frm);
					URL url = new URL("http://" + TpoUtil.HOSTNAME
							+ "/student/servlet/SendEmailOnLAN?");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setRequestMethod("POST");
					connection.setDoInput(true);
					connection.setDoOutput(true);

					ObjectOutputStream objOut = new ObjectOutputStream(
							connection.getOutputStream());
					objOut.writeObject(emailObject);
					objOut.flush();
					objOut.close();

					InputStream inStream = connection.getInputStream();
					BufferedReader input = new BufferedReader(
							new InputStreamReader(inStream));
					String line = "";
					while ((line = input.readLine()) != null)
						if (!Encryption.getEncryptedString("TRUE").equals(line)) {
							throw new ServletException("E-mail Can not be sent");
						}
				} else if ("SERVER".equalsIgnoreCase(env) || "LOCAL".equalsIgnoreCase(env)) {
					sendEmail(frm, recipients, message, subject, recipientType);
				} 
			}
		} catch (UnknownHostException e) {
			UIBackingBean
					.setSuccessMessage(FbMessageUtil.getLabel("Your_operation_done_successfully_E_mail_is_not_sent_because_Internet_is_not_available"));
		} catch (SendFailedException e) {
			StringBuffer mes = new StringBuffer("This are the wrong email address.<br>");
			
			Address[] addresses = e.getInvalidAddresses();
			for(Address address : addresses){
				mes.append("<br>" + address.toString());
			}
			List<String> r = new ArrayList<String>(1);
			r.add(superUserEmail);
			postMail(r, "This are the wrong email address", mes.toString(), from, RecipientType.TO);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (ServletException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return true;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

		
	public void sendEmailWithAttachment(String from, String to, String body, String subject, List<File> fileList)
			throws MessagingException {
		
		super.sendEmailWithAttachment(from, to, body, subject, fileList,env);
	}
	
	public void sendEmail(String from, String to, String body, String subject,
			javax.mail.Message.RecipientType recipientType) throws MessagingException, AddressException {
		
		super.sendEmail(from, to, body, subject, recipientType,env);
	}

}
