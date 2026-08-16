/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import tpo.admin.beans.AdminUser;
import tpo.beans.Parent;
import tpo.beans.UIBackingBean;
import tpo.hibernate.Achivements;
import tpo.hibernate.Backdetails;
import tpo.hibernate.Contactinfo;
import tpo.hibernate.Percentageinfo;
import tpo.hibernate.Personalinfo;
import tpo.hibernate.Registration;
import tpo.util.FbMessageUtil;
import tpo.util.FbResourceUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
@Component("excelSheetReader")
public class ExcelSheetReader extends Parent {

	private Logger logger = LoggerFactory.getLogger(ExcelSheetReader.class);

	/**
	 ** This method is used to read the data's from an excel file.
	 * 
	 * @param fileName - Name of the excel file.
	 * 
	 * 
	 */
	public List<Registration> readExcelFile(InputStream inputStream, String userName, String path, String fileName) {

		List<Registration> list = new ArrayList<Registration>();
		int rowNo = 2;
		int i = 0;
		Object valueObj = null;
		try {
			/**
			 * Create a new instance for FileInputStream class
			 */
			// FileInputStream fileInputStream = new FileInputStream(fileName);
			/**
			 * Create a new instance for POIFSFileSystem class
			 */

			POIFSFileSystem fsFileSystem = new POIFSFileSystem(inputStream);
			/*
			 * Create a new instance for HSSFWorkBook Class
			 */
			HSSFWorkbook workBook = new HSSFWorkbook(fsFileSystem);
			HSSFSheet hssfSheet = workBook.getSheetAt(0);
			/**
			 * Iterate the rows and cells of the spreadsheet to get all the datas.
			 */
			Iterator rowIterator = hssfSheet.rowIterator();
			Registration registration = null;
			Personalinfo personalinfo = null;
			Percentageinfo percentageinfo = null;
			Backdetails backdetails = null;
			Contactinfo contactinfo = null;
			Achivements achivements = null;
			// ignore first row
			rowIterator.next();
			while (rowIterator.hasNext()) {
				registration = new Registration();
				personalinfo = new Personalinfo();
				percentageinfo = new Percentageinfo();
				backdetails = new Backdetails();
				contactinfo = new Contactinfo();
				achivements = new Achivements();
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator<Cell> iterator = hssfRow.cellIterator();
				while (iterator.hasNext()) {
					HSSFCell hssfCell = (HSSFCell) iterator.next();
					i = hssfCell.getRowIndex();
					valueObj = getCellValue(hssfCell);
					if (i == 0) {
						String rollNumber = ((String) valueObj).toLowerCase();
						registration.setRollnumber(rollNumber);
						registration.setLastUpdated(Calendar.getInstance().getTime());
						String updatedBy = null;
						if (userName == null) {
							updatedBy = supperUser;
							if (AdminUser.getUser() != null) {
								updatedBy = AdminUser.getUser().getUserName();
							}
						} else {
							updatedBy = userName;
						}
						registration.setLastUpdatedBy(updatedBy);
						personalinfo.setRollnumber(rollNumber);
						personalinfo.setLastUpdated(Calendar.getInstance().getTime());
						personalinfo.setLastUpdatedBy(updatedBy);
						personalinfo.setRegistration(registration);

						percentageinfo.setRollnumber(rollNumber);
						percentageinfo.setLastUpdated(Calendar.getInstance().getTime());
						percentageinfo.setLastUpdatedBy(updatedBy);
						percentageinfo.setRegistration(registration);

						backdetails.setRollnumber(rollNumber);
						backdetails.setLastUpdated(Calendar.getInstance().getTime());
						backdetails.setLastUpdatedBy(updatedBy);
						backdetails.setRegistration(registration);

						contactinfo.setRollnumber(rollNumber);
						contactinfo.setLastUpdated(Calendar.getInstance().getTime());
						contactinfo.setLastUpdatedBy(updatedBy);
						contactinfo.setRegistration(registration);

						achivements.setRollnumber(rollNumber);
						achivements.setLastUpdated(Calendar.getInstance().getTime());
						achivements.setLastUpdatedBy(updatedBy);
						achivements.setRegistration(registration);
						continue;
					}
					if (i == 1) {
						if (valueObj == null) {
							valueObj = "";
						}
						registration.setFirstName((String) valueObj);
						continue;
					}
					if (i == 2) {
						if (valueObj == null) {
							valueObj = "";
						}
						registration.setLastName((String) valueObj);
						continue;
					}
					if (i == 3) {
						if (valueObj == null) {
							valueObj = "";
						}
						personalinfo.setCourse((String) valueObj);
						continue;
					}
					if (i == 4) {
						if (valueObj == null) {
							valueObj = "";
						}
						personalinfo.setBranch((String) valueObj);
						continue;
					}
					if (i == 5) {
						if (valueObj == null) {
							valueObj = "";
						}
						// cell.setCellValue("Specialization");
						personalinfo.setSpecialization((String) valueObj);
						continue;
					}
					if (i == 6) {
						// cell.setCellValue("Semester");
						if (valueObj != null) {
							personalinfo.setSemester((String) valueObj);
						} else {
							personalinfo.setSemester("NA");
						}
						continue;
					}
					if (i == 7) {
						personalinfo.setDob(new Date());
						// cell.setCellValue("Date Of Birth");
						if (valueObj != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							Date formatedDate = null;
							formatedDate = sdf.parse((String) valueObj);
							personalinfo.setDob(formatedDate);
						} else {
							personalinfo.setDob(new Date());
						}
						continue;
					}
					if (i == 8) {
						// cell.setCellValue("High School %");
						if (valueObj != null) {
							percentageinfo.setHighSchoolPercent((Double) valueObj);
						} else {
							percentageinfo.setHighSchoolPercent(0d);
						}
						continue;
					}
					if (i == 9) {
						if (valueObj != null && !valueObj.equals("null")) {
							percentageinfo.setHighSchoolPassing(Integer.parseInt((String) valueObj));
						} else {
							percentageinfo.setHighSchoolPassing(0);
						}
						continue;

					}
					if (i == 10) {
						// cell.setCellValue("High School Board");
						if (valueObj != null) {
							percentageinfo.setHighSchoolBoard((String) valueObj);
						} else {
							percentageinfo.setHighSchoolBoard("NA");
						}
						continue;
					}
					if (i == 11) {
						// cell.setCellValue("Higher Secondary %");
						/*
						 * if (valueObj instanceof String) { valueObj = 0d; }
						 */
						if (valueObj != null)
							percentageinfo.setHigherSecondarypercent((Double) valueObj);
						continue;
					}
					if (i == 12) {
						// cell.setCellValue("Higher Secondary Passing Year");
						if (valueObj != null && !valueObj.equals("null")) {
							percentageinfo.setHigherSecondaryPassing(Integer.parseInt((String) valueObj));
						} else {
							percentageinfo.setHigherSecondaryPassing(0);
						}
						continue;
					}
					if (i == 13) {
						// cell.setCellValue("Higher Secondary Board");
						percentageinfo.setHigherSecondaryBoard((String) valueObj);
						continue;
					}
					if (i == 14) {
						// cell.setCellValue("ME/Bsc I Sem");
						percentageinfo.setMeBsc1sem((Double) valueObj);
						continue;
					}
					if (i == 15) {
						// cell.setCellValue("ME/Bsc II Sem");
						percentageinfo.setMeBsc2sem((Double) valueObj);
						continue;
					}
					if (i == 16) {
						// cell.setCellValue("ME/Bsc III Sem");
						percentageinfo.setMeBsc3sem((Double) valueObj);
						continue;
					}
					if (i == 17) {
						// cell.setCellValue("ME/Bsc IV Sem");
						percentageinfo.setMeBsc4sem((Double) valueObj);
						continue;
					}
					if (i == 18) {
						// cell.setCellValue("Diploma Others");
						percentageinfo.setDiplomaOthers((Double) valueObj);
						continue;
					}
					if (i == 19) {
						// cell.setCellValue("University");
						personalinfo.setDiplomaUniversity((String) valueObj);
						continue;
					}
					if (i == 20) {
						// cell.setCellValue("BE I Sem");
						percentageinfo.setBe1sem((Double) valueObj);
						continue;
					}
					if (i == 21) {
						// cell.setCellValue("BE II Sem");
						percentageinfo.setBe2sem((Double) valueObj);
						continue;
					}
					if (i == 22) {
						// cell.setCellValue("BE III Sem");
						percentageinfo.setBe3sem((Double) valueObj);
						continue;
					}
					if (i == 23) {
						// cell.setCellValue("BE IV Sem");
						percentageinfo.setBe4sem((Double) valueObj);
						continue;
					}
					if (i == 24) {
						// cell.setCellValue("BE V Sem");
						percentageinfo.setBe5sem((Double) valueObj);
						continue;
					}
					if (i == 25) {
						// cell.setCellValue("BE VI Sem");
						percentageinfo.setBe6sem((Double) valueObj);
						continue;
					}
					if (i == 26) {
						// cell.setCellValue("BE VII Sem");
						percentageinfo.setBe7sem((Double) valueObj);
						continue;
					}
					if (i == 27) {
						// cell.setCellValue("BE VIII Sem");
						if (hssfCell.getNumericCellValue() != 0)
							percentageinfo.setBe8sem((Double) valueObj);
						continue;
					}
					if (i == 28) {
						// cell.setCellValue("Be First Year %");
						percentageinfo.setAvgbe1year((Double) valueObj);
						continue;
					}
					if (i == 29) {
						// cell.setCellValue("Be Second Year %");
						percentageinfo.setAvgbe2year((Double) valueObj);
						continue;
					}
					if (i == 30) {
						// cell.setCellValue("Be Third Year %");
						percentageinfo.setAvgbe3year((Double) valueObj);
						continue;
					}
					if (i == 31) {
						// cell.setCellValue("Be Fourth Year %");
						percentageinfo.setAvgbe4year((Double) valueObj);
						continue;
					}
					if (i == 32) {
						// cell.setCellValue("Be %");
						percentageinfo.setBeAverege((Double) valueObj);
						continue;
					}
					if (i == 33) {
						// cell.setCellValue("Backlog");
						Double doub = (Double) valueObj;
						if (doub != null) {
							backdetails.setBackLog(doub.intValue());
						} else {
							backdetails.setBackLog(0);
						}
						continue;
					}
					if (i == 34) {
						// cell.setCellValue("Back Details");
						backdetails.setBackDetails((String) valueObj);
						continue;
					}
					if (i == 35) {
						// cell.setCellValue("Number Of Backlogs");
						Double doub = (Double) valueObj;
						if (doub != null) {
							backdetails.setNumberOfBacklogs(doub.intValue());
						} else {
							backdetails.setNumberOfBacklogs(0);
						}

						continue;
					}
					if (i == 36) {
						// cell.setCellValue("BA Group");
						Double doub = (Double) valueObj;
						if (doub != null) {
							backdetails.setBaGroup(doub.intValue());
						} else {
							backdetails.setBaGroup(0);
						}
						continue;
					}
					if (i == 37) {
						// cell.setCellValue("Pass More Then One Attempt");
						Double doub = (Double) valueObj;
						if (doub != null) {
							backdetails.setPassMoreThenOneAttempt(doub.intValue());
						} else {
							backdetails.setPassMoreThenOneAttempt(0);
						}
						continue;
					}
					if (i == 38) {
						// cell.setCellValue("Email Address");
						if (valueObj == null) {
							registration.setEmail("NA");
						} else {
							registration.setEmail((String) valueObj);
						}
						continue;
					}
					if (i == 39) {
						// cell.setCellValue("Phone Number");
						contactinfo.setPhoneNumber((String) valueObj);
						continue;
					}
					if (i == 40) {
						// cell.setCellValue("Mobile Number");
						if (valueObj != null) {
							contactinfo.setMobileNumber((String) valueObj);
						} else {
							contactinfo.setMobileNumber("0");
						}
						continue;
					}
					if (i == 41) {
						if (valueObj == null) {
							contactinfo.setPresentAddress("NA");
						} else {
							// cell.setCellValue("Present Address");
							contactinfo.setPresentAddress((String) valueObj);
						}
						continue;
					}
					if (i == 42) {
						if (valueObj == null) {
							contactinfo.setPresentCity("NA");
						} else {
							// cell.setCellValue("Present City");
							contactinfo.setPresentCity((String) valueObj);
						}
						continue;
					}
					if (i == 43) {
						if (valueObj == null) {
							contactinfo.setPresentState("NA");
						} else {
							// cell.setCellValue("Present State");
							contactinfo.setPresentState((String) valueObj);
						}
						continue;
					}
					if (i == 44) {
						if (valueObj == null) {
							contactinfo.setPermanentAddress("NA");
						} else {
							// cell.setCellValue("Permanent Address");
							contactinfo.setPermanentAddress((String) valueObj);
						}
						continue;
					}
					if (i == 45) {
						if (valueObj == null) {
							contactinfo.setPermanentCity("NA");
						} else {
							// cell.setCellValue("Permanent City");
							contactinfo.setPermanentCity((String) valueObj);
						}
						continue;
					}
					if (i == 46) {
						if (valueObj == null) {
							contactinfo.setPermanentState("NA");
						} else {
							// cell.setCellValue("Permanent State");
							contactinfo.setPermanentState((String) valueObj);
						}
						continue;
					}
					if (i == 47) {
						// cell.setCellValue("Hieght");
						if (valueObj == null) {
							contactinfo.setHieght(0d);
						} else {
							contactinfo.setHieght((Double) valueObj);
						}
						continue;
					}
					if (i == 48) {
						// cell.setCellValue("Weight");
						Double doub = (Double) valueObj;
						if (doub != null) {
							contactinfo.setWeight(doub.intValue());
						} else {
							contactinfo.setWeight(0);
						}
						continue;
					}
					if (i == 49) {
						if (valueObj == null) {
							contactinfo.setGlassPowerRight("");
						} else {
							// cell.setCellValue("Glass Power Left");
							contactinfo.setGlassPowerRight((String) valueObj);
						}
						continue;
					}
					if (i == 50) {
						if (valueObj == null) {
							contactinfo.setGlassPowerLeft("");
						} else {
							// cell.setCellValue("Glass Power Right");
							contactinfo.setGlassPowerLeft((String) valueObj);
						}
						continue;
					}
					if (i == 51) {
						if (valueObj == null) {
							valueObj = "";
						}
						personalinfo.setYearOfPassing(Integer.parseInt((String) valueObj));
						continue;
					}
					if (i == 52) {
						// cell.setCellValue("Remark");
						personalinfo.setRemark((String) valueObj);
						continue;
					}
					if (i == 53) {
						// cell.setCellValue("Gender");
						if (valueObj == null) {
							valueObj = "O";
						}
						personalinfo.setGender((String) valueObj);
						continue;
					}
					if (i == 54) {
						if (valueObj == null) {
							valueObj = "";
						}
						registration.setCollegeName((String) valueObj);
						continue;
					}
					if (i == 55) {
						if (valueObj == null) {
							personalinfo.setCompanyName("");
						} else {
							personalinfo.setCompanyName((String) valueObj);
						}
					}
					if (i == 56) {
						if (valueObj == null) {
							valueObj = false;
						}
						backdetails.setBlackList((Boolean) valueObj);
					}
					if (i == 57) {
						personalinfo.setGraduationUniversity((String) valueObj);
					}
					if (i == 58) {
						personalinfo.setPostGraduationCourse((String) valueObj);
					}
					if (i == 59) {
						personalinfo.setPostGraduationBranch((String) valueObj);
					}
					if (i == 60) {
						personalinfo.setPostGraduationUniversity((String) valueObj);
					}
					if (i == 61) {
						percentageinfo.setMeAverage((Double) valueObj);
					}
					if (i == 62) {
						if (valueObj == null) {
							personalinfo.setDiploma("NA");
						} else {
							personalinfo.setDiploma((String) valueObj);
						}
					}
					if (i == 63) {
						personalinfo.setDiplomaBranch((String) valueObj);
					}
					if (i == 64) {
						percentageinfo.setDiploma1sem((Double) valueObj);
					}
					if (i == 65) {
						percentageinfo.setDiploma1sem((Double) valueObj);
					}
					if (i == 66) {
						percentageinfo.setDiploma1sem((Double) valueObj);
					}
					if (i == 67) {
						percentageinfo.setDiploma1sem((Double) valueObj);
					}
					if (i == 68) {
						percentageinfo.setDiploma1sem((Double) valueObj);
					}
					if (i == 69) {
						percentageinfo.setDiploma1sem((Double) valueObj);
					}
					if (i == 70) {
						if (valueObj == null) {
							personalinfo.setCurrentCourse("Graduation");
						}
						personalinfo.setCurrentCourse((String) valueObj);
					}
					if (i == 71) {
						if (valueObj != null) {
							registration.setApproved((Boolean) valueObj);
						} else {
							registration.setApproved(false);
						}
					}
					if (i == 72) {
						achivements.setAcedamic((String) valueObj);
					}
					if (i == 73) {
						achivements.setSports((String) valueObj);
					}
					if (i == 74) {
						achivements.setOthers((String) valueObj);
					}
					if (i == 75) {
						if (valueObj == null) {
							valueObj = "0";
						}
						backdetails.setEducationGap(Short.parseShort((String) valueObj));
					}

					if (i == 76) {
						if (valueObj == null) {
							registration.setPassword("A".concat(String.valueOf(getRandomNumber())).concat("X"));
						} else {
							registration.setPassword((String) valueObj);
						}
					}
					if (i == 77) {
						if (valueObj == null) {
							registration.setEmailVarified(false);
						} else {
							registration.setEmailVarified((Boolean) valueObj);
						}
					}
					if (i == 78) {
						personalinfo.setBloodGroup((String) valueObj);
					}
					if (i == 79) {
						personalinfo.setHandicapped((String) valueObj);
					}
					if (i == 80) {
						contactinfo.setNumberVerified((Boolean) valueObj);
					}

				}
				registration.setPersonalinfo(personalinfo);
				registration.setPercentageinfo(percentageinfo);
				registration.setBackdetails(backdetails);
				registration.setContactinfo(contactinfo);
				registration.setAchivements(achivements);
				list.add(registration);
				rowNo++;
			}
			// ExcelHandler.generate(list);
		} catch (Exception e) {
			list = null;
			Object params[] = new Object[5];
			params[0] = rowNo;
			params[1] = i + 1;
			params[2] = getColumnName(i);
			params[3] = valueObj != null ? valueObj.toString() : "Null";
			params[4] = e.getMessage();
			if (userName != null && path != null && fileName != null) {
				createErrorFile(path, params, fileName);
			} else {
				UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("errorToProcessFile", params));
			}
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return list;
	}

