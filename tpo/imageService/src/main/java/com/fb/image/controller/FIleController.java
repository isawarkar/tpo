package com.fb.image.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fb.image.service.FileService;

@RestController
@RequestMapping("fileService")
@CrossOrigin("*")
public class FIleController {

	@Autowired
	FileService service;

	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.ALL_VALUE)
	public ResponseEntity<String> saveImage(@RequestParam("file") MultipartFile file,
			@RequestParam("fileName") String fileName, @RequestParam("location") String location) {
		return new ResponseEntity<>(service.saveFile(fileName, file, location), HttpStatus.OK);
	}

	@GetMapping(value = "/download/{location}/{fileName}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable("location") String location,
			@PathVariable("fileName") String fileName) {
		byte[] result = service.downloadFile(location, fileName);
		try {
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			result = null;
		}
		return null;
	}

	@PostMapping(value = "/download/{location}/{fileName}")
	public ResponseEntity<byte[]> downloadPostFile(@PathVariable("location") String location,
			@PathVariable("fileName") String fileName) {
		byte[] result = service.downloadFile(location, fileName);
		try {
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			result = null;
		}
		return null;
	}

	@PostMapping(value = "/download")
	public ResponseEntity<byte[]> downloadPostFile1(@RequestParam("location") String location,
			@RequestParam("fileName") String fileName) {
		byte[] result = service.downloadFile(location, fileName);
		try {
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			result = null;
		}
		return null;
	}

	@DeleteMapping(value = "/delete/{location}/{fileName}")
	public void deleteFile(@PathVariable("location") String location, @PathVariable("fileName") String fileName) {
		service.deleteFile(location, fileName);
	}

	@DeleteMapping(value = "/delete")
	public void deleteFileWithParam(@RequestParam("location") String location, @RequestParam("fileName") String fileName) {
		service.deleteFile(location, fileName);
	}
	
	@DeleteMapping(value = "/deleteFolder")
	public void deleteFolder(@RequestParam("name") String name) {
		service.deleteFolder(name);
	}

	@PostMapping(value = "/fileExist")
	public ResponseEntity<Boolean> isFileExist(@RequestParam("location") String location,
			@RequestParam("fileName") String fileName) {
		if (service.isFileExist(location, fileName)) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(true, HttpStatus.NOT_FOUND);
		}
	}

}
