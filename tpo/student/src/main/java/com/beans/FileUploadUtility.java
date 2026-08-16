package com.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.util.IMAGECONS;

@Component("fileUploadUtility")
public class FileUploadUtility {

	@Autowired
	HttpPostMultipart httpPostMultipart;

	public String uploadFileWithByteArray(String url, String fileName, byte[] fileByteArray, IMAGECONS location) {
		try {
			// Set header
			Map<String, String> headers = new HashMap<>();
			headers.put("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");

			httpPostMultipart.setConnection(url);
			// Add form field

			httpPostMultipart.addFormField("fileName", fileName);
			httpPostMultipart.addFormField("location", location.toString());

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
