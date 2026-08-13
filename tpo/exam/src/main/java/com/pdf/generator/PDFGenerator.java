package com.pdf.generator;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.beans.ResultBean;
import com.hibernate.Exam;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.util.CCPConstant;
import com.util.TpoUtil;



/**
 * 
 * This class implements HangTagGenerator interface and thus is mainly
 * responsible for creating and generating hang tag in PDF form.
 * 
 */
@Component("pDFGenerator")
@Scope("session")
public class PDFGenerator{

	private Logger logger = LoggerFactory.getLogger(PDFGenerator.class);

	private Document document;

	private PdfWriter pdfWriter;

	private final float marginLeft = 3f;

	private final float marginRight = 3f;

	private final float marginTop = 10f;

	private final float marginBottom = 3f;

	
	public byte[] generateCertificate(ResultBean bean) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
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
					WordUtils.capitalize(bean.getFirstName()) + " "
							+ WordUtils.capitalize(bean.getLastName()),
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

}
