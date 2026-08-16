/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beans.QuestionBean;
import com.hibernate.Questions;
import com.util.FbMessageUtil;
import com.util.FbResourceUtil;

/**
 * @author Uddanda Technologies
 */
public class SubmitQuestion extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(SubmitQuestion.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String qNumber = request.getParameter("qNumber");
			String action = request.getParameter("action");
			String answer = request.getParameter("answer");
			QuestionBean bean = (QuestionBean) request.getSession()
					.getAttribute(QuestionBean.class.getSimpleName());
			String time = request.getParameter("time");
			if (time != null) {
				if (request.getSession() != null) {
					if (bean != null) {
						String time1[] =  time.split(":");
						bean.setTime(Double.valueOf(time1[0] + "." + time1[1]));
					}
				}
			}
			if (qNumber != null) {
				int qNo = new Integer(qNumber);
				if (action.equals("submit") && !answer.equals("z")) {
					bean.checkAnswer(answer, qNo);
				}
				Questions questionObj = null;
				if (qNo <= bean.getQuestionList().size()) {
					questionObj = bean.setMainQuestion(bean.getQuestionList()
							.get(--qNo));
					if(questionObj.getOptiona().isEmpty()){
						questionObj.setOptiona(FbResourceUtil.getLabel("NA"));
					}
					if(questionObj.getOptionb().isEmpty()){
						questionObj.setOptionb(FbResourceUtil.getLabel("NA"));
					}
					if(questionObj.getOptionc().isEmpty()){
						questionObj.setOptionc(FbResourceUtil.getLabel("NA"));
					}
					if(questionObj.getOptiond().isEmpty()){
						questionObj.setOptiond(FbResourceUtil.getLabel("NA"));
					}
					setJSONResponse(response, questionObj);
			
				}else{
					response.setStatus(400);
		            response.getWriter().write(FbMessageUtil.getLabel("This_is_the_last_question"));
				}
				
				// setXMLResponse(response, bean);
			}
			bean.updateResult();
		} catch (NumberFormatException e) {
			 response.setStatus(400);
             response.getWriter().write(e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			 response.setStatus(401);
             response.getWriter().write(FbMessageUtil.getLabel("Your_session_has_been_expired_Please_contact_your_administrator"));
			logger.error(e.getMessage());
			e.printStackTrace();
		}catch (Exception e) {
			 response.setStatus(400);
             response.getWriter().write(e.getMessage());
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void setJSONResponse(HttpServletResponse response,
			Questions questionObj) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");

		PrintWriter writer = response.getWriter();
		if (questionObj != null) {
			JSONArray jsonArray = new JSONArray();
			jsonArray.add(questionObj);
			String json = jsonArray.toJSONString();
			writer.print(json);
			writer.flush();
			writer.close();
			questionObj = null;
		}
	}

	private void setXMLResponse(HttpServletResponse response, QuestionBean bean)
			throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		response.getWriter().write("<questions>");
		String question = bean.getQuestion().replaceAll("&", "&amp;");
		question = question.replaceAll("<", "&lt;");
		question = question.replaceAll(">", "&gt;");

		response.getWriter().write("<question>" + question + "</question>");
		String optionA = bean.getOptionA();
		if (optionA == null || optionA.equals("")) {
			response.getWriter().write("<A>None</A>");
		} else {
			optionA = optionA.replaceAll("&", "&amp;");
			optionA = optionA.replaceAll("<", "&lt;");
			optionA = optionA.replaceAll(">", "&gt;");
			response.getWriter().write("<A>" + optionA + "</A>");
		}
		String optionB = bean.getOptionB();
		if (optionB == null || optionB.equals("")) {
			response.getWriter().write("<B>None</B>");
		} else {
			optionB = optionB.replaceAll("&", "&amp;");
			optionB = optionB.replaceAll("<", "&lt;");
			optionB = optionB.replaceAll(">", "&gt;");
			response.getWriter().write("<B>" + optionB + "</B>");
		}
		String optionC = bean.getOptionC();
		if (optionC == null || optionC.equals("")) {
			response.getWriter().write("<C>None</C>");
		} else {
			optionC = optionC.replaceAll("&", "&amp;");
			optionC = optionC.replaceAll("<", "&lt;");
			optionC = optionC.replaceAll(">", "&gt;");
			response.getWriter().write("<C>" + optionC + "</C>");
		}
		String optionD = bean.getOptionD();
		if (optionD == null || optionD.equals("")) {
			response.getWriter().write("<D>None</D>");
		} else {
			optionD = optionD.replaceAll("&", "&amp;");
			optionD = optionD.replaceAll("<", "&lt;");
			optionD = optionD.replaceAll(">", "&gt;");
			response.getWriter().write("<D>" + optionD + "</D>");
		}
		response.getWriter()
				.write("<image>" + bean.getImageName() + "</image>");
		response.getWriter().write("</questions>");
	}

}
