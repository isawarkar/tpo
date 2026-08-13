package tpo.pdf.generator;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import tpo.admin.beans.AdminUser;
import tpo.beans.Parent;
import tpo.beans.ResultBean;
import tpo.dao.CommonDBBean;
import tpo.hibernate.College;
import tpo.hibernate.Company;
import tpo.hibernate.EmployeeEfforts;
import tpo.hibernate.Exam;
import tpo.hibernate.HallTicket;
import tpo.hibernate.HallTicketConnect;
import tpo.hibernate.Logindetails;
import tpo.hibernate.Module;
import tpo.hibernate.Notice;
import tpo.hibernate.Project;
import tpo.hibernate.Registration;
import tpo.hibernate.Result;
import tpo.hibernate.annotation.ReferralHistory;
import tpo.hibernate.annotation.StudentFeeDetails;
import tpo.imageservice.client.FileUploadUtility;
import tpo.qr.code.GenerateQRCode;
import tpo.util.AES;
import tpo.util.CCPConstant;
import tpo.util.FbResourceUtil;
import tpo.util.IMAGECONS;
import tpo.util.TpoUtil;

/**
 * 
 * This class implements HangTagGenerator interface and thus is mainly
 * responsible for creating and generating hang tag in PDF form.
 * 
 */
@Component("pDFGenerator")
@Scope("session")
public class PDFGenerator extends Parent {

	private Logger logger = LoggerFactory.getLogger(PDFGenerator.class);

	private Document document;

	private PdfWriter pdfWriter;

	private final float marginLeft = 3f;

	private final float marginRight = 3f;

	private final float marginTop = 10f;

	private final float marginBottom = 3f;

	private float pageWidth = 90f;

	@Autowired
	private FileUploadUtility fileUploadUtility;
	
