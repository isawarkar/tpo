package tpo.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpo.dao.CommonDBBean;
import tpo.util.FacesUtil;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

public class AjaxServlet extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(AjaxServlet.class);

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			String rollnumber = request.getParameter("dfdnmfbnndfn");
			String userName = request.getParameter("dfdnmfbnndfnfdgdfgfgfdgjlkgh");
			String password = request.getParameter("fdfdfemailfdf");
			FacesContext facesContext = FacesUtil.getFacesContext(request, response);
			CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName(),
					facesContext);
			if (commonDBBean != null) {
				String[] pass = password.split("46556");
				if (rollnumber != null) {
					if (commonDBBean.verifyEmail(rollnumber, pass[1])) {
						out.println("<center><font color='green' size='10'>"
								+ FbMessageUtil.getLabel(
										"Email_Verified_Successfully")
								+ "</font> <br/> <a href='http://"+TpoUtil.HOSTNAME+"'>Home Page</a><center>");
					} else {
						out.println("<font color='error' size='5'>"
								+ FbMessageUtil.getLabel("Error_Please_try_after_some_time") + "</font>");

					}
				}
				else if (userName != null) {
					if (commonDBBean.verifyUserEmail(userName, pass[1])) {
						out.println("<center><font color='green' size='10'>"
								+ FbMessageUtil.getLabel(
										"Email_Verified_Successfully")
								+ "</font> <br/> <a href='http://"+TpoUtil.HOSTNAME+"'>Home Page</a><center>");
					} else {
						out.println("<font color='error' size='5'>"
								+ FbMessageUtil.getLabel("Error_Please_try_after_some_time") + "</font>");

					}
				}		
				
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}


}
