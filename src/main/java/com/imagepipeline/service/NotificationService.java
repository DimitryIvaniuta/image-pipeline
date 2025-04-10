package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/**
 * Service responsible for sending notifications about new image uploads via SNS.
 */
@Service
@Slf4j
public class NotificationService {

    private final SnsClient snsClient;

    private final AwsProperties awsProperties;

    public NotificationService(final SnsClient snsClient, final AwsProperties awsProperties) {
        this.snsClient = snsClient;
        this.awsProperties = awsProperties;
    }

    /**
     * Publishes a notification about the new image upload.
     *
     * @param imageUrl the S3 URL of the uploaded image.
     */
    public void notifyImageUpload(String imageUrl) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(awsProperties.getSns().getTopicArn())
                .subject("New Image Upload")
                .message("A new image has been uploaded: " + imageUrl)
                .build();
        snsClient.publish(request);
        log.info("SNS notification sent for image: {}", imageUrl);
    }

}
