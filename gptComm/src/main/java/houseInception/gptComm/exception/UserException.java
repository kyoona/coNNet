package houseInception.gptComm.exception;

import houseInception.gptComm.response.status.StatusCode;

public class UserException extends BaseException{
    public UserException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public UserException(StatusCode status) {
        this(status, "UserException가 발생했습니다.");
    }
}
