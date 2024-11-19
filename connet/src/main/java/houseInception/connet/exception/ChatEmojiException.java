package houseInception.connet.exception;

import houseInception.connet.response.status.StatusCode;

public class ChatEmojiException extends BaseException{

    public ChatEmojiException(StatusCode status, String errorMessage) {
        super(status, errorMessage);
    }

    public ChatEmojiException(StatusCode status) {
        super(status, "ChatEmojiException가 발생하였습니다.");
    }
}
