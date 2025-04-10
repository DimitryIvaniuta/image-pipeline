package com.imagepipeline.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ImageProcessingServiceTest {

    @Autowired
    private ImageProcessingService imageProcessingService;

    @Autowired
    private ProgressTrackerService progressTrackerService;

    @MockitoBean
    private S3StorageService s3StorageService;

    @MockitoBean
    private MetadataService metadataService;

    @MockitoBean
    private ThumbnailService thumbnailService;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void testProcessImage() throws IOException {
        // Create a dummy file.
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());

        // Stub dependent services to simply succeed.
        when(s3StorageService.uploadToS3(any(MultipartFile.class))).thenReturn("http://s3.amazonaws.com/dummy/test.txt");
        doNothing().when(metadataService).extractAndStoreMetadata(any(MultipartFile.class), anyString());
        doNothing().when(thumbnailService).generateThumbnail(any(MultipartFile.class));
        doNothing().when(notificationService).notifyImageUpload(anyString());

        String jobId = "job123";

        // Process the image asynchronously.
        CompletableFuture<Void> future = imageProcessingService.processImage(jobId, file);
        future.join(); // wait until the CompletableFuture completes

        // Verify that final progress is 100%.
        int progress = progressTrackerService.getProgress(jobId);
        assertEquals(100, progress);

        // Verify that each dependency was called exactly once.
        verify(s3StorageService, times(1)).uploadToS3(any(MultipartFile.class));
        verify(metadataService, times(1)).extractAndStoreMetadata(any(MultipartFile.class), anyString());
        verify(thumbnailService, times(1)).generateThumbnail(any(MultipartFile.class));
        verify(notificationService, times(1)).notifyImageUpload(anyString());
    }
}
