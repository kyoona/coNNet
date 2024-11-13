package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class FriendException extends BaseException{

    public FriendException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public FriendException(StatusCode status) {
        this(status, "FriendException가 발생했습니다.");
    }
}
