package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class GroupException extends BaseException{

    public GroupException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public GroupException(StatusCode status) {
        this(status, "GroupException이 발생하였습니다.");
    }
}
