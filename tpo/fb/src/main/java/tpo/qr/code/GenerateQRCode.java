package tpo.qr.code;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import images.R;

public class GenerateQRCode {

	private static GenerateQRCode generator;


	private GenerateQRCode() {
		//
	}

	public static GenerateQRCode getInstance() {
		if (generator == null) {
			generator = new GenerateQRCode();
		}
		return generator;
	}

	public byte[] createQRImageNOtWorking(String qrCodeText, int size, String fileType) throws WriterException, IOException {
		byte[] imageInByte = null;
		try {
			// Create new configuration that specifies the error correction
			Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix bitMatrix = null;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bitMatrix = writer.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hints);

			// Load QR image
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, getMatrixConfig());

			// Initialize combined image
			BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) combined.getGraphics();

			// Write QR code to new image at position 0/0
			g.drawImage(qrImage, 0, 0, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			addLogoInside(qrImage, g);

			// Write combined image as PNG to OutputStream
			ImageIO.write(combined, fileType, os);
			imageInByte = os.toByteArray();
			Files.copy(new ByteArrayInputStream(imageInByte), Paths.get("D:\\MYQR.png"),StandardCopyOption.REPLACE_EXISTING);

			// os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageInByte;
	}

	
	public byte[] createQRImage(String qrCodeText, int size, String fileType)
			throws WriterException, IOException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage qrImage = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		qrImage.createGraphics();

		Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.RED);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		addLogoInside(qrImage, graphics);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(qrImage, fileType, baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		imageInByte =baos.toByteArray();
		/*
		 * Files.copy(new ByteArrayInputStream(imageInByte), Paths.get("D:\\MYQR1.png"),
		 * StandardCopyOption.REPLACE_EXISTING);
		 * 
		 */		baos.close();
		return imageInByte;
	}

	public File createQRImage(String qrCodeText, int size, String fileType, String fileName)
			throws WriterException, IOException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		File tempFile = File.createTempFile(fileName, "." + "png");
		ImageIO.write(image, "png", tempFile);
		return tempFile;
	}

	private MatrixToImageConfig getMatrixConfig() {
		// ARGB Colors
		// Check Colors ENUM
		return new MatrixToImageConfig(GenerateQRCode.Colors.WHITE.getArgb(), GenerateQRCode.Colors.ORANGE.getArgb());
	}

	public enum Colors {

		BLUE(0xFF40BAD0), RED(0xFFE91C43), PURPLE(0xFF8A4F9E), ORANGE(0xFFF4B13D), WHITE(0xFFFFFFFF), BLACK(0xFF000000);

		private final int argb;

		Colors(final int argb) {
			this.argb = argb;
		}

		public int getArgb() {
			return argb;
		}
	}
	
	private void addLogoInside(BufferedImage qrImage, Graphics2D graphics) throws IOException {
		// Load logo image
		String fileName = R.class.getResource("fb.png").getFile();
		fileName = fileName.replaceAll("%20", " ");
		File fb = new File(fileName);
		BufferedImage overly = ImageIO.read(fb);

		// Calculate the delta height and width between QR code and logo
		int deltaHeight = qrImage.getHeight() - overly.getHeight();
		int deltaWidth = qrImage.getWidth() - overly.getWidth();
		graphics.drawImage(overly, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
	}

}