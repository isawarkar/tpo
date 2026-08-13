package com.beans;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.util.TpoUtil;

@Repository("ImageBean")
@Scope("session")
public class ImageBean {

	private boolean isImageCorrect = false;
	
	private String verificationNumber;

	public boolean isImageCorrect() {
		return isImageCorrect;
	}

	public void setImageCorrect(boolean isImageCorrect) {
		this.isImageCorrect = isImageCorrect;
	}
	
	public RenderedImage getTextAsImage() {
		if (verificationNumber == null) {
			verificationNumber = TpoUtil.get6DigitRandomNumber().toString();
		}
		Color textColor = Color.DARK_GRAY;
		Color glowColor = Color.ORANGE;
		int fontSize = 15;
		String fontName = "Tahoma";
		int fontStyle = Font.PLAIN;
		int glowWidth = 1;
		float paddingSize = 2;
		Font font = new Font(fontName, fontStyle, fontSize);

		AffineTransform transform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(transform, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, verificationNumber);
		LineMetrics lineMetrics = font.getLineMetrics(verificationNumber, frc);
		Rectangle2D logicalBounds = glyphVector.getLogicalBounds();

		int imageWidth = (int) Math.ceil(logicalBounds.getWidth() + glowWidth * 2 + paddingSize * 2);
		int imageHeight = (int) Math.ceil(logicalBounds.getHeight() + glowWidth * 2 + paddingSize * 2);
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setPaint(Color.WHITE);
		graphics.fill(new Rectangle2D.Float(0, 0, imageWidth, imageHeight));

		float x = glowWidth + paddingSize;
		float y = glowWidth + paddingSize + lineMetrics.getAscent();
		Shape textShape = glyphVector.getOutline(x, y);

		float glowR = 255 - glowColor.getRed();
		float glowG = 255 - glowColor.getGreen();
		float glowB = 255 - glowColor.getBlue();

		float maxStrokeWidth = glowWidth * 2;
		for (int gradationCount = (int) Math.ceil(glowWidth / 1.5), i = 1; i <= gradationCount; i++) {
			float saturation = ((float) i) / gradationCount;
			float r = 255 - saturation * glowR;
			float g = 255 - saturation * glowG;
			float b = 255 - saturation * glowB;
			Color currentGlowColor = new Color((int) r, (int) g, (int) b);
			float strokeWidth = maxStrokeWidth - maxStrokeWidth / gradationCount * (i - 1);
			Stroke stroke = new BasicStroke(strokeWidth, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_ROUND);
			Shape textOutlineShape = stroke.createStrokedShape(textShape);
			graphics.setStroke(stroke);
			graphics.setPaint(currentGlowColor);
			graphics.fill(textOutlineShape);
		}

		graphics.setPaint(textColor);
		graphics.fill(textShape);

		return image;
	}

	public void refreshImage() {
		verificationNumber = null;
	}

	public String getVerificationNumber() {
		return verificationNumber;
	}

	public void setVerificationNumber(String verificationNumber) {
		this.verificationNumber = verificationNumber;
	}


}
