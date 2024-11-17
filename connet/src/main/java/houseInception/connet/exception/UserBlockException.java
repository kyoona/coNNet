package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class UserBlockException extends BaseException{
    public UserBlockException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public UserBlockException(StatusCode status) {
        this(status, "UserBlockException가 발생했습니다.");
    }
}
