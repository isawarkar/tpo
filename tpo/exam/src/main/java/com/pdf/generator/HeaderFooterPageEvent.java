package com.pdf.generator;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
public class HeaderFooterPageEvent extends PdfPageEventHelper {
    public void onStartPage(PdfWriter writer,Document document) {
    	//Rectangle rect = writer.getBoxSize("art");
        //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Left"), rect.getLeft(), rect.getTop(), 0);
        //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Right"), rect.getRight(), rect.getTop(), 0);
    }
    public void onEndPage(PdfWriter writer,Document document) {
    	Rectangle rect = writer.getBoxSize("art");
       // ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Bottom Left"), rect.getLeft(), rect.getBottom(), 0);
       // ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_RIGHT, new Phrase("Note:This certificate is not valid without signature & Seal"), rect.getRight(), rect.getBottom(), 0);
    
    }
} 