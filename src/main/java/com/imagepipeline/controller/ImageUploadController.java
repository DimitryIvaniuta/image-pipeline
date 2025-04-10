package com.imagepipeline.controller;

import com.imagepipeline.service.ImageProcessingService;
import com.imagepipeline.service.ProgressTrackerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller that handles image upload requests.
 */
@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    private final ImageProcessingService imageProcessingService;

    private final ProgressTrackerService progressTrackerService;

    /**
     * Constructor for dependency injection.
     *
     * @param imageProcessingService service that handles the image processing pipeline.
     */
    public ImageUploadController(ImageProcessingService imageProcessingService,
                                 ProgressTrackerService progressTrackerService) {
        this.imageProcessingService = imageProcessingService;
        this.progressTrackerService = progressTrackerService;
    }

    /**
     * Endpoint to upload an image.
     * The uploaded image is processed through:
     * <ol>
     *   <li>Storing the image in S3.</li>
     *   <li>Extracting and storing image metadata in DynamoDB.</li>
     *   <li>Generating a thumbnail and storing it in S3.</li>
     *   <li>Notifying other system components via SNS.</li>
     * </ol>
     *
     * @param file the image file uploaded as MultipartFile.
     * @return a response message indicating the result of the operation.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        // Generate a unique job ID for this upload.
        String jobId = UUID.randomUUID().toString();

        // Start processing the image asynchronously.
        imageProcessingService.processImage(jobId, file);

        return ResponseEntity.ok(Map.of("jobId", jobId));
    }

    /**
     * Returns the progress percentage for a given job.
     *
     * @param jobId the job identifier.
     * @return a JSON object with the jobId and its progress percentage.
     */
    @GetMapping("/progress/{jobId}")
    public ResponseEntity<Map<String, Object>> getProgressByJob(@PathVariable String jobId) {
        int progress = progressTrackerService.getProgress(jobId);
        return ResponseEntity.ok(Map.of("jobId", jobId, "progress", progress));
    }

    /**
     * Returns progress percentages for all active jobs.
     *
     * @return a JSON object mapping jobIds to their progress percentages.
     */
    @GetMapping("/progress")
    public ResponseEntity<Map<String, Integer>> getAllProgress() {
        return ResponseEntity.ok(progressTrackerService.getAllProgress());
    }

}
