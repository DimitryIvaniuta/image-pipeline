package com.imagepipeline.service;

import com.imagepipeline.config.AwsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Service for generating thumbnails and uploading them to S3.
 */
@Service
@Slf4j
public class ThumbnailService {

    static final int THUMBNAIL_WIDTH = 150;
    static final int THUMBNAIL_HEIGHT = 150;

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    /**
     * Constructs the ThumbnailService.
     *
     * @param s3Client      the AWS S3 client.
     * @param awsProperties configuration properties for AWS.
     */
    public ThumbnailService(S3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    /**
     * Generates and uploads a thumbnail.
     *
     * @param file the image file.
     * @throws IOException if an error occurs.
     */
    public void generateThumbnail(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage thumbnail = resizeImage(originalImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "jpg", baos);
        byte[] thumbnailBytes = baos.toByteArray();

        String thumbnailKey = "thumbnails/" + UUID.randomUUID().toString() + "_thumbnail_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(thumbnailKey)
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(thumbnailBytes));
        String thumbnailUrl = "https://s3.amazonaws.com/" + awsProperties.getS3().getBucket() + "/" + thumbnailKey;
        log.info("Thumbnail uploaded to S3: {}", thumbnailUrl);
    }

    /**
     * Resizes an image.
     *
     * @param originalImage the original image.
     * @param targetWidth   the width.
     * @param targetHeight  the height.
     * @return the resized BufferedImage.
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

}
