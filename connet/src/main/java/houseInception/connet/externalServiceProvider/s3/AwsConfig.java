package houseInception.connet.externalServiceProvider.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.s3.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.s3.secretAccessKey}")
    private String secretAccessKey;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .serviceConfiguration(S3Configuration.builder()
                        .checksumValidationEnabled(false)
                        .build())
                .build();
    }
}
