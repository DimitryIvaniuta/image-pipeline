package com.imagepipeline.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for AWS settings.
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    /**
     * AWS region.
     */
    @Setter
    private String region;

    /**
     * S3 specific settings.
     */
    private final S3 s3 = new S3();

    /**
     * DynamoDB specific settings.
     */
    private final DynamoDb dynamodb = new DynamoDb();

    /**
     * SNS specific settings.
     */
    private final Sns sns = new Sns();

    @Setter
    @Getter
    public static class S3 {

        /**
         * The S3 bucket name.
         */
        private String bucket;

    }

    @Setter
    @Getter
    public static class DynamoDb {

        /**
         * The DynamoDB table name.
         */
        private String table;

    }

    @Setter
    @Getter
    public static class Sns {

        /**
         * The SNS topic ARN.
         */
        private String topicArn;

    }
}
