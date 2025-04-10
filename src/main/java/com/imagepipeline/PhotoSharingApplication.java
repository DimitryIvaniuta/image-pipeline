package com.imagepipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Photo Sharing Application.
 */
@SpringBootApplication
public class PhotoSharingApplication {

    /**
     * The entry point of the Photo Sharing Application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(PhotoSharingApplication.class, args);
    }

}
