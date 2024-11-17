package houseInception.connet.externalServiceProvider.s3;

import houseInception.connet.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static houseInception.connet.response.status.BaseErrorCode.CAN_NOT_UPLOAD_FILE_TO_S3;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ServiceProvider {

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public void uploadImage(String objectKey, MultipartFile file){
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (S3Exception e) {
            log.error("S3Exception ", e);
            throw new S3UploadException(CAN_NOT_UPLOAD_FILE_TO_S3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
