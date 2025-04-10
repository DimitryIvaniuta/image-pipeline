package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AwsProperties awsProperties;

    @MockitoBean
    private SnsClient snsClient;

    @Test
    void testNotifyImageUpload() {
        String imageUrl = "http://s3.amazonaws.com/dummy/test.jpg";

        // Invoke the notification method.
        notificationService.notifyImageUpload(imageUrl);

        // Verify that the SNS clients publish method was called.
        verify(snsClient, times(1)).publish(any(PublishRequest.class));
    }
}
