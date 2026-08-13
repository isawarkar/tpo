/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.hibernate;

import java.io.Serializable;
import java.util.Date;

import com.util.FbResourceUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class Personalinfo implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private Registration registration;

	private String course;

	private String branch;

	private String semester;

	private String specialization;

	private String companyName;

	private String remark;

	private String gender;

	private Date dob;

	private Date lastUpdated;

	private Integer yearOfPassing;

	private String resume;

	private String resumeType;
	
	private String postGraduationCourse;

	private String postGraduationBranch;

	private String currentCourse;

	private String diploma;

	private String diplomaBranch;

	private String diplomaUniversity;

	private String graduationUniversity;

	private Integer diplomaYearOfPassing;

	private String postGraduationUniversity;

	private String bloodGroup;

	private String handicapped;

	private Integer pgYearOfPassing;

	private String lastUpdatedBy;
	
	private String companyID;

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(
				"<br><br><font color=green size=4><b>"+FbResourceUtil.getLabel("This_is_your_Personal_Information")+"</b></font><br>");

		str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Current_Course")+"</font>= "
				+ currentCourse);

		if (diploma != null) {

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Diploma")+"</font>= "
					+ diploma);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Diploma_Branch")+"</font>= "
					+ diplomaBranch);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Diploma_University_Board")+"</font>= "
					+ diplomaUniversity);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Diploma_Year_Of_Passing")+"</font>= "
					+ diplomaYearOfPassing);
		}

		if (course != null) {
			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Graduation_Degree")+"</font>= "
					+ course);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Graduation_Degree_Branch")+"</font>= "
					+ branch);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Graduation_Degree_University")+"</font>= "
					+ graduationUniversity);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Graduation_Degree_Year_of_Passing")+"</font>= "
					+ yearOfPassing);
		}

		if (postGraduationCourse != null) {

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("PG_Degree")+"</font>= "
					+ postGraduationCourse);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("PG_Degree_Branch")+"Post Graduation Branch</font>= "
					+ postGraduationBranch);

			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("PG_Degree_University")+"</font>= "
					+ postGraduationUniversity);
			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("PG_Degree_Year_of_Passing")+"</font>= "
					+ pgYearOfPassing);
		}

		str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Semester")+"</font>= " + semester);

		str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Specialization")+"</font>= "
				+ specialization);

		str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Selected_IN")+"</font>= "
				+ companyName);

		str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Gender")+"</font>= " + gender);
		if (dob != null)
			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Date_of_Birth")+"</font>= "
					+ TpoUtil.getDateToString(dob));

		if (resume != null)
			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Resume")+"</font>= " + resume);
		if (remark != null)
			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Remarks")+"</font>= " + remark);
		if (lastUpdated != null)
			str.append("<br> <font color=gray size=3> "+FbResourceUtil.getLabel("Last_Updated_on")+"</font>= "
					+ lastUpdated);

		return str.toString();
	}

	// Property accessors

	public String getRollnumber() {
		return this.rollnumber;
	}

	public void setRollnumber(String rollnumber) {
		this.rollnumber = rollnumber;
	}

	public Registration getRegistration() {
		return this.registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public String getCourse() {
		return this.course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getBranch() {
		return this.branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSemester() {
		return this.semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getSpecialization() {
		return this.specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return this.dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Integer getYearOfPassing() {
		return this.yearOfPassing;
	}

	public void setYearOfPassing(Integer yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
	}

	/**
	 * @return the resume
	 */
	public synchronized String getResume() {
		return resume;
	}

	/**
	 * @param resume
	 *            the resume to set
	 */
	public synchronized void setResume(String resume) {
		this.resume = resume;
	}

	/**
	 * @return the postGraduationCourse
	 */
	public String getPostGraduationCourse() {
		return postGraduationCourse;
	}

	/**
	 * @param postGraduationCourse
	 *            the postGraduationCourse to set
	 */
	public void setPostGraduationCourse(String postGraduationCourse) {
		this.postGraduationCourse = postGraduationCourse;
	}

	/**
	 * @return the postGraduationBranch
	 */
	public String getPostGraduationBranch() {
		return postGraduationBranch;
	}

	/**
	 * @param postGraduationBranch
	 *            the postGraduationBranch to set
	 */
	public void setPostGraduationBranch(String postGraduationBranch) {
		this.postGraduationBranch = postGraduationBranch;
	}

	/**
	 * @return the currentCourse
	 */
	public String getCurrentCourse() {
		return currentCourse;
	}

	/**
	 * @param currentCourse
	 *            the currentCourse to set
	 */
	public void setCurrentCourse(String currentCourse) {
		this.currentCourse = currentCourse;
	}

	/**
	 * @return the diploma
	 */
	public String getDiploma() {
		return diploma;
	}

	/**
	 * @param diploma
	 *            the diploma to set
	 */
	public void setDiploma(String diploma) {
		this.diploma = diploma;
	}

	/**
	 * @return the diplomaBranch
	 */
	public String getDiplomaBranch() {
		return diplomaBranch;
	}

	/**
	 * @param diplomaBranch
	 *            the diplomaBranch to set
	 */
	public void setDiplomaBranch(String diplomaBranch) {
		this.diplomaBranch = diplomaBranch;
	}

	/**
	 * @return the diplomaUniversity
	 */
	public String getDiplomaUniversity() {
		return diplomaUniversity;
	}

	/**
	 * @param diplomaUniversity
	 *            the diplomaUniversity to set
	 */
	public void setDiplomaUniversity(String diplomaUniversity) {
		this.diplomaUniversity = diplomaUniversity;
	}

	/**
	 * @return the graduationUniversity
	 */
	public String getGraduationUniversity() {
		return graduationUniversity;
	}

	/**
	 * @param graduationUniversity
	 *            the graduationUniversity to set
	 */
	public void setGraduationUniversity(String graduationUniversity) {
		this.graduationUniversity = graduationUniversity;
	}

	/**
	 * @return the postGraduationUniversity
	 */
	public String getPostGraduationUniversity() {
		return postGraduationUniversity;
	}

	/**
	 * @param postGraduationUniversity
	 *            the postGraduationUniversity to set
	 */
	public void setPostGraduationUniversity(String postGraduationUniversity) {
		this.postGraduationUniversity = postGraduationUniversity;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getHandicapped() {
		return handicapped;
	}

	public void setHandicapped(String handicapped) {
		this.handicapped = handicapped;
	}

	public Integer getDiplomaYearOfPassing() {
		return diplomaYearOfPassing;
	}

	public void setDiplomaYearOfPassing(Integer diplomaYearOfPassing) {
		this.diplomaYearOfPassing = diplomaYearOfPassing;
	}

	public Integer getPgYearOfPassing() {
		return pgYearOfPassing;
	}

	public void setPgYearOfPassing(Integer pgYearOfPassing) {
		this.pgYearOfPassing = pgYearOfPassing;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((rollnumber == null) ? 0 : rollnumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Personalinfo other = (Personalinfo) obj;
		if (rollnumber == null) {
			if (other.rollnumber != null)
				return false;
		} else if (!rollnumber.equals(other.rollnumber))
			return false;
		return true;
	}
 
	public String getResumeType() {
		if(resume != null){
				resumeType = resume.substring(resume.lastIndexOf(".")+1, resume.length());
		}
		return resumeType;
	}

	public void setResumeType(String resumeType) {
		this.resumeType = resumeType;
	}

	public String getCompanyID() {
		return companyID;
	}

	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	

}