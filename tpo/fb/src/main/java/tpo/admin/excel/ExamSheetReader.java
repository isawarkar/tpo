/* 
 *  Class Name : This  
 *  v1.0 
 *  This file is copyrighted by Uddanda Technologies.  
 *  Contents of this file can not be changed with out the permission Uddanda Technologies 
 *  
 */
package tpo.admin.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpo.beans.UIBackingBean;
import tpo.hibernate.Questions;
import tpo.hibernate.QuestionsId;
import tpo.util.FbMessageUtil;

/**
 * @author Uddanda Technologies
 */
public class ExamSheetReader {

	private Logger logger = LoggerFactory.getLogger(ExamSheetReader.class);

	/**
	 ** This method is used to read the data's from an excel file.
	 * 
	 * @param fileName
	 *            - Name of the excel file.
	 * 
	 * 
	 */
	public List<Questions> readExcelFile(InputStream inputStream) {

		List<Questions> list = new ArrayList<Questions>();

		try {
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
			 * Iterate the rows and cells of the spreadsheet to get all the
			 * datas.
			 */
			Iterator rowIterator = hssfSheet.rowIterator();
			Questions questions = null;
			QuestionsId id;
			// ignore first row
			rowIterator.next();
			while (rowIterator.hasNext()) {
				questions = new Questions();
				id = new QuestionsId();
				HSSFRow hssfRow = (HSSFRow) rowIterator.next();
				Iterator iterator = hssfRow.cellIterator();
				while (iterator.hasNext()) {
					HSSFCell hssfCell = (HSSFCell) iterator.next();
					int cellNumber = hssfCell.getRowIndex();
					CellType cellType = hssfCell.getCellType();
					switch (cellNumber) {
					case 0:
						setQType(id, hssfCell, cellType);
						break;
					case 1:
						setQNumber(id, hssfCell, cellType);
						break;
					case 2:
						setQuestion(questions, hssfCell, cellType);
						break;
					case 3:
						setOptionA(questions, hssfCell, cellType);
						break;
					case 4:
						setOptionB(questions, hssfCell, cellType);
						break;
					case 5:
						setOptionC(questions, hssfCell, cellType);
						break;
					case 6:
						setOptionD(questions, hssfCell, cellType);
						break;
					case 7:
						setAnswer(questions, hssfCell, cellType);
						String strArr[] = questions.getAnswer().split(",");
						if(strArr != null && strArr.length > 0){
						for(String ans : strArr){
							if(!ans.matches("[a-dA-D]")){
								UIBackingBean.setErrorMessage(FbMessageUtil.getLabel("Please_enter_correct_answer"));
								return null;
							}
						}
						}
						break;
					case 8:
						setQuestionType(questions, hssfCell, cellType);
						break;
					case 9:
						setAssignedNo(questions, hssfCell, cellType);
						break;
					case 10:
						setIsImage(questions, hssfCell, cellType);
						break;
					}
					questions.setId(id);
				}
				list.add(questions);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	private void setIsImage(Questions questions, HSSFCell hssfCell, CellType cellType) {
	switch (cellType) {
		case STRING:
			questions.setIsImage(Boolean.valueOf(hssfCell.getRichStringCellValue().toString()));
			break;
		case NUMERIC:
			questions.setIsImage(Boolean.valueOf(String.valueOf(hssfCell.getNumericCellValue())));
			break;
		case BOOLEAN:
			questions.setIsImage(Boolean.valueOf(String.valueOf(hssfCell.getBooleanCellValue())));
			break;
		}
	}
	
	private void setAssignedNo(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
			case STRING:
				questions.setAssignedNo(Double.valueOf(hssfCell.getRichStringCellValue().toString()));
				break;
			case NUMERIC:
				questions.setAssignedNo(Double.valueOf(String.valueOf(hssfCell.getNumericCellValue())));
				break;
			case BOOLEAN:
				questions.setAssignedNo(Double.valueOf(String.valueOf(hssfCell.getBooleanCellValue())));
				break;
			}
		}
	
	private void setQuestionType(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
			case STRING:
				questions.setQuestionType(hssfCell.getRichStringCellValue().toString());
				break;
			case NUMERIC:
				questions.setQuestionType(String.valueOf(hssfCell.getNumericCellValue()));
				break;
			case BOOLEAN:
				questions.setQuestionType(String.valueOf(hssfCell.getBooleanCellValue()));
				break;
			}
		}

	private void setAnswer(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			questions.setAnswer(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			questions.setAnswer(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			questions.setAnswer(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}

	private void setOptionD(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			questions.setOptiond(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			questions.setOptiond(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			questions.setOptiond(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}

	private void setOptionC(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			questions.setOptionc(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			questions.setOptionc(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			questions.setOptionc(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}

	private void setOptionB(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			questions.setOptionb(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			questions.setOptionb(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			questions.setOptionb(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}

	private void setOptionA(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			questions.setOptiona(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			questions.setOptiona(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			questions.setOptiona(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}

	private void setQuestion(Questions questions, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			questions.setQuestion(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			questions.setQuestion(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			questions.setQuestion(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}

	private void setQNumber(QuestionsId id, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			id.setQno(new Double(hssfCell.getRichStringCellValue().toString()).intValue());
			break;
		case NUMERIC:
			id.setQno(new Double(hssfCell.getNumericCellValue()).intValue());
			break;
		}
	}

	private void setQType(QuestionsId id, HSSFCell hssfCell, CellType cellType) {
		switch (cellType) {
		case STRING:
			id.setQtype(hssfCell.getRichStringCellValue().toString());
			break;
		case NUMERIC:
			id.setQtype(String.valueOf(hssfCell.getNumericCellValue()));
			break;
		case BOOLEAN:
			id.setQtype(String.valueOf(hssfCell.getBooleanCellValue()));
			break;
		}
	}
}
