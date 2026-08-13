/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.login;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class ErrorFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(ErrorFilter.class);

	private String errorPage;

	public void destroy() {
		

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest))
			throw new ServletException("non-HTTP use is not supported!");

		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		try {
			filterChain.doFilter(request, response);
		} catch (javax.faces.FacesException e) {
			e.printStackTrace();
			logger.error("Caught a FacesException", e);
			redirectHttpRequest(httpServletRequest, httpServletResponse);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error("Caught a RuntimeException", e);
			redirectHttpRequest(httpServletRequest, httpServletResponse);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Caught a FileNotFoundException", e);
			redirectHttpRequest(httpServletRequest, httpServletResponse);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
			Throwable rootCause = e.getRootCause();
			if (rootCause != null)
				logger.error("Caught a ServletException, root cause logged",
						rootCause);
			else
				logger.error("Caught a ServletException", e);
			redirectHttpRequest(httpServletRequest, httpServletResponse);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Caught a generic Exception", e);
			redirectHttpRequest(httpServletRequest, httpServletResponse);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		errorPage = filterConfig.getInitParameter("errorPage");
	}

	private void redirectHttpRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		if (httpServletResponse instanceof HttpServletResponse) {
			String fullErrorPagee = TpoUtil.getBasePath(httpServletRequest)
					.concat(errorPage).toString();
			try {
				httpServletResponse.sendRedirect(fullErrorPagee);
			} catch (IOException ioe) {
				logger.warn("IOException while redirecting", ioe);
				throw ioe;
			}
		}
	}

}
