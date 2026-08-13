package com.fb.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String saveFile(String fileName, MultipartFile file, String location);

	byte[] downloadFile(String path, String fileName);

	void deleteFile(String path, String fileName);

	void deleteFolder(String name);

	boolean isFileExist(String path, String fileName);
}
