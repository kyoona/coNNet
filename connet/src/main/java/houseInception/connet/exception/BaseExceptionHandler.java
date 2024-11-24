package houseInception.connet.exception;

import houseInception.connet.response.BaseErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.NoSuchElementException;

import static houseInception.connet.response.status.BaseErrorCode.*;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler({NoHandlerFoundException.class, TypeMismatchException.class})
    public ResponseEntity<BaseErrorResponse> handleBadRequest(Exception e) {
        log.error("Bad Request Exception: {}", e.getMessage(), e);

        return BaseErrorResponse.get(BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.error("Method Not Allowed Exception: {}", e.getMessage(), e);

        return BaseErrorResponse.get(METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({IllegalArgumentException.class, IOException.class})
    public ResponseEntity<BaseErrorResponse> handleInternalServerError(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage(), e);

        return BaseErrorResponse.get(INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.error("No Such Element Exception: {}", e.getMessage(), e);

        return BaseErrorResponse.get(NOT_FOUND);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<BaseErrorResponse> handleUserException(UserException e) {
        log.error("User Exception<{}>: {}", e.getStatus().getMessage(), e);

        return BaseErrorResponse.get(e.getStatus());
    }

    @ExceptionHandler(GptRoomException.class)
    public ResponseEntity<BaseErrorResponse> handleChatRoomException(GptRoomException e) {
        log.error("GptRoom Exception<{}>: {}", e.getStatus().getMessage(), e);

        return BaseErrorResponse.get(e.getStatus());
    }

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<BaseErrorResponse> handleFriendExceptionn(FriendException e) {
        log.error("Friend Exception<{}>: {}", e.getStatus().getMessage(), e);

        return BaseErrorResponse.get(e.getStatus());
    }

    @ExceptionHandler(PrivateRoomException.class)
    public ResponseEntity<BaseErrorResponse> handlePrivateRoomException(PrivateRoomException e) {
        log.error("PrivateRoom Exception<{}>: ", e.getStatus().getMessage(), e);

        return BaseErrorResponse.get(e.getStatus());
    }

    @ExceptionHandler(InValidTokenException.class)
    public ResponseEntity<BaseErrorResponse> handleInValidTokenException(InValidTokenException e) {
        log.error("InValidToken Exception: {}", e.getMessage(), e);

        return BaseErrorResponse.get(e.getStatus());
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<BaseErrorResponse> handleJsonParseException(JsonParseException e) {
        log.error("JsonParse Exception: {}", e.getMessage(), e);

        return BaseErrorResponse.get(e.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception: {}", e.getMessage(), e);

        return BaseErrorResponse.get(INTERNAL_SERVER_ERROR);
    }
}
