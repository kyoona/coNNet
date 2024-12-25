package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class GroupChatException extends BaseException{

    public GroupChatException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public GroupChatException(StatusCode status) {
        this(status, "GroupChatException가 발생하였습니다.");
    }
}
