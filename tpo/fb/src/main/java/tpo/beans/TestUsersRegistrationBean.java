package tpo.beans;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
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
import tpo.dao.CommonDBBean;
import tpo.hibernate.Achivements;
import tpo.hibernate.Backdetails;
import tpo.hibernate.College;
import tpo.hibernate.Contactinfo;
import tpo.hibernate.Percentageinfo;
import tpo.hibernate.Personalinfo;
import tpo.hibernate.Registration;
import tpo.util.Encryption;
import tpo.util.FbMessageUtil;
import tpo.util.TpoUtil;

@Repository("TestUsersRegistrationBean")
@Transactional(readOnly = true)
@Scope("request")
public class TestUsersRegistrationBean extends Parent {

	private Logger logger = LoggerFactory.getLogger(TestUsersRegistrationBean.class);

	@Autowired
	private SessionFactory sessionFactory;

	private String prefixInENO;

	private String deletePrefixEno;

	private int startRange;

	private int endRange;

	private String password;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String saveRecord() {
		Session session;
		try {
			int tempStartRage = startRange;
			CommonDBBean bean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			if (bean != null) {
				while (tempStartRage < endRange) {
					String rn = prefixInENO + "_" + startRange;
					if (bean.isRecordExist(rn)) {
						UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Record_already_exist_for", rn));
						return "";
					}
					tempStartRage++;
				}
			}
			tempStartRage = 0;

			String collegeName = null;
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				session = sessionFactory.getCurrentSession();
				Criteria criteria = session.createCriteria(College.class);
				criteria.add(Restrictions.eq("logindetails.userName", user.getUserName()));
				List<College> collegeList = criteria.list();
				if (collegeList != null && collegeList.size() > 0) {
					collegeName = collegeList.get(0).getCollegeName();
				}
				if (collegeName != null) {
					while (startRange < endRange) {
						Registration registration = new Registration();
						Personalinfo personalinfo = new Personalinfo();
						Percentageinfo percentageinfo = new Percentageinfo();
						Contactinfo contactinfo = new Contactinfo();
						Backdetails backdetails = new Backdetails();
						Achivements achivements = new Achivements();

						registration.setRollnumber(prefixInENO +startRange);
						registration.setFirstName("Test");
						registration.setLastName("User");
						registration.setCollegeName(collegeName);
						registration.setPassword(Encryption.getEncryptedString(password));
						registration.setEmail(user.getEmail());
						registration.setEmailVarified(true);
						registration.setApproved(false);
						registration.setLastUpdated(Calendar.getInstance().getTime());
						registration.setLastUpdatedBy(user.getUserName());
						registration.setStatus(true);
						registration.setTheme("T2");
						registration.setColor(1);
						session.save(registration);

						percentageinfo.setRollnumber(registration.getRollnumber());
						percentageinfo.setRegistration(registration);
						percentageinfo.setHighSchoolPassing(0);
						percentageinfo.setHighSchoolPercent(0.0);
						percentageinfo.setHighSchoolBoard("NA");
						percentageinfo.setLastUpdated(Calendar.getInstance().getTime());
						percentageinfo.setLastUpdatedBy(user.getUserName());
						registration.setPercentageinfo(percentageinfo);

						session.save(percentageinfo);

						personalinfo.setRollnumber(registration.getRollnumber());
						registration.setPersonalinfo(personalinfo);
						personalinfo.setRegistration(registration);
						personalinfo.setSemester("NA");
						personalinfo.setGender("X");
						personalinfo.setDob(Calendar.getInstance().getTime());
						personalinfo.setCurrentCourse("NA");
						personalinfo.setLastUpdated(Calendar.getInstance().getTime());
						personalinfo.setLastUpdatedBy(user.getUserName());
						session.save(personalinfo);

						backdetails.setRollnumber(registration.getRollnumber());
						backdetails.setRegistration(registration);
						registration.setBackdetails(backdetails);
						backdetails.setBackLog(0);
						backdetails.setPassMoreThenOneAttempt(0);
						backdetails.setNumberOfBacklogs(0);
						backdetails.setBaGroup(0);
						backdetails.setEducationGap((short) 0);
						backdetails.setBlackList(false);
						backdetails.setLastUpdated(Calendar.getInstance().getTime());
						backdetails.setLastUpdatedBy(user.getUserName());
						session.save(backdetails);

						contactinfo.setRollnumber(registration.getRollnumber());
						registration.setContactinfo(contactinfo);
						contactinfo.setRegistration(registration);
						contactinfo.setMobileNumber("NA");
						contactinfo.setNumberVerified(false);
						contactinfo.setPresentAddress("NA");
						contactinfo.setPresentCity("NA");
						contactinfo.setPresentState("NA");
						contactinfo.setPermanentAddress("NA");
						contactinfo.setPermanentCity("NA");
						contactinfo.setPermanentState("NA");
						contactinfo.setHieght(0.0);
						contactinfo.setWeight(0);
						contactinfo.setGlassPowerLeft("6/6");
						contactinfo.setGlassPowerRight("6/6");
						contactinfo.setLastUpdated(Calendar.getInstance().getTime());
						contactinfo.setLastUpdatedBy(user.getUserName());
						session.save(contactinfo);

						achivements.setRollnumber(registration.getRollnumber());
						achivements.setRegistration(registration);
						achivements.setLastUpdated(Calendar.getInstance().getTime());
						achivements.setLastUpdatedBy(user.getUserName());
						session.save(achivements);
						startRange++;
					}
					endRange = endRange + startRange;
					UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("users_generated_successfully", startRange-1));
				}
			}
			return "";
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String deleteRecords() {
		Session session;
		try {
			AdminUser user = AdminUser.getUser();
			if (user != null) {
				session = sessionFactory.getCurrentSession();
				NativeQuery<?> query = session.createSQLQuery("delete from registration where rollnumber like '"
						+ deletePrefixEno + "%' and lastUpdatedBy = '" + user.getUserName() + "'");
				int totalCount = (int) query.executeUpdate();
				// percentageinfo
				query = session.createSQLQuery("delete from percentageinfo where rollnumber like '" + deletePrefixEno
						+ "%' and lastUpdatedBy = '" + user.getUserName() + "'");
				query.executeUpdate();

				// contactinfo
				query = session.createSQLQuery("delete from contactinfo where rollnumber like '" + deletePrefixEno
						+ "%' and lastUpdatedBy = '" + user.getUserName() + "'");
				query.executeUpdate();

				// backdetails
				query = session.createSQLQuery("delete from backdetails where rollnumber like '" + deletePrefixEno
						+ "%' and lastUpdatedBy = '" + user.getUserName() + "'");
				query.executeUpdate();

				// personalinfo
				query = session.createSQLQuery("delete from personalinfo where rollnumber like '" + deletePrefixEno
						+ "%' and lastUpdatedBy = '" + user.getUserName() + "'");
				query.executeUpdate();
				// achivements
				query = session.createSQLQuery("delete from achivements where rollnumber like '" + deletePrefixEno
						+ "%' and lastUpdatedBy = '" + user.getUserName() + "'");
				query.executeUpdate();
				UIBackingBean.setSuccessMessage(FbMessageUtil.getLabel("users_deleted_successfully", totalCount));
			}
			return "";
		} catch (HibernateException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	public String getPrefixInENO() {
		return prefixInENO;
	}

	public void setPrefixInENO(String prefixInENO) {
		this.prefixInENO = prefixInENO;
	}

	public int getStartRange() {
		return startRange;
	}

	public void setStartRange(int startRange) {
		this.startRange = startRange;
	}

	public int getEndRange() {
		return endRange;
	}

	public void setEndRange(int endRange) {
		this.endRange = endRange;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDeletePrefixEno() {
		return deletePrefixEno;
	}

	public void setDeletePrefixEno(String deletePrefixEno) {
		this.deletePrefixEno = deletePrefixEno;
	}

}
