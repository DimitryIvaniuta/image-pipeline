package com.imagepipeline.controller;

import com.imagepipeline.service.ImageProcessingService;
import com.imagepipeline.service.ProgressTrackerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageUploadController.class)
class ImageUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageProcessingService imageProcessingService;

    @MockitoBean
    private ProgressTrackerService progressTrackerService;

    /**
     * Tests the image upload endpoint.
     * Sends a multipart request and expects a JSON response with a jobId.
     */
    @Test
    void testUploadImage() throws Exception {
        // Create a dummy file as test content.
        byte[] content = "Test File Content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, content);

        // Stub the asynchronous processing method to immediately complete.
        when(imageProcessingService.processImage(anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Perform a multipart request to /api/images/upload.
        mockMvc.perform(multipart("/api/images/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId", not(emptyString())));

        // Verify that processImage was called exactly once.
        verify(imageProcessingService, times(1)).processImage(anyString(), any());
    }

    /**
     * Tests that requesting progress for a specific job returns the expected JSON.
     */
    @Test
    void testGetProgressByJob() throws Exception {
        String jobId = "job-123";
        int expectedProgress = 75;
        when(progressTrackerService.getProgress(jobId)).thenReturn(expectedProgress);

        // Perform a GET request to /api/images/progress/{jobId}.
        mockMvc.perform(get("/api/images/progress/" + jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId", is(jobId)))
                .andExpect(jsonPath("$.progress", is(expectedProgress)));
    }

    /**
     * Tests that retrieving the progress of all jobs returns the correct mapping.
     */
    @Test
    void testGetAllProgress() throws Exception {
        Map<String, Integer> progressMap = Map.of("job-123", 75, "job-456", 50);
        when(progressTrackerService.getAllProgress()).thenReturn(progressMap);

        // Perform a GET request to /api/images/progress.
        mockMvc.perform(get("/api/images/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['job-123']", is(75)))
                .andExpect(jsonPath("$.['job-456']", is(50)));
    }

}
