package houseInception.gptComm.exception;

import houseInception.gptComm.response.status.StatusCode;

public class ChatRoomException extends BaseException{
    public ChatRoomException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public ChatRoomException(StatusCode status) {
        this(status, "ChatRoomException가 발생하였습니다.");
    }
}
