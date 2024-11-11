package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

import static houseInception.connet.response.status.BaseErrorCode.INTERNAL_SERVER_ERROR;

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
