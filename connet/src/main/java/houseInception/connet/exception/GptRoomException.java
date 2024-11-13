package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class GptRoomException extends BaseException{
    public GptRoomException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public GptRoomException(StatusCode status) {
        this(status, "GptRoomException가 발생하였습니다.");
    }
}
