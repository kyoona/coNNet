package houseInception.connet.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseListResponse<T> {

    private String requestId;
    private List<T> result;

    public BaseListResponse(List<T> result) {
        this.requestId = MDC.get("request_id");
        this.result = result;
    }
}
