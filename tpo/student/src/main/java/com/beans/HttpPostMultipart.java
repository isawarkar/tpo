package com.beans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.util.TpoUtil;

@Component("httpPostMultipart")
public class HttpPostMultipart {
	private final String boundary = UUID.randomUUID().toString();
	private static final String LINE = "\r\n";
	private HttpURLConnection httpConn;
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;

	/**
	 * This constructor initializes a new HTTP POST request with content type is set
	 * to multipart/form-data
	 *
	 * @param requestURL
	 * @param charset
	 * @param headers
	 * @throws IOException
	 */
	public void setConnection(String requestURL) throws IOException {
		this.charset = "utf-8";
		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);
		httpConn.setRequestMethod("POST");
		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		if (headers != null && headers.size() > 0) {
			Iterator<String> it = headers.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headers.get(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
	}

	/**
	 * This constructor initializes a new HTTP POST request with content type is set
	 * to multipart/form-data
	 *
	 * @param requestURL
	 * @param charset
	 * @param headers
	 * @throws IOException
	 */
	public void setGETConnection(String requestURL) throws IOException {
		this.charset = "utf-8";
		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);
		httpConn.setRequestMethod("GET");
		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		if (headers != null && headers.size() > 0) {
			Iterator<String> it = headers.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headers.get(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
	}

	

	/**
	 * This constructor initializes a new HTTP POST request with content type is set
	 * to multipart/form-data
	 *
	 * @param requestURL
	 * @param charset
	 * @param headers
	 * @throws IOException
	 */
	public void setDeleteConnection(String requestURL) throws IOException {
		this.charset = "utf-8";
		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);
		httpConn.setRequestMethod("DELETE");
		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		if (headers != null && headers.size() > 0) {
			Iterator<String> it = headers.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = headers.get(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
	}

	/**
	 * Adds a form field to the request
	 *
	 * @param name  field name
	 * @param value field value
	 */
	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE);
		writer.append("Content-Type: text/plain; charset=" + charset).append(LINE);
		writer.append(LINE);
		writer.append(value).append(LINE);
		writer.flush();
	}

	/**
	 * Adds a upload file section to the request
	 *
	 * @param fieldName
	 * @param uploadFile
	 * @throws IOException
	 */
	public void addFilePart(String fieldName, File uploadFile, String fileName) throws IOException {
		writer.append("--" + boundary).append(LINE);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
				.append(LINE);
		writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
		writer.append("Content-Transfer-Encoding: binary").append(LINE);
		writer.append(LINE);
		writer.flush();

		FileInputStream inputStream = new FileInputStream(uploadFile);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();
		writer.append(LINE);
		writer.flush();
	}

	/**
	 * Adds a upload file section to the request
	 *
	 * @param fieldName
	 * @param uploadFile
	 * @throws IOException
	 */
	public void addFilePart(String fieldName, byte[] fileByteArray, String fileName) throws IOException {
		writer.append("--" + boundary).append(LINE);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"")
				.append(LINE);
		writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
		writer.append("Content-Transfer-Encoding: binary").append(LINE);
		writer.append(LINE);
		writer.flush();

		/*
		 * FileInputStream inputStream = new FileInputStream(uploadFile); byte[] buffer
		 * = new byte[4096]; int bytesRead = -1; while ((bytesRead =
		 * inputStream.read(buffer)) != -1) { outputStream.write(buffer, 0, bytesRead);
		 * }
		 */
		outputStream.write(fileByteArray);
		outputStream.flush();
		// inputStream.close();
		writer.append(LINE);
		writer.flush();
	}

	/**
	 * Completes the request and receives response from the server.
	 *
	 * @return String as response in case the server returned status OK, otherwise
	 *         an exception is thrown.
	 * @throws IOException
	 */
	public String finish() throws IOException {
		String response = null;
		writer.flush();
		writer.append("--" + boundary + "--").append(LINE);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = httpConn.getInputStream().read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			response = result.toString(this.charset);
			httpConn.disconnect();
		} else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
			response = null;
			httpConn.disconnect();
		} else {
			response = null;
			httpConn.disconnect();
		}
		return response;
	}

	/**
	 * Completes the request and receives response from the server.
	 *
	 * @return String as response in case the server returned status OK, otherwise
	 *         an exception is thrown.
	 * @throws IOException
	 */
	public byte[] download() throws IOException {
		byte[] buffer;
		writer.flush();
		writer.append("--" + boundary + "--").append(LINE);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			InputStream inputStream = httpConn.getInputStream();
			buffer = TpoUtil.convertInputStreamToBytesArray(inputStream);
			httpConn.disconnect();
		} else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
			buffer = null;
			httpConn.disconnect();
		} else {
			buffer = null;
			httpConn.disconnect();
		}
		return buffer;
	}

	/**
	 * Completes the request and receives response from the server.
	 *
	 * @return String as response in case the server returned status OK, otherwise
	 *         an exception is thrown.
	 * @throws IOException
	 */
	public String delete() throws IOException {
		writer.flush();
		writer.append("--" + boundary + "--").append(LINE);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			httpConn.disconnect();
			return "deleted";
		} else {
			httpConn.disconnect();
			return "";
		}
	}

	public boolean isFileExist() throws IOException {
		boolean flag = false;
		writer.flush();
		writer.append("--" + boundary + "--").append(LINE);
		writer.close();
		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK) {
			httpConn.disconnect();
			flag = true;
		} else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
			flag = false;
			httpConn.disconnect();
		} else {
			httpConn.disconnect();
			flag = false;
		}
		return flag;
	}
}