package com.fb.eureka.server.eurekaServer.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String saveImage(String fileName,MultipartFile file,String location);
    
    String saveImageWithExt(String fileName,MultipartFile file, String location);

    byte[] downloadImage(String path,String fileName);
    byte[] downloadImageS3(String path,String fileName);
    
    boolean isFileExist(String path,String fileName);
}
