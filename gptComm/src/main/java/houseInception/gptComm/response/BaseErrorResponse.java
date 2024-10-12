package houseInception.gptComm.response;

import houseInception.gptComm.response.status.StatusCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    }

    public static ResponseEntity<BaseErrorResponse> get(StatusCode code) {
        BaseErrorResponse res = new BaseErrorResponse(code.getCode(), code.getMessage(), null);
        return new ResponseEntity<>(res, code.getHttpStatus());
    }
}
