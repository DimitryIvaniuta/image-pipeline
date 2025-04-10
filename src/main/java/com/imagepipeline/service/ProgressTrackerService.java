package com.imagepipeline.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to track progress percentages for image processing jobs.
 */
@Service
public class ProgressTrackerService {

    // A thread-safe map storing jobId to progress percentage (0-100)
    private final ConcurrentHashMap<String, Integer> progressMap = new ConcurrentHashMap<>();

    /**
     * Sets the progress percentage for a given job.
     *
     * @param jobId   the job identifier.
     * @param percent the completion percentage.
     */
    public void setProgress(String jobId, int percent) {
        progressMap.put(jobId, percent);
    }

    /**
     * Retrieves the progress percentage for a given job.
     *
     * @param jobId the job identifier.
     * @return the current progress percentage.
     */
    public int getProgress(String jobId) {
        return progressMap.getOrDefault(jobId, 0);
    }

    /**
     * Returns the progress statistics for all jobs.
     *
     * @return a map of job identifiers to their progress percentages.
     */
    public Map<String, Integer> getAllProgress() {
        return new ConcurrentHashMap<>(progressMap);
    }

    /**
     * Removes a job from the progress tracking (e.g. after completion).
     *
     * @param jobId the job identifier.
     */
    public void removeJob(String jobId) {
        progressMap.remove(jobId);
    }

}
