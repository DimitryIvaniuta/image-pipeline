package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class S3StorageServiceTest {

    @Autowired
    private S3StorageService s3StorageService;

    @Autowired
    private AwsProperties awsProperties;

    @MockitoBean
    private S3Client s3Client;

    @Test
    void testUploadToS3() throws IOException {
        // Create a dummy file.
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());

        // Invoke the upload method.
        String url = s3StorageService.uploadToS3(file);

        // Assert that the returned URL is not null and contains the bucket name.
        assertNotNull(url);
        assertTrue(url.contains(awsProperties.getS3().getBucket()));

        // Verify that the S3 client's putObject method was called exactly once.
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

}
