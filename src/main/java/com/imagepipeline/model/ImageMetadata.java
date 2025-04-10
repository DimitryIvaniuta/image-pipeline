package com.imagepipeline.model;

import lombok.Getter;
import lombok.ToString;

/**
 * POJO representing the metadata for an image.
 */
@Getter
@ToString
public class ImageMetadata {

    private final String imageId;
    private final String imageUrl;
    private final int width;
    private final int height;
    private final String format;
    private final String tags;

    /**
     * Constructs an ImageMetadata object.
     *
     * @param imageId  Unique identifier for the image.
     * @param imageUrl URL where the image is stored.
     * @param width    Image width in pixels.
     * @param height   Image height in pixels.
     * @param format   Format of the image file.
     * @param tags     Associated tags for the image.
     */
    public ImageMetadata(String imageId, String imageUrl, int width, int height, String format, String tags) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
        this.format = format;
        this.tags = tags;
    }

}
