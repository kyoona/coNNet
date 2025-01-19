package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class ChannelException extends BaseException{

    public ChannelException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public ChannelException(StatusCode status) {
        this(status, "ChannelException가 발생하였습니다.");
    }
}
