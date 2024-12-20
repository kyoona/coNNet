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
    ALREADY_BLOCK_USER(400010, "이미 차단된 유저입니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_USER_BLOCK(400011, "차단이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_CONTENT_IN_CHAT(400012, "채팅의 내용이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_VALID_FILE_NAME(400013, "파일 이름이 옳바르지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_CHAT(400014, "채팅이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HAS_EMOJI(400015, "이모지가 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_EMOJI(400016, "이모지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_GROUP_TAG(400017, "유효하지 않은 태그입니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_GROUP(400018, "그룹이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_IN_GROUP(400019, "그룹에 속하지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_IN_GROUP(400020, "이미 그룹에 속한 사용자입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HAS_GROUP_INVITE(400021, "이미 그룹 초대된 사용자입니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_GROUP_INVITE(400022, "그룹에 초대되지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),

    INVALID_GOOGLE_TOKEN(40101, "유효하지 않은 Google Access Token입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(40102, "유효하지 않은 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),

    //5XX 서버 에러
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR),
    CANT_NOT_PARSE_SOCKET_MESSAGE(50001, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR),
    CAN_NOT_UPLOAD_FILE_TO_S3(50002, HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);

    private int code; //서버 내부 오류 코드
    private String message; //서버 내부 오류 메세지
    private HttpStatus httpStatus; //Http 응답 status
}
