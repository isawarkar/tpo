/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.beans;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;

import org.apache.catalina.core.ApplicationPart;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.hibernate.Exam;
import tpo.hibernate.Questions;
import tpo.hibernate.QuestionsId;
import tpo.util.CCPConstant;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("QuestionAddBean")
@Transactional(readOnly = true)
@Scope("session")
public class QuestionAddBean {

	private Logger logger = LoggerFactory.getLogger(QuestionAddBean.class);

	private Questions questions;

	private QuestionsId id;

	private String currentDocMode = CCPConstant.CREATE;

	private String resultType;

	private ApplicationPart file;

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void insertQuestion() {
		try {
			questions.setQuestion(new String (questions.getQuestion().getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			questions.setOptiona(new String (questions.getOptiona().getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			questions.setOptionb(new String (questions.getOptionb().getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			questions.setOptionc(new String (questions.getOptionc().getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			questions.setOptiond(new String (questions.getOptiond().getBytes (StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
			
			if (CCPConstant.MULTIPLE.equals(questions.getQuestionType())) {
				String strArr[] = questions.getAnswer().split(",");
				if (strArr == null || strArr.length == 0) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_answer"));
					return;
				}
				if (strArr.length > 4) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("You_should_have_maximum_four_answers"));
					return;
				}
				for (String ans : strArr) {
					if (!ans.matches("[a-dA-D]")) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_answer"));
						return;
					}
				}
			} else if (CCPConstant.SINGLE.equals(questions.getQuestionType())) {
				String strArr[] = questions.getAnswer().split(",");
				if (strArr == null || strArr.length > 1) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_answer_single"));
					return;
				}
			}
			Session session = sessionFactory.getCurrentSession();
			if (!"".equals(file.getSubmittedFileName())) {
				if (file.getSize() > TpoUtil.IMAGE_SIZE) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_select_small_size_image_Photo"));
					return;
				}
				if (TpoUtil.imageTypes.contains(file.getContentType())) {
					Blob blob = Hibernate.getLobCreator(session).createBlob(file.getInputStream(), file.getSize());
					questions.setImage(blob);
					questions.setIsImage(true);
				}else{
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Only_Image_Type_can_be_uploaded"));
					return;
				}

			}

			Criteria criteria = session.createCriteria(Exam.class);
			criteria.add(Restrictions.eq("testname", id.getQtype()));
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				criteria.add(Restrictions.eq("createdBy", user.getUserName()));
			}
			Exam exam = (Exam) criteria.uniqueResult();
			if (exam != null && CCPConstant.NUMBER.equalsIgnoreCase(exam.getResultType())
					&& questions.getAssignedNo() == null) {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_Assigned_No"));
				return;
			} else {
				questions.setAssignedNo(0d);
			}
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				setQuestionNo(id.getQtype());
				criteria = session.createCriteria(Questions.class);
				criteria.add(Restrictions.eq("id", id));
				if (criteria.uniqueResult() != null) {
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Question_already_exists_for", id.getQno()));
				} else {
					questions.setId(id);
					session.save(questions);
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Question_addedd_successfully"));
				}
			} else {
				session.update(questions);
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("Question_updated_successfully"));
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Questions getQuestions() {
		return questions;
	}

	public void setQuestions(Questions questions) {
		this.questions = questions;
	}

	public QuestionsId getId() {
		return id;
	}

	public void setId(QuestionsId id) {
		this.id = id;
	}

	public String getCurrentDocMode() {
		return currentDocMode;
	}

	public void setCurrentDocMode(String currentDocMode) {
		this.currentDocMode = currentDocMode;
	}

	public void setQuestionNo(String examName) {
		try {
			if (CCPConstant.CREATE.equals(currentDocMode)) {
				Session session = sessionFactory.getCurrentSession();
				String queryStr = "SELECT max(qno) FROM questions q where qtype='" + examName + "'";
				AdminUser user = AdminUser.getUser();
				if (user != null
						&& (CCPConstant.USER.equals(user.getRole()) || CCPConstant.ADMIN.equals(user.getRole()))) {
					queryStr = queryStr + " and qtype in(SELECT testname FROM exam where createdBy='"
							+ AdminUser.getUser().getUserName() + "')";
				}
				@SuppressWarnings("rawtypes")
				NativeQuery<Integer> query = session.createSQLQuery(queryStr);
				Integer no = (Integer) query.uniqueResult();
				String queryExamStr = "SELECT resultType FROM exam where testname ='" + examName + "' AND createdBy='"
						+ AdminUser.getUser().getUserName() + "'";
				NativeQuery<String> queryExam = session.createSQLQuery(queryExamStr);
				resultType = (String) queryExam.uniqueResult();
				if (no == null && resultType == null) {
					UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Not_a_valid_exam"));
					id.setQno(null);
				} else if (resultType != null && no == null) {
					id.setQno(0);
				} else {
					id.setQno(no.intValue() + 1);
				}

			}
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public ApplicationPart getFile() {
		return file;
	}

	public void setFile(ApplicationPart file) {
		this.file = file;
	}

}
