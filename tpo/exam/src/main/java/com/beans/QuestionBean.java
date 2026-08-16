/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.email.EmailUtil;
import com.hibernate.Exam;
import com.hibernate.Questions;
import com.hibernate.QuestionsId;
import com.hibernate.Result;
import com.hibernate.ResultId;
import com.pdf.generator.PDFGenerator;
import com.util.CCPConstant;
import com.util.FbMessageUtil;
import com.util.IMAGECONS;

import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("QuestionBean")
@Transactional(readOnly = true)
@Scope("session")
public class QuestionBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(QuestionBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private SessionObject sessionObject;

	@Autowired
	private PDFGenerator pDFGenerator;

	@Autowired
	private FileUploadUtility fileUploadUtility;

	private String qAnswer = new String();

	private String minute = FbMessageUtil.getLabel("minute");

	private String second = FbMessageUtil.getLabel("second");

	private Double time;

	private Boolean messageLabel = true;

	private Integer questionNo = 0;

	private String loginName;

	private String firstName;

	private String lastName;

	private String email;

	private String test;

	private String question = FbMessageUtil.getLabel("What_is_java");

	private String optionA = FbMessageUtil.getLabel("Java_is_a_OOPS");

	private String optionB = FbMessageUtil.getLabel("Java_is_a_pure_OOPS");

	private String optionC = FbMessageUtil.getLabel("Java_is_a_structure_programing");

	private String optionD = FbMessageUtil.getLabel("Java_is_a_database");

	private String questionType = CCPConstant.SINGLE;

	private String imageName = null;

	private Boolean startButton;

	private Boolean submitButton = false;

	private Boolean nextButton = false;

	private Boolean finisButton = false;

	private Boolean negativeExam = false;

	private int startNumber;

	private int endNumber;

	private int noOfQuestions;

	private List<Integer> questionList = null;

	private Map<Integer, String> answersMap = null;

	private Set<Integer> submmitedSet = null;

	private String role;

	private String dateAndTime;

	private Exam exam;

	private int attempt;

	private boolean sessionExpiredMessage;

	public String getDateAndTime() {
		if (dateAndTime == null) {
			Date date = Calendar.getInstance().getTime();
			dateAndTime = TpoUtil.getDateToStringInddmmyyyyHHmmSS(date);
		}
		return dateAndTime;
	}

	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public String getQuestionListLinks() {
		StringBuffer str = new StringBuffer();
		int rown = 1;
		if (noOfQuestions != 0) {
			for (int i = 1; i <= noOfQuestions; i++) {
				if (rown == 1) {
					str.append("<ul class='pager'>");
				}
				str.append("<li class='activeBlue'><a style='cursor:hand' title=\""
						+ FbMessageUtil.getLabel("Click_here_to_go_on_this_Question") + "\"	onclick=\"setQuestionNo('"
						+ i + "');\" id=\"Q" + i + "\">Q" + i + "</a></li>");
				if (rown == 3) {
					str.append("</ul>");
					rown = 0;
				}
				rown++;
			}
		}
		str.append("</ul>");
		return str.toString();
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String startExam() {
		if (sessionFactory != null) {
			Session session = sessionFactory.getCurrentSession();
			if (session == null) {
				session = sessionFactory.openSession();
			}
			if (exam == null) {
				exam = getExam(session);
			}
			startButton = false;
			submitButton = true;
			finisButton = true;
			messageLabel = false;
			questionNo = 1;
			if (noOfQuestions < endNumber) {
				questionList = TpoUtil.getRandomNumbers(noOfQuestions, startNumber, endNumber);
				if (questionList == null || questionList.size() < noOfQuestions) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_contact_admin"));
					return "mainTest";
				}
				answersMap = new Hashtable<Integer, String>(questionList.size());
				submmitedSet = new HashSet<Integer>(questionList.size());
			}
			setQuestion();
			if (questionList != null && questionList.size() > 0) {
				setMainQuestion(questionList.get(0));
			}
			refreshResult();
			return "mainTest";
		}
		sessionExpiredMessage = true;
		return "sessionExpiredAction";
	}

	public void setQuestion() {
		--questionNo;
		if (questionList != null) {
			setMainQuestion(questionList.get(questionNo));
		}
		++questionNo;

	}

	public Questions setMainQuestion(Integer qNo) {
		Questions questionObj = null;
		try {
			if (qNo != null) {
				if (sessionFactory == null) {
					sessionFactory = sessionObject.getSessionFactory();
				}
				Session session = sessionFactory.getCurrentSession();
				if (session == null) {
					session = sessionFactory.openSession();
				}
				if (exam == null) {
					exam = getExam(session);
				}
				if (exam.getNegativeMark() != null && exam.getNegativeMark() > 0) {
					negativeExam = true;
				} else {
					negativeExam = false;
				}
				QuestionsId id = new QuestionsId();
				id.setQno(qNo);
				id.setQtype(test);
				questionObj = (Questions) session.get(Questions.class, id);
				if (questionObj != null) {
					question = questionObj.getQuestion();
					optionA = questionObj.getOptiona();
					optionB = questionObj.getOptionb();
					optionC = questionObj.getOptionc();
					optionD = questionObj.getOptiond();
					qAnswer = questionObj.getAnswer();
					questionType = questionObj.getQuestionType();
					if (questionObj.getIsImage() != null && questionObj.getIsImage()) {
						imageName = id.getQno() + "_" + id.getQtype() + "_" + question;
					} else {
						imageName = null;
					}
				}

			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return questionObj;
	}

	private Integer getCriteria(Session hibernateSession) {
		Criteria criteria = hibernateSession.createCriteria(Exam.class)
				.setProjection(Projections.property("passingcriteria"));
		criteria.add(Restrictions.eq("testname", test));
		Integer passingCriteria = (Integer) criteria.uniqueResult();
		return passingCriteria;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String setResult() {
		String action = "finishExam";
		try {
			Integer loginAttempt = (Integer) TpoUtil.getSession().getAttribute("loginAttempt");
			if (sessionFactory == null) {
				sessionFactory = sessionObject.getSessionFactory();
			}
			Session hibernateSession = sessionFactory.getCurrentSession();
			if (hibernateSession == null) {
				hibernateSession = sessionFactory.openSession();
			}
			StringBuffer stringBuffer = new StringBuffer();
			Integer criteria = getCriteria(hibernateSession);
			Result resultUpdate = getResultObjectToUpdate(loginAttempt, hibernateSession);
			ResultBean resultBean = (ResultBean) TpoUtil.getManagedBean(ResultBean.class.getSimpleName());
			// To-Do
			resultBean.setFirstName(firstName);
			resultBean.setLastName(lastName);
			resultBean.setEmail(email);
			Double percent = 0.0;
			if (exam == null) {
				exam = getExam(hibernateSession);
			}
			percent = calculatePercentage(hibernateSession, percent, exam);

			if (role != null) {
				stringBuffer.append(role);
				if (resultUpdate != null) {
					setResult(resultUpdate, percent);
					if (percent >= criteria) {
						resultUpdate.setResult(CCPConstant.Qualified);
						stringBuffer.append(FbMessageUtil.getLabel("you_are_qualified"));
						resultBean.setName(stringBuffer.toString());
						stringBuffer = new StringBuffer();
						stringBuffer.append(FbMessageUtil.getLabel("You_got")).append(percent)
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "%")
								.append(FbMessageUtil.getLabel("and_our_criteria_was")).append(criteria)
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "%");
						resultBean.setCriteria(stringBuffer.toString());
						resultBean.setResult(CCPConstant.Qualified);
					} else {
						stringBuffer.append(FbMessageUtil.getLabel("you_are_disqualified"));
						resultBean.setName(stringBuffer.toString());
						stringBuffer = new StringBuffer();
						stringBuffer.append(FbMessageUtil.getLabel("You_got")).append(percent)
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "%")
								.append(FbMessageUtil.getLabel("and_our_criteria_was")).append(criteria)
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "%");
						resultBean.setCriteria(stringBuffer.toString());
						resultBean.setResult(CCPConstant.Disqualified);
					}
					hibernateSession.update(resultUpdate);
				}
			} else {
				StringBuffer emailMessage = new StringBuffer();
				List<String> address = new ArrayList<String>(1);
				stringBuffer.append(FbMessageUtil.getLabel("Dear")).append(" ").append(resultBean.getFirstName())
						.append(" ").append(resultBean.getLastName()).append(",<br>");
				if (resultUpdate != null) {
					List<File> certificateFileList = new ArrayList<>(1);
					address.add(resultBean.getEmail());
					setResult(resultUpdate, percent);
					resultBean.setExam(exam);
					String subject = FbMessageUtil.getLabel("Your_Result");
					if (percent >= criteria) {
						subject = subject + " " + FbMessageUtil.getLabel("Congratulation");
						stringBuffer.append("<font size='5' color='green'>" + FbMessageUtil.getLabel("Congratulation")
								+ "</font><br>");
						stringBuffer.append(FbMessageUtil.getLabel("Congratulation") + " "
								+ FbMessageUtil.getLabel("you_are_qualified"));
						resultBean.setName(stringBuffer.toString());
						emailMessage.append(stringBuffer.toString());
						stringBuffer = new StringBuffer();
						stringBuffer.append(FbMessageUtil.getLabel("You_got")).append(" ").append(percent).append(" ")
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "% ")
								.append(FbMessageUtil.getLabel("and_our_criteria_was")).append(" ").append(criteria)
								.append(" ")
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "% ");
						resultBean.setCriteria(stringBuffer.toString());
						resultBean.setResult(CCPConstant.Qualified);
						resultUpdate.setResult(CCPConstant.Qualified);
						if ((exam.getFirstClassMark() != 0 && percent >= exam.getFirstClassMark())
								&& (exam.getHonoursMark() != 0 && percent < exam.getHonoursMark())) {
							resultBean.setResult(FbMessageUtil.getLabel("Qualified_in_First_Class"));
							emailMessage.append("<br>")
									.append("<font size='5' color='cyan'>" + resultBean.getResult() + "</font><br>");
							subject = subject + "(" + resultBean.getResult() + ")";
							resultBean.setResultIn(FbMessageUtil.getLabel("First_Class"));
							resultUpdate.setResult(CCPConstant.QualifiedinFirstClass);
						}
						if ((exam.getHonoursMark() != 0 && percent >= exam.getHonoursMark())) {
							resultBean.setResult(FbMessageUtil.getLabel("Qualified_in_Honours_Class"));
							emailMessage.append("<br>")
									.append("<font size='5' color='orange'>" + resultBean.getResult() + "</font><br>");
							subject = subject + "(" + resultBean.getResult() + ")";
							resultBean.setResultIn(FbMessageUtil.getLabel("Honours"));
							resultUpdate.setResult(CCPConstant.QualifiedInHonours);
						}
						emailMessage.append("<br>");
						emailMessage.append(stringBuffer.toString());
						resultBean.setPercent(percent);
						if (exam.getAllowCertDownload()) {
							byte[] certificate = pDFGenerator.generateCertificate(resultBean);

							if (certificate != null) {
								String certFileName = "Certificate_" + resultUpdate.getTestName() + "_"
										+ resultUpdate.getId().getLoginname() + "_" + resultUpdate.getTotalnumbers()
										+ ".pdf";

								fileUploadUtility.uploadFileWithByteArrayWithExt(getFileServiceUrl() + "/upload",
										certFileName, certificate,
										IMAGECONS.student.toString() + resultUpdate.getId().getLoginname() + "/"
												+ IMAGECONS.certificate.toString());
								emailMessage.append("<br>");
								emailMessage.append(FbMessageUtil.getLabel("please_find_attached_cert"));
								resultBean.setCertFileName(certFileName + ".pdf");
								File file = new File(certFileName);
								try {

									OutputStream os = new FileOutputStream(file);
									os.write(certificate);
									os.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
								resultBean.setCertificate(certificate);
								certificateFileList.add(file);
							}
						} else {
							resultBean.setCertificate(null);
						}

					} else {
						subject = subject + " " + FbMessageUtil.getLabel("opps");
						stringBuffer
								.append("<font size='5' color='red'>" + FbMessageUtil.getLabel("opps") + "</font><br>");
						stringBuffer.append(FbMessageUtil.getLabel("you_are_disqualified"));
						resultBean.setName(stringBuffer.toString());
						emailMessage.append(stringBuffer.toString());
						stringBuffer = new StringBuffer();
						stringBuffer.append(FbMessageUtil.getLabel("You_got")).append(" ").append(percent)
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "% ")
								.append(FbMessageUtil.getLabel("and_our_criteria_was")).append(" ").append(criteria)
								.append(" ")
								.append(CCPConstant.NUMBER.equals(exam.getResultType())
										? " "
										: "% ");
						resultBean.setCriteria(stringBuffer.toString());
						resultBean.setResult(CCPConstant.Disqualified);
						resultUpdate.setResult(CCPConstant.Disqualified);
						emailMessage.append("<br>");
						emailMessage.append(stringBuffer.toString());
					}
					if (resultUpdate.getTotalTimeTaken() > resultUpdate.getTotalTime()) {
						Object param[] = new Object[2];
						param[0] = resultUpdate.getTotalTimeTaken();
						param[1] = resultUpdate.getTotalTime();
						resultBean.setError(FbMessageUtil.getLabel("exam_error_1", param));
						return action;
					}
					hibernateSession.update(resultUpdate);
					deleteRefreshResult(hibernateSession);
					EmailUtil emailUtill = getEmailInstance();
					if (emailUtill != null && exam.getShowResult()) {
						emailMessage.append(TpoUtil.getMesageString());
						if (certificateFileList != null && certificateFileList.size() > 0) {
							emailUtill.sendEmailWithAttachment(TpoUtil.ADMIN_EMAIL, address.get(0),
									emailMessage.toString(), subject, certificateFileList);
						} else {
							emailUtill.postMail(address, subject, emailMessage.toString(), TpoUtil.ADMIN_EMAIL,
									Message.RecipientType.TO);
						}
					}
				}
			}

		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			TpoUtil.replaceManagedBean(QuestionBean.class.getSimpleName(), new QuestionBean());
		}
		return action;
	}

	private Double calculatePercentage(Session hibernateSession, Double percent, Exam exam) {
		double result;
		if (answersMap != null && answersMap.size() > 0 && exam != null) {
			if (CCPConstant.NUMBER.equals(exam.getResultType())) {

				StringBuffer Qnos = null;
				for (Map.Entry<Integer, String> entry : answersMap.entrySet()) {
					if (Qnos == null) {
						Qnos = new StringBuffer();
						Qnos.append(entry.getKey());
					} else {
						Qnos.append(",").append(entry.getKey());
					}
				}
				String queryStr = "SELECT sum(assignedNo) FROM questions where qno in(" + Qnos.toString()
						+ ") and qtype='" + test + "'";
				NativeQuery<BigDecimal> query = hibernateSession.createSQLQuery(queryStr);
				BigDecimal no = (BigDecimal) query.uniqueResult();
				percent = no.doubleValue();
			} else {
				result = answersMap.size();
				percent = (result * 100) / noOfQuestions;
			}
		}
		if (CCPConstant.NUMBER.equals(exam.getResultType())) {
			if (exam.getNegativeMark() > 0.0) {
				int totalNegativeQuestion = submmitedSet.size() - answersMap.size();
				percent = percent - (totalNegativeQuestion * exam.getNegativeMark());
			}
		}
		if (percent < 0) {
			percent = Double.valueOf(Math.round(percent));
		} else {
			percent = Double.valueOf(Math.floor(percent));
		}
		return percent;
	}

	private Exam getExam(Session hibernateSession) {
		Criteria criteriaEx = hibernateSession.createCriteria(Exam.class);
		criteriaEx.add(Restrictions.eq("testname", test));
		Exam exam = (Exam) criteriaEx.uniqueResult();
		return exam;
	}

	private void setResult(Result resultUpdate, Double percent) {
		resultUpdate.setTotalnumbers(percent);
		if (time != null) {
			resultUpdate.setTotalTimeTaken(resultUpdate.getTotalTime() - time);
		}
		StringBuffer correctQuestionNOs = null;
		StringBuffer actualQuestionNOs = null;
		if (answersMap != null && answersMap.size() > 0) {
			for (Map.Entry<Integer, String> entry : answersMap.entrySet()) {
				if (correctQuestionNOs == null) {
					correctQuestionNOs = new StringBuffer();
					correctQuestionNOs.append(entry.getKey());
				} else {
					correctQuestionNOs.append(",").append(entry.getKey());
				}
			}

			for (String s : correctQuestionNOs.toString().split(",")) {
				questionList.remove(Integer.valueOf(s.trim()));
			}
			if (questionList != null && questionList.size() > 0) {
				for (Integer no : questionList) {
					if (actualQuestionNOs == null) {
						actualQuestionNOs = new StringBuffer();
						actualQuestionNOs.append(no);
					} else {
						actualQuestionNOs.append(",").append(no);
					}
				}
			}

		} else {
			if (questionList != null && questionList.size() > 0) {
				for (Integer no : questionList) {
					if (actualQuestionNOs == null) {
						actualQuestionNOs = new StringBuffer();
						actualQuestionNOs.append(no);
					} else {
						actualQuestionNOs.append(",").append(no);
					}
				}
			}
		}
		StringBuffer allNumbers = new StringBuffer();
		if (correctQuestionNOs != null && actualQuestionNOs != null) {
			allNumbers.append(correctQuestionNOs).append("#").append(actualQuestionNOs.toString());
		}
		if (correctQuestionNOs == null && actualQuestionNOs != null) {
			allNumbers.append("#").append(actualQuestionNOs.toString());
		}
		if (correctQuestionNOs != null && actualQuestionNOs == null) {
			allNumbers.append(correctQuestionNOs.toString()).append("#");
		}

		resultUpdate.setQuestions(allNumbers.toString());

	}

	private Result getResultObjectToUpdate(Integer loginAttempt, Session hibernateSession) {
		ResultId id = new ResultId(loginName, loginAttempt);
		Result resultUpdate = (Result) hibernateSession.get(Result.class, id);
		return resultUpdate;
	}

	public void checkAnswer(String answer, int qNo) {
		if (submmitedSet != null) {
			submmitedSet.add(qNo - 1);
		}
		if (answersMap != null) {
			char[] chars1 = answer.toCharArray();
			Arrays.sort(chars1);
			String answer1 = new String(chars1);
			int actualQno = questionList.get(qNo - 2);
			if (answersMap.get(actualQno) != null) {
				char[] chars = answersMap.get(actualQno).toCharArray();
				Arrays.sort(chars);
				String sorted = new String(chars);
				if (!sorted.equalsIgnoreCase(answer1)) {
					answersMap.remove(actualQno);
				}
			} else {
				char[] chars = qAnswer.toCharArray();
				Arrays.sort(chars);
				String sorted = new String(chars);
				if (sorted.equalsIgnoreCase(answer1)) {
					answersMap.put(actualQno, answer);
				}
			}
		}
	}

	/**
	 * @return the loginName
	 */
	public synchronized String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public synchronized void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the optionA
	 */
	public String getOptionA() {
		return optionA;
	}

	/**
	 * @param optionA the optionA to set
	 */
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}

	/**
	 * @return the optionB
	 */
	public String getOptionB() {
		return optionB;
	}

	/**
	 * @param optionB the optionB to set
	 */
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}

	/**
	 * @return the optionC
	 */
	public String getOptionC() {
		return optionC;
	}

	/**
	 * @param optionC the optionC to set
	 */
	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}

	/**
	 * @return the optionD
	 */
	public String getOptionD() {
		return optionD;
	}

	/**
	 * @param optionD the optionD to set
	 */
	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the questionNo
	 */
	public Integer getQuestionNo() {
		return questionNo;
	}

	/**
	 * @param questionNo the questionNo to set
	 */
	public void setQuestionNo(Integer questionNo) {
		this.questionNo = questionNo;
	}

	/**
	 * @return the finisButton
	 */
	public Boolean getFinisButton() {
		return finisButton;
	}

	/**
	 * @param finisButton the finisButton to set
	 */
	public void setFinisButton(Boolean finisButton) {
		this.finisButton = finisButton;
	}

	/**
	 * @return the startButton
	 */
	public Boolean getStartButton() {
		return startButton;
	}

	/**
	 * @param startButton the startButton to set
	 */
	public void setStartButton(Boolean startButton) {
		this.startButton = startButton;
	}

	/**
	 * @return the nextButton
	 */
	public Boolean getNextButton() {
		return nextButton;
	}

	/**
	 * @param nextButton the nextButton to set
	 */
	public void setNextButton(Boolean nextButton) {
		this.nextButton = nextButton;
	}

	/**
	 * @return the submitButton
	 */
	public Boolean getSubmitButton() {
		return submitButton;
	}

	/**
	 * @param submitButton the submitButton to set
	 */
	public void setSubmitButton(Boolean submitButton) {
		this.submitButton = submitButton;
	}

	/**
	 * @return the messageLabel
	 */
	public Boolean getMessageLabel() {
		return messageLabel;
	}

	/**
	 * @param messageLabel the messageLabel to set
	 */
	public void setMessageLabel(Boolean messageLabel) {
		this.messageLabel = messageLabel;
	}

	/**
	 * @return the test
	 */
	public synchronized String getTest() {
		return test;
	}

	/**
	 * @param test the test to set
	 */
	public synchronized void setTest(String test) {
		this.test = test;
	}

	/**
	 * @return the time
	 */
	public synchronized Double getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public synchronized void setTime(Double time) {
		this.time = time;
	}

	/**
	 * @return the minute
	 */
	public synchronized String getMinute() {
		return minute;
	}

	/**
	 * @param minute the minute to set
	 */
	public synchronized void setMinute(String minute) {
		this.minute = minute;
	}

	/**
	 * @return the second
	 */
	public synchronized String getSecond() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public synchronized void setSecond(String second) {
		this.second = second;
	}

	/**
	 * @return the questionList
	 */
	public synchronized List<Integer> getQuestionList() {
		return questionList;
	}

	/**
	 * @return the startNumber
	 */
	public synchronized int getStartNumber() {
		return startNumber;
	}

	/**
	 * @param startNumber the startNumber to set
	 */
	public synchronized void setStartNumber(int startNumber) {
		this.startNumber = startNumber;
	}

	/**
	 * @return the endNumber
	 */
	public synchronized int getEndNumber() {
		return endNumber;
	}

	/**
	 * @param endNumber the endNumber to set
	 */
	public synchronized void setEndNumber(int endNumber) {
		this.endNumber = endNumber;
	}

	/**
	 * @return the noOfQuestions
	 */
	public synchronized int getNoOfQuestions() {
		return noOfQuestions;
	}

	/**
	 * @param noOfQuestions the noOfQuestions to set
	 */
	public synchronized void setNoOfQuestions(int noOfQuestions) {
		this.noOfQuestions = noOfQuestions;
	}

	/**
	 * @return the role
	 */
	public synchronized String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public synchronized void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Boolean getNegativeExam() {
		return negativeExam;
	}

	public void setNegativeExam(Boolean negativeExam) {
		this.negativeExam = negativeExam;
	}

	public void deleteRefreshResult(Session hibernateSession) {

		if (hibernateSession == null) {
			hibernateSession = sessionFactory.openSession();

		}
		SessionData pojo = (SessionData) hibernateSession.get(SessionData.class, loginName);
		hibernateSession.delete(pojo);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateResult() {
		refreshResult();
	}

	public void refreshResult() {
		Session hibernateSession = sessionFactory.getCurrentSession();
		if (hibernateSession == null) {
			hibernateSession = sessionFactory.openSession();
		}
		SessionData pojo = null;
		Map<String, SessionData> map = null;
		Criteria criteria = (Criteria) hibernateSession.createCriteria(SessionData.class);
		pojo = (SessionData) criteria.add(Restrictions.eq("enrollmentNumber", loginName)).uniqueResult();

		// Db Call

		if (pojo == null) {
			pojo = new SessionData();
			pojo.setEnrollmentNumber(loginName);
			pojo.setTimeLeft(Double.valueOf(Integer.valueOf(minute) + 1));
			if (exam == null) {
				exam = getExam(hibernateSession);
			}
			Integer loginAttempt = (Integer) TpoUtil.getSession().getAttribute("loginAttempt");
			pojo.setAttempt(loginAttempt);
			pojo.setCreatedBy(exam.getCreatedBy());
			pojo.setCreatedDate(Calendar.getInstance().getTime());
		} else {

			double timeTaken = pojo.getTimeLeft() - (getTime() == null ? 0 : getTime());
			double time = pojo.getTimeLeft() - timeTaken;
			if (time < 1) {
				time = Math.round(time);
			}
			pojo.setTimeLeft(time);
		}
		if (questionList != null)
			pojo.setTotalQuestions(questionList.size());
		if (submmitedSet != null)
			pojo.setAnswersSubmitted(submmitedSet.size());
		if (answersMap != null)
			pojo.setCorrectAns(answersMap.size());

		Double percent = 0.0;
		percent = calculatePercentage(hibernateSession, percent, exam);
		pojo.setPercentageOrNumber(percent);
		Integer crit = exam.getPassingcriteria();
		pojo.setResult(CCPConstant.Disqualified);
		if (percent >= crit) {
			pojo.setResult(CCPConstant.Qualified);
		}
		if ((exam.getFirstClassMark() != 0 && percent >= exam.getFirstClassMark())
				&& (exam.getHonoursMark() != 0 && percent < exam.getHonoursMark())) {
			pojo.setResult(CCPConstant.QualifiedinFirstClass);
		}
		if ((exam.getHonoursMark() != 0 && percent >= exam.getHonoursMark())) {
			pojo.setResult(CCPConstant.QualifiedInHonours);
		}
		hibernateSession.saveOrUpdate(pojo);
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean getSessionExpiredMessage() {
		return sessionExpiredMessage;
	}

	public void setSessionExpiredMessage(boolean sessionExpiredMessage) {
		this.sessionExpiredMessage = sessionExpiredMessage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
