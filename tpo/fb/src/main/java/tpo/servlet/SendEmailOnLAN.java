package tpo.servlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpo.email.EmailObject;
import tpo.email.EmailUtil;
import tpo.util.Encryption;
import tpo.util.FacesUtil;
import tpo.util.TpoUtil;

public class SendEmailOnLAN extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(SendEmailOnLAN.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			ObjectInputStream objIn = new ObjectInputStream(
					request.getInputStream());
			EmailObject emailObject = null;
			try {
				emailObject = (EmailObject) objIn.readObject();

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (emailObject != null) {
				try {
					FacesContext facesContext = FacesUtil.getFacesContext(
							request, response);
					EmailUtil emailUtill = (EmailUtil) TpoUtil.getManagedBean(
							"emailUtil", facesContext);

					if (emailUtill != null) {
						if (emailObject.getFile() != null) {
							List<File> list = new ArrayList<>(1);
							list.add(emailObject.getFile());
							emailUtill.sendEmailWithAttachment(
									emailObject.getFrom(),
									emailObject.getRecipients(),
									emailObject.getMessage(),
									emailObject.getSubject(),
									list);
						} else {
							emailUtill.sendEmail(emailObject.getFrom(),
									emailObject.getRecipients(),
									emailObject.getMessage(),
									emailObject.getSubject(),Message.RecipientType.BCC);
						}
					}
				} catch (AddressException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				response.getWriter().write(
						Encryption.getEncryptedString("TRUE"));
			}
			emailObject.setFrom(null);
			emailObject.setFile(null);
			emailObject.setMessage(null);
			emailObject.setRecipients(null);
			emailObject.setSubject(null);
			emailObject = null;
			out.flush();
			out.close();
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
