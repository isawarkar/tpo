/*
 * Class Name : This v1.0 This file is copyrighted by Uddanda Technologies.
 * Contents of this file can not be changed with out the permission Uddanda
 * Technologies
 * 
 * 
 * package com.login;
 * 
 * import java.io.IOException;
 * 
 * import javax.servlet.Filter; import javax.servlet.FilterChain; import
 * javax.servlet.FilterConfig; import javax.servlet.ServletException; import
 * javax.servlet.ServletRequest; import javax.servlet.ServletResponse; import
 * javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse;
 * 
 *//**
	 * @author Uddanda Technologies
	 *//*
		 * public class StudentLoginFilter implements Filter {
		 * 
		 * private static String startURL;
		 * 
		 * public void destroy() { }
		 * 
		 * public void doFilter(ServletRequest request, ServletResponse response,
		 * FilterChain filterChain) throws IOException, ServletException { if (!(request
		 * instanceof HttpServletRequest)) throw new
		 * ServletException("non-HTTP use is not supported!");
		 * 
		 * final HttpServletRequest req = (HttpServletRequest) request; final
		 * HttpServletResponse res = (HttpServletResponse) response; try { Student
		 * student = (Student) req.getSession().getAttribute("Student"); if (null ==
		 * student || null == student.getUserName()) {
		 * res.sendRedirect(TpoUtil.getBasePath(req).concat(startURL)); }
		 * filterChain.doFilter(request, response); } catch (IllegalStateException e) {
		 * System.out.print("" + e.getMessage()); } catch (Exception e) {
		 * e.printStackTrace(); }
		 * 
		 * finally { // NDC.pop(); } }
		 * 
		 * public void init(FilterConfig filterConfig) throws ServletException {
		 * startURL = filterConfig.getInitParameter("startURL"); }
		 * 
		 * }
		 */