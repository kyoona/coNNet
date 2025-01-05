package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;
import lombok.Getter;

import static houseInception.connet.response.status.BaseErrorCode.CAN_NOT_PARSE;

@Getter
public class SerializationException extends BaseException{

    private String payload;

    public SerializationException(StatusCode status, String errorMessage, String payload) {
        super(status, errorMessage);
        this.payload = payload;
    }

    public SerializationException(String payload) {
        this(CAN_NOT_PARSE, "SerializationException가 발생했습니다.", payload);
    }
}