	public byte[] generateHallTicket(Registration registration, List<HallTicket> list, String userName) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.addHeader("Uddanda", "Technologies");
			document.open();
			for (HallTicket hallTicket : list) {
				PdfPCell pdfPCell;
				Image image;
				addFbandClientLogo(userName);

				PdfPTable dateTimeContaner = getPdfPTable(1);
				dateTimeContaner.setWidthPercentage(pageWidth);

				PdfPTable dateTimeMainTable = getPdfPTable(1);

				PdfPTable companyLDateTimeStudentProfilePicTable = getPdfPTable(4);
				float[] columnWidth = new float[] { 15f, 35f, 35f, 15f };
				companyLDateTimeStudentProfilePicTable.setWidths(columnWidth);

				// Adding Company logo
				if (hallTicket.getCompany() != null) {
					image = Image.getInstance(fileUploadUtility.downloadFile(getImageServiceUrl() + "/downloadImage", String.valueOf(hallTicket.getCompanyID()), IMAGECONS.company));
				} else {
					image = Image.getInstance(TpoUtil.getNABytes());
				}
				image.setAlignment(Image.ALIGN_LEFT);
				image.setWidthPercentage(50f);
				pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
				pdfPCell.setBorder(0);
				pdfPCell.addElement(image);
				companyLDateTimeStudentProfilePicTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL,
						"Date:" + TpoUtil.getDateToStringInddmmyyyy(hallTicket.getDate()), 17, 10, Color.BLUE,
						Color.WHITE);
				pdfPCell.setBorder(0);
				companyLDateTimeStudentProfilePicTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "Reporting Time:" + hallTicket.getTime(), 17, 10, Color.BLUE,
						Color.WHITE);
				pdfPCell.setBorder(0);
				companyLDateTimeStudentProfilePicTable.addCell(pdfPCell);

				// Adding Student Image
				if (registration != null) {
					CommonDBBean commonDBBean = (CommonDBBean)TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
					image = Image.getInstance(commonDBBean.getStudentProfilePic(registration.getRollnumber()));
				} else {
					image = Image.getInstance(TpoUtil.getNABytes());
				}
				image.setAlignment(Image.ALIGN_RIGHT);
				image.setWidthPercentage(50f);
				pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
				pdfPCell.setBorder(0);
				pdfPCell.addElement(image);
				companyLDateTimeStudentProfilePicTable.addCell(pdfPCell);

				dateTimeMainTable.addCell(companyLDateTimeStudentProfilePicTable);

				PdfPTable pdfPTableComapanyAndHallTicketBatch = getPdfPTable(4);
				pdfPTableComapanyAndHallTicketBatch.setWidths(new float[] { 20f, 50f, 30f, 10f });
				pdfPCell = getFormatedCell(Font.BOLD, "Company Name ", 15, 10, Color.BLACK, Color.WHITE);
				pdfPCell.setBackgroundColor(Color.green);
				pdfPTableComapanyAndHallTicketBatch.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, hallTicket.getCompanyName(), 15, 10, Color.BLACK, Color.WHITE);
				pdfPCell.setBackgroundColor(Color.green);
				pdfPTableComapanyAndHallTicketBatch.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Exam Hall Ticket Batch ", 15, 10, Color.BLACK, Color.WHITE);
				pdfPCell.setBackgroundColor(Color.green);
				pdfPTableComapanyAndHallTicketBatch.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getPersonalinfo().getYearOfPassing()),
						15, 10, Color.BLACK, Color.WHITE);
				pdfPCell.setBackgroundColor(Color.green);
				pdfPTableComapanyAndHallTicketBatch.addCell(pdfPCell);

				dateTimeMainTable.addCell(pdfPTableComapanyAndHallTicketBatch);

				PdfPTable personalInfoTable = getPdfPTable(2);

				pdfPCell = getFormatedCell(Font.NORMAL, "ASSIGNED NUMBER :", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "COLLEGE NAME :", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, registration.getCollegeName(), 12, 8, Color.BLUE, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "CANDIDATE NAME: ", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, registration.getFirstName() + " " + registration.getLastName(),
						12, 8, Color.BLUE, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "ENROLLMENT NUMBER: ", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, registration.getRollnumber(), 12, 8, Color.BLUE, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "DATE OF BIRTH: ", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL,
						TpoUtil.getDateToStringInddmmyyyy(registration.getPersonalinfo().getDob()), 12, 8, Color.BLUE,
						Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "GENDER:", 15, 10, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL,
						"M".equals(registration.getPersonalinfo().getGender()) ? "Male" : "Female", 15, 10, Color.BLUE,
						Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "CONTACT NO: ", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, registration.getContactinfo().getMobileNumber(), 12, 8,
						Color.BLUE, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "Email ID: ", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, registration.getEmail(), 12, 8, Color.BLUE, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "ADDRESS: ", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, registration.getContactinfo().getPresentAddress(), 12, 8,
						Color.BLUE, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "Remark:", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				pdfPCell = getFormatedCell(Font.NORMAL, "", 12, 8, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				personalInfoTable.addCell(pdfPCell);

				dateTimeMainTable.addCell(personalInfoTable);

				dateTimeContaner.addCell(dateTimeMainTable);

				document.add(dateTimeContaner);

				PdfPTable percentageTable = getPdfPTable(9);
				float[] columnWidths = new float[] { 30f, 5f, 5f, 5f, 5f, 7f, 7f, 7f, 5f };
				percentageTable.setWidths(columnWidths);
				percentageTable.setWidthPercentage(pageWidth);
				percentageTable.setSpacingBefore(40f);
				pdfPCell = getFormatedCell(Font.BOLD, "Course/Branch", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "10th(%)", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Passing Year", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "12th(%)", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Passing Year", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Diploma(%)", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Back Log(Y/N)", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Engg.(%)", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "Passing Year", 11, 10, Color.BLACK, Color.LIGHT_GRAY);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getCourse(), 15, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL,
						String.valueOf(registration.getPercentageinfo().getHighSchoolPercent()), 15, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL,
						String.valueOf(registration.getPercentageinfo().getHighSchoolPassing()), 15, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL,
						String.valueOf(registration.getPercentageinfo().getHigherSecondarypercent() == null ? "NA"
								: registration.getPercentageinfo().getHigherSecondarypercent()),
						15, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL,
						String.valueOf(registration.getPercentageinfo().getHigherSecondaryPassing() == null ? "NA"
								: registration.getPercentageinfo().getHigherSecondaryPassing()),
						15, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				if (registration.getPercentageinfo().getDiplomaOthers() != null) {
					pdfPCell = getFormatedCell(Font.NORMAL,
							String.valueOf(registration.getPercentageinfo().getDiplomaOthers() == 0.0 ? "NA"
									: registration.getPercentageinfo().getDiplomaOthers()),
							15, 10, Color.BLACK, Color.WHITE);
				} else {
					pdfPCell = getFormatedCell(Font.NORMAL, "NA", 15, 10, Color.BLACK, Color.WHITE);
				}
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL,
						registration.getBackdetails().getBackLog() == 0 ? FbResourceUtil.getLabel("NO")
								: FbResourceUtil.getLabel("YES"),
						15, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(registration.getPercentageinfo().getBeAverege()),
						15, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL,
						String.valueOf(registration.getPersonalinfo().getYearOfPassing()), 15, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				document.add(percentageTable);

				PdfPTable extraNote = getPdfPTable(2);
				extraNote.setWidths(new float[] { 10f, 90f });
				extraNote.setSpacingBefore(40f);
				extraNote.setWidthPercentage(pageWidth);
				pdfPCell = getFormatedCell(Font.NORMAL, "Extra Note                    ", 15, 10, Color.BLACK,
						Color.WHITE);
				extraNote.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLACK, Color.WHITE);
				extraNote.addCell(pdfPCell);
				document.add(extraNote);

				PdfPTable declarationTable = getPdfPTable(1);
				declarationTable.setWidthPercentage(pageWidth);
				declarationTable.setSpacingBefore(40f);
				pdfPCell = getFormatedCell(Font.BOLD,
						"Declarations: I hereby declare that the foregoing information is correct and complete to the best of my knowledge and belief and nothing has been concealed. If at any time, I am found to have concealed any material information or given any false details about myself, I understand that my appointment shall be liable to summary termination without any notice or compensation thereof to me.",
						15, 10, Color.RED, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				declarationTable.addCell(pdfPCell);
				document.add(declarationTable);

				if (hallTicket.getAllowDigitalSignature()) {
					PdfPTable qrCodeTable = getPdfPTable(1);
					qrCodeTable.setWidthPercentage(pageWidth);
					qrCodeTable.setSpacingBefore(250f);
					pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
					pdfPCell.setBorder(0);
					String sigNString = registration.getRollnumber() + "#" + hallTicket.getDigitalSignature();
					byte[] qrImage = GenerateQRCode.getInstance()
							.createQRImage(AES.symmetricEncrypt(sigNString, TpoUtil.geyKeyInfo()), 125, "png");
					image = Image.getInstance(qrImage);
					image.setBorder(0);
					image.setAlignment(Image.ALIGN_RIGHT);
					image.setWidthPercentage(20f);
					pdfPCell.addElement(image);
					qrCodeTable.addCell(pdfPCell);
					document.add(qrCodeTable);
				}

				PdfPTable pdfPTableSign = getPdfPTable(2);
				pdfPTableSign.setWidthPercentage(pageWidth);
				if (!hallTicket.getAllowDigitalSignature()) {
					pdfPTableSign.setSpacingBefore(400f);
				}
				pdfPCell = getFormatedCell(Font.BOLD, "Signature of Candidate", 15, 10, Color.BLACK, Color.WHITE);
				pdfPCell.setBorderColor(Color.WHITE);
				pdfPTableSign.addCell(pdfPCell);
				if (!hallTicket.getAllowDigitalSignature()) {
					pdfPCell = getFormatedCell(Font.BOLD, "Signature & Seal of Authority T&P", 15, 10, Color.BLACK,
							Color.WHITE);
				} else {
					pdfPCell = getFormatedCell(Font.BOLD, "This is Digitally Sign Ticket.Please scan QR code", 15, 10,
							Color.BLACK, Color.WHITE);
				}
				pdfPCell.setBorderColor(Color.WHITE);
				pdfPTableSign.addCell(pdfPCell);
				document.add(pdfPTableSign);

				document.newPage();
			}
			document.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return byteArrayOutputStream.toByteArray();

	}

	public byte[] generateCertificate(ResultBean bean) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			Registration registration = bean.getRegistration();
			Exam exam = bean.getExam();
			Document.compress = false;
			document = new Document(PageSize.LEDGER, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			Rectangle rect = new Rectangle(36, 36, 1180, 785);

			pdfWriter.setBoxSize("art", rect);
			HeaderFooterPageEvent event = new HeaderFooterPageEvent();
			pdfWriter.setPageEvent(event);
			document.addAuthor(CCPConstant.APP_NAME);
			document.addHeader("Uddanda", "Technologies");
			document.open();
			PdfContentByte canvas = pdfWriter.getDirectContent();
			rect.setBorder(Rectangle.BOX);
			rect.setBorderColor(Color.RED);
			rect.setBorderWidth(4);
			canvas.rectangle(rect);
			Image image;
			image = Image.getInstance(TpoUtil.getFBFileLogo());
			image.setAlignment(Image.ALIGN_CENTER);
			image.setBorder(0);
			document.add(image);

			float fntSize, lineSpacing;
			fntSize = 25.7f;
			lineSpacing = 50f;
			Paragraph p = new Paragraph(
					new Phrase(lineSpacing, "THIS CERTIFIES THAT", FontFactory.getFont(FontFactory.COURIER, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);
			fntSize = 30.7f;
			lineSpacing = 30f;
			p = new Paragraph(new Phrase(lineSpacing,
					WordUtils.capitalize(registration.getFirstName()) + " "
							+ WordUtils.capitalize(registration.getLastName()),
					FontFactory.getFont(FontFactory.TIMES_BOLD, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);
			fntSize = 25.7f;
			lineSpacing = 90f;
			p = new Paragraph(
					new Phrase(lineSpacing, "IS RECOGNIZED BY", FontFactory.getFont(FontFactory.COURIER, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);
			fntSize = 25.7f;
			lineSpacing = 30f;
			p = new Paragraph(new Phrase(lineSpacing, "THE FRESHER BUDDY CERTIFICATION PROGRAM AS AN",
					FontFactory.getFont(FontFactory.COURIER, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);

			fntSize = 30.7f;
			lineSpacing = 40f;
			p = new Paragraph(new Phrase(lineSpacing, exam.getTestname() + "  Certified Associate",
					FontFactory.getFont(FontFactory.TIMES_BOLD, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);

			if (bean.getResultIn() != null) {
				fntSize = 20.7f;
				lineSpacing = 30f;
				p = new Paragraph(
						new Phrase(lineSpacing, "In", FontFactory.getFont(FontFactory.TIMES_ITALIC, fntSize)));
				p.setAlignment(Paragraph.ALIGN_CENTER);
				document.add(p);

				fntSize = 40.7f;
				lineSpacing = 40f;
				p = new Paragraph(new Phrase(lineSpacing, bean.getResultIn() + "(" + bean.getPercent() + ")",
						FontFactory.getFont(FontFactory.TIMES_BOLD, fntSize)));
				p.setAlignment(Paragraph.ALIGN_CENTER);
				document.add(p);

			}
			Date date = new Date();
			fntSize = 20.7f;
			lineSpacing = 140f;
			p = new Paragraph(
					new Phrase(lineSpacing, date.toString(), FontFactory.getFont(FontFactory.TIMES_BOLD, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);
			fntSize = 20.7f;
			lineSpacing = 80f;
			p = new Paragraph(
					new Phrase(lineSpacing, "Signature & Seal", FontFactory.getFont(FontFactory.TIMES_BOLD, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);

			fntSize = 10.7f;
			lineSpacing = 100f;
			p = new Paragraph(new Phrase(lineSpacing, "Note:This certificate is not valid without signature & Seal",
					FontFactory.getFont(FontFactory.TIMES_BOLD, fntSize)));
			p.setAlignment(Paragraph.ALIGN_CENTER);
			document.add(p);

			document.addCreationDate();
			document.addCreator("By Fresher Buddy");
			document.addTitle("Certificate of Excellence");

			document.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return byteArrayOutputStream.toByteArray();

	}

	@SuppressWarnings("unchecked")
	public byte[] generateRegistrationForm(Registration registration, CommonDBBean commonDBBean) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.addHeader("Uddanda", "Technologies");
			document.open();

			Image image;
			if (commonDBBean != null) {
				String userName = commonDBBean.getUserNameByCollegeName(registration.getCollegeName());
				addFbandClientLogo(userName);
			}

			PdfPTable dateTimeContaner = getPdfPTable(1);
			dateTimeContaner.setWidthPercentage(pageWidth);

			PdfPTable dateTimeMainTable = getPdfPTable(1);

			// Main Info start
			PdfPTable barcodeQrProfilePicTable = getPdfPTable(3);
			PdfPCell pdfPCell;

			pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
			pdfPCell.setBorder(1);
			image = generateBarCode128(String.valueOf(registration.getRollnumber()));
			image.setBorder(1);
			image.setBorderColor(Color.BLUE);
			image.setAlignment(Image.ALIGN_LEFT);
			image.setWidthPercentage(80f);
			pdfPCell.addElement(image);
			barcodeQrProfilePicTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
			pdfPCell.setBorder(1);
			image = Image
					.getInstance(GenerateQRCode.getInstance().createQRImage(registration.getRollnumber(), 100, "png"));
			image.setBorder(1);
			image.setBorderColor(Color.BLUE);
			image.setAlignment(Image.ALIGN_CENTER);
			image.setWidthPercentage(30f);
			pdfPCell.addElement(image);
			barcodeQrProfilePicTable.addCell(pdfPCell);

			if (registration != null) {
				image = Image.getInstance(commonDBBean.getStudentProfilePic(registration.getRollnumber()));
			} 
			pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
			pdfPCell.setBorder(1);
			image.setBorder(1);
			image.setBorderColor(Color.BLUE);
			image.setAlignment(Image.ALIGN_RIGHT);
			image.setWidthPercentage(30f);
			pdfPCell.addElement(image);
			barcodeQrProfilePicTable.addCell(pdfPCell);

			dateTimeMainTable.addCell(barcodeQrProfilePicTable);

			// new table

			PdfPTable pdfRollNumberAndCouseTable = getPdfPTable(4);

			pdfPCell = getFormatedCell(Font.NORMAL, "Current Course", 15, 10, Color.BLUE, Color.WHITE);
			pdfPCell.setBorder(1);
			pdfRollNumberAndCouseTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getCurrentCourse(), 15, 10,
					Color.BLACK, Color.WHITE);
			pdfPCell.setBorder(1);
			pdfRollNumberAndCouseTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Enrollment Number", 15, 10, Color.BLUE, Color.WHITE);
			pdfPCell.setBorder(1);
			pdfRollNumberAndCouseTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getRollnumber()), 15, 10, Color.BLACK,
					Color.WHITE);
			pdfPCell.setBorder(1);
			pdfRollNumberAndCouseTable.addCell(pdfPCell);

			dateTimeMainTable.addCell(pdfRollNumberAndCouseTable);

			dateTimeContaner.addCell(dateTimeMainTable);

			PdfPTable personalInfoTable = getPdfPTable(4);
			personalInfoTable.setWidths(new float[] { 20f, 40f, 20f, 40f });

			PdfPTable personalInfo = getPdfPTable(1);
			pdfPCell = getFormatedCell(Font.BOLD, "This is your Personal Information", 15, 10, Color.RED, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			personalInfo.addCell(pdfPCell);
			personalInfo.setSpacingBefore(10f);
			dateTimeContaner.addCell(personalInfo);

			pdfPCell = getFormatedCell(Font.NORMAL, "First Name", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getFirstName(), 12, 10, Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Last Name", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getLastName(), 12, 10, Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Date Of Birth", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD,
					TpoUtil.getDateToStringInddmmyyyy(registration.getPersonalinfo().getDob()), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Gender", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD,
					"M".equals(registration.getPersonalinfo().getGender()) ? "Male" : "Female", 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Contact No.", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getMobileNumber(), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "E-mail ID", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getEmail(), 12, 10, Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "College Name", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getCollegeName(), 12, 10, Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Semester", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getSemester(), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			// Diploma
			pdfPCell = getFormatedCell(Font.NORMAL, "Handicapped Details", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getHandicapped(), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Blood Group", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getBloodGroup(), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Specialization", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getSpecialization(), 12, 10,
					Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Selected IN", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getCompanyName(), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			// Height
			pdfPCell = getFormatedCell(Font.NORMAL, "Height(cms)", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getContactinfo().getHieght()), 12, 10,
					Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Weight(kg)", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getContactinfo().getWeight()), 12, 10,
					Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Glass Power Left", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getGlassPowerLeft(), 12, 10,
					Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Glass Power Right", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getGlassPowerRight(), 12, 10,
					Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			// Years
			pdfPCell = getFormatedCell(Font.NORMAL, "Passing Year", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getPersonalinfo().getYearOfPassing()), 12,
					10, Color.BLACK, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Status", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD,
					String.valueOf(registration.getApproved() == true ? "Approved" : "Pending"), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Resume", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getResume(), 12, 10, Color.BLACK,
					Color.WHITE);
			personalInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, "Last Updated", 12, 10, Color.BLUE, Color.WHITE);
			personalInfoTable.addCell(pdfPCell);
			if (registration.getPersonalinfo().getLastUpdated() != null) {
				pdfPCell = getFormatedCell(Font.BOLD,
						TpoUtil.getDateToString(registration.getPersonalinfo().getLastUpdated()), 12, 10, Color.BLACK,
						Color.WHITE);
			} else {
				pdfPCell = getFormatedCell(Font.BOLD, "", 12, 10, Color.BLACK, Color.WHITE);
			}
			personalInfoTable.addCell(pdfPCell);

			dateTimeContaner.addCell(personalInfoTable);
			document.add(dateTimeContaner);

			PdfPTable acinfo = getPdfPTable(1);
			pdfPCell = getFormatedCell(Font.BOLD,
					"This is your Academic Information(Last Update on "
							+ TpoUtil.getDateToString(registration.getPercentageinfo().getLastUpdated()) + ")",
					15, 10, Color.RED, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			acinfo.addCell(pdfPCell);
			acinfo.setSpacingBefore(10f);
			document.add(acinfo);

			PdfPTable percentageTableFirst = getPdfPTable(4);
			percentageTableFirst.setWidths(new float[] { 30f, 10f, 30f, 30f });
			percentageTableFirst.setWidthPercentage(pageWidth);
			percentageTableFirst.setSpacingBefore(10f);
			pdfPCell = getFormatedCell(Font.BOLD, "Course", 12, 10, Color.BLUE, Color.WHITE);
			percentageTableFirst.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Passing Year", 12, 10, Color.BLUE, Color.WHITE);
			percentageTableFirst.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Branch", 12, 10, Color.BLUE, Color.WHITE);
			percentageTableFirst.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "University", 12, 10, Color.BLUE, Color.WHITE);
			percentageTableFirst.addCell(pdfPCell);

			if (registration.getPercentageinfo().getHighSchoolPercent() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, "10th", 12, 10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getHighSchoolPassing()), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPercentageinfo().getHighSchoolBoard(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);

			}

			if (registration.getPercentageinfo().getHigherSecondarypercent() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, "12th", 12, 10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getHigherSecondaryPassing()), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPercentageinfo().getHigherSecondaryBoard(), 12,
						10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);

			}

			if (!"NA".equals(registration.getPersonalinfo().getDiploma())) {
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getDiploma(), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPersonalinfo().getDiplomaYearOfPassing()), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getDiplomaBranch(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getDiplomaUniversity(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);

			}
			if (registration.getPersonalinfo().getCourse() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getCourse(), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getPersonalinfo().getYearOfPassing()),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getBranch(), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getGraduationUniversity(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
			}

			if (registration.getPersonalinfo().getPostGraduationCourse() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getPostGraduationCourse(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPersonalinfo().getPgYearOfPassing()), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getPostGraduationBranch(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getPostGraduationUniversity(), 12,
						10, Color.BLACK, Color.WHITE);
				percentageTableFirst.addCell(pdfPCell);
			}

			document.add(percentageTableFirst);

			PdfPTable percentageTable = getPdfPTable(10);
			percentageTable.setWidths(new float[] { 46f, 6f, 6f, 6f, 6f, 6f, 6f, 6f, 6f, 6f });
			percentageTable.setWidthPercentage(pageWidth);
			percentageTable.setSpacingBefore(10f);
			pdfPCell = getFormatedCell(Font.BOLD, "Course/Degree/Education", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Per(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "I(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "II(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "III(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "IV(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "V(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "VI(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "VII(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "VIII(%)", 12, 10, Color.BLUE, Color.WHITE);
			percentageTable.addCell(pdfPCell);

			if (registration.getPercentageinfo().getHighSchoolPercent() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, "10th", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getHighSchoolPercent()), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
			}

			if (registration.getPercentageinfo().getHigherSecondarypercent() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, "12th", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getHigherSecondarypercent()), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
			}
			if (!"NA".equals(registration.getPersonalinfo().getDiploma())) {
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getDiploma(), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiplomaOthers()), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiploma1sem() != null
								? registration.getPercentageinfo().getDiploma1sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiploma2sem() != null
								? registration.getPercentageinfo().getDiploma2sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiploma3sem() != null
								? registration.getPercentageinfo().getDiploma3sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiploma4sem() != null
								? registration.getPercentageinfo().getDiploma4sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiploma5sem() != null
								? registration.getPercentageinfo().getDiploma5sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getDiploma6sem() != null
								? registration.getPercentageinfo().getDiploma6sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
			}

			if (registration.getPersonalinfo().getCourse() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getCourse(), 12, 10, Color.BLACK,
						Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBeAverege() != null
								? registration.getPercentageinfo().getBeAverege()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe1sem() != null
								? registration.getPercentageinfo().getBe1sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe2sem() != null
								? registration.getPercentageinfo().getBe2sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe3sem() != null
								? registration.getPercentageinfo().getBe3sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe4sem() != null
								? registration.getPercentageinfo().getBe4sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe5sem() != null
								? registration.getPercentageinfo().getBe5sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe6sem() != null
								? registration.getPercentageinfo().getBe6sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe7sem() != null
								? registration.getPercentageinfo().getBe7sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getBe8sem() != null
								? registration.getPercentageinfo().getBe8sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
			}

			if (registration.getPersonalinfo().getPostGraduationCourse() != null) {
				pdfPCell = getFormatedCell(Font.BOLD, registration.getPersonalinfo().getPostGraduationCourse(), 12, 10,
						Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getMeAverage() != null
								? registration.getPercentageinfo().getMeAverage()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getMeBsc1sem() != null
								? registration.getPercentageinfo().getMeBsc1sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getMeBsc2sem() != null
								? registration.getPercentageinfo().getMeBsc2sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getMeBsc3sem() != null
								? registration.getPercentageinfo().getMeBsc3sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD,
						String.valueOf(registration.getPercentageinfo().getMeBsc4sem() != null
								? registration.getPercentageinfo().getMeBsc4sem()
								: "NA"),
						12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
				pdfPCell = getFormatedCell(Font.BOLD, "NA", 12, 10, Color.BLACK, Color.WHITE);
				percentageTable.addCell(pdfPCell);
			}

			document.add(percentageTable);

			PdfPTable backTable = getPdfPTable(1);
			pdfPCell = getFormatedCell(Font.BOLD,
					"This is your Back Details Information(Last Update on "
							+ TpoUtil.getDateToString(registration.getBackdetails().getLastUpdated()) + ")",
					15, 10, Color.RED, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			backTable.addCell(pdfPCell);
			backTable.setSpacingBefore(10f);
			document.add(backTable);

			PdfPTable backDetailsTable = getPdfPTable(6);
			backDetailsTable.setWidthPercentage(pageWidth);
			backDetailsTable.setSpacingBefore(10f);
			pdfPCell = getFormatedCell(Font.BOLD, "Present Backlog", 12, 10, Color.BLUE, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Back Detail's", 12, 10, Color.BLUE, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Pass more then one attempt", 12, 10, Color.BLUE, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "No of Backlog's", 12, 10, Color.BLUE, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "BA Group(Year/Sem Back)", 12, 10, Color.BLUE, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Education Gap(Years)", 12, 10, Color.BLUE, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD,
					registration.getBackdetails().getBackLog() == 0 ? FbResourceUtil.getLabel("NO")
							: FbResourceUtil.getLabel("YES"),
					12, 10, Color.BLACK, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, registration.getBackdetails().getBackDetails(), 12, 10, Color.BLACK,
					Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD,
					registration.getBackdetails().getPassMoreThenOneAttempt() == 0 ? FbResourceUtil.getLabel("NO")
							: FbResourceUtil.getLabel("YES"),
					12, 10, Color.BLACK, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getBackdetails().getNumberOfBacklogs()),
					12, 10, Color.BLACK, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD,
					registration.getBackdetails().getBaGroup() == 0
							? FbResourceUtil.getLabel(FbResourceUtil.getLabel("NO"))
							: FbResourceUtil.getLabel("YES"),
					12, 10, Color.BLACK, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, String.valueOf(registration.getBackdetails().getEducationGap()), 12,
					10, Color.BLACK, Color.WHITE);
			backDetailsTable.addCell(pdfPCell);

			document.add(backDetailsTable);

			PdfPTable addressTable = getPdfPTable(1);
			pdfPCell = getFormatedCell(Font.BOLD, "This is your Address Information", 15, 10, Color.RED, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			addressTable.addCell(pdfPCell);
			addressTable.setSpacingBefore(10f);
			document.add(addressTable);

			PdfPTable addressInfoTable = getPdfPTable(6);
			addressInfoTable.setWidthPercentage(pageWidth);
			addressInfoTable.setSpacingBefore(10f);
			pdfPCell = getFormatedCell(Font.BOLD, "Phone Number", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPhoneNumber() == null ? "NA"
					: registration.getContactinfo().getPhoneNumber(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Mobile Number", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getMobileNumber() == null ? "NA"
					: registration.getContactinfo().getMobileNumber(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "", 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "", 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Present Address", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPresentAddress() == null ? "NA"
					: registration.getContactinfo().getPresentAddress(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Present City", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPresentCity() == null ? "NA"
					: registration.getContactinfo().getPresentCity(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Present State", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPresentState() == null ? "NA"
					: registration.getContactinfo().getPresentState(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Permanent Address", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPermanentAddress() == null ? "NA"
					: registration.getContactinfo().getPermanentAddress(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Permanent City", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPermanentCity() == null ? "NA"
					: registration.getContactinfo().getPermanentCity(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Permanent State", 12, 10, Color.BLUE, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, registration.getContactinfo().getPermanentState() == null ? "NA"
					: registration.getContactinfo().getPermanentState(), 12, 10, Color.BLACK, Color.WHITE);
			addressInfoTable.addCell(pdfPCell);

			document.add(addressInfoTable);

			PdfPTable Achivements = getPdfPTable(1);
			pdfPCell = getFormatedCell(Font.BOLD,
					"This is your Achivements Information(Last Update on "
							+ TpoUtil.getDateToString(registration.getAchivements().getLastUpdated()) + ")",
					15, 10, Color.RED, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			Achivements.addCell(pdfPCell);
			Achivements.setSpacingBefore(10f);
			document.add(Achivements);

			PdfPTable achivementsInfoTable = getPdfPTable(2);
			achivementsInfoTable.setWidths(new float[] { 20f, 80f });
			achivementsInfoTable.setWidthPercentage(pageWidth);
			achivementsInfoTable.setSpacingBefore(10f);
			pdfPCell = getFormatedCell(Font.BOLD, "Acedamic Achivements", 12, 10, Color.BLUE, Color.WHITE);
			achivementsInfoTable.addCell(pdfPCell);

			StyleSheet styles = null;
			/** Extra properties. */
			HashMap<String, Object> providers = null;

			List<Element> objects = HTMLWorker
					.parseToList(new StringReader(registration.getAchivements().getAcedamic() == null ? "NA"
							: registration.getAchivements().getAcedamic()), styles, providers);

			pdfPCell = getFormatedCell(Font.BOLD, "", 12, 10, Color.BLACK, Color.WHITE);
			if (objects != null && objects.size() > 0) {
				pdfPCell.addElement(objects.get(0));
			}
			achivementsInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Sports Achivements", 12, 10, Color.BLUE, Color.WHITE);
			achivementsInfoTable.addCell(pdfPCell);

			objects = HTMLWorker.parseToList(new StringReader(registration.getAchivements().getSports() == null ? "NA"
					: registration.getAchivements().getSports()), styles, providers);

			pdfPCell = getFormatedCell(Font.BOLD, "", 12, 10, Color.BLACK, Color.WHITE);
			if (objects != null && objects.size() > 0) {
				pdfPCell.addElement(objects.get(0));
			}
			achivementsInfoTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.BOLD, "Others Achivements", 12, 10, Color.BLUE, Color.WHITE);
			achivementsInfoTable.addCell(pdfPCell);

			objects = HTMLWorker.parseToList(new StringReader(registration.getAchivements().getOthers() == null ? "NA"
					: registration.getAchivements().getOthers()), styles, providers);

			pdfPCell = getFormatedCell(Font.BOLD, "", 12, 10, Color.BLACK, Color.WHITE);
			if (objects != null && objects.size() > 0) {
				pdfPCell.addElement(objects.get(0));
			}
			achivementsInfoTable.addCell(pdfPCell);

			document.add(achivementsInfoTable);

			PdfPTable declarationTable = getPdfPTable(1);
			declarationTable.setWidthPercentage(pageWidth);
			declarationTable.setSpacingBefore(20f);
			pdfPCell = getFormatedCell(Font.NORMAL,
					"Declarations: I hereby declare that the foregoing information is correct and complete to the best of my knowledge and belief and nothing has been concealed. If at any time, I am found to have concealed any material information or given any false details about myself, I understand that my appointment shall be liable to summary termination without any notice or compensation thereof to me.",
					12, 10, Color.BLACK, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			declarationTable.addCell(pdfPCell);
			document.add(declarationTable);

			PdfPTable pdfPTableSign = getPdfPTable(2);
			pdfPTableSign.setWidthPercentage(pageWidth);
			pdfPTableSign.setSpacingBefore(40f);
			pdfPCell = getFormatedCell(Font.BOLD, "Signature of Candidate", 15, 10, Color.BLACK, Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			pdfPTableSign.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.BOLD, "Signature & Seal of Authority T&P", 15, 10, Color.BLACK,
					Color.WHITE);
			pdfPCell.setBorderColor(Color.WHITE);
			pdfPTableSign.addCell(pdfPCell);
			document.add(pdfPTableSign);
			document.newPage();
			document.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return byteArrayOutputStream.toByteArray();

	}

	public void generateStudentList(List<Registration> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			addStudent(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "FB_" + AdminUser.getUser().getUserName());

	}

	public void generateHallTicketList(List<HallTicketConnect> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateHallticketListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(),
				"Hallticket_List_" + AdminUser.getUser().getUserName());

	}

	public void generateCollegeList(List<College> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateCollegeListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "College_List_" + AdminUser.getUser().getUserName());

	}

	public void generateNoticeList(List<Notice> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateNoticeListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "User_List_" + AdminUser.getUser().getUserName());

	}

	public void generateUserList(List<Logindetails> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateUserListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "User_List_" + AdminUser.getUser().getUserName());

	}

	public void generateOpenningList(List<HallTicket> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateOpenningListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(),
				"Openning_List_" + AdminUser.getUser().getUserName());

	}

	public void generateEmployeeEffortsList(List<EmployeeEfforts> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateEmployeeEffortsListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(),
				"Employee_Efforts_List_" + AdminUser.getUser().getUserName());

	}

	public void generateProjectList(List<Project> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateProjectListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "Project_List_" + AdminUser.getUser().getUserName());

	}

	public void generateModuleList(List<Module> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateModuleListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "Module_List_" + AdminUser.getUser().getUserName());

	}

	public void generateFeeList(List<StudentFeeDetails> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateFeeListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(),
				"StudentFeeList_" + AdminUser.getUser().getUserName());

	}

	public void generateReferralList(List<ReferralHistory> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			// pdfFilePath = PDFUtil.getContextFilePath();
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateReferralListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "ReferralList_" + AdminUser.getUser().getUserName());

	}

	public void generateCompanyList(List<Company> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateCompanyListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "Company_List_" + AdminUser.getUser().getUserName());

	}

	public void generateResultList(List<Result> list) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			Document.compress = false;
			document = new Document(PageSize._11X17, marginLeft, marginRight, marginTop, marginBottom);
			pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.addAuthor(CCPConstant.APP_NAME);
			document.open();
			addFbandClientLogo(AdminUser.getUser().getUserName());
			generateResultListPDF(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				closeDocument();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		TpoUtil.renderPDFFile(byteArrayOutputStream.toByteArray(), "ResultList_" + AdminUser.getUser().getUserName());

	}

	private void addFbandClientLogo(String userName)
			throws BadElementException, MalformedURLException, IOException, DocumentException {
		PdfPCell pdfPCell;
		PdfPTable clientAndFblogoTable = getPdfPTable(2);
		clientAndFblogoTable.setWidthPercentage(pageWidth);
		// Fb logo
		pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
		pdfPCell.setBorder(0);

		Image image;
		image = Image.getInstance(TpoUtil.getFBFileLogo());
		image.setBorder(0);
		image.setAlignment(Image.ALIGN_LEFT);
		image.setWidthPercentage(30f);
		pdfPCell.addElement(image);
		clientAndFblogoTable.addCell(pdfPCell);

		// Fb clientlog
		pdfPCell = getFormatedCell(Font.NORMAL, "", 15, 10, Color.BLUE, Color.WHITE);
		pdfPCell.setBorder(0);

		if (userName != null) {
			image = Image.getInstance(fileUploadUtility.downloadFile(getImageServiceUrl() + "/downloadImage", userName, IMAGECONS.userlogo));
		} else {
			image = Image.getInstance(TpoUtil.getFBFileLogo());
		}
		image.setBorder(0);
		image.setAlignment(Image.ALIGN_RIGHT);
		image.setWidthPercentage(30f);
		pdfPCell.addElement(image);
		clientAndFblogoTable.addCell(pdfPCell);

		document.add(clientAndFblogoTable);
	}

	private void addStudent(List<Registration> list) throws DocumentException, MalformedURLException, IOException {
		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Student list", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable billNumberTable = getPdfPTable(7);
		billNumberTable.setWidthPercentage(pageWidth);
		float[] columnWidths = new float[] { 6f, 23f, 10f, 20f, 15f, 20f, 10f };
		billNumberTable.setWidths(columnWidths);
		pdfPCell = getFormatedCell(Font.BOLD, "S-No", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "En.No", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Photo", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Name", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "College Name", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Selected In", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Passing", 12, 10, Color.BLACK, Color.WHITE);
		billNumberTable.addCell(pdfPCell);
		int i = 0;
		for (Registration registration : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			billNumberTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(registration.getRollnumber()), 10, 10, Color.BLUE,
					Color.WHITE);
			// pdfPCell.setTop(120f);
			// To print the barcode
			// pdfPCell.addElement(generateBarCode128(String.valueOf(registration.getRollnumber())));
			// pdfPCell.addElement(generateBarCode128(String.valueOf(registration.getRollnumber())));
			billNumberTable.addCell(pdfPCell);

			byte[] b = null;
			CommonDBBean commonDBBean = (CommonDBBean) TpoUtil.getManagedBean(CommonDBBean.class.getSimpleName());
			if (commonDBBean != null) {
				b = commonDBBean.getStudentProfilePic(registration);
				if (b == null) {
					b = TpoUtil.convertInputStreamToBytesArray(TpoUtil.getNAFile());
				}
			}
			pdfPCell = getFormatedCell(Font.NORMAL, "", 10, 10, Color.BLUE, Color.WHITE);
			Image image = Image.getInstance(b);
			image.setAlt(registration.getFirstName() + registration.getLastName());
			image.setWidthPercentage(70f);

			pdfPCell.addElement(image);
			billNumberTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.NORMAL, registration.getFirstName() + " " + registration.getLastName(), 10,
					10, Color.BLUE, Color.WHITE);
			billNumberTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.NORMAL,
					registration.getCollegeName() == null ? "" : registration.getCollegeName(), 10, 10, Color.BLUE,
					Color.WHITE);
			billNumberTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.NORMAL, registration.getPersonalinfo().getCompanyName() == null ? ""
					: registration.getPersonalinfo().getCompanyName(), 10, 10, Color.BLUE, Color.WHITE);
			billNumberTable.addCell(pdfPCell);
			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(registration.getPersonalinfo().getYearOfPassing() == null ? ""
							: registration.getPersonalinfo().getYearOfPassing()),
					10, 10, Color.BLUE, Color.WHITE);
			billNumberTable.addCell(pdfPCell);
		}
		document.add(billNumberTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateHallticketListPDF(List<HallTicketConnect> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Current Hallticket List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(7);
		float[] columnWidths = new float[] { 3f, 20f, 12f, 40f, 3f, 3f, 10f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Enrollment No", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "HallTicket ID", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Company Name", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Applied", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Approved", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Company Status", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);

		int i = 0;
		for (HallTicketConnect hallticket : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, hallticket.getId().getRollnumber(), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(hallticket.getId().getHallTicket().getHallTicketId()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(hallticket.getId().getHallTicket().getCompanyName()),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					hallticket.getIsApplied() == true ? FbResourceUtil.getLabel("YES") : FbResourceUtil.getLabel("NO"),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					hallticket.getIsApproved() == true ? FbResourceUtil.getLabel("YES") : FbResourceUtil.getLabel("NO"),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					hallticket.getId().getHallTicket().getIsActive() == true ? FbResourceUtil.getLabel("Active")
							: FbResourceUtil.getLabel("InActive"),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateUserListPDF(List<Logindetails> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "User List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(9);
		float[] columnWidths = new float[] { 3f, 10f, 3f, 15f, 15f, 12f, 20f, 12f, 30f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "User Name", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Role", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Status", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Last Login", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Valid Till", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Address", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Mobile", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Email", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (Logindetails login : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(login.getUserName()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, login.getRole(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(login.getActive() == true ? FbResourceUtil.getLabel("Active")
							: FbResourceUtil.getLabel(FbResourceUtil.getLabel("InActive"))),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(login.getLastLogin()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyy(login.getValidTill())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, login.getUserdetails().getAddress(), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(login.getUserdetails().getMobleNo()), 10, 10,
					Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, login.getUserdetails().getEmail(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);
		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateNoticeListPDF(List<Notice> list) throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Notice List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(4);
		float[] columnWidths = new float[] { 3f, 40f, 3f, 10f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Notice Name", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Status", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Expiry Date", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (Notice notice : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(notice.getNoticeName()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					notice.getActive() == true ? FbResourceUtil.getLabel("Active")
							: FbResourceUtil.getLabel(FbResourceUtil.getLabel("InActive")),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(notice.getExpiryDate()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);
		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateCollegeListPDF(List<College> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Current College List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(9);
		float[] columnWidths = new float[] { 3f, 10f, 20f, 10f, 12f, 10f, 20f, 10f, 30f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "College Code", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("College_Name"), 12, 10, Color.BLACK,
				Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Place", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Univercity", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Date of Openning", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "E-mail Address", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Website", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Address", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (College college : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(college.getCollegeName()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, college.getCollegeFullName(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(college.getPlace()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, college.getUniversity(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyy(college.getDateOfOpening())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, college.getEmailAddress(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, college.getSiteAddress(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, college.getAddress(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);
		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateOpenningListPDF(List<HallTicket> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Current Openning List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(7);
		float[] columnWidths = new float[] { 3f, 10f, 40f, 12f, 7f, 12f, 6f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Company ID", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Company Name", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Date of Visit", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Time", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Package Offering", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Status", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (HallTicket openning : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(openning.getHallTicketId()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, openning.getCompanyName(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyy(openning.getDate())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, openning.getTime(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(openning.getPackageOffering()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, openning.getIsActive() == true ? FbResourceUtil.getLabel("Active")
					: FbResourceUtil.getLabel("InActive"), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);
		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateProjectListPDF(List<Project> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Projects", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(5);
		float[] columnWidths = new float[] { 3f, 20f, 10f, 30f, 5f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("User_Name"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Project_ID"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("project_name"), 12, 10, Color.BLACK,
				Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Status"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);

		int i = 0;
		for (Project project : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, project.getCreatedBy(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(project.getProjectid()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(project.getProjectname()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			if (project.getStatus() != null) {
				pdfPCell = getFormatedCell(Font.NORMAL, project.getStatus() == 1 ? FbResourceUtil.getLabel("Active")
						: FbResourceUtil.getLabel("InActive"), 10, 10, Color.BLUE, Color.WHITE);
			} else {
				pdfPCell = getFormatedCell(Font.NORMAL, "NA", 10, 10, Color.BLUE, Color.WHITE);

			}
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateModuleListPDF(List<Module> list) throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Modules", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(7);
		float[] columnWidths = new float[] { 3f, 20f, 10f, 30f, 10f, 30f, 5f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("User_Name"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Project_ID"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("project_name"), 12, 10, Color.BLACK,
				Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Module_id"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Module_Name"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Status"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);

		int i = 0;
		for (Module module : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, module.getCreatedBy(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(module.getProject().getProjectid()), 10, 10,
					Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(module.getProject().getProjectname()), 10, 10,
					Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(module.getModuleid()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(module.getModulename()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			if (module.getStatus() != null) {
				pdfPCell = getFormatedCell(Font.NORMAL, module.getStatus() == 1 ? FbResourceUtil.getLabel("Active")
						: FbResourceUtil.getLabel("InActive"), 10, 10, Color.BLUE, Color.WHITE);
			} else {
				pdfPCell = getFormatedCell(Font.NORMAL, "NA", 10, 10, Color.BLUE, Color.WHITE);

			}
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateEmployeeEffortsListPDF(List<EmployeeEfforts> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Employee Efforts", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(10);
		float[] columnWidths = new float[] { 3f, 20f, 2f, 9f, 6f, 6f, 20f, 2f, 20f, 4f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("User_Name"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("effortid"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Effort_Date"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Project_ID"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Module_id"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Description"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Time"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Remarks"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Leave_Day"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (EmployeeEfforts effort : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, effort.getLogindetails().getUserName(), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(effort.getEffortid()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyy(effort.getSdate())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(effort.getProject() == null ? "" : effort.getProject()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(effort.getModule() == null ? "" : effort.getModule()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(effort.getDescription() == null ? "" : effort.getDescription()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(effort.getTime() == null ? "" : effort.getTime()),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(effort.getRemarks()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			if (effort.getLeaveday() != null && !effort.getLeaveday().isEmpty()) {
				pdfPCell = getFormatedCell(Font.NORMAL,
						String.valueOf(effort.getLeaveday() == "F" ? FbResourceUtil.getLabel("FullDay")
								: FbResourceUtil.getLabel("HalfDay")),
						10, 10, Color.BLUE, Color.WHITE);
			} else {
				pdfPCell = getFormatedCell(Font.NORMAL, "NA", 10, 10, Color.BLUE, Color.WHITE);

			}
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateReferralListPDF(List<ReferralHistory> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("referralList"), 20, 10, Color.BLACK,
				Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(4);
		float[] columnWidths = new float[] { 3f, 10f, 10f, 6f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("referred"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("referredBY"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Date"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (ReferralHistory detail : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, detail.getReferred(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, detail.getReferredBY(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyyHHmmSS(detail.getDate())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);
		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateFeeListPDF(List<StudentFeeDetails> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Student Fee List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(7);
		float[] columnWidths = new float[] { 3f, 10f, 6f, 6f, 6f, 6f, 6f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Enrollment_No"), 12, 10, Color.BLACK,
				Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Amount_Paid"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Amount_Paid_On"), 12, 10, Color.BLACK,
				Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Amount_Due"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Amount_Due_On"), 12, 10, Color.BLACK,
				Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("Reminder_On"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (StudentFeeDetails detail : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, detail.getRollNumber(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(detail.getAmountPaid()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyy(detail.getPaidOn())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);
			boolean flag;
			if (detail.getAmountDue() > 0
					&& (detail.getDueOn() != null && detail.getDueOn().before(Calendar.getInstance().getTime()))) {
				pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(detail.getAmountDue()), 10, 10, Color.RED,
						Color.WHITE);
				flag = true;
			} else {
				pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(detail.getAmountDue()), 10, 10, Color.BLUE,
						Color.WHITE);
				flag = false;
			}
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(
							TpoUtil.getDateToStringInddmmyyyy(detail.getDueOn() != null ? detail.getDueOn() : null)),
					10, 10, flag ? Color.RED : Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					detail.getReminderOn() != null && detail.getReminderOn() ? FbResourceUtil.getLabel("YES")
							: FbResourceUtil.getLabel("NO"),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private void generateCompanyListPDF(List<Company> list)
			throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Company List", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(16);
		float[] columnWidths = new float[] { 2f, 10f, 5f, 5f, 15f, 4f, 15f, 4f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f };
		mainTable.setWidths(columnWidths);
		mainTable.setSpacingBefore(20f);
		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, FbResourceUtil.getLabel("SNo"), 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Company Name", 12, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Date of Visit", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "E-mail", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Package", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "ID", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Profile", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Daomain", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Website", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "LinkedIn", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Twiter", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Glass Door", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Facebook", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "TOTAL", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "REMARK", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Created BY", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);

		int i = 0;
		for (Company company : list) {
			if (company.getCompanyID() == 1) {
				continue;
			}
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, company.getCompanyname(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(TpoUtil.getDateToStringInddmmyyyy(company.getDateofvisit())), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, company.getEmail(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(company.getPackageOffering()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getCompanyID() == null ? 0 : company.getCompanyID()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getProfile() == null ? "" : company.getProfile()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getDomain() == null ? "" : company.getDomain()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getWebsite() == null ? "" : company.getWebsite()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getLinkedIn() == null ? "" : company.getLinkedIn()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getTwiter() == null ? "" : company.getTwiter()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getGlassdoor() == null ? "" : company.getGlassdoor()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getFacebook() == null ? "" : company.getFacebook()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(company.getTotal() == null ? 0 : company.getTotal()),
					10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, company.getRemarks(), 10, 5, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL,
					String.valueOf(company.getCreatedBy() == null ? "" : company.getCreatedBy()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private PdfPTable addSignature(PdfPTable pdfPTable) {
		PdfPCell pdfPCell;
		PdfPTable pdfPTableSign = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		pdfPTableSign.setSpacingBefore(40f);
		pdfPCell = getFormatedCell(Font.BOLD, "Authorized Signature", 20, 10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTableSign.addCell(pdfPCell);
		return pdfPTableSign;
	}

	private void generateResultListPDF(List<Result> list) throws DocumentException, MalformedURLException, IOException {

		PdfPTable pdfPTable = getPdfPTable(2);
		pdfPTable.setWidthPercentage(pageWidth);
		PdfPCell pdfPCell = getFormatedCell(Font.BOLD, "Result list for Exam name = " + list.get(0).getTestName(), 20,
				10, Color.BLACK, Color.WHITE);
		pdfPCell.setColspan(2);
		pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfPCell.setTop(20f);
		pdfPCell.setLeft(200f);
		pdfPCell.setBorderColor(Color.WHITE);
		pdfPTable.addCell(pdfPCell);
		document.add(pdfPTable);

		PdfPTable mainTable = getPdfPTable(10);
		float[] columnWidths = new float[] { 4f, 15f, 4f, 10f, 5f, 15f, 20f, 5f, 5f, 5f };
		mainTable.setWidths(columnWidths);

		mainTable.setWidthPercentage(pageWidth);
		pdfPCell = getFormatedCell(Font.BOLD, "S-No.", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "E-No.", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Attempt", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Test Name", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "%/No.", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Result", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Date of Exam", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Time Was(MM:SS)", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "Time Taken(MM:SS)", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		pdfPCell = getFormatedCell(Font.BOLD, "No of Questions", 12, 10, Color.BLACK, Color.WHITE);
		mainTable.addCell(pdfPCell);
		int i = 0;
		for (Result result : list) {
			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(++i), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, result.getId().getLoginname(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(result.getId().getAttempt()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, result.getTestName(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(result.getTotalnumbers()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, result.getResult(), 10, 10, Color.BLUE, Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(result.getDateTaken()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(result.getTotalTime()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(result.getTotalTimeTaken()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

			pdfPCell = getFormatedCell(Font.NORMAL, String.valueOf(result.getNumberOfQuestion()), 10, 10, Color.BLUE,
					Color.WHITE);
			mainTable.addCell(pdfPCell);

		}
		document.add(mainTable);

		PdfPTable pdfPTableSign = addSignature(pdfPTable);
		document.add(pdfPTableSign);

		document.close();

		// pdfPTableSign = null;
		pdfPTable = null;
	}

	private Image generateBarCode128(String text) {
		PdfContentByte cb = pdfWriter.getDirectContent();
		Barcode128 code128 = new Barcode128();
		code128.setCode(text);
		Image image = code128.createImageWithBarcode(cb, null, null);
		// phrase = new Phrase(new Chunk(image, 0, 0));
		return image;
	}

	private void closeDocument() throws DocumentException {
		if (document != null) {
			if (document.isOpen()) {
				try {
					document.close();
					document = null;
				} catch (Exception e) {
					throw new DocumentException("Error while closing the HangTag PDF." + e.getMessage());
				}
			}
		}
	}

	private PdfPTable getPdfPTable(int colSpan) {
		PdfPTable pdfPTable = new PdfPTable(colSpan);
		return pdfPTable;
	}

	private PdfPCell getFormatedCell(int fostStyle, String text, int size, int height, Color textColor,
			Color cellColor) {
		if (text == null || "".equals(text) || "null".equals(text)) {
			text = new String("");
			textColor = Color.WHITE;
		}
		FontSelector sel = new FontSelector();

		BaseFont bf = null;
		try {
			bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		} catch (DocumentException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		Font font = new Font(bf, height);
		font.setColor(textColor);
		font.setSize(size);
		sel.addFont(font);
		Phrase phrase = sel.process(text);
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.addElement(phrase);
		pdfPCell.setBackgroundColor(cellColor);
		return pdfPCell;
	}

}
