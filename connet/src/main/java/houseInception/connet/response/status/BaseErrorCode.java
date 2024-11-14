package houseInception.connet.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseErrorCode implements StatusCode{

    //4XX 클라이언트 에러
    BAD_REQUEST(40000, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED),
    FORBIDDEN(40300, HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN),
    NOT_FOUND(40400, HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(40500, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), HttpStatus.METHOD_NOT_ALLOWED),

    NO_SUCH_CHATROOM(40002, "채팅방이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_CHATROOM_USER(40003, "채팅방에 대한 권한이 없습니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_USER(40004, "사용자가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_FRIEND(40005, "친구가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_FRIEND_REQUEST(40006, "이미 요청된 관계입니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_FRIEND_REQUEST(40007, "요청이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    CANT_NOT_REQUEST_SELF(40008, "본인에게 친구 요청을 보낼 수 없습니다.", HttpStatus.BAD_REQUEST),
    BLOCK_USER(40009, "차단된 유저입니다.", HttpStatus.BAD_REQUEST),

    INVALID_GOOGLE_TOKEN(40101, "유효하지 않은 Google Access Token입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(40102, "유효하지 않은 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),

    //5XX 서버 에러
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);

    private int code; //서버 내부 오류 코드
    private String message; //서버 내부 오류 메세지
    private HttpStatus httpStatus; //Http 응답 status
}
