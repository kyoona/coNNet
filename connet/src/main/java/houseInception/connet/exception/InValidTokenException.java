package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class InValidTokenException extends BaseException{

    public InValidTokenException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public InValidTokenException(StatusCode status) {
        this(status, "유효하지 않은 토큰입니다.");
    }
}
