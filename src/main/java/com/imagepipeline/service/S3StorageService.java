package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Service responsible for uploading image files to Amazon S3.
 */
@Service
@Slf4j
public class S3StorageService {

    private final S3Client s3Client;

    private final AwsProperties awsProperties;

    /**
     * Constructs a S3StorageService with an AWS S3 client and AWS properties.
     *
     * @param s3Client       the AWS S3 client.
     * @param awsProperties  the injected AWS properties.
     */
    public S3StorageService(S3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    /**
     * Uploads the provided image file to Amazon S3.
     *
     * @param file the image file to upload.
     * @return the public URL of the uploaded image.
     * @throws IOException if an error occurs during file upload.
     */
    public String uploadToS3(MultipartFile file) throws IOException {
        // Generate a unique filename.
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Build the PutObjectRequest with the desired parameters.
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        // Upload the file to S3.
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Construct and return the public URL of the image (assumes bucket is public).
        String imageUrl = "https://s3.amazonaws.com/" + awsProperties.getS3().getBucket() + "/" + fileName;
        log.info("Image uploaded to S3: {}", imageUrl);

        return imageUrl;
    }

}
