package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class SocketException extends BaseException{

    public SocketException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public SocketException(StatusCode status) {
        super(status, "SocketException가 발생했습니다.");
    }
}
