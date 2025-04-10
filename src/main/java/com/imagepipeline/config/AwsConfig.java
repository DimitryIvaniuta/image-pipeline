package com.imagepipeline.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * AWS configuration for creating AWS SDK client beans.
 */
@Configuration
@EnableConfigurationProperties(AwsProperties.class)
@AllArgsConstructor
public class AwsConfig {

    private final AwsProperties awsProperties;

    /**
     * Creates an Amazon S3 client.
     *
     * @return an S3Client instance configured for a specific region.
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperties.getRegion()))
                .build();
    }

    /**
     * Creates an Amazon DynamoDB client.
     *
     * @return a DynamoDbClient instance configured for a specific region.
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(awsProperties.getRegion()))
                .build();
    }

    /**
     * Creates an Amazon SNS client.
     *
     * @return an SnsClient instance configured for a specific region.
     */
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(awsProperties.getRegion()))
                .build();
    }

}
