package com.fb.eureka.server.eurekaServer.image.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fb.eureka.server.eurekaServer.image.service.ImageService;

@RestController
@RequestMapping("imageService")
@CrossOrigin("*")
public class ImageController {

	@Autowired
	ImageService service;

	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.ALL_VALUE)
	public ResponseEntity<String> saveImage(@RequestParam("file") MultipartFile file,
			@RequestParam("fileName") String fileName,@RequestParam("location") String location) {
		return new ResponseEntity<>(service.saveImage(fileName, file,location), HttpStatus.OK);
	}

	@PostMapping(path = "/uploadWithExt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.ALL_VALUE)
	public ResponseEntity<String> uploadWithExt(@RequestParam("file") MultipartFile file,
			@RequestParam("fileName") String fileName, @RequestParam("location") String location) {
		return new ResponseEntity<>(service.saveImageWithExt(fileName, file, location), HttpStatus.OK);
	}

	@GetMapping(value = "/download/{location}/{fileName}")
	public ResponseEntity<byte[]> downloadImageS3(@PathVariable("location") String location,
			@PathVariable("fileName") String fileName) {
		byte[] result = service.downloadImageS3(location, fileName);
		try {
			if (result != null) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			result = null;
		}
		return null;
	}

	@PostMapping(value = "/download/{location}/{fileName}")
	public ResponseEntity<byte[]> downloadImagePost(@PathVariable("location") String location,
			@PathVariable("fileName") String fileName) {
		byte[] result = service.downloadImageS3(location, fileName);
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

	@PostMapping(value = "/downloadImage/{location}/{fileName}")
	public ResponseEntity<byte[]> downloadImage(@PathVariable("location") String location,
			@PathVariable("fileName") String fileName) {
		byte[] result = service.downloadImage(location, fileName);
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

	@PostMapping(value = "/downloadImage")
	public ResponseEntity<byte[]> downloadImageWithExt(@RequestParam("location") String location,
			@RequestParam("fileName") String fileName) {
		byte[] result = service.downloadImageS3(location, fileName);
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
	
	@PostMapping(value = "/fileExist")
	public ResponseEntity<Boolean> isFileExist(@RequestParam("location") String location,
			@RequestParam("fileName") String fileName) {
		if (service.isFileExist(location, fileName)) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(true, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
