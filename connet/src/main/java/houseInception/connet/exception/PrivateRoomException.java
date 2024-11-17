package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class PrivateRoomException extends BaseException{

    public PrivateRoomException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public PrivateRoomException(StatusCode status) {
        this(status, "PrivateRoomException가 발생하였습니다.");
    }
}
