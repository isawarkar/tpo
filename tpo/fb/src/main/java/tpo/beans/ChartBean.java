package tpo.beans;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.openfaces.component.chart.ChartModel;
import org.openfaces.component.chart.PieChartView;
import org.openfaces.component.chart.PieSectorInfo;
import org.openfaces.component.chart.PlainModel;
import org.openfaces.component.chart.PlainSeries;
import org.openfaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tpo.admin.beans.AdminUser;
import tpo.admin.beans.RegistrationTableBean;
import tpo.hibernate.Company;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.util.CCPConstant;
import tpo.util.FbResourceUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Repository("ChartBean")
@Transactional(readOnly = true)
@Scope("session")
public class ChartBean {

	public Integer hallticketId = null;
	public String comapnayName = null;

	public PieChartView pieChartView;

	public PieChartView resultChartView;

	public PieChartView studentChartView;

	public PieChartView companyChartView;

	public PieChartView studentSelectedChartView;

	public int totalResultCount;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public ChartModel getStudentChart() {
		Session session = sessionFactory.getCurrentSession();
		AdminUser user = AdminUser.getUser();
		List<String> collegeList = null;
		if (user != null) {
			collegeList = AdminUser.getUser().getCollegeList();
		}
		NativeQuery<?> query;
		String quertStr = "select count(r.rollnumber) from registration r where r.collegeName in ("
				+ TpoUtil.getComaSeprateValue(collegeList) + ")";
		query = session.createNativeQuery(quertStr.toString());
		int totalStudent = ((BigInteger) query.getSingleResult()).intValue();
		if (totalStudent > 0) {
			quertStr = "select count(r.rollnumber) from registration r where r.approved=true and r.collegeName in ("
					+ TpoUtil.getComaSeprateValue(collegeList) + ")";
			query = session.createNativeQuery(quertStr.toString());
			int approved = ((BigInteger) query.getSingleResult()).intValue();

			quertStr = "select count(r.rollnumber) from registration r ,backdetails b where r.rollnumber=b.rollnumber and b.blackList=true and r.collegeName in ("
					+ TpoUtil.getComaSeprateValue(collegeList) + ")";
			query = session.createNativeQuery(quertStr.toString());
			int blackListed = ((BigInteger) query.getSingleResult()).intValue();

			quertStr = "select count(r.rollnumber) from registration r ,personalinfo p where r.rollnumber=p.rollnumber and p.companyName!='' and r.collegeName in ("
					+ TpoUtil.getComaSeprateValue(collegeList) + ")";
			query = session.createNativeQuery(quertStr.toString());
			int selected = ((BigInteger) query.getSingleResult()).intValue();
			Map<String, Integer> data = new HashMap<String, Integer>();

			data.put(FbResourceUtil.getLabel(CCPConstant.TOTAL), new Integer(totalStudent));
			data.put(FbResourceUtil.getLabel("Black_Listed"), new Integer(blackListed));
			data.put(FbResourceUtil.getLabel(CCPConstant.APPROVED), new Integer(approved));
			data.put(FbResourceUtil.getLabel(CCPConstant.PENDING), new Integer(totalStudent - approved));
			data.put(FbResourceUtil.getLabel(CCPConstant.SELECTED), new Integer(selected));

			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("StudentChart");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getCompanyChart() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class)
				.setProjection(Projections.projectionList().add(Projections.property("companyname"), "companyname")
						.add(Projections.property("packageOffering"), "packageOffering"));
		String userName = AdminUser.getUser().getUserName();
		criteria.add(Restrictions.eq("createdBy", userName));
		criteria.add(Restrictions.ne("packageOffering", ""));
		criteria.addOrder(Order.asc("packageOffering"));
		criteria.setMaxResults(10);
		List<Object> companyList = criteria.list();
		if (companyList != null && !companyList.isEmpty()) {
			Map<String, Integer> data = new HashMap<String, Integer>();
			for (int i = 0; i < companyList.size(); i++) {
				Object[] obj = (Object[]) companyList.get(i);
				data.put((String) obj[0], new Integer((String) obj[1]));
			}
			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("CompanyChart");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getResultAnalysis() {
		Session session = sessionFactory.getCurrentSession();
		AdminUser user = AdminUser.getUser();
		String userList = TpoUtil.getComaSeprateValue(TpoUtil.getUserList(user.getUserName()));
		String queryStr = "SELECT m.Qualified FROM (SELECT count(q.result) as Qualified FROM result q where q.result='Qualified' and q.createdBy in("
				+ userList
				+ ") UNION All SELECT count(q.result) as Disqualified FROM result q where q.result='DisQualified' and q.createdBy in("
				+ userList
				+ ") UNION All SELECT count(q.result) as FirstClass FROM result q where q.result='Qualified in First Class' and q.createdBy in("
				+ userList
				+ ") UNION All SELECT count(q.result) as Honours FROM result q where q.result='Qualified in Honours' and q.createdBy in("
				+ userList + ")) m;";
		NativeQuery<?> query = session.createNativeQuery(queryStr);
		List<BigInteger> resultList = (List<BigInteger>) query.getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			Map<String, Integer> data = new HashMap<String, Integer>();
			int qualified = resultList.get(0).intValue();
			int disqualified = resultList.get(1).intValue();
			int qualifiedinFirstClass = resultList.get(2).intValue();
			int qualifiedInHonours = resultList.get(3).intValue();
			if(qualified == 0 && disqualified == 0 && qualifiedinFirstClass == 0 && qualifiedInHonours == 0){
				return null;
			}

			totalResultCount = qualified + disqualified + qualifiedinFirstClass + qualifiedInHonours;
			data.put(FbResourceUtil.getLabel("Qualified_in_First_Class"), new Integer(qualifiedinFirstClass));
			data.put(FbResourceUtil.getLabel("DisQualified"), new Integer(disqualified));
			data.put(FbResourceUtil.getLabel("Qualified"), new Integer(qualified));
			data.put(FbResourceUtil.getLabel("Qualified_In_Honors"), new Integer(qualifiedInHonours));

			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("StudentChart");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getOpenningStatus() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(HallTicket.class);
		String userName = AdminUser.getUser().getUserName();
		criteria.add(Restrictions.eq("userName", userName));
		criteria.add(Restrictions.eq("isActive", true));
		List<HallTicket> opennings = criteria.list();
		if (opennings != null && !opennings.isEmpty()) {
			Map<String, Integer> data = new HashMap<String, Integer>();
			if (opennings != null && !opennings.isEmpty()) {
				for (HallTicket hallTicket : opennings) {
					data.put(hallTicket.getCompanyName() + "(" + hallTicket.getHallTicketId() + ")",
							new Integer(hallTicket.getHallTicketConnect().size()));
				}
			}
			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("OpenningStatus");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public ChartModel getSelectedIn() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Company.class).setProjection(Projections.property("companyname"));
		String userName = AdminUser.getUser().getUserName();
		List<String> userNames = AdminUser.getUser().getUserList();
		criteria.add(Restrictions.in("createdBy", userNames));
		List<String> companyList = criteria.list();
		if (companyList != null && !companyList.isEmpty()) {
			Map<String, Integer> data = new HashMap<String, Integer>();
			for (String companyName : companyList) {
				data.put(companyName, getStudentCount(userName, companyName, session));
			}
			PlainSeries series = new PlainSeries();
			series.setData(data);
			series.setKey("Selected In");

			PlainModel model = new PlainModel();
			model.addSeries(series);
			return model;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Integer getStudentCount(String userName, String companyName, Session session) {

		String queryStr = "select count(r.rollnumber) from personalinfo pi,registration r where pi.rollnumber = r.rollnumber and pi.companyName like '%"
				+ companyName + "%' and collegeName in ("
				+ TpoUtil.getComaSeprateValue(TpoUtil.getAllCollegeList(userName,null)) + ")";
		NativeQuery<BigInteger> query = session.createSQLQuery(queryStr);
		BigInteger totalCount = (BigInteger) query.uniqueResult();
		if (totalCount != null) {
			return totalCount.intValue();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public ChartModel getOpenningStatusById() {

		if (hallticketId != null) {
			PlainModel model = new PlainModel();
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(HallTicket.class);
			String userName = AdminUser.getUser().getUserName();
			criteria.add(Restrictions.eq("userName", userName));
			criteria.add(Restrictions.eq("hallTicketId", hallticketId));
			HallTicket openning = (HallTicket) criteria.uniqueResult();
			if (openning != null) {
				Map data = new HashMap();
				Set<HallTicketConnect> hallTicketConnects = null;
				if (openning != null && openning.getHallTicketConnect() != null
						&& openning.getHallTicketConnect().size() > 0) {
					hallTicketConnects = openning.getHallTicketConnect();
					int applied = 0;
					int approved = 0;
					for (HallTicketConnect hallTicket : hallTicketConnects) {
						if (hallTicket.getIsApplied()) {
							applied++;
						}
						if (hallTicket.getIsApproved()) {
							approved++;
						}
					}
					data.put(FbResourceUtil.getLabel("Applied_for") + openning.getCompanyName() + " is " + applied,
							new Integer(applied));
					data.put(FbResourceUtil.getLabel("Approved_for") + openning.getCompanyName() + " is " + approved,
							new Integer(approved));
					data.put(FbResourceUtil.getLabel("Shortlisted_for") + openning.getCompanyName() + " is "
							+ hallTicketConnects.size(), new Integer(hallTicketConnects.size()));

				}

				PlainSeries series = new PlainSeries();
				series.setData(data);
				series.setKey("OpenningStatusById");
				model.addSeries(series);
				return model;
			}
			return null;
		} else {
			return null;
		}
	}

	/*
	 * public String openningStatusAction() { PieSectorInfo sectorInfo =
	 * pieChartView.getSelectedSector(); OpenningListTableBean bean =
	 * (OpenningListTableBean)TpoUtil.getManagedBean(OpenningListTableBean.class
	 * .getSimpleName()); if(bean != null){ String str[] =
	 * sectorInfo.getKey().toString().split("\\("); if(str.length ==2){ String
	 * str1[] = str[1].split("\\)"); hallticketId = new Integer(str1[0]);
	 * comapnayName = str[0]; bean.setList(hallticketId,comapnayName); } }
	 * return "adminDashboard"; }
	 */

	public String companyStatusAction() {
		PieSectorInfo sectorInfo = companyChartView.getSelectedSector();
		CompanyTableBean bean = (CompanyTableBean) TpoUtil.getManagedBean(CompanyTableBean.class.getSimpleName());
		if (bean != null) {
			bean.setCompanyName(sectorInfo.getKey().toString().split("_")[0]);
		}
		return "companyList";
	}

	public String goToResultList() {
		PieSectorInfo sectorInfo = resultChartView.getSelectedSector();
		StudentTableBean bean = (StudentTableBean) TpoUtil.getManagedBean(StudentTableBean.class.getSimpleName());
		if (bean != null) {
			bean.setResult(sectorInfo.getKey().toString().split("_")[0]);
		}
		return "adminResultList";
	}

	public String goToStudentList() {
		PieSectorInfo sectorInfo = studentChartView.getSelectedSector();
		RegistrationTableBean bean = (RegistrationTableBean) TpoUtil
				.getManagedBean(RegistrationTableBean.class.getSimpleName());
		if (bean != null) {
			String status = sectorInfo.getKey().toString().split("_")[0];
			if (status != null) {
				bean.resetSearch();
				if (status.equals(CCPConstant.APPROVED)) {
					bean.setStatus("A");
				} else if (status.equals(CCPConstant.Black_Listed)) {
					bean.setBlackListed(true);
				} else if (status.equals(CCPConstant.SELECTED)) {
					bean.setSelected(true);
				} else if (status.equals(CCPConstant.PENDING)) {
					bean.setStatus("P");
				}
			}

		}
		return "studentList";
	}

	public String goToSelectedStudentList() {
		PieSectorInfo sectorInfo = studentSelectedChartView.getSelectedSector();
		RegistrationTableBean bean = (RegistrationTableBean) TpoUtil
				.getManagedBean(RegistrationTableBean.class.getSimpleName());
		if (bean != null) {
			String companyName = sectorInfo.getKey().toString().split("_")[0];
			if (companyName != null) {
				bean.resetSearch();
				bean.setCompanyName(companyName);
			}

		}
		return "studentList";
	}

	public String getTooltip() {
		PieSectorInfo sector = Faces.var("sector", PieSectorInfo.class);
		DecimalFormat format = new DecimalFormat("#,###");
		return (String) sector.getKey() + " - " + format.format(sector.getValue());
	}

	public Integer getHallticketId() {
		return hallticketId;
	}

	public void setHallticketId(Integer hallticketId) {
		this.hallticketId = hallticketId;
	}

	public PieChartView getPieChartView() {
		return pieChartView;
	}

	public void setPieChartView(PieChartView pieChartView) {
		this.pieChartView = pieChartView;
	}

	public String getComapnayName() {
		return comapnayName;
	}

	public void setComapnayName(String comapnayName) {
		this.comapnayName = comapnayName;
	}

	public PieChartView getResultChartView() {
		return resultChartView;
	}

	public void setResultChartView(PieChartView resultChartView) {
		this.resultChartView = resultChartView;
	}

	public PieChartView getStudentChartView() {
		return studentChartView;
	}

	public void setStudentChartView(PieChartView studentChartView) {
		this.studentChartView = studentChartView;
	}

	public PieChartView getCompanyChartView() {
		return companyChartView;
	}

	public void setCompanyChartView(PieChartView companyChartView) {
		this.companyChartView = companyChartView;
	}

	public int getTotalResultCount() {
		return totalResultCount;
	}

	public void setTotalResultCount(int totalResultCount) {
		this.totalResultCount = totalResultCount;
	}

	public PieChartView getStudentSelectedChartView() {
		return studentSelectedChartView;
	}

	public void setStudentSelectedChartView(PieChartView studentSelectedChartView) {
		this.studentSelectedChartView = studentSelectedChartView;
	}

}
