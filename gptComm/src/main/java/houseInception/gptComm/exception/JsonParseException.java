package houseInception.gptComm.exception;

import houseInception.gptComm.response.status.BaseErrorCode;
import houseInception.gptComm.response.status.StatusCode;

import static houseInception.gptComm.response.status.BaseErrorCode.INTERNAL_SERVER_ERROR;

public class JsonParseException extends BaseException{
    public JsonParseException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public JsonParseException(StatusCode status) {
        this(status, "JsonParseException가 발생했습니다.");
    }

    public JsonParseException() {
        this(INTERNAL_SERVER_ERROR, "JsonParseException가 발생했습니다.");
    }
}
