package houseInception.gptComm.exception;

import houseInception.gptComm.response.status.StatusCode;

public class BaseExceptionImpl extends BaseException{

    public BaseExceptionImpl(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public BaseExceptionImpl(StatusCode status) {
        super(status, "예외가 발생했습니다.");
    }
}
