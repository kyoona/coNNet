package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class FileException extends BaseException{

    public FileException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public FileException(StatusCode status) {
        this(status, "FileException이 발생하였습니다.");
    }
}
