package com.imagepipeline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuration for the asynchronous executor.
 */
@Configuration
public class AsyncConfig {

    /**
     * Creates a fixed thread pool executor with 10 threads.
     *
     * @return an Executor instance.
     */
    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(5);
    }

}
