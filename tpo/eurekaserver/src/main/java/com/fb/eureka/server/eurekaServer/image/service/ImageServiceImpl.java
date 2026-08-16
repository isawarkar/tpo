package com.fb.eureka.server.eurekaServer.image.service;

import static org.apache.http.entity.ContentType.IMAGE_BMP;
import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {
	@Autowired
	private FileStore fileStore;
	
	@Value("${bucketName}")
	String bucketName;

	@Override
	public String saveImage(String fileName, MultipartFile file, String location) {
		// check if the file is empty
		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file");
		}
		
		// get file metadata
		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		String path = String.format("%s/%s", bucketName, location);
		String newFileName = String.format("%s", fileName + ".png");
		try {
			fileStore.upload(path, newFileName, Optional.of(metadata), file.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException("Failed to upload file", e);
		}

		return "File Uploaded";
	}

	@Override
	public String saveImageWithExt(String fileName, MultipartFile file, String location) {
		// check if the file is empty
		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file");
		}
		// Check if the file is an image
		if (!Arrays.asList(IMAGE_PNG.getMimeType(), IMAGE_BMP.getMimeType(), IMAGE_GIF.getMimeType(),
				IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
			throw new IllegalStateException("FIle uploaded is not an image");
		}
		// get file metadata
		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		String path = String.format("%s/%s", bucketName, location);
		String newFileName = String.format("%s", fileName);
		try {
			fileStore.upload(path, newFileName, Optional.of(metadata), file.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException("Failed to upload file", e);
		}

		return "File Uploaded";
	}

	@Override
	public byte[] downloadImage(String path, String fileName) {
		if (fileStore.isFileExist(path, fileName + ".png")) {
			return fileStore.download(path, fileName);
		} else {
			return null;
		}
	}

	@Override
	public byte[] downloadImageS3(String path, String fileName) {
		if (fileStore.isFileExist(path, fileName)) {
			return fileStore.downloadS3(path, fileName);
		} else {
			return null;
		}
	}

	@Override
	public boolean isFileExist(String path, String fileName) {
		return fileStore.isFileExist(path, fileName);
	}
}
