package com.imagepipeline.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Service to orchestrate the image processing pipeline.
 * Delegates tasks to specific services:
 * S3StorageService, MetadataService, ThumbnailService, and NotificationService.
 */
@Service
public class ImageProcessingService {

    private final S3StorageService s3StorageService;
    private final MetadataService metadataService;
    private final ThumbnailService thumbnailService;
    private final ProgressTrackerService progressTrackerService;
    private final NotificationService notificationService;
    private final Executor executor;

    /**
     * Constructs an ImageProcessingService with its sub-services.
     *
     * @param s3StorageService       service for S3 storage operations.
     * @param metadataService        service for metadata extraction/storage in DynamoDB.
     * @param thumbnailService       service for thumbnail generation and storage.
     * @param progressTrackerService service for tracking job progress.
     * @param notificationService    service for notifying system components via SNS.
     * @param executor               the executor for asynchronous processing.
     */
    public ImageProcessingService(S3StorageService s3StorageService,
                                  MetadataService metadataService,
                                  ThumbnailService thumbnailService,
                                  ProgressTrackerService progressTrackerService,
                                  NotificationService notificationService,
                                  Executor executor) {
        this.s3StorageService = s3StorageService;
        this.metadataService = metadataService;
        this.thumbnailService = thumbnailService;
        this.progressTrackerService = progressTrackerService;
        this.notificationService = notificationService;
        this.executor = executor;
    }

    /**
     * Processes an image asynchronously through the entire pipeline.
     *
     * @param jobId the unique identifier for this upload job.
     * @param file  the uploaded image file.
     * @return a CompletableFuture representing the asynchronous task.
     */
    public CompletableFuture<Void> processImage(String jobId, MultipartFile file) {
        // Initialize progress for the job.
        progressTrackerService.setProgress(jobId, 0);

        return CompletableFuture.supplyAsync(() -> {
                    try {
                        String imageUrl = s3StorageService.uploadToS3(file);
                        progressTrackerService.setProgress(jobId, 25);
                        return imageUrl;
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executor)
                .thenCompose(imageUrl -> CompletableFuture.supplyAsync(() -> {
                    try {
                        metadataService.extractAndStoreMetadata(file, imageUrl);
                        progressTrackerService.setProgress(jobId, 50);
                        return imageUrl;
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executor))
                .thenCompose(imageUrl -> CompletableFuture.supplyAsync(() -> {
                    try {
                        thumbnailService.generateThumbnail(file);
                        progressTrackerService.setProgress(jobId, 75);
                        return imageUrl;
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                }, executor))
                .thenCompose(imageUrl -> CompletableFuture.runAsync(() -> {
                    notificationService.notifyImageUpload(imageUrl);
                    progressTrackerService.setProgress(jobId, 100);
                }, executor));
    }

}
