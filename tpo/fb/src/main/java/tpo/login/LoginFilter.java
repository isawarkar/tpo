/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.login;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpo.admin.beans.AdminUser;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class LoginFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

	private String startURL;

	public void destroy() {
		

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest))
			throw new ServletException("non-HTTP use is not supported!");

		final HttpServletRequest servletRequest = (HttpServletRequest) request;
		final HttpServletResponse servletResponse = (HttpServletResponse) response;
		try {
			AdminUser user = (AdminUser) servletRequest.getSession()
					.getAttribute("AdminUser");
			if (null == user || null == user.getRole()) {
				servletResponse.sendRedirect(TpoUtil
						.getBasePath(servletRequest).concat(startURL));
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			logger.error("Caught a generic Exception", e);
		}

		finally {
			NDC.pop();
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		startURL = filterConfig.getInitParameter("startURL");
	}

}
