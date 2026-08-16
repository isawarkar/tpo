package tpo.imageservice.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tpo.util.IMAGECONS;

@Component("fileUploadUtility")
public class FileUploadUtility {

	@Autowired
	private HttpPostMultipart httpPostMultipart;

	/*
	 * public String uploadFileWithByteArray(String url, String fileName, byte[]
	 * fileByteArray, IMAGECONS location) { try { // Set header Map<String, String>
	 * headers = new HashMap<>(); headers.put("User-Agent",
	 * "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36"
	 * );
	 * 
	 * httpPostMultipart.setConnection(url); // Add form field
	 * 
	 * httpPostMultipart.addFormField("fileName", fileName);
	 * httpPostMultipart.addFormField("location", location.toString());
	 * 
	 * // Add file httpPostMultipart.addFilePart("file", fileByteArray, fileName);
	 * // Print result String response = httpPostMultipart.finish(); return
	 * response; } catch (Exception e) { e.printStackTrace(); } return null; }
	 */

	public String uploadFileWithByteArray(String url, String fileName, byte[] fileByteArray, IMAGECONS location) {

		String boundary = "----JavaBoundary" + System.currentTimeMillis();

		try {
			URL uploadUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);

			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) "
					+ "AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/79.0.3945.88 Safari/537.36");

			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

			try (OutputStream output = connection.getOutputStream();
					PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8),
							true)) {

				// fileName field
				writer.append("--").append(boundary).append("\r\n");
				writer.append("Content-Disposition: form-data; name=\"fileName\"\r\n");
				writer.append("Content-Type: text/plain; charset=UTF-8\r\n");
				writer.append("\r\n");
				writer.append(fileName).append("\r\n");

				// location field
				writer.append("--").append(boundary).append("\r\n");
				writer.append("Content-Disposition: form-data; name=\"location\"\r\n");
				writer.append("Content-Type: text/plain; charset=UTF-8\r\n");
				writer.append("\r\n");
				writer.append(location != null ? location.toString() : "").append("\r\n");

				// file field
				writer.append("--").append(boundary).append("\r\n");
				writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName)
						.append("\"\r\n");
				writer.append("Content-Type: application/octet-stream\r\n");
				writer.append("\r\n");
				writer.flush();

				output.write(fileByteArray);
				output.flush();

				writer.append("\r\n");
				writer.append("--").append(boundary).append("--\r\n");
				writer.flush();
			}

			int responseCode = connection.getResponseCode();

			InputStream responseStream = responseCode >= 200 && responseCode < 300 ? connection.getInputStream()
					: connection.getErrorStream();

			String response;

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {

				StringBuilder result = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				response = result.toString();
			}

			System.out.println("HTTP Response Code: " + responseCode);
			System.out.println("Response: " + response);

			return response;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String uploadFileWithByteArrayWithExt(String url, String fileName, byte[] fileByteArray, String location) {
		try {
			// Set header
			Map<String, String> headers = new HashMap<>();
			headers.put("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

			httpPostMultipart.setConnection(url);
			// Add form field

			httpPostMultipart.addFormField("fileName", fileName);
			httpPostMultipart.addFormField("location", location);

			// Add file
			httpPostMultipart.addFilePart("file", fileByteArray, fileName);
			// Print result
			String response = httpPostMultipart.finish();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] downloadFile(String url, String fileName, IMAGECONS location) {
		try {
			// Set URL
			httpPostMultipart.setGETConnection(url + "/" + location + "/" + fileName);
			// Add form field

			// Print result
			return httpPostMultipart.download();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public byte[] downloadFileWithParam(String url, String fileName, String location) {
		try {
			// Set URL
			httpPostMultipart.setGETConnection(url);
			// Add form field

			httpPostMultipart.addFormField("location", location);
			httpPostMultipart.addFormField("fileName", fileName);

			// Print result
			return httpPostMultipart.download();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String deleteFileWithParam(String url, String fileName, String location) {
		try {
			// Set URL
			httpPostMultipart.setDeleteConnection(url);
			httpPostMultipart.addFormField("location", location);
			httpPostMultipart.addFormField("fileName", fileName);
			return httpPostMultipart.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String deleteFolder(String url, String name) {
		try {
			// Set URL
			httpPostMultipart.setDeleteConnection(url);
			httpPostMultipart.addFormField("name", name);
			return httpPostMultipart.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean isFileExist(String url, String fileName, String location) {
		try {
			// Set URL
			httpPostMultipart.setGETConnection(url);
			httpPostMultipart.addFormField("location", location);
			httpPostMultipart.addFormField("fileName", fileName);
			return httpPostMultipart.isFileExist();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
