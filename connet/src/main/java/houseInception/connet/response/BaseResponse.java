package houseInception.connet.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseResponse<T> {
    private String requestId;
    private T result;

    public BaseResponse(T result) {
        this.requestId = MDC.get("request_id");
        this.result = result;
    }

    public static BaseResponse<BaseResultDto> getSimpleRes(Long id) {
        return new BaseResponse<>(MDC.get("request_id"), BaseResultDto.get(id));
    }
}
