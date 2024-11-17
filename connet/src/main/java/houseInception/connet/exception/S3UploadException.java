package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class S3UploadException extends BaseException{

    public S3UploadException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public S3UploadException(StatusCode status) {
        super(status, "S3UploadException가 발생했습니다.");
    }
}
