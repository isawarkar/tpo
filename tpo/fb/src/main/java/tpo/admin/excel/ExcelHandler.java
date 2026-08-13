/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.excel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpo.hibernate.College;
import tpo.hibernate.Company;
import tpo.hibernate.EmployeeEfforts;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Module;
import tpo.hibernate.Notice;
import tpo.hibernate.Project;
import tpo.hibernate.Questions;
import tpo.hibernate.Registration;
import tpo.hibernate.Result;
import tpo.hibernate.annotation.ReferralHistory;
import tpo.hibernate.annotation.StudentFeeDetails;
import tpo.util.FbResourceUtil;
import tpo.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class ExcelHandler {

	private static Logger logger = LoggerFactory.getLogger(ExcelHandler.class);

	private static final int ROWSIZE = 81;
	private static final int ROWSIZE_EXAM = 12;
	private static final int ROWSIZE_RESULT = 9;
	private static final int ROWSIZE_COMPANY = 16;
	private static final int ROWSIZE_OPENNING = 6;
	private static final int ROWSIZE_COLLEGE = 8;
	private static final int ROWSIZE_HALLTICKET = 6;
	private static final int ROWSIZE_USER = 8;
	private static final int ROWSIZE_NOTICLIST = 3;
	private static final int ROWSIZE_EFFORTS = 9;
	private static final int ROWSIZE_PROJECTS = 4;
	private static final int ROWSIZE_MODULES = 6;
	private static final int ROWSIZE_FEE = 6;
	private static final int ROWSIZE_REFERRAL = 3;

	public static byte[] generateExam(List<Questions> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook.createSheet(reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_EXAM; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Exam_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Question_No")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Question")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Option_A")));
					continue;
				}
				if (i == 4) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Option_B")));
					continue;
				}
				if (i == 5) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Option_C")));
					continue;
				}
				if (i == 6) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Option_D")));
					continue;
				}
				if (i == 7) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Answer")));
					continue;
				}
				if (i == 8) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Question_Type")));
					continue;
				}
				if (i == 9) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Assigned_No")));
					continue;
				}
				if (i == 10) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("IsImage")));
					continue;
				}
				if (i == 11) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Image")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.AQUA.getIndex());
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(true);
			HSSFRow chieldRow;
			for (Questions Questions : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_EXAM; j++) {
					cellStyle.setFont(rowsFontStyle);
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						cell.setCellValue(new HSSFRichTextString(Questions.getId().getQtype()));
						continue;
					}
					if (j == 1) {
						cell.setCellValue(Questions.getId().getQno());
						continue;
					}
					if (j == 2) {
						cell.setCellValue(new HSSFRichTextString(Questions.getQuestion()));
						continue;
					}
					if (j == 3) {
						cell.setCellValue(new HSSFRichTextString(Questions.getOptiona()));
						continue;
					}
					if (j == 4) {
						cell.setCellValue(new HSSFRichTextString(Questions.getOptionb()));
						continue;
					}
					if (j == 5) {
						cell.setCellValue(new HSSFRichTextString(Questions.getOptionc()));
						continue;
					}
					if (j == 6) {
						cell.setCellValue(new HSSFRichTextString(Questions.getOptiond()));
						continue;
					}
					if (j == 7) {
						cell.setCellValue(new HSSFRichTextString(Questions.getAnswer()));
						continue;
					}
					if (j == 8) {
						cell.setCellValue(new HSSFRichTextString(Questions.getQuestionType()));
						continue;
					}
					if (j == 9) {
						cell.setCellValue(new HSSFRichTextString(String.valueOf(Questions.getAssignedNo())));
						continue;
					}
					if (j == 10) {
						cell.setCellValue(new HSSFRichTextString(
								String.valueOf(Questions.getIsImage() == null ? "false" : Questions.getIsImage())));
						continue;
					}
				}
			}

			workBook.write(byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			byteArrayOutputStream = null;
		}
		return null;
	}

	public static byte[] generateNoticListXls(List<Notice> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_NOTICLIST; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Notice_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Status")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Expiry")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Notice notice : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_NOTICLIST; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, notice.getNoticeName());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, notice.getActive() == true ? FbResourceUtil.getLabel("Active")
								: FbResourceUtil.getLabel("InActive"));
						continue;
					}
					if (j == 2) {
						setCellValue(cell, notice.getExpiryDate());
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateUserXls(List<Logindetails> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_USER; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("User_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Role")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Status")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Last_Login")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Valid_Till")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Address")));
					continue;
				}
				if (i == 6) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Number")));
					continue;
				}
				if (i == 7) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("E_mail")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Logindetails login : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_USER; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, login.getUserName());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, login.getRole());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, login.getActive() == true ? FbResourceUtil.getLabel("Active")
								: FbResourceUtil.getLabel("InActive"));
						continue;
					}
					if (j == 3) {

						setCellValue(cell, login.getLastLogin());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, TpoUtil.getDateToStringInddmmyyyy(login.getValidTill()));
						continue;
					}
					if (j == 5) {
						setCellValue(cell, login.getUserdetails().getAddress());
						continue;
					}
					if (j == 6) {
						setCellValue(cell, login.getUserdetails().getMobleNo());
						continue;
					}
					if (j == 7) {
						setCellValue(cell, login.getUserdetails().getEmail());
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateCollegeXls(List<College> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_COLLEGE; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("College_Code")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("College_Name")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Place")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("University")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Date_Of_Opening")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("E_mail")));
					continue;
				}
				if (i == 6) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Website")));
					continue;
				}
				if (i == 7) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Address")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (College college : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_COLLEGE; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, college.getCollegeName());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, college.getCollegeFullName());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, college.getPlace());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, college.getUniversity());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, TpoUtil.getDateToStringInddmmyyyy(college.getDateOfOpening()));
						continue;
					}
					if (j == 5) {
						setCellValue(cell, college.getEmailAddress());
						continue;
					}
					if (j == 6) {
						setCellValue(cell, college.getSiteAddress());
						continue;
					}
					if (j == 7) {
						setCellValue(cell, college.getAddress());
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateHallticketXls(List<HallTicketConnect> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_HALLTICKET; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Enrollment_No")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("HallTicket_ID")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Company_Name")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Applied")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Approved")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Company_Status")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (HallTicketConnect hallticket : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_HALLTICKET; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, hallticket.getId().getRollnumber());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, hallticket.getId().getHallTicket().getHallTicketId());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, hallticket.getId().getHallTicket().getCompanyName());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, hallticket.getIsApplied() == true ? FbResourceUtil.getLabel("YES")
								: FbResourceUtil.getLabel("NO"));
						continue;
					}
					if (j == 4) {

						setCellValue(cell, hallticket.getIsApproved() == true ? FbResourceUtil.getLabel("YES")
								: FbResourceUtil.getLabel("NO"));
						continue;
					}
					if (j == 5) {
						setCellValue(cell,
								hallticket.getId().getHallTicket().getIsActive() == true
										? FbResourceUtil.getLabel("Active")
										: FbResourceUtil.getLabel("InActive"));
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateOpeningXls(List<HallTicket> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_OPENNING; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Company_ID")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Company_Name")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Date_Of_Visit")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Time")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Package_Offering")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Status")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (HallTicket openning : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_OPENNING; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, openning.getHallTicketId());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, openning.getCompanyName());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, TpoUtil.getDateToStringInddmmyyyy(openning.getDate()));
						continue;
					}
					if (j == 3) {

						setCellValue(cell, openning.getTime());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, openning.getPackageOffering());
						continue;
					}
					if (j == 5) {
						setCellValue(cell, openning.getIsActive() == true ? FbResourceUtil.getLabel("Active")
								: FbResourceUtil.getLabel("InActive"));
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateReferralXls(List<ReferralHistory> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_REFERRAL; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("referred")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("referredBY")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Date")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (ReferralHistory detail : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_REFERRAL; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, detail.getReferred());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, detail.getReferredBY());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, detail.getDate());
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateFeeDetailsXls(List<StudentFeeDetails> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_FEE; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Enrollment_No")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Amount_Paid")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Amount_Paid_On")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Amount_Due")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Amount_Due_On")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Reminder_On")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (StudentFeeDetails detail : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_FEE; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, detail.getRollNumber());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, detail.getAmountPaid());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, detail.getPaidOn());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, detail.getAmountDue());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, detail.getDueOn());
						continue;
					}
					if (j == 5) {
						setCellValue(cell,
								detail.getReminderOn() != null && detail.getReminderOn()
										? FbResourceUtil.getLabel("YES")
										: FbResourceUtil.getLabel("NO"));
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateCompanyXls(List<Company> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_COMPANY; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Company_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Date_Of_Visit")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("E_mail")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Package_Offering")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("CompanyID")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Profile")));
					continue;
				}
				if (i == 6) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Domain")));
					continue;
				}
				if (i == 7) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Website")));
					continue;
				}
				if (i == 8) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("LinkedIN")));
					continue;
				}
				if (i == 9) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Twiter")));
					continue;
				}
				if (i == 10) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("GlassDoor")));
					continue;
				}
				if (i == 11) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Facebook")));
					continue;
				}

				if (i == 12) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Total")));
					continue;
				}
				if (i == 13) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Remarks")));
					continue;
				}
				if (i == 14) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("created_By")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Company company : list) {
				if (company.getCompanyID() == 1) {
					continue;
				}
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_COMPANY; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, company.getCompanyname());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, company.getDateofvisit());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, company.getEmail());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, company.getPackageOffering());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, company.getCompanyID());
						continue;
					}
					if (j == 5) {
						setCellValue(cell, company.getProfile());
						continue;
					}
					if (j == 6) {

						setCellValue(cell, company.getDomain());
						continue;
					}
					if (j == 7) {

						setCellValue(cell, company.getWebsite());
						continue;
					}
					if (j == 8) {

						setCellValue(cell, company.getLinkedIn());
						continue;
					}
					if (j == 9) {
						setCellValue(cell, company.getTwiter());
						continue;
					}
					if (j == 10) {

						setCellValue(cell, company.getGlassdoor());
						continue;
					}
					if (j == 11) {

						setCellValue(cell, company.getFacebook());
						continue;
					}
					if (j == 12) {

						setCellValue(cell, company.getTotal());
						continue;
					}
					if (j == 13) {

						setCellValue(cell, company.getRemarks());
						continue;
					}
					if (j == 14) {
						setCellValue(cell, company.getCreatedBy());
						continue;
					}
				}
			}

			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateProjectXls(List<Project> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_PROJECTS; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("User_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Project_ID")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("project_name")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Status")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Project project : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_PROJECTS; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, project.getCreatedBy());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, project.getProjectid());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, project.getProjectname());
						continue;
					}
					if (j == 3) {
						setCellValue(cell, project.getStatus() == 1 ? FbResourceUtil.getLabel("Active")
								: FbResourceUtil.getLabel("InActive"));
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateModuleXls(List<Module> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_MODULES; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("User_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Project_ID")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("project_name")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Module_id")));
					continue;
				}
				if (i == 4) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Module_Name")));
					continue;
				}
				if (i == 5) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Status")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Module module : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_MODULES; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, module.getCreatedBy());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, module.getProject().getProjectid());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, module.getProject().getProjectname());
						continue;
					}
					if (j == 3) {
						setCellValue(cell, module.getModuleid());
						continue;
					}
					if (j == 4) {
						setCellValue(cell, module.getModulename());
						continue;
					}
					if (j == 5) {
						setCellValue(cell, module.getStatus() == 1 ? FbResourceUtil.getLabel("Active")
								: FbResourceUtil.getLabel("InActive"));
						continue;
					}
				}
			}
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateEmployeeEffortsXls(List<EmployeeEfforts> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_EFFORTS; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("User_Name")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("effortid")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Effort_Date")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Project_ID")));
					continue;
				}
				if (i == 4) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Module_id")));
					continue;
				}
				if (i == 5) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Description")));
					continue;
				}
				if (i == 6) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Time")));
					continue;
				}
				if (i == 7) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Remarks")));
					continue;
				}
				if (i == 8) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Leave_Day")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (EmployeeEfforts effort : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_EFFORTS; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, effort.getLogindetails().getUserName());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, effort.getEffortid());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, effort.getSdate());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, effort.getProject());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, effort.getModule());
						continue;
					}
					if (j == 5) {
						setCellValue(cell, effort.getDescription());
						continue;
					}
					if (j == 6) {

						setCellValue(cell, effort.getTime());
						continue;
					}
					if (j == 7) {

						setCellValue(cell, effort.getRemarks());
						continue;
					}
					if (j == 8) {
						if (effort.getLeaveday() != null && !effort.getLeaveday().isEmpty())
							setCellValue(cell, effort.getLeaveday() == "F" ? FbResourceUtil.getLabel("FullDay")
									: FbResourceUtil.getLabel("HalfDay"));
						continue;
					}
				}
			}

			workBook.write(byteArrayOutputStream);

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateResultList(List<Result> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

			for (int i = 0; i < ROWSIZE_RESULT; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				cell.setCellStyle(cellStyle);
				if (i == 0) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Enrollment_No")));
					continue;
				}
				if (i == 1) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Attempt")));
					continue;
				}
				if (i == 2) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Test_Name")));
					continue;
				}
				if (i == 3) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Percentage_No")));
					continue;
				}
				if (i == 4) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Result")));
					continue;
				}
				if (i == 5) {
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Date_of_Exam")));
					continue;
				}
				if (i == 6) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Time_Was")));
					continue;
				}
				if (i == 7) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Time_Taken")));
					continue;
				}
				if (i == 8) {
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("No_of_Questions")));
					continue;
				}
			}
			int i = 0;
			cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Result result : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE_RESULT; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {
						setCellValue(cell, result.getId().getLoginname());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, result.getId().getAttempt());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, result.getTestName());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, result.getTotalnumbers());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, result.getResult());
						continue;
					}
					if (j == 5) {
						setCellValue(cell, result.getDateTaken());
						continue;
					}
					if (j == 6) {

						setCellValue(cell, result.getTotalTime());
						continue;
					}
					if (j == 7) {

						setCellValue(cell, result.getTotalTimeTaken());
						continue;
					}
					if (j == 8) {

						setCellValue(cell, result.getNumberOfQuestion());
						continue;
					}
				}
			}

			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] generateStudentList(List<Registration> list, String reportName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			HSSFWorkbook workBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = workBook
					.createSheet(reportName.length() > 30 ? reportName.substring(0, 29) : reportName);
			HSSFFont columnFontStyle = workBook.createFont();
			columnFontStyle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			columnFontStyle.setItalic(true);

			HSSFFont rowsFontStyle = workBook.createFont();
			rowsFontStyle.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
			// rowsFontStyle.setBold(true);

			HSSFRow row = spreadSheet.createRow(0);

			HSSFCell cell;

			for (int i = 0; i < ROWSIZE; i++) {
				spreadSheet.setDefaultColumnWidth((short) 15);

				cell = row.createCell((short) i);
				// createCellStyle(workBook, columnFontStyle, cell,
				// HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
				if (i == 0) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Enrollment_No")));
					continue;
				}
				if (i == 1) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("First_Name")));
					continue;
				}
				if (i == 2) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Last_Name")));
					continue;
				}
				if (i == 3) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation")));
					continue;
				}
				if (i == 4) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_Degree_Branch")));
					continue;
				}
				if (i == 5) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					spreadSheet.setDefaultColumnWidth((short) 25);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Specialization")));
					continue;
				}
				if (i == 6) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Semester")));
					continue;
				}
				if (i == 7) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Date_of_Birth")));
					continue;
				}
				if (i == 8) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("High_School")));
					continue;
				}
				if (i == 9) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("High_School_Year_Of_Passing")));
					cell.setCellType(CellType.STRING);
					continue;
				}
				if (i == 10) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("High_School_Board")));
					continue;
				}
				if (i == 11) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Higher_Secondary")));
					continue;
				}
				if (i == 12) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(
							new HSSFRichTextString(FbResourceUtil.getLabel("Higher_Secondary_Year_Of_Passing")));
					cell.setCellType(CellType.STRING);
					continue;
				}
				if (i == 13) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Higher_Secondary_Board")));
					continue;
				}
				if (i == 14) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_1_Semester")));
					continue;
				}
				if (i == 15) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_2_Semester")));
					continue;
				}
				if (i == 16) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_2_Semester")));
					continue;
				}
				if (i == 17) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_4_Semester")));
					continue;
				}
				if (i == 18) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_percent")));
					continue;
				}
				if (i == 19) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_University_Board")));
					continue;
				}
				if (i == 20) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_1_Semester")));
					continue;
				}
				if (i == 21) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_2_Semester")));
					continue;
				}
				if (i == 22) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_3_Semester")));
					continue;
				}
				if (i == 23) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_4_Semester")));
					continue;
				}
				if (i == 24) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_5_Semester")));
					continue;
				}
				if (i == 25) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_6_Semester")));
					continue;
				}
				if (i == 26) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_7_Semester")));
					continue;
				}
				if (i == 27) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_8_Semester")));
					continue;
				}
				if (i == 28) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_First_Year")));
					continue;
				}
				if (i == 29) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_Second_Year")));
					continue;
				}
				if (i == 30) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_Third_Year")));
					continue;
				}
				if (i == 31) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_Fourth_Year")));
					continue;
				}
				if (i == 32) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_percent")));
					continue;
				}
				if (i == 33) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Present_Backlog")));
					continue;
				}
				if (i == 34) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Details_of_Backlog")));
					continue;
				}
				if (i == 35) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Number_of_Backlog")));
					continue;
				}
				if (i == 36) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Please_select_baGroup")));
					continue;
				}
				if (i == 37) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(
							FbResourceUtil.getLabel("Any_semester_passed_in_more_than_one_attempt")));
					continue;
				}
				if (i == 38) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					spreadSheet.setDefaultColumnWidth((short) 30);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("E_mail")));
					continue;
				}
				if (i == 39) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Phone_Number")));
					continue;
				}
				if (i == 40) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Mobile")));
					continue;
				}
				if (i == 41) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					spreadSheet.setDefaultColumnWidth((short) 30);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Present_Address")));
					continue;
				}
				if (i == 42) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					spreadSheet.setDefaultColumnWidth((short) 30);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Present_City")));
					continue;
				}
				if (i == 43) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Present_State")));
					continue;
				}
				if (i == 44) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					spreadSheet.setDefaultColumnWidth((short) 30);
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Permanent_Address")));
					continue;
				}
				if (i == 45) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Permanent_City")));
					continue;
				}
				if (i == 46) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Permanent_State")));
					continue;
				}
				if (i == 47) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Height")));
					continue;
				}
				if (i == 48) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Weight")));
					continue;
				}
				if (i == 49) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Glass_Power_Left")));
					continue;
				}
				if (i == 50) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Glass_Power_Right")));
					continue;
				}
				if (i == 51) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Year_of_Passing")));
					cell.setCellType(CellType.STRING);
					continue;
				}
				if (i == 52) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Remarks")));
					continue;
				}
				if (i == 53) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Gender")));
					continue;
				}
				if (i == 54) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("College_Name")));
					continue;
				}
				if (i == 55) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Company_Name")));
				}
				if (i == 56) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Student_is_Black_Listed")));
				}
				if (i == 57) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Graduation_Degree_University")));
				}
				if (i == 58) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_Degree")));
				}
				if (i == 59) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_Degree_Branch")));
				}
				if (i == 60) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("PG_Degree_University")));
				}
				if (i == 61) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Post_Graduation_percent")));
				}
				if (i == 62) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma")));
				}
				if (i == 63) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_Branch")));
				}
				if (i == 64) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_1_Semester")));
				}
				if (i == 65) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_2_Semester")));
				}
				if (i == 66) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_3_Semester")));
				}
				if (i == 67) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_4_Semester")));
				}
				if (i == 68) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_5_Semester")));
				}
				if (i == 69) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Diploma_6_Semester")));
				}
				if (i == 70) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Current_Course")));
				}
				if (i == 71) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Status")));
				}
				if (i == 72) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Academic")));
				}
				if (i == 73) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Sports")));
				}
				if (i == 74) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Others")));
				}
				if (i == 75) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Education_Gap_in_Years")));
					cell.setCellType(CellType.STRING);
				}
				if (i == 76) {
					createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Password")));
					cell.setCellType(CellType.STRING);
				}
				if (i == 77) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Verified_EMail")));
					cell.setCellType(CellType.STRING);
				}
				if (i == 78) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Blood_Group")));
					cell.setCellType(CellType.STRING);
				}
				if (i == 79) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Handicapped_Details")));
					cell.setCellType(CellType.STRING);
				}
				if (i == 80) {
					createCellStyle(workBook, columnFontStyle, cell,
							HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
					cell.setCellValue(new HSSFRichTextString(FbResourceUtil.getLabel("Verified_Mobile_Number")));
					cell.setCellType(CellType.STRING);
				}
			}
			int i = 0;
			HSSFCellStyle cellStyle = createCellStyle(workBook, columnFontStyle,
					HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			// cellStyle = workBook.createCellStyle();
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			HSSFRow chieldRow;
			for (Registration registration : list) {
				i++;
				chieldRow = spreadSheet.createRow(i);
				for (int j = 0; j < ROWSIZE; j++) {
					cell = chieldRow.createCell((short) j);
					cell.setCellStyle(cellStyle);
					if (j == 0) {

						setCellValue(cell, registration.getRollnumber());
						continue;
					}
					if (j == 1) {
						setCellValue(cell, registration.getFirstName());
						continue;
					}
					if (j == 2) {
						setCellValue(cell, registration.getLastName());
						continue;
					}
					if (j == 3) {

						setCellValue(cell, registration.getPersonalinfo().getCourse());
						continue;
					}
					if (j == 4) {

						setCellValue(cell, registration.getPersonalinfo().getBranch());
						continue;
					}
					if (j == 5) {
						setCellValue(cell, registration.getPersonalinfo().getSpecialization());
						continue;
					}
					if (j == 6) {

						setCellValue(cell, registration.getPersonalinfo().getSemester());
						continue;
					}
					if (j == 7) {

						setCellValue(cell, registration.getPersonalinfo().getDob());
						continue;
					}
					if (j == 8) {

						setCellValue(cell, registration.getPercentageinfo().getHighSchoolPercent());
						continue;
					}
					if (j == 9) {

						setCellValue(cell, String.valueOf(registration.getPercentageinfo().getHighSchoolPassing()));
						continue;
					}
					if (j == 10) {

						setCellValue(cell, registration.getPercentageinfo().getHighSchoolBoard());
						continue;
					}
					if (j == 11) {

						setCellValue(cell, registration.getPercentageinfo().getHigherSecondarypercent());
						continue;
					}
					if (j == 12) {

						setCellValue(cell,
								String.valueOf(registration.getPercentageinfo().getHigherSecondaryPassing()));
						continue;
					}
					if (j == 13) {

						setCellValue(cell, registration.getPercentageinfo().getHigherSecondaryBoard());
						continue;
					}
					if (j == 14) {
						setCellValue(cell, registration.getPercentageinfo().getMeBsc1sem());
						continue;
					}
					if (j == 15) {
						setCellValue(cell, registration.getPercentageinfo().getMeBsc2sem());
						continue;
					}
					if (j == 16) {
						setCellValue(cell, registration.getPercentageinfo().getMeBsc3sem());
						continue;
					}
					if (j == 17) {
						setCellValue(cell, registration.getPercentageinfo().getMeBsc4sem());
						continue;
					}
					if (j == 18) {
						setCellValue(cell, registration.getPercentageinfo().getDiplomaOthers());
						continue;
					}
					if (j == 19) {
						setCellValue(cell, registration.getPersonalinfo().getDiplomaUniversity());
						continue;
					}
					if (j == 20) {
						setCellValue(cell, registration.getPercentageinfo().getBe1sem());
						continue;
					}
					if (j == 21) {
						setCellValue(cell, registration.getPercentageinfo().getBe2sem());
						continue;
					}
					if (j == 22) {
						setCellValue(cell, registration.getPercentageinfo().getBe3sem());
						continue;
					}
					if (j == 23) {
						setCellValue(cell, registration.getPercentageinfo().getBe4sem());
						continue;
					}
					if (j == 24) {
						setCellValue(cell, registration.getPercentageinfo().getBe5sem());
						continue;
					}
					if (j == 25) {
						setCellValue(cell, registration.getPercentageinfo().getBe6sem());
						continue;
					}
					if (j == 26) {
						setCellValue(cell, registration.getPercentageinfo().getBe7sem());
						continue;
					}
					if (j == 27) {
						setCellValue(cell, registration.getPercentageinfo().getBe8sem());
						continue;
					}
					if (j == 28) {
						setCellValue(cell, registration.getPercentageinfo().getAvgbe1year());
						continue;
					}
					if (j == 29) {
						setCellValue(cell, registration.getPercentageinfo().getAvgbe2year());
						continue;
					}
					if (j == 30) {
						setCellValue(cell, registration.getPercentageinfo().getAvgbe3year());
						continue;
					}
					if (j == 31) {
						setCellValue(cell, registration.getPercentageinfo().getAvgbe4year());
						continue;
					}
					if (j == 32) {
						setCellValue(cell, registration.getPercentageinfo().getBeAverege());
						continue;
					}
					if (j == 33) {

						setCellValue(cell, registration.getBackdetails().getBackLog());
						continue;
					}
					if (j == 34) {
						setCellValue(cell, registration.getBackdetails().getBackDetails());
						continue;
					}
					if (j == 35) {

						setCellValue(cell, registration.getBackdetails().getNumberOfBacklogs());
						continue;
					}
					if (j == 36) {

						setCellValue(cell, registration.getBackdetails().getBaGroup());
						continue;
					}
					if (j == 37) {

						setCellValue(cell, registration.getBackdetails().getPassMoreThenOneAttempt());
						continue;
					}
					if (j == 38) {

						setCellValue(cell, registration.getEmail());
						continue;
					}
					if (j == 39) {
						setCellValue(cell, registration.getContactinfo().getPhoneNumber());
						continue;
					}
					if (j == 40) {

						setCellValue(cell, registration.getContactinfo().getMobileNumber());
						continue;
					}
					if (j == 41) {

						setCellValue(cell, registration.getContactinfo().getPresentAddress());
						continue;
					}
					if (j == 42) {

						setCellValue(cell, registration.getContactinfo().getPresentCity());
						continue;
					}
					if (j == 43) {

						setCellValue(cell, registration.getContactinfo().getPresentState());
						continue;
					}
					if (j == 44) {

						setCellValue(cell, registration.getContactinfo().getPermanentAddress());
						continue;
					}
					if (j == 45) {

						setCellValue(cell, registration.getContactinfo().getPermanentCity());
						continue;
					}
					if (j == 46) {

						setCellValue(cell, registration.getContactinfo().getPermanentState());
						continue;
					}
					if (j == 47) {

						setCellValue(cell, registration.getContactinfo().getHieght());
						continue;
					}
					if (j == 48) {

						setCellValue(cell, registration.getContactinfo().getWeight());
						continue;
					}
					if (j == 49) {

						setCellValue(cell, registration.getContactinfo().getGlassPowerLeft());
						continue;
					}
					if (j == 50) {

						setCellValue(cell, registration.getContactinfo().getGlassPowerRight());
						continue;
					}
					if (j == 51) {

						setCellValue(cell, String.valueOf(registration.getPersonalinfo().getYearOfPassing()));
						continue;
					}
					if (j == 52) {
						setCellValue(cell, registration.getPersonalinfo().getRemark());
						continue;
					}
					if (j == 53) {

						setCellValue(cell, registration.getPersonalinfo().getGender());
						continue;
					}
					if (j == 54) {

						setCellValue(cell, registration.getCollegeName());
						continue;
					}
					if (j == 55) {
						setCellValue(cell, registration.getPersonalinfo().getCompanyName());
					}
					if (j == 56) {
						if (registration.getBackdetails().getBlackList() == null) {
							setCellValue(cell, false);
						} else {
							setCellValue(cell, registration.getBackdetails().getBlackList());
						}
					}
					if (j == 57) {

						setCellValue(cell, registration.getPersonalinfo().getGraduationUniversity());
					}
					if (j == 58) {
						setCellValue(cell, registration.getPersonalinfo().getPostGraduationCourse());
					}
					if (j == 59) {
						setCellValue(cell, registration.getPersonalinfo().getPostGraduationBranch());
					}
					if (j == 60) {
						setCellValue(cell, registration.getPersonalinfo().getPostGraduationUniversity());
					}
					if (j == 61) {
						setCellValue(cell, registration.getPercentageinfo().getMeAverage());
					}
					if (j == 62) {
						setCellValue(cell, registration.getPersonalinfo().getDiploma());
					}
					if (j == 63) {
						setCellValue(cell, registration.getPersonalinfo().getDiplomaBranch());
					}
					if (j == 64) {
						setCellValue(cell, registration.getPercentageinfo().getDiploma1sem());
					}
					if (j == 65) {
						setCellValue(cell, registration.getPercentageinfo().getDiploma2sem());
					}
					if (j == 66) {
						setCellValue(cell, registration.getPercentageinfo().getDiploma3sem());
					}
					if (j == 67) {
						setCellValue(cell, registration.getPercentageinfo().getDiploma4sem());
					}
					if (j == 68) {
						setCellValue(cell, registration.getPercentageinfo().getDiploma5sem());
					}
					if (j == 69) {
						setCellValue(cell, registration.getPercentageinfo().getDiploma6sem());
					}
					if (j == 70) {

						setCellValue(cell, registration.getPersonalinfo().getCurrentCourse());
					}
					if (j == 71) {

						setCellValue(cell, registration.getApproved());
					}
					if (j == 72) {
						if (registration.getAchivements() == null
								|| registration.getAchivements().getAcedamic() == null) {
							setCellValue(cell, "");
						} else {
							setCellValue(cell, registration.getAchivements().getAcedamic());
						}
					}
					if (j == 73) {
						if (registration.getAchivements() == null
								|| registration.getAchivements().getSports() == null) {
							setCellValue(cell, "");
						} else {
							setCellValue(cell, registration.getAchivements().getSports());
						}
					}
					if (j == 74) {
						if (registration.getAchivements() == null
								|| registration.getAchivements().getOthers() == null) {
							setCellValue(cell, "");
						} else {
							setCellValue(cell, registration.getAchivements().getOthers());
						}
					}
					if (j == 75) {

						setCellValue(cell, String.valueOf(registration.getBackdetails().getEducationGap()));
					}
					if (j == 76) {
						setCellValue(cell, String.valueOf(registration.getPassword()));
					}
					if (j == 77) {
						setCellValue(cell, registration.getEmailVarified());
					}
					if (j == 78) {
						setCellValue(cell, registration.getPersonalinfo().getBloodGroup());
					}
					if (j == 79) {
						setCellValue(cell, registration.getPersonalinfo().getHandicapped());
					}
					if (j == 80) {
						setCellValue(cell, registration.getContactinfo().getNumberVerified() == null ? "FALSE"
								: registration.getContactinfo().getNumberVerified());
					}
				}
			}

			HSSFSheet spreadSheetNew = workBook.createSheet("Important Note");
			HSSFRow impNote = spreadSheetNew.createRow(0);
			cell = impNote.createCell((short) 0);
			// cell.setCellStyle(cellStyle);
			createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
			cell = impNote.createCell((short) 1);
			// cell.setCellStyle(cellStyle);
			cellStyle = createCellStyle(workBook, columnFontStyle, HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			setCellValue(cell, "Optional Column");

			impNote = spreadSheetNew.createRow(1);
			cell = impNote.createCell((short) 0);
			// cell.setCellStyle(cellStyle);
			createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.RED.getIndex());
			cell = impNote.createCell((short) 1);
			// cell.setCellStyle(cellStyle);
			cellStyle = createCellStyle(workBook, columnFontStyle, HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			setCellValue(cell, "Mandatory Column");
			impNote = spreadSheetNew.createRow(2);
			cell = impNote.createCell((short) 0);
			// cell.setCellStyle(cellStyle);
			createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
			cell = impNote.createCell((short) 1);
			// cell.setCellStyle(cellStyle);
			cellStyle = createCellStyle(workBook, columnFontStyle, HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			setCellValue(cell, "Mandatory Column");
			impNote = spreadSheetNew.createRow(3);
			cell = impNote.createCell((short) 0);
			// cell.setCellStyle(cellStyle);
			createCellStyle(workBook, columnFontStyle, cell, HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
			cell = impNote.createCell((short) 1);
			// cell.setCellStyle(cellStyle);
			cellStyle = createCellStyle(workBook, columnFontStyle, HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			cellStyle.setWrapText(false);
			cellStyle.setFont(rowsFontStyle);
			setCellValue(cell, "Mandatory Column");
			// Outputting to Excel spreadsheet
			workBook.write(byteArrayOutputStream);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return byteArrayOutputStream.toByteArray();
	}

	private static HSSFCellStyle createCellStyle(HSSFWorkbook workBook, HSSFFont columnFontStyle, short color) {
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderRight(BorderStyle.THICK);
		cellStyle.setBorderTop(BorderStyle.THICK);
		cellStyle.setBorderLeft(BorderStyle.THICK);
		cellStyle.setBorderBottom(BorderStyle.THICK);
		cellStyle.setFillForegroundColor(color);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);
		cellStyle.setFont(columnFontStyle);
		cellStyle.setWrapText(true);
		return cellStyle;
	}

	private static void createCellStyle(HSSFWorkbook workBook, HSSFFont columnFontStyle, HSSFCell cell, short color) {
		HSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setBorderRight(BorderStyle.THICK);
		cellStyle.setBorderTop(BorderStyle.THICK);
		cellStyle.setBorderLeft(BorderStyle.THICK);
		cellStyle.setBorderBottom(BorderStyle.THICK);
		cellStyle.setFillForegroundColor(color);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setAlignment(HorizontalAlignment.RIGHT);
		cellStyle.setFont(columnFontStyle);
		cellStyle.setWrapText(true);
		cell.setCellStyle(cellStyle);
	}

	private static void setCellValue(HSSFCell cell, Object object) {
		if (object instanceof String) {
			cell.setCellValue(new HSSFRichTextString((String) object));
			cell.setCellType(CellType.STRING);
		} else if (object instanceof Double) {
			cell.setCellValue((Double) object);
			cell.setCellType(CellType.NUMERIC);
		} else if (object instanceof Integer) {
			cell.setCellValue((Integer) object);
			cell.setCellType(CellType.NUMERIC);
		} else if (object instanceof Long) {
			cell.setCellValue((Long) object);
			cell.setCellType(CellType.NUMERIC);
		} else if (object instanceof Short) {
			cell.setCellValue((Short) object);
			cell.setCellType(CellType.NUMERIC);
		} else if (object instanceof Boolean) {
			cell.setCellValue((Boolean) object);
			cell.setCellType(CellType.BOOLEAN);
		} else if (object instanceof Date) {
			cell.setCellValue(new HSSFRichTextString(TpoUtil.getDateToString((Date) object)));
			cell.setCellType(CellType.STRING);
		}
	}
}
