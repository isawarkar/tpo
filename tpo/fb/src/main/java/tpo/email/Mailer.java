package tpo.email;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tpo.util.AES;
import tpo.util.TpoUtil;

@Component
public class Mailer {
	
	@Value("${gp}")
	public String gp;
	
	public Session getSeesionForGmail() {

		final String pass = AES.symmetricDecrypt(gp, TpoUtil.getKeyInfo());
		Properties props = new Properties();
		props.put("mail.smtp.user", TpoUtil.ADMIN_EMAIL);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.debug", "false");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.EnableSSL.enable", "true");

		props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");

		return Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(TpoUtil.ADMIN_EMAIL, pass);
			}
		});

	}
}