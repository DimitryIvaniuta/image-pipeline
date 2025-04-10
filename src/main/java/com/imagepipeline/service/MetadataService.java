package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import com.imagepipeline.model.ImageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service to extract image metadata and store it in DynamoDB.
 */
@Service
@Slf4j
public class MetadataService {

    private final DynamoDbClient dynamoDbClient;
    private final AwsProperties awsProperties;

    /**
     * Constructs the MetadataService.
     *
     * @param dynamoDbClient the AWS DynamoDB client.
     * @param awsProperties  configuration properties for AWS.
     */
    public MetadataService(DynamoDbClient dynamoDbClient, AwsProperties awsProperties) {
        this.dynamoDbClient = dynamoDbClient;
        this.awsProperties = awsProperties;
    }

    /**
     * Extracts metadata from an image file and stores it in DynamoDB.
     *
     * @param file     the image file.
     * @param imageUrl the S3 URL.
     * @throws IOException if an error occurs.
     */
    public void extractAndStoreMetadata(MultipartFile file, String imageUrl) throws IOException {
        ImageMetadata metadata = extractMetadata(file, imageUrl);
        storeMetadata(metadata);
    }

    /**
     * Extracts metadata.
     *
     * @param file     the image file.
     * @param imageUrl the S3 URL.
     * @return an ImageMetadata object.
     * @throws IOException if an error occurs.
     */
    private ImageMetadata extractMetadata(MultipartFile file, String imageUrl) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        String format = getFileExtension(file.getOriginalFilename());
        String tags = "default,photo";
        String imageId = UUID.randomUUID().toString();
        ImageMetadata metadata = new ImageMetadata(imageId, imageUrl, width, height, format, tags);
        log.info("Extracted metadata: {}", metadata);
        return metadata;
    }

    /**
     * Stores the metadata in DynamoDB.
     *
     * @param metadata the image metadata.
     */
    private void storeMetadata(ImageMetadata metadata) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("imageId", AttributeValue.builder().s(metadata.getImageId()).build());
        item.put("imageUrl", AttributeValue.builder().s(metadata.getImageUrl()).build());
        item.put("width", AttributeValue.builder().n(String.valueOf(metadata.getWidth())).build());
        item.put("height", AttributeValue.builder().n(String.valueOf(metadata.getHeight())).build());
        item.put("format", AttributeValue.builder().s(metadata.getFormat()).build());
        item.put("tags", AttributeValue.builder().s(metadata.getTags()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(awsProperties.getDynamodb().getTable())
                .item(item)
                .build();
        try {
            dynamoDbClient.putItem(request);
            log.info("Successfully stored metadata for imageId: {}", metadata.getImageId());
        } catch (Exception e) {
            log.error("Failed to store metadata for imageId: {}. Error: {}", metadata.getImageId(), e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts the file extension from the provided filename.
     *
     * @param fileName the filename.
     * @return the file extension in lowercase, or "unknown" if not found.
     */
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return "unknown";
    }
}
