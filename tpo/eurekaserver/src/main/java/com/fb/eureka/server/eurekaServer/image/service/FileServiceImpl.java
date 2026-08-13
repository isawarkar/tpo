package com.fb.eureka.server.eurekaServer.image.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fb.eureka.server.eurekaServer.image.config.BucketName;

@Service
public class FileServiceImpl implements FileService {
	@Autowired
	private FileStore fileStore;

	@Override
	public String saveFile(String fileName, MultipartFile file, String location) {
		// check if the file is empty
		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file");
		}

		// get file metadata
		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		String path = String.format("%s/%s", BucketName.bucketName, location);
		String newFileName = String.format("%s", fileName);
		try {
			fileStore.upload(path, newFileName, Optional.of(metadata), file.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException("Failed to upload file", e);
		}

		return "File Uploaded";
	}

	@Override
	public byte[] downloadFile(String path, String fileName) {
		if (fileStore.isFileExist(path, fileName)) {
			return fileStore.downloadFile(path, fileName);
		} else {
			return null;
		}
	}

	@Override
	public void deleteFile(String path, String fileName) {
		if (fileStore.isFileExist(path, fileName)) {
		fileStore.deleteFile(path, fileName);
		}
	}
	
	@Override
	public void deleteFolder(String name) {
		fileStore.deleteFolder(name);
	}

	@Override
	public boolean isFileExist(String path, String fileName) {
		return fileStore.isFileExist(path, fileName);
	}

}