	private void createErrorFile(String path, Object[] params, String fileName) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		String errorPath = path + "error/";
		try {
			fw = new FileWriter(errorPath + "error_" + TpoUtil.getDateToStringYYYYMMdd(new Date()) + ".txt");
			bw = new BufferedWriter(fw);
			// bw.write("Error while executing file.");
			// bw.write(System.getProperty("line.separator"));
			String error = FbMessageUtil.getLabel("errorToProcessFile", params);
			// bw.write(error);
			for (String err : error.split(",")) {
				bw.write(err);
				bw.write(System.getProperty("line.separator"));
			}
			TpoUtil.moveTheFile(errorPath, new File(path + fileName), null);
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static Integer getRandomNumber() {
		Random ra = new Random();
		Integer rendonNumber = ra.nextInt(100000000);
		while (rendonNumber <= 10000000) {
			rendonNumber = ra.nextInt(100000000);
		}
		return Math.abs(rendonNumber);
	}

	private static Object getCellValue(HSSFCell hssfCell) {
		Object obj = null;
		switch (hssfCell.getCellType()) {
		case STRING:
			obj = hssfCell.getRichStringCellValue().getString();
			if (obj == null) {
				obj = "NA";
			}
			break;
		case NUMERIC:
			obj = hssfCell.getNumericCellValue();
			break;
		case BOOLEAN:
			obj = hssfCell.getBooleanCellValue();
			break;
		case BLANK:
			obj = null;
			break;
		default:
			break;
		}
		return obj;
	}

	private String getColumnName(int i) {
		String columnName = null;
		switch (i) {
		case 0: {
			columnName = FbResourceUtil.getLabel("Enrollment_No");
			break;
		}
		case 1: {
			columnName = FbResourceUtil.getLabel("First_Name");
			break;
		}
		case 2: {
			columnName = FbResourceUtil.getLabel("Last_Name");
			break;
		}
		case 3: {
			columnName = FbResourceUtil.getLabel("Graduation");
			break;
		}
		case 4: {
			columnName = FbResourceUtil.getLabel("Graduation_Degree_Branch");
			break;
		}
		case 5: {
			columnName = FbResourceUtil.getLabel("Specialization");
			break;
		}
		case 6: {
			columnName = FbResourceUtil.getLabel("Semester");
			break;
		}
		case 7: {
			columnName = FbResourceUtil.getLabel("Date_of_Birth");
			break;
		}
		case 8: {
			columnName = FbResourceUtil.getLabel("High_School");
			break;
		}
		case 9: {
			columnName = FbResourceUtil.getLabel("High_School_Year_Of_Passing");
			break;
		}
		case 10: {
			columnName = FbResourceUtil.getLabel("High_School_Board");
			break;
		}
		case 11: {
			columnName = FbResourceUtil.getLabel("Higher_Secondary");
			break;
		}
		case 12: {
			columnName = FbResourceUtil.getLabel("Higher_Secondary_Year_Of_Passing");
			break;
		}
		case 13: {
			columnName = FbResourceUtil.getLabel("Higher_Secondary_Board");
			break;
		}
		case 14: {
			columnName = FbResourceUtil.getLabel("PG_1_Semester");
			break;
		}
		case 15: {
			columnName = FbResourceUtil.getLabel("PG_2_Semester");
			break;
		}
		case 16: {
			columnName = FbResourceUtil.getLabel("PG_2_Semester");
			break;
		}
		case 17: {
			columnName = FbResourceUtil.getLabel("PG_4_Semester");
			break;
		}
		case 18: {
			columnName = FbResourceUtil.getLabel("Diploma_percent");
			break;
		}
		case 19: {
			columnName = FbResourceUtil.getLabel("Diploma_University_Board");
			break;
		}
		case 20: {
			columnName = FbResourceUtil.getLabel("Graduation_1_Semester");
			break;
		}
		case 21: {
			columnName = FbResourceUtil.getLabel("Graduation_2_Semester");
			break;
		}
		case 22: {
			columnName = FbResourceUtil.getLabel("Graduation_3_Semester");
			break;
		}
		case 23: {
			columnName = FbResourceUtil.getLabel("Graduation_4_Semester");
			break;
		}
		case 24: {
			columnName = FbResourceUtil.getLabel("Graduation_5_Semester");
			break;
		}
		case 25: {
			columnName = FbResourceUtil.getLabel("Graduation_6_Semester");
			break;
		}
		case 26: {
			columnName = FbResourceUtil.getLabel("Graduation_7_Semester");
			break;
		}
		case 27: {
			columnName = FbResourceUtil.getLabel("Graduation_8_Semester");
			break;
		}
		case 28: {
			columnName = FbResourceUtil.getLabel("Graduation_First_Year");
			break;
		}
		case 29: {
			columnName = FbResourceUtil.getLabel("Graduation_Second_Year");
			break;
		}
		case 30: {
			columnName = FbResourceUtil.getLabel("Graduation_Third_Year");
			break;
		}
		case 31: {
			columnName = FbResourceUtil.getLabel("Graduation_Fourth_Year");
			break;
		}
		case 32: {
			columnName = FbResourceUtil.getLabel("Graduation_percent");
			break;
		}
		case 33: {
			columnName = FbResourceUtil.getLabel("Present_Backlog");
			break;
		}
		case 34: {
			columnName = FbResourceUtil.getLabel("Details_of_Backlog");
			break;
		}
		case 35: {
			columnName = FbResourceUtil.getLabel("Number_of_Backlog");
			break;
		}
		case 36: {
			columnName = FbResourceUtil.getLabel("Please_select_baGroup");
			break;
		}
		case 37: {
			columnName = FbResourceUtil.getLabel("Any_semester_passed_in_more_than_one_attempt");
			break;
		}
		case 38: {
			columnName = FbResourceUtil.getLabel("E_mail");
			break;
		}
		case 39: {
			columnName = FbResourceUtil.getLabel("Phone_Number");
			break;
		}
		case 40: {
			columnName = FbResourceUtil.getLabel("Mobile");
			break;
		}
		case 41: {
			columnName = FbResourceUtil.getLabel("Present_Address");
			break;
		}
		case 42: {
			columnName = FbResourceUtil.getLabel("Present_City");
			break;
		}
		case 43: {
			columnName = FbResourceUtil.getLabel("Present_State");
			break;
		}
		case 44: {
			columnName = FbResourceUtil.getLabel("Permanent_Address");
			break;
		}
		case 45: {
			columnName = FbResourceUtil.getLabel("Permanent_City");
			break;
		}
		case 46: {
			columnName = FbResourceUtil.getLabel("Permanent_State");
			break;
		}
		case 47: {
			columnName = FbResourceUtil.getLabel("Height");
			break;
		}
		case 48: {
			columnName = FbResourceUtil.getLabel("Weight");
			break;
		}
		case 49: {
			columnName = FbResourceUtil.getLabel("Glass_Power_Left");
			break;
		}
		case 50: {
			columnName = FbResourceUtil.getLabel("Glass_Power_Right");
			break;
		}
		case 51: {
			columnName = FbResourceUtil.getLabel("Year_of_Passing");
			break;
		}
		case 52: {
			columnName = FbResourceUtil.getLabel("Remarks");
			break;
		}
		case 53: {
			columnName = FbResourceUtil.getLabel("Gender");
			break;
		}
		case 54: {
			columnName = FbResourceUtil.getLabel("College_Name");
			break;
		}
		case 55: {
			columnName = FbResourceUtil.getLabel("Company_Name");
			break;
		}
		case 56: {
			columnName = FbResourceUtil.getLabel("Student_is_Black_Listed");
			break;
		}
		case 57: {
			columnName = FbResourceUtil.getLabel("Graduation_Degree_University");
			break;
		}
		case 58: {
			columnName = FbResourceUtil.getLabel("PG_Degree");
			break;
		}
		case 59: {
			columnName = FbResourceUtil.getLabel("PG_Degree_Branch");
			break;
		}
		case 60: {
			columnName = FbResourceUtil.getLabel("PG_Degree_University");
			break;
		}
		case 61: {
			columnName = FbResourceUtil.getLabel("Post_Graduation_percent");
			break;
		}
		case 62: {
			columnName = FbResourceUtil.getLabel("Diploma");
			break;
		}
		case 63: {
			columnName = FbResourceUtil.getLabel("Diploma_Branch");
			break;
		}
		case 64: {
			columnName = FbResourceUtil.getLabel("Diploma_1_Semester");
			break;
		}
		case 65: {
			columnName = FbResourceUtil.getLabel("Diploma_2_Semester");
			break;
		}
		case 66: {
			columnName = FbResourceUtil.getLabel("Diploma_3_Semester");
			break;
		}
		case 67: {
			columnName = FbResourceUtil.getLabel("Diploma_4_Semester");
			break;
		}
		case 68: {
			columnName = FbResourceUtil.getLabel("Diploma_5_Semester");
			break;
		}
		case 69: {
			columnName = FbResourceUtil.getLabel("Diploma_6_Semester");
			break;
		}
		case 70: {
			columnName = FbResourceUtil.getLabel("Current_Course");
			break;
		}
		case 71: {
			columnName = FbResourceUtil.getLabel("Status");
			break;
		}
		case 72: {
			columnName = FbResourceUtil.getLabel("Academic");
			break;
		}
		case 73: {
			columnName = FbResourceUtil.getLabel("Sports");
			break;
		}
		case 74: {
			columnName = FbResourceUtil.getLabel("Others");
			break;
		}
		case 75: {
			columnName = FbResourceUtil.getLabel("Education_Gap_in_Years");
			break;
		}
		case 76: {
			columnName = FbResourceUtil.getLabel("Password");
			break;
		}
		case 77: {
			columnName = FbResourceUtil.getLabel("Verified_EMail");
			break;
		}
		case 78: {
			columnName = FbResourceUtil.getLabel("Blood_Group");
			break;
		}
		case 79: {
			columnName = FbResourceUtil.getLabel("Handicapped_Details");
			break;
		}
		case 80: {
			columnName = FbResourceUtil.getLabel("Verified_Mobile_Number");
			break;
		}
		}
		return columnName;
	}
}
