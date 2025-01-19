package houseInception.connet.response;

import houseInception.connet.response.status.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseErrorResponse {
    private int code;
    private String message;
    private String requestId;

    public BaseErrorResponse(StatusCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.requestId = MDC.get("request_id");
    }

    public static ResponseEntity<BaseErrorResponse> get(StatusCode code) {
        BaseErrorResponse res = new BaseErrorResponse(code.getCode(), code.getMessage(), MDC.get("request_id"));
        return new ResponseEntity<>(res, code.getHttpStatus());
    }
}
