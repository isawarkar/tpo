/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package com.beans;

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

import com.hibernate.Result;
import com.util.FbResourceUtil;
import com.util.TpoUtil;

/**
 * @author Uddanda Technologies
 */
public class ExcelHandler {

	private static Logger logger = LoggerFactory.getLogger(ExcelHandler.class);

	private static final int ROWSIZE_RESULT = 9;

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
