package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.awt.Color;
import java.awt.Graphics2D;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class MetadataServiceTest {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private AwsProperties awsProperties;

    @MockitoBean
    private DynamoDbClient dynamoDbClient;

    @Test
    void testExtractAndStoreMetadata() throws IOException {
        // Create a valid dummy image.
        int width = 100;
        int height = 100;
        BufferedImage dummyImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = dummyImage.createGraphics();
        graphics.setPaint(Color.BLUE);
        graphics.fillRect(0, 0, dummyImage.getWidth(), dummyImage.getHeight());
        graphics.dispose();

        // Write the BufferedImage to a byte array output stream in PNG format.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "png", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        // Create a MockMultipartFile using the valid image data.
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", imageBytes);

        // Call the metadata extraction and storage method.
        metadataService.extractAndStoreMetadata(file, "http://s3.amazonaws.com/dummy/test.png");

        // Verify that the DynamoDB client's putItem method was called.
        verify(dynamoDbClient, times(1)).putItem(any(PutItemRequest.class));
    }

}
