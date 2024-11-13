package houseInception.connet.response.status;

import org.springframework.http.HttpStatus;

public interface StatusCode {
    int getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}
