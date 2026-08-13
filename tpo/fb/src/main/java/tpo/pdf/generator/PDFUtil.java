package tpo.pdf.generator;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.FontSelector;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * 
 * This utility class has functionalities like populating, formatting the PDF
 * document generated for hang tag.
 * 
 */
public class PDFUtil {

	private static Phrase phrase = null;

	/**
	 * Formats the PdfPCell with the text and the text attributes. Creates a
	 * Child PdfPCell
	 * 
	 * /** To create PDFP Table
	 * 
	 * @param colspan
	 * @param widthPercentage
	 * @return PdfPTable
	 */
	public static PdfPTable createPdfPTable(int colspan, float widthPercentage) {
		PdfPTable newPdfPTable = new PdfPTable(colspan);
		newPdfPTable.setWidthPercentage(widthPercentage);
		newPdfPTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		return newPdfPTable;
	}

	/**
	 * To format the font
	 * 
	 * @param text
	 * @param height
	 * @param color
	 * @return
	 */
	public static Phrase formatFont(String text, int height, Color color,
			int fontType) {
		if (text == null || text == "") {
			text = new String("");
			color = Color.WHITE;
		}
		text = " " + text + " ";
		FontSelector sel = new FontSelector();
		Font font = new Font(fontType, height);
		font.setColor(color);
		font.setSize(10);
		sel.addFont(font);
		Phrase ph = sel.process(text);
		return ph;
	}

	/**
	 * To create Blank PDF Cell
	 * 
	 * @param colspan
	 * @param backGroundColor
	 * @return
	 */
	public static PdfPCell createBlankPdfPCell(int colspan,
			Color backGroundColor) {
		PdfPCell newPdfPCell = new PdfPCell();
		newPdfPCell.setColspan(colspan);
		newPdfPCell.setBackgroundColor(backGroundColor);
		newPdfPCell.setBorderColor(backGroundColor);
		newPdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		newPdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return newPdfPCell;
	}

	/**
	 * To create an individual cell
	 * 
	 * @param phrase
	 * @param backGroundColor
	 * @param horizAllign
	 * @param colSpan
	 * @return
	 * @throws BadElementException
	 */
	public static PdfPCell createCell(Phrase phrase, Color backGroundColor,
			int horizAllign, int colSpan) throws BadElementException {
		PdfPCell pdfpCell;
		pdfpCell = new PdfPCell(phrase);
		pdfpCell.setHorizontalAlignment(horizAllign);
		pdfpCell.setVerticalAlignment(Element.ALIGN_CENTER);
		pdfpCell.setBackgroundColor(backGroundColor);
		pdfpCell.setColspan(colSpan);
		pdfpCell.setBorderColor(backGroundColor);

		return pdfpCell;
	}

	/**
	 * To get the current Date
	 * 
	 * @param dateFormat
	 * @return
	 */
	public static String getCurrentDate(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(d);
	}
	
}
