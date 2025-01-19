package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class GroupInviteException extends BaseException{

    public GroupInviteException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public GroupInviteException(StatusCode status) {
        this(status, "GroupInviteException가 발생하였습니다.");
    }
}
