/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.hibernate;

import java.io.Serializable;
import java.util.Date;

import tpo.util.FbResourceUtil;

/**
 * @author Uddanda Technologies
 */
public class Percentageinfo implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rollnumber;

	private Registration registration;

	private Double highSchoolPercent;

	private Integer highSchoolPassing;

	private String highSchoolBoard;

	private Double higherSecondarypercent;

	private Integer higherSecondaryPassing;

	private String higherSecondaryBoard = "";

	private Double be1sem;

	private Double be2sem;

	private Double be3sem;

	private Double be4sem;

	private Double be5sem;

	private Double be6sem;

	private Double be7sem;

	private Double be8sem;

	private Double meBsc1sem;

	private Double meBsc2sem;

	private Double meBsc3sem;

	private Double meBsc4sem;

	private Double diplomaOthers;

	private String diplomaUniversity;

	private Double avgbe1year;

	private Double avgbe2year;

	private Double avgbe3year;

	private Double avgbe4year;

	private Double beAverege;

	private Double meAverage;

	private Double diploma1sem;

	private Double diploma2sem;

	private Double diploma3sem;

	private Double diploma4sem;

	private Double diploma5sem;

	private Double diploma6sem;

	private Date lastUpdated;

	private String lastUpdatedBy;

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(
				"<br><br><font color=green size=4><b>"+FbResourceUtil.getLabel("This_is_your_Academic_Information")+"</b></font><br>");

		str.append("<br> <font color=red size=3> "+FbResourceUtil.getLabel("High_School")+"</font>= ")
				.append(highSchoolPercent == null ? FbResourceUtil.getLabel("NA") : highSchoolPercent);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("High_School_Year_Of_Passing")+"</font>= ")
				.append(highSchoolPassing == null ? FbResourceUtil.getLabel("NA") : highSchoolPassing);

		str.append("<br> <font color=red size=3> "+FbResourceUtil.getLabel("High_School_Board")+"</font>= ")
				.append(highSchoolBoard == null ? FbResourceUtil.getLabel("NA") : highSchoolBoard);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Higher_Secondary")+"</font>= ")
				.append(higherSecondarypercent == null ? FbResourceUtil.getLabel("NA")
						: higherSecondarypercent);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Higher_Secondary_Year_Of_Passing")+"</font>= ")
				.append(higherSecondaryPassing == null ? FbResourceUtil.getLabel("NA")
						: higherSecondaryPassing);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Higher_Secondary_Board")+"</font>= ")
				.append(higherSecondaryBoard == null ? FbResourceUtil.getLabel("NA")
						: higherSecondaryBoard);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_1_Semester")+"</font>= ")
				.append(be1sem == null ? FbResourceUtil.getLabel("NA") : be1sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_2_Semester")+"</font>= ")
				.append(be2sem == null ? FbResourceUtil.getLabel("NA") : be2sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_3_Semester")+"</font>= ")
				.append(be3sem == null ? FbResourceUtil.getLabel("NA") : be3sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_4_Semester")+"</font>= ")
				.append(be4sem == null ? FbResourceUtil.getLabel("NA") : be4sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_5_Semester")+"</font>= ")
				.append(be5sem == null ? FbResourceUtil.getLabel("NA") : be5sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_6_Semester")+"</font>= ")
				.append(be6sem == null ? FbResourceUtil.getLabel("NA") : be6sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_7_Semester")+"</font>= ")
				.append(be7sem == null ? FbResourceUtil.getLabel("NA") : be7sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_8_Semester")+"</font>= ")
				.append(be8sem == null ? FbResourceUtil.getLabel("NA") : be8sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_First_Year")+" </font>= ")
				.append(avgbe1year == null ? FbResourceUtil.getLabel("NA") : avgbe1year);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_Second_Year")+"  </font>= ")
				.append(avgbe2year == null ? FbResourceUtil.getLabel("NA") : avgbe2year);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_Third_Year")+"  </font>= ")
				.append(avgbe3year == null ? FbResourceUtil.getLabel("NA") : avgbe3year);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_Fourth_Year")+"  </font>= ")
				.append(avgbe4year == null ? FbResourceUtil.getLabel("NA") : avgbe4year);

		str.append("<br> <font color=red size=3> "+FbResourceUtil.getLabel("Graduation_percent")+" </font>= ")
				.append(beAverege == null ? FbResourceUtil.getLabel("NA") : beAverege);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("PG_1_Semester")+"</font>= ")
				.append(meBsc1sem == null ? FbResourceUtil.getLabel("NA") : meBsc1sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("PG_2_Semester")+" </font>= ")
				.append(meBsc2sem == null ? FbResourceUtil.getLabel("NA") : meBsc2sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("PG_3_Semester")+" </font>= ")
				.append(meBsc3sem == null ? FbResourceUtil.getLabel("NA") : meBsc3sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("PG_4_Semester")+"</font>= ")
				.append(meBsc4sem == null ? FbResourceUtil.getLabel("NA") : meBsc4sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Post_Graduation_percent")+" </font>= ")
				.append(meAverage == null ? FbResourceUtil.getLabel("NA") : meAverage);

		str.append(
				"<br> <font color=red size=3>"+FbResourceUtil.getLabel("Diploma_percent")+" </font>= ")
				.append(diplomaOthers == null ? FbResourceUtil.getLabel("NA") : diplomaOthers);

		str.append(
				"<br> <font color=red size=3>"+FbResourceUtil.getLabel("Diploma_University_Board")+"</font>= ")
				.append(diplomaUniversity == null ? FbResourceUtil.getLabel("NA") : diplomaUniversity);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Diploma_1_Semester")+" </font>= ")
				.append(diploma1sem == null ? FbResourceUtil.getLabel("NA") : diploma1sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Diploma_2_Semester")+"</font>= ")
				.append(diploma2sem == null ? FbResourceUtil.getLabel("NA") : diploma2sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Diploma_3_Semester")+" </font>= ")
				.append(diploma3sem == null ? FbResourceUtil.getLabel("NA") : diploma3sem);

		str.append(
				"<br> <font color=red size=3>"+FbResourceUtil.getLabel("Diploma_4_Semester")+"</font>= ")
				.append(diploma4sem == null ? FbResourceUtil.getLabel("NA") : diploma4sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Diploma_5_Semester")+"</font>= ")
				.append(diploma5sem == null ? FbResourceUtil.getLabel("NA") : diploma5sem);

		str.append(
				"<br> <font color=red size=3> "+FbResourceUtil.getLabel("Diploma_6_Semester")+" </font>= ")
				.append(diploma6sem == null ? FbResourceUtil.getLabel("NA") : diploma6sem);

		return str.toString();
	}

	// Constructors

	/** default constructor */
	public Percentageinfo() {
	}

	/** minimal constructor */
	public Percentageinfo(String rollnumber, Registration registration,
			Double highSchoolPercent, Integer highSchoolPassing,
			String highSchoolBoard, Double higherSecondarypercent,
			Integer higherSecondaryPassing, String higherSecondaryBoard) {
		this.rollnumber = rollnumber;
		this.registration = registration;
		this.highSchoolPercent = highSchoolPercent;
		this.highSchoolPassing = highSchoolPassing;
		this.highSchoolBoard = highSchoolBoard;
		this.higherSecondarypercent = higherSecondarypercent;
		this.higherSecondaryPassing = higherSecondaryPassing;
		this.higherSecondaryBoard = higherSecondaryBoard;
	}

	/** full constructor */
	public Percentageinfo(String rollnumber, Registration registration,
			Double highSchoolPercent, Integer highSchoolPassing,
			String highSchoolBoard, Double higherSecondarypercent,
			Integer higherSecondaryPassing, String higherSecondaryBoard,
			Double be1sem, Double be2sem, Double be3sem, Double be4sem,
			Double be5sem, Double be6sem, Double be7sem, Double be8sem,
			Double meBsc1sem, Double meBsc2sem, Double meBsc3sem,
			Double meBsc4sem, Double diplomaOthers, String diplomaUniversity,
			Double avgbe1year, Double avgbe2year, Double avgbe3year,
			Double avgbe4year, Double beAverege, Double meAverage,
			Double diploma1sem, Double diploma2sem, Double diploma3sem,
			Double diploma4sem, Double diploma5sem, Double diploma6sem) {
		this.rollnumber = rollnumber;
		this.registration = registration;
		this.highSchoolPercent = highSchoolPercent;
		this.highSchoolPassing = highSchoolPassing;
		this.highSchoolBoard = highSchoolBoard;
		this.higherSecondarypercent = higherSecondarypercent;
		this.higherSecondaryPassing = higherSecondaryPassing;
		this.higherSecondaryBoard = higherSecondaryBoard;
		this.be1sem = be1sem;
		this.be2sem = be2sem;
		this.be3sem = be3sem;
		this.be4sem = be4sem;
		this.be5sem = be5sem;
		this.be6sem = be6sem;
		this.be7sem = be7sem;
		this.be8sem = be8sem;
		this.meBsc1sem = meBsc1sem;
		this.meBsc2sem = meBsc2sem;
		this.meBsc3sem = meBsc3sem;
		this.meBsc4sem = meBsc4sem;
		this.diplomaOthers = diplomaOthers;
		this.diplomaUniversity = diplomaUniversity;
		this.avgbe1year = avgbe1year;
		this.avgbe2year = avgbe2year;
		this.avgbe3year = avgbe3year;
		this.avgbe4year = avgbe4year;
		this.beAverege = beAverege;
		this.meAverage = meAverage;
		this.diploma1sem = diploma1sem;
		this.diploma2sem = diploma2sem;
		this.diploma3sem = diploma3sem;
		this.diploma4sem = diploma4sem;
		this.diploma5sem = diploma5sem;
		this.diploma6sem = diploma6sem;
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

	public Double getHighSchoolPercent() {
		return this.highSchoolPercent;
	}

	public void setHighSchoolPercent(Double highSchoolPercent) {
		this.highSchoolPercent = highSchoolPercent;
	}

	public Integer getHighSchoolPassing() {
		return this.highSchoolPassing;
	}

	public void setHighSchoolPassing(Integer highSchoolPassing) {
		this.highSchoolPassing = highSchoolPassing;
	}

	public String getHighSchoolBoard() {
		return this.highSchoolBoard;
	}

	public void setHighSchoolBoard(String highSchoolBoard) {
		this.highSchoolBoard = highSchoolBoard;
	}

	public Double getHigherSecondarypercent() {
		return this.higherSecondarypercent;
	}

	public void setHigherSecondarypercent(Double higherSecondarypercent) {
		this.higherSecondarypercent = higherSecondarypercent;
	}

	public Integer getHigherSecondaryPassing() {
		return this.higherSecondaryPassing;
	}

	public void setHigherSecondaryPassing(Integer higherSecondaryPassing) {
		this.higherSecondaryPassing = higherSecondaryPassing;
	}

	public String getHigherSecondaryBoard() {
		return this.higherSecondaryBoard;
	}

	public void setHigherSecondaryBoard(String higherSecondaryBoard) {
		this.higherSecondaryBoard = higherSecondaryBoard;
	}

	public Double getBe1sem() {
		return this.be1sem;
	}

	public void setBe1sem(Double be1sem) {
		this.be1sem = be1sem;
	}

	public Double getBe2sem() {
		return this.be2sem;
	}

	public void setBe2sem(Double be2sem) {
		this.be2sem = be2sem;
	}

	public Double getBe3sem() {
		return this.be3sem;
	}

	public void setBe3sem(Double be3sem) {
		this.be3sem = be3sem;
	}

	public Double getBe4sem() {
		return this.be4sem;
	}

	public void setBe4sem(Double be4sem) {
		this.be4sem = be4sem;
	}

	public Double getBe5sem() {
		return this.be5sem;
	}

	public void setBe5sem(Double be5sem) {
		this.be5sem = be5sem;
	}

	public Double getBe6sem() {
		return this.be6sem;
	}

	public void setBe6sem(Double be6sem) {
		this.be6sem = be6sem;
	}

	public Double getBe7sem() {
		return this.be7sem;
	}

	public void setBe7sem(Double be7sem) {
		this.be7sem = be7sem;
	}

	public Double getBe8sem() {
		return this.be8sem;
	}

	public void setBe8sem(Double be8sem) {
		this.be8sem = be8sem;
	}

	public Double getMeBsc1sem() {
		return this.meBsc1sem;
	}

	public void setMeBsc1sem(Double meBsc1sem) {
		this.meBsc1sem = meBsc1sem;
	}

	public Double getMeBsc2sem() {
		return this.meBsc2sem;
	}

	public void setMeBsc2sem(Double meBsc2sem) {
		this.meBsc2sem = meBsc2sem;
	}

	public Double getMeBsc3sem() {
		return this.meBsc3sem;
	}

	public void setMeBsc3sem(Double meBsc3sem) {
		this.meBsc3sem = meBsc3sem;
	}

	public Double getMeBsc4sem() {
		return this.meBsc4sem;
	}

	public void setMeBsc4sem(Double meBsc4sem) {
		this.meBsc4sem = meBsc4sem;
	}

	public Double getDiplomaOthers() {
		return this.diplomaOthers;
	}

	public void setDiplomaOthers(Double diplomaOthers) {
		this.diplomaOthers = diplomaOthers;
	}

	public String getDiplomaUniversity() {
		return this.diplomaUniversity;
	}

	public void setDiplomaUniversity(String diplomaUniversity) {
		this.diplomaUniversity = diplomaUniversity;
	}

	public Double getAvgbe1year() {
		return this.avgbe1year;
	}

	public void setAvgbe1year(Double avgbe1year) {
		this.avgbe1year = avgbe1year;
	}

	public Double getAvgbe2year() {
		return this.avgbe2year;
	}

	public void setAvgbe2year(Double avgbe2year) {
		this.avgbe2year = avgbe2year;
	}

	public Double getAvgbe3year() {
		return this.avgbe3year;
	}

	public void setAvgbe3year(Double avgbe3year) {
		this.avgbe3year = avgbe3year;
	}

	public Double getAvgbe4year() {
		return this.avgbe4year;
	}

	public void setAvgbe4year(Double avgbe4year) {
		this.avgbe4year = avgbe4year;
	}

	public Double getBeAverege() {
		return this.beAverege;
	}

	public void setBeAverege(Double beAverege) {
		this.beAverege = beAverege;
	}

	public Double getMeAverage() {
		return this.meAverage;
	}

	public void setMeAverage(Double meAverage) {
		this.meAverage = meAverage;
	}

	public Double getDiploma1sem() {
		return this.diploma1sem;
	}

	public void setDiploma1sem(Double diploma1sem) {
		this.diploma1sem = diploma1sem;
	}

	public Double getDiploma2sem() {
		return this.diploma2sem;
	}

	public void setDiploma2sem(Double diploma2sem) {
		this.diploma2sem = diploma2sem;
	}

	public Double getDiploma3sem() {
		return this.diploma3sem;
	}

	public void setDiploma3sem(Double diploma3sem) {
		this.diploma3sem = diploma3sem;
	}

	public Double getDiploma4sem() {
		return this.diploma4sem;
	}

	public void setDiploma4sem(Double diploma4sem) {
		this.diploma4sem = diploma4sem;
	}

	public Double getDiploma5sem() {
		return this.diploma5sem;
	}

	public void setDiploma5sem(Double diploma5sem) {
		this.diploma5sem = diploma5sem;
	}

	public Double getDiploma6sem() {
		return this.diploma6sem;
	}

	public void setDiploma6sem(Double diploma6sem) {
		this.diploma6sem = diploma6sem;
	}

}