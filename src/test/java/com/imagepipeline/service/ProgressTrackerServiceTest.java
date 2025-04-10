package com.imagepipeline.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProgressTrackerServiceTest {

    @Autowired
    private ProgressTrackerService progressTrackerService;

    @Test
    void testProgressTracking() {
        String jobId = "job123";

        // Set progress to 50%.
        progressTrackerService.setProgress(jobId, 50);
        int progress = progressTrackerService.getProgress(jobId);
        assertEquals(50, progress);

        // Remove the job and verify progress returns default value (0).
        progressTrackerService.removeJob(jobId);
        progress = progressTrackerService.getProgress(jobId);
        assertEquals(0, progress);
    }

}
