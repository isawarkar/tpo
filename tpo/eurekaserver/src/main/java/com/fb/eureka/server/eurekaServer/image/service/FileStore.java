package com.fb.eureka.server.eurekaServer.image.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

@Service
public class FileStore {
	@Autowired
	private AmazonS3 amazonS3;
	
	@Value("${bucketName}")
	String bucketName;

	public void upload(String path, String fileName, Optional<Map<String, String>> optionalMetaData,
			InputStream inputStream) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		optionalMetaData.ifPresent(map -> {
			if (!map.isEmpty()) {
				map.forEach(objectMetadata::addUserMetadata);
			}
		});
		try {
			amazonS3.putObject(path, fileName, inputStream, objectMetadata);
		} catch (AmazonServiceException e) {
			throw new IllegalStateException("Failed to upload the file", e);
		}
	}

	public byte[] downloadS3(String path, String fileName) {
		try {
			S3Object object = amazonS3.getObject(bucketName + "/" + path, fileName);
			S3ObjectInputStream objectContent = object.getObjectContent();
			return IOUtils.toByteArray(objectContent);
		} catch (AmazonServiceException | IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public byte[] download(String path, String fileName) {
		try {
			S3Object object = amazonS3.getObject(bucketName + "/" + path, fileName + ".png");
			S3ObjectInputStream objectContent = object.getObjectContent();
			return IOUtils.toByteArray(objectContent);
		} catch (AmazonServiceException | IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public byte[] downloadFile(String path, String fileName) {
		try {
			S3Object object = amazonS3.getObject(bucketName + "/" + path, fileName);
			S3ObjectInputStream objectContent = object.getObjectContent();
			return IOUtils.toByteArray(objectContent);
		} catch (AmazonServiceException | IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public void deleteFile(String path, String fileName) {
		try {
			amazonS3.deleteObject(bucketName + "/" + path, fileName);
		} catch (AmazonServiceException e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isFileExist(String path, String fileName) {
		try {
			return amazonS3.doesObjectExist(bucketName + "/" + path, fileName);
		} catch (AmazonServiceException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public void deleteFolder(String name) {
		try {
		
		   ObjectListing listing= amazonS3.listObjects(bucketName, name);
		   List<S3ObjectSummary> summaries = listing.getObjectSummaries();

		   while (listing.isTruncated()) {
		      listing = amazonS3.listNextBatchOfObjects (listing);
		      summaries.addAll (listing.getObjectSummaries());
		   }
		  for(S3ObjectSummary s:summaries) {
			  amazonS3.deleteObject(bucketName, s.getKey());
		  }
		} catch (AmazonServiceException e) {
			System.out.println(e.getMessage());
		}
	}
	
	

}
